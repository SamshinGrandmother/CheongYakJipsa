package me.synn3r.jipsa.core.global.component.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.synn3r.jipsa.core.global.component.validation.annotation.PasswordConfirm;
import me.synn3r.jipsa.core.global.component.validation.core.password.ConfirmablePassword;

public class PasswordConfirmValidator implements ConstraintValidator<PasswordConfirm, ConfirmablePassword> {

	@Override
	public boolean isValid(ConfirmablePassword value, ConstraintValidatorContext context) {

		if (value == null) {
			return true;
		}

		String password = value.getPassword();
		String passwordConfirm = value.getPasswordConfirm();

		if (password == null || passwordConfirm == null) {
			return false;
		}

		return password.equals(passwordConfirm);
	}
}
