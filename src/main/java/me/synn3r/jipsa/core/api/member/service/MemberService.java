package me.synn3r.jipsa.core.api.member.service;

import java.util.List;
import me.synn3r.jipsa.core.api.member.domain.MemberRequest;
import me.synn3r.jipsa.core.api.member.domain.MemberResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberSearchCondition;

public interface MemberService {


  List<MemberResponse> findMembers(MemberSearchCondition memberSearchCondition);

  MemberResponse findMember(long id);

  long saveMember(MemberRequest memberRequest);

  void updateMember(MemberRequest memberRequest);

  void updatePassword(MemberRequest memberRequest);

  void deleteMember(long id);

  MemberResponse findMemberByUserId(String username);

}
