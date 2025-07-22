package me.synn3r.jipsa.core.component.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationFailureHandler extends
  ExceptionMappingAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final String AUTHENTICATION_FAILURE_FORWARD_URL = "/login";
    private final String AUTHENTICATION_FAILURE_FORWARD_PATH =
      AUTHENTICATION_FAILURE_FORWARD_URL + "?reason=login.unknown";

    public DefaultAuthenticationFailureHandler() {
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
        super.onAuthenticationFailure(request, response, exception);

    }
}
