package me.synn3r.jipsa.core.global.component.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import me.synn3r.jipsa.core.global.component.validation.validator.PasswordComplexityValidator;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordComplexityValidator.class)
public @interface PasswordComplexity {

	String message() default "{member.validation.password.complexity}";

	Class[] groups() default {};

	Class[] payload() default {};

}
