# 신규/수정 참여자 페이지 테이블(게시판) 형태 리팩토링 작업 문서

- **작성일**: 2026-04-16
- **브랜치**: `Page_Recruitment_information_1.3.3`
- **작업 배경**: AI 채용공고 추천 기능 추가에 맞춰, 기존 카드(card-modern) 형태로 구성된 참여자 등록/수정 화면을 **게시판(테이블) 형태**로 개편하여 정보 밀도와 가독성을 높이고 추천 키워드/알선 영역과 시각적 위계를 분리한다.

---

## 1. 대상 파일

### 1.1 직접 수정 대상 (JSP 페이지)
| 구분 | 경로 | 비고 |
|------|------|------|
| 신규 등록 | `src/main/webapp/WEB-INF/views/NewParticipantsPage.jsp` | `form action="/newparticipant.login"` |
| 수정 | `src/main/webapp/WEB-INF/views/UpdateParticipantsPage.jsp` | `form action="/participantUpdate.login"` |

### 1.2 직접 수정 대상 (Tag 파일 - 실제 구조 변경 위치)
| Tag | 경로 | 현재 라인수 |
|-----|------|-------------|
| 기본정보 | `src/main/webapp/WEB-INF/tags/ParticipantBasic.tag` | 110 |
| 상담정보 | `src/main/webapp/WEB-INF/tags/ParticipantCounsel.tag` | 281 |
| 취업정보 | `src/main/webapp/WEB-INF/tags/ParticipantEmployment.tag` | 98 |

> **핵심**: 두 JSP 페이지는 `<mytag:ParticipantBasic/>`, `<mytag:ParticipantCounsel/>`, `<mytag:ParticipantEmployment/>`를 각각 포함하는 얇은 레이아웃 파일이다. 따라서 **실제 카드→테이블 변환은 3개의 Tag 파일에서 이뤄지며**, JSP 파일 자체는 상단 타이틀 영역/스크립트 소폭 조정만 필요하다.

### 1.3 참고/연관 파일 (수정 최소화)
- `src/main/resources/static/css/participantCss/custom-modern_0.0.1.css` — `.card-modern`, `.default-card-body` 관련 스타일. 테이블 신규 CSS는 별도 파일로 분리 권장.
- `src/main/resources/static/js/participants_insert_update_CommonnessJS_0.1.1.js` — 폼 검증/제출 JS. **input id/name은 절대 변경 금지**(이 JS가 직접 참조).
- `src/main/resources/static/js/jobCategorySelectRenderText_0.0.2.js`, `educationDiv_0.0.2.js`, `particcertifDiv_0.0.2.js`, `recommendJS_0.0.1.js`, `participantKeyWordInputJS_0.1.2.js` — 동적 DIV(자격증/교육/키워드/추천) 렌더링. DOM 삽입 지점 id(`particcertifCertif`, `education`, `keywords-container`, `basicSchoollist` 등) **유지 필수**.

---

## 2. 현재 구조 분석

### 2.1 공통 패턴 (3개 Tag 모두 동일)
```
<div class="row card-modern [mt-3]">
  <div class="card-header">
    <h1 class="card-title">{섹션명}</h1>
    <div class="card-tools">
      <button data-lte-toggle="card-collapse">…</button>
      <input type="hidden" id="{section}JobNo" …>
    </div>
  </div>
  <div class="card-body">
    <div class="row [default-card-body]">
      <div class="col-md-1 w-auto"> <label/><input or select/> </div>
      …반복…
    </div>
  </div>
</div>
```
- Bootstrap 5 Grid (`col-md-1 w-auto`)로 **라벨 위 / 입력 아래** 세로 배치
- 필수 항목은 `<span class="text-danger">*</span>` 접미
- `datepicker-div`, `recommend`(자동완성 목록), `particcertif-div`, `education-div`, `hidden-div`(알선 상세) 등 특수 블록 존재

### 2.2 섹션별 필드 목록

#### ① 기본정보 (ParticipantBasic.tag)
| # | label | id/name | type | 필수 | 비고 |
|---|-------|---------|------|-----|------|
| 1 | 참여자 | basicPartic | text | ✅ | |
| 2 | 생년월일 | basicDob | datepicker | ✅ | |
| 3 | 성별 | basicGender | select | - | 남/여 |
| 4 | 모집경로 | basicRecruit | select | - | 센터배정 외 5 |
| 5 | 참여유형 | basicPartType | select | - | 1/2 |
| 6 | 주소(시,구) | basicAddress | text(readonly) | ✅ | Kakao 우편번호 |
| 7 | 학교명 | basicSchool | text+자동완성 | ✅ | `#basicSchoollist` |
| 8 | 학력 | basicEducation | select | ✅ | 고졸~대학원 |
| 9 | 전공 | basicSpecialty | text+자동완성 | ✅ | `#basicSpecialtylist` |
| 10 | 경력(년) | basicAntecedents | number(0~40) | - | |
| 11 | 특정계층 | basicSpecific | select | - | O/X |
| 12 | **자격증** | particcertifCertif | 동적 DIV | - | +/- 버튼으로 N개 |
| - | (숨김) 채용번호 | basicJobNo | hidden | - | |

#### ② 상담정보 (ParticipantCounsel.tag)
- 취업역량(A~D), 최근상담일, 진행단계(12종), 초기상담일, 구직만료일, IAP수립일, IAP 3개월차(disabled), IAP 5개월차(disabled), 기간만료예정일*, 직무카테고리-대분류*, 직무카테고리-중분류*, 희망직무*, 희망월급*, 알선요청, 간접고용서비스, 수당지급일
- **교육내역** (동적 DIV: `#education`)
- **숨김 영역 `#hiddenDiv`** (알선요청=희망 시 노출)
  - 키워드 UI (`#keywords-container`, `#suggested-keywords-container`)
  - 알선 상세 정보 textarea (`#jobPlacementTextArea`, height 300px)
  - 추천사 textarea (`#suggestionTextArea`, height 300px)

#### ③ 취업정보 (ParticipantEmployment.tag)
- 취창업일*, 취창업처리일, 퇴사일, 취업유형*(동적 옵션), 취업처, 임금*, 직무, 취업인센티브_구분*(11종), 일경험분류(4종), 기타, 메모(textarea 3행)

---

## 3. 목표(To-Be) 테이블 구조

### 3.1 디자인 원칙
1. **게시판형 2열 테이블**: 관리자 상세보기 화면처럼 `<label> | <input>` 가로 쌍을 테이블 셀로 배치.
2. **반응형**: 데스크톱은 4열(라벨/입력/라벨/입력), 태블릿은 2열, 모바일은 1열로 자동 변환.
3. **섹션 헤더 유지**: 기존 `card-header`의 타이틀/접기 버튼은 유지하되, `card-body` 내부만 테이블로 전환.
4. **동적 DIV 영역은 셀 통합(colspan)**: 자격증/교육내역/키워드/알선 상세는 한 행 전체(colspan=4) 사용.
5. **id/name 불변**: JS 연동 보존을 위해 모든 입력 요소의 id/name은 기존과 **정확히 동일** 유지.

### 3.2 권장 HTML 스켈레톤
```html
<div class="row card-modern">
  <div class="card-header">
    <h1 class="card-title">기본정보</h1>
    <div class="card-tools"> … 기존 유지 … </div>
  </div>
  <div class="card-body p-0">
    <table class="table table-bordered participant-detail-table mb-0">
      <colgroup>
        <col style="width:12%"><col style="width:38%">
        <col style="width:12%"><col style="width:38%">
      </colgroup>
      <tbody>
        <tr>
          <th scope="row"><label for="basicPartic">참여자<span class="text-danger">*</span></label></th>
          <td><input type="text" class="form-control form-control-sm" id="basicPartic" name="basicPartic" value="..."></td>
          <th scope="row"><label for="basicDob">생년월일<span class="text-danger">*</span></label></th>
          <td> <div class="input-group input-group-sm datepicker-div"> … </div> </td>
        </tr>
        <!-- … 필드 쌍 반복 … -->
        <tr>
          <th scope="row"><label>자격증</label></th>
          <td colspan="3">
            <div class="particcertif-div-header"> … +/- 버튼 … </div>
            <div class="particcertif-div-content d-flex" id="particcertifCertif"></div>
            <div class="overflow-y-scroll recommend" id="basicParticcertiflist"></div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
```

### 3.3 섹션별 행(Row) 배치안

#### ① 기본정보 (7행)
| 행 | 좌측 Label | 좌측 Input | 우측 Label | 우측 Input |
|----|-----------|-----------|-----------|-----------|
| 1 | 참여자* | basicPartic | 생년월일* | basicDob |
| 2 | 성별 | basicGender | 모집경로 | basicRecruit |
| 3 | 참여유형 | basicPartType | 특정계층 | basicSpecific |
| 4 | 주소(시,구)* | basicAddress | 경력(년) | basicAntecedents |
| 5 | 학교명* | basicSchool (+ recommend) | 학력* | basicEducation |
| 6 | 전공* | basicSpecialty (+ recommend) | — | — |
| 7 | 자격증 | **colspan=3** (particcertif-div 전체) | | |

#### ② 상담정보 (11행)
| 행 | 좌 Label | 좌 Input | 우 Label | 우 Input |
|----|---------|---------|---------|---------|
| 1 | 취업역량* | counselJobSkill | 진행단계 | counselProgress |
| 2 | 초기상담일 | counselInItCons | 최근상담일 | counselLastCons |
| 3 | IAP수립일 | counselIAPDate | 기간만료예정일* | counselEXPDate |
| 4 | IAP 3개월차 | counselIAP3Month(disabled) | IAP 5개월차 | counselIAP5Month(disabled) |
| 5 | 구직만료일 | counselJobEX | 수당지급일 | counselAllowancePayment |
| 6 | 직무카테고리-대* | jobCategoryLarge | 직무카테고리-중* | jobCategoryMid |
| 7 | 희망직무* | counselJobWant | 희망월급(만원)* | counselSalWant |
| 8 | 알선요청 | counselPlacement | 간접고용서비스 | counselEmploymentService |
| 9 | 교육내역 | **colspan=3** (education-div) | | |
| 10 | (hiddenDiv 시작, colspan=4) — 키워드 추가 UI 블록 | | | |
| 11 | (hiddenDiv, colspan=4) — 알선 상세 textarea 50% + 추천사 textarea 50% | | | |

> `#hiddenDiv`는 기존 `display` 토글 로직(JobPlacementDetail 함수)이 그대로 동작하도록 **외부 래퍼(`<tr class="hidden-div-row" id="hiddenDiv">` 등)**로 감싼다. jQuery `.show()/.hide()`가 table-row/table-cell에서도 동작하므로 CSS `display` 값만 주의.

#### ③ 취업정보 (7행)
| 행 | 좌 Label | 좌 Input | 우 Label | 우 Input |
|----|---------|---------|---------|---------|
| 1 | 취창업일* | employmentStartDate | 취창업처리일 | employmentProcDate |
| 2 | 퇴사일 | employmentQuit | 취업유형* | employmentEmpType |
| 3 | 취업처 | employmentLoyer | 임금(만원)* | employmentSalary |
| 4 | 직무 | employmentJobRole | 일경험분류 | employmentJobcat |
| 5 | 취업인센티브* | employmentIncentive | 기타 | employmentOthers |
| 6 | 메모 | **colspan=3** textarea | | |

---

## 4. CSS 설계

### 4.1 신규 파일
`src/main/resources/static/css/participantCss/participantTable_0.0.1.css`
```css
.participant-detail-table { table-layout: fixed; font-size: 0.92rem; }
.participant-detail-table > tbody > tr > th {
  background: #f7f9fc; vertical-align: middle; text-align: left;
  padding: 0.55rem 0.75rem; font-weight: 600; color: #344054;
}
.participant-detail-table > tbody > tr > td { padding: 0.45rem 0.75rem; vertical-align: middle; }
.participant-detail-table .form-control,
.participant-detail-table .form-select { height: calc(1.9rem + 2px); }
.participant-detail-table .datepicker-div .input-group { flex-wrap: nowrap; }
.participant-detail-table td.full-cell { padding: 0.75rem; }

/* 반응형: 992px 미만에서 2열(=라벨/입력 1쌍) */
@media (max-width: 991.98px) {
  .participant-detail-table colgroup col:nth-child(n+3) { display: none; }
  .participant-detail-table tr > th:nth-of-type(2),
  .participant-detail-table tr > td:nth-of-type(2) { display: block; width: 100%; }
}
```
- JSP `<head>`에 `<link rel="stylesheet" href="/css/participantCss/participantTable_0.0.1.css">` 추가

### 4.2 기존 CSS 유지/비활성
- `.card-modern`, `.card-header`, `.card-tools`는 계속 사용(섹션 래퍼 그대로)
- `.default-card-body` 관련 여백 규칙은 테이블 내부에서 상쇄될 수 있으므로 **card-body에 `p-0` 추가**로 대응

---

## 5. JSP 파일 자체 수정 포인트

두 JSP는 현재 단순 tag 호출 구조라 **큰 변경은 없으나** 다음을 점검:

### 5.1 NewParticipantsPage.jsp
- `<head>`에 신규 CSS `participantTable_0.0.1.css` 추가
- `<mytag:ParticipantBasic/>` 등 호출부 변경 없음

### 5.2 UpdateParticipantsPage.jsp
- 동일하게 CSS 링크 추가
- 하단 `$(document).ready` 블록 내 `selectOption(...)` 호출은 **id 기반이므로 그대로 동작**. 검증 불필요하지만 QA 시 확인.

---

## 6. 작업 순서 (체크리스트)

- [ ] **1. 브랜치 생성**: `git checkout -b Page_Recruitment_information_1.3.3_table_refactor`
- [ ] **2. CSS 신규 작성**: `participantTable_0.0.1.css` 생성 및 두 JSP `<head>`에 링크
- [ ] **3. ParticipantBasic.tag 변환**
  - `card-body` → `<table class="participant-detail-table">` 교체
  - 모든 input/select/label id·name·value 바인딩 유지
  - 자격증 동적 영역 `<tr><td colspan="3">` 배치
  - 로컬에서 신규/수정 양쪽 화면 렌더 확인
- [ ] **4. ParticipantEmployment.tag 변환** (가장 단순, 다음 순서)
- [ ] **5. ParticipantCounsel.tag 변환** (가장 복잡)
  - `#hiddenDiv` 토글 로직이 `JobPlacementDetail` 함수와 연동됨 — 래퍼를 `<tbody class="hidden-div" id="hiddenDiv">` 혹은 `<tr>`로 감싸 display 토글 동작 확인
  - `#education`, `#keywords-container`, textarea 2종 DOM id 유지
  - 직무카테고리 대/중 분류 select 연쇄 동작 확인 (`jobCategorySelectRenderText_0.0.2.js`)
- [ ] **6. 브라우저 QA**
  - 신규 등록 → 필수값 검증(토스트/sweetalert) 동작
  - 수정 진입 → 서버 값 셀렉트 선택 유지(`selectOption` 호출)
  - 알선요청=희망 전환 시 `#hiddenDiv` 표시
  - 자격증/교육 +/- 버튼 동작
  - 학교명/전공 자동완성 목록 위치 자연스러운지 확인(테이블 셀 기준 상대 위치)
  - 반응형: 1200px / 992px / 768px / 576px 폭에서 레이아웃 점검
- [ ] **7. 회귀 검증**
  - `participants_insert_update_CommonnessJS_0.1.1.js`의 `$("#xxx").val()` 호출부 전수 확인(id 변경 0건 보증)
  - 저장 후 DB insert 결과 비교(변환 전후 동일 값)
- [ ] **8. 커밋 규칙**: `[Refactor][Page_Recruitment_information_1.3.3]: 참여자 신규/수정 화면 테이블 레이아웃 전환`

---

## 7. 리스크 & 주의 사항

| # | 리스크 | 완화 방안 |
|---|-------|----------|
| R1 | input id/name 변경 시 저장/검증 JS 전면 실패 | 변환은 **래핑만** 바꾸고 속성 원본 유지. diff에서 id/name 변경 0건인지 확인. |
| R2 | `#hiddenDiv`가 `<table>` 내부에 있을 때 `display:none` 토글이 브라우저별 차이 발생 | 테이블 **밖으로 빼서** `card-body` 직속에 두거나, `display: table-row-group/table-row`를 CSS로 명시 |
| R3 | 자동완성 목록(`.recommend` div)이 테이블 셀 width를 넘어 잘림 | 셀 `position: relative`, 목록 `position: absolute; z-index: 5` 추가 |
| R4 | Datepicker가 테이블 셀 overflow에 의해 가려짐 | `.card-body`에 `overflow: visible` 보장, datepicker `z-index` 재확인 |
| R5 | `card-collapse` 버튼 클릭 시 AdminLTE가 `card-body` 전체를 접음 → 테이블도 함께 접히는지 확인 | 기본 AdminLTE 동작 그대로 수용 |
| R6 | 모바일 레이아웃에서 4열 테이블이 가로 스크롤 발생 | 4.1 CSS의 `@media (max-width: 991.98px)` 규칙으로 2열 재배열 |

---

## 8. 산출물

- 수정: `ParticipantBasic.tag`, `ParticipantCounsel.tag`, `ParticipantEmployment.tag`, `NewParticipantsPage.jsp`, `UpdateParticipantsPage.jsp`
- 신규: `src/main/resources/static/css/participantCss/participantTable_0.0.1.css`
- 문서: 본 문서(`01_Participant_Page_Table_Refactor_Plan.md`)

---

## 9. [신규] 다중 희망직무 입력 및 드래그&드랍 순위 변경 기능

### 9.1 배경 및 목적
AI 채용공고 추천 기능을 추가하면서, 참여자가 **직무카테고리-대분류, 직무카테고리-중분류, 희망직무를 여러 개** 입력할 수 있도록 변경한다. 각 희망직무 항목에는 **희망 순위(1순위, 2순위 …)**가 부여되며, 사용자가 **드래그&드랍으로 순위를 변경**할 수 있도록 한다.

### 9.2 현재 상태 (As-Is)

| 항목 | 현재 구조 | 비고 |
|------|----------|------|
| 직무카테고리-대분류 | `<select id="jobCategoryLarge">` 단일 선택 | `CounselDTO.jobCategoryLarge` (String) |
| 직무카테고리-중분류 | `<select id="jobCategoryMid">` 단일 선택 | `CounselDTO.jobCategoryMid` (String) |
| 희망직무 | `<input id="counselJobWant">` 단일 텍스트 | `CounselDTO.counselJobWant` (String) |
| JS 렌더링 | `jobCategorySelectRenderText_0.0.2.js` — 대분류 `change` 이벤트 시 중분류 옵션 갱신 | `$('#jobCategoryLarge')`, `$('#jobCategoryMid')` 직접 참조 |
| 검증 JS | `participants_insert_update_CommonnessJS_0.1.1.js` — `jobCategoryLarge.val()`, `jobCategoryMid.val()`, `counselJobWant.val()` 단일 값 검증 | 알선요청=희망 시 필수 |

### 9.3 목표 상태 (To-Be)

#### 9.3.1 UI 구성
- 테이블 내 **"희망직무" 행**을 기존 2개 행(6행: 대/중 분류, 7행: 희망직무/월급) → **1개 통합 영역(colspan=4)**으로 변경
- 해당 영역 내부에 **리스트형 UI** 배치:

```
┌─────────────────────────────────────────────────────────────────────┐
│  희망직무 목록  [+ 추가]                                              │
├─────┬──────────────┬──────────────┬──────────┬──────┬───────────────┤
│ 순위 │ 대분류        │ 중분류        │ 희망직무   │ 삭제  │  ≡ (드래그핸들) │
├─────┼──────────────┼──────────────┼──────────┼──────┼───────────────┤
│  1  │ [IT·개발·데이터 ▼] │ [IT·개발 ▼]     │ [프론트엔드] │  🗑  │  ≡            │
│  2  │ [디자인·미디어 ▼]  │ [디자인 ▼]      │ [UI/UX   ] │  🗑  │  ≡            │
│  3  │ [경영·사무 ▼]     │ [총무·법무 ▼]    │ [사무보조  ] │  🗑  │  ≡            │
└─────┴──────────────┴──────────────┴──────────┴──────┴───────────────┘
```

- **최소 1개, 최대 5개** 항목 입력 가능 (추가 버튼 비활성 제어)
- **순위 번호**는 드래그&드랍 후 자동 재산정 (1부터 순서대로)
- 각 행의 대분류 select 변경 시 해당 행의 중분류만 갱신 (행 단위 독립 연쇄)
- **희망월급(`counselSalWant`)은 희망직무 영역과 분리**하여 별도 행에 유지

#### 9.3.2 HTML 구조 (테이블 내부)
```html
<!-- 상담정보 테이블 내 희망직무 영역 -->
<tr>
  <th scope="row"><label>희망직무<span class="text-danger">*</span></label></th>
  <td colspan="3" class="p-0">
    <div id="jobWishListContainer">
      <!-- 헤더 -->
      <div class="job-wish-header d-flex align-items-center p-2">
        <span class="fw-bold">희망직무 목록</span>
        <span id="jobWishCount" class="badge bg-primary ms-2">0 / 5</span>
        <button type="button" class="btn btn-sm btn-outline-primary ms-auto" id="btnAddJobWish">
          <i class="bi bi-plus-lg"></i> 추가
        </button>
      </div>
      <!-- Sortable 리스트 -->
      <div id="jobWishList" class="job-wish-sortable-list">
        <!-- JS로 동적 생성되는 행 템플릿 -->
        <!--
        <div class="job-wish-item d-flex align-items-center" data-index="0">
          <span class="job-wish-rank badge bg-secondary me-2">1</span>
          <select class="form-select form-select-sm jobCategoryLargeItem" name="jobCategoryLargeList">…</select>
          <select class="form-select form-select-sm jobCategoryMidItem ms-1" name="jobCategoryMidList">…</select>
          <input type="text" class="form-control form-control-sm counselJobWantItem ms-1" name="counselJobWantList" placeholder="희망직무 입력">
          <input type="hidden" class="jobWishRank" name="jobWishRankList" value="1">
          <button type="button" class="btn btn-sm btn-outline-danger ms-1 btnRemoveJobWish"><i class="bi bi-trash"></i></button>
          <span class="job-wish-handle ms-1" style="cursor:grab;"><i class="bi bi-grip-vertical"></i></span>
        </div>
        -->
      </div>
    </div>
  </td>
</tr>
```

#### 9.3.3 드래그&드랍 구현 — SortableJS 활용
- 프로젝트에 이미 **SortableJS** CDN이 `NewParticipantsPage.jsp`에 로드되어 있음 (`sortablejs@1.15.0`)
- `UpdateParticipantsPage.jsp`에는 SortableJS CDN이 **누락** → 추가 필요
- 초기화 코드:
```javascript
const jobWishSortable = new Sortable(document.getElementById('jobWishList'), {
    handle: '.job-wish-handle',     // 드래그 핸들만으로 드래그 가능
    animation: 150,
    ghostClass: 'job-wish-ghost',   // 드래그 중 반투명 스타일
    onEnd: function () {
        reindexJobWishList();       // 순위 번호 재산정
    }
});
```
- `reindexJobWishList()` 함수: `#jobWishList` 자식을 순회하며 `data-index`, `.job-wish-rank` 텍스트, `.jobWishRank` hidden value를 1부터 갱신

### 9.4 데이터 전송 구조

#### 9.4.1 Form 전송 방식 (배열 파라미터)
기존 단일 값 → 배열(List)로 변경:

| 기존 name | 신규 name | 전송 예시 |
|-----------|-----------|----------|
| `jobCategoryLarge` | `jobCategoryLargeList` | `["IT·개발·데이터", "디자인·미디어·문화"]` |
| `jobCategoryMid` | `jobCategoryMidList` | `["IT·개발", "디자인"]` |
| `counselJobWant` | `counselJobWantList` | `["프론트엔드", "UI/UX"]` |
| (없음) | `jobWishRankList` | `[1, 2]` |

> **기존 id 유지 방안**: 첫 번째 항목(1순위)의 값을 기존 hidden input(`id="jobCategoryLarge"`, `id="jobCategoryMid"`, `id="counselJobWant"`)에도 동기화하여, **기존 JS 검증 로직(`participants_insert_update_CommonnessJS_0.1.1.js`)이 수정 없이 작동**하도록 한다.

```javascript
// 폼 제출 직전 — 기존 hidden 필드에 1순위 값 동기화
function syncPrimaryJobWish() {
    const firstItem = document.querySelector('#jobWishList .job-wish-item');
    if (firstItem) {
        $('#jobCategoryLarge').val(firstItem.querySelector('.jobCategoryLargeItem').value);
        $('#jobCategoryMid').val(firstItem.querySelector('.jobCategoryMidItem').value);
        $('#counselJobWant').val(firstItem.querySelector('.counselJobWantItem').value);
    }
}
```

#### 9.4.2 백엔드 수신
- **CounselDTO 확장** (또는 별도 DTO):
```java
// 기존 필드 유지 (하위 호환)
private String jobCategoryLarge;   // 1순위 대분류 (기존)
private String jobCategoryMid;     // 1순위 중분류 (기존)
private String counselJobWant;     // 1순위 희망직무 (기존)

// 신규 추가
private List<String> jobCategoryLargeList;   // 전체 대분류 목록
private List<String> jobCategoryMidList;     // 전체 중분류 목록
private List<String> counselJobWantList;     // 전체 희망직무 목록
private List<Integer> jobWishRankList;       // 순위 목록
```

#### 9.4.3 DB 저장 전략 (2가지 안)

**안 A. 기존 컬럼에 구분자 저장 (최소 변경)**
```sql
-- COUNSEL 테이블 기존 컬럼 활용
jobCategoryLarge = 'IT·개발·데이터|디자인·미디어·문화'
jobCategoryMid   = 'IT·개발|디자인'
counselJobWant   = '프론트엔드|UI/UX'
-- 신규 컬럼 추가
jobWishRanks     = '1|2'
```
- 장점: DDL 변경 최소, 기존 조회 쿼리 호환
- 단점: 검색(LIKE) 시 부정확, 정규화 위반

**안 B. 별도 테이블 분리 (정규화, 권장)**
```sql
CREATE TABLE COUNSEL_JOB_WISH (
    wishId      INT IDENTITY(1,1) PRIMARY KEY,
    counselJobNo INT NOT NULL,           -- COUNSEL FK
    wishRank    INT NOT NULL DEFAULT 1,  -- 희망 순위
    categoryLarge NVARCHAR(100),         -- 대분류
    categoryMid   NVARCHAR(100),         -- 중분류
    jobWant       NVARCHAR(200),         -- 희망직무
    CONSTRAINT FK_COUNSEL_JOB_WISH FOREIGN KEY (counselJobNo) REFERENCES COUNSEL(jobNo)
);
```
- 장점: 정규화, 순위별 독립 검색/정렬 가능, AI 추천 매칭 시 유리
- 단점: 새 매퍼 XML + DAO/Service 추가 필요
- **기존 COUNSEL 테이블의 `jobCategoryLarge`, `jobCategoryMid`, `counselJobWant` 컬럼은 1순위 값으로 유지 (하위 호환)**

### 9.5 JS 수정 범위

#### 9.5.1 신규 JS 파일
`src/main/webapp/js/jobWishListManager_0.0.1.js`
- 역할: 희망직무 항목 CRUD, SortableJS 초기화, 대/중분류 연쇄 select(행 독립), 순위 재산정, 기존 hidden 동기화
- 두 JSP `<head>`에 `<script>` 추가
- 기존 `jobCategorySelectRenderText_0.0.2.js`의 카테고리 데이터(`jobCategoryLargeArray`, `jobCategoryMidArray`)를 **공유 모듈 또는 전역 변수로 분리**하거나, 신규 JS에서 동일 데이터를 import

#### 9.5.2 기존 JS 변경

| 파일 | 변경 내용 |
|------|----------|
| `jobCategorySelectRenderText_0.0.2.js` | 카테고리 데이터를 전역으로 노출하거나, 신규 JS와 중복을 제거. 기존 단일 select(`#jobCategoryLarge`, `#jobCategoryMid`) 렌더링은 **hidden input 유지 목적으로 잔류** 가능 |
| `participants_insert_update_CommonnessJS_0.1.1.js` | `btn_check` 클릭 핸들러 내에서 `syncPrimaryJobWish()` 호출 추가 (폼 제출 직전). 기존 `jobCategoryLarge.val()`, `jobCategoryMid.val()`, `counselJobWant.val()` 검증은 1순위 동기화 후 그대로 동작 |
| `UpdateParticipantsPage.jsp` | `$(document).ready` 내 `selectOption(jobCategoryLarge, …)`, `selectOption(jobCategoryMid, …)` 호출을 **다중 항목 복원 로직으로 교체**. 서버에서 List를 JSON으로 전달받아 `addJobWishItem()` 반복 호출 |

#### 9.5.3 행 추가/삭제 로직 (의사코드)
```javascript
const MAX_JOB_WISH = 5;
const MIN_JOB_WISH = 1;

function addJobWishItem(data) {
    const count = $('#jobWishList .job-wish-item').length;
    if (count >= MAX_JOB_WISH) return;

    const $item = $(JOB_WISH_TEMPLATE); // 템플릿 clone
    // data가 있으면 select 값 복원
    if (data) {
        $item.find('.jobCategoryLargeItem').val(data.categoryLarge);
        updateMidOptionsForRow($item, data.categoryLarge, data.categoryMid);
        $item.find('.counselJobWantItem').val(data.jobWant);
    }
    $('#jobWishList').append($item);
    reindexJobWishList();
    updateAddButtonState();
}

function removeJobWishItem($item) {
    const count = $('#jobWishList .job-wish-item').length;
    if (count <= MIN_JOB_WISH) {
        alertDefaultInfo("최소 1개의 희망직무는 필수입니다.");
        return;
    }
    $item.remove();
    reindexJobWishList();
    updateAddButtonState();
}

function reindexJobWishList() {
    $('#jobWishList .job-wish-item').each(function (i) {
        $(this).attr('data-index', i);
        $(this).find('.job-wish-rank').text(i + 1);
        $(this).find('.jobWishRank').val(i + 1);
    });
    $('#jobWishCount').text($('#jobWishList .job-wish-item').length + ' / ' + MAX_JOB_WISH);
    syncPrimaryJobWish(); // 1순위 값 기존 hidden에 동기화
}
```

### 9.6 CSS 추가 (participantTable_0.0.1.css에 추가)
```css
/* 희망직무 리스트 스타일 */
.job-wish-sortable-list { min-height: 60px; }
.job-wish-item {
  padding: 0.5rem 0.75rem;
  border-bottom: 1px solid #e9ecef;
  background: #fff;
  transition: background 0.15s;
}
.job-wish-item:hover { background: #f8f9fa; }
.job-wish-ghost { opacity: 0.4; background: #cfe2ff; }
.job-wish-handle { font-size: 1.2rem; color: #adb5bd; }
.job-wish-handle:hover { color: #495057; }
.job-wish-rank { min-width: 28px; text-align: center; }
.job-wish-header { background: #f7f9fc; border-bottom: 2px solid #dee2e6; }
.job-wish-item .form-select,
.job-wish-item .form-control { max-width: 200px; }
```

### 9.7 상담정보 테이블 행 배치안 (수정)

기존 3.3절 ② 상담정보 배치에서 **6~7행을 통합 변경**:

| 행 | 좌 Label | 좌 Input | 우 Label | 우 Input |
|----|---------|---------|---------|---------|
| 1 | 취업역량* | counselJobSkill | 진행단계 | counselProgress |
| 2 | 초기상담일 | counselInItCons | 최근상담일 | counselLastCons |
| 3 | IAP수립일 | counselIAPDate | 기간만료예정일* | counselEXPDate |
| 4 | IAP 3개월차 | counselIAP3Month(disabled) | IAP 5개월차 | counselIAP5Month(disabled) |
| 5 | 구직만료일 | counselJobEX | 수당지급일 | counselAllowancePayment |
| **6** | **희망직무*** | **colspan=3** — 다중 희망직무 리스트 UI (`#jobWishListContainer`) | | |
| 7 | 희망월급(만원)* | counselSalWant | 알선요청 | counselPlacement |
| 8 | 간접고용서비스 | counselEmploymentService | — | — |
| 9 | 교육내역 | **colspan=3** (education-div) | | |
| 10 | (hiddenDiv, colspan=4) — 키워드 | | | |
| 11 | (hiddenDiv, colspan=4) — 알선 상세 + 추천사 | | | |

> **기존 단일 select(`#jobCategoryLarge`, `#jobCategoryMid`)와 input(`#counselJobWant`)은 `type="hidden"`으로 변환**하여 form 내에 유지. 1순위 값이 자동 동기화되므로 기존 검증 로직 및 MyBatis 바인딩 호환.

### 9.8 수정 진입 시 데이터 복원 (UpdateParticipantsPage.jsp)

서버에서 다중 희망직무 데이터를 JSP에 전달하는 방식:
```jsp
<script>
    // 서버에서 JSON 배열로 전달
    const jobWishData = JSON.parse('${jobWishListJson}');
    // 예: [{"categoryLarge":"IT·개발·데이터","categoryMid":"IT·개발","jobWant":"프론트엔드","rank":1}, ...]
    
    window.addEventListener('load', () => {
        if (jobWishData && jobWishData.length > 0) {
            jobWishData.forEach(item => addJobWishItem(item));
        } else {
            // 기존 단일 값 호환 — 레거시 데이터 복원
            addJobWishItem({
                categoryLarge: '${counsel.jobCategoryLarge}',
                categoryMid: '${counsel.jobCategoryMid}',
                jobWant: '${counsel.counselJobWant}',
                rank: 1
            });
        }
    });
</script>
```

### 9.9 리스크 추가

| # | 리스크 | 완화 방안 |
|---|-------|----------|
| R7 | SortableJS가 `UpdateParticipantsPage.jsp`에 누락 | JSP `<head>`에 CDN 추가 확인 |
| R8 | 다중 항목 → 기존 단일 String DB 컬럼과 호환 깨짐 | 9.4.1의 hidden input 동기화 + DB 전략(안 A 또는 B) 결정 후 구현 |
| R9 | 행별 대/중분류 연쇄 select가 기존 전역 `$('#jobCategoryLarge')` 이벤트와 충돌 | 신규 JS에서는 **class 기반 이벤트 위임**(`$(document).on('change', '.jobCategoryLargeItem', …)`)으로 행 독립 처리. 기존 전역 select는 hidden으로 전환하여 이벤트 발생 안 함 |
| R10 | 드래그 중 datepicker/자동완성 팝업이 간섭 | SortableJS `handle` 옵션으로 핸들 아이콘만 드래그 가능하게 제한 |
| R11 | 모바일에서 드래그 터치 이벤트 미동작 | SortableJS는 기본적으로 touch 지원. `forceFallback: true` 옵션으로 모바일 호환 강화 |

### 9.10 작업 체크리스트 (추가)

- [ ] **9-1.** DB 저장 전략 결정 (안 A: 구분자 / 안 B: 별도 테이블) → 팀 합의
- [ ] **9-2.** `UpdateParticipantsPage.jsp` — SortableJS CDN 추가
- [ ] **9-3.** 카테고리 데이터 전역 공유 구조 분리 (`jobCategoryData.js` 또는 기존 JS 내 `window.JOB_CATEGORY` 노출)
- [ ] **9-4.** `jobWishListManager_0.0.1.js` 신규 작성 (항목 CRUD + SortableJS + 행별 연쇄 select + 순위 재산정 + hidden 동기화)
- [ ] **9-5.** `ParticipantCounsel.tag` — 기존 대/중분류 select + 희망직무 input을 hidden으로 전환, 다중 리스트 UI 영역 추가
- [ ] **9-6.** `participants_insert_update_CommonnessJS_0.1.1.js` — `btn_check` 내 `syncPrimaryJobWish()` 호출 추가
- [ ] **9-7.** 백엔드: CounselDTO 확장 (List 필드) + Controller 파라미터 바인딩
- [ ] **9-8.** DB: 안 B 선택 시 `COUNSEL_JOB_WISH` 테이블 DDL + MyBatis 매퍼 작성
- [ ] **9-9.** UpdateParticipantsPage.jsp — 수정 진입 시 다중 항목 JSON 복원 로직 추가
- [ ] **9-10.** QA: 드래그&드랍 순위 변경 → 저장 → 재진입 시 순위 유지 확인
- [ ] **9-11.** QA: 모바일 터치 드래그 동작 확인

---

## 10. 후속 작업 (Out of Scope)
- AI 채용공고 추천 섹션을 테이블 우측 사이드에 고정 노출하는 레이아웃 설계는 별도 문서(`02_AI_Recommendation_Panel_Integration.md`)로 분리 작성 예정.
