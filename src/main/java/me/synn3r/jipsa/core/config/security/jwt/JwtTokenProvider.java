package me.synn3r.jipsa.core.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;
import me.synn3r.jipsa.core.component.security.userdetails.DefaultUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private static final String AUTHORITIES = "auth";

  private final DefaultUserDetailsService userDetailsService;
  private final String secret;
  private final long expirationMinutes;
  private Key signingKey;

  public JwtTokenProvider(
    DefaultUserDetailsService userDetailsService,
    @Value("${jwt.secret}") String secret,
    @Value("${jwt.expiration-minutes:60}") long expirationMinutes) {
    this.userDetailsService = userDetailsService;
    this.secret = secret;
    this.expirationMinutes = expirationMinutes;
  }

  @PostConstruct
  void init() {
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(Authentication authentication) {
    Instant now = Instant.now();
    String authorities = authentication.getAuthorities().stream()
      .map(Object::toString)
      .collect(Collectors.joining(","));

    return Jwts.builder()
      .setSubject(authentication.getName())
      .claim(AUTHORITIES, authorities)
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
      .signWith(signingKey, SignatureAlgorithm.HS256)
      .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims claims = parseClaims(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
    return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
  }

  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(signingKey)
      .build()
      .parseClaimsJws(token)
      .getBody();
  }
}
