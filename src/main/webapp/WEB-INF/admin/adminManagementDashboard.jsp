<%--
  관리자 대시보드 - 지점별 당해년도 운영 현황
  Created by: JobMoa Admin System
  Date: 2026-06-02
  Description: 14개 지점의 배정인원, 자체모집, 참여자수, 취소인원, 상담사수, 상담사 1명당 초기상담 인원 현황
  DB: J_참여자관리, J_참여자관리_로그인정보, J_참여자관리_지점, J_참여자관리_취소자백업
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 관리자 대시보드</title>
    <mytag:Logo/>

    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="author" content="JobMoa" />

    <!-- jQuery JS -->
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

    <!-- 관리자 대시보드 전용 CSS -->
    <link rel="stylesheet" href="/css/adminCss/adminManagementDashboard_0.0.2.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin::App Gnb-->
    <mytag:gnb gnb_main_header="관리자" gnb_sub_header="관리자 대시보드"/>
    <!--end::App Gnb-->

    <!--begin::App Main-->
    <main class="app-main">
        <!--begin::App Content-->
        <div class="app-content">
            <!--begin::Container-->
            <div class="container-fluid">

                <!-- 페이지 헤더 -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-body d-flex justify-content-between align-items-center flex-wrap gap-2">
                                <div>
                                    <h3 class="fw-bold text-brand mb-1">
                                        <i class="bi bi-building"></i> 지점별 당해년도 운영 현황
                                    </h3>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 필터 영역 -->
                <div class="search-panel mb-4">
                    <div class="row g-3 align-items-end">
                        <div class="col-auto">
                            <label class="form-label fw-semibold">기준 연도</label>
                            <select class="form-control form-control-modern" id="yearFilter" style="width: 140px;">
                                <!-- JS에서 동적 생성 -->
                            </select>
                        </div>
                        <div class="col">
                            <label class="form-label fw-semibold">지점 필터 <small class="text-muted">(여러 지점을 클릭하여 비교)</small></label>
                            <div id="branchFilterBtns" class="d-flex flex-wrap gap-2">
                                <!-- JS에서 동적 생성 -->
                            </div>
                        </div>
                        <div class="col-auto d-flex gap-2">
                            <button class="btn btn-light" onclick="loadDashboardData()" title="데이터 새로고침">
                                <i class="bi bi-arrow-clockwise"></i> 새로고침
                            </button>
                            <button class="btn btn-success" onclick="downloadExcel()">
                                <i class="bi bi-download"></i> 엑셀 다운로드
                            </button>
                        </div>
                    </div>
                </div>

                <!-- KPI 카드 섹션 -->
                <div class="row g-3 mb-4">
                    <div class="col-md col-6">
                        <div class="mgmt-kpi-card">
                            <div class="mgmt-kpi-label">총 배정 인원</div>
                            <div class="mgmt-kpi-value" id="kpiAllocated">-</div>
                            <div class="mgmt-kpi-sub">당해년도 누적 등록</div>
                        </div>
                    </div>
                    <div class="col-md col-6">
                        <div class="mgmt-kpi-card accent-indigo">
                            <div class="mgmt-kpi-label">자체 모집 비율</div>
                            <div class="mgmt-kpi-value" id="kpiSelfRatio">-</div>
                            <div class="mgmt-kpi-sub">자체모집 / 총 배정</div>
                        </div>
                    </div>
                    <div class="col-md col-6">
                        <div class="mgmt-kpi-card accent-emerald">
                            <div class="mgmt-kpi-label">초기상담 진행률</div>
                            <div class="mgmt-kpi-value" id="kpiConsultRatio">-</div>
                            <div class="mgmt-kpi-sub">참여자수 / 총 배정</div>
                        </div>
                    </div>
                    <div class="col-md col-6">
                        <div class="mgmt-kpi-card accent-rose">
                            <div class="mgmt-kpi-label">평균 취소율</div>
                            <div class="mgmt-kpi-value" id="kpiCancelRatio">-</div>
                            <div class="mgmt-kpi-sub">취소인원 / 참여자수</div>
                        </div>
                    </div>
                    <div class="col-md col-12">
                        <div class="mgmt-kpi-card accent-amber">
                            <div class="mgmt-kpi-label">상담사 1명당 초기상담</div>
                            <div class="mgmt-kpi-value" id="kpiCounselorLoad">-</div>
                            <div class="mgmt-kpi-sub">업무 과부하 판정 지표</div>
                        </div>
                    </div>
                </div>

                <!-- 차트 1: 지점별 인원 현황 (그룹형 막대 + 꺾은선) -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-header bg-transparent">
                                <h5 class="card-title fw-bold mb-0">
                                    <i class="bi bi-bar-chart-line text-brand"></i> 지점별 인원 현황
                                </h5>
                            </div>
                            <div class="card-body">
                                <div id="mgmtChart1"></div>
                                <div class="mgmt-chart-notice mt-3">
                                    <i class="bi bi-info-circle text-primary me-2"></i>
                                    <div>
                                        <strong>인원 현황 차트 안내</strong>
                                        <p class="mb-0 mt-1 small">
                                            참여자수(파랑) / 자체모집(초록) / 취소인원(빨강)이 누적 적층되며,
                                            막대 상단에 합계가 표시됩니다. 세그먼트가 작은 경우 수치는 생략됩니다.
                                            우측 보조축으로 상담사 1명당 초기상담 인원(황색선) 트렌드가 표시됩니다.
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 차트 2: 전년 대비 배정인원 비교 -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-header bg-transparent">
                                <h5 class="card-title fw-bold mb-0">
                                    <i class="bi bi-graph-up text-brand"></i> 전년 대비 배정인원 비교
                                </h5>
                            </div>
                            <div class="card-body">
                                <div id="mgmtChart2"></div>
                                <div class="mgmt-chart-notice mt-3">
                                    <i class="bi bi-info-circle text-primary me-2"></i>
                                    <div>
                                        <strong>전년 대비 비교 안내</strong>
                                        <p class="mb-0 mt-1 small">
                                            당해년도 배정인원(실선)과 전년도 배정인원(점선)의 차이를 지점별로 비교하여
                                            연간 배정 규모 및 흐름 추이를 추적할 수 있습니다.
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 데이터 테이블 -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-header bg-transparent d-flex justify-content-between align-items-center">
                                <h5 class="card-title fw-bold mb-0">
                                    <span class="mgmt-title-bar"></span>
                                    <i class="bi bi-table text-brand"></i> 지점별 당해년도 운영 현황
                                </h5>
                                <small class="text-muted">* 컬럼 헤더를 클릭하면 정렬됩니다.</small>
                            </div>
                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table table-hover mb-0" id="mgmtTable">
                                        <thead class="table-light">
                                        <tr>
                                            <th class="mgmt-th-sortable" onclick="sortTable(0)">
                                                지점명 <i class="bi bi-chevron-expand ms-1"></i>
                                            </th>
                                            <th class="mgmt-th-sortable text-end" onclick="sortTable(1)">
                                                배정인원 <i class="bi bi-chevron-expand ms-1"></i>
                                            </th>
                                            <th class="mgmt-th-sortable text-end" onclick="sortTable(2)">
                                                자체모집인원수 <i class="bi bi-chevron-expand ms-1"></i>
                                            </th>
                                            <th class="mgmt-th-sortable text-end" onclick="sortTable(3)">
                                                참여자수 <i class="bi bi-chevron-expand ms-1"></i>
                                            </th>
                                            <th class="mgmt-th-sortable text-end" onclick="sortTable(4)">
                                                취소인원 <i class="bi bi-chevron-expand ms-1"></i>
                                            </th>
                                            <th class="mgmt-th-sortable text-end" onclick="sortTable(5)">
                                                상담사수 <i class="bi bi-chevron-expand ms-1"></i>
                                            </th>
                                            <th class="mgmt-th-sortable text-end mgmt-th-highlight" onclick="sortTable(6)">
                                                상담사 1명당 초기상담 인원 <i class="bi bi-chevron-expand ms-1"></i>
                                            </th>
                                        </tr>
                                        </thead>
                                        <tbody id="mgmtTableBody">
                                        </tbody>
                                        <tfoot>
                                        <tr class="mgmt-table-footer" id="mgmtTableFooter">
                                        </tr>
                                        </tfoot>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
            <!--end::Container-->
        </div>
        <!--end::App Content-->
    </main>
    <!--end::App Main-->

    <!--begin::Footer-->
    <footer class="app-footer">
        <div class="float-end d-none d-sm-inline">JobMoa Admin System v1.0</div>
        <strong>Copyright &copy; 2024-2026 JobMoa.</strong> All rights reserved.
    </footer>
    <!--end::Footer-->
</div>
<!--end::App Wrapper-->

<!--begin::Script-->
<script defer src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es5.min.js"
        integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ=" crossorigin="anonymous"></script>
<script defer src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
<script defer src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
        integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+" crossorigin="anonymous"></script>
<script defer src="/js/adminlte.js"></script>

<!-- 관리자 대시보드 전용 JS -->
<script defer src="/js/adminJs/adminManagementDashboard_0.0.3.js"></script>
<!--end::Script-->

</body>
</html>
