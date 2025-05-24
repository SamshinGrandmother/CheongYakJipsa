package me.synn3r.jipsa.core.batch.domain;

import lombok.Getter;

@Getter
public class HomeRequest {

  private int page = 1;
  private final int perPage;
  private final String serviceKey;

  public HomeRequest(int perPage, String serviceKey) {
    this.perPage = perPage;
    this.serviceKey = serviceKey;
  }

  public void nextPage() {
    page++;
  }

}
