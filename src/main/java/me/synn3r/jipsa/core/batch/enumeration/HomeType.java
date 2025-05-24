package me.synn3r.jipsa.core.batch.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HomeType {
  SPECIALAPT("특별공급APT", "01"),
  FIRSTAPT("1순위APT", "02"),
  SECONDAPT("2순위APT", "03"),
  NORANKAPT("무순위APT", "04"),
  CANCELAPT("취소후재공급", "07"),
  SPECIALPRIVAPT("특별공급 민간사전청약 APT", "08"),
  FIRSTPRIVAPT("1순위APT 민간 사전청약 APT", "09"),
  SECONDPRIVAPT("2순위APT 민간사전청약 APT", "10"),
  OFFICETEL("오피스텔/도시형생활주택/민간임대", "05"),
  PUBLIC("공공지원민간임대", "04"),
  NONE("미지정", "00");

  private final String homeTypeName;
  private final String homeTypeCode;

  public static HomeType findByHomeTypeCode(String homeTypeCode) {
    for (HomeType homeType : HomeType.values()) {
      if (homeType.getHomeTypeCode().equals(homeTypeCode)) {
        return homeType;
      }
    }
    return NONE;
  }
}
