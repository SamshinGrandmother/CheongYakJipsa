package me.synn3r.jipsa.core.component.validation.validator;

import java.util.Optional;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.synn3r.jipsa.core.component.validation.annotation.PasswordConfirmValid;
import me.synn3r.jipsa.core.component.validation.domain.ConfirmablePassword;

public class PasswordConfirmValidator implements ConstraintValidator<PasswordConfirmValid, ConfirmablePassword> {
	@Override
	public boolean isValid(ConfirmablePassword value, ConstraintValidatorContext context) {
		return Optional.ofNullable(value.getPassword()).orElse("").equals(value.getPasswordConfirm());
	}
}
