package me.synn3r.jipsa.core.api.auth.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthRequest {

  @NotBlank(message = "{validation.auth.user-id.required}")
  private String userId;

  @NotBlank(message = "{validation.auth.password.required}")
  private String password;
}
