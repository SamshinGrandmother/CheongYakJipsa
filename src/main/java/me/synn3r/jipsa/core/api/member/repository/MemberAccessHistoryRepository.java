package me.synn3r.jipsa.core.api.member.repository;

import me.synn3r.jipsa.core.api.member.entity.MemberAccessHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAccessHistoryRepository extends JpaRepository<MemberAccessHistory, Long>,
  MemberAccessHistoryRepositoryCustom {

}
