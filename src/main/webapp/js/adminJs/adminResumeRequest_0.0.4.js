/**
 * @file 관리자 이력서 요청 관리 (목록 조회, 검색, 상세 조회, 상태 변경)
 * @version 0.0.4
 * @requires jQuery, SweetAlert2, DataTables, Bootstrap
 */

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
            let html = '<option value="">전체</option>';
            data.forEach(function(item) {
                html += '<option value="' + item.branchName + '">' + item.branchName + '</option>';
            });
            $('#searchBranch').html(html);
        }
    });
}

function loadCounselorOptions(branch) {
    const params = {};
    if (branch) {
        params.searchBranch = branch;
    }
    $.ajax({
        url: '/admin/api/counselors',
        method: 'GET',
        data: params,
        success: function(data) {
            let html = '<option value="">전체</option>';
            data.forEach(function(item) {
                html += '<option value="' + item.userId + '">' + item.memberName + ' (' + item.userId + ')</option>';
            });
            $('#searchCounselor').html(html);
        }
    });
}

function searchResumeRequests() {
    const params = {
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
                const statusClass = item.requestStatus == '완료' ? 'success' : (item.requestStatus == '거절' ? 'danger' : 'warning');
                const actionButtons =
                    '<button class="btn btn-sm btn-info" onclick="viewResumeRequest(' + item.resumeRegNo + ')"><i class="bi bi-eye"></i> 상세</button>' +
                    ' <button class="btn btn-sm btn-success" onclick="updateStatus(' + item.resumeRegNo + ', \'완료\')"><i class="bi bi-check"></i> 완료</button>' +
                    ' <button class="btn btn-sm btn-danger" onclick="updateStatus(' + item.resumeRegNo + ', \'거절\')"><i class="bi bi-x"></i> 거절</button>';
                // thead 13개 컬럼과 1:1 정렬: 등록번호/구직번호/참여자명/지점/상담사/기업명/담당자명/이메일/비상연락처/상태/등록일/수정일/액션
                resumeRequestTable.row.add([
                    item.resumeRegNo,                                                                              // 등록번호
                    item.resumeJobNo || '',                                                                        // 구직번호
                    item.participantName || '',                                                                    // 참여자명
                    item.branch || '',                                                                             // 지점
                    item.counselorName || '',                                                                      // 상담사
                    item.companyName || '',                                                                        // 기업명
                    item.managerName || '',                                                                        // 담당자명
                    item.managerEmail || '',                                                                       // 이메일
                    item.emergencyContact || '',                                                                   // 비상연락처
                    '<span class="badge bg-' + statusClass + '">' + (item.requestStatus || '요청') + '</span>',    // 상태
                    item.resumeRegDate || '',                                                                      // 등록일
                    item.resumeUpdateDate || '',                                                                   // 수정일
                    actionButtons                                                                                  // 액션
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
const OverlayScrollbarsObj = OverlayScrollbarsGlobal.OverlayScrollbars;
if (document.querySelector('.app-sidebar-wrapper')) {
    OverlayScrollbarsObj(document.querySelector('.app-sidebar-wrapper'), {});
}
