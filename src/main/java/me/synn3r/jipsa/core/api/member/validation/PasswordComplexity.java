package me.synn3r.jipsa.core.api.member.validation;


import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordComplexityValidator.class)
public @interface PasswordComplexity {

  String message() default "비밀번호는 대/소문자, 숫자, 특수문자 포함 8글자 이상이어야 합니다.";

  Class[] groups() default {};

  Class[] payload() default {};


}
