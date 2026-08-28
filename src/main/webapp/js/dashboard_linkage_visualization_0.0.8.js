/**
 * 연계 실적 대시보드 시각화 스크립트.
 * 서버 주입 전역(LINKAGE_TOTALS, LINKAGE_BY_BRANCH, LINKAGE_BY_COUNSELOR, LINKAGE_BY_BRANCH_CATEGORY)으로
 * KPI 카드(연계 건수 2기준) / 지점별 스택 막대차트(일반·컨소시엄 분리, 1순위·2순위 연계 2분류 누적, 실적확정·전체 2차트) /
 * 지점·상담사별 상세 테이블을 렌더링한다.
 * 지표: 연계 건수 · 종료일 기준(실제종료일이 실적기간 내) / 실적 기간 전체(연계일 기준).
 * ※ 차트만 1순위 연계(5종)/2순위 연계(그 외 전부) 2분류로 표시. KPI·테이블·엑셀은 실적인정 5종 기준 유지.
 * + 엑셀 다운로드 버튼 핸들러(집계표를 지점별/상담사별 시트로 내려받음).
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
    const branchCategoryRows = Array.isArray(LINKAGE_BY_BRANCH_CATEGORY) ? LINKAGE_BY_BRANCH_CATEGORY : [];

    // 연계 분류 2종(고정 순서: 1순위 → 2순위) + 분류별 고정 색상.
    // 1순위 연계 = 연계유형 5종, 2순위 연계 = 그 외 전부(기타·복지연계·빈값 등).
    // ※ 문자열은 매퍼 recognizedCategoryExpr(CASE) 출력값과 바이트 단위로 일치해야 조회맵이 매칭된다.
    const LINKAGE_CATEGORIES = ['1순위 연계(일경험·희리패·심리상담)', '2순위 연계'];
    const CATEGORY_COLORS = ['#22c55e', '#70a4ee']; // 초록 = 1순위 연계, 블루 = 2순위 연계

    // 컨소시엄 지점(고정 5개). 지점 마스터에 별도 플래그가 없어 화면단 상수로 관리.
    const CONSORTIUM_BRANCHES = ['의정부', '북부', '광명', '성남', '인천'];
    const isConsortium = (b) => CONSORTIUM_BRANCHES.includes(b);
    // 지점 라벨: 컨소시엄이면 "(컨소시엄)" 접미사
    const branchLabel = (b) => isConsortium(b) ? b + '(컨소시엄)' : b;

    // 지점×분류 조회맵: byBranchCategory[지점][분류] = { term, full }
    const byBranchCategory = {};
    branchCategoryRows.forEach((r) => {
        const b = r.branch;
        const c = r.linkageCategory;
        if (!byBranchCategory[b]) {
            byBranchCategory[b] = {};
        }
        byBranchCategory[b][c] = {
            term: toNum(r.terminatedEventCount),
            full: toNum(r.fullPeriodEventCount)
        };
    });

    // ===== KPI 카드 =====
    function renderKpi() {
        document.getElementById('kpiFullPeriod').textContent = fmt(totals.fullPeriodEventCount);
        document.getElementById('kpiTerminated').textContent = fmt(totals.terminatedEventCount);
    }

    // ===== 스택 막대차트 공통 옵션 (1순위/2순위 연계 2분류 누적) =====
    function buildStackedOptions(categories, series) {
        return {
            chart: { type: 'bar', height: 340, stacked: true, toolbar: { show: false }, fontFamily: 'inherit' },
            series: series,
            xaxis: { categories: categories, labels: { style: { colors: '#64748b', fontSize: '12px' } } },
            yaxis: { min: 0, labels: { style: { colors: '#64748b', fontSize: '12px' } } },
            plotOptions: { bar: { borderRadius: 4, columnWidth: '55%' } },
            colors: CATEGORY_COLORS,
            dataLabels: { enabled: false },
            legend: { position: 'top' },
            grid: { borderColor: '#e2e8f0', strokeDashArray: 4 },
            tooltip: { theme: 'light' },
            noData: { text: '데이터가 없습니다.', style: { color: '#94a3b8', fontSize: '14px' } }
        };
    }

    /**
     * 지점 목록과 지표(term|full)로 1순위/2순위 연계 2분류 스택 series를 생성한다.
     * 데이터 없는 (지점,분류)은 0으로 채운다.
     */
    function stackedSeries(branches, metric) {
        return LINKAGE_CATEGORIES.map((c) => ({
            name: c,
            data: branches.map((b) => {
                const cell = byBranchCategory[b] && byBranchCategory[b][c];
                return cell ? cell[metric] : 0;
            })
        }));
    }

    /** 대상 요소에 스택 차트를 렌더링한다. */
    function renderStacked(elId, branches, labels, metric) {
        const el = document.getElementById(elId);
        if (!el) {
            return;
        }
        const options = buildStackedOptions(labels, stackedSeries(branches, metric));
        new ApexCharts(el, options).render();
    }

    // ===== 지점별 스택 막대차트 (일반/컨소시엄 × 실적확정/전체) =====
    function renderBranchChart() {
        // 일반 지점(컨소시엄 제외, 서버 정렬 유지)
        const generalBranches = branchRows.filter((r) => !isConsortium(r.branch)).map((r) => r.branch);
        renderStacked('linkageBranchChartTerm', generalBranches, generalBranches, 'term');
        renderStacked('linkageBranchChartFull', generalBranches, generalBranches, 'full');

        // 컨소시엄 5개 지점(고정), 라벨에 (컨소시엄) 접미사
        const consortiumLabels = CONSORTIUM_BRANCHES.map((b) => branchLabel(b));
        renderStacked('linkageConsortiumChartTerm', CONSORTIUM_BRANCHES, consortiumLabels, 'term');
        renderStacked('linkageConsortiumChartFull', CONSORTIUM_BRANCHES, consortiumLabels, 'full');
    }

    // ===== 지점별 상세 테이블 =====
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
            '<td>' + escapeHtml(branchLabel(r.branch)) + '</td>' +
            '<td class="fw-semibold">' + fmt(r.terminatedEventCount) + '</td>' +
            '<td class="fw-semibold">' + fmt(r.fullPeriodEventCount) + '</td>' +
            '</tr>'
        ).join('');
    }

    // ===== 상담사별 상세 테이블 (지점 필터 지원) =====
    function renderCounselorTable(rows) {
        const tbody = document.getElementById('counselorTableBody');
        if (!tbody) {
            return;
        }
        if (!rows || rows.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted py-3">데이터가 없습니다.</td></tr>';
            return;
        }
        tbody.innerHTML = rows.map((r, idx) =>
            '<tr>' +
            '<td>' + (idx + 1) + '</td>' +
            '<td>' + escapeHtml(branchLabel(r.branch)) + '</td>' +
            '<td>' + escapeHtml(r.counselorName || r.counselorAccount) + '</td>' +
            '<td class="fw-semibold">' + fmt(r.terminatedEventCount) + '</td>' +
            '<td class="fw-semibold">' + fmt(r.fullPeriodEventCount) + '</td>' +
            '</tr>'
        ).join('');
    }

    // ===== 상담사별 지점 필터 버튼 그룹 =====
    function renderCounselorFilter() {
        const container = document.getElementById('counselorBranchFilter');
        if (!container) {
            renderCounselorTable(counselorRows);
            return;
        }

        // 데이터에 등장하는 지점 목록(중복 제거, 등장 순서 유지)
        const branches = [];
        counselorRows.forEach((r) => {
            if (r.branch && branches.indexOf(r.branch) === -1) {
                branches.push(r.branch);
            }
        });

        const buttons = ['<button type="button" class="btn btn-sm btn-primary active" data-branch="">전체</button>']
            .concat(branches.map((b) =>
                '<button type="button" class="btn btn-sm btn-outline-secondary" data-branch="' +
                escapeHtml(b) + '">' + escapeHtml(branchLabel(b)) + '</button>'
            ));
        container.innerHTML = buttons.join('');

        container.addEventListener('click', (e) => {
            const btn = e.target.closest('button[data-branch]');
            if (!btn) {
                return;
            }
            container.querySelectorAll('button').forEach((el) => {
                el.classList.remove('btn-primary', 'active');
                el.classList.add('btn-outline-secondary');
            });
            btn.classList.remove('btn-outline-secondary');
            btn.classList.add('btn-primary', 'active');

            const selected = btn.getAttribute('data-branch');
            const filtered = selected
                ? counselorRows.filter((r) => r.branch === selected)
                : counselorRows;
            renderCounselorTable(filtered);
        });

        renderCounselorTable(counselorRows);
    }

    // ===== 엑셀 다운로드 버튼 =====
    function bindExcelDownload() {
        const btn = document.getElementById('linkageExcelBtn');
        if (btn) {
            btn.addEventListener('click', function () {
                window.location.href = '/linkageDashboardExcel.login';
            });
        }
    }

    function init() {
        renderKpi();
        renderBranchChart();
        renderBranchTable();
        renderCounselorFilter();
        bindExcelDownload();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
