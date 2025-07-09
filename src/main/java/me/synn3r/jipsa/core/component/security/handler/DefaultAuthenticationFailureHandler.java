package me.synn3r.jipsa.core.component.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import me.synn3r.jipsa.core.component.security.enumerations.AuthenticationFailureType;
import me.synn3r.jipsa.core.component.security.logging.AuthenticationFailureLogger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationFailureHandler extends
  ExceptionMappingAuthenticationFailureHandler implements AuthenticationFailureHandler {

  private final AuthenticationFailureLogger authenticationFailureLogger;
  // todo configuration 을 사용하여 자동주입받을 수 있게 변경
  private final String USERNAME_PARAMETER_NAME = "email";
  private final String AUTHENTICATION_FAILURE_FORWARD_URL = "/login";
  private final String AUTHENTICATION_FAILURE_FORWARD_PATH =
    AUTHENTICATION_FAILURE_FORWARD_URL + "?reason=login.unknown";

  public DefaultAuthenticationFailureHandler(
    AuthenticationFailureLogger authenticationFailureLogger) {
    this.authenticationFailureLogger = authenticationFailureLogger;
    super.setDefaultFailureUrl(AUTHENTICATION_FAILURE_FORWARD_PATH);

    Map<String, String> failureUrlMap = new HashMap<>();
    failureUrlMap.put(UsernameNotFoundException.class.getName(),
      AUTHENTICATION_FAILURE_FORWARD_URL + "?reason=login.notfound");
    failureUrlMap.put(BadCredentialsException.class.getName(),
      AUTHENTICATION_FAILURE_FORWARD_URL + "?reason=login.notfound");
    super.setExceptionMappings(failureUrlMap);
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
    AuthenticationException exception) throws ServletException, IOException {
    AuthenticationFailureType failureType = AuthenticationFailureType.valueOf(exception);
    authenticationFailureLogger.saveAuthenticationFailureHistory(
      request.getParameter(USERNAME_PARAMETER_NAME), failureType);
    super.onAuthenticationFailure(request, response, exception);

  }
}
