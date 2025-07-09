package me.synn3r.jipsa.core.api.member.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class PasswordConfirmValidator implements ConstraintValidator<PasswordConfirm, Object> {

  private String password;
  private String passwordConfirm;


  @Override
  public void initialize(PasswordConfirm constraintAnnotation) {
    this.password = constraintAnnotation.getPassword();
    this.passwordConfirm = constraintAnnotation.getPasswordConfirm();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {

    Object getPassword = new BeanWrapperImpl(value).getPropertyValue(password);
    Object getPasswordConfirm = new BeanWrapperImpl(value).getPropertyValue(passwordConfirm);

    if (getPassword == null || getPasswordConfirm == null) {
      return false;
    }

    return (getPassword.toString()).equals(getPasswordConfirm.toString());
  }
}
