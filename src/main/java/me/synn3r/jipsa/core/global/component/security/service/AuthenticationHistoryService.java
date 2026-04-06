package me.synn3r.jipsa.core.global.component.security.service;

import org.springframework.security.core.AuthenticationException;

public interface AuthenticationHistoryService {

	void recordSuccess(String userId);

	void recordFailure(String userId, AuthenticationException exception);
}
