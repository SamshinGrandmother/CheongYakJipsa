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

/**
 * Access Token 재발급 API 컨트롤러.
 *
 * <p>이 컨트롤러는 {@code POST /api/auth/refresh} 엔드포인트를 제공하며,
 * 유효한 Refresh Token을 수신하면 새로운 Access Token과 Refresh Token을 발급합니다.</p>
 *
 * <h2>Refresh Token Rotation</h2>
 * <p>토큰 갱신 시 기존 Refresh Token의 tokenId를 무효화하고 새 tokenId를 Redis에 저장합니다.
 * 이미 사용된 tokenId로 재요청하면 401을 반환하며, 이는 탈취된 토큰의 재사용을 방지합니다.</p>
 *
 * <h2>요청/응답 형식</h2>
 * <pre>
 * POST /api/auth/refresh
 * Authorization: Bearer {refreshToken}
 *
 * HTTP/1.1 200 OK
 * Authorization: Bearer {newAccessToken}
 * { "refreshToken": "{newRefreshToken}" }
 * </pre>
 *
 * <h2>검증 단계</h2>
 * <ol>
 *   <li>Authorization 헤더 존재 및 Bearer 접두어 확인</li>
 *   <li>JWT 서명 유효성 검사 ({@link JwtTokenProvider#validateToken(String)})</li>
 *   <li>Refresh Token 타입 확인 ({@link JwtTokenProvider#isRefreshToken(String)})</li>
 *   <li>Redis에 저장된 tokenId와 일치 여부 확인 ({@link RefreshTokenService#validate(String, String)})</li>
 *   <li>응답 직전 기존 tokenId가 여전히 일치할 때만 원자적으로 교체 (로그아웃과의 경합 방지)</li>
 * </ol>
 *
 * @author synn3r
 * @see RefreshTokenService Refresh Token 저장/검증/삭제 서비스
 * @see LogoutController 로그아웃 시 Refresh Token 무효화 컨트롤러
 * @since 1.0
 */
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

	/**
	 * Refresh Token을 검증하고 새로운 Access Token과 Refresh Token을 발급합니다.
	 *
	 * <p>검증에 실패한 모든 경우(헤더 누락, 서명 오류, 타입 불일치, tokenId 불일치)에
	 * 401 Unauthorized를 반환합니다. 성공 시 새 Access Token은 응답 헤더에,
	 * 새 Refresh Token은 응답 바디에 포함됩니다.</p>
	 *
	 * @param bearerToken Authorization 헤더 값 ({@code "Bearer {refreshToken}"} 형식)
	 * @return 성공 시 200 OK + 새 토큰, 실패 시 401 Unauthorized
	 */
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

		if (!refreshTokenService.rotate(userId, tokenId, newTokenId)) {
			return unauthorized();
		}

		return ResponseEntity.ok()
			.header(AUTHORIZATION_HEADER, BEARER_PREFIX + newAccessToken)
			.body(new TokenRefreshResponse(newRefreshToken));
	}

	/**
	 * 401 Unauthorized 응답을 생성합니다.
	 *
	 * @return 다국어 오류 메시지가 담긴 401 응답
	 */
	private ResponseEntity<FailResponse> unauthorized() {
		String message = messageSource.getMessage(INVALID_REFRESH_TOKEN_MESSAGE_KEY, null,
			LocaleContextHolder.getLocale());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(FailResponse.of(message));
	}

	/**
	 * 토큰 갱신 성공 응답 바디.
	 *
	 * @param refreshToken 새로 발급된 Refresh Token
	 */
	public record TokenRefreshResponse(String refreshToken) {
	}
}
