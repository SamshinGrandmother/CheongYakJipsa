package me.synn3r.jipsa.core.component.security.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import java.security.InvalidAlgorithmParameterException;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jipsa.security.jwt")
@Getter
public class JwtProperties {

  private final JWSSigner signer;
  private final int minute;
  private final JwtAlgorithm algorithm;

  public JwtProperties(int minute, String secretKey, JwtAlgorithm algorithm)
    throws KeyLengthException, InvalidAlgorithmParameterException {
    this.minute = minute;
    this.algorithm = algorithm;
    signer = new MACSigner(secretKey);

    if (!signer.supportedJWSAlgorithms().contains(algorithm.getJwsAlgorithm())) {
      throw new InvalidAlgorithmParameterException(
        String.format("%s 알고리즘은 키 길이를 %d 로 사용할 수 없습니다. ", algorithm.name(), secretKey.length()));
    }
  }

  public JWSAlgorithm getAlgorithm() {
    return algorithm.getJwsAlgorithm();
  }
}
