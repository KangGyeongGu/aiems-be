# (AUTH) Spring Boot Starter Template

공통 응답·예외, 보안·JWT, 회원·인증 포함 Spring Boot 스타터

</br>

## Coverage

> [Wiki](https://github.com/KangGyeongGu/spring-boot-starter-template/wiki)

- **공통 인프라**
- **보안 · JWT**
- **WebSocket(STOMP)**
- **회원 · 자사 인증**
- **Refresh Token**
- **소셜 로그인(OAuth2)**
- **테스트 체계**

</br>

## Stack
- Spring Boot 3.5.16
- Java 21
- Gradle
- PostgreSQL
- Flyway
- Spring Security
- JWT(jjwt 0.12)
- Spring Data JPA
- Spring Data Redis
- WebSocket STOMP

</br>

## Getting Started



| # | 대상 | 변경 | 방법 |
|---|---|---|---|
| 1 | 패키지 `com.aiems.be` | 새 패키지 | IDE 리팩터링 |
| 2 | 진입점 `BApplication` | 새 이름 | IDE 리팩터링 |
| 3 | `settings.gradle` 의 `rootProject.name` | 새 프로젝트명 | 수기 |
| 4 | `build.gradle` 의 `group` | 새 패키지 | 수기 |
| 5 | `ApiResponseAdvice` 의 `@RestControllerAdvice(basePackages = "com.aiems.be")` | 새 패키지 | 수기 |
| 6 | `lombok.config` 의 `com.aiems.be.config.AuthRedis` | 새 패키지 | 수기 |
| 7 | `scripts/local/docker-compose.yml` 의 `container_name` · `POSTGRES_DB` · `POSTGRES_USER` · healthcheck | 새 프로젝트명 | 수기 |

</br>

## Configuration

추후 기능 확장 등에 따라 유지하되 초기 스프린트에서 사용하지 않는 경우 `.env` 설정 토글 방식으로 빈 등록에서 제외한다.

| 키 | 값 | 동작                                                                                                |
|---|---|---------------------------------------------------------------------------------------------------|
| `AUTH_LOCAL_ENABLED` | `true` / `false` | 자사 로그인 API. 명시적으로 `true` 여야 활성. `false` 면 `signup`/`login` 경로가 인가에서 차단.                           |
| `AUTH_SOCIAL_ENABLED` | `true` / `false` | 소셜(OAuth2) 로그인. 명시적으로 `true` 여야 활성                                                             |
| `WEBSOCKET_ENABLED` | `true` / `false` | WebSocket. 명시적으로 `true` 여야 활성                                                                               |
| `REFRESH_TOKEN_STORE` | `rdb` / `redis` | Refresh Token 저장소. 명시적으로 `rdb` 또는 `redis` 를 주입. `redis` 는 `REDIS_HOST`/`REDIS_PORT` 의 인증용 Redis(`@AuthRedis`)에 연결 |

### Social Provider

`AUTH_SOCIAL_ENABLED=true` 인 경우, provider 자격증명은 채운 것만 등록되고 빈 값은 자동 제외된다.

| provider | 키 |
|---|---|
| Google | `GOOGLE_CLIENT_ID` · `GOOGLE_CLIENT_SECRET` |
| Kakao | `KAKAO_CLIENT_ID` · `KAKAO_CLIENT_SECRET` |
| Naver | `NAVER_CLIENT_ID` · `NAVER_CLIENT_SECRET` |
| GitHub | `GITHUB_CLIENT_ID` · `GITHUB_CLIENT_SECRET` |

</br>

## Removal

사용하지 않는 기능은 아래 목록 삭제로 제거한다.

### WebSocket

| 대상 | 위치 |
|---|---|
| 코드 | `src/main/java/**/websocket/` 전체 |
| 테스트 | `src/test/java/**/websocket/` 전체 |
| 의존성 | `build.gradle` 의 `spring-boot-starter-websocket` |
| 설정 | `application.yml` 의 `app.websocket` · `.env` 의 `WEBSOCKET_ENABLED` |

### 소셜 로그인(OAuth2)

| 대상 | 위치 |
|---|---|
| 코드 | `modules/auth/oauth2/` 전체 · `domain/SocialAccount` · `repository/SocialAccountRepository` |
| 코드 참조 | `AuthService` 의 `socialAccountRepository` 필드, 탈퇴 시 삭제 호출 |
| 테스트 | `modules/auth/oauth2/` 아래 전체 · `AuthServiceTest` 의 `socialAccountRepository` 목 필드, 탈퇴 검증 줄 |
| 스키마 | `db/migration/{vendor}/V3__init_social_auth.sql` |
| 의존성 | `build.gradle` 의 `spring-boot-starter-oauth2-client` |
| 설정 | `application.yml` 의 `app.auth.social` · `app.oauth2` · `.env` 의 `AUTH_SOCIAL_ENABLED` · `OAUTH2_*` · provider 자격증명 |

### 자사(로컬) 로그인

| 대상 | 위치 |
|---|---|
| 코드 | `web/controller/LocalAuthController` · `security/LocalUserDetails` · `security/LocalUserDetailsService` · `security/LocalAuthSecurityCustomizer` · `domain/LocalAccount` · `repository/LocalAccountRepository` |
| 코드 참조 | `AuthService` 의 자사 가입·로그인 메서드, `localAccountRepository` 참조 |
| 테스트 | `AuthServiceTest` · `AuthControllerTest` · `AuthFlowIntegrationTest` 의 자사 가입·로그인 케이스 |
| 스키마 | `db/migration/{vendor}/V2__init_local_auth.sql` |
| 설정 | `application.yml` 의 `app.auth.local` · `.env` 의 `AUTH_LOCAL_ENABLED` |

### Redis-based Refresh Token 스토어

| 대상 | 위치 |
|---|---|
| 코드 | `token/store/RdbRefreshTokenStore` · `token/RefreshTokenScheduler` · `domain/RefreshToken` · `repository/RefreshTokenRepository` |
| 테스트 | `modules/auth/repository/RefreshTokenRepositoryTest` |
| 스키마 | `db/migration/{vendor}/V4__init_refresh_tokens.sql` |
| 설정 | `.env` 의 `REFRESH_TOKEN_STORE=redis` 유지 |
