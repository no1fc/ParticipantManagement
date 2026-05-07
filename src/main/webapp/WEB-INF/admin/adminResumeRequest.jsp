<%--
  이력서 요청 관리 페이지 (Resume Request Management)
  Created by: JobMoa Admin System
  Date: 2026-03-17
  Description: 이력서 요청양식 관리
  DB: J_참여자관리_이력서요청양식
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 이력서 요청 관리</title>
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

    <link rel="stylesheet" href="/css/adminCss/adminResumeRequest_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:gnb gnb_main_header="관리자" gnb_sub_header="이력서 요청 관리"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card-modern border-0 shadow-sm">
                            <div class="card-body">
                                <h3 class="fw-bold text-brand mb-2">
                                    <i class="bi bi-file-earmark-person"></i> 이력서 요청 관리
                                </h3>
                                <p class="text-muted mb-0">기업의 이력서 요청을 관리합니다.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 검색 패널 -->
                <div class="search-panel">
                    <h5 class="mb-3"><i class="bi bi-search"></i> 이력서 요청 검색</h5>
                    <div class="row g-3">
                        <div class="col-md-2">
                            <label class="form-label">지점</label>
                            <select class="form-control form-control-modern" id="searchBranch">
                                <option value="">전체</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">상담사</label>
                            <select class="form-control form-control-modern" id="searchCounselor">
                                <option value="">전체</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">참여자명</label>
                            <input type="text" class="form-control form-control-modern" id="searchName" placeholder="이름 입력">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">상태</label>
                            <select class="form-control form-control-modern" id="searchStatus">
                                <option value="">전체</option>
                                <option value="대기">대기</option>
                                <option value="완료">완료</option>
                                <option value="반려">반려</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">등록일 (시작)</label>
                            <input type="date" class="form-control form-control-modern" id="searchStartDate">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">등록일 (종료)</label>
                            <input type="date" class="form-control form-control-modern" id="searchEndDate">
                        </div>
                        <div class="col-md-3 d-flex align-items-end">
                            <button class="btn btn-light w-100 me-2" onclick="searchResumeRequests()">
                                <i class="bi bi-search"></i> 검색
                            </button>
                            <button class="btn btn-outline-secondary w-100" onclick="resetSearch()">
                                <i class="bi bi-arrow-counterclockwise"></i> 초기화
                            </button>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-12">
                        <div class="table-modern">
                            <table id="resumeRequestTable" class="table table-hover mb-0">
                                <thead>
                                <tr>
                                    <th>등록번호</th>
                                    <th>구직번호</th>
                                    <th>참여자명</th>
                                    <th>지점</th>
                                    <th>상담사</th>
                                    <th>기업명</th>
                                    <th>담당자명</th>
                                    <th>이메일</th>
                                    <th>비상연락처</th>
                                    <th>상태</th>
                                    <th>등록일</th>
                                    <th>수정일</th>
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
        </div>
    </main>

    <footer class="app-footer">
        <div class="float-end d-none d-sm-inline">JobMoa Admin System v1.0</div>
        <strong>Copyright &copy; 2024-2026 JobMoa.</strong> All rights reserved.
    </footer>
</div>

<!-- 상세보기 모달 -->
<div class="modal fade" id="resumeRequestModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title">이력서 요청 상세</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="row g-3">
                    <div class="col-md-6">
                        <strong>구직번호:</strong> <span id="detail-jobNo"></span>
                    </div>
                    <div class="col-md-6">
                        <strong>기업명:</strong> <span id="detail-company"></span>
                    </div>
                    <div class="col-md-6">
                        <strong>담당자명:</strong> <span id="detail-manager"></span>
                    </div>
                    <div class="col-md-6">
                        <strong>이메일:</strong> <span id="detail-email"></span>
                    </div>
                    <div class="col-md-6">
                        <strong>비상연락처:</strong> <span id="detail-phone"></span>
                    </div>
                    <div class="col-md-6">
                        <strong>상태:</strong> <span id="detail-status"></span>
                    </div>
                    <div class="col-12">
                        <strong>기타요청사항:</strong>
                        <p class="mt-2 border p-3 rounded" id="detail-request"></p>
                    </div>
                    <div class="col-12">
                        <strong>개인정보 처리 동의:</strong>
                        <ul class="list-unstyled mt-2">
                            <li><i class="bi bi-check-circle-fill text-success"></i> 기업개인정보처리동의: <span id="detail-privacy1"></span></li>
                            <li><i class="bi bi-check-circle-fill text-success"></i> 기업담당자개인정보처리동의: <span id="detail-privacy2"></span></li>
                            <li><i class="bi bi-check-circle-fill text-info"></i> 마케팅개인정보사용동의: <span id="detail-marketing"></span></li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
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

<script src="/js/adminJs/adminResumeRequest_0.0.2.js"></script>

</body>
</html>
