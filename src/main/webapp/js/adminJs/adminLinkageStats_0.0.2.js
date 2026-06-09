/**
 * @file 연계 현황 관리 (지점별/상담사별 연계 통계, 점수별 분류, 차트 렌더링)
 * @version 0.0.2
 * @requires jQuery, SweetAlert2, ApexCharts
 */

let branchChart = null;
let typeChart = null;
let currentView = 'branch'; // 'branch' or 'counselor'
let currentBranch = '';
let cachedBranchStats = [];
let currentScoreFilter = null; // null, 'score3', 'score2', 'score1', 'score0'

$(document).ready(function () {
    loadBranchOptions();
    loadLinkageData();
});

// 지점 목록 로드
function loadBranchOptions() {
    $.ajax({
        url: '/admin/api/branches',
        type: 'GET',
        success: function (data) {
            const $select = $('#searchBranch');
            if (Array.isArray(data)) {
                data.forEach(function (branch) {
                    if (branch.branchUseStatus === '사용') {
                        $select.append('<option value="' + branch.branchName + '">' + branch.branchName + '</option>');
                    }
                });
            }
        }
    });
}

// 검색 파라미터
function getSearchParams() {
    return {
        searchBranch: $('#searchBranch').val(),
        searchStartDate: $('#searchStartDate').val(),
        searchEndDate: $('#searchEndDate').val()
    };
}

// 검색
function searchLinkageStats() {
    currentView = 'branch';
    currentBranch = '';
    currentScoreFilter = null;
    $('#drillDownBar').hide();
    clearScoreActive();
    loadLinkageData();
}

// 초기화
function resetSearch() {
    $('#searchBranch').val('');
    $('#searchStartDate').val('');
    $('#searchEndDate').val('');
    searchLinkageStats();
}

// 메인 데이터 로드
function loadLinkageData() {
    const params = getSearchParams();

    $.ajax({
        url: '/admin/api/linkage-stats',
        type: 'GET',
        data: params,
        success: function (res) {
            const branchStats = res.branchStats || [];
            cachedBranchStats = branchStats;

            // KPI 업데이트
            $('#totalLinkageCount').text(res.totalCount + '건');

            const typeStats = res.typeStats || [];
            if (typeStats.length > 0) {
                $('#topLinkageType').text(typeStats[0].linkageType);
            } else {
                $('#topLinkageType').text('-');
            }

            // 점수별 분류 업데이트 (KPI 카드 내)
            updateScoreCards(branchStats);

            // 차트 렌더링
            renderBranchChart(branchStats);
            renderTypeChart(typeStats);
            renderTable(branchStats, 'branch');
        },
        error: function () {
            Swal.fire('오류', '데이터를 불러올 수 없습니다.', 'error');
        }
    });
}

// ===== 점수별 분류 로직 =====

function classifyByScore(branchStats) {
    const filtered = branchStats.filter(function (b) {
        return b.branchLabel !== '테스트';
    });
    return {
        score3: filtered.filter(function (b) { return b.linkageCount >= 40; }),
        score2: filtered.filter(function (b) { return b.linkageCount >= 20 && b.linkageCount < 40; }),
        score1: filtered.filter(function (b) { return b.linkageCount >= 10 && b.linkageCount < 20; }),
        score0: filtered.filter(function (b) { return b.linkageCount < 10; })
    };
}

function updateScoreCards(branchStats) {
    const scores = classifyByScore(branchStats);
    $('#scoreCount3').text(scores.score3.length + '곳');
    $('#scoreCount2').text(scores.score2.length + '곳');
    $('#scoreCount1').text(scores.score1.length + '곳');
    $('#scoreCount0').text(scores.score0.length + '곳');
}

function filterByScore(scoreKey) {
    // 토글: 같은 필터 클릭 시 해제
    if (currentScoreFilter === scoreKey) {
        currentScoreFilter = null;
        clearScoreActive();
        renderBranchChart(cachedBranchStats);
        renderTable(cachedBranchStats, 'branch');
        return;
    }

    currentScoreFilter = scoreKey;
    currentView = 'branch';
    currentBranch = '';
    $('#drillDownBar').hide();

    // 활성 상태 표시
    clearScoreActive();
    $('[data-score="' + scoreKey + '"]').addClass('active');

    // 해당 점수 범위 데이터만 필터링
    const scores = classifyByScore(cachedBranchStats);
    const filteredData = scores[scoreKey] || [];

    renderBranchChart(filteredData);
    renderTable(filteredData, 'branch');
}

function clearScoreActive() {
    $('.score-item').removeClass('active');
}

// ===== 차트 렌더링 =====

// 지점별 막대 차트
function renderBranchChart(data) {
    if (branchChart) {
        branchChart.destroy();
    }

    const categories = data.map(function (d) { return d.branchLabel; });
    const series = data.map(function (d) { return d.linkageCount; });

    const options = {
        series: [{
            name: '연계 건수',
            data: series
        }],
        chart: {
            type: 'bar',
            height: 400,
            events: {
                dataPointSelection: function (event, chartContext, config) {
                    const branchName = categories[config.dataPointIndex];
                    drillDownToCounselor(branchName);
                }
            },
            toolbar: { show: true }
        },
        plotOptions: {
            bar: {
                horizontal: false,
                borderRadius: 4,
                columnWidth: '60%'
            }
        },
        dataLabels: {
            enabled: true,
            offsetY: -20,
            style: { fontSize: '12px', colors: ['#333'] }
        },
        xaxis: {
            categories: categories,
            title: { text: '지점' },
            labels: { rotate: -45, rotateAlways: false }
        },
        yaxis: {
            title: { text: '연계 건수' }
        },
        colors: ['#4361ee'],
        title: {
            text: '지점별 연계 현황',
            align: 'left',
            style: { fontSize: '16px', fontWeight: 700 }
        },
        tooltip: {
            y: { formatter: function (val) { return val + '건'; } }
        }
    };

    branchChart = new ApexCharts(document.querySelector('#linkageMainChart'), options);
    branchChart.render();
}

// 상담사별 드릴다운
function drillDownToCounselor(branch) {
    currentView = 'counselor';
    currentBranch = branch;

    const params = getSearchParams();
    params.searchBranch = branch;

    $('#drillDownBar').show();
    $('#drillDownBranch').text(branch + ' 지점');

    $.ajax({
        url: '/admin/api/linkage-stats/by-counselor',
        type: 'GET',
        data: params,
        success: function (data) {
            renderCounselorChart(data, branch);
            renderTable(data, 'counselor');

            // 유형 차트도 해당 지점으로 필터
            $.ajax({
                url: '/admin/api/linkage-stats/by-type',
                type: 'GET',
                data: params,
                success: function (typeData) {
                    renderTypeChart(typeData);
                }
            });
        },
        error: function () {
            Swal.fire('오류', '상담사별 데이터를 불러올 수 없습니다.', 'error');
        }
    });
}

// 상담사별 막대 차트
function renderCounselorChart(data, branch) {
    if (branchChart) {
        branchChart.destroy();
    }

    const categories = data.map(function (d) { return d.counselorName; });
    const series = data.map(function (d) { return d.linkageCount; });

    const options = {
        series: [{
            name: '연계 건수',
            data: series
        }],
        chart: {
            type: 'bar',
            height: 400,
            toolbar: { show: true }
        },
        plotOptions: {
            bar: {
                horizontal: false,
                borderRadius: 4,
                columnWidth: '60%'
            }
        },
        dataLabels: {
            enabled: true,
            style: { fontSize: '12px', colors: ['#333'] }
        },
        xaxis: {
            categories: categories,
            title: { text: '상담사' }
        },
        yaxis: {
            title: { text: '연계 건수' }
        },
        colors: ['#06d6a0'],
        title: {
            text: branch + ' 지점 - 상담사별 연계 현황',
            align: 'left',
            style: { fontSize: '16px', fontWeight: 700 }
        },
        tooltip: {
            y: { formatter: function (val) { return val + '건'; } }
        }
    };

    branchChart = new ApexCharts(document.querySelector('#linkageMainChart'), options);
    branchChart.render();
}

// 지점 뷰로 돌아가기
function backToBranchView() {
    currentView = 'branch';
    currentBranch = '';
    currentScoreFilter = null;
    clearScoreActive();
    $('#drillDownBar').hide();
    loadLinkageData();
}

// 연계유형별 도넛 차트
function renderTypeChart(data) {
    if (typeChart) {
        typeChart.destroy();
    }

    if (!data || data.length === 0) {
        $('#linkageTypeChart').html('<div class="text-center text-muted py-5">데이터가 없습니다.</div>');
        return;
    }

    const labels = data.map(function (d) { return d.linkageType; });
    const series = data.map(function (d) { return d.linkageCount; });

    const options = {
        series: series,
        chart: {
            type: 'donut',
            height: 350
        },
        labels: labels,
        colors: ['#4361ee', '#4cc9f0', '#06d6a0', '#ff9a3c', '#f72585', '#b5179e'],
        title: {
            text: '연계유형별 분포',
            align: 'left',
            style: { fontSize: '16px', fontWeight: 700 }
        },
        legend: {
            position: 'bottom',
            fontSize: '12px'
        },
        dataLabels: {
            enabled: true,
            formatter: function (val) {
                return Math.round(val) + '%';
            }
        },
        tooltip: {
            y: { formatter: function (val) { return val + '건'; } }
        }
    };

    typeChart = new ApexCharts(document.querySelector('#linkageTypeChart'), options);
    typeChart.render();
}

// 데이터 테이블 렌더링
function renderTable(data, type) {
    const $tbody = $('#linkageTableBody');
    $tbody.empty();

    if (!data || data.length === 0) {
        $tbody.html('<tr><td colspan="3" class="text-center text-muted py-3">데이터가 없습니다.</td></tr>');
        return;
    }

    // 테이블 헤더 변경
    const $thead = $('#linkageTableHead');
    if (type === 'branch') {
        $thead.html('<tr><th>순위</th><th>지점</th><th>연계 건수</th></tr>');
    } else {
        $thead.html('<tr><th>순위</th><th>상담사</th><th>연계 건수</th></tr>');
    }

    data.forEach(function (item, index) {
        const name = type === 'branch' ? item.branchLabel : item.counselorName;
        const row = '<tr>'
            + '<td>' + (index + 1) + '</td>'
            + '<td>' + name + '</td>'
            + '<td><strong>' + item.linkageCount + '</strong>건</td>'
            + '</tr>';
        $tbody.append(row);
    });
}
