package me.synn3r.jipsa.core.api.member.domain;

import lombok.Getter;
import lombok.Setter;
import me.synn3r.jipsa.core.api.commons.domain.SearchCondition;
import me.synn3r.jipsa.core.global.component.security.enums.Role;

@Getter
@Setter
public class MemberSearchCondition implements SearchCondition {

	private String name;
	private String email;
	private Role role;

}
