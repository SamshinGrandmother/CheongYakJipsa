package me.synn3r.jipsa.core.component.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final static String AUTHENTICATION_SUCCESS_FORWARD_URL = "/calendar";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
        response.sendRedirect(AUTHENTICATION_SUCCESS_FORWARD_URL);

    }
}
