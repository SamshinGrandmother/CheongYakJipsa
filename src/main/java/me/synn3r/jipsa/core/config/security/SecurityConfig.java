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
  private final AuthenticationSuccessHandler successHandler;
  private final AuthenticationFailureHandler failureHandler;

  @Bean
  public String userNameParameter() {
    return "userId";
  }

  @Bean
  public String passwordParameter() {
    return "password";
  }

  @Bean
  public String loginUrl() {
    return "/login";
  }

  @Bean
  public String logoutUrl() {
    return "/logout";
  }

  @Bean
  public String saveMemberApi() {
    return "/members";
  }

  @Bean
  public RequestMatcher loginAntMatcher() {
    return new AntPathRequestMatcher(loginUrl(), HttpMethod.POST.name());
  }

  @Bean
  public RequestMatcher logoutMatcher() {
    return new AntPathRequestMatcher(logoutUrl(), HttpMethod.GET.name());
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
        .usernameParameter(userNameParameter())
        .passwordParameter(passwordParameter())
        .loginPage(loginUrl())
        .failureHandler(failureHandler)
        .successHandler(successHandler)
        .loginProcessingUrl(loginUrl())
      )
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers(loginUrl(), "/", "/bootstrap/**", "/pages/**", "/signup", "/swagger-ui/**",
          "/v3/api-docs/**")
        .permitAll()
        .requestMatchers(HttpMethod.POST, saveMemberApi()).permitAll()
        .requestMatchers("/**")
        .authenticated())
      .logout(customize -> customize
        .logoutRequestMatcher(logoutMatcher())
        .logoutSuccessUrl(loginUrl()))
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
