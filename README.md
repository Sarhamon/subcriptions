# subcriptions

Netflix, Spotify, ChatGPT Plus... 매달/매년 빠져나가는 구독료를 한 곳에서 추적하는 **개인용 구독 트래커**.
KRW/USD/JPY를 섞어 등록해도 자동으로 원화 환산 금액이 함께 표시된다.

> Spring Boot 4 + Mustache(SSR) + REST API 기반의 학습용 프로젝트 (1~14강 범위).

---

## 한눈에 보기

- **홈 대시보드**: 월 합산 지출(원화 환산), 이번 달 갱신 건수, 7일 이내 임박 건수, 자동결제 수, 갱신 임박 목록 4건
- **구독 CRUD**: 등록 / 목록 / 상세 / 수정 / 삭제
- **REST API (JSON)**: `/api/subscriptions` GET·POST·PATCH·DELETE, 없는 ID는 JSON 404
- **분류 태그 + 필터**: 엔터테인먼트/음악/AI/생산성/게임/개발 태그, 태그별 목록 필터
- **다통화 + 실시간 환율**: KRW/USD/JPY 각각 등록, 외화는 괄호로 원화 환산 동시 표기
- **갱신일 자동 처리**: 다음 갱신일이 이미 지난 구독은 조회 시 다음 주기로 자동 슬라이드
- **갱신 임박 강조**: 3일·7일 이내 갱신은 카드에 강조 배지
- **댓글(메모) 엔티티**: 구독별 메모 저장 기반 — 엔티티·리파지터리 (14강 범위, UI는 향후)
- **가로 카드 캐러셀**: 목록을 한눈에 비교, 마우스 클릭+드래그로 좌우 스크롤
- **우측 슬라이드 메뉴**: 헤더 우측 햄버거(☰) → 사이드 패널 슬라이드 인
- **404 공통 처리**: 화면은 통일된 404 페이지, REST는 JSON 404 응답
- **테스트**: REST 통합(MockMvc) + 리파지터리 테스트 **9건** 통과
- **부팅 시 시드 데이터**: 구독 7건 + 태그 + 댓글이 자동 적재되어 바로 화면 확인 가능

---

## 기술 스택

| 영역 | 사용 기술 |
|------|---------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.6 |
| Build | Gradle |
| ORM | Spring Data JPA |
| DB | H2 (인메모리) |
| View | Mustache (서버 사이드 렌더링) |
| Web | Spring Web MVC, RestClient |
| REST / JSON | `@RestController`, Jackson 3 |
| 테스트 | JUnit 5, MockMvc |
| 보일러플레이트 | Lombok |
| 외부 API | [open.er-api.com](https://www.exchangerate-api.com/docs/free) (무료·API 키 불필요, KRW 포함 170개 통화) |

---

## 빠른 시작

```bash
./gradlew bootRun
```

- 앱: <http://localhost:8081>
- H2 콘솔: <http://localhost:8081/h2-console>
  - JDBC URL: `jdbc:h2:mem:subecodb`
  - User Name: `sa` (비밀번호 없음)

> 포트 `8081`을 쓰는 이유: 학교 실습 환경에서 8000/8080이 다른 프로세스에 점거되는 경우가 잦아 회피용으로 지정.

---

## 디렉토리 구조

```
src/main/java/com/framework/subcriptions/
 ├─ SubcriptionsApplication.java       # 진입점 (@EnableScheduling 포함)
 ├─ controller/
 │   ├─ HomeController.java             # "/" 홈 대시보드 (지표 집계)
 │   ├─ SubscriptionController.java     # 구독 CRUD 화면 라우팅
 │   ├─ SubscriptionApiController.java  # REST API (/api/subscriptions, JSON)
 │   ├─ RatesController.java            # 환율 조회 / 수동 갱신
 │   └─ GlobalExceptionHandler.java     # 도메인 예외 → 에러 뷰 매핑
 ├─ domain/
 │   ├─ Subscription.java               # JPA 엔티티 (Builder)
 │   ├─ Comment.java                    # 구독 댓글 (단방향 @ManyToOne)
 │   ├─ BillingCycle.java               # MONTHLY / YEARLY enum + 날짜 계산
 │   ├─ Currency.java                   # KRW / USD / JPY enum + fallback 환율
 │   ├─ Tag.java                        # 분류 태그 enum (@ElementCollection)
 │   └─ SubscriptionNotFoundException.java
 ├─ dto/
 │   ├─ SubscriptionForm.java           # 폼/JSON 입력 DTO (→ 엔티티)
 │   └─ SubscriptionView.java           # 화면/JSON 출력 DTO (계산 사전 완료)
 ├─ repository/
 │   ├─ SubscriptionRepository.java     # JpaRepository
 │   └─ CommentRepository.java          # findBySubscriptionId
 └─ service/
     ├─ SubscriptionService.java        # CRUD + 갱신일 슬라이드 로직
     └─ ExchangeRateService.java        # 환율 캐시 + 매일 자정 자동 갱신

src/test/java/com/framework/subcriptions/
 ├─ SubcriptionsApplicationTests.java               # 컨텍스트 로드
 ├─ controller/SubscriptionApiControllerTest.java   # REST 통합 테스트 (MockMvc) 6건
 └─ repository/CommentRepositoryTest.java            # 리파지터리 테스트 2건

src/main/resources/
 ├─ application.properties             # 포트/인코딩/H2/JPA 설정
 ├─ data.sql                           # 구독 7건 + 태그 + 댓글 시드
 ├─ static/css/style.css               # 전역 스타일 + 카드 캐러셀 + 사이드 메뉴
 └─ templates/
     ├─ index.mustache                 # 홈 대시보드
     ├─ rates.mustache                 # 환율 현황 (실시간/폴백 상태)
     ├─ layouts/
     │   ├─ header.mustache            # 공통 헤더 + 사이드 메뉴 + 토글 JS
     │   └─ footer.mustache
     ├─ error.mustache                 # Spring 기본 에러 뷰
     ├─ error/404.mustache             # 404 전용 뷰
     └─ subscriptions/
         ├─ index.mustache             # 카드 캐러셀 목록 (드래그 스크롤 JS 포함)
         ├─ new.mustache               # 등록 폼
         ├─ edit.mustache              # 수정 폼
         └─ show.mustache              # 단건 상세
```

---

## 주요 설계 노트

### 갱신일 자동 슬라이드

`SubscriptionService`는 `findAll` / `findById` 시점에 `applyRenewalIfDue`를 호출한다.
다음 갱신일이 오늘보다 이전이면 `Subscription#slideToNextCycle`을 반복 호출해 한 주기씩 밀어준다.
별도의 배치/스케줄러 없이 "조회되는 순간" 상태가 따라잡히는 단순한 전략.

### 환율 환산

`ExchangeRateService`는 부팅 시 `@PostConstruct`로 [open.er-api.com](https://www.exchangerate-api.com/)을 한 번 호출해 `ConcurrentHashMap`에 캐싱하고,
이후 매일 자정(`@Scheduled(cron = "0 0 0 * * *")`)에 다시 갱신한다.
응답이 비거나 API가 실패하면 `Currency` enum에 박힌 fallback 환율을 그대로 유지한다.
JPY는 직접 환율 대신 `KRW/USD ÷ JPY/USD` 교차 환율로 계산한다.

> 초기에는 Frankfurter API를 썼으나 **데이터 소스(ECB)에 KRW가 없어** HTML 에러를 반환 → KRW를 지원하는 open.er-api.com으로 교체했다.

`SubscriptionView`가 화면 진입 직전에 `displayPrice` 문자열을 미리 만들어두므로 템플릿은 단순히 출력만 한다.
외화는 `220 달러 (329,560원)` 형태로 한 줄에 함께 표기.

### REST API + JSON

`SubscriptionApiController`(`@RestController`)가 `/api/subscriptions`로 GET·POST·PATCH·DELETE를 제공한다.
반환 객체는 Jackson이 JSON으로 직렬화하며, 읽기 응답은 화면과 동일한 `SubscriptionView`를 재사용한다.
화면용 404는 HTML이지만, REST는 **컨트롤러 로컬 `@ExceptionHandler`**로 `{"message": ...}` JSON 404를 따로 응답한다(전역 핸들러보다 우선).

### 홈 대시보드

`HomeController`가 전체 구독을 모아 스트림으로 집계한다 — 월 합산 지출, 이번 달 갱신 건수, 7일 이내 임박 수, 자동결제 수, 그리고 갱신일 순 정렬 상위 4건.

### 댓글(Comment) 엔티티

`Comment`가 구독을 단방향 `@ManyToOne`으로 참조하고, `CommentRepository.findBySubscriptionId(id)`로 구독별 메모를 조회한다.
엔티티·리파지터리까지(14강 범위)이며 컨트롤러·서비스·UI는 향후 과제.

### 테스트

`SubscriptionApiControllerTest`(MockMvc로 REST CRUD·404 검증 6건), `CommentRepositoryTest`(조회 쿼리 2건), 컨텍스트 로드 1건 — 총 9건이 `./gradlew test`로 모두 통과한다.

### 카드 캐러셀 + 드래그 스크롤

목록 화면은 `<article>` 리스트 대신 `.card-carousel`에 가로 flex + `scroll-snap-type: x mandatory`를 적용.
마우스 드래그는 `mousedown`/`mousemove`/`mouseup`을 직접 잡고 `requestAnimationFrame`으로 `scrollLeft`를 프레임 단위로 배칭해 끊김을 없앴다.
드래그 중에는 `.dragging` 클래스를 부여해 스냅 해제 + 카드 hover 깜빡임 방지.
이동 거리가 5px을 초과하면 클릭 이벤트를 캡처 단계에서 막아 카드 링크 이동을 취소한다.

### 사이드 메뉴 슬라이드

헤더 우측 햄버거 버튼을 누르면 `body.menu-open` 클래스만 토글한다.
모든 트랜지션(오버레이 페이드, 패널 `translateX`)은 CSS가 담당하고 JS는 클래스 토글과 ESC 단축키만 책임.

### 도메인 예외 → 공통 404

`SubscriptionService`는 미발견 시 `SubscriptionNotFoundException`을 던지고,
`GlobalExceptionHandler`(`@ControllerAdvice`)가 이를 잡아 `error/404` 뷰 + HTTP 404로 변환한다.

---

## Mustache 템플릿 컨벤션

```
templates/
 ├─ layouts/
 │   ├─ header.mustache    # 모든 페이지가 import
 │   └─ footer.mustache
 └─ <domain>/
     ├─ index.mustache     # 목록  GET /<domain>
     ├─ new.mustache       # 생성 폼 GET /<domain>/new
     ├─ edit.mustache      # 수정 폼 GET /<domain>/{id}/edit
     └─ show.mustache      # 상세    GET /<domain>/{id}
```

- 모든 페이지는 `{{>layouts/header}}` / `{{>layouts/footer}}` partial로 공통 영역 포함
- 페이지 제목은 `title` 모델 변수로 컨트롤러에서 전달
- 폼 입력 DTO: `<Entity>Form`, 화면 출력 DTO: `<Entity>View`

---

## 시드 데이터

`src/main/resources/data.sql`에 KRW/USD/JPY × MONTHLY/YEARLY × 자동결제 on/off 조합을 골고루 포함한 6건이 들어있다.
H2 인메모리이므로 부팅할 때마다 동일한 데이터로 초기화된다.
값을 바꾸고 싶다면 그 파일만 수정 후 재기동.

---

## 로드맵

- [x] 갱신 임박 인앱 강조 (3·7일 배지 + 홈 임박 목록)
- [x] REST API + JSON (`/api/subscriptions`)
- [x] 댓글 엔티티 · 리파지터리 (14강 범위)
- [ ] 댓글 화면 CRUD (컨트롤러·서비스·UI, 15강 이후)
- [ ] 회원가입 / 멀티 사용자 시스템
- [ ] 이메일 / 푸시 발송
- [ ] 실제 결제 게이트웨이 연동 (Stripe, 토스페이먼츠 등)
- [ ] 환율 캐시 영속화 (현재는 인메모리, 재기동 시 fallback부터 시작)
- [ ] 폼 검증 (`spring-boot-starter-validation` 도입)

---

## 베이스 프로젝트

구조/컨벤션은 [`framework_springboot`](https://github.com/Sarhamon/framework_springboot)의 패턴을 따른다.
다만 본 프로젝트에서는 의도적으로 `domain/` 패키지명과 `service/` 계층을 유지한다.
