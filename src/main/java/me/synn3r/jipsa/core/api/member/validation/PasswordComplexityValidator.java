package me.synn3r.jipsa.core.api.member.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordComplexityValidator implements ConstraintValidator<PasswordComplexity, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        return password == null  || password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-{};':\"\\\\|,.<>/?]).{8,}$");
    }
}
