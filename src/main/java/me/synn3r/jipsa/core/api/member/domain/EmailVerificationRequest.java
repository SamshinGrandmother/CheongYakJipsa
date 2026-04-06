package me.synn3r.jipsa.core.api.member.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이메일 인증 요청 DTO.
 *
 * <p>이메일 인증 코드 발송 및 검증 요청에 사용됩니다.</p>
 *
 * <h2>사용 케이스</h2>
 * <ul>
 *   <li><b>인증 코드 발송</b>: {@code email} 필드만 필요</li>
 *   <li><b>인증 코드 검증</b>: {@code email}, {@code verificationCode} 필드 모두 필요</li>
 * </ul>
 *
 * <h2>API 사용 예시</h2>
 * <pre>{@code
 * // 인증 코드 발송 요청
 * POST /api/email/verification
 * {
 *   "email": "user@example.com"
 * }
 *
 * // 인증 코드 검증 요청
 * POST /api/email/verification/verify
 * {
 *   "email": "user@example.com",
 *   "verificationCode": "123456"
 * }
 * }</pre>
 *
 * @author synn3r
 * @since 1.0
 */
@Getter
@NoArgsConstructor
public class EmailVerificationRequest {

	/**
	 * 인증 대상 이메일 주소.
	 *
	 * <p>유효한 이메일 형식이어야 합니다.</p>
	 */
	@NotEmpty(message = "{email.validation.email.notEmpty}")
	@Email(message = "{email.validation.email.invalid}")
	private String email;

	/**
	 * 인증 코드.
	 *
	 * <p>인증 코드 검증 시에만 필요합니다.
	 * 6자리 숫자 문자열 형식입니다.</p>
	 */
	private String verificationCode;
}
