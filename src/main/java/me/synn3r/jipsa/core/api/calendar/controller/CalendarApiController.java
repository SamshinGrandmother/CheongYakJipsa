package me.synn3r.jipsa.core.api.calendar.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.api.calendar.domain.CalendarResponse;
import me.synn3r.jipsa.core.api.calendar.service.CalendarService;

@Tag(name = "Calendar", description = "캘린더 API")
@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarApiController {

	private final CalendarService calendarService;

	@Operation(summary = "캘린더 조회", description = "년월별 청약 일정을 조회합니다.")
	@GetMapping
	public ResponseEntity<List<CalendarResponse>> getCalendar(
		@RequestParam(required = false) Integer year,
		@RequestParam(required = false) Integer month) {
		List<CalendarResponse> calendarList = calendarService.getCalendar(year, month);
		return ResponseEntity.ok(calendarList);
	}
}
