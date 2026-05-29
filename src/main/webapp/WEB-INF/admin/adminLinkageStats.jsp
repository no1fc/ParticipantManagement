<%--
  연계 현황 페이지 (Admin Linkage Statistics)
  Description: 지점별/상담사별 연계 현황 막대 그래프 + 연계유형별 도넛 차트
  DB: J_참여자관리, J_참여자관리_로그인정보
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 연계 현황</title>
    <mytag:Logo/>

    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>

    <!-- Fonts -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.0.12/index.css"
          integrity="sha256-tXJfXfp6Ewt1ilPzLDtQnJV4hclT9XuaZUKyUvmyr+Q=" crossorigin="anonymous" />

    <!-- OverlayScrollbars -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/styles/overlayscrollbars.min.css"
          integrity="sha256-tZHrRjVqNSRyWg2wbppGnT833E/Ys0DHWGwT04GiqQg=" crossorigin="anonymous" />

    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
          integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI=" crossorigin="anonymous" />

    <!-- AdminLTE -->
    <link rel="stylesheet" href="/css/adminlte.min.css" />

    <!-- ApexCharts -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/apexcharts@3.37.1/dist/apexcharts.css"
          integrity="sha256-4MX+61mt9NVvvuPjUWdUdyfZfxSB1/Rf9WtqRHgG5S0=" crossorigin="anonymous" />
    <script defer src="https://cdn.jsdelivr.net/npm/apexcharts@3.37.1/dist/apexcharts.min.js"
            integrity="sha256-+vh8GkaU7C9/wbSLIcwq82tQ2wTf44aOHA8HlBMwRI8=" crossorigin="anonymous"></script>

    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">

    <!-- SweetAlert2 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script defer src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
    <script defer src="/js/sweetAlert_0.0.1.js"></script>

    <!-- Modern Design System -->
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">

    <!-- 페이지 전용 CSS -->
    <link rel="stylesheet" href="/css/adminCss/adminLinkageStats_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:gnb gnb_main_header="지점관리" gnb_sub_header="연계 현황"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <!-- 페이지 헤더 -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-body">
                                <h3 class="fw-bold text-brand mb-2">
                                    <i class="bi bi-diagram-3"></i> 연계 현황
                                </h3>
                                <p class="text-muted mb-0">지점별/상담사별 연계 현황을 확인할 수 있습니다. 막대 그래프를 클릭하면 상담사별 상세 현황을 볼 수 있습니다.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 검색 패널 -->
                <div class="search-panel">
                    <h5 class="mb-3"><i class="bi bi-search"></i> 검색 필터</h5>
                    <div class="row g-3">
                        <div class="col-md-3">
                            <label class="form-label">지점</label>
                            <select class="form-control form-control-modern" id="searchBranch">
                                <option value="">전체</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">시작일</label>
                            <input type="date" class="form-control form-control-modern" id="searchStartDate">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">종료일</label>
                            <input type="date" class="form-control form-control-modern" id="searchEndDate">
                        </div>
                        <div class="col-md-3 d-flex align-items-end">
                            <button class="btn btn-light w-100 me-2" onclick="searchLinkageStats()">
                                <i class="bi bi-search"></i> 검색
                            </button>
                            <button class="btn btn-outline-secondary w-100" onclick="resetSearch()">
                                <i class="bi bi-arrow-counterclockwise"></i> 초기화
                            </button>
                        </div>
                    </div>
                </div>

                <!-- KPI 카드 -->
                <div class="row g-3 mb-4 align-items-stretch">
                    <div class="col-md-4 d-flex">
                        <div class="kpi-card position-relative w-100">
                            <i class="bi bi-link-45deg kpi-icon"></i>
                            <div class="kpi-label">전체 연계 건수</div>
                            <div class="kpi-value" id="totalLinkageCount">-</div>
                        </div>
                    </div>
                    <div class="col-md-4 d-flex">
                        <div class="kpi-card success position-relative w-100">
                            <i class="bi bi-award kpi-icon"></i>
                            <div class="kpi-label">최다 연계유형</div>
                            <div class="kpi-value kpi-value-text" id="topLinkageType">-</div>
                        </div>
                    </div>
                    <div class="col-md-4 d-flex">
                        <div class="kpi-card emerald position-relative w-100">
                            <i class="bi bi-building kpi-icon"></i>
                            <div class="kpi-label">참여 지점 수</div>
                            <div class="kpi-value" id="branchCount">-</div>
                        </div>
                    </div>
                </div>

                <!-- 드릴다운 표시 바 -->
                <div class="drill-down-bar" id="drillDownBar" style="display: none;">
                    <span>
                        <i class="bi bi-geo-alt-fill"></i>
                        <span class="branch-name" id="drillDownBranch"></span> 상담사별 현황
                    </span>
                    <button class="btn btn-sm btn-outline-primary" onclick="backToBranchView()">
                        <i class="bi bi-arrow-left"></i> 지점 전체보기
                    </button>
                </div>

                <!-- 차트 영역 -->
                <div class="row g-4 mb-4 align-items-stretch">
                    <div class="col-lg-8 d-flex">
                        <div class="card-modern border-0 shadow-sm w-100">
                            <div class="card-body chart-container">
                                <div id="linkageMainChart"></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-4 d-flex">
                        <div class="card-modern border-0 shadow-sm w-100 d-flex flex-column">
                            <div class="card-body d-flex flex-column justify-content-center">
                                <div id="linkageTypeChart"></div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 데이터 테이블 -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-header bg-transparent">
                                <h5 class="card-title fw-bold mb-0">
                                    <i class="bi bi-table"></i> 상세 데이터
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-hover linkage-table">
                                        <thead id="linkageTableHead">
                                            <tr>
                                                <th>순위</th>
                                                <th>지점</th>
                                                <th>연계 건수</th>
                                            </tr>
                                        </thead>
                                        <tbody id="linkageTableBody">
                                            <tr><td colspan="3" class="text-center text-muted py-3">로딩 중...</td></tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </main>

    <mytag:footer/>
</div>

<!-- OverlayScrollbars -->
<script defer src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es6.min.js"
        integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ=" crossorigin="anonymous"></script>

<!-- Popper.js -->
<script defer src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>

<!-- Bootstrap 5 -->
<script defer src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.min.js"
        integrity="sha384-0pUGZvbkm6XF6gxjEnlmuGrJXVbNuzT9qBBavbLwCsOGabYfZo0T0to5eqruptLy" crossorigin="anonymous"></script>

<!-- AdminLTE -->
<script defer src="/js/adminlte.js"></script>

<!-- OverlayScrollbars Configure -->
<script>
    const SELECTOR_SIDEBAR_WRAPPER = '.sidebar-wrapper';
    const Default = {
        scrollbarTheme: 'os-theme-light',
        scrollbarAutoHide: 'leave',
        scrollbarClickScroll: true,
    };
    document.addEventListener('DOMContentLoaded', function () {
        const sidebarWrapper = document.querySelector(SELECTOR_SIDEBAR_WRAPPER);
        if (sidebarWrapper && typeof OverlayScrollbarsGlobal?.OverlayScrollbars !== 'undefined') {
            OverlayScrollbarsGlobal.OverlayScrollbars(sidebarWrapper, {
                scrollbars: {
                    theme: Default.scrollbarTheme,
                    autoHide: Default.scrollbarAutoHide,
                    clickScroll: Default.scrollbarClickScroll,
                },
            });
        }
    });
</script>

<!-- 페이지 전용 JS -->
<script defer src="/js/adminJs/adminLinkageStats_0.0.1.js"></script>

</body>
</html>
