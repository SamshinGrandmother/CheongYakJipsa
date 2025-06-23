package me.synn3r.jipsa.core.mvc.calendar.service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.batch.entity.House;
import me.synn3r.jipsa.core.batch.repository.HouseRepository;
import me.synn3r.jipsa.core.mvc.calendar.domain.CalendarResponse;
import me.synn3r.jipsa.core.mvc.calendar.mapper.CalendarMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

  private final HouseRepository houseRepository;
  private final CalendarMapper calendarMapper;


  @Override
  public List<CalendarResponse> getCalendar(Integer year, Integer month) {
    LocalDate today = LocalDate.now();
    if (year != null && month != null) {
      today = LocalDate.of(year, month, 1);
    }

    LocalDate beginDate = LocalDate.of(today.getYear(), today.getMonthValue(), 1);
    LocalDate endDate = LocalDate.of(today.getYear(), today.getMonthValue(),
      today.getMonth().equals(Month.FEBRUARY) ? today.getMonth().maxLength() - 1
        : today.getMonth().maxLength());

    List<House> houseList = houseRepository.findByApplyDateStartAfterAndApplyDateEndBefore(
      beginDate, endDate);

    return calendarMapper.convertToCalendar(houseList);
  }
}
