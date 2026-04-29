# 참여자 페이지 테이블 리팩토링 — 진행 상황 (Progress Tracking)

- **작성일**: 2026-04-16
- **기준 문서**: `01_Participant_Page_Table_Refactor_Plan.md`
- **브랜치**: `Page_Recruitment_information_1.3.3`
- **최종 갱신**: 2026-04-16 (Phase 1~3 구현 완료, MyBatis 쿼리 정리, 디자인 개편 form-board 적용)

---

## 1. 전체 진행 요약

| 구분 | 상태 | 비고 |
|------|------|------|
| **카드→테이블 전환 (섹션 3~5)** | :white_check_mark: 완료 | 3개 Tag 파일 모두 테이블 레이아웃 적용 |
| **다중 희망직무 기능 (섹션 9)** | :white_check_mark: 완료 | JS/DTO/DB/UI 전 영역 구현 완료 |
| **CSS 신규 파일** | :white_check_mark: 완료 | `participantTable_0.0.1.css` 생성 |
| **JS 신규 파일** | :white_check_mark: 완료 | `jobWishListManager_0.0.1.js` 생성 |
| **백엔드 확장** | :white_check_mark: 완료 | CounselDTO List 필드, WishJobDTO, mapper 추가 |
| **DB 테이블 추가** | :white_check_mark: 완료 | `J_참여자관리_희망직무` 테이블 생성 |
| **MyBatis 쿼리 정리** | :white_check_mark: 완료 | J_참여자관리에서 희망직무/카테고리 컬럼 제거 후 쿼리 수정 |
| **신규 등록 기능 검증** | :white_check_mark: 완료 | 신규 참여자 작성 정상 작동 확인 |
| **수정 페이지 기능 검증** | :wavy_dash: 진행 중 | 카테고리/희망직무 로딩 정상화 완료, 추가 검증 필요 |
| **QA/회귀 검증** | :wavy_dash: 부분 완료 | 본격 QA는 진행 중 |

---

## 2. 체크리스트별 상세 진행 상황

### 2.1 카드→테이블 전환 (계획서 섹션 6 체크리스트)

#### [x] 1. 브랜치 생성
- **상태**: :white_check_mark: 완료 (기존 브랜치 `Page_Recruitment_information_1.3.3` 사용)

#### [x] 2. CSS 신규 작성 (`participantTable_0.0.1.css`)
- **상태**: :white_check_mark: 완료
- **경로**: `src/main/webapp/css/participantCss/participantTable_0.0.1.css`
- **포함 스타일**:
  - `.participant-table` 기본 스타일 (table-layout: fixed, colgroup 15%/35%)
  - `<th>` 라벨 셀 (배경 #f8f9fa, 우측 정렬, font-weight 600)
  - `<td>` 입력 셀 (padding, vertical-align)
  - `.form-control`, `.form-select` 너비 100%
  - 자동완성 셀 (`td.autocomplete-cell` + `.recommend` position:absolute, z-index:1050)
  - 반응형 미디어 쿼리 (`@media max-width: 991.98px` → 2열 전환)
  - 희망직무 리스트 스타일 (`.wish-job-item`, `.wish-job-handle`, `.wish-job-rank` 등)
- **두 JSP `<head>`에 CSS 링크 추가**: :white_check_mark: 완료
  - `NewParticipantsPage.jsp` (line 158 뒤)
  - `UpdateParticipantsPage.jsp` (custom-modern 뒤 + SortableJS CDN 추가)

#### [x] 3. ParticipantBasic.tag 변환
- **상태**: :white_check_mark: 완료 (7행 배치)
- **경로**: `src/main/webapp/WEB-INF/tags/ParticipantBasic.tag`
- **결과**: card-modern 외부 구조 유지, 내부 `<div class="row">` → `<table class="participant-table">`로 교체
- 모든 `id`/`name` 속성 유지 (basicPartic, basicDob, basicSchool, basicSpecialty, particcertifCertif 등)
- 자동완성 셀에 `class="autocomplete-cell"` 추가, Daum 우편번호 #layer 보존

#### [x] 4. ParticipantEmployment.tag 변환
- **상태**: :white_check_mark: 완료 (6행 배치)
- **경로**: `src/main/webapp/WEB-INF/tags/ParticipantEmployment.tag`
- **결과**: 가장 단순한 변환, 메모는 `colspan=3` textarea

#### [x] 5. ParticipantCounsel.tag 변환
- **상태**: :white_check_mark: 완료 (9행 테이블 + hiddenDiv 외부 배치)
- **경로**: `src/main/webapp/WEB-INF/tags/ParticipantCounsel.tag`
- **핵심 처리**:
  - `#hiddenDiv`는 `</table>` 뒤, `card-body` 내부에 `<div>`로 배치 → jQuery `.show()/.hide()` 정상 동작
  - 행6: 다중 희망직무 UI (`#wishJobListContainer` + `#addWishJobBtn`)
  - 기존 `#jobCategoryLarge`, `#jobCategoryMid`, `#counselJobWant`는 hidden input으로 전환
  - 교육내역(`#education`), 키워드(`#keywords-container`) DOM id 유지

#### [x] 6. 브라우저 QA — 신규 등록
- **상태**: :white_check_mark: 완료 (신규 참여자 작성 정상 작동)
- **확인 항목**:
  - [x] 필수값 검증 동작
  - [x] 자격증/교육 +/- 버튼 동작
  - [x] 학교명/전공 자동완성 위치
  - [x] 다중 희망직무 추가/삭제/드래그
  - [x] 알선요청=희망 전환 시 hiddenDiv 표시
  - [x] DB 저장 정상 (J_참여자관리 + J_참여자관리_희망직무)

#### [x] 7. 회귀 검증 — 수정 페이지
- **상태**: :wavy_dash: 진행 중 (카테고리/희망직무 데이터 로딩 정상화 완료)
- **2026-04-16 추가 작업**:
  - `J_참여자관리`에서 희망직무, 직무_카테고리_대/중/소 컬럼 제거됨 (DB 변경)
  - `Counsel-mapping.xml` 수정: SELECT/UPDATE 쿼리에서 해당 컬럼 참조 제거
  - 수정 페이지 진입 시 `wishJobList` collection으로 J_참여자관리_희망직무 테이블에서 자동 로드
  - 1순위 항목은 `syncPrimaryJobWish()` JS 함수로 hidden input에 동기화 (폼 검증용)

---

### 2.2 다중 희망직무 기능 (계획서 섹션 9 체크리스트)

#### [x] 9-1. DB 저장 전략 결정
- **상태**: :white_check_mark: 완료 — **안 B (별도 테이블 분리)** 선택
- **결정 사항**: `J_참여자관리_희망직무` 신규 테이블 생성, `J_참여자관리`에서는 희망직무/카테고리 컬럼 제거

#### [x] 9-2. UpdateParticipantsPage.jsp — SortableJS CDN 추가
- **상태**: :white_check_mark: 완료
- **추가 위치**: `<head>` 내부, custom-modern CSS 뒤

#### [x] 9-3. 카테고리 데이터 전역 공유 구조 분리
- **상태**: :white_check_mark: 완료
- **변경 내용**: `jobCategorySelectRenderText_0.0.2.js`에서 `window.JOB_CATEGORY_LARGE`, `window.JOB_CATEGORY_MID`로 전역 노출
- 기존 단일 select 렌더링 함수는 그대로 유지 (행 내부 데이터 참조만 전역으로 변경)

#### [x] 9-4. `jobWishListManager_0.0.1.js` 신규 작성
- **상태**: :white_check_mark: 완료
- **경로**: `src/main/webapp/js/jobWishListManager_0.0.1.js`
- **구현 항목**:
  - [x] `addWishJobItem(data)` / `removeWishJobItem($item)` (최소 1개, 최대 5개)
  - [x] SortableJS 초기화 (handle: `.wish-job-handle`, animation: 150, forceFallback: true)
  - [x] 행별 독립 대/중분류 연쇄 select (이벤트 위임 `.wish-large` change)
  - [x] `reindex()` — 순위/name 재산정
  - [x] `syncPrimaryJobWish()` — 1순위 → hidden input 동기화 (전역 함수)
  - [x] `initWishJobList(arr)` — 수정 페이지 진입 시 데이터 복원 (전역 함수)
  - [x] 추가/삭제 버튼 활성화 제어 (`updateState()`)
  - [x] 카운트 뱃지 갱신 (`#wishJobCount`)

#### [x] 9-5. ParticipantCounsel.tag — 다중 희망직무 UI 영역 추가
- **상태**: :white_check_mark: 완료
- **변경 내용**:
  - 기존 `<select id="jobCategoryLarge">`, `<select id="jobCategoryMid">`, `<input id="counselJobWant">` → `type="hidden"` 전환
  - `#wishJobListContainer` 추가 (행6, colspan=3)
  - `#addWishJobBtn` 추가 버튼
  - `#wishJobCount` 카운트 뱃지

#### [x] 9-6. `participants_insert_update_CommonnessJS_0.1.1.js` — syncPrimaryJobWish 호출 추가
- **상태**: :white_check_mark: 완료
- **위치**: `btn_check` 클릭 핸들러 내 폼 제출 직전 (line ~302)
- **구현**: `if (typeof syncPrimaryJobWish === 'function') { syncPrimaryJobWish(); }`

#### [x] 9-7. 백엔드: CounselDTO 확장 + Controller 파라미터 바인딩
- **상태**: :white_check_mark: 완료
- **CounselDTO**: `private List<WishJobDTO> wishJobList;` 필드 추가
- **WishJobDTO**: 신규 클래스 생성 (pk, counselJobNo, wishRank, jobWant, categoryLarge, categoryMid, categorySub)
- **Controller**: Spring MVC `@ModelAttribute` 자동 바인딩 활용 (별도 수정 불필요)
- **UpdateController**: 수정 페이지 진입 시 `wishJobs` JSON 배열을 model에 전달

#### [x] 9-8. DB: J_참여자관리_희망직무 테이블 DDL + MyBatis 매퍼 작성
- **상태**: :white_check_mark: 완료 (DDL 실행 완료)
- **DDL**: `src/main/resources/SQL/JOBMOA_참여자관리프로그램_CreateTable.sql` 끝에 추가
- **테이블 구조**: PK, 구직번호, 희망순위, 희망직무, 직무_카테고리_대/중/소, 등록일시
- **외래키**: 구직번호 → J_참여자관리(구직번호) ON DELETE CASCADE
- **인덱스**: IX_희망직무_구직번호 (구직번호)
- **MyBatis 매퍼** (`Counsel-mapping.xml`):
  - [x] `counselSelectWishJobs` — 구직번호 기준 SELECT, 희망순위 ORDER BY
  - [x] `counselWishJobDelete` — 구직번호 기준 일괄 삭제
  - [x] `counselWishJobInsert` — foreach로 일괄 등록
  - [x] CounselResultMap에 collection 추가 (자동 로드)
- **typeAlias**: `sql-map-config.xml`에 `wishJob` 등록
- **Service**: `BasicServiceImpl.insert/update`에 delete-then-insert 패턴 추가

#### [x] 9-9. UpdateParticipantsPage.jsp — 수정 진입 시 다중 항목 복원
- **상태**: :white_check_mark: 완료
- **구현**: `$(document).ready` 내에서 `${wishJobs}` JSON 파싱 → `initWishJobList(wishJobArr)` 호출
- **레거시 호환**: 다중 데이터 없을 시 빈 항목 1개 자동 생성

#### [ ] 9-10. QA: 드래그&드랍 순위 변경 검증
- **상태**: :wavy_dash: 신규 등록은 검증됨, 수정 후 재진입 검증 필요

#### [ ] 9-11. QA: 모바일 터치 드래그 동작 확인
- **상태**: :x: 미진행

---

## 3. 2026-04-16 MyBatis 쿼리 정리 (J_참여자관리 컬럼 제거 대응)

### 변경 배경
`J_참여자관리_희망직무` 신규 테이블이 추가되면서 기존 `J_참여자관리` 테이블의 다음 컬럼들이 제거됨:
- `희망직무`
- `직무_카테고리_대`
- `직무_카테고리_중`
- `직무_카테고리_소`

이로 인해 기존 SQL 쿼리에서 해당 컬럼 참조 시 "열 해결 불가" 오류 발생.

### 수정 내역

#### `Counsel-mapping.xml`
1. **`counselUpdate`** SET 절에서 제거:
   - ~~`희망직무 = #{counselJobWant}`~~ (제거)
   - ~~`직무_카테고리_대/중/소 = ...`~~ (제거)
   - 한글 주석으로 변경 사유 명시
2. **`counselSelectOne`** SELECT 절에서 제거:
   - ~~`A.희망직무 AS counselJobWant`~~ (제거)
   - ~~`A.직무_카테고리_대/중/소 AS ...`~~ (제거)
   - 한글 주석으로 wishJobList collection을 통한 자동 로드 안내
3. **CounselResultMap**:
   - `counselJobWant`, `jobCategoryLarge`, `jobCategoryMid` 매핑 제거 (해당 컬럼이 SELECT되지 않으므로)
   - `wishJobList` collection은 그대로 유지 (J_참여자관리_희망직무 테이블에서 자동 로드)

#### `CounselDTO.java`
- `counselJobWant`, `jobCategoryLarge`, `jobCategoryMid`, `jobCategorySub` 필드는 유지하되,
  "1순위 동기화용, 미저장" 주석 추가 (폼 검증 및 syncPrimaryJobWish 동기화 목적)

#### `UpdateParticipantsPage.jsp`
- hidden input이 된 `#jobCategoryLarge`/`#jobCategoryMid`에 대한 `selectOption()` 호출 제거
- 데이터 복원은 `initWishJobList()`가 wishJobList 배열을 통해 처리

#### `BasicServiceImpl.java`
- 다중 희망직무 등록/수정 로직에 한글 주석 보강
- delete-then-insert 패턴 명시

### 데이터 흐름 (최종)

```
[수정 페이지 진입]
  ├─ counselSelectOne 실행 → CounselDTO 기본 필드 로드
  └─ CounselResultMap collection 자동 트리거
       └─ counselSelectWishJobs 실행 → wishJobList 채움
            └─ JSP에서 wishJobs JSON으로 전달
                 └─ initWishJobList()로 UI 렌더링

[저장]
  ├─ syncPrimaryJobWish() — 1순위 → hidden input
  ├─ 폼 제출 (wishJobList[0..n] 파라미터 포함)
  ├─ counselUpdate — 희망직무/카테고리 제외하고 J_참여자관리 갱신
  └─ counselWishJobDelete + counselWishJobInsert — J_참여자관리_희망직무 갱신
```

---

## 4. 파일별 현재 상태 매트릭스

| 파일 | 경로 | 상태 | 비고 |
|------|------|------|------|
| ParticipantBasic.tag | `WEB-INF/tags/` | :white_check_mark: 변환 완료 | 7행 배치 |
| ParticipantCounsel.tag | `WEB-INF/tags/` | :white_check_mark: 변환 완료 | 9행 + hiddenDiv 외부, 다중 희망직무 UI 포함 |
| ParticipantEmployment.tag | `WEB-INF/tags/` | :white_check_mark: 변환 완료 | 6행 배치 |
| NewParticipantsPage.jsp | `WEB-INF/views/` | :white_check_mark: 링크 추가 | CSS + jobWishListManager JS |
| UpdateParticipantsPage.jsp | `WEB-INF/views/` | :white_check_mark: 링크 추가 + 데이터 복원 | SortableJS CDN, initWishJobList 호출 |
| participantTable_0.0.1.css | `webapp/css/participantCss/` | :white_check_mark: 신규 생성 | 테이블 + 희망직무 스타일 |
| jobWishListManager_0.0.1.js | `webapp/js/` | :white_check_mark: 신규 생성 | 다중 희망직무 CRUD + SortableJS |
| jobCategorySelectRenderText_0.0.2.js | `webapp/js/` | :white_check_mark: 전역 데이터 노출 | window.JOB_CATEGORY_LARGE/MID |
| participants_insert_update_CommonnessJS_0.1.1.js | `webapp/js/` | :white_check_mark: syncPrimaryJobWish 호출 | 폼 제출 직전 동기화 |
| CounselDTO.java | `biz/` | :white_check_mark: List 필드 추가 + 주석 | wishJobList + 미저장 필드 안내 |
| WishJobDTO.java | `biz/` | :white_check_mark: 신규 생성 | 다중 희망직무 단위 DTO |
| Counsel-mapping.xml | `mappings/` | :white_check_mark: 쿼리 정리 + 주석 | 희망직무/카테고리 컬럼 제거 대응 |
| sql-map-config.xml | `resources/` | :white_check_mark: typeAlias 추가 | wishJob alias 등록 |
| BasicServiceImpl.java | `biz/` | :white_check_mark: 희망직무 로직 추가 | delete-then-insert 패턴 |
| UpdateController.java | `view/participant/` | :white_check_mark: wishJobs JSON 전달 | model attribute 추가 |
| JOBMOA_..._CreateTable.sql | `resources/SQL/` | :white_check_mark: DDL 추가 | J_참여자관리_희망직무 테이블 |

---

## 5. 리스크 현황

| # | 리스크 | 심각도 | 현재 상태 | 결과 |
|---|-------|--------|----------|------|
| R1 | input id/name 변경 시 JS 전면 실패 | 높음 | :white_check_mark: 해소 | 모든 id/name 유지 확인 |
| R2 | `#hiddenDiv` table 내부 display 토글 이슈 | 중간 | :white_check_mark: 해소 | hiddenDiv를 테이블 외부 div로 배치 |
| R3 | 자동완성 목록 테이블 셀 overflow 잘림 | 중간 | :white_check_mark: 해소 | `td.autocomplete-cell { position:relative; overflow:visible }` |
| R4 | Datepicker overflow 가려짐 | 낮음 | :white_check_mark: 해소 | 테이블 셀 overflow 처리 |
| R5 | card-collapse 버튼 동작 | 낮음 | :white_check_mark: 해소 | card-modern 외부 구조 유지 |
| R6 | 모바일 4열 테이블 가로 스크롤 | 중간 | :white_check_mark: 해소 | CSS 반응형 2열 재배열 |
| R7 | SortableJS UpdatePage 누락 | 낮음 | :white_check_mark: 해소 | CDN 추가 완료 |
| R8 | 다중 항목 ↔ 단일 String DB 호환 | 높음 | :white_check_mark: 해소 | 안 B 채택, J_참여자관리 컬럼 제거로 일관성 확보 |
| R9 | 행별 연쇄 select ↔ 전역 이벤트 충돌 | 중간 | :white_check_mark: 해소 | 클래스 기반 이벤트 위임으로 행 독립 처리 |
| R10 | 드래그 중 팝업 간섭 | 낮음 | :white_check_mark: 해소 | SortableJS handle 옵션 적용 |
| R11 | 모바일 터치 드래그 미동작 | 낮음 | :wavy_dash: 코드 적용 | `forceFallback: true` 옵션 적용, 실기기 검증 필요 |
| R12 | J_참여자관리 컬럼 제거 후 기존 쿼리 오류 | 높음 | :white_check_mark: 해소 | counselUpdate/SelectOne에서 해당 컬럼 제거 |

---

## 6. 변경 이력

| 날짜 | 작업자 | 변경 내용 |
|------|--------|----------|
| 2026-04-16 | — | 초기 진행 상황 문서 작성. 전체 미착수 상태 확인. |
| 2026-04-16 | Claude | Phase 1~3 전체 구현 완료. CSS/JS 신규 생성, 3개 Tag 테이블 전환, WishJobDTO/매퍼/서비스 추가, 다중 희망직무 UI/저장 로직 구현. |
| 2026-04-16 | SD | DB DDL 실행 완료 (`J_참여자관리_희망직무` 생성). 신규 참여자 작성 기능 정상 작동 검증. |
| 2026-04-16 | SD + Claude | J_참여자관리에서 희망직무/카테고리 컬럼 제거에 따른 MyBatis 쿼리 정리. counselUpdate/SelectOne에서 해당 컬럼 참조 제거. ResultMap 정리 및 한글 주석 추가. UpdateParticipantsPage의 selectOption 잔재 정리. |
| 2026-04-16 | SD + Claude | **디자인 개편**: card-modern → form-board 디자인으로 전환 (예시 `participantNew.html` 기준). 각 Tag가 독립된 테이블처럼 보이도록 흰색 배경 + 상단 다크 액센트 보더 + 좌측 파란 액센트 바 적용. 카드 collapse 버튼 제거 (예시에 없음), 구직번호 hidden input은 form-board 직속으로 재배치. 입력 컨트롤(폼/셀렉트) 디자인 통일 (높이 34px, 14px font, 파란 포커스 보더). 색상 팔레트도 예시 기준 (#2563eb primary, #f1f5f9 label bg)으로 교체. |
| 2026-04-16 | SD + Claude | **자격증/교육내역 동적 추가/삭제 통일**: 희망직무와 동일한 패턴(항목별 [핸들][입력][삭제] + 추가 버튼 + 카운트 뱃지 + SortableJS 드래그)으로 재구현. `particcertifDiv_0.0.2.js`, `educationDiv_0.0.2.js` 전면 재작성 (기존 plus/minus 아이콘 토글 제거, `addCertItem`/`removeCertItem`, `addEduItem`/`removeEduItem` 전역 함수 노출). 기존 `specialty(arr)`, `education(arr)` 초기화 함수는 유지하여 수정 페이지 호환. CSS에 `.cert-item`, `.edu-item` 공통 스타일 추가 (`.wish-job-item`과 통합 셀렉터). Tag 파일에 `#addCertBtn`, `#addEduBtn` 버튼 및 `#certCount`, `#eduCount` 뱃지 추가. |
| 2026-04-16 | SD | **집중알선요청 컬럼 추가**: `J_참여자관리`에 `집중알선여부 bit(1)` 컬럼 추가 (PRD 6.1 대응). CounselDTO에 `boolean counselFocusedPlacement` 필드, Counsel-mapping.xml 의 counselUpdate / counselSelectOne / ResultMap 에 컬럼 매핑 추가. |
| 2026-04-16 | SD + Claude | **집중알선요청 바인딩 검증 및 수정**: JSP select option value가 "미희망"/"희망" 문자열이어서 boolean 타입 DTO에 바인딩 실패하는 문제 발견 → `value="false"`/`value="true"`로 변경하여 Spring MVC가 boolean으로 정상 변환하도록 수정. MyBatis는 bit(1) ↔ boolean 자동 매핑. DTO 주석 오류("IAP수료일 5개월 이후 여부") 수정. |
| 2026-04-16 | SD + Claude | **테이블 레이아웃 재구성**: 기본정보 표를 8칸(colgroup 10%/15% × 4쌍)으로 확장. 상담정보 표를 6칸(12%/22%/12%/22%/12%/20%)으로 확장. 교육내역 UI를 상담정보에서 기본정보로 이동 (자격증과 나란히 배치). 상담정보에서 교육내역 행 제거. CSS에 `.participant-table--8col`, `.participant-table--6col` 변형 클래스 추가 (좁은 셀 내부 폰트/패딩 축소). 반응형 미디어 쿼리에 `colspan="5"`, `colspan="7"` 대응 추가. |

---

> **참고**: 이 문서는 작업 진행에 따라 지속적으로 갱신합니다.
> 다음 단계: 수정 페이지 회귀 QA (드래그&드랍 순위 저장 후 재진입 일치 검증, 모바일 터치 검증).