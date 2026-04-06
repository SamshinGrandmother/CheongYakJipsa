package me.synn3r.jipsa.core.api.auth.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.api.auth.service.RefreshTokenService;
import me.synn3r.jipsa.core.api.commons.domain.FailResponse;
import me.synn3r.jipsa.core.global.component.security.jwt.JwtTokenProvider;
import me.synn3r.jipsa.core.global.component.security.userdetails.DefaultUserDetails;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenRefreshController {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final String INVALID_REFRESH_TOKEN_MESSAGE_KEY = "security.error.InvalidRefreshToken";

	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenService refreshTokenService;
	private final UserDetailsService userDetailsService;
	private final MessageSource messageSource;

	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(
		@RequestHeader(value = AUTHORIZATION_HEADER, required = false) String bearerToken) {

		if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(BEARER_PREFIX)) {
			return unauthorized();
		}

		String refreshToken = bearerToken.substring(BEARER_PREFIX.length());

		if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
			return unauthorized();
		}

		String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
		String tokenId = jwtTokenProvider.getTokenIdFromRefreshToken(refreshToken);

		if (!refreshTokenService.validate(userId, tokenId)) {
			return unauthorized();
		}

		DefaultUserDetails userDetails = (DefaultUserDetails)userDetailsService.loadUserByUsername(userId);

		String newAccessToken = jwtTokenProvider.createAccessToken(userDetails);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);
		String newTokenId = jwtTokenProvider.getTokenIdFromRefreshToken(newRefreshToken);

		refreshTokenService.save(userId, newTokenId);

		return ResponseEntity.ok()
			.header(AUTHORIZATION_HEADER, BEARER_PREFIX + newAccessToken)
			.body(new TokenRefreshResponse(newRefreshToken));
	}

	private ResponseEntity<FailResponse> unauthorized() {
		String message = messageSource.getMessage(INVALID_REFRESH_TOKEN_MESSAGE_KEY, null,
			LocaleContextHolder.getLocale());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(FailResponse.of(message));
	}

	public record TokenRefreshResponse(String refreshToken) {
	}
}
