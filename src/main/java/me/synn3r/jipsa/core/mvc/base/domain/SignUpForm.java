package me.synn3r.jipsa.core.mvc.base.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpForm {
	@NotBlank
	private String name;
	@NotBlank
	private String userId;
	@NotBlank
	@Email
	private String email;
	@NotBlank
	private String password;
	@NotBlank
	private String passwordConfirm;
}
