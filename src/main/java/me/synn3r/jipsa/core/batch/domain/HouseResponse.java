package me.synn3r.jipsa.core.batch.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class HouseResponse {

  private int currentCount;
  private int matchCount;
  private int page;
  private int perPage;
  private int totalCount;
  private final List<HouseScheduleResponse> data = new ArrayList<>();
}
