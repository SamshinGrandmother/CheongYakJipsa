package me.synn3r.jipsa.core.component.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Date;
import me.synn3r.jipsa.core.component.security.jwt.exception.TokenException;
import me.synn3r.jipsa.core.component.security.jwt.exception.TokenExpiredException;
import me.synn3r.jipsa.core.component.security.jwt.exception.TokenParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider implements TokenProvider {

  private final JwtProperties jwtProperties;
  @Value("${spring.application.name}")
  private String issuer;

  public JwtProvider(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
  }

  @Override
  public void validateToken(String token) throws TokenException {
    try {
      SignedJWT parsedJwt = SignedJWT.parse(token);
      Date expire = parsedJwt.getJWTClaimsSet().getExpirationTime();
      if (expire.before(new Date())) {
        throw new TokenExpiredException("토큰이 만료되었습니다. ");
      }
    } catch (ParseException e) {
      throw new TokenParseException(e.getMessage(), e);
    }
  }

  @Override
  public String generateToken(Authentication authentication) {
    Date now = new Date();
    Date expire = new Date(now.getTime() + (1000L * 60 * jwtProperties.getMinute()));

    try {
      JWSHeader header = new JWSHeader(jwtProperties.getAlgorithm());
      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .claim(JWTClaimNames.ISSUED_AT, now)
        .claim(JWTClaimNames.EXPIRATION_TIME, expire)
        .claim(JWTClaimNames.ISSUER, issuer)
        .claim(JWTClaimNames.AUDIENCE, authentication.getPrincipal())
        .build();
      SignedJWT jwt = new SignedJWT(header, claimsSet);
      jwt.sign(jwtProperties.getSigner());
      return jwt.serialize();
    } catch (JOSEException e) {
      throw new TokenException(e.getMessage(), e);
    }

  }
}
