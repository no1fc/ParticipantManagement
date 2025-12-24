<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 25. 4. 15.
  Time: 오전 9:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <!-- mouse pointer 모양 bootstrap 5 -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <!-- 실적 정보 Dfault 디자인 -->
    <link rel="stylesheet" href="/css/DashBoardBranchScoreAndSituation.css" />

    <style>

    </style>
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin:::App Gnb-->
    <mytag:gnb gnb_main_header="실적관리" gnb_sub_header="대시보드"/>
    <!--end:::App Gnb-->
    <!--begin:::App main content-->
    <!--begin::App Main-->
    <main class="app-main">
        <!--begin::App Content-->
        <div class="app-content">
            <!--begin::Main content-->
            <div class="container-fluid dashboard-container">
                <!-- 컨트롤 패널 -->
                <c:if test="${IS_BRANCH_MANAGER || IS_MANAGER}">
                    <div class="control-panel fade-in">
                        <div class="row">
                            <div class="col-12">
                                <h6><i class="bi bi-sliders"></i> 실적 조회 설정</h6>
                            </div>
                        </div>

                        <%-- TODO 기간별 선택 기능 제작 완료 후 d-none 설정 제거 --%>
                        <div class="col-md-12">
                            <div class="modern-btn-group">
                                <div id="graphAndTableButtonContainer">
                                    <span class="placeholderText">* 실적 기간 선택</span>
                                    <div class="input-group w-100">
                                        <div class="input-group-prepend">
                                            <span class="input-group-text">
                                                <i class="bi bi-calendar-date"></i>
                                            </span>
                                        </div>
                                        <input
                                                type="date"
                                                class="form-control"
                                                name="dashBoardStartDate"
                                                id="dashBoardStartDate"
                                                autocomplete="off"
                                                value="${dashBoardStartDate}"
                                        >
                                        <div class="input-group-prepend input-group-append">
                                            <span class="input-group-text">~</span>
                                        </div>
                                        <input
                                                type="date"
                                                class="form-control"
                                                name="dashBoardEndDate"
                                                id="dashBoardEndDate"
                                                autocomplete="off"
                                                value="${dashBoardEndDate}"
                                        >
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="row g-3">
                            <!-- 그래프/테이블 선택 -->
                            <div class="col-lg-3 col-md-6">
                                <div class="modern-btn-group">
                                    <div id="graphAndTableButtonContainer">
                                        <span class="placeholderText">* 그래프 표 선택</span>
                                        <div class="btn-group w-100" role="group" aria-label="뷰 선택">
                                            <input type="radio" class="btn-check" name="GraphAndTable" id="performanceGraphButton" autocomplete="off" value="false" checked>
                                            <label class="btn" for="performanceGraphButton">
                                                <i class="bi bi-bar-chart-line-fill"></i>실적 그래프
                                            </label>

                                            <input type="radio" class="btn-check" name="GraphAndTable" id="performanceTableButton" autocomplete="off" value="true">
                                            <label class="btn" for="performanceTableButton">
                                                <i class="bi bi-table"></i>실적 표
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- 고용유지 포함여부 -->
                            <div class="col-lg-3 col-md-6">
                                <div class="modern-btn-group">
                                    <div id="excludeRetentionButtonContainer">
                                        <span class="placeholderText">* 고용유지 실적 포함 여부</span>
                                        <div class="btn-group w-100" role="group" aria-label="고용유지 설정">
                                            <input type="radio" class="btn-check" name="excludeRetention" id="excludeRetentionRadio" autocomplete="off" value="false" checked>
                                            <label class="btn" for="excludeRetentionRadio">
                                                <i class="bi bi-shield-x"></i>고용유지 미포함
                                            </label>

                                            <input type="radio" class="btn-check" name="excludeRetention" id="includeRetentionRadio" autocomplete="off" value="true">
                                            <label class="btn" for="includeRetentionRadio">
                                                <i class="bi bi-shield-check"></i>고용유지 포함
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- 지점/개인 선택 -->
                            <div class="col-lg-3 col-md-6">
                                <div class="modern-btn-group">
                                    <div id="branchAndPeopleButtonContainer">
                                        <span class="placeholderText">* 해당 기능은 실적 표에만 적용되는 사항입니다.</span>
                                        <div class="btn-group w-100" role="group" aria-label="분석 단위">
                                            <input type="radio" class="btn-check" name="branchAndPeople" id="branchRadio" autocomplete="off" value="false" checked>
                                            <label class="btn" for="branchRadio">
                                                <i class="bi bi-building-fill"></i>지점별
                                            </label>

                                            <input type="radio" class="btn-check" name="branchAndPeople" id="peopleRadio"  autocomplete="off" value="true">
                                            <label class="btn" for="peopleRadio">
                                                <i class="bi bi-person-circle"></i>개인별
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- 1년 미만 근로자 -->
                            <div class="col-lg-3 col-md-6">
                                <div class="modern-btn-group">
                                    <div id="buttonContainer">
                                        <span class="placeholderText">* 해당 버튼 클릭시 조회 시작</span>
                                        <div class="btn-group w-100" role="group" aria-label="근로자 기준">
                                            <input type="radio" class="btn-check" name="dashboardFlagEmployment" id="excludeRadio" autocomplete="off" value="false" checked>
                                            <label class="btn" for="excludeRadio">
                                                <i class="bi bi-person-dash"></i>1년 미만 근로자 미포함
                                            </label>

                                            <input type="radio" class="btn-check" name="dashboardFlagEmployment" id="includeRadio" autocomplete="off" value="true">
                                            <label class="btn" for="includeRadio">
                                                <i class="bi bi-person-plus"></i>1년 미만 근로자 포함
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                </c:if>

                <!-- 로딩 영역 -->
                <div id="loadingScoreChartDiv" class="chart-container modern-loader" style="display: none;">
                    <div class="modern-loader-text">실적 정보를 불러오는 중입니다</div>
                    <div class="loader"></div>
                </div>

                <!-- 실적 테이블 영역 -->
                <div id="performanceTableDiv" class="modern-table-container" style="display: none;">
                    <div class="table-scroll-container">
                        <table id="performanceTable" class="table table-hover">
                            <thead id="performanceTableHead">
                            <tr class="text-center">
                                <th>연번</th>
                                <th id="dashBoardUserName-th" class="d-none">상담사</th>
                                <th id="dashboardBranch-th">지점명</th>
                                <th id="dashboardByYearCount1-th">참여자(2023)</th>
                                <th id="dashboardByYearCount2-th">참여자(2024)</th>
                                <th id="dashboardByYearCount3-th">참여자(2025)</th>
                                <th id="dashboardTotalCount-th">참여자 합계</th>
                                <th id="totalCompleted-th">종료자</th>
                                <th id="totalEmployed-th">취업자</th>
                                <th id="specialGroupCount-th">특정계층</th>
                                <th id="totalEmployedSpecialGroupCount-th">취업자실적</th>
                                <th id="employmentRate-th">취업자률실적</th>
                                <th id="referredEmploymentCount-th">알선</th>
                                <th id="placementRate-th">알선실적</th>
                                <th id="betterJobCount-th">나은취업</th>
                                <th id="betterJobRate-th">나은실적</th>
                                <th id="earlyEmploymentCount-th">조기취업</th>
                                <th id="earlyEmploymentRate-th">조기실적</th>
                                <th id="employmentScore-th">취업점수</th>
                                <th id="placementScore-th">알선점수</th>
                                <th id="betterJobScore-th">나은점수</th>
                                <th id="earlyEmploymentScore-th">조기취업점수</th>
                                <th id="retentionScore-th">고용유지점수</th>
                                <th id="totalScore-th">총점</th>
                            </tr>
                            </thead>
                            <tbody id="performanceTableBody" class="text-center">
                            </tbody>
                        </table>
                    </div>
                    <div class="table-info">
                        <i class="bi bi-info-circle me-2"></i>
                        <span id="tableRowCount">총 0개 항목</span> | 스크롤하여 더 많은 데이터를 확인하세요
                    </div>
                </div>


                <!-- 메인 차트 영역 -->
                <div id="scoreChartDiv" class="chart-container fade-in">
                    <h5><i class="bi bi-graph-up"></i>지점별 실적 현황</h5>
                    <div id="scoreChart"></div>
                </div>

                <!-- 개별 로딩 영역 -->
                <div id="loadingDiv" class="chart-container modern-loader" style="display: none;">
                    <div class="modern-loader-text">상세 분석 데이터를 불러오는 중입니다</div>
                    <div class="loader"></div>
                </div>

                <!-- 상세 차트 그리드 -->
                <div id="detail-chart-grid" class="chart-grid">
                    <div class="chart-card">
                        <div class="card-header">
                            <i class="bi bi-trophy-fill text-warning me-2"></i>총점 현황
                        </div>
                        <div id="totalScore"></div>
                    </div>

                    <div class="chart-card">
                        <div class="card-header">
                            <i class="bi bi-people-fill text-success me-2"></i>취업자 점수
                        </div>
                        <div id="employmentScore"></div>
                    </div>

                    <div class="chart-card">
                        <div class="card-header">
                            <i class="bi bi-briefcase-fill text-info me-2"></i>알선 점수
                        </div>
                        <div id="placementScore"></div>
                    </div>

                    <div class="chart-card">
                        <div class="card-header">
                            <i class="bi bi-shield-fill-check text-primary me-2"></i>고용유지 점수
                        </div>
                        <div id="retentionScore"></div>
                    </div>

                    <div class="chart-card">
                        <div class="card-header">
                            <i class="bi bi-speedometer2 text-danger me-2"></i>조기취업 점수
                        </div>
                        <div id="earlyEmploymentScore"></div>
                    </div>

                    <div class="chart-card">
                        <div class="card-header">
                            <i class="bi bi-star-fill text-purple me-2"></i>나은일자리 점수
                        </div>
                        <div id="betterJobScore"></div>
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

<script>
    // 초기 데이터 정의
    const initData = [
        {name:"총점", data:0.01},
        {name:"남부", data:0.1},
        {name:"서부", data:0.2}
    ];

    let changJson = {
        name:[], data:[]
    }

    // 데이터 확인 및 차트 렌더링
    let responseData = ${branchAvg}; // JSP에서 전달된 데이터

    // 첫 번째 차트 - 점수 현황
    function renderScoreChart(data) {

        console.log(data);
        let yaxisAnnotation = [{
            y: data.data[0],
            borderColor: '#FF4560',
            label: {
                borderColor: '#FF4560',
                style: {
                    color: '#fff',
                    background: '#FF4560'
                },
                text:'지점 평균: ' + data.data[0] + '점'
            }
        }];

        const options = {
            series: [{
                name: "평균",
                data: data.data?.slice(1,data.data.length)
            }],
            chart: {
                type: 'bar',
                height: 350,
                events: {
                    dataPointSelection: function(event, chartContext, config) { // click 대신 dataPointSelection 사용
                        const clickIndex = config.dataPointIndex;
                        const branchName = data.name[clickIndex+1];
                        // console.log(clickIndex);
                        // console.log(branchName);
                        fetchData(branchName);
                    }
                }
            },
            plotOptions: {
                bar: {
                    horizontal: false,
                    columnWidth: '55%',
                    dataLabels: {
                        position: 'top'
                    }
                },
            },
            dataLabels: {
                enabled: true,
                formatter: function(val) {
                    return val.toFixed(1);
                }
            },
            xaxis: {
                categories: data.name?.slice(1,data.name.length),
                position: 'bottom'
            },
            yaxis:{
                max: 55,
                min: 40,            // 최소값 설정
                tickAmount: 15,     // 눈금 간격 설정
                labels: {
                    formatter: function(val) {
                        return val.toFixed(1);
                    }
                }
            },
            annotations: {
                yaxis: yaxisAnnotation
            }
        };

        const chart = new ApexCharts(document.querySelector("#scoreChart"), options);
        chart.render();
    }

    //두 번째 차트 사용자별 점수
    // 각 차트에 표시할 데이터를 정의하는 함수
    const totalScore = $("#totalScore");
    const employmentScore = $("#employmentScore");
    const placementScore = $("#placementScore");
    const retentionScore = $("#retentionScore");
    const earlyEmploymentScore = $("#earlyEmploymentScore");
    const betterJobScore = $("#betterJobScore");
    const loadingDiv = $('#loadingDiv');

    function getChartOptions(chartIndex, title, maxScore, jsonValue, jsonScore, jsonTopScore) {
        // 각 차트의 제목과 데이터 설정
        let chartTitle = title[chartIndex];
        let seriesData = [{
            name: title[chartIndex]+' 점수',
            data: selectData[jsonValue[chartIndex]][jsonScore[chartIndex]]
        }];
        let yaxisAnnotation = [{
            y: selectData[jsonValue[chartIndex]][jsonTopScore[chartIndex]],
            borderColor: '#FF4560',
            label: {
                borderColor: '#FF4560',
                style: {
                    color: '#fff',
                    background: '#FF4560'
                },
                text: '상위20% ' + title[chartIndex]+' 평균: ' + selectData[jsonValue[chartIndex]][jsonTopScore[chartIndex]] + '점'
            }
        }];
        const chartColors = [
            ['#2196F3'],    // 총점 - 파란색
            ['#00BCD4'],    // 취업자 - 틸색
            ['#009688'],    // 알선취업자 - 청록색
            ['#4CAF50'],    // 고용유지 - 녹색
            ['#8BC34A'],    // 조기취업자 - 라임색
            ['#3F51B5']     // 나은일자리 - 인디고
        ];

        // 차트 옵션 구성
        const options = {
            series: seriesData,
            colors: chartColors[chartIndex],
            chart: {
                height: 450,
                type: 'bar',
                toolbar: {
                    show: true,
                    tools: {
                        download: true,
                        selection: true,
                    }
                },
                dropShadow: {
                    enabled: true,
                    opacity: 0.3,
                    blur: 3
                },
                events: {
                    dataPointSelection: function(event, chartContext, config) { // click 대신 dataPointSelection 사용
                        const clickIndex = config.dataPointIndex;
                        const userID = selectData.userID[clickIndex];
                        location.href = "scoreDashboard.login?dashboardUserID=" + userID + "&dashboardBranch=" + selectData.branch;
                    }
                }
            },
            plotOptions: {
                bar: {
                    horizontal: false,
                    columnWidth: '90%',
                    endingShape: 'rounded',
                    borderRadius: 4,
                    dataLabels: {
                        position: 'top'
                    }
                },
            },
            stroke: {
                show: true,
                width: 10,
                colors: ['transparent']
            },
            dataLabels: {
                enabled: true,
                formatter: function(val) {
                    return val.toFixed(1) + '점';
                },
                style: {
                    fontSize: '10px',
                    fontFamily: 'Helvetica, Arial, sans-serif',
                    fontWeight: 'bold',
                    colors: ['#ffffff']
                }
            },
            xaxis: {
                categories: selectData.username,
                tickPlacement: 'between',
                labels: {
                    style: {
                        fontSize: '12px',
                        fontWeight: 600,
                    },
                    rotate: -45,
                    hideOverlappingLabels: false
                }
            },
            yaxis: {
                title: {
                    text: '점수',
                    style: {
                        fontSize: '10px',
                        fontWeight: 600
                    }
                },
                min: 0,
                max: maxScore[chartIndex],
                forceNiceScale: true,
                tickAmount: 5,
                labels: {
                    formatter: function(val) {
                        return val.toFixed(0) + '점';
                    }
                }
            },
            legend: {
                position: 'top',
                horizontalAlign: 'right',
                floating: true,
                fontSize: '13px',
                markers: {
                    size: 8
                }
            },
            title: {
                text: chartTitle,
                align: 'center',
                style: {
                    fontSize: '16px',
                    fontWeight: 'bold'
                },
                margin: 20
            },
            tooltip: {
                shared: true,
                intersect: false,
                y: {
                    formatter: function(val) {
                        return val.toFixed(1) + '점';
                    }
                },
                theme: 'dark'
            },
            grid: {
                borderColor: '#e0e0e0'
            },
            markers: {
                size: 6,
                hover: {
                    size: 9
                }
            },
            annotations: {
                yaxis: yaxisAnnotation
            }
        };

        return options;
    }

    // 차트 렌더링 함수 수정
    function renderDistributionChart(title, maxScore, jsonValue, jsonScore, jsonTopScore) {
        // 각 차트 요소를 선택하고 렌더링
        for (let i = 0; i < jsonValue.length; i++) {
            const chartElement = document.querySelector("#"+jsonScore[i]);
            if (chartElement) {
                const options = getChartOptions(i, title, maxScore, jsonValue, jsonScore, jsonTopScore);
                const chart = new ApexCharts(chartElement, options);
                chart.render();
            }
        }
    }

    let chartFlag = false;
    let disableFlag = true;
    //상담사별 데이터 불러오는 fetch(비동기 함수)
    function fetchData(data) {
        //초기화 함수
        emptyFunction();
        const $loadingDiv = $('#loadingDiv');
        $loadingDiv.show().html('<div class="modern-loader-text">실적 정보를 불러오는 중입니다</div><div class="loader"></div>');
        const excludeRetention = $('#excludeRetentionRadio'); //고용유지 포함여부
        let isExcludeRetention = !excludeRetention.is(':checked');

        let dashBoardStartDate = $('#dashBoardStartDate').val();
        let dashBoardEndDate = $('#dashBoardEndDate').val();

        console.log("isExcludeRetention :[" +isExcludeRetention+"]")
        //여러번 클릭하는 것을 방지하기 위해 flag 설정으로 확인 후 반환한다.
        if(!disableFlag){
            alertDefaultWarning('데이터를 조회하는 중입니다.','')
            return;
        }
        //첫번째 실행으로 비활성화 진행
        disableFlag = false;

        fetch('dashBoardAjaxBranchScore.login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json; charset=UTF-8',
            },
            body: JSON.stringify({
                dashboardBranch:data,
                dashboardCondition: chartFlag,
                dashBoardStartDate: dashBoardStartDate,
                dashBoardEndDate: dashBoardEndDate,
                dashboardExcludeRetention: isExcludeRetention //고용유지 포함 여부확인
            })
        }).then(async response => {
            // let data = JSON.parse(await response.json());
            let data = await response.json();

            // JSON 데이터 수정
            // data = JSON.parse(data);
            console.log(data);
            changeDataAVG(data);
            changeDataUser(data);

            //"total" 총점,"employment"취업자,"placement"알선취업자,"retention"고용유지,"earlyEmployment"조기취업자,"betterJob"나은일자리
            const title = ["총점","취업자","알선취업자","고용유지","조기취업자","나은일자리"]
            const maxScore = [90,30,25,15,10,10] //점수 확인용
            const jsonValue = ["total","employment","placement","retention","earlyEmployment","betterJob"]
            const jsonScore = ["totalScore","employmentScore","placementScore","retentionScore","earlyEmploymentScore","betterJobScore"]
            const jsonTopScore = ["totalStandardScore","employmentTopScore","placementTopScore","retentionTopScore","earlyEmploymentTopScore","betterJobTopScore"]
            // console.log(jsonScore.length);
            renderDistributionChart(title, maxScore, jsonValue, jsonScore, jsonTopScore);
            $loadingDiv.hide()
            //완료 후 활성화
            disableFlag = true;
        }).catch(r => {
            $loadingDiv.html('<div class="status-error p-3"> 오류가 발생했습니다.(다른 지점은 선택할 수 없습니다. </div>');
            console.log(r);
            //오류가 발생해도 사용해야하니 활성화
            disableFlag = true;
        })
    }

    let selectData = {
        username:[],
        userID:[],
        branch:{},
        retention: {
            retentionTopScore: {},
            retentionScore:[]
        },
        placement:{
            placementTopScore:{},
            placementScore:[]
        },
        employment: {
            employmentTopScore:{},
            employmentScore:[]
        },
        earlyEmployment:{
            earlyEmploymentTopScore:{},
            earlyEmploymentScore:[]
        },
        betterJob:{
            betterJobTopScore:{},
            betterJobScore:[]
        },
        total:{
            totalStandardScore: {},
            myBranchScore: {},
            totalScore:[]
        },
    }

    function changeDataAVG(data){
        data.branchScore.forEach(function(value, key){
            //console.log(value.retentionTopScore);
            selectData.retention.retentionTopScore = value.retentionTopScore;
            //console.log(value.placementTopScore);
            selectData.placement.placementTopScore = value.placementTopScore;
            //console.log(value.employmentTopScore);
            selectData.employment.employmentTopScore = value.employmentTopScore;
            //console.log(value.earlyEmploymentTopScore);
            selectData.earlyEmployment.earlyEmploymentTopScore = value.earlyEmploymentTopScore;
            //console.log(value.betterJobTopScore);
            selectData.betterJob.betterJobTopScore = value.betterJobTopScore;
            //console.log(value.totalStandardScore); // 전체 총점
            selectData.total.totalStandardScore = value.totalStandardScore;
        })
    }

    function changeDataUser(data){
        data.branchUserScore.forEach(function(value, key){
            // console.log("changeDataUser 실행 사용자 이름 : [%s]",value.username);
            selectData.username.push(value.username);

            // console.log("changeDataUser 실행 사용자 계정 : [%s]",value.userID);
            selectData.userID.push(value.userID);

            // console.log("changeDataUser 실행 사용자 지점 : [%s]",value.branch)
            selectData.branch = value.branch;

            // console.log("changeDataUser 실행 고용유지 : [%s]",value.retentionScore);
            selectData.retention.retentionScore.push(value.retentionScore);

            // console.log("changeDataUser 실행 알선취업 : [%s]",value.placementScore);
            selectData.placement.placementScore.push(value.placementScore);

            // console.log("changeDataUser 실행 취업자 : [%s]",value.employmentScore);
            if(value.employmentScore === null){
                selectData.employment.employmentScore.push(0);
            }
            else{
                selectData.employment.employmentScore.push(value.employmentScore);
            }
            // console.log("changeDataUser 실행 조기취업 : [%s]",value.earlyEmploymentScore);
            selectData.earlyEmployment.earlyEmploymentScore.push(value.earlyEmploymentScore);

            // console.log("changeDataUser 실행 나은일자리 : [%s]",value.betterJobScore);
            selectData.betterJob.betterJobScore.push(value.betterJobScore);

            // console.log("changeDataUser 실행 개인 총점 : [%s]",value.totalScore);//개인 총점
            selectData.total.totalScore.push(value.totalScore);

            // console.log("changeDataUser 실행 지점 총정 : [%s]",value.myBranchScore); //지점 총점
            selectData.total.myBranchScore = value.myBranchScore;
        })
    }

    function emptyFunction(){
        selectData = {
            username:[],
            userID:[],
            branch:{},
            retention: {
                retentionTopScore: {},
                retentionScore:[]
            },
            placement:{
                placementTopScore:{},
                placementScore:[]
            },
            employment: {
                employmentTopScore:{},
                employmentScore:[]
            },
            earlyEmployment:{
                earlyEmploymentTopScore:{},
                earlyEmploymentScore:[]
            },
            betterJob:{
                betterJobTopScore:{},
                betterJobScore:[]
            },
            total:{
                totalStandardScore: {},
                myBranchScore: {},
                totalScore:[]
            },
        };
        totalScore.empty();
        employmentScore.empty();
        placementScore.empty();
        retentionScore.empty();
        earlyEmploymentScore.empty();
        betterJobScore.empty();
        loadingDiv.hide();
    }

    // 테이블 행 수 업데이트 함수
    function updateTableRowCount() {
        const $tableBody = $('#performanceTableBody');
        const rowCount = $tableBody.find('tr').length;
        $('#tableRowCount').text(`총 ${rowCount}개 항목`);
    }

    $(document).ready(function () {
        changeData(responseData);
        // 데이터 존재 여부 확인 (JavaScript 방식으로 수정)
        if (!responseData || Object.keys(responseData).length === 0) {
            changeData(initData)
        }
        //지점 평균 데이터 출력
        renderScoreChart(changJson);

        function changeData(data){
            //JSON 데이터로 변환
            try{
                data = JSON.parse(data);
            }
            //오류 발생시 이미 JSON 데이터이므로 오류 출력
            catch (e) {
                console.log(`Error: ${e}`);
            }
            // 데이터 map 진행
            finally{
                data.map(function(item){
                    changJson.name.push(item.name);
                    changJson.data.push(item.data);
                });
                //console.log(changJson);
            }
        }

        // 테이블 변경 감지 및 행 수 업데이트
        const tableBody = document.getElementById('performanceTableBody');
        if (tableBody) {
            const observer = new MutationObserver(function(mutations) {
                mutations.forEach(function(mutation) {
                    if (mutation.type === 'childList') {
                        updateTableRowCount();
                    }
                });
            });

            observer.observe(tableBody, {
                childList: true,
                subtree: true
            });
        }

        // 초기 행 수 업데이트
        updateTableRowCount();

        const excludeRadio = $('#excludeRadio'); //미포함
        const includeRadio = $('#includeRadio'); //포함
        const performanceGraphButton = $('#performanceGraphButton'); //실적 그래프 버튼
        const performanceTableButton = $('#performanceTableButton'); //실적 표 버튼
        const excludeRetentionRadio = $('#excludeRetentionRadio'); // 고용유지 미포함 버튼
        const includeRetentionRadio = $('#includeRetentionRadio'); // 고용유지 포함 버튼
        const branchRadio = $('#branchRadio'); // 지점 버튼
        const peopleRadio = $('#peopleRadio'); // 개인 버튼
        const performanceTable = $("#performanceTable"); // 실적 테이블 id
        let isProcessing = false;

        //1년 미만 상담사 미포함
        excludeRadio.click(function(){
            if (isProcessing) return; // 처리 중이면 중복 클릭 방지
            isProcessing = true;

            let excludeVal = excludeRadio.val();
            chartFlag = excludeVal;
            disableRadioButtons();

            graphAndTableChange(excludeVal);

        });
        //1년 미만 상담사 포함
        includeRadio.click(function(){
            if (isProcessing) return; // 처리 중이면 중복 클릭 방지
            isProcessing = true;

            let includeVal = includeRadio.val();
            chartFlag = includeVal;
            disableRadioButtons();

            // fetchPerformanceContract(includeVal).finally(() => {
            //     enableRadioButtons();
            //     isProcessing = false;
            // });
            graphAndTableChange(includeVal);
        });

        function graphAndTableChange(data) {
            if(performanceGraphButton.is(':checked')){
                console.log('실적 그래프 체크 상태 : [' + performanceGraphButton.is(':checked')+']');
                fetchPerformanceContract(data).finally(() => {
                    enableRadioButtons();
                    isProcessing = false;
                });
            }
            else if(performanceTableButton.is(':checked')){
                console.log('실적 표 체크 상태 : [' + performanceTableButton.is(':checked') +']');
                fetchPerformanceTableContract(data).finally(() =>{
                    enableRadioButtons();
                    isProcessing = false;
                });
            }
            else{
                alertDefaultWarning('오류발생 페이지를 새로고침합니다.','')
                window.location.reload();
            }
        }

        // 라디오 버튼 제어 함수
        function disableRadioButtons() {
            excludeRadio.prop('disabled', true);
            includeRadio.prop('disabled', true);

            performanceGraphButton.prop('disabled', true);
            performanceTableButton.prop('disabled', true);

            excludeRetentionRadio.prop('disabled', true);
            includeRetentionRadio.prop('disabled', true);

            branchRadio.prop('disabled', true);
            peopleRadio.prop('disabled', true);
        }

        function enableRadioButtons() {
            excludeRadio.prop('disabled', false);
            includeRadio.prop('disabled', false);

            performanceGraphButton.prop('disabled', false);
            performanceTableButton.prop('disabled', false);

            excludeRetentionRadio.prop('disabled', false);
            includeRetentionRadio.prop('disabled', false);

            branchRadio.prop('disabled', false);
            peopleRadio.prop('disabled', false);
        }

        function fetchPerformanceContract(conditionFlag) {
            return new Promise((resolve, reject) => {
                $("#performanceTableDiv").hide(); //실적표 숨기기
                console.log(conditionFlag, " 실적 그래프 생성중");
                $("#scoreChartDiv").empty();
                // 개인 실적 그래프 삭제
                totalScore.empty();
                employmentScore.empty();
                placementScore.empty();
                retentionScore.empty();
                earlyEmploymentScore.empty();
                betterJobScore.empty();
                loadingDiv.hide();

                const $loadingScoreChartDiv = $("#loadingScoreChartDiv");
                $loadingScoreChartDiv.show().html('<div class="modern-loader-text">실적 정보를 불러오는 중입니다</div><div class="loader"></div>');

                const $dashBoardStartDateInput = $('#dashBoardStartDate');
                const $dashBoardEndDateInput = $('#dashBoardEndDate');

                changJson = {
                    name: [],
                    data: []
                }

                const excludeRetention = $('#excludeRetentionRadio'); //고용유지 포함여부
                let isExcludeRetention = !excludeRetention.is(':checked');
                console.log("isExcludeRetention :[" +isExcludeRetention+"]")

                fetch('scoreBranchPerformanceGraphAjax.login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json; charset=utf-8',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify({
                        dashBoardStartDate: $dashBoardStartDateInput.val(),//EX: '2024-11-01', //FIXME 추후 실적 일정 설정 input 추가시 수정
                        dashBoardEndDate: $dashBoardEndDateInput.val(),//EX: '2025-10-31',
                        dashboardFlagCondition: conditionFlag,
                        dashboardExcludeRetention: isExcludeRetention,
                        isManagement: ${IS_MANAGER},
                        isBranchManagement: ${IS_BRANCH_MANAGER}
                    })
                })
                    .then(async response => {
                        console.log(conditionFlag, " 실적 그래프삭제 진행끝");
                        $("#scoreChartDiv").html('<h5><i class="bi bi-graph-up"></i>지점별 실적 현황</h5><div id="scoreChart"></div>');


                        console.log(response);
                        const data = await response.json();

                        if (data.length === 0) {
                            throw new Error("실적 데이터를 불러오는 동안 오류가 발생했습니다.");
                        }

                        // 실적 차트에 맞도록 데이터 변환
                        changeData(data);
                        // 실적 차트 생성
                        renderScoreChart(changJson);
                        $loadingScoreChartDiv.hide();

                        resolve(data); // 성공 시 데이터 반환
                    })
                    .catch(error => {
                        console.error(error);
                        $loadingScoreChartDiv.html('<div class="status-error p-3">Error 발생 : ' + error + '</div>');
                        reject(error); // 에러 발생 시 reject
                    });
            });
        }


        function fetchPerformanceTableContract(conditionFlag,sortType,sortColumn) {
            //테이블 조회 기본 설정
            // sortType = sortType || 'DESC';
            // sortColumn = sortColumn || 'totalScore';

            return new Promise((resolve, reject) => {
                console.log(conditionFlag, " 실적 표 생성중");
                $("#scoreChartDiv").empty();
                //테이블 삭제
                $("#performanceTableBody").empty();

                const branchRadio = $('#branchRadio'); // 지점 버튼
                const peopleRadio = $('#peopleRadio'); // 개인 버튼
                let branchAndPeople = branchRadio.is(':checked');
                let peopleRadioIS = peopleRadio.is(':checked');

                if (peopleRadioIS) {
                    const counselTH = $('#dashBoardUserName-th');
                    counselTH.removeClass('d-none');
                }
                else {
                    const counselTH = $('#dashBoardUserName-th');
                    counselTH.addClass('d-none');
                }

                // 개인 실적 그래프 삭제
                totalScore.empty();
                employmentScore.empty();
                placementScore.empty();
                retentionScore.empty();
                earlyEmploymentScore.empty();
                betterJobScore.empty();
                loadingDiv.hide();

                const $loadingScoreChartDiv = $("#loadingScoreChartDiv");
                $loadingScoreChartDiv.show().html('<div class="modern-loader-text">실적 표를 불러오는 중입니다</div><div class="loader"></div>');

                const excludeRetention = $('#excludeRetentionRadio'); //고용유지 포함여부
                let isExcludeRetention = !excludeRetention.is(':checked');
                console.log("isExcludeRetention :[" +isExcludeRetention+"]")

                const $dashBoardStartDateInput = $('#dashBoardStartDate');
                const $dashBoardEndDateInput = $('#dashBoardEndDate');

                fetch('scoreBranchPerformanceTableAjax.login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        dashBoardStartDate: $dashBoardStartDateInput.val(),//EX: '2024-11-01', //FIXME 추후 실적 일정 설정 input 추가시 수정
                        dashBoardEndDate: $dashBoardEndDateInput.val(),//EX: '2025-10-31',
                        dashboardFlagCondition: conditionFlag,
                        dashboardExcludeRetention: isExcludeRetention,
                        isManagement: ${IS_MANAGER},
                        isBranchManagement: ${IS_BRANCH_MANAGER},
                        dashboardBranchAndPeople: branchAndPeople,
                        sortType: sortType,
                        sortColumn: sortColumn
                    })
                })
                    .then(async response => {
                        console.log(conditionFlag, " 실적 표 삭제 진행끝");
                        $("#scoreChartDiv").append('<div id="scoreChart"></div>');

                        let data = JSON.parse(await response.text());
                        console.log("formanceTable Data : [" + data + "]");
                        if (data.length === 0) {
                            throw new Error("실적 데이터를 불러오는 동안 오류가 발생했습니다.");
                        }

                        let numberFormat;
                        data.forEach(function(value, key){
                            numberFormat = Number(key)+1;
                            let row = '<tr>';
                            row += '<td>' + numberFormat + '</td>'; //순번
                            if(peopleRadioIS){
                                row += '<td>' + value.dashBoardUserName + '</td>'; //상담사
                            }
                            row += '<td>' + value.dashboardBranch + '</td>'; //지점
                            row += '<td>' + value.dashboardByYearCount1 + '</td>'; //전전년도 (ex 2023 참여자
                            row += '<td>' + value.dashboardByYearCount2 + '</td>'; //전년도 (ex 2024) 참여자
                            row += '<td>' + value.dashboardByYearCount3 + '</td>'; //당해년도 (ex 2025) 참여자
                            row += '<td>' + value.dashboardTotalCount + '</td>'; //참여자 합계
                            row += '<td>' + value.totalCompleted + '</td>'; //종료자
                            row += '<td>' + value.totalEmployed + '</td>'; //취업자
                            row += '<td>' + value.specialGroupCount + '</td>'; //특정계층
                            row += '<td>' + value.totalEmployedSpecialGroupCount + '</td>'; //취업자실적(특정+취업자가중치)
                            row += '<td>' + value.employmentRate + '</td>'; //취업자률
                            row += '<td>' + value.referredEmploymentCount + '</td>'; //알선
                            row += '<td>' + value.placementRate.toFixed(2) + '</td>'; //알선실적
                            row += '<td>' + value.betterJobCount + '</td>'; //나은취업
                            row += '<td>' + value.betterJobRate.toFixed(2) + '</td>'; //나은실적
                            row += '<td>' + value.earlyEmploymentCount + '</td>'; //조기취업
                            row += '<td>' + value.earlyEmploymentRate.toFixed(2) + '</td>'; //조기실적
                            row += '<td>' + value.employmentScore.toFixed(2) + '</td>'; //취업점수
                            row += '<td>' + value.placementScore.toFixed(2) + '</td>'; //알선점수
                            row += '<td>' + value.betterJobScore.toFixed(2) + '</td>'; //나은점수
                            row += '<td>' + value.earlyEmploymentScore.toFixed(2) + '</td>'; //조기취업점수
                            row += '<td>' + value.retentionScore.toFixed(2) + '</td>'; //고용유지점수
                            row += '<td>' + value.totalScore.toFixed(2) + '</td>'; //총점
                            row += '</tr>';
                            $("#performanceTableBody").append(row);
                        })

                        // 로딩 표시 제거
                        $loadingScoreChartDiv.hide();

                        $("#performanceTableDiv").show();//데이터 생성 후 실적표 표시

                        resolve(data); // 성공 시 데이터 반환
                    })
                    .catch(error => {
                        console.error(error);
                        $loadingScoreChartDiv.html('<div class="status-error p-3">Error 발생 : ' + error + '</div>');
                        reject(error); // 에러 발생 시 reject
                    });
            });
        }


        const performanceTableHead = $('#performanceTableHead');
        performanceTableHead.on('click', 'th', function () {

            //여러번 클릭을 방지하기 위해 버튼클릭을 잠금
            isProcessing = true;
            disableRadioButtons();

            const $this = $(this);
            const $sort = $('#sort');
            // 현재 선택한 th 글자
            let beforeText = $this.text().trim();
            // span tag가 있는 th 태그의 글자
            let afterText = $('th:has(span)').text().trim();
            // span tag가 있는 th 태그의 class가 down 여부
            let isUp = $sort.find('i').hasClass('bi-sort-up');
            // asc desc 필터 확인을 확인 후 값을 변경
            let sortType = 'ASC';

            if (beforeText === '연번') {
                return;
            }

            $sort.remove();

            console.log("isUp : [" + isUp + "]");
            if (beforeText === afterText && !isUp) {
                //오름차순
                console.log('오름차순 실행');
                $this.append('<span id="sort"><i class="bi bi-sort-up"></i></span>');
                // sortType = 'ASC';
            }
            else{
                //내림차순
                console.log('내림차순 실행');
                $this.append('<span id="sort"><i class="bi bi-sort-down"></i></span>');
                sortType = 'DESC';

            }

            fetchPerformanceTableContract(chartFlag, sortType, $this.attr('id')).finally(() => {
                //Error Or success 시 버튼 잠금 해제 및 값 출력
                isProcessing = false;
                enableRadioButtons();
            });

        })

    });
</script>
<script
        src="https://cdn.jsdelivr.net/npm/apexcharts@3.37.1/dist/apexcharts.min.js"
        integrity="sha256-+vh8GkaU7C9/wbSLIcwq82tQ2wTf44aOHA8HlBMwRI8="
        crossorigin="anonymous"
></script>
</html>