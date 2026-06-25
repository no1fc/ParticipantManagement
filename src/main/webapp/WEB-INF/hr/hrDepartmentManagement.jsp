<%--
  HR 부서/조직 관리 (데모)
  Description: 입퇴사자관리(HR) 부서 마스터 관리
  DB: J_부서
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 HR - 부서/조직 관리</title>
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
    <link rel="stylesheet" href="/css/adminlte.min.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap5.min.css">
    <script defer src="https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js"></script>
    <script defer src="https://cdn.datatables.net/1.13.4/js/dataTables.bootstrap5.min.js"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script defer src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>

    <!-- Admin Common Design System (재사용) -->
    <link rel="stylesheet" href="/css/adminCss/adminCommon_0.0.2.css">
    <!-- Page-specific CSS -->
    <link rel="stylesheet" href="/css/hrCss/hrDepartmentManagement_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:hrGnb active="departments" sub_header="부서/조직 관리"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <!-- 페이지 헤더 -->
                <div class="admin-page-header">
                    <div class="admin-page-title">
                        <h4><i class="bi bi-diagram-3"></i> 부서/조직 관리 <span class="hr-demo-badge">데모</span></h4>
                        <p>입퇴사자관리(HR) 부서 마스터를 관리합니다. (DB: J_부서)</p>
                    </div>
                    <div class="admin-page-actions">
                        <button class="btn btn-primary" onclick="openAddDepartmentModal()">
                            <i class="bi bi-plus-circle"></i> 부서 추가
                        </button>
                    </div>
                </div>

                <!-- 검색 필터 -->
                <div class="admin-filter-panel">
                    <div class="admin-filter-title"><i class="bi bi-search"></i> 부서 검색</div>
                    <div class="admin-filter-row">
                        <div class="admin-filter-group">
                            <label>부서명</label>
                            <input type="text" class="form-control" id="searchDeptName" onkeyup="if(event.key==='Enter') searchDepartments()">
                        </div>
                        <div class="admin-filter-group">
                            <label>부서유형</label>
                            <select class="form-control" id="searchDeptType">
                                <option value="">전체</option>
                                <option value="본사">본사</option>
                                <option value="지점">지점</option>
                                <option value="팀">팀</option>
                                <option value="사업부">사업부</option>
                            </select>
                        </div>
                        <div class="admin-filter-group">
                            <label>사용여부</label>
                            <select class="form-control" id="searchUseStatus">
                                <option value="사용">사용</option>
                                <option value="미사용">미사용</option>
                                <option value="">전체</option>
                            </select>
                        </div>
                        <div class="admin-filter-actions">
                            <button class="btn btn-primary" onclick="searchDepartments()"><i class="bi bi-search"></i> 검색</button>
                            <button class="btn btn-secondary" onclick="resetDepartmentSearch()">초기화</button>
                        </div>
                    </div>
                </div>

                <!-- 부서 목록 -->
                <div class="admin-table-card">
                    <div class="table-responsive">
                        <table id="departmentTable" class="table admin-data-table mb-0">
                            <thead>
                            <tr>
                                <th>부서코드</th>
                                <th>부서명</th>
                                <th>부서유형</th>
                                <th>상위부서</th>
                                <th>지점명</th>
                                <th>순서</th>
                                <th>사용여부</th>
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

<!-- 부서 추가/수정 모달 -->
<div class="modal fade admin-modal" id="departmentModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="departmentModalLabel">부서 정보</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="departmentForm">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">부서코드 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="deptCode" placeholder="예: BR_001" required>
                            <small class="text-muted">PK · 수정 시 변경 불가</small>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">부서명 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="deptName" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">부서유형 <span class="text-danger">*</span></label>
                            <select class="form-control" id="deptType">
                                <option value="본사">본사</option>
                                <option value="지점">지점</option>
                                <option value="팀">팀</option>
                                <option value="사업부">사업부</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">상위부서</label>
                            <select class="form-control" id="parentDeptCode">
                                <option value="">(최상위)</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">지점명</label>
                            <input type="text" class="form-control" id="branchName" placeholder="잡모아 '지점' 문자열과 1:1">
                            <small class="text-muted">지점유형만 입력</small>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">순서</label>
                            <input type="number" class="form-control" id="deptOrder" value="1">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="saveDepartment()">저장</button>
            </div>
        </div>
    </div>
</div>

<script defer src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es5.min.js"
        integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ=" crossorigin="anonymous"></script>
<script defer src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
<script defer src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
        integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+" crossorigin="anonymous"></script>
<script defer src="/js/adminlte.js"></script>

<script defer src="/js/hrJs/hrDepartmentManagement_0.0.1.js"></script>

</body>
</html>
