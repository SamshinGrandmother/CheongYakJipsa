package me.synn3r.jipsa.core.api.member.service.email;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synn3r.jipsa.core.api.member.service.email.store.EmailVerificationStore;
import me.synn3r.jipsa.core.api.member.service.email.strategy.EmailVerificationStrategy;
import me.synn3r.jipsa.core.api.member.service.email.strategy.LinkEmailVerificationStrategy;

/**
 * 이메일 인증 서비스.
 *
 * <p>이메일 인증과 관련된 비즈니스 로직을 처리합니다.
 * {@link EmailVerificationStrategy}를 통해 인증 방식을 추상화하고,
 * {@link EmailVerificationStore}를 통해 저장소를 추상화합니다.</p>
 *
 * <h2>주요 기능</h2>
 * <ul>
 *   <li>{@link #sendVerificationEmail(String)} - 인증 이메일 발송</li>
 *   <li>{@link #verifyCode(String, String)} - 인증 코드 검증</li>
 *   <li>{@link #verifyByToken(String)} - 인증 토큰 검증 (링크 방식)</li>
 *   <li>{@link #isEmailVerified(String)} - 인증 완료 상태 확인</li>
 * </ul>
 *
 * <h2>장애 대비</h2>
 * <p>인증 완료 여부 확인 시 모든 저장소를 순회하여
 * 하나라도 인증 완료 상태이면 {@code true}를 반환합니다.</p>
 *
 * @author synn3r
 * @since 1.0
 * @see EmailVerificationStrategy
 * @see EmailVerificationStore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

	private final EmailVerificationStrategy emailVerificationStrategy;
	private final List<EmailVerificationStore> verificationStores;

	/**
	 * 인증 이메일을 발송합니다.
	 *
	 * <p>현재 활성화된 {@link EmailVerificationStrategy}를 사용하여
	 * 인증 이메일을 발송합니다. 기본적으로 6자리 인증번호 방식이 사용됩니다.</p>
	 *
	 * @param email 인증 대상 이메일 주소
	 * @throws RuntimeException 이메일 발송 실패 시
	 */
	public void sendVerificationEmail(String email) {
		log.debug("인증 이메일 발송 요청: email={}", email);
		emailVerificationStrategy.sendVerification(email);
	}

	/**
	 * 인증 코드를 검증합니다.
	 *
	 * <p>사용자가 입력한 인증 코드를 검증합니다.
	 * 검증 성공 시 인증 완료 상태로 표시됩니다.</p>
	 *
	 * @param email 인증 대상 이메일 주소
	 * @param code  사용자가 입력한 인증 코드
	 * @return 인증 성공 여부
	 */
	public boolean verifyCode(String email, String code) {
		log.debug("인증 코드 검증 요청: email={}", email);
		return emailVerificationStrategy.verify(email, code);
	}

	/**
	 * 인증 토큰을 검증합니다 (링크 방식).
	 *
	 * <p>이메일에 포함된 인증 링크의 토큰을 검증합니다.
	 * 토큰에는 이메일 주소와 UUID가 Base64로 인코딩되어 있습니다.</p>
	 *
	 * @param token Base64 인코딩된 인증 토큰
	 * @return 인증 성공 여부
	 */
	public boolean verifyByToken(String token) {
		log.debug("인증 토큰 검증 요청");

		if (emailVerificationStrategy instanceof LinkEmailVerificationStrategy linkStrategy) {
			return linkStrategy.verifyByToken(token);
		}

		log.warn("링크 인증 전략이 활성화되지 않았습니다.");
		return false;
	}

	/**
	 * 이메일 인증 완료 여부를 확인합니다.
	 *
	 * <p>모든 저장소를 순회하여 하나라도 인증 완료 상태이면
	 * {@code true}를 반환합니다. 이를 통해 특정 저장소 장애 시에도
	 * 서비스를 유지할 수 있습니다.</p>
	 *
	 * @param email 확인할 이메일 주소
	 * @return 인증 완료 여부 (true: 인증 완료)
	 */
	public boolean isEmailVerified(String email) {
		for (EmailVerificationStore store : verificationStores) {
			try {
				if (store.isVerified(email)) {
					log.debug("이메일 인증 확인됨: email={}, store={}",
						email, store.getClass().getSimpleName());
					return true;
				}
			} catch (Exception e) {
				log.warn("저장소 {} 조회 실패: {}", store.getClass().getSimpleName(), e.getMessage());
			}
		}
		log.debug("이메일 미인증: email={}", email);
		return false;
	}
}
