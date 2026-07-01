<%--
  HR 근속정책 관리 (데모)
  Description: 입퇴사자관리(HR) 재입사자 근속 가중 정책(키-값) 관리
  DB: J_근속산정정책
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 HR - 근속정책 관리</title>
    <mytag:Logo/>

    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="author" content="JobMoa" />

    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.0.12/index.css"
          integrity="sha256-tXJfXfp6Ewt1ilPzLDtQnJV4hclT9XuaZUKyUvmyr+Q=" crossorigin="anonymous" />
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
    <link rel="stylesheet" href="/css/hrCss/hrTenurePolicyManagement_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:hrGnb active="tenure-policies" sub_header="근속정책 관리"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <!-- 페이지 헤더 -->
                <div class="admin-page-header">
                    <div class="admin-page-title">
                        <h4><i class="bi bi-percent"></i> 근속정책 관리 <span class="hr-demo-badge">데모</span></h4>
                        <p>재입사자 이전 재직기간 근속 가중(%) 정책을 관리합니다. (DB: J_근속산정정책)</p>
                    </div>
                    <div class="admin-page-actions">
                        <button class="btn btn-primary" onclick="openAddTenurePolicyModal()">
                            <i class="bi bi-plus-circle"></i> 정책 추가
                        </button>
                    </div>
                </div>

                <!-- 안내 배너 -->
                <div class="hr-policy-hint">
                    <i class="bi bi-info-circle"></i>
                    전역 <strong>REHIRE_WEIGHT</strong> 값은 재입사 시 이전 cycle 근속의 기본 가중(%)입니다.
                    개별 cycle(입퇴사 관리)에 가중%가 지정되면 그 값이 우선하고, NULL cycle만 이 전역값을 상속합니다.
                </div>

                <!-- 검색 필터 -->
                <div class="admin-filter-panel">
                    <div class="admin-filter-title"><i class="bi bi-search"></i> 정책 검색</div>
                    <div class="admin-filter-row">
                        <div class="admin-filter-group">
                            <label>적용범위</label>
                            <input type="text" class="form-control" id="searchScope" placeholder="예: 직급산정" onkeyup="if(event.key==='Enter') searchTenurePolicies()">
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
                            <button class="btn btn-primary" onclick="searchTenurePolicies()"><i class="bi bi-search"></i> 검색</button>
                            <button class="btn btn-secondary" onclick="resetTenurePolicySearch()">초기화</button>
                        </div>
                    </div>
                </div>

                <!-- 정책 목록 -->
                <div class="admin-table-card">
                    <div class="table-responsive">
                        <table id="tenurePolicyTable" class="table admin-data-table mb-0">
                            <thead>
                            <tr>
                                <th>정책키</th>
                                <th>가중퍼센트</th>
                                <th>적용범위</th>
                                <th>비고</th>
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

<!-- 정책 추가/수정 모달 -->
<div class="modal fade admin-modal" id="tenurePolicyModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="tenurePolicyModalLabel">근속정책 정보</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="tenurePolicyForm">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">정책키 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="policyKey" placeholder="예: REHIRE_WEIGHT" required>
                            <small class="text-muted">논리키 · 수정 시 변경 불가</small>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">가중퍼센트 <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <input type="number" class="form-control" id="weightPercent" min="0" max="100" placeholder="0~100" required>
                                <span class="input-group-text">%</span>
                            </div>
                            <small class="text-muted">0~100 사이</small>
                        </div>
                        <div class="col-md-12">
                            <label class="form-label">적용범위</label>
                            <input type="text" class="form-control" id="scope" placeholder="예: 직급산정 / 근속수당">
                        </div>
                        <div class="col-md-12">
                            <label class="form-label">비고</label>
                            <textarea class="form-control" id="remark" rows="3"></textarea>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="saveTenurePolicy()">저장</button>
            </div>
        </div>
    </div>
</div>

<script defer src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
<script defer src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
        integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+" crossorigin="anonymous"></script>
<script defer src="/js/adminlte.js"></script>

<script defer src="/js/hrJs/hrTenurePolicyManagement_0.0.1.js"></script>

</body>
</html>
