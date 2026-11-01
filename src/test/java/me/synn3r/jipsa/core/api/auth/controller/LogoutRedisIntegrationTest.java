package me.synn3r.jipsa.core.api.auth.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.synn3r.jipsa.core.api.auth.AuthTestSupport;
import me.synn3r.jipsa.core.api.auth.service.RefreshTokenService;
import me.synn3r.jipsa.core.api.auth.service.impl.RedisRefreshTokenServiceImpl;
import me.synn3r.jipsa.core.global.component.security.enums.Role;
import me.synn3r.jipsa.core.global.component.security.filter.JwtAuthenticationFilter;
import me.synn3r.jipsa.core.global.component.security.filter.ProfileVerificationFilter;
import me.synn3r.jipsa.core.global.component.security.handler.DefaultAuthenticationFailureHandler;
import me.synn3r.jipsa.core.global.component.security.handler.DefaultAuthenticationSuccessHandler;
import me.synn3r.jipsa.core.global.component.security.handler.JwtAccessDeniedHandler;
import me.synn3r.jipsa.core.global.component.security.handler.JwtAuthenticationEntryPoint;
import me.synn3r.jipsa.core.global.component.security.jwt.JwtTokenProvider;
import me.synn3r.jipsa.core.global.component.security.userdetails.DefaultUserDetails;
import me.synn3r.jipsa.core.global.config.RedisConfig;
import me.synn3r.jipsa.core.global.config.security.JwtConfig;
import me.synn3r.jipsa.core.global.config.security.SecurityConfig;

@Tag("redis-integration")
@WebMvcTest(controllers = {LogoutController.class, TokenRefreshController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, ProfileVerificationFilter.class,
	JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class, RedisConfig.class,
	RedisRefreshTokenServiceImpl.class, LogoutRedisIntegrationTest.RedisTestConfiguration.class})
class LogoutRedisIntegrationTest extends AuthTestSupport {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private RefreshTokenService refreshTokenService;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private UserDetailsService userDetailsService;
	@MockBean
	private DefaultAuthenticationSuccessHandler authenticationSuccessHandler;
	@MockBean
	private DefaultAuthenticationFailureHandler authenticationFailureHandler;
	@MockBean
	private MessageSource messageSource;

	private String userId;
	private String otherUserId;
	private String accessToken;
	private String refreshToken;
	private DefaultUserDetails userDetails;

	@BeforeEach
	void setUp() {
		userId = "logout-it-" + UUID.randomUUID();
		otherUserId = userId + "-other";
		userDetails = new DefaultUserDetails(1L, userId, "fixture@example.com", "Fixture", "", Role.NORMAL);
		accessToken = jwtTokenProvider.createAccessToken(userDetails);
		refreshToken = jwtTokenProvider.createRefreshToken(userId);
		when(messageSource.getMessage(any(), any(), any())).thenReturn("test response");
		when(userDetailsService.loadUserByUsername(userId)).thenReturn(userDetails);
		refreshTokenService.save(userId, jwtTokenProvider.getTokenIdFromRefreshToken(refreshToken));
	}

	@AfterEach
	void cleanUpOwnKeys() {
		redisTemplate.delete(List.of(key(userId), key(otherUserId)));
	}

	@Test
	@DisplayName("실제 Redis에서 본인 토큰만 삭제하고 이전 Refresh 재사용을 거부한다")
	void logoutInvalidatesRefreshAndPreservesOtherUser() throws Exception {
		refreshTokenService.save(otherUserId, "other-token-id");
		assertRefreshTtl();

		logout();

		assertThat(redisTemplate.hasKey(key(userId))).isFalse();
		assertThat(refreshTokenService.validate(otherUserId, "other-token-id")).isTrue();
		mockMvc.perform(post("/api/auth/refresh").header("Authorization", "Bearer " + refreshToken))
			.andExpect(status().isUnauthorized());
		// 이번 브랜치에서는 Access를 폐기하지 않으므로 같은 Access로 반복 로그아웃이 가능하다.
		logout();
	}

	@Test
	@DisplayName("갱신 후 로그아웃하면 새 Refresh Token도 무효화한다")
	void logoutAfterRotationInvalidatesNewRefresh() throws Exception {
		var response = mockMvc.perform(post("/api/auth/refresh")
				.header("Authorization", "Bearer " + refreshToken))
			.andExpect(status().isOk()).andReturn().getResponse();
		String newRefresh = objectMapper.readTree(response.getContentAsString()).get("refreshToken").asText();
		assertThat(refreshTokenService.validate(userId, jwtTokenProvider.getTokenIdFromRefreshToken(newRefresh))).isTrue();

		logout();

		mockMvc.perform(post("/api/auth/refresh").header("Authorization", "Bearer " + newRefresh))
			.andExpect(status().isUnauthorized());
		assertThat(redisTemplate.hasKey(key(userId))).isFalse();
	}

	@Test
	@DisplayName("검증 후 멈춘 갱신 요청이 로그아웃 뒤에 Redis 키를 되살릴 수 없다")
	void inFlightRefreshCannotRecreateLoggedOutToken() throws Exception {
		CountDownLatch validated = new CountDownLatch(1);
		CountDownLatch resumeRefresh = new CountDownLatch(1);
		when(userDetailsService.loadUserByUsername(userId)).thenAnswer(invocation -> {
			validated.countDown();
			assertThat(resumeRefresh.await(10, TimeUnit.SECONDS)).isTrue();
			return userDetails;
		});

		try (var executor = Executors.newSingleThreadExecutor()) {
			var result = executor.submit(() -> mockMvc.perform(post("/api/auth/refresh")
				.header("Authorization", "Bearer " + refreshToken)).andReturn().getResponse());
			try {
				assertThat(validated.await(10, TimeUnit.SECONDS)).isTrue();
				logout();
			} finally {
				resumeRefresh.countDown();
			}
			assertThat(result.get(10, TimeUnit.SECONDS).getStatus()).isEqualTo(401);
		}
		assertThat(redisTemplate.hasKey(key(userId))).isFalse();
	}

	private void logout() throws Exception {
		mockMvc.perform(post("/api/auth/logout").header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("같은 Refresh Token의 동시 갱신은 하나만 성공한다")
	void concurrentRefreshHasOnlyOneWinner() throws Exception {
		CyclicBarrier validated = new CyclicBarrier(2);
		when(userDetailsService.loadUserByUsername(userId)).thenAnswer(invocation -> {
			validated.await(10, TimeUnit.SECONDS);
			return userDetails;
		});
		try (var executor = Executors.newFixedThreadPool(2)) {
			var first = executor.submit(() -> mockMvc.perform(post("/api/auth/refresh")
				.header("Authorization", "Bearer " + refreshToken)).andReturn().getResponse().getStatus());
			var second = executor.submit(() -> mockMvc.perform(post("/api/auth/refresh")
				.header("Authorization", "Bearer " + refreshToken)).andReturn().getResponse().getStatus());
			assertThat(List.of(first.get(10, TimeUnit.SECONDS), second.get(10, TimeUnit.SECONDS)))
				.containsExactlyInAnyOrder(200, 401);
		}
	}

	@Test
	@DisplayName("원자적 교체는 새 토큰과 Refresh TTL을 함께 저장한다")
	void rotationStoresNewIdAndTtl() {
		String previousId = jwtTokenProvider.getTokenIdFromRefreshToken(refreshToken);
		assertThat(refreshTokenService.rotate(userId, previousId, "replacement-token-id")).isTrue();
		assertThat(refreshTokenService.validate(userId, previousId)).isFalse();
		assertThat(refreshTokenService.validate(userId, "replacement-token-id")).isTrue();
		assertRefreshTtl();
	}

	@Test
	@DisplayName("다른 토큰이 저장되어 있으면 갱신이 덮어쓰지 않는다")
	void staleRotationPreservesCurrentToken() {
		String currentId = jwtTokenProvider.getTokenIdFromRefreshToken(refreshToken);
		assertThat(refreshTokenService.rotate(userId, "stale-token-id", "replacement-token-id")).isFalse();
		assertThat(refreshTokenService.validate(userId, currentId)).isTrue();
	}

	@Test
	@DisplayName("이미 만료된 Redis 키를 교체 요청으로 재생성할 수 없다")
	void expiredRefreshRecordCannotBeRecreated() {
		String previousId = jwtTokenProvider.getTokenIdFromRefreshToken(refreshToken);
		redisTemplate.expireAt(key(userId), Date.from(Instant.parse("2000-01-01T00:00:00Z")));

		assertThat(refreshTokenService.rotate(userId, previousId, "replacement-token-id")).isFalse();
		assertThat(redisTemplate.hasKey(key(userId))).isFalse();
	}

	private static String key(String userId) {
		return "refresh_token:" + userId;
	}

	private void assertRefreshTtl() {
		assertThat(redisTemplate.getExpire(key(userId), TimeUnit.MILLISECONDS))
			.isBetween(TEST_REFRESH_TOKEN_VALIDITY - TimeUnit.SECONDS.toMillis(30), TEST_REFRESH_TOKEN_VALIDITY);
	}

	@TestConfiguration
	static class RedisTestConfiguration extends AuthTestSupport {

		@Bean
		JwtConfig jwtConfig() {
			return getTestJwtConfig();
		}

		@Bean
		JwtTokenProvider jwtTokenProvider(JwtConfig jwtConfig, UserDetailsService userDetailsService) {
			return new JwtTokenProvider(jwtConfig, userDetailsService);
		}

		@Bean
		LettuceConnectionFactory redisConnectionFactory() {
			String host = System.getenv().getOrDefault("TEST_REDIS_HOST", "127.0.0.1");
			int port = Integer.parseInt(System.getenv().getOrDefault("TEST_REDIS_PORT", "16380"));
			return new LettuceConnectionFactory(host, port);
		}
	}
}
