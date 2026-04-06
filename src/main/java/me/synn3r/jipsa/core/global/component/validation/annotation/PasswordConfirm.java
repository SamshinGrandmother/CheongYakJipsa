package me.synn3r.jipsa.core.global.component.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import me.synn3r.jipsa.core.global.component.validation.validator.PasswordConfirmValidator;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordConfirmValidator.class)
public @interface PasswordConfirm {

	String message() default "{member.validation.password.confirm}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
