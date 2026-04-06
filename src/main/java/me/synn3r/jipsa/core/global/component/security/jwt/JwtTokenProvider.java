package me.synn3r.jipsa.core.global.component.security.jwt;

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
import me.synn3r.jipsa.core.global.component.security.enums.Role;
import me.synn3r.jipsa.core.global.component.security.userdetails.DefaultUserDetails;
import me.synn3r.jipsa.core.global.config.security.JwtConfig;

@Slf4j
@Component
public class JwtTokenProvider {

	private static final String CLAIM_ID = "id";
	private static final String CLAIM_EMAIL = "email";
	private static final String CLAIM_NAME = "name";
	private static final String CLAIM_ROLE = "role";
	private static final String CLAIM_TOKEN_ID = "tokenId";
	private static final String CLAIM_TYPE = "type";
	private static final String TOKEN_TYPE_REFRESH = "refresh";
	private static final String TOKEN_TYPE_PROFILE_VERIFICATION = "profile_verification";

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
			.claim(CLAIM_ID, userDetails.getId())
			.claim(CLAIM_EMAIL, userDetails.getEmail())
			.claim(CLAIM_NAME, userDetails.getName())
			.claim(CLAIM_ROLE, userDetails.getRole().name())
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
			.claim(CLAIM_TOKEN_ID, UUID.randomUUID().toString())
			.claim(CLAIM_TYPE, TOKEN_TYPE_REFRESH)
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
			.claim(CLAIM_TYPE, TOKEN_TYPE_PROFILE_VERIFICATION)
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

		long id = claims.get(CLAIM_ID, Long.class);
		String userId = claims.getSubject();
		String email = claims.get(CLAIM_EMAIL, String.class);
		String name = claims.get(CLAIM_NAME, String.class);
		Role role = Role.valueOf(claims.get(CLAIM_ROLE, String.class));

		DefaultUserDetails userDetails = new DefaultUserDetails(id, userId, email, name, "", role);

		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String getUserIdFromToken(String token) {
		return parseClaims(token).getSubject();
	}

	public String getTokenIdFromRefreshToken(String token) {
		return parseClaims(token).get(CLAIM_TOKEN_ID, String.class);
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

	public boolean isRefreshToken(String token) {
		try {
			Claims claims = parseClaims(token);
			return TOKEN_TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class));
		} catch (Exception e) {
			return false;
		}
	}

	public boolean validateProfileVerificationToken(String token) {
		try {
			Claims claims = parseClaims(token);
			String type = claims.get(CLAIM_TYPE, String.class);
			return TOKEN_TYPE_PROFILE_VERIFICATION.equals(type);
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
