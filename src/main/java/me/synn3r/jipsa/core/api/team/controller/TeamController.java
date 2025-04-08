package me.synn3r.jipsa.core.api.team.controller;

import java.util.List;
import me.synn3r.jipsa.core.api.base.domain.Request.Insert;
import me.synn3r.jipsa.core.api.base.domain.Request.Update;
import me.synn3r.jipsa.core.api.team.domain.TeamRequest;
import me.synn3r.jipsa.core.api.team.domain.TeamResponse;
import me.synn3r.jipsa.core.api.team.domain.TeamSearchCondition;
import me.synn3r.jipsa.core.api.team.service.TeamService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamController {

  private final TeamService teamService;

  public TeamController(TeamService teamService) {
    this.teamService = teamService;
  }

  @GetMapping("/team")
  public List<TeamResponse> getTeamList(TeamSearchCondition teamSearchCondition) {
    return teamService.findTeams(teamSearchCondition);
  }

  @GetMapping("/team/{id}")
  public TeamResponse getTeam(@PathVariable("id") long id) {
    return teamService.findTeam(id);
  }

  @PostMapping("/team")
  public long saveTeam(@RequestBody @Validated(Insert.class) TeamRequest teamRequest) {
    return teamService.saveTeam(teamRequest);
  }

  @PutMapping("/team")
  public void updateTeam(@RequestBody @Validated(Update.class) TeamRequest teamRequest) {
    teamService.updateTeam(teamRequest);
  }

  @DeleteMapping("/team/{id}")
  public void deleteTeam(@PathVariable long id) {
    teamService.deleteTeam(id);
  }
}
