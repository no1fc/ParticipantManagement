<%--
  HR 직원 관리 (데모)
  Description: 입퇴사자관리(HR) 직원 마스터 CRUD (신규등록/편집/퇴사)
  DB: J_직원 ⋈ J_직원_재직 ⋈ J_부서 ⋈ J_직원_계정 (+ 부서배치/재직기간/발령이력)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 HR - 직원 관리</title>
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
    <link rel="stylesheet" href="/css/hrCss/hrEmployeeManagement_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:hrGnb active="employees" sub_header="직원 관리"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <!-- 페이지 헤더 -->
                <div class="admin-page-header">
                    <div class="admin-page-title">
                        <h4><i class="bi bi-people"></i> 직원 관리 <span class="hr-demo-badge">데모</span></h4>
                        <p>입퇴사자관리(HR) 직원 마스터를 관리합니다. (DB: J_직원 외 6테이블)</p>
                    </div>
                    <div class="admin-page-actions">
                        <button class="btn btn-primary" onclick="openAddEmployeeModal()">
                            <i class="bi bi-plus-circle"></i> 직원 등록
                        </button>
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
                            <label>주부서</label>
                            <select class="form-control" id="searchDeptCode">
                                <option value="">전체</option>
                            </select>
                        </div>
                        <div class="admin-filter-group">
                            <label>재직상태</label>
                            <select class="form-control" id="searchEmpStatus">
                                <option value="">재직</option>
                                <option value="휴직">휴직</option>
                                <option value="퇴사">퇴사</option>
                                <option value="ALL">전체</option>
                            </select>
                        </div>
                        <div class="admin-filter-actions">
                            <button class="btn btn-primary" onclick="searchEmployees()"><i class="bi bi-search"></i> 검색</button>
                            <button class="btn btn-secondary" onclick="resetEmployeeSearch()">초기화</button>
                        </div>
                    </div>
                </div>

                <!-- 직원 목록 -->
                <div class="admin-table-card">
                    <div class="table-responsive">
                        <table id="employeeTable" class="table admin-data-table mb-0">
                            <thead>
                            <tr>
                                <th>아이디</th>
                                <th>이름</th>
                                <th>주부서</th>
                                <th>직급</th>
                                <th>재직상태</th>
                                <th>입사일</th>
                                <th>계정상태</th>
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

<!-- 직원 추가/수정 모달 -->
<div class="modal fade admin-modal" id="employeeModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="employeeModalLabel">직원 정보</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="employeeForm">
                    <h6 class="hr-form-section"><i class="bi bi-person-vcard"></i> 신원 (J_직원)</h6>
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label class="form-label">아이디 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="userId" placeholder="예: hong123" required>
                            <small class="text-muted">PK · 수정 시 변경 불가</small>
                        </div>
                        <div class="col-md-4" id="passwordGroup">
                            <label class="form-label">비밀번호 <span class="text-danger">*</span></label>
                            <input type="password" class="form-control" id="password" autocomplete="new-password">
                            <small class="text-muted">등록 시 필수 · BCrypt 저장</small>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">이름 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="name" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">이메일</label>
                            <input type="email" class="form-control" id="email">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">전화번호</label>
                            <input type="text" class="form-control" id="phone">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">최초입사일 <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" id="hireDate" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">다우사번</label>
                            <input type="text" class="form-control" id="daouNo">
                        </div>
                        <div class="col-md-8">
                            <label class="form-label">비고</label>
                            <input type="text" class="form-control" id="note">
                        </div>
                    </div>

                    <h6 class="hr-form-section mt-3"><i class="bi bi-briefcase"></i> 재직 (J_직원_재직)</h6>
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label class="form-label">주부서 <span class="text-danger">*</span></label>
                            <select class="form-control" id="deptCode" required>
                                <option value="">(선택)</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">직급</label>
                            <input type="text" class="form-control" id="position" placeholder="예: 상담사">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">권한</label>
                            <input type="text" class="form-control" id="role" value="상담">
                            <small class="text-muted">COUNSEL 로그인 역할 · 기본 '상담'</small>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">관리자권한</label>
                            <select class="form-control" id="isAdmin">
                                <option value="false">일반</option>
                                <option value="true">관리자</option>
                            </select>
                        </div>
                        <div class="col-md-4" id="accountStatusGroup" style="display:none;">
                            <label class="form-label">계정상태</label>
                            <select class="form-control" id="accountStatus">
                                <option value="사용">사용</option>
                                <option value="정지">정지</option>
                                <option value="잠금">잠금</option>
                                <option value="승인대기">승인대기</option>
                            </select>
                        </div>
                    </div>
                    <p class="hr-form-hint mt-2">
                        <i class="bi bi-info-circle"></i> 등록 시 직원·계정·재직·부서배치·재직기간·발령이력이 함께 생성됩니다.
                        사이트 접근(로그인 권한)은 <b>사이트 접속·권한 관리</b> 화면에서 별도 부여합니다.
                    </p>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="saveEmployee()">저장</button>
            </div>
        </div>
    </div>
</div>

<script defer src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
<script defer src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
        integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+" crossorigin="anonymous"></script>
<script defer src="/js/adminlte.js"></script>

<script defer src="/js/hrJs/hrEmployeeManagement_0.0.1.js"></script>

</body>
</html>
