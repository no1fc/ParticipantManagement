let userTable;
let userModal;

$(document).ready(function() {
    // DataTable 초기화
    userTable = $('#userTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[0, 'asc']],
        pageLength: 25,
        responsive: true
    });

    // 모달 초기화
    userModal = new bootstrap.Modal(document.getElementById('userModal'));

    // 지점 목록 로드
    loadBranchOptions();

    // 초기 데이터 로드
    loadUsers();
});

function loadBranchOptions() {
    $.ajax({
        url: '/admin/api/branches',
        method: 'GET',
        success: function(data) {
            let searchHtml = '<option value="">전체</option>';
            let modalHtml = '<option value="">선택</option>';
            data.forEach(function(item) {
                const opt = `<option value="${item.branchName}">${item.branchName}</option>`;
                searchHtml += opt;
                modalHtml += opt;
            });
            $('#searchBranch').html(searchHtml);
            $('#userBranch').html(modalHtml);
        }
    });
}

function loadUsers(params) {
    $.ajax({
        url: '/admin/api/users',
        method: 'GET',
        data: params || {},
        success: function(data) {
            userTable.clear();
            data.forEach(function(item) {
                const adminIcon = item.isAdmin ? '<i class="bi bi-check-circle-fill text-success"></i>' : '<i class="bi bi-x-circle-fill text-muted"></i>';
                const statusBadge = item.useStatus === '사용'
                    ? `<span class="role-badge status-active">${item.useStatus}</span>`
                    : `<span class="role-badge status-inactive">${item.useStatus || '잠금'}</span>`;
                const roleBadge = item.isAdmin ? `<span class="role-badge role-admin">${item.role}</span>` : `<span class="role-badge role-counselor">${item.role || '상담'}</span>`;
                userTable.row.add([
                    item.memberNo,
                    item.memberName || '',
                    item.userId || '',
                    item.branch || '',
                    roleBadge,
                    adminIcon,
                    item.hireDate || '',
                    item.continuousPeriod || '',
                    statusBadge,
                    item.assignWeight || 1.0,
                    `<button class="btn btn-sm btn-warning" onclick="editUser(${item.memberNo})"><i class="bi bi-pencil"></i> 수정</button>
                     <button class="btn btn-sm btn-danger" onclick="deleteUser(${item.memberNo})"><i class="bi bi-trash"></i> 삭제</button>
                     <button class="btn btn-sm btn-info" onclick="resetPassword(${item.memberNo})"><i class="bi bi-key"></i> 비밀번호 초기화</button>`
                ]);
            });
            userTable.draw(false);
        },
        error: function() {
            Swal.fire('오류', '사용자 목록 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

function searchUsers() {
    const params = {
        searchName: $('#searchName').val(),
        searchUserId: $('#searchUserId').val(),
        searchBranch: $('#searchBranch').val(),
        searchRole: $('#searchRole').val()
    };
    loadUsers(params);
}

/**
 * 사용자 추가 모달 열기
 */
function openAddUserModal() {
    $('#userForm')[0].reset();
    $('#userModalLabel').text('사용자 추가');
    $('#userNo').prop('readonly', false);
    $('#userPassword').val('jobmoa100!');
    userModal.show();
}

function editUser(userNo) {
    $.ajax({
        url: '/admin/api/users/' + userNo,
        method: 'GET',
        success: function(data) {
            $('#userModalLabel').text('사용자 수정');
            $('#userNo').val(data.memberNo).prop('readonly', true);
            $('#userName').val(data.memberName);
            $('#userId').val(data.userId);
            $('#userPassword').val('');
            $('#userBranch').val(data.branch);
            $('#userRole').val(data.role);
            $('#userEmail').val(data.email);
            $('#userPhone').val(data.phoneNumber);
            $('#hireDate').val(data.hireDate);
            $('#lastAssignmentDate').val(data.lastAssignDate);
            $('#serviceYears').val(data.continuousPeriod);
            $('#assignmentWeight').val(data.assignWeight);
            $('#adminAuth').prop('checked', data.isAdmin == true || data.isAdmin == 1);
            $('#performanceWrite').prop('checked', data.performanceWrite != false && data.performanceWrite != 0);
            $('#dailyReportIssue').prop('checked', data.dailyReportIssue != false && data.dailyReportIssue != 0);
            $('#userStatus').val(data.useStatus);
            $('#viewOrder').val(data.viewOrder);
            userModal.show();
        },
        error: function() {
            Swal.fire('오류', '사용자 정보 조회 중 오류가 발생했습니다.', 'error');
        }
    });
}

function saveUser() {
    const formData = {
        memberNo: $('#userNo').val(),
        memberName: $('#userName').val(),
        userId: $('#userId').val(),
        password: $('#userPassword').val(),
        branch: $('#userBranch').val(),
        role: $('#userRole').val(),
        email: $('#userEmail').val(),
        phoneNumber: $('#userPhone').val(),
        hireDate: $('#hireDate').val(),
        lastAssignDate: $('#lastAssignmentDate').val(),
        // continuousPeriod: $('#serviceYears').val(), //DB에 함수로 들어가 있기 때문에 주석처리
        assignWeight: $('#assignmentWeight').val(),
        isAdmin: $('#adminAuth').is(':checked'),
        performanceWrite: $('#performanceWrite').is(':checked'),
        dailyReportIssue: $('#dailyReportIssue').is(':checked'),
        useStatus: $('#userStatus').val(),
        viewOrder: $('#viewOrder').val()
    };

    console.log(formData);

    const isEdit = $('#userNo').prop('readonly');
    const method = isEdit ? 'PUT' : 'POST';
    const url = isEdit ? '/admin/api/users/' + formData.memberNo : '/admin/api/users';

    $.ajax({
        url: url,
        method: method,
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function(res) {
            if (res.success) {
                Swal.fire('성공', res.message || '저장되었습니다.', 'success');
                userModal.hide();
                loadUsers();
            } else {
                Swal.fire('오류', res.message || '저장 중 오류가 발생했습니다.', 'error');
            }
        },
        error: function() {
            Swal.fire('오류', '저장 중 오류가 발생했습니다.', 'error');
        }
    });
}

function deleteUser(userNo) {
    Swal.fire({
        title: '삭제 확인',
        text: '정말 이 사용자를 삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/admin/api/users/' + userNo,
                method: 'DELETE',
                success: function(res) {
                    Swal.fire('삭제됨', res.message || '사용자가 삭제되었습니다.', 'success');
                    loadUsers();
                },
                error: function() {
                    Swal.fire('오류', '삭제 중 오류가 발생했습니다.', 'error');
                }
            });
        }
    });
}

function resetPassword(userNo) {
    Swal.fire({
        title: '비밀번호 초기화',
        text: '비밀번호를 "jobmoa100!"로 초기화하시겠습니까?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '초기화',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/admin/api/users/' + userNo + '/reset-password',
                method: 'PUT',
                success: function(res) {
                    Swal.fire('완료', res.message || '비밀번호가 초기화되었습니다.', 'success');
                },
                error: function() {
                    Swal.fire('오류', '비밀번호 초기화 중 오류가 발생했습니다.', 'error');
                }
            });
        }
    });
}

/**
 * 엑셀 다운로드
 */
function exportToExcel() {
    window.location.href = '/admin/api/users/export';
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