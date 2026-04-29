<%--
  참여자 관리 페이지 (Participant Management)
  Created by: JobMoa Admin System
  Date: 2026-03-17
  Description: 참여자 정보 조회, 등록, 수정, 삭제 및 상세 정보 관리
  DB: J_참여자관리, J_참여자관리_자격증, J_참여자관리_직업훈련
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 참여자 관리</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <mytag:Logo/>

    <!--begin::Primary Meta Tags-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="title" content="AdminLTE v4 | Participant Management" />
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

    <link rel="stylesheet" href="/css/adminCss/adminParticipantManagement_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin::App Gnb-->
    <mytag:gnb gnb_main_header="관리자" gnb_sub_header="참여자 관리"/>
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
                                    <i class="bi bi-people-fill"></i> 참여자 관리
                                </h3>
                                <p class="text-muted mb-0">참여자 정보를 조회, 등록, 수정, 삭제할 수 있습니다.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 검색 패널 -->
                <div class="search-panel">
                    <h5 class="mb-3"><i class="bi bi-search"></i> 참여자 검색</h5>
                    <div class="row g-3">
                        <div class="col-md-3">
                            <label class="form-label">구직번호</label>
                            <input type="text" class="form-control form-control-modern" id="searchJobNo" placeholder="구직번호 입력">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">참여자명</label>
                            <input type="text" class="form-control form-control-modern" id="searchName" placeholder="이름 입력">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">지점</label>
                            <select class="form-control form-control-modern" id="searchBranch">
                                <option value="">전체</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">진행단계</label>
                            <select class="form-control form-control-modern" id="searchStatus">
                                <option value="">전체</option>
                                <option value="IAP전">IAP전</option>
                                <option value="IAP후">IAP후</option>
                                <option value="취업">취업</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">등록일 (시작)</label>
                            <input type="date" class="form-control form-control-modern" id="searchStartDate">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">등록일 (종료)</label>
                            <input type="date" class="form-control form-control-modern" id="searchEndDate">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">마감여부</label>
                            <select class="form-control form-control-modern" id="searchClosed">
                                <option value="">전체</option>
                                <option value="0">진행중</option>
                                <option value="1">마감</option>
                            </select>
                        </div>
                        <div class="col-md-3 d-flex align-items-end">
                            <button class="btn btn-light w-100" onclick="searchParticipants()">
                                <i class="bi bi-search"></i> 검색
                            </button>
                        </div>
                    </div>
                </div>

                <!-- 액션 버튼 -->
                <div class="row mb-3">
                    <div class="col-12 text-end">
                        <button class="btn btn-primary" onclick="openAddModal()">
                            <i class="bi bi-plus-circle"></i> 참여자 추가
                        </button>
                        <button class="btn btn-success" onclick="exportToExcel()">
                            <i class="bi bi-file-earmark-excel"></i> 엑셀 다운로드
                        </button>
                    </div>
                </div>

                <!-- 참여자 목록 테이블 -->
                <div class="row">
                    <div class="col-12">
                        <div class="table-modern">
                            <table id="participantTable" class="table table-hover mb-0">
                                <thead>
                                <tr>
                                    <th>구직번호</th>
                                    <th>참여자명</th>
                                    <th>생년월일</th>
                                    <th>성별</th>
                                    <th>지점</th>
                                    <th>전담자</th>
                                    <th>진행단계</th>
                                    <th>등록일</th>
                                    <th>마감여부</th>
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

<!-- 참여자 추가/수정 모달 -->
<div class="modal fade" id="participantModal" tabindex="-1" aria-labelledby="participantModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="participantModalLabel">참여자 정보</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="participantForm">
                    <div class="row g-3">
                        <!-- 기본 정보 -->
                        <div class="col-12">
                            <h6 class="fw-bold text-primary border-bottom pb-2">기본 정보</h6>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">구직번호 <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="jobNo" name="구직번호" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">참여자명 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="participantName" name="참여자" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">생년월일</label>
                            <input type="date" class="form-control" id="birthDate" name="생년월일">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">성별</label>
                            <select class="form-control" id="gender" name="성별">
                                <option value="">선택</option>
                                <option value="남">남</option>
                                <option value="여">여</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">지점</label>
                            <select class="form-control" id="branch" name="지점">
                                <option value="">선택</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">모집경로</label>
                            <input type="text" class="form-control" id="recruitPath" name="모집경로">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">참여유형</label>
                            <input type="text" class="form-control" id="participationType" name="참여유형">
                        </div>

                        <!-- 학력 및 경력 -->
                        <div class="col-12 mt-4">
                            <h6 class="fw-bold text-primary border-bottom pb-2">학력 및 경력</h6>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">학력</label>
                            <input type="text" class="form-control" id="education" name="학력">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">경력</label>
                            <input type="text" class="form-control" id="career" name="경력">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">특정계층</label>
                            <input type="text" class="form-control" id="specialClass" name="특정계층">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">취업역량</label>
                            <input type="text" class="form-control" id="employmentCapacity" name="취업역량">
                        </div>

                        <!-- 상담 정보 -->
                        <div class="col-12 mt-4">
                            <h6 class="fw-bold text-primary border-bottom pb-2">상담 정보</h6>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">전담자 계정</label>
                            <input type="text" class="form-control" id="counselorAccount" name="전담자_계정">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">진행단계</label>
                            <select class="form-control" id="progressStage" name="진행단계">
                                <option value="">선택</option>
                                <option value="IAP 전">IAP전</option>
                                <option value="IAP 후">IAP후</option>
                                <option value="취업">취업</option>
                            </select>
                        </div>

                        <!-- 희망 정보 -->
                        <div class="col-12 mt-4">
                            <h6 class="fw-bold text-primary border-bottom pb-2">희망 정보</h6>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">희망직무</label>
                            <input type="text" class="form-control" id="desiredJob" name="희망직무">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">희망급여</label>
                            <input type="text" class="form-control" id="desiredSalary" name="희망급여">
                        </div>

                        <!-- 기타 정보 -->
                        <div class="col-12 mt-4">
                            <h6 class="fw-bold text-primary border-bottom pb-2">기타 정보</h6>
                        </div>
                        <div class="col-12">
                            <label class="form-label">메모</label>
                            <textarea class="form-control" id="memo" name="메모" rows="3"></textarea>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" onclick="saveParticipant()">저장</button>
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

<script src="/js/adminJs/adminParticipantManagement_0.0.1.js"></script>
<!--end::Script-->

<script>
    // OverlayScrollbars 초기화 (adminParticipantManagement_0.0.1.js 이후 실행)
    const {
        OverlayScrollbars: OS2
    } = OverlayScrollbarsGlobal;
    if (document.querySelector('.app-sidebar-wrapper')) {
        OS2(document.querySelector('.app-sidebar-wrapper'), {});
    }
</script>

</body>
</html>
