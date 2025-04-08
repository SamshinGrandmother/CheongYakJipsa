package me.synn3r.jipsa.core.api.team.repository;

import static me.synn3r.jipsa.core.api.team.entity.QTeam.team;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import me.synn3r.jipsa.core.api.base.enumeration.DeleteType;
import me.synn3r.jipsa.core.api.team.domain.TeamResponse;
import me.synn3r.jipsa.core.api.team.domain.TeamSearchCondition;

public class TeamRepositoryCustomImpl implements TeamRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public TeamRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
    this.jpaQueryFactory = jpaQueryFactory;
  }


  @Override
  public List<TeamResponse> getTeamList(TeamSearchCondition teamSearchCondition) {
    return jpaQueryFactory.select(
        Projections.fields(TeamResponse.class,
          team.id,
          team.name
        )
      ).from(team)
      .where(teamSearchCondition.getName() == null ? null : team.name.containsIgnoreCase(
          teamSearchCondition.getName()),
        team.deleteType.eq(DeleteType.ACTIVE))
      .fetch();
  }
}
