<%--
  HR 계정 관리 (데모)
  Description: 입퇴사자관리(HR) 계정 상태·비밀번호 관리
  DB: J_직원_계정 (이름: J_직원 조인)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 HR - 계정 관리</title>
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
    <link rel="stylesheet" href="/css/hrCss/hrAccountManagement_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:hrGnb active="accounts" sub_header="계정 관리"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <div class="admin-page-header">
                    <div class="admin-page-title">
                        <h4><i class="bi bi-key"></i> 계정 관리 <span class="hr-demo-badge">데모</span></h4>
                        <p>직원 계정의 상태·비밀번호를 관리합니다. (DB: J_직원_계정) · 계정은 직원당 1:1</p>
                    </div>
                </div>

                <div class="admin-filter-panel">
                    <div class="admin-filter-title"><i class="bi bi-search"></i> 계정 검색</div>
                    <div class="admin-filter-row">
                        <div class="admin-filter-group">
                            <label>아이디</label>
                            <input type="text" class="form-control" id="searchUserId" onkeyup="if(event.key==='Enter') searchAccounts()">
                        </div>
                        <div class="admin-filter-group">
                            <label>이름</label>
                            <input type="text" class="form-control" id="searchUserName" onkeyup="if(event.key==='Enter') searchAccounts()">
                        </div>
                        <div class="admin-filter-group">
                            <label>계정상태</label>
                            <select class="form-control" id="searchAccountStatus">
                                <option value="">전체</option>
                                <option value="사용">사용</option>
                                <option value="정지">정지</option>
                                <option value="잠금">잠금</option>
                                <option value="퇴사">퇴사</option>
                                <option value="승인대기">승인대기</option>
                            </select>
                        </div>
                        <div class="admin-filter-actions">
                            <button class="btn btn-primary" onclick="searchAccounts()"><i class="bi bi-search"></i> 검색</button>
                            <button class="btn btn-secondary" onclick="resetAccountSearch()">초기화</button>
                        </div>
                    </div>
                </div>

                <div class="admin-table-card">
                    <div class="table-responsive">
                        <table id="accountTable" class="table admin-data-table mb-0">
                            <thead>
                            <tr>
                                <th>아이디</th>
                                <th>이름</th>
                                <th>계정상태</th>
                                <th>로그인실패</th>
                                <th>비밀번호</th>
                                <th>비번변경일</th>
                                <th>마지막로그인</th>
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

<!-- 계정상태 변경 모달 -->
<div class="modal fade admin-modal" id="statusModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title">계정 상태 변경</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p class="mb-2"><strong id="statusUserLabel"></strong></p>
                <input type="hidden" id="statusUserId">
                <label class="form-label">계정상태</label>
                <select class="form-control" id="statusValue">
                    <option value="사용">사용</option>
                    <option value="정지">정지</option>
                    <option value="잠금">잠금</option>
                    <option value="퇴사">퇴사</option>
                    <option value="승인대기">승인대기</option>
                </select>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="saveStatus()">변경</button>
            </div>
        </div>
    </div>
</div>

<!-- 비밀번호 초기화 모달 -->
<div class="modal fade admin-modal" id="resetModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title">비밀번호 초기화</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p class="mb-2"><strong id="resetUserLabel"></strong></p>
                <input type="hidden" id="resetUserId">
                <label class="form-label">임시 비밀번호 <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="resetPasswordValue" placeholder="임시 비밀번호 입력">
                <small class="text-muted">BCrypt로 암호화되어 저장됩니다.</small>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="saveResetPassword()">초기화</button>
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

<script defer src="/js/hrJs/hrAccountManagement_0.0.1.js"></script>

</body>
</html>
