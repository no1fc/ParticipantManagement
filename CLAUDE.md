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

Output WAR: `target/JobmoaProject-1.3.3-SNAPSHOT.war`

## Technology Stack

- **Spring Boot 3.4.1** with Java 17, packaged as WAR
- **Spring MVC** with JSP views (`/WEB-INF/views/*.jsp`)
- **MyBatis 3.5.6** for SQL (XML mappers, no Spring Data)
- **MSSQL Server** as the primary database (T-SQL syntax; Korean column names are common)
- **Redis (Jedis)** for caching with Jackson serialization
- **Google Gemini AI** (`google-genai`) for AI-powered job recommendation
- **Apache POI** for Excel generation
- **Spring WebSocket** with STOMP/SockJS
- **Lombok** for boilerplate reduction
- **OkHttp** for external HTTP calls

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
├── application.properties          # DB, Redis, Mail, Gemini API config (dev/default)
├── production.application.properties # Production overrides (loaded by RootConfig @PropertySource)
├── sql-map-config.xml              # MyBatis type aliases + mapper registration
└── mappings/*.xml                  # MyBatis SQL statements (15 mapper files)

src/main/webapp/
├── WEB-INF/views/                  # JSP views (participantMain, admin/, chatBot/, etc.)
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
- Services follow interface + `*Impl` pattern
- DAOs use `SqlSessionTemplate` directly with XML mapper namespaces
- No Spring Data repositories — all SQL is in `src/main/resources/mappings/*.xml`

**URL mapping patterns (mixed):**
- Legacy: `.do` suffix patterns (e.g., `/login.do`, `/participantMain.do`)
- Modern: REST-style `@GetMapping`/`@PostMapping` (e.g., `/admin`, `/admin/users`)
- Async controllers use `@RestController` with `/...Async` paths

**AOP Transactions:**
- Declared in `RootConfig.java` via `AspectJExpressionPointcut`
- Pointcut: `execution(* com.jobmoa.app.CounselMain.biz.*.*Impl.*(..))`
- Methods prefixed `select*` are read-only; all others use `PROPAGATION_REQUIRED`
- Separate transaction advisor exists for `jobPlacement` package

**Authentication:**
- `LoginInterceptor` checks `HTTPSession` attribute `JOBMOA_LOGIN_DATA` (a `LoginBean`)
- Excludes `/login.do`, `/jobinfo/**`, static resources, and WebSocket endpoints
- Role/branch access data comes from `MemberDTO`

**MyBatis mapper convention:**
- Each DAO class has a corresponding `*-mapping.xml` in `mappings/`
- The XML `namespace` matches the DAO class name
- Mapper files named by module: `{Module}-mapping.xml` (e.g., `Participant-mapping.xml`)
- All mappers must be registered in `sql-map-config.xml`

**Gemini AI integration (`CounselMain/biz/recommend/`):**
- `GeminiApiService` uses Google Generative AI SDK (`com.google.genai.Client`) for job recommendation
- `ParticipantJobRecommendServiceImpl` orchestrates the full recommendation flow
- `RecommendConcurrencyManager` handles concurrent recommendation requests
- Model configured via `gemini.api.model` property; client bean initialized in `RootConfig.java`
- Recommendation results stored in DB via `ParticipantJobRecommendDAO` (mapper: `ParticipantJobRecommend-mapping.xml`)

## Key Configuration

**Property loading:** `RootConfig.java` uses `@PropertySource("classpath:production.application.properties")` — so production config is loaded from `production.application.properties`, while `application.properties` provides Spring Boot defaults (port, session, etc.). Both files are tracked in git — do not add new secrets without team awareness.

Server runs on port `8088`. Session timeout is 6 hours (`21600` seconds).

WebSocket endpoints: `/ws`, `/ws-notification`; message broker prefixes: `/topic` (subscribe), `/app` (send).

ViewResolver: prefix `/WEB-INF/` + suffix `.jsp`.