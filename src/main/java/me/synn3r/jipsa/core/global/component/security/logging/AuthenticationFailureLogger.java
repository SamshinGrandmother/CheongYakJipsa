package me.synn3r.jipsa.core.global.component.security.logging;

import me.synn3r.jipsa.core.global.component.security.enums.AuthenticationFailureType;

public interface AuthenticationFailureLogger {

	void saveAuthenticationFailureHistory(String username, AuthenticationFailureType failureType);
}
