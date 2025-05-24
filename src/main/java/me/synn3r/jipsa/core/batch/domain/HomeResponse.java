package me.synn3r.jipsa.core.batch.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class HomeResponse {

  private int currentCount;
  private int matchCount;
  private int page;
  private int perPage;
  private int totalCount;
  private final List<HomeScheduleResponse> data = new ArrayList<>();
}
