package me.synn3r.jipsa.core.batch.repository;

import java.time.LocalDate;
import java.util.List;
import me.synn3r.jipsa.core.batch.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {

  boolean existsByManageNumber(long manageNumber);

  List<House> findByApplyDateStartAfterAndApplyDateEndBefore(LocalDate applyDateStart,
    LocalDate applyDateEnd);
}
