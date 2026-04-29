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
    <title>알선 현황 대시보드</title>
    <!-- Jobmoa 로고 이미지 -->
    <mytag:Logo/>
    <!--begin::Primary Meta Tags-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="title" content="AdminLTE v4 | Dashboard" />
    <meta name="author" content="ColorlibHQ" />
    <meta
            name="description"
            content="AdminLTE is a Free Bootstrap 5 Admin Dashboard, 30 example pages using Vanilla JS."
    />
    <script src="https://cdn.tailwindcss.com"></script>

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
    
    <!-- Custom Modern CSS (Global Design System) -->
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">
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

    <!-- sweetalert2 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
    <script src="/js/sweetAlert_0.0.1.js"></script>

</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin:::App Gnb-->
    <mytag:gnb gnb_main_header="실적관리" gnb_sub_header="지점별 알선 현황"/>
    <!--end:::App Gnb-->

    <!--begin:::App main content-->
    <!--begin::App Main-->
    <main class="app-main">
        <!--begin::App Content-->
        <div class="app-content">
            <div class="container-fluid">
                <section id="arrangementDashboard" data-endpoint="/dashboard/arrangement-data" class="rounded-3xl border border-slate-200 bg-gradient-to-br from-slate-50 via-white to-amber-50 p-6 shadow-sm">
                    <div class="flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between">
                        <div>
                            <p class="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">Counseling</p>
                            <h1 class="mt-2 text-2xl font-semibold text-slate-900">알선 현황 대시보드</h1>
                            <p class="mt-2 max-w-xl text-sm text-slate-500">년도와 월을 변경하면 지점별 알선수가 비동기로 갱신됩니다.</p>
                        </div>
                        <div class="flex flex-wrap gap-3">
                            <div class="rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 shadow-sm backdrop-blur">
                                <label class="block text-xs font-semibold text-slate-500">년도</label>
                                <select id="arrangementYear" class="mt-2 w-32 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 shadow-sm focus:border-brand-400 focus:outline-none focus:ring-2 focus:ring-brand-200"></select>
                            </div>
                            <div class="rounded-2xl border border-slate-200 bg-white/80 px-4 py-3 shadow-sm backdrop-blur">
                                <label class="block text-xs font-semibold text-slate-500">월</label>
                                <select id="arrangementMonth" class="mt-2 w-32 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 shadow-sm focus:border-brand-400 focus:outline-none focus:ring-2 focus:ring-brand-200"></select>
                            </div>
                        </div>
                    </div>

                    <div class="mt-8 grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
                        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
                            <div class="flex items-center justify-between">
                                <p class="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">전 지점 총 알선수</p>
                                <span class="rounded-full bg-teal-100 px-3 py-1 text-xs font-semibold text-brand-700">Total</span>
                            </div>
                            <div id="totalArrangementValue" class="mt-4 text-3xl font-semibold text-slate-900">0</div>
                            <p id="totalArrangementDate" class="mt-2 text-xs text-slate-500">선택 월 기준 합산</p>
                        </div>
                        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
                            <div class="flex items-center justify-between">
                                <p class="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">현재 알선 1위 지점</p>
                                <span class="rounded-full bg-amber-100 px-3 py-1 text-xs font-semibold text-amber-700">TOP 1</span>
                            </div>
                            <div class="mt-4 text-2xl font-semibold text-slate-900" id="firstRankBranch">-</div>
                            <p class="mt-2 text-xs text-slate-500">알선수/알선점수 <span id="firstRankCount">0/0.00</span></p>
                        </div>
                        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
                            <div class="flex items-center justify-between">
                                <p class="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">현재 알선 2위 지점</p>
                                <span class="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-700">TOP 2</span>
                            </div>
                            <div class="mt-4 text-2xl font-semibold text-slate-900" id="secondRankBranch">-</div>
                            <p class="mt-2 text-xs text-slate-500">알선수/알선점수 <span id="secondRankCount">0/0.00</span></p>
                        </div>
                        <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
                            <div class="flex items-center justify-between">
                                <p id="achievementRatePrevious" class="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">전월 대비 달성률</p>
                                <span class="rounded-full bg-emerald-100 px-3 py-1 text-xs font-semibold text-emerald-700">Rate</span>
                            </div>
                            <div class="mt-4 flex items-end gap-2">
                                <span id="achievementRateValue" class="text-3xl font-semibold text-slate-900">0</span>
                                <span class="text-sm font-semibold text-slate-500">%</span>
                            </div>
                            <p id="achievementPrevious" class="mt-2 text-xs text-slate-500">전월 대비 기준</p>
                        </div>
                    </div>

                    <div class="mt-8 grid grid-cols-1 gap-6 xl:grid-cols-3">
                        <div class="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm xl:col-span-2">
                            <div class="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
                                <div>
                                    <p class="text-sm font-semibold text-slate-800">지점별 알선수</p>
                                    <p class="text-xs text-slate-500">최소 0 ~ 최대 20 범위로 표시</p>
                                </div>
                                <span id="arrangementMonthLabel" class="rounded-full bg-slate-100 px-4 py-2 text-xs font-semibold text-slate-600">-</span>
                            </div>
                            <div id="arrangementChart" class="mt-4 h-80"></div>
                        </div>
                        <div class="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
                            <p class="text-sm font-semibold text-slate-800">지점 목록</p>
                            <p class="mt-1 text-xs text-slate-500">차트 기준 14개 지점</p>
                                                        <div id="branchRankList" class="mt-4 grid grid-cols-2 gap-2 text-xs text-slate-600">
                                <div data-branch="남부" data-default-index="0" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>남부</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                                <div data-branch="서부" data-default-index="1" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>서부</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                                <div data-branch="부천" data-default-index="2" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>부천</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                                <div data-branch="수원" data-default-index="3" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>수원</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                                <div data-branch="인천" data-default-index="4" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>인천</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                                <div data-branch="천호" data-default-index="5" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>천호</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                                <div data-branch="의정부" data-default-index="6" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>의정부</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                                <div data-branch="인천남부" data-default-index="7" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>인천남부</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                                <div data-branch="동대문" data-default-index="8" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>동대문</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                                <div data-branch="광명" data-default-index="9" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>광명</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                                <div data-branch="안양" data-default-index="10" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>안양</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                                <div data-branch="북부" data-default-index="11" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>북부</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                                <div data-branch="성남" data-default-index="12" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>성남</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                                <div data-branch="관악" data-default-index="13" class="branch-item flex items-center justify-between gap-2 rounded-full bg-slate-100 px-3 py-2">
                                    <span>관악</span><span class="branch-rank text-[11px] font-semibold text-slate-500">-위</span>
                                </div>
                            </div>
                            <div class="mt-6 rounded-2xl border border-dashed border-slate-200 bg-slate-50 p-4 text-xs text-slate-500">
                                데이터가 없을 경우 0으로 표시되며, 차트는 연도/월 변경 시 자동 갱신됩니다.
                            </div>
                        </div>
                    </div>
                </section>
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
        const branches = ["남부","서부","부천","수원","인천","천호","의정부","인천남부","동대문","광명","안양","북부","성남","관악"];
// 서버에서 내려온 초기 데이터를 안전하게 파싱 (EL 표현식이 빈 값일 경우 대비)
        let initialChartData;
        try {
            initialChartData = ${empty arrangementChartData ? (empty arrangementChartJson ? '[]' : arrangementChartJson) : arrangementChartData};
            if (!Array.isArray(initialChartData)) initialChartData = [];
        } catch (e) {
            console.warn("initialChartData 파싱 실패:", e);
            initialChartData = [];
        }

        let initialCardData;
        try {
            initialCardData = ${empty arrangementCardData ? '[]' : arrangementCardData};
        } catch (e) {
            console.warn("initialCardData 파싱 실패:", e);
            initialCardData = [];
        }

        let currentPercentMap = {};
        function formatScore(value) {
            if (value === null || value === undefined || isNaN(value)) {
                return "0.00";
            }
            return Number(value).toFixed(2);
        }


        const chartEl = document.querySelector("#arrangementChart");
        const chartOptions = {
            chart: {
                type: "bar",
                height: 320,
                toolbar: { show: false },
                fontFamily: "inherit"
            },
            series: [{ name: "알선수", data: [] }],
            xaxis: {
                categories: branches,
                labels: { style: { colors: "#64748b", fontSize: "12px" } }
            },
            yaxis: {
                min: 0,
                max: 20,
                tickAmount: 4,
                labels: { style: { colors: "#64748b", fontSize: "12px" } }
            },
            plotOptions: {
                bar: {
                    borderRadius: 8,
                    columnWidth: "55%",
                    dataLabels: {
                        position: "center"
                    }
                }
            },
            colors: ["#f97316"],
            dataLabels: { enabled: false },
            grid: { borderColor: "#e2e8f0", strokeDashArray: 4 },
            tooltip: { theme: "light" }
        };
        const arrangementChart = new ApexCharts(chartEl, chartOptions);
        arrangementChart.render();

        function mapChartData(data) {
            const map = {};
            data.forEach(item => {
                // 숫자가 아닌 값이 들어올 경우를 대비하여 Number()로 변환 후 NaN이면 0으로 처리
                const count = Number(item.arrangementCount);
                map[item.branch] = isNaN(count) ? 0 : count;
            });
            return branches.map(branch => map[branch] ?? 0);
        }
        function updateBranchRanks(data) {
            const percentMap = {};
            const countMap = {};
            (data || []).forEach(item => {
                percentMap[item.branch] = item.arrangementRankScorePercent;
                countMap[item.branch] = item.arrangementCount;
            });
            const items = $("#branchRankList .branch-item").get();
            items.sort(function(a, b) {
                const percentA = percentMap[$(a).data("branch")];
                const percentB = percentMap[$(b).data("branch")];
                const valueA = percentA === undefined || percentA === null ? -1 : percentA;
                const valueB = percentB === undefined || percentB === null ? -1 : percentB;
                if (valueA !== valueB) {
                    return valueB - valueA;
                }
                return ($(a).data("default-index") || 0) - ($(b).data("default-index") || 0);
            });
            $("#branchRankList").append(items);
            let rankCounter = 1;
            $("#branchRankList .branch-item").each(function() {
                const branch = $(this).data("branch");
                const percent = percentMap[branch];
                const hasValue = percent !== undefined && percent !== null;
                const rank = hasValue ? rankCounter++ : null;
                const count = countMap[branch] ?? 0;
                const percentText = formatScore(percent);
                const baseClasses = "branch-item flex items-center justify-between gap-2 rounded-full px-3 py-2";
                const rankClasses = rank === 1
                    ? "bg-amber-100 text-amber-900 ring-1 ring-amber-200"
                    : rank === 2
                        ? "bg-slate-200 text-slate-800 ring-1 ring-slate-300"
                        : rank === 3
                            ? "bg-orange-100 text-orange-900 ring-1 ring-orange-200"
                            : "bg-slate-100 text-slate-600";
                $(this).attr("class", baseClasses + " " + rankClasses);
                $(this).find("span").first().text(branch + " " + count + "/" + percentText);
                $(this).find(".branch-rank").text(rank ? rank + "위" : "-위");
            });
        }
        function updateCards(cardData) {
            const data = Array.isArray(cardData) ? cardData[0] : cardData;
            const firstCount = data && data.firstRank ? data.firstRank.arrangementCount : 0;
            const firstRate = data && data.firstRank ? data.firstRank.arrangementRankScorePercent : null;
            const secondCount = data && data.secondRank ? data.secondRank.arrangementCount : 0;
            const secondRate = data && data.secondRank ? data.secondRank.arrangementRankScorePercent : null;

            const $arrangementMonth = $("#arrangementMonth");


            if ($arrangementMonth.val() == 0) {
                $("#totalArrangementDate").empty().text("선택 년도 실적 기준 합산");
                $("#achievementRatePrevious").empty().text("전년도 대비 달성률");
                $("#achievementPrevious").empty().text("전년도 대비 기준")
                $arrangementMonth.removeClass("w-32")
                $arrangementMonth.addClass("w-40")
            }
            else {
                $("#totalArrangementDate").empty().text("선택 월 기준 합산");
                $("#achievementRatePrevious").empty().text("전월 대비 달성률");
                $("#achievementPrevious").empty().text("전월 대비 기준")
                $arrangementMonth.removeClass("w-40")
                $arrangementMonth.addClass("w-32")
            }

            $("#totalArrangementValue").text(data ? data.totalArrangement : 0);
            $("#firstRankBranch").text(data && data.firstRank ? data.firstRank.branch : "-");
            $("#firstRankCount").text(firstCount + "/" + formatScore(firstRate));
            $("#secondRankBranch").text(data && data.secondRank ? data.secondRank.branch : "-");
            $("#secondRankCount").text(secondCount + "/" + formatScore(secondRate));
            $("#achievementRateValue").text(data ? data.previousMonthAchievementRate : 0);
        }
        function updateChart(chartData) {
            // 유효한 배열인지 확인 — 문자열이나 null이 넘어온 경우 빈 배열로 대체
            if (!Array.isArray(chartData)) {
                try {
                    chartData = JSON.parse(chartData);
                } catch (e) {
                    console.warn("chartData 파싱 실패, 빈 배열로 초기화:", e);
                    chartData = [];
                }
            }
            const seriesData = mapChartData(chartData || []);
            currentPercentMap = {};
            (chartData || []).forEach(item => {
                currentPercentMap[item.branch] = item.arrangementRankScorePercent;
            });
            // NaN이 포함되지 않도록 최종 필터링
            const safeSeries = seriesData.map(v => (isNaN(v) ? 0 : v));
            try {
                let chartDefaulterUpdateOptions = {
                    series: [{ name: "알선수", data: safeSeries }],
                    yaxis: {
                        min: 0,
                        max: 20,
                        tickAmount: 10,
                        labels: { style: { colors: "#64748b", fontSize: "12px" } }
                    }
                }

                if($("#arrangementMonth").val() == 0){
                    chartDefaulterUpdateOptions = {
                        series: [{ name: "알선수", data: safeSeries }],
                        yaxis: {
                            min: 0,
                            max: 70,
                            tickAmount: 14,
                            labels: { style: { colors: "#64748b", fontSize: "12px" } }
                        }
                    }
                }

                arrangementChart.updateOptions(chartDefaulterUpdateOptions, true, true); // redrawPaths=true, animate=true
            }
            catch (e) {
                console.warn(e)
            }
            updateBranchRanks(chartData || []);
        }

        function updateMonthLabel(year, month) {
            const $arrangementMonthLabel = $("#arrangementMonthLabel")
            $arrangementMonthLabel.empty().text(year + "년 " + month + "월 기준");
            if (month == 0) {
                $arrangementMonthLabel.empty().text(year + "년 실적 기준");
            }
        }

        function loadArrangementData(year, month) {
            const endpoint = "/ajax/branchArrangement.login";
            $.ajax({
                url: endpoint,
                method: "GET",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                data: { arrangementYear: year, arrangementMonth: month },
                success: function(response) {
                    const chartData = response.arrangementChartData || response.arrangementChartJson || response;
                    const cardData = response.arrangementCardData || initialCardData;

                    // chartData, cardData가 Object 형식이 아니라면 Json.parse를 적용
                    if (typeof chartData !== "object") {
                        updateChart(JSON.parse(chartData));
                    }
                    else
                        updateChart(chartData || []);

                    if (typeof cardData !== "object") {
                        updateCards(JSON.parse(cardData));
                    }
                    else
                        updateCards(cardData || []);

                    updateMonthLabel(year, month);
                },
                error: function() {
                    updateChart(initialChartData || []);
                    updateCards(initialCardData || []);
                    updateMonthLabel(year, month);
                }
            });
        }

        function initSelectors() {
            const now = new Date();
            const currentYear = now.getFullYear();
            const currentMonth = now.getMonth() + 1;
            const yearSelect = $("#arrangementYear");
            const monthSelect = $("#arrangementMonth");

            for (let year = currentYear - 3; year <= currentYear + 1; year++) {
                yearSelect.append(new Option(year + "년", year));
            }
            monthSelect.append(new Option("실적 알선 조회", 0));
            for (let month = 1; month <= 12; month++) {
                monthSelect.append(new Option(month + "월", month));
            }

            yearSelect.val(currentYear);
            monthSelect.val(currentMonth);
        }

        initSelectors();
        updateChart(initialChartData || []);
        updateCards(initialCardData || []);
        updateMonthLabel($("#arrangementYear").val(), $("#arrangementMonth").val());

        $("#arrangementYear, #arrangementMonth").on("change", function() {
            const date = new Date();
            const $arrangementYear = $("#arrangementYear");
            const $arrangementMonth = $("#arrangementMonth");
            const year = $arrangementYear.val()
            const month = $arrangementMonth.val();
            const currentYear = date.getFullYear();
            const currentMonth = date.getMonth() + 1;

            if (!year || !month) return;
            if (year > currentYear || (year >= currentYear && month > currentMonth)) {
                /*
                * 작성되어 있는 년도가 현재 년도보다 크면 TRUE
                * (작성 년도가 현재 년도 보다 크거나 같고 작성 월이 현재 월보다 크면) TRUE
                * */
                alert("현재날짜보다 이후 날짜는 선택이 불가능합니다.")
                console.log("현재날짜보다 이후 날짜는 선택이 불가능합니다.")
                $arrangementYear.val(currentYear);
                $arrangementMonth.val(currentMonth);
                loadArrangementData(currentYear, currentMonth);
                return;
            }

            loadArrangementData(year, month);
        });
    })
</script>
</html>











