package me.synn3r.jipsa.core.api.team.repository;

import java.util.Optional;
import me.synn3r.jipsa.core.api.base.enumeration.DeleteType;
import me.synn3r.jipsa.core.api.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {

  Optional<Team> findTeamByDeleteTypeAndId(DeleteType deleteType, long id);
}
