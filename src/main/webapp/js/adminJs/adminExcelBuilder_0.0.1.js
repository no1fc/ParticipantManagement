$(document).ready(function() {
    loadBranchOptions();
    loadCounselorOptions('');

    $('#excelSearchBranch').on('change', function() {
        loadCounselorOptions($(this).val());
    });

    $('.admin-sheet-option').on('click', function() {
        var $cb = $(this).find('input[type="checkbox"]');
        $cb.prop('checked', !$cb.is(':checked'));
        $(this).toggleClass('checked', $cb.is(':checked'));
        updateColumnPanel();
        updateSummary();
    });

    updateColumnPanel();
    updateSummary();
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
            $('#excelSearchBranch').html(html);
        }
    });
}

function loadCounselorOptions(branch) {
    $.ajax({
        url: '/admin/api/counselors',
        data: { searchBranch: branch || '' },
        success: function(data) {
            var html = '<option value="">전체</option>';
            data.forEach(function(item) {
                html += '<option value="' + item.userId + '">' + item.memberName + ' (' + item.userId + ')</option>';
            });
            $('#excelSearchCounselor').html(html);
        }
    });
}

function updateColumnPanel() {
    var mainChecked = $('input[name="excelSheet"][value="main"]').is(':checked');
    $('#columnSelectionPanel').toggle(mainChecked);
}

function toggleAllColumns(checked) {
    $('input[name="excelColumn"]').prop('checked', checked);
    $('.group-toggle').prop('checked', checked);
    updateSummary();
}

function toggleGroup(groupName, checked) {
    $('input.group-' + groupName).prop('checked', checked);
    updateSummary();
}

function getSelectedSheets() {
    var sheets = [];
    $('input[name="excelSheet"]:checked').each(function() {
        sheets.push($(this).val());
    });
    return sheets.join(',');
}

function getSelectedColumns() {
    var columns = [];
    $('input[name="excelColumn"]:checked').each(function() {
        columns.push($(this).val());
    });
    return columns.join(',');
}

function getFilterParams() {
    return {
        searchBranch: $('#excelSearchBranch').val() || '',
        searchCounselor: $('#excelSearchCounselor').val() || '',
        searchStatus: $('#excelSearchStatus').val() || '',
        searchStartDate: $('#excelSearchStartDate').val() || '',
        searchEndDate: $('#excelSearchEndDate').val() || '',
        searchClosed: $('#excelSearchClosed').val() || ''
    };
}

function resetFilters() {
    $('#excelSearchBranch').val('');
    $('#excelSearchCounselor').val('');
    $('#excelSearchStatus').val('');
    $('#excelSearchStartDate').val('');
    $('#excelSearchEndDate').val('');
    $('#excelSearchClosed').val('');
    loadCounselorOptions('');
    updateSummary();
}

function updateSummary() {
    var sheets = [];
    $('input[name="excelSheet"]:checked').each(function() {
        sheets.push($(this).closest('.admin-sheet-option').find('.sheet-name').text());
    });

    var filters = getFilterParams();
    var activeFilters = [];
    if (filters.searchBranch) activeFilters.push('지점: ' + filters.searchBranch);
    if (filters.searchCounselor) activeFilters.push('상담사: ' + $('#excelSearchCounselor option:selected').text());
    if (filters.searchStatus) activeFilters.push('진행단계: ' + filters.searchStatus);
    if (filters.searchStartDate) activeFilters.push('시작일: ' + filters.searchStartDate);
    if (filters.searchEndDate) activeFilters.push('종료일: ' + filters.searchEndDate);
    if (filters.searchClosed) activeFilters.push('마감: ' + (filters.searchClosed === '0' ? '진행중' : '마감'));

    var sheetHtml = sheets.length > 0
        ? sheets.map(function(s) { return '<span class="summary-item">' + s + '</span>'; }).join('')
        : '<span class="summary-empty">선택된 시트가 없습니다.</span>';

    if ($('input[name="excelSheet"][value="main"]').is(':checked')) {
        var colCount = $('input[name="excelColumn"]:checked').length;
        var totalCols = $('input[name="excelColumn"]').length;
        sheetHtml += ' <span class="summary-item">컬럼: ' + colCount + '/' + totalCols + '개</span>';
    }

    var filterHtml = activeFilters.length > 0
        ? activeFilters.map(function(f) { return '<span class="summary-item">' + f + '</span>'; }).join('')
        : '<span class="summary-empty">전체 데이터</span>';

    $('#summarySheets').html(sheetHtml);
    $('#summaryFilters').html(filterHtml);
}

function downloadExcel() {
    var sheets = getSelectedSheets();
    if (!sheets) {
        Swal.fire('알림', '다운로드할 시트를 1개 이상 선택해주세요.', 'warning');
        return;
    }

    if (sheets.indexOf('main') !== -1) {
        var cols = getSelectedColumns();
        if (!cols) {
            Swal.fire('알림', '참여자 기본정보 시트의 컬럼을 1개 이상 선택해주세요.', 'warning');
            return;
        }
    }

    showLoading('엑셀 파일을 생성하는 중입니다...');
    try {
        var params = getFilterParams();
        params.excelSheets = sheets;
        var queryString = $.param(params);
        if (sheets.indexOf('main') !== -1) {
            queryString += '&excelColumns=' + encodeURIComponent(getSelectedColumns());
        }
        window.location.href = '/admin/api/excel/custom-export?' + queryString;
        setTimeout(hideLoading, 3000);
    } catch (e) {
        hideLoading();
        Swal.fire('오류', '엑셀 다운로드 중 오류가 발생했습니다.', 'error');
    }
}

function showLoading(message) {
    hideLoading();
    var html = '<div id="admin-loading" class="admin-loading-overlay">' +
        '<div class="admin-loading-box">' +
        '<div class="admin-loading-spinner"></div>' +
        '<p class="admin-loading-message">' + (message || '처리 중...') + '</p>' +
        '</div></div>';
    $('body').append(html);
}

function hideLoading() {
    $('#admin-loading').remove();
}