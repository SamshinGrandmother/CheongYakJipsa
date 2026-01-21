package me.synn3r.jipsa.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.addSecurityItem(new SecurityRequirement().addList("Bearer"))
			.components(new Components().addSecuritySchemes("Bearer",
				new SecurityScheme()
					.name("Bearer")
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")
					.description("JWT Access Token을 입력하세요.")))
			.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
			.title("청약집사 Swagger")
			.description("청약집사 유저, 알림 등에 관한 REST API")
			.version("0.0.1");
	}

}
