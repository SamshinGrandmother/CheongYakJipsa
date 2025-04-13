package me.synn3r.jipsa.core.api.member.service;

import java.util.List;
import java.util.NoSuchElementException;
import me.synn3r.jipsa.core.api.member.domain.MemberRequest;
import me.synn3r.jipsa.core.api.member.domain.MemberResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberSearchCondition;
import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.api.member.entity.mapper.MemberMapper;
import me.synn3r.jipsa.core.api.member.repository.MemberRepository;
import me.synn3r.jipsa.core.component.security.enumerations.AuthenticationFailureType;
import me.synn3r.jipsa.core.component.security.logging.AuthenticationFailureLogger;
import me.synn3r.jipsa.core.component.security.logging.AuthenticationSuccessLogger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService, AuthenticationSuccessLogger,
  AuthenticationFailureLogger {

  private final MemberRepository memberRepository;
  private final MemberMapper memberMapper;
  private final PasswordEncoder passwordEncoder;
  private final MemberAccessService memberAccessService;

  public MemberServiceImpl(MemberRepository memberRepository, MemberMapper memberMapper,
    PasswordEncoder passwordEncoder, MemberAccessService memberAccessService) {
    this.memberRepository = memberRepository;
    this.memberMapper = memberMapper;
    this.passwordEncoder = passwordEncoder;
    this.memberAccessService = memberAccessService;
  }

  @Override
  public List<MemberResponse> findMembers(MemberSearchCondition memberSearchCondition) {
    return memberRepository.findMembers(memberSearchCondition);
  }

  @Override
  public MemberResponse findMember(long id) {
    return memberMapper.toMemberResponse(memberRepository.findById(id)
      .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다. ")));
  }

  @Override
  @Transactional
  public long saveMember(MemberRequest memberRequest) {
    if (memberRepository.existsMemberByEmail(memberRequest.getEmail())) {
      throw new DuplicateKeyException("이미 존재하는 이메일 입니다. ");
    }
    Member member = memberRepository.save(
      memberMapper.toEntity(memberRequest, passwordEncoder.encode(
        memberRequest.getPassword())));
    return member.getId();
  }

  @Override
  @Transactional
  public void updateMember(MemberRequest memberRequest) {
    Member member = memberRepository.findById(memberRequest.getId())
      .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다. "));
    member.update(memberRequest.getName());
  }

  @Override
  @Transactional
  public void updatePassword(MemberRequest memberRequest) {
    Member member = memberRepository.findById(memberRequest.getId())
      .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다. "));
    member.updatePassword(memberRequest.getPassword());
  }

  @Override
  @Transactional
  public void deleteMember(long id) {
    Member member = memberRepository.findById(id)
      .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다. "));
    member.delete();
  }

  @Override
  @Transactional
  public void saveAuthenticationFailureHistory(String username,
    AuthenticationFailureType authenticationFailureType) {
    Member member = memberRepository.findByEmail(username);
    memberAccessService.saveMemberAccessFailureHistory(member, authenticationFailureType);
  }

  @Override
  @Transactional
  public void saveAuthenticationSuccessHistory(UserDetails userDetails) {
    Member member = memberRepository.findByEmail(userDetails.getUsername());
    memberAccessService.saveMemberAccessHistory(member);
  }
}
