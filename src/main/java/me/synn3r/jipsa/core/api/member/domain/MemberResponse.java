package me.synn3r.jipsa.core.api.member.domain;

import lombok.Getter;
import lombok.Setter;
import me.synn3r.jipsa.core.api.base.domain.Response;
import me.synn3r.jipsa.core.component.security.Role;

@Getter
@Setter
public class MemberResponse implements Response {

  private long id;
  private String name;
  private Role role;
  private String email;

}
