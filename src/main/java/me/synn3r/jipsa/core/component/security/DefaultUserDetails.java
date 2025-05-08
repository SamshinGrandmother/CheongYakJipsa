package me.synn3r.jipsa.core.component.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class DefaultUserDetails implements UserDetails {

  private long id;
  private String userId;
  private String email;
  private String name;
  private String password;
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(role);
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return userId;
  }

  public DefaultUserDetails(long id, String userId, String email, String name, String password, Role role) {
    this.id = id;
    this.userId = userId;
    this.email = email;
    this.name = name;
    this.password = password;
    this.role = role;
  }
}
