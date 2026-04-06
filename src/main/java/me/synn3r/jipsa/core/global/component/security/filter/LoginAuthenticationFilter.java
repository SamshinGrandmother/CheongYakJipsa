package me.synn3r.jipsa.core.global.component.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private static final String LOGIN_URL = "/api/auth/login";
	private static final String HTTP_METHOD = "POST";

	private final ObjectMapper objectMapper;

	public LoginAuthenticationFilter(ObjectMapper objectMapper) {
		super(new AntPathRequestMatcher(LOGIN_URL, HTTP_METHOD));
		this.objectMapper = objectMapper;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException, IOException {
		LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

		String userId = loginRequest.getUserId();
		String password = loginRequest.getPassword();

		if (userId == null) {
			userId = "";
		}
		if (password == null) {
			password = "";
		}

		userId = userId.trim();

		UsernamePasswordAuthenticationToken authRequest =
			UsernamePasswordAuthenticationToken.unauthenticated(userId, password);

		authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	@Getter
	@Setter
	@NoArgsConstructor
	private static class LoginRequest {
		private String userId;
		private String password;
	}
}
