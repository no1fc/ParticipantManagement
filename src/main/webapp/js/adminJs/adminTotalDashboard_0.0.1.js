/* =============================================
   통합 관리 대시보드 JS
   adminTotalDashboard_0.0.1.js
   ============================================= */

$(document).ready(function() {
    initializeDashboard();
});

function initializeDashboard() {
    loadKPIData();
}

var performanceChart, statusChart, placementChart;

function loadKPIData() {
    $.ajax({
        url: '/admin/api/kpi',
        method: 'GET',
        success: function(res) {
            var kpi = res.kpi || {};
            $('#totalParticipants').text((kpi.totalParticipants || 0).toLocaleString() + '명');
            $('#monthlyNewAssignments').text((kpi.monthlyNewAssignments || 0) + '건');
            $('#employment').text((kpi.fiscal_employed || 0) + '명');
            $('#completed').text((kpi.fiscal_completed || 0) + '명');
            $('#employmentRate').text((kpi.employmentRate || 0).toFixed(1) + '%');

            var branchStats = res.branchStats || [];
            window._branchCategories = branchStats.map(function(b) { return b.branchLabel || b.branch || ''; });
            window._branchCounts    = branchStats.map(function(b) { return b.employmentCount || 0; });
            window._branchAlseon   = branchStats.map(function(b) { return b.placementCount || 0; });

            loadEmploymentPerformanceChart();
            loadParticipantStatusChart();
            loadJobPlacementChart();
        },
        error: function() {
            $('#totalParticipants').text('-');
            $('#monthlyNewAssignments').text('-');
            $('#employment').text('-');
            $('#completed').text('-');
            $('#employmentRate').text('-');
            loadEmploymentPerformanceChart();
            loadParticipantStatusChart();
            loadJobPlacementChart();
        }
    });
}

function loadEmploymentPerformanceChart() {
    var categories   = window._branchCategories || [];
    var employData   = window._branchCounts || [];
    var placementData = window._branchAlseon || [];

    var options = {
        chart: { type: 'bar', height: 350, toolbar: { show: true } },
        plotOptions: { bar: { horizontal: false, columnWidth: '55%' } },
        dataLabels: { enabled: true },
        stroke: { show: true, width: 2, colors: ['transparent'] },
        series: [
            { name: '취업 성공', data: employData.length > 0 ? employData : [0] },
            { name: '알선 취업', data: placementData.length > 0 ? placementData : [0] }
        ],
        xaxis: { categories: categories.length > 0 ? categories : ['-'] },
        yaxis: { title: { text: '인원 (명)' } },
        fill: { opacity: 1 },
        colors: ['#4361ee', '#4cc9f0'],
        tooltip: { y: { formatter: function(val) { return val + '명'; } } }
    };

    if (performanceChart) { performanceChart.destroy(); }
    performanceChart = new ApexCharts(document.querySelector('#employmentPerformanceChart'), options);
    performanceChart.render();
}

function loadParticipantStatusChart() {
    var categories = window._branchCategories || [];
    var counts     = window._branchCounts || [];

    var options = {
        chart: { type: 'donut', height: 350 },
        series: counts.length > 0 ? counts : [1],
        labels: categories.length > 0 ? categories : ['데이터 없음'],
        colors: ['#4361ee', '#3f37c9', '#4895ef', '#4cc9f0', '#ffc300', '#06d6a0', '#e2e3e5',
                 '#f72585', '#b5179e', '#ff9a3c', '#f77f00', '#1b9aaa', '#06d6a0', '#560bad'],
        legend: { position: 'bottom' },
        dataLabels: {
            enabled: true,
            formatter: function(val, opts) {
                return opts.w.config.series[opts.seriesIndex] + '명';
            }
        },
        tooltip: { y: { formatter: function(val) { return val + '명'; } } }
    };

    if (statusChart) { statusChart.destroy(); }
    statusChart = new ApexCharts(document.querySelector('#participantStatusChart'), options);
    statusChart.render();
}

function loadJobPlacementChart() {
    var categories = window._branchCategories || [];
    var data       = window._branchAlseon || [];

    var options = {
        chart: { type: 'bar', height: 300, toolbar: { show: false } },
        series: [{ name: '알선 취업', data: data.length > 0 ? data : [0] }],
        xaxis: { categories: categories.length > 0 ? categories : ['-'] },
        plotOptions: { bar: { horizontal: false, columnWidth: '50%' } },
        colors: ['#f72585'],
        dataLabels: { enabled: true },
        yaxis: { title: { text: '건수' } },
        tooltip: { y: { formatter: function(val) { return val + '건'; } } }
    };

    if (placementChart) { placementChart.destroy(); }
    placementChart = new ApexCharts(document.querySelector('#jobPlacementChart'), options);
    placementChart.render();
}

// OverlayScrollbars 초기화
var OverlayScrollbarsGlobalObj = OverlayScrollbarsGlobal;
var OverlayScrollbars = OverlayScrollbarsGlobalObj.OverlayScrollbars;
if (document.querySelector('.app-sidebar-wrapper')) {
    OverlayScrollbars(document.querySelector('.app-sidebar-wrapper'), {});
}