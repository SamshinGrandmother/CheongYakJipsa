package me.synn3r.jipsa.core.global.component.security.jwt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;

import me.synn3r.jipsa.core.api.auth.AuthTestSupport;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenProvider 테스트")
class JwtTokenProviderTest extends AuthTestSupport {

	@Mock
	private UserDetailsService userDetailsService;

	private JwtTokenProvider jwtTokenProvider;

	@BeforeEach
	void setUp() {
		jwtTokenProvider = new JwtTokenProvider(getTestJwtConfig(), userDetailsService);
	}

	@Test
	@DisplayName("createRefreshToken()으로 생성한 토큰은 isRefreshToken()이 true를 반환해야 함")
	void isRefreshTokenWithRefreshToken() {
		String refreshToken = jwtTokenProvider.createRefreshToken("testUser");

		assertTrue(jwtTokenProvider.isRefreshToken(refreshToken));
	}

	@Test
	@DisplayName("createAccessToken()으로 생성한 토큰은 isRefreshToken()이 false를 반환해야 함")
	void isRefreshTokenWithAccessToken() {
		String accessToken = jwtTokenProvider.createAccessToken(getMockUserDetails());

		assertFalse(jwtTokenProvider.isRefreshToken(accessToken));
	}

	@Test
	@DisplayName("유효하지 않은 문자열은 isRefreshToken()이 false를 반환해야 함")
	void isRefreshTokenWithInvalidString() {
		assertFalse(jwtTokenProvider.isRefreshToken("invalid.token.string"));
	}
}
