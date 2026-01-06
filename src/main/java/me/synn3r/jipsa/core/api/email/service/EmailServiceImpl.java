package me.synn3r.jipsa.core.api.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender emailSender;
  private final MessageSource messageSource;
  private final Map<String, Integer> verifyCodes = new ConcurrentHashMap<>();

  public EmailServiceImpl(JavaMailSender emailSender, MessageSource messageSource) {
    this.emailSender = emailSender;
    this.messageSource = messageSource;
  }


  @Override
  public void verifyEmail(String email) {

    try {
      MimeMessage message = emailSender.createMimeMessage();

      MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
      helper.setTo(email);
      helper.setSubject(getMessage("email.subject.verification"));

      int randomNumber = createRandomNumber();
      helper.setText(buildVerificationBody(randomNumber), true);

      verifyCodes.put(email, randomNumber);
      emailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException(getMessage("email.send.failure"), e);
    }

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

  private String buildVerificationBody(int verificationCode) {
    return "<p>" + getMessage("email.body.greeting") + "</p>" +
      "<p>" + getMessage("email.body.description") + "</p>" +
      "<h2>" + verificationCode + "</h2>";
  }

  private String getMessage(String code) {
    return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
  }


}
