package me.synn3r.jipsa.core.global.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import me.synn3r.jipsa.core.global.config.SwaggerConfig;

/**
 * 개발/QA 환경 전용 Spring Security 설정 클래스.
 *
 * <p>이 설정은 <strong>dev, qa 프로파일에서만 활성화</strong>되며,
 * 프론트엔드 개발자와의 협업 및 API 테스트를 원활하게 하기 위해
 * 특정 개발용 엔드포인트들의 인증을 우회합니다.</p>
 *
 * <h2>허용되는 엔드포인트</h2>
 * <ul>
 *   <li><code>/swagger-ui/**</code> - Swagger UI 화면</li>
 *   <li><code>/v3/api-docs/**</code> - OpenAPI 문서 (JSON/YAML)</li>
 *   <li><code>/api/dev/security/**</code> - 개발용 인증 API (임시 토큰 발급 등)</li>
 * </ul>
 *
 * <h2>활성화 방법</h2>
 * <p>애플리케이션 실행 시 <code>dev</code> 또는 <code>qa</code> 프로파일을 활성화합니다:</p>
 * <pre>
 * # application.yaml
 * spring:
 *   profiles:
 *     active: dev  # 또는 qa
 *
 * # 또는 JVM 옵션
 * -Dspring.profiles.active=dev  # 또는 qa
 * </pre>
 *
 * <h2>보안 주의사항</h2>
 * <p><strong>이 설정은 절대로 운영 환경(prod)에서 활성화되어서는 안 됩니다.</strong></p>
 * <ul>
 *   <li>Swagger UI를 통해 API 구조가 외부에 노출될 수 있습니다.</li>
 *   <li>개발용 토큰 발급 API를 통해 인증 우회가 가능합니다.</li>
 *   <li>운영 환경에서는 반드시 {@link SecurityConfig}만 활성화되어야 합니다.</li>
 * </ul>
 *
 * <h2>SecurityFilterChain 우선순위</h2>
 * <p>{@code @Order(1)}로 설정되어 {@link SecurityConfig}의 기본 필터 체인보다
 * <strong>먼저 매칭</strong>됩니다. 지정된 패턴에 해당하는 요청만 이 필터 체인에서 처리되고,
 * 그 외의 요청은 {@link SecurityConfig#securityFilterChain}에서 처리됩니다.</p>
 *
 * @author synn3r
 * @see SecurityConfig 운영 환경을 포함한 기본 보안 설정
 * @see "DevSecurityController - 개발용 토큰 발급 API 컨트롤러 (api.security.controller 패키지)"
 * @see SwaggerConfig Swagger/OpenAPI 문서 설정
 * @since 1.0
 */
@Profile({"dev", "qa"})
@Configuration
public class DevSecurityConfig {

	/**
	 * Swagger UI 접근을 위한 URL 패턴.
	 *
	 * <p>Swagger UI는 API 문서를 시각적으로 표현하고,
	 * 브라우저에서 직접 API를 테스트할 수 있는 인터페이스를 제공합니다.</p>
	 *
	 * @see SwaggerConfig Swagger 설정 클래스
	 */
	private static final String SWAGGER_UI_PATTERN = "/swagger-ui/**";

	/**
	 * OpenAPI 3.0 문서 접근을 위한 URL 패턴.
	 *
	 * <p>이 엔드포인트는 API 명세를 JSON 또는 YAML 형식으로 제공하며,
	 * Swagger UI가 이 문서를 기반으로 화면을 렌더링합니다.</p>
	 *
	 * <p>주요 엔드포인트:</p>
	 * <ul>
	 *   <li><code>/v3/api-docs</code> - JSON 형식의 API 문서</li>
	 *   <li><code>/v3/api-docs.yaml</code> - YAML 형식의 API 문서</li>
	 * </ul>
	 */
	private static final String API_DOCS_PATTERN = "/v3/api-docs/**";

	/**
	 * 개발용 인증 API 접근을 위한 URL 패턴.
	 *
	 * <p>이 패턴에 해당하는 API는 실제 로그인 없이 테스트용 JWT 토큰을 발급받을 수 있어,
	 * 프론트엔드 개발자가 인증이 필요한 API를 쉽게 테스트할 수 있습니다.</p>
	 */
	private static final String DEV_SECURITY_PATTERN = "/api/dev/security/**";

	/**
	 * 개발 환경 전용 {@link SecurityFilterChain}을 생성합니다.
	 *
	 * <p>이 필터 체인은 다음과 같은 특징을 가집니다:</p>
	 * <ul>
	 *   <li><strong>securityMatcher</strong>: 지정된 패턴의 요청만 이 필터 체인에서 처리</li>
	 *   <li><strong>CSRF 비활성화</strong>: REST API이므로 CSRF 보호 불필요</li>
	 *   <li><strong>STATELESS 세션</strong>: JWT 기반 인증으로 세션 미사용</li>
	 *   <li><strong>모든 요청 허용</strong>: 매칭된 패턴의 모든 요청에 대해 인증 우회</li>
	 * </ul>
	 *
	 * @param http Spring Security의 {@link HttpSecurity} 빌더
	 * @return 구성된 {@link SecurityFilterChain} 인스턴스
	 * @throws Exception 보안 설정 중 오류 발생 시
	 */
	@Bean
	@Order(1)
	public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher(SWAGGER_UI_PATTERN, API_DOCS_PATTERN, DEV_SECURITY_PATTERN)
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(authorize -> authorize
				.anyRequest().permitAll());

		return http.build();
	}
}
