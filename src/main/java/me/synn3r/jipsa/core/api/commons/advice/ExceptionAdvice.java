package me.synn3r.jipsa.core.api.commons.advice;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import me.synn3r.jipsa.core.api.commons.domain.FailResponse;

@RestControllerAdvice
public class ExceptionAdvice {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<FailResponse> resolveException(Exception e) {
		return ResponseEntity.internalServerError().body(FailResponse.of(e.getMessage()));
	}

	@ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
	public ResponseEntity<FailResponse> resolveBindException(BindException e) {

		String errorMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();

		return ResponseEntity.badRequest().body(FailResponse.of(errorMessage));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<FailResponse> resolveNoResourceFoundException(NoResourceFoundException e) {
		return ResponseEntity.badRequest().body(FailResponse.of(e.getMessage()));
	}
}
