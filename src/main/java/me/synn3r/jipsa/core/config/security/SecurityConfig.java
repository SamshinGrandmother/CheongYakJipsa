package me.synn3r.jipsa.core.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final String USERNAME_PARAMETER_NAME = "email";
  private final String PASSWORD_PARAMETER_NAME = "password";
  private final AuthenticationSuccessHandler successHandler;
  private final AuthenticationFailureHandler failureHandler;
  private final String LOGIN_METHOD = HttpMethod.POST.name();
  private final String LOGIN_URL = "/login";

  @Bean(name = "loginAntMatcher")
  public RequestMatcher loginAntMatcher() {
    return new AntPathRequestMatcher(LOGIN_URL, LOGIN_METHOD);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
                            .usernameParameter(USERNAME_PARAMETER_NAME)
                            .passwordParameter(PASSWORD_PARAMETER_NAME)
                            .loginPage(LOGIN_URL)
                            .failureHandler(failureHandler)
                            .successHandler(successHandler)
                            .loginProcessingUrl(LOGIN_URL)
                    )
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers(LOGIN_URL, "/", "/bootstrap/**", "/pages/**")
        .permitAll()
        .requestMatchers("/**")
        .authenticated())
      .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
      .rememberMe(httpSecurityRememberMeConfigurer -> httpSecurityRememberMeConfigurer
        .rememberMeParameter("rememberMe")
        .tokenValiditySeconds(3600))
      ;

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
