package me.synn3r.jipsa.core.api.team.service;

import java.util.List;
import me.synn3r.jipsa.core.api.team.domain.TeamRequest;
import me.synn3r.jipsa.core.api.team.domain.TeamResponse;
import me.synn3r.jipsa.core.api.team.domain.TeamSearchCondition;

public interface TeamService {

  List<TeamResponse> findTeams(TeamSearchCondition condition);

  TeamResponse findTeam(long id);

  long saveTeam(TeamRequest teamRequest);

  void updateTeam(TeamRequest teamRequest);

  void deleteTeam(long id);

}
