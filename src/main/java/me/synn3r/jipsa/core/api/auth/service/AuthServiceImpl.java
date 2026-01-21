package me.synn3r.jipsa.core.api.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.api.auth.domain.LoginRequest;
import me.synn3r.jipsa.core.api.auth.domain.LoginResponse;
import me.synn3r.jipsa.core.api.auth.domain.PasswordVerifyResponse;
import me.synn3r.jipsa.core.api.auth.domain.RefreshToken;
import me.synn3r.jipsa.core.api.auth.repository.RefreshTokenRepository;
import me.synn3r.jipsa.core.component.security.jwt.JwtTokenProvider;
import me.synn3r.jipsa.core.component.security.userdetails.DefaultUserDetails;
import me.synn3r.jipsa.core.config.security.JwtConfig;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtConfig jwtConfig;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	public LoginResponse login(LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword())
		);

		DefaultUserDetails userDetails = (DefaultUserDetails)authentication.getPrincipal();

		String accessToken = jwtTokenProvider.createAccessToken(userDetails);
		String refreshToken = jwtTokenProvider.createRefreshToken(userDetails.getUsername());

		String tokenId = jwtTokenProvider.getTokenIdFromRefreshToken(refreshToken);
		long refreshTokenValiditySeconds =
			TimeUnit.MILLISECONDS.toSeconds(jwtConfig.getRefreshTokenValidity());

		RefreshToken refreshTokenEntity = RefreshToken.builder()
			.tokenId(tokenId)
			.userId(userDetails.getUsername())
			.token(refreshToken)
			.expiration(refreshTokenValiditySeconds)
			.build();

		refreshTokenRepository.save(refreshTokenEntity);

		eventPublisher.publishEvent(new AuthenticationSuccessEvent(authentication));

		return LoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.accessTokenExpiresIn(jwtConfig.getAccessTokenValidity())
			.refreshTokenExpiresIn(jwtConfig.getRefreshTokenValidity())
			.build();
	}

	@Override
	public void logout(String userId) {
		refreshTokenRepository.deleteAllByUserId(userId);
	}

	@Override
	public LoginResponse refreshToken(String refreshToken) {
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new BadCredentialsException("유효하지 않은 Refresh Token입니다.");
		}

		String tokenId = jwtTokenProvider.getTokenIdFromRefreshToken(refreshToken);
		RefreshToken storedToken = refreshTokenRepository.findById(tokenId)
			.orElseThrow(() -> new BadCredentialsException("Refresh Token이 존재하지 않습니다."));

		if (!storedToken.getToken().equals(refreshToken)) {
			throw new BadCredentialsException("Refresh Token이 일치하지 않습니다.");
		}

		String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
		Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
		DefaultUserDetails userDetails = (DefaultUserDetails)authentication.getPrincipal();

		refreshTokenRepository.delete(storedToken);

		String newAccessToken = jwtTokenProvider.createAccessToken(userDetails);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

		String newTokenId = jwtTokenProvider.getTokenIdFromRefreshToken(newRefreshToken);
		long refreshTokenValiditySeconds =
			TimeUnit.MILLISECONDS.toSeconds(jwtConfig.getRefreshTokenValidity());

		RefreshToken newRefreshTokenEntity = RefreshToken.builder()
			.tokenId(newTokenId)
			.userId(userId)
			.token(newRefreshToken)
			.expiration(refreshTokenValiditySeconds)
			.build();

		refreshTokenRepository.save(newRefreshTokenEntity);

		return LoginResponse.builder()
			.accessToken(newAccessToken)
			.refreshToken(newRefreshToken)
			.accessTokenExpiresIn(jwtConfig.getAccessTokenValidity())
			.refreshTokenExpiresIn(jwtConfig.getRefreshTokenValidity())
			.build();
	}

	@Override
	public PasswordVerifyResponse verifyPassword(DefaultUserDetails userDetails, String password) {
		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
		}

		String verificationToken =
			jwtTokenProvider.createProfileVerificationToken(userDetails.getUsername());

		return PasswordVerifyResponse.builder()
			.verificationToken(verificationToken)
			.expiresIn(jwtConfig.getProfileVerificationTokenValidity())
			.build();
	}
}
