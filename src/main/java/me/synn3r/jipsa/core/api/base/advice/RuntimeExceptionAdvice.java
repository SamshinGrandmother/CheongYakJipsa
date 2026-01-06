package me.synn3r.jipsa.core.api.base.advice;

import java.util.NoSuchElementException;
import me.synn3r.jipsa.core.api.base.domain.FailResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RuntimeExceptionAdvice {

  private final MessageSource messageSource;

  public RuntimeExceptionAdvice(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<FailResponse> resolveRuntimeException(RuntimeException e) {
    return ResponseEntity.internalServerError()
      .body(FailResponse.of("error.internal", getMessage(e.getMessage(), "error.internal")));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<FailResponse> resolveNoSuchElementException(NoSuchElementException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(FailResponse.of("error.not-found", getMessage(e.getMessage(), "error.not-found")));
  }

  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<FailResponse> resolveDuplicateKeyException(DuplicateKeyException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(FailResponse.of("error.bad-request", getMessage(e.getMessage(), "error.bad-request")));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<FailResponse> resolveIllegalArgumentException(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(FailResponse.of("error.bad-request", getMessage(e.getMessage(), "error.bad-request")));
  }

  private String getMessage(String code, String fallbackCode) {
    return messageSource.getMessage(code, null,
      messageSource.getMessage(fallbackCode, null, LocaleContextHolder.getLocale()),
      LocaleContextHolder.getLocale());
  }
}
