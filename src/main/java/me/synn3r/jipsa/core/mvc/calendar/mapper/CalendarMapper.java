package me.synn3r.jipsa.core.mvc.calendar.mapper;

import java.util.List;
import me.synn3r.jipsa.core.batch.entity.House;
import me.synn3r.jipsa.core.mvc.calendar.domain.CalendarResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface CalendarMapper {

  List<CalendarResponse> convertToCalendar(List<House> houseList);

  @Mappings({
    @Mapping(target = "")
  })
  CalendarResponse convertToCalendarResponse(House house);


}
