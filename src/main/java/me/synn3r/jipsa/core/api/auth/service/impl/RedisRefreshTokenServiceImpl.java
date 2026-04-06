package me.synn3r.jipsa.core.api.auth.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.api.auth.service.RefreshTokenService;
import me.synn3r.jipsa.core.global.config.security.JwtConfig;

@Service
@RequiredArgsConstructor
public class RedisRefreshTokenServiceImpl implements RefreshTokenService {

	private static final String KEY_PREFIX = "refresh_token:";

	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtConfig jwtConfig;

	@Override
	public void save(String userId, String tokenId) {
		String key = KEY_PREFIX + userId;
		redisTemplate.opsForValue().set(key, tokenId, jwtConfig.getRefreshTokenValidity(), TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean validate(String userId, String tokenId) {
		String key = KEY_PREFIX + userId;
		Object storedTokenId = redisTemplate.opsForValue().get(key);
		return tokenId.equals(storedTokenId);
	}

	@Override
	public void delete(String userId) {
		redisTemplate.delete(KEY_PREFIX + userId);
	}
}
