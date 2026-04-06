package me.synn3r.jipsa.core.global.component.security.handler;

import java.io.IOException;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.api.auth.service.RefreshTokenService;
import me.synn3r.jipsa.core.global.component.security.jwt.JwtTokenProvider;
import me.synn3r.jipsa.core.global.component.security.service.AuthenticationHistoryService;
import me.synn3r.jipsa.core.global.component.security.userdetails.DefaultUserDetails;

@Component
@RequiredArgsConstructor
public class DefaultAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final String CHARACTER_ENCODING = "UTF-8";
	private static final String SUCCESS_MESSAGE_KEY = "security.success.Authentication";

	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenService refreshTokenService;
	private final AuthenticationHistoryService authenticationHistoryService;
	private final ObjectMapper objectMapper;
	private final MessageSource messageSource;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		DefaultUserDetails userDetails = (DefaultUserDetails)authentication.getPrincipal();
		String userId = userDetails.getUsername();

		String accessToken = jwtTokenProvider.createAccessToken(userDetails);
		String refreshToken = jwtTokenProvider.createRefreshToken(userId);
		String tokenId = jwtTokenProvider.getTokenIdFromRefreshToken(refreshToken);

		refreshTokenService.save(userId, tokenId);

		response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken);
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(CHARACTER_ENCODING);

		String message = messageSource.getMessage(SUCCESS_MESSAGE_KEY, null, LocaleContextHolder.getLocale());
		AuthenticationSuccessResponse successResponse = new AuthenticationSuccessResponse(message, refreshToken);
		objectMapper.writeValue(response.getWriter(), successResponse);

		authenticationHistoryService.recordSuccess(userId);
	}

	public record AuthenticationSuccessResponse(String message, String refreshToken) {
	}
}
