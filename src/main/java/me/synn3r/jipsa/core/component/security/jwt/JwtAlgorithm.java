package me.synn3r.jipsa.core.component.security.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import lombok.Getter;

@Getter
public enum JwtAlgorithm {
  RS256(JWSAlgorithm.RS256),
  RS384(JWSAlgorithm.RS384),
  RS512(JWSAlgorithm.RS512),
  PS256(JWSAlgorithm.PS256),
  PS384(JWSAlgorithm.PS384),
  PS512(JWSAlgorithm.PS512),
  ES256(JWSAlgorithm.ES256),
  ES256K(JWSAlgorithm.ES256K),
  ES384(JWSAlgorithm.ES384),
  ES512(JWSAlgorithm.ES512),
  EdDSA(JWSAlgorithm.EdDSA),
  Ed25519(JWSAlgorithm.Ed25519),
  Ed448(JWSAlgorithm.Ed448),
  HS256(JWSAlgorithm.HS256),
  HS384(JWSAlgorithm.HS384),
  HS512(JWSAlgorithm.HS512),
  ;

  private final JWSAlgorithm jwsAlgorithm;

  JwtAlgorithm(JWSAlgorithm jwsAlgorithm) {
    this.jwsAlgorithm = jwsAlgorithm;
  }
}
