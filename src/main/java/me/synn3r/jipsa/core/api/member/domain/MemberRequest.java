package me.synn3r.jipsa.core.api.member.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synn3r.jipsa.core.api.base.domain.Request.Insert;
import me.synn3r.jipsa.core.api.base.domain.Request.Update;
import me.synn3r.jipsa.core.api.base.domain.Request.UpdatePassword;
import me.synn3r.jipsa.core.component.security.Role;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {

  @NotNull(groups = {Update.class, UpdatePassword.class})
  @Min(value = 1, groups = {Update.class, UpdatePassword.class})
  private Long id;

  @NotEmpty(groups = {Insert.class, Update.class})
  private String name;

  @NotEmpty(groups = {Insert.class})
  @Email(groups = {Insert.class})
  private String email;

  @NotNull(groups = {Insert.class})
  private Role role;

  @NotEmpty(groups = {Insert.class, UpdatePassword.class})
  private String password;
  @NotEmpty(groups = {Insert.class, UpdatePassword.class})
  private String passwordConfirm;

}
