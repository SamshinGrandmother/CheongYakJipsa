package me.synn3r.jipsa.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
      .addSecurityItem(new SecurityRequirement().addList("SESSION"))
      .components(new Components().addSecuritySchemes("SESSION",
        new SecurityScheme()
          .name("SESSION")
          .type(SecurityScheme.Type.APIKEY)
          .in(SecurityScheme.In.HEADER)
          .name("Cookie")))
      .info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
      .title("CodeArena Swagger")
      .description("CodeArena 유저 및 인증 , ps, 알림에 관한 REST API")
      .version("1.0.0");
  }


}
