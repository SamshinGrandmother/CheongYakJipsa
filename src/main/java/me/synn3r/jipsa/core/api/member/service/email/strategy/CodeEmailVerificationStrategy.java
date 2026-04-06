package me.synn3r.jipsa.core.api.member.service.email.strategy;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import me.synn3r.jipsa.core.api.member.service.email.store.EmailVerificationStore;

/**
 * 6자리 인증번호 입력 방식의 이메일 인증 전략 구현체.
 *
 * <p>회원가입 시 기본으로 사용되는 인증 방식입니다. 6자리 숫자로 구성된
 * 인증번호를 이메일로 발송하고, 사용자가 입력한 코드를 검증합니다.</p>
 *
 * <h2>인증 흐름</h2>
 * <ol>
 *   <li>사용자가 이메일 주소 입력</li>
 *   <li>6자리 랜덤 인증번호 생성 ({@link SecureRandom} 사용)</li>
 *   <li>모든 {@link EmailVerificationStore} 구현체에 저장 (장애 대비)</li>
 *   <li>인증번호가 포함된 HTML 이메일 발송</li>
 *   <li>사용자가 화면에서 인증번호 입력</li>
 *   <li>저장된 값과 비교하여 검증</li>
 *   <li>검증 성공 시 인증 완료 상태로 표시</li>
 * </ol>
 *
 * <h2>보안 고려사항</h2>
 * <ul>
 *   <li>인증번호는 {@link SecureRandom}을 사용하여 암호학적으로 안전하게 생성</li>
 *   <li>인증번호 유효 시간: 5분</li>
 *   <li>동일 이메일 재발송 시 이전 인증번호 무효화 (덮어쓰기)</li>
 *   <li>인증 성공 후 인증 코드 즉시 삭제</li>
 * </ul>
 *
 * <h2>장애 대비</h2>
 * <p>모든 {@link EmailVerificationStore} 구현체(Redis, DB)에 인증 코드를 저장합니다.
 * 저장 시 특정 저장소가 실패해도 다른 저장소에 저장을 시도합니다.
 * 검증 시에는 하나의 저장소에서라도 검증에 성공하면 인증 완료로 처리합니다.</p>
 *
 * <h2>프론트엔드 개발자를 위한 안내</h2>
 * <p><b>API 호출 순서:</b></p>
 * <ol>
 *   <li>{@code POST /api/email/verification} - 인증 코드 발송 요청</li>
 *   <li>사용자가 이메일에서 6자리 코드 확인</li>
 *   <li>{@code POST /api/email/verification/verify} - 인증 코드 검증</li>
 *   <li>검증 성공 후 {@code POST /api/sign-up} - 회원가입 진행</li>
 * </ol>
 *
 * <h2>활성화 방법</h2>
 * <p>이 전략은 {@code application.yml}에서 설정으로 활성화됩니다:</p>
 * <pre>{@code
 * email:
 *   verification:
 *     strategy: code  # 기본값, 생략 가능
 * }</pre>
 *
 * <p>링크 방식으로 전환하려면:</p>
 * <pre>{@code
 * email:
 *   verification:
 *     strategy: link
 * }</pre>
 *
 * @author synn3r
 * @since 1.0
 * @see EmailVerificationStrategy
 * @see EmailVerificationStore
 */
@Slf4j
@Component
@ConditionalOnProperty(
	name = "email.verification.strategy",
	havingValue = "code",
	matchIfMissing = true
)
public class CodeEmailVerificationStrategy implements EmailVerificationStrategy {

	private static final int CODE_LENGTH = 6;
	private static final int CODE_MIN = 100000;
	private static final int CODE_MAX = 900000;
	private static final long EXPIRATION_MINUTES = 5;

	private final JavaMailSender javaMailSender;
	private final List<EmailVerificationStore> verificationStores;
	private final MessageSource messageSource;
	private final SecureRandom secureRandom;

	/**
	 * CodeEmailVerificationStrategy를 생성합니다.
	 *
	 * <p>Spring에서 모든 {@link EmailVerificationStore} 구현체를 자동 주입받습니다.
	 * 이를 통해 Redis와 DB 저장소 모두에 인증 정보를 저장할 수 있습니다.</p>
	 *
	 * @param javaMailSender     이메일 발송 서비스
	 * @param verificationStores 인증 정보 저장소 목록 (Redis, DB 등)
	 * @param messageSource      다국어 메시지 소스
	 */
	public CodeEmailVerificationStrategy(
		JavaMailSender javaMailSender,
		List<EmailVerificationStore> verificationStores,
		MessageSource messageSource) {
		this.javaMailSender = javaMailSender;
		this.verificationStores = verificationStores;
		this.messageSource = messageSource;
		this.secureRandom = new SecureRandom();
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>6자리 인증번호를 생성하여 모든 저장소에 저장하고 이메일을 발송합니다.</p>
	 */
	@Override
	public void sendVerification(String email) {
		String code = generateCode();

		saveToAllStores(email, code);
		sendVerificationEmail(email, code);

		log.info("인증 코드 발송 완료: email={}", email);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>사용자가 입력한 인증번호를 검증합니다. 모든 저장소를 순회하며
	 * 하나라도 일치하면 인증 성공으로 처리합니다.</p>
	 */
	@Override
	public boolean verify(String email, String verificationData) {
		for (EmailVerificationStore store : verificationStores) {
			try {
				String storedCode = store.find(email);
				if (storedCode != null && storedCode.equals(verificationData)) {
					markAsVerifiedInAllStores(email);
					log.info("인증 코드 검증 성공: email={}", email);
					return true;
				}
			} catch (Exception e) {
				log.warn("저장소 {} 검증 실패: {}", store.getClass().getSimpleName(), e.getMessage());
			}
		}
		log.warn("인증 코드 검증 실패: email={}", email);
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EmailVerificationType getType() {
		return EmailVerificationType.CODE;
	}

	/**
	 * 6자리 랜덤 인증번호를 생성합니다.
	 *
	 * @return 100000 ~ 999999 사이의 6자리 숫자 문자열
	 */
	private String generateCode() {
		int code = CODE_MIN + secureRandom.nextInt(CODE_MAX);
		return String.valueOf(code);
	}

	/**
	 * 모든 저장소에 인증 코드를 저장합니다.
	 *
	 * <p>특정 저장소 저장 실패 시 로그를 남기고 다음 저장소로 진행합니다.</p>
	 *
	 * @param email 이메일 주소
	 * @param code  인증 코드
	 */
	private void saveToAllStores(String email, String code) {
		for (EmailVerificationStore store : verificationStores) {
			try {
				store.save(email, code, EXPIRATION_MINUTES);
			} catch (Exception e) {
				log.warn("저장소 {} 저장 실패: {}", store.getClass().getSimpleName(), e.getMessage());
			}
		}
	}

	/**
	 * 모든 저장소에서 인증 완료 처리를 수행합니다.
	 *
	 * @param email 이메일 주소
	 */
	private void markAsVerifiedInAllStores(String email) {
		for (EmailVerificationStore store : verificationStores) {
			try {
				store.delete(email);
				store.markAsVerified(email);
			} catch (Exception e) {
				log.warn("저장소 {} 인증 완료 처리 실패: {}", store.getClass().getSimpleName(), e.getMessage());
			}
		}
	}

	/**
	 * 인증번호가 포함된 이메일을 발송합니다.
	 *
	 * @param email 수신자 이메일 주소
	 * @param code  인증 코드
	 */
	private void sendVerificationEmail(String email, String code) {
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

			helper.setTo(email);
			helper.setSubject(messageSource.getMessage(
				"email.send.subject.verification", null, LocaleContextHolder.getLocale()));
			helper.setText(buildVerificationEmailHtml(code), true);

			javaMailSender.send(message);
		} catch (MessagingException e) {
			log.error("이메일 발송 실패: email={}, error={}", email, e.getMessage());
			throw new RuntimeException(
				messageSource.getMessage("email.send.failed", null, LocaleContextHolder.getLocale()), e);
		}
	}

	/**
	 * 인증번호 이메일 HTML 템플릿을 생성합니다.
	 *
	 * @param code 인증 코드
	 * @return HTML 문자열
	 */
	private String buildVerificationEmailHtml(String code) {
		return """
			<!DOCTYPE html>
			<html lang="ko">
			<head>
			  <meta charset="UTF-8">
			  <title>이메일 인증</title>
			  <style>
			    body { font-family: 'Malgun Gothic', sans-serif; }
			    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
			    .code { font-size: 32px; font-weight: bold; color: #2563eb;
			            letter-spacing: 8px; padding: 20px; background: #f3f4f6;
			            border-radius: 8px; text-align: center; margin: 20px 0; }
			    .notice { color: #6b7280; font-size: 14px; }
			  </style>
			</head>
			<body>
			  <div class="container">
			    <h1>청약집사 이메일 인증</h1>
			    <p>안녕하세요, 청약집사를 이용해 주셔서 감사합니다.</p>
			    <p>회원가입을 완료하려면 아래 인증번호 6자리를 입력해 주세요.</p>
			    <div class="code">%s</div>
			    <p class="notice">* 인증번호는 5분간 유효합니다.</p>
			    <p class="notice">* 본인이 요청하지 않은 경우 이 메일을 무시해 주세요.</p>
			  </div>
			</body>
			</html>
			""".formatted(code);
	}
}
