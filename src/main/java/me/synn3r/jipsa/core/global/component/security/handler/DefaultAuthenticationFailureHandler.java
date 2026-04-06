package me.synn3r.jipsa.core.global.component.security.handler;

import java.io.IOException;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.global.component.security.enums.AuthenticationFailureType;
import me.synn3r.jipsa.core.global.component.security.service.AuthenticationHistoryService;

@Component
@RequiredArgsConstructor
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private static final String CHARACTER_ENCODING = "UTF-8";
	private static final String SPRING_SECURITY_LAST_USERNAME = "SPRING_SECURITY_LAST_USERNAME";
	private static final String UNKNOWN_USER = "unknown";

	private final AuthenticationHistoryService authenticationHistoryService;
	private final ObjectMapper objectMapper;
	private final MessageSource messageSource;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException {

		String userId = extractUserId(request, exception);

		authenticationHistoryService.recordFailure(userId, exception);

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(CHARACTER_ENCODING);

		String errorMessage = determineErrorMessage(exception);
		AuthenticationFailureResponse failureResponse = new AuthenticationFailureResponse(errorMessage);

		objectMapper.writeValue(response.getWriter(), failureResponse);
		response.getWriter().flush();
	}

	private String extractUserId(HttpServletRequest request, AuthenticationException exception) {
		if (exception instanceof UsernameNotFoundException) {
			return exception.getMessage();
		}

		Object principal = request.getAttribute(SPRING_SECURITY_LAST_USERNAME);
		if (principal != null) {
			return principal.toString();
		}

		return UNKNOWN_USER;
	}

	private String determineErrorMessage(AuthenticationException exception) {
		if (exception instanceof UsernameNotFoundException || exception instanceof BadCredentialsException) {
			return AuthenticationFailureType.BAD_CREDENTIALS.getResponseText();
		}

		AuthenticationFailureType failureType = AuthenticationFailureType.valueOf(exception);
		return messageSource.getMessage(failureType.getResponseText(), null, LocaleContextHolder.getLocale());
	}

	public record AuthenticationFailureResponse(String message) {
	}
}
