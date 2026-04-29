# AI_Function_1Week_Progress.md — 1주차: 요구사항 정리 및 기존 구조 분석

> **기간:** 2026-04-03 ~ 2026-04-10  
> **상태:** ✅ 진행중  
> **목표:** PRD 문서 기반 작업 범위 확정 및 기존 코드 구조 전체 파악

---

## 1. 이번 주 목표

기존 참여자관리 시스템의 코드 흐름을 완전히 파악하고,  
이번 개발에서 수정/추가가 필요한 파일 목록과 영향 범위를 확정한다.

---

## 2. 세부 작업 목록

### 2-1. PRD 검토 및 작업 범위 확정

- [x] `src/Docs_md/AI_Function_PRD.md` 전체 정독
- [ ] 기능 요구사항 항목별 개발 가능 여부 검토
  - 집중 알선 여부 컬럼 추가 가능 여부
  - 추천채용정보 저장 테이블 신규 생성 가능 여부
  - Gemini API 연동 가능 여부 (API Key 발급 여부 확인)
- [ ] 이번 범위(P0/P1)와 제외 범위(P2/카카오톡) 팀 내 재확인
- [ ] 개발 착수 전 질의사항 목록 작성

---

### 2-2. 기존 참여자관리 컨트롤러 분석

- [x] `src/main/java/com/jobmoa/app/CounselMain/view/` 디렉토리 탐색
  - 참여자 관련 Controller 파일명 확인
  - 참여자 조회/검색 엔드포인트 URL 목록 정리
  - 요청 파라미터 구조 파악 (검색 조건 Bean 확인)
  - 응답 방식 확인 (JSP forward / JSON 응답 분기)
- [x] 확인 항목 메모:
  - [x] 참여자 리스트 조회 URL: `GET /participant.login`
    - 컨트롤러: `view/participant/ParticipantController.java`
    - 메서드: `participantPageController()`
  - [x] 참여자 검색 조건 파라미터 Bean 클래스명: `ParticipantDTO`
    - 경로: `biz/participant/ParticipantDTO.java`
    - 검색에 사용하는 필드: `search`, `searchOption`, `searchType`, `endDateOption`, `participantPartType`, `participantInItCons`, `searchStartDate`, `searchEndDate`, `column`, `order`, `page`, `pageRows`
  - [x] 참여자 결과 DTO 클래스명: `ParticipantDTO` (검색 조건과 동일 클래스 사용)
  - [x] 응답 방식: JSP forward → `views/participantMain` (`WEB-INF/views/participantMain.jsp`)
  - [x] model attribute 키: `datas` (참여자 리스트), `totalCount`, `page`, `startButton`, `endButton`, `totalButton`

---

### 2-3. 기존 참여자관리 Service/DAO 분석

- [x] `src/main/java/com/jobmoa/app/CounselMain/biz/` 디렉토리 탐색
  - 참여자 관련 Service 인터페이스 및 Impl 파일명 확인
  - 참여자 관련 DAO 파일명 확인
  - DAO에서 사용하는 SqlSession namespace 확인
- [x] 확인 항목 메모:
  - [x] 참여자 Service 인터페이스명: `ParticipantService` (`biz/participant/ParticipantService.java`)
  - [x] 참여자 ServiceImpl명: `ParticipantServiceImpl` (Spring Bean name: `"participant"`)
    - 경로: `biz/participant/ParticipantServiceImpl.java`
    - 특이사항: `@EnableScheduling` 적용됨, 취소자 백업 스케줄러 내장 (`매일 00:00`)
    - `sanitizeSearchTerm()` 메서드로 특수문자 제거 처리 포함
  - [x] 참여자 DAO명: `ParticipantDAO` (`biz/participant/ParticipantDAO.java`)
  - [x] SqlSession namespace 값: `"ParticipantDAO"` (XML mapper namespace와 동일)
  - [x] DAO 패턴: `participantCondition` 필드값으로 queryId를 동적으로 결정하는 방식
    - 예: `participantDTO.setParticipantCondition("selectAllParticipantBasic")` → `sqlSession.selectList("ParticipantDAO.selectAllParticipantBasic", ...)`

---

### 2-4. 기존 MyBatis Mapper XML 분석

- [x] `src/main/resources/mappings/` 디렉토리 탐색
  - 참여자 관련 mapper XML 파일명 확인
  - 현재 검색 쿼리 `SELECT` 문 분석
  - MSSQL T-SQL 특이사항 확인
  - 참여자 INSERT/UPDATE 쿼리 위치 확인
- [x] 확인 항목 메모:
  - [x] 참여자 mapper XML 파일명: `Participant-mapping.xml`
    - namespace: `ParticipantDAO`
  - [x] 참여자 리스트 조회 queryId: `selectAllParticipantBasic`
  - [x] 참여자 건수 조회 queryId: `selectCountParticipant`
  - [x] 현재 검색 조건 파라미터 목록 (fragment sql):
    - `participantSearchCondition` — `searchStatus`, `searchOption`, `search`, `searchStartDate`, `searchEndDate`
      - 검색 옵션: `참여자`, `구직번호`, `진행단계`, `최근상담일`, `전담자`, `알선`
    - `participantSearchFiterCondition` — `participantInItCons`(년도), `participantPartType`(1/2유형), `endDateOption`(마감여부), `searchType`(noInitial/recent21/jobExpire/periodExpire/employment)
    - `participantAuthCondition` — `participantBranch`, `participantUserid`, `searchPath`
  - [x] 페이징 방식: `ROW_NUMBER() OVER (ORDER BY ...)` 서브쿼리 방식
    - 파라미터: `startPage`, `endPage`, `pageRows`
    - `WHERE SUB_QUERY.RowNum > #{startPage} AND #{endPage} >= SUB_QUERY.RowNum`
  - [x] 기타 특이사항:
    - 엑셀 다운로드용 전체 조회: `participantExcel` queryId
    - 전담자 변경: `transferSelect`, `transferUpdate`
    - 취소자 백업: `participantBackupOldCancelled`, `participantDeleteOldCancelled`

---

### 2-5. 참여자관리 JSP 화면 분석

- [x] `src/main/webapp/WEB-INF/` 디렉토리 탐색
  - 참여자관리 JSP 파일명 및 위치 확인
  - 검색 폼 구조 파악
  - 리스트 테이블 구조 파악
  - JS/CSS 파일 분리 여부 확인
- [x] 확인 항목 메모:
  - [x] 참여자관리 JSP 경로: `src/main/webapp/WEB-INF/views/participantMain.jsp`
  - [x] 검색 폼 구조: **별도 태그 파일로 분리됨**
    - 태그 파일: `src/main/webapp/WEB-INF/tags/pariticipantSearchForm.tag`
    - JSP에서 `<mytag:pariticipantSearchForm actionURL="/participant.login"/>` 형태로 사용
    - 검색 폼 action URL: `/participant.login` (GET 방식)
  - [x] 검색 폼 input name 목록:
    - `page` (hidden), `endDateOption`, `participantInItCons`, `participantPartType`
    - `searchOption`, `search`, `pageRows`
    - `searchType` (radio: `""`, `noInitial`, `recent21`, `jobExpire`, `periodExpire`, `employment`)
  - [x] 리스트 테이블 컬럼 목록: `삭제`, `연번`, (참여자명, 진행단계 등 — 추가 확인 필요)
    - 테이블은 `participantMain.jsp` 내에 직접 정의됨
  - [x] 연동 JS 파일 경로:
    - `/js/participant_main_0.0.3.js` (참여자 조회 메인 JS)
    - `/js/participant_excel_download_0.0.1.js` (엑셀 다운)
    - `/js/paginationJS_0.0.1.js` (페이지네이션)
    - `/js/selectOptionJS_0.0.1.js` (셀렉트 옵션)
    - `/js/sweetAlert_0.0.1.js` (알림창)
  - [x] 연동 CSS 파일 경로:
    - `/css/participantCss/participantMain_0.0.1.css`
    - `/css/participantCss/custom-modern_0.0.1.css`
    - `/css/participantCss/dailyWorkReportCss_0.0.1.css`

---

### 2-6. 참여자 DB 테이블 구조 확인

- [x] 코드(mapper XML, DTO) 분석으로 컬럼 구조 파악 완료
- [ ] MSSQL에서 실제 테이블 컬럼 목록 직접 조회로 재확인 (DB 접속 필요)
  ```sql
  SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE, COLUMN_DEFAULT
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_NAME = 'J_참여자관리'
  ORDER BY ORDINAL_POSITION;
  ```
- [x] 코드 분석 기반 확인 항목:
  - [x] 테이블명: `J_참여자관리`
  - [x] 구직번호 컬럼명: `구직번호` (int, PK)
  - [x] 진행단계 컬럼명: `진행단계` (VARCHAR)
  - [x] 학력 컬럼명: `학력` (Basic-mapping.xml 확인, `basicEducation` alias)
  - [x] 전공 컬럼명: `전공` (`participantSpecialty` alias)
  - [x] 카테고리 대분류 컬럼명: `직무_카테고리_대` (`jobCategoryLarge` alias)
  - [x] 카테고리 중분류 컬럼명: `직무_카테고리_중` (`jobCategoryMid` alias)
  - [x] 카테고리 소분류 컬럼명: `직무_카테고리_소` (`jobCategorySub` alias) ← DB 컬럼 존재 여부 직접 확인 필요
  - [x] 희망직무 컬럼명: `희망직무` (`participantJobWant` alias)
  - [x] 알선상세정보 구조:
    - **별도 테이블** `J_참여자관리_알선상세정보`에 저장
    - 컬럼: `구직번호`, `상세정보`, `추천사`, `등록일`, `수정일`, `등록번호`(PK)
    - `J_참여자관리`와 LEFT JOIN으로 조회 (`B.상세정보 AS placementDetail`)
    - DTO 필드: `placementDetail` (상세정보), `suggestionDetail` (추천사)
  - [x] 집중알선여부 컬럼 존재 여부: **없음** (신규 추가 필요)
  - [x] 관련 테이블 목록 (코드 내 확인):
    - `J_참여자관리` — 기본 참여자 정보
    - `J_참여자관리_로그인정보` — 전담자(상담사) 계정 정보
    - `J_참여자관리_알선상세정보` — 알선 상세 정보 (별도 테이블)
    - `J_참여자관리_자격증` — 자격증 정보
    - `J_참여자관리_직업훈련` — 직업훈련 정보
    - `J_참여자관리_취소자백업` — 취소자 백업
    - `J_참여자관리_이력서요청양식` — 이력서 요청
    - `J_참여자관리_JOB_POSTING` — 고용24 채용공고 (별도 패키지)

---

### 2-7. 고용24 채용정보 테이블 구조 확인

- [x] 채용정보 테이블 및 스케줄러 소스 위치 확인 완료
- [x] 확인 항목 메모:
  - [x] 채용정보 테이블명: `J_참여자관리_JOB_POSTING`
  - [x] 구인인증번호 컬럼명: `wanted_auth_no` (`wantedAuthNo` alias)
  - [x] 기업명 컬럼명: `company_nm` (`compNm` alias)
  - [x] 채용제목 컬럼명: `recrut_title` (`recrutTitle` alias)
  - [x] 업종 컬럼명: `jobs_nm` (`occNm` alias)
  - [x] 채용공고 URL 컬럼명: `wanted_info_url` (`wantedInfoUrl` alias)
  - [x] 활성 여부 컬럼: `is_active` (1=활성, 0=비활성/마감)
  - [x] 스케줄러 클래스 경로: `recruitmentFormation/biz/RecruitmentScheduler.java`
    - 패키지: `com.jobmoa.app.recruitmentFormation.biz`
    - 실행 주기: `0 0 0,6,12,18 * * *` (00:00, 06:00, 12:00, 18:00)
    - 동작: 고용24 API 호출 → UPSERT → 마감 공고 `is_active = 0` 처리
  - [x] 채용공고 mapper: `Recruitment-mapping.xml` (namespace: `RecruitmentDAO`)
  - [x] DTO: `RecruitmentPostingDTO`, `RecruitmentSearchDTO`, `RecruitmentResultDTO$JobItem`
  - [x] typeAlias: `recruitmentSearch`, `jobItem` (sql-map-config.xml 확인)

---

### 2-8. Spring 설정 확인

- [x] `src/main/resources/sql-map-config.xml` 확인 완료
  - [x] typeAlias 등록 방식: 직접 `<typeAlias>` 태그 선언 방식
    - 현재 등록된 alias 목록: `member`, `participant`, `basic`, `counsel`, `employment`, `particcertif`, `education`, `dashboard`, `report`, `pra`, `praHistoryRequest`, `praDailyReport`, `praScoringConfigHistory`, `praCsvHistory`, `arrangement`, `admin`, `jobPlacement`, `recruitmentPosting`, `recruitmentSearch`, `jobItem`
    - **신규 추천 Bean 등록 필요**: `J_참여자관리_참여자추천채용정보` 저장용 Bean의 alias 추가
  - [x] mapper 등록 방식: `<mapper resource="mappings/[파일명]"/>` 직접 등록 방식
    - **신규 추천 mapper XML 등록 필요**
- [x] Transaction AOP Pointcut 확인 완료
  - 파일: `biz/common/PointCutConfig.java`
  - 현재 pointcut: `execution(* com.jobmoa.app.CounselMain.biz.*.*Impl.*(..))`
  - **문제**: `biz.` 바로 하위의 단일 레벨 패키지만 적용됨
  - **신규 `recommend` 하위 패키지는 적용 안 됨** → 3주차에 `..` 변경 필요
    ```
    변경 전: execution(* com.jobmoa.app.CounselMain.biz.*.*Impl.*(..))
    변경 후: execution(* com.jobmoa.app.CounselMain.biz..*.*Impl.*(..))
    ```

---

### 2-9. Gemini API 사전 준비

- [ ] Google AI Studio에서 Gemini API Key 발급 여부 확인
- [x] Gemini API 연동 방식 확인 완료
  - REST API 직접 호출 방식이 아닌 **Java SDK 의존성 추가 방식**으로 진행
  - 사용할 모델명: `gemini-2.5-flash-preview`
- [ ] `pom.xml` 의존성 추가 필요 여부 검토
  - `spring-web` RestTemplate 사용 가능 여부 (이미 포함 여부)
  - JSON 파싱: `jackson-databind` 포함 여부
- [x] Gemini Java SDK 연동 정보:
  - [x] pom.xml 추가 필요 의존성:
    ```xml
    <dependency>
      <groupId>com.google.genai</groupId>
      <artifactId>google-genai</artifactId>
      <version>1.0.0</version>
    </dependency>
    ```
  - [x] Java SDK 사용 예시:
    ```java
    import com.google.genai.Client;
    import com.google.genai.types.GenerateContentResponse;

    // API Key는 환경변수 GEMINI_API_KEY 또는 application.properties에서 주입
    Client client = new Client();
    GenerateContentResponse response =
        client.models.generateContent(
            "gemini-2.5-flash-preview",
            "프롬프트 텍스트",
            null);
    System.out.println(response.text());
    ```
  - [x] 사용할 모델명: `gemini-2.5-flash-preview`
  - [ ] API Key 발급 및 `application.properties` 등록 방식 결정
    - 옵션 A: 환경변수 `GEMINI_API_KEY` 설정
    - 옵션 B: `application.properties`에 `gemini.api.key=...` 직접 설정 후 `@Value` 주입

---

## 3. 산출물

| 산출물 | 내용 | 완료 여부 |
|--------|------|----------|
| 작업 범위 확정 메모 | 본 문서 2-1~2-9 항목 작성 | ✅ 완료 |
| 수정 필요 파일 목록 | 아래 섹션 4 작성 | ✅ 완료 |

---

## 4. 영향 범위 파일 목록 (1주차 분석 결과)

### 수정 대상 파일

| 파일 경로 | 수정 사유 |
|-----------|-----------|
| `src/main/resources/mappings/Participant-mapping.xml` | `participantSearchFiterCondition` fragment에 집중알선여부 조건 추가, SELECT에 집중알선여부 컬럼 추가 |
| `src/main/java/.../biz/participant/ParticipantDTO.java` | `집중알선여부` 필드 추가 |
| `src/main/java/.../biz/common/PointCutConfig.java` | pointcut 표현식 `biz.*` → `biz..*` 변경 (recommend 하위 패키지 Transaction 적용) |
| `src/main/webapp/WEB-INF/tags/pariticipantSearchForm.tag` | 집중알선여부 셀렉트박스 검색 필터 추가 |
| `src/main/webapp/WEB-INF/views/participantMain.jsp` | 리스트 테이블에 추천 버튼 컬럼 및 모달 마크업 추가 |
| `src/main/webapp/js/participant_main_0.0.3.js` | 모달 열기/닫기/AJAX 연동 JS 추가 |
| `src/main/resources/sql-map-config.xml` | 신규 추천 Bean typeAlias 등록, 신규 mapper XML 등록 |
| `src/main/resources/application.properties` | Gemini API Key 설정 추가 |
| `pom.xml` | `google-genai` SDK 의존성 추가 |

### 신규 생성 파일

| 파일 경로 | 설명 |
|-----------|------|
| `src/main/resources/mappings/ParticipantJobRecommend-mapping.xml` | 추천채용정보 CRUD mapper XML |
| `src/main/java/.../CounselMain/biz/recommend/ParticipantJobRecommendDTO.java` | 추천채용정보 저장/조회용 Bean |
| `src/main/java/.../CounselMain/biz/recommend/ParticipantJobRecommendDAO.java` | 추천채용정보 DAO |
| `src/main/java/.../CounselMain/biz/recommend/ParticipantJobRecommendService.java` | 추천 서비스 인터페이스 |
| `src/main/java/.../CounselMain/biz/recommend/ParticipantJobRecommendServiceImpl.java` | 추천 서비스 구현체 |
| `src/main/java/.../CounselMain/biz/recommend/GeminiApiService.java` | Gemini API 호출 서비스 |
| `src/main/webapp/css/participantCss/recommendModal_0.0.1.css` | 추천 모달 전용 CSS |

### DB 변경 대상

| 테이블명 | 변경 내용 |
|---------|-----------|
| `J_참여자관리` | `집중알선여부 CHAR(1) NOT NULL DEFAULT 'N'` 컬럼 추가 |
| `J_참여자관리_참여자추천채용정보` | 신규 테이블 생성 (AI 추천 결과 저장) |
| `J_참여자관리_희망직무` | 신규 테이블 생성 (희망직무 다중 저장 분리) |

---

## 5. 주요 질의사항 및 결정 사항

| # | 질의 내용 | 답변/결정                        | 확인일        |
|---|-----------|------------------------------|------------|
| 1 | Gemini API Key 발급 및 예산 승인 여부 | 무료 API Key로 발급 상태            | 2026-04-09 |
| 2 | `J_참여자관리` 테이블 직접 ALTER 가능 여부 (운영 DB 접근 권한) | 가능                           | 2026-04-09 |
| 3 | 진행단계 값 정확한 DB 저장값 확인 (`IAP후` / `미취업사후관리` 정확한 문자열) | IAP 후 / 미취업사후관리 문자           | 2026-04-09 |
| 4 | `직무_카테고리_소` 컬럼 `J_참여자관리` 테이블 존재 여부 직접 확인 | 직무_카테고리_소 컬럼이 있으나 사용은 안하는 중  | 2026-04-09 |
| 5 | 채용정보 후보군 최대 조회 건수 기준 결정 (Gemini 비용 vs 추천 품질) | 추천 품질                        | 2026-04-09 |
| 6 | `J_참여자관리_희망직무` 분리 테이블 작업 이번 범위 포함 여부 확정 | 2 Week에 포함하여 진행예정            | 2026-04-09 |
| 7 | Gemini API Key 관리 방식 결정 (환경변수 vs application.properties) | application.properties 관리 사용 | 2026-04-09 |

---

## 6. 변경 이력

| 날짜         | 버전   | 변경 내용                                               | 작성자 |
|------------|------|-----------------------------------------------------|-----|
| 2026-04-03 | v0.1 | 최초 작성                                               | SD  |
| 2026-04-03 | v0.2 | 실제 코드 분석 결과 반영 (2-2 ~ 2-9 전체 확인 항목 채움, 영향 범위 목록 완성) | SD  |
| 2026-04-09 | v0.3 | 주요 질의사항 및 결정 사항 기입                                  | SD  |