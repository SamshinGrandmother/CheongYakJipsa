# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

청약집사 (CheongyakJipsa) - 부동산 청약 일정 자동 알림 서비스. 북마크한 부동산의 청약 일정이 등록되면 이메일/문자로 알림.

## Build Commands

```bash
# 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 단일 테스트 실행
./gradlew test --tests "me.synn3r.jipsa.core.api.member.service.MemberServiceImplTest"

# Clean
./gradlew clean
```

## Tech Stack

- **Java 21**, **Spring Boot 3.3.0**
- **Spring Security** - JWT 기반 인증 (STATELESS)
- **Spring Data JPA** + **QueryDSL 5.1.0** - 타입안전 동적 쿼리
- **MapStruct 1.5.5** - DTO ↔ Entity 매핑
- **Spring Batch** - 청약 일정 수집 (외부 API → DB)
- **Spring Cloud OpenFeign** - 외부 API 호출
- **MariaDB**, **Redis** (RefreshToken 저장)
- **Jasypt** - 민감정보 암호화

## Architecture

```
src/main/java/me/synn3r/jipsa/core/
├── api/           # REST API (controller, service, domain, repository, mapper)
│   ├── auth/      # JWT 로그인/로그아웃/토큰갱신
│   ├── member/    # 회원 CRUD
│   ├── calendar/  # 청약 일정 조회
│   ├── email/     # 이메일 인증
│   └── base/      # 공통 (Response, Exception Advice, BaseEntity)
├── batch/         # Spring Batch (청약 일정 수집)
├── component/     # 보안 컴포넌트
│   └── security/
│       ├── jwt/           # JwtTokenProvider, JwtAuthenticationFilter
│       ├── userdetails/   # DefaultUserDetails, DefaultUserDetailsService
│       └── logging/       # 인증 이벤트 로깅
└── config/        # 설정 (Security, JPA, QueryDSL, Redis, Feign 등)
```

## Key Patterns

### JWT 인증 흐름
1. `POST /api/auth/login` → Access Token (30분) + Refresh Token (14일, Redis 저장)
2. `JwtAuthenticationFilter`가 모든 요청에서 토큰 검증
3. 프로필 수정 시 `ProfileVerificationFilter`로 비밀번호 재검증 필요

### 응답 구조
```java
SuccessResponse<T> { resultType: SUCCESS, data: T }
FailResponse { resultType: FAIL, errorMessage: String }
```

### Validation Groups
- `Insert.class` - 회원가입
- `Update.class` - 정보 수정
- `UpdatePassword.class` - 비밀번호 변경

### QueryDSL & MapStruct
- Generated 소스: `build/generated/sources/annotationProcessor/java/main`
- QueryDSL Custom Repository: `*RepositoryCustom` 인터페이스 + `*RepositoryCustomImpl` 구현
- MapStruct: Spring Bean으로 생성 (생성자 주입), `@Mapper(componentModel = "spring")`

## Environment Variables

```
JASYPT_KEY      # Jasypt 암호화 키
JWT_SECRET      # JWT 서명 키
REDIS_HOST      # Redis 호스트 (default: localhost)
REDIS_PORT      # Redis 포트 (default: 6379)
```

## Public Endpoints (인증 불필요)

- `POST /api/auth/login`, `/api/auth/refresh`
- `POST /members` (회원가입)
- `POST /verify/email`, `/check/email/code`
