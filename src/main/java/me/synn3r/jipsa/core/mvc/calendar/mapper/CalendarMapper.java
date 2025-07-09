package me.synn3r.jipsa.core.mvc.calendar.mapper;

import java.util.List;
import me.synn3r.jipsa.core.batch.entity.House;
import me.synn3r.jipsa.core.mvc.calendar.domain.CalendarResponse;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

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
