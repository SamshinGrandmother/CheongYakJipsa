package me.synn3r.jipsa.core.mvc.base.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.api.member.repository.MemberRepository;
import me.synn3r.jipsa.core.component.security.Role;
import me.synn3r.jipsa.core.mvc.base.domain.SignUpForm;
import me.synn3r.jipsa.core.mvc.base.mapper.SignUpMapper;
import me.synn3r.jipsa.core.mvc.base.service.SignUpService;

@Service
public class SignUpServiceImpl implements SignUpService {
	private final MemberRepository memberRepository;
	private final SignUpMapper signUpMapper;
	private final PasswordEncoder passwordEncoder;

	public SignUpServiceImpl(MemberRepository memberRepository, SignUpMapper signUpMapper,
		PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.signUpMapper = signUpMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void signUp(SignUpForm signUpForm) {
		if (memberRepository.existsMemberByUserId(signUpForm.getUserId())) {
			throw new IllegalArgumentException("signUp.duplicate.userId");
		} else if (memberRepository.existsMemberByEmail(signUpForm.getEmail())) {
			throw new IllegalArgumentException("signUp.duplicate.email");
		}

		Member member = signUpMapper.toMember(signUpForm,
			passwordEncoder.encode(signUpForm.getPassword().getPassword()), Role.NORMAL);

		memberRepository.save(member);

	}
}
