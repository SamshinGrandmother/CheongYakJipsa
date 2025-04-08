package me.synn3r.jipsa.core.api.team;

import me.synn3r.jipsa.core.api.team.domain.TeamRequest;
import me.synn3r.jipsa.core.api.team.domain.TeamResponse;
import me.synn3r.jipsa.core.api.team.domain.TeamSearchCondition;
import me.synn3r.jipsa.core.api.team.entity.Team;

public abstract class TeamTestSupport {

  protected TeamSearchCondition getMockedSearchCondition() {
    return getMockedSearchCondition("비서");
  }

  protected TeamSearchCondition getMockedSearchCondition(String name) {
    TeamSearchCondition condition = new TeamSearchCondition();
    condition.setName(name);

    return condition;
  }

  protected TeamRequest getMockedTeamRequest() {
    return getMockedTeamRequest(null, "비서실");
  }

  protected TeamRequest getMockedTeamRequest(Long id, String name) {
    return new TeamRequest(id, name);
  }

  protected Team getMockedTeam() {
    return getMockedTeam(null, "비서실");
  }

  protected Team getMockedTeam(Long id, String name) {
    return new Team(id, name);
  }

  protected TeamResponse getMockedTeamResponse() {
    return getMockedTeamResponse(1L, "비서실");
  }

  protected TeamResponse getMockedTeamResponse(long id, String name) {
    TeamResponse teamResponse = new TeamResponse();
    teamResponse.setId(id);
    teamResponse.setName(name);

    return teamResponse;
  }
}
