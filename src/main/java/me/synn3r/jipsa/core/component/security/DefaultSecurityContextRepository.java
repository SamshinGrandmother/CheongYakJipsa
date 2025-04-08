package me.synn3r.jipsa.core.component.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import me.synn3r.jipsa.core.component.security.jwt.JwtProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DefaultSecurityContextRepository implements SecurityContextRepository {

  private final JwtProvider jwtProvider;
  private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
  private final Map<String, SecurityContext> securityContextMap = new HashMap<>();
  private static final String TOKEN_KEY = HttpHeaders.AUTHORIZATION;
  private static final String TOKEN_PREFIX = "Bearer ";

  public DefaultSecurityContextRepository(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  @Override
  public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
    String header = readTokenByHeader(requestResponseHolder.getRequest());
    if (StringUtils.hasText(header)) {
      jwtProvider.validateToken(header);

      return securityContextMap.get(header);
    }
    return securityContextHolderStrategy.createEmptyContext();
  }

  @Override
  public void saveContext(SecurityContext context, HttpServletRequest request,
    HttpServletResponse response) {
    String token = jwtProvider.generateToken(context.getAuthentication());
    securityContextMap.put(token, context);
    response.addHeader(TOKEN_KEY, TOKEN_PREFIX + token);
  }

  @Override
  public boolean containsContext(HttpServletRequest request) {
    String header = readTokenByHeader(request);

    return securityContextMap.containsKey(header);
  }


  protected String readTokenByHeader(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(TOKEN_KEY)).orElse("").replace(TOKEN_PREFIX, "");
  }
}
