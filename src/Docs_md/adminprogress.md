# 관리자 페이지 연동 작업 진행상황

> 작업일자: 2026-03-25
> 브랜치: Page_Recruitment_information_1.3.3

---

## 발견된 문제점 목록

### 🔴 버그 (즉시 수정 필요)

| 페이지 | 문제 | 상태 |
|--------|------|------|
| adminBranchManagement.jsp | JS `editBranch()`/`saveBranch()`에서 잘못된 element ID 참조: `branchType1`, `branchType2`, `branchBusiness`, `branchOrder` → HTML 실제 ID: `type1`, `type2`, `department`, `order` | ✅ 수정완료 |
| adminStandardAmount.jsp | JS `editStandardAmount()`/`saveStandardAmount()`에서 잘못된 ID: `saCategory`→`division`, `saYear`→`year`, `saMinWage`→`minWage`, `saMaxWage`→`maxWage`, `saSuccessFee`→`successFee` | ✅ 수정완료 |
| adminStandardAmount.jsp | JS `editBetterWage()`/`saveBetterWage()`에서 잘못된 ID: `bwYear`→`betterYear`, `bwYearDate`→`betterYearDate`, `bwWage`→`betterWage` | ✅ 수정완료 |
| adminJobPlacement.jsp | JS `viewDetail()`/`editJobPlacement()`에서 `$('#jobNoField')` → HTML에 `id="jobNo"` | ✅ 수정완료 |
| adminJobPlacement.jsp | JS `loadJobPlacements()`에서 테이블 컬럼 수 불일치 (7 헤더 vs 6 JS 컬럼, recommendation 누락) | ✅ 수정완료 |
| adminResumeRequest.jsp | JS `loadResumeRequests()`에서 수정일 컬럼 누락 (10 헤더 vs 9 JS 컬럼) | ✅ 수정완료 |
| adminParticipantManagement.jsp | Form 필드 ID mismatch: `recruitmentPath` → 실제 DTO 필드명 `recruitPath` | ✅ 수정완료 |
| adminParticipantManagement_0.0.1.js | `viewDetail()` 함수가 존재하지 않는 URL로 redirect | ✅ 수정완료 |

### 🟡 미연동 (데이터 하드코딩)

| 페이지 | 문제 | 상태 |
|--------|------|------|
| adminParticipantManagement.jsp | 테이블 목데이터 하드코딩, 지점 드롭다운 미연동 | ✅ 수정완료 |
| adminUserManagement.jsp | 테이블 목데이터 하드코딩, 지점 드롭다운 미연동 | ✅ 수정완료 |
| adminBranchManagement.jsp | 테이블/카드 목데이터 하드코딩 | ✅ 수정완료 |
| adminDailyReport.jsp | 테이블 목데이터 하드코딩, 통계카드 하드코딩 | ✅ 수정완료 |
| adminStandardAmount.jsp | 테이블 목데이터 하드코딩 | ✅ 수정완료 |
| adminAssignmentHistory.jsp | 테이블 목데이터 하드코딩 | ✅ 수정완료 |
| adminJobPlacement.jsp | 테이블 목데이터 하드코딩 | ✅ 수정완료 |
| adminResumeRequest.jsp | 테이블 목데이터 하드코딩 | ✅ 수정완료 |
| adminCertificateTraining.jsp | 테이블 목데이터 하드코딩 | ✅ 수정완료 |

### 🟠 폼 필드 불일치

| 페이지 | 문제 | 상태 |
|--------|------|------|
| adminParticipantManagement.jsp | 학교명, 전공 필드 → DB에 없는 컬럼 | ✅ 제거완료 |
| adminParticipantManagement.jsp | 취창업일, 취업처, 취업유형, 임금 필드 → AdminDTO에 없는 필드 | ✅ 제거완료 |
| adminParticipantManagement.jsp | 특정계층, 취업역량, 희망직무, 희망급여 필드 → DB에 있지만 form에 없음 | ✅ 추가완료 |

### 🔵 GNB/네비게이션

| 페이지     | 문제 | 상태 |
|---------|------|------|
| gnb.tag | 사이드바에 관리자 메뉴 없음 | ✅ 추가완료 |

---

## 수정 내역

### adminParticipantManagement.jsp
- 하드코딩 목데이터 행 제거
- Form 필드 수정:
  - `id="recruitmentPath"` → `id="recruitPath"`
  - 학교명, 전공 섹션 제거 (DB 미존재)
  - 취창업일, 취업처, 취업유형, 임금 제거 (AdminDTO 미존재)
  - 특정계층(`specialClass`), 취업역량(`employmentCapacity`), 희망직무(`desiredJob`), 희망급여(`desiredSalary`) 추가
- 지점 AJAX 로딩 스크립트 추가

### adminParticipantManagement_0.0.1.js
- `viewDetail()` 함수: 잘못된 URL redirect → 모달 read-only 표시로 변경
- `editParticipant()` 함수: 새 필드 ID 반영 (specialClass, employmentCapacity, desiredJob, desiredSalary)
- `loadBranchOptions()` 함수 추가, 페이지 로드 시 호출

### adminUserManagement.jsp
- 하드코딩 목데이터 행 제거 (2행)
- 지점 AJAX 로딩 스크립트 추가 (검색 패널 + 모달)

### adminBranchManagement.jsp
- 하드코딩 카드뷰 3개 제거, 동적 카드 렌더링으로 교체
- 하드코딩 테이블 목데이터 3행 제거
- JS 함수 `editBranch()`/`saveBranch()` element ID 수정: `branchType1`→`type1`, `branchType2`→`type2`, `branchBusiness`→`department`, `branchOrder`→`order`
- `loadBranches()` 에서 카드뷰도 함께 렌더링하도록 수정

### adminDailyReport.jsp
- 하드코딩 목데이터 행 제거
- 통계카드 4개를 동적 데이터로 업데이트하는 로직 추가

### adminStandardAmount.jsp
- 하드코딩 목데이터 행 제거 (2개 테이블)
- JS ID 수정:
  - `saCategory`→`division`, `saYear`→`year`, `saMinWage`→`minWage`, `saMaxWage`→`maxWage`, `saSuccessFee`→`successFee`
  - `bwYear`→`betterYear`, `bwYearDate`→`betterYearDate`, `bwWage`→`betterWage`

### adminAssignmentHistory.jsp
- 하드코딩 목데이터 행 제거 (2개 테이블)

### adminJobPlacement.jsp
- 하드코딩 목데이터 행 제거
- JS `viewDetail()`/`editJobPlacement()` `$('#jobNoField')` → `$('#jobNo')`
- JS `loadJobPlacements()` recommendation 컬럼 추가 (7번째 컬럼)

### adminResumeRequest.jsp
- 하드코딩 목데이터 행 제거
- JS `loadResumeRequests()` `resumeUpdateDate` 컬럼 추가

### adminCertificateTraining.jsp
- 하드코딩 목데이터 행 제거 (2개 테이블)

### adminGnb.tag
- 사이드바에 관리자(IS_MANAGER 권한) 메뉴 추가:
  - 대시보드 (/admin)
  - 사용자관리 (/admin/users)
  - 지점관리 (/admin/branches)
  - 참여자관리 (/admin/participants)
  - 일일업무보고 (/admin/daily-reports)
  - 기준금액관리 (/admin/standard-amounts)
  - 배정히스토리 (/admin/assignment-history)
  - 알선관리 (/admin/job-placements)
  - 이력서요청관리 (/admin/resume-requests)
  - 자격증/직업훈련 (/admin/certificate-training)

---

## 미해결 항목

- `adminParticipantManagement.jsp` 참여자 추가 기능: 현재 관리자 패널에서 추가는 불가 (메인 상담화면에서만 가능), 조회/삭제만 지원
- `adminDailyReport.jsp` 통계카드: 현재 API가 목록만 반환, 합계 없음. JS에서 합산 처리
- 엑셀 다운로드 API (`/admin/api/participants/export`, `/admin/api/users/export`) 미구현 (별도 작업 필요)