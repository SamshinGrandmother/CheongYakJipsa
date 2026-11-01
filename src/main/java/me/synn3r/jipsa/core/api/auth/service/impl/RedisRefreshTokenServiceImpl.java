package me.synn3r.jipsa.core.api.auth.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.api.auth.service.RefreshTokenService;
import me.synn3r.jipsa.core.global.config.security.JwtConfig;

/**
 * Redis를 백엔드로 사용하는 {@link RefreshTokenService} 구현체.
 *
 * <h2>Redis 키 구조</h2>
 * <pre>
 * Key  : "refresh_token:{userId}"
 * Value: tokenId (UUID 문자열)
 * TTL  : JwtConfig.refreshTokenValidity (기본 14일)
 * </pre>
 *
 * <p>userId 하나당 하나의 키만 유지하여, 마지막으로 발급된 Refresh Token의
 * tokenId만 유효 상태로 관리합니다. 토큰 갱신(Rotation)은 기존 값이 일치할 때만
 * 교체하므로, 로그아웃으로 삭제된 키를 갱신 요청이 되살릴 수 없습니다.</p>
 *
 * @author synn3r
 * @see RefreshTokenService
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class RedisRefreshTokenServiceImpl implements RefreshTokenService {

	/**
	 * Redis 키 접두어.
	 * 실제 키는 {@code "refresh_token:{userId}"} 형태입니다.
	 */
	private static final String KEY_PREFIX = "refresh_token:";
	private static final DefaultRedisScript<Long> ROTATE_SCRIPT = new DefaultRedisScript<>("""
		if redis.call('GET', KEYS[1]) == ARGV[1] then
		    redis.call('SET', KEYS[1], ARGV[2], 'PX', ARGV[3])
		    return 1
		end
		return 0
		""", Long.class);

	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtConfig jwtConfig;

	/**
	 * {@inheritDoc}
	 *
	 * <p>Redis에 {@code "refresh_token:{userId}"} 키로 tokenId를 저장하며,
	 * TTL은 {@link JwtConfig#getRefreshTokenValidity()} 밀리초로 설정됩니다.</p>
	 */
	@Override
	public void save(String userId, String tokenId) {
		String key = KEY_PREFIX + userId;
		redisTemplate.opsForValue().set(key, tokenId, jwtConfig.getRefreshTokenValidity(), TimeUnit.MILLISECONDS);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Redis에서 해당 키의 값을 조회하여 전달된 tokenId와 비교합니다.
	 * 키가 만료되었거나 존재하지 않으면 {@code null}이 반환되므로 {@code false}를 반환합니다.</p>
	 */
	@Override
	public boolean validate(String userId, String tokenId) {
		if (!StringUtils.hasText(userId) || !StringUtils.hasText(tokenId)) {
			return false;
		}
		String key = KEY_PREFIX + userId;
		Object storedTokenId = redisTemplate.opsForValue().get(key);
		return tokenId.equals(storedTokenId);
	}

	@Override
	public boolean rotate(String userId, String expectedTokenId, String newTokenId) {
		if (!StringUtils.hasText(userId) || !StringUtils.hasText(expectedTokenId)
			|| !StringUtils.hasText(newTokenId)) {
			return false;
		}
		Long result = redisTemplate.execute(ROTATE_SCRIPT, List.of(KEY_PREFIX + userId),
			expectedTokenId, newTokenId, Long.toString(jwtConfig.getRefreshTokenValidity()));
		return Long.valueOf(1L).equals(result);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Redis에서 {@code "refresh_token:{userId}"} 키를 삭제합니다.
	 * 키가 존재하지 않더라도 예외가 발생하지 않습니다.</p>
	 */
	@Override
	public void delete(String userId) {
		redisTemplate.delete(KEY_PREFIX + userId);
	}
}
