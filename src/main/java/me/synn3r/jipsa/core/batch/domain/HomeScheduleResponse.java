package me.synn3r.jipsa.core.batch.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.UpperSnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(value = UpperSnakeCaseStrategy.class)
public class HomeScheduleResponse {

  @Schema(name = "주택관리번호")
  private String HOUSE_MANAGE_NO;
  @Schema(name = "공고번호")
  private String PBLANC_NO;
  @Schema(name = "주택명")
  private String HOUSE_NM;
  @Schema(name = "주택구분코드(01: APT, 09: 민간사전청약, 10: 신혼희망타운)")
  private String HOUSE_SECD;
  @Schema(name = "주택구분코드명")
  private String HOUSE_SECD_NM;
  @Schema(name = "주택상세구분코드(01: 민영, 03: 국민)")
  private String HOUSE_DTL_SECD;
  @Schema(name = "주택상세구분코드명")
  private String HOUSE_DTL_SECD_NM;
  @Schema(name = "분양구분코드(0: 분양주택, 1: 분양전환 가능임대)")
  private String RENT_SECD;
  @Schema(name = "분양구분코드명")
  private String RENT_SECD_NM;
  @Schema(name = "공급지역코드")
  private String SUBSCRPT_AREA_CODE;
  @Schema(name = "공급지역명")
  private String SUBSCRPT_AREA_CODE_NM;
  @Schema(name = "공급위치 우편번호")
  private String HSSPLY_ZIP;
  @Schema(name = "공급위치")
  private String HSSPLY_ADRES;
  @Schema(name = "공급규모")
  private String TOT_SUPLY_HSHLDCO;
  @Schema(name = "모집공고일 (YYYY-MM-DD)")
  private String RCRIT_PBLANC_DE;
  @Schema(name = "청약접수시작일 (YYYY-MM-DD)")
  private String RCEPT_BGNDE;
  @Schema(name = "청약접수종료일 (YYYY-MM-DD)")
  private String RCEPT_ENDDE;
  @Schema(name = "특별공급 접수시작일 (YYYY-MM-DD)")
  private String SPSPLY_RCEPT_BGNDE;
  @Schema(name = "특별공급 접수종료일 (YYYY-MM-DD)")
  private String SPSPLY_RCEPT_ENDDE;
  @Schema(name = "1순위 해당지역 접수시작일 (YYYY-MM-DD)")
  private String GNRL_RNK1_CRSPAREA_RCPTDE;
  @Schema(name = "1순위 해당지역 접수종료일 (YYYY-MM-DD)")
  private String GNRL_RNK1_CRSPAREA_ENDDE;
  @Schema(name = "1순위 경기지역 접수시작일 (YYYY-MM-DD)")
  private String GNRL_RNK1_ETC_GG_RCPTDE;
  @Schema(name = "1순위 경기지역 접수종료일 (YYYY-MM-DD)")
  private String GNRL_RNK1_ETC_GG_ENDDE;
  @Schema(name = "1순위 기타지역 접수시작일 (YYYY-MM-DD)")
  private String GNRL_RNK1_ETC_AREA_RCPTDE;
  @Schema(name = "1순위 기타지역 접수종료일 (YYYY-MM-DD)")
  private String GNRL_RNK1_ETC_AREA_ENDDE;
  @Schema(name = "2순위 해당지역 접수시작일 (YYYY-MM-DD)")
  private String GNRL_RNK2_CRSPAREA_RCPTDE;
  @Schema(name = "2순위 해당지역 접수종료일 (YYYY-MM-DD)")
  private String GNRL_RNK2_CRSPAREA_ENDDE;
  @Schema(name = "2순위 경기지역 접수시작일 (YYYY-MM-DD)")
  private String GNRL_RNK2_ETC_GG_RCPTDE;
  @Schema(name = "2순위 경기지역 접수종료일 (YYYY-MM-DD)")
  private String GNRL_RNK2_ETC_GG_ENDDE;
  @Schema(name = "2순위 기타지역 접수시작일 (YYYY-MM-DD)")
  private String GNRL_RNK2_ETC_AREA_RCPTDE;
  @Schema(name = "2순위 기타지역 접수종료일 (YYYY-MM-DD)")
  private String GNRL_RNK2_ETC_AREA_ENDDE;
  @Schema(name = "당첨자발표일 (YYYY-MM-DD)")
  private String PRZWNER_PRESNATN_DE;
  @Schema(name = "계약시작일 (YYYY-MM-DD)")
  private String CNTRCT_CNCLS_BGNDE;
  @Schema(name = "계약종료일 (YYYY-MM-DD)")
  private String CNTRCT_CNCLS_ENDDE;
  @Schema(name = "홈페이지주소")
  private String HMPG_ADRES;
  @Schema(name = "건설업체명(시공사)")
  private String CNSTRCT_ENTRPS_NM;
  @Schema(name = "문의처")
  private String MDHS_TELNO;
  @Schema(name = "사업주체명(시행사)")
  private String BSNS_MBY_NM;
  @Schema(name = "입주예정월")
  private String MVN_PREARNGE_YM;
  @Schema(name = "투기과열지구")
  private String SPECLT_RDN_EARTH_AT;
  @Schema(name = "조정대상지역 (Y: 과열지역, N: 미대상주택)")
  private String MDAT_TRGET_AREA_SECD;
  @Schema(name = "분양가상한제")
  private String PARCPRC_ULS_AT;
  @Schema(name = "정비사업")
  private String IMPRMN_BSNS_AT;
  @Schema(name = "공공주택지구")
  private String PUBLIC_HOUSE_EARTH_AT;
  @Schema(name = "대규모 택지개발지구")
  private String LRSCL_BLDLND_AT;
  @Schema(name = "수도권 내 민영 공공주택지구")
  private String NPLN_PRVOPR_PUBLIC_HOUSE_AT;
  @Schema(name = "공공주택 특별법 적용 여부")
  private String PUBLIC_HOUSE_SPCLW_APPLC_AT;
  @Schema(name = "분양정보 URL")
  private String PBLANC_URL;
}
