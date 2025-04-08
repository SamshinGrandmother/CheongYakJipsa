package me.synn3r.jipsa.core.component.security.jwt;

import me.synn3r.jipsa.core.component.security.jwt.exception.TokenException;
import org.springframework.security.core.Authentication;

public interface TokenProvider {

  void validateToken(String token) throws TokenException;

  String generateToken(Authentication authentication);

}
