package me.synn3r.jipsa.core.global.component.security.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
	NORMAL,
	ADMIN;

	@Override
	public String getAuthority() {
		return name();
	}
}
