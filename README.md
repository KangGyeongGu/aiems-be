# aiems-be Backup

AI 기반 응급환자 이송 관제 서비스 WAS Application

Origin Organization: [SSAFY-13th-2nd-semester-final](https://github.com/SSAFY-13th-2nd-semester-final)

## 기술 스택

- Java 21, Spring Boot 3.5, Gradle
- Spring Data JPA, MySQL(hibernate-spatial), Flyway
- Spring Security, JWT
- Spring Batch, ShedLock
- Redis
- RabbitMQ (AMQP · STOMP Relay)

## 구현 범위

### 병상 정보 수집 배치

- 국립중앙의료원 실시간 가용병상 API 주기 수집
- Redis TTL 캐시 적재
- ShedLock 분산락 단일 실행

### RabbitMQ 메시징

- direct exchange · durable 큐 · DLX/DLQ 토폴로지
- AI 환자 분석 동기 RPC(reply-to) · 이송 요약 일지 비동기 발행(replyTo · CorrelationData)
- publisher confirm/returns, 소비 재시도 소진 시 DLQ 격리, messageId 멱등 소비

### STOMP 브로커 릴레이

- 브로커 릴레이 경유 다중 인스턴스 실시간 전달 (유저 목적지 브로드캐스트)
- CONNECT 프레임 JWT 인증 인터셉터

### 역할 분리 기동

- WAS / Batch 역할 별 기동 프로파일 분리
