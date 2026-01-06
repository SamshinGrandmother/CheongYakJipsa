package me.synn3r.jipsa.core.api.base.advice;

import java.util.Objects;
import me.synn3r.jipsa.core.api.base.domain.FailResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ExceptionAdvice {

  private final MessageSource messageSource;

  public ExceptionAdvice(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<FailResponse> resolveException(Exception e) {
    return ResponseEntity.internalServerError()
      .body(FailResponse.of("error.internal", getMessage("error.internal")));
  }

  @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
  public ResponseEntity<FailResponse> resolveBindException(BindException e) {

    String errorMessage = messageSource.getMessage(
      Objects.requireNonNull(e.getBindingResult().getFieldError()),
      LocaleContextHolder.getLocale());

    return ResponseEntity.badRequest()
      .body(FailResponse.of("error.bad-request", errorMessage));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<FailResponse> resolveNoResourceFoundException(NoResourceFoundException e) {
    return ResponseEntity.badRequest()
      .body(FailResponse.of("error.not-found", getMessage("error.not-found")));
  }

  private String getMessage(String code) {
    return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
  }
}
