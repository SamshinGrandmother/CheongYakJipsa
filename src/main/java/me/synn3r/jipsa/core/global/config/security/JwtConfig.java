package me.synn3r.jipsa.core.global.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

	private String secret;
	private long accessTokenValidity;
	private long refreshTokenValidity;
	private long profileVerificationTokenValidity;
}
