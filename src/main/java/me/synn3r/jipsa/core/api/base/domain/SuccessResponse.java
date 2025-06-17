package me.synn3r.jipsa.core.api.base.domain;

import lombok.Getter;

@Getter
public class SuccessResponse implements Response {

  private String message;

  public SuccessResponse(String message) {
    this.message = message;
  }

  public static SuccessResponse of(String message) {
    return new SuccessResponse(message);
  }
}
