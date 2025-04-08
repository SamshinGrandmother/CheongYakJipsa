package me.synn3r.jipsa.core.component.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.synn3r.jipsa.core.component.security.logging.AuthenticationSuccessLogger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final AuthenticationSuccessLogger authenticationSuccessLogger;

  public DefaultAuthenticationSuccessHandler(
    AuthenticationSuccessLogger authenticationSuccessLogger) {
    this.authenticationSuccessLogger = authenticationSuccessLogger;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
    Authentication authentication) {
    authenticationSuccessLogger.saveAuthenticationSuccessHistory(
      (UserDetails) authentication.getPrincipal());

  }
}
