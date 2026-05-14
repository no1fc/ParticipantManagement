# 상담 일정 관리 2~3단계 설계서

> 작성일: 2026-05-12
> 상태: 설계 완료, 구현 대기
> 선행 작업: 1단계 상담사용 CRUD 완료

---

## 1. 개요

### 1.1 목적
1단계(상담사용 일정 CRUD)에 이어, 관리자 통합 조회(2단계)와 메인 데스크용 공개 일정 조회(3단계)를 구현한다.

### 1.2 프로토타입 참조
- `src/main/webapp/prototype/schedule-manager.html` — 관리자 통합 조회
- `src/main/webapp/prototype/schedule-public.html` — 공개 일정 조회

### 1.3 개발 단계
| 단계 | 범위 | 상태 |
|------|------|------|
| 1단계 | 상담사용 일정 CRUD | **완료** |
| 2단계 | 관리자 지점 일정 통합 조회 + 통계 | 설계 완료 |
| 3단계 | 메인 데스크용 공개 일정 조회 | 설계 완료 |

---

## 2. 2단계: 관리자 지점 일정 통합 조회

### 2.1 요구사항
- 관리자(`IS_MANAGER` 또는 `IS_BRANCH_MANAGER`)가 지점 내 전체 상담사 일정을 통합 조회
- 상담사별 색상 구분 + 칩 필터링 (켜기/끄기로 특정 상담사 일정 표시/숨김)
- 통계 카드: 상담사 수, 이번 주 일정, 오늘 일정, 이번 달 일정
- 관리자가 타인 일정 수정/삭제 시 해당 상담사에게 WebSocket 실시간 알림 전송
- 월/주/일 뷰 전환 지원

### 2.2 API 엔드포인트

**페이지 컨트롤러**
| URL | 메서드 | 설명 |
|-----|--------|------|
| `/scheduleManager.login` | GET | 관리자 통합 조회 페이지 |

**REST API (ScheduleApiController에 추가)**
| URL | 메서드 | 설명 |
|-----|--------|------|
| `/api/schedule/branch-list` | GET | 지점 전체 일정 (기간 + 상담사 필터) |
| `/api/schedule/stats` | GET | 통계 집계 (상담사수, 주간/오늘/월간) |
| `/api/schedule/counselors` | GET | 지점 내 상담사 목록 (색상 배정용) |

### 2.3 MyBatis 추가 쿼리

**selectScheduleByBranch**
```sql
SELECT A.일정ID, A.구직번호, A.상담사ID, A.일정날짜, A.시작시간, A.종료시간,
       A.일정유형, A.메모, A.알림분,
       ISNULL(B.참여자, '(미정)') as participantName,
       C.이름 as counselorName
FROM J_참여자관리_상담일정 A
LEFT JOIN J_참여자관리 B ON A.구직번호 = B.구직번호
INNER JOIN J_참여자관리_로그인정보 C ON A.상담사ID = C.아이디
WHERE A.삭제여부 = 'N'
  AND C.지점 = #{branch}
  AND A.일정날짜 >= #{searchStartDate}
  AND A.일정날짜 <= #{searchEndDate}
  -- 상담사 필터 (선택)
ORDER BY A.일정날짜, A.시작시간
```

**selectScheduleStats**
```sql
SELECT
    (SELECT COUNT(DISTINCT 아이디) FROM J_참여자관리_로그인정보
     WHERE 지점 = #{branch} AND 아이디사용여부 = '사용') as counselorCount,
    -- 이번 주/오늘/이번 달 일정 수 집계
```

**selectCounselorsByBranch**
```sql
SELECT 아이디 as counselorId, 이름 as counselorName
FROM J_참여자관리_로그인정보
WHERE 지점 = #{branch} AND 아이디사용여부 = '사용'
ORDER BY 조회순서
```

### 2.4 프론트엔드 구성

| 파일                                          | 설명 |
|---------------------------------------------|------|
| `views/schedule/scheduleManagerPage.jsp`    | 통계 카드 + 상담사 필터 칩 + FullCalendar |
| `js/scheduleManager_0.0.2.js`               | 캘린더 초기화, 상담사 필터, 통계 로드, 이벤트 클릭 팝업 |
| `css/scheduleCss/scheduleManager_0.0.1.css` | 필터 칩, 통계 카드, 팝업 스타일 |

### 2.5 WebSocket 알림 (관리자 수정/삭제 시)
- 토픽: `/topic/schedule-modified/{counselorId}`
- 메시지: `{ type: "SCHEDULE_MODIFIED", title: "일정 변경 알림", message: "관리자가 ... 일정을 수정/삭제했습니다." }`
- `gnb-notification_0.0.1.js`에 구독 + 메시지 처리 추가

### 2.6 GNB 메뉴
- `gnb.tag`에 관리자 전용 메뉴 추가: "지점 일정 통합 조회" → `/scheduleManager.login`
- 관리자 권한(`IS_MANAGER` 또는 `IS_BRANCH_MANAGER`) 체크하여 조건부 표시

---

## 3. 3단계: 공개 일정 조회 (메인 데스크용)

### 3.1 요구사항
- 회사 메인 데스크 화면에 표시하기 위한 공개 일정 조회 페이지
- 인증: `J_참여자관리_로그인정보`의 `고유번호` + `지점`으로 로그인
- 세션 유지형: 한번 인증하면 브라우저 닫기 전까지 유지
- 지점 내 전체 상담사 일정 표시
- 참여자명 마스킹: "이민수" → "이**"
- 캘린더(월간) + 날짜 클릭 시 시간별 테이블 표시
- LoginInterceptor 제외 대상 (기존 로그인 세션 불필요)
- GNB 없는 독립 레이아웃

### 3.2 인증 흐름
```
[/schedulePublic/login 접속] → 인증 폼 (지점 선택 + 고유번호 입력)
    ↓
[POST /schedulePublic/api/verify] → J_참여자관리_로그인정보에서 검증
    ↓ (성공)
[HttpSession에 SCHEDULE_PUBLIC_AUTH = {지점명} 저장]
    ↓
[캘린더 표시] → API 호출 시 세션의 지점 기준으로 조회
```

### 3.3 API 엔드포인트

**페이지 컨트롤러 (SchedulePublicController)**
| URL | 메서드 | 설명 |
|-----|--------|------|
| `/schedulePublic/login` | GET | 인증 페이지 |

**REST API (SchedulePublicApiController)**
| URL | 메서드 | 설명 |
|-----|--------|------|
| `/schedulePublic/api/verify` | POST | 고유번호+지점 검증 |
| `/schedulePublic/api/schedule-list` | GET | 지점 전체 일정 (참여자명 마스킹) |
| `/schedulePublic/api/day-detail` | GET | 날짜별 시간표 데이터 |
| `/schedulePublic/api/counselors` | GET | 지점 내 상담사 목록 |

### 3.4 MyBatis 추가 쿼리

**validatePublicAccess**
```sql
SELECT COUNT(*)
FROM J_참여자관리_로그인정보
WHERE 고유번호 = #{uniqueNumber}
  AND 지점 = #{branch}
  AND 아이디사용여부 = '사용'
```

**selectPublicScheduleByBranch** — 2단계 `selectScheduleByBranch`와 동일 구조 (참여자명 마스킹은 Java 서비스에서 처리)

**selectPublicDayDetail** — 특정 날짜 + 지점, 시간순 정렬, 상담사별 그룹핑 가능

### 3.5 참여자명 마스킹 로직
```java
public String maskName(String name) {
    if (name == null || name.isEmpty()) return "(미정)";
    if (name.length() <= 1) return name;
    return name.charAt(0) + "*".repeat(name.length() - 1);
}
```

### 3.6 프론트엔드 구성

| 파일 | 설명 |
|------|------|
| `views/schedule/schedulePublicPage.jsp` | 독립 레이아웃 (GNB 없음), 인증 폼 + 캘린더 + 시간표 |
| `js/schedulePublic_0.0.1.js` | 인증 처리, 캘린더 초기화, 날짜 클릭 시 시간표 렌더링 |
| `css/scheduleCss/schedulePublic_0.0.1.css` | 인증 카드, 캘린더, 시간표 테이블 스타일 |

### 3.7 설정 변경
- `WebMvcConfig.java` — LoginInterceptor `excludePathPatterns`에 `/schedulePublic/**` 추가
- `WebMvcConfig.java` — `@ComponentScan`에 `view.schedule` 이미 등록되어 있으므로 추가 불필요

### 3.8 시간별 테이블 구조
프로토타입(`schedule-public.html`)과 동일:
```
| 시간   | 김상담      | 박전담      | 이취업      | 최역량      |
|--------|------------|------------|------------|------------|
| 09:00  | -          | 이** 대면  | -          | -          |
| 10:00  | 김** 대면  | -          | -          | 노** 대면  |
| 11:00  | -          | -          | 송** 대면  | -          |
| ...    | ...        | ...        | ...        | ...        |
```

---

## 4. 파일 목록 요약

### 2단계 신규 파일 (3개)
| 파일                                                      | 설명 |
|---------------------------------------------------------|------|
| `webapp/WEB-INF/views/schedule/scheduleManagerPage.jsp` | 관리자 통합 조회 JSP |
| `webapp/js/scheduleManager_0.0.2.js`                    | 관리자 JS |
| `webapp/css/scheduleCss/scheduleManager_0.0.1.css`      | 관리자 CSS |

### 3단계 신규 파일 (5개)
| 파일 | 설명 |
|------|------|
| `CounselMain/view/schedule/SchedulePublicController.java` | 공개 페이지 컨트롤러 |
| `CounselMain/view/schedule/SchedulePublicApiController.java` | 공개 API 컨트롤러 |
| `webapp/WEB-INF/views/schedule/schedulePublicPage.jsp` | 공개 페이지 JSP |
| `webapp/js/schedulePublic_0.0.1.js` | 공개 페이지 JS |
| `webapp/css/scheduleCss/schedulePublic_0.0.1.css` | 공개 페이지 CSS |

### 수정 파일 (2~3단계 공통)
| 파일 | 변경 내용 |
|------|-----------|
| `view/schedule/ScheduleController.java` | `scheduleManager.login` 매핑 추가 |
| `view/schedule/ScheduleApiController.java` | branch-list, stats, counselors API 추가 |
| `biz/schedule/ScheduleService.java` | 지점별 조회, 통계, 상담사 목록, 마스킹 메서드 추가 |
| `biz/schedule/ScheduleServiceImpl.java` | 위 인터페이스 구현 |
| `biz/schedule/ScheduleDAO.java` | 지점별/공개 조회 DAO 메서드 추가 |
| `resources/mappings/Schedule-mapping.xml` | 6개 쿼리 추가 |
| `config/WebMvcConfig.java` | LoginInterceptor 제외 경로 추가 |
| `webapp/WEB-INF/tags/gnb.tag` | 관리자 메뉴 추가 |
| `webapp/js/gnb-notification_0.0.1.js` | schedule-modified 구독 추가 |
