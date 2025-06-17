package me.synn3r.jipsa.core.mvc.calendar.controller;

import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.mvc.calendar.service.CalendarService;
import org.springframework.stereotype.Controller;
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
    @RequestParam(name = "year") int year,
    @RequestParam(name = "month") int month) {
    return "pages/Calendar";
  }
}
