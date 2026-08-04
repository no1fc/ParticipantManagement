/**
 * @file HR 인원현황 대시보드 (데모) — 읽기전용 집계 렌더링
 * @version 0.0.1
 * @requires jQuery, ApexCharts, SweetAlert2
 */

const HR_BRAND = '#1d3a5f';
const HR_TENURE_COLORS = ['#c0392b', '#e08e0b', '#2e86c1', '#1d3a5f'];

$(document).ready(function () {
    loadDashboard();
});

function loadDashboard() {
    $.ajax({
        url: '/hr/api/dashboard',
        method: 'GET',
        success: function (data) {
            renderSummary(data.summary || {});
            renderDeptChart(data.byDepartment || []);
            renderTenureChart(data.tenure || []);
            renderTimeline(data.timeline || []);
        },
        error: function () {
            Swal.fire('오류', '대시보드 데이터 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

function renderSummary(s) {
    $('#scActive').text(numberOrZero(s.active));
    $('#scOnLeave').text(numberOrZero(s.onLeave));
    $('#scResigned').text(numberOrZero(s.resigned));
    $('#scMonthlyHire').text(numberOrZero(s.monthlyHire));
    $('#scMonthlyResign').text(numberOrZero(s.monthlyResign));
}

function renderDeptChart(rows) {
    const labels = rows.map(function (r) { return r.label; });
    const values = rows.map(function (r) { return toInt(r.value); });
    const height = Math.max(260, labels.length * 26 + 40);

    const options = {
        chart: { type: 'bar', height: height, toolbar: { show: false }, fontFamily: 'inherit' },
        series: [{ name: '인원', data: values }],
        colors: [HR_BRAND],
        plotOptions: { bar: { horizontal: true, borderRadius: 3, barHeight: '65%' } },
        dataLabels: { enabled: true },
        xaxis: { categories: labels },
        grid: { borderColor: '#eef1f5' },
        noData: { text: '데이터가 없습니다.' }
    };
    renderChart('#deptChart', options);
}

function renderTenureChart(rows) {
    const labels = rows.map(function (r) { return r.label; });
    const values = rows.map(function (r) { return toInt(r.value); });

    const options = {
        chart: { type: 'donut', height: 300, fontFamily: 'inherit' },
        series: values,
        labels: labels,
        colors: HR_TENURE_COLORS,
        legend: { position: 'bottom' },
        dataLabels: { enabled: true },
        plotOptions: { pie: { donut: { size: '60%' } } },
        noData: { text: '데이터가 없습니다.' }
    };
    renderChart('#tenureChart', options);
}

function renderTimeline(rows) {
    const $body = $('#timelineBody');
    $body.empty();
    if (!rows.length) {
        $body.append('<tr><td colspan="4" class="text-center text-muted">최근 발령 이력이 없습니다.</td></tr>');
        return;
    }
    rows.forEach(function (r) {
        const tr = $('<tr></tr>');
        tr.append($('<td></td>').text(formatDate(r.eventDate)));
        tr.append($('<td></td>').text(r.name || ''));
        tr.append($('<td></td>').html('<span class="hr-event-badge">' + escapeHtml(r.eventType || '') + '</span>'));
        tr.append($('<td></td>').text(r.reason || ''));
        $body.append(tr);
    });
}

// ===== 유틸 =====
function renderChart(selector, options) {
    const el = document.querySelector(selector);
    if (!el) return;
    el.innerHTML = '';
    new ApexCharts(el, options).render();
}

function numberOrZero(v) {
    const n = toInt(v);
    return n.toLocaleString();
}

function toInt(v) {
    const n = parseInt(v, 10);
    return isNaN(n) ? 0 : n;
}

function formatDate(v) {
    if (!v) return '';
    // 서버가 'yyyy-MM-dd' 또는 ISO 문자열/타임스탬프 반환 가능 → 앞 10자리만 사용
    const s = String(v);
    return s.length >= 10 ? s.substring(0, 10) : s;
}

function escapeHtml(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}
