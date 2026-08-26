/**
 * 연계 실적 대시보드 시각화 스크립트.
 * 서버가 주입한 전역(LINKAGE_TOTALS, LINKAGE_BY_BRANCH, LINKAGE_BY_COUNSELOR)을 기반으로
 * KPI 카드 / 지점별 막대차트(참여자수·건수 토글) / 지점·상담사별 상세 테이블을 렌더링한다.
 */
(function () {
    'use strict';

    /** 숫자 안전 변환 (NaN/null → 0). */
    function toNum(value) {
        const n = Number(value);
        return isNaN(n) ? 0 : n;
    }

    /** 천단위 콤마 포맷. */
    function fmt(value) {
        return toNum(value).toLocaleString('ko-KR');
    }

    /** HTML 이스케이프 (텍스트 셀 XSS 방지). */
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text == null ? '' : String(text);
        return div.innerHTML;
    }

    const totals = (typeof LINKAGE_TOTALS === 'object' && LINKAGE_TOTALS) ? LINKAGE_TOTALS : {};
    const branchRows = Array.isArray(LINKAGE_BY_BRANCH) ? LINKAGE_BY_BRANCH : [];
    const counselorRows = Array.isArray(LINKAGE_BY_COUNSELOR) ? LINKAGE_BY_COUNSELOR : [];

    // ===== KPI 카드 =====
    function renderKpi() {
        document.getElementById('kpiLinkageParticipant').textContent = fmt(totals.linkageParticipantCount);
        document.getElementById('kpiLinkageEvent').textContent = fmt(totals.linkageEventCount);
        document.getElementById('kpiTerminatedParticipant').textContent = fmt(totals.terminatedParticipantCount);
        document.getElementById('kpiTerminatedEvent').textContent = fmt(totals.terminatedEventCount);
    }

    // ===== 지점별 막대차트 (참여자수/건수 토글) =====
    let branchChart = null;
    let currentMetric = 'participant'; // 'participant' | 'event'

    function branchSeries(metric) {
        const totalKey = metric === 'event' ? 'linkageEventCount' : 'linkageParticipantCount';
        const termKey = metric === 'event' ? 'terminatedEventCount' : 'terminatedParticipantCount';
        return [
            { name: '전체 연계', data: branchRows.map((r) => toNum(r[totalKey])) },
            { name: '종료자 연계', data: branchRows.map((r) => toNum(r[termKey])) }
        ];
    }

    function renderBranchChart() {
        const el = document.getElementById('linkageBranchChart');
        if (!el) {
            return;
        }
        const categories = branchRows.map((r) => r.branch || '-');
        const options = {
            chart: { type: 'bar', height: 380, toolbar: { show: false }, fontFamily: 'inherit' },
            series: branchSeries(currentMetric),
            xaxis: { categories: categories, labels: { style: { colors: '#64748b', fontSize: '12px' } } },
            yaxis: { min: 0, labels: { style: { colors: '#64748b', fontSize: '12px' } } },
            plotOptions: { bar: { borderRadius: 6, columnWidth: '60%' } },
            colors: ['#6366f1', '#22c55e'],
            dataLabels: { enabled: false },
            legend: { position: 'top' },
            grid: { borderColor: '#e2e8f0', strokeDashArray: 4 },
            tooltip: { theme: 'light' }
        };
        branchChart = new ApexCharts(el, options);
        branchChart.render();
    }

    function setMetric(metric) {
        currentMetric = metric;
        if (branchChart) {
            branchChart.updateSeries(branchSeries(metric), true);
        }
        const partBtn = document.getElementById('btnMetricParticipant');
        const eventBtn = document.getElementById('btnMetricEvent');
        const isPart = metric === 'participant';
        partBtn.className = 'btn ' + (isPart ? 'btn-primary' : 'btn-outline-primary');
        eventBtn.className = 'btn ' + (isPart ? 'btn-outline-primary' : 'btn-primary');
    }

    // ===== 상세 테이블 =====
    function renderBranchTable() {
        const tbody = document.getElementById('branchTableBody');
        if (!tbody) {
            return;
        }
        if (branchRows.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-3">데이터가 없습니다.</td></tr>';
            return;
        }
        tbody.innerHTML = branchRows.map((r, idx) =>
            '<tr>' +
            '<td>' + (idx + 1) + '</td>' +
            '<td>' + escapeHtml(r.branch) + '</td>' +
            '<td class="text-end border-start fw-semibold">' + fmt(r.linkageParticipantCount) + '</td>' +
            '<td class="text-end text-muted">' + fmt(r.linkageEventCount) + '</td>' +
            '<td class="text-end border-start fw-semibold">' + fmt(r.terminatedParticipantCount) + '</td>' +
            '<td class="text-end text-muted">' + fmt(r.terminatedEventCount) + '</td>' +
            '</tr>'
        ).join('');
    }

    function renderCounselorTable() {
        const tbody = document.getElementById('counselorTableBody');
        if (!tbody) {
            return;
        }
        if (counselorRows.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-3">데이터가 없습니다.</td></tr>';
            return;
        }
        tbody.innerHTML = counselorRows.map((r, idx) =>
            '<tr>' +
            '<td>' + (idx + 1) + '</td>' +
            '<td>' + escapeHtml(r.branch) + '</td>' +
            '<td>' + escapeHtml(r.counselorName || r.counselorAccount) + '</td>' +
            '<td class="text-end border-start fw-semibold">' + fmt(r.linkageParticipantCount) + '</td>' +
            '<td class="text-end text-muted">' + fmt(r.linkageEventCount) + '</td>' +
            '<td class="text-end border-start fw-semibold">' + fmt(r.terminatedParticipantCount) + '</td>' +
            '<td class="text-end text-muted">' + fmt(r.terminatedEventCount) + '</td>' +
            '</tr>'
        ).join('');
    }

    function bindEvents() {
        const partBtn = document.getElementById('btnMetricParticipant');
        const eventBtn = document.getElementById('btnMetricEvent');
        if (partBtn) {
            partBtn.addEventListener('click', () => setMetric('participant'));
        }
        if (eventBtn) {
            eventBtn.addEventListener('click', () => setMetric('event'));
        }
    }

    function init() {
        renderKpi();
        renderBranchChart();
        renderBranchTable();
        renderCounselorTable();
        bindEvents();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
