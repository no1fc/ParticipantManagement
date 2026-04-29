# AI_Function_4Week_Progress.md — 4주차: 참여자관리 화면 리스트 수정 및 버튼 추가

> **기간:** 2026-04-24 ~ 2026-05-01  
> **상태:** 🔲 진행중  
> **목표:** 참여자관리 JSP 화면에 집중 알선 검색 필터, 추천 버튼, 모달 기본 레이아웃 구현

---

## 1. 이번 주 목표

백엔드 기반이 완성된 상태에서 화면(JSP) 레이어를 수정한다.  
이번 주는 **데이터 연동 없이 UI 구조만 완성**하는 것을 목표로 한다.  
(실제 데이터 바인딩은 5주차에 진행)

---

## 2. 세부 작업 목록

### 4-1. 검색 필터 UI 추가 (JSP)

> 수정 파일: `src/main/webapp/WEB-INF/view/[참여자관리 JSP]`  
> (1주차에 확인한 JSP 파일 경로 적용)

- [x] 기존 검색 폼(`<form>`) 구조 파악
  - 검색 폼 action URL 확인
  - 기존 검색 조건 input 목록 확인
- [x] 집중 알선 여부 필터 UI 추가
  ```html
  <!-- 집중알선여부 검색 필터 (기존 검색 조건 영역 내 추가) -->
  <div class="search-item">
      <label>집중 알선</label>
      <select name="집중알선여부">
          <option value="">전체</option>
          <option value="1">집중 알선 대상</option>
          <option value="0">일반</option>
      </select>
  </div>
  ```
  - 체크박스 방식 vs 셀렉트박스 방식 중 기존 UI 패턴에 맞는 방식 선택 : 체크박스 형식
- [ ] 검색 시 `집중알선여부` 파라미터가 서버에 전달되는지 확인
  - 기존 검색 폼 submit JS 함수에 별도 처리 필요 여부 확인
- [x] 페이지 재진입 시 선택값 유지 처리 (JSTL `selected` 조건 처리)
  ```html
  <option value="1" ${searchBean.집중알선여부 == '1' ? 'selected' : ''}>집중 알선 대상</option>
  ```

---

### 4-2. 참여자 리스트 테이블 수정 (JSP)

- [x] 리스트 테이블 헤더(`<th>`)에 "추천" 컬럼 추가
  ```html
  <th>추천 채용정보</th>
  ```
- [x] 리스트 데이터 행(`<td>`)에 추천 버튼 추가
  ```html
  <td>
    <button type="button"
            class="btn-recommend"
            data-gujik="${data.participantJobNo}"
            data-name="${data.participantPartic}"
            data-stage="${data.participantProgress}"
            onclick="openRecommendModal(this)">
        추천 채용정보
    </button>
  </td>
  ```
  - `data-gujik`: 구직번호 (모달 API 호출 시 사용)
  - `data-name`: 참여자명 (모달 헤더 표시용)
  - `data-stage`: 진행단계 (AI 추천 저장 버튼 활성화 조건 판단용)
- [x] 집중 알선 여부 표시 UI 추가
  ```html
  <!-- 기존 참여자명 또는 진행단계 컬럼 옆에 배지 추가 -->
  <c:if test="${data.participantISIntensiveMediation == 'Y'}">
  <p class="badge-jibjoong">집중알선</p>
  </c:if>
  ```
- [x] 컬럼 너비 및 레이아웃 조정 (기존 테이블 균형 유지)

---

### 4-3. 추천 모달 기본 레이아웃 작성 (JSP/HTML)

> 모달은 기존 참여자관리 JSP 파일 하단에 추가하거나, 별도 include 파일로 분리

- [x] 모달 컨테이너 기본 마크업
  ```html
  <!-- 참여자 추천 채용정보 모달 -->
  <div id="recommendModal" class="modal-overlay" style="display:none;">
    <div class="modal-container">
  
      <!-- =========================
           모달 헤더
           - 이미지처럼 좌측 제목 / 우측 닫기 버튼
           ========================= -->
      <div class="modal-header">
        <div class="modal-header-text">
          <h3 id="modalTitle">AI 알선 추천 채용정보</h3>
          <p class="modal-subtitle">고용24 DB와 참여자 특성을 분석한 추천 결과입니다.</p>
        </div>
  
        <button type="button" class="btn-modal-close" onclick="closeRecommendModal()">✕</button>
      </div>
  
      <!-- =========================
           참여자 요약 카드
           - 이미지처럼 상단에 핵심 정보를 박스 형태로 표시
           ========================= -->
      <div class="modal-participant-summary">
        <div class="summary-grid">
          <div class="summary-item">
            <span class="summary-label">참여자명</span>
            <div class="summary-value" id="infoName"></div>
          </div>
  
          <div class="summary-item">
            <span class="summary-label">진행단계</span>
            <div class="summary-value" id="infoStage"></div>
          </div>
  
          <div class="summary-item">
            <span class="summary-label">전공 / 희망직무</span>
            <div class="summary-value" id="infoMajor"></div>
          </div>
  
          <div class="summary-item">
            <span class="summary-label">상태</span>
            <div class="summary-value" id="infoJibjoong"></div>
          </div>
        </div>
      </div>
  
      <!-- =========================
           상세 정보 카드
           ========================= -->
      <div class="modal-section-card">
        <div class="section-title">참여자 기본 정보</div>
  
        <table class="info-table">
          <tr>
            <th>구직번호</th>
            <td id="infoGujikNo"></td>
            <th>학력</th>
            <td id="infoEducation"></td>
          </tr>
          <tr>
            <th>카테고리</th>
            <td colspan="3" id="infoCategory"></td>
          </tr>
          <tr>
            <th>희망직무</th>
            <td colspan="3" id="infoWantedJob"></td>
          </tr>
          <tr>
            <th>알선상세정보</th>
            <td colspan="3" id="infoAlsonDetail"></td>
          </tr>
        </table>
      </div>
  
      <!-- =========================
           액션 영역
           ========================= -->
      <div class="modal-action-area">
        <button type="button" id="btnAiRecommend" class="btn-ai-recommend" onclick="saveAiRecommend()" style="display:none;">
          AI 추천 채용정보 저장
        </button>
        <span id="recommendStatusMsg" class="status-msg"></span>
      </div>
  
      <!-- =========================
           로딩 영역
           ========================= -->
      <div id="recommendLoading" class="loading-area" style="display:none;">
        <span>추천 정보를 불러오는 중...</span>
      </div>
  
      <!-- =========================
           베스트 추천 영역
           ========================= -->
      <div id="bestRecommendArea" class="best-recommend-area" style="display:none;">
        <div class="section-title section-title-blue">베스트 매칭 채용정보</div>
        <div id="bestRecommendContent" class="best-card"></div>
      </div>
  
      <!-- =========================
           추천 채용정보 리스트 영역
           ========================= -->
      <div class="modal-section-card">
        <div class="section-title">기타 추천 후보군 (고용24 연동)</div>
  
        <div id="recommendListEmpty" class="empty-msg" style="display:none;">
          저장된 추천 채용정보가 없습니다.
        </div>
  
        <table id="recommendListTable" class="recommend-table" style="display:none;">
          <thead>
          <tr>
            <th>No</th>
            <th>기업명</th>
            <th>채용공고</th>
            <th>업종</th>
            <th>추천점수</th>
            <th>추천사유</th>
            <th>공고링크</th>
          </tr>
          </thead>
          <tbody id="recommendListBody"></tbody>
        </table>
      </div>
  
      <!-- =========================
           하단 공유 영역
           ========================= -->
      <div class="modal-bottom-bar">
        <div class="modal-share-area">
          <!-- TODO: 카카오톡 공유 버튼 추가 예정 -->
        </div>
      </div>
  
    </div>
  </div>
  ```

---

### 4-4. 모달 CSS 작성

> 기존 CSS 파일에 추가하거나, 신규 CSS 파일 분리 (`recommend-modal_0.0.1.css`)

- [x] 모달 css 적용
  ```css
  /* recommend-modal_0.0.1.css */
  /* =========================
  모달 오버레이
  ========================= */
  #recommendModal.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(17, 24, 39, 0.65);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  backdrop-filter: blur(4px);
  }
  
  /* =========================
  모달 본체
  ========================= */
  #recommendModal .modal-container {
  width: min(1120px, 96vw);
  max-height: 92vh;
  background: #fff;
  border-radius: 18px;
  overflow-y: auto;
  overflow-x: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.28);
  border: 1px solid rgba(226, 232, 240, 0.9);
  }
  
  /* =========================
  상단 헤더
  ========================= */
  #recommendModal .modal-header {
  background: linear-gradient(90deg, #283593 0%, #3949ab 40%, #3f51b5 100%);
  color: #fff;
  padding: 18px 22px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  }
  
  #recommendModal .modal-header-text {
  display: flex;
  flex-direction: column;
  gap: 6px;
  }
  
  #recommendModal #modalTitle {
  margin: 0;
  font-size: 1.65rem;
  font-weight: 800;
  letter-spacing: -0.02em;
  }
  
  #recommendModal .modal-subtitle {
  margin: 0;
  font-size: 0.92rem;
  color: rgba(255, 255, 255, 0.92);
  }
  
  #recommendModal .btn-modal-close {
  width: 38px;
  height: 38px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
  font-size: 1.2rem;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
  }
  
  #recommendModal .btn-modal-close:hover {
  background: rgba(255, 255, 255, 0.24);
  transform: scale(1.05);
  }
  
  /* =========================
  상단 참여자 요약 카드
  ========================= */
  #recommendModal .modal-participant-summary {
  display: grid;
  grid-template-columns: 92px 1fr;
  gap: 18px;
  align-items: center;
  padding: 20px 20px 0 20px;
  }
  
  #recommendModal .summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  }
  
  #recommendModal .summary-item {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 14px 16px;
  box-shadow: 0 6px 16px rgba(15, 23, 42, 0.05);
  }
  
  #recommendModal .summary-label {
  display: block;
  font-size: 0.82rem;
  color: #6b7280;
  margin-bottom: 8px;
  font-weight: 700;
  }
  
  #recommendModal .summary-value {
  font-size: 1rem;
  font-weight: 800;
  color: #1e293b;
  line-height: 1.35;
  }
  
  /* =========================
  섹션 카드 공통
  ========================= */
  #recommendModal .modal-section-card {
  margin: 18px 20px 0 20px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 18px;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.04);
  }
  
  #recommendModal .section-title {
  font-size: 1.02rem;
  font-weight: 800;
  color: #0f172a;
  margin-bottom: 14px;
  }
  
  #recommendModal .section-title-blue {
  color: #1e40af;
  }
  
  /* =========================
  정보 테이블
  ========================= */
  #recommendModal .info-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  }
  
  #recommendModal .info-table th {
  width: 11%;
  background: #f8fafc;
  color: #64748b;
  font-size: 0.88rem;
  font-weight: 700;
  text-align: left;
  padding: 14px 16px;
  border-bottom: 1px solid #e5e7eb;
  vertical-align: top;
  }
  
  #recommendModal .info-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #e5e7eb;
  color: #1e293b;
  font-size: 0.95rem;
  font-weight: 600;
  vertical-align: top;
  }
  
  #recommendModal .info-table tr:last-child th,
  #recommendModal .info-table tr:last-child td {
  border-bottom: none;
  }
  
  /* =========================
  상태 표시
  ========================= */
  #recommendModal .status-pill {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 700;
  line-height: 1;
  white-space: nowrap;
  }
  
  #recommendModal .status-pill.is-highlight {
  background: #fff3e0;
  color: #ef6c00;
  border: 1px solid #ffcc80;
  }
  
  #recommendModal .status-pill.is-success {
  background: #e8f5e9;
  color: #2e7d32;
  border: 1px solid #a5d6a7;
  }
  
  /* =========================
  액션 영역
  ========================= */
  #recommendModal .modal-action-area {
  margin: 18px 20px 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  }
  
  #recommendModal .btn-ai-recommend {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 11px 18px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 8px 18px rgba(37, 99, 235, 0.2);
  transition: transform 0.15s ease, background 0.2s ease;
  }
  
  #recommendModal .btn-ai-recommend:hover {
  background: #1d4ed8;
  transform: translateY(-1px);
  }
  
  #recommendModal .status-msg {
  color: #475569;
  font-size: 0.92rem;
  font-weight: 600;
  }
  
  /* =========================
  로딩 영역
  ========================= */
  #recommendModal .loading-area {
  margin: 18px 20px 0 20px;
  padding: 14px 16px;
  background: #f8fafc;
  border: 1px dashed #cbd5e1;
  border-radius: 12px;
  color: #334155;
  font-weight: 600;
  }
  
  /* =========================
  베스트 추천 영역
  ========================= */
  #recommendModal .best-recommend-area {
  margin: 18px 20px 0 20px;
  background: #eef4ff;
  border: 1px solid #dbe7ff;
  border-radius: 16px;
  padding: 18px;
  }
  
  #recommendModal .best-card {
  background: #fff;
  border: 1px solid #dbe3ff;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 8px 18px rgba(59, 130, 246, 0.08);
  }
  
  /* =========================
  추천 리스트
  ========================= */
  #recommendModal .empty-msg {
  padding: 16px;
  border-radius: 12px;
  background: #f8fafc;
  color: #64748b;
  text-align: center;
  border: 1px dashed #cbd5e1;
  }
  
  #recommendModal .recommend-table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  }
  
  #recommendModal .recommend-table thead th {
  background: #f8fafc;
  color: #334155;
  font-size: 0.88rem;
  font-weight: 800;
  padding: 12px 10px;
  border-bottom: 1px solid #e5e7eb;
  text-align: center;
  }
  
  #recommendModal .recommend-table tbody td {
  padding: 12px 10px;
  border-bottom: 1px solid #eef2f7;
  text-align: center;
  font-size: 0.92rem;
  color: #1e293b;
  vertical-align: middle;
  }
  
  #recommendModal .recommend-table tbody tr:hover {
  background: #f8fbff;
  }
  
  #recommendModal .recommend-table tbody tr:last-child td {
  border-bottom: none;
  }
  
  /* =========================
  집중알선 배지
  ========================= */
  #recommendModal .badge-jibjoong {
  display: inline-flex;
  align-items: center;
  background: #f59e0b;
  color: #fff;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 999px;
  margin-left: 6px;
  }
  
  /* =========================
  하단 바
  ========================= */
  #recommendModal .modal-bottom-bar {
  padding: 18px 20px 20px 20px;
  }
  
  /* =========================
  반응형
  ========================= */
  @media (max-width: 992px) {
  #recommendModal .summary-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  }
  
  @media (max-width: 768px) {
  #recommendModal .modal-container {
  width: 100%;
  max-height: 96vh;
  border-radius: 14px;
  }
  
      #recommendModal .modal-participant-summary {
          grid-template-columns: 1fr;
      }
  
      #recommendModal .summary-grid {
          grid-template-columns: 1fr;
      }
  
      #recommendModal #modalTitle {
          font-size: 1.25rem;
      }
  
      #recommendModal .info-table,
      #recommendModal .recommend-table {
          display: block;
          overflow-x: auto;
          white-space: nowrap;
      }
  }
  ```

---

### 4-5. 모달 열기/닫기 JS 기본 동작 구현

> 기존 JS 파일에 추가하거나, 신규 JS 파일 분리 (`recommend-modal_0.0.3.js`)

- [x] 모달 열기 함수 작성
  ```javascript
  function openRecommendModal(btn) {
      var gujikNo = btn.getAttribute('data-gujik');
      var name    = btn.getAttribute('data-name');
      var stage   = btn.getAttribute('data-stage');

      // 모달 헤더 표시
      document.getElementById('modalTitle').innerText = name + ' - 추천 채용정보';

      // AI 추천 저장 버튼: 진행단계 조건 체크
      var allowStages = ['IAP후', '미취업사후관리'];
      var btnAi = document.getElementById('btnAiRecommend');
      if (allowStages.includes(stage)) {
          btnAi.style.display = 'inline-block';
          btnAi.setAttribute('data-gujik', gujikNo);
      } else {
          btnAi.style.display = 'none';
      }

      // 리스트 초기화
      resetModalContent();

      // 모달 오픈
      document.getElementById('recommendModal').style.display = 'flex';

      // 데이터 로드 (5주차 AJAX 연동 시 구현)
      // loadRecommendData(gujikNo);
  }
  ```
- [x] 모달 닫기 함수 작성
  ```javascript
    /* include로 modal을 불러오기 때문에 document로 감싸야 함수 실행이 가능함 */
    $(document).ready(function () {
      /**
       * 추천 모달 닫기 함수
       */
      document.getElementById('recommendModal').addEventListener('click', function(e) {
          if (e.target === this) closeRecommendModal();
      });
    })
  ```
- [x] 모달 내용 초기화 함수 작성
  ```javascript
  function resetModalContent() {
      document.getElementById('infoName').innerText = '';
      document.getElementById('infoGujikNo').innerText = '';
      document.getElementById('infoStage').innerText = '';
      document.getElementById('infoJibjoong').innerText = '';
      document.getElementById('infoEducation').innerText = '';
      document.getElementById('infoMajor').innerText = '';
      document.getElementById('infoCategory').innerText = '';
      document.getElementById('infoWantedJob').innerText = '';
      document.getElementById('infoAlsonDetail').innerText = '';
      document.getElementById('recommendListBody').innerHTML = '';
      document.getElementById('recommendListTable').style.display = 'none';
      document.getElementById('recommendListEmpty').style.display = 'none';
      document.getElementById('bestRecommendArea').style.display = 'none';
      document.getElementById('bestRecommendContent').innerHTML = '';
      document.getElementById('recommendStatusMsg').innerText = '';
  }
  ```
- [x] 모달 오버레이 클릭 시 닫기 처리
  ```javascript
  document.getElementById('recommendModal').addEventListener('click', function(e) {
      if (e.target === this) closeRecommendModal();
  });
  ```
- [x] ESC 키 닫기 처리
  ```javascript
  document.addEventListener('keydown', function(e) {
      if (e.key === 'Escape') closeRecommendModal();
  });
  ```

---

### 4-6. Controller 확인

- [ ] 기존 참여자 리스트 조회 Controller에서 검색 조건 Bean에 `집중알선여부` 파라미터 바인딩 여부 확인
  - Spring MVC의 `@ModelAttribute` 또는 직접 `@RequestParam` 방식 확인
  - 자동 바인딩 가능하면 Controller 수정 불필요
- [ ] 수동 처리 필요 시 Controller에 파라미터 바인딩 코드 추가

---

## 3. 산출물

| 산출물 | 내용 | 완료 여부 |
|--------|------|----------|
| JSP 수정 | 집중알선 필터 UI + 추천 버튼 + 집중알선 배지 | 🔲 |
| JSP 추가 | 추천 모달 전체 마크업 | 🔲 |
| CSS 추가 | 모달 + 배지 + 베스트카드 + 버튼 스타일 | 🔲 |
| JS 추가 | 모달 열기/닫기/초기화 함수 | 🔲 |
| Controller 확인 | 집중알선여부 파라미터 바인딩 동작 확인 | 🔲 |

---

## 4. UI 동작 확인 체크리스트

- [x] 집중 알선 필터 선택 후 검색 시 파라미터 서버 전달 확인
- [x] 페이지 재진입 시 필터 선택값 유지 확인
- [x] 리스트에 "추천 채용정보" 버튼 렌더링 확인
- [x] 버튼 클릭 시 모달 오픈 확인
- [x] 모달 닫기(X 버튼, 오버레이 클릭, ESC) 동작 확인
- [ ] `IAP후` / `미취업사후관리` 참여자 → AI 추천 저장 버튼 노출 확인
- [ ] 그 외 참여자 → AI 추천 저장 버튼 미노출 확인
- [ ] 집중알선 대상 참여자 배지 표시 확인

---

## 5. 변경 이력

| 날짜         | 버전   | 변경 내용            | 작성자 |
|------------|------|------------------|-----|
| 2026-04-03 | v0.1 | 최초 작성            | SD  |
| 2026-04-10 | v0.2 | modal생성 및 css 적용 | SD  |