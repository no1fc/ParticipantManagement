# 상담 일정 관리 기능 설계서

> 작성일: 2026-05-12
> 상태: 설계 완료, 구현 대기

---

## 1. 개요

### 1.1 목적
상담사가 참여자와의 상담 일정을 등록/조회/수정/삭제할 수 있는 캘린더 기반 일정 관리 기능을 개발한다.
관리자는 지점 내 전체 상담사의 일정을 통합 조회하고 수정/삭제할 수 있다.

### 1.2 대상 사용자
| 역할 | 설명 |
|------|------|
| 상담사 | 본인 일정 CRUD, 참여자 연결 |
| 관리자 | 지점 전체 일정 통합 조회, 수정/삭제 |
| 외부 사용자 | 공개 일정 조회 (3단계, 미정) |

### 1.3 개발 단계
| 단계 | 범위 | 우선순위 |
|------|------|----------|
| 1단계 | 상담사용 일정 CRUD (핵심) | 최우선 |
| 2단계 | 관리자 통합 조회 + 통계 | 후순위 |
| 3단계 | 외부 공개 페이지 | 미정 |

### 1.4 프로토타입 참조
- `src/main/webapp/prototype/schedule-counselor.html` — 상담사용
- `src/main/webapp/prototype/schedule-manager.html` — 관리자용
- `src/main/webapp/prototype/schedule-public.html` — 공개용

---

## 2. 아키텍처

### 2.1 모듈 위치
기존 `CounselMain` 모듈 내 `schedule` 패키지로 추가한다.
기존 AOP 트랜잭션, LoginInterceptor가 자동 적용된다.

```
CounselMain/
├── biz/schedule/
│   ├── ScheduleDTO.java
│   ├── ScheduleService.java
│   ├── ScheduleServiceImpl.java
│   └── ScheduleDAO.java
└── view/schedule/
    ├── ScheduleController.java
    └── ScheduleApiController.java
```

### 2.2 데이터 흐름
```
[브라우저] FullCalendar + jQuery $.ajax
    ↓ ↑ JSON
[ScheduleApiController] @RestController — 세션 체크, DTO 바인딩
    ↓ ↑
[ScheduleServiceImpl] @Service — 비즈니스 로직, 권한 검증
    ↓ ↑
[ScheduleDAO] @Repository — SqlSessionTemplate, namespace="ScheduleDAO"
    ↓ ↑
[Schedule-mapping.xml] MyBatis SQL (T-SQL)
    ↓ ↑
[MSSQL] J_참여자관리_상담일정 테이블
```

### 2.3 설정 변경 (1단계 필수)
| 파일 | 변경 내용 |
|------|-----------|
| `RootConfig.java` | `@ComponentScan`에 `com.jobmoa.app.CounselMain.biz.schedule` 추가 |
| `WebMvcConfig.java` | `@ComponentScan`에 `com.jobmoa.app.CounselMain.view.schedule` 추가 |
| `sql-map-config.xml` | `ScheduleDTO` typeAlias 등록 + `Schedule-mapping.xml` mapper 등록 |

---

## 3. DB 설계

### 3.1 J_참여자관리_상담일정 테이블

```sql
CREATE TABLE J_참여자관리_상담일정 (
    일정ID          INT IDENTITY(1,1) PRIMARY KEY,
    구직번호        INT NULL,              -- FK → 참여자관리.구직번호 (미정 일정 허용)
    상담사ID        VARCHAR(50) NOT NULL,  -- LoginBean.memberUserID
    일정날짜        DATE NOT NULL,
    시작시간        TIME NOT NULL,
    종료시간        TIME NULL,
    일정유형        VARCHAR(20) NOT NULL,  -- 대면상담, 전화상담, 화상상담, 기타
    메모            NVARCHAR(500) NULL,
    알림분          INT NULL DEFAULT 30,   -- 일정 시작 N분 전 알림 (NULL=알림없음)
    등록일          DATETIME DEFAULT GETDATE(),
    수정일          DATETIME DEFAULT GETDATE(),
    삭제여부        CHAR(1) DEFAULT 'N'    -- 논리삭제 (Y/N)
);
```

### 3.2 설계 결정 사항
- **별도 테이블**: 참여자 1명에 여러 일정이 가능하므로 참여자관리 테이블에 컬럼 추가가 아닌 별도 테이블 사용
- **구직번호 NULL 허용**: 참여자 미정 일정 등록 가능 (프로토타입의 "(미정)" 기능)
- **논리삭제**: `삭제여부='Y'`로 논리삭제, 실수 삭제 시 복구 가능
- **한국어 컬럼명**: 기존 정부 스키마 패턴 준수
- **알림분 컬럼**: 일정별 개별 알림 시간 설정 (분 단위), NULL이면 알림 없음, 기본값 30분
- **일정 중복 차단**: 같은 상담사, 같은 날짜/시간대에 중복 일정 등록 불가

---

## 4. API 설계

### 4.1 페이지 컨트롤러 (ScheduleController)
| URL | 메서드 | 설명 | 단계 |
|-----|--------|------|------|
| `/schedule.login` | GET | 상담사 일정 페이지 | 1단계 |
| `/scheduleManager.login` | GET | 관리자 통합 조회 페이지 | 2단계 |

### 4.2 REST API (ScheduleApiController)
| URL | 메서드 | 설명 | 단계 |
|-----|--------|------|------|
| `/api/schedule/list` | GET | 일정 목록 조회 (월별, 상담사별 필터) | 1단계 |
| `/api/schedule/detail/{일정ID}` | GET | 일정 상세 조회 | 1단계 |
| `/api/schedule/save` | POST | 일정 등록 | 1단계 |
| `/api/schedule/update` | PUT | 일정 수정 | 1단계 |
| `/api/schedule/delete/{일정ID}` | DELETE | 일정 삭제 (논리삭제) | 1단계 |
| `/api/schedule/participants` | GET | 참여자 검색 (자동완성용) | 1단계 |
| `/api/schedule/drag-update` | PUT | 드래그 앤 드롭 일정 이동 (날짜/시간 변경) | 1단계 |
| `/api/schedule/today` | GET | 금일 J_참여자관리_상담일정 (대시보드 위젯용) | 1단계 |
| `/api/schedule/stats` | GET | 관리자 통계 | 2단계 |

### 4.3 권한 처리
- **상담사**: `상담사ID = loginBean.memberUserID` — 본인 일정만 CRUD
- **관리자**: `IS_MANAGER` 또는 `IS_BRANCH_MANAGER` 세션 속성 체크 → 지점 내 전체 일정 조회/수정/삭제

---

## 5. Java 클래스 설계

### 5.1 ScheduleDTO
```java
@Data
public class ScheduleDTO {
    // 테이블 매핑
    private int scheduleId;           // 일정ID
    private Integer participantJobNo; // 구직번호 (null 허용)
    private String counselorId;       // 상담사ID
    private String scheduleDate;      // 일정날짜
    private String startTime;         // 시작시간
    private String endTime;           // 종료시간
    private String scheduleType;      // 일정유형
    private String memo;              // 메모
    private String regDate;           // 등록일
    private String modDate;           // 수정일
    private String deleteYn;          // 삭제여부

    // JOIN 결과
    private String participantName;   // 참여자명 (참여자관리 JOIN)
    private String participantStage;  // 진행단계

    // 알림
    private Integer alertMinutes;     // 알림분 (일정 시작 N분 전, null=알림없음)

    // 검색/필터
    private String searchStartDate;   // 조회 시작일
    private String searchEndDate;     // 조회 종료일
    private String keyword;           // 참여자 검색 키워드
    private String condition;         // 쿼리 분기용
}
```

### 5.2 ScheduleDAO
```java
@Repository
public class ScheduleDAO {
    @Autowired
    private SqlSessionTemplate sqlSession;

    private static final String ns = "ScheduleDAO.";

    public List<ScheduleDTO> selectList(ScheduleDTO dto) {
        return sqlSession.selectList(ns + "selectScheduleList", dto);
    }
    public ScheduleDTO selectDetail(ScheduleDTO dto) {
        return sqlSession.selectOne(ns + "selectScheduleDetail", dto);
    }
    public int insert(ScheduleDTO dto) {
        return sqlSession.insert(ns + "insertSchedule", dto);
    }
    public int update(ScheduleDTO dto) {
        return sqlSession.update(ns + "updateSchedule", dto);
    }
    public int delete(ScheduleDTO dto) {
        return sqlSession.update(ns + "deleteSchedule", dto);
    }
    public List<ScheduleDTO> selectParticipantSearch(ScheduleDTO dto) {
        return sqlSession.selectList(ns + "selectParticipantSearch", dto);
    }
}
```

### 5.3 ScheduleService / ScheduleServiceImpl
- 인터페이스 + `@Service("schedule")` 구현체
- 비즈니스 로직: 권한 검증, 시간 중복 체크, 입력값 검증

---

## 6. MyBatis 매퍼

### 6.1 Schedule-mapping.xml 쿼리 목록
| ID | 타입 | 설명 | 단계 |
|----|------|------|------|
| `selectScheduleList` | SELECT | 월별 일정 목록 (상담사ID + 기간 필터, 참여자관리 JOIN) | 1단계 |
| `selectScheduleDetail` | SELECT | 일정 상세 1건 | 1단계 |
| `insertSchedule` | INSERT | 일정 등록 (SCOPE_IDENTITY 반환) | 1단계 |
| `updateSchedule` | UPDATE | 일정 수정 (수정일 갱신) | 1단계 |
| `deleteSchedule` | UPDATE | 논리삭제 (삭제여부='Y') | 1단계 |
| `selectParticipantSearch` | SELECT | 참여자 자동완성 검색 (TOP 10) | 1단계 |
| `selectTodaySchedule` | SELECT | 금일 J_참여자관리_상담일정 (대시보드 위젯) | 1단계 |
| `selectDuplicateCheck` | SELECT | 일정 시간 중복 체크 | 1단계 |
| `updateScheduleDrag` | UPDATE | 드래그 앤 드롭 일정 이동 (날짜/시간만 변경) | 1단계 |
| `selectAlertTargets` | SELECT | 알림 대상 일정 조회 (스케줄러용) | 1단계 |
| `selectScheduleByBranch` | SELECT | 지점 전체 일정 (관리자용) | 2단계 |
| `selectScheduleStats` | SELECT | 통계 집계 (상담사수, 주간/일간/월간) | 2단계 |

### 6.2 참여자 검색 쿼리 (자동완성)
```sql
SELECT TOP 10
    A.구직번호 as participantJobNo,
    A.참여자 as participantName,
    A.진행단계 as participantStage
FROM 참여자관리 A
WHERE A.상담사ID = #{counselorId}
  AND (A.참여자 LIKE '%'+#{keyword}+'%'
       OR CAST(A.구직번호 AS VARCHAR) LIKE '%'+#{keyword}+'%')
  AND A.종결여부 = 'N'
ORDER BY A.참여자
```

---

## 7. 프론트엔드 설계

### 7.1 JSP 뷰
| 파일 | 용도 | 단계 |
|------|------|------|
| `/WEB-INF/views/schedule/schedulePage.jsp` | 상담사 일정 페이지 | 1단계 |
| `/WEB-INF/views/schedule/scheduleManagerPage.jsp` | 관리자 통합 조회 | 2단계 |

### 7.2 JS/CSS
| 파일                                          | 설명 | 단계 |
|---------------------------------------------|------|------|
| `js/schedule_0.0.2.js`                      | 캘린더, CRUD AJAX, 모달 | 1단계 |
| `css/scheduleCss/schedule_0.0.1.css`        | 캘린더/모달 커스텀 스타일 | 1단계 |
| `js/scheduleManager_0.0.2.js`               | 관리자 필터링, 통계 | 2단계 |
| `css/scheduleCss/scheduleManager_0.0.1.css` | 관리자 페이지 스타일 | 2단계 |

### 7.3 라이브러리
| 라이브러리 | 용도 | 적용 방식 |
|------------|------|-----------|
| FullCalendar 6.1.11 | 캘린더 UI | CDN |
| jQuery | AJAX 통신 | 기존 프로젝트 포함 |
| SweetAlert2 | 알림/확인 다이얼로그 | 기존 프로젝트 포함 |
| AdminLTE 3 (Bootstrap 4) | 레이아웃/모달/폼 | 기존 프로젝트 포함 |

### 7.4 프로토타입 → JSP 변환 포인트
- 독립 HTML 사이드바/헤더 → `<gnb:gnb/>`, `<footer:footer/>` JSP 태그 치환
- Bootstrap 5 문법 → Bootstrap 4 (AdminLTE) 문법 변환
- vanilla JS → jQuery `$.ajax()` 변환
- 브라우저 `alert()` → SweetAlert2 변환
- 인라인 CSS → 별도 CSS 파일 분리

---

## 8. 유효성 검증

### 8.1 프론트엔드 (JavaScript)
| 필드 | 검증 규칙 |
|------|-----------|
| 일정 날짜 | 필수, 날짜 형식 |
| 시작 시간 | 필수 |
| 종료 시간 | 선택, 시작 시간 이후 |
| 일정 유형 | 필수, 4가지 중 선택 |
| 메모 | 최대 500자 |
| 알림 설정 | 선택, 분 단위 양수 (알림없음/10분/30분/60분/직접입력) |

### 8.2 백엔드 (Controller/Service)
| 항목 | 검증 내용 |
|------|-----------|
| 세션 | LoginBean 존재 여부 |
| 상담사ID | 세션의 memberUserID와 일치 (상담사 본인 확인) |
| 구직번호 | 값이 있을 경우 참여자관리 테이블에 존재하는지 확인 |
| 일정 중복 | 같은 상담사, 같은 날짜/시간에 중복 일정 체크 |
| 관리자 권한 | 타인 일정 수정/삭제 시 IS_MANAGER 또는 IS_BRANCH_MANAGER 체크 |

---

## 9. 에러 처리

### 9.1 응답 형식
```json
{
    "statusData": "success" | "error",
    "message": "처리 결과 메시지",
    "data": { ... }
}
```

### 9.2 에러 시나리오
| 상황 | HTTP 코드 | 메시지 |
|------|-----------|--------|
| 미로그인 | 401 | 로그인이 필요합니다 |
| 권한 없음 | 403 | 수정/삭제 권한이 없습니다 |
| 일정 미존재 | 404 | 해당 일정을 찾을 수 없습니다 |
| 필수값 누락 | 400 | 필수 항목을 입력해주세요 |
| 시간 중복 | 409 | 해당 시간에 이미 일정이 있습니다 |
| 서버 오류 | 500 | 처리 중 오류가 발생했습니다 |

---

## 10. 상담일지 연동 (보류)
일정 상세에서 "상담일지 작성" 버튼은 1단계에서 UI만 비활성 처리한다.
기존 `participantCounsel` 모듈과의 연동은 별도 작업으로 진행한다.

---

## 11. 파일 목록 요약

### 신규 생성 파일
| 파일                                                      | 설명 | 단계 |
|---------------------------------------------------------|------|------|
| `CounselMain/biz/schedule/ScheduleDTO.java`             | 일정 DTO | 1단계 |
| `CounselMain/biz/schedule/ScheduleService.java`         | 서비스 인터페이스 | 1단계 |
| `CounselMain/biz/schedule/ScheduleServiceImpl.java`     | 서비스 구현체 | 1단계 |
| `CounselMain/biz/schedule/ScheduleDAO.java`             | DAO | 1단계 |
| `CounselMain/biz/schedule/ScheduleAlertScheduler.java`  | WebSocket 알림 스케줄러 | 1단계 |
| `CounselMain/view/schedule/ScheduleController.java`     | 페이지 컨트롤러 | 1단계 |
| `CounselMain/view/schedule/ScheduleApiController.java`  | REST API 컨트롤러 | 1단계 |
| `resources/mappings/Schedule-mapping.xml`               | MyBatis 매퍼 | 1단계 |
| `webapp/WEB-INF/views/schedule/schedulePage.jsp`        | 상담사 일정 JSP | 1단계 |
| `webapp/js/schedule_0.0.2.js`                           | 상담사 일정 JS | 1단계 |
| `webapp/css/scheduleCss/schedule_0.0.1.css`             | 상담사 일정 CSS | 1단계 |
| `webapp/WEB-INF/views/schedule/scheduleManagerPage.jsp` | 관리자 통합 조회 JSP | 2단계 |
| `webapp/js/scheduleManager_0.0.2.js`                    | 관리자 페이지 JS | 2단계 |
| `webapp/css/scheduleCss/scheduleManager_0.0.1.css`      | 관리자 페이지 CSS | 2단계 |

### 수정 파일
| 파일 | 변경 내용 | 단계 |
|------|-----------|------|
| `config/RootConfig.java` | ComponentScan에 `biz.schedule` 추가 | 1단계 |
| `config/WebMvcConfig.java` | ComponentScan에 `view.schedule` 추가 | 1단계 |
| `resources/sql-map-config.xml` | typeAlias + mapper 등록 | 1단계 |
| `webapp/WEB-INF/tags/gnb.tag` | "상담 일정" 메뉴 항목 추가 | 1단계 |
| 대시보드 JSP | 금일 J_참여자관리_상담일정 위젯 영역 추가 | 1단계 |
| 대시보드 JS | 금일 J_참여자관리_상담일정 AJAX 호출 + 타임라인 렌더링 | 1단계 |
| `webapp/WEB-INF/tags/adminGnb.tag` | "상담 일정" + "지점 일정 통합 조회" 메뉴 추가 | 2단계 |

---

## 12. 드래그 앤 드롭 일정 이동

### 12.1 동작 방식
- FullCalendar의 `editable: true`, `eventDrop`, `eventResize` 이벤트 활용
- 드래그로 날짜/시간 변경 시 확인 다이얼로그(SweetAlert2) 표시
- 확인 → `/api/schedule/drag-update` PUT 호출 (일정ID, 변경된 날짜, 시작시간, 종료시간)
- 이동 대상 시간에 중복 일정이 있으면 차단 후 원위치 복원
- 취소 → `event.revert()` 호출로 원위치 복원

### 12.2 권한
- 상담사: 본인 일정만 드래그 가능
- 관리자: 지점 내 모든 일정 드래그 가능 (2단계)

---

## 13. WebSocket 알림

### 13.1 알림 방식
- 기존 WebSocket 인프라 활용 (`/ws-notification`, STOMP/SockJS)
- 일정 등록 시 `알림분` 값 저장 (기본 30분, 사용자가 분 단위로 설정 가능)
- `알림분`이 NULL이면 해당 일정은 알림 없음

### 13.2 알림 스케줄러
- `ScheduleAlertScheduler` 컴포넌트 신규 생성 (`@Component`, `@Scheduled`)
- 매 1분마다 실행: 현재 시각 기준으로 알림 대상 일정 조회
- 조건: `일정날짜 = 오늘` AND `시작시간 - 알림분 = 현재시각(분 단위)` AND `삭제여부 = 'N'`
- 대상 일정 발견 시 해당 상담사에게 WebSocket 메시지 전송

### 13.3 알림 메시지 형식
```json
{
    "type": "SCHEDULE_ALERT",
    "title": "상담 일정 알림",
    "message": "30분 후 이민수님과 대면상담 일정이 있습니다.",
    "scheduleId": 123,
    "startTime": "14:00"
}
```

### 13.4 알림 UI
- 일정 등록/수정 모달에 "알림 설정" 필드 추가 (분 단위 입력 또는 선택)
- 옵션: 알림없음, 10분 전, 30분 전, 1시간 전, 직접입력

### 13.5 파일 목록
| 파일 | 설명 | 단계 |
|------|------|------|
| `CounselMain/biz/schedule/ScheduleAlertScheduler.java` | 알림 스케줄러 | 1단계 |
| Schedule-mapping.xml에 `selectAlertTargets` 쿼리 추가 | 알림 대상 조회 | 1단계 |

---

## 14. 대시보드 금일 J_참여자관리_상담일정 위젯

### 14.1 구성
```
┌─────────────────────────────────┐
│  [N건] 오늘 J_참여자관리_상담일정              ← 건수 카드 상단
│─────────────────────────────────│
│  09:30  이민수 - 대면상담         │
│  10:30  박지현 - 전화상담         │  ← 타임라인 목록
│  14:00  김영호 - 대면상담         │
│  15:00  최수진 - 화상상담         │
│                                   │
│  일정이 없습니다 (0건일 때)        │
└─────────────────────────────────┘
```

### 14.2 데이터 흐름
- 대시보드 페이지 로드 시 `/api/schedule/today` GET 호출
- 응답: 해당 상담사의 금일 일정 목록 (시간순 정렬)
- 건수 카드 + 시간순 타임라인 렌더링
- 일정 클릭 시 상담 일정 페이지(`/schedule.login`)로 이동

### 14.3 수정 대상 파일
| 파일 | 변경 내용 |
|------|-----------|
| 대시보드 JSP | 금일 J_참여자관리_상담일정 위젯 HTML 영역 추가 |
| 대시보드 JS | `$.ajax` 호출 + 타임라인 DOM 렌더링 |
| `schedule_0.0.1.css` 또는 대시보드 CSS | 위젯 스타일 |

---

## 15. GNB 메뉴 추가

### 15.1 gnb.tag (상담사용)
- "상담 일정" 메뉴 항목 추가
- 아이콘: `bi-calendar-event` 또는 기존 GNB 아이콘 체계에 맞춤
- URL: `/schedule.login`

### 15.2 adminGnb.tag (관리자용)
- "상담 일정" 하위 메뉴 2개:
  - 상담 일정 조회 → `/schedule.login`
  - 지점 일정 통합 조회 → `/scheduleManager.login` (2단계)

---

## 16. 단계별 최종 범위 (추가 기능 반영)

### 16.1 1단계: 상담사용 일정 CRUD + 부가기능
- [ ] DB: J_참여자관리_상담일정 테이블 DDL 실행 (알림분 컬럼 포함)
- [ ] 설정: RootConfig, WebMvcConfig, sql-map-config.xml 업데이트
- [ ] Java: ScheduleDTO, ScheduleDAO, ScheduleService, ScheduleServiceImpl
- [ ] Java: ScheduleController, ScheduleApiController
- [ ] Java: ScheduleAlertScheduler (WebSocket 알림)
- [ ] MyBatis: Schedule-mapping.xml (CRUD + 참여자 검색 + 중복체크 + 드래그 + 금일조회 + 알림대상)
- [ ] JSP: schedulePage.jsp (FullCalendar + 등록/수정 모달 + 상세 모달)
- [ ] JS: schedule_0.0.2.js (캘린더, AJAX CRUD, 참여자 자동완성, 드래그 앤 드롭, 알림 설정)
- [ ] CSS: schedule_0.0.1.css (캘린더/모달 스타일)
- [ ] GNB: gnb.tag에 "상담 일정" 메뉴 추가
- [ ] 대시보드: 금일 J_참여자관리_상담일정 위젯 (건수 카드 + 타임라인)
- [ ] 유효성 검증 + 일정 중복 차단
- [ ] 에러 처리 + SweetAlert2 알림

### 16.2 2단계: 관리자 통합 조회
- [ ] Java: 관리자 페이지 매핑 + 통계/지점 조회 API 추가
- [ ] MyBatis: 지점별 조회, 통계 집계 쿼리 추가
- [ ] JSP: scheduleManagerPage.jsp (통계 카드 + 상담사 필터 + 캘린더)
- [ ] JS: scheduleManager_0.0.2.js (필터링, 통계, 상세 팝업, 드래그 수정)
- [ ] CSS: scheduleManager_0.0.1.css
- [ ] GNB: adminGnb.tag에 "지점 일정 통합 조회" 메뉴 추가
- [ ] 관리자 권한으로 수정/삭제/드래그 기능
- [ ] 상담사별 색상 구분 + 칩 필터

### 16.3 3단계: 외부 공개 페이지 (미정)
- [ ] 접근 방식 결정 (접근코드 인증 / 로그인 / 기타)
- [ ] LoginInterceptor 제외 여부 결정
- [ ] 참여자명 마스킹 처리
- [ ] 날짜별 시간표 뷰
- [ ] 상세 설계는 3단계 착수 시 별도 진행
