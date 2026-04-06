package me.synn3r.jipsa.core.api.member.service.email.strategy;

/**
 * 이메일 인증 타입을 정의하는 열거형.
 *
 * <p>이메일 인증 시 사용되는 두 가지 방식을 구분합니다.
 * 각 타입에 따라 {@link EmailVerificationStrategy} 구현체가 다르게 동작합니다.</p>
 *
 * <h2>인증 타입별 특징</h2>
 * <table border="1">
 *   <tr>
 *     <th>타입</th>
 *     <th>설명</th>
 *     <th>유효 시간</th>
 *   </tr>
 *   <tr>
 *     <td>{@link #CODE}</td>
 *     <td>6자리 숫자 인증번호 입력</td>
 *     <td>5분</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #LINK}</td>
 *     <td>이메일 내 인증 링크 클릭</td>
 *     <td>30분</td>
 *   </tr>
 * </table>
 *
 * @author synn3r
 * @since 1.0
 * @see EmailVerificationStrategy
 */
public enum EmailVerificationType {

	/**
	 * 6자리 인증번호 입력 방식.
	 *
	 * <p>이메일로 발송된 6자리 숫자 코드를 회원가입 화면에서
	 * 직접 입력하여 인증하는 방식입니다. 기본(Primary) 인증 방식으로 사용됩니다.</p>
	 */
	CODE,

	/**
	 * 인증 링크 클릭 방식.
	 *
	 * <p>이메일로 발송된 고유 인증 링크를 클릭하여
	 * 자동으로 인증을 완료하는 방식입니다.</p>
	 */
	LINK
}
