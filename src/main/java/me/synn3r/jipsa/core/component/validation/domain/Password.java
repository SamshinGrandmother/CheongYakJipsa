package me.synn3r.jipsa.core.component.validation.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import me.synn3r.jipsa.core.component.validation.annotation.PasswordComplexityValid;
import me.synn3r.jipsa.core.component.validation.annotation.PasswordConfirmValid;

@Getter
@Setter
@PasswordComplexityValid
@PasswordConfirmValid
public class Password implements ComplexPassword, ConfirmablePassword {
	@NotBlank
	private String password;
	@NotBlank
	private String passwordConfirm;
}
