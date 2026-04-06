package me.synn3r.jipsa.core.api.member.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synn3r.jipsa.core.api.commons.entity.BaseEntity;
import me.synn3r.jipsa.core.api.member.service.email.strategy.EmailVerificationType;

/**
 * 이메일 인증 정보를 저장하는 엔티티.
 *
 * <p>회원가입 시 발송된 이메일 인증 정보를 영속적으로 저장합니다.
 * Redis와 함께 이중 저장소로 운영되어 장애 상황에서도 인증 프로세스를 보장합니다.</p>
 *
 * <h2>테이블 정보</h2>
 * <ul>
 *   <li>테이블명: email_verification</li>
 *   <li>인덱스: idx_email_verification_email (email 컬럼)</li>
 * </ul>
 *
 * <h2>저장 데이터</h2>
 * <ul>
 *   <li>이메일 주소</li>
 *   <li>인증 코드 또는 토큰</li>
 *   <li>인증 타입 (CODE/LINK)</li>
 *   <li>만료 시각</li>
 *   <li>인증 완료 여부</li>
 * </ul>
 *
 * @author synn3r
 * @since 1.0
 * @see EmailVerificationType
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	name = "email_verification",
	indexes = @Index(name = "idx_email_verification_email", columnList = "email")
)
@Comment("이메일 인증 정보 테이블")
public class EmailVerification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("이메일 인증 식별자")
	@Column(name = "email_verification_id")
	private Long id;

	@Column(nullable = false, length = 255)
	@Comment("인증 대상 이메일 주소")
	private String email;

	@Column(nullable = false, length = 255)
	@Comment("인증 코드 또는 토큰")
	private String verificationData;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Comment("인증 타입 (CODE: 인증번호, LINK: 링크)")
	private EmailVerificationType verificationType;

	@Column(nullable = false)
	@Comment("만료 시각")
	private LocalDateTime expiresAt;

	@Column(nullable = false)
	@Comment("인증 완료 여부")
	private boolean verified = false;

	@Column
	@Comment("인증 완료 시각")
	private LocalDateTime verifiedAt;

	/**
	 * 이메일 인증 정보를 생성합니다.
	 *
	 * @param email            인증 대상 이메일 주소
	 * @param verificationData 인증 코드 또는 토큰
	 * @param verificationType 인증 타입
	 * @param expiresAt        만료 시각
	 */
	@Builder
	public EmailVerification(String email, String verificationData,
		EmailVerificationType verificationType, LocalDateTime expiresAt) {
		this.email = email;
		this.verificationData = verificationData;
		this.verificationType = verificationType;
		this.expiresAt = expiresAt;
	}

	/**
	 * 인증 완료 처리를 수행합니다.
	 *
	 * <p>인증 완료 상태로 변경하고 인증 완료 시각을 기록합니다.</p>
	 */
	public void markAsVerified() {
		this.verified = true;
		this.verifiedAt = LocalDateTime.now();
	}

	/**
	 * 인증 코드가 만료되었는지 확인합니다.
	 *
	 * @return 만료 여부 (현재 시각이 만료 시각을 지났으면 true)
	 */
	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiresAt);
	}

	/**
	 * 인증 데이터를 업데이트합니다.
	 *
	 * <p>이메일 재발송 시 새로운 인증 코드/토큰과 만료 시각으로 갱신합니다.
	 * 인증 완료 상태는 초기화됩니다.</p>
	 *
	 * @param verificationData 새로운 인증 코드 또는 토큰
	 * @param expiresAt        새로운 만료 시각
	 */
	public void updateVerificationData(String verificationData, LocalDateTime expiresAt) {
		this.verificationData = verificationData;
		this.expiresAt = expiresAt;
		this.verified = false;
		this.verifiedAt = null;
	}
}
