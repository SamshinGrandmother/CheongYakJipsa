package me.synn3r.jipsa.core.component.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import me.synn3r.jipsa.core.component.security.enumerations.AuthenticationFailureType;
import me.synn3r.jipsa.core.component.security.logging.AuthenticationFailureLogger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

  private final AuthenticationFailureLogger authenticationFailureLogger;
  private final ObjectMapper objectMapper;
  // todo configuration 을 사용하여 자동주입받을 수 있게 변경
  private final String USERNAME_PARAMETER_NAME = "email";

  public DefaultAuthenticationFailureHandler(
    AuthenticationFailureLogger authenticationFailureLogger, ObjectMapper objectMapper) {
    this.authenticationFailureLogger = authenticationFailureLogger;
    this.objectMapper = objectMapper;
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
    AuthenticationException exception) {
    AuthenticationFailureType failureType = AuthenticationFailureType.valueOf(exception);
    authenticationFailureLogger.saveAuthenticationFailureHistory(
      request.getParameter(USERNAME_PARAMETER_NAME), failureType);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    try (Writer writer = response.getWriter()) {
      writer.write(objectMapper.writeValueAsString(failureType.getResponseText()));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
