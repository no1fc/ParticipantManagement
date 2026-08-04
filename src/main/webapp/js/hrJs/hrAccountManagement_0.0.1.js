/**
 * @file HR 계정 관리 (데모) — J_직원_계정 상태변경·비번초기화·잠금해제
 * @version 0.0.1
 * @requires jQuery, SweetAlert2, DataTables, Bootstrap, OverlayScrollbars
 */

let accountTable, statusModal, resetModal;

const STATUS_BADGE = {
    '사용': 'hr-st-on',
    '정지': 'hr-st-stop',
    '잠금': 'hr-st-lock',
    '퇴사': 'hr-st-quit',
    '승인대기': 'hr-st-wait'
};

$(document).ready(function () {
    accountTable = $('#accountTable').DataTable({
        language: { url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json' },
        order: [[0, 'asc']],
        pageLength: 25
    });
    statusModal = new bootstrap.Modal(document.getElementById('statusModal'));
    resetModal = new bootstrap.Modal(document.getElementById('resetModal'));
    loadAccounts();
});

function currentSearch() {
    return {
        searchUserId: $('#searchUserId').val(),
        searchUserName: $('#searchUserName').val(),
        searchAccountStatus: $('#searchAccountStatus').val()
    };
}

function loadAccounts() {
    $.ajax({
        url: '/hr/api/accounts',
        method: 'GET',
        data: currentSearch(),
        success: function (data) {
            accountTable.clear();
            data.forEach(function (item) {
                const badgeClass = STATUS_BADGE[item.accountStatus] || 'hr-st-quit';
                const statusBadge = `<span class="hr-badge ${badgeClass}">${item.accountStatus || ''}</span>`;
                const pwBadge = item.hasPassword === 1
                    ? '<span class="hr-badge hr-st-on">설정</span>'
                    : '<span class="hr-badge hr-st-quit">미설정</span>';
                const uid = item.userId.replace(/'/g, "\\'");
                accountTable.row.add([
                    item.userId,
                    item.userName || '',
                    statusBadge,
                    item.loginFailCount != null ? item.loginFailCount : 0,
                    pwBadge,
                    item.pwChangeDate || '-',
                    item.lastLoginAt || '-',
                    `<button class="btn btn-sm btn-primary" onclick="openStatusModal('${uid}','${item.accountStatus || ''}')"><i class="bi bi-toggle-on"></i> 상태</button>
                     <button class="btn btn-sm btn-warning" onclick="openResetModal('${uid}','${(item.userName || '').replace(/'/g, "\\'")}')"><i class="bi bi-key"></i> 비번초기화</button>
                     <button class="btn btn-sm btn-success" onclick="unlockAccount('${uid}')"><i class="bi bi-unlock"></i> 잠금해제</button>`
                ]);
            });
            accountTable.draw(false);
        },
        error: function () { Swal.fire('오류', '계정 목록 로드 중 오류가 발생했습니다.', 'error'); }
    });
}

function searchAccounts() { loadAccounts(); }

function resetAccountSearch() {
    $('#searchUserId').val('');
    $('#searchUserName').val('');
    $('#searchAccountStatus').val('');
    loadAccounts();
}

function openStatusModal(userId, currentStatus) {
    $('#statusUserId').val(userId);
    $('#statusUserLabel').text('아이디: ' + userId);
    $('#statusValue').val(currentStatus || '사용');
    statusModal.show();
}

function saveStatus() {
    const userId = $('#statusUserId').val();
    const accountStatus = $('#statusValue').val();
    $.ajax({
        url: '/hr/api/accounts/' + encodeURIComponent(userId) + '/status',
        method: 'PUT',
        data: JSON.stringify({ accountStatus: accountStatus }),
        contentType: 'application/json',
        success: function (res) {
            if (res.success) { Swal.fire('성공', res.message, 'success'); statusModal.hide(); loadAccounts(); }
            else { Swal.fire('오류', res.message, 'error'); }
        },
        error: function () { Swal.fire('오류', '상태 변경 중 오류가 발생했습니다.', 'error'); }
    });
}

function openResetModal(userId, userName) {
    $('#resetUserId').val(userId);
    $('#resetUserLabel').text('아이디: ' + userId + (userName ? ' (' + userName + ')' : ''));
    $('#resetPasswordValue').val('');
    resetModal.show();
}

function saveResetPassword() {
    const userId = $('#resetUserId').val();
    const password = $('#resetPasswordValue').val().trim();
    if (!password) { Swal.fire('확인', '임시 비밀번호를 입력하세요.', 'warning'); return; }
    $.ajax({
        url: '/hr/api/accounts/' + encodeURIComponent(userId) + '/reset-password',
        method: 'POST',
        data: JSON.stringify({ password: password }),
        contentType: 'application/json',
        success: function (res) {
            if (res.success) { Swal.fire('성공', res.message, 'success'); resetModal.hide(); loadAccounts(); }
            else { Swal.fire('오류', res.message, 'error'); }
        },
        error: function () { Swal.fire('오류', '비밀번호 초기화 중 오류가 발생했습니다.', 'error'); }
    });
}

function unlockAccount(userId) {
    Swal.fire({
        title: '잠금 해제',
        text: userId + ' 계정의 로그인 실패 횟수를 초기화하고 상태를 사용으로 전환합니다.',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '잠금해제',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/hr/api/accounts/' + encodeURIComponent(userId) + '/unlock',
                method: 'POST',
                success: function (res) {
                    Swal.fire(res.success ? '완료' : '오류', res.message, res.success ? 'success' : 'error');
                    loadAccounts();
                },
                error: function () { Swal.fire('오류', '잠금 해제 중 오류가 발생했습니다.', 'error'); }
            });
        }
    });
}

const { OverlayScrollbars } = OverlayScrollbarsGlobal;
if (document.querySelector('.app-sidebar-wrapper')) {
    OverlayScrollbars(document.querySelector('.app-sidebar-wrapper'), {});
}
