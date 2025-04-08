package me.synn3r.jipsa.core.api.member.domain;

import lombok.Getter;
import lombok.Setter;
import me.synn3r.jipsa.core.api.base.domain.SearchCondition;
import me.synn3r.jipsa.core.component.security.Role;

@Getter
@Setter
public class MemberSearchCondition implements SearchCondition {

  private String name;
  private String email;
  private Role role;

}
