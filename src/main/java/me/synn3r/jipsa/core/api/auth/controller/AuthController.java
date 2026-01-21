package me.synn3r.jipsa.core.api.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.api.auth.domain.LoginRequest;
import me.synn3r.jipsa.core.api.auth.domain.LoginResponse;
import me.synn3r.jipsa.core.api.auth.domain.PasswordVerifyRequest;
import me.synn3r.jipsa.core.api.auth.domain.PasswordVerifyResponse;
import me.synn3r.jipsa.core.api.auth.domain.TokenRefreshRequest;
import me.synn3r.jipsa.core.api.auth.service.AuthService;
import me.synn3r.jipsa.core.component.security.userdetails.DefaultUserDetails;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "로그인", description = "사용자 ID와 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		LoginResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "로그아웃", description = "현재 사용자의 모든 Refresh Token을 삭제합니다.")
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@AuthenticationPrincipal DefaultUserDetails userDetails) {
		authService.logout(userDetails.getUsername());
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "토큰 갱신", description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.")
	@PostMapping("/refresh")
	public ResponseEntity<LoginResponse> refresh(
		@Valid @RequestBody TokenRefreshRequest request) {
		LoginResponse response = authService.refreshToken(request.getRefreshToken());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "비밀번호 확인",
		description = "민감한 작업 수행 전 비밀번호를 확인하고 검증 토큰을 발급받습니다.")
	@PostMapping("/verify-password")
	public ResponseEntity<PasswordVerifyResponse> verifyPassword(
		@AuthenticationPrincipal DefaultUserDetails userDetails,
		@Valid @RequestBody PasswordVerifyRequest request) {
		PasswordVerifyResponse response = authService.verifyPassword(userDetails, request.getPassword());
		return ResponseEntity.ok(response);
	}
}
