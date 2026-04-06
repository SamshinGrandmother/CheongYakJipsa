package me.synn3r.jipsa.core.component.security.enumerations;

import lombok.Getter;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

@Getter
public enum AuthenticationFailureType {
  BAD_CREDENTIALS("비밀번호 미일치", null, BadCredentialsException.class),
  USERNAME_NOT_FOUND("사용자 ID를 찾을 수 없음", AuthenticationFailureType.BAD_CREDENTIALS.text,
    UsernameNotFoundException.class),
  ACCOUNT_EXPIRED("계정 만료", null, AccountExpiredException.class),
  PASSWORD_EXPIRED("비밀번호 만료", null, CredentialsExpiredException.class),
  ALREADY_LOCKED("계정 잠김", null, LockedException.class),
  ETC("기타", null, AuthenticationException.class);

  private final String text;
  private final String responseText;
  private final Class<? extends AuthenticationException> exception;

  AuthenticationFailureType(String text, String responseText,
    Class<? extends AuthenticationException> exception) {
    this.text = text;
    this.responseText = responseText;
    this.exception = exception;
  }

  public static <T extends AuthenticationException> AuthenticationFailureType valueOf(T exception) {
    AuthenticationFailureType[] values = values();
    for (AuthenticationFailureType value : values) {
      if (value.exception.isAssignableFrom(exception.getClass())) {
        return value;
      }
    }
    throw new IllegalArgumentException("열거형에 해당하는 예외가 없습니다. ");
  }

  public String getResponseText() {
    return StringUtils.hasText(responseText) ? responseText : text;
  }
}
