package me.synn3r.jipsa.core.global.component.security.enums;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import lombok.Getter;

@Getter
public enum AuthenticationFailureType {
	BAD_CREDENTIALS("security.exception.BadCredentials", null, BadCredentialsException.class),
	USERNAME_NOT_FOUND("security.exception.UsernameNotFound", AuthenticationFailureType.BAD_CREDENTIALS.text,
		UsernameNotFoundException.class),
	ACCOUNT_EXPIRED("security.exception.AccountExpired", null, AccountExpiredException.class),
	PASSWORD_EXPIRED("security.exception.PasswordExpired", null, CredentialsExpiredException.class),
	ALREADY_LOCKED("security.exception.AlreadyLocked", null, LockedException.class),
	ETC("security.exception.Etc", null, AuthenticationException.class);

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
