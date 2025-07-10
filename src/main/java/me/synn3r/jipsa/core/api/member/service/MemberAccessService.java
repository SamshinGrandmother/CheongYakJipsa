package me.synn3r.jipsa.core.api.member.service;

import java.util.List;
import me.synn3r.jipsa.core.api.member.domain.MemberAccessHistoryResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberAccessSearchCondition;
import me.synn3r.jipsa.core.api.member.entity.Member;
import me.synn3r.jipsa.core.component.security.enumerations.AuthenticationFailureType;

public interface MemberAccessService {

    List<MemberAccessHistoryResponse> getMemberAccessHistory(
      MemberAccessSearchCondition searchCondition);

    void saveMemberAccessHistory(Member member);

    void saveMemberAccessFailureHistory(Member member,
      AuthenticationFailureType authenticationFailureType);

    void saveMemberAccessFailureHistory(String memberId,
      AuthenticationFailureType authenticationFailureType);

}
