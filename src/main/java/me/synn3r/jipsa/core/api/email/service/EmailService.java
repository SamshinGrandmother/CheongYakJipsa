package me.synn3r.jipsa.core.api.email.service;

public interface EmailService {

  void verifyEmail(String email);

  boolean checkVerifyCode(String email, int verifyCode);
}
