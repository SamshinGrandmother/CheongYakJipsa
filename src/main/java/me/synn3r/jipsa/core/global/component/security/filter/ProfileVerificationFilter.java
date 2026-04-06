package me.synn3r.jipsa.core.global.component.security.filter;

import java.io.IOException;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.api.commons.domain.FailResponse;
import me.synn3r.jipsa.core.global.component.security.jwt.JwtTokenProvider;

@Component
@RequiredArgsConstructor
public class ProfileVerificationFilter extends OncePerRequestFilter {

	private static final String VERIFICATION_TOKEN_HEADER = "X-Profile-Verification-Token";
	private static final String MEMBERS_URI = "/members";

	private final JwtTokenProvider jwtTokenProvider;
	private final ObjectMapper objectMapper;
	private final MessageSource messageSource;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String uri = request.getRequestURI();
		String method = request.getMethod();

		if (requiresVerification(uri, method)) {
			String verificationToken = request.getHeader(VERIFICATION_TOKEN_HEADER);

			if (!isValidVerificationToken(verificationToken)) {
				String message = messageSource.getMessage("security.error.ProfileVerificationRequired", null,
					LocaleContextHolder.getLocale());
				sendForbiddenResponse(response, message);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private boolean requiresVerification(String uri, String method) {
		return uri.equals(MEMBERS_URI) && HttpMethod.PUT.name().equals(method);
	}

	private boolean isValidVerificationToken(String token) {
		if (!StringUtils.hasText(token)) {
			return false;
		}
		return jwtTokenProvider.validateToken(token)
			&& jwtTokenProvider.validateProfileVerificationToken(token);
	}

	private void sendForbiddenResponse(HttpServletResponse response, String message)
		throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setCharacterEncoding("UTF-8");

		FailResponse failResponse = FailResponse.of(message);
		objectMapper.writeValue(response.getOutputStream(), failResponse);
	}
}
