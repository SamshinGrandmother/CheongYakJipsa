package me.synn3r.jipsa.core.component.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import me.synn3r.jipsa.core.component.validation.validator.PasswordComplexityValidator;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordComplexityValidator.class)
public @interface PasswordComplexityValid {
	String message() default "{jakarta.validation.constraints.PasswordComplexity.message}";

	Class[] groups() default {};

	Class[] payload() default {};
}
