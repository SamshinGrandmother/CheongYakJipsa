package me.synn3r.jipsa.core.api.security.service;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.api.base.enumeration.ResultType;
import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.api.member.entity.MemberAccessHistory;
import me.synn3r.jipsa.core.api.member.repository.MemberAccessHistoryRepository;
import me.synn3r.jipsa.core.api.member.repository.MemberRepository;
import me.synn3r.jipsa.core.global.component.security.enums.AuthenticationFailureType;
import me.synn3r.jipsa.core.global.component.security.service.AuthenticationHistoryService;

@Service
@RequiredArgsConstructor
public class AuthenticationHistoryServiceImpl implements AuthenticationHistoryService {

	private final MemberAccessHistoryRepository memberAccessHistoryRepository;
	private final MemberRepository memberRepository;

	@Override
	@Transactional
	public void recordSuccess(String userId) {
		Member member = memberRepository.findByUserId(userId);
		MemberAccessHistory history = new MemberAccessHistory(
			member,
			userId,
			ResultType.SUCCESS,
			null
		);
		memberAccessHistoryRepository.save(history);
	}

	@Override
	@Transactional
	public void recordFailure(String userId, AuthenticationException exception) {
		Member member = memberRepository.findByUserId(userId);
		AuthenticationFailureType failureType = AuthenticationFailureType.valueOf(exception);

		MemberAccessHistory history = new MemberAccessHistory(
			member,
			userId,
			ResultType.FAILURE,
			failureType
		);
		memberAccessHistoryRepository.save(history);
	}
}
