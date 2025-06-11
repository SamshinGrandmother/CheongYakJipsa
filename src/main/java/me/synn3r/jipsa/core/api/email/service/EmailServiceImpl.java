package me.synn3r.jipsa.core.api.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender emailSender;
  private final TemplateEngine templateEngine;
  private final Map<String, Integer> verifyCodes = new ConcurrentHashMap<>();

  public EmailServiceImpl(JavaMailSender emailSender, TemplateEngine templateEngine) {
    this.emailSender = emailSender;
    this.templateEngine = templateEngine;
  }


  @Override
  public void verifyEmail(String email) {

    try {
      MimeMessage message = emailSender.createMimeMessage();

      MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
      helper.setTo(email);
      helper.setSubject("[쳥약집사] 이메일 인증번호 발송 메일입니다.");

      Context context = new Context();
      int randomNumber = createRandomNumber();
      context.setVariables(Map.of("randomNumber", randomNumber));

      String VerifyHtml = templateEngine.process("/email/verification", context);
      helper.setText(VerifyHtml, true);

      verifyCodes.put(email, randomNumber);
      emailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("이메일 전송 중 문제가 발생했습니다.", e);
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


}
