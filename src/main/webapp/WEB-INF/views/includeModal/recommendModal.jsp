<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 26. 4. 10.
  Time: 오전 11:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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
      <div class="summary-avatar">
        <span id="summaryGender">남</span>
      </div>

      <div class="summary-grid">
        <div class="summary-item">
          <span class="summary-label">구직번호</span>
          <div class="summary-value" id="infoGujikNo"></div>
        </div>

        <div class="summary-item">
          <span class="summary-label">참여자명</span>
          <div class="summary-value" id="infoName"></div>
        </div>

        <div class="summary-item">
          <span class="summary-label">진행단계</span>
          <div class="summary-value" id="infoStage"></div>
        </div>

        <div class="summary-item">
          <span class="summary-label">학력 / 전공</span>
          <div class="summary-value" id="infoEducation"></div>
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
          <th >카테고리</th>
          <td colspan="2" id="infoCategory"></td>
          <th >희망직무</th>
          <td colspan="2" id="infoWantedJob"></td>
        </tr>

        <tr>
          <th>알선상세정보</th>
          <td colspan="5" id="infoAlsonDetail"></td>
        </tr>

        <tr>
          <th>추천사</th>
          <td colspan="5" id="infoAdditionalInfo"></td>
        </tr>
      </table>
    </div>

    <!-- =========================
         액션 영역
         ========================= -->
    <div class="modal-action-area">
      <button type="button" id="btnAiRecommend" class="btn-ai-recommend" onclick="saveAiRecommend()" style="display:none;">
        AI 채용정보 추천 시작 및 저장
      </button>
      <span id="recommendSlotInfo" class="recommend-slot-info" style="display:none;">
        현재 AI 추천 진행 중: <span id="recommendSlotCount">0</span>건 / 최대 5건
      </span>
      <div>
        <span id="loadingSpinner" class="loading-spinner" style="display: none;"></span>
        <span id="recommendStatusMsg" class="status-msg"></span>
      </div>

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
      <div class="section-title-with-action">
        <div class="section-title">기타 추천 후보군 (고용24 연동)</div>
        <div class="section-action-area">
          <button type="button" id="btnKakaoShare" class="btn-kakao-share" onclick="shareSelectedViaKakao()">
            <svg class="kakao-icon" viewBox="0 0 24 24" width="20" height="20" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 3C6.48 3 2 6.58 2 10.94c0 2.8 1.86 5.27 4.66 6.67-.15.56-.96 3.6-.99 3.83 0 0-.02.17.09.24.11.06.24.01.24.01.32-.04 3.7-2.44 4.28-2.86.55.08 1.13.12 1.72.12 5.52 0 10-3.58 10-7.99C22 6.58 17.52 3 12 3z" fill="#191919"/>
            </svg>
            선택 항목 카카오톡 공유
          </button>
          <button type="button" id="btnKakaoCancelShare" class="btn-kakao-cancel" onclick="cancelKakaoShareQueue()" style="display:none;">
            공유 취소
          </button>
          <span id="kakaoShareStatus" class="kakao-share-status"></span>
        </div>
      </div>

      <!-- 공유 진행 상태 표시 -->
      <div id="kakaoShareProgress" class="kakao-share-progress" style="display:none;">
        <div class="share-progress-bar">
          <div id="shareProgressFill" class="share-progress-fill" style="width:0%"></div>
        </div>
        <div class="share-progress-info">
          <span id="shareProgressText"></span>
          <span id="shareNextInfo" class="share-next-info"></span>
        </div>
      </div>

      <div id="recommendListEmpty" class="empty-msg" style="display:none;">
        저장된 추천 채용정보가 없습니다.
        <p class="empty-sub">
          AI 추천 저장 버튼을 클릭하면
          <br>참여자 조건에 맞는 채용정보를 자동으로 추천합니다.
          <br>(2~3분의 시간이 소요됩니다. AI 추천 후 다른 작업이 가능합니다.)
        </p>
      </div>

      <table id="recommendListTable" class="recommend-table" style="display:none;">
        <thead>
        <tr>
          <th><input type="checkbox" id="checkAll" onclick="toggleAllRecommendCheck(this)"></th>
          <th>No</th>
          <th>기업명</th>
          <th>채용공고</th>
          <th>업종</th>
          <th>추천점수</th>
          <th>추천사유</th>
          <th>공고링크</th>
          <th>공유</th>
        </tr>
        </thead>
        <tbody id="recommendListBody"></tbody>
      </table>
    </div>

  </div>
</div>

<!-- AI 추천 완료 토스트 알림 컨테이너 (모달 외부) -->
<div id="recommendToastContainer" class="recommend-toast-container"></div>