package me.synn3r.jipsa.core.mvc.calendar.domain;

import java.time.LocalDate;
import me.synn3r.jipsa.core.batch.enumeration.HouseType;
import me.synn3r.jipsa.core.batch.enumeration.LocationType;

public record CalendarResponse(long id, String title, LocalDate start,
                               LocalDate end, HouseType houseType,
                               LocationType locationType) {

}
