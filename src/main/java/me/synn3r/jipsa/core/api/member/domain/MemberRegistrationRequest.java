package me.synn3r.jipsa.core.api.member.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synn3r.jipsa.core.global.component.security.enums.Role;
import me.synn3r.jipsa.core.global.component.validation.annotation.PasswordComplexity;
import me.synn3r.jipsa.core.global.component.validation.annotation.PasswordConfirm;

/**
 * 회원가입 요청 DTO.
 *
 * <p>신규 회원 등록에 필요한 정보를 담는 요청 객체입니다.
 * 모든 필드는 유효성 검증이 적용됩니다.</p>
 *
 * <h2>필수 필드</h2>
 * <ul>
 *   <li>{@code userId} - 로그인에 사용할 사용자 ID</li>
 *   <li>{@code name} - 사용자 이름</li>
 *   <li>{@code email} - 이메일 주소 (인증 필요)</li>
 *   <li>{@code password} - 비밀번호 (복잡도 규칙 적용)</li>
 *   <li>{@code passwordConfirm} - 비밀번호 확인</li>
 *   <li>{@code phoneNumber} - 전화번호</li>
 * </ul>
 *
 * <h2>검증 규칙</h2>
 * <ul>
 *   <li>비밀번호: 대/소문자, 숫자, 특수문자 포함 8자 이상</li>
 *   <li>비밀번호 확인: password 필드와 일치해야 함</li>
 *   <li>이메일: 사전에 인증이 완료되어야 회원가입 가능</li>
 * </ul>
 *
 * <h2>API 사용 예시</h2>
 * <pre>{@code
 * POST /api/sign-up
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
 * @author synn3r
 * @since 1.0
 * @see PasswordComplexity 비밀번호 복잡도 검증
 * @see PasswordConfirm 비밀번호 확인 검증
 * @see MemberPassword 비밀번호 캡슐화 클래스
 */
@Getter
@NoArgsConstructor
public class MemberRegistrationRequest {

	@NotEmpty(message = "{member.validation.userId.notEmpty}")
	private String userId;

	@NotEmpty(message = "{member.validation.name.notEmpty}")
	private String name;

	@NotEmpty(message = "{member.validation.email.notEmpty}")
	@Email(message = "{member.validation.email.invalid}")
	private String email;

	private final Role role = Role.NORMAL;

	@Valid
	@NotNull(message = "{member.validation.credentials.notNull}")
	@JsonUnwrapped
	private MemberPassword credentials;

	@NotEmpty(message = "{member.validation.phoneNumber.notEmpty}")
	private String phoneNumber;

	/**
	 * 비밀번호를 반환합니다.
	 *
	 * @return 비밀번호 문자열
	 */
	public String getPassword() {
		return credentials != null ? credentials.getPassword() : null;
	}
}
