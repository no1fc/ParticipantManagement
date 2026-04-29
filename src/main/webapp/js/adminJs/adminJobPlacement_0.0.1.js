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
    loadJobPlacements();
});

function loadJobPlacements() {
    $.ajax({
        url: '/admin/api/job-placements',
        method: 'GET',
        success: function(data) {
            jobPlacementTable.clear();
            data.forEach(function(item) {
                jobPlacementTable.row.add([
                    item.registrationNo,
                    item.placementJobNo || '',
                    item.detailInfo || '',
                    item.recommendation || '',
                    item.placementRegDate || '',
                    item.placementUpdateDate || '',
                    `<button class="btn btn-sm btn-info" onclick="viewDetail(${item.registrationNo})"><i class="bi bi-eye"></i> 상세</button>
                     <button class="btn btn-sm btn-warning" onclick="editJobPlacement(${item.registrationNo})"><i class="bi bi-pencil"></i> 수정</button>
                     <button class="btn btn-sm btn-danger" onclick="deleteJobPlacement(${item.registrationNo})"><i class="bi bi-trash"></i> 삭제</button>`
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
    const formData = {
        registrationNo: $('#registrationNo').val(),
        placementJobNo: $('#jobNo').val(),
        detailInfo: $('#detailInfo').val(),
        recommendation: $('#recommendation').val()
    };

    const isEdit = $('#registrationNo').prop('readonly');
    const method = isEdit ? 'PUT' : 'POST';
    const url = isEdit ? '/admin/api/job-placements/' + formData.registrationNo : '/admin/api/job-placements';

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
    }).then((result) => {
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

const { OverlayScrollbars } = OverlayScrollbarsGlobal;
if (document.querySelector('.app-sidebar-wrapper')) {
    OverlayScrollbars(document.querySelector('.app-sidebar-wrapper'), {});
}