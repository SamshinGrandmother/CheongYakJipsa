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

  String message() default "비밀번호가 일치하지 않습니다.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String getPassword() default "password";

  String getPasswordConfirm() default "passwordConfirm";

}
