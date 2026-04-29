let dailyReportTable;

$(document).ready(function() {
    dailyReportTable = $('#dailyReportTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[0, 'desc']],
        pageLength: 25,
        scrollX: true
    });

    loadDailyReports();
});

function loadDailyReports(params) {
    $.ajax({
        url: '/admin/api/daily-reports',
        method: 'GET',
        data: params || {},
        success: function(data) {
            dailyReportTable.clear();
            let sumDailyGeneral = 0, sumDailyPlacement = 0;
            let sumMonthGeneral = 0, sumMonthPlacement = 0;
            data.forEach(function(item) {
                dailyReportTable.row.add([
                    item.pk,
                    item.reportBranch || '',
                    item.reportUserId || '',
                    item.type1 || 0,
                    item.type2 || 0,
                    item.dailyGeneralEmp || 0,
                    item.dailyPlacementEmp || 0,
                    item.weekGeneralEmp || 0,
                    item.weekPlacementEmp || 0,
                    item.monthGeneralEmp || 0,
                    item.monthPlacementEmp || 0,
                    item.yearGeneralEmp || 0,
                    item.yearPlacementEmp || 0,
                    item.reportDate || '',
                    item.reportRegDate || ''
                ]);
                sumDailyGeneral += (item.dailyGeneralEmp || 0);
                sumDailyPlacement += (item.dailyPlacementEmp || 0);
                sumMonthGeneral += (item.monthGeneralEmp || 0);
                sumMonthPlacement += (item.monthPlacementEmp || 0);
            });
            dailyReportTable.draw(false);
            $('#statDailyGeneral').text(sumDailyGeneral + '명');
            $('#statDailyPlacement').text(sumDailyPlacement + '명');
            $('#statMonthGeneral').text(sumMonthGeneral + '명');
            $('#statMonthPlacement').text(sumMonthPlacement + '명');
        },
        error: function() {
            Swal.fire('오류', '일일업무보고 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

function searchReports() {
    const params = {
        searchBranch: $('#searchBranch').val(),
        searchUserId: $('#searchUserId').val(),
        reportDate: $('#searchReportDate').val()
    };
    loadDailyReports(params);
}

const { OverlayScrollbars } = OverlayScrollbarsGlobal;
if (document.querySelector('.app-sidebar-wrapper')) {
    OverlayScrollbars(document.querySelector('.app-sidebar-wrapper'), {});
}