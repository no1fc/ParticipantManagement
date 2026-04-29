<%--
  로그인정보 관리 페이지 (User Management)
  Created by: JobMoa Admin System
  Date: 2026-03-17
  Description: 전담자 및 관리자 계정 관리
  DB: J_참여자관리_로그인정보
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 사용자 관리</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <mytag:Logo/>

    <!--begin::Primary Meta Tags-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="title" content="AdminLTE v4 | User Management" />
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

    <link rel="stylesheet" href="/css/adminCss/adminUserManagement_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin::App Gnb-->
    <mytag:gnb gnb_main_header="관리자" gnb_sub_header="사용자 관리"/>
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
                                    <i class="bi bi-person-badge"></i> 사용자 관리
                                </h3>
                                <p class="text-muted mb-0">전담자 및 관리자 계정을 관리합니다.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 검색 패널 -->
                <div class="search-panel">
                    <h5 class="mb-3"><i class="bi bi-search"></i> 사용자 검색</h5>
                    <div class="row g-3">
                        <div class="col-md-3">
                            <label class="form-label">이름</label>
                            <input type="text" class="form-control" id="searchName" placeholder="이름 입력">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">아이디</label>
                            <input type="text" class="form-control" id="searchUserId" placeholder="아이디 입력">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">지점</label>
                            <select class="form-control" id="searchBranch">
                                <option value="">전체</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">권한</label>
                            <select class="form-control" id="searchRole">
                                <option value="">전체</option>
                                <option value="상담">상담</option>
                                <option value="PRA">배정관리자</option>
                                <option value="파트장">파트장</option>
                                <option value="팀장">팀장</option>
                                <option value="총괄">총괄</option>
                                <option value="차장">차장</option>
                                <option value="이사">사내이사</option>
                                <option value="상무이사">상무이사</option>
                                <option value="전무">전무</option>
                                <option value="대표">대표</option>
                            </select>
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <button class="btn btn-light w-100" onclick="searchUsers()">
                                <i class="bi bi-search"></i> 검색
                            </button>
                        </div>
                    </div>
                </div>

                <!-- 액션 버튼 -->
                <div class="row mb-3">
                    <div class="col-12 text-end">
                        <button class="btn btn-primary" onclick="openAddUserModal()">
                            <i class="bi bi-plus-circle"></i> 사용자 추가
                        </button>
                        <button class="btn btn-success" onclick="exportToExcel()">
                            <i class="bi bi-file-earmark-excel"></i> 엑셀 다운로드
                        </button>
                    </div>
                </div>

                <!-- 사용자 목록 테이블 -->
                <div class="row">
                    <div class="col-12">
                        <div class="table-modern">
                            <table id="userTable" class="table table-hover mb-0">
                                <thead>
                                <tr>
                                    <th>전담자번호</th>
                                    <th>이름</th>
                                    <th>아이디</th>
                                    <th>지점</th>
                                    <th>권한</th>
                                    <th>관리자권한</th>
                                    <th>입사일</th>
                                    <th>근속기간구분</th>
                                    <th>아이디사용여부</th>
                                    <th>배정가중치</th>
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

<!-- 사용자 추가/수정 모달 -->
<div class="modal fade" id="userModal" tabindex="-1" aria-labelledby="userModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="userModalLabel">사용자 정보</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="userForm">
                    <div class="row g-3">
                        <!-- 기본 정보 -->
                        <div class="col-12">
                            <h6 class="fw-bold text-primary border-bottom pb-2">기본 정보</h6>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">전담자번호 <span class="text-danger">*(자동생성)</span></label>
                            <input type="number" class="form-control" id="userNo" name="전담자번호" required disabled>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">이름 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="userName" name="이름" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">아이디 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="userId" name="아이디" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">비밀번호 <span class="text-danger">*</span></label>
                            <input type="password" class="form-control" id="userPassword" name="비밀번호" required>
                            <small class="text-muted">기본값: jobmoa100!</small>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">지점 <span class="text-danger">*</span></label>
                            <select class="form-control" id="userBranch" name="지점" required>
                                <option value="">선택</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">권한</label>
                            <select class="form-control" id="userRole" name="권한">
                                <option value="상담">상담</option>
                                <option value="PRA">배정관리자</option>
                                <option value="파트장">파트장</option>
                                <option value="팀장">팀장</option>
                                <option value="총괄">총괄</option>
                                <option value="차장">차장</option>
                                <option value="이사">사내이사</option>
                                <option value="상무이사">상무이사</option>
                                <option value="전무">전무</option>
                                <option value="대표">대표</option>
                            </select>
                        </div>

                        <!-- 연락처 정보 -->
                        <div class="col-12 mt-3">
                            <h6 class="fw-bold text-primary border-bottom pb-2">연락처 정보</h6>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">이메일</label>
                            <input type="email" class="form-control" id="userEmail" name="이메일">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">전화번호</label>
                            <input type="text" class="form-control" id="userPhone" name="전화번호" placeholder="010-1234-5678">
                        </div>

                        <!-- 근무 정보 -->
                        <div class="col-12 mt-3">
                            <h6 class="fw-bold text-primary border-bottom pb-2">근무 정보</h6>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">입사일</label>
                            <input type="date" class="form-control" id="hireDate" name="입사일">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">최종발령일</label>
                            <input type="date" class="form-control" id="lastAssignmentDate" name="최종발령일">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">근속기간구분 <span class="text-danger">*(자동계산)</span></label>
                            <select class="form-control" id="serviceYears" name="근속기간구분" required disabled readonly>
                                <option value="1년미만">1년미만</option>
                                <option value="1년이상">1년이상</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">배정가중치</label>
                            <input type="number" step="0.1" class="form-control" id="assignmentWeight" name="배정가중치" value="1.0">
                        </div>

                        <!-- 권한 및 설정 -->
                        <div class="col-12 mt-3">
                            <h6 class="fw-bold text-primary border-bottom pb-2">권한 및 설정</h6>
                        </div>
                        <div class="col-md-4">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="adminAuth" name="관리자권한">
                                <label class="form-check-label" for="adminAuth">관리자권한</label>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="performanceWrite" name="실적작성여부" checked>
                                <label class="form-check-label" for="performanceWrite">실적작성여부</label>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="dailyReportIssue" name="일일업무일지발급여부" checked>
                                <label class="form-check-label" for="dailyReportIssue">일일업무일지발급여부</label>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">아이디사용여부</label>
                            <select class="form-control" id="userStatus" name="아이디사용여부">
                                <option value="사용">사용</option>
                                <option value="잠금">잠금</option>
                                <option value="퇴사">퇴사</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">조회순서</label>
                            <input type="number" class="form-control" id="viewOrder" name="조회순서" value="1">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="saveUser()">저장</button>
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

<script src="/js/adminJs/adminUserManagement_0.0.1.js"></script>
<!--end::Script-->

</body>
</html>
