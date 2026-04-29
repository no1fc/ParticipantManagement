<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 24. 12. 30.
  Time: 오후 5:16
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

    

    <link rel="stylesheet" href="/css/participantCss/dashBoardSuccessMoney_0.0.2.css" />
    
    <!-- Custom Modern CSS (Global Design System) -->
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin:::App Gnb-->
    <mytag:gnb gnb_main_header="실적관리" gnb_sub_header="대시보드" gnb_sub_menu_header="성공금 현황"/>
    <!--end:::App Gnb-->
    <!--begin:::App main content-->
    <!--begin::App Main-->
    <main class="app-main">
        <!--begin::App Content-->
        <div class="app-content h-100">
            <!--begin::Main content-->
            <div class="container-fluid h-100">
                <div class="dash-bar">
                    <span class="dash-chip">성공금 리스트</span>
                    <span class="dash-chip">테이블 + 그래프</span>
                </div>
                <!-- 필요 본문 내용은 이쪽에 만들어 주시면 됩니다. -->
                <div class="row d-flex align-items-center justify-content-center h-100">
                    <div class="col-md-6 h-100 p-2">
                        <div class="card-modern h-100">
                            <div class="card-header border-0">
                                <h3 class="card-title text-primary fw-bold"><i class="bi bi-table me-2"></i>성공금 데이터</h3>
                            </div>
                            <div class="card-body p-0 overflow-auto">
                                <div id="chart-data-container" class="h-100">
                                    <table id="chart-data-table" class="table table-striped table-hover table-bordered text-center mb-0">
                                        <thead class="table-dark">
                                        <tr class="position-sticky top-0">
                                            <th>구직번호</th>
                                            <th>등록일</th>
                                            <th>상담사</th>
                                            <th>참여자</th>
                                            <th>성공금</th>
                                            <th>인센티브</th>
                                            <th>지점</th>
                                        </tr>
                                        </thead>
                                        <tbody class="table-light">
                                        <c:choose>
                                            <c:when test="${empty successMoneyDetails}">
                                                <tr>
                                                    <td colspan="7">성공금이 없습니다.</td>
                                                </tr>
                                            </c:when>
                                            <c:when test="${not empty successMoneyDetails}">
                                                <c:forEach items="${successMoneyDetails}" var="data">
                                                    <tr>
                                                        <td>${data.dashBoardJobNo}</td>
                                                        <td>${data.dashBoardDate}</td>
                                                        <td>${data.dashBoardUserName}</td>
                                                        <td>${data.dashBoardPartic}</td>
                                                        <td>${data.dashBoardSuccessMoney}</td>
                                                        <td>${data.dashBoardIncentive}</td>
                                                        <td>${data.dashBoardUserBranch}</td>
                                                    </tr>
                                                </c:forEach>
                                            </c:when>
                                        </c:choose>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-6 h-100 p-2">
                        <div class="card-modern h-100">
                             <div class="card-header border-0">
                                <h3 class="card-title text-primary fw-bold"><i class="bi bi-graph-up-arrow me-2"></i>성공금 및 인센티브 차트</h3>
                            </div>
                            <div class="card-body">
                                <div id="chart-container" class="h-100">
                                    <div id="chart-area"></div>
                                    <div id="chart-bar"></div>
                                    <div id="chart-area1"></div>
                                    <div id="chart-bar1"></div>
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


<script>
    $(document).ready(function () {
        let successMoneyJson = JSON.parse('${successMoneyJson}');
        let incentiveJson = JSON.parse('${incentiveJson}');

        const generateTimeSeriesData = (data) => {
            return data.map(item => [
                new Date(item.date).getTime(),
                item.data
            ]);
        };

        const transSuccessMoney = generateTimeSeriesData(successMoneyJson);
        const transIncentive = generateTimeSeriesData(incentiveJson);

        console.log(transSuccessMoney);
        console.log(transIncentive);

        let options1 = {
            chart: {
                id: "chart2",
                type: "bar",
                height: 150,
                foreColor: "#ccc",
                toolbar: {
                    autoSelected: "pan",
                    show: false
                }
            },
            colors: ["#00BAEC"],
            stroke: {
                width: 3
            },
            grid: {
                borderColor: "#555",
                clipMarkers: false,
                yaxis: {
                    lines: {
                        show: false
                    }
                }
            },
            dataLabels: {
                enabled: false
            },
            fill: {
                gradient: {
                    enabled: true,
                    opacityFrom: 0.55,
                    opacityTo: 0
                }
            },
            markers: {
                size: 5,
                colors: ["#000524"],
                strokeColor: "#00BAEC",
                strokeWidth: 3
            },
            series: [
                {
                    data: transSuccessMoney
                }
            ],
            tooltip: {
                theme: "dark"
            },
            xaxis: {
                type: "datetime",
                labels:{
                    datetimeFormatter: {
                        year: 'yyyy년',
                        month: 'yy년 MM월',
                        day: 'dd일',
                        hour: 'HH:mm',
                    }
                }
            },
            yaxis: {
                min: 0,
                tickAmount: 4
            }
        };

        let chart1 = new ApexCharts(document.querySelector("#chart-area"), options1);

        chart1.render();

        let options2 = {
            chart: {
                id: "chart1",
                height: 200,
                type: "bar",
                foreColor: "#ccc",
                brush: {
                    target: "chart2",
                    enabled: true
                },
                selection: {
                    enabled: true,
                    fill: {
                        color: "#fff",
                        opacity: 0.4
                    },
                }
            },
            colors: ["#FF0080"],
            series: [{
                data:transSuccessMoney
            }],
            stroke: {
                width: 2
            },
            grid: {
                borderColor: "#444"
            },
            markers: {
                size: 0
            },
            xaxis: {
                type: "datetime",
                tooltip: {
                    enabled: false
                },
                labels:{
                    datetimeFormatter: {
                        year: 'yy년',
                        month: 'yy년 MM월',
                        day: 'dd일',
                        hour: 'HH:mm',
                    }
                }
            },
            yaxis: {
                tickAmount: 2
            }
        };

        let chart2 = new ApexCharts(document.querySelector("#chart-bar"), options2);

        chart2.render();

        let options3 = {
            chart: {
                id: "chart3",
                type: "bar",
                height: 150,
                foreColor: "#ccc",
                toolbar: {
                    autoSelected: "pan",
                    show: false
                }
            },
            colors: ["#00BAEC"],
            stroke: {
                width: 3
            },
            grid: {
                borderColor: "#555",
                clipMarkers: false,
                yaxis: {
                    lines: {
                        show: false
                    }
                }
            },
            dataLabels: {
                enabled: false
            },
            fill: {
                gradient: {
                    enabled: true,
                    opacityFrom: 0.55,
                    opacityTo: 0
                }
            },
            markers: {
                size: 5,
                colors: ["#000524"],
                strokeColor: "#00BAEC",
                strokeWidth: 3
            },
            series: [
                {
                    data: transIncentive
                }
            ],
            tooltip: {
                theme: "dark"
            },
            xaxis: {
                type: "datetime",
                labels:{
                    datetimeFormatter: {
                        year: 'yyyy년',
                        month: 'yy년 MM월',
                        day: 'dd일',
                        hour: 'HH:mm',
                    }
                }
            },
            yaxis: {
                min: 0,
                tickAmount: 4
            }
        };

        let chart3 = new ApexCharts(document.querySelector("#chart-area1"), options3);
        chart3.render();


        let options4 = {
            chart: {
                id: "chart4",
                height: 200,
                type: "bar",
                foreColor: "#ccc",
                brush: {
                    target: "chart3",
                    enabled: true
                },
                selection: {
                    enabled: true,
                    fill: {
                        color: "#fff",
                        opacity: 0.4
                    },
                }
            },
            colors: ["#FF0080"],
            series: [{
                data:transIncentive
            }],
            stroke: {
                width: 2
            },
            grid: {
                borderColor: "#444"
            },
            markers: {
                size: 0
            },
            xaxis: {
                type: "datetime",
                tooltip: {
                    enabled: false
                },
                labels:{
                    datetimeFormatter: {
                        year: 'yy년',
                        month: 'yy년 MM월',
                        day: 'dd일',
                        hour: 'HH:mm',
                    }
                }
            },
            yaxis: {
                tickAmount: 2
            }
        };

        let chart4 = new ApexCharts(document.querySelector("#chart-bar1"), options4);
        chart4.render();
    })


</script>

</html>
