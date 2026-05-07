let jobPlacementTable, jobPlacementModal;

$(document).ready(function() {
    jobPlacementTable = $('#jobPlacementTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[0, 'desc']],
        pageLength: 25
    });

    jobPlacementModal = new bootstrap.Modal(document.getElementById('jobPlacementModal'));

    // 지점/상담사 목록 로드
    loadBranchOptions();

    // 초기 데이터 로드
    loadJobPlacements();

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

function searchJobPlacements() {
    var params = {
        searchBranch: $('#searchBranch').val(),
        searchCounselor: $('#searchCounselor').val(),
        searchName: $('#searchName').val(),
        searchStartDate: $('#searchStartDate').val(),
        searchEndDate: $('#searchEndDate').val()
    };
    loadJobPlacements(params);
}

function resetSearch() {
    $('#searchBranch').val('');
    $('#searchCounselor').html('<option value="">전체</option>');
    $('#searchName').val('');
    $('#searchStartDate').val('');
    $('#searchEndDate').val('');
    loadJobPlacements();
}

function loadJobPlacements(params) {
    $.ajax({
        url: '/admin/api/job-placements',
        method: 'GET',
        data: params || {},
        success: function(data) {
            jobPlacementTable.clear();
            data.forEach(function(item) {
                jobPlacementTable.row.add([
                    item.registrationNo,
                    item.participantName || '',
                    item.branch || '',
                    item.counselorName || '',
                    item.placementJobNo || '',
                    item.detailInfo || '',
                    item.recommendation || '',
                    item.placementRegDate || '',
                    item.placementUpdateDate || '',
                    '<button class="btn btn-sm btn-info" onclick="viewDetail(' + item.registrationNo + ')"><i class="bi bi-eye"></i> 상세</button>' +
                    ' <button class="btn btn-sm btn-warning" onclick="editJobPlacement(' + item.registrationNo + ')"><i class="bi bi-pencil"></i> 수정</button>' +
                    ' <button class="btn btn-sm btn-danger" onclick="deleteJobPlacement(' + item.registrationNo + ')"><i class="bi bi-trash"></i> 삭제</button>'
                ]);
            });
            jobPlacementTable.draw(false);
        },
        error: function() {
            Swal.fire('오류', '알선 상세정보 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

function openAddModal() {
    $('#jobPlacementForm')[0].reset();
    $('#modalLabel').text('알선 상세정보 추가');
    $('#registrationNo').val('').prop('readonly', false);
    jobPlacementModal.show();
}

function viewDetail(regNo) {
    $.ajax({
        url: '/admin/api/job-placements/' + regNo,
        method: 'GET',
        success: function(data) {
            $('#modalLabel').text('알선 상세정보 조회');
            $('#registrationNo').val(data.registrationNo).prop('readonly', true);
            $('#jobNo').val(data.placementJobNo);
            $('#detailInfo').val(data.detailInfo);
            $('#recommendation').val(data.recommendation);
            jobPlacementModal.show();
        },
        error: function() {
            Swal.fire('오류', '알선 상세정보 조회 중 오류가 발생했습니다.', 'error');
        }
    });
}

function editJobPlacement(regNo) {
    $.ajax({
        url: '/admin/api/job-placements/' + regNo,
        method: 'GET',
        success: function(data) {
            $('#modalLabel').text('알선 상세정보 수정');
            $('#registrationNo').val(data.registrationNo).prop('readonly', true);
            $('#jobNo').val(data.placementJobNo);
            $('#detailInfo').val(data.detailInfo);
            $('#recommendation').val(data.recommendation);
            jobPlacementModal.show();
        },
        error: function() {
            Swal.fire('오류', '알선 상세정보 조회 중 오류가 발생했습니다.', 'error');
        }
    });
}

function saveJobPlacement() {
    var formData = {
        registrationNo: $('#registrationNo').val(),
        placementJobNo: $('#jobNo').val(),
        detailInfo: $('#detailInfo').val(),
        recommendation: $('#recommendation').val()
    };

    var isEdit = $('#registrationNo').prop('readonly');
    var method = isEdit ? 'PUT' : 'POST';
    var url = isEdit ? '/admin/api/job-placements/' + formData.registrationNo : '/admin/api/job-placements';

    $.ajax({
        url: url,
        method: method,
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function(res) {
            if (res.success) {
                Swal.fire('성공', res.message || '저장되었습니다.', 'success');
                jobPlacementModal.hide();
                loadJobPlacements();
            } else {
                Swal.fire('오류', res.message || '저장 중 오류가 발생했습니다.', 'error');
            }
        },
        error: function() {
            Swal.fire('오류', '저장 중 오류가 발생했습니다.', 'error');
        }
    });
}

function deleteJobPlacement(regNo) {
    Swal.fire({
        title: '삭제 확인',
        text: '정말 이 알선정보를 삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    }).then(function(result) {
        if (result.isConfirmed) {
            $.ajax({
                url: '/admin/api/job-placements/' + regNo,
                method: 'DELETE',
                success: function(res) {
                    Swal.fire('삭제됨', res.message || '알선정보가 삭제되었습니다.', 'success');
                    loadJobPlacements();
                },
                error: function() {
                    Swal.fire('오류', '삭제 중 오류가 발생했습니다.', 'error');
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
