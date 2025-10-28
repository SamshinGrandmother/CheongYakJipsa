package me.synn3r.jipsa.core.component.validation.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.synn3r.jipsa.core.component.validation.annotation.PasswordComplexityValid;
import me.synn3r.jipsa.core.component.validation.domain.ComplexPassword;

public class PasswordComplexityValidator implements ConstraintValidator<PasswordComplexityValid, ComplexPassword> {
	private final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
	private final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
	private final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]");
	private final Pattern SPECIAL_CHARACTERS_PATTERN = Pattern.compile("[!@#$%^&*()_+=\\-{};':\"\\\\|,.<>/?]");
	private final Pattern LENGTH_PATTERN = Pattern.compile(".{8,}");
	private final Pattern[] PASSWORD_COMPLEXITY_PATTERNS = {LOWERCASE_PATTERN, UPPERCASE_PATTERN, NUMBER_PATTERN,
		SPECIAL_CHARACTERS_PATTERN};

	@Override
	public boolean isValid(ComplexPassword value, ConstraintValidatorContext context) {
		String password = value.getPassword();

		if (!LENGTH_PATTERN.matcher(password).matches()) {
			return false;
		}

		for (Pattern passwordComplexityPattern : PASSWORD_COMPLEXITY_PATTERNS) {
			Matcher matcher = passwordComplexityPattern.matcher(password);
			if (!matcher.find()) {
				return false;
			}
		}
		return true;
	}
}
