package me.synn3r.jipsa.core.mvc.base.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import me.synn3r.jipsa.core.component.validation.domain.Password;

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

	@Valid
	private Password password = new Password();
}
