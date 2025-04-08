package me.synn3r.jipsa.core.component.security.jwt.exception;

public class TokenParseException extends TokenException {

  public TokenParseException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public TokenParseException(String msg) {
    super(msg);
  }
}
