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

