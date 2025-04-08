package me.synn3r.jipsa.core.api.base.advice;

import java.util.NoSuchElementException;
import me.synn3r.jipsa.core.api.base.domain.FailResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RuntimeExceptionAdvice {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<FailResponse> resolveRuntimeException(RuntimeException e) {
    return ResponseEntity.internalServerError().body(FailResponse.of(e.getMessage()));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<FailResponse> resolveNoSuchElementException(NoSuchElementException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FailResponse.of(e.getMessage()));
  }

  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<FailResponse> resolveDuplicateKeyException(DuplicateKeyException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FailResponse.of(e.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<FailResponse> resolveIllegalArgumentException(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FailResponse.of(e.getMessage()));
  }

}
