package me.synn3r.jipsa.core.component.security.userdetails;

import java.util.Optional;
import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.api.member.entity.mapper.MemberMapper;
import me.synn3r.jipsa.core.api.member.repository.MemberRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final MessageSource messageSource;

    public DefaultUserDetailsService(MemberRepository memberRepository, MemberMapper memberMapper,
      MessageSource messageSource) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
        this.messageSource = messageSource;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = Optional.ofNullable(memberRepository.findByUserId(username))
          .orElseThrow(() -> new UsernameNotFoundException(
            messageSource.getMessage("auth.user.not-found", new Object[]{username},
              LocaleContextHolder.getLocale())));
        return memberMapper.toUserDetails(member);
    }
}
