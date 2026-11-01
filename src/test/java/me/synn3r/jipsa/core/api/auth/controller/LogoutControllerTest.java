package me.synn3r.jipsa.core.api.auth.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import me.synn3r.jipsa.core.api.auth.AuthTestSupport;
import me.synn3r.jipsa.core.api.auth.service.RefreshTokenService;
import me.synn3r.jipsa.core.global.component.security.enums.Role;
import me.synn3r.jipsa.core.global.component.security.filter.JwtAuthenticationFilter;
import me.synn3r.jipsa.core.global.component.security.filter.ProfileVerificationFilter;
import me.synn3r.jipsa.core.global.component.security.handler.DefaultAuthenticationFailureHandler;
import me.synn3r.jipsa.core.global.component.security.handler.DefaultAuthenticationSuccessHandler;
import me.synn3r.jipsa.core.global.component.security.handler.JwtAccessDeniedHandler;
import me.synn3r.jipsa.core.global.component.security.handler.JwtAuthenticationEntryPoint;
import me.synn3r.jipsa.core.global.component.security.jwt.JwtTokenProvider;
import me.synn3r.jipsa.core.global.component.security.userdetails.DefaultUserDetails;
import me.synn3r.jipsa.core.global.config.security.SecurityConfig;

@WebMvcTest(controllers = {LogoutController.class, TokenRefreshController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, ProfileVerificationFilter.class,
	JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class, LogoutControllerTest.JwtTestConfiguration.class})
@DisplayName("로그아웃 컨트롤러 테스트")
class LogoutControllerTest extends AuthTestSupport {

	private static final String LOGOUT_URL = "/api/auth/logout";
	private static final String USER_ID = "testUser";
	private static final String OTHER_USER_ID = "otherUser";
	private static final String LOGOUT_SUCCESS_MESSAGE = "로그아웃 되었습니다.";

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@MockBean
	private UserDetailsService userDetailsService;

	@MockBean
	private DefaultAuthenticationSuccessHandler authenticationSuccessHandler;

	@MockBean
	private DefaultAuthenticationFailureHandler authenticationFailureHandler;

	@MockBean
	private RefreshTokenService refreshTokenService;

	@MockBean
	private MessageSource messageSource;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("인증 없이 로그아웃하면 401을 반환하고 Refresh Token을 삭제하지 않아야 함")
	void logoutWithoutAuthentication() throws Exception {
		when(messageSource.getMessage(any(), any(), any())).thenReturn("인증이 필요합니다.");

		mockMvc.perform(post(LOGOUT_URL))
			.andExpect(status().isUnauthorized());

		verifyNoInteractions(refreshTokenService);
	}

	@Test
	@DisplayName("인증된 사용자가 로그아웃하면 200과 성공 메시지를 반환해야 함")
	void logoutSuccess() throws Exception {
		when(messageSource.getMessage(any(), any(), any())).thenReturn(LOGOUT_SUCCESS_MESSAGE);

		mockMvc.perform(post(LOGOUT_URL)
				.header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(getMockUserDetails())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(LOGOUT_SUCCESS_MESSAGE));

		verify(refreshTokenService).delete(USER_ID);
	}

	@Test
	@DisplayName("로그아웃하면 현재 인증된 사용자의 Refresh Token만 삭제해야 함")
	void logoutDeletesOnlyAuthenticatedUsersRefreshToken() throws Exception {
		when(messageSource.getMessage(any(), any(), any())).thenReturn(LOGOUT_SUCCESS_MESSAGE);

		mockMvc.perform(post(LOGOUT_URL)
				.header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(
					new DefaultUserDetails(2L, OTHER_USER_ID, "other@example.com", "Other", "", Role.NORMAL))))
			.andExpect(status().isOk());

		verify(refreshTokenService).delete(OTHER_USER_ID);
		verify(refreshTokenService, never()).delete(USER_ID);
	}

	@Test
	@DisplayName("Redis에 Refresh Token이 없어도 로그아웃은 정상 처리되어야 함 (멱등성)")
	void logoutIsIdempotentWhenRefreshTokenNotInRedis() throws Exception {
		when(messageSource.getMessage(any(), any(), any())).thenReturn(LOGOUT_SUCCESS_MESSAGE);
		doNothing().when(refreshTokenService).delete(any());

		String accessToken = jwtTokenProvider.createAccessToken(getMockUserDetails());
		for (int attempt = 0; attempt < 2; attempt++) {
			mockMvc.perform(post(LOGOUT_URL)
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value(LOGOUT_SUCCESS_MESSAGE));
		}
		verify(refreshTokenService, times(2)).delete(USER_ID);
	}

	@Test
	@DisplayName("Refresh Token으로 로그아웃을 인증할 수 없다")
	void logoutRejectsRefreshToken() throws Exception {
		assertRejected(jwtTokenProvider.createRefreshToken(USER_ID));
	}

	@Test
	@DisplayName("프로필 확인 토큰으로 로그아웃을 인증할 수 없다")
	void logoutRejectsProfileVerificationToken() throws Exception {
		assertRejected(jwtTokenProvider.createProfileVerificationToken(USER_ID));
	}

	@ParameterizedTest(name = "invalid token case {index}")
	@MethodSource("invalidAccessTokens")
	@DisplayName("손상되거나 만료되거나 필수 클레임이 잘못된 토큰은 401이며 삭제하지 않는다")
	void logoutRejectsInvalidAccessTokens(String token) throws Exception {
		assertRejected(token);
	}

	@Test
	@DisplayName("운영 필터 체인도 정상 Refresh Token을 갱신 컨트롤러에 전달한다")
	void refreshWorksThroughProductionSecurityChain() throws Exception {
		String refreshToken = jwtTokenProvider.createRefreshToken(USER_ID);
		String tokenId = jwtTokenProvider.getTokenIdFromRefreshToken(refreshToken);
		when(refreshTokenService.validate(USER_ID, tokenId)).thenReturn(true);
		when(refreshTokenService.rotate(eq(USER_ID), eq(tokenId), anyString())).thenReturn(true);
		when(userDetailsService.loadUserByUsername(USER_ID)).thenReturn(getMockUserDetails());

		mockMvc.perform(post("/api/auth/refresh")
				.header("Authorization", "Bearer " + refreshToken))
			.andExpect(status().isOk())
			.andExpect(header().exists("Authorization"))
			.andExpect(jsonPath("$.refreshToken").isString());
	}

	@Test
	@DisplayName("Redis 삭제가 실패하면 로그아웃 성공으로 응답하지 않는다")
	void logoutDoesNotAcknowledgeFailedDeletion() throws Exception {
		doThrow(new RedisConnectionFailureException("test Redis unavailable"))
			.when(refreshTokenService).delete(USER_ID);

		mockMvc.perform(post(LOGOUT_URL)
				.header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(getMockUserDetails())))
			.andExpect(status().is5xxServerError());
	}

	private void assertRejected(String token) throws Exception {
		when(messageSource.getMessage(any(), any(), any())).thenReturn("인증이 필요합니다.");
		mockMvc.perform(post(LOGOUT_URL).header("Authorization", "Bearer " + token))
			.andExpect(status().isUnauthorized());
		verifyNoInteractions(refreshTokenService);
	}

	private static Stream<String> invalidAccessTokens() {
		Map<String, Object> accessClaims = Map.of("id", 1L, "role", "NORMAL");
		return Stream.of(
			"not-a-jwt",
			Jwts.builder().claims(accessClaims).subject(USER_ID)
				.signWith(Keys.hmacShaKeyFor(TEST_JWT_SECRET.getBytes(StandardCharsets.UTF_8))).compact(),
			Jwts.builder().claims(accessClaims).subject(" ").expiration(Date.from(Instant.parse("2100-01-01T00:00:00Z")))
				.signWith(Keys.hmacShaKeyFor(TEST_JWT_SECRET.getBytes(StandardCharsets.UTF_8))).compact(),
			signedToken(accessClaims, TEST_JWT_SECRET, Instant.parse("2000-01-01T00:00:00Z")),
			signedToken(accessClaims, "differentSigningSecretAtLeast256BitsLongForTestsOnly", Instant.parse("2100-01-01T00:00:00Z")),
			signedToken(Map.of("role", "NORMAL"), TEST_JWT_SECRET, Instant.parse("2100-01-01T00:00:00Z")),
			signedToken(Map.of("id", 1L), TEST_JWT_SECRET, Instant.parse("2100-01-01T00:00:00Z")),
			signedToken(Map.of("id", 1L, "role", "UNKNOWN"), TEST_JWT_SECRET, Instant.parse("2100-01-01T00:00:00Z")),
			signedToken(Map.of("id", "not-a-number", "role", "NORMAL"), TEST_JWT_SECRET, Instant.parse("2100-01-01T00:00:00Z")),
			signedToken(Map.of("id", 1L, "role", "NORMAL", "type", "unexpected"), TEST_JWT_SECRET, Instant.parse("2100-01-01T00:00:00Z")));
	}

	private static String signedToken(Map<String, Object> claims, String secret, Instant expiration) {
		return Jwts.builder().claims(claims).subject(USER_ID).expiration(Date.from(expiration))
			.signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).compact();
	}

	@TestConfiguration
	static class JwtTestConfiguration extends AuthTestSupport {
		@Bean
		JwtTokenProvider jwtTokenProvider(UserDetailsService userDetailsService) {
			return new JwtTokenProvider(getTestJwtConfig(), userDetailsService);
		}
	}
}
