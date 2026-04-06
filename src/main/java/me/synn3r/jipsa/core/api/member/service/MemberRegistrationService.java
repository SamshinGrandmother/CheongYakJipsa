package me.synn3r.jipsa.core.api.member.service;

import me.synn3r.jipsa.core.api.member.domain.MemberRegistrationRequest;

/**
 * 회원가입 서비스 인터페이스.
 *
 * <p>회원가입 프로세스와 관련된 비즈니스 로직을 정의합니다.
 * 이메일 인증, 중복 검사, 회원 등록 기능을 포함합니다.</p>
 *
 * <h2>회원가입 흐름</h2>
 * <ol>
 *   <li>{@link #sendVerificationEmail(String)} - 이메일 인증 코드 발송</li>
 *   <li>{@link #verifyEmailCode(String, String)} - 인증 코드 검증</li>
 *   <li>{@link #registerMember(MemberRegistrationRequest)} - 회원 등록</li>
 * </ol>
 *
 * <h2>검증 규칙</h2>
 * <ul>
 *   <li>이메일 인증이 완료되어야 회원가입 가능</li>
 *   <li>이메일 및 사용자 ID 중복 불가</li>
 *   <li>비밀번호는 대/소문자, 숫자, 특수문자 포함 8자 이상</li>
 * </ul>
 *
 * @author synn3r
 * @since 1.0
 * @see MemberRegistrationRequest
 */
public interface MemberRegistrationService {

	/**
	 * 신규 회원을 등록합니다.
	 *
	 * <p>이메일 인증이 완료된 상태에서만 회원가입이 가능합니다.
	 * 이메일 또는 사용자 ID가 중복되면 예외가 발생합니다.</p>
	 *
	 * @param request 회원가입 요청 정보
	 * @return 생성된 회원 ID
	 * @throws IllegalStateException                      이메일 인증이 완료되지 않은 경우
	 * @throws org.springframework.dao.DuplicateKeyException 이메일 또는 사용자 ID가 중복된 경우
	 */
	Long registerMember(MemberRegistrationRequest request);

	/**
	 * 이메일 인증 코드를 발송합니다.
	 *
	 * <p>입력된 이메일 주소로 6자리 인증 코드를 발송합니다.
	 * 인증 코드는 5분간 유효합니다.</p>
	 *
	 * @param email 인증 대상 이메일 주소
	 * @throws RuntimeException 이메일 발송 실패 시
	 */
	void sendVerificationEmail(String email);

	/**
	 * 이메일 인증 코드를 검증합니다.
	 *
	 * <p>사용자가 입력한 인증 코드를 검증합니다.
	 * 검증 성공 시 인증 완료 상태가 저장됩니다.</p>
	 *
	 * @param email 인증 대상 이메일 주소
	 * @param code  사용자가 입력한 6자리 인증 코드
	 * @return 인증 성공 여부 (true: 인증 성공)
	 */
	boolean verifyEmailCode(String email, String code);

	/**
	 * 이메일 인증 토큰을 검증합니다 (링크 방식).
	 *
	 * <p>이메일에 포함된 인증 링크를 클릭하면 호출됩니다.
	 * 토큰 검증 후 인증 완료 처리합니다.</p>
	 *
	 * @param token Base64 인코딩된 인증 토큰
	 * @return 인증 성공 여부 (true: 인증 성공)
	 */
	boolean verifyEmailByToken(String token);
}
