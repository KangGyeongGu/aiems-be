# aiems-be Backup

AI 기반 응급환자 이송 관제 서비스 WAS Application

Origin Organization: [SSAFY-13th-2nd-semester-final](https://github.com/SSAFY-13th-2nd-semester-final)

## 기술 스택

- Java 21, Gradle, Spring Boot 3.5, Spring Batch
- Spring Data JPA, MySQL(hibernate-spatial), Flyway, Redis
- Spring Cloud Gateway, Spring Security, JWT
- RabbitMQ (AMQP·STOMP Relay)

## 구현 범위

### MSA 구축

- 도메인 기준 8개 마이크로 서비스로 분리 설계
- Spring Cloud Gateway 기반 서비스 단일 진입점 구현
- 내부 서비스 간 OpenFeign 기반 REST 동기 · 비동기 RabbitMQ 이벤트

### 병상 정보 수집 배치

- 국립중앙의료원 실시간 가용병상 API 주기 수집
- Redis TTL 캐시 적재

### RabbitMQ 메시징

- direct exchange · durable 큐 · DLX/DLQ 토폴로지
- RabbitMQ AMQP 기반 환자 분석 AI 요청 · 구급활동일지 생성 AI 요청 비동기 처리

### STOMP 브로커 릴레이

- RabbitMQ Broker 기반 MSA 앱 간 이벤트 발행 · 전달
- CONNECT 프레임 JWT 인증 인터셉터

### 주체별 인증·인가

- 병원 · 119 구급대 · 관제 센터 주체별 로그인 인증
- 주체별 Http REST API · WS Role 역할별 접근 인가