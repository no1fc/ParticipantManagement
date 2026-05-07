# 관리자 페이지 전면 수정 개발계획

> **작성일:** 2026-05-06  
> **프로젝트:** JobMoa 참여자관리 시스템 v1.3.3  
> **대상:** 관리자 페이지 4개 핵심 기능 재정비  
> **상태:** 개발 전 계획 확정 대기  
> **현재 세션 범위:** 개발계획 문서 정리만 수행하고, 구현은 진행하지 않는다.

---

## 1. 검토 결과 요약

기존 계획은 요구사항의 큰 방향은 맞지만, 실제 코드와 대조했을 때 개발 전에 바로잡아야 할 부분이 있다.

| 구분 | 문제되는 부분 | 재정리 기준 |
|------|--------------|-------------|
| 권한 처리 | `LoginInterceptor`는 `/admin/**` 로그인 여부만 확인하고 관리자/지점관리자 권한은 확인하지 않는다. 기존 계획은 API 권한만 언급하고 페이지 라우팅 권한이 빠져 있다. | `/admin` 페이지와 `/admin/api` 모두 관리자 권한을 확인한다. 일반 상담사는 접근 차단, 지점관리자는 자기 지점 데이터만 조회한다. |
| 사용자 관리 권한 | 상담사 아이디/비밀번호/권한 관리는 민감 기능인데, 기존 계획의 권한 범위가 명확하지 않다. | 사용자 관리 페이지와 관련 API는 `IS_MANAGER=true`인 시스템 관리자만 허용한다. |
| 참여자 페이지 범위 | 요구사항은 "확인 및 Excel 다운로드"인데 기존 화면에는 수정/삭제 액션이 있고 계획에도 정리가 부족하다. | 참여자 관리 페이지에서는 추가/수정/삭제 액션을 제거하고 조회, 상세확인, Excel 다운로드 중심으로 정리한다. |
| DTO/매퍼 가정 | 기존 문서에는 `AdminDTO`가 168개 필드라고 되어 있으나 현재 파일 기준 `private` 필드는 134개다. MyBatis resultType도 `AdminDTO`가 아니라 alias `admin`을 사용한다. | 실제 구조 기준으로 문서화하고, 필요한 필드만 추가한다. 신규 쿼리는 `resultType="admin"`을 사용한다. |
| Excel 다운로드 | JS는 `/admin/api/participants/export`를 호출하지만 컨트롤러가 없다. 또한 현재 JS는 검색 조건을 Excel 요청에 전달하지 않는다. | 신규 Excel endpoint를 만들고, 화면의 지점/상담사/기간/상태 필터를 query string으로 그대로 전달한다. 서버에서 다시 권한 범위를 강제한다. |
| 상담사 필터 | 페이지 1, 3, 4에서 공통으로 필요한 상담사 목록 API가 없다. | `GET /admin/api/counselors`를 공통 API로 추가하고 지점 선택에 따라 상담사 select를 갱신한다. |
| 통계 페이지 | 기존 `/admin/api/kpi`는 전체/지점별 요약만 반환하고 상담사별 분류와 필터가 없다. 지점관리자에게 전체 통계가 노출될 수 있다. | KPI와 신규 상담사별 통계 API 모두 권한 범위와 필터를 적용한다. |
| 알선/이력서 목록 | 기존 쿼리는 알선/이력서 테이블만 조회해 참여자명, 상담사명, 지점 정보가 없다. | `J_참여자관리`, `J_참여자관리_로그인정보`를 JOIN하고 지점/상담사/기간 필터를 적용한다. |
| 자동번호 생성 | `MAX(전담자번호)+1`을 화면에서 조회한 뒤 그대로 저장하면 동시 등록 시 충돌 가능성이 있다. | `next-no`는 표시용으로만 사용하고, 최종 등록 시 서버/SQL에서 번호를 다시 확정한다. |
| 날짜 필터 | `등록일 <= yyyy-MM-dd` 방식은 DB 컬럼이 datetime이면 해당 날짜의 00:00:00 이후 데이터를 누락할 수 있다. | 종료일 필터는 `< DATEADD(day, 1, CAST(#{searchEndDate} AS date))` 패턴으로 적용한다. |

---

## 2. 최종 확정 범위

이번 개발 범위는 아래 4개 요구사항으로 한정한다.

| 번호 | 요구사항 | 대상 화면 | 핵심 결과 |
|------|----------|----------|-----------|
| 1 | 각 지점별, 상담사별 참여자 정보 확인 및 Excel 다운로드 | `/admin/participants` | 지점/상담사/기간/상태 필터, 상담사명 표시, 필터 반영 Excel 다운로드 |
| 2 | 상담사 아이디, 비밀번호, 권한 관리 | `/admin/users` | 시스템 관리자 전용 계정 관리, 자동번호 표시, 아이디 중복체크, 권한/사용여부 관리 |
| 3 | 각 지점별, 상담사별 알선 현황 및 취업자 수 확인 | `/admin` | 권한 범위가 적용된 KPI, 지점/상담사별 종료자/취업자/알선취업자 통계 |
| 4 | 지점별, 상담사별 알선요청 내용 확인 | `/admin/job-placements`, `/admin/resume-requests` | 알선/이력서 요청 목록에 참여자명, 상담사명, 지점, 상태, 등록일 필터 표시 |

이번 범위에서 제외한다.

- 관리자 전체 메뉴 10개를 모두 리디자인하지 않는다.
- 참여자 신규 등록, 참여자 수정, 참여자 삭제 기능을 관리자 참여자 페이지에서 확장하지 않는다.
- 사용자 비밀번호 저장 방식 변경 같은 인증 구조 개선은 이번 범위에 포함하지 않는다.
- React/Vue 같은 신규 프론트엔드 스택을 도입하지 않는다.

---

## 3. 현재 코드 기준 구조

### 3.1 관리자 파일 구성

```text
백엔드
├── src/main/java/com/jobmoa/app/CounselMain/biz/adminpage/AdminDTO.java
├── src/main/java/com/jobmoa/app/CounselMain/biz/adminpage/AdminDAO.java
├── src/main/java/com/jobmoa/app/CounselMain/biz/adminpage/AdminService.java
├── src/main/java/com/jobmoa/app/CounselMain/biz/adminpage/AdminServiceImpl.java
├── src/main/java/com/jobmoa/app/CounselMain/view/adminpage/AdminPageController.java
├── src/main/java/com/jobmoa/app/CounselMain/view/adminpage/AdminApiController.java
└── src/main/resources/mappings/Admin-mapping.xml

프론트엔드
├── src/main/webapp/WEB-INF/admin/*.jsp
├── src/main/webapp/js/adminJs/*_0.0.1.js
└── src/main/webapp/css/adminCss/*_0.0.1.css
```

### 3.2 현재 확인된 사실

- `AdminPageController`는 `/admin`, `/admin/users`, `/admin/participants`, `/admin/job-placements`, `/admin/resume-requests` 등 10개 페이지를 반환한다.
- `AdminApiController`는 `/admin/api` 하위 REST API를 제공하지만, 현재 메서드에 `HttpSession` 기반 관리자 권한 검사가 없다.
- `LoginInterceptor`는 `/admin/**` 요청의 로그인 여부는 확인한다. 다만 `IS_MANAGER`, `IS_BRANCH_MANAGER`에 따른 관리자 권한은 확인하지 않는다.
- 로그인 시 세션에는 `JOBMOA_LOGIN_DATA`, `IS_MANAGER`, `IS_BRANCH_MANAGER`, `IS_PRA_MANAGER`가 저장된다.
- `IS_BRANCH_MANAGER`는 `MemberRoleCheck` 기준 `이사`, `차장`, `본부장`, `총괄`, `팀장`, `파트장` 역할이면 true다.
- `AdminDTO`는 현재 168개 private 필드를 가진 통합 DTO다. (신규 필드 추가 후 173개)
- MyBatis `sql-map-config.xml`에는 `AdminDTO`가 alias `admin`으로 등록되어 있다.
- `Admin-mapping.xml`의 namespace는 `AdminDAO`다.
- Excel 템플릿 `template2.xlsx`는 `src/main/resources/excelTemplates`에 존재한다.

---

## 4. 공통 설계

### 4.1 관리자 권한 정책

| 사용자 유형 | 세션 기준 | 페이지 접근 | 데이터 범위 |
|-------------|----------|-------------|-------------|
| 시스템 관리자 | `IS_MANAGER=true` | 4개 대상 페이지 전체 접근 | 전체 지점, 전체 상담사 |
| 지점 관리자 | `IS_BRANCH_MANAGER=true` | 참여자/대시보드/알선요청 페이지 접근 | 로그인 사용자의 `memberBranch`로 강제 제한 |
| 일반 상담사 | 둘 다 false | 관리자 페이지 접근 불가 | 없음 |

사용자 관리(`/admin/users`, `/admin/api/users/**`)는 시스템 관리자만 접근한다. 지점관리자도 상담사 비밀번호와 권한을 변경할 수 없게 한다.

### 4.2 공통 권한 처리 방식

권한 체크 로직을 각 API에 중복 작성하지 않고 공통 지원 클래스로 분리한다.

| 파일 | 역할 |
|------|------|
| `src/main/java/com/jobmoa/app/CounselMain/view/adminpage/AdminAccessSupport.java` | 세션에서 로그인/권한 정보를 읽고, 관리자 접근 가능 여부와 지점 범위를 판단한다. |
| `AdminPageController.java` | 페이지 진입 시 일반 상담사는 차단하고, 지점관리자의 사용자 관리 페이지 접근도 차단한다. |
| `AdminApiController.java` | API별로 시스템 관리자 전용 또는 관리자/지점관리자 공통 권한을 적용한다. |
| `AdminParticipantExcel.java` | Excel 다운로드 요청에도 동일한 권한/지점 범위를 적용한다. |

구현 시 지켜야 할 규칙:

- 세션 Boolean은 `(boolean)` 캐스팅 대신 `Boolean.TRUE.equals(session.getAttribute("IS_MANAGER"))` 패턴을 사용한다.
- `JOBMOA_LOGIN_DATA`가 없으면 API는 401을 반환한다.
- 관리자 권한이 없으면 API는 403을 반환한다.
- 지점관리자는 요청 파라미터의 `searchBranch` 값을 신뢰하지 않고 항상 `loginBean.getMemberBranch()`로 덮어쓴다.
- 시스템 관리자만 빈 `searchBranch`로 전체 조회할 수 있다.

### 4.3 공통 상담사 목록 API

페이지 1, 3, 4에서 지점 선택 시 상담사 select를 갱신하기 위한 공통 API를 추가한다.

| 항목 | 내용 |
|------|------|
| Endpoint | `GET /admin/api/counselors` |
| Query parameter | `searchBranch` 또는 `branch` |
| 시스템 관리자 | 지점 파라미터가 있으면 해당 지점 상담사, 없으면 전체 사용 상담사 |
| 지점 관리자 | 파라미터와 무관하게 자기 지점 상담사만 반환 |
| 응답 필드 | `userId`, `memberName`, `branch` |

MyBatis 쿼리는 alias `admin`을 사용한다.

```xml
<select id="selectCounselorsByBranch" resultType="admin">
    SELECT
        아이디 AS userId,
        이름 AS memberName,
        지점 AS branch
    FROM J_참여자관리_로그인정보
    WHERE 아이디사용여부 = '사용'
    <if test="searchBranch != null and searchBranch != ''">
        AND 지점 = #{searchBranch}
    </if>
    ORDER BY 지점, 조회순서, 이름
</select>
```

### 4.4 검색 파라미터 표준

기존 `AdminDTO` 필드를 최대한 재사용하고, 부족한 필드만 추가한다.

| 파라미터 | 용도 |
|----------|------|
| `searchBranch` | 지점 필터 |
| `searchCounselor` | 상담사 계정 ID 정확 일치 필터 |
| `searchName` | 참여자명 또는 사용자명 검색 |
| `searchUserId` | 사용자 관리 페이지 아이디 검색 |
| `searchStartDate` | 등록일/실적 시작일 |
| `searchEndDate` | 등록일/실적 종료일 |
| `searchStatus` | 참여자 진행단계 또는 이력서 요청 상태 |
| `searchClosed` | 참여자 마감 여부 |

날짜 종료 조건은 datetime 누락을 막기 위해 아래 기준을 사용한다.

```sql
AND A.등록일 < DATEADD(day, 1, CAST(#{searchEndDate} AS date))
```

### 4.5 프론트엔드 버전 관리

- JS를 수정하면 기존 파일을 직접 덮어쓰지 않고 `{화면명}_0.0.2.js`를 만든다.
- CSS를 수정하는 화면은 `{화면명}_0.0.2.css`를 만든다.
- JSP의 `<script>`와 `<link>` 참조를 신규 버전으로 변경한다.
- CSS 변경이 없는 화면은 JS만 0.0.2로 올린다.

---

## 5. 페이지별 개발계획

## 5.1 페이지 1: 참여자 정보 확인 및 Excel 다운로드

### 목표

관리자 또는 지점관리자가 지점별, 상담사별 참여자 목록을 확인하고 현재 검색 조건 그대로 Excel 파일을 다운로드할 수 있게 한다.

### 화면 변경

| 파일 | 변경 내용 |
|------|----------|
| `src/main/webapp/WEB-INF/admin/adminParticipantManagement.jsp` | 상담사 select 추가, 테이블에 상담사명 컬럼 추가, 추가/수정/삭제 버튼 제거 또는 상세확인 버튼으로 정리 |
| `src/main/webapp/js/adminJs/adminParticipantManagement_0.0.2.js` | 지점 변경 시 상담사 목록 로드, 참여자 조회 조건에 `searchCounselor` 포함, Excel 다운로드 URL에 현재 필터 query string 포함 |
| `src/main/webapp/css/adminCss/adminParticipantManagement_0.0.2.css` | 검색 패널 또는 버튼 배치가 깨지는 경우에만 추가 |

참여자 테이블 기본 컬럼:

| 컬럼 | 데이터 |
|------|--------|
| 구직번호 | `jobNo` |
| 참여자명 | `participantName` |
| 생년월일 | `birthDate` |
| 성별 | `gender` |
| 지점 | `branch` |
| 상담사 | `counselorName` + `counselorAccount` |
| 진행단계 | `progressStage` |
| 등록일 | `participantRegDate` |
| 마감 | `closed` |

### 백엔드 변경

| 파일 | 변경 내용 |
|------|----------|
| `AdminDTO.java` | `searchCounselor`, `counselorName`, `recruitPath`, `participationType`, `employmentDate`, `employmentType`, `employerName` 등 Excel 핵심 컬럼에 필요한 누락 필드 추가 |
| `AdminDAO.java` | 화면 목록용 `selectParticipantList`와 별도로 Excel 컬럼을 조회하는 `selectParticipantExcelList(AdminDTO dto)` 추가 |
| `AdminService.java` / `AdminServiceImpl.java` | `getParticipantList(AdminDTO dto)`, `getParticipantExcelList(AdminDTO dto)`에 권한 적용 후 DAO 호출 |
| `AdminApiController.java` | `GET /admin/api/participants`에 권한 체크와 지점 범위 강제 적용 |
| `Admin-mapping.xml` | `selectParticipantList`에 상담사 JOIN, `searchCounselor`, 날짜 필터, 지점 범위 적용 |
| 신규 `AdminParticipantExcel.java` | `GET /admin/api/participants/export` 구현 |

참여자 목록 쿼리 변경 방향:

```sql
FROM J_참여자관리 A
LEFT JOIN J_참여자관리_로그인정보 M ON A.전담자_계정 = M.아이디
WHERE 1=1
  -- searchBranch, searchCounselor, searchName, searchStatus, searchClosed, searchStartDate, searchEndDate 적용
```

Excel 다운로드 기준:

- Endpoint: `GET /admin/api/participants/export`
- 파일명: `관리자_참여자목록_yyyy-MM-dd.xlsx`
- 데이터 범위: 화면 검색 조건과 서버 권한 범위를 모두 적용한 참여자 목록
- 지점관리자: 요청 query string에 다른 지점이 있어도 자기 지점만 다운로드
- 컬럼: 화면 목록 컬럼에 등록일, 모집경로, 참여유형, 취창업일, 취업유형 등 관리자 확인에 필요한 핵심 필드를 추가
- 응답: `Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`

---

## 5.2 페이지 2: 상담사 ID, 비밀번호, 권한 관리

### 목표

시스템 관리자가 상담사 계정의 아이디, 초기 비밀번호, 권한, 관리자권한, 사용여부를 관리할 수 있게 한다.

### 권한

| 사용자 유형 | 접근 |
|-------------|------|
| 시스템 관리자 | 허용 |
| 지점 관리자 | 차단 |
| 일반 상담사 | 차단 |

### 화면 변경

| 파일 | 변경 내용 |
|------|----------|
| `src/main/webapp/WEB-INF/admin/adminUserManagement.jsp` | 전담자번호 자동생성 표시, 아이디 중복체크 결과 영역 추가, 권한/관리자권한/사용여부 필드 명확화 |
| `src/main/webapp/js/adminJs/adminUserManagement_0.0.2.js` | 추가 모달 열 때 `next-no` 조회, 아이디 blur 또는 버튼 클릭 시 중복체크, 저장 전 중복확인 통과 여부 검증 |
| `src/main/webapp/css/adminCss/adminUserManagement_0.0.2.css` | 중복체크 피드백 표시가 필요한 경우 추가 |

### 백엔드 변경

| 파일 | 변경 내용 |
|------|----------|
| `Admin-mapping.xml` | `selectNextMemberNo`, `selectUserIdExists` 추가, `insertUser`의 전담자번호 할당 안정화 |
| `AdminDAO.java` | `selectNextMemberNo()`, `selectUserIdExists(AdminDTO dto)` 추가 |
| `AdminService.java` / `AdminServiceImpl.java` | `getNextMemberNo()`, `checkUserIdExists()`, 시스템 관리자 권한 전제의 사용자 저장 로직 정리 |
| `AdminApiController.java` | `GET /admin/api/users/next-no`, `GET /admin/api/users/check-id` 추가, 기존 사용자 CRUD와 비밀번호 초기화 API에 시스템 관리자 권한 적용 |

자동번호 처리 기준:

- `GET /admin/api/users/next-no`는 화면 표시용이다.
- 최종 저장 시 클라이언트에서 받은 `memberNo`를 그대로 신뢰하지 않는다.
- 신규 사용자 등록은 서버에서 전담자번호를 다시 확정한다.
- MSSQL 기준으로 `MAX(전담자번호)+1`을 사용할 경우 동시 등록 충돌을 줄이기 위해 최종 INSERT 단계에서 잠금 또는 재조회 방식을 적용한다.

아이디 중복체크 기준:

| 상황 | 기준 |
|------|------|
| 신규 등록 | 동일 `아이디`가 있으면 저장 차단 |
| 수정 | 자기 자신의 기존 아이디는 중복으로 보지 않음 |
| 빈 아이디 | 프론트와 백엔드 모두 검증 실패 처리 |

비밀번호 기준:

- 신규 등록 기본값은 기존 화면과 동일하게 `jobmoa100!`를 사용한다.
- 수정 API는 기존처럼 비밀번호를 직접 변경하지 않는다.
- 비밀번호 초기화는 기존 `PUT /admin/api/users/{userNo}/reset-password`를 유지하고 시스템 관리자 권한을 추가한다.

---

## 5.3 페이지 3: 알선 현황 및 취업자 수 확인

### 목표

관리자 또는 지점관리자가 지점별, 상담사별 종료자 수, 취업자 수, 알선취업자 수, 취업률을 확인할 수 있게 한다.

### 화면 변경

| 파일 | 변경 내용 |
|------|----------|
| `src/main/webapp/WEB-INF/admin/adminTotalDashboard.jsp` | 지점/상담사/기간 검색 패널 추가, 상담사별 통계 테이블 추가, 기존 KPI 카드 유지 |
| `src/main/webapp/js/adminJs/adminTotalDashboard_0.0.2.js` | 상담사 목록 로드, KPI/통계 API에 필터 전달, 통계 테이블 렌더링, 차트 데이터 필터 반영 |
| `src/main/webapp/css/adminCss/adminTotalDashboard_0.0.2.css` | 통계 테이블과 검색 패널 배치 보정 |

통계 테이블 컬럼:

| 컬럼 | 설명 |
|------|------|
| 지점 | 참여자 지점 |
| 상담사ID | `전담자_계정` |
| 상담사명 | 로그인정보 이름 |
| 종료자 수 | 기존 `selectBranchParticipantStats`의 종료 판정 기준 활용 |
| 취업자 수 | 기존 취업 판정 기준 활용 |
| 알선취업자 수 | 취업자 중 `취업유형='알선'` |
| 취업률 | `취업자 수 / 종료자 수 * 100`, 종료자 0이면 0.0 |

### 백엔드 변경

| 파일 | 변경 내용 |
|------|----------|
| `AdminDTO.java` | `searchCounselor`, `counselorName`, `completedCount`, `employedCount`, `placementEmployedCount` 추가. 기존 `employmentRate`는 재사용 |
| `AdminDAO.java` | `selectPlacementStatsByCounselor(AdminDTO dto)` 추가, 기존 `selectDashboardKpi()`와 `selectBranchParticipantStats()`는 필터를 받도록 `AdminDTO dto` 파라미터를 추가 |
| `AdminService.java` / `AdminServiceImpl.java` | `getPlacementStatsByCounselor(AdminDTO dto)`, 필터형 `getDashboardData(AdminDTO dto)` 추가 |
| `AdminApiController.java` | `GET /admin/api/placement-stats` 추가, `GET /admin/api/kpi`에 권한/필터 적용 |
| `Admin-mapping.xml` | 기존 CTE를 기반으로 상담사별 GROUP BY 쿼리 추가, 기존 KPI/지점 통계에도 권한 범위 적용 |

기간 기준:

- 기본값은 기존 대시보드와 동일하게 실적연도 기준을 사용한다.
- 검색 시작일/종료일이 들어오면 해당 기간을 우선 적용한다.
- 종료자 판정은 기존 `실제종료일` 계산 방식을 유지한다.
- 취업자와 알선취업자 판정은 기존 `취업_F`, `알선_F` 기준을 유지한다.

---

## 5.4 페이지 4: 지점별, 상담사별 알선요청 내용 확인

### 목표

알선상세정보와 이력서 요청 목록에서 참여자명, 지점, 상담사 정보를 함께 확인하고 지점/상담사 기준으로 필터링할 수 있게 한다.

기존 구조상 알선 관련 화면이 2개로 분리되어 있으므로 이번 계획에서도 기존 화면을 유지한다.

| 화면 | URL | 데이터 |
|------|-----|--------|
| 알선 관리 | `/admin/job-placements` | `J_참여자관리_알선상세정보` |
| 이력서 요청 | `/admin/resume-requests` | `J_참여자관리_이력서요청양식` |

### 화면 변경

| 파일 | 변경 내용 |
|------|----------|
| `src/main/webapp/WEB-INF/admin/adminJobPlacement.jsp` | 지점/상담사/기간 검색 패널 추가, 테이블에 참여자명/지점/상담사 컬럼 추가 |
| `src/main/webapp/js/adminJs/adminJobPlacement_0.0.2.js` | 검색 필터 전달, 상담사 cascading, 테이블 컬럼 변경 |
| `src/main/webapp/WEB-INF/admin/adminResumeRequest.jsp` | 지점/상담사/기간/상태 검색 패널 추가, 테이블과 상세 모달에 참여자/상담사 정보 추가 |
| `src/main/webapp/js/adminJs/adminResumeRequest_0.0.2.js` | 검색 필터 전달, 상담사 cascading, 테이블/상세 데이터 매핑 변경 |
| `src/main/webapp/css/adminCss/adminJobPlacement_0.0.2.css` | 검색 패널 보정이 필요한 경우 추가 |
| `src/main/webapp/css/adminCss/adminResumeRequest_0.0.2.css` | 검색 패널 보정이 필요한 경우 추가 |

### 백엔드 변경

| 파일 | 변경 내용 |
|------|----------|
| `AdminDTO.java` | `searchCounselor`, `counselorName`, `companyPrivacyConsent`, `managerPrivacyConsent`, `marketingConsent` 추가. `participantName`, `counselorAccount`, `branch`는 기존 필드 재사용 |
| `Admin-mapping.xml` | `selectJobPlacementList`, `selectJobPlacementOne`, `selectResumeRequestList`, `selectResumeRequestOne`에 참여자/상담사 JOIN과 필터 적용 |
| `AdminApiController.java` | `GET /admin/api/job-placements`, `GET /admin/api/resume-requests` 및 상세 API에 권한/지점 범위 적용 |

알선상세정보 목록 쿼리 방향:

```sql
FROM J_참여자관리_알선상세정보 A
LEFT JOIN J_참여자관리 P ON A.구직번호 = P.구직번호
LEFT JOIN J_참여자관리_로그인정보 M ON P.전담자_계정 = M.아이디
WHERE 1=1
  -- P.지점, P.전담자_계정, A.등록일 기간 필터 적용
```

이력서 요청 목록 쿼리 방향:

```sql
FROM J_참여자관리_이력서요청양식 R
LEFT JOIN J_참여자관리 P ON R.구직번호 = P.구직번호
LEFT JOIN J_참여자관리_로그인정보 M ON P.전담자_계정 = M.아이디
WHERE 1=1
  -- P.지점, P.전담자_계정, R.등록일 기간, R.상태 필터 적용
```

상세 조회도 목록과 같은 지점 권한을 적용한다. 지점관리자가 URL의 등록번호를 직접 바꿔 다른 지점 데이터를 조회하지 못하게 한다.

---

## 6. 수정 대상 파일 목록

### 6.1 백엔드

| 파일 경로 | 변경 유형 | 내용 |
|----------|----------|------|
| `src/main/java/com/jobmoa/app/CounselMain/view/adminpage/AdminAccessSupport.java` | 신규 | 관리자 세션/권한/지점 범위 공통 처리 |
| `src/main/java/com/jobmoa/app/CounselMain/view/adminpage/AdminPageController.java` | 수정 | 페이지 진입 권한 체크 |
| `src/main/java/com/jobmoa/app/CounselMain/view/adminpage/AdminApiController.java` | 수정 | API 권한 체크, 상담사 API, 사용자 보조 API, 통계 API 추가 |
| `src/main/java/com/jobmoa/app/CounselMain/view/report/AdminParticipantExcel.java` | 신규 | 관리자 참여자 목록 Excel 다운로드 |
| `src/main/java/com/jobmoa/app/CounselMain/biz/adminpage/AdminDTO.java` | 수정 | 검색/상담사/통계/이력서 상세 누락 필드 추가 |
| `src/main/java/com/jobmoa/app/CounselMain/biz/adminpage/AdminDAO.java` | 수정 | 신규 select 메서드 추가 |
| `src/main/java/com/jobmoa/app/CounselMain/biz/adminpage/AdminService.java` | 수정 | 신규 서비스 메서드 추가 |
| `src/main/java/com/jobmoa/app/CounselMain/biz/adminpage/AdminServiceImpl.java` | 수정 | 신규 서비스 구현 |
| `src/main/resources/mappings/Admin-mapping.xml` | 수정 | 상담사 조회, 참여자 JOIN/필터, 사용자 보조 쿼리, 상담사별 통계, 알선/이력서 JOIN 쿼리 |
| `src/main/resources/sql-map-config.xml` | 확인 | 기존 `admin` alias와 `Admin-mapping.xml` 등록이 이미 있으므로 신규 DTO를 만들지 않는 한 수정하지 않는다. |
| `src/main/java/com/jobmoa/app/config/RootConfig.java` | 확인 | 신규 파일을 기존 스캔 패키지에 두므로 수정하지 않는다. |
| `src/main/java/com/jobmoa/app/config/WebMvcConfig.java` | 확인 | 신규 파일을 기존 스캔 패키지에 두므로 수정하지 않는다. |

### 6.2 프론트엔드

| 파일 경로 | 변경 유형 |
|----------|----------|
| `src/main/webapp/WEB-INF/admin/adminParticipantManagement.jsp` | 수정 |
| `src/main/webapp/WEB-INF/admin/adminUserManagement.jsp` | 수정 |
| `src/main/webapp/WEB-INF/admin/adminTotalDashboard.jsp` | 수정 |
| `src/main/webapp/WEB-INF/admin/adminJobPlacement.jsp` | 수정 |
| `src/main/webapp/WEB-INF/admin/adminResumeRequest.jsp` | 수정 |
| `src/main/webapp/js/adminJs/adminParticipantManagement_0.0.2.js` | 신규 |
| `src/main/webapp/js/adminJs/adminUserManagement_0.0.2.js` | 신규 |
| `src/main/webapp/js/adminJs/adminTotalDashboard_0.0.2.js` | 신규 |
| `src/main/webapp/js/adminJs/adminJobPlacement_0.0.2.js` | 신규 |
| `src/main/webapp/js/adminJs/adminResumeRequest_0.0.2.js` | 신규 |
| `src/main/webapp/css/adminCss/*_0.0.2.css` | 화면 레이아웃 수정이 필요한 경우에만 신규 |

---

## 7. 구현 순서

```text
1단계: 공통 권한 기반 정리
  - AdminAccessSupport 추가
  - AdminPageController 페이지 접근 제어
  - AdminApiController API 접근 제어 패턴 적용

2단계: 공통 필터 기반 추가
  - AdminDTO에 searchCounselor, counselorName 등 공통 필드 추가
  - 상담사 목록 API/DAO/Service/Mapper 추가
  - 프론트 공통 패턴: 지점 변경 시 상담사 select 갱신

3단계: 사용자 관리 페이지
  - 시스템 관리자 전용 처리
  - next-no, check-id API 추가
  - 사용자 저장 전 검증 강화
  - adminUserManagement_0.0.2.js 반영

4단계: 참여자 정보 및 Excel 페이지
  - 참여자 목록 JOIN/필터 보강
  - 수정/삭제 액션 제거 또는 상세확인으로 변경
  - AdminParticipantExcel 추가
  - adminParticipantManagement_0.0.2.js 반영

5단계: 알선요청 내용 페이지
  - 알선상세정보/이력서요청 목록 JOIN/필터 보강
  - 상세 조회 권한 범위 적용
  - adminJobPlacement_0.0.2.js, adminResumeRequest_0.0.2.js 반영

6단계: 알선 현황 및 취업자 수 대시보드
  - KPI 권한 범위 적용
  - 상담사별 통계 API 추가
  - 통계 테이블/차트 필터 반영
  - adminTotalDashboard_0.0.2.js 반영

7단계: 검증
  - compile/test
  - 권한별 수동 테스트
  - Excel 다운로드 파일 검증
```

---

## 8. 검증 기준

### 8.1 빌드 검증

```bash
./mvnw clean compile
./mvnw test
```

테스트가 없는 영역은 compile 성공과 수동 시나리오 검증으로 보완한다.

### 8.2 권한 검증

| 계정 유형 | 검증 내용 |
|-----------|----------|
| 시스템 관리자 | 4개 대상 페이지 접근 가능, 전체 지점/상담사 조회 가능 |
| 지점 관리자 | 참여자/대시보드/알선요청 페이지 접근 가능, 자기 지점만 조회 가능 |
| 지점 관리자 | `/admin/users`와 `/admin/api/users/**` 접근 차단 |
| 일반 상담사 | `/admin/**` 페이지 접근 차단 |
| 일반 상담사 | `/admin/api/**` API 접근 시 403 |

### 8.3 페이지별 검증

| 페이지 | 검증 항목 |
|--------|-----------|
| 참여자 정보 | 지점 선택 시 상담사 목록 변경, 상담사 필터 적용, 수정/삭제 액션 미노출, Excel 파일 내용이 화면 필터와 일치 |
| 사용자 관리 | 자동번호 표시, 아이디 중복체크, 신규 등록, 수정, 삭제, 비밀번호 초기화, 시스템 관리자 권한만 허용 |
| 대시보드 | 지점/상담사/기간 필터 적용, 상담사별 종료자/취업자/알선취업자 집계, 취업률 0 나누기 방지 |
| 알선 관리 | 참여자명/지점/상담사 표시, 필터 적용, 상세 조회 권한 범위 적용 |
| 이력서 요청 | 참여자명/지점/상담사/상태 표시, 필터 적용, 상태 변경 권한 범위 적용 |

### 8.4 데이터 검증

- 지점관리자가 query string으로 다른 지점을 넘겨도 자기 지점 데이터만 반환된다.
- 종료일 필터가 해당 날짜 전체 데이터를 포함한다.
- 상담사 미선택 시 지점 전체 상담사 데이터가 조회된다.
- 검색 결과가 0건이어도 화면과 Excel 응답이 오류 없이 처리된다.
- Excel 파일명 한글이 깨지지 않는다.

---

## 9. 개발 전 확정된 결정 사항

1. 사용자 관리 페이지는 시스템 관리자 전용으로 개발한다.
2. 참여자 정보 페이지는 조회와 Excel 다운로드 중심으로 정리하고, 관리자 화면에서 참여자 삭제 기능은 제거한다.
3. 지점관리자는 모든 조회성 관리자 기능에서 자기 지점으로 강제 제한한다.
4. Excel 다운로드는 화면 필터와 동일한 조건을 서버에서 다시 적용한다.
5. 알선요청 내용 확인은 기존 2개 화면(`/admin/job-placements`, `/admin/resume-requests`)을 유지하면서 공통 필터와 JOIN 정보를 추가한다.
6. 신규 파일은 기존 component scan 범위 안의 패키지에만 추가한다.
7. 이번 세션에서는 이 문서만 확정하고, 실제 Java/JSP/JS/CSS 구현은 다음 작업에서 진행한다.
