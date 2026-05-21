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
          <th>복사</th>
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

<!-- 상담일지 복사 서브모달 -->
<div id="copyLogModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); z-index:10001;" onclick="closeCopyLogModal()">
  <div class="copy-log-container" style="position:absolute; top:50%; left:50%; transform:translate(-50%,-50%); width:700px; max-height:85vh; overflow-y:auto; background:#fff; border-radius:8px; box-shadow:0 4px 20px rgba(0,0,0,0.3);" onclick="event.stopPropagation()">

    <!-- 헤더 -->
    <div class="copy-log-header d-flex justify-content-between align-items-center px-4 py-3" style="border-bottom:1px solid #dee2e6;">
      <h5 class="mb-0 font-weight-bold"><i class="fas fa-copy mr-2"></i>상담일지 복사</h5>
      <button type="button" class="close" onclick="closeCopyLogModal()" aria-label="닫기">
        <span aria-hidden="true">&times;</span>
      </button>
    </div>

    <!-- 항목 선택 영역 -->
    <div class="copy-column-section px-4 py-3">
      <label class="font-weight-bold mb-2 d-block">기본 항목</label>
      <div class="row">
        <div class="col-4 form-check form-check-inline ml-2 mb-2">
          <input class="form-check-input copy-column-check" type="checkbox" id="copy_companyNm" data-key="companyNm" onchange="updateCopyPreview()" checked>
          <label class="form-check-label" for="copy_companyNm">기업명</label>
        </div>
        <div class="col-4 form-check form-check-inline ml-2 mb-2">
          <input class="form-check-input copy-column-check" type="checkbox" id="copy_recrutTitle" data-key="recrutTitle" onchange="updateCopyPreview()" checked>
          <label class="form-check-label" for="copy_recrutTitle">채용공고</label>
        </div>
        <div class="col-4 form-check form-check-inline ml-2 mb-2">
          <input class="form-check-input copy-column-check" type="checkbox" id="copy_indTpNm" data-key="indTpNm" onchange="updateCopyPreview()" checked>
          <label class="form-check-label" for="copy_indTpNm">업종</label>
        </div>
        <div class="col-4 form-check form-check-inline ml-2 mb-2">
          <input class="form-check-input copy-column-check" type="checkbox" id="copy_jobsNm" data-key="jobsNm" onchange="updateCopyPreview()" checked>
          <label class="form-check-label" for="copy_jobsNm">직무</label>
        </div>
        <div class="col-4 form-check form-check-inline ml-2 mb-2">
          <input class="form-check-input copy-column-check" type="checkbox" id="copy_qualification" data-key="qualification" onchange="updateCopyPreview()" checked>
          <label class="form-check-label" for="copy_qualification">지원자격</label>
        </div>
        <div class="col-4 form-check form-check-inline ml-2 mb-2">
          <input class="form-check-input copy-column-check" type="checkbox" id="copy_salDesc" data-key="salDesc" onchange="updateCopyPreview()" checked>
          <label class="form-check-label" for="copy_salDesc">급여</label>
        </div>
        <div class="col-4 form-check form-check-inline ml-2 mb-2">
          <input class="form-check-input copy-column-check" type="checkbox" id="copy_empTpNm" data-key="empTpNm" onchange="updateCopyPreview()" checked>
          <label class="form-check-label" for="copy_empTpNm">고용형태</label>
        </div>
        <div class="col-4 form-check form-check-inline ml-2 mb-2">
          <input class="form-check-input copy-column-check" type="checkbox" id="copy_regionNm" data-key="regionNm" onchange="updateCopyPreview()" checked>
          <label class="form-check-label" for="copy_regionNm">근무지역</label>
        </div>
        <div class="col-4 form-check form-check-inline ml-2 mb-2">
          <input class="form-check-input copy-column-check" type="checkbox" id="copy_recommendationReason" data-key="recommendationReason" onchange="updateCopyPreview()" checked>
          <label class="form-check-label" for="copy_recommendationReason">추천사유</label>
        </div>
        <div class="col-4 form-check form-check-inline ml-2 mb-2">
          <input class="form-check-input copy-column-check" type="checkbox" id="copy_wantedInfoUrl" data-key="wantedInfoUrl" onchange="updateCopyPreview()" checked>
          <label class="form-check-label" for="copy_wantedInfoUrl">공고URL</label>
        </div>
        <div class="col-4 form-check form-check-inline ml-2 mb-2">
          <input class="form-check-input copy-column-check" type="checkbox" id="copy_infoSvc" data-key="infoSvc" onchange="updateCopyPreview()" checked>
          <label class="form-check-label" for="copy_infoSvc">정보제공처</label>
        </div>
      </div>

      <!-- 더보기 토글 -->
      <button type="button" class="btn btn-sm btn-outline-secondary mt-2 mb-2" onclick="toggleMoreCopyColumns()">
        더보기 <i class="fas fa-chevron-down ml-1"></i>
      </button>

      <!-- 추가 항목 (기본 숨김) -->
      <div class="copy-column-more" style="display:none;">
        <label class="font-weight-bold mb-2 d-block mt-2">추가 항목</label>
        <div class="row">
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_reperNm" data-key="reperNm" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_reperNm">대표자명</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_busiSize" data-key="busiSize" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_busiSize">회사규모</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_totPsncnt" data-key="totPsncnt" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_totPsncnt">근로자수</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_capitalAmt" data-key="capitalAmt" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_capitalAmt">자본금</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_yrSalesAmt" data-key="yrSalesAmt" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_yrSalesAmt">연매출</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_busiCont" data-key="busiCont" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_busiCont">사업내용</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_homePg" data-key="homePg" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_homePg">홈페이지</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_recrutPeri" data-key="recrutPeri" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_recrutPeri">채용기간</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_recruitCnt" data-key="recruitCnt" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_recruitCnt">모집인원</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_jobCont" data-key="jobCont" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_jobCont">직무내용</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_workTimeCont" data-key="workTimeCont" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_workTimeCont">근무시간</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_fourIns" data-key="fourIns" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_fourIns">4대보험</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_retirePay" data-key="retirePay" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_retirePay">퇴직금</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_welfareDesc" data-key="welfareDesc" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_welfareDesc">복리후생</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_career" data-key="career" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_career">경력</label>
          </div>
          <div class="col-4 form-check form-check-inline ml-2 mb-2">
            <input class="form-check-input copy-column-check" type="checkbox" id="copy_education" data-key="education" onchange="updateCopyPreview()">
            <label class="form-check-label" for="copy_education">학력</label>
          </div>
        </div>
      </div>
    </div>

    <!-- 미리보기 영역 -->
    <div class="copy-preview-section px-4 py-3" style="border-top:1px solid #dee2e6;">
      <label class="font-weight-bold mb-2 d-block">미리보기</label>
      <textarea id="copyPreviewText" class="form-control" rows="10" readonly placeholder="항목을 선택하면 미리보기가 표시됩니다."></textarea>
    </div>

    <!-- 액션 버튼 -->
    <div class="copy-log-actions px-4 py-3 d-flex justify-content-end" style="border-top:1px solid #dee2e6;">
      <button type="button" class="btn btn-primary mr-2" onclick="executeCopyLog()">
        <i class="fas fa-copy mr-1"></i>복사하기
      </button>
      <button type="button" class="btn btn-secondary" onclick="closeCopyLogModal()">
        닫기
      </button>
    </div>

  </div>
</div>