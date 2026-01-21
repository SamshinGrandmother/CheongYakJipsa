package me.synn3r.jipsa.core.api.auth.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {

  @NotBlank(message = "Refresh Token을 입력해주세요.")
  private String refreshToken;
}
