/**
 * @file HR 부서배치·겸직 관리 (데모) — 직원별 부서배치(J_직원_부서배치, M:N): 겸직추가/주부서변경/배치종료
 * @version 0.0.1
 * @requires jQuery, SweetAlert2, DataTables, Bootstrap
 */

let assignEmployeeTable;
let assignModal;

/** 현재 선택된 직원 컨텍스트 */
let currentUserId = null;
let currentName = null;
/** 부서 드롭다운 캐시 (최초 1회 로드) */
let deptOptionsCache = null;

$(document).ready(function () {
    assignEmployeeTable = $('#assignEmployeeTable').DataTable({
        language: { url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json' },
        order: [[1, 'asc']],
        pageLength: 25
    });
    assignModal = new bootstrap.Modal(document.getElementById('assignModal'));
    loadEmployees();
});

function currentSearch() {
    return {
        searchName: $('#searchName').val(),
        searchUserId: $('#searchUserId').val(),
        searchEmpStatus: $('#searchEmpStatus').val()
    };
}

function loadEmployees() {
    $.ajax({
        url: '/hr/api/assignments/employees',
        method: 'GET',
        data: currentSearch(),
        success: function (data) {
            assignEmployeeTable.clear();
            (data || []).forEach(function (item) {
                assignEmployeeTable.row.add([
                    item.userId,
                    item.name || '',
                    item.deptName || '-',
                    empStatusBadge(item.empStatus),
                    (item.assignCount != null ? item.assignCount + '개' : '-'),
                    manageButton(item)
                ]);
            });
            assignEmployeeTable.draw(false);
        },
        error: function () { Swal.fire('오류', '직원 목록 로드 중 오류가 발생했습니다.', 'error'); }
    });
}

function empStatusBadge(status) {
    if (status === '재직') return '<span class="hr-badge hr-badge-active">재직</span>';
    if (status === '휴직') return '<span class="hr-badge hr-badge-leave">휴직</span>';
    if (status === '퇴사') return '<span class="hr-badge hr-badge-resigned">퇴사</span>';
    return '<span class="hr-badge">' + (status || '-') + '</span>';
}

function manageButton(item) {
    const nameEsc = (item.name || '').replace(/'/g, "\\'");
    return '<button class="btn btn-sm btn-primary" onclick="openAssign(\'' + item.userId + '\', \'' + nameEsc + '\')">' +
        '<i class="bi bi-diagram-3"></i> 배치 관리</button>';
}

function searchEmployees() { loadEmployees(); }

function resetAssignmentSearch() {
    $('#searchName').val('');
    $('#searchUserId').val('');
    $('#searchEmpStatus').val('재직');
    loadEmployees();
}

/** 부서배치 관리 모달: 선택 직원의 배치 목록 로드 + 부서 드롭다운 준비 */
function openAssign(userId, name) {
    currentUserId = userId;
    currentName = name;
    $('#assignEmpName').text((name || userId) + ' (' + userId + ')');
    closeAddAssignment();
    ensureDeptOptions();
    loadAssignments(userId);
    assignModal.show();
}

/** 부서 드롭다운 옵션을 최초 1회 로드해 캐시한다. */
function ensureDeptOptions() {
    if (deptOptionsCache !== null) {
        $('#addDeptCode').html(deptOptionsCache);
        return;
    }
    $.ajax({
        url: '/hr/api/assignments/depts',
        method: 'GET',
        success: function (data) {
            let html = '';
            (data || []).forEach(function (d) {
                html += '<option value="' + d.deptCode + '">' + (d.deptName || d.deptCode) + '</option>';
            });
            deptOptionsCache = html;
            $('#addDeptCode').html(html);
        },
        error: function () { Swal.fire('오류', '부서 목록 로드 중 오류가 발생했습니다.', 'error'); }
    });
}

function loadAssignments(userId) {
    $.ajax({
        url: '/hr/api/assignments/' + encodeURIComponent(userId),
        method: 'GET',
        success: function (data) {
            const $body = $('#assignTableBody');
            $body.empty();
            (data || []).forEach(function (a) {
                const isOpen = (a.endDate == null || a.endDate === '');
                const isPrimary = (a.isPrimary === true);
                const kind = isPrimary
                    ? '<span class="hr-badge hr-badge-primary">주부서</span>'
                    : '<span class="hr-badge hr-badge-side">겸직</span>';
                const endCell = isOpen ? '<span class="text-muted">배치중</span>' : (a.endDate || '-');
                $body.append(
                    '<tr>' +
                    '<td>' + (a.deptName || a.deptCode || '-') + '</td>' +
                    '<td>' + kind + '</td>' +
                    '<td>' + (a.startDate || '-') + '</td>' +
                    '<td>' + endCell + '</td>' +
                    '<td>' + actionButtons(a, isOpen, isPrimary) + '</td>' +
                    '</tr>'
                );
            });
            if (($('#assignTableBody tr').length) === 0) {
                $body.append('<tr><td colspan="5" class="text-center text-muted">부서배치 이력이 없습니다.</td></tr>');
            }
        },
        error: function () { Swal.fire('오류', '부서배치 목록 로드 중 오류가 발생했습니다.', 'error'); }
    });
}

/** 현재 겸직 배치만 [주부서 지정]·[배치 종료] 가능. 주부서·종료된 배치는 액션 없음. */
function actionButtons(a, isOpen, isPrimary) {
    if (!isOpen || isPrimary) return '<span class="text-muted">-</span>';
    const deptEsc = (a.deptName || a.deptCode || '').replace(/'/g, "\\'");
    return '<button class="btn btn-sm btn-warning me-1" onclick="changePrimary(' + a.assignPk + ', \'' + deptEsc + '\')">' +
        '<i class="bi bi-star"></i> 주부서 지정</button>' +
        '<button class="btn btn-sm btn-danger" onclick="endAssignment(' + a.assignPk + ', \'' + deptEsc + '\')">' +
        '<i class="bi bi-box-arrow-right"></i> 배치 종료</button>';
}

/** 겸직 추가 폼 토글 */
function openAddAssignment() {
    $('#addStartDate').val('');
    $('#addReason').val('');
    $('#addAssignmentForm').slideDown(120);
}

function closeAddAssignment() {
    $('#addAssignmentForm').hide();
}

function submitAddAssignment() {
    const deptCode = $('#addDeptCode').val();
    if (!deptCode) { Swal.fire('확인', '부서를 선택하세요.', 'warning'); return; }
    const payload = {
        deptCode: deptCode,
        startDate: ($('#addStartDate').val() || null),
        reason: $('#addReason').val().trim()
    };
    $.ajax({
        url: '/hr/api/assignments/' + encodeURIComponent(currentUserId),
        method: 'POST',
        data: JSON.stringify(payload),
        contentType: 'application/json',
        success: function (res) {
            if (res.success) {
                Swal.fire('성공', res.message, 'success');
                closeAddAssignment();
                loadAssignments(currentUserId);
                loadEmployees();
            } else { Swal.fire('오류', res.message, 'error'); }
        },
        error: function () { Swal.fire('오류', '겸직 추가 중 오류가 발생했습니다.', 'error'); }
    });
}

/** 주부서 변경: 대상 겸직을 주부서로 승격 */
function changePrimary(assignPk, deptName) {
    Swal.fire({
        title: '주부서 변경',
        html: '<b>' + deptName + '</b> 부서를 주부서로 지정합니다.<br>기존 주부서는 겸직으로 전환됩니다.',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '변경',
        cancelButtonText: '취소'
    }).then(function (r) {
        if (!r.isConfirmed) return;
        $.ajax({
            url: '/hr/api/assignments/' + assignPk + '/primary',
            method: 'PUT',
            data: JSON.stringify({ userId: currentUserId }),
            contentType: 'application/json',
            success: function (res) {
                if (res.success) {
                    Swal.fire('성공', res.message, 'success');
                    loadAssignments(currentUserId);
                    loadEmployees();
                } else { Swal.fire('오류', res.message, 'error'); }
            },
            error: function () { Swal.fire('오류', '주부서 변경 중 오류가 발생했습니다.', 'error'); }
        });
    });
}

/** 배치 종료(겸직해제) */
function endAssignment(assignPk, deptName) {
    Swal.fire({
        title: '배치 종료',
        html: '<b>' + deptName + '</b> 겸직 배치를 종료합니다.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '종료',
        cancelButtonText: '취소',
        confirmButtonColor: '#c0392b'
    }).then(function (r) {
        if (!r.isConfirmed) return;
        $.ajax({
            url: '/hr/api/assignments/' + assignPk + '/end',
            method: 'PUT',
            data: JSON.stringify({ userId: currentUserId }),
            contentType: 'application/json',
            success: function (res) {
                if (res.success) {
                    Swal.fire('완료', res.message, 'success');
                    loadAssignments(currentUserId);
                    loadEmployees();
                } else { Swal.fire('오류', res.message, 'error'); }
            },
            error: function () { Swal.fire('오류', '배치 종료 중 오류가 발생했습니다.', 'error'); }
        });
    });
}
