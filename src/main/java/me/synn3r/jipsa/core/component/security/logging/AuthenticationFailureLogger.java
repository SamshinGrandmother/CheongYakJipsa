package me.synn3r.jipsa.core.component.security.logging;

import me.synn3r.jipsa.core.component.security.enumerations.AuthenticationFailureType;

public interface AuthenticationFailureLogger {

  void saveAuthenticationFailureHistory(String username, AuthenticationFailureType failureType);
}
