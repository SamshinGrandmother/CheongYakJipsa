package me.synn3r.jipsa.core.api.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synn3r.jipsa.core.api.base.domain.Response;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordVerifyResponse implements Response {

  private String verificationToken;
  private long expiresIn;
}
