<%--
  통합 관리 대시보드 (Admin Total Dashboard)
  Created by: JobMoa Admin System
  Date: 2026-03-17
  Description: 전체 KPI 요약, 실시간 실적 그래프, 상태별 참여자 분포
  DB: J_참여자관리, J_참여자관리_로그인정보, J_참여자관리_지점
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 통합 관리 대시보드</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <mytag:Logo/>

    <!--begin::Primary Meta Tags-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="title" content="AdminLTE v4 | Admin Dashboard" />
    <meta name="author" content="JobMoa" />
    <!--end::Primary Meta Tags-->

    <!-- jQuery JS -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>

    <!--begin::Fonts-->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.0.12/index.css"
          integrity="sha256-tXJfXfp6Ewt1ilPzLDtQnJV4hclT9XuaZUKyUvmyr+Q=" crossorigin="anonymous" />
    <!--end::Fonts-->

    <!--begin::Third Party Plugin(OverlayScrollbars)-->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/styles/overlayscrollbars.min.css"
          integrity="sha256-tZHrRjVqNSRyWg2wbppGnT833E/Ys0DHWGwT04GiqQg=" crossorigin="anonymous" />
    <!--end::Third Party Plugin(OverlayScrollbars)-->

    <!--begin::Third Party Plugin(Bootstrap Icons)-->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
          integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI=" crossorigin="anonymous" />
    <!--end::Third Party Plugin(Bootstrap Icons)-->

    <!--begin::Required Plugin(AdminLTE)-->
    <link rel="stylesheet" href="/css/adminlte.css" />
    <!--end::Required Plugin(AdminLTE)-->

    <!-- ApexCharts -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/apexcharts@3.37.1/dist/apexcharts.css"
          integrity="sha256-4MX+61mt9NVvvuPjUWdUdyfZfxSB1/Rf9WtqRHgG5S0=" crossorigin="anonymous" />
    <script src="https://cdn.jsdelivr.net/npm/apexcharts@3.37.1/dist/apexcharts.min.js"
            integrity="sha256-+vh8GkaU7C9/wbSLIcwq82tQ2wTf44aOHA8HlBMwRI8=" crossorigin="anonymous"></script>

    <!-- Font Awesome -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <!-- SweetAlert2 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
    <script src="/js/sweetAlert_0.0.1.js"></script>

    <!-- Modern Design System -->
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">

    <!-- 대시보드 전용 CSS -->
    <link rel="stylesheet" href="/css/adminCss/adminTotalDashboard_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin::App Gnb-->
    <mytag:gnb gnb_main_header="관리자" gnb_sub_header="통합 관리 대시보드"/>
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
                            <div class="card-body">
                                <h3 class="fw-bold text-brand mb-2">
                                    <i class="bi bi-speedometer2"></i> 통합 관리 대시보드
                                </h3>
                                <p class="text-muted mb-0">전체 지점의 KPI 및 실시간 현황을 확인할 수 있습니다.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 검색 패널 -->
                <div class="search-panel">
                    <h5 class="mb-3"><i class="bi bi-search"></i> 대시보드 필터</h5>
                    <div class="row g-3">
                        <div class="col-md-3">
                            <label class="form-label">지점</label>
                            <select class="form-control form-control-modern" id="searchBranch">
                                <option value="">전체</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">상담사</label>
                            <select class="form-control form-control-modern" id="searchCounselor">
                                <option value="">전체</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">시작일</label>
                            <input type="date" class="form-control form-control-modern" id="searchStartDate">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">종료일</label>
                            <input type="date" class="form-control form-control-modern" id="searchEndDate">
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <button class="btn btn-light w-100 me-2" onclick="searchDashboard()">
                                <i class="bi bi-search"></i> 검색
                            </button>
                            <button class="btn btn-outline-secondary w-100" onclick="resetSearch()">
                                <i class="bi bi-arrow-counterclockwise"></i> 초기화
                            </button>
                        </div>
                    </div>
                </div>

                <!-- KPI 카드 섹션 -->
                <div class="row g-3 mb-5">
                    <div class="col-md-4">
                        <div class="kpi-card position-relative">
                            <i class="bi bi-database kpi-icon"></i>
                            <div class="kpi-label">전체 데이터 수</div>
                            <div class="kpi-value" id="totalParticipants">-</div>
                            <small>참여자 전체 데이터 수</small>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="kpi-card success position-relative">
                            <i class="bi bi-person-plus kpi-icon"></i>
                            <div class="kpi-label">당월 신규 배정</div>
                            <div class="kpi-value" id="monthlyNewAssignments">-</div>
                            <small>이번 달 등록일 기준</small>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="kpi-card emerald position-relative">
                            <i class="bi bi-person-check kpi-icon"></i>
                            <div class="kpi-label">실적연도 취업자</div>
                            <div class="kpi-value" id="employment">-</div>
                            <small>11월~10월 취업자</small>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="kpi-card orange position-relative">
                            <i class="bi bi-person-dash kpi-icon"></i>
                            <div class="kpi-label">실적연도 종료자</div>
                            <div class="kpi-value" id="completed">-</div>
                            <small>11월~10월 종료자</small>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="kpi-card danger position-relative">
                            <i class="bi bi-percent kpi-icon"></i>
                            <div class="kpi-label">실적연도 취업률</div>
                            <div class="kpi-value" id="employmentRate">-</div>
                            <small>11월~10월 종료자 대비 취업자</small>
                        </div>
                    </div>
                </div>

                <!-- 실시간 실적 그래프 -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-header bg-transparent">
                                <h5 class="card-title fw-bold mb-0">
                                    <i class="bi bi-bar-chart-line text-brand"></i> 지점별 취업 성과 현황
                                </h5>
                            </div>
                            <div class="card-body">
                                <div id="employmentPerformanceChart"></div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 상담사별 실적 통계 테이블 -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-header bg-transparent">
                                <h5 class="card-title fw-bold mb-0">
                                    <i class="bi bi-table text-brand"></i> 상담사별 실적 통계
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="table-modern">
                                    <table id="placementStatsTable" class="table table-hover mb-0">
                                        <thead>
                                        <tr>
                                            <th>지점</th>
                                            <th>상담사ID</th>
                                            <th>상담사명</th>
                                            <th>종료자수</th>
                                            <th>취업자수</th>
                                            <th>알선취업자수</th>
                                            <th>취업률</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 상태별 참여자 분포 -->
                <div class="row mb-4">
                    <!-- 상태별 참여자 분포 -->
                    <div class="card-modern border-0 shadow-sm col-6">
                        <div class="card-header bg-transparent">
                            <h5 class="card-title fw-bold mb-0">
                                <i class="bi bi-pie-chart text-brand"></i> 지점 참여자 분포
                            </h5>
                        </div>
                        <div class="card-body">
                            <div id="participantStatusChart"></div>
                        </div>
                    </div>
                    <!-- 알선 현황 차트 -->
                    <div class="card-modern border-0 shadow-sm col-6">
                        <div class="card-header bg-transparent">
                            <h5 class="card-title fw-bold mb-0">
                                <i class="bi bi-clipboard-data text-brand"></i> 지점별 알선 현황
                            </h5>
                        </div>
                        <div class="card-body">
                            <div id="jobPlacementChart"></div>
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
<script src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es5.min.js"
        integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ=" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
        integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+" crossorigin="anonymous"></script>
<script src="/js/adminlte.js"></script>

<!-- 대시보드 전용 JS -->
<script src="/js/adminJs/adminTotalDashboard_0.0.2.js"></script>
<!--end::Script-->

</body>
</html>
