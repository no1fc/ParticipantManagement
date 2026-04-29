# 채용공고(고용24) 서비스 설계서

- **문서 버전:** 0.1 (Beta)
- **작성일:** 2026-03-24
- **프로젝트:** JobMoa — 국민취업지원제도 참여자 맞춤형 채용정보 서비스
- **작성 단계:** 1단계 (채용공고 통합 리스트 구현)

---

## 1. 서비스 개요

### 1.1 목적

국민취업지원제도 참여자(1유형·2유형)가 별도의 외부 사이트 탐색 없이 JobMoa 플랫폼 내에서 고용24의 검증된 채용 데이터를 조회·검색할 수 있도록 지원한다.

### 1.2 핵심 가치

| 항목 | 내용 |
|------|------|
| 서비스 대상 | 국민취업지원제도 1유형·2유형 참여자 |
| 정보 출처 | 고용24 Open API (워크넷 채용정보) |
| 접근 방식 | 로그인 불필요 공개 페이지 (`/jobinfo`) |
| 핵심 목표 | 조건 기반 채용공고 검색 및 원본 링크 연결 |

### 1.3 단계별 구축 목표

| 단계 | 목표 | 상태 |
|------|------|------|
| 1단계 | 고용24 API 연동 기반 채용공고 검색 페이지 구현 | **진행 중** |
| 2단계 | 참여자 이력서·자격증·희망직무 기반 맞춤형 추천 알고리즘 도입 | 추후 예정 |

---

## 2. 시스템 아키텍처

> **[Word 변환 시 교체 예정]** 아래 텍스트 구조도를 시스템 아키텍처 다이어그램 이미지로 대체한다.

### 2.1 전체 구조 (텍스트 표현)

```
[사용자 브라우저 (JSP)]
        │
        │  AJAX GET /recruitmentInformation/search
        ▼
[Spring Boot Server - jobinfoApiController / jobinfoMainController]
        │
        │  DB 조회 (RecruitmentDAO.searchPostings)
        ▼
[MSSQL - J_참여자관리_JOB_POSTING]
        ▲
        │  하루 4회 동기화 (00:00/06:00/12:00/18:00)
[RecruitmentScheduler (@Scheduled)]
        │
        │  고용24 Open API 호출 (스케줄러 전용)
        ▼
[고용24 Open API Server → XML 파싱 → MERGE UPSERT]
```

### 2.2 기술 스택

| 영역 | 기술 |
|------|------|
| View | JSP (Spring MVC ViewResolver) |
| Controller | Spring Boot 3.4.1 (`@Controller`) |
| Service | Spring Service (`@Service`) |
| API 통신 | `RestTemplate` 또는 `WebClient` |
| DB | MSSQL Server (MyBatis 3.5.6) |
| Cache | Redis (Jedis) — 2단계 도입 |
| Scheduler | `@Scheduled` (Spring Boot) — 2단계 도입 |

### 2.3 패키지 구조

```
src/main/java/com/jobmoa/app/recruitmentFormation/
├── biz/
│   ├── RecruitmentService.java           (인터페이스 — 구현됨)
│   ├── RecruitmentServiceImpl.java       (구현체 — 구현됨: RestTemplate + XML 파싱)
│   ├── RecruitmentDAO.java               (DB 연동 — 2단계)
│   └── dto/
│       ├── RecruitmentSearchDTO.java     (검색 파라미터 — 구현됨)
│       └── RecruitmentResultDTO.java     (API 응답 매핑 — 구현됨)
└── view/
    └── jobinfo/
        ├── jobinfoMainController.java    (구현됨: /jobinfo 렌더링)
        └── jobinfoApiController.java     (구현됨: /recruitmentInformation/search REST API)
```

---

## 3. 고용24 Open API 연동 명세

### 3.1 API 기본 정보

| 항목 | 내용 |
|------|------|
| 제공처 | 고용24 (워크넷) |
| 호출 방식 | HTTP GET |
| 응답 형식 | XML |
| 인증 방식 | `authKey` 파라미터 |
| 호출 유형 | `callTp=L` (채용공고 목록) |

### 3.2 필수 요청 파라미터

| 파라미터명 | 설명 | 예시 |
|------------|------|------|
| `authKey` | API 인증키 | (발급키) |
| `callTp` | 호출 유형 | `L` |
| `returnType` | 응답 형식 | `xml` |
| `startPage` | 시작 페이지 | `1` |
| `display` | 페이지당 건수 | `10` ~ `100` |

### 3.3 검색 조건 파라미터

| 파라미터명 | 설명 | 코드 형식 | 다중선택 |
|------------|------|-----------|----------|
| `region` | 근무 지역 | 5자리 숫자 코드 | 최대 5개 |
| `occupation` | 희망 직종 | 3~6자리 숫자 코드 | 최대 10개 |
| `eduLv` | 학력 | `00`~`07` | 다중 |
| `career` | 경력 | `N`/`E`/`Z` | 단일 |
| `minCareerM` | 최소 경력 (월) | 숫자 | — |
| `maxCareerM` | 최대 경력 (월) | 숫자 | — |
| `empTpCd` | 고용형태 코드 | `4`,`10`,`11`,`20`,`21`,`Y` | 단일 |
| `salTpCd` | 임금형태 코드 | `1`~`4` | 단일 |
| `minPay` | 최소 급여 | 숫자 | — |
| `maxPay` | 최대 급여 | 숫자 | — |
| `keyword` | 키워드 | 문자열 | `,` 구분 다중 |
| `sortTp` | 정렬 방식 | `1`(최신) / `2`(오래된순) | 단일 |
| `regDtTp` | 등록일 기준 | `0`(전체)/`1`(오늘)/`2`(1주)/`3`(2주)/`4`(1달) | 단일 |

### 3.4 API 응답 구조 (주요 필드)

| 응답 필드명 | DB 컬럼명 | 타입 | 설명 |
|-------------|-----------|------|------|
| `wantedAuthNo` | `wanted_auth_no` | `VARCHAR(50)` | **PK** 구인인증번호 |
| `company` | `company_nm` | `NVARCHAR(100)` | 회사명 |
| `title` | `recrut_title` | `NVARCHAR(300)` | 채용제목 |
| `jobsCd` | `jobs_cd` | `INT` | 직종코드 |
| `empTpCd` | `emp_tp_cd` | `INT` | 고용형태코드 |
| `salTpNm` | `sal_tp_nm` | `NVARCHAR(20)` | 임금형태 |
| `sal` | `sal_desc` | `NVARCHAR(100)` | 급여 상세 텍스트 |
| `minSal` | `min_sal` | `INT` | 최소임금액 |
| `maxSal` | `max_sal` | `INT` | 최대임금액 |
| `minEdubg` | `min_edubg` | `NVARCHAR(50)` | 최소학력 |
| `maxEdubg` | `max_edubg` | `NVARCHAR(50)` | 최대학력 |
| `career` | `career` | `NVARCHAR(50)` | 경력 |
| `holidayTpNm` | `holiday_tp_nm` | `NVARCHAR(50)` | 근무형태 |
| `region` | `region_nm` | `NVARCHAR(100)` | 근무지역 |
| `regDt` | `reg_dt` | `DATE` | 공고 등록일 |
| `closeDt` | `close_dt` | `DATE` | 공고 마감일 |
| `wantedInfoUrl` | `wanted_info_url` | `VARCHAR(1000)` | 워크넷 원본 URL |
| `wantedMobileInfoUrl` | `mobile_info_url` | `VARCHAR(1000)` | 모바일 원본 URL |
| `smodifyDtm` | `smodify_dtm` | `DATETIME` | 최종수정일시 |

### 3.5 트래픽 관리 정책

| 항목 | 1단계 (현재) | 2단계 (추후) |
|------|-------------|-------------|
| 호출 방식 | 사용자 검색 요청 시 API 직접 호출 | Scheduler 4회/일 일괄 호출 후 DB 저장 |
| 응답 데이터 | API 응답을 실시간 변환하여 반환 | 자체 DB 조회 |
| 동기화 주기 | 실시간 | 00:00 / 06:00 / 12:00 / 18:00 |
| 마감 공고 처리 | API 응답에서 제외됨 | DB에서 즉각 블라인드 처리 |

---

## 4. 화면 설계 (UI)

> **[Word 변환 시 교체 예정]** 아래 각 항목별 UI 설명을 실제 화면 캡처 이미지 또는 와이어프레임 이미지로 대체한다.

### 4.1 페이지 구성 개요

| 영역 | 설명 |
|------|------|
| 상단 헤더 | 서비스 타이틀, 뒤로가기 링크 (sticky, 글래스모피즘 효과) |
| 좌측/중앙 | 8개 그룹 상세 검색 필터 패널 |
| 하단 | AJAX 비동기 카드형 검색 결과 + 페이지네이션 |
| 팝업 | 지역·직종 2단 모달 창 |

### 4.2 검색 필터 패널 상세

> **[Word 변환 시 이미지 추가 예정]** — 각 필터 그룹 UI 스크린샷

#### [필터 1] 지역 및 직종 (모달 다중 선택)

- **근무 지역:** 시/도 → 시/군/구 2단 계층 선택, **최대 5개** 제한
- **희망 직종:** 대분류 → 세부 직종 2단 계층 선택, **최대 10개** 제한
- **UX:** 모달 내 텍스트 검색창 제공 (빠른 필터링)
- **선택 표시:** 칩(Chip) 형태로 선택 항목 시각화

#### [필터 2] 고용 형태 및 급여 (조건부 활성화)

- **고용/임금 형태:** Dropdown 선택 (정규직, 계약직, 시간제 등)
- **급여 범위:** 임금형태 선택 시에만 최소/최대 입력창 활성화 (입력 오류 방지)

#### [필터 3] 학력 및 경력 (상호 배타 제어)

- **학력:** 체크박스 다중 선택 — `학력무관` 선택 시 나머지 자동 해제
- **경력:** `신입` / `경력` / `관계없음` — `경력` 선택 시 최소·최대 경력 개월 수 입력창 슬라이드 다운 노출

#### [필터 4] 기업 형태 및 상세 조건

- **기업 형태:** 대기업, 벤처기업, 공공기관, 외국계, 청년친화 체크박스
- **사원수:** 5인 미만 ~ 100인 이상 드롭다운 (6단계)
- **우대 조건:** 일반 우대 / 컴퓨터 활용 우대 드롭다운

#### [필터 5] 근무 형태 및 부가 조건

- **근무 형태:** 주5일, 주6일, 격주 토요일 등 드롭다운
- **등록일:** 오늘 / 1주 이내 / 2주 이내 / 1달 이내
- **정렬:** 최신순 / 오래된순
- **페이지당 건수:** 10 / 20 / 30 / 50 / 100건

#### [필터 6] 키워드 검색

- 직종명, 회사명 자유 텍스트 입력
- 엔터키 이벤트 바인딩 (검색 실행)
- 쉼표(`,`) 구분 다중 키워드 지원

### 4.3 검색 결과 영역

> **[Word 변환 시 이미지 추가 예정]** — 채용공고 카드 UI, 빈 결과 UI, 로딩 스피너 스크린샷

#### Job Card 구성 요소

| 요소 | 내용 |
|------|------|
| 회사명 | 상단 강조 표시 |
| 공고명 | 메인 타이틀 |
| 태그 | 지역(파랑), 직종(초록), 고용형태(노랑), 근무형태(보라) |
| 급여 | 아이콘 강조, `salTpNm + sal_desc` 조합 |
| 학력/경력 | Badge 형태 |
| 등록일·마감일 | 날짜 표시 |
| 상세보기 버튼 | 고용24 원본 URL `target="_blank"` 연결 |
| 출처 명시 | **"본 정보는 고용24에서 제공된 정보입니다"** (공공데이터 준수) |

#### 예외 처리 UI

| 상황 | 처리 방식 |
|------|-----------|
| API 통신 중 | 로딩 스피너(Spinner) 노출 |
| 검색 결과 없음 | Empty State 안내 문구 노출 |
| 초기 진입 시 | 최신 채용공고 자동 표출 |
| 유효성 오류 | Toast 알림 노출 |

### 4.4 페이지네이션

- 블록 단위 (1블록 = 10페이지) 네비게이션
- `prev` / `next` 블록 이동 화살표
- 현재 페이지 강조 표시
- 검색 조건 유지 상태에서 페이지 이동

---

## 5. 백엔드 API 명세

### 5.1 엔드포인트 목록

| 메서드 | URL | 설명 | 인증 |
|--------|-----|------|------|
| `GET` | `/jobinfo` | 채용공고 검색 페이지 렌더링 | 불필요 (공개) |
| `GET` | `/recruitmentInformation/search` | 채용공고 검색 결과 JSON 반환 | 불필요 (공개) |

### 5.2 `/recruitmentInformation/search` 요청 파라미터

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `region` | `String[]` | 선택 | 지역 코드 배열 (최대 5개) |
| `occupation` | `String[]` | 선택 | 직종 코드 배열 (최대 10개) |
| `empTpCd` | `String` | 선택 | 고용형태 코드 |
| `salTpCd` | `String` | 선택 | 임금형태 코드 |
| `minPay` | `Integer` | 조건부 | 최소 급여 (`salTpCd` 입력 시 필수) |
| `maxPay` | `Integer` | 조건부 | 최대 급여 (`salTpCd` 입력 시 필수) |
| `eduLv` | `String[]` | 선택 | 학력 코드 배열 |
| `career` | `String` | 선택 | 경력 구분 (`N`/`E`/`Z`) |
| `minCareerM` | `Integer` | 조건부 | 최소 경력 개월 (`career=E` 시 필수) |
| `maxCareerM` | `Integer` | 조건부 | 최대 경력 개월 (`career=E` 시 필수) |
| `keyword` | `String` | 선택 | 키워드 (쉼표 구분 다중) |
| `regDtTp` | `String` | 선택 | 등록일 기준 (`0`~`4`) |
| `sortTp` | `String` | 선택 | 정렬 방식 (`1`=최신, `2`=오래된순) |
| `startPage` | `Integer` | 필수 | 시작 페이지 번호 |
| `display` | `Integer` | 필수 | 페이지당 건수 |

### 5.3 `/recruitmentInformation/search` 응답 구조

```json
{
  "total": 1234,
  "startPage": 1,
  "display": 10,
  "list": [
    {
      "wantedAuthNo": "...",
      "companyNm": "...",
      "recrutTitle": "...",
      "salTpNm": "월급",
      "salDesc": "250만원 이상",
      "empTpNm": "정규직",
      "regionNm": "서울 강남구",
      "occNm": "경영·사무",
      "holidayTpNm": "주5일",
      "minEdubg": "고졸",
      "career": "신입",
      "regDt": "2026-03-20",
      "closeDt": "2026-04-15",
      "wantedInfoUrl": "https://www.work24.go.kr/..."
    }
  ]
}
```

---

## 6. DB 테이블 설계 (2단계 적용)

> 1단계에서는 DB 저장 없이 API 응답을 직접 반환한다.
> 2단계 Scheduler 방식 도입 시 아래 테이블을 생성하여 사용한다.

### 6.1 채용공고 테이블 (`JOB_POSTING`)

| 컬럼명 | MSSQL 타입 | 제약 조건 | 설명 |
|--------|-----------|-----------|------|
| `wanted_auth_no` | `VARCHAR(50)` | **PK** | 구인인증번호 |
| `company_nm` | `NVARCHAR(100)` | NOT NULL | 회사명 |
| `biz_reg_no` | `VARCHAR(20)` | — | 사업자등록번호 |
| `ind_tp_nm` | `NVARCHAR(100)` | — | 업종명 |
| `recrut_title` | `NVARCHAR(300)` | NOT NULL | 채용제목 |
| `jobs_cd` | `INT` | — | 직종코드 |
| `emp_tp_cd` | `INT` | — | 고용형태코드 |
| `sal_tp_nm` | `NVARCHAR(20)` | — | 임금형태 |
| `sal_desc` | `NVARCHAR(100)` | — | 급여 상세 텍스트 |
| `min_sal` | `INT` | — | 최소임금액 |
| `max_sal` | `INT` | — | 최대임금액 |
| `min_edubg` | `NVARCHAR(50)` | — | 최소학력 |
| `max_edubg` | `NVARCHAR(50)` | — | 최대학력 |
| `career` | `NVARCHAR(50)` | — | 경력 |
| `holiday_tp_nm` | `NVARCHAR(50)` | — | 근무형태 |
| `region_nm` | `NVARCHAR(100)` | — | 근무지역 |
| `zip_cd` | `VARCHAR(10)` | — | 우편번호 |
| `strt_nm_addr` | `NVARCHAR(200)` | — | 도로명주소 |
| `basic_addr` | `NVARCHAR(200)` | — | 기본주소 |
| `detail_addr` | `NVARCHAR(200)` | — | 상세주소 |
| `reg_dt` | `DATE` | — | 공고 등록일 |
| `close_dt` | `DATE` | — | 공고 마감일 |
| `info_svc` | `NVARCHAR(50)` | — | 정보제공처 |
| `wanted_info_url` | `VARCHAR(1000)` | — | 워크넷 채용정보 URL |
| `mobile_info_url` | `VARCHAR(1000)` | — | 모바일 URL |
| `smodify_dtm` | `DATETIME` | — | 최종수정일시 |
| `sync_dtm` | `DATETIME` | NOT NULL | DB 동기화 일시 (자체 관리용) |
| `is_active` | `BIT` | DEFAULT 1 | 활성 여부 (마감 시 0) |

---

## 7. 데이터 표출 및 공공데이터 준수 정책

### 7.1 출처 명시 (심사 필수)

모든 채용공고 카드 및 상세 연결 시 다음 문구를 반드시 표시한다.

> **"본 정보는 고용24에서 제공된 정보입니다"**

- 원본 링크: `wantedInfoUrl` → `target="_blank"` 연결

### 7.2 데이터 최신성 관리

| 정책 | 내용 |
|------|------|
| 마감 공고 처리 | API 응답에 포함되지 않은 공고는 즉각 화면 미표출 (1단계: API 응답 기준, 2단계: `is_active=0` 처리) |
| 데이터 파기 | 마감일 경과 후 일정 기간 경과 시 DB에서 삭제 (2단계) |
| 동기화 실패 처리 | Scheduler 실패 시 알림 발송 및 기존 데이터 유지 (2단계) |

---

## 8. 인증 및 접근 제어

| 경로 | 인증 필요 여부 | 설명 |
|------|--------------|------|
| `/jobinfo` | **불필요** | `LoginInterceptor` 제외 경로 |
| `/recruitmentInformation/search` | **불필요** | 공개 검색 API |
| `/jobinfo/**` | **불필요** | 하위 경로 전체 공개 |

> `WebMvcConfig.java`의 `LoginInterceptor` 제외 경로에 `/jobinfo/**`가 등록되어 있다.

---

## 9. 구현 진행 현황

### 9.1 완료 항목

- [x] `/jobinfo` 페이지 라우팅 컨트롤러 (`jobinfoMainController.java`)
- [x] 채용공고 검색 화면 JSP (`recruitmentInformation/index.jsp`)
- [x] 검색 필터 UI (지역, 직종, 고용형태, 급여, 학력, 경력, 기업형태, 키워드)
- [x] 지역 2단 모달 (23개 시/도, 250+ 시/군/구 코드 내장)
- [x] 직종 2단 모달 (13개 대분류, 100+ 세부 직종 코드 내장)
- [x] AJAX 기반 검색 결과 렌더링 (카드형 UI)
- [x] 페이지네이션 (블록 단위)
- [x] 로딩 스피너, Empty State, Toast 알림 UI
- [x] 커스텀 CSS (`style_0.0.1.css`)

### 9.2 미구현 항목 (1단계)

- [x] `/recruitmentInformation/search` REST 컨트롤러 구현 — `jobinfoApiController.java` (`@RestController`)
- [x] `RecruitmentService` / `RecruitmentServiceImpl` 구현 — `recruitmentFormation/biz/` 패키지
- [x] `RecruitmentSearchDTO` / `RecruitmentResultDTO` DTO 클래스 작성 — `recruitmentFormation/biz/dto/` 패키지
- [x] 고용24 API 실제 호출 로직 — `RestTemplate` 사용, `RecruitmentServiceImpl.search()`
- [x] API XML 응답 파싱 로직 — `DocumentBuilderFactory` DOM 파싱, `<wanted>` 요소 추출
- [x] API 인증키 `application.properties` 등록 — `work24.api.key` 키 추가 (실제 발급키 입력 필요)

> **완료일:** 2026-03-30
> **비고:** `application.properties`의 `work24.api.key` 값을 실제 고용24 발급 인증키로 교체해야 서비스가 정상 동작함.

### 9.3 추후 구현 항목 (2단계)

- [x] `JOB_POSTING` 테이블 생성 DDL — `src/Docs/sql/JOBMOA_참여자관리프로그램_CreateTable.sql` (실 DB 적용 필요)
- [x] MyBatis 매퍼 — `RecruitmentDAO.java` + `Recruitment-mapping.xml` (MERGE UPSERT)
- [x] `@Scheduled` Scheduler 구현 — `RecruitmentScheduler.java` (00:00/06:00/12:00/18:00)
- [x] 마감 공고 블라인드 처리 — `deactivateOldPostings()` (sync_dtm 기준 is_active=0)
- [ ] 참여자 맞춤형 추천 알고리즘

> **완료일:** 2026-03-30
> **비고:** DDL(`JOBMOA_참여자관리프로그램_CreateTable.sql`)을 실제 MSSQL 서버에 실행해야 스케줄러가 정상 동작함.

---

## 10. 향후 개선 방향

1. **맞춤형 추천:** 참여자의 희망 직종, 보유 자격증, 거주 지역 데이터를 기반으로 큐레이션 점수를 산정하여 상위 노출
2. **북마크 기능:** 로그인한 참여자가 관심 공고를 저장할 수 있는 기능
3. **공고 알림:** 참여자의 조건에 맞는 신규 공고 등록 시 알림 (WebSocket/메일)
4. **통계 대시보드:** 관리자가 직종별·지역별 채용 트렌드를 확인할 수 있는 집계 화면
