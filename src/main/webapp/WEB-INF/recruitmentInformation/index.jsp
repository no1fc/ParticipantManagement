<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 26. 3. 23.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>채용공고 검색 - JobMoa</title>
    <mytag:Logo/>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"
            integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <link rel="preconnect" href="https://cdn.jsdelivr.net"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet"/>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans+KR:wght@300;400;500;600;700&family=Noto+Sans+KR:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/recruitmentInformationCss/style_0.0.1.css"/>

</head>
<body>

<!-- ===== 헤더 ===== -->
<header class="ri-header">
    <a href="/jobinfo" class="ri-brand">
        <span class="logo-text">JobMoa</span>
        <span class="page-title"><i class="bi bi-briefcase me-1"></i>채용공고 검색</span>
    </a>
    <a href="/jobinfo" class="ri-back"><i class="bi bi-arrow-left"></i> 메인으로</a>
</header>

<!-- ===== 메인 ===== -->
<main class="ri-shell">

    <section class="ri-hero">
        <h1><i class="bi bi-briefcase-fill me-2" style="color:var(--accent-1)"></i>채용공고 검색</h1>
        <p>고용24 채용정보를 검색하고 원하는 공고를 찾아보세요.</p>
    </section>

    <!-- ======================== 검색 패널 ======================== -->
    <section class="search-panel">

        <!-- ===== 패널 헤더 (토글) ===== -->
        <div class="sp-panel-header">
            <div class="sp-panel-title-wrap">
                <i class="bi bi-sliders2"></i>
                <span class="sp-panel-title">채용 검색 필터</span>
            </div>
            <button type="button" class="sp-toggle-btn" id="filterToggleBtn">
                <span id="toggleBtnText">상세 필터 펼치기</span>
                <i class="bi bi-chevron-down" id="toggleIcon"></i>
            </button>
        </div>

        <!-- 1. 근무지역 / 희망직종 -->
        <div class="sp-section-title"><i class="bi bi-geo-alt"></i> 지역 / 직종</div>
        <div class="row g-3 mb-3">
            <!-- 근무지역 -->
            <div class="col-12 col-md-6">
                <label class="form-label-sm"><i class="bi bi-geo-alt"></i> 근무 지역 <span class="chip-limit ms-1" id="regionLimitText">(최대 5개)</span></label>
                <button type="button" class="modal-trigger-btn" id="regionTriggerBtn" data-bs-toggle="modal" data-bs-target="#regionModal">
                    <i class="bi bi-map"></i>
                    <span class="trigger-text" id="regionTriggerText">지역을 선택하세요</span>
                    <i class="bi bi-chevron-down text-muted" style="font-size:.75rem;"></i>
                </button>
                <div class="selected-chips" id="regionChips"></div>
            </div>
            <!-- 희망직종 -->
            <div class="col-12 col-md-6">
                <label class="form-label-sm"><i class="bi bi-briefcase"></i> 희망 직종 <span class="chip-limit ms-1" id="occupationLimitText">(최대 10개)</span></label>
                <button type="button" class="modal-trigger-btn" id="occupationTriggerBtn" data-bs-toggle="modal" data-bs-target="#occupationModal">
                    <i class="bi bi-tools"></i>
                    <span class="trigger-text" id="occupationTriggerText">직종을 선택하세요</span>
                    <i class="bi bi-chevron-down text-muted" style="font-size:.75rem;"></i>
                </button>
                <div class="selected-chips" id="occupationChips"></div>
            </div>
        </div>

        <!-- ===== 상세 필터 (접기/펼치기) ===== -->
        <div class="sp-advanced-filters" id="advancedFilters">

        <hr class="sp-divider">

        <!-- 2. 고용형태 / 임금형태 / 급여범위 -->
        <div class="sp-section-title"><i class="bi bi-cash-coin"></i> 고용 / 급여</div>
        <div class="row g-3 mb-3">
            <div class="col-12 col-md-4">
                <label class="form-label-sm"><i class="bi bi-person-workspace"></i> 고용 형태</label>
                <select id="empTp" class="form-select">
                    <option value="">전체</option>
                    <option value="10">기간 정함 없는 근로 (정규직)</option>
                    <option value="11">정규직 시간선택제</option>
                    <option value="20">기간 정함 있는 근로 (계약직)</option>
                    <option value="21">계약직 시간선택제</option>
                    <option value="4">파견근로</option>
                    <option value="Y">대체인력채용</option>
                </select>
            </div>
            <div class="col-12 col-md-3">
                <label class="form-label-sm"><i class="bi bi-cash-coin"></i> 임금 형태</label>
                <select id="salTp" class="form-select">
                    <option value="">전체</option>
                    <option value="Y">연봉</option>
                    <option value="M">월급</option>
                    <option value="D">일급</option>
                    <option value="H">시급</option>
                </select>
            </div>
            <div class="col-12 col-md-5">
                <label class="form-label-sm"><i class="bi bi-currency-won"></i> 급여 범위 <span class="text-muted fw-normal">(임금형태 선택 후 입력)</span></label>
                <div class="pay-range-row">
                    <input type="number" id="minPay" class="form-control" placeholder="최소 (원)" min="0" disabled>
                    <span class="pay-sep">~</span>
                    <input type="number" id="maxPay" class="form-control" placeholder="최대 (원)" min="0" disabled>
                </div>
            </div>
        </div>

        <hr class="sp-divider">

        <!-- 3. 학력 -->
        <div class="sp-section-title"><i class="bi bi-mortarboard"></i> 학력</div>
        <div class="cb-group mb-3" id="educationGroup">
            <label class="cb-item" data-val="00"><input type="checkbox" value="00"><i class="bi bi-check2 cb-icon"></i> 학력무관</label>
            <label class="cb-item" data-val="01"><input type="checkbox" value="01"><i class="bi bi-check2 cb-icon"></i> 초졸 이하</label>
            <label class="cb-item" data-val="02"><input type="checkbox" value="02"><i class="bi bi-check2 cb-icon"></i> 중졸</label>
            <label class="cb-item" data-val="03"><input type="checkbox" value="03"><i class="bi bi-check2 cb-icon"></i> 고졸</label>
            <label class="cb-item" data-val="04"><input type="checkbox" value="04"><i class="bi bi-check2 cb-icon"></i> 대졸(2~3년)</label>
            <label class="cb-item" data-val="05"><input type="checkbox" value="05"><i class="bi bi-check2 cb-icon"></i> 대졸(4년)</label>
            <label class="cb-item" data-val="06"><input type="checkbox" value="06"><i class="bi bi-check2 cb-icon"></i> 석사</label>
            <label class="cb-item" data-val="07"><input type="checkbox" value="07"><i class="bi bi-check2 cb-icon"></i> 박사</label>
        </div>
        <p class="text-muted" style="font-size:.75rem;margin-top:-8px;margin-bottom:0;">※ 학력무관 선택 시 다른 항목은 선택 불가</p>

        <hr class="sp-divider">

        <!-- 4. 경력 -->
        <div class="sp-section-title"><i class="bi bi-briefcase"></i> 경력</div>
        <div class="cb-group mb-3" id="careerGroup">
            <label class="cb-item" data-val="N"><input type="checkbox" value="N"><i class="bi bi-check2 cb-icon"></i> 신입</label>
            <label class="cb-item" data-val="E"><input type="checkbox" value="E"><i class="bi bi-check2 cb-icon"></i> 경력</label>
            <label class="cb-item" data-val="Z"><input type="checkbox" value="Z"><i class="bi bi-check2 cb-icon"></i> 관계없음</label>
        </div>

        <hr class="sp-divider">

        <!-- 5. 근무형태 -->
        <div class="sp-section-title"><i class="bi bi-sliders"></i> 상세 조건</div>
        <div class="row g-3 mb-3">
            <div class="col-12 col-md-4">
                <label class="form-label-sm"><i class="bi bi-calendar-week"></i> 근무 형태</label>
                <select id="holidayTp" class="form-select">
                    <option value="">전체</option>
                    <option value="1">주 5일 근무</option>
                    <option value="2">주 6일 근무</option>
                    <option value="3">토요 격주 휴무</option>
                    <option value="9">기타</option>
                </select>
            </div>
        </div>

        <hr class="sp-divider">

        <!-- 7. 정렬 / 건수 -->
        <div class="row g-3 mb-3">
            <div class="col-12 col-md-3">
                <label class="form-label-sm"><i class="bi bi-calendar-event"></i> 등록일</label>
                <select id="regDate" class="form-select">
                    <option value="">전체</option>
                    <option value="D-0">오늘</option>
                    <option value="W-1">1주 이내</option>
                    <option value="W-2">2주 이내</option>
                    <option value="M-1">한달 이내</option>
                </select>
            </div>
            <div class="col-12 col-md-3">
                <label class="form-label-sm"><i class="bi bi-sort-down"></i> 정렬 방식</label>
                <select id="sortOrderBy" class="form-select">
                    <option value="DESC">최신 등록순</option>
                    <option value="ASC">오래된 등록순</option>
                </select>
            </div>
            <div class="col-12 col-md-2">
                <label class="form-label-sm"><i class="bi bi-list-ol"></i> 페이지당 건수</label>
                <select id="display" class="form-select">
                    <option value="10">10건</option>
                    <option value="20">20건</option>
                    <option value="30">30건</option>
                    <option value="50">50건</option>
                    <option value="100">100건</option>
                </select>
            </div>
        </div>

        </div><!-- /sp-advanced-filters -->

        <hr class="sp-divider">

        <!-- 8. 키워드 (맨 마지막) -->
        <div class="sp-section-title"><i class="bi bi-search"></i> 키워드 검색</div>
        <div class="row g-3 mb-3">
            <div class="col-12">
                <label class="form-label-sm"><i class="bi bi-keyboard"></i> 키워드 <span class="text-muted fw-normal">(직종명, 회사명, 업무 내용 등 / 쉼표로 다중 입력 가능)</span></label>
                <div class="kw-wrap">
                    <i class="bi bi-search"></i>
                    <input type="text" id="keyword" class="form-control"
                           placeholder="예: 전략영업, 개발자, ERP, 사무직" maxlength="200">
                </div>
            </div>
        </div>

        <!-- 9. 버튼 -->
        <div class="d-flex align-items-center gap-2 justify-content-end">
            <button type="button" class="btn-reset" id="resetBtn"><i class="bi bi-arrow-clockwise"></i> 초기화</button>
            <button type="button" class="btn-search" id="searchBtn"><i class="bi bi-search"></i> 채용공고 검색</button>
        </div>

    </section>

    <!-- ======================== 결과 영역 ======================== -->
    <section class="results-section">
        <div class="results-header mb-3" id="resultsHeader" style="display:none !important;">
            <p class="results-count mb-0">총 <strong id="totalCount">0</strong>건의 채용공고</p>
        </div>
        <div class="loading-wrap" id="loadingWrap">
            <div class="spinner-ring"></div>
            <p>채용공고를 불러오는 중입니다...</p>
        </div>
        <div class="empty-state hidden" id="emptyState">
            <i class="bi bi-exclamation-circle"></i>
            <p>채용공고를 불러오는 중 문제가 발생했습니다.</p>
        </div>
        <div class="job-list-wrap" id="jobCardsGrid"></div>
        <nav class="mt-4" id="paginationWrap" style="display:none;">
            <div class="ri-pagination" id="paginationBtns"></div>
        </nav>
    </section>

</main>

<!-- ======================== 근무지역 모달 ======================== -->
<div class="modal fade ri-modal" id="regionModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="bi bi-geo-alt-fill text-success"></i> 근무 지역 선택 <span class="text-muted fw-normal fs-6 ms-1">(최대 5개)</span></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="modal-search-bar">
                    <input type="text" id="regionSearchInput" class="form-control form-control-sm" placeholder="지역명 검색 (예: 강남, 수원)">
                </div>
                <div class="modal-two-col" id="regionTwoCol">
                    <div class="modal-left-panel" id="regionSidoList"></div>
                    <div class="modal-right-panel" id="regionDetailList"></div>
                </div>
                <div class="modal-selected-area">
                    <div class="modal-selected-label">선택된 지역 · <span class="modal-limit-text"><span id="regionSelectedCount">0</span> / 5개</span></div>
                    <div class="selected-chips" id="regionModalChips"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary btn-sm" id="regionModalClear">선택 초기화</button>
                <button type="button" class="btn btn-success btn-sm" data-bs-dismiss="modal" id="regionModalConfirm">확인</button>
            </div>
        </div>
    </div>
</div>

<!-- ======================== 희망직종 모달 ======================== -->
<div class="modal fade ri-modal" id="occupationModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="bi bi-briefcase-fill text-primary"></i> 희망 직종 선택 <span class="text-muted fw-normal fs-6 ms-1">(최대 10개)</span></h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="modal-search-bar">
                    <input type="text" id="occSearchInput" class="form-control form-control-sm" placeholder="직종명 검색 (예: 소프트웨어, 간호사)">
                </div>
                <div class="modal-two-col" id="occTwoCol">
                    <div class="modal-left-panel" id="occCategoryList"></div>
                    <div class="modal-right-panel" id="occDetailList"></div>
                </div>
                <div class="modal-selected-area">
                    <div class="modal-selected-label">선택된 직종 · <span class="modal-limit-text"><span id="occSelectedCount">0</span> / 10개</span></div>
                    <div class="selected-chips" id="occModalChips"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary btn-sm" id="occModalClear">선택 초기화</button>
                <button type="button" class="btn btn-primary btn-sm" data-bs-dismiss="modal" id="occModalConfirm">확인</button>
            </div>
        </div>
    </div>
</div>

<div class="toast-wrap" id="toastWrap"></div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="/js/recruitmentInformationJS/index_0.0.1.js"></script>

<%-- 서버에서 pre-fetch한 초기 채용공고 데이터 (JSON) --%>
<script id="initialJobData" type="application/json">${initialResultJson}</script>

</body>
</html>