package me.synn3r.jipsa.core.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Profile({"dev", "qa"})
@Configuration
public class SwaggerConfig {

	private static final String SECURITY_SCHEME_NAME = "Bearer";
	private static final String BEARER_TOKEN_DESCRIPTION = """
		JWT Access Token을 입력하세요.
		
		[개발 환경] Dev Auth API에서 토큰을 발급받을 수 있습니다:
		1. 'Dev Auth' 섹션의 'GET /api/dev/auth/token' API 실행
		2. 응답의 'accessToken' 값을 복사
		3. 이 창에 붙여넣기 후 'Authorize' 클릭
		""";

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
			.components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
				new SecurityScheme()
					.name(SECURITY_SCHEME_NAME)
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")
					.description(BEARER_TOKEN_DESCRIPTION)))
			.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
			.title("청약집사 API")
			.description("""
				청약집사 유저, 알림 등에 관한 REST API
				
				## 인증 방법
				1. **Dev Auth API 사용 (개발 환경)**: 'Dev Auth' 섹션에서 토큰 발급 후 Authorize 버튼 클릭
				2. **로그인 API 사용**: POST /api/auth/login 으로 로그인 후 응답 헤더의 Authorization 토큰 사용
				""")
			.version("0.0.1");
	}

}
