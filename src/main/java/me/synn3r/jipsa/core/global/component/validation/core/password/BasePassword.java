package me.synn3r.jipsa.core.global.component.validation.core.password;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synn3r.jipsa.core.global.component.validation.annotation.PasswordComplexity;
import me.synn3r.jipsa.core.global.component.validation.annotation.PasswordConfirm;

/**
 * 비밀번호 관련 필드를 캡슐화한 기본 구현체.
 *
 * <p>{@link ComplexPassword}와 {@link ConfirmablePassword} 인터페이스를 모두 구현하며,
 * 비밀번호와 비밀번호 확인 필드에 대한 기본 유효성 검증을 제공합니다.</p>
 *
 * <h2>사용 방법</h2>
 * <p>비밀번호 입력이 필요한 요청 DTO에서 이 클래스를 상속받는 서브클래스를 만들어 사용합니다.</p>
 *
 * <pre>{@code
 * public class MemberPassword extends BasePassword {
 *     // 필요시 추가 검증이나 필드 추가
 * }
 *
 * public class MemberRegistrationRequest {
 *     @Valid
 *     private MemberPassword password;
 * }
 * }</pre>
 *
 * @author synn3r
 * @since 1.0
 * @see ComplexPassword
 * @see ConfirmablePassword
 */
@Getter
@NoArgsConstructor
@PasswordComplexity
@PasswordConfirm
public class BasePassword implements ComplexPassword, ConfirmablePassword {

	@NotEmpty(message = "{member.validation.password.notEmpty}")
	private String password;

	@NotEmpty(message = "{member.validation.passwordConfirm.notEmpty}")
	private String passwordConfirm;
}
