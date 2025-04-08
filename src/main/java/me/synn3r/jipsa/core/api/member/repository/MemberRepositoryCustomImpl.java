package me.synn3r.jipsa.core.api.member.repository;

import static me.synn3r.jipsa.core.api.member.entity.QMember.member;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import me.synn3r.jipsa.core.api.base.enumeration.DeleteType;
import me.synn3r.jipsa.core.api.member.domain.MemberResponse;
import me.synn3r.jipsa.core.api.member.domain.MemberSearchCondition;
import org.springframework.util.StringUtils;

public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public MemberRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
    this.jpaQueryFactory = jpaQueryFactory;
  }

  @Override
  public List<MemberResponse> findMembers(MemberSearchCondition memberSearchCondition) {
    return jpaQueryFactory.select(Projections.fields(
        MemberResponse.class,
        member.id,
        member.name,
        member.email,
        member.role
      ))
      .from(member)
      .where(
        StringUtils.hasText(memberSearchCondition.getName()) ? member.name.contains(
          memberSearchCondition.getName()) : null,
        StringUtils.hasText(memberSearchCondition.getEmail()) ? member.email.containsIgnoreCase(
          memberSearchCondition.getEmail()) : null,
        member.deleteType.eq(DeleteType.ACTIVE)
      )
      .fetch();
  }
}
