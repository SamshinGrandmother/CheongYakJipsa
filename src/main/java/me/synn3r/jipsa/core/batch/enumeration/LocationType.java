package me.synn3r.jipsa.core.batch.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LocationType {
  SEOUL("서울"),
  GWANGJU("광주"),
  DAEGU("대구"),
  DAEJEON("대전"),
  BUSAN("부산"),
  SEJONG("세종"),
  ULSAN("울산"),
  INCHEON("인천"),
  GANGWON("강원"),
  GYEONGGI("경기"),
  GYEONGNAM("경남"),
  GYEONGBUK("경북"),
  JEONNAM("전남"),
  JEONBUK("전북"),
  JEJU("제주"),
  CHUNGNAM("충남"),
  CHUNGBUK("충북");

  private final String name;

  public static LocationType getLocationType(final String name) {
    for (final LocationType type : LocationType.values()) {
      if (type.getName().equals(name)) {
        return type;
      }
    }
    throw new IllegalArgumentException(name + " is not a valid location type");
  }

}
