package me.synn3r.jipsa.core.batch.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import me.synn3r.jipsa.core.batch.domain.HomeScheduleResponse;
import me.synn3r.jipsa.core.batch.enumeration.HomeType;
import me.synn3r.jipsa.core.batch.enumeration.LocationType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper
public interface HomeMapper {

  @Mappings({
    @Mapping(source = "HOUSE_MANAGE_NO", target = "manageNumber"),
    @Mapping(source = "HOUSE_NM", target = "name"),
    @Mapping(source = "HOUSE_SECD", target = "homeType", qualifiedByName = "classifyHomeType"),
    @Mapping(source = "SUBSCRPT_AREA_CODE_NM", target = "locationType", qualifiedByName = "classifyLocationType"),
    @Mapping(source = "HSSPLY_ADRES", target = "address"),
    @Mapping(source = "PBLANC_URL", target = "url"),
    @Mapping(source = "RCEPT_BGNDE", target = "applyDateStart", qualifiedByName = "stringToDate"),
    @Mapping(source = "RCEPT_ENDDE", target = "applyDateEnd", qualifiedByName = "stringToDate")
  })
  Home toEntity(HomeScheduleResponse homeResponse);

  @Named("stringToDate")
  public static LocalDate stringToDate(String date) {
    return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  @Named("classifyHomeType")
  public static HomeType classifyHomeType(String homeTypeCode) {
    return HomeType.findByHomeTypeCode(homeTypeCode);
  }

  @Named("classifyLocationType")
  public static LocationType classifyLocationType(String locationTypeCode) {
    return LocationType.getLocationType(locationTypeCode);
  }
}
