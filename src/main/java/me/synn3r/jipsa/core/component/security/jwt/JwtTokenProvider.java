package me.synn3r.jipsa.core.component.security.jwt;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import me.synn3r.jipsa.core.component.security.Role;
import me.synn3r.jipsa.core.component.security.userdetails.DefaultUserDetails;
import me.synn3r.jipsa.core.config.security.JwtConfig;

@Slf4j
@Component
public class JwtTokenProvider {

	private final SecretKey secretKey;
	private final long accessTokenValidity;
	private final long refreshTokenValidity;
	private final long profileVerificationTokenValidity;
	private final UserDetailsService userDetailsService;

	public JwtTokenProvider(JwtConfig jwtConfig, UserDetailsService userDetailsService) {
		this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
		this.accessTokenValidity = jwtConfig.getAccessTokenValidity();
		this.refreshTokenValidity = jwtConfig.getRefreshTokenValidity();
		this.profileVerificationTokenValidity = jwtConfig.getProfileVerificationTokenValidity();
		this.userDetailsService = userDetailsService;
	}

	public String createAccessToken(DefaultUserDetails userDetails) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + accessTokenValidity);

		return Jwts.builder()
			.subject(userDetails.getUsername())
			.claim("id", userDetails.getId())
			.claim("email", userDetails.getEmail())
			.claim("name", userDetails.getName())
			.claim("role", userDetails.getRole().name())
			.issuedAt(now)
			.expiration(expiryDate)
			.signWith(secretKey)
			.compact();
	}

	public String createRefreshToken(String userId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + refreshTokenValidity);

		return Jwts.builder()
			.subject(userId)
			.claim("tokenId", UUID.randomUUID().toString())
			.claim("type", "refresh")
			.issuedAt(now)
			.expiration(expiryDate)
			.signWith(secretKey)
			.compact();
	}

	public String createProfileVerificationToken(String userId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + profileVerificationTokenValidity);

		return Jwts.builder()
			.subject(userId)
			.claim("type", "profile_verification")
			.issuedAt(now)
			.expiration(expiryDate)
			.signWith(secretKey)
			.compact();
	}

	public Authentication getAuthentication(String token) {
		Claims claims = parseClaims(token);
		String userId = claims.getSubject();

		DefaultUserDetails userDetails =
			(DefaultUserDetails)userDetailsService.loadUserByUsername(userId);

		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public Authentication getAuthenticationFromClaims(String token) {
		Claims claims = parseClaims(token);

		long id = claims.get("id", Long.class);
		String userId = claims.getSubject();
		String email = claims.get("email", String.class);
		String name = claims.get("name", String.class);
		Role role = Role.valueOf(claims.get("role", String.class));

		DefaultUserDetails userDetails = new DefaultUserDetails(id, userId, email, name, "", role);

		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String getUserIdFromToken(String token) {
		return parseClaims(token).getSubject();
	}

	public String getTokenIdFromRefreshToken(String token) {
		return parseClaims(token).get("tokenId", String.class);
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}

	public boolean validateProfileVerificationToken(String token) {
		try {
			Claims claims = parseClaims(token);
			String type = claims.get("type", String.class);
			return "profile_verification".equals(type);
		} catch (Exception e) {
			return false;
		}
	}

	public long getAccessTokenValidity() {
		return accessTokenValidity;
	}

	public long getRefreshTokenValidity() {
		return refreshTokenValidity;
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}
