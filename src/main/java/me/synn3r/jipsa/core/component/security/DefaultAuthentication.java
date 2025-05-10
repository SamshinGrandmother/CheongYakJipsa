package me.synn3r.jipsa.core.component.security;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class DefaultAuthentication extends AbstractAuthenticationToken {

  private String userId;
  private String password;


  public DefaultAuthentication(
    Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
  }

  public DefaultAuthentication(String userId, String password) {
    super(null);
    this.userId = userId;
    this.password = password;
  }

  @Override
  public Object getCredentials() {
    return password;
  }

  @Override
  public Object getPrincipal() {
    return userId;
  }
}
