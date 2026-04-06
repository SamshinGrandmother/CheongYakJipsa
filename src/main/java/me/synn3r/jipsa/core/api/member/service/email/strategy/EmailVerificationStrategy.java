package me.synn3r.jipsa.core.api.member.service.email.strategy;

/**
 * 이메일 인증 전략 인터페이스.
 *
 * <p>이메일 인증 방식을 추상화하여 다양한 인증 방법을 지원합니다.
 * Strategy 패턴을 적용하여 런타임에 인증 방식을 교체할 수 있습니다.</p>
 *
 * <h2>구현체</h2>
 * <table border="1">
 *   <tr>
 *     <th>구현체</th>
 *     <th>설명</th>
 *     <th>기본 활성화</th>
 *   </tr>
 *   <tr>
 *     <td>{@link CodeEmailVerificationStrategy}</td>
 *     <td>6자리 인증번호 입력 방식</td>
 *     <td><b>예 (@Primary)</b></td>
 *   </tr>
 *   <tr>
 *     <td>{@link LinkEmailVerificationStrategy}</td>
 *     <td>인증 링크 클릭 방식</td>
 *     <td>아니오</td>
 *   </tr>
 * </table>
 *
 * <h2>인증 방식 전환 방법</h2>
 * <p>기본 인증 방식을 변경하려면:</p>
 * <ol>
 *   <li>{@link CodeEmailVerificationStrategy}의 {@code @Primary} 어노테이션을 제거</li>
 *   <li>{@link LinkEmailVerificationStrategy}에 {@code @Primary} 어노테이션을 추가</li>
 *   <li>또는 설정 파일에서 {@code @ConditionalOnProperty}를 사용하여 조건부 활성화</li>
 * </ol>
 *
 * <h2>사용 예시</h2>
 * <pre>{@code
 * // 서비스에서 주입 받아 사용 (@Primary로 인해 CodeEmailVerificationStrategy 주입)
 * @Service
 * public class EmailVerificationService {
 *     private final EmailVerificationStrategy strategy;
 *
 *     public void sendVerification(String email) {
 *         strategy.sendVerification(email);
 *     }
 *
 *     public boolean verify(String email, String code) {
 *         return strategy.verify(email, code);
 *     }
 * }
 * }</pre>
 *
 * @author synn3r
 * @since 1.0
 * @see CodeEmailVerificationStrategy
 * @see LinkEmailVerificationStrategy
 */
public interface EmailVerificationStrategy {

	/**
	 * 인증 이메일을 발송합니다.
	 *
	 * <p>구현체에 따라 인증번호가 포함된 이메일 또는
	 * 인증 링크가 포함된 이메일이 발송됩니다.</p>
	 *
	 * <p><b>구현체별 동작:</b></p>
	 * <ul>
	 *   <li>{@link CodeEmailVerificationStrategy}: 6자리 숫자 코드를 생성하여 이메일 발송</li>
	 *   <li>{@link LinkEmailVerificationStrategy}: UUID 토큰 기반 인증 링크를 이메일 발송</li>
	 * </ul>
	 *
	 * @param email 인증 대상 이메일 주소
	 * @throws IllegalArgumentException 유효하지 않은 이메일 형식인 경우
	 * @throws RuntimeException         이메일 발송 실패 시
	 */
	void sendVerification(String email);

	/**
	 * 인증을 검증합니다.
	 *
	 * <p>사용자가 입력한 인증 데이터(코드 또는 토큰)를 검증합니다.
	 * 검증 성공 시 모든 저장소에서 인증 완료 상태로 표시됩니다.</p>
	 *
	 * @param email            인증 대상 이메일 주소
	 * @param verificationData 인증 코드 (6자리 숫자) 또는 토큰 (UUID 문자열)
	 * @return 인증 성공 여부 (true: 인증 성공)
	 */
	boolean verify(String email, String verificationData);

	/**
	 * 이 전략의 인증 타입을 반환합니다.
	 *
	 * @return 인증 타입 ({@link EmailVerificationType#CODE} 또는 {@link EmailVerificationType#LINK})
	 */
	EmailVerificationType getType();
}
