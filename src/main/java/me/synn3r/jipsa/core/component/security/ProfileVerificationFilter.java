package me.synn3r.jipsa.core.component.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ProfileVerificationFilter extends OncePerRequestFilter {

  private static final String VERIFICATION_ATTRIBUTE_NAME = "PROFILE_VERIFIED";
  private static final String PROFILE_URI = "/profile";
  private static final String PROFILE_VERIFICATION_URI = "/profile/verify";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
    FilterChain filterChain) throws ServletException, IOException {
    String uri = request.getRequestURI();
    HttpSession session = request.getSession(false);

    boolean isVerified =
      session != null && Boolean.TRUE.equals(session.getAttribute(VERIFICATION_ATTRIBUTE_NAME));

    if (uri.startsWith(PROFILE_URI) && !uri.startsWith(PROFILE_VERIFICATION_URI) && !isVerified) {
      response.sendRedirect(PROFILE_VERIFICATION_URI);
      return;
    }

    filterChain.doFilter(request, response);

  }
}
