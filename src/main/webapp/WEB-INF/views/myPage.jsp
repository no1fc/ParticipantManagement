<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 24. 12. 30.
  Time: 오후 5:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <mytag:Logo/>
    <meta charset="utf-8" />
    <!--begin::Primary Meta Tags-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="title" content="AdminLTE v4 | Dashboard" />
    <meta name="author" content="ColorlibHQ" />
    <meta
            name="description"
            content="AdminLTE is a Free Bootstrap 5 Admin Dashboard, 30 example pages using Vanilla JS."
    />
    <meta
            name="keywords"
            content="bootstrap 5, bootstrap, bootstrap 5 admin dashboard, bootstrap 5 dashboard, bootstrap 5 charts, bootstrap 5 calendar, bootstrap 5 datepicker, bootstrap 5 tables, bootstrap 5 datatable, vanilla js datatable, colorlibhq, colorlibhq dashboard, colorlibhq admin dashboard"
    />
    <!--end::Primary Meta Tags-->
    <!-- jQuery JS -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>

    <!--begin::Fonts-->
    <link
            rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.0.12/index.css"
            integrity="sha256-tXJfXfp6Ewt1ilPzLDtQnJV4hclT9XuaZUKyUvmyr+Q="
            crossorigin="anonymous"
    />
    <!--end::Fonts-->
    <!--begin::Third Party Plugin(OverlayScrollbars)-->
    <link
            rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/styles/overlayscrollbars.min.css"
            integrity="sha256-tZHrRjVqNSRyWg2wbppGnT833E/Ys0DHWGwT04GiqQg="
            crossorigin="anonymous"
    />
    <!--end::Third Party Plugin(OverlayScrollbars)-->
    <!--begin::Third Party Plugin(Bootstrap Icons)-->
    <link
            rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
            integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI="
            crossorigin="anonymous"
    />
    <!--end::Third Party Plugin(Bootstrap Icons)-->
    <!--begin::Required Plugin(AdminLTE)-->
    <link rel="stylesheet" href="/css/adminlte.css" />
    <!--end::Required Plugin(AdminLTE)-->
    <!-- apexcharts -->
    <link
            rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/apexcharts@3.37.1/dist/apexcharts.css"
            integrity="sha256-4MX+61mt9NVvvuPjUWdUdyfZfxSB1/Rf9WtqRHgG5S0="
            crossorigin="anonymous"
    />
    <!-- jsvectormap -->
    <link
            rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/jsvectormap@1.5.3/dist/css/jsvectormap.min.css"
            integrity="sha256-+uGLJmmTKOqBr+2E6KDYs/NRsHxSkONXFHUL0fy2O/4="
            crossorigin="anonymous"
    />
    <!-- mouse pointer 모양 bootstrap 5 -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <!-- myPage JS -->
    <!-- SweetAlert2 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script src="/js/myPageJS_0.0.2.js"></script>
    <link rel="stylesheet" href="/css/participantCss/myPage_0.0.3.css">
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin:::App Gnb-->
    <mytag:gnb gnb_main_header="마이페이지" gnb_sub_header="계정정보확인"/>
    <!--end:::App Gnb-->
    <!--begin:::App main content-->
    <!--begin::App Main-->
    <main class="app-main">
        <!--begin::App Content-->
        <div class="app-content">
            <!--begin::Main content-->
            <div class="container-fluid">
            <!-- 필요 본문 내용은 이쪽에 만들어 주시면 됩니다. -->
                <div class="row">
                    <div class="col-md-10 offset-md-1">
                        <!-- 비밀번호 확인 카드 (초기 상태) -->
                        <div id="password-verify-section" class="card-modern mb-4">
                            <div class="p-4 border-bottom">
                                <h3 class="fw-bold m-0">계정 정보 확인</h3>
                            </div>
                            <div class="p-4">
                                <div class="myPage-summary mb-3">
                                    <span class="badge bg-warning text-dark border p-2">보안 확인 필요</span>
                                </div>
                                <div class="card-modern bg-light border">
                                    <div class="p-3 border-bottom d-flex align-items-center">
                                        <div class="fw-bold">비밀번호 확인</div>
                                        <div id="checkPasswordBtn" class="btn btn-sm btn-outline-primary ms-auto">확인</div>
                                    </div>
                                    <div class="p-4">
                                        <label for="checkPassword" class="form-label">비밀번호</label>
                                        <input type="password" id="checkPassword" class="form-control w-100">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 탭 콘텐츠 (비밀번호 확인 후 표시) -->
                        <div id="mypage-tabs-section" style="display:none;">
                            <!-- 탭 네비게이션 -->
                            <ul class="nav nav-tabs mb-0" role="tablist">
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link active" data-bs-toggle="tab" data-bs-target="#tab-profile" type="button" role="tab">
                                        <i class="bi bi-person-circle me-1"></i> 계정 정보
                                    </button>
                                </li>
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link" data-bs-toggle="tab" data-bs-target="#tab-daily-report" type="button" role="tab">
                                        <i class="bi bi-clipboard-data me-1"></i> 일일보고
                                    </button>
                                </li>
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link" data-bs-toggle="tab" data-bs-target="#tab-security" type="button" role="tab">
                                        <i class="bi bi-shield-lock me-1"></i> 보안 설정
                                    </button>
                                </li>
                            </ul>

                            <div class="tab-content card-modern" style="border-top-left-radius:0; border-top-right-radius:0;">
                                <!-- 탭 1: 계정 정보 (이메일, 전화번호, 읽기전용 정보) -->
                                <div class="tab-pane fade show active p-4" id="tab-profile" role="tabpanel">
                                    <h5 class="fw-bold mb-3">기본 정보</h5>
                                    <div class="row mb-3">
                                        <div class="col-md-6">
                                            <label class="form-label text-muted">이름</label>
                                            <p id="profileName" class="fw-semibold">-</p>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label text-muted">아이디</label>
                                            <p id="profileId" class="fw-semibold">-</p>
                                        </div>
                                    </div>
                                    <div class="row mb-3">
                                        <div class="col-md-6">
                                            <label class="form-label text-muted">지점</label>
                                            <p id="profileBranch" class="fw-semibold">-</p>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label text-muted">고유번호</label>
                                            <p id="profileUniqueNo" class="fw-semibold">-</p>
                                        </div>
                                    </div>
                                    <div class="row mb-3">
                                        <div class="col-md-4">
                                            <label class="form-label text-muted">등록일</label>
                                            <p id="profileRegDate" class="fw-semibold">-</p>
                                        </div>
                                        <div class="col-md-4">
                                            <label class="form-label text-muted">입사일</label>
                                            <p id="profileJoinDate" class="fw-semibold">-</p>
                                        </div>
                                        <div class="col-md-4">
                                            <label class="form-label text-muted">근속구분</label>
                                            <p id="profileContinuous" class="fw-semibold">-</p>
                                        </div>
                                    </div>

                                    <hr>
                                    <h5 class="fw-bold mb-3">연락처 수정</h5>
                                    <div class="row mb-3">
                                        <div class="col-md-6">
                                            <label for="contactPhone" class="form-label">전화번호 (내선)</label>
                                            <input type="text" class="form-control" id="contactPhone" placeholder="02-0000-0000(000)">
                                        </div>
                                        <div class="col-md-6">
                                            <label for="contactEmail" class="form-label">이메일</label>
                                            <input type="email" class="form-control" id="contactEmail" placeholder="example@daou.co.kr">
                                        </div>
                                    </div>
                                    <div class="text-end">
                                        <button type="button" class="btn btn-primary" id="saveContactBtn">
                                            <i class="bi bi-check-lg me-1"></i> 연락처 저장
                                        </button>
                                    </div>
                                </div>

                                <!-- 탭 2: 일일보고 -->
                                <div class="tab-pane fade p-4" id="tab-daily-report" role="tabpanel">
                                    <div class="d-flex align-items-center mb-3">
                                        <h5 class="fw-bold m-0">일일업무보고</h5>
                                        <span id="reportUpdateDate" class="badge bg-light text-muted border ms-2">-</span>
                                    </div>
                                    <div class="table-responsive">
                                        <table class="table table-bordered text-center align-middle">
                                            <thead class="table-light">
                                                <tr>
                                                    <th></th>
                                                    <th>일반 취업</th>
                                                    <th>알선 취업</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <tr>
                                                    <td class="fw-bold table-light">금일</td>
                                                    <td><input type="number" class="form-control text-center report-input" id="todayEmployment" value="0"></td>
                                                    <td><input type="number" class="form-control text-center report-input" id="todayPlacement" value="0"></td>
                                                </tr>
                                                <tr>
                                                    <td class="fw-bold table-light">금주</td>
                                                    <td><input type="number" class="form-control text-center report-input" id="weekEmployment" value="0"></td>
                                                    <td><input type="number" class="form-control text-center report-input" id="weekPlacement" value="0"></td>
                                                </tr>
                                                <tr>
                                                    <td class="fw-bold table-light">금월</td>
                                                    <td><input type="number" class="form-control text-center report-input" id="monthEmployment" value="0"></td>
                                                    <td><input type="number" class="form-control text-center report-input" id="monthPlacement" value="0"></td>
                                                </tr>
                                                <tr>
                                                    <td class="fw-bold table-light">금년</td>
                                                    <td><input type="number" class="form-control text-center report-input" id="yearEmployment" value="0"></td>
                                                    <td><input type="number" class="form-control text-center report-input" id="yearPlacement" value="0"></td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                    <div class="text-end">
                                        <button type="button" class="btn btn-primary" id="saveDailyReportBtn">
                                            <i class="bi bi-check-lg me-1"></i> 일일보고 저장
                                        </button>
                                    </div>
                                </div>

                                <!-- 탭 3: 보안 설정 (비밀번호 변경) -->
                                <div class="tab-pane fade p-4" id="tab-security" role="tabpanel">
                                    <h5 class="fw-bold mb-3">비밀번호 변경</h5>
                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="mb-3">
                                                <label for="newPW" class="form-label">새 비밀번호</label>
                                                <input type="password" class="form-control" id="newPW" placeholder="새 비밀번호 입력">
                                                <small class="text-muted">영문 대소문자, 특수문자(!@#$%^&*), 숫자 포함 6자 이상</small>
                                            </div>
                                            <div class="mb-3">
                                                <label for="newPWConfirm" class="form-label">새 비밀번호 확인</label>
                                                <input type="password" class="form-control" id="newPWConfirm" placeholder="비밀번호 확인">
                                                <div id="pwValidationResult" class="mt-1"></div>
                                            </div>
                                            <button type="button" class="btn btn-primary" id="changePasswordBtn">
                                                <i class="bi bi-shield-check me-1"></i> 비밀번호 변경
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
            <!--end::Main content-->
        </div>
        <!--end::App Content-->
    </main>
    <!--end::App Main-->
    <!--end:::App main content-->

    <!--begin::Footer-->
    <mytag:footer/>
    <!--end::Footer-->

</div>

</body>
<!--begin::Script-->
<!--begin::Third Party Plugin(OverlayScrollbars)-->
<script
        src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es6.min.js"
        integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ="
        crossorigin="anonymous"
></script>
<!--end::Third Party Plugin(OverlayScrollbars)--><!--begin::Required Plugin(popperjs for Bootstrap 5)-->
<script
        src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r"
        crossorigin="anonymous"
></script>
<!--end::Required Plugin(popperjs for Bootstrap 5)--><!--begin::Required Plugin(Bootstrap 5)-->
<script
        src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.min.js"
        integrity="sha384-0pUGZvbkm6XF6gxjEnlmuGrJXVbNuzT9qBBavbLwCsOGabYfZo0T0to5eqruptLy"
        crossorigin="anonymous"
></script>
<!--end::Required Plugin(Bootstrap 5)--><!--begin::Required Plugin(AdminLTE)-->
<script src="js/adminlte.js"></script>
<!--begin::Script-->
<!--begin::OverlayScrollbars Configure-->
<script>
    const SELECTOR_SIDEBAR_WRAPPER = '.sidebar-wrapper';
    const Default = {
        scrollbarTheme: 'os-theme-light',
        scrollbarAutoHide: 'leave',
        scrollbarClickScroll: true,
    };
    document.addEventListener('DOMContentLoaded', function () {
        const sidebarWrapper = document.querySelector(SELECTOR_SIDEBAR_WRAPPER);
        if (sidebarWrapper && typeof OverlayScrollbarsGlobal?.OverlayScrollbars !== 'undefined') {
            OverlayScrollbarsGlobal.OverlayScrollbars(sidebarWrapper, {
                scrollbars: {
                    theme: Default.scrollbarTheme,
                    autoHide: Default.scrollbarAutoHide,
                    clickScroll: Default.scrollbarClickScroll,
                },
            });
        }
    });
</script>
<!--end::OverlayScrollbars Configure-->

<!-- OPTIONAL SCRIPTS -->
<!-- sortablejs -->
<script>
    const connectedSortables = document.querySelectorAll('.connectedSortable');
    connectedSortables.forEach((connectedSortable) => {
        let sortable = new Sortable(connectedSortable, {
            group: 'shared',
            handle: '.card-header',
        });
    });

    const cardHeaders = document.querySelectorAll('.connectedSortable .card-header');
    cardHeaders.forEach((cardHeader) => {
        cardHeader.style.cursor = 'move';
    });
</script>
<!-- apexcharts -->
<script
        src="https://cdn.jsdelivr.net/npm/apexcharts@3.37.1/dist/apexcharts.min.js"
        integrity="sha256-+vh8GkaU7C9/wbSLIcwq82tQ2wTf44aOHA8HlBMwRI8="
        crossorigin="anonymous"
></script>
</html>

