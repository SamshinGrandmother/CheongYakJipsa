package me.synn3r.jipsa.core.mvc.calendar.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.batch.repository.HouseRepository;
import me.synn3r.jipsa.core.mvc.calendar.domain.CalendarResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

  private final HouseRepository houseRepository;


  @Override
  public List<CalendarResponse> getCalendar(int year, int month) {
    LocalDate beginDate = LocalDate.of(year, month, 1);
    LocalDate endDate = LocalDate.of(year, month, 31);

    houseRepository.findByApplyDateStartAfterAndApplyDateEndBefore(beginDate, endDate);
  }
}
