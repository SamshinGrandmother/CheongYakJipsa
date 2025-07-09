package me.synn3r.jipsa.core.mvc.calendar.service;

import java.util.List;
import me.synn3r.jipsa.core.mvc.calendar.domain.CalendarResponse;

public interface CalendarService {

  List<CalendarResponse> getCalendar(Integer year, Integer month);
}
