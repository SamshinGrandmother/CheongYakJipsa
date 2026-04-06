package me.synn3r.jipsa.core.api.auth.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import me.synn3r.jipsa.core.api.auth.AuthTestSupport;
import me.synn3r.jipsa.core.api.auth.service.RefreshTokenService;
import me.synn3r.jipsa.core.config.security.TestSecurityConfig;
import me.synn3r.jipsa.core.global.component.security.jwt.JwtTokenProvider;
import me.synn3r.jipsa.core.global.component.security.userdetails.DefaultUserDetails;

@WebMvcTest(controllers = TokenRefreshController.class,
	includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = TestSecurityConfig.class))
@Import(TestSecurityConfig.class)
@DisplayName("토큰 갱신 컨트롤러 테스트")
class TokenRefreshControllerTest extends AuthTestSupport {

	private static final String REFRESH_URL = "/api/auth/refresh";
	private static final String VALID_REFRESH_TOKEN = "valid.refresh.token";
	private static final String NEW_ACCESS_TOKEN = "new.access.token";
	private static final String NEW_REFRESH_TOKEN = "new.refresh.token";
	private static final String NEW_TOKEN_ID = "new-token-id";
	private static final String USER_ID = "testUser";
	private static final String TOKEN_ID = "test-token-id";

	@MockBean
	private JwtTokenProvider jwtTokenProvider;

	@MockBean
	private RefreshTokenService refreshTokenService;

	@MockBean
	private UserDetailsService userDetailsService;

	@MockBean
	private MessageSource messageSource;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("유효한 Refresh Token으로 갱신 시 200과 새 토큰들이 반환되어야 함")
	void refreshSuccess() throws Exception {
		DefaultUserDetails userDetails = getMockUserDetails();
		stubValidRefreshScenario(userDetails);

		mockMvc.perform(post(REFRESH_URL)
				.header("Authorization", "Bearer " + VALID_REFRESH_TOKEN))
			.andExpect(status().isOk())
			.andExpect(header().string("Authorization", "Bearer " + NEW_ACCESS_TOKEN))
			.andExpect(jsonPath("$.refreshToken").value(NEW_REFRESH_TOKEN));

		verify(refreshTokenService).save(USER_ID, NEW_TOKEN_ID);
	}

	@Test
	@DisplayName("Authorization 헤더가 없으면 401을 반환해야 함")
	void refreshWithoutAuthorizationHeader() throws Exception {
		when(messageSource.getMessage(any(), any(), any())).thenReturn("유효하지 않은 Refresh Token입니다.");

		mockMvc.perform(post(REFRESH_URL))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("Bearer 접두어가 없는 토큰이면 401을 반환해야 함")
	void refreshWithoutBearerPrefix() throws Exception {
		when(messageSource.getMessage(any(), any(), any())).thenReturn("유효하지 않은 Refresh Token입니다.");

		mockMvc.perform(post(REFRESH_URL)
				.header("Authorization", VALID_REFRESH_TOKEN))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("JWT 서명이 유효하지 않은 토큰이면 401을 반환해야 함")
	void refreshWithInvalidJwtSignature() throws Exception {
		when(jwtTokenProvider.validateToken(VALID_REFRESH_TOKEN)).thenReturn(false);
		when(messageSource.getMessage(any(), any(), any())).thenReturn("유효하지 않은 Refresh Token입니다.");

		mockMvc.perform(post(REFRESH_URL)
				.header("Authorization", "Bearer " + VALID_REFRESH_TOKEN))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("Access Token을 Refresh Token 자리에 사용하면 401을 반환해야 함")
	void refreshWithAccessToken() throws Exception {
		when(jwtTokenProvider.validateToken(VALID_REFRESH_TOKEN)).thenReturn(true);
		when(jwtTokenProvider.isRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(false);
		when(messageSource.getMessage(any(), any(), any())).thenReturn("유효하지 않은 Refresh Token입니다.");

		mockMvc.perform(post(REFRESH_URL)
				.header("Authorization", "Bearer " + VALID_REFRESH_TOKEN))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("Redis에 없는 tokenId이면 401을 반환해야 함 (이미 사용되었거나 탈취된 토큰)")
	void refreshWithAlreadyUsedToken() throws Exception {
		when(jwtTokenProvider.validateToken(VALID_REFRESH_TOKEN)).thenReturn(true);
		when(jwtTokenProvider.isRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(true);
		when(jwtTokenProvider.getUserIdFromToken(VALID_REFRESH_TOKEN)).thenReturn(USER_ID);
		when(jwtTokenProvider.getTokenIdFromRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(TOKEN_ID);
		when(refreshTokenService.validate(USER_ID, TOKEN_ID)).thenReturn(false);
		when(messageSource.getMessage(any(), any(), any())).thenReturn("유효하지 않은 Refresh Token입니다.");

		mockMvc.perform(post(REFRESH_URL)
				.header("Authorization", "Bearer " + VALID_REFRESH_TOKEN))
			.andExpect(status().isUnauthorized());
	}

	private void stubValidRefreshScenario(DefaultUserDetails userDetails) {
		when(jwtTokenProvider.validateToken(VALID_REFRESH_TOKEN)).thenReturn(true);
		when(jwtTokenProvider.isRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(true);
		when(jwtTokenProvider.getUserIdFromToken(VALID_REFRESH_TOKEN)).thenReturn(USER_ID);
		when(jwtTokenProvider.getTokenIdFromRefreshToken(VALID_REFRESH_TOKEN)).thenReturn(TOKEN_ID);
		when(refreshTokenService.validate(USER_ID, TOKEN_ID)).thenReturn(true);
		when(userDetailsService.loadUserByUsername(USER_ID)).thenReturn(userDetails);
		when(jwtTokenProvider.createAccessToken(userDetails)).thenReturn(NEW_ACCESS_TOKEN);
		when(jwtTokenProvider.createRefreshToken(USER_ID)).thenReturn(NEW_REFRESH_TOKEN);
		when(jwtTokenProvider.getTokenIdFromRefreshToken(NEW_REFRESH_TOKEN)).thenReturn(NEW_TOKEN_ID);
	}
}
