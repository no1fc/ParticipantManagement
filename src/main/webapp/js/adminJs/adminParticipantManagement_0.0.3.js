/**
 * 참여자 관리 페이지 JavaScript
 * Version: 0.0.3
 * Description: 참여자 목록 조회, 검색, 상세 오프캔버스, 엑셀 다운로드
 */

let participantTable;
let participantOffcanvas;

$(document).ready(function() {
    participantTable = $('#participantTable').DataTable({
        language: { url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json' },
        order: [[1, 'desc']],
        pageLength: 25,
        scrollX: true,
        autoWidth: false,
        columnDefs: [
            { orderable: false, targets: [0, 10] }
        ]
    });

    participantOffcanvas = new bootstrap.Offcanvas(document.getElementById('participantOffcanvas'));

    loadBranchOptions();
    loadCounselorOptions('');
    loadParticipants();

    $('#searchBranch').on('change', function() {
        loadCounselorOptions($(this).val());
    });

    // 전체 선택 체크박스
    $('#selectAll').on('change', function() {
        $('.row-check').prop('checked', $(this).is(':checked'));
    });

    // 고급검색 토글
    $('#btnToggleAdvanced').on('click', function() {
        var $adv = $('#advancedFilters');
        $adv.toggleClass('show');
        $(this).html($adv.hasClass('show') ? '<i class="bi bi-chevron-up"></i> 접기' : '<i class="bi bi-chevron-down"></i> 고급검색');
    });
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
    $.ajax({
        url: '/admin/api/counselors',
        data: { searchBranch: branch || '' },
        success: function(data) {
            var html = '<option value="">전체</option>';
            data.forEach(function(item) {
                html += '<option value="' + item.userId + '">' + item.memberName + ' (' + item.userId + ')</option>';
            });
            $('#searchCounselor').html(html);
        }
    });
}

function getParticipantSearchParams() {
    return {
        searchJobNo: $('#searchJobNo').val(),
        searchName: $('#searchName').val(),
        searchBranch: $('#searchBranch').val(),
        searchCounselor: $('#searchCounselor').val(),
        searchStatus: $('#searchStatus').val(),
        searchStartDate: $('#searchStartDate').val(),
        searchEndDate: $('#searchEndDate').val(),
        searchClosed: $('#searchClosed').val()
    };
}

function searchParticipants() {
    loadParticipants(getParticipantSearchParams());
}

function resetParticipantFilters() {
    $('#searchJobNo').val('');
    $('#searchName').val('');
    $('#searchBranch').val('');
    $('#searchCounselor').val('');
    $('#searchStatus').val('');
    $('#searchStartDate').val('');
    $('#searchEndDate').val('');
    $('#searchClosed').val('');
    loadCounselorOptions('');
    loadParticipants();
}

function loadParticipants(params) {
    $.ajax({
        url: '/admin/api/participants',
        method: 'GET',
        data: params || {},
        success: function(data) {
            participantTable.clear();
            data.forEach(function(item) {
                var closedBadge = item.closed
                    ? '<span class="admin-status-badge badge-closed">마감</span>'
                    : '<span class="admin-status-badge badge-active">진행중</span>';

                var stageBadge = '';
                if (item.progressStage === '취업') {
                    stageBadge = '<span class="admin-status-badge badge-employed">' + item.progressStage + '</span>';
                } else if (item.progressStage === 'IAP후') {
                    stageBadge = '<span class="admin-status-badge badge-iap-after">' + item.progressStage + '</span>';
                } else if (item.progressStage === 'IAP전') {
                    stageBadge = '<span class="admin-status-badge badge-iap-before">' + item.progressStage + '</span>';
                } else {
                    stageBadge = '<span class="admin-status-badge badge-active">' + (item.progressStage || '') + '</span>';
                }

                var counselorDisplay = (item.counselorName || '') + (item.counselorAccount ? ' (' + item.counselorAccount + ')' : '');

                participantTable.row.add([
                    '<input type="checkbox" class="form-check-input row-check" value="' + item.jobNo + '">',
                    item.jobNo,
                    item.participantName || '',
                    item.birthDate || '',
                    item.gender || '',
                    item.branch || '',
                    counselorDisplay,
                    stageBadge,
                    item.participantRegDate || '',
                    closedBadge,
                    '<button class="admin-icon-btn" onclick="openParticipantDetail(' + item.jobNo + ')" title="상세 조회"><i class="bi bi-eye"></i></button>'
                ]);
            });
            participantTable.draw(false);
            updateParticipantMetrics(data);
        },
        error: function() {
            Swal.fire('오류', '참여자 목록 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

function updateParticipantMetrics(data) {
    var total = data.length;
    var active = 0, closed = 0, employed = 0, recent = 0;
    var now = new Date();
    var sevenDaysAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);

    data.forEach(function(item) {
        if (item.closed) { closed++; } else { active++; }
        if (item.progressStage === '취업') { employed++; }
        if (item.participantRegDate) {
            var regDate = new Date(item.participantRegDate);
            if (regDate >= sevenDaysAgo) { recent++; }
        }
    });

    $('#metricTotal').text(total);
    $('#metricActive').text(active);
    $('#metricClosed').text(closed);
    $('#metricEmployed').text(employed);
    $('#metricRecent').text(recent);
}

function openParticipantDetail(jobNo) {
    $.ajax({
        url: '/admin/api/participants/' + jobNo,
        method: 'GET',
        success: function(data) {
            // 기본 정보
            $('#detailJobNo').text(data.jobNo || '');
            $('#detailName').text(data.participantName || '');
            $('#detailBirthDate').text(data.birthDate || '');
            $('#detailGender').text(data.gender || '');
            $('#detailBranch').text(data.branch || '');
            $('#detailRecruitPath').text(data.recruitPath || '');
            $('#detailParticipationType').text(data.participationType || '');

            // 학력/경력
            $('#detailEducation').text(data.education || '');
            $('#detailCareer').text(data.career || '');
            $('#detailSpecialClass').text(data.specialClass || '');
            $('#detailEmploymentCapacity').text(data.employmentCapacity || '');

            // 상담 정보
            $('#detailCounselor').text((data.counselorName || '') + (data.counselorAccount ? ' (' + data.counselorAccount + ')' : ''));
            $('#detailProgressStage').text(data.progressStage || '');

            // 희망 정보
            $('#detailDesiredJob').text(data.desiredJob || '');
            $('#detailDesiredSalary').text(data.desiredSalary || '');

            // 메모
            $('#detailMemo').text(data.memo || '');

            // offcanvas 제목
            $('#offcanvasTitle').text(data.participantName + ' (' + data.jobNo + ')');

            participantOffcanvas.show();
        },
        error: function() {
            Swal.fire('오류', '참여자 정보 조회 중 오류가 발생했습니다.', 'error');
        }
    });
}

function exportToExcel() {
    showLoading('엑셀 파일을 준비하는 중입니다...');
    try {
        var params = $.param({
            searchJobNo: $('#searchJobNo').val() || '',
            searchName: $('#searchName').val() || '',
            searchBranch: $('#searchBranch').val() || '',
            searchCounselor: $('#searchCounselor').val() || '',
            searchStatus: $('#searchStatus').val() || '',
            searchStartDate: $('#searchStartDate').val() || '',
            searchEndDate: $('#searchEndDate').val() || '',
            searchClosed: $('#searchClosed').val() || ''
        });
        window.location.href = '/admin/api/participants/export?' + params;
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
