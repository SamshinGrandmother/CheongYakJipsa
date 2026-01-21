package me.synn3r.jipsa.core.api.calendar.mapper;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import me.synn3r.jipsa.core.api.calendar.domain.CalendarResponse;
import me.synn3r.jipsa.core.batch.entity.House;

@Mapper
public interface CalendarMapper {

	@IterableMapping(qualifiedByName = "convertToCalendarResponse")
	List<CalendarResponse> convertToCalendar(List<House> houseList);

	@Named(value = "convertToCalendarResponse")
	@Mappings({
		@Mapping(source = "applyDateStart", target = "start"),
		@Mapping(source = "applyDateEnd", target = "end"),
		@Mapping(source = "name", target = "title"),
	})
	CalendarResponse convertToCalendarResponse(House house);

}
