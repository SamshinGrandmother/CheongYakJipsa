package me.synn3r.jipsa.core.component.security;

import me.synn3r.jipsa.core.api.member.entity.mapper.MemberMapper;
import me.synn3r.jipsa.core.api.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;
  private final MemberMapper memberMapper;

  public DefaultUserDetailsService(MemberRepository memberRepository, MemberMapper memberMapper) {
    this.memberRepository = memberRepository;
    this.memberMapper = memberMapper;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return memberMapper.toUserDetails(memberRepository.findByUserId(username));
  }
}
