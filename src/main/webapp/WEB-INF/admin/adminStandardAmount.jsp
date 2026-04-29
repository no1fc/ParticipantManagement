<%--
  기준금액 관리 페이지 (Standard Amount Management)
  Created by: JobMoa Admin System
  Date: 2026-03-17
  Description: 임금 기준금액 및 성공금 관리
  DB: J_참여자관리_기준금액, J_참여자관리_나은기준임금
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 기준금액 관리</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <mytag:Logo/>

    <!--begin::Primary Meta Tags-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="title" content="AdminLTE v4 | Standard Amount Management" />
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

    <!-- Font Awesome -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <!-- DataTables -->
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap5.min.css">
    <script src="https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.4/js/dataTables.bootstrap5.min.js"></script>

    <!-- SweetAlert2 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
    <script src="/js/sweetAlert_0.0.1.js"></script>

    <!-- Modern Design System -->
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">

    <link rel="stylesheet" href="/css/adminCss/adminStandardAmount_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin::App Gnb-->
    <mytag:gnb gnb_main_header="관리자" gnb_sub_header="기준금액 관리"/>
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
                                    <i class="bi bi-cash-stack"></i> 기준금액 관리
                                </h3>
                                <p class="text-muted mb-0">임금 기준금액, 성공금, 나은기준임금을 관리합니다.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 임금 기준금액 섹션 -->
                <div class="section-card">
                    <div class="section-header">
                        <h5 class="mb-0"><i class="bi bi-currency-exchange"></i> 임금 기준금액 및 성공금</h5>
                    </div>

                    <div class="row mb-3">
                        <div class="col-12 text-end">
                            <button class="btn btn-primary" onclick="openStandardAmountModal()">
                                <i class="bi bi-plus-circle"></i> 기준금액 추가
                            </button>
                        </div>
                    </div>

                    <div class="table-modern">
                        <table id="standardAmountTable" class="table table-hover mb-0">
                            <thead>
                            <tr>
                                <th>PK</th>
                                <th>구분</th>
                                <th>년도</th>
                                <th>임금 최소</th>
                                <th>임금 최대</th>
                                <th>성공금</th>
                                <th>액션</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 나은기준임금 섹션 -->
                <div class="section-card">
                    <div class="section-header">
                        <h5 class="mb-0"><i class="bi bi-graph-up"></i> 나은기준임금</h5>
                    </div>

                    <div class="row mb-3">
                        <div class="col-12 text-end">
                            <button class="btn btn-primary" onclick="openBetterWageModal()">
                                <i class="bi bi-plus-circle"></i> 나은기준임금 추가
                            </button>
                        </div>
                    </div>

                    <div class="table-modern">
                        <table id="betterWageTable" class="table table-hover mb-0">
                            <thead>
                            <tr>
                                <th>PK</th>
                                <th>년도</th>
                                <th>년도 DATE</th>
                                <th>나은임금</th>
                                <th>등록일</th>
                                <th>액션</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
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

<!-- 기준금액 추가/수정 모달 -->
<div class="modal fade" id="standardAmountModal" tabindex="-1" aria-labelledby="standardAmountModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="standardAmountModalLabel">기준금액 정보</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="standardAmountForm">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">구분 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="division" name="구분" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">년도 <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" id="year" name="년도" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">임금 최소 <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="minWage" name="임금_최소" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">임금 최대</label>
                            <input type="number" class="form-control" id="maxWage" name="임금_최대">
                        </div>
                        <div class="col-md-12">
                            <label class="form-label">성공금 <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="successFee" name="성공금" required>
                        </div>
                        <input type="hidden" id="standardAmountPK" name="PK">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="saveStandardAmount()">저장</button>
            </div>
        </div>
    </div>
</div>

<!-- 나은기준임금 추가/수정 모달 -->
<div class="modal fade" id="betterWageModal" tabindex="-1" aria-labelledby="betterWageModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="betterWageModalLabel">나은기준임금 정보</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="betterWageForm">
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label">년도 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="betterYear" name="년도" required placeholder="예: 2024년">
                        </div>
                        <div class="col-12">
                            <label class="form-label">년도 DATE <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" id="betterYearDate" name="년도DATE" required>
                        </div>
                        <div class="col-12">
                            <label class="form-label">나은임금 <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="betterWage" name="나은임금" required>
                        </div>
                        <input type="hidden" id="betterWagePK" name="PK">
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="saveBetterWage()">저장</button>
            </div>
        </div>
    </div>
</div>

<!--begin::Script-->
<script src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es5.min.js"
        integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ=" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
        integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+" crossorigin="anonymous"></script>
<script src="/js/adminlte.js"></script>

<script src="/js/adminJs/adminStandardAmount_0.0.1.js"></script>
<!--end::Script-->

</body>
</html>
