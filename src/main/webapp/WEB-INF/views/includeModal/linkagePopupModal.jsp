<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--
  국취 연계실적 독려 팝업 (임시기능 ~2026-10-31) — 시안 picture.jpg 레이아웃.
  - 상담사 대시보드 진입 시 노출. 데이터는 /linkage-popup/summary 비동기 조회.
  - 노출/문구/기준값은 J_참여자관리_연계실적팝업설정(시드값)에서 제어.
  - 제거 시: 이 include 1줄 + linkagePopup_*.js/css + DashBoardPage.jsp head 2줄만 삭제.
  - 아이콘 = Bootstrap Icons(대시보드에 이미 로드됨).
--%>
<div id="linkagePopupOverlay" class="lp-overlay" style="display:none;">
    <div id="linkagePopupModal" class="lp-modal lp-stage-1">

        <!-- 헤더(중앙정렬) -->
        <div class="lp-header">
            <h3 class="lp-title">국민취업지원제도 연계 실적 안내</h3>
            <p class="lp-subhead">오늘도 참여자에게 더 많은 기회를 연결해 주세요!</p>
            <button type="button" class="lp-close" id="lpCloseBtn" aria-label="닫기">
                <i class="bi bi-x-lg"></i>
            </button>
        </div>

        <!-- 본문: 좌측 지표 / 우측 단계 패널 -->
        <div class="lp-body">

            <!-- 좌측 컬럼 -->
            <div class="lp-left">
                <p class="lp-section-label"><span id="lpNameLabel" class="lp-name"></span>님, 현재 연계 현황을 확인해 주세요!</p>

                <div class="lp-card">
                    <div class="lp-card-icon lp-ico-blue"><i class="bi bi-people-fill"></i></div>
                    <div class="lp-card-text">
                        <span class="lp-card-label">전체 상담사 평균 연계 건수</span>
                    </div>
                    <div class="lp-card-value lp-val-blue"><strong id="lpBenchmark">0</strong><span class="lp-unit">건 내외</span></div>
                </div>

                <div class="lp-card">
                    <div class="lp-card-icon lp-ico-green"><i class="bi bi-bullseye"></i></div>
                    <div class="lp-card-text">
                        <span class="lp-card-label">1인당 목표 연계 건수</span>
                        <span class="lp-card-sub">(<span id="lpTargetBase">40</span>건 &divide; 지점 상담사 수)</span>
                    </div>
                    <div class="lp-card-value lp-val-green"><strong id="lpTarget">0</strong><span class="lp-unit">건 이상</span></div>
                </div>

                <div class="lp-card lp-card-highlight">
                    <div class="lp-card-icon lp-ico-orange"><i class="bi bi-person-fill"></i></div>
                    <div class="lp-card-text">
                        <span class="lp-card-label"><span id="lpNameMy" class="lp-name-strong"></span>님의 현재 연계 건수</span>
                    </div>
                    <div class="lp-card-value lp-val-accent"><strong id="lpMy">0</strong><span class="lp-unit">건</span></div>
                </div>

                <div class="lp-card">
                    <div class="lp-card-icon lp-ico-purple"><i class="bi bi-flag-fill"></i></div>
                    <div class="lp-card-text">
                        <span class="lp-card-label">목표까지 남은 연계 건수</span>
                    </div>
                    <div class="lp-card-value lp-val-purple"><strong id="lpRemaining">0</strong><span class="lp-unit">건</span></div>
                </div>

                <div class="lp-notice">
                    <i class="bi bi-graph-up-arrow"></i>
                    <span>연계 건수 실적은 <b class="lp-notice-em">업무담당자 성과평가(연봉테이블)</b>에 반영됩니다.</span>
                </div>
            </div>

            <!-- 우측 단계 패널 -->
            <div class="lp-right">
                <div class="lp-panel">
                    <p class="lp-stage-headline" id="lpStageHeadline"></p>
                    <p class="lp-stage-sub" id="lpStageSub"></p>

                    <hr class="lp-divider">

                    <p class="lp-message" id="lpMessage"></p>

                    <!-- 게이지: 목표 대비 달성률(현재 ÷ 목표) -->
                    <div class="lp-gauge">
                        <p class="lp-gauge-pct">목표 달성률 <b id="lpGaugePct">0%</b></p>
                        <div class="lp-gauge-track">
                            <div class="lp-gauge-fill" id="lpGaugeFill"></div>
                            <div class="lp-gauge-marker" id="lpGaugeMarker">
                                <span class="lp-gauge-dot"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>

        <!-- 푸터 -->
        <div class="lp-footer">
            <div class="lp-footer-actions">
                <label class="lp-dont-show">
                    <input type="checkbox" id="lpDontShowToday"> 오늘 하루 보지 않기
                </label>
                <button type="button" class="lp-footer-close" id="lpFooterClose">닫기</button>
            </div>
            <p class="lp-note" id="lpPeriodNote">※ 연계 실적 집계 기준: 일경험 및 타사업 연계일이 등록된 건을 기준으로 집계됩니다.</p>
        </div>

    </div>
</div>
