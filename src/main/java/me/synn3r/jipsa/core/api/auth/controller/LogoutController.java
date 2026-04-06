package me.synn3r.jipsa.core.api.auth.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.api.auth.service.RefreshTokenService;
import me.synn3r.jipsa.core.api.commons.domain.FailResponse;
import me.synn3r.jipsa.core.api.commons.domain.SuccessResponse;

/**
 * 로그아웃 API 컨트롤러.
 *
 * <p>이 컨트롤러는 {@code POST /api/auth/logout} 엔드포인트를 제공하며,
 * 로그아웃 시 Redis에 저장된 Refresh Token을 무효화합니다.</p>
 *
 * <h2>보안 동작</h2>
 * <p>이 엔드포인트는 인증된 사용자만 접근할 수 있습니다.
 * {@link me.synn3r.jipsa.core.global.component.security.filter.JwtAuthenticationFilter}가
 * 요청의 Access Token을 검증하고 {@link org.springframework.security.core.context.SecurityContextHolder}에
 * 인증 정보를 설정한 후에 컨트롤러에 도달합니다.</p>
 *
 * <h2>Refresh Token 무효화</h2>
 * <p>로그아웃 시 {@link RefreshTokenService#delete(String)}를 호출하여
 * Redis에 저장된 Refresh Token을 삭제합니다. 이후에는 해당 사용자의
 * Refresh Token으로 Access Token 재발급이 불가능합니다.</p>
 *
 * <p>Redis에 해당 키가 없는 경우(이미 만료되었거나 삭제된 경우)에도
 * 멱등성을 보장하며 정상적으로 200을 반환합니다.</p>
 *
 * <p>현재는 기기별이 아닌 사용자별 Refresh Token 한 개를 관리합니다.
 * 이미 발급된 Access Token은 삭제되지 않으며 만료 시점까지 유효합니다.
 * 클라이언트는 로그아웃 성공 후 보관한 Access/Refresh Token을 모두 제거해야 합니다.</p>
 *
 * <h2>요청/응답 예시</h2>
 * <pre>
 * POST /api/auth/logout
 * Authorization: Bearer {accessToken}
 *
 * HTTP/1.1 200 OK
 * { "message": "로그아웃 되었습니다." }
 * </pre>
 *
 * @author synn3r
 * @see RefreshTokenService Refresh Token 관리 서비스
 * @see me.synn3r.jipsa.core.api.auth.controller.TokenRefreshController 토큰 갱신 컨트롤러
 * @since 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LogoutController {

	private static final String LOGOUT_SUCCESS_MESSAGE_KEY = "security.success.Logout";

	private final RefreshTokenService refreshTokenService;
	private final MessageSource messageSource;

	/**
	 * 로그아웃 처리 후 Refresh Token을 무효화합니다.
	 *
	 * <p>인증된 사용자의 userId를 {@link Authentication}에서 추출하여
	 * Redis에 저장된 Refresh Token을 삭제합니다.</p>
	 *
	 * @param authentication Spring Security가 주입하는 현재 인증 객체.
	 *                       {@code JwtAuthenticationFilter}에 의해 설정됨.
	 * @return 200 OK와 로그아웃 성공 메시지
	 */
	@PostMapping("/logout")
	@Operation(summary = "로그아웃 및 Refresh Token 무효화",
		description = "유효한 Access Token으로 요청합니다. 사용자별 Refresh Token을 삭제하여 이후 갱신을 차단합니다. "
			+ "반복 호출도 200을 반환하며, 기존 Access Token은 만료까지 유효합니다.",
		security = @SecurityRequirement(name = "Bearer"))
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Refresh Token 삭제 완료 또는 이미 없음",
			content = @Content(schema = @Schema(implementation = SuccessResponse.class),
				examples = @ExampleObject(value = "{\"message\":\"로그아웃 되었습니다.\"}"))),
		@ApiResponse(responseCode = "401", description = "Access Token 누락, 만료, 서명 오류 또는 토큰 종류 불일치",
			content = @Content(schema = @Schema(implementation = FailResponse.class))),
		@ApiResponse(responseCode = "500", description = "Redis 삭제 실패 등 서버 오류; 로그아웃 성공이 아님",
			content = @Content(schema = @Schema(implementation = FailResponse.class)))
	})
	public ResponseEntity<SuccessResponse> logout(@Parameter(hidden = true) Authentication authentication) {
		String userId = authentication.getName();
		refreshTokenService.delete(userId);

		String message = messageSource.getMessage(LOGOUT_SUCCESS_MESSAGE_KEY, null,
			LocaleContextHolder.getLocale());
		return ResponseEntity.ok(SuccessResponse.of(message));
	}
}
