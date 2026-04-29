let branchTable, branchModal;

$(document).ready(function() {
    branchTable = $('#branchTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[6, 'asc']],  // 순서 기준 정렬
        pageLength: 25
    });

    branchModal = new bootstrap.Modal(document.getElementById('branchModal'));
    loadBranches();
});

function loadBranches() {
    $.ajax({
        url: '/admin/api/branches',
        method: 'GET',
        success: function(data) {
            // 테이블 업데이트
            branchTable.clear();
            // 카드 업데이트
            let cardHtml = '';
            data.forEach(function(item) {
                branchTable.row.add([
                    item.branchNo,
                    item.branchName || '',
                    item.branchStaff || 0,
                    item.type1 || 0,
                    item.type2 || 0,
                    item.department || '',
                    item.branchOrder || '',
                    `<button class="btn btn-sm btn-warning" onclick="editBranch(${item.branchNo})"><i class="bi bi-pencil"></i> 수정</button>
                     <button class="btn btn-sm btn-danger" onclick="deleteBranch(${item.branchNo})"><i class="bi bi-trash"></i> 삭제</button>`
                ]);
                cardHtml += `
                    <div class="col-md-4">
                        <div class="branch-card">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <div class="branch-name">${item.branchName || ''}</div>
                                    <small class="text-muted">사업부: ${item.department || '-'}</small>
                                </div>
                                <button class="btn btn-sm btn-outline-primary" onclick="editBranch(${item.branchNo})">
                                    <i class="bi bi-pencil"></i>
                                </button>
                            </div>
                            <div class="branch-stat">
                                <div class="stat-item">
                                    <div class="stat-label">지점인원</div>
                                    <div class="stat-value">${item.branchStaff || 0}명</div>
                                </div>
                                <div class="stat-item">
                                    <div class="stat-label">유형1</div>
                                    <div class="stat-value">${item.type1 || 0}</div>
                                </div>
                                <div class="stat-item">
                                    <div class="stat-label">유형2</div>
                                    <div class="stat-value">${item.type2 || 0}</div>
                                </div>
                                <div class="stat-item">
                                    <div class="stat-label">순서</div>
                                    <div class="stat-value">${item.branchOrder || 0}</div>
                                </div>
                            </div>
                        </div>
                    </div>`;
            });
            branchTable.draw(false);
            $('#branchCardContainer').html(cardHtml);
        },
        error: function() {
            Swal.fire('오류', '지점 목록 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

function openAddBranchModal() {
    $('#branchForm')[0].reset();
    $('#branchModalLabel').text('지점 추가');
    $('#branchNo').val('').prop('readonly', false);
    branchModal.show();
}

function editBranch(branchNo) {
    $.ajax({
        url: '/admin/api/branches/' + branchNo,
        method: 'GET',
        success: function(data) {
            $('#branchModalLabel').text('지점 수정');
            $('#branchNo').val(data.branchNo).prop('readonly', true);
            $('#branchName').val(data.branchName);
            $('#branchStaff').val(data.branchStaff);
            $('#type1').val(data.type1);
            $('#type2').val(data.type2);
            $('#department').val(data.department);
            $('#order').val(data.branchOrder);
            branchModal.show();
        },
        error: function() {
            Swal.fire('오류', '지점 정보 조회 중 오류가 발생했습니다.', 'error');
        }
    });
}

function saveBranch() {
    const formData = {
        branchNo: $('#branchNo').val(),
        branchName: $('#branchName').val(),
        branchStaff: $('#branchStaff').val(),
        type1: $('#type1').val(),
        type2: $('#type2').val(),
        department: $('#department').val(),
        branchOrder: $('#order').val()
    };

    const isEdit = $('#branchNo').prop('readonly');
    const method = isEdit ? 'PUT' : 'POST';
    const url = isEdit ? '/admin/api/branches/' + formData.branchNo : '/admin/api/branches';

    $.ajax({
        url: url,
        method: method,
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function(res) {
            if (res.success) {
                Swal.fire('성공', res.message || '저장되었습니다.', 'success');
                branchModal.hide();
                loadBranches();
            } else {
                Swal.fire('오류', res.message || '저장 중 오류가 발생했습니다.', 'error');
            }
        },
        error: function() {
            Swal.fire('오류', '저장 중 오류가 발생했습니다.', 'error');
        }
    });
}

function deleteBranch(branchNo) {
    Swal.fire({
        title: '삭제 확인',
        text: '정말 이 지점을 삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/admin/api/branches/' + branchNo,
                method: 'DELETE',
                success: function(res) {
                    Swal.fire('삭제됨', res.message || '지점이 삭제되었습니다.', 'success');
                    loadBranches();
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