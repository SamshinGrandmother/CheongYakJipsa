package me.synn3r.jipsa.core.api.calendar.service;

import java.util.List;

import me.synn3r.jipsa.core.api.calendar.domain.CalendarResponse;

public interface CalendarService {

	List<CalendarResponse> getCalendar(Integer year, Integer month);
}
