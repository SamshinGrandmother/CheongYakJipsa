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

  String message() default "{validation.member.password.complexity}";

  Class[] groups() default {};

  Class[] payload() default {};


}
