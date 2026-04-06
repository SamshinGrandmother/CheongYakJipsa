package me.synn3r.jipsa.core.api.member.service.email.store;

/**
 * 이메일 인증 정보 저장소 인터페이스.
 *
 * <p>이메일 인증 코드 또는 토큰을 저장하고 조회하는 기능을 정의합니다.
 * Redis와 Database 구현체가 존재하며, 장애 대비를 위해
 * 모든 구현체에 동시에 저장됩니다.</p>
 *
 * <h2>구현체</h2>
 * <ul>
 *   <li>{@link RedisEmailVerificationStore} - Redis 기반 저장소 (TTL 지원)</li>
 *   <li>{@link DatabaseEmailVerificationStore} - MariaDB 기반 저장소</li>
 * </ul>
 *
 * <h2>사용 예시</h2>
 * <pre>{@code
 * // 여러 저장소에 동시 저장 (장애 대비)
 * for (EmailVerificationStore store : verificationStores) {
 *     try {
 *         store.save(email, code, 5);
 *     } catch (Exception e) {
 *         log.warn("저장소 저장 실패: {}", e.getMessage());
 *     }
 * }
 * }</pre>
 *
 * <h2>장애 대비 설계</h2>
 * <p>Redis 또는 DB 중 하나의 저장소가 장애 상태이더라도
 * 나머지 저장소를 통해 인증 프로세스를 유지할 수 있도록 설계되었습니다.
 * 저장 시 모든 저장소에 저장하고, 조회 시 하나라도 성공하면 인증이 가능합니다.</p>
 *
 * @author synn3r
 * @since 1.0
 * @see RedisEmailVerificationStore
 * @see DatabaseEmailVerificationStore
 */
public interface EmailVerificationStore {

	/**
	 * 이메일 인증 정보를 저장합니다.
	 *
	 * <p>동일 이메일에 대해 기존 인증 정보가 있으면 덮어쓰기 됩니다.</p>
	 *
	 * @param email             인증 대상 이메일 주소
	 * @param verificationData  인증 코드 (6자리 숫자) 또는 토큰 (UUID)
	 * @param expirationMinutes 만료 시간(분) - Redis는 TTL, DB는 expiresAt 필드로 관리
	 */
	void save(String email, String verificationData, long expirationMinutes);

	/**
	 * 저장된 인증 정보를 조회합니다.
	 *
	 * <p>만료된 인증 정보는 반환하지 않습니다.</p>
	 *
	 * @param email 조회할 이메일 주소
	 * @return 저장된 인증 코드 또는 토큰, 없거나 만료된 경우 {@code null}
	 */
	String find(String email);

	/**
	 * 인증 정보를 삭제합니다.
	 *
	 * <p>인증 완료 후 또는 만료된 데이터 정리 시 호출됩니다.</p>
	 *
	 * @param email 삭제할 이메일 주소
	 */
	void delete(String email);

	/**
	 * 인증 완료 상태를 저장합니다.
	 *
	 * <p>인증 코드 검증이 성공한 후 호출되며,
	 * 회원가입 시점까지 인증 완료 상태를 유지합니다.</p>
	 *
	 * @param email 인증 완료된 이메일 주소
	 */
	void markAsVerified(String email);

	/**
	 * 이메일 인증 완료 여부를 확인합니다.
	 *
	 * <p>회원가입 시 이메일 인증 여부를 검증하는 데 사용됩니다.</p>
	 *
	 * @param email 확인할 이메일 주소
	 * @return 인증 완료 여부 (true: 인증 완료)
	 */
	boolean isVerified(String email);
}
