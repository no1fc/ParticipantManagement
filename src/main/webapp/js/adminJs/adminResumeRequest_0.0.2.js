let resumeRequestTable, resumeRequestModal;

$(document).ready(function() {
    resumeRequestTable = $('#resumeRequestTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[0, 'desc']],
        pageLength: 25
    });

    resumeRequestModal = new bootstrap.Modal(document.getElementById('resumeRequestModal'));

    // 지점/상담사 목록 로드
    loadBranchOptions();

    // 초기 데이터 로드
    loadResumeRequests();

    // 지점 변경 시 상담사 목록 갱신
    $('#searchBranch').on('change', function() {
        loadCounselorOptions($(this).val());
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
    var params = {};
    if (branch) {
        params.searchBranch = branch;
    }
    $.ajax({
        url: '/admin/api/counselors',
        method: 'GET',
        data: params,
        success: function(data) {
            var html = '<option value="">전체</option>';
            data.forEach(function(item) {
                html += '<option value="' + item.userId + '">' + item.memberName + ' (' + item.userId + ')</option>';
            });
            $('#searchCounselor').html(html);
        }
    });
}

function searchResumeRequests() {
    var params = {
        searchBranch: $('#searchBranch').val(),
        searchCounselor: $('#searchCounselor').val(),
        searchName: $('#searchName').val(),
        searchStatus: $('#searchStatus').val(),
        searchStartDate: $('#searchStartDate').val(),
        searchEndDate: $('#searchEndDate').val()
    };
    loadResumeRequests(params);
}

function resetSearch() {
    $('#searchBranch').val('');
    $('#searchCounselor').html('<option value="">전체</option>');
    $('#searchName').val('');
    $('#searchStatus').val('');
    $('#searchStartDate').val('');
    $('#searchEndDate').val('');
    loadResumeRequests();
}

function loadResumeRequests(params) {
    $.ajax({
        url: '/admin/api/resume-requests',
        method: 'GET',
        data: params || {},
        success: function(data) {
            resumeRequestTable.clear();
            data.forEach(function(item) {
                var statusClass = item.requestStatus == '완료' ? 'success' : (item.requestStatus == '거절' ? 'danger' : 'warning');
                resumeRequestTable.row.add([
                    item.resumeRegNo,
                    item.participantName || '',
                    item.branch || '',
                    item.counselorName || '',
                    item.resumeJobNo || '',
                    item.companyName || '',
                    item.managerName || '',
                    '<span class="badge bg-' + statusClass + '">' + (item.requestStatus || '요청') + '</span>',
                    item.resumeRegDate || '',
                    item.resumeUpdateDate || '',
                    '<button class="btn btn-sm btn-info" onclick="viewResumeRequest(' + item.resumeRegNo + ')"><i class="bi bi-eye"></i> 상세</button>' +
                    ' <button class="btn btn-sm btn-success" onclick="updateStatus(' + item.resumeRegNo + ', \'완료\')"><i class="bi bi-check"></i> 완료</button>' +
                    ' <button class="btn btn-sm btn-danger" onclick="updateStatus(' + item.resumeRegNo + ', \'거절\')"><i class="bi bi-x"></i> 거절</button>'
                ]);
            });
            resumeRequestTable.draw(false);
        },
        error: function() {
            Swal.fire('오류', '이력서 요청 목록 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

function viewResumeRequest(regNo) {
    $.ajax({
        url: '/admin/api/resume-requests/' + regNo,
        method: 'GET',
        success: function(data) {
            $('#detail-participantName').text(data.participantName || '-');
            $('#detail-branch').text(data.branch || '-');
            $('#detail-counselorName').text(data.counselorName || '-');
            $('#detail-jobNo').text(data.resumeJobNo || '-');
            $('#detail-company').text(data.companyName || '-');
            $('#detail-manager').text(data.managerName || '-');
            $('#detail-email').text(data.managerEmail || '-');
            $('#detail-phone').text(data.emergencyContact || '-');
            $('#detail-status').text(data.requestStatus || '-');
            $('#detail-request').text(data.otherRequests || '-');
            $('#detail-privacy1').text(data.companyPrivacyConsent ? '동의' : '미동의');
            $('#detail-privacy2').text(data.managerPrivacyConsent ? '동의' : '미동의');
            $('#detail-marketing').text(data.marketingConsent ? '동의' : '미동의');
            resumeRequestModal.show();
        },
        error: function() {
            Swal.fire('오류', '이력서 요청 상세 조회 중 오류가 발생했습니다.', 'error');
        }
    });
}

function updateStatus(regNo, newStatus) {
    Swal.fire({
        title: '상태 변경',
        text: '상태를 "' + newStatus + '"로 변경하시겠습니까?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '변경',
        cancelButtonText: '취소'
    }).then(function(result) {
        if (result.isConfirmed) {
            $.ajax({
                url: '/admin/api/resume-requests/' + regNo + '/status',
                method: 'PUT',
                data: JSON.stringify({ requestStatus: newStatus }),
                contentType: 'application/json',
                success: function(res) {
                    Swal.fire('완료', res.message || '상태가 변경되었습니다.', 'success');
                    loadResumeRequests();
                },
                error: function() {
                    Swal.fire('오류', '상태 변경 중 오류가 발생했습니다.', 'error');
                }
            });
        }
    });
}

// OverlayScrollbars 초기화
var OverlayScrollbarsObj = OverlayScrollbarsGlobal.OverlayScrollbars;
if (document.querySelector('.app-sidebar-wrapper')) {
    OverlayScrollbarsObj(document.querySelector('.app-sidebar-wrapper'), {});
}
