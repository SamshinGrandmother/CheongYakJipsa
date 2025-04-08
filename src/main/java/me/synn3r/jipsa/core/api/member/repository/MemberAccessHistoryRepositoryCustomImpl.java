package me.synn3r.jipsa.core.api.member.repository;

import static me.synn3r.jipsa.core.api.member.entity.QMemberAccessHistory.memberAccessHistory;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import me.synn3r.jipsa.core.api.member.domain.MemberAccessHistoryResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberAccessSearchCondition;

public class MemberAccessHistoryRepositoryCustomImpl implements
  MemberAccessHistoryRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public MemberAccessHistoryRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
    this.jpaQueryFactory = jpaQueryFactory;
  }

  @Override
  public List<MemberAccessHistoryResponse> getMemberAccessResponseList(
    MemberAccessSearchCondition searchCondition) {
    return jpaQueryFactory.select(
        Projections.fields(
          MemberAccessHistoryResponse.class,
          memberAccessHistory.id,
          memberAccessHistory.member.name,
          memberAccessHistory.resultType,
          memberAccessHistory.failureType,
          memberAccessHistory.accessAt
        )
      )
      .from(memberAccessHistory)
      .join(memberAccessHistory.member)
      .fetch();
  }
}
