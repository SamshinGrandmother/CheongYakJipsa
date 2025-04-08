package me.synn3r.jipsa.core.component.security.jwt.exception;

public class TokenExpiredException extends TokenException {

  public TokenExpiredException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public TokenExpiredException(String msg) {
    super(msg);
  }
}
