# AI_Function_9Week_Progress.md — 9주차: 상담사 피드백 반영 및 UX 개선

> **기간:** 2026-05-29 ~ 2026-06-05  
> **상태:** 🔲 미시작  
> **목표:** 실제 사용자(상담사) 피드백 수집 후 UX 개선 사항을 반영

---

## 1. 이번 주 목표

통합 테스트가 완료된 기능을 상담사에게 시연하고,  
실제 업무 흐름에서 발생하는 불편 사항을 수집·반영한다.  
기능 품질보다 **사용성(Usability)** 중심으로 개선한다.

---

## 2. 세부 작업 목록

### 9-1. 상담사 시연 및 피드백 수집

#### 9-1-1. 시연 준비
- [ ] 시연용 테스트 데이터 준비
  - 집중 알선 대상 참여자 샘플 (`infoFocus = '1'` 데이터)
  - `IAP후` 진행단계 참여자 샘플 (`infoStage = 'IAP후'`)
  - `미취업사후관리` 진행단계 참여자 샘플 (`infoStage = '미취업사후관리'`)
  - `Referral.infoAlsonDetail` 있는 참여자 샘플
  - `Referral.infoAlsonDetail` 없는 참여자 샘플
- [ ] 시연 시나리오 스크립트 작성
  1. 집중 알선 필터로 검색
  2. 추천 채용정보 모달 열기
  3. AI 추천 저장 버튼 클릭
  4. 추천 결과 확인 (`recommendationScore`, `recommendationReason`, `bestJobInfo`)

#### 9-1-2. 피드백 수집 체크리스트
- [ ] 검색 필터 위치 및 레이블 명확성
- [ ] 리스트 버튼 크기/위치 적절성
- [ ] 모달 사이즈 및 정보 가독성
- [ ] 추천 결과 표현 방식 (점수, 사유, 베스트)
- [ ] AI 추천 저장 소요 시간 체감
- [ ] 전체 업무 흐름 편의성

#### 9-1-3. 피드백 기록표

| # | 피드백 내용 | 우선순위 | 대응 방향 | 반영 여부 |
|---|------------|---------|----------|----------|
| 1 | | | | 🔲 |
| 2 | | | | 🔲 |
| 3 | | | | 🔲 |
| 4 | | | | 🔲 |
| 5 | | | | 🔲 |

---

### 9-2. UI 문구 및 레이블 개선

> 피드백 결과 반영 항목 (시연 후 확정)

- [ ] 검색 필터 레이블 문구 재검토
  - 예: "집중 알선" → "집중 알선 대상자"
- [ ] 버튼 텍스트 명확화
  - 예: "추천 채용정보" → "AI 추천 채용정보 보기"
  - 예: "AI 추천 채용정보 저장" → "추천 채용정보 새로 받기"
- [ ] 모달 헤더 타이틀 개선
- [ ] 빈 상태 메시지 문구 개선
- [ ] 로딩 메시지 문구 개선
  - "AI 추천 처리 중..." 보다 구체적인 표현 검토
- [ ] 저장 완료 메시지 문구 개선
  - 예: "추천 저장 완료 (5건)" → "5개의 채용정보가 추천 저장되었습니다."
- [ ] 오류 메시지 문구 상담사 친화적으로 개선
  - 기술적 표현 → 업무 언어로 변경

---

### 9-3. 모달 레이아웃 개선

- [ ] 참여자 기본정보 영역 컴팩트하게 정리
  - 상담에 자주 쓰는 정보(`infoStage`, `infoFocus`, `Referral.infoAlsonDetail`) 상단 배치
  - `infoEducation`/`infoMajor`/`categoryList`는 접기/펼치기 처리 검토
- [ ] 추천 리스트 기본 표시 건수 결정 (기본 5건, 더보기)
  ```javascript
  // 처음에는 5건만 표시, 더보기로 전체 표시
  var defaultShowCount = 5;
  var showAll = false;

  function renderRecommendList(list) {
      var displayList = showAll ? list : list.slice(0, defaultShowCount);
      // ... 렌더링 ...
      if (list.length > defaultShowCount && !showAll) {
          // 더보기 버튼 표시
      }
  }
  ```
- [ ] 모달 스크롤 동작 개선 (참여자 정보 고정, 추천 리스트 스크롤)
  ```css
  .modal-participant-info {
      position: sticky;
      top: 0;
      background: #fff;
      z-index: 1;
      border-bottom: 1px solid #ddd;
      padding-bottom: 12px;
  }
  .modal-recommend-list {
      overflow-y: auto;
      max-height: 350px;
  }
  ```
- [ ] 모달 너비 반응형 처리 (1366×768 화면에서도 불편 없도록)
  ```css
  .modal-container {
      width: min(900px, 95vw);
      max-height: 85vh;
  }
  ```

---

### 9-4. 검색 필터 접근성 개선

- [ ] 집중 알선 필터를 자주 쓰는 검색 조건 최상단 또는 강조 위치에 배치
- [ ] 검색 버튼과 집중 알선 체크박스 간 간격 최적화
- [ ] 현재 적용된 필터 조건 표시 (예: 태그 형태로 "집중알선: 대상자")
  ```html
  <div id="activeFilterTags" class="filter-tags">
      <!-- JS로 동적 렌더링 -->
  </div>
  ```
  ```javascript
  function renderActiveFilterTags(searchParams) {
      var container = document.getElementById('activeFilterTags');
      container.innerHTML = '';
      if (searchParams.집중알선여부 === '1') {
          container.innerHTML += '<span class="filter-tag">집중알선: 대상자 <a onclick="clearFilter(\'집중알선여부\')">✕</a></span>';
      }
  }
  ```

---

### 9-5. 추천 결과 가독성 개선 (피드백 반영)

- [ ] 추천 사유 기본 표시 줄 수 조정 (2줄 기본 표시 후 더보기)
- [ ] 기업명(`recommendedJobCompany`)/채용제목(`recommendedJobTitle`) 강조 폰트 굵기 조정
- [ ] 베스트 채용정보 배경색 및 테두리 색상 조정 (너무 강하거나 약한 경우)
- [ ] `recommendationScore` null인 경우(알선상세정보 없는 참여자) 점수 컬럼 처리 방식 결정
  - 옵션 1: "-" 표시
  - 옵션 2: 점수 컬럼 자체 숨김
- [ ] 저장일시(`createdAt`) 표시 형식 결정 (예: "2026-05-10 14:32" 또는 "3일 전")

---

### 9-6. 성능 체감 개선

- [ ] AI 추천 저장 중 진행 상황 표시 개선
  ```javascript
  var progressMessages = [
      'AI가 참여자 정보를 분석 중...',
      '채용정보를 검색하는 중...',
      '최적 채용정보를 선별하는 중...',
      '추천 결과를 저장하는 중...'
  ];
  var msgIndex = 0;
  var progressInterval = setInterval(function() {
      msgIndex = (msgIndex + 1) % progressMessages.length;
      document.getElementById('recommendStatusMsg').innerText = progressMessages[msgIndex];
  }, 3000);
  // 완료 시 clearInterval(progressInterval);
  ```
- [ ] 로딩 스피너 추가 (텍스트만 있는 경우 시각적 피드백 부족)
  ```css
  .loading-spinner {
      display: inline-block;
      width: 20px; height: 20px;
      border: 3px solid #ddd;
      border-top-color: #2980b9;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
  }
  @keyframes spin { to { transform: rotate(360deg); } }
  ```

---

## 3. 산출물

| 산출물 | 내용 | 완료 여부 |
|--------|------|----------|
| 피드백 기록표 | 상담사 피드백 전체 수집 | 🔲 |
| UI 문구 개선 | 버튼/메시지/레이블 텍스트 수정 | 🔲 |
| 모달 레이아웃 개선 | 컴팩트 정보 영역 + 리스트 스크롤 | 🔲 |
| 검색 필터 UX | 활성 필터 태그 표시 | 🔲 |
| 로딩 경험 개선 | 진행 메시지 + 스피너 | 🔲 |

---

## 4. 사용성 검증 체크리스트

- [ ] 처음 쓰는 상담사가 별도 설명 없이 집중 알선 필터를 찾을 수 있다
- [ ] 추천 버튼 위치가 자연스럽다
- [ ] 모달에서 원하는 정보를 3초 이내에 확인할 수 있다
- [ ] AI 추천 저장 버튼이 언제 눌러야 하는지 명확하다
- [ ] 오류 발생 시 무엇을 해야 할지 메시지가 충분하다

---

## 5. 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2026-04-03 | v0.1 | 최초 작성 | SD |
| 2026-04-13 | v0.2 | 시연 데이터 준비 항목의 필드명을 실제 API 응답 키로 수정(`infoFocus`, `infoStage`, `infoAlsonDetail`); 추천 결과 필드명 수정(`recommendedJobCompany`, `recommendedJobTitle`, `recommendationScore`, `createdAt`) | SD |