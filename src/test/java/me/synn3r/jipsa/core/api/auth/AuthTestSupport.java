package me.synn3r.jipsa.core.api.auth;

import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetailsService;

import me.synn3r.jipsa.core.global.component.security.enums.Role;
import me.synn3r.jipsa.core.global.component.security.jwt.JwtTokenProvider;
import me.synn3r.jipsa.core.global.component.security.userdetails.DefaultUserDetails;
import me.synn3r.jipsa.core.global.config.security.JwtConfig;

public abstract class AuthTestSupport {

	protected static final String TEST_JWT_SECRET =
		"mySecretKeyForJwtTokenGenerationThatIsAtLeast256BitsLong";
	protected static final long TEST_ACCESS_TOKEN_VALIDITY = 1800000L;
	protected static final long TEST_REFRESH_TOKEN_VALIDITY = 1209600000L;
	protected static final long TEST_PROFILE_VERIFICATION_TOKEN_VALIDITY = 300000L;

	protected JwtConfig getTestJwtConfig() {
		JwtConfig jwtConfig = new JwtConfig();
		jwtConfig.setSecret(TEST_JWT_SECRET);
		jwtConfig.setAccessTokenValidity(TEST_ACCESS_TOKEN_VALIDITY);
		jwtConfig.setRefreshTokenValidity(TEST_REFRESH_TOKEN_VALIDITY);
		jwtConfig.setProfileVerificationTokenValidity(TEST_PROFILE_VERIFICATION_TOKEN_VALIDITY);
		return jwtConfig;
	}

	protected JwtTokenProvider getTestJwtTokenProvider() {
		UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
		return new JwtTokenProvider(getTestJwtConfig(), userDetailsService);
	}

	protected DefaultUserDetails getMockUserDetails() {
		return new DefaultUserDetails(1L, "testUser", "test@email.com", "테스트유저", "password", Role.NORMAL);
	}
}
