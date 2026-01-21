package me.synn3r.jipsa.core.api.auth.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "refreshToken")
public class RefreshToken {

	@Id
	private String tokenId;

	@Indexed
	private String userId;

	private String token;

	@TimeToLive
	private Long expiration;
}
