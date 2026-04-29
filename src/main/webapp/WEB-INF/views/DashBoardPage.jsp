<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 24. 12. 30.
  Time: 오후 5:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

    <!-- chart.js script -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels"></script>

    <script src="/js/drawChartCenterTextPlugin_0.0.1.js"></script>
    <script src="/js/dashBoardJS_0.0.1.js"></script>

    <!-- ApexChart로 변경 -->
    <script src="/js/ApexChartMainDashBoardJS_0.0.1.js"></script>

    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>

    <!-- 랜덤 색상 지정 -->
    <%-- 현재 사용안하고 있음 --%>
<%--    <script src="/js/randomColorGenerator_0.0.1.js"></script>--%>

    <!-- 진행바 스타일 적용 -->
    <link rel="stylesheet" href="/css/participantCss/dashboard_0.0.2.css">

    <link rel="stylesheet" href="/css/participantCss/dashBoardUi_0.0.2.css">
    <!-- Modern Design System -->
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <mytag:gnb gnb_main_header="실적관리" gnb_sub_header="대시보드"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid py-4 pt-3 pb-0">

                <!-- 상단 헤더 섹션 -->
                <div class="card-modern p-4 mb-2">
                    <div class="row align-items-center">
                        <div class="col-md-3">
                            <div class="text-muted small mb-1">${JOBMOA_LOGIN_DATA.memberBranch}지점</div>
                            <h3 class="fw-bold mb-0">${JOBMOA_LOGIN_DATA.memberUserName} <span class="fs-5 fw-normal text-muted">상담사</span></h3>
                        </div>
                        <div class="col-md-2 border-start ps-4">
                            <div class="small text-muted mb-1">년도 조회</div>
                            <select class="form-select form-select-sm fw-bold border-0 bg-light" id="yearSelect" style="width: 100px;">
                                <!-- JS에서 연도 자동 생성 -->
                            </select>
                        </div>
                        <div class="col-md-7">
                            <div class="d-flex justify-content-end gap-2">
                                <a class="quick-link-item" href="https://jobmoa.daouoffice.com/app/board" target="_blank"><i class="bi bi- megaphone me-1"></i> 다우톡 공지</a>
                                <a class="quick-link-item" onclick="alert('아직 지원되지 않는 서비스입니다.')" ><i class="bi bi-cpu me-1"></i> AI 직무분석</a>
                                <a class="quick-link-item" onclick="alert('아직 지원되지 않는 서비스입니다.')" ><i class="bi bi-file-earmark-text me-1"></i> AI 자소서</a>
                                <a class="quick-link-item" onclick="alert('아직 지원되지 않는 서비스입니다.')" ><i class="bi bi-lightbulb me-1"></i> AI 컨설팅</a>
                                <a class="quick-link-item" onclick="alert('아직 지원되지 않는 서비스입니다.')" ><i class="bi bi-book me-1"></i> 업무 메뉴얼</a>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 첫 번째 줄: 금일 업무 / 통계 / 성과 -->
                <div class="row g-4 mb-3">
                    <!-- 금일 업무 -->
                    <div class="col-xl-3 col-lg-4 pe-1">
                        <div class="card-modern">
                            <div class="card-modern-header">
                                <i class="bi bi-clipboard-check text-primary"></i> 금일 업무
                            </div>
                            <div class="card-body">
                                <div class="list-group list-group-flush">
                                    <c:choose>
                                        <c:when test="${not empty dailyDashboard}">
                                            <div class="work-list-item">
                                                <span class="fs-7">초기상담 미실시자</span>
                                                <span class="badge bg-danger rounded-pill">${dailyDashboard.dashBoardInItCons}</span>
                                            </div>
                                            <div class="work-list-item">
                                                <span class="fs-7 text-muted">최근상담 21일 경과</span>
                                                <span class="badge bg-secondary rounded-pill">${dailyDashboard.dashBoardLastCons}</span>
                                            </div>
                                            <div class="work-list-item">
                                                <span class="fs-7 fw-bold">구직 만료 도래</span>
                                                <span class="badge bg-warning text-dark rounded-pill">${dailyDashboard.dashBoardJobEX}</span>
                                            </div>
                                            <div class="work-list-item">
                                                <span class="fs-7 fw-bold">기간 만료 예정</span>
                                                <span class="badge bg-info rounded-pill">${dailyDashboard.dashBoardEXPDate}</span>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="text-center py-4 text-muted fs-7">확인할 내역이 없습니다.</div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 참여자 통계 -->
                    <div class="col-xl-3 col-lg-8 ps-1 pe-1">
                        <div class="card-modern">
                            <div class="card-modern-header">
                                <i class="bi bi-people text-success"></i> 참여자 통계
                            </div>
                            <div class="card-body">
                                <div id="thisYearParticipant" class="mb-3 p-3 bg-light rounded-3"></div>
                                <div id="currentParticipant" class="mb-3 p-3 bg-light rounded-3"></div>
                                <div id="totalUnemployedParticipant" class="mb-3 p-3 bg-light rounded-3">
                                    <div id="totalParticipant" class="mb-3 bg-light rounded-3">
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>

                    <!-- 성과 점수 현황 -->
                    <div class="col-xl-6 col-lg-12 ps-1">
                        <div class="card-modern">
                            <div class="card-modern-header d-flex justify-content-between">
                                <span><i class="bi bi-graph-up-arrow text-warning"></i> 성과 점수 현황</span>
                                <a href="${IS_MANAGER? 'scoreBranchDashboard.login':'scoreDashboard.login'}" class="btn btn-xs btn-outline-secondary py-0 fs-8">상세정보</a>
                            </div>
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col-8 d-flex justify-content-between">
                                        <div id="employmentRateChart" style="height:30vh; width:50vw;"></div>
                                        <div id="ArrangementChart" style="height:30vh; width:50vw;"></div>
                                        <div id="scoreChart" style="height:30vh; width:50vw;"></div>
                                    </div>
                                    <div class="col-4 border-start text-center">
                                        <div class="mb-3">
                                            <div class="fs-8 text-muted">취업률</div>
                                            <div id="employment-rate" class="h4 fw-bold text-danger mb-0">${employmentRate.employmentRate}%</div>
                                            <div class="fs-8 text-muted mt-1">현재 취업자: ${employmentRate.totalEmployed}명</div>
                                            <div class="fs-8 text-muted mt-1">현재 종료자: ${employmentRate.totalCompleted}명</div>
                                        </div>
                                        <div class="rank-badge mt-2">
                                            <div class="fs-8">순위</div>
                                            <div id="myTotalRanking" class="fs-7 fw-bold text-primary mb-1"></div>
                                            <div id="myRanking" class="fs-1 fw-bold mb-0"></div>
                                            <div class="fs-8">등급</div>
                                            <div id="nextRanking" class="fs-8 text-muted mt-1"></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="text-center fs-8 text-muted mt-3">
                                    * 입사 1년 미만자는 참고용으로만 활용 바랍니다.
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 두 번째 줄: 개인 실적 달성률 & 성공금 -->
                <div class="row g-4">
                    <div class="col-xl-9 pe-1">
                        <div class="card-modern">
                            <div class="card-modern-header">
                                <i class="bi bi-bullseye text-danger"></i> 개인 실적 달성률
                            </div>
                            <div class="card-body">
                                <div class="row text-center g-3">
                                    <!-- 각 차트 아이템을 좀 더 세련되게 박싱 처리 -->
                                    <div class="col-md-2 col-6">
                                        <div class="kpi-chart-item">
                                            <div class="fs-8 fw-bold text-dark mt-1">배정인원</div>
                                            <div id="exemple" style="min-height: 130px;"></div>
                                        </div>
                                    </div>
                                    <div class="col-md-2 col-6">
                                        <div class="kpi-chart-item">
                                            <div class="fs-8 fw-bold text-dark mt-1">종료 취업자</div>
                                            <div id="terminatedEmploymentChart" style="min-height: 130px;"></div>
                                        </div>
                                    </div>
                                    <div class="col-md-2 col-6">
                                        <div class="kpi-chart-item">
                                            <div class="fs-8 fw-bold text-dark mt-1">알선 취업자</div>
                                            <div id="referralEmploymentChart" style="min-height: 130px;"></div>
                                        </div>
                                    </div>
                                    <div class="col-md-2 col-6">
                                        <div class="kpi-chart-item">
                                            <div class="fs-8 fw-bold text-dark mt-1">조기 취업자</div>
                                            <div id="earlyEmploymentChart" style="min-height: 130px;"></div>
                                        </div>
                                    </div>
                                    <div class="col-md-2 col-6">
                                        <div class="kpi-chart-item">
                                            <div class="fs-8 fw-bold text-dark mt-1">나은 일자리</div>
                                            <div id="betterJobChart" style="min-height: 130px;"></div>
                                        </div>
                                    </div>
                                    <div class="col-md-2 col-6">
                                        <div class="kpi-chart-item">
                                            <div class="fs-8 fw-bold text-dark mt-1">인센 요건</div>
                                            <div id="incentiveQualificationChart" style="min-height: 130px;"></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-xl-3">
                        <div class="card-modern">
                            <div class="card-modern-header d-flex justify-content-between">
                                <span><i class="bi bi-wallet2 text-success"></i> 성공금</span>
                                <a href="successMoney.login" class="btn btn-xs btn-outline-secondary py-0 fs-8">보기</a>
                            </div>
                            <div class="card-body">
                                <div class="h-100 d-flex flex-column justify-content-between">
                                    <canvas id="ex-chart-bar1" style="height:30vh; width:50vw;"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </main>

    <mytag:footer/>

</div>

</body>
<!--begin::Script-->
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
<!--begin::Required Plugin(AdminLTE)-->
<script src="js/adminlte.js"></script>
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

<!-- OPTIONAL SCRIPTS -->
<!-- sortablejs -->
<script
        src="https://cdn.jsdelivr.net/npm/sortablejs@1.15.0/Sortable.min.js"
        integrity="sha256-ipiJrswvAR4VAx/th+6zWsdeYmVae0iJuiR+6OqHJHQ="
        crossorigin="anonymous"
></script>
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

<%-- Chart.js doughnut chart javascript Start--%>
<script>
    $(document).ready(function () {

        let employmentRate = ${empty myKPI.employmentRate ? 0 : myKPI.employmentRate}; // 취업률
        console.log("employmentRate : " + employmentRate);
        let avgEmploymentRateMiddle = ${empty myKPI.avgEmploymentRateMiddle ? 0 : myKPI.avgEmploymentRateMiddle}; // 평균 취업률
        console.log("avgEmploymentRateMiddle : " + avgEmploymentRateMiddle);
        let placementRate = ${empty myKPI.placementRate ? 0 : myKPI.placementRate}; // 알선취업률
        console.log("employmentRate : "+placementRate);
        let avgPlacementRateMiddle = ${empty myKPI.avgPlacementRateMiddle ? 0 : myKPI.avgPlacementRateMiddle}; // 평균 알선취업률
        console.log("avgPlacementRateMiddle : "+avgPlacementRateMiddle);
        let earlyEmploymentRate = ${empty myKPI.earlyEmploymentRate ? 0 : myKPI.earlyEmploymentRate}; // 조기취업률
        console.log("earlyEmploymentRate : "+ earlyEmploymentRate);
        let avgEarlyEmploymentRateMiddle = ${empty myKPI.avgEarlyEmploymentRateMiddle ? 0 : myKPI.avgEarlyEmploymentRateMiddle}; // 평균 조기취업률
        console.log("avgEarlyEmploymentRateMiddle : "+ avgEarlyEmploymentRateMiddle);
        let betterJobRate = ${empty myKPI.betterJobRate ? 0 : myKPI.betterJobRate}; // 나은일자리
        console.log("betterJobRate : "+ betterJobRate);
        let avgBetterJobRateMiddle = ${empty myKPI.avgBetterJobRateMiddle ? 0 : myKPI.avgBetterJobRateMiddle}; // 평균 나은일자리
        console.log("avgBetterJobRateMiddle : "+ avgBetterJobRateMiddle);
        let assignedParticipants = ${empty myKPI.assignedParticipants ? 0 : myKPI.assignedParticipants}; // 배정인원수
        console.log("assignedParticipants : "+ assignedParticipants);
        let noServiceCount = ${empty myKPI.noServiceCount ? 0 : myKPI.noServiceCount}; // 인센 미해당 서비스미제공
        console.log("noServiceCount : "+ noServiceCount);
        let falseCaseNum = ${empty myKPI.falseCaseNum ? 0 : myKPI.falseCaseNum}; // 인센 미해당
        console.log("falseCaseNum : "+ falseCaseNum);
        let trueCaseNum = ${empty myKPI.trueCaseNum ? 0 : myKPI.trueCaseNum}; // 인센 해당
        console.log("trueCaseNum : "+ trueCaseNum);

        // 음수 확인 후 고정 값 지정
        function checkNegative(value1,value2) {
            let result = (value1 - value2);

            if (result < 0) {
                result = 0;
            }
            return result;
        }


        let colors = ['#0064a6','#0064A600']

        //배정 인원 차트
        let series = [assignedParticipants, 95-assignedParticipants]
        let labels = ['배정 인원','남은 목표']
        let donut = new ApexCharts(document.querySelector("#exemple"), apexChartDoughnut('배정인원(목표:95)',series, labels,colors,false));
        donut.render();

        //종료 취업자 차트
        series = [employmentRate, 65-employmentRate]
        labels = ['종료 취업자','남은 목표']
        donut = new ApexCharts(document.querySelector("#terminatedEmploymentChart"), apexChartDoughnut('종료 취업자',series, labels,colors,true));
        donut.render();

        console.log("placementRate : "+placementRate);
        console.log("checkNegative(avgPlacementRateMiddle,placementRate) : "+checkNegative(avgPlacementRateMiddle,placementRate));
        //알선 취업자 차트
        series = [placementRate, checkNegative(avgPlacementRateMiddle,placementRate)]
        labels = ['알선 취업자','남은 목표']
        donut = new ApexCharts(document.querySelector("#referralEmploymentChart"), apexChartDoughnut('알선 취업자',series, labels,colors,true));
        donut.render();

        //조기 취업자 차트
        series = [earlyEmploymentRate, checkNegative(avgEarlyEmploymentRateMiddle,earlyEmploymentRate)]
        labels = ['조기 취업자','남은 목표']
        donut = new ApexCharts(document.querySelector("#earlyEmploymentChart"), apexChartDoughnut('조기 취업자',series, labels,colors,true));
        donut.render();

        //나은일자리 차트
        series = [betterJobRate, checkNegative(avgBetterJobRateMiddle,betterJobRate)]
        labels = ['나은일자리','남은 목표']
        donut = new ApexCharts(document.querySelector("#betterJobChart"), apexChartDoughnut('나은일자리',series, labels,colors,true));
        donut.render();

        //인센 요건 충족 차트
        series = [noServiceCount, falseCaseNum, trueCaseNum]
        labels = ['서비스 미제공', '미해당', '해당']
        colors = ['#ff0707', '#ff9100ff', '#0064a6']
        donut = new ApexCharts(document.querySelector("#incentiveQualificationChart"), apexChartDoughnut('인센 요건 충족',series, labels,colors,true));
        donut.render();

        <%--/*//전체 chart 플러그인 추가--%>
        <%--Chart.register(drawCenterTextPlugin);--%>
        <%--Chart.register(ChartDataLabels);--%>
    })
</script>
<%-- Chart.js Doughnut Chart javascript  End --%>
<%-- Chart.js Bar Chart javascript Start --%>
<script>
    $('document').ready(function () {
        let data_title = JSON.parse('${dashBoardDataTitle}');
        //chart 성공금 현황
        //성공금 발생
        let id=$('#ex-chart-bar1');
        let lable=['성공금 발생'];
        let data={title:data_title,text:JSON.parse('${dashBoardSuccessMoney}')};
        console.log(data);
        chart_bar_data_my(id,lable,data);
    })
</script>
<%-- Chart.js Bar Chart javascript End --%>

<%-- 참여자통계 Javascript Start --%>
<script>
    /*
    * 백단에서 각 조건에 맞는 참여자 숫자 데이터 를 전달받습니다.
    * 전달받은 숫자 데이터를 계산하여 % 형식으로 보여주고
    * title, subtitle 형식에 맞게 html div 태그 형식으로 뿌려줍니다.
    * */

    $(document).ready(function () {
        const thisYearParticipant = $('#thisYearParticipant');
        const currentParticipant = $('#currentParticipant');
        const totalParticipant = $('#totalParticipant');
        const totalUnemployedParticipant = $('#totalUnemployedParticipant');

        // 총 참여자 수 계산
        function calculateTotalParticipants(data) {
            let total = 0;
            $.each(data, function (_, value) {
                total += parseInt(value.data);
            });
            return total;
        }

        // 비율 계산
        function calculatePercentage(total, data) {
            let arr = [];
            $.each(data, function (_, value) {
                arr[_] = ((parseInt(value.data) / total) * 100).toFixed(2);
                isNaN(arr[_]) ? arr[_] = 0 : arr[_];
            })
            //console.log("arr:"+arr);
            return arr;
        }

        // 유형별 서브타이틀 생성
        function generateTypeSubtitle(data) {
            let subtitle = '';
            $.each(data, function (index, value) {
                subtitle += (index === 0 ? 'Ⅰ유형 ' : 'Ⅱ유형 ') + value.data + '명';
                if (index < data.length - 1) {
                    subtitle += ' / ';
                }
            });
            return subtitle;
        }

        // 년도별 서브타이틀 생성
        function generateYearSubtitle(data) {
            let subtitle = '';
            $.each(data, function (index, value) {
                subtitle += value.year + '년 ' + value.data + '명';
                if (index < data.length - 1) {
                    subtitle += ' / ';
                }
            });
            return subtitle;
        }

        // 진행자 데이터 처리
        function processParticipantData(targetElement, data, titleTemplate , subtitleTemplate) {
            const total = calculateTotalParticipants(data);
            const subtitle =  subtitleTemplate;
            const percentage = calculatePercentage(total, data);
            const title = titleTemplate + total + "명";
            const divContent = createDiv(title, subtitle, percentage);
            appendDiv(targetElement, divContent);
        }

        // DIV 생성
        function createDiv(title, subtitle, percentage) {
            let returnValue =
                "<div class='fw-bold'>"+title+"</div>"+
                "<div class='fs-8'>"+subtitle+"</div>"
            // 참여자 통계 그래프가 필요하다면 주석 해제
            //     +
            //     "<div class='progress rounded rounded-2'>";
            //
            // percentage.map((data)=> {
            //     returnValue += "<div class='progress-bar' role='progressbar'" +
            //         "style='width: " + data + "%; background: linear-gradient(90deg, "+randomColor()+", "+randomColor()+");color: black;font-weight: bold;' aria-valuenow='" + data + "'" +
            //         "aria-valuemin='0' aria-valuemax='100'>" + data + "%</div>"
            // });
            // returnValue += "</div>";

            return returnValue;
        }

        // DIV 추가
        function appendDiv(target, divContent) {
            target.append(divContent);
        }

        // 데이터
        let thisYearData = JSON.parse('${empty nowParticipantJsonData ?"[{\"year\":\"0\",\"data\":\"0\"}]":nowParticipantJsonData}');
        let currentData = JSON.parse('${empty currentParticipantJsonData ?"[{\"year\":\"0\",\"data\":\"0\"}]":currentParticipantJsonData}');
        let totalData = JSON.parse('${empty totalParticipantJsonData ?"[{\"year\":\"0\",\"data\":\"0\"}]":totalParticipantJsonData}');
        let totalUnemployedParticipantData = JSON.parse('${empty totalUnemployedParticipantJsonData ?"[{\"year\":\"0\",\"data\":\"0\"}]":totalUnemployedParticipantJsonData}');

        // 각 참여자 데이터 처리
        processParticipantData(thisYearParticipant, thisYearData, ${dashBoardYear}+"년 진행자 수 ", generateTypeSubtitle(thisYearData));
        processParticipantData(currentParticipant, currentData, "현재 진행자 수 ", generateYearSubtitle(currentData));
        processParticipantData(totalParticipant, totalData, "총 참여자 수 ", generateYearSubtitle(totalData));
        processParticipantData(totalUnemployedParticipant, totalUnemployedParticipantData, "미취업사후관리 및 종료 수", generateYearSubtitle(totalUnemployedParticipantData));
    });
</script>
<%-- 참여자통계 Javascript End --%>

<%-- 년도 조회 Start --%>
<script>
    $(document).ready(function () {
        // 연도 조회 셀렉트박스 생성 부분만 디자인에 맞게 최적화
        const yearSelect = $('#yearSelect');
        let currentDate = new Date();
        let currentYear = currentDate.getFullYear();
        for(var i=currentYear; i>=currentYear-5; i--){
            yearSelect.append('<option value="'+i+'">'+i+'</option>');
        }
        yearSelect.val('${dashBoardYear}');
        yearSelect.change(function () {
            location.href = "/dashboard.login?dashBoardYear=" + $(this).val();
        });

    })
</script>
<%-- 년도 조회 End --%>

<%-- 템플릿용으로 만들어둔 등수 현황 데이터 --%>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        let scoreStatusData = ${scoreJson};
        const nextRanginScore = scoreStatusData[0].pointsToNextGrade;
        const nextRanking = scoreStatusData[0].nextGrade;

        console.log(Math.floor(nextRanginScore*100)/100);
        let $nextRanking = $('#nextRanking');
        // $nextRanking.text("다음등급까지: "+ nextRanginScore + "점");
        if(nextRanking !== 'x'){
            $nextRanking.append("다음 등급 "+nextRanking);
        }

        /* 취업률 등급 표 시작 */
        // 취업률 등급 배열
        const employmentRateRank = [69.8, 63.5, 59.5];
        // 취업률
        let employmentRate = ${not empty employmentRate.employmentRate? employmentRate.employmentRate : 0};
        // 취업률 차트
        const employmentRateOptions = PerformanceScoreStatus([employmentRate], '취업률', '%',
            [scoreChartColorChange(employmentRate, employmentRateRank)], ['취업률'], '취업률', 80, 30, 0.5, 20, 1,
            '취업률 현황', employmentRateRank)
        // 취업률 차트 생성
        const employmentRateChart = new ApexCharts(document.querySelector("#employmentRateChart"), employmentRateOptions);
        employmentRateChart.render();
        /* 취업률 등급 표 끝 */

        /* 알선 현황 등급 표 시작 */
        // 알선 현황 등급 배열
        const placementRateRank = [6, 4, 2];
        // 알선 개수
        let referredEmploymentCount = ${not empty employmentRate.referredEmploymentCount? employmentRate.referredEmploymentCount : 0};
        // 알선 현황 차트
        const ArrangementOptions = PerformanceScoreStatus([referredEmploymentCount], '알선', '개',
            [scoreChartColorChange(referredEmploymentCount, placementRateRank)], ['알선 개수'], '알선개수', 12, 0, 0.5, 6, 0,
            '알선 현황', placementRateRank)
        // 알선 현황 차트 생성
        const ArrangementChart = new ApexCharts(document.querySelector("#ArrangementChart"), ArrangementOptions);
        ArrangementChart.render();
        /* 알선 현황 등급 표 끝 */

        /* 점수 현황 표 및 텍스트 작성 시작 */
        // 점수 현황 텍스트 추가 및 컬러 변경
        const $myRanking = $('#myRanking');
        let ranking = scoreStatusData[0].myRanking;
        $myRanking.text(ranking);
        // 등급에 따른 색지정
        // 전체 등급 표기
        $('#myTotalRanking').text(scoreStatusData[0].myTotalRanking);
        // 등급 색갈 변경
        rankingColorChange($myRanking, ranking);

        // 총점
        let myTotalScore = scoreStatusData[0].myScore;
        // 점수 현황
        const scoreStatusRank = [48.7, 47, 45.8];
        const scoreStatusOptions = PerformanceScoreStatus(scoreStatusData[0].data, '점수', '점',
            [scoreChartColorChange(myTotalScore, scoreStatusRank)], ['내 점수'], '점수', 50, 35, 0.5, 30, 1,
            '점수 현황', scoreStatusRank,
            {dataPointSelection: function (event, chartContext, config) {
                    // click 대신 dataPointSelection 사용
                    const clickIndex = config.dataPointIndex;
                    // const branchName = Datas.thisSuccess.branch[branchIndex];

                    // 총점 클릭시 모달 표시 or 페이지이동
                    if (clickIndex === 0) {
                        location.href = "scoreBranchDashboard.login";
                    }
                }
            })

        // 점수 현황 차트 생성
        const scoreStatusChart = new ApexCharts(document.querySelector("#scoreChart"), scoreStatusOptions);
        scoreStatusChart.render();
        /* 점수 현황 표 및 텍스트 추가 끝 */
    });

    function scoreChartColorChange(score, flagScore) {
        console.log("scoreChartColorChange score: " + score);
        console.log("scoreChartColorChange flagScore: " + flagScore);
        console.log("scoreChartColorChange flagScore.length: " + flagScore.length);
        let rankingColor = 'rgba(255,7,7,0.79)'; // D등급 (기본값)

        if (flagScore[0] <= score) {
            rankingColor = 'rgba(0,203,48,0.74)';     // A등급: 초록
        }
        else if (flagScore[1] <= score) {
            rankingColor = 'rgba(0,100,166,0.78)';     // B등급: 파랑
        }
        else if (flagScore[2] <= score) {
            rankingColor = 'rgba(239,255,0,0.78)';     // C등급: 노랑
        }
        return rankingColor;
    }

    function rankingColorChange($myRanking, myRanking) {

        let rankingColor = 'rgba(255,7,7,0.79)'; // D등급 (기본값)

        if (myRanking === 'A') {
            rankingColor = 'rgba(0,203,48,0.74)';     // A등급: 초록
        }
        else if (myRanking === 'B') {
            rankingColor = 'rgba(0,100,166,0.78)';     // B등급: 파랑
        }
        else if (myRanking === 'C') {
            rankingColor = 'rgba(239,255,0,0.78)';     // C등급: 노랑
        }

        if ($myRanking && $myRanking.length && $myRanking.length > 0) {
            $myRanking.css('color', rankingColor);
        }
    }
</script>

</html>
