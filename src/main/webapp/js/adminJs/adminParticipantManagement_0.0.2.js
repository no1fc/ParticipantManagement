let participantTable;
let participantModal;

$(document).ready(function() {
    participantTable = $('#participantTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[0, 'desc']],
        pageLength: 25,
        responsive: true
    });

    participantModal = new bootstrap.Modal(document.getElementById('participantModal'));

    loadBranchOptions();
    loadCounselorOptions('');
    loadParticipants();

    $('#searchBranch').on('change', function() {
        loadCounselorOptions($(this).val());
    });
});

function loadBranchOptions() {
    $.ajax({
        url: '/admin/api/branches',
        method: 'GET',
        success: function(data) {
            let html = '<option value="">전체</option>';
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
            let html = '<option value="">전체</option>';
            data.forEach(function(item) {
                html += '<option value="' + item.userId + '">' + item.memberName + ' (' + item.userId + ')</option>';
            });
            $('#searchCounselor').html(html);
        }
    });
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
                    ? '<span class="status-badge status-closed">마감</span>'
                    : '<span class="status-badge status-active">진행중</span>';
                var counselorDisplay = (item.counselorName || '') + (item.counselorAccount ? ' (' + item.counselorAccount + ')' : '');
                participantTable.row.add([
                    item.jobNo,
                    item.participantName || '',
                    item.birthDate || '',
                    item.gender || '',
                    item.branch || '',
                    counselorDisplay,
                    '<span class="status-badge status-active">' + (item.progressStage || '') + '</span>',
                    item.participantRegDate || '',
                    closedBadge,
                    '<button class="btn btn-sm btn-info action-btn" onclick="viewDetail(' + item.jobNo + ')"><i class="bi bi-eye"></i> 상세</button>'
                ]);
            });
            participantTable.draw(false);
        },
        error: function() {
            Swal.fire('오류', '참여자 목록 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

function searchParticipants() {
    var params = {
        searchJobNo: $('#searchJobNo').val(),
        searchName: $('#searchName').val(),
        searchBranch: $('#searchBranch').val(),
        searchCounselor: $('#searchCounselor').val(),
        searchStatus: $('#searchStatus').val(),
        searchStartDate: $('#searchStartDate').val(),
        searchEndDate: $('#searchEndDate').val(),
        searchClosed: $('#searchClosed').val()
    };
    loadParticipants(params);
}

function viewDetail(jobNo) {
    $.ajax({
        url: '/admin/api/participants/' + jobNo,
        method: 'GET',
        success: function(data) {
            $('#participantModalLabel').text('참여자 상세 조회');
            $('#jobNo').val(data.jobNo).prop('readonly', true);
            $('#participantName').val(data.participantName).prop('readonly', true);
            $('#birthDate').val(data.birthDate).prop('readonly', true);
            $('#gender').val(data.gender).prop('disabled', true);
            $('#branch').val(data.branch).prop('disabled', true);
            $('#counselorAccount').val(data.counselorAccount).prop('readonly', true);
            $('#progressStage').val(data.progressStage).prop('disabled', true);
            $('#recruitPath').val(data.recruitPath).prop('readonly', true);
            $('#participationType').val(data.participationType).prop('readonly', true);
            $('#education').val(data.education).prop('readonly', true);
            $('#career').val(data.career).prop('readonly', true);
            $('#specialClass').val(data.specialClass).prop('readonly', true);
            $('#employmentCapacity').val(data.employmentCapacity).prop('readonly', true);
            $('#desiredJob').val(data.desiredJob).prop('readonly', true);
            $('#desiredSalary').val(data.desiredSalary).prop('readonly', true);
            $('#memo').val(data.memo).prop('readonly', true);
            participantModal.show();
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
        Swal.fire('오류', '엑셀 다운로드 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.', 'error');
    }
}

function showLoading(message) {
    hideLoading();
    var overlay = document.createElement('div');
    overlay.id = 'loading-overlay';
    overlay.style.cssText = 'position:fixed;top:0;left:0;width:100%;height:100%;background-color:rgba(0,0,0,0.5);display:flex;justify-content:center;align-items:center;z-index:9999;';
    var loadingBox = document.createElement('div');
    loadingBox.style.cssText = 'background-color:white;padding:20px;border-radius:8px;box-shadow:0 4px 8px rgba(0,0,0,0.2);text-align:center;max-width:80%;';
    var spinner = document.createElement('div');
    spinner.style.cssText = 'border:4px solid #f3f3f3;border-top:4px solid #3498db;border-radius:50%;width:40px;height:40px;margin:0 auto 15px;animation:spin 1s linear infinite;';
    var style = document.createElement('style');
    style.textContent = '@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }';
    document.head.appendChild(style);
    var messageElem = document.createElement('p');
    messageElem.style.margin = '0';
    messageElem.textContent = message || '처리 중...';
    loadingBox.appendChild(spinner);
    loadingBox.appendChild(messageElem);
    overlay.appendChild(loadingBox);
    document.body.appendChild(overlay);
}

function hideLoading() {
    var overlay = document.getElementById('loading-overlay');
    if (overlay) {
        document.body.removeChild(overlay);
    }
}
