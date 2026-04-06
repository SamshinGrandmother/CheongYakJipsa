package me.synn3r.jipsa.core.api.member.domain;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.synn3r.jipsa.core.api.commons.domain.Request.Insert;
import me.synn3r.jipsa.core.api.commons.domain.Request.Update;
import me.synn3r.jipsa.core.api.commons.domain.Request.UpdatePassword;
import me.synn3r.jipsa.core.global.component.security.enums.Role;
import me.synn3r.jipsa.core.global.component.validation.annotation.PasswordComplexity;
import me.synn3r.jipsa.core.global.component.validation.annotation.PasswordConfirm;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordConfirm(groups = {Insert.class, UpdatePassword.class})
public class MemberRequest {

	@NotNull(groups = {Update.class, UpdatePassword.class}, message = "{member.validation.id.notNull}")
	@Min(value = 1, groups = {Update.class, UpdatePassword.class}, message = "{member.validation.id.min}")
	private Long id;

	@NotEmpty(groups = {Insert.class}, message = "{member.validation.userId.notEmpty}")
	private String userId;

	@NotEmpty(groups = {Insert.class, Update.class}, message = "{member.validation.name.notEmpty}")
	private String name;

	@NotEmpty(groups = {Insert.class, Update.class}, message = "{member.validation.email.notEmpty}")
	@Email(groups = {Insert.class, Update.class}, message = "{member.validation.email.invalid}")
	private String email;

	@NotNull(groups = {Insert.class}, message = "{member.validation.role.notNull}")
	private Role role;

	@NotEmpty(groups = {Insert.class, UpdatePassword.class}, message = "{member.validation.password.notEmpty}")
	@PasswordComplexity(groups = {Insert.class, UpdatePassword.class})
	private String password;

	@NotEmpty(groups = {Insert.class, UpdatePassword.class}, message = "{member.validation.passwordConfirm.notEmpty}")
	private String passwordConfirm;

	@NotEmpty(groups = {Insert.class, Update.class}, message = "{member.validation.phoneNumber.notEmpty}")
	private String phoneNumber;

	@AssertTrue(groups = {Insert.class, Update.class}, message = "{member.validation.isEmailVerified.assertTrue}")
	private Boolean isEmailVerified;

}
