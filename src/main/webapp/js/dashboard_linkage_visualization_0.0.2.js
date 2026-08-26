/**
 * 연계 실적 대시보드 시각화 스크립트.
 * 서버 주입 전역(LINKAGE_TOTALS, LINKAGE_BY_BRANCH, LINKAGE_BY_COUNSELOR)으로
 * KPI 카드(연계 건수 2기준) / 지점별 막대차트(2계열) / 지점·상담사별 상세 테이블을 렌더링한다.
 * 지표: 연계 건수 · 실적 기간 전체(연계일 기준) / 종료일 기준(실제종료일이 실적기간 내).
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

    const LABEL_FULL = '연계 건수 (실적 기간 전체)';
    const LABEL_TERM = '연계 건수 (종료일 기준)';

    // ===== KPI 카드 =====
    function renderKpi() {
        document.getElementById('kpiFullPeriod').textContent = fmt(totals.fullPeriodEventCount);
        document.getElementById('kpiTerminated').textContent = fmt(totals.terminatedEventCount);
    }

    // ===== 지점별 막대차트 (2계열) =====
    function renderBranchChart() {
        const el = document.getElementById('linkageBranchChart');
        if (!el) {
            return;
        }
        const categories = branchRows.map((r) => r.branch || '-');
        const options = {
            chart: { type: 'bar', height: 380, toolbar: { show: false }, fontFamily: 'inherit' },
            series: [
                { name: LABEL_FULL, data: branchRows.map((r) => toNum(r.fullPeriodEventCount)) },
                { name: LABEL_TERM, data: branchRows.map((r) => toNum(r.terminatedEventCount)) }
            ],
            xaxis: { categories: categories, labels: { style: { colors: '#64748b', fontSize: '12px' } } },
            yaxis: { min: 0, labels: { style: { colors: '#64748b', fontSize: '12px' } } },
            plotOptions: { bar: { borderRadius: 6, columnWidth: '60%' } },
            colors: ['#6366f1', '#22c55e'],
            dataLabels: { enabled: false },
            legend: { position: 'top' },
            grid: { borderColor: '#e2e8f0', strokeDashArray: 4 },
            tooltip: { theme: 'light' }
        };
        new ApexCharts(el, options).render();
    }

    // ===== 상세 테이블 =====
    function renderBranchTable() {
        const tbody = document.getElementById('branchTableBody');
        if (!tbody) {
            return;
        }
        if (branchRows.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted py-3">데이터가 없습니다.</td></tr>';
            return;
        }
        tbody.innerHTML = branchRows.map((r, idx) =>
            '<tr>' +
            '<td>' + (idx + 1) + '</td>' +
            '<td>' + escapeHtml(r.branch) + '</td>' +
            '<td class="fw-semibold">' + fmt(r.fullPeriodEventCount) + '</td>' +
            '<td class="fw-semibold">' + fmt(r.terminatedEventCount) + '</td>' +
            '</tr>'
        ).join('');
    }

    function renderCounselorTable() {
        const tbody = document.getElementById('counselorTableBody');
        if (!tbody) {
            return;
        }
        if (counselorRows.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-3">데이터가 없습니다.</td></tr>';
            return;
        }
        tbody.innerHTML = counselorRows.map((r, idx) =>
            '<tr>' +
            '<td>' + (idx + 1) + '</td>' +
            '<td>' + escapeHtml(r.branch) + '</td>' +
            '<td>' + escapeHtml(r.counselorName || r.counselorAccount) + '</td>' +
            '<td class="fw-semibold">' + fmt(r.fullPeriodEventCount) + '</td>' +
            '<td class="fw-semibold">' + fmt(r.terminatedEventCount) + '</td>' +
            '</tr>'
        ).join('');
    }

    function init() {
        renderKpi();
        renderBranchChart();
        renderBranchTable();
        renderCounselorTable();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
