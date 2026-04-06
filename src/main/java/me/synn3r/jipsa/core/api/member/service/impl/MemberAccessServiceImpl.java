package me.synn3r.jipsa.core.api.member.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.synn3r.jipsa.core.api.commons.enumeration.ResultType;
import me.synn3r.jipsa.core.api.member.domain.MemberAccessHistoryResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberAccessSearchCondition;
import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.api.member.entity.mapper.MemberAccessHistoryMapper;
import me.synn3r.jipsa.core.api.member.repository.MemberAccessHistoryRepository;
import me.synn3r.jipsa.core.api.member.service.MemberAccessService;
import me.synn3r.jipsa.core.global.component.security.enums.AuthenticationFailureType;

@Service
@Transactional(readOnly = true)
public class MemberAccessServiceImpl implements MemberAccessService {

	private final MemberAccessHistoryMapper memberAccessHistoryMapper;
	private final MemberAccessHistoryRepository memberAccessHistoryRepository;

	public MemberAccessServiceImpl(MemberAccessHistoryMapper memberAccessHistoryMapper,
		MemberAccessHistoryRepository memberAccessHistoryRepository) {
		this.memberAccessHistoryMapper = memberAccessHistoryMapper;
		this.memberAccessHistoryRepository = memberAccessHistoryRepository;
	}

	@Override
	public List<MemberAccessHistoryResponse> getMemberAccessHistory(
		MemberAccessSearchCondition searchCondition) {
		return memberAccessHistoryRepository.getMemberAccessResponseList(searchCondition);
	}

	@Override
	@Transactional
	public void saveMemberAccessHistory(Member member) {
		memberAccessHistoryRepository.save(
			memberAccessHistoryMapper.toEntity(member, ResultType.SUCCESS));
	}

	@Override
	@Transactional
	public void saveMemberAccessFailureHistory(Member member,
		AuthenticationFailureType authenticationFailureType) {

		memberAccessHistoryRepository.save(
			memberAccessHistoryMapper.toEntity(member, ResultType.FAILURE, authenticationFailureType));

	}

	@Override
	public void saveMemberAccessFailureHistory(String memberId, AuthenticationFailureType authenticationFailureType) {
		memberAccessHistoryRepository.save(
			memberAccessHistoryMapper.toEntity(memberId, ResultType.FAILURE, authenticationFailureType));
	}

}
