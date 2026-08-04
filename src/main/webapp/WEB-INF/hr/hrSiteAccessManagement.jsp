<%--
  HR 사이트 접속·권한 관리 (데모)
  Description: 입퇴사자관리(HR) 직원별 사이트 접속/권한 프로비저닝
  DB: J_직원_사이트접속 (이름: J_직원, 사이트명: J_사이트 조인)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 HR - 사이트 접속·권한 관리</title>
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

    <link rel="stylesheet" href="/css/adminCss/adminCommon_0.0.2.css">
    <link rel="stylesheet" href="/css/hrCss/hrSiteAccessManagement_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:hrGnb active="site-access" sub_header="사이트 접속·권한 관리"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <div class="admin-page-header">
                    <div class="admin-page-title">
                        <h4><i class="bi bi-shield-check"></i> 사이트 접속·권한 관리 <span class="hr-demo-badge">데모</span></h4>
                        <p>직원별 사이트 접속/사이트내권한을 부여합니다. (DB: J_직원_사이트접속) · 전부 수동 부여</p>
                    </div>
                    <div class="admin-page-actions">
                        <button class="btn btn-primary" onclick="openAddSiteAccessModal()">
                            <i class="bi bi-plus-circle"></i> 접속 부여
                        </button>
                    </div>
                </div>

                <div class="hr-rule-note">
                    <i class="bi bi-info-circle"></i>
                    권한 해석 우선순위: <strong>부서코드 지정 행 &gt; 부서코드 NULL 기본행 &gt; J_직원_재직.권한 상속</strong>.
                    사이트내권한을 비우면(NULL) 재직.권한을 상속합니다.
                </div>

                <div class="admin-filter-panel">
                    <div class="admin-filter-title"><i class="bi bi-search"></i> 접속 검색</div>
                    <div class="admin-filter-row">
                        <div class="admin-filter-group">
                            <label>직원아이디</label>
                            <input type="text" class="form-control" id="searchUserId" onkeyup="if(event.key==='Enter') searchSiteAccess()">
                        </div>
                        <div class="admin-filter-group">
                            <label>사이트</label>
                            <select class="form-control" id="searchSiteCode">
                                <option value="">전체</option>
                            </select>
                        </div>
                        <div class="admin-filter-actions">
                            <button class="btn btn-primary" onclick="searchSiteAccess()"><i class="bi bi-search"></i> 검색</button>
                            <button class="btn btn-secondary" onclick="resetSiteAccessSearch()">초기화</button>
                        </div>
                    </div>
                </div>

                <div class="admin-table-card">
                    <div class="table-responsive">
                        <table id="siteAccessTable" class="table admin-data-table mb-0">
                            <thead>
                            <tr>
                                <th>직원아이디</th>
                                <th>이름</th>
                                <th>사이트</th>
                                <th>부서코드</th>
                                <th>접속허용</th>
                                <th>사이트내권한</th>
                                <th>부여일</th>
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

<!-- 접속 부여/수정 모달 -->
<div class="modal fade admin-modal" id="siteAccessModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="siteAccessModalLabel">사이트 접속 부여</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="siteAccessForm">
                    <input type="hidden" id="accessPk">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">직원아이디 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="userId" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">사이트 <span class="text-danger">*</span></label>
                            <select class="form-control" id="siteCode"></select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">부서코드</label>
                            <input type="text" class="form-control" id="deptCode" placeholder="공백 = 전 부서 공통 기본행">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">접속허용</label>
                            <select class="form-control" id="accessAllowed">
                                <option value="true">허용</option>
                                <option value="false">차단</option>
                            </select>
                        </div>
                        <div class="col-12">
                            <label class="form-label">사이트내권한</label>
                            <input type="text" class="form-control" id="siteRole" placeholder="공백 = J_직원_재직.권한 상속">
                            <small class="text-muted">예: HR_MANAGER, HR_STAFF, HR_VIEW (또는 COUNSEL 역할)</small>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="saveSiteAccess()">저장</button>
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

<script defer src="/js/hrJs/hrSiteAccessManagement_0.0.1.js"></script>

</body>
</html>
