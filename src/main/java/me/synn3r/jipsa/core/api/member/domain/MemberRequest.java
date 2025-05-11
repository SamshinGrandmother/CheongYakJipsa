package me.synn3r.jipsa.core.api.member.domain;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.synn3r.jipsa.core.api.base.domain.Request.Insert;
import me.synn3r.jipsa.core.api.base.domain.Request.Update;
import me.synn3r.jipsa.core.api.base.domain.Request.UpdatePassword;
import me.synn3r.jipsa.core.api.member.validation.PasswordComplexity;
import me.synn3r.jipsa.core.api.member.validation.PasswordConfirm;
import me.synn3r.jipsa.core.component.security.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordConfirm(groups = {Insert.class, UpdatePassword.class})
public class MemberRequest {

  @NotNull(groups = {Update.class, UpdatePassword.class})
  @Min(value = 1, groups = {Update.class, UpdatePassword.class})
  private Long id;

  @NotEmpty(groups = {Insert.class})
  private String userId;

  @NotEmpty(groups = {Insert.class, Update.class}, message = "이름을 입력해 주세요.")
  private String name;

  @NotEmpty(groups = {Insert.class, Update.class,},message = "이메일을 입력해 주세요.")
  @Email(groups = {Insert.class, Update.class}, message = "이메일 형식대로 입력해 주세요")
  private String email;

  @NotNull(groups = {Insert.class})
  private Role role;

  @NotEmpty(groups = {Insert.class, UpdatePassword.class})
  @PasswordComplexity(groups = {Insert.class, UpdatePassword.class})
  private String password;

  @NotEmpty(groups = {Insert.class, UpdatePassword.class}, message="비밀번호 확인을 입력해 주세요.")
  private String passwordConfirm;

  @NotEmpty(groups = {Insert.class, Update.class},message = "전화번호를 입력해 주세요.")
  private String phoneNumber;

}
