<%--
  연계 실적 대시보드 (Linkage Performance Dashboard)
  Description: 실적기간(전년 11/1 ~ 당해 10/31) 중 연계일이 있는 참여자를 기준으로
               전체/지점별/상담사별 연계 참여자 수(중복제거)와 연계 건수(이벤트 수)를
               전체·종료자 두 축으로 표시한다.
  DB: J_참여자관리_연계, J_참여자관리, J_참여자관리_로그인정보, J_참여자관리_지점
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 연계 실적 대시보드</title>
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

    <!-- Modern Design System -->
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">

    <!-- 페이지 전용 CSS -->
    <link rel="stylesheet" href="/css/participantCss/dashboard_linkage_0.0.2.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:gnb gnb_main_header="실적관리" gnb_sub_header="연계 실적 대시보드"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <!-- 페이지 헤더 -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-body d-flex justify-content-between align-items-center flex-wrap gap-2">
                                <div>
                                    <h3 class="fw-bold text-brand mb-2">
                                        <i class="bi bi-link-45deg"></i> 연계 실적 대시보드
                                    </h3>
                                    <p class="text-muted mb-0">
                                        실적기간 중 연계일이 있는 참여자를 전체/종료자 기준으로 집계합니다. 참여자 수(중복제거)와 연계 건수를 함께 확인할 수 있습니다.
                                    </p>
                                </div>
                                <div class="d-flex align-items-center gap-2 flex-wrap">
                                    <span class="badge bg-light text-dark border fs-6 period-badge">
                                        실적기간 ${linkageStartDate} ~ ${linkageEndDate}
                                    </span>
                                    <button type="button" id="linkageExcelBtn" class="btn btn-success btn-sm">
                                        <i class="bi bi-file-earmark-excel-fill"></i> 엑셀 다운로드
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- KPI 카드 2종 (연계 건수 2기준) -->
                <div class="row g-3 mb-4 align-items-stretch">
                    <div class="col-md-6 d-flex">
                        <div class="kpi-card w-100">
                            <i class="bi bi-diagram-3 kpi-icon"></i>
                            <div class="kpi-label">연계 건수 (실적 기간 전체)</div>
                            <div class="kpi-value" id="kpiFullPeriod">-</div>
                            <div class="kpi-sub">연계일이 실적기간 내인 연계 건수</div>
                        </div>
                    </div>
                    <div class="col-md-6 d-flex">
                        <div class="kpi-card success w-100">
                            <i class="bi bi-person-check-fill kpi-icon"></i>
                            <div class="kpi-label">연계 건수 (종료일 기준)</div>
                            <div class="kpi-value" id="kpiTerminated">-</div>
                            <div class="kpi-sub">실제종료일이 실적기간 내인 참여자의 연계 건수</div>
                        </div>
                    </div>
                </div>

                <!-- 차트 영역 (일반 지점 / 컨소시엄 지점 좌우 분리) — 실적 인정/미인정 2종 스택형 -->
                <div class="row g-3 mb-4">
                    <div class="col-lg-6 d-flex">
                        <div class="card-modern border-0 shadow-sm w-100">
                            <div class="card-header bg-transparent">
                                <h5 class="card-title fw-bold mb-0">
                                    <i class="bi bi-bar-chart-line"></i> 현재 연계 현황
                                </h5>
                                <br>
                                <small class="text-muted">연계 건수(실적 확정): 10월 31일 이내 기간만료 예정자를 포함하여, 취업·중단·기간만료 등으로 종료가 확정된 참여자</small>
                                <br>
                                <small class="text-muted">연계 건수(종료 미확정): 10월 31일 이내 종료 여부가 아직 확정되지 않은 참여자</small>
                            </div>
                            <div class="card-body">
                                <div class="text-muted small fw-semibold mb-1">연계 건수(실적 확정)</div>
                                <div id="linkageBranchChartTerm"></div>
                                <div class="text-muted small fw-semibold mt-3 mb-1">연계 건수(종료일 미확정)</div>
                                <div id="linkageBranchChartFull"></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6 d-flex">
                        <div class="card-modern border-0 shadow-sm w-100">
                            <div class="card-header bg-transparent">
                                <h5 class="card-title fw-bold mb-0">
                                    <i class="bi bi-diagram-2"></i> 컨소시엄 지점 분리 현황
                                </h5>
                                <br>
                                <small class="text-muted">컨소시엄 지점(의정부·북부·광명·성남·인천서부)를 별도 표시</small>
                            </div>
                            <div class="card-body">
                                <div class="text-muted small fw-semibold mb-1">연계 건수(실적 확정)</div>
                                <div id="linkageConsortiumChartTerm"></div>
                                <div class="text-muted small fw-semibold mt-3 mb-1">연계 건수(종료일 미확정)</div>
                                <div id="linkageConsortiumChartFull"></div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 지점별 상세 테이블 -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-header bg-transparent">
                                <h5 class="card-title fw-bold mb-0"><i class="bi bi-table"></i> 지점별 상세</h5>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-hover linkage-table align-middle">
                                        <thead>
                                            <tr>
                                                <th>순위</th>
                                                <th>지점</th>
                                                <th>연계 건수<br>(실적 확정)</th>
                                                <th>연계 건수<br>(종료 미확정자 포함)</th>
                                            </tr>
                                        </thead>
                                        <tbody id="branchTableBody">
                                            <tr><td colspan="4" class="text-center text-muted py-3">로딩 중...</td></tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 상담사별 상세 테이블 -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-header bg-transparent d-flex justify-content-between align-items-center flex-wrap gap-2">
                                <h5 class="card-title fw-bold mb-0"><i class="bi bi-person-lines-fill"></i> 상담사별 상세</h5>
                                <div id="counselorBranchFilter" class="btn-group btn-group-sm flex-wrap" role="group" aria-label="지점 필터"></div>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-hover linkage-table align-middle">
                                        <thead>
                                            <tr>
                                                <th>순위</th>
                                                <th>지점</th>
                                                <th>상담사</th>
                                                <th>연계 건수<br>(실적 확정)</th>
                                                <th>연계 건수<br>(종료 미확정자 포함)</th>
                                            </tr>
                                        </thead>
                                        <tbody id="counselorTableBody">
                                            <tr><td colspan="5" class="text-center text-muted py-3">로딩 중...</td></tr>
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

<!-- 서버 주입 데이터 -->
<script>
    const LINKAGE_TOTALS = ${empty linkageTotals ? '{}' : linkageTotals};
    const LINKAGE_BY_BRANCH = ${empty linkageByBranch ? '[]' : linkageByBranch};
    const LINKAGE_BY_COUNSELOR = ${empty linkageByCounselor ? '[]' : linkageByCounselor};
    const LINKAGE_BY_BRANCH_CATEGORY = ${empty linkageByBranchCategory ? '[]' : linkageByBranchCategory};
</script>

<!-- OverlayScrollbars Configure -->
<script>
    const SELECTOR_SIDEBAR_WRAPPER = '.sidebar-wrapper';
    const OS_DEFAULT = {
        scrollbarTheme: 'os-theme-light',
        scrollbarAutoHide: 'leave',
        scrollbarClickScroll: true,
    };
    document.addEventListener('DOMContentLoaded', function () {
        const sidebarWrapper = document.querySelector(SELECTOR_SIDEBAR_WRAPPER);
        if (sidebarWrapper && typeof OverlayScrollbarsGlobal?.OverlayScrollbars !== 'undefined') {
            OverlayScrollbarsGlobal.OverlayScrollbars(sidebarWrapper, {
                scrollbars: {
                    theme: OS_DEFAULT.scrollbarTheme,
                    autoHide: OS_DEFAULT.scrollbarAutoHide,
                    clickScroll: OS_DEFAULT.scrollbarClickScroll,
                },
            });
        }
    });
</script>

<!-- 페이지 전용 JS -->
<script defer src="/js/dashboard_linkage_visualization_0.0.9.js"></script>

</body>
</html>
