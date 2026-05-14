<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 지점 일정 통합 조회</title>
    <mytag:Logo/>

    <!--begin::Primary Meta Tags-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <!--end::Primary Meta Tags-->

    <!--begin::Fonts-->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.0.12/index.css"
          integrity="sha256-tXJfXfp6Ewt1ilPzLDtQnJV4hclT9XuaZUKyUvmyr+Q="
          crossorigin="anonymous" />
    <!--end::Fonts-->

    <!--begin::Third Party Plugin(OverlayScrollbars)-->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/styles/overlayscrollbars.min.css"
          integrity="sha256-tZHrRjVqNSRyWg2wbppGnT833E/Ys0DHWGwT04GiqQg="
          crossorigin="anonymous" />
    <!--end::Third Party Plugin(OverlayScrollbars)-->

    <!--begin::Third Party Plugin(Bootstrap Icons)-->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
          integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI="
          crossorigin="anonymous" />
    <!--end::Third Party Plugin(Bootstrap Icons)-->

    <!--begin::Required Plugin(AdminLTE)-->
    <link rel="stylesheet" href="/css/adminlte.css" />
    <!--end::Required Plugin(AdminLTE)-->

    <!--begin::FullCalendar CSS-->
    <link href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.css" rel="stylesheet" />
    <!--end::FullCalendar CSS-->

    <!--begin::SweetAlert2 CSS-->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css" />
    <!--end::SweetAlert2 CSS-->

    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.1.js"
            integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4="
            crossorigin="anonymous"></script>

    <!--begin::Schedule Manager CSS-->
    <link rel="stylesheet" href="/css/scheduleCss/scheduleManager_0.0.1.css" />
    <!--end::Schedule Manager CSS-->
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <mytag:gnb gnb_main_header="상담관리" gnb_sub_header="지점 일정 통합 조회"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid py-4 pt-3 pb-0">

                <!-- Hidden fields for JS -->
                <input type="hidden" id="counselorId" value="${counselorId}">
                <input type="hidden" id="counselorName" value="${counselorName}">
                <input type="hidden" id="branch" value="${branch}">

                <!-- Page Actions -->
                <div class="page-actions">
                    <h4><i class="bi bi-calendar3 me-2"></i>지점 일정 통합 조회</h4>
                    <div>
                        <span class="badge bg-info text-dark me-2" style="font-size:0.8rem;">
                            <i class="bi bi-geo-alt me-1"></i>${branch}
                        </span>
                        <select class="form-select form-select-sm d-inline-block" style="width:auto;" id="viewSelect">
                            <option value="dayGridMonth">월간</option>
                            <option value="timeGridWeek">주간</option>
                            <option value="timeGridDay">일간</option>
                        </select>
                    </div>
                </div>

                <!-- Stats Cards -->
                <div class="stats-row">
                    <div class="stat-card">
                        <div class="stat-number" id="statCounselorCount">-</div>
                        <div class="stat-label">상담사 수</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="statWeeklyCount">-</div>
                        <div class="stat-label">이번 주 일정</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="statTodayCount">-</div>
                        <div class="stat-label">오늘 일정</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number" id="statMonthlyCount">-</div>
                        <div class="stat-label">이번 달 일정</div>
                    </div>
                </div>

                <!-- Filter Bar -->
                <div class="filter-bar">
                    <div class="filter-label"><i class="bi bi-funnel me-1"></i>상담사 필터</div>
                    <div class="counselor-chips" id="counselorChips"></div>
                    <button class="btn btn-outline-secondary btn-sm" id="btnSelectAll">전체 선택</button>
                </div>

                <!-- Calendar -->
                <div id="calendar"></div>

            </div>
        </div>
    </main>
</div>
<!--end::App Wrapper-->

<!-- Detail Popup -->
<div class="detail-popup" id="detailPopup">
    <div class="popup-header">
        <strong id="popupTitle"></strong>
        <button class="btn btn-sm p-0" id="closePopup"><i class="bi bi-x-lg"></i></button>
    </div>
    <div class="popup-body">
        <div class="popup-row"><div class="popup-label">상담사</div><div class="popup-value" id="popupCounselor"></div></div>
        <div class="popup-row"><div class="popup-label">날짜</div><div class="popup-value" id="popupDate"></div></div>
        <div class="popup-row"><div class="popup-label">시간</div><div class="popup-value" id="popupTime"></div></div>
        <div class="popup-row"><div class="popup-label">참여자</div><div class="popup-value" id="popupParticipant"></div></div>
        <div class="popup-row"><div class="popup-label">유형</div><div class="popup-value" id="popupType"></div></div>
        <div class="popup-row"><div class="popup-label">메모</div><div class="popup-value" id="popupMemo"></div></div>
    </div>
</div>

<!--begin::Third Party Plugin(OverlayScrollbars)-->
<script src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es6.min.js"
        integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ="
        crossorigin="anonymous"></script>
<!--end::Third Party Plugin(OverlayScrollbars)-->

<!--begin::Required Plugin(popperjs for Bootstrap 5)-->
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r"
        crossorigin="anonymous"></script>
<!--end::Required Plugin(popperjs for Bootstrap 5)-->

<!--begin::Required Plugin(Bootstrap 5)-->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.min.js"
        integrity="sha384-0pUGZvbkm6XF6gxjEnlmuGrJXVbNuzT9qBBavbLwCsOGabYfZo0T0to5eqruptLy"
        crossorigin="anonymous"></script>
<!--end::Required Plugin(Bootstrap 5)-->

<!--begin::Required Plugin(AdminLTE)-->
<script src="/js/adminlte.js"></script>
<!--end::Required Plugin(AdminLTE)-->

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

<!--begin::FullCalendar JS-->
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.js"></script>
<!--end::FullCalendar JS-->

<!--begin::SweetAlert2 JS-->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.all.min.js"></script>
<!--end::SweetAlert2 JS-->

<!--begin::Schedule Manager JS-->
<script src="/js/scheduleManager_0.0.2.js"></script>
<!--end::Schedule Manager JS-->

</body>
</html>
