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
    <!-- datepicker CSS JS -->
    <!-- mouse pointer 모양 bootstrap 5 -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <!-- Bootstrap Datepicker 로드 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/js/bootstrap-datepicker.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/locales/bootstrap-datepicker.ko.min.js" integrity="sha512-L4qpL1ZotXZLLe8Oo0ZyHrj/SweV7CieswUODAAPN/tnqN3PA1P+4qPu5vIryNor6HQ5o22NujIcAZIfyVXwbQ==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="/js/datepickerJS_0.0.1.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/css/bootstrap-datepicker.min.css">
    <link rel="stylesheet" href="/css/participantCss/datepicker_0.0.1.css">

    <!-- recommend JS -->
    <script src="/js/recommendJS_0.0.1.js"></script>
    <link rel="stylesheet" href="/css/participantCss/recommend_0.0.2.css">

    <!-- educationDiv JS -->
    <script src="/js/educationDiv_0.0.2.js"></script>

    <!-- particcertifDiv JS -->
    <script src="/js/particcertifDiv_0.0.2.js"></script>

    <!-- participants_insert_update_CommonnessJS_0.1.1.js  -->
    <script src="/js/participants_insert_update_CommonnessJS_0.1.1.js"></script>

    <!-- selectOption JS -->
    <script src="/js/selectOptionJS_0.0.1.js"></script>

    <!-- InputLimits JS -->
    <script src="/js/InputLimits_0.0.1.js"></script>

    <!-- sweetalert2 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
    <script src="/js/sweetAlert_0.0.1.js"></script>

    <!-- kakao 주소 API 호출 JS API 문서 주소 https://postcode.map.daum.net/guide#usage -->
    <script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>

    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>

    <!-- 키워드 입력 JS 추가 -->
    <script src="/js/participantKeyWordInputJS_0.1.2.js"></script>
    <!-- 키워드 입력 CSS 추가 -->
    <link rel="stylesheet" href="/css/participantCss/keyWordInputCss_0.0.1.css">

    <!-- jobPlacementDefault JS -->
    <script src="/js/jobCategorySelectRenderText_0.0.2.js"></script>
    <!-- 다중 희망직무 관리 JS -->
    <script src="/js/jobWishListManager_0.0.1.js"></script>
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">
    <link rel="stylesheet" href="/css/participantCss/participantTable_0.0.1.css">

    <!-- sortablejs CDN -->
    <script
            src="https://cdn.jsdelivr.net/npm/sortablejs@1.15.0/Sortable.min.js"
            integrity="sha256-ipiJrswvAR4VAx/th+6zWsdeYmVae0iJuiR+6OqHJHQ="
            crossorigin="anonymous"
    ></script>
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin:::App Gnb-->
    <mytag:gnb gnb_main_header="${!branchManagementPageFlag ? '상담관리':'지점관리'}" gnb_sub_header="${basic.basicPartic} 참여자 관리"/>
    <!--end:::App Gnb-->
    <!--begin:::App main content-->
    <!--begin::App Main-->
    <main class="app-main">
        <!--begin::App Content-->
        <div class="app-content">
            <!--begin::Main content-->
            <div class="container-fluid">
                <!-- 필요 본문 내용은 이쪽에 만들어 주시면 됩니다. -->
                <div class="container-fluid">
                    <div class="row pt-3">
                        <div class="col-md-12">
                            <form id="participantsForm" name="participantsForm" method="POST" action="/participantUpdate.login" class="form-horizontal">
                                <%-- 지점 관리 / 지점 전체 참여자 일때 추가해서 페이지 전환이 가능하도록 추가 --%>
                                <input type="hidden" name="branchManagementPageFlag" id="branchManagementPageFlag" value="${branchManagementPageFlag}">
                                <%-- 검색 필터 hiddenInput tag --%>
                                <mytag:searchHiddenInput/>

                                <%-- 참여자 수정 버튼 시작 --%>
                                <div class="row pb-2 mb-1">
                                    <div class="col-12 text-end">
                                        <button type="button" class="btn btn-primary" id="btn_check">
                                            참여자 수정
                                        </button>
                                    </div>
                                </div>
                                <%-- 참여자 수정 버튼 끝 --%>

                                <%-- 기본정보 입력 tag --%>
                                <mytag:ParticipantBasic basic="${basic}"/>

                                <%-- 상담정보 입력 tag --%>
                                <mytag:ParticipantCounsel counsel="${consult}"/>

                                <%-- 취업정보 입력 tag --%>
                                <mytag:ParticipantEmployment employment="${employment}"/>
                            </form>
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
<!--end::Required Plugin(AdminLTE)--><!--begin::OverlayScrollbars Configure-->
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


<script>
    $(document).ready(function () {
        let basicAntecedents = $("#basicAntecedents");
        let counselSalWant = $("#counselSalWant");
        let employmentSalary = $("#employmentSalary");

        //경력 최소 최대 입력 값
        inputLimitsWithRegex(basicAntecedents, 0, 40);
        //희망급여 최소 최대 입력 값
        inputLimitsWithRegex(counselSalWant, 0, 1000);
        //임금 최소 최대 입력 값
        inputLimitsWithRegex(employmentSalary, 0, 1000);

        //자격증 배열을 백단에서 전달받습니다.
        let specialtyArr = JSON.parse('${particcertifs}') ;
        console.log(specialtyArr);
        specialty(specialtyArr);

        //성별 목록 내용 변경
        selectOption($("#basicGender"),"${basic.basicGender}");

        //모집경로 목록 내용 변경
        selectOption($("#basicRecruit"),"${basic.basicRecruit}");

        //참여유형 목록 내용 변경
        selectOption($("#basicPartType"),"${basic.basicPartType}");

        //학력 목록 내용 변경
        selectOption($("#basicEducation"),"${basic.basicEducation}");
        console.log("${basic.basicEducation}");

        //특정계층 목록 내용 변경
        selectOption($("#basicSpecific"),"${basic.basicSpecific}");

        //직업훈련 배열을 백단에서 전달받습니다.
        let educationArr = JSON.parse('${educations}') ;
        education(educationArr);

        <%-- 목록 내용 변경 시작 --%>
        //취업역량
        selectOption($("#counselJobSkill"),"${counsel.counselJobSkill}");

        //알선요청 목록 내용 변경
        selectOption($("#counselPlacement"),"${(counsel.counselPlacement eq '' || counsel.counselPlacement eq null)?'미희망':counsel.counselPlacement}");
        <%-- 목록 내용 변경 끝 --%>

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
        selectOption(counselProgress,"${counsel.counselProgress}");

        //취업인센티브_구분
        selectOption(employmentIncentive,"${employment.employmentIncentive}");

        //일경험 구분
        selectOption(employmentJobcat,"${employment.employmentJobcat}");

        //간접고용서비스 목록 내용 변경
        selectOption($("#counselEmploymentService"),"${counsel.counselEmploymentService}");

        <%-- 직무 카테고리(대/중)는 hidden input으로 전환됨. 다중 희망직무 UI에서 렌더링 --%>
        <%-- 목록 내용 변경 끝 --%>

        <%-- 취업유형 변경 시작 --%>
        changeSelect(counselProgress, employmentEmpType,"${counsel.counselProgress}");
        //취업유형
        selectOption(employmentEmpType,"${employment.employmentEmpType}");

        <%-- 취업유형 변경 끝 --%>

        //알선 상세 정보 page 로딩시 한번 실행
        //page 로딩시 알선 상세정보 입력란을 숨김
        const hiddenDiv = $("#hiddenDiv");
        //page 로딩시 알선 상세정보 입력란을 숨김
        JobPlacementDetail(hiddenDiv);

        // 다중 희망직무 데이터 복원
        if (typeof initWishJobList === 'function') {
            let wishJobArr = [];
            try {
                wishJobArr = JSON.parse('${wishJobs}');
            } catch (e) { /* 파싱 실패 시 빈 배열 */ }
            // 레거시 호환: 다중 데이터 없을 시 기존 단일 값으로 1건 복원
            if (!wishJobArr || wishJobArr.length === 0) {
                let legacyLarge = "${counsel.jobCategoryLarge}";
                let legacyMid = "${counsel.jobCategoryMid}";
                let legacyWant = "${counsel.counselJobWant}";
                if (legacyLarge || legacyWant) {
                    wishJobArr = [{categoryLarge: legacyLarge, categoryMid: legacyMid, jobWant: legacyWant}];
                }
            }
            initWishJobList(wishJobArr);
        }

    });


    <%-- 키워드 등록 시작 --%>
    // JavaScript
    window.addEventListener('load', () => {
        // 서버에서 온 값 예: "[자기주도성, 품질 관리 및 보안 인식, 책임감, 사용자 중심 사고(UI·UX 이해)]"
        const serverKeywords = "${counsel.recommendedKeywords}";
        if (serverKeywords && typeof window.addKeywordsFromBackend === 'function') {
            window.addKeywordsFromBackend(serverKeywords);
        }
    });
    <%-- 키워드 등록 끝 --%>

</script>


</html>
