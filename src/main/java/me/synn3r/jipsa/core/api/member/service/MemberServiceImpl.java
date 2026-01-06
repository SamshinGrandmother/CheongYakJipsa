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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final MessageSource messageSource;

    public MemberServiceImpl(MemberRepository memberRepository, MemberMapper memberMapper,
      PasswordEncoder passwordEncoder, MemberAccessService memberAccessService,
      MessageSource messageSource) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
        this.memberAccessService = memberAccessService;
        this.messageSource = messageSource;
    }

    @Override
    public List<MemberResponse> findMembers(MemberSearchCondition memberSearchCondition) {
        return memberRepository.findMembers(memberSearchCondition);
    }

    @Override
    public MemberResponse findMember(long id) {
        return memberMapper.toMemberResponse(memberRepository.findById(id)
          .orElseThrow(() -> new NoSuchElementException(getMessage("member.not-found"))));
    }

    @Override
    @Transactional
    public long saveMember(MemberRequest memberRequest) {
        if (memberRepository.existsMemberByEmail(memberRequest.getEmail())) {
            throw new DuplicateKeyException(getMessage("member.email.duplicate"));
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
          .orElseThrow(() -> new NoSuchElementException(getMessage("member.not-found")));

        member.updateMemberInfo(memberRequest);

    }

    @Override
    @Transactional
    public void updatePassword(MemberRequest memberRequest) {
        Member member = memberRepository.findById(memberRequest.getId())
          .orElseThrow(() -> new NoSuchElementException(getMessage("member.not-found")));
        member.updatePassword(passwordEncoder.encode(memberRequest.getPassword()));
    }

    @Override
    @Transactional
    public void deleteMember(long id) {
        Member member = memberRepository.findById(id)
          .orElseThrow(() -> new NoSuchElementException(getMessage("member.not-found")));
        member.delete();
    }

    @Override
    public MemberResponse findMemberByUserId(String userId) {
        return memberMapper.toMemberResponse(memberRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public void saveAuthenticationFailureHistory(String username,
      AuthenticationFailureType authenticationFailureType) {
        Member member = memberRepository.findByUserId(username);
        if (member != null) {
            memberAccessService.saveMemberAccessFailureHistory(member, authenticationFailureType);
            return;
        }
        memberAccessService.saveMemberAccessFailureHistory(username, authenticationFailureType);
    }

    @Override
    @Transactional
    public void saveAuthenticationSuccessHistory(UserDetails userDetails) {
        Member member = memberRepository.findByUserId(userDetails.getUsername());
        memberAccessService.saveMemberAccessHistory(member);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
