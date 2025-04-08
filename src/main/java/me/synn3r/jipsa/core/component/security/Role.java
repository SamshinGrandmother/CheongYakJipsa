package me.synn3r.jipsa.core.component.security;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
  NORMAL,
  ADMIN;


  @Override
  public String getAuthority() {
    return name();
  }
}
