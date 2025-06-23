# 프로젝트 개요

이 프로젝트는 Docker 환경에서 Spring Boot 기반의 백엔드 서버 3개와 Nginx 로드 밸런서, 그리고 STOMP 기반 WebSocket 채팅 서버를 구성하여, 클라우드 환경과 유사한 로컬 개발 환경을 구성하는 것을 목표로 합니다.

---

## 📅 1일차  
**Docker 기반 Spring 다중 서버 + Nginx 로드 밸런싱 환경 구축**

### ✅ 주요 작업

- `backend1`, `backend2`, `backend3`: 동일한 Spring 애플리케이션 컨테이너 3개 생성
- 각 컨테이너에 `PROJECT_NAME` 환경변수로 서버 식별 값 주입
- `nginx.conf`를 통해 **라운드로빈 방식**으로 요청 분산 처리
- Nginx 컨테이너에서 3개 백엔드 컨테이너를 `upstream`으로 연결
- MySQL 컨테이너 구성 및 `volume` 설정 완료 (`/volumes/mysql-data`)
- Spring 컨트롤러에서 `/api` 엔드포인트로 응답값 출력
- `docker-compose.yml`을 사용해 전체 구성 자동화

### 🔍 결과 확인

- 브라우저에서 `http://localhost` 접속 시 backend 서버가 순차적으로 응답하는 구조 확인 완료

---

## 📅 2일차  
**STOMP 기반 WebSocket 채팅 서버 및 Nginx WebSocket 프록시 구성**

### ✅ 주요 작업

- Spring WebSocket + STOMP 설정 완료 (`/ws-chat` 엔드포인트 사용)
- 프론트엔드에서 `nickname` 파라미터 기반 WebSocket 연결 구현 (`/ws-chat?nickname=xxx`)
- `@MessageMapping`, `@SendTo` 로직으로 STOMP 메시지 처리 구성
- Nginx에서 `/ws-chat` 요청을 **WebSocket 업그레이드**와 함께 백엔드로 프록시 처리
- `nginx.conf`에 `proxy_set_header` 및 `Upgrade` 헤더 설정으로 WebSocket 연결 유지
- backend1 ~ backend3 컨테이너에 동일한 WebSocket 서버 적용
- MySQL과 연결되는 Spring backend 컨테이너 정상 기동 확인
- Redis Pub/Sub 없이도 WebSocket 연결 및 메시지 송수신 확인

### 🔍 결과 확인

- 브라우저에서 `http://localhost` 접속 후 WebSocket 연결 및 채팅 메시지 송수신 정상 작동

---

## 📅 3일차
**Redis Pub/Sub 기반 WebSocket 채팅 메시지 분산 처리 구현**

### ✅ 주요 작업

- Redis 컨테이너 추가 및 `StringRedisTemplate` 기반 Pub/Sub 구조 도입
- 각 Spring 서버가 Redis를 통해 채팅 메시지를 publish → subscribe 하도록 구성
- `RedisPublisher` 클래스: 채팅 메시지를 Redis 채널(`room.*`)로 발행
- `RedisSubscriber` 클래스: Redis로부터 메시지를 수신해 WebSocket 구독자에게 전달
- `RedisMessageListenerContainer` 설정으로 `room.*`, `user.*` 채널 패턴 구독 처리
- 서버 간 메시지 일관성을 위해 단일 서버가 아닌 모든 서버가 동일한 메시지를 수신하도록 설계
- 귓속말 기능을 위해 `/user/queue/private` 경로 활용 (WebSocket STOMP)

### 🔍 결과 확인

- `backend1`, `backend2`, `backend3` 중 어떤 서버로 접속해도 채팅방 메시지가 모든 서버에 동기화됨
- Redis가 메시지를 중계하여 **멀티 서버 환경에서 실시간 채팅 일관성 보장**
- 클라이언트 간 귓속말 메시지 또한 정상 수신됨 (`/user/queue/private`)

---

## 📅 4일차
**AI 자동응답 기능 연동 및 WebSocket 프록시 문제 해결**

### ✅ 주요 작업

- `GPTService.java`를 통해 **OpenAI GPT-4.1 API** 연동 구현
    - 클라이언트 메시지를 OpenAI API로 전송하고 응답을 다시 채팅방에 전송
- Spring STOMP 채팅 흐름에 **AI 챗봇 자동응답 기능 추가**
    - 메시지를 수신하면 GPT 응답을 자동으로 브로드캐스트
- `HttpClient`를 사용해 OpenAI API 호출 및 JSON 응답 파싱 로직 작성

### 🔐 보안 및 GitHub Push Protection 대응

- 실수로 커밋된 **API Key 노출 문제 해결**
    - GitHub Push Protection으로 인한 `GH013` 에러 발생
    - 해당 API Key가 포함된 파일을 **삭제하고 커밋 내역을 정리**한 뒤 `--force push`로 재업로드

### 🛠️ WebSocket 연결 문제 해결

- Nginx에서 **WebSocket 연결 실패 및 프록시 문제** 디버깅
    - `proxy_pass`, `Upgrade`, `Connection` 등 헤더를 재설정하여 WebSocket 정상 연결
    - `/ws-chat` 경로의 WebSocket 요청이 백엔드로 올바르게 전달되도록 `nginx.conf` 수정
- 브라우저 콘솔(F12) 및 `docker logs` 명령어를 통한 실시간 로그 확인

### 🔍 결과 확인

- 클라이언트가 일반 채팅 메시지를 보내면, GPT-4.1 응답이 자동으로 수신되어 실시간 전송됨
- 브라우저에서 WebSocket 연결 및 AI 응답 동작 모두 정상 작동
- 민감 정보가 GitHub에 포함되지 않도록 **토큰을 제거한 커밋으로 정리 완료**


