package me.synn3r.jipsa.core.global.component.security.logging;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationSuccessLogger {

	void saveAuthenticationSuccessHistory(UserDetails userDetails);

}
