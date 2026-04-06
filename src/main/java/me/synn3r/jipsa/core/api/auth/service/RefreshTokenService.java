package me.synn3r.jipsa.core.api.auth.service;

public interface RefreshTokenService {

	void save(String userId, String tokenId);

	boolean validate(String userId, String tokenId);

	void delete(String userId);
}
