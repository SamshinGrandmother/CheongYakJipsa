package me.synn3r.jipsa.core.api.member.repository;

import java.util.List;
import me.synn3r.jipsa.core.api.member.domain.MemberResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberSearchCondition;

public interface MemberRepositoryCustom {

  List<MemberResponse> findMembers(MemberSearchCondition memberSearchCondition);
}
