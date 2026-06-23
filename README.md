# JobmoaProject (잡모아)

> 한국 정부 취업상담·알선 통합 관리 시스템

상담사·관리자가 구직 참여자의 **상담 → 직업훈련 → 알선 → 취업**까지 전 과정을 관리하고, AI(Gemini)로 채용공고를 추천하며, 기업과의 알선·이력서 요청을 처리하는 웹 애플리케이션입니다. 정부 사업 스키마를 그대로 반영하여 **DB 컬럼명·UI 텍스트는 모두 한국어**입니다.

---

## 1. 한눈에 보는 구조

3개 모듈로 구성됩니다.

| 모듈 | 역할 | 대표 경로 |
|------|------|-----------|
| **CounselMain** | 상담관리 핵심 — 참여자/교육/취업/일지/관리자/AI추천/일정 | `/admin`, `/api/recommend`, `/api/schedule` |
| **jobPlacement** | 알선 — 기업 대상 알선 페이지, WebSocket 채팅, 이력서 요청 | `/jobPlacement` |
| **recruitmentFormation** | 채용정보 — 고용24(WORK24) 채용공고 수집·동기화 | `/jobinfo`, `/recruitmentInformation` |

요청은 항상 다음 흐름을 탑니다.

```
LoginInterceptor → Controller(@Controller/@RestController) → Service(*Impl) → DAO(@Repository) → MyBatis(XML) → MSSQL
```

---

## 2. 기술 스택

| 분류 | 사용 기술 |
|------|-----------|
| 런타임 | **Spring Boot 3.4.1**, Java 17, WAR 패키징 (외부 Tomcat / 임베디드 Tomcat 8088) |
| 웹 | Spring MVC + **JSP** (`/WEB-INF/views/*.jsp`), JSTL |
| 영속성 | **MyBatis 3.5.6** (XML 매퍼, JPA 미사용) + Spring JDBC + DBCP2 |
| DB | **MSSQL Server** (T-SQL, 한국어 컬럼명) |
| 캐시 | **Redis** (Jedis + Jackson 직렬화) |
| AI | **Google Gemini** (`google-genai` 1.47.0) — 채용공고 추천 |
| 실시간 | Spring **WebSocket** (STOMP/SockJS) |
| 외부 API | 고용24(WORK24), Kakao Map |
| 기타 | Apache POI(엑셀), OkHttp, Lombok, BCrypt(crypto only) |
| 프론트 | **AdminLTE 4** (Bootstrap 5) + jQuery + SweetAlert2 + ApexCharts |

> 빌드 산출물: `target/JobmoaProject-1.7.0-SNAPSHOT.war`

---

## 3. 빠른 시작

### 3-1. 사전 준비 — `.env` 작성

자격증명은 `src/main/resources/.env`(git 제외)에 둡니다. `RootConfig`가 `@PropertySource("classpath:.env")`로 로드합니다. 템플릿: `src/main/resources/.example.application.properties`.

필수 환경변수:

```
JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD
REDIS_HOST, REDIS_PORT, REDIS_PASSWORD
MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD
GEMINI_API_KEY, GEMINI_MODEL_STAGE1, GEMINI_MODEL_STAGE2
WORK24_API_KEY, KAKAO_JAVASCRIPT_KEY
```

### 3-2. 실행 / 빌드

```bash
# 로컬 실행 (임베디드 Tomcat, http://localhost:8088)
./mvnw spring-boot:run

# 운영 배포용 WAR 빌드
./mvnw clean package        # → target/JobmoaProject-1.7.0-SNAPSHOT.war

# 컴파일만 / 테스트
./mvnw clean compile
./mvnw test
./mvnw test -Dtest=IntegrationTest   # 단일 테스트 클래스
```

> 세션 타임아웃 6시간(`21600s`). 로그인 세션 키는 `JOBMOA_LOGIN_DATA`.

---

## 4. 디렉터리 구조 (요약)

```
src/main/java/com/jobmoa/app/
├── TestProjectApplication.java     # 진입점 (@SpringBootApplication, WAR)
├── config/                         # Java 기반 빈 설정
│   ├── RootConfig.java             # DataSource·MyBatis·Redis·Mail·트랜잭션(AOP)·Gemini
│   ├── WebMvcConfig.java           # MVC·인터셉터·뷰리졸버·컴포넌트스캔(view)
│   ├── SecurityConfig.java         # (빈 껍데기 — Spring Security 미사용)
│   └── WebSocketConfig.java        # STOMP 엔드포인트·메시지 브로커
├── CounselMain/        biz/(서비스+DAO) · view/(컨트롤러)
├── jobPlacement/       biz/ · view/(async, webSocket)
└── recruitmentFormation/  biz/ · view/jobinfo

src/main/resources/
├── application.properties          # ${ENV} 플레이스홀더
├── .env                            # 실제 자격증명 (git 제외)
├── sql-map-config.xml              # MyBatis typeAlias + 매퍼 등록
└── mappings/*.xml                  # MyBatis SQL (24개 매퍼)

src/main/webapp/
├── WEB-INF/views/*.jsp · WEB-INF/tags/*.tag   # 화면 + 커스텀 태그
├── js/  {feature}_0.0.X.js                      # 버전드 JS
└── css/ {feature}_0.0.X.css                     # 버전드 CSS
```

---

## 5. OT 문서 (실습생 필독 순서)

| 순서 | 문서 | 위치 |
|------|------|------|
| 1 | 본 README | (루트) |
| 2 | 시스템 아키텍처 정의서 | [`docs/운영/시스템_아키텍처_정의서.md`](docs/운영/시스템_아키텍처_정의서.md) |
| 3 | 데이터베이스 설계서 (ERD) | [`docs/기능명세/데이터베이스_설계서_ERD.md`](docs/기능명세/데이터베이스_설계서_ERD.md) |
| 4 | API 명세서 | [`docs/기능명세/API_명세서.md`](docs/기능명세/API_명세서.md) |
| 5 | 개발 표준 정의서 | [`docs/운영/개발_표준_정의서.md`](docs/운영/개발_표준_정의서.md) |

> 코딩 규칙 원본: 프로젝트 루트 `CLAUDE.md`, `.claude/rules/{java-spring,mybatis,jsp-frontend}-conventions.md`

---

## 6. 절대 하지 말 것 (핵심 금지사항)

- ❌ **JPA / Hibernate / Spring Data 도입** — SQL은 MyBatis XML 매퍼에만 작성
- ❌ **`@Transactional` 어노테이션** — 트랜잭션은 `RootConfig`의 AOP 포인트컷이 전담
- ❌ **MyBatis `${param}`** — SQL injection 위험. 값 바인딩은 `#{param}`만
- ❌ **레거시 `.do` URL을 REST로 리팩터링** — 명시 요청 없이는 그대로 유지
- ❌ **React/Vue/Angular, npm/webpack/vite** — jQuery + JSP 스택 유지
- ❌ **JS `var`** — `let`/`const`만 사용
- ❌ **DB 컬럼명·UI 텍스트 영어화** — 정부 스키마, 모두 한국어 유지
- ❌ **시크릿 하드코딩** — `.env` + 환경변수만
- ⚠️ **새 패키지 추가 시** `RootConfig`(biz) + `WebMvcConfig`(view) **양쪽**에 컴포넌트 스캔 등록 (와일드카드 스캔 아님)
