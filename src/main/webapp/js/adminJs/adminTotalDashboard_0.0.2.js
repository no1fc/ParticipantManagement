/* =============================================
   통합 관리 대시보드 JS
   adminTotalDashboard_0.0.2.js
   ============================================= */

var performanceChart, statusChart, placementChart;
var placementStatsTable;

$(document).ready(function() {
    // 지점 목록 로드
    loadBranchOptions();

    // 상담사별 통계 DataTable 초기화
    placementStatsTable = $('#placementStatsTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[0, 'asc'], [2, 'asc']],
        pageLength: 25
    });

    // 지점 변경 시 상담사 목록 갱신
    $('#searchBranch').on('change', function() {
        loadCounselorOptions($(this).val());
    });

    // 초기 대시보드 로드
    initializeDashboard();
});

function loadBranchOptions() {
    $.ajax({
        url: '/admin/api/branches',
        method: 'GET',
        success: function(data) {
            var html = '<option value="">전체</option>';
            data.forEach(function(item) {
                html += '<option value="' + item.branchName + '">' + item.branchName + '</option>';
            });
            $('#searchBranch').html(html);
        }
    });
}

function loadCounselorOptions(branch) {
    var params = {};
    if (branch) {
        params.searchBranch = branch;
    }
    $.ajax({
        url: '/admin/api/counselors',
        method: 'GET',
        data: params,
        success: function(data) {
            var html = '<option value="">전체</option>';
            data.forEach(function(item) {
                html += '<option value="' + item.userId + '">' + item.memberName + ' (' + item.userId + ')</option>';
            });
            $('#searchCounselor').html(html);
        }
    });
}

function getSearchParams() {
    return {
        searchBranch: $('#searchBranch').val(),
        searchCounselor: $('#searchCounselor').val(),
        searchStartDate: $('#searchStartDate').val(),
        searchEndDate: $('#searchEndDate').val()
    };
}

function searchDashboard() {
    var params = getSearchParams();
    loadKPIData(params);
    loadPlacementStats(params);
}

function resetSearch() {
    $('#searchBranch').val('');
    $('#searchCounselor').html('<option value="">전체</option>');
    $('#searchStartDate').val('');
    $('#searchEndDate').val('');
    loadKPIData();
    loadPlacementStats();
}

function initializeDashboard() {
    loadKPIData();
    loadPlacementStats();
}

function loadKPIData(params) {
    $.ajax({
        url: '/admin/api/kpi',
        method: 'GET',
        data: params || {},
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

function loadPlacementStats(params) {
    $.ajax({
        url: '/admin/api/placement-stats',
        method: 'GET',
        data: params || {},
        success: function(data) {
            placementStatsTable.clear();
            data.forEach(function(item) {
                var rate = (item.employmentRate != null) ? item.employmentRate.toFixed(1) + '%' : '0.0%';
                placementStatsTable.row.add([
                    item.branch || '',
                    item.counselorId || '',
                    item.counselorName || '',
                    item.completedCount || 0,
                    item.employedCount || 0,
                    item.placementEmployedCount || 0,
                    rate
                ]);
            });
            placementStatsTable.draw(false);
        },
        error: function() {
            placementStatsTable.clear().draw(false);
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
var OverlayScrollbarsObj = OverlayScrollbarsGlobal.OverlayScrollbars;
if (document.querySelector('.app-sidebar-wrapper')) {
    OverlayScrollbarsObj(document.querySelector('.app-sidebar-wrapper'), {});
}
