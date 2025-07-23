# Spring 인증 방식 예제 모음

이 리포지터리는 Spring Security를 사용한 다양한 인증 방식을 학습하고 비교하기 위한 예제 프로젝트 모음입니다.  
각 폴더는 독립적인 Spring Boot 애플리케이션으로 구성되어 있으며, 특정 인증 방식 하나에 집중하여 구현되어 있습니다.

## 📁 예제 목록

| 폴더명              | 설명 |
|--------------------|------|
| `form-login`       | 커스텀 로그인 페이지를 사용하는 전통적인 폼 로그인 방식 |
| `http-basic-auth`  | HTTP Basic 인증을 활용한 간단한 인증 방식 |
| `jwt-auth`         | Access / Refresh 토큰을 활용한 JWT 기반 무상태 인증 방식 |
| `oauth2`           | Google, GitHub 등 외부 OAuth2 공급자를 이용한 로그인 방식 |

---

## 🔐 인증 방식 설명

### 1. `form-login`
- Spring Security의 전통적인 폼 로그인 방식
- 로그인 페이지 커스터마이징
- In-memory 사용자 정보 등록
- `/login`, `/logout` 지원

### 2. `http-basic-auth`
- HTTP 요청의 `Authorization` 헤더를 활용한 인증
- 주로 테스트용 API 인증 등에 사용
- 별도 UI 없이 브라우저/도구에서 인증 창 제공

### 3. `jwt-auth`
- JWT(Json Web Token)를 활용한 무상태 인증 방식
- 로그인 시 access token + refresh token을 발급
- access token은 요청마다 인증 필터에서 검증
- refresh token으로 access token 재발급 가능

### 4. `oauth2`
- Spring Security의 OAuth2 Client 기능 사용
- Google 등의 외부 OAuth2 공급자 연동
- OAuth2 로그인 후 사용자 정보 수신 및 세션 생성

---

## ✅ 실행 환경

- Java 17 이상
- Gradle (또는 내장된 `./gradlew`)
- Spring Boot 3.x
- OAuth2의 경우 구글, 카카오, 네이버에 개발자 환경 설정 연결 필요
