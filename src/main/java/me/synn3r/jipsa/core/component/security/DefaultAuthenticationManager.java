package me.synn3r.jipsa.core.component.security;

import java.util.List;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthenticationManager extends ProviderManager {

  public DefaultAuthenticationManager(
    List<AuthenticationProvider> providers) {
    super(providers);
  }
}
