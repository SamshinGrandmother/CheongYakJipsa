package me.synn3r.jipsa.core.global.component.validation.core.password;

/**
 * 비밀번호 복잡도 검증을 위한 인터페이스.
 *
 * <p>비밀번호 복잡도 검증이 필요한 객체는 이 인터페이스를 구현해야 합니다.</p>
 *
 * @author synn3r
 * @since 1.0
 * @see me.synn3r.jipsa.core.global.component.validation.validator.PasswordComplexityValidator
 */
public interface ComplexPassword {

	String getPassword();
}
