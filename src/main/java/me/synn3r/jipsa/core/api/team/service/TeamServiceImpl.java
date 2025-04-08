package me.synn3r.jipsa.core.api.team.service;

import java.util.List;
import java.util.NoSuchElementException;
import me.synn3r.jipsa.core.api.base.enumeration.DeleteType;
import me.synn3r.jipsa.core.api.team.domain.TeamRequest;
import me.synn3r.jipsa.core.api.team.domain.TeamResponse;
import me.synn3r.jipsa.core.api.team.domain.TeamSearchCondition;
import me.synn3r.jipsa.core.api.team.entity.Team;
import me.synn3r.jipsa.core.api.team.entity.TeamMapper;
import me.synn3r.jipsa.core.api.team.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService {

  private final TeamRepository teamRepository;
  private final TeamMapper teamMapper;

  public TeamServiceImpl(TeamRepository teamRepository, TeamMapper teamMapper) {
    this.teamRepository = teamRepository;
    this.teamMapper = teamMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public List<TeamResponse> findTeams(TeamSearchCondition condition) {
    return teamRepository.getTeamList(condition);
  }

  @Override
  @Transactional(readOnly = true)
  public TeamResponse findTeam(long id) {
    Team team = teamRepository.findTeamByDeleteTypeAndId(DeleteType.ACTIVE, id)
      .orElseThrow(() -> new NoSuchElementException("부서가 존재하지 않습니다. "));
    return teamMapper.toResponse(team);
  }

  @Override
  @Transactional
  public long saveTeam(TeamRequest teamRequest) {
    Team team = teamRepository.save(teamMapper.toEntity(teamRequest));

    return team.getId();
  }

  @Override
  @Transactional
  public void updateTeam(TeamRequest teamRequest) {
    Team team = teamRepository.findTeamByDeleteTypeAndId(DeleteType.ACTIVE, teamRequest.getId())
      .orElseThrow(() -> new NoSuchElementException("부서가 존재하지 않습니다. "));

    team.update(teamRequest.getName());
  }

  @Override
  @Transactional
  public void deleteTeam(long id) {
    Team team = teamRepository.findById(id)
      .orElseThrow(() -> new NoSuchElementException("부서가 존재하지 않습니다. "));

    team.delete();
  }
}
