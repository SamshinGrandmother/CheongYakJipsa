package me.synn3r.jipsa.core.api.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.synn3r.jipsa.core.api.member.entity.EmailVerification;

/**
 * 이메일 인증 정보 저장소.
 *
 * <p>이메일 인증 정보의 CRUD 작업을 처리하는 Spring Data JPA Repository입니다.
 * {@link me.synn3r.jipsa.core.api.member.service.email.store.DatabaseEmailVerificationStore}에서
 * 사용됩니다.</p>
 *
 * <h2>주요 쿼리 메서드</h2>
 * <ul>
 *   <li>{@link #findTopByEmailOrderByCreatedAtDesc(String)} - 최신 인증 정보 조회</li>
 *   <li>{@link #existsByEmailAndVerified(String, boolean)} - 인증 완료 여부 확인</li>
 * </ul>
 *
 * @author synn3r
 * @since 1.0
 * @see EmailVerification
 */
@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

	/**
	 * 이메일 주소로 가장 최근 인증 정보를 조회합니다.
	 *
	 * <p>동일 이메일에 대해 여러 번 인증 요청이 있을 수 있으므로,
	 * 생성 시각 기준 최신 레코드를 반환합니다.</p>
	 *
	 * @param email 조회할 이메일 주소
	 * @return 인증 정보 (Optional)
	 */
	Optional<EmailVerification> findTopByEmailOrderByCreatedAtDesc(String email);

	/**
	 * 이메일 주소의 인증 정보 존재 여부를 확인합니다.
	 *
	 * @param email 확인할 이메일 주소
	 * @return 존재 여부
	 */
	boolean existsByEmail(String email);

	/**
	 * 인증 완료된 이메일 존재 여부를 확인합니다.
	 *
	 * <p>회원가입 시 이메일 인증 완료 여부를 검증하는 데 사용됩니다.</p>
	 *
	 * @param email    확인할 이메일 주소
	 * @param verified 인증 완료 여부 (true: 인증 완료)
	 * @return 존재 여부
	 */
	boolean existsByEmailAndVerified(String email, boolean verified);
}
