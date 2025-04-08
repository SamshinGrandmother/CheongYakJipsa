package me.synn3r.jipsa.core.component.security.jwt.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenException extends AuthenticationException {

  public TokenException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public TokenException(String msg) {
    super(msg);
  }
}
