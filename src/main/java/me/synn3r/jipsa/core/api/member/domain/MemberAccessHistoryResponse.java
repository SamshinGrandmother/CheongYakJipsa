package me.synn3r.jipsa.core.api.member.domain;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import me.synn3r.jipsa.core.api.base.enumeration.ResultType;
import me.synn3r.jipsa.core.component.security.enumerations.AuthenticationFailureType;

@Getter
@Setter
public class MemberAccessHistoryResponse {

  private long id;
  private String name;
  private ResultType resultType;
  private AuthenticationFailureType failureType;
  private LocalDateTime accessAt;

}
