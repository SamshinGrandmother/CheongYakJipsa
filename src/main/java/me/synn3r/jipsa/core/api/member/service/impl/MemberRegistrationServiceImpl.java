package me.synn3r.jipsa.core.api.member.service.impl;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synn3r.jipsa.core.api.member.domain.MemberRegistrationRequest;
import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.api.member.entity.mapper.MemberMapper;
import me.synn3r.jipsa.core.api.member.repository.MemberRepository;
import me.synn3r.jipsa.core.api.member.service.MemberRegistrationService;
import me.synn3r.jipsa.core.api.member.service.email.EmailVerificationService;

/**
 * 회원가입 서비스 구현체.
 *
 * <p>회원가입 프로세스의 비즈니스 로직을 구현합니다.
 * 이메일 인증, 중복 검사, 비밀번호 암호화, 회원 저장을 처리합니다.</p>
 *
 * <h2>회원가입 프로세스</h2>
 * <ol>
 *   <li><b>이메일 인증 요청</b>: {@link #sendVerificationEmail(String)}</li>
 *   <li><b>인증 코드 검증</b>: {@link #verifyEmailCode(String, String)}</li>
 *   <li><b>회원 등록</b>: {@link #registerMember(MemberRegistrationRequest)}
 *     <ul>
 *       <li>이메일 인증 완료 여부 확인</li>
 *       <li>이메일/사용자ID 중복 검사</li>
 *       <li>비밀번호 암호화</li>
 *       <li>회원 정보 저장</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <h2>트랜잭션</h2>
 * <p>기본적으로 읽기 전용 트랜잭션이 적용되며,
 * 쓰기 작업에만 {@code @Transactional}을 명시합니다.</p>
 *
 * @author synn3r
 * @since 1.0
 * @see MemberRegistrationService
 * @see EmailVerificationService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRegistrationServiceImpl implements MemberRegistrationService {

	private final MemberRepository memberRepository;
	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;
	private final EmailVerificationService emailVerificationService;
	private final MessageSource messageSource;

	/**
	 * {@inheritDoc}
	 *
	 * <p>회원가입 처리 순서:</p>
	 * <ol>
	 *   <li>이메일 인증 완료 여부 확인</li>
	 *   <li>이메일 중복 검사</li>
	 *   <li>사용자 ID 중복 검사</li>
	 *   <li>비밀번호 암호화</li>
	 *   <li>회원 정보 저장</li>
	 * </ol>
	 */
	@Override
	@Transactional
	public Long registerMember(MemberRegistrationRequest request) {
		log.info("회원가입 요청: email={}, userId={}", request.getEmail(), request.getUserId());

		validateEmailVerified(request.getEmail());
		validateEmailNotDuplicate(request.getEmail());
		validateUserIdNotDuplicate(request.getUserId());

		String encodedPassword = passwordEncoder.encode(request.getPassword());
		Member member = memberMapper.toEntity(request, encodedPassword);
		Member savedMember = memberRepository.save(member);

		log.info("회원가입 완료: memberId={}, userId={}", savedMember.getId(), savedMember.getUserId());
		return savedMember.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendVerificationEmail(String email) {
		log.debug("이메일 인증 코드 발송 요청: email={}", email);
		emailVerificationService.sendVerificationEmail(email);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verifyEmailCode(String email, String code) {
		log.debug("이메일 인증 코드 검증 요청: email={}", email);
		return emailVerificationService.verifyCode(email, code);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verifyEmailByToken(String token) {
		log.debug("이메일 인증 토큰 검증 요청");
		return emailVerificationService.verifyByToken(token);
	}

	/**
	 * 이메일 인증이 완료되었는지 검증합니다.
	 *
	 * @param email 검증할 이메일 주소
	 * @throws IllegalStateException 이메일 인증이 완료되지 않은 경우
	 */
	private void validateEmailVerified(String email) {
		if (!emailVerificationService.isEmailVerified(email)) {
			String message = messageSource.getMessage(
				"member.registration.emailNotVerified", null, LocaleContextHolder.getLocale());
			log.warn("이메일 미인증 상태로 회원가입 시도: email={}", email);
			throw new IllegalStateException(message);
		}
	}

	/**
	 * 이메일 중복 여부를 검증합니다.
	 *
	 * @param email 검증할 이메일 주소
	 * @throws DuplicateKeyException 이메일이 이미 존재하는 경우
	 */
	private void validateEmailNotDuplicate(String email) {
		if (memberRepository.existsMemberByEmail(email)) {
			String message = messageSource.getMessage(
				"member.registration.emailDuplicate", null, LocaleContextHolder.getLocale());
			log.warn("중복 이메일로 회원가입 시도: email={}", email);
			throw new DuplicateKeyException(message);
		}
	}

	/**
	 * 사용자 ID 중복 여부를 검증합니다.
	 *
	 * @param userId 검증할 사용자 ID
	 * @throws DuplicateKeyException 사용자 ID가 이미 존재하는 경우
	 */
	private void validateUserIdNotDuplicate(String userId) {
		if (memberRepository.findByUserId(userId) != null) {
			String message = messageSource.getMessage(
				"member.registration.userIdDuplicate", null, LocaleContextHolder.getLocale());
			log.warn("중복 사용자 ID로 회원가입 시도: userId={}", userId);
			throw new DuplicateKeyException(message);
		}
	}
}
