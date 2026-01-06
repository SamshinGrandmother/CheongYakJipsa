package me.synn3r.jipsa.core.api.calendar.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.api.calendar.domain.CalendarResponse;
import me.synn3r.jipsa.core.api.calendar.service.CalendarService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

  private final CalendarService calendarService;

  @GetMapping
  public List<CalendarResponse> calendar(
    @RequestParam(name = "year", required = false) Integer year,
    @RequestParam(name = "month", required = false) Integer month) {
    return calendarService.getCalendar(year, month);
  }
}
