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
    <!-- mouse pointer 모양 bootstrap 5 -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="/css/participantCss/dashBoardScoreAndSituation_0.0.2.css" />

    <!-- Custom Modern CSS (Global Design System) -->
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">
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
            <div class="container-fluid">
                <div class="dash-filter-bar">
                    <span class="dash-chip">점수/평균 분포</span>
                    <span class="dash-chip">필터: 전체</span>
                </div>
                <!-- 필요 본문 내용은 이쪽에 만들어 주시면 됩니다. -->
                <!-- 점수 현황 차트 -->
                <div class="card-modern mb-4">
                    <div class="card-header border-0">
                         <h3 class="card-title text-primary fw-bold"><i class="bi bi-graph-up me-2"></i>점수 현황</h3>
                    </div>
                    <div class="card-body">
                         <div id="scoreChart"></div>
                    </div>
                </div>

                <!-- 분포 현황 차트 -->
                <div class="card-modern">
                    <div class="card-header border-0">
                        <h3 class="card-title text-primary fw-bold"><i class="bi bi-pie-chart me-2"></i>분포 현황</h3>
                    </div>
                    <div class="card-body">
                         <div id="distributionChart"></div>
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

<script
        src="https://cdn.jsdelivr.net/npm/apexcharts@3.37.1/dist/apexcharts.min.js"
        integrity="sha256-+vh8GkaU7C9/wbSLIcwq82tQ2wTf44aOHA8HlBMwRI8="
        crossorigin="anonymous"
></script>
<script>


    // 초기 데이터 정의
    const initData = [{
        "completedCount": {"name": "종료자수", "data": "0"},
        "myScore": {
            "name": "개인 점수",
            "data": [0,0,0,0,0,0],
            "oneData": [0,0,0,0,0]
        },
        "myCount": {
            "name": "점수 분포",
            "data": [0,0,0,0,0],
            "avgData": [0,0,0,0,0]
        }
    }];

    // 데이터 확인 및 차트 렌더링
    let scoreAndAvg = ${scoreAndAvg}; // JSP에서 전달된 데이터


    // 가로열 제목 데이터
    const xCategories = ['총점', '취업자점수', '알선취업자점수', '조기취업자점수', '고용유지자점수', '나은일자리점수'];

    // 첫 번째 차트 - 점수 현황
    function renderScoreChart(data) {
        const options = {
            series: [{
                name: '획득 점수',
                data: data[0].myScore.data
            }],
            chart: {
                type: 'bar',
                height: 350
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
                    return val.toFixed(2);
                }
            },
            xaxis: {
                categories: xCategories,
                position: 'bottom',

                labels: {
                    useHTML: true,  // HTML 활성화
                    formatter: function(value) {
                        // HTML 태그를 사용하여 줄바꿈과 색상 변경
                        const parts = value.split('\n');
                        return parts;
                    },
                    rotate: 0,
                    maxHeight: 50,
                    style: {
                        fontSize: '14px'
                    }
                }
            },
            yaxis: {
                tickAmount: 5,     // 눈금 간격 설정
                labels: {
                    formatter: function(val) {
                        return val.toFixed(0);
                    }
                }
            },
            colors: ['#008FFB']
        };

        const chart = new ApexCharts(document.querySelector("#scoreChart"), options);
        chart.render();
    }

    // 두 번째 차트 - 점수별 인원수 및 비율
    function renderDistributionChart(data) {
        const options = {
            series: [{
                name: '인원수',
                type: 'column',
                data: data[0].myCount.data
            }, {
                name: '비율',
                type: 'line',
                data: data[0].myCount.avgData
            }],
            chart: {
                height: 350,
                type: 'line',
                toolbar: {
                    show: true
                }
            },
            stroke: {
                width: [0, 4],
                curve: 'smooth'
            },
            dataLabels: {
                enabled: true,
                enabledOnSeries: [0, 1],
                formatter: function(val, opt) {
                    if (opt.seriesIndex === 0) {
                        return val+'명';
                    }
                    // 비율 데이터의 경우
                    return val+'%';
                }
            },
            xaxis: {
                categories: ['취업자수', '알선취업자수', '조기취업자수', '고용유지자수', '나은일자리수'],
            },
            yaxis: [{
                title: {
                    text: '인원수'
                }
            }, {
                opposite: true,
                title: {
                    text: '비율 (%)'
                },
                max: 100  // 퍼센트 최대값
            }],
            colors: ['#00E396', '#008FFB'],
            title: {
                text: '점수별 인원 분포 및 비율',
                align: 'center'
            },
            tooltip: {
                y: [{
                    formatter: function(val) {
                        return val+'명';
                    }
                }, {
                    formatter: function(val) {
                        return val+'%';
                    }
                }]
            }
        };

        const chart = new ApexCharts(document.querySelector("#distributionChart"), options);
        chart.render();
    }
    $(document).ready(function () {

        //가로열 데이터에 1인당 점수 표기 추가
        scoreAndAvg[0].myScore.oneData.forEach((data, i) => {
            xCategories[i+1] = xCategories[i+1]+"\n1인당 점수 : "+data
        })

        // 데이터 존재 여부 확인 (JavaScript 방식으로 수정)
        if (!scoreAndAvg || Object.keys(scoreAndAvg).length === 0) {
            renderScoreChart(initData);
            renderDistributionChart(initData);
            return;
        }
        renderScoreChart(scoreAndAvg);
        renderDistributionChart(scoreAndAvg);
        console.log(xCategories);
    });
</script>
</html>

