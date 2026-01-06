package me.synn3r.jipsa.core.api.member.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordConfirmValidator.class)
public @interface PasswordConfirm {

  String message() default "{validation.member.password.match}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String getPassword() default "password";

  String getPasswordConfirm() default "passwordConfirm";

}
