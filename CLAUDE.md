# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Run locally (embedded Tomcat on port 8088)
./mvnw spring-boot:run

# Build WAR for production deployment
./mvnw clean package

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=IntegrationTest

# Compile only
./mvnw clean compile
```

Output WAR: `target/JobmoaProject-1.4.0-SNAPSHOT.war`

## Technology Stack

- **Spring Boot 3.4.1** with Java 17, packaged as WAR
- **Spring MVC** with JSP views (`/WEB-INF/views/*.jsp`)
- **MyBatis 3.5.6** for SQL (XML mappers, no Spring Data)
- **MSSQL Server** as the primary database (T-SQL syntax; Korean column names are common)
- **Redis (Jedis)** for caching with Jackson serialization
- **Google Gemini AI** (`google-genai` SDK v1.47.0) for AI-powered job recommendation
- **Apache POI** for Excel generation
- **Spring WebSocket** with STOMP/SockJS
- **Lombok** for boilerplate reduction
- **OkHttp** for external HTTP calls
- **AdminLTE 3** (Bootstrap 4) for frontend UI framework
- **jQuery** + SweetAlert2 + ApexCharts for frontend interactivity

## Project Structure

```
src/main/java/com/jobmoa/app/
├── TestProjectApplication.java     # Entry point (@SpringBootApplication)
├── config/                         # Spring configuration beans
│   ├── RootConfig.java             # DataSource, MyBatis, Redis, Mail, Transactions, Gemini client
│   ├── WebMvcConfig.java           # MVC, interceptors, message converters
│   ├── SecurityConfig.java         # Security configuration
│   └── WebSocketConfig.java        # STOMP endpoint, message broker
├── CounselMain/                    # Main counseling module
│   ├── biz/                        # Services + DAOs (business logic)
│   │   ├── recommend/              # Gemini AI job recommendation (GeminiApiService, concurrency mgr)
│   │   ├── adminpage/              # Admin service layer
│   │   └── ...                     # participant*, login, dashboard, report, etc.
│   └── view/                       # Controllers (request handlers)
│       ├── adminpage/              # AdminPageController, AdminApiController
│       └── ...                     # login, dashboard, chatBot, mypage, etc.
├── jobPlacement/                   # Job placement module
│   ├── biz/jobPlacement/           # Service + DAO
│   └── view/                       # Controllers + WebSocket + starbucks
└── recruitmentFormation/           # Recruitment information module
    ├── biz/                        # RecruitmentService, DAO, Scheduler, DTOs
    └── view/jobinfo/               # jobinfoMainController, jobinfoApiController

src/main/resources/
├── application.properties          # Config with env var placeholders (${JDBC_URL}, etc.)
├── .env                            # Actual credentials (git-ignored, loaded via @PropertySource)
├── sql-map-config.xml              # MyBatis type aliases + mapper registration
└── mappings/*.xml                  # MyBatis SQL statements

src/main/webapp/
├── WEB-INF/views/                  # JSP views (participantMain, admin/, chatBot/, etc.)
├── WEB-INF/tags/                   # Custom JSP tags (gnb.tag, adminGnb.tag, footer.tag, etc.)
├── js/                             # JavaScript files (versioned: {feature}_0.0.X.js)
└── css/                            # CSS files with module subdirectories
```

## Architecture

**Request flow:** `LoginInterceptor → Controller → Service → DAO → MyBatis → MSSQL`

**Three modules with explicit per-package component scanning:**
- `CounselMain` — primary counseling management (participants, education, employment, reports, admin, AI recommend)
- `jobPlacement` — job placement with async REST endpoints, WebSocket chat, and starbucks page
- `recruitmentFormation` — recruitment information with scheduled data sync (`RecruitmentScheduler`)

**IMPORTANT: Component scanning is NOT wildcard-based.** New packages must be explicitly added to both `RootConfig.java` (for `biz/` services) and `WebMvcConfig.java` (for `view/` controllers), or Spring will not detect them.

**Layering conventions:**
- `config/` — Java-based Spring bean configuration only
- `biz/` — service interfaces + `*Impl` classes + DAO classes per feature
- `view/` — `@Controller` classes, one per feature domain

**Service/DAO pattern:**
- Services follow interface + `*Impl` pattern with `@Service`
- DAOs use `@Repository` + `SqlSessionTemplate` (bean name `mybatis`) with XML mapper namespaces
- DAO namespace constant: `private static final String ns = "ClassNameDAO.";` — must match XML `<mapper namespace="ClassNameDAO">`
- No Spring Data repositories — all SQL is in `src/main/resources/mappings/*.xml`

**Controller naming:**
- Page controllers: `*Controller` with `@Controller`, return JSP view names
- REST API: `*ApiController` with `@RestController`, return JSON
- Async: `*AsyncController` with `@RestController`

**URL mapping patterns (mixed):**
- Legacy: `.do` suffix patterns (e.g., `/login.do`, `/participantMain.do`) — do NOT refactor to REST unless explicitly asked
- Modern: REST-style `@GetMapping`/`@PostMapping` (e.g., `/admin`, `/admin/users`)

**AOP Transactions:**
- Declared in `RootConfig.java` via `AspectJExpressionPointcut`
- CounselMain pointcut: `execution(* com.jobmoa.app.CounselMain.biz..*.*Impl.*(..))` (recursive `..`)
- jobPlacement pointcut: `execution(* com.jobmoa.app.jobPlacement.biz.*.*Impl.*(..))` (single level `*`)
- Methods prefixed `select*` are read-only; all others use `PROPAGATION_REQUIRED`
- **Do NOT use `@Transactional` annotations** — AOP handles all transaction boundaries

**Authentication:**
- `LoginInterceptor` checks `HTTPSession` attribute `JOBMOA_LOGIN_DATA` (a `LoginBean`)
- Excludes: `/login.do`, `/jobinfo/**`, `/recruitmentInformation/**`, `/jobPlacement/**`, `/Starbucks/**`, static resources, WebSocket endpoints

**MyBatis mapper convention:**
- Each DAO class has a corresponding `*-mapping.xml` in `mappings/`
- The XML `namespace` matches the DAO class name
- Mapper files named: `{Module}-mapping.xml` (e.g., `Participant-mapping.xml`)
- All mappers must be registered in `sql-map-config.xml` (both `<typeAliases>` for DTOs and `<mappers>` for XML files)
- **T-SQL syntax only** (MSSQL): `TOP N`, `IIF()`, `ISNULL()`, `GETDATE()`, string concat with `+`
- **Korean column names are intentional** (government schema) — do not rename to English
- Use `#{param}` for parameters (PreparedStatement). Never use `${param}` (SQL injection risk)
- Wrap comparison operators (`<=`, `>=`) in `<![CDATA[]]>`

**Gemini AI integration (`CounselMain/biz/recommend/`):**
- `GeminiApiService` uses Google Generative AI SDK (`com.google.genai.Client`) for job recommendation
- `ParticipantJobRecommendServiceImpl` orchestrates the full recommendation flow
- `RecommendConcurrencyManager` handles concurrent recommendation requests
- Default model: `gemma-4-31b-it` (configurable via `gemini.api.model`); client bean in `RootConfig.java`

## Key Configuration

**Environment variables:** `application.properties` contains `${VAR}` placeholders. Actual credentials go in `src/main/resources/.env` (git-ignored). `RootConfig.java` loads `.env` via `@PropertySource(value = "classpath:.env", ignoreResourceNotFound = true)`.

Required env vars: `JDBC_URL`, `JDBC_USERNAME`, `JDBC_PASSWORD`, `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `GEMINI_API_KEY`, `WORK24_API_KEY`, `KAKAO_JAVASCRIPT_KEY`.

Server runs on port `8088`. Session timeout is 6 hours (`21600` seconds).

WebSocket endpoints: `/ws`, `/ws-notification`; message broker prefixes: `/topic` (subscribe), `/app` (send).

ViewResolver: prefix `/WEB-INF/` + suffix `.jsp`.

## Conventions & Prohibitions

- **No JPA/Hibernate/Spring Data** — MyBatis + XML mappers only
- **No React/Vue/Angular** — jQuery + JSP stack only; no npm/webpack/vite
- **All UI text in Korean** — no i18n or English translation
- **Lombok required** — use `@Data`/`@Getter`/`@Setter` for DTOs, `@Slf4j` for logging
- **JS/CSS versioning** — files named `{feature}_0.0.X.js` / `{feature}_0.0.X.css`; bump patch version on changes
- **Frontend libs** — jQuery `$.ajax()` for AJAX, SweetAlert2 for alerts, ApexCharts for charts
- **Custom JSP tags** — `gnb.tag`, `adminGnb.tag`, `footer.tag`, `pagination.tag` in `/WEB-INF/tags/`
- **No `var` in JavaScript** — always use `let` or `const`; `var`는 사용 금지

## Git Commit 메시지 형식

커밋 메시지는 반드시 아래 형식을 따라 한국어로 작성한다. `$GIT_BRANCH_NAME`은 현재 브랜치명으로 치환한다.

```
[커밋타입][브랜치명]: 제목

### 1. 주요 변경 사항 (코드 리뷰)
- 변경된 코드에 대한 상세 설명을 코드 리뷰 형식으로 작성
- 예: `A` 함수의 로직을 `B`로 변경하여 성능을 개선함

### 2. 이번 업데이트 요약
- 이번 커밋으로 변경된 내용의 핵심 요약

### 3. 확인 체크리스트
- [ ] 관련 기능 테스트 완료
- [ ] 빌드 성공 확인
```

**커밋 타입:** feat, fix, refactor, docs, test, chore, perf, ci
**예시:** `[feat][main]: 상담일정 관리자 통합 조회 기능 구현`
