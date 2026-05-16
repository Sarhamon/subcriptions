# subcriptions

내가 가입한 여러 구독 서비스를 한 곳에서 관리하고, 갱신일이 다가오면 알려주는 **개인용 구독 트래커**.

> 📚 Spring Boot 팀 프로젝트

---

## 🎯 프로젝트 목적

매달/매년 자동 결제되는 구독 서비스(Netflix, YouTube Premium, 클라우드 스토리지 등)가 늘어나면서 다음과 같은 문제를 해결하고자 함:

- 어떤 서비스를 얼마에 구독 중인지 한눈에 보기 어렵다
- 갱신일을 놓쳐서 원치 않게 다음 달 결제가 발생한다
- 자동 결제 여부와 결제 수단을 따로 관리하기 번거롭다

---

## ✨ 핵심 기능 (MVP)

### 1. 구독 CRUD
- 구독 서비스 등록 / 조회 / 수정
- 구독 해지 처리

### 2. 관리자 인증 코드
- 회원가입·로그인 시스템 없음
- 환경 변수로 지정한 **관리자 인증 코드** 입력 시에만 접근 허용
- 인증 후 세션 유지

### 3. 갱신 알림
- 웹 접속 시 갱신일 **D-3부터** 화면 상단에 노출

---

## 🛠 기술 스택

| 영역 | 사용 기술 |
|------|---------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.6 |
| Build | Gradle |
| ORM | Spring Data JPA |
| DB | H2 (인메모리, 개발/학습용) |
| View | Mustache (SSR) |
| Web | Spring Web MVC |

### 추가 예정 의존성
- **Validation** — 폼 입력 검증

---

## 🚫 MVP 범위 밖 (Out of Scope)

다음은 **포함하지 않음** (필요해지면 추후 도입):

- 회원가입 / 멀티 사용자 시스템
- 이메일 발송
- 실제 결제 게이트웨이 연동 (Stripe, 토스페이먼츠 등)
- 다국가 통화 변환
- 모바일 앱 / 푸시 알림

---

## 🏃 로컬 실행

```bash
./gradlew bootRun
```

기본 포트: `http://localhost:8080`
H2 콘솔: `http://localhost:8080/h2-console` (설정 후)

---

## 📁 디렉토리 구조 (예정)

```
src/main/java/com/framework/subcriptions/
 ├─ SubcriptionsApplication.java
 ├─ domain/          # 엔티티
 ├─ repository/      # JpaRepository
 ├─ service/         # 비즈니스 로직
 ├─ controller/      # MVC 컨트롤러
 ├─ dto/             # 요청/응답 DTO
 └─ config/          # 관리자 인증 등 설정

src/main/resources/
 ├─ application.properties
 └─ templates/       # Mustache 템플릿
```
