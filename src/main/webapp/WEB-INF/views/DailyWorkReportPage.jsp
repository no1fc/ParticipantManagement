<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 25. 3. 13.
  Time: 오후 4:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <mytag:Logo/>
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
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css" />
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
    <link rel="stylesheet" href="//code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://code.jquery.com/ui/1.13.2/jquery-ui.min.js"></script>

    <!-- CSS 추가 -->
    <link rel="stylesheet" href="/css/participantCss/dailyWorkReportCss_0.0.1.css">

    <!-- datepicker CSS JS -->
    <!-- mouse pointer 모양 bootstrap 5 -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <!-- Bootstrap Datepicker 로드 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/js/bootstrap-datepicker.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/locales/bootstrap-datepicker.ko.min.js" integrity="sha512-L4qpL1ZotXZLLe8Oo0ZyHrj/SweV7CieswUODAAPN/tnqN3PA1P+4qPu5vIryNor6HQ5o22NujIcAZIfyVXwbQ==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<%--    <script src="/js/datepickerJS_0.0.1.js"></script>--%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/css/bootstrap-datepicker.min.css">
    <link rel="stylesheet" href="/css/participantCss/datepicker_0.0.1.css">

    <!-- Chart.js CDN -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

    <!-- DailyWorkReportPageJS_0.0.1.js -->
    <script src="/js/DailyWorkReportPageJS_0.0.1.js"></script>

    <!-- Employment Rate Charts JS -->
    <script src="/js/employmentRateCharts.js"></script>
    <link rel="stylesheet" href="/css/participantCss/employmentRateCharts_0.0.1.css">

    <link rel="stylesheet" href="/css/participantCss/dailyWorkReportUi_0.0.2.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin:::App Gnb-->
    <mytag:gnb gnb_main_header="실적관리" gnb_sub_header="일일업무보고"/>
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
                    <form action="/downloadExcel.login" id="downloadForm" method="POST">
                        <div class="row">
                            <div class="row col-md-12 mt-2 mb-2 p-0 align-items-center justify-content-end">
                                <div class="text-center report-date-box">
                                    <label class="type-label report-label">일일보고 일자</label>
                                    <input type="text" id="dailyReportDate"
                                           class="datepicker_on dailyReportDate form-control ms-auto me-auto"
                                           name="dailyUpdateStatusDate"
                                    >
                                </div>
                                <div class="col-md-1  p-0">
                                    <button type="button" id="dailyReportUpdateBtn" class="dailyReportUpdateBtn">
                                        <i class="bi bi-save-fill"></i>
                                        <span class="dailyReportUpdateText">일일보고 저장</span>
                                    </button>
                                </div>

                                <div class="text-center report-date-box">
                                    <label class="type-label report-label">마지막 다운로드</label>
                                    <input type="text" id="lastSavedDate"
                                           class="lastSavedDate form-control ms-auto me-auto"
                                           value="${users==null? 0: users[0].endUpdateStatus}"
                                           disabled
                                    >
                                </div>
                                <div class="col-md-1  p-0">
                                    <button type="button" id="downloadBtn" class="excel-download-btn">
                                        <i class="bi bi-file-earmark-excel-fill"></i>
                                        <span class="excel-download-text">엑셀 다운로드</span>
                                    </button>
                                    <input type="hidden" name="branch" value="${JOBMOA_LOGIN_DATA.memberBranch}">
                                    <input type="hidden" id="year" name="year" value="">
                                </div>
                            </div>

                            <!-- 등급별 퍼센티지(%) -->
                            <div class="employment-rate">
                            </div>

                            <div class="row col-md-12 mt-2 p-0 d-flex align-items-start justify-content-end">
                                <div class="row col-md-2">
                                    <div class="personnel-container" id="sortableList">
                                        <%--<div class="personnel-header">
                                            금일 배정인원
                                        </div>
                                        <div class="personnel-content">
                                            <div>
                                                <label for="branchType1" class="type-label">1유형</label>
                                                <input type="number" id="branchType1" name="branchType1" class="type-input"
                                                       value="${branchType1}">
                                            </div>
                                            <div>
                                                <label for="branchType2" class="type-label">2유형</label>
                                                <input type="number" id="branchType2" name="branchType2" class="type-input"
                                                       value="${branchType2}">
                                            </div>
                                        </div>--%>
                                        <div class="personnel-header">
                                            상담사 엑셀 순서
                                            <span id="position-save-btn" class="position-save-btn btn">순서 저장</span>
                                        </div>
                                        <c:choose>
                                            <c:when test="${empty users}">
                                                <div class="user-item">
                                                    <span>선택할 전담자가 없습니다.</span>
                                                </div>
                                            </c:when>
                                            <c:when test="${not empty users}">
                                                <c:forEach var="user" items="${users}" varStatus="status">
                                                    <!-- 기존 user-item div들이 여기에 반복됨 -->
                                                    <div class="user-item row col-md-12 p-0" data-id="${user.userID}">
                                                        <div class="drag-handle d-flex align-items-center">
                                                            <span class="verticalEllipsis">&#8942;</span>
                                                            <span class="position-input" data-value="${status.count}">${status.count}</span>
                                                            <label for="checkbox_${user.userID}" class="toggle"
                                                                   class="toggle min-width-75">
                                                                    ${user.counselorName}
                                                            </label>
                                                            <span class="checkbox-wrapper-3">
                                                            <input type="checkbox" id="checkbox_${user.userID}" class="checkbox default-checkbox counselorAccount" name="userIds" value="${user.userID}" ${user.memberDailyWorkDiaryIssuance?"checked":""} />
                                                            <label for="checkbox_${user.userID}" class="toggle"><span></span></label>
                                                        </span>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </c:when>
                                        </c:choose>
                                    </div>
                                </div>

                                <div class="row col-md-10 ">
                                    <div class="col-md-12 ps-1 pe-0">
                                        <table id="status-table" class="status-table">
                                            <thead id="status-thead" class="status-thead">
                                            <tr class="status-thead-tr">
                                                <th scope="col" colspan="2" rowspan="2" class="text-center status-thead-th-name">
                                                    <span>상담사</span>
                                                </th>
                                                <th scope="col" colspan="2" class="text-center">
                                                    상담사별 금일 배정인원
                                                </th>
                                                <th scope="col" colspan="2" class="text-center">
                                                    금일 누적 실적
                                                </th>
                                                <th scope="col" colspan="2" class="text-center">
                                                    금주 누적 실적
                                                </th>
                                                <th scope="col" colspan="2" class="text-center">
                                                    금월 누적 실적
                                                </th>
                                                <th scope="col" colspan="2" class="text-center">
                                                    금년 누적 실적
                                                </th>
                                            </tr>
                                            <tr class="status-thead-tr">
                                                <!-- 배정 인원 th table -->
                                                <th scope="col" class="text-center">
                                                    1유형
                                                </th>
                                                <th scope="col" class="text-center">
                                                    2유형
                                                </th>
                                                <!-- 금일,금주,금월,금년 누적 실적 th table -->
                                                <c:forEach begin="0" end="3" varStatus="i">
                                                    <th scope="col" class="text-center">
                                                        일반 취업
                                                    </th>
                                                    <th scope="col" class="text-center">
                                                        알선 취업
                                                    </th>
                                                </c:forEach>
                                            </tr>
                                            </thead>
                                            <tbody id="status-tbody" class="status-tbody">
                                            <c:forEach var="user" items="${users}">
                                                <tr class="status-tbody-tr" data-id="${user.userID}">
                                                    <!-- 상담사 성명 -->
                                                    <th scope="col" colspan="2" class="text-center">
                                                        <span class="status-tbody-span">${user.counselorName}</span>
                                                    </th>
                                                    <!-- 상담사 성명 -->
                                                    <!-- 금일 누적 1유형 th table 시작 -->
                                                    <th scope="col" class="text-center">
                                                        <input type="number" id="todayPersonnelOneType_${user.userID}" name="memberTodayPersonnelOneTypes" class="todayPersonnelOne form-control w-75 ms-auto me-auto"
                                                               value="${user.todayPersonnelOneType}" min="0">
                                                    </th>
                                                    <th scope="col" class="text-center">
                                                        <input type="number" id="todayPersonnelTwoType_${user.userID}" name="memberTodayPersonnelTwoTypes" class="todayPersonnelTwo form-control w-75 ms-auto me-auto"
                                                               value="${user.todayPersonnelTwoType}" min="0">
                                                    </th>
                                                    <!-- 금일 누적 1유형 th table 끝 -->
                                                    <!-- 금일 누적 실적 th table 시작 -->
                                                    <th scope="col" class="text-center">
                                                        <input type="number" id="todayEmployment_${user.userID}" name="memberTodayEmployments" class="todayEmployment form-control w-75 ms-auto me-auto"
                                                               value="${user.memberTodayEmployment}" min="0">
                                                    </th>
                                                    <th scope="col" class="text-center">
                                                        <input type="number" id="todayPlacement_${user.userID}" name="memberTodayPlacements" class="todayPlacement form-control w-75 ms-auto me-auto"
                                                               value="${user.memberTodayPlacement}" min="0">
                                                    </th>
                                                    <!-- 금일 누적 실적 th table 끝 -->
                                                    <!-- 금주 누적 실적 th table 시작 -->
                                                    <th scope="col" class="text-center">
                                                        <input type="number" id="toWeekEmployment_${user.userID}" name="memberToWeekEmployments" class="toWeekEmployment form-control w-75 ms-auto me-auto"
                                                               value="${user.memberToWeekEmployment}" min="0">
                                                    </th>
                                                    <th scope="col" class="text-center">
                                                        <input type="number" id="toWeekPlacement_${user.userID}" name="memberToWeekPlacements" class="toWeekPlacement form-control w-75 ms-auto me-auto"
                                                               value="${user.memberToWeekPlacement}" min="0">
                                                    </th>
                                                    <!-- 금주 누적 실적 th table 끝 -->
                                                    <!-- 금월 누적 실적 th table 시작 -->
                                                    <th scope="col" class="text-center">
                                                        <input type="number" id="toMonthEmployment_${user.userID}" name="memberToMonthEmployments" class="toMonthEmployment form-control w-75 ms-auto me-auto"
                                                               value="${user.memberToMonthEmployment}" min="0">
                                                    </th>
                                                    <th scope="col" class="text-center">
                                                        <input type="number" id="toMonthPlacement_${user.userID}" name="memberToMonthPlacements" class="toMonthPlacement form-control w-75 ms-auto me-auto"
                                                               value="${user.memberToMonthPlacement}" min="0">
                                                    </th>
                                                    <!-- 금월 누적 실적 th table 끝 -->
                                                    <!-- 금년 누적 실적 th table 시작 -->
                                                    <th scope="col" class="text-center">
                                                        <input type="number" id="toYearEmployment_${user.userID}" name="memberToYearEmployments" class="toYearEmployment form-control w-75 ms-auto me-auto"
                                                               value="${user.memberToYearEmployment}" min="0">
                                                    </th>
                                                    <th scope="col" class="text-center">
                                                        <input type="number" id="toYearPlacement_${user.userID}" name="memberToYearPlacements" class="toYearPlacement form-control w-75 ms-auto me-auto"
                                                               value="${user.memberToYearPlacement}" min="0">
                                                    </th>
                                                    <!-- 금년 누적 실적 th table 끝 -->
                                                </tr>
                                            </c:forEach>

                                            </tbody>
                                        </table>
                                    </div>

                                </div>

                            </div>
                        </div>

                    </form>
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

<!-- JavaScript 추가 순서지정 -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/Sortable/1.14.0/Sortable.min.js"></script>
<script>
    $(document).ready(function() {
        // 실적 배정인원 초기화
        initializeValues();

        const $userItem = $('.user-item')

        // 체크박스 상태 변경 시 실행되는 함수도 수정
        $userItem.on('change', function() {
            const changeVal = $(this).data('id');
            const $targetRow = $('.status-tbody-tr[data-id="'+changeVal+'"]');
            const $inputs = $targetRow.find('input');

            $inputs.prop('disabled', !$inputs.is(':disabled'));
        });

        $userItem.each(function () {
            const changeVal = $(this).data('id');
            const $targetRow = $('.status-tbody-tr[data-id="'+changeVal+'"]');
            const $inputs = $targetRow.find('input');
            const defaultCheckbox = $(this).find('.default-checkbox')

            let flag = defaultCheckbox.is(':checked');

            // 처음 시작시 checkbox에 따라 선택
            if (!flag){
                $inputs.prop('disabled', !$inputs.is(':disabled'));
            }
        })


        // Excel 다운로드 버튼
        $('#downloadBtn').on('click',()=>{
            $('#downloadForm').submit();
        });

        // sortable 초기화
        $("#sortableList").sortable({
            items: ".user-item",             // 정렬 대상
            handle: ".drag-handle",          // 드래그 핸들
            axis: "y",                       // 세로 방향으로만 이동 가능
            cursor: "move",                  // 드래그 시 커서 모양
            opacity: 0.6,                    // 드래그 중인 항목의 투명도
            update: function(event, ui) {    // 정렬이 완료된 후 실행
                updateOrder();
                updateTbodyOrder();
            }
        });

        /* datepicker JS Start */
        const datepicker_on = $('.datepicker_on');
        let dateValue = "";

        datepicker_on.on('keyup', function () {
            dateValue = $(this).val();
            if(dateValue.length == 7) {
                dateValue+='-'
            }
            else if(dateValue.length == 4) {
                dateValue+='-'
            }
            $(this).val(dateValue);
        });

        datepicker_on.on('change', function () {
            dateValue = $(this).val();
            const datePattern = /^\d{4}-\d{2}-\d{2}$/;
            console.log(dateValue);
            if (!datePattern.test(dateValue)) {
                $(this).val(''); // 날짜형식이 아니면 삭제
                console.log('Invalid date format, value cleared.');
            } else {
                console.log('datepicker_on');
                //console.log(dateValue);
            }
        });

        default_datepicker(datepicker_on);
        /* datepicker JS End */

        /* dailyReportUpdate JS Start */
        // 일일보고 저장 기능 시작
        $("#dailyReportUpdateBtn").on('click',
            async () => {
                // 금일 배정 전체 인원 1유형
                const todayPersonnelOneType = $('#branchType1');
                const todayPersonnelOneTypeVal = todayPersonnelOneType.val();
                // 금일 배정 전체 인원 2유형
                const todayPersonnelTwoType = $('#branchType2');
                const todayPersonnelTwoTypeVal = todayPersonnelTwoType.val();

                // 일일보고저장일자
                const dailyReportDate = $('#dailyReportDate');
                const dailyReportDateVal = dailyReportDate.val();

                // 상담사 계정
                const counselorAccount = $('.counselorAccount');
                const counselorAccountArray = classToArray(counselorAccount);

                // 상담사별 금일 배정인원
                // 1유형
                const todayPersonnelOne = $('.todayPersonnelOne');
                const todayPersonnelOneArray = classToArray(todayPersonnelOne);
                // 2유형
                const todayPersonnelTwo = $('.todayPersonnelTwo');
                const todayPersonnelTwoArray = classToArray(todayPersonnelTwo);

                // 금일 누적 실적
                // 일반 취업
                const todayEmployment = $('.todayEmployment');
                const todayEmploymentArray = classToArray(todayEmployment);
                // 알선 취업
                const todayPlacement = $('.todayPlacement');
                const todayPlacementArray = classToArray(todayPlacement);

                // 금주 누적 실적
                // 일반 취업
                const toWeekEmployment = $('.toWeekEmployment');
                const toWeekEmploymentArray = classToArray(toWeekEmployment);
                // 알선 취업
                const toWeekPlacement = $('.toWeekPlacement');
                const toWeekPlacementArray = classToArray(toWeekPlacement);

                // 금월 누적 실적
                // 일반 취업
                const toMonthEmployment = $('.toMonthEmployment');
                const toMonthEmploymentArray = classToArray(toMonthEmployment);
                // 알선 취업
                const toMonthPlacement = $('.toMonthPlacement');
                const toMonthPlacementArray = classToArray(toMonthPlacement);

                // 금년 누적 실적
                // 일반 실적
                const toYearEmployment = $('.toYearEmployment');
                const toYearEmploymentArray =  classToArray(toYearEmployment);
                // 알선 취업
                const toYearPlacement = $('.toYearPlacement');
                const toYearPlacementArray = classToArray(toYearPlacement);

                try{
                    const updateFetch = await fetch("/dashboard/branchReportUpdate.login", {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'charset': 'utf-8'
                        },
                        body: JSON.stringify({
                            userIds: counselorAccountArray,
                            branchType1: todayPersonnelOneTypeVal,
                            branchType2: todayPersonnelTwoTypeVal,
                            todayPersonnelOneTypes: todayPersonnelOneArray,
                            todayPersonnelTwoTypes: todayPersonnelTwoArray,
                            memberTodayEmployments: todayEmploymentArray,
                            memberTodayPlacements: todayPlacementArray,
                            memberToWeekEmployments: toWeekEmploymentArray,
                            memberToWeekPlacements:toWeekPlacementArray,
                            memberToMonthEmployments: toMonthEmploymentArray,
                            memberToMonthPlacements: toMonthPlacementArray,
                            memberToYearEmployments: toYearEmploymentArray,
                            memberToYearPlacements: toYearPlacementArray,
                            dailyUpdateStatusDate: dailyReportDateVal
                        })
                    })
                    const result = await updateFetch.json();
                    console.log("일일보고 저장 완료:", result);
                    alert(result.message);

                }
                catch (e){
                    console.log("일일보고 저장 실패:", e);
                    alert('일일보고 저장을 실패했습니다.:'+result.message);
                }




            });// 일일보고 저장 기능 끝

        /* dailyReportUpdate JS End */

        /* Position-save-btn JS Start */
        // 상담사 순서 및 저장 여부 변경
        $('#position-save-btn').on('click',async () => {
            const counselorAccount = $('.counselorAccount');
            const counselorAccountArray = classToArray(counselorAccount);
            const counselorAccountChecked = classToArray_CheckBox(counselorAccount);
            console.log(counselorAccountArray)
            console.log(counselorAccountChecked)

            try{
                const updateFetch = await fetch("/dashboard/memberOrderUpdate.login", {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'charset': 'utf-8'
                    },
                    body: JSON.stringify({
                        userIds: counselorAccountArray,
                        memberDailyWorkDiaryIssuances: counselorAccountChecked
                    })
                })
                const result = await updateFetch.json();
                console.log("상담사 순서 저장 완료:", result);
                alert(result.message);

            }
            catch (e){
                console.log("상담사 순서 저장 실패:", e);
                alert('상담사 순서 저장을 실패했습니다.:'+result.message);
            }
        })


        /* Position-save-btn JS End */

        /* Chart 생성 Start */
        // class 속성 배열 변환 함수
        function classToArray(classSelector) {
            const elements = $(classSelector);
            const array = [];
            elements.each(function() {
                array.push($(this).val());
            });
            return array;
        }

        function classToArray_CheckBox(classSelector) {
            const elements = $(classSelector);
            const array = [];
            elements.each(function() {
                array.push($(this).is(':checked'));
            });
            return array;
        }

        // Chart.js CDN 로드 확인 후 차트 초기화
        if (typeof Chart === 'undefined') {
            console.error('Chart.js가 로드되지 않았습니다.');
            return;
        }

        // 그래프 컨테이너 생성
        const employmentRateContainer = $('.employment-rate');

        const chartHTML = `
        <div class="employment-charts-wrapper">
            <div class="charts-container">
                <div class="chart-box">
                    <h4 class="chart-title">📈 총점 등급 취지 분석</h4>
                    <p class="chart-subtitle">지점 총점은 총 4개의 등급으로 분류</p>
                    <div class="chart-canvas-wrapper">
                        <canvas id="totalScoreChart"></canvas>
                    </div>
                </div>
                <div class="chart-box">
                    <h4 class="chart-title">📊 취업률 등급 취지 분석</h4>
                    <p class="chart-subtitle">지점 취업률(%)은 총 4개의 등급으로 분류</p>
                    <div class="chart-canvas-wrapper">
                        <canvas id="employmentRateChart"></canvas>
                    </div>
                </div>
                <div class="chart-box">
                    <h4 class="chart-title">🏆 알선취업률 등급 취지 분석</h4>
                    <p class="chart-subtitle">지점 알선 취업률(%)은 총 4개의 등급으로 분류</p>
                    <div class="chart-canvas-wrapper">
                        <canvas id="placementRateChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    `;

        employmentRateContainer.html(chartHTML);

        // 등급 기준 데이터
        const employmentGrades = { A: 69.8, B: 63.5, C: 59.5, D: 55.6 };
        const placementGrades = { A: 15.2, B: 8.2, C: 4.9, D: 3 };
        const totalScoreGrades = { A: 48.7, B: 47, C: 45.8, D: 44 };

        // 총점 그래프 생성
        createSingleBarChart('totalScoreChart', '지점 총점', ${rateScore.totalScore}, totalScoreGrades, 50, 40, '');

        // 취업률 그래프 생성
        createSingleBarChart('employmentRateChart', '지점 취업률', ${rateScore.employmentRate}, employmentGrades, 80, 40, '%');

        // 알선취업률 그래프 생성
        createSingleBarChart('placementRateChart', '지점 알선률', ${rateScore.placementRate}, placementGrades, 30, 0, '%');

        /* Chart 생성 End */
  });
</script>

</html>
