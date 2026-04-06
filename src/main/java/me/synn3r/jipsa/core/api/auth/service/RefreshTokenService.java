package me.synn3r.jipsa.core.api.auth.service;

/**
 * Refresh Token의 저장, 검증, 삭제를 담당하는 서비스 인터페이스.
 *
 * <p>JWT Refresh Token Rotation 패턴을 지원하기 위해 서버 측에서
 * 유효한 tokenId를 관리합니다. 클라이언트가 보유한 Refresh Token의 tokenId가
 * 서버에 저장된 값과 일치해야만 Access Token 재발급이 허용됩니다.</p>
 *
 * <h2>Refresh Token Rotation 흐름</h2>
 * <ol>
 *   <li>로그인 성공: 새 Refresh Token 생성 → {@link #save(String, String)} 호출</li>
 *   <li>토큰 갱신 요청: {@link #validate(String, String)}으로 tokenId 일치 여부 확인</li>
	 *   <li>갱신 성공: {@link #rotate(String, String, String)}으로 기존 tokenId를 조건부 교체</li>
 *   <li>로그아웃: {@link #delete(String)}으로 Redis에서 토큰 삭제</li>
 * </ol>
 *
 * <h2>탈취 감지</h2>
 * <p>이미 사용된 tokenId(갱신 후 폐기된 tokenId)로 요청이 들어오면
 * {@link #validate(String, String)}이 {@code false}를 반환합니다.
 * 이는 Refresh Token이 탈취되어 재사용되는 경우를 감지하는 데 활용됩니다.</p>
 *
 * @author synn3r
 * @see me.synn3r.jipsa.core.api.auth.service.impl.RedisRefreshTokenServiceImpl Redis 기반 구현체
 * @since 1.0
 */
public interface RefreshTokenService {

	/**
	 * Refresh Token의 tokenId를 저장합니다.
	 *
	 * <p>기존에 저장된 tokenId가 있으면 덮어씁니다.
	 * TTL은 Refresh Token 유효기간({@code JwtConfig.refreshTokenValidity})과 동일하게 설정됩니다.</p>
	 *
	 * @param userId  저장할 사용자의 식별자 (JWT subject)
	 * @param tokenId Refresh Token에 포함된 고유 식별자 (UUID)
	 */
	void save(String userId, String tokenId);

	/**
	 * 주어진 tokenId가 서버에 저장된 값과 일치하는지 검증합니다.
	 *
	 * <p>다음 경우에 {@code false}를 반환합니다:</p>
	 * <ul>
	 *   <li>해당 userId의 키가 Redis에 존재하지 않는 경우 (만료 또는 로그아웃)</li>
	 *   <li>저장된 tokenId와 전달된 tokenId가 다른 경우 (이미 사용된 토큰)</li>
	 * </ul>
	 *
	 * @param userId  검증할 사용자의 식별자
	 * @param tokenId 클라이언트가 전송한 Refresh Token의 tokenId
	 * @return 저장된 tokenId와 일치하면 {@code true}, 그렇지 않으면 {@code false}
	 */
	boolean validate(String userId, String tokenId);

	/**
	 * 현재 tokenId가 일치할 때만 새 tokenId로 원자적으로 교체하고 Refresh TTL을 설정합니다.
	 * 로그아웃 또는 만료로 삭제된 키를 다시 생성하지 않습니다.
	 *
	 * @param userId 사용자 식별자
	 * @param expectedTokenId 갱신 요청에서 검증한 기존 tokenId
	 * @param newTokenId 새 Refresh Token의 tokenId
	 * @return 교체에 성공하면 true, 키가 없거나 다른 tokenId이면 false
	 */
	boolean rotate(String userId, String expectedTokenId, String newTokenId);

	/**
	 * 해당 사용자의 Refresh Token을 무효화합니다.
	 *
	 * <p>Redis에서 해당 userId의 키를 삭제합니다.
	 * 이후 해당 사용자의 Refresh Token으로는 Access Token 재발급이 불가능합니다.</p>
	 *
	 * <p>키가 존재하지 않는 경우에도 예외 없이 정상 처리됩니다(멱등성 보장).</p>
	 *
	 * @param userId 삭제할 사용자의 식별자
	 */
	void delete(String userId);
}
