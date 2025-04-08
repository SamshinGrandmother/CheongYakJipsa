package me.synn3r.jipsa.core.api.member.repository;

import java.util.List;
import me.synn3r.jipsa.core.api.member.domain.MemberAccessHistoryResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberAccessSearchCondition;

public interface MemberAccessHistoryRepositoryCustom {

  List<MemberAccessHistoryResponse> getMemberAccessResponseList(
    MemberAccessSearchCondition searchCondition);
}
