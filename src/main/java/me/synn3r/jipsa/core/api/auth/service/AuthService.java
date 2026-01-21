package me.synn3r.jipsa.core.api.auth.service;

import me.synn3r.jipsa.core.api.auth.domain.LoginRequest;
import me.synn3r.jipsa.core.api.auth.domain.LoginResponse;
import me.synn3r.jipsa.core.api.auth.domain.PasswordVerifyResponse;
import me.synn3r.jipsa.core.component.security.userdetails.DefaultUserDetails;

public interface AuthService {

  LoginResponse login(LoginRequest request);

  void logout(String userId);

  LoginResponse refreshToken(String refreshToken);

  PasswordVerifyResponse verifyPassword(DefaultUserDetails userDetails, String password);
}
