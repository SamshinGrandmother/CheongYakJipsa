package me.synn3r.jipsa.core.component.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.synn3r.jipsa.core.component.security.logging.AuthenticationSuccessLogger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DefaultAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final AuthenticationSuccessLogger authenticationSuccessLogger;
  private final static String AUTHENTICATION_SUCCESS_FORWARD_URL = "/calendar";

  public DefaultAuthenticationSuccessHandler(
    AuthenticationSuccessLogger authenticationSuccessLogger) {
    this.authenticationSuccessLogger = authenticationSuccessLogger;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
    Authentication authentication) throws IOException {
    authenticationSuccessLogger.saveAuthenticationSuccessHistory(
      (UserDetails) authentication.getPrincipal());
    response.sendRedirect(AUTHENTICATION_SUCCESS_FORWARD_URL);

  }
}
