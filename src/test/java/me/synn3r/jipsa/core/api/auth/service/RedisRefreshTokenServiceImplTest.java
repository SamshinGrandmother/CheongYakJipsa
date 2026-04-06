package me.synn3r.jipsa.core.api.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import me.synn3r.jipsa.core.api.auth.service.impl.RedisRefreshTokenServiceImpl;
import me.synn3r.jipsa.core.global.config.security.JwtConfig;

@ExtendWith(MockitoExtension.class)
@DisplayName("Redis Refresh Token 서비스 테스트")
class RedisRefreshTokenServiceImplTest {

	private static final String USER_ID = "testUser";
	private static final String TOKEN_ID = "test-token-id-uuid";
	private static final String KEY_PREFIX = "refresh_token:";
	private static final long TTL = 1209600000L;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	@Mock
	private JwtConfig jwtConfig;

	@InjectMocks
	private RedisRefreshTokenServiceImpl refreshTokenService;

	@Test
	@DisplayName("save() 호출 시 Redis에 올바른 key와 TTL로 tokenId가 저장되어야 함")
	void save() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(jwtConfig.getRefreshTokenValidity()).thenReturn(TTL);

		refreshTokenService.save(USER_ID, TOKEN_ID);

		verify(valueOperations).set(KEY_PREFIX + USER_ID, TOKEN_ID, TTL, TimeUnit.MILLISECONDS);
	}

	@Test
	@DisplayName("validate() - Redis에 저장된 tokenId와 일치하면 true를 반환해야 함")
	void validateSuccess() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(KEY_PREFIX + USER_ID)).thenReturn(TOKEN_ID);

		assertTrue(refreshTokenService.validate(USER_ID, TOKEN_ID));
	}

	@Test
	@DisplayName("validate() - Redis에 해당 userId의 key가 없으면 false를 반환해야 함")
	void validateKeyNotFound() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(KEY_PREFIX + USER_ID)).thenReturn(null);

		assertFalse(refreshTokenService.validate(USER_ID, TOKEN_ID));
	}

	@Test
	@DisplayName("validate() - Redis에 저장된 tokenId와 불일치하면 false를 반환해야 함")
	void validateTokenIdMismatch() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(KEY_PREFIX + USER_ID)).thenReturn("different-token-id");

		assertFalse(refreshTokenService.validate(USER_ID, TOKEN_ID));
	}

	@Test
	@DisplayName("delete() 호출 시 Redis에서 해당 userId의 key가 삭제되어야 함")
	void delete() {
		refreshTokenService.delete(USER_ID);

		verify(redisTemplate).delete(KEY_PREFIX + USER_ID);
	}
}
