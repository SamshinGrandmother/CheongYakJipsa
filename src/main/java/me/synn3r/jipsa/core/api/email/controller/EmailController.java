package me.synn3r.jipsa.core.api.email.controller;

import io.swagger.v3.oas.annotations.Operation;
import me.synn3r.jipsa.core.api.base.domain.FailResponse;
import me.synn3r.jipsa.core.api.base.domain.Response;
import me.synn3r.jipsa.core.api.base.domain.SuccessResponse;
import me.synn3r.jipsa.core.api.email.domain.EmailRequest;
import me.synn3r.jipsa.core.api.email.service.EmailService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

  private final EmailService emailService;
  private final MessageSource messageSource;

  public EmailController(EmailService emailService, MessageSource messageSource) {
    this.emailService = emailService;
    this.messageSource = messageSource;
  }


  @PostMapping("/verify/email")
  @Operation(summary = "이메일 인증", description = "회원가입 or 마이페이지에서 이메일을 인증할 수 있는 기능")
  public ResponseEntity<Response> verifyEmail(@RequestBody EmailRequest emailRequest) {

    emailService.verifyEmail(emailRequest.getEmail());

    return ResponseEntity.ok(SuccessResponse.of(getMessage("email.send.success")));
  }

  @PostMapping("/check/email/code")
  @Operation(summary = "이메일 인증코드 확인", description = "입력한 코드로 이메일 인증 확인을 하는 API 입니다.")
  public ResponseEntity<Response> checkVerifyCode(@RequestBody EmailRequest emailRequest) {

    boolean result = emailService.checkVerifyCode(emailRequest.getEmail(),
      emailRequest.getVerifyCode());

    if (result) {
      return ResponseEntity.ok(SuccessResponse.of(getMessage("email.verify.success")));
    } else {
      return ResponseEntity.status(401)
        .body(FailResponse.of("email.verify.failure", getMessage("email.verify.failure")));
    }
  }

  private String getMessage(String code) {
    return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
  }


}
