package me.synn3r.jipsa.core.api.base.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FailResponse implements Response {

  private String message;


  public static FailResponse of(String message) {
    return new FailResponse(message);
  }
}
