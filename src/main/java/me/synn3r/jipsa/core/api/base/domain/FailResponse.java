package me.synn3r.jipsa.core.api.base.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FailResponse implements Response {

  private String code;
  private String message;


  public static FailResponse of(String code, String message) {
    return new FailResponse(code, message);
  }
}
