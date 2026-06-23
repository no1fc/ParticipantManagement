/**
 * @file 관리자 대시보드 - 지점별 당해년도 운영 현황 (다중 지점 선택, 차트, KPI)
 * @version 0.0.3
 * @requires jQuery, SweetAlert2, ApexCharts
 */

let allDashboardData = [];
let currentSort = { column: 0, asc: true };
let chart1 = null;
let chart2 = null;
let selectedBranches = new Set();

$(document).ready(function () {
    initYearFilter();
    loadDashboardData();
});

// ===== 연도 필터 초기화 =====
function initYearFilter() {
    const currentYear = new Date().getFullYear();
    const $select = $('#yearFilter');
    for (let y = currentYear; y >= 2024; y--) {
        const label = y === currentYear ? y + '년 (당해)' : y + '년';
        $select.append('<option value="' + y + '">' + label + '</option>');
    }
    $select.on('change', function () {
        loadDashboardData();
    });
}

// ===== 지점 필터 버튼 생성 =====
function initBranchFilter(data) {
    const $container = $('#branchFilterBtns');
    $container.empty();

    // 전체 버튼
    const allActive = selectedBranches.size === 0 ? 'btn-primary' : 'btn-outline-secondary';
    $container.append(
        '<button class="btn btn-sm ' + allActive + ' mgmt-branch-btn" onclick="toggleAllBranches()" data-branch="">전체 지점</button>'
    );

    // 개별 지점 버튼
    data.forEach(function (item) {
        const active = selectedBranches.has(item.branchName) ? 'btn-primary' : 'btn-outline-secondary';
        $container.append(
            '<button class="btn btn-sm ' + active + ' mgmt-branch-btn" onclick="toggleBranch(\'' + item.branchName + '\')" data-branch="' + item.branchName + '">' +
            item.branchName +
            '</button>'
        );
    });
}

// ===== 지점 토글 (다중 선택) =====
function toggleBranch(branchName) {
    if (selectedBranches.has(branchName)) {
        selectedBranches.delete(branchName);
    } else {
        selectedBranches.add(branchName);
    }
    updateBranchBtnState();
    renderAll();
}

// ===== 전체 지점 토글 =====
function toggleAllBranches() {
    selectedBranches.clear();
    updateBranchBtnState();
    renderAll();
}

// ===== 필터 버튼 활성 상태 업데이트 =====
function updateBranchBtnState() {
    const isAllSelected = selectedBranches.size === 0;
    $('.mgmt-branch-btn').each(function () {
        const branch = $(this).data('branch');
        let isActive;
        if (branch === '' || branch === undefined) {
            isActive = isAllSelected;
        } else {
            isActive = selectedBranches.has(String(branch));
        }
        if (isActive) {
            $(this).removeClass('btn-outline-secondary').addClass('btn-primary');
        } else {
            $(this).removeClass('btn-primary').addClass('btn-outline-secondary');
        }
    });
}

// ===== 필터된 데이터 반환 =====
function getFilteredData() {
    if (selectedBranches.size === 0) {
        return [...allDashboardData];
    }
    return allDashboardData.filter(function (d) {
        return selectedBranches.has(d.branchName);
    });
}

// ===== 데이터 로드 =====
function loadDashboardData() {
    const year = $('#yearFilter').val();

    $.ajax({
        url: '/admin/api/management-dashboard',
        method: 'GET',
        data: { searchYear: year },
        success: function (data) {
            allDashboardData = data || [];
            initBranchFilter(allDashboardData);
            renderAll();
        },
        error: function (xhr) {
            if (xhr.status === 401 || xhr.status === 403) {
                Swal.fire('접근 불가', '관리자 권한이 필요합니다.', 'error');
            } else {
                Swal.fire('오류', '데이터를 불러오지 못했습니다.', 'error');
            }
        }
    });
}

// ===== 전체 렌더링 =====
function renderAll() {
    const filtered = getFilteredData();
    const sorted = getSortedData(filtered);
    renderKPICards(sorted);
    renderChart1(sorted);
    renderChart2(sorted);
    renderTable(sorted);
}

// ===== 정렬 =====
function getSortedData(data) {
    const sorted = [...data];
    sorted.sort(function (a, b) {
        let valA, valB;
        switch (currentSort.column) {
            case 0: valA = a.branchName; valB = b.branchName; break;
            case 1: valA = a.assignedCount; valB = b.assignedCount; break;
            case 2: valA = a.selfRecruitCount; valB = b.selfRecruitCount; break;
            case 3: valA = a.activeParticipantCount; valB = b.activeParticipantCount; break;
            case 4: valA = a.canceledCount; valB = b.canceledCount; break;
            case 5: valA = a.counselorWeighted; valB = b.counselorWeighted; break;
            case 6: valA = a.counselorLoad; valB = b.counselorLoad; break;
            default: valA = a.branchName; valB = b.branchName;
        }
        if (typeof valA === 'string') {
            return currentSort.asc ? valA.localeCompare(valB) : valB.localeCompare(valA);
        }
        return currentSort.asc ? valA - valB : valB - valA;
    });
    return sorted;
}

function sortTable(columnIndex) {
    if (currentSort.column === columnIndex) {
        currentSort.asc = !currentSort.asc;
    } else {
        currentSort.column = columnIndex;
        currentSort.asc = true;
    }
    renderAll();
}

// ===== KPI 카드 렌더링 =====
function renderKPICards(data) {
    if (!data || data.length === 0) {
        $('#kpiAllocated').text('-');
        $('#kpiSelfRatio').text('-');
        $('#kpiConsultRatio').text('-');
        $('#kpiCancelRatio').text('-');
        $('#kpiCounselorLoad').text('-');
        return;
    }

    let totalAssigned = 0, totalSelfRecruit = 0, totalActive = 0, totalCanceled = 0, totalCounselor = 0;
    data.forEach(function (d) {
        totalAssigned += d.assignedCount;
        totalSelfRecruit += d.selfRecruitCount;
        totalActive += d.activeParticipantCount;
        totalCanceled += d.canceledCount;
        totalCounselor += d.counselorWeighted;
    });

    $('#kpiAllocated').text(totalAssigned.toLocaleString() + '명');
    $('#kpiSelfRatio').text(totalAssigned > 0 ? ((totalSelfRecruit / totalAssigned) * 100).toFixed(1) + '%' : '0%');
    $('#kpiConsultRatio').text(totalAssigned > 0 ? ((totalActive / totalAssigned) * 100).toFixed(1) + '%' : '0%');
    $('#kpiCancelRatio').text(totalActive > 0 ? ((totalCanceled / totalActive) * 100).toFixed(1) + '%' : '0%');
    $('#kpiCounselorLoad').text(totalCounselor > 0 ? (totalActive / totalCounselor).toFixed(1) + '명/인' : '0명/인');
}

// ===== 테이블 렌더링 =====
function renderTable(data) {
    const $tbody = $('#mgmtTableBody');
    const $tfoot = $('#mgmtTableFooter');
    $tbody.empty();
    $tfoot.empty();

    if (!data || data.length === 0) {
        $tfoot.html('<td colspan="7" class="text-center text-muted py-4">필터링 결과 데이터가 없습니다.</td>');
        return;
    }

    let totalAssigned = 0, totalSelfRecruit = 0, totalActive = 0, totalCanceled = 0, totalCounselor = 0;

    data.forEach(function (row) {
        $tbody.append(
            '<tr>' +
            '<td class="fw-semibold">' + row.branchName + '</td>' +
            '<td class="text-end">' + row.assignedCount.toLocaleString() + '명</td>' +
            '<td class="text-end">' + row.selfRecruitCount.toLocaleString() + '명</td>' +
            '<td class="text-end fw-medium text-primary">' + row.activeParticipantCount.toLocaleString() + '명</td>' +
            '<td class="text-end text-danger">' + row.canceledCount.toLocaleString() + '명</td>' +
            '<td class="text-end font-monospace">' + row.counselorWeighted.toFixed(2) + '명</td>' +
            '<td class="text-end fw-bold mgmt-td-highlight">' + row.counselorLoad.toFixed(1) + '명/인</td>' +
            '</tr>'
        );

        totalAssigned += row.assignedCount;
        totalSelfRecruit += row.selfRecruitCount;
        totalActive += row.activeParticipantCount;
        totalCanceled += row.canceledCount;
        totalCounselor += row.counselorWeighted;
    });

    const totalLoad = totalCounselor > 0 ? (totalActive / totalCounselor).toFixed(1) : '0.0';

    $tfoot.html(
        '<td class="fw-bold text-primary">전체 합계</td>' +
        '<td class="text-end fw-bold">' + totalAssigned.toLocaleString() + '명</td>' +
        '<td class="text-end fw-bold">' + totalSelfRecruit.toLocaleString() + '명</td>' +
        '<td class="text-end fw-bold text-primary">' + totalActive.toLocaleString() + '명</td>' +
        '<td class="text-end fw-bold text-danger">' + totalCanceled.toLocaleString() + '명</td>' +
        '<td class="text-end fw-bold font-monospace">' + totalCounselor.toFixed(2) + '명</td>' +
        '<td class="text-end fw-bold mgmt-td-highlight">' + totalLoad + '명/인</td>'
    );
}

// ===== 차트 1: 스택형 막대 + 꺾은선 복합 =====
function renderChart1(data) {
    if (chart1) {
        chart1.destroy();
        chart1 = null;
    }

    if (!data || data.length === 0) return;

    const labels = data.map(function (d) { return d.branchName; });

    // 스택 총합 계산 (라벨 표시 기준용)
    const stackTotals = data.map(function (d) {
        return d.activeParticipantCount + d.selfRecruitCount + d.canceledCount;
    });
    const maxTotal = Math.max.apply(null, stackTotals);

    // y축 최대값: 스택 합산 최대 + 100 (10단위 올림)
    const yMax = Math.ceil((maxTotal + 100) / 10) * 10;
    const tickCount = Math.min(10, Math.max(4, Math.round(yMax / 50)));

    // 꺾은선 최대값
    const loadValues = data.map(function (d) { return d.counselorLoad; });
    const loadMax = Math.ceil(Math.max.apply(null, loadValues) + 10);

    const options = {
        series: [
            {
                name: '참여자수',
                type: 'bar',
                data: data.map(function (d) { return d.activeParticipantCount; })
            },
            {
                name: '자체모집인원수',
                type: 'bar',
                data: data.map(function (d) { return d.selfRecruitCount; })
            },
            {
                name: '취소인원',
                type: 'bar',
                data: data.map(function (d) { return d.canceledCount; })
            },
            {
                name: '상담사 1명당 초기상담 인원',
                type: 'line',
                data: loadValues
            }
        ],
        chart: {
            type: 'bar',
            height: 450,
            stacked: true,
            toolbar: { show: false },
            fontFamily: 'inherit',
            offsetY: 10
        },
        plotOptions: {
            bar: {
                columnWidth: '55%',
                borderRadius: 8,
                borderRadiusApplication: 'end',
                borderRadiusWhenStacked: 'last',
                dataLabels: {
                    total: {
                        enabled: true,
                        style: {
                            fontSize: '11px',
                            fontWeight: 700,
                            color: '#374151'
                        },
                        formatter: function (val) {
                            return val.toLocaleString() + '명';
                        }
                    }
                }
            }
        },
        colors: ['#3b82f6', '#22c55e', '#ef4444', '#f59e0b'],
        fill: {
            opacity: [1, 1, 1, 1]
        },
        stroke: {
            width: [0, 0, 0, 3],
            curve: 'smooth'
        },
        markers: {
            size: [0, 0, 0, 6],
            strokeWidth: 2,
            strokeColors: '#fff',
            hover: { size: 8 }
        },
        xaxis: {
            categories: labels,
            labels: { style: { colors: '#64748b', fontSize: '12px' } }
        },
        yaxis: [
            {
                seriesName: '참여자수',
                title: { text: '인원 수 (명)', style: { fontWeight: 600, fontSize: '12px' } },
                min: 0,
                max: yMax,
                tickAmount: tickCount,
                labels: {
                    style: { colors: '#64748b', fontSize: '12px' },
                    formatter: function (val) { return Math.round(val); }
                }
            },
            {
                seriesName: '자체모집인원수',
                show: false,
                min: 0,
                max: yMax
            },
            {
                seriesName: '취소인원',
                show: false,
                min: 0,
                max: yMax
            },
            {
                seriesName: '상담사 1명당 초기상담 인원',
                opposite: true,
                title: { text: '상담사 1명당 초기상담 인원 (명/인)', style: { fontWeight: 600, fontSize: '12px' } },
                min: 0,
                max: loadMax,
                tickAmount: tickCount,
                labels: {
                    style: { colors: '#f59e0b', fontSize: '12px' },
                    formatter: function (val) { return val.toFixed(1); }
                }
            }
        ],
        grid: {
            borderColor: '#e2e8f0',
            strokeDashArray: 4
        },
        tooltip: {
            theme: 'light',
            shared: true,
            intersect: false,
            y: {
                formatter: function (val, opts) {
                    if (opts.seriesIndex === 3) return val.toFixed(1) + '명/인';
                    return val.toLocaleString() + '명';
                }
            }
        },
        legend: {
            position: 'top',
            fontSize: '12px',
            markers: { radius: 12 }
        },
        dataLabels: {
            enabled: true,
            enabledOnSeries: [0, 1, 2],
            formatter: function (val, opts) {
                if (val === 0) return '';
                const total = stackTotals[opts.dataPointIndex];
                if (total > 0 && (val / total) < 0.05) return '';
                return val.toLocaleString();
            },
            style: {
                fontSize: '11px',
                fontWeight: 600,
                colors: ['#fff']
            }
        }
    };

    chart1 = new ApexCharts(document.querySelector('#mgmtChart1'), options);
    chart1.render();
}

// ===== 차트 2: 전년 대비 배정인원 꺾은선 =====
function renderChart2(data) {
    if (chart2) {
        chart2.destroy();
        chart2 = null;
    }

    if (!data || data.length === 0) return;

    const labels = data.map(function (d) { return d.branchName; });
    const currentYear = $('#yearFilter').val();
    const prevYear = parseInt(currentYear) - 1;

    // 전년도 데이터 로드
    $.ajax({
        url: '/admin/api/management-dashboard',
        method: 'GET',
        data: { searchYear: prevYear },
        success: function (prevData) {
            const prevAllocated = labels.map(function (branchName) {
                const match = (prevData || []).find(function (p) { return p.branchName === branchName; });
                return match ? match.assignedCount : 0;
            });

            const options = {
                series: [
                    {
                        name: '당해년도 배정인원 (' + currentYear + '년)',
                        data: data.map(function (d) { return d.assignedCount; })
                    },
                    {
                        name: '전년도 배정인원 (' + prevYear + '년)',
                        data: prevAllocated
                    }
                ],
                chart: {
                    type: 'line',
                    height: 380,
                    toolbar: { show: false },
                    fontFamily: 'inherit'
                },
                colors: ['#1d3a5f', '#94a3b8'],
                stroke: {
                    width: [3, 2],
                    curve: 'smooth',
                    dashArray: [0, 6]
                },
                markers: {
                    size: 6,
                    strokeWidth: 2,
                    strokeColors: '#fff',
                    hover: { size: 8 }
                },
                xaxis: {
                    categories: labels,
                    labels: { style: { colors: '#64748b', fontSize: '12px' } }
                },
                yaxis: {
                    title: { text: '배정 인원 수 (명)', style: { fontWeight: 600, fontSize: '12px' } },
                    labels: {
                        style: { colors: '#64748b', fontSize: '12px' },
                        formatter: function (val) { return Math.round(val); }
                    }
                },
                grid: {
                    borderColor: '#e2e8f0',
                    strokeDashArray: 4
                },
                tooltip: {
                    theme: 'light',
                    shared: true,
                    intersect: false,
                    y: { formatter: function (val) { return val.toLocaleString() + '명'; } }
                },
                legend: {
                    position: 'top',
                    fontSize: '12px',
                    markers: { radius: 12 }
                },
                dataLabels: { enabled: false }
            };

            chart2 = new ApexCharts(document.querySelector('#mgmtChart2'), options);
            chart2.render();
        },
        error: function () {
            // 전년도 데이터 실패 시 당해년도만 표시
            const options = {
                series: [{
                    name: '당해년도 배정인원 (' + currentYear + '년)',
                    data: data.map(function (d) { return d.assignedCount; })
                }],
                chart: { type: 'line', height: 380, toolbar: { show: false }, fontFamily: 'inherit' },
                colors: ['#1d3a5f'],
                stroke: { width: 3, curve: 'smooth' },
                markers: { size: 6, strokeWidth: 2, strokeColors: '#fff' },
                xaxis: {
                    categories: labels,
                    labels: { style: { colors: '#64748b', fontSize: '12px' } }
                },
                yaxis: {
                    title: { text: '배정 인원 수 (명)' },
                    labels: {
                        style: { colors: '#64748b', fontSize: '12px' },
                        formatter: function (val) { return Math.round(val); }
                    }
                },
                grid: { borderColor: '#e2e8f0', strokeDashArray: 4 },
                tooltip: { theme: 'light', y: { formatter: function (val) { return val.toLocaleString() + '명'; } } },
                legend: { position: 'top', markers: { radius: 12 } },
                dataLabels: { enabled: false }
            };
            chart2 = new ApexCharts(document.querySelector('#mgmtChart2'), options);
            chart2.render();
        }
    });
}

// ===== 엑셀 다운로드 =====
function downloadExcel() {
    const year = $('#yearFilter').val();
    let url = '/admin/api/management-dashboard/excel?searchYear=' + year;
    window.location.href = url;
}
