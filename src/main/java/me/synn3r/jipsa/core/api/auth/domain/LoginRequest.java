package me.synn3r.jipsa.core.api.auth.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  @NotBlank(message = "사용자 ID를 입력해주세요.")
  private String userId;

  @NotBlank(message = "비밀번호를 입력해주세요.")
  private String password;
}
