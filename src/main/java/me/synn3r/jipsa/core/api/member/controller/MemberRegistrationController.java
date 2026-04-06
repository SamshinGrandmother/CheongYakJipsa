package me.synn3r.jipsa.core.api.member.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.api.commons.domain.FailResponse;
import me.synn3r.jipsa.core.api.commons.domain.Response;
import me.synn3r.jipsa.core.api.commons.domain.SuccessResponse;
import me.synn3r.jipsa.core.api.member.domain.EmailVerificationRequest;
import me.synn3r.jipsa.core.api.member.domain.MemberRegistrationRequest;
import me.synn3r.jipsa.core.api.member.service.MemberRegistrationService;

/**
 * 회원가입 및 이메일 인증 관련 REST API 컨트롤러.
 *
 * <p>회원가입 프로세스와 이메일 인증 기능을 제공합니다.
 * 모든 엔드포인트는 인증 없이 접근 가능합니다.</p>
 *
 * <h2>제공 API</h2>
 * <table border="1">
 *   <tr>
 *     <th>Method</th>
 *     <th>URI</th>
 *     <th>설명</th>
 *   </tr>
 *   <tr>
 *     <td>POST</td>
 *     <td>/api/sign-up</td>
 *     <td>회원가입</td>
 *   </tr>
 *   <tr>
 *     <td>POST</td>
 *     <td>/api/email/verification</td>
 *     <td>이메일 인증 코드 발송</td>
 *   </tr>
 *   <tr>
 *     <td>POST</td>
 *     <td>/api/email/verification/verify</td>
 *     <td>이메일 인증 코드 확인</td>
 *   </tr>
 *   <tr>
 *     <td>GET</td>
 *     <td>/api/email-verify</td>
 *     <td>이메일 인증 (링크 클릭 방식)</td>
 *   </tr>
 * </table>
 *
 * <h2>회원가입 흐름</h2>
 * <ol>
 *   <li>{@code POST /api/email/verification} - 이메일로 인증 코드 발송</li>
 *   <li>{@code POST /api/email/verification/verify} - 인증 코드 검증</li>
 *   <li>{@code POST /api/sign-up} - 회원 정보 입력 및 가입 완료</li>
 * </ol>
 *
 * <h2>프론트엔드 개발자를 위한 안내</h2>
 * <p><b>인증 코드 방식 (기본):</b></p>
 * <ol>
 *   <li>사용자가 이메일 입력 후 "인증 코드 발송" 버튼 클릭</li>
 *   <li>서버에서 이메일로 6자리 인증 코드 발송</li>
 *   <li>사용자가 이메일에서 코드 확인 후 입력 필드에 입력</li>
 *   <li>"인증 확인" 버튼 클릭으로 코드 검증</li>
 *   <li>인증 성공 시 회원가입 폼 제출 가능</li>
 * </ol>
 *
 * <p><b>링크 클릭 방식:</b></p>
 * <ol>
 *   <li>사용자가 이메일 입력 후 "인증 메일 발송" 버튼 클릭</li>
 *   <li>서버에서 이메일로 인증 링크 발송</li>
 *   <li>사용자가 이메일에서 인증 링크 클릭</li>
 *   <li>서버에서 자동 인증 처리 후 회원가입 페이지로 리다이렉트</li>
 *   <li>회원가입 폼 제출 가능</li>
 * </ol>
 *
 * @author synn3r
 * @since 1.0
 * @see MemberRegistrationService
 * @see MemberRegistrationRequest
 * @see EmailVerificationRequest
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "회원가입", description = "회원가입 및 이메일 인증 API")
public class MemberRegistrationController {

	private final MemberRegistrationService memberRegistrationService;
	private final MessageSource messageSource;

	/**
	 * 신규 회원을 등록합니다.
	 *
	 * <p>회원가입 전 이메일 인증이 완료되어야 합니다.
	 * 이메일 인증이 완료되지 않은 경우 400 Bad Request를 반환합니다.</p>
	 *
	 * <h3>요청 예시</h3>
	 * <pre>{@code
	 * POST /api/sign-up
	 * Content-Type: application/json
	 *
	 * {
	 *   "userId": "johndoe",
	 *   "name": "John Doe",
	 *   "email": "john@example.com",
	 *   "password": "Password1!",
	 *   "passwordConfirm": "Password1!",
	 *   "phoneNumber": "010-1234-5678"
	 * }
	 * }</pre>
	 *
	 * <h3>성공 응답</h3>
	 * <pre>{@code
	 * HTTP/1.1 201 Created
	 * {
	 *   "message": "회원가입이 완료되었습니다."
	 * }
	 * }</pre>
	 *
	 * @param request 회원가입 요청 정보
	 * @return 회원가입 결과
	 */
	@PostMapping("/api/sign-up")
	@Operation(
		summary = "회원가입",
		description = "신규 회원을 등록합니다. 이메일 인증이 완료된 상태여야 합니다."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "회원가입 성공",
			content = @Content(schema = @Schema(implementation = SuccessResponse.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "유효성 검증 실패 또는 이메일 미인증",
			content = @Content(schema = @Schema(implementation = FailResponse.class))
		),
		@ApiResponse(
			responseCode = "409",
			description = "이미 존재하는 이메일 또는 사용자 ID",
			content = @Content(schema = @Schema(implementation = FailResponse.class))
		)
	})
	public ResponseEntity<Response> signUp(
		@Parameter(description = "회원가입 요청 정보", required = true)
		@Valid @RequestBody MemberRegistrationRequest request) {

		memberRegistrationService.registerMember(request);

		String message = messageSource.getMessage(
			"member.registration.success",
			null,
			LocaleContextHolder.getLocale()
		);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(SuccessResponse.of(message));
	}

	/**
	 * 이메일 인증 코드를 발송합니다.
	 *
	 * <p>입력된 이메일 주소로 6자리 인증 코드를 발송합니다.
	 * 인증 코드는 5분간 유효합니다.</p>
	 *
	 * <h3>요청 예시</h3>
	 * <pre>{@code
	 * POST /api/email/verification
	 * Content-Type: application/json
	 *
	 * {
	 *   "email": "user@example.com"
	 * }
	 * }</pre>
	 *
	 * <h3>성공 응답</h3>
	 * <pre>{@code
	 * HTTP/1.1 200 OK
	 * {
	 *   "message": "인증 코드가 발송되었습니다."
	 * }
	 * }</pre>
	 *
	 * @param request 이메일 인증 요청 정보
	 * @return 발송 결과
	 */
	@PostMapping("/api/email/verification")
	@Operation(
		summary = "이메일 인증 코드 발송",
		description = "입력된 이메일 주소로 6자리 인증 코드를 발송합니다. 인증 코드는 5분간 유효합니다."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "이메일 발송 성공",
			content = @Content(schema = @Schema(implementation = SuccessResponse.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "유효하지 않은 이메일 형식",
			content = @Content(schema = @Schema(implementation = FailResponse.class))
		),
		@ApiResponse(
			responseCode = "500",
			description = "이메일 발송 실패",
			content = @Content(schema = @Schema(implementation = FailResponse.class))
		)
	})
	public ResponseEntity<Response> sendVerificationEmail(
		@Parameter(description = "이메일 인증 요청 정보", required = true)
		@Valid @RequestBody EmailVerificationRequest request) {

		memberRegistrationService.sendVerificationEmail(request.getEmail());

		String message = messageSource.getMessage(
			"email.verification.sent",
			null,
			LocaleContextHolder.getLocale()
		);

		return ResponseEntity.ok(SuccessResponse.of(message));
	}

	/**
	 * 이메일 인증 코드를 확인합니다.
	 *
	 * <p>사용자가 입력한 인증 코드를 검증합니다.
	 * 검증 성공 시 해당 이메일은 인증 완료 상태가 됩니다.</p>
	 *
	 * <h3>요청 예시</h3>
	 * <pre>{@code
	 * POST /api/email/verification/verify
	 * Content-Type: application/json
	 *
	 * {
	 *   "email": "user@example.com",
	 *   "verificationCode": "123456"
	 * }
	 * }</pre>
	 *
	 * <h3>성공 응답</h3>
	 * <pre>{@code
	 * HTTP/1.1 200 OK
	 * {
	 *   "message": "이메일 인증이 완료되었습니다."
	 * }
	 * }</pre>
	 *
	 * @param request 이메일 및 인증 코드 정보
	 * @return 인증 결과
	 */
	@PostMapping("/api/email/verification/verify")
	@Operation(
		summary = "이메일 인증 코드 확인",
		description = "사용자가 입력한 6자리 인증 코드를 검증합니다."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "인증 성공",
			content = @Content(schema = @Schema(implementation = SuccessResponse.class))
		),
		@ApiResponse(
			responseCode = "401",
			description = "인증 실패 (코드 불일치 또는 만료)",
			content = @Content(schema = @Schema(implementation = FailResponse.class))
		)
	})
	public ResponseEntity<Response> verifyEmailCode(
		@Parameter(description = "이메일 및 인증 코드 정보", required = true)
		@Valid @RequestBody EmailVerificationRequest request) {

		boolean verified = memberRegistrationService.verifyEmailCode(
			request.getEmail(),
			request.getVerificationCode()
		);

		if (verified) {
			String message = messageSource.getMessage(
				"email.verification.success",
				null,
				LocaleContextHolder.getLocale()
			);
			return ResponseEntity.ok(SuccessResponse.of(message));
		} else {
			String message = messageSource.getMessage(
				"email.verification.failed",
				null,
				LocaleContextHolder.getLocale()
			);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(FailResponse.of(message));
		}
	}

	/**
	 * 이메일 인증 링크를 처리합니다 (링크 클릭 방식).
	 *
	 * <p>이메일에 포함된 인증 링크를 클릭하면 호출됩니다.
	 * 토큰 검증 후 인증 완료 처리합니다.</p>
	 *
	 * <h3>요청 예시</h3>
	 * <pre>{@code
	 * GET /api/email-verify?token=dXNlckBleGFtcGxlLmNvbTphYmNkLTEyMzQ=
	 * }</pre>
	 *
	 * <h3>성공 응답</h3>
	 * <pre>{@code
	 * HTTP/1.1 200 OK
	 * {
	 *   "message": "이메일 인증이 완료되었습니다."
	 * }
	 * }</pre>
	 *
	 * <h3>프론트엔드 연동 안내</h3>
	 * <p>이 API는 주로 이메일 클라이언트에서 직접 호출됩니다.
	 * 프론트엔드에서는 인증 성공 후 회원가입 페이지로 리다이렉트 처리를 권장합니다.</p>
	 *
	 * @param token 인증 토큰 (Base64 인코딩)
	 * @return 인증 결과
	 */
	@GetMapping("/api/email-verify")
	@Operation(
		summary = "이메일 인증 (링크 방식)",
		description = "이메일에 포함된 인증 링크를 처리합니다. 토큰은 Base64로 인코딩되어 있습니다."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "인증 성공",
			content = @Content(schema = @Schema(implementation = SuccessResponse.class))
		),
		@ApiResponse(
			responseCode = "401",
			description = "인증 실패 (토큰 불일치 또는 만료)",
			content = @Content(schema = @Schema(implementation = FailResponse.class))
		)
	})
	public ResponseEntity<Response> verifyEmailByLink(
		@Parameter(description = "인증 토큰 (Base64 인코딩)", required = true)
		@RequestParam String token) {

		boolean verified = memberRegistrationService.verifyEmailByToken(token);

		if (verified) {
			String message = messageSource.getMessage(
				"email.verification.success",
				null,
				LocaleContextHolder.getLocale()
			);
			return ResponseEntity.ok(SuccessResponse.of(message));
		} else {
			String message = messageSource.getMessage(
				"email.verification.failed",
				null,
				LocaleContextHolder.getLocale()
			);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(FailResponse.of(message));
		}
	}
}
