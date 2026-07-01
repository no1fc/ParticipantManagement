<%--
  HR 인원현황 대시보드 (읽기전용)
  Description: 입퇴사자관리(HR) 인원 현황 집계 대시보드
  DB: J_직원, J_직원_재직기간, J_직원_부서배치, J_직원_발령이력, J_부서 (읽기전용)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 HR - 인원현황 대시보드</title>
    <mytag:Logo/>

    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="author" content="JobMoa" />

    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.0.12/index.css"
          integrity="sha256-tXJfXfp6Ewt1ilPzLDtQnJV4hclT9XuaZUKyUvmyr+Q=" crossorigin="anonymous" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
          integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI=" crossorigin="anonymous" />
    <link rel="stylesheet" href="/css/adminlte.min.css" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script defer src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/apexcharts@3.45.1/dist/apexcharts.min.js"></script>

    <!-- Admin Common Design System (재사용) -->
    <link rel="stylesheet" href="/css/adminCss/adminCommon_0.0.2.css">
    <!-- Page-specific CSS -->
    <link rel="stylesheet" href="/css/hrCss/hrDashboard_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:hrGnb active="dashboard" sub_header="인원현황 대시보드"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <!-- 페이지 헤더 -->
                <div class="admin-page-header">
                    <div class="admin-page-title">
                        <h4><i class="bi bi-speedometer2"></i> 인원현황 대시보드 <span class="hr-demo-badge">데모</span></h4>
                        <p>입퇴사자관리(HR) 인원 현황을 집계합니다. (읽기전용)</p>
                    </div>
                </div>

                <!-- 요약 카드 -->
                <div class="hr-summary-grid">
                    <div class="hr-summary-card hr-sc-active">
                        <div class="hr-sc-label"><i class="bi bi-person-check"></i> 재직</div>
                        <div class="hr-sc-value" id="scActive">-</div>
                    </div>
                    <div class="hr-summary-card hr-sc-leave">
                        <div class="hr-sc-label"><i class="bi bi-pause-circle"></i> 휴직</div>
                        <div class="hr-sc-value" id="scOnLeave">-</div>
                    </div>
                    <div class="hr-summary-card hr-sc-resigned">
                        <div class="hr-sc-label"><i class="bi bi-person-dash"></i> 퇴사</div>
                        <div class="hr-sc-value" id="scResigned">-</div>
                    </div>
                    <div class="hr-summary-card hr-sc-hire">
                        <div class="hr-sc-label"><i class="bi bi-box-arrow-in-right"></i> 당월 입사</div>
                        <div class="hr-sc-value" id="scMonthlyHire">-</div>
                    </div>
                    <div class="hr-summary-card hr-sc-resign">
                        <div class="hr-sc-label"><i class="bi bi-box-arrow-right"></i> 당월 퇴사</div>
                        <div class="hr-sc-value" id="scMonthlyResign">-</div>
                    </div>
                </div>

                <!-- 차트 2단 -->
                <div class="row g-3 mt-1">
                    <div class="col-lg-7">
                        <div class="admin-table-card h-100">
                            <div class="hr-card-title"><i class="bi bi-bar-chart-line"></i> 부서별 인원</div>
                            <div id="deptChart"></div>
                        </div>
                    </div>
                    <div class="col-lg-5">
                        <div class="admin-table-card h-100">
                            <div class="hr-card-title"><i class="bi bi-pie-chart"></i> 근속 분포</div>
                            <div id="tenureChart"></div>
                        </div>
                    </div>
                </div>

                <!-- 최근 발령/입퇴사 타임라인 -->
                <div class="admin-table-card mt-3">
                    <div class="hr-card-title"><i class="bi bi-clock-history"></i> 최근 발령 이력 (최근 10건)</div>
                    <div class="table-responsive">
                        <table class="table admin-data-table mb-0">
                            <thead>
                            <tr>
                                <th style="width:130px;">발령일</th>
                                <th style="width:120px;">직원</th>
                                <th style="width:120px;">발령유형</th>
                                <th>사유</th>
                            </tr>
                            </thead>
                            <tbody id="timelineBody">
                            <tr><td colspan="4" class="text-center text-muted">로딩 중...</td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>

            </div>
        </div>
    </main>

    <footer class="app-footer">
        <div class="float-end d-none d-sm-inline">JobMoa HR (데모)</div>
        <strong>Copyright &copy; 2024-2026 JobMoa.</strong> All rights reserved.
    </footer>
</div>

<script defer src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
<script defer src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
        integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+" crossorigin="anonymous"></script>
<script defer src="/js/adminlte.js"></script>

<script defer src="/js/hrJs/hrDashboard_0.0.1.js"></script>

</body>
</html>
