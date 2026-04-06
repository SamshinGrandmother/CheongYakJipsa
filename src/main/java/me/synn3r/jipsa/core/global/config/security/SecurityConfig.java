package me.synn3r.jipsa.core.global.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.global.component.security.filter.JwtAuthenticationFilter;
import me.synn3r.jipsa.core.global.component.security.filter.LoginAuthenticationFilter;
import me.synn3r.jipsa.core.global.component.security.filter.ProfileVerificationFilter;
import me.synn3r.jipsa.core.global.component.security.handler.DefaultAuthenticationFailureHandler;
import me.synn3r.jipsa.core.global.component.security.handler.DefaultAuthenticationSuccessHandler;
import me.synn3r.jipsa.core.global.component.security.handler.JwtAccessDeniedHandler;
import me.synn3r.jipsa.core.global.component.security.handler.JwtAuthenticationEntryPoint;

/**
 * 청약집사 애플리케이션의 핵심 Spring Security 설정 클래스.
 *
 * <p>이 설정은 <strong>모든 프로파일(dev, qa, prod)에서 활성화</strong>되며,
 * JWT 기반의 STATELESS 인증 체계를 구성합니다. REST API 보안을 위한
 * 핵심 필터 체인과 CORS 정책을 정의합니다.</p>
 *
 * <h2>보안 아키텍처</h2>
 * <pre>
 * 요청 → CorsFilter → LoginAuthenticationFilter → JwtAuthenticationFilter
 *                                                        ↓
 *                                              ProfileVerificationFilter → Controller
 * </pre>
 *
 * <h2>필터 체인 순서</h2>
 * <ol>
 *   <li>{@link LoginAuthenticationFilter} - {@code /api/auth/login} 요청 처리, JWT 토큰 발급</li>
 *   <li>{@link JwtAuthenticationFilter} - 모든 요청의 JWT 토큰 검증 및 인증 정보 설정</li>
 *   <li>{@link ProfileVerificationFilter} - 프로필 수정 시 비밀번호 재검증</li>
 * </ol>
 *
 * <h2>인증이 필요 없는 엔드포인트 (Public Endpoints)</h2>
 * <ul>
 *   <li>{@code POST /api/auth/login} - 로그인</li>
 *   <li>{@code POST /members} - 회원가입</li>
 *   <li>{@code POST /verify/email} - 이메일 인증 요청</li>
 *   <li>{@code POST /check/email/code} - 이메일 인증 코드 확인</li>
 * </ul>
 *
 * <h2>CORS 정책</h2>
 * <p>모든 Origin, Header, Method를 허용하며, {@code Authorization} 헤더를 노출합니다.
 * 이는 프론트엔드 애플리케이션에서 JWT 토큰을 응답 헤더로부터 읽을 수 있도록 하기 위함입니다.</p>
 *
 * <h2>예외 처리</h2>
 * <ul>
 *   <li>{@link JwtAuthenticationEntryPoint} - 인증되지 않은 요청 (401 Unauthorized)</li>
 *   <li>{@link JwtAccessDeniedHandler} - 권한이 없는 요청 (403 Forbidden)</li>
 * </ul>
 *
 * <h2>세션 정책</h2>
 * <p>{@link SessionCreationPolicy#STATELESS}로 설정되어 서버 측에 세션을 생성하지 않습니다.
 * 모든 인증 상태는 클라이언트가 전송하는 JWT 토큰으로 관리됩니다.</p>
 *
 * @author synn3r
 * @see DevSecurityConfig 개발/QA 환경 전용 보안 설정 (Swagger UI, 개발용 토큰 발급)
 * @see JwtAuthenticationFilter JWT 토큰 검증 필터
 * @see LoginAuthenticationFilter 로그인 인증 필터
 * @see ProfileVerificationFilter 프로필 수정 시 비밀번호 재검증 필터
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

	/**
	 * 로그인 API 엔드포인트 URL.
	 *
	 * <p>이 엔드포인트는 {@link LoginAuthenticationFilter}에서 처리되며,
	 * 사용자 인증 성공 시 JWT Access Token과 Refresh Token을 발급합니다.</p>
	 *
	 * <p>요청 형식:</p>
	 * <pre>
	 * POST /api/auth/login
	 * Content-Type: application/json
	 *
	 * {
	 *   "email": "user@example.com",
	 *   "password": "password123"
	 * }
	 * </pre>
	 *
	 * @see LoginAuthenticationFilter 로그인 요청을 처리하는 필터
	 * @see DefaultAuthenticationSuccessHandler 인증 성공 시 토큰 발급 핸들러
	 */
	private static final String LOGIN_URL = "/api/auth/login";

	/**
	 * 회원 관련 API 엔드포인트 URL.
	 *
	 * <p>{@code POST} 메소드로 요청 시 회원가입을 처리하며,
	 * 인증 없이 접근 가능합니다. 다른 HTTP 메소드(GET, PUT, DELETE 등)는
	 * 인증이 필요합니다.</p>
	 *
	 * @see me.synn3r.jipsa.core.api.member.controller.MemberController 회원 컨트롤러
	 */
	private static final String MEMBERS_URL = "/members";

	/**
	 * 이메일 인증 요청 API 엔드포인트 URL.
	 *
	 * <p>회원가입 또는 비밀번호 찾기 시 이메일로 인증 코드를 발송합니다.
	 * 인증 없이 접근 가능합니다.</p>
	 *
	 * @see me.synn3r.jipsa.core.api.email.controller.EmailController 이메일 컨트롤러
	 */
	private static final String VERIFY_EMAIL_URL = "/verify/email";

	/**
	 * 이메일 인증 코드 확인 API 엔드포인트 URL.
	 *
	 * <p>사용자가 입력한 인증 코드가 서버에서 발송한 코드와 일치하는지 검증합니다.
	 * 인증 없이 접근 가능합니다.</p>
	 *
	 * @see me.synn3r.jipsa.core.api.email.controller.EmailController 이메일 컨트롤러
	 */
	private static final String CHECK_EMAIL_CODE_URL = "/check/email/code";

	/**
	 * 회원가입 API 엔드포인트 URL.
	 *
	 * <p>신규 회원 등록을 처리합니다. 이메일 인증 완료 후 접근 가능합니다.
	 * 인증 없이 접근 가능합니다.</p>
	 *
	 * @see me.synn3r.jipsa.core.api.member.controller.MemberRegistrationController 회원가입 컨트롤러
	 */
	private static final String SIGN_UP_URL = "/api/sign-up";

	/**
	 * 이메일 인증 코드 발송 API 엔드포인트 URL.
	 *
	 * <p>회원가입 시 이메일로 인증 코드를 발송합니다.
	 * 인증 없이 접근 가능합니다.</p>
	 *
	 * @see me.synn3r.jipsa.core.api.member.controller.MemberRegistrationController 회원가입 컨트롤러
	 */
	private static final String EMAIL_VERIFICATION_URL = "/api/email/verification";

	/**
	 * 이메일 인증 코드 검증 API 엔드포인트 URL.
	 *
	 * <p>사용자가 입력한 인증 코드를 검증합니다.
	 * 인증 없이 접근 가능합니다.</p>
	 *
	 * @see me.synn3r.jipsa.core.api.member.controller.MemberRegistrationController 회원가입 컨트롤러
	 */
	private static final String EMAIL_VERIFICATION_VERIFY_URL = "/api/email/verification/verify";

	/**
	 * 이메일 인증 링크 처리 API 엔드포인트 URL.
	 *
	 * <p>이메일에 포함된 인증 링크를 처리합니다.
	 * 인증 없이 접근 가능합니다.</p>
	 *
	 * @see me.synn3r.jipsa.core.api.member.controller.MemberRegistrationController 회원가입 컨트롤러
	 */
	private static final String EMAIL_VERIFY_LINK_URL = "/api/email-verify";

	/**
	 * JWT 토큰이 담기는 HTTP 헤더 이름.
	 *
	 * <p>클라이언트는 이 헤더에 {@code Bearer {token}} 형식으로 토큰을 전송하고,
	 * 서버는 인증 성공 시 이 헤더로 새로운 토큰을 응답합니다.</p>
	 *
	 * <p>CORS 설정에서 이 헤더를 노출(expose)하여 프론트엔드에서
	 * 응답 헤더의 토큰 값을 읽을 수 있도록 합니다.</p>
	 *
	 * @see #corsConfigurationSource() CORS 설정에서 이 헤더를 노출
	 */
	private static final String AUTHORIZATION_HEADER = "Authorization";

	/**
	 * 모든 경로를 매칭하는 Ant 패턴.
	 *
	 * <p>CORS 설정에서 모든 경로에 동일한 정책을 적용하기 위해 사용됩니다.</p>
	 */
	private static final String ALL_PATTERN = "/**";

	/**
	 * JWT 토큰을 검증하고 인증 정보를 SecurityContext에 설정하는 필터.
	 *
	 * @see JwtAuthenticationFilter
	 */
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	/**
	 * 인증되지 않은 요청에 대한 예외를 처리하는 EntryPoint.
	 *
	 * <p>401 Unauthorized 응답을 반환합니다.</p>
	 *
	 * @see JwtAuthenticationEntryPoint
	 */
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	/**
	 * 권한이 없는 요청에 대한 예외를 처리하는 핸들러.
	 *
	 * <p>403 Forbidden 응답을 반환합니다.</p>
	 *
	 * @see JwtAccessDeniedHandler
	 */
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	/**
	 * 프로필 수정 요청 시 비밀번호 재검증을 수행하는 필터.
	 *
	 * <p>민감한 정보 변경 시 추가적인 보안 검증을 제공합니다.</p>
	 *
	 * @see ProfileVerificationFilter
	 */
	private final ProfileVerificationFilter profileVerificationFilter;

	/**
	 * 인증 성공 시 JWT 토큰을 발급하고 응답하는 핸들러.
	 *
	 * @see DefaultAuthenticationSuccessHandler
	 */
	private final DefaultAuthenticationSuccessHandler authenticationSuccessHandler;

	/**
	 * 인증 실패 시 에러 응답을 처리하는 핸들러.
	 *
	 * @see DefaultAuthenticationFailureHandler
	 */
	private final DefaultAuthenticationFailureHandler authenticationFailureHandler;

	/**
	 * JSON 직렬화/역직렬화를 위한 ObjectMapper.
	 *
	 * <p>{@link LoginAuthenticationFilter}에서 로그인 요청 본문을 파싱하는 데 사용됩니다.</p>
	 */
	private final ObjectMapper objectMapper;

	/**
	 * Spring Security의 {@link AuthenticationManager}를 Bean으로 등록합니다.
	 *
	 * <p>AuthenticationManager는 실제 인증 로직을 수행하는 핵심 컴포넌트로,
	 * {@link LoginAuthenticationFilter}에서 사용자 인증 시 사용됩니다.</p>
	 *
	 * <p>내부적으로 {@code DefaultUserDetailsService}를
	 * 통해 사용자 정보를 조회하고, 비밀번호를 검증합니다.</p>
	 *
	 * @param authenticationConfiguration Spring Security의 인증 설정
	 * @return 구성된 {@link AuthenticationManager} 인스턴스
	 * @throws Exception 인증 관리자 생성 중 오류 발생 시
	 */
	@Bean
	public AuthenticationManager authenticationManager(
		AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	/**
	 * 로그인 요청을 처리하는 {@link LoginAuthenticationFilter}를 Bean으로 등록합니다.
	 *
	 * <p>이 필터는 {@code /api/auth/login} 엔드포인트로 들어오는 POST 요청을 가로채서
	 * JSON 형식의 로그인 요청을 처리합니다.</p>
	 *
	 * <h3>필터 동작 흐름</h3>
	 * <ol>
	 *   <li>요청 본문에서 email/password를 추출</li>
	 *   <li>{@link AuthenticationManager}를 통해 인증 수행</li>
	 *   <li>성공 시 {@link DefaultAuthenticationSuccessHandler}가 JWT 토큰 발급</li>
	 *   <li>실패 시 {@link DefaultAuthenticationFailureHandler}가 에러 응답</li>
	 * </ol>
	 *
	 * @param authenticationManager 인증을 수행할 AuthenticationManager
	 * @return 구성된 {@link LoginAuthenticationFilter} 인스턴스
	 * @see LoginAuthenticationFilter JSON 기반 로그인 필터
	 * @see DefaultAuthenticationSuccessHandler 인증 성공 핸들러
	 * @see DefaultAuthenticationFailureHandler 인증 실패 핸들러
	 */
	@Bean
	public LoginAuthenticationFilter loginAuthenticationFilter(
		AuthenticationManager authenticationManager) {
		LoginAuthenticationFilter filter = new LoginAuthenticationFilter(objectMapper);
		filter.setAuthenticationManager(authenticationManager);
		filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
		filter.setAuthenticationFailureHandler(authenticationFailureHandler);
		return filter;
	}

	/**
	 * 메인 {@link SecurityFilterChain}을 구성합니다.
	 *
	 * <p>이 메소드는 애플리케이션의 핵심 보안 정책을 정의합니다:</p>
	 *
	 * <h3>보안 설정 구성</h3>
	 * <ul>
	 *   <li><strong>CSRF</strong>: 비활성화 (REST API는 CSRF 토큰 불필요)</li>
	 *   <li><strong>세션</strong>: STATELESS (JWT 기반 인증)</li>
	 *   <li><strong>CORS</strong>: {@link #corsConfigurationSource()}에서 정의한 정책 적용</li>
	 *   <li><strong>예외 처리</strong>: 커스텀 EntryPoint 및 AccessDeniedHandler 사용</li>
	 *   <li><strong>Frame Options</strong>: SAMEORIGIN (H2 Console 등 iframe 허용)</li>
	 * </ul>
	 *
	 * <h3>필터 체인 순서</h3>
	 * <pre>
	 * UsernamePasswordAuthenticationFilter 위치에 LoginAuthenticationFilter 배치
	 *     ↓
	 * JwtAuthenticationFilter (토큰 검증)
	 *     ↓
	 * ProfileVerificationFilter (프로필 수정 시 비밀번호 재검증)
	 * </pre>
	 *
	 * <h3>접근 제어 규칙</h3>
	 * <ul>
	 *   <li>{@code /api/auth/login} - 모든 사용자 허용</li>
	 *   <li>{@code POST /members} - 모든 사용자 허용 (회원가입)</li>
	 *   <li>{@code POST /verify/email, /check/email/code} - 모든 사용자 허용</li>
	 *   <li>그 외 모든 요청 - 인증 필요</li>
	 * </ul>
	 *
	 * @param http Spring Security의 {@link HttpSecurity} 빌더
	 * @param authenticationManager 인증 수행을 위한 AuthenticationManager
	 * @return 구성된 {@link SecurityFilterChain} 인스턴스
	 * @throws Exception 보안 설정 중 오류 발생 시
	 * @see DevSecurityConfig#devSecurityFilterChain(HttpSecurity) 개발/QA 환경 전용 필터 체인
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
		AuthenticationManager authenticationManager) throws Exception {

		LoginAuthenticationFilter loginFilter = loginAuthenticationFilter(authenticationManager);

		http
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler))
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(LOGIN_URL).permitAll()
				.requestMatchers(HttpMethod.POST, MEMBERS_URL).permitAll()
				.requestMatchers(HttpMethod.POST, VERIFY_EMAIL_URL, CHECK_EMAIL_CODE_URL).permitAll()
				.requestMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
				.requestMatchers(HttpMethod.POST, EMAIL_VERIFICATION_URL).permitAll()
				.requestMatchers(HttpMethod.POST, EMAIL_VERIFICATION_VERIFY_URL).permitAll()
				.requestMatchers(HttpMethod.GET, EMAIL_VERIFY_LINK_URL).permitAll()
				.anyRequest().authenticated())
			.headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
			.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(profileVerificationFilter, JwtAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * CORS 필터를 Bean으로 등록합니다.
	 *
	 * <p>이 필터는 Spring Security 필터 체인 외부에서도 CORS 정책을 적용해야 하는
	 * 경우에 사용될 수 있습니다. 내부적으로 {@link #corsConfigurationSource()}를 사용합니다.</p>
	 *
	 * @return 구성된 {@link CorsFilter} 인스턴스
	 * @see #corsConfigurationSource() CORS 설정 소스
	 */
	@Bean
	public CorsFilter corsFilter() {
		return new CorsFilter(corsConfigurationSource());
	}

	/**
	 * CORS 정책을 정의하는 {@link UrlBasedCorsConfigurationSource}를 구성합니다.
	 *
	 * <p>이 설정은 프론트엔드 애플리케이션과의 Cross-Origin 통신을 허용합니다.</p>
	 *
	 * <h3>CORS 설정 상세</h3>
	 * <ul>
	 *   <li><strong>Credentials</strong>: 허용 (쿠키, Authorization 헤더 등)</li>
	 *   <li><strong>Allowed Origins</strong>: 모든 Origin 허용 ({@code *} 패턴)</li>
	 *   <li><strong>Allowed Headers</strong>: 모든 헤더 허용</li>
	 *   <li><strong>Allowed Methods</strong>: 모든 HTTP 메소드 허용 (GET, POST, PUT, DELETE 등)</li>
	 *   <li><strong>Exposed Headers</strong>: {@code Authorization} 헤더 노출</li>
	 * </ul>
	 *
	 * <h3>Authorization 헤더 노출의 필요성</h3>
	 * <p>기본적으로 브라우저는 CORS 응답에서 특정 헤더만 JavaScript에 노출합니다.
	 * JWT 토큰이 담긴 {@code Authorization} 헤더를 프론트엔드에서 읽을 수 있도록
	 * 명시적으로 노출 설정이 필요합니다.</p>
	 *
	 * <h3>보안 고려사항</h3>
	 * <p>현재 모든 Origin을 허용하고 있습니다. 운영 환경에서는 허용할 Origin을
	 * 명시적으로 지정하는 것이 권장됩니다:</p>
	 * <pre>
	 * config.addAllowedOrigin("https://cheongyakjipsa.com");
	 * config.addAllowedOrigin("https://www.cheongyakjipsa.com");
	 * </pre>
	 *
	 * @return 구성된 {@link UrlBasedCorsConfigurationSource} 인스턴스
	 */
	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.addExposedHeader(AUTHORIZATION_HEADER);

		source.registerCorsConfiguration(ALL_PATTERN, config);
		return source;
	}
}
