package me.synn3r.jipsa.core.api.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import me.synn3r.jipsa.core.api.auth.domain.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

	Optional<RefreshToken> findByUserId(String userId);

	List<RefreshToken> findAllByUserId(String userId);

	void deleteByUserId(String userId);

	void deleteAllByUserId(String userId);
}
