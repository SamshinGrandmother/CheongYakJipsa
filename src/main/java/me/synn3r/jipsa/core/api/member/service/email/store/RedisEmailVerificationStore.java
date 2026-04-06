package me.synn3r.jipsa.core.api.member.service.email.store;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis 기반 이메일 인증 정보 저장소 구현체.
 *
 * <p>Redis의 TTL(Time-To-Live) 기능을 활용하여 인증 코드의
 * 자동 만료를 지원합니다. 빠른 조회 성능을 제공하며,
 * 분산 환경에서도 일관된 인증 상태를 유지합니다.</p>
 *
 * <h2>키 구조</h2>
 * <table border="1">
 *   <tr>
 *     <th>용도</th>
 *     <th>키 패턴</th>
 *     <th>예시</th>
 *   </tr>
 *   <tr>
 *     <td>인증 코드 저장</td>
 *     <td>{@code email:verification:{email}}</td>
 *     <td>{@code email:verification:user@example.com}</td>
 *   </tr>
 *   <tr>
 *     <td>인증 완료 상태</td>
 *     <td>{@code email:verified:{email}}</td>
 *     <td>{@code email:verified:user@example.com}</td>
 *   </tr>
 * </table>
 *
 * <h2>만료 정책</h2>
 * <ul>
 *   <li>인증 코드: 설정된 만료 시간 후 자동 삭제 (기본 5분)</li>
 *   <li>인증 완료 상태: 24시간 유지 (회원가입 완료 대기)</li>
 * </ul>
 *
 * <h2>장애 처리</h2>
 * <p>Redis 연결 실패 시 예외가 발생하며, 상위 레이어에서
 * try-catch로 처리하여 다른 저장소(DB)로 폴백합니다.</p>
 *
 * @author synn3r
 * @since 1.0
 * @see EmailVerificationStore
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisEmailVerificationStore implements EmailVerificationStore {

	private static final String VERIFICATION_KEY_PREFIX = "email:verification:";
	private static final String VERIFIED_KEY_PREFIX = "email:verified:";
	private static final long VERIFIED_EXPIRATION_HOURS = 24;

	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * {@inheritDoc}
	 *
	 * <p>Redis에 인증 코드를 저장하고 TTL을 설정합니다.
	 * 동일 이메일에 대한 기존 데이터는 자동으로 덮어쓰기 됩니다.</p>
	 */
	@Override
	public void save(String email, String verificationData, long expirationMinutes) {
		String key = VERIFICATION_KEY_PREFIX + email;
		redisTemplate.opsForValue().set(key, verificationData, expirationMinutes, TimeUnit.MINUTES);
		log.debug("Redis에 인증 코드 저장: email={}, expiration={}분", email, expirationMinutes);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Redis에서 인증 코드를 조회합니다.
	 * TTL이 만료된 키는 Redis에서 자동 삭제되므로 별도의 만료 체크가 불필요합니다.</p>
	 */
	@Override
	public String find(String email) {
		String key = VERIFICATION_KEY_PREFIX + email;
		Object value = redisTemplate.opsForValue().get(key);
		return value != null ? value.toString() : null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Redis에서 인증 코드를 삭제합니다.</p>
	 */
	@Override
	public void delete(String email) {
		String key = VERIFICATION_KEY_PREFIX + email;
		redisTemplate.delete(key);
		log.debug("Redis에서 인증 코드 삭제: email={}", email);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Redis에 인증 완료 상태를 저장합니다.
	 * 24시간 TTL을 설정하여 회원가입 완료 전까지 상태를 유지합니다.</p>
	 */
	@Override
	public void markAsVerified(String email) {
		String key = VERIFIED_KEY_PREFIX + email;
		redisTemplate.opsForValue().set(key, "true", VERIFIED_EXPIRATION_HOURS, TimeUnit.HOURS);
		log.debug("Redis에 인증 완료 상태 저장: email={}", email);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Redis에서 인증 완료 상태를 확인합니다.</p>
	 */
	@Override
	public boolean isVerified(String email) {
		String key = VERIFIED_KEY_PREFIX + email;
		Object value = redisTemplate.opsForValue().get(key);
		return "true".equals(value != null ? value.toString() : null);
	}
}
