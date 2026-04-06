package me.synn3r.jipsa.core.global.component.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.synn3r.jipsa.core.global.component.validation.annotation.PasswordComplexity;
import me.synn3r.jipsa.core.global.component.validation.core.password.ComplexPassword;

public class PasswordComplexityValidator implements ConstraintValidator<PasswordComplexity, ComplexPassword> {

	private static final String PASSWORD_PATTERN =
		"^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-{};':\"\\\\|,.<>/?]).{8,}$";

	@Override
	public boolean isValid(ComplexPassword value, ConstraintValidatorContext context) {

		if (value == null) {
			return true;
		}

		String password = value.getPassword();
		return password == null || password.matches(PASSWORD_PATTERN);
	}
}
