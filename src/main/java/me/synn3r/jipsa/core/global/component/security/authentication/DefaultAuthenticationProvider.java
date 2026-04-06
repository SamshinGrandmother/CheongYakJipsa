package me.synn3r.jipsa.core.global.component.security.authentication;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationProvider extends DaoAuthenticationProvider {

	public DefaultAuthenticationProvider(PasswordEncoder passwordEncoder,
		UserDetailsService userDetailsService) {
		super(passwordEncoder);
		setUserDetailsService(userDetailsService);
	}
}
