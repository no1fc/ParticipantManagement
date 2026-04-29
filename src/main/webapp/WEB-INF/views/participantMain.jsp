<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 24. 12. 30.
  Time: 오후 5:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

    <!-- jQuery JS -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>

    <meta
            name="keywords"
            content="bootstrap 5, bootstrap, bootstrap 5 admin dashboard, bootstrap 5 dashboard, bootstrap 5 charts, bootstrap 5 calendar, bootstrap 5 datepicker, bootstrap 5 tables, bootstrap 5 datatable, vanilla js datatable, colorlibhq, colorlibhq dashboard, colorlibhq admin dashboard"
    />
    <!--end::Primary Meta Tags-->
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
    <!--begin::Third Party Plugin(OverlayScrollbars)-->
    <script
            src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es6.min.js"
            integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ="
            crossorigin="anonymous"
    ></script>
    <!--end::Third Party Plugin(OverlayScrollbars)-->
    <!--begin::Required Plugin(popperjs for Bootstrap 5)-->
    <script
            src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
            integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r"
            crossorigin="anonymous"
    ></script>
    <!--end::Required Plugin(popperjs for Bootstrap 5)-->
    <!--begin::Required Plugin(Bootstrap 5)-->
    <script
            src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.min.js"
            integrity="sha384-0pUGZvbkm6XF6gxjEnlmuGrJXVbNuzT9qBBavbLwCsOGabYfZo0T0to5eqruptLy"
            crossorigin="anonymous"
    ></script>
    <!--end::Required Plugin(Bootstrap 5)-->


    <!-- sortablejs -->
    <script
            src="https://cdn.jsdelivr.net/npm/sortablejs@1.15.0/Sortable.min.js"
            integrity="sha256-ipiJrswvAR4VAx/th+6zWsdeYmVae0iJuiR+6OqHJHQ="
            crossorigin="anonymous"
    ></script>
    <!-- sortablejs -->
    <!-- apexcharts -->
    <script
            src="https://cdn.jsdelivr.net/npm/apexcharts@3.37.1/dist/apexcharts.min.js"
            integrity="sha256-+vh8GkaU7C9/wbSLIcwq82tQ2wTf44aOHA8HlBMwRI8="
            crossorigin="anonymous"
    ></script>
    <!--begin::Required Plugin(AdminLTE)-->
    <script src="/js/adminlte.js"></script>
    <!--end::Required Plugin(AdminLTE)-->

    <!-- pagination JS 파일 -->
    <script src="/js/paginationJS_0.0.1.js"></script>

    <!-- selectOptionJS JS 파일 -->
    <script src="/js/selectOptionJS_0.0.1.js"></script>

    <!-- Excel 다운 버튼 CSS 추가 -->
    <link rel="stylesheet" href="/css/participantCss/dailyWorkReportCss_0.0.1.css">

    <!-- sweetalert2 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
    <script src="/js/sweetAlert_0.0.1.js"></script>


    <!-- 새로 분리한 JS 파일들 추가 -->
    <!-- 참여자조회 Js 코드들 -->
    <script src="/js/participant_main_0.0.3.js"></script>
    <script src="/js/participant_excel_download_0.0.1.js"></script>

    <!-- 참여자 조회 기본 디자인 설정 -->
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">
    <link rel="stylesheet" href="/css/participantCss/participantMain_0.0.2.css">


    <!-- 추천 채용정보 CSS & JS -->
    <link rel="stylesheet" href="/css/participantCss/recommend-modal_0.0.2.css">
    <script src="/js/recommend-modal_0.0.3.js"></script>

    <!-- SockJS + STOMP + JOBMOA_USER_ID는 gnb.tag에서 공통 로드 -->

    <!-- Kakao JavaScript SDK (카카오톡 공유 기능) -->
    <script src="https://t1.kakaocdn.net/kakao_js_sdk/2.7.4/kakao.min.js"
            integrity="sha384-DKYJZ8NLiK8MN4/C5P2dtSmLQ4KwPaoqAfyA/DfmEc1VDxu4yyC7wy6K1Hs90nka"
            crossorigin="anonymous"></script>
    <script>
        Kakao.init('${kakaoJsKey}');
    </script>
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin:::App Gnb-->
    <mytag:gnb gnb_main_header="상담관리" gnb_sub_header="참여자 조회"/>
    <!--end:::App Gnb-->

    <!--begin:::App main content-->
    <!--begin::App Main-->
    <main class="app-main">
        <!--begin::App Content-->
        <div class="app-content">
            <div class="container-fluid">
                <!-- 필요 본문 내용은 이쪽에 만들어 주시면 됩니다. -->

                <div class="row col-md-12 pt-4 me-auto ms-auto">
                    <mytag:pariticipantSearchForm actionURL="/participant.login"/>

                    <%-- 참여자 테이블 시작 --%>
                    <div class="col-12 card-modern p-4">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <span class="text-muted small">현재 <span class="fw-bold countSpan">X</span>명</span>
                            <span class="text-muted small">총 <span class="fw-bold totalCountSpan">${totalCount}</span>명</span>
                            <%-- 기업 파일에서 조회하는 거라는 것을 알려준다. --%>
                            <input type="hidden" id="branchManagementPageFlag" value="${branchManagementPageFlag?true:false}">
                        </div>
                        <div class="table-responsive">
                            <table class="table table-modern align-middle table-hover">
                                <thead>
                                <tr class="text-center bg-light">
                                    <th class="text-center" style="width: 50px;"><input type="button" class="btn btn-sm btn-outline-danger" id="delete_btn" value="삭제"></th>
                                    <%--                                    <th class="table-Column">--%>
                                    <%--                                        <span class="column">구직번호</span>--%>
                                    <%--                                    </th>--%>
                                    <th class="table-Column">
                                        <span class="column">연번</span>
                                        <i class="bi bi-exclamation-square-fill"></i>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">유형</span>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">참여자</span>
                                        <p class="m-0 p-0 participant-subtext-xs">생년월일</p>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">최근상담일</span>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">상담경과일</span>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">진행단계</span>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">초기상담일</span>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">IAP3개월차</span>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">IAP5개월차</span>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">간접고용서비스</span>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">수당지급일</span>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">구직만료일</span>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">기간만료일</span>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">마감</span>
                                    </th>
                                    <th class="table-Column">
                                        <span class="column">추천 채용정보</span>
                                    </th>
                                </tr>
                                </thead>
                                <tbody class="align-middle">

                                <c:choose>
                                    <c:when test="${empty datas}">
                                        <td class="text-center" colspan="15">
                                            검색된 참여자가 없습니다.
                                        </td>
                                    </c:when>
                                    <c:when test="${not empty datas}">
                                        <c:forEach items="${datas}" var="data" varStatus="status" >
                                            <tr class="text-center">
                                                <td><label class="text-center w-100 h-100"><input type="checkbox" class="delete" name="delete" value="${data.participantJobNo}"></label></td>
                                                <td class="rowNumTd">${data.rowNum}
                                                    <input type="hidden" class="participantJobNo" value="${data.participantJobNo}" readonly>
                                                    <input type="hidden" class="rowNum" value="${data.rowNum}" readonly>
                                                </td>
                                                <td>${data.participantPartType} 유형</td>
                                                <td>
                                                    <a class="selectParticipant" href="/participantUpdate.login">
                                                        <c:if test="${data.participantISIntensiveMediation}">
                                                        <p class="badge-jibjoong m-0 p-0 participant-subtext-sm">집중알선</p>
                                                        </c:if>
                                                        ${data.participantPartic}
                                                        <p class="m-0 p-0 participant-subtext-sm">${data.participantDob}</p>
                                                    </a>
                                                </td>
                                                <td>${data.participantLastCons}</td>
                                                <td class="adventCons-td">${data.participantAdventCons}</td>
                                                <td class="participantProgress-td">${data.participantProgress}</td>
                                                <td>${data.participantInItCons}</td>
                                                <td class="participantIAP3Month-td">${data.participantIAP3Month} <input type="hidden" class="isIap3Month" value="${data.participantISIAP3Month}" readonly></td>
                                                <td class="participantIAP5Month-td">${data.participantIAP5Month} <input type="hidden" class="isIap5Month" value="${data.participantISIAP5Month}" readonly></td>
                                                <td>${data.participantEmploymentService eq '' or data.participantEmploymentService eq null?0:data.participantEmploymentService} 회</td>
                                                <td>${data.participantAllowanceDate}</td>
                                                <td>${data.participantJobEX}</td>
                                                <td>${data.participantEXPDate}</td>
                                                <td class="text-center isClose_td">
                                                    <span class="badge ${data.participantClose ? 'bg-danger' : 'bg-success'} isClose_span">${data.participantClose ?'마감':'진행중'}</span>
                                                </td>
                                                <td>
                                                    <button type="button"
                                                            class="btn-recommend"
                                                            data-gujik="${data.participantJobNo}"
                                                            data-name="${data.participantPartic}"
                                                            data-stage="${data.participantProgress}"
                                                            onclick="openRecommendModal(this)"
<%--                                                            onclick="alert('개발 진행중인 기능입니다.')"--%>
                                                    >
                                                        추천 채용정보
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <%-- 참여자 테이블 끝 --%>

                    <%-- 페이지네이션 시작 --%>
                    <div class="col-md-11 text-center ms-auto me-auto d-flex justify-content-center">
                        <ul class="pagination">
                        </ul>
                    </div>
                    <%-- 페이지네이션 끝 --%>
                </div>

            </div>

        </div>
        <!--end::App Content-->
    </main>
    <!--end::App Main-->
    <!--end:::App Main content-->

    <!--begin::Footer-->
    <mytag:footer/>
    <!--end::Footer-->



</div>

<jsp:include page="/WEB-INF/views/includeModal/recommendModal.jsp"/>

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
    $(document).ready(function(){
        <%-- pagination 시작 --%>
        // page 변수
        let page = parseInt("${page}", 10) || 1; // page가 비어있거나 아닌 경우 숫자로 변환 후 기본값 1 적용
        page = (page == 0) ? 1 : page; //page가 0이라면 1로 변경
        // startButton 변수
        const startButton = parseInt("${startButton}", 10) || 1; // startButton 기본값 1
        // endButton 변수
        const endButton = parseInt("${endButton}", 10) || 1; // endButton 기본값 10
        // totalButton 변수
        const totalButton = parseInt("${totalButton}", 10) || 0; // totalButton 기본값 0
        //pagination JS 함수 호출
        paginationAddItems(page, startButton, endButton, totalButton);
        <%-- pagination 끝 --%>
    })
</script>
</html>
