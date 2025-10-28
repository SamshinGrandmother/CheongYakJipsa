package me.synn3r.jipsa.core.component.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import me.synn3r.jipsa.core.component.validation.validator.PasswordConfirmValidator;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordConfirmValidator.class)
public @interface PasswordConfirmValid {
	String message() default "{jakarta.validation.constraints.signUp.passwordConfirm.message}";

	Class[] groups() default {};

	Class[] payload() default {};
}
