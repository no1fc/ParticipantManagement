# 상담 일정 관리 시스템 개발 계획

## Context

현재 상담사들이 상담 일정을 PPT로 수동 취합하여 공유/모니터 표시하고 있다. 이를 웹 기반 상담 일정 관리 시스템으로 구축하여 실시간 조회/등록/공유가 가능하도록 한다.

---

## 1. 페이지 구성 (3개)

| 페이지 | 접근 권한 | 주요 기능 |
|--------|----------|----------|
| **상담 일정 조회/등록** | 상담사 (로그인) | 본인 일정 CRUD, 참여자 검색/선택, 상담일지 연동 |
| **지점 일정 통합 조회** | 지점 관리자 (로그인) | 지점 내 전체 상담사 일정 조회, 상담사별 필터/색상 |
| **공개 일정 조회** | 누구나 (비로그인) | 지점 선택 + 접근코드 입력 후 일정 조회, 일자별 시간대 표시 |

---

## 2. 데이터베이스 설계

### 2.1 J_상담일정 (상담 스케줄 테이블)

```sql
CREATE TABLE J_상담일정 (
    일정PK           INT IDENTITY(1,1) PRIMARY KEY,
    지점             NVARCHAR(50)   NOT NULL,
    전담자_계정       NVARCHAR(50)   NOT NULL,
    전담자_이름       NVARCHAR(50)   NOT NULL,
    참여자_구직번호    INT            NULL,
    참여자_이름       NVARCHAR(50)   NULL,
    일정일시          DATETIME       NOT NULL,
    일정종료일시       DATETIME       NULL,
    일정유형          NVARCHAR(20)   NULL DEFAULT N'대면상담',
    메모             NVARCHAR(500)  NULL,
    등록일            DATETIME       NOT NULL DEFAULT GETDATE(),
    수정일            DATETIME       NULL
);

CREATE INDEX IDX_상담일정_전담자 ON J_상담일정 (전담자_계정, 지점);
CREATE INDEX IDX_상담일정_지점일시 ON J_상담일정 (지점, 일정일시);
```

### 2.2 J_지점접근코드 (공개 페이지 인증)

```sql
CREATE TABLE J_지점접근코드 (
    접근코드PK    INT IDENTITY(1,1) PRIMARY KEY,
    지점         NVARCHAR(50)   NOT NULL UNIQUE,
    접근코드      NVARCHAR(20)   NOT NULL,
    활성여부      BIT            NOT NULL DEFAULT 1,
    등록일        DATETIME       NOT NULL DEFAULT GETDATE()
);
```

---

## 3. 백엔드 구조

### 3.1 신규 파일

```
src/main/java/com/jobmoa/app/CounselMain/
├── biz/counselSchedule/
│   ├── CounselScheduleDTO.java        # 일정 DTO
│   ├── BranchAccessCodeDTO.java       # 접근코드 DTO
│   ├── CounselScheduleDAO.java        # MyBatis DAO (ns="CounselScheduleDAO.")
│   ├── CounselScheduleService.java    # 서비스 인터페이스
│   └── CounselScheduleServiceImpl.java # @Service("counselScheduleService")
└── view/counselSchedule/
    ├── CounselScheduleController.java       # @Controller - 페이지 1,2
    ├── CounselScheduleApiController.java    # @RestController - CRUD API
    └── CounselSchedulePublicController.java # @Controller + @RestController 혼합 - 페이지 3

src/main/resources/mappings/
└── CounselSchedule-mapping.xml   # MyBatis 매퍼 (T-SQL)
```

### 3.2 API 엔드포인트

**인증 필요 (상담사)**
| Method | URL | 설명 |
|--------|-----|------|
| GET | `/counselSchedule` | 상담사 일정 페이지 |
| GET | `/counselScheduleBranch` | 지점 관리자 페이지 |
| GET | `/counselSchedule/api/schedules` | 내 일정 조회 (startDate, endDate) |
| POST | `/counselSchedule/api/schedules` | 일정 등록 |
| PUT | `/counselSchedule/api/schedules/{id}` | 일정 수정 |
| DELETE | `/counselSchedule/api/schedules/{id}` | 일정 삭제 |
| GET | `/counselSchedule/api/participants` | 내 참여자 검색 |
| GET | `/counselSchedule/api/branch/schedules` | 지점 전체 일정 (관리자) |
| GET | `/counselSchedule/api/branch/counselors` | 지점 상담사 목록 |

**비인증 (공개)**
| Method | URL | 설명 |
|--------|-----|------|
| GET | `/schedulePublic` | 공개 조회 페이지 |
| GET | `/schedulePublic/api/branches` | 지점 목록 |
| POST | `/schedulePublic/api/verify` | 접근코드 검증 |
| GET | `/schedulePublic/api/schedules` | 지점 일정 조회 |

### 3.3 수정할 기존 파일

| 파일 | 변경 내용 |
|------|----------|
| `config/RootConfig.java` | `@ComponentScan`에 `biz.counselSchedule` 추가 |
| `config/WebMvcConfig.java` | `@ComponentScan`에 `view.counselSchedule` 추가 + `/schedulePublic/**` 제외 |
| `biz/interceptor/LoginInterceptor.java` | `/schedulePublic/**` 제외 패턴 추가 |
| `resources/sql-map-config.xml` | typeAlias + mapper 등록 |
| `WEB-INF/tags/gnb.tag` | 사이드바에 "상담 일정" 메뉴 추가 |

---

## 4. 프론트엔드 구조

### 4.1 신규 파일

```
src/main/webapp/
├── WEB-INF/views/
│   ├── CounselSchedulePage.jsp         # 페이지 1: 상담사 일정
│   └── CounselScheduleBranchPage.jsp   # 페이지 2: 관리자 통합 조회
├── WEB-INF/external/
│   └── CounselSchedulePublicPage.jsp   # 페이지 3: 공개 조회
├── js/
│   ├── counselScheduleCalendar_0.0.1.js  # 페이지 1 JS
│   ├── counselScheduleBranch_0.0.1.js    # 페이지 2 JS
│   └── counselSchedulePublic_0.0.1.js    # 페이지 3 JS
└── css/participantCss/
    └── counselSchedule_0.0.1.css         # 공통 CSS
```

### 4.2 FullCalendar 6.x CDN

```html
<link href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.js"></script>
```

---

## 5. 페이지별 상세 UI 설계

### 5.1 페이지 1 - 상담사 일정 (CounselSchedulePage)

- **레이아웃**: AdminLTE 기반, gnb.tag 포함
- **달력**: FullCalendar `dayGridMonth` / `timeGridWeek` / `timeGridDay` 뷰
- **일정 등록 모달** (날짜 클릭 또는 등록 버튼):
  - 날짜/시간 선택 (시: 09~18, 분: 00/10/20/30/40/50)
  - 참여자 검색 (이름 검색 → 자동완성 → 이름+구직번호 표시)
  - 일정 유형 (대면상담/전화상담/화상상담/기타)
  - 메모 (500자)
- **일정 클릭 모달**:
  - 상세 정보 표시
  - "상담일지 작성" → `/UpdateCounselPage?구직번호={참여자_구직번호}` 이동
  - "수정" → 수정 폼
  - "삭제" → SweetAlert 확인 후 삭제

### 5.2 페이지 2 - 관리자 통합 조회 (CounselScheduleBranchPage)

- **상단 필터**: 상담사 체크박스 목록 (전체 선택 기본)
- **색상 범례**: 상담사별 자동 배정 색상
- **달력**: FullCalendar, 모든 상담사 일정 동시 표시
- **이벤트 클릭**: 읽기 전용 상세 팝업

### 5.3 페이지 3 - 공개 조회 (CounselSchedulePublicPage)

- **gnb 없음**, Jobmoa 로고 + 독립 레이아웃
- **1단계**: 지점 선택 드롭다운 + 접근코드 입력 + "조회" 버튼
- **2단계** (인증 성공 후):
  - FullCalendar 월간 달력
  - 날짜 클릭 → 시간대별 테이블 (행: 09:00~18:00, 열: 상담사)
  - 각 셀: 참여자명(마스킹) + 일정유형
  - 완전 읽기 전용

---

## 6. 보안 고려사항

- 상담사 API: 세션의 `loginBean.getMemberUserID()`를 SQL WHERE 조건에 필수 포함
- 수정/삭제: `WHERE 일정PK = #{id} AND 전담자_계정 = #{counselorId}` 소유권 검증
- 관리자: `memberISManager == true` 검증 + 같은 지점만 조회
- 공개 페이지: 접근코드 검증 후 세션에 인증 지점 저장, 이후 API에서 확인
- 공개 페이지 참여자명: 첫 글자만 표시 (예: "김**")

---

## 7. 구현 단계

### Phase 0: 프로토타입 (현재 - 백엔드 없이)
1. 정적 HTML 프로토타입 3페이지 생성 (`src/main/webapp/prototype/`)
2. FullCalendar CDN + 더미 데이터로 UI 검증

### Phase 1: DB + 백엔드 기반
1. DB 테이블 생성
2. DTO, DAO, Service, Mapper 생성
3. `RootConfig`, `sql-map-config.xml` 등록

### Phase 2: 페이지 1 - 상담사 일정
1. Controller + ApiController 생성
2. `WebMvcConfig` 등록
3. JSP + JS + CSS 생성
4. CRUD 테스트

### Phase 3: 페이지 2 - 관리자 조회
1. 관리자용 API 추가
2. JSP + JS 생성
3. 색상 배정 및 필터 테스트

### Phase 4: 페이지 3 - 공개 조회
1. Public Controller 생성
2. LoginInterceptor 제외 패턴 추가
3. JSP + JS 생성
4. 접근코드 검증 테스트

### Phase 5: 통합
1. gnb.tag 사이드바 메뉴 추가
2. 상담일지 연동 링크
3. 크로스브라우저 및 모바일 테스트

---

## 8. 검증 방법

1. `./mvnw spring-boot:run` 후 `http://localhost:8088/counselSchedule` 접속
2. 일정 등록 → DB 확인 → 달력 표시 확인
3. 관리자 계정으로 `/counselScheduleBranch` 접속 → 다른 상담사 일정 표시 확인
4. 비로그인 상태로 `/schedulePublic` 접속 → 접근코드 입력 → 일정 조회 확인
5. 일정 클릭 → 상담일지 페이지 이동 확인

---

## 9. 프로토타입 파일 (Phase 0에서 생성)

백엔드 없이 바로 브라우저에서 확인 가능한 정적 HTML:

```
src/main/webapp/prototype/
├── schedule-counselor.html   # 페이지 1 프로토타입
├── schedule-manager.html     # 페이지 2 프로토타입
└── schedule-public.html      # 페이지 3 프로토타입
```

FullCalendar CDN + 하드코딩 더미 데이터로 동작하며, 실제 개발 시 JSP로 전환한다.
