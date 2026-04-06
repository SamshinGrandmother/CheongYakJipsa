package me.synn3r.jipsa.core.global.component.security.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.synn3r.jipsa.core.api.auth.AuthTestSupport;
import me.synn3r.jipsa.core.api.auth.service.RefreshTokenService;
import me.synn3r.jipsa.core.global.component.security.jwt.JwtTokenProvider;
import me.synn3r.jipsa.core.global.component.security.service.AuthenticationHistoryService;
import me.synn3r.jipsa.core.global.component.security.userdetails.DefaultUserDetails;

@ExtendWith(MockitoExtension.class)
@DisplayName("인증 성공 핸들러 테스트")
class DefaultAuthenticationSuccessHandlerTest extends AuthTestSupport {

	private static final String ACCESS_TOKEN = "test.access.token";
	private static final String REFRESH_TOKEN = "test.refresh.token";
	private static final String TOKEN_ID = "test-token-id";
	private static final String SUCCESS_MESSAGE = "인증이 완료되었습니다.";

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private RefreshTokenService refreshTokenService;

	@Mock
	private AuthenticationHistoryService authenticationHistoryService;

	@Mock
	private MessageSource messageSource;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private DefaultAuthenticationSuccessHandler handler;

	@BeforeEach
	void setUp() {
		handler = new DefaultAuthenticationSuccessHandler(
			jwtTokenProvider, refreshTokenService, authenticationHistoryService, objectMapper, messageSource);
	}

	@Test
	@DisplayName("로그인 성공 시 Authorization 헤더에 Access Token이 포함되어야 함")
	void onAuthenticationSuccessAccessTokenInHeader() throws IOException {
		DefaultUserDetails userDetails = getMockUserDetails();
		Authentication authentication = mockAuthentication(userDetails);
		stubTokenCreation(userDetails);

		MockHttpServletResponse response = performSuccess(authentication);

		assertEquals("Bearer " + ACCESS_TOKEN, response.getHeader("Authorization"));
	}

	@Test
	@DisplayName("로그인 성공 시 응답 body에 refreshToken이 포함되어야 함")
	void onAuthenticationSuccessRefreshTokenInBody() throws IOException {
		DefaultUserDetails userDetails = getMockUserDetails();
		Authentication authentication = mockAuthentication(userDetails);
		stubTokenCreation(userDetails);

		MockHttpServletResponse response = performSuccess(authentication);

		@SuppressWarnings("unchecked")
		Map<String, String> body = objectMapper.readValue(response.getContentAsString(), Map.class);
		assertEquals(REFRESH_TOKEN, body.get("refreshToken"));
	}

	@Test
	@DisplayName("로그인 성공 시 Refresh Token의 tokenId가 Redis에 저장되어야 함")
	void onAuthenticationSuccessRefreshTokenSavedToRedis() throws IOException {
		DefaultUserDetails userDetails = getMockUserDetails();
		Authentication authentication = mockAuthentication(userDetails);
		stubTokenCreation(userDetails);

		performSuccess(authentication);

		verify(refreshTokenService).save(userDetails.getUsername(), TOKEN_ID);
	}

	@Test
	@DisplayName("로그인 성공 시 접속 이력이 기록되어야 함")
	void onAuthenticationSuccessRecordsHistory() throws IOException {
		DefaultUserDetails userDetails = getMockUserDetails();
		Authentication authentication = mockAuthentication(userDetails);
		stubTokenCreation(userDetails);

		performSuccess(authentication);

		verify(authenticationHistoryService).recordSuccess(userDetails.getUsername());
	}

	private Authentication mockAuthentication(DefaultUserDetails userDetails) {
		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(userDetails);
		return authentication;
	}

	private void stubTokenCreation(DefaultUserDetails userDetails) {
		when(jwtTokenProvider.createAccessToken(userDetails)).thenReturn(ACCESS_TOKEN);
		when(jwtTokenProvider.createRefreshToken(userDetails.getUsername())).thenReturn(REFRESH_TOKEN);
		when(jwtTokenProvider.getTokenIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(TOKEN_ID);
		when(messageSource.getMessage(any(), any(), any())).thenReturn(SUCCESS_MESSAGE);
	}

	private MockHttpServletResponse performSuccess(Authentication authentication) throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		handler.onAuthenticationSuccess(request, response, authentication);
		return response;
	}
}
