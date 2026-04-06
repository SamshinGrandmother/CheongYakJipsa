package me.synn3r.jipsa.core.api.member.service.email.strategy;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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
 * 인증 링크 클릭 방식의 이메일 인증 전략 구현체.
 *
 * <p>UUID 기반의 고유 토큰을 생성하여 인증 링크를 이메일로 발송합니다.
 * 사용자가 링크를 클릭하면 자동으로 인증이 완료됩니다.</p>
 *
 * <h2>인증 흐름</h2>
 * <ol>
 *   <li>사용자가 이메일 주소 입력</li>
 *   <li>UUID 기반 토큰 생성</li>
 *   <li>토큰에 이메일 정보를 Base64로 인코딩하여 포함</li>
 *   <li>모든 {@link EmailVerificationStore} 구현체에 저장</li>
 *   <li>인증 링크가 포함된 이메일 발송</li>
 *   <li>사용자가 링크 클릭</li>
 *   <li>토큰 검증 후 인증 완료</li>
 * </ol>
 *
 * <h2>링크 형식</h2>
 * <pre>{@code
 * https://cheongyakjipsa.com/api/email-verify?token={base64EncodedToken}
 * }</pre>
 *
 * <p>토큰 형식: {@code {email}:{uuid}} (Base64 인코딩)</p>
 *
 * <h2>활성화 방법</h2>
 * <p>이 전략은 {@code application.yml}에서 설정으로 활성화됩니다:</p>
 * <pre>{@code
 * email:
 *   verification:
 *     strategy: link
 * }</pre>
 *
 * <p>인증번호 방식(기본값)으로 전환하려면:</p>
 * <pre>{@code
 * email:
 *   verification:
 *     strategy: code  # 또는 생략
 * }</pre>
 *
 * <h2>프론트엔드 개발자를 위한 안내</h2>
 * <p><b>API 호출 순서:</b></p>
 * <ol>
 *   <li>{@code POST /api/email/verification} - 인증 링크 발송 요청</li>
 *   <li>사용자가 이메일에서 인증 링크 클릭</li>
 *   <li>서버에서 {@code GET /api/email-verify?token=xxx} 자동 호출</li>
 *   <li>인증 성공 응답 후 회원가입 페이지로 리다이렉트</li>
 *   <li>{@code POST /api/sign-up} - 회원가입 진행</li>
 * </ol>
 *
 * <h2>설정</h2>
 * <p>{@code application.yml}에서 다음 설정이 필요합니다:</p>
 * <pre>{@code
 * app:
 *   base-url: https://cheongyakjipsa.com
 * }</pre>
 *
 * @author synn3r
 * @since 1.0
 * @see EmailVerificationStrategy
 * @see CodeEmailVerificationStrategy
 */
@Slf4j
@Component
@ConditionalOnProperty(
	name = "email.verification.strategy",
	havingValue = "link"
)
public class LinkEmailVerificationStrategy implements EmailVerificationStrategy {

	private static final long EXPIRATION_MINUTES = 30;
	private static final String TOKEN_SEPARATOR = ":";

	@Value("${app.base-url:http://localhost:8080}")
	private String baseUrl;

	private final JavaMailSender javaMailSender;
	private final List<EmailVerificationStore> verificationStores;
	private final MessageSource messageSource;

	/**
	 * LinkEmailVerificationStrategy를 생성합니다.
	 *
	 * @param javaMailSender     이메일 발송 서비스
	 * @param verificationStores 인증 정보 저장소 목록 (Redis, DB 등)
	 * @param messageSource      다국어 메시지 소스
	 */
	public LinkEmailVerificationStrategy(
		JavaMailSender javaMailSender,
		List<EmailVerificationStore> verificationStores,
		MessageSource messageSource) {
		this.javaMailSender = javaMailSender;
		this.verificationStores = verificationStores;
		this.messageSource = messageSource;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>UUID 기반 토큰을 생성하여 모든 저장소에 저장하고 인증 링크가 포함된 이메일을 발송합니다.</p>
	 */
	@Override
	public void sendVerification(String email) {
		String uuid = UUID.randomUUID().toString();
		String token = encodeToken(email, uuid);

		saveToAllStores(email, uuid);
		sendVerificationEmail(email, token);

		log.info("인증 링크 발송 완료: email={}", email);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>토큰을 디코딩하여 이메일과 UUID를 추출한 뒤 검증합니다.</p>
	 */
	@Override
	public boolean verify(String email, String verificationData) {
		for (EmailVerificationStore store : verificationStores) {
			try {
				String storedUuid = store.find(email);
				if (storedUuid != null && storedUuid.equals(verificationData)) {
					markAsVerifiedInAllStores(email);
					log.info("인증 링크 검증 성공: email={}", email);
					return true;
				}
			} catch (Exception e) {
				log.warn("저장소 {} 검증 실패: {}", store.getClass().getSimpleName(), e.getMessage());
			}
		}
		log.warn("인증 링크 검증 실패: email={}", email);
		return false;
	}

	/**
	 * Base64 인코딩된 토큰에서 이메일과 UUID를 추출하여 검증합니다.
	 *
	 * <p>URL의 token 파라미터를 직접 검증할 때 사용합니다.</p>
	 *
	 * @param encodedToken Base64 인코딩된 토큰 ({@code email:uuid})
	 * @return 검증 성공 여부
	 */
	public boolean verifyByToken(String encodedToken) {
		try {
			String decoded = new String(Base64.getUrlDecoder().decode(encodedToken), StandardCharsets.UTF_8);
			String[] parts = decoded.split(TOKEN_SEPARATOR, 2);

			if (parts.length != 2) {
				log.warn("잘못된 토큰 형식: {}", encodedToken);
				return false;
			}

			String email = parts[0];
			String uuid = parts[1];

			return verify(email, uuid);
		} catch (IllegalArgumentException e) {
			log.warn("토큰 디코딩 실패: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EmailVerificationType getType() {
		return EmailVerificationType.LINK;
	}

	/**
	 * 이메일과 UUID를 Base64 인코딩된 토큰으로 변환합니다.
	 *
	 * @param email 이메일 주소
	 * @param uuid  UUID 문자열
	 * @return Base64 URL-safe 인코딩된 토큰
	 */
	private String encodeToken(String email, String uuid) {
		String raw = email + TOKEN_SEPARATOR + uuid;
		return Base64.getUrlEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 모든 저장소에 인증 토큰(UUID)을 저장합니다.
	 *
	 * @param email 이메일 주소
	 * @param uuid  UUID 문자열
	 */
	private void saveToAllStores(String email, String uuid) {
		for (EmailVerificationStore store : verificationStores) {
			try {
				store.save(email, uuid, EXPIRATION_MINUTES);
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
	 * 인증 링크가 포함된 이메일을 발송합니다.
	 *
	 * @param email 수신자 이메일 주소
	 * @param token Base64 인코딩된 토큰
	 */
	private void sendVerificationEmail(String email, String token) {
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

			String verificationUrl = baseUrl + "/api/email-verify?token=" + token;

			helper.setTo(email);
			helper.setSubject(messageSource.getMessage(
				"email.send.subject.verification.link", null, LocaleContextHolder.getLocale()));
			helper.setText(buildVerificationEmailHtml(verificationUrl), true);

			javaMailSender.send(message);
		} catch (MessagingException e) {
			log.error("이메일 발송 실패: email={}, error={}", email, e.getMessage());
			throw new RuntimeException(
				messageSource.getMessage("email.send.failed", null, LocaleContextHolder.getLocale()), e);
		}
	}

	/**
	 * 인증 링크 이메일 HTML 템플릿을 생성합니다.
	 *
	 * @param verificationUrl 인증 URL
	 * @return HTML 문자열
	 */
	private String buildVerificationEmailHtml(String verificationUrl) {
		return """
			<!DOCTYPE html>
			<html lang="ko">
			<head>
			  <meta charset="UTF-8">
			  <title>이메일 인증</title>
			  <style>
			    body { font-family: 'Malgun Gothic', sans-serif; }
			    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
			    .button { display: inline-block; padding: 16px 32px; background: #2563eb;
			              color: white; text-decoration: none; border-radius: 8px;
			              font-size: 16px; font-weight: bold; margin: 20px 0; }
			    .notice { color: #6b7280; font-size: 14px; }
			    .url { word-break: break-all; color: #6b7280; font-size: 12px; }
			  </style>
			</head>
			<body>
			  <div class="container">
			    <h1>청약집사 이메일 인증</h1>
			    <p>안녕하세요, 청약집사를 이용해 주셔서 감사합니다.</p>
			    <p>회원가입을 완료하려면 아래 버튼을 클릭해 주세요.</p>
			    <a href="%s" class="button">이메일 인증하기</a>
			    <p class="notice">* 인증 링크는 30분간 유효합니다.</p>
			    <p class="notice">* 버튼이 작동하지 않으면 아래 링크를 복사하여 브라우저에 붙여넣으세요.</p>
			    <p class="url">%s</p>
			    <p class="notice">* 본인이 요청하지 않은 경우 이 메일을 무시해 주세요.</p>
			  </div>
			</body>
			</html>
			""".formatted(verificationUrl, verificationUrl);
	}
}
