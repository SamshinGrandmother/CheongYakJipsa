package me.synn3r.jipsa.core.config.security;

import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.component.security.DefaultSecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.GenericFilterBean;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final String USERNAME_PARAMETER_NAME = "email";
  private final String PASSWORD_PARAMETER_NAME = "password";
  private final DefaultSecurityContextRepository securityContextRepository;
  private final AuthenticationSuccessHandler successHandler;
  private final AuthenticationFailureHandler failureHandler;
  private final AuthenticationManager authenticationManager;
  private final String LOGIN_METHOD = HttpMethod.POST.name();
  private final String LOGIN_URL = "/login";


  @Bean
  public GenericFilterBean loginFilter() {
    UsernamePasswordAuthenticationFilter loginFilter = new UsernamePasswordAuthenticationFilter();
    loginFilter.setAuthenticationSuccessHandler(successHandler);
    loginFilter.setAuthenticationFailureHandler(failureHandler);
    loginFilter.setSecurityContextRepository(securityContextRepository);
    loginFilter.setAuthenticationManager(authenticationManager);
    loginFilter.setUsernameParameter(USERNAME_PARAMETER_NAME);
    loginFilter.setPasswordParameter(PASSWORD_PARAMETER_NAME);
    return loginFilter;
  }

  @Bean(name = "loginAntMatcher")
  public RequestMatcher loginAntMatcher() {
    return new AntPathRequestMatcher(LOGIN_URL, LOGIN_METHOD);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .csrf(AbstractHttpConfigurer::disable)
      .addFilterBefore(loginFilter(),
        UsernamePasswordAuthenticationFilter.class)
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers(LOGIN_URL)
        .permitAll()
        .requestMatchers("/**")
        .authenticated())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .securityContext(
        configurer -> configurer.
          securityContextRepository(securityContextRepository))
      .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin));

    return http.build();
  }

  @Bean
  public CorsFilter corsFilter() {
    return new CorsFilter(corsConfigurationSource());
  }

  @Bean
  public UrlBasedCorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowCredentials(true);
    config.addAllowedOriginPattern("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");

    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
