package me.synn3r.jipsa.core.api.email.service;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender emailSender;
	private final Map<String, Integer> verifyCodes = new ConcurrentHashMap<>();

	public EmailServiceImpl(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}

	@Override
	public void verifyEmail(String email) {

		try {
			MimeMessage message = emailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
			helper.setTo(email);
			helper.setSubject("[청약집사] 이메일 인증번호 발송 메일입니다.");

			int randomNumber = createRandomNumber();
			String verifyHtml = buildVerificationEmailHtml(randomNumber);
			helper.setText(verifyHtml, true);

			verifyCodes.put(email, randomNumber);
			emailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("이메일 전송 중 문제가 발생했습니다.", e);
		}

	}

	private String buildVerificationEmailHtml(int randomNumber) {
		return """
			<!DOCTYPE html>
			<html>
			<head>
			  <meta charset="UTF-8">
			  <title>이메일 인증</title>
			</head>
			<body>
			  <h1>안녕하세요. 청약 집사에 오신 것을 환영합니다.</h1>
			  <p>회원가입을 하기 위해 아래 인증번호 6자리를 입력해주세요.</p>
			  <p style="font-size: 24px; font-weight: bold;">%d</p>
			  <p>감사합니다.</p>
			</body>
			</html>
			""".formatted(randomNumber);
	}

	@Override
	public boolean checkVerifyCode(String email, int verifyCode) {

		if (Objects.equals(verifyCodes.get(email), verifyCode)) {
			verifyCodes.remove(email);
			return true;
		}
		return false;

	}

	public int createRandomNumber() {
		Random random = new Random();
		int number = 100000 + random.nextInt(900000);
		return number;
	}

}
