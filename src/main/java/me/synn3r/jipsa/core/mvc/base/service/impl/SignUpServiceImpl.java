package me.synn3r.jipsa.core.mvc.base.service.impl;

import org.springframework.stereotype.Service;

import me.synn3r.jipsa.core.api.member.repository.MemberRepository;
import me.synn3r.jipsa.core.mvc.base.domain.SignUpForm;
import me.synn3r.jipsa.core.mvc.base.service.SignUpService;

@Service
public class SignUpServiceImpl implements SignUpService {
	private final MemberRepository memberRepository;

	public SignUpServiceImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public void signUp(SignUpForm signUpForm) {
		if (memberRepository.existsMemberByUserId(signUpForm.getUserId())) {
			throw new IllegalArgumentException("사용자가 이미 존재합니다. ");
		}

	}
}
