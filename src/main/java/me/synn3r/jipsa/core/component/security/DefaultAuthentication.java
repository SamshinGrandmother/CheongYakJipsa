package me.synn3r.jipsa.core.component.security;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class DefaultAuthentication extends AbstractAuthenticationToken {

  private String email;
  private String password;


  public DefaultAuthentication(
    Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
  }

  public DefaultAuthentication(String email, String password) {
    super(null);
    this.email = email;
    this.password = password;
  }

  @Override
  public Object getCredentials() {
    return password;
  }

  @Override
  public Object getPrincipal() {
    return email;
  }
}
