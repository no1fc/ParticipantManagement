<%--
  자격증/직업훈련 관리 페이지 (Certificate & Training Management)
  Created by: JobMoa Admin System
  Date: 2026-03-17
  Description: 참여자 자격증 및 직업훈련 정보 관리
  DB: J_참여자관리_자격증, J_참여자관리_직업훈련
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 자격증/직업훈련 관리</title>
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

    <link rel="stylesheet" href="/css/adminCss/adminCertificateTraining_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:gnb gnb_main_header="관리자" gnb_sub_header="자격증/직업훈련 관리"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-body">
                                <h3 class="fw-bold text-brand mb-2">
                                    <i class="bi bi-award"></i> 자격증/직업훈련 관리
                                </h3>
                                <p class="text-muted mb-0">참여자의 자격증 및 직업훈련 정보를 관리합니다.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 자격증 섹션 -->
                <div class="section-card">
                    <div class="section-header">
                        <h5 class="mb-0"><i class="bi bi-patch-check"></i> 자격증 관리</h5>
                    </div>

                    <div class="row mb-3">
                        <div class="col-12 text-end">
                            <button class="btn btn-primary" onclick="openCertificateModal()">
                                <i class="bi bi-plus-circle"></i> 자격증 추가
                            </button>
                        </div>
                    </div>

                    <div class="table-modern">
                        <table id="certificateTable" class="table table-hover mb-0">
                            <thead>
                            <tr>
                                <th>자격증번호</th>
                                <th>구직번호</th>
                                <th>자격증명</th>
                                <th>액션</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 직업훈련 섹션 -->
                <div class="section-card">
                    <div class="section-header">
                        <h5 class="mb-0"><i class="bi bi-book"></i> 직업훈련 관리</h5>
                    </div>

                    <div class="row mb-3">
                        <div class="col-12 text-end">
                            <button class="btn btn-primary" onclick="openTrainingModal()">
                                <i class="bi bi-plus-circle"></i> 직업훈련 추가
                            </button>
                        </div>
                    </div>

                    <div class="table-modern">
                        <table id="trainingTable" class="table table-hover mb-0">
                            <thead>
                            <tr>
                                <th>직업훈련번호</th>
                                <th>구직번호</th>
                                <th>직업훈련명</th>
                                <th>액션</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
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

<!-- 자격증 추가/수정 모달 -->
<div class="modal fade" id="certificateModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="certificateModalLabel">자격증 정보</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="certificateForm">
                    <div class="mb-3">
                        <label class="form-label">구직번호 <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" id="cert-jobNo" name="구직번호" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">자격증명 <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="cert-name" name="자격증" required>
                    </div>
                    <input type="hidden" id="cert-id" name="자격증번호">
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="saveCertificate()">저장</button>
            </div>
        </div>
    </div>
</div>

<!-- 직업훈련 추가/수정 모달 -->
<div class="modal fade" id="trainingModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="trainingModalLabel">직업훈련 정보</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="trainingForm">
                    <div class="mb-3">
                        <label class="form-label">구직번호 <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" id="train-jobNo" name="구직번호" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">직업훈련명 <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="train-name" name="직업훈련" required>
                    </div>
                    <input type="hidden" id="train-id" name="직업훈련번호">
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="saveTraining()">저장</button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es5.min.js"
        integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ=" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
        integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+" crossorigin="anonymous"></script>
<script src="/js/adminlte.js"></script>

<script src="/js/adminJs/adminCertificateTraining_0.0.1.js"></script>

</body>
</html>
