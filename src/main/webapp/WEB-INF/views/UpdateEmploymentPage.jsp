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
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css" />
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
    <!-- apexcharts -->
    <script
            src="https://cdn.jsdelivr.net/npm/apexcharts@3.37.1/dist/apexcharts.min.js"
            integrity="sha256-+vh8GkaU7C9/wbSLIcwq82tQ2wTf44aOHA8HlBMwRI8="
            crossorigin="anonymous"
    ></script>

    <!-- sortablejs -->
    <script
            src="https://cdn.jsdelivr.net/npm/sortablejs@1.15.0/Sortable.min.js"
            integrity="sha256-ipiJrswvAR4VAx/th+6zWsdeYmVae0iJuiR+6OqHJHQ="
            crossorigin="anonymous"
    ></script>
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
    <!-- mouse pointer 모양 bootstrap 5 -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <!--end::Required Plugin(Bootstrap 5)--><!--begin::Required Plugin(AdminLTE)-->
    <script src="/js/adminlte.js"></script>
    <!--end::Required Plugin(AdminLTE)-->

    <!-- datepicker CSS JS -->
    <!-- Bootstrap Datepicker 로드 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/js/bootstrap-datepicker.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/locales/bootstrap-datepicker.ko.min.js" integrity="sha512-L4qpL1ZotXZLLe8Oo0ZyHrj/SweV7CieswUODAAPN/tnqN3PA1P+4qPu5vIryNor6HQ5o22NujIcAZIfyVXwbQ==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="/js/datepickerJS_0.0.1.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/css/bootstrap-datepicker.min.css">
    <link rel="stylesheet" href="/css/participantCss/datepicker_0.0.1.css">

    <!-- recommend JS -->
    <script src="/js/recommendJS_0.0.1.js"></script>

    <!-- selectOption JS -->
    <script src="/js/selectOptionJS_0.0.1.js"></script>

    <!-- inputLimits -->
    <script src="/js/InputLimits_0.0.1.js"></script>

    <!-- sweetalert2 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
    <script src="/js/sweetAlert_0.0.1.js"></script>


</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin:::App Gnb-->
    <mytag:gnb gnb_main_header="상담관리" gnb_sub_header="${employment.employmentPartic} 참여자 취업정보 수정"/>
    <!--end:::App Gnb-->
    <!--begin:::App main content-->
    <!--begin::App Main-->
    <main class="app-main">
        <!--begin::App Content-->
        <div class="app-content">
            <!--begin::Main content-->
            <!-- 필요 본문 내용은 이쪽에 만들어 주시면 됩니다. -->
            <div class="container-fluid">
                <div class="row pt-3">
                    <div class="col-md-12">
                        <form id="newParticipantsForm" name="newParticipantsForm" method="POST" action="/updateemployment.login" class="form-horizontal">
                            <%-- 검색 필터 hiddenInput tag --%>
                            <mytag:searchHiddenInput/>

                            <%-- 참여자 수정 버튼 시작 --%>
                            <div class="row pb-2 mb-1">
                                <div class="col-1 text-start">
                                    <div class="w-auto">
                                        <label for="counselProgress" class="form-label">진행단계</label>
                                        <select class="form-select" aria-label="Default select example" id="counselProgress" name="counselProgress">
                                            <option selected value="IAP 전">IAP 전</option>
                                            <option value="IAP 후">IAP 후</option>
                                            <option value="미고보">미고보</option>
                                            <option value="고보일반">고보일반</option>
                                            <option value="등록창업">등록창업</option>
                                            <option value="미등록창업">미등록창업</option>
                                            <option value="미취업사후관리">미취업사후관리</option>
                                            <option value="미취업사후종료">미취업사후종료</option>
                                            <option value="유예">유예</option>
                                            <option value="취소">취소</option>
                                            <option value="이관">이관</option>
                                            <option value="중단">중단</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-11 text-end">
                                    <button type="button" class="btn btn-primary" id="btn_check">
                                        참여자 수정
                                    </button>
                                </div>
                            </div>
                            <%-- 참여자 수정 버튼 끝 --%>
                            <mytag:ParticipantEmployment employment="${employment}"/>
                        </form>
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

<script>
    $(document).ready(function () {

        <%-- form 전달 시작 --%>
        const btn_check = $("#btn_check") // 전송 버튼을 추가
        btn_check.on("click", function () {

            //취창업일
            const employmentStartDate = $("#employmentStartDate").val();
            //취창업처리일
            const employmentProcDate = $("#employmentProcDate").val();
            //퇴사일
            const employmentQuit = $("#employmentQuit").val();
            //취업유형
            const employmentEmpType = $("#employmentEmpType").val();
            //취업처
            const employmentLoyer = $("#employmentLoyer").val();
            //임금
            const employmentSalary = $("#employmentSalary").val();
            //취업인센티브_구분
            const employmentIncentive = $("#employmentIncentive").val();

            //취창업일이 비어있고 임금 OR 취업인센티브_구분이 비어있다면 함수를 내보낸다.
            if(!employmentStartDate.length > 0){
                //임금이 작성되어 있거나
                flag = employmentSalary.length > 0;
                //취업인센티브_구분이 선택되어 있다면
                flag = flag || employmentIncentive.length > 0;

                flag = flag || employmentProcDate.length > 0;
                flag = flag || employmentQuit.length > 0;
                flag = flag || employmentEmpType.length > 0;
                flag = flag || employmentLoyer.length > 0;
                if(flag){
                    alertDefaultInfo("취창업일을 입력해주세요.","");
                    return;
                }
            }
            else {
                //임금이 작성되어 있거나
                if (!employmentSalary.length > 0){
                    alertDefaultInfo("임금은 필수 입력입니다.","");
                    return;
                }
                //취업인센티브_구분이 선택되어 있다면
                if (!employmentIncentive.length > 0){
                    alertDefaultInfo("취업인센티브_구분은 필수 입력입니다.","");
                    return;
                }
            }


            const form = $("#newParticipantsForm");
            form.submit();
        });
        <%-- form 전달 끝 --%>

        <%-- 각 ID 별 변수 --%>
        //취업유형
        const employmentEmpType = $("#employmentEmpType");
        //취업인센티브
        const employmentIncentive = $("#employmentIncentive");
        //일경험
        const employmentJobcat = $("#employmentJobcat");
        //진행단계
        const counselProgress = $("#counselProgress");

        <%-- 목록 내용 변경 시작 --%>
        //진행단계
        selectOption(counselProgress,"${counselProgress}");

        //취업인센티브_구분
        selectOption(employmentIncentive,"${employment.employmentIncentive}");

        //일경험 구분
        selectOption(employmentJobcat,"${employment.employmentJobcat}");
        <%-- 목록 내용 변경 끝 --%>

        <%-- 취업유형 변경 시작 --%>
        changeSelect(counselProgress, employmentEmpType,"${counselProgress}");
        //취업유형
        selectOption(employmentEmpType,"${employment.employmentEmpType}");
        <%-- 취업유형 변경 끝 --%>

        let employmentSalary = $("#employmentSalary");

        //임금 최소 최대 입력 값
        inputLimitsWithRegex(employmentSalary, 0, 1000);

    });
</script>


</html>
