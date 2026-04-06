package me.synn3r.jipsa.core.api.member.service.email.store;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synn3r.jipsa.core.api.member.entity.EmailVerification;
import me.synn3r.jipsa.core.api.member.repository.EmailVerificationRepository;
import me.synn3r.jipsa.core.api.member.service.email.strategy.EmailVerificationType;

/**
 * MariaDB 기반 이메일 인증 정보 저장소 구현체.
 *
 * <p>영속적인 인증 정보 저장을 제공하며, Redis 장애 시
 * 백업 저장소로 활용됩니다. 인증 이력 추적 및
 * 감사(Audit) 목적으로도 사용될 수 있습니다.</p>
 *
 * <h2>테이블 구조</h2>
 * <p>{@link EmailVerification} 엔티티를 통해 다음 정보를 저장합니다:</p>
 * <ul>
 *   <li>이메일 주소 (email)</li>
 *   <li>인증 코드/토큰 (verification_data)</li>
 *   <li>인증 타입 (verification_type)</li>
 *   <li>만료 시각 (expires_at)</li>
 *   <li>인증 완료 여부 (verified)</li>
 *   <li>인증 완료 시각 (verified_at)</li>
 *   <li>생성/수정 시각 (created_at, updated_at)</li>
 * </ul>
 *
 * <h2>만료 처리</h2>
 * <p>Redis와 달리 자동 만료가 되지 않으므로, 조회 시점에
 * {@code expiresAt} 필드를 확인하여 만료 여부를 판단합니다.</p>
 *
 * <h2>트랜잭션</h2>
 * <p>모든 쓰기 작업은 {@code @Transactional}로 처리됩니다.</p>
 *
 * @author synn3r
 * @since 1.0
 * @see EmailVerificationStore
 * @see EmailVerification
 * @see EmailVerificationRepository
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseEmailVerificationStore implements EmailVerificationStore {

	private final EmailVerificationRepository emailVerificationRepository;

	/**
	 * {@inheritDoc}
	 *
	 * <p>DB에 인증 정보를 저장합니다. 동일 이메일에 대한 기존 데이터가 있으면
	 * 인증 코드와 만료 시각을 업데이트합니다.</p>
	 */
	@Override
	@Transactional
	public void save(String email, String verificationData, long expirationMinutes) {
		LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expirationMinutes);

		Optional<EmailVerification> existingOpt = emailVerificationRepository
			.findTopByEmailOrderByCreatedAtDesc(email);

		if (existingOpt.isPresent()) {
			EmailVerification existing = existingOpt.get();
			existing.updateVerificationData(verificationData, expiresAt);
			log.debug("DB에 인증 코드 업데이트: email={}, expiration={}분", email, expirationMinutes);
		} else {
			EmailVerification newVerification = EmailVerification.builder()
				.email(email)
				.verificationData(verificationData)
				.verificationType(EmailVerificationType.CODE)
				.expiresAt(expiresAt)
				.build();
			emailVerificationRepository.save(newVerification);
			log.debug("DB에 인증 코드 신규 저장: email={}, expiration={}분", email, expirationMinutes);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>DB에서 인증 코드를 조회합니다. 만료된 데이터는 {@code null}을 반환합니다.</p>
	 */
	@Override
	@Transactional(readOnly = true)
	public String find(String email) {
		return emailVerificationRepository.findTopByEmailOrderByCreatedAtDesc(email)
			.filter(v -> !v.isExpired())
			.map(EmailVerification::getVerificationData)
			.orElse(null);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>DB에서 해당 이메일의 인증 정보를 논리적으로 무효화합니다.
	 * (실제 삭제 대신 만료 시각을 과거로 설정)</p>
	 */
	@Override
	@Transactional
	public void delete(String email) {
		emailVerificationRepository.findTopByEmailOrderByCreatedAtDesc(email)
			.ifPresent(v -> {
				v.updateVerificationData(v.getVerificationData(), LocalDateTime.now().minusMinutes(1));
				log.debug("DB에서 인증 코드 만료 처리: email={}", email);
			});
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>DB에 인증 완료 상태를 저장합니다.</p>
	 */
	@Override
	@Transactional
	public void markAsVerified(String email) {
		emailVerificationRepository.findTopByEmailOrderByCreatedAtDesc(email)
			.ifPresent(v -> {
				v.markAsVerified();
				log.debug("DB에 인증 완료 상태 저장: email={}", email);
			});
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>DB에서 인증 완료 상태를 확인합니다.</p>
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean isVerified(String email) {
		return emailVerificationRepository.existsByEmailAndVerified(email, true);
	}
}
