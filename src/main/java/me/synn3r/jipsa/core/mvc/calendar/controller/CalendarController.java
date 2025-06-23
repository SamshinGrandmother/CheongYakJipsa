package me.synn3r.jipsa.core.mvc.calendar.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.mvc.calendar.domain.CalendarResponse;
import me.synn3r.jipsa.core.mvc.calendar.service.CalendarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

  private final CalendarService calendarService;

  @GetMapping
  @PostMapping
  public String calendar(
    @RequestParam(name = "year", required = false) Integer year,
    @RequestParam(name = "month", required = false) Integer month, Model model) {
    List<CalendarResponse> calendarList = calendarService.getCalendar(year, month);
    model.addAttribute("month", month);
    model.addAttribute("year", year);
    model.addAttribute("calendarList", calendarList);
    return "pages/Calendar";
  }
}
