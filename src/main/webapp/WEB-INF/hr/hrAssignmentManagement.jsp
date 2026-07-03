<%--
  HR 부서배치·겸직 관리 (데모)
  Description: 직원별 부서배치(J_직원_부서배치, M:N) 관리 — 겸직 추가 / 주부서 변경 / 배치 종료
  DB: J_직원_부서배치 (+ 주부서 변경 시 J_직원_재직 · J_직원_발령이력)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 HR - 부서배치·겸직 관리</title>
    <mytag:Logo/>

    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="author" content="JobMoa" />

    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.0.12/index.css"
          integrity="sha256-tXJfXfp6Ewt1ilPzLDtQnJV4hclT9XuaZUKyUvmyr+Q=" crossorigin="anonymous" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
          integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI=" crossorigin="anonymous" />
    <link rel="stylesheet" href="/css/adminlte.min.css" />
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap5.min.css">
    <script defer src="https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js"></script>
    <script defer src="https://cdn.datatables.net/1.13.4/js/dataTables.bootstrap5.min.js"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script defer src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>

    <!-- Admin Common Design System (재사용) -->
    <link rel="stylesheet" href="/css/adminCss/adminCommon_0.0.2.css">
    <!-- Page-specific CSS -->
    <link rel="stylesheet" href="/css/hrCss/hrAssignmentManagement_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:hrGnb active="assignments" sub_header="부서배치·겸직 관리"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <!-- 페이지 헤더 -->
                <div class="admin-page-header">
                    <div class="admin-page-title">
                        <h4><i class="bi bi-arrow-left-right"></i> 부서배치·겸직 관리 <span class="hr-demo-badge">데모</span></h4>
                        <p>직원별 부서배치를 관리합니다. 겸직 추가·주부서 변경·배치 종료. (DB: J_직원_부서배치)</p>
                    </div>
                </div>

                <!-- 검색 필터 -->
                <div class="admin-filter-panel">
                    <div class="admin-filter-title"><i class="bi bi-search"></i> 직원 검색</div>
                    <div class="admin-filter-row">
                        <div class="admin-filter-group">
                            <label>이름</label>
                            <input type="text" class="form-control" id="searchName" onkeyup="if(event.key==='Enter') searchEmployees()">
                        </div>
                        <div class="admin-filter-group">
                            <label>아이디</label>
                            <input type="text" class="form-control" id="searchUserId" onkeyup="if(event.key==='Enter') searchEmployees()">
                        </div>
                        <div class="admin-filter-group">
                            <label>재직상태</label>
                            <select class="form-control" id="searchEmpStatus">
                                <option value="재직" selected>재직</option>
                                <option value="휴직">휴직</option>
                                <option value="퇴사">퇴사</option>
                                <option value="ALL">전체</option>
                            </select>
                        </div>
                        <div class="admin-filter-actions">
                            <button class="btn btn-primary" onclick="searchEmployees()"><i class="bi bi-search"></i> 검색</button>
                            <button class="btn btn-secondary" onclick="resetAssignmentSearch()">초기화</button>
                        </div>
                    </div>
                </div>

                <!-- 직원 목록 -->
                <div class="admin-table-card">
                    <div class="table-responsive">
                        <table id="assignEmployeeTable" class="table admin-data-table mb-0">
                            <thead>
                            <tr>
                                <th>아이디</th>
                                <th>이름</th>
                                <th>주부서</th>
                                <th>재직상태</th>
                                <th>현재 배치수</th>
                                <th>액션</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
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

<!-- 부서배치 관리 모달 -->
<div class="modal fade admin-modal" id="assignModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="bi bi-diagram-3"></i> 부서배치 관리 — <span id="assignEmpName"></span></h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span class="hr-assign-hint"><i class="bi bi-info-circle"></i> 주부서는 직원당 1개(현재 배치 중). 겸직은 여러 부서 동시 배치 가능. 주부서는 종료할 수 없습니다.</span>
                    <button class="btn btn-sm btn-success" onclick="openAddAssignment()"><i class="bi bi-plus-lg"></i> 겸직 추가</button>
                </div>

                <!-- 겸직 추가 폼 (토글) -->
                <div id="addAssignmentForm" class="hr-add-form" style="display:none;">
                    <div class="row g-2 align-items-end">
                        <div class="col-md-5">
                            <label class="form-label mb-1">부서 <span class="text-danger">*</span></label>
                            <select class="form-control form-control-sm" id="addDeptCode"></select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label mb-1">시작일</label>
                            <input type="date" class="form-control form-control-sm" id="addStartDate">
                            <small class="text-muted">비우면 오늘</small>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label mb-1">사유</label>
                            <input type="text" class="form-control form-control-sm" id="addReason" placeholder="예: 프로젝트 겸직">
                        </div>
                    </div>
                    <div class="text-end mt-2">
                        <button class="btn btn-sm btn-secondary" onclick="closeAddAssignment()">취소</button>
                        <button class="btn btn-sm btn-primary" onclick="submitAddAssignment()">배치 추가</button>
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="table admin-data-table mb-0" id="assignTable">
                        <thead>
                        <tr>
                            <th>부서</th>
                            <th>구분</th>
                            <th>시작일</th>
                            <th>종료일</th>
                            <th>액션</th>
                        </tr>
                        </thead>
                        <tbody id="assignTableBody"></tbody>
                    </table>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
            </div>
        </div>
    </div>
</div>

<script defer src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
<script defer src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
        integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+" crossorigin="anonymous"></script>
<script defer src="/js/adminlte.js"></script>

<script defer src="/js/hrJs/hrAssignmentManagement_0.0.1.js"></script>

</body>
</html>
