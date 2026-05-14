# JobmoaProject 코드 분석 및 수정 필요 항목

작성일: 2026-05-13  
분석 방식: Superpowers `using-superpowers`, 테스트 실패 후 `systematic-debugging` 적용  
검증 명령: `.\mvnw.cmd test`

## 요약

현재 프로젝트에서 가장 먼저 수정해야 할 영역은 인증 우회, 비밀번호 처리, 배포물 내 비밀값 포함, 테스트 환경 붕괴입니다. 특히 `src/main/resources/.env`가 `target/classes/.env`로 복사되어 배포물에 포함될 수 있고, `LoginInterceptor`가 `/api/**`, `*.api`, `/**/*.api`, `/**/*.do`를 모두 제외하고 있어 전역 로그인 게이트가 사실상 많은 엔드포인트에 적용되지 않습니다.

`.\mvnw.cmd test`는 실패했습니다. 루트 원인은 테스트가 `RootConfig`를 직접 로딩하면서 `application.properties`를 로딩하지 않아 `@Value("${jdbc.maxTotal}")`가 숫자로 변환되지 못하는 구조입니다.

## 우선순위

- P0: 즉시 수정해야 하는 보안/배포 차단 이슈
- P1: 운영 장애 또는 데이터 노출 가능성이 큰 이슈
- P2: 유지보수성, 안정성, 품질을 낮추는 이슈

## P0 수정 항목

### 1. `.env`가 클래스패스와 배포물에 포함될 수 있음

근거:

- `src/main/java/com/jobmoa/app/config/RootConfig.java:56`에서 `@PropertySource(value = "classpath:.env", ignoreResourceNotFound = true)` 사용
- `CLAUDE.md:68`, `CLAUDE.md:139`에서 실제 credential을 `src/main/resources/.env`에 두는 구조로 안내
- `target/classes/.env` 파일 존재 확인
- `.gitignore:43`으로 Git 추적은 막고 있으나 Maven resources 복사는 막지 못함

위험:

- Git에는 안 올라가더라도 WAR/JAR 또는 서버 배포 디렉터리에 DB, Redis, Mail, API Key가 포함될 수 있습니다.
- 이미 배포된 산출물이 외부로 공유됐다면 credential 회전이 필요합니다.

수정 방향:

- 실제 비밀값은 `src/main/resources` 밖으로 이동합니다.
- 운영은 OS 환경변수, 서버 secret store, CI/CD secret으로 주입합니다.
- `pom.xml` resources 설정에서 `.env`, `.env.*`를 명시적으로 제외합니다.
- `RootConfig`의 `classpath:.env` 의존을 제거하거나 로컬 개발 전용 프로파일에서만 외부 파일 경로로 로드합니다.

### 2. 전역 로그인 인터셉터가 주요 API와 `.do` 요청을 통째로 제외함

근거:

- `src/main/java/com/jobmoa/app/CounselMain/biz/interceptor/LoginInterceptor.java:20` `/api/**`
- `src/main/java/com/jobmoa/app/CounselMain/biz/interceptor/LoginInterceptor.java:21` `*.api`
- `src/main/java/com/jobmoa/app/CounselMain/biz/interceptor/LoginInterceptor.java:22` `/**/*.api`
- `src/main/java/com/jobmoa/app/CounselMain/biz/interceptor/LoginInterceptor.java:23` `/**/*.do`
- 실제 검사 로직은 `LoginInterceptor.java:64`의 `JOBMOA_LOGIN_DATA` 존재 여부

위험:

- `.api`, `/api/**`, `.do` 기반 컨트롤러는 인터셉터의 세션 검사를 받지 않습니다.
- 일부 컨트롤러가 자체 권한 체크를 하더라도 전체 정책이 일관되지 않아 새 API 추가 시 인증 누락이 반복됩니다.

수정 방향:

- 제외 목록은 `/login.do`, 정적 리소스, 명확히 공개된 `/jobinfo/**`, `/schedulePublic/**` 정도로 축소합니다.
- `/api/**`, `*.api`, `/**/*.api`, `/**/*.do` 같은 광범위 패턴은 제거합니다.
- `WebMvcConfig`와 `LoginInterceptor` 양쪽에 흩어진 제외 목록을 하나로 통합합니다.
- 인증 필요/공개 API 목록을 테스트로 고정합니다.

### 3. 비밀번호 변경 API가 인증 및 검증 흐름과 분리되어 있음

근거:

- `src/main/java/com/jobmoa/app/CounselMain/view/login/LoginAsyncController.java:20` `changePW.api`
- `LoginAsyncController.java:26`에서 사용자 ID, 비밀번호, 변경 비밀번호를 로그로 출력
- `LoginAsyncController.java:42`에서 `changePassword` 실행
- `src/main/resources/mappings/Member-mapping.xml:33` `changePassword`
- `Member-mapping.xml:36` 비밀번호 직접 업데이트
- `Member-mapping.xml:37` 조건은 아이디뿐임

위험:

- 인증 코드 검증 성공 여부와 비밀번호 변경이 서버 상태로 연결되어 있지 않습니다.
- 비밀번호가 로그에 남고, DB도 평문 비밀번호 비교/저장 구조입니다.
- 계정 ID를 알면 비밀번호 변경 시도 표면이 큽니다.

수정 방향:

- 비밀번호 변경은 로그인 세션 또는 Redis에 저장된 검증 완료 토큰을 요구하도록 묶습니다.
- DB에는 `BCryptPasswordEncoder` 같은 단방향 해시만 저장합니다.
- 로그인 쿼리도 평문 비교 대신 ID 조회 후 `PasswordEncoder.matches()`로 변경합니다.
- 비밀번호, 인증번호, DTO 전체 출력 로그를 제거하거나 마스킹합니다.

### 4. AI/챗봇 API가 인증 없이 외부 API 비용과 개인정보 처리를 유발할 수 있음

근거:

- `src/main/java/com/jobmoa/app/CounselMain/view/chatBot/chatAjax.java:17` `/api`
- `chatAjax.java:19` 요청 객체 로그 출력
- `src/main/java/com/jobmoa/app/CounselMain/view/participant/recommendAjax/recommendAjaxController.java:20` `/api/recommend`
- `recommendAjaxController.java:32` `/detail`은 `HttpSession` 체크 없이 `getRecommendDetailResponse()` 호출
- `recommendAjaxController.java:80` `/saveRecommendAI`
- `recommendAjaxController.java:90-92` 세션이 없어도 `memberUserID`만 `null`로 두고 계속 진행
- `recommendAjaxController.java:126`에서 `processAndSaveRecommend()` 실행

위험:

- 로그인 없이 참여자 추천 상세, AI 추천 생성, 챗봇 호출이 가능할 수 있습니다.
- 외부 API 비용, 개인정보 처리, DB 저장 작업이 비인증 요청으로 발생할 수 있습니다.

수정 방향:

- `/api`, `/api/recommend/**`는 로그인 세션 필수로 전환합니다.
- 참여자 구직번호 접근 권한을 상담사/지점/관리자 기준으로 검증합니다.
- AI 호출은 사용자별/전체 rate limit과 감사 로그를 적용합니다.

## P1 수정 항목

### 5. MyBatis `${}` 직접 치환으로 SQL Injection 위험이 있음

근거:

- `src/main/resources/mappings/Education-mapping.xml:25` `구직번호 = ${educationJobNo}`
- `src/main/resources/mappings/Education-mapping.xml:27` `직업훈련 = ${education}`
- `src/main/resources/mappings/Education-mapping.xml:36` `구직번호 = ${educationJobNo}`
- `src/main/resources/mappings/Particcertif-mapping.xml:25`, `Particcertif-mapping.xml:27`에도 주석 처리된 위험 패턴 존재

위험:

- `${}`는 PreparedStatement 바인딩이 아니라 문자열 삽입입니다.
- 사용하지 않는 쿼리라도 나중에 재활성화되면 즉시 취약점이 됩니다.

수정 방향:

- 모든 사용자 입력은 `#{}`로 변경합니다.
- 사용하지 않는 위험 쿼리는 주석 보관 대신 삭제합니다.
- `rg "\$\{" src/main/resources/mappings`를 CI 체크로 추가합니다. 단, `#{array[${index}]}` 패턴은 별도 리팩터링 대상으로 분리합니다.

### 6. WebSocket Origin과 인증 경계가 열려 있음

근거:

- `src/main/java/com/jobmoa/app/config/WebSocketConfig.java:22` `ALLOWED_ORIGINS_WILDCARD = "*"`
- `WebSocketConfig.java:50`, `WebSocketConfig.java:55`에서 wildcard origin 허용
- `LoginInterceptor.java:20-23`의 광범위 API 제외와 `WebMvcConfig.java`의 `/ws/**`, `/ws-notification/**` 제외

위험:

- 운영 환경에서 임의 Origin의 SockJS/STOMP 연결이 가능할 수 있습니다.
- 구독 destination 단위 권한 검증이 보이지 않습니다.

수정 방향:

- 운영 도메인 allowlist로 제한합니다.
- STOMP `ChannelInterceptor`에서 세션/사용자 권한을 확인합니다.
- `/topic/.../{userId}` 형태의 개인 알림 destination은 본인 구독만 허용합니다.

### 7. `.\mvnw.cmd test`가 실패하며 테스트 환경이 재현 불가능함

근거:

- 테스트 결과: `Tests run: 13, Failures: 0, Errors: 13, Skipped: 0`
- 주요 오류: `Failed to convert value of type 'java.lang.String' to required type 'int'; For input string: "${jdbc.maxTotal}"`
- `src/test/java/com/jobmoa/app/config/RootConfigTest.java:22` `@SpringJUnitConfig(classes = RootConfig.class)`
- `src/test/java/com/jobmoa/app/config/IntegrationTest.java:16` `@SpringJUnitConfig(classes = {RootConfig.class, WebMvcConfig.class, WebSocketConfig.class})`
- `src/main/resources/application.properties:10`에는 `jdbc.maxTotal=20`이 있지만 테스트 구성에서 로드되지 않음

루트 원인:

- 테스트가 Spring Boot 애플리케이션 컨텍스트가 아니라 Java Config를 직접 올리고 있습니다.
- 이 경로에서는 `application.properties`가 자동 로딩되지 않아 `@Value` placeholder가 해소되지 않습니다.

수정 방향:

- 설정 통합 테스트는 `@SpringBootTest(classes = TestProjectApplication.class)` 또는 `@TestPropertySource`로 필요한 속성을 명시합니다.
- DB/Redis/Mail/Gemini는 단위 테스트에서 mock/fake로 분리합니다.
- 실제 외부 리소스 연결 테스트는 `integration` 프로파일 또는 Testcontainers 기반으로 분리합니다.

### 8. XML 파서가 XXE 방어 설정 없이 외부 XML을 파싱함

근거:

- `src/main/java/com/jobmoa/app/recruitmentFormation/biz/RecruitmentServiceImpl.java:20` `DocumentBuilderFactory`
- `RecruitmentServiceImpl.java:492-494`에서 `DocumentBuilderFactory.newInstance().newDocumentBuilder()` 직접 사용
- 입력은 고용24 외부 API 응답

위험:

- 외부 XML 파싱 시 DTD, external entity 차단 설정이 없습니다.

수정 방향:

- `DocumentBuilderFactory`에 `disallow-doctype-decl`, external entities 비활성화, XInclude 비활성화, entity expansion 제한을 설정합니다.
- XML 크기 제한과 파싱 실패 시 응답 본문 로그 길이 제한을 유지합니다.

### 9. 외부 HTTP 호출에 timeout과 취소 정책이 부족함

근거:

- `RootConfig.java:334-335` `new RestTemplate()`
- `RecruitmentServiceImpl.java:63` 서비스 내부에서 `new RestTemplate()`
- `RecruitmentServiceImpl.java:309` `restTemplate.exchange(...)`
- `ChatBotFunction.java:19` `new OkHttpClient()`
- `ChatBotFunction.java:25` `execute()`
- `ChatBotService.java:117`, `ChatBotService.java:141` 무한 루프와 `Thread.sleep(4000)`

위험:

- 외부 API 지연 시 요청 스레드 또는 스케줄러가 장시간 블로킹될 수 있습니다.
- 챗봇 run 상태 polling에 최대 대기 시간이 없어 장애 시 요청이 끝나지 않을 수 있습니다.

수정 방향:

- `RestTemplateBuilder` 또는 `ClientHttpRequestFactory`로 connect/read timeout을 설정합니다.
- OkHttp는 connect/read/write/call timeout을 설정합니다.
- polling은 최대 시도 횟수와 실패 응답을 명확히 둡니다.

## P2 수정 항목

### 10. Maven 의존성 버전 관리가 Spring Boot BOM과 충돌하기 쉬움

근거:

- `pom.xml:8` Spring Boot `3.4.1`
- `pom.xml:42-74` Spring Framework 모듈을 `6.2.1`, `6.2.8`, `6.2.10` 등으로 개별 지정
- `pom.xml:154-168` Jackson을 `2.15.2`로 직접 고정
- `pom.xml:126-127` `mybatis-spring 2.1.0`
- `pom.xml:113-114` `mssql-jdbc 12.9.0.jre11-preview`
- `pom.xml:219-221` JUnit 5와 `pom.xml:247-248` JUnit 4가 함께 존재

위험:

- Boot가 검증한 dependency set에서 벗어나 런타임 호환성 문제가 생길 수 있습니다.
- Spring 6 기반 프로젝트에서 MyBatis-Spring 2.x는 우선 호환성 재검증이 필요합니다.

수정 방향:

- Spring, Jackson, JUnit 버전은 가능한 Spring Boot dependency management에 맡깁니다.
- MyBatis-Spring은 Spring 6 호환 라인으로 정리합니다.
- preview JDBC driver는 운영용 stable 버전으로 교체합니다.

### 11. Boot 실행과 JSP 구성 설명이 충돌할 수 있음

근거:

- `CLAUDE.md:11`은 `./mvnw spring-boot:run` 로컬 실행 안내
- `pom.xml:86` `tomcat-embed-jasper`가 주석 처리됨
- JSP view는 `/WEB-INF/...` 기반으로 사용

위험:

- 외부 WAS 배포는 동작하더라도 `spring-boot:run`에서 JSP 렌더링이 깨질 수 있습니다.

수정 방향:

- 실행 모드를 하나로 정합니다.
- 내장 Tomcat 실행을 지원하려면 Jasper 의존성을 명시하고 로컬 실행 검증을 추가합니다.
- 외부 Tomcat WAR 전용이라면 문서와 Maven scope를 그 기준으로 정리합니다.

### 12. 문서/산출물 버전과 애플리케이션 이름이 어긋남

근거:

- `pom.xml:13` `1.5.0-SNAPSHOT`
- `CLAUDE.md:24` `target/JobmoaProject-1.4.0-SNAPSHOT.war`
- `src/main/resources/application.properties:1` `spring.application.name=TestProject`
- `target/JobmoaProject-1.4.0-SNAPSHOT` 디렉터리 존재

위험:

- 배포 산출물 확인, 장애 로그 식별, 문서 신뢰도가 떨어집니다.

수정 방향:

- `CLAUDE.md`, `pom.xml`, `spring.application.name`, 배포 스크립트의 버전/이름을 맞춥니다.
- `target/`은 빌드 산출물로 두고 문서 기준으로 사용하지 않습니다.

### 13. 수동 스레드와 스케줄러 동시 실행 제어가 부족함

근거:

- `RecruitmentScheduler.java:50-51` 앱 시작 시 `new Thread`와 `Thread.sleep(3000)`
- `RecruitmentScheduler.java:71` 하루 4회 스케줄
- `RecruitmentServiceImpl.java:52`, `RecruitmentServiceImpl.java:55` 1회 최대 60,000건 수집/상세 처리
- `RecruitmentServiceImpl.java:169` 처리 중 `Thread.sleep(1000)`

위험:

- 다중 인스턴스 배포 시 동일 동기화가 중복 실행될 수 있습니다.
- 시작 직후 스레드가 Spring lifecycle, shutdown, tracing 관리 밖에서 돕니다.

수정 방향:

- `TaskExecutor`, `@Async`, 조건 기반 시작 로직으로 전환합니다.
- 다중 노드면 DB lock 또는 ShedLock 같은 스케줄 락을 둡니다.
- 외부 API 동기화는 페이지/상세 처리 단위를 나누고 진행 상태를 저장합니다.

### 14. 민감 데이터와 대형 프롬프트 로그가 과다함

근거:

- `LoginController.java:61` 로그인 DTO 로그
- `LoginAsyncController.java:26` 비밀번호 로그
- `AsyncMyPage.java:87` 비밀번호 로그
- `AsyncMailSend.java:101` 인증번호 포함 메일 본문 로그
- `GeminiApiService.java:36`, `GeminiApiService.java:38`, `GeminiApiService.java:147`, `GeminiApiService.java:149` 프롬프트/응답 `System.out`
- `chatAjax.java:19` 사용자 메시지 요청 객체 로그

위험:

- 로그에 개인정보, 비밀번호, 인증번호, AI 프롬프트/응답이 남습니다.
- 장애 분석 로그가 곧 개인정보 저장소가 될 수 있습니다.

수정 방향:

- 비밀번호, 인증번호, API 응답 원문, 참여자 개인정보는 로그 금지 또는 마스킹합니다.
- `System.out.println`은 `log.debug`로 변경하고 운영 프로파일에서는 비활성화합니다.
- 로그 보존 기간과 접근 권한을 정리합니다.

### 15. 필드 주입과 직접 생성 객체가 많아 테스트/교체가 어렵다

근거:

- 컨트롤러, 서비스, DAO 전반에 `@Autowired` 필드 주입 다수
- `WebMvcConfig.java:64`에서 `new LoginInterceptor()` 직접 생성
- `RecruitmentServiceImpl.java:63`, `ChatBotFunction.java:19`에서 HTTP 클라이언트 직접 생성

위험:

- 테스트에서 mock 주입이 어렵고, 공통 timeout/인증/로깅 정책을 적용하기 어렵습니다.

수정 방향:

- 신규/수정 코드부터 생성자 주입으로 전환합니다.
- Interceptor, HTTP client는 Bean으로 등록해 설정을 중앙화합니다.

## 권장 수정 순서

1. P0-1 `.env` 배포물 포함 제거 및 credential 회전 여부 판단
2. P0-2 `LoginInterceptor` 제외 패턴 축소
3. P0-3 비밀번호 변경/재설정 플로우 재설계
4. P0-4 AI/챗봇/추천 API 인증 및 권한 검사 추가
5. P1-5 MyBatis `${}` 제거
6. P1-7 테스트 환경 복구
7. P1-6, P1-8, P1-9 운영 안정성 보강
8. P2 항목은 위 수정 후 별도 정리

## 검증 기록

실행:

```powershell
.\mvnw.cmd test
```

결과:

- 실패
- `Tests run: 13, Failures: 0, Errors: 13, Skipped: 0`
- 주요 원인: `RootConfig` 직접 로딩 테스트에서 `application.properties`가 로드되지 않아 `${jdbc.maxTotal}` placeholder가 해소되지 않음

## 생성/수정 파일

- 생성: `docs/superpowers/2026-05-13-project-code-analysis.md`

