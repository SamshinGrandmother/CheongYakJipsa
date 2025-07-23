# 🏠청약집사

[~~야레야레~~](https://www.youtube.com/watch?v=FoO7Pmx0bE4)

## 개요 🐕

둥지를 갖고 싶지만 바쁜 당신을 위해 당신이 원하는 청약 일정을 자동으로 알려드립니다.

북마크한 부동산에 대한 청약 일정이 새로 등록되면 문자나 메일로 알림을 보내는 서비스입니다.

## Skills 🔨

### Backend 💯

- ☕ JDK 21
- 🍂 Spring Boot 3.3.0
- 🍂 Spring Security 3.3.0
- 🍂 Spring Cloud Open Feign 4.0.3
- 🍂 Spring Data JPA 3.3.0
- ⌨️ QueryDSL 5.1.0
- 💾 MariaDB
- 🧪 Junit 5.10
- 🌶️ Lombok 1.18.32

### Frontend 😀

- 🕘 Thymeleaf
- 🎨 Material Design

## RoadMap

### 1단계 목표 : 기본 기능 완성 및 안정화

- **Must To Have 기능 구현**
    - 공공 API 청약 정보 수집 기능 완성
    - 사용자 북마크 및 알림 기능 구현
    - 관리자 API 호출 주기 관리 기능
    - 수동 API 호출 기능
- **테스트 및 문서화**
    - 단위 테스트 및 통합 테스트 작성
    - API 문서화 (Swagger/OpenAPI)
    - 기본 배포 스크립트 작성

- **배포 자동화**
    - 운영서버에 배포
    - github action 을 통해 새로운 버전 릴리즈 시 운영서버로 자동 배포
    - docker 로 빌드하여 컨테이너 기반의 격리된 환경에서 운영

### 2단계 목표 : MVC에서 REST API 분리

- **백엔드 REST API 개발**
    - 기존 MVC Controller를 REST API로 전환
    - ResponseEntity를 활용한 표준 HTTP 응답 구현
    - 에러 처리 및 상태 코드 표준화
- **프론트엔드 분리 (React or Vue)**
    - React 기반 사용자 인터페이스 구축
    - 관리자 대시보드 개발
    - API 연동 및 상태 관리

