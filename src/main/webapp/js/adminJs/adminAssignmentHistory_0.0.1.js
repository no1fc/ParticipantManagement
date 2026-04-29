let csvHistoryTable, formulaHistoryTable;

$(document).ready(function() {
    // DataTable 초기화
    csvHistoryTable = $('#csvHistoryTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[0, 'desc']],
        pageLength: 15,
        scrollX: true
    });

    formulaHistoryTable = $('#formulaHistoryTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[0, 'desc']],
        pageLength: 15,
        scrollX: true
    });

    // 초기 데이터 로드
    loadCsvHistory();
    loadFormulaHistory();
});

/**
 * CSV 히스토리 로드
 * TODO: GET /admin/api/assignment-csv-history
 */
function loadCsvHistory() {
    $.ajax({
        url: '/admin/api/assignment-csv-history',
        method: 'GET',
        success: function(data) {
            csvHistoryTable.clear();
            data.forEach(function(item) {
                csvHistoryTable.row.add([
                    item.pk,
                    item.csvBranch || '',
                    item.csvWriterId || '',
                    item.rowNumber || '',
                    item.counselorId || '',
                    item.csvParticipant || '',
                    item.csvParticipationType || '',
                    item.csvGender || '',
                    item.csvBirthDate || '',
                    item.csvRecruitPath || '',
                    item.csvCareer || '',
                    item.csvEducation || '',
                    item.csvSpecificClass || '',
                    item.csvProgressStage || '',
                    item.csvTravelCounselor || '',
                    item.csvRegDate || ''
                ]);
            });
            csvHistoryTable.draw(false);
        },
        error: function() {
            Swal.fire('오류', '배정 CSV 히스토리 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

function loadFormulaHistory() {
    $.ajax({
        url: '/admin/api/assignment-formula-history',
        method: 'GET',
        success: function(data) {
            formulaHistoryTable.clear();
            data.forEach(function(item) {
                formulaHistoryTable.row.add([
                    item.pk,
                    item.formulaBranch || '',
                    item.formulaWriterId || '',
                    item.weightLoad || '',
                    item.weightProcess || '',
                    item.weightContinuous || '',
                    item.weightPace || '',
                    item.gapThreshold || '',
                    item.dailyLimit || '',
                    item.g1Limit || '',
                    item.g2Limit || '',
                    item.g3Limit || '',
                    item.formulaRegDate || ''
                ]);
            });
            formulaHistoryTable.draw(false);
        },
        error: function() {
            Swal.fire('오류', '배정 산정식 히스토리 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

// OverlayScrollbars 초기화
const {
    OverlayScrollbars,
    ScrollbarsHidingPlugin,
    SizeObserverPlugin,
    ClickScrollPlugin
} = OverlayScrollbarsGlobal;

if (document.querySelector('.app-sidebar-wrapper')) {
    OverlayScrollbars(document.querySelector('.app-sidebar-wrapper'), {});
}