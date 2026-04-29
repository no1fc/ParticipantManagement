<%--
  일일업무보고 페이지 (Daily Report)
  Created by: JobMoa Admin System
  Date: 2026-03-17
  Description: 일일업무보고 조회 및 통계
  DB: J_참여자관리_일일업무보고
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 일일업무보고</title>
    <mytag:Logo/>

    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="author" content="JobMoa" />

    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.0.12/index.css"
          integrity="sha256-tXJfXfp6Ewt1ilPzLDtQnJV4hclT9XuaZUKyUvmyr+Q=" crossorigin="anonymous" />

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/styles/overlayscrollbars.min.css"
          integrity="sha256-tZHrRjVqNSRyWg2wbppGnT833E/Ys0DHWGwT04GiqQg=" crossorigin="anonymous" />

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
          integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI=" crossorigin="anonymous" />

    <link rel="stylesheet" href="/css/adminlte.css" />

    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap5.min.css">
    <script src="https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.4/js/dataTables.bootstrap5.min.js"></script>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>

    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">

    <link rel="stylesheet" href="/css/adminCss/adminDailyReport_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:gnb gnb_main_header="관리자" gnb_sub_header="일일업무보고"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-body">
                                <h3 class="fw-bold text-brand mb-2">
                                    <i class="bi bi-clipboard-data"></i> 일일업무보고
                                </h3>
                                <p class="text-muted mb-0">전담자별 일일업무보고 및 실적을 조회합니다.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 통계 카드 -->
                <div class="row mb-4">
                    <div class="col-md-3">
                        <div class="stat-card">
                            <div class="mb-2 text-muted">금일 일반취업 합계</div>
                            <div class="stat-number" id="statDailyGeneral">-</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-card">
                            <div class="mb-2 text-muted">금일 알선취업 합계</div>
                            <div class="stat-number" id="statDailyPlacement">-</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-card">
                            <div class="mb-2 text-muted">금월 일반취업 합계</div>
                            <div class="stat-number" id="statMonthGeneral">-</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-card">
                            <div class="mb-2 text-muted">금월 알선취업 합계</div>
                            <div class="stat-number" id="statMonthPlacement">-</div>
                        </div>
                    </div>
                </div>

                <!-- 검색 패널 -->
                <div class="row mb-3">
                    <div class="col-md-3">
                        <label class="form-label">지점</label>
                        <select class="form-control" id="searchBranch">
                            <option value="">전체</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">아이디</label>
                        <input type="text" class="form-control" id="searchUserId" placeholder="아이디 입력">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">업무보고일</label>
                        <input type="date" class="form-control" id="searchReportDate">
                    </div>
                    <div class="col-md-3 d-flex align-items-end">
                        <button class="btn btn-primary w-100" onclick="searchReports()">
                            <i class="bi bi-search"></i> 검색
                        </button>
                    </div>
                </div>

                <!-- 일일업무보고 테이블 -->
                <div class="row">
                    <div class="col-12">
                        <div class="table-modern">
                            <table id="dailyReportTable" class="table table-hover mb-0">
                                <thead>
                                <tr>
                                    <th>PK</th>
                                    <th>지점</th>
                                    <th>아이디</th>
                                    <th>유형1</th>
                                    <th>유형2</th>
                                    <th>금일일반취업</th>
                                    <th>금일알선취업</th>
                                    <th>금주일반취업</th>
                                    <th>금주알선취업</th>
                                    <th>금월일반취업</th>
                                    <th>금월알선취업</th>
                                    <th>금년일반취업</th>
                                    <th>금년알선취업</th>
                                    <th>업무보고일</th>
                                    <th>실적등록일</th>
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
    </main>

    <footer class="app-footer">
        <div class="float-end d-none d-sm-inline">JobMoa Admin System v1.0</div>
        <strong>Copyright &copy; 2024-2026 JobMoa.</strong> All rights reserved.
    </footer>
</div>

<script src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es5.min.js"
        integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ=" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
        integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+" crossorigin="anonymous"></script>
<script src="/js/adminlte.js"></script>

<script src="/js/adminJs/adminDailyReport_0.0.1.js"></script>

</body>
</html>
