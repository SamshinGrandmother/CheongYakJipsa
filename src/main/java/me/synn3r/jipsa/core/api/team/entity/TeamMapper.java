package me.synn3r.jipsa.core.api.team.entity;

import me.synn3r.jipsa.core.api.team.domain.TeamRequest;
import me.synn3r.jipsa.core.api.team.domain.TeamResponse;
import org.mapstruct.Mapper;

@Mapper
public interface TeamMapper {

  Team toEntity(TeamRequest teamRequest);

  TeamResponse toResponse(Team team);
}
