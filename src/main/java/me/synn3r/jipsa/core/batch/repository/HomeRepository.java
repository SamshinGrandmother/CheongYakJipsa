package me.synn3r.jipsa.core.batch.repository;

import me.synn3r.jipsa.core.batch.entity.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeRepository extends JpaRepository<Home, Long> {

  boolean existsByManageNumber(long manageNumber);
}
