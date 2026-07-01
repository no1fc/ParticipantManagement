<%--
  HR 입퇴사 관리 (데모)
  Description: 직원별 재직 cycle(J_직원_재직기간) 관리 — 재입사(새 cycle)/퇴사(사유)/cycle 편집(가중%·직급반영)
  DB: J_직원_재직기간 (+ 재입사/퇴사 시 J_직원 · J_직원_계정 · J_직원_발령이력)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 HR - 입퇴사 관리</title>
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
    <link rel="stylesheet" href="/css/hrCss/hrEmployment_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:hrGnb active="employments" sub_header="입퇴사 관리"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <!-- 페이지 헤더 -->
                <div class="admin-page-header">
                    <div class="admin-page-title">
                        <h4><i class="bi bi-arrow-repeat"></i> 입퇴사 관리 <span class="hr-demo-badge">데모</span></h4>
                        <p>직원별 재직 cycle을 관리합니다. 재입사(새 cycle)·퇴사(사유)·근속 가중% 편집. (DB: J_직원_재직기간)</p>
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
                                <option value="ALL">전체</option>
                                <option value="재직">재직</option>
                                <option value="휴직">휴직</option>
                                <option value="퇴사">퇴사</option>
                            </select>
                        </div>
                        <div class="admin-filter-actions">
                            <button class="btn btn-primary" onclick="searchEmployees()"><i class="bi bi-search"></i> 검색</button>
                            <button class="btn btn-secondary" onclick="resetEmploymentSearch()">초기화</button>
                        </div>
                    </div>
                </div>

                <!-- 직원 목록 -->
                <div class="admin-table-card">
                    <div class="table-responsive">
                        <table id="employmentTable" class="table admin-data-table mb-0">
                            <thead>
                            <tr>
                                <th>아이디</th>
                                <th>이름</th>
                                <th>주부서</th>
                                <th>재직상태</th>
                                <th>총근속일</th>
                                <th>cycle수</th>
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

<!-- 입퇴사 이력(cycle 타임라인) 모달 -->
<div class="modal fade admin-modal" id="historyModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="bi bi-clock-history"></i> 입퇴사 이력 — <span id="historyEmpName"></span></h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span class="hr-cycle-hint"><i class="bi bi-info-circle"></i> 재입사 = 새 cycle(재직순번+1). 근속 = 현재 cycle 100% + Σ(이전 cycle × 가중%).</span>
                    <span id="historyActionBar"></span>
                </div>
                <div class="table-responsive">
                    <table class="table admin-data-table mb-0" id="cycleTable">
                        <thead>
                        <tr>
                            <th>순번</th>
                            <th>입사일</th>
                            <th>퇴사일</th>
                            <th>퇴사사유</th>
                            <th>가중%</th>
                            <th>직급반영</th>
                            <th>편집</th>
                        </tr>
                        </thead>
                        <tbody id="cycleTableBody"></tbody>
                    </table>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
            </div>
        </div>
    </div>
</div>

<!-- 재입사 모달 -->
<div class="modal fade admin-modal" id="rehireModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="bi bi-arrow-repeat"></i> 재입사 처리</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p class="hr-cycle-hint mb-3"><i class="bi bi-info-circle"></i> <b id="rehireEmpName"></b> 직원의 새 재직 cycle을 생성합니다. 계정이 '정지'면 '사용'으로 복구됩니다.</p>
                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label">재입사 입사일 <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" id="rehireHireDate" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">근속 가중%</label>
                        <input type="number" class="form-control" id="rehireWeight" min="0" max="100" placeholder="비우면 정책 상속(REHIRE_WEIGHT)">
                        <small class="text-muted">이전 cycle 근속 반영율 · 0~100 · 비우면 전역 정책</small>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">직급반영여부</label>
                        <select class="form-control" id="rehireReflect">
                            <option value="false">미반영</option>
                            <option value="true">반영</option>
                        </select>
                        <small class="text-muted">직급산정 근속에 이전 cycle 포함</small>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">사유</label>
                        <input type="text" class="form-control" id="rehireReason" placeholder="예: 경력직 재입사">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="submitRehire()">재입사 처리</button>
            </div>
        </div>
    </div>
</div>

<!-- 퇴사 모달 -->
<div class="modal fade admin-modal" id="resignModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title"><i class="bi bi-box-arrow-right"></i> 퇴사 처리</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p class="hr-cycle-hint mb-3"><i class="bi bi-exclamation-triangle"></i> <b id="resignEmpName"></b> 직원을 퇴사 처리합니다. 현재 cycle이 종료되고 계정이 자동으로 정지됩니다.</p>
                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label">퇴사일</label>
                        <input type="date" class="form-control" id="resignDate">
                        <small class="text-muted">비우면 오늘</small>
                    </div>
                    <div class="col-md-12">
                        <label class="form-label">퇴사사유</label>
                        <input type="text" class="form-control" id="resignReason" placeholder="예: 개인 사정">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-danger" onclick="submitResign()">퇴사 처리</button>
            </div>
        </div>
    </div>
</div>

<!-- cycle 편집 모달 -->
<div class="modal fade admin-modal" id="cycleEditModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="bi bi-pencil"></i> cycle 편집 (순번 <span id="cycleEditSeq"></span>)</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p class="hr-cycle-hint mb-3"><i class="bi bi-info-circle"></i> 종료된 재직 cycle의 근속 가중%·직급반영여부를 조정합니다.</p>
                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label">근속 가중%</label>
                        <input type="number" class="form-control" id="editWeight" min="0" max="100" placeholder="비우면 정책 상속">
                        <small class="text-muted">0~100 · 비우면 전역 정책</small>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">직급반영여부</label>
                        <select class="form-control" id="editReflect">
                            <option value="false">미반영</option>
                            <option value="true">반영</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="submitCycleEdit()">저장</button>
            </div>
        </div>
    </div>
</div>

<script defer src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
<script defer src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
        integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+" crossorigin="anonymous"></script>
<script defer src="/js/adminlte.js"></script>

<script defer src="/js/hrJs/hrEmployment_0.0.1.js"></script>

</body>
</html>
