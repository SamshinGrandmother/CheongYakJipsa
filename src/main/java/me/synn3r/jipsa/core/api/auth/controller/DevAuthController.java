package me.synn3r.jipsa.core.api.auth.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.global.component.security.enums.Role;
import me.synn3r.jipsa.core.global.component.security.jwt.JwtTokenProvider;
import me.synn3r.jipsa.core.global.component.security.userdetails.DefaultUserDetails;
import me.synn3r.jipsa.core.global.config.SwaggerConfig;
import me.synn3r.jipsa.core.global.config.security.DevSecurityConfig;

/**
 * 개발 환경 전용 인증 API 컨트롤러.
 *
 * <p>이 컨트롤러는 <strong>dev 프로파일에서만 활성화</strong>되며,
 * 프론트엔드 개발자가 Swagger UI에서 인증이 필요한 API를 테스트할 수 있도록
 * 임시 JWT 토큰을 발급합니다.</p>
 *
 * <h2>주요 기능</h2>
 * <ul>
 *   <li>실제 로그인 없이 테스트용 JWT Access Token 발급</li>
 *   <li>사용자 역할(USER, ADMIN 등) 선택 가능</li>
 *   <li>Swagger UI의 Authorize 기능과 연동</li>
 * </ul>
 *
 * <h2>사용 방법</h2>
 * <ol>
 *   <li>Swagger UI에서 <code>GET /api/dev/auth/token</code> API 실행</li>
 *   <li>응답의 <code>accessToken</code> 값 복사</li>
 *   <li>Swagger UI 상단의 <strong>'Authorize'</strong> 버튼 클릭</li>
 *   <li>복사한 토큰 입력 후 'Authorize' 클릭</li>
 *   <li>이후 인증이 필요한 API 테스트 가능</li>
 * </ol>
 *
 * <h2>보안 주의사항</h2>
 * <p><strong>이 컨트롤러는 절대로 운영 환경(prod)에서 활성화되어서는 안 됩니다.</strong></p>
 * <ul>
 *   <li>실제 인증 과정 없이 토큰이 발급됩니다.</li>
 *   <li>발급된 토큰으로 모든 인증 필요 API에 접근 가능합니다.</li>
 *   <li>{@code @Profile("dev")} 어노테이션으로 dev 환경에서만 Bean이 생성됩니다.</li>
 * </ul>
 *
 * @author synn3r
 * @see DevSecurityConfig 개발 환경 전용 보안 설정 (이 API의 인증 우회 담당)
 * @see JwtTokenProvider JWT 토큰 생성 및 검증
 * @see SwaggerConfig Swagger UI 설정
 * @since 1.0
 */
@Profile("dev")
@Tag(name = "Dev Auth", description = "개발용 인증 API (dev 프로파일에서만 활성화)")
@RestController
@RequestMapping("/api/dev/auth")
@RequiredArgsConstructor
public class DevAuthController {

	/**
	 * 개발용 테스트 사용자의 고유 식별자.
	 *
	 * <p>실제 데이터베이스의 사용자와 무관한 가상의 ID입니다.</p>
	 */
	private static final long DEV_USER_ID = 1L;

	/**
	 * 개발용 테스트 사용자의 로그인 ID.
	 *
	 * <p>JWT 토큰의 subject claim에 포함되는 값입니다.</p>
	 */
	private static final String DEV_USER_USER_ID = "devUser";

	/**
	 * 개발용 테스트 사용자의 이메일 주소.
	 *
	 * <p>JWT 토큰의 email claim에 포함되는 값입니다.</p>
	 */
	private static final String DEV_USER_EMAIL = "dev@test.com";

	/**
	 * 개발용 테스트 사용자의 이름.
	 *
	 * <p>JWT 토큰의 name claim에 포함되는 값입니다.</p>
	 */
	private static final String DEV_USER_NAME = "개발자";

	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * 개발용 JWT Access Token을 발급합니다.
	 *
	 * <p>이 API는 실제 사용자 인증 없이 테스트용 토큰을 발급합니다.
	 * 발급된 토큰은 {@link JwtTokenProvider}에 설정된 유효 시간 동안 사용 가능합니다.</p>
	 *
	 * <h3>응답 활용 방법</h3>
	 * <ul>
	 *   <li><code>accessToken</code>: 순수 토큰 값, 프로그래밍 방식으로 사용 시 활용</li>
	 *   <li><code>authorizationHeader</code>: "Bearer " 접두사 포함, HTTP 헤더에 직접 사용 가능</li>
	 * </ul>
	 *
	 * @param role 발급할 토큰에 부여할 사용자 역할 (기본값: USER)
	 * @return JWT 토큰 정보가 담긴 {@link DevTokenResponse}
	 * @see Role 사용 가능한 역할 목록
	 */
	@GetMapping("/token")
	@Operation(
		summary = "개발용 토큰 발급",
		description = "Swagger UI 테스트를 위한 임시 JWT 토큰을 발급합니다. " +
			"발급받은 토큰을 Swagger UI 상단의 'Authorize' 버튼을 클릭하여 입력하세요. " +
			"이 API는 dev 프로파일에서만 활성화됩니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "토큰 발급 성공",
			content = @Content(schema = @Schema(implementation = DevTokenResponse.class)))
	})
	public ResponseEntity<DevTokenResponse> issueDevToken(
		@Parameter(description = "사용자 역할", example = "USER")
		@RequestParam(defaultValue = "USER") Role role) {

		DefaultUserDetails devUser = new DefaultUserDetails(
			DEV_USER_ID,
			DEV_USER_USER_ID,
			DEV_USER_EMAIL,
			DEV_USER_NAME,
			"",
			role
		);

		String accessToken = jwtTokenProvider.createAccessToken(devUser);

		return ResponseEntity.ok(new DevTokenResponse(
			accessToken,
			"Bearer " + accessToken,
			jwtTokenProvider.getAccessTokenValidity() / 1000,
			devUser.getUsername(),
			role.name()
		));
	}

	/**
	 * 개발용 토큰 발급 API의 응답 객체.
	 *
	 * <p>발급된 JWT 토큰과 관련 메타데이터를 포함합니다.</p>
	 *
	 * @param accessToken         JWT Access Token 원본 값
	 * @param authorizationHeader HTTP Authorization 헤더에 사용할 값 ("Bearer " 접두사 포함)
	 * @param expiresInSeconds    토큰 만료까지 남은 시간 (초 단위)
	 * @param userId              토큰에 포함된 사용자 ID
	 * @param role                토큰에 포함된 사용자 역할
	 */
	@Schema(description = "개발용 토큰 응답")
	public record DevTokenResponse(
		@Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
		String accessToken,

		@Schema(description = "Swagger Authorize에 입력할 값 (Bearer 포함)", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
		String authorizationHeader,

		@Schema(description = "토큰 유효 시간 (초)", example = "1800")
		long expiresInSeconds,

		@Schema(description = "사용자 ID", example = "devUser")
		String userId,

		@Schema(description = "사용자 역할", example = "USER")
		String role
	) {
	}
}
