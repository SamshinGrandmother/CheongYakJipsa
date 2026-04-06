package me.synn3r.jipsa.core.api.member.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.synn3r.jipsa.core.global.component.validation.core.password.BasePassword;

/**
 * 회원 비밀번호 입력을 위한 DTO.
 *
 * <p>{@link BasePassword}를 상속받아 비밀번호 복잡도 검증과
 * 비밀번호 확인 일치 검증을 자동으로 적용받습니다.</p>
 *
 * @author synn3r
 * @since 1.0
 * @see BasePassword
 */
@Getter
@NoArgsConstructor
public class MemberPassword extends BasePassword {
}
