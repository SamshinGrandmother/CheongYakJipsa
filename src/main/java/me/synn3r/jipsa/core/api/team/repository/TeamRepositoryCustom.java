package me.synn3r.jipsa.core.api.team.repository;

import java.util.List;
import me.synn3r.jipsa.core.api.team.domain.TeamResponse;
import me.synn3r.jipsa.core.api.team.domain.TeamSearchCondition;

public interface TeamRepositoryCustom {

  List<TeamResponse> getTeamList(TeamSearchCondition teamSearchCondition);
}
