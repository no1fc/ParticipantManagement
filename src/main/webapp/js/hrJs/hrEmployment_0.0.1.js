/**
 * @file HR 입퇴사 관리 (데모) — 직원별 재직 cycle(J_직원_재직기간) 관리: 재입사/퇴사/cycle 편집
 * @version 0.0.1
 * @requires jQuery, SweetAlert2, DataTables, Bootstrap
 */

let employmentTable;
let historyModal, rehireModal, resignModal, cycleEditModal;

/** 현재 선택된 직원/cycle 컨텍스트 */
let currentUserId = null;
let currentName = null;
let editingCyclePk = null;

$(document).ready(function () {
    employmentTable = $('#employmentTable').DataTable({
        language: { url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json' },
        order: [[1, 'asc']],
        pageLength: 25
    });
    historyModal = new bootstrap.Modal(document.getElementById('historyModal'));
    rehireModal = new bootstrap.Modal(document.getElementById('rehireModal'));
    resignModal = new bootstrap.Modal(document.getElementById('resignModal'));
    cycleEditModal = new bootstrap.Modal(document.getElementById('cycleEditModal'));
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
        url: '/hr/api/employments',
        method: 'GET',
        data: currentSearch(),
        success: function (data) {
            employmentTable.clear();
            (data || []).forEach(function (item) {
                employmentTable.row.add([
                    item.userId,
                    item.name || '',
                    item.deptName || '-',
                    empStatusBadge(item.empStatus),
                    (item.tenureDays != null ? item.tenureDays + '일' : '-'),
                    (item.cycleCount != null ? item.cycleCount : '-'),
                    historyButton(item)
                ]);
            });
            employmentTable.draw(false);
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

function historyButton(item) {
    return '<button class="btn btn-sm btn-primary" onclick="openHistory(\'' + item.userId + '\', \'' + (item.name || '').replace(/'/g, "\\'") + '\', \'' + (item.empStatus || '') + '\')">' +
        '<i class="bi bi-clock-history"></i> 입퇴사 이력</button>';
}

function searchEmployees() { loadEmployees(); }

function resetEmploymentSearch() {
    $('#searchName').val('');
    $('#searchUserId').val('');
    $('#searchEmpStatus').val('ALL');
    loadEmployees();
}

/** 입퇴사 이력 모달: 선택 직원의 cycle 목록 로드 + 상태별 액션(재입사/퇴사) */
function openHistory(userId, name, empStatus) {
    currentUserId = userId;
    currentName = name;
    $('#historyEmpName').text((name || userId) + ' (' + userId + ')');

    // 상태별 상단 액션 버튼: 퇴사 → 재입사, 그 외 → 퇴사
    let actionBar = '';
    if (empStatus === '퇴사') {
        actionBar = '<button class="btn btn-sm btn-success" onclick="openRehire()"><i class="bi bi-arrow-repeat"></i> 재입사</button>';
    } else {
        actionBar = '<button class="btn btn-sm btn-danger" onclick="openResign()"><i class="bi bi-box-arrow-right"></i> 퇴사 처리</button>';
    }
    $('#historyActionBar').html(actionBar);

    loadCycles(userId);
    historyModal.show();
}

function loadCycles(userId) {
    $.ajax({
        url: '/hr/api/employments/' + encodeURIComponent(userId) + '/cycles',
        method: 'GET',
        success: function (data) {
            const $body = $('#cycleTableBody');
            $body.empty();
            (data || []).forEach(function (c) {
                const isOpen = (c.resignDate == null || c.resignDate === '');
                const weight = (c.weightPercent != null ? c.weightPercent + '%' : '<span class="text-muted">정책상속</span>');
                const reflect = (c.reflectPosition === true ? '<i class="bi bi-check-lg text-success"></i>' : '-');
                // 종료된 cycle만 편집 가능 (열린 cycle은 재입사/퇴사로 관리)
                const editBtn = isOpen
                    ? '<span class="text-muted">-</span>'
                    : '<button class="btn btn-sm btn-warning" onclick="openCycleEdit(' + c.cyclePk + ', ' + c.seq + ', ' +
                        (c.weightPercent != null ? c.weightPercent : 'null') + ', ' + (c.reflectPosition === true) + ')">' +
                        '<i class="bi bi-pencil"></i></button>';
                const resign = isOpen ? '<span class="hr-badge hr-badge-active">재직중</span>' : (c.resignDate || '-');
                $body.append(
                    '<tr>' +
                    '<td>' + c.seq + '</td>' +
                    '<td>' + (c.hireDate || '-') + '</td>' +
                    '<td>' + resign + '</td>' +
                    '<td>' + (c.resignReason || '-') + '</td>' +
                    '<td>' + weight + '</td>' +
                    '<td>' + reflect + '</td>' +
                    '<td>' + editBtn + '</td>' +
                    '</tr>'
                );
            });
            if (($('#cycleTableBody tr').length) === 0) {
                $body.append('<tr><td colspan="7" class="text-center text-muted">재직 cycle이 없습니다.</td></tr>');
            }
        },
        error: function () { Swal.fire('오류', 'cycle 목록 로드 중 오류가 발생했습니다.', 'error'); }
    });
}

/** 재입사 모달 오픈 */
function openRehire() {
    $('#rehireEmpName').text((currentName || currentUserId));
    $('#rehireHireDate').val('');
    $('#rehireWeight').val('');
    $('#rehireReflect').val('false');
    $('#rehireReason').val('');
    rehireModal.show();
}

function submitRehire() {
    const hireDate = $('#rehireHireDate').val();
    if (!hireDate) { Swal.fire('확인', '재입사 입사일은 필수입니다.', 'warning'); return; }
    const weightRaw = $('#rehireWeight').val();
    const payload = {
        hireDate: hireDate,
        weightPercent: (weightRaw === '' ? null : parseInt(weightRaw, 10)),
        reflectPosition: $('#rehireReflect').val() === 'true',
        reason: $('#rehireReason').val().trim()
    };
    $.ajax({
        url: '/hr/api/employments/' + encodeURIComponent(currentUserId) + '/rehire',
        method: 'POST',
        data: JSON.stringify(payload),
        contentType: 'application/json',
        success: function (res) {
            if (res.success) {
                Swal.fire('성공', res.message, 'success');
                rehireModal.hide();
                historyModal.hide();
                loadEmployees();
            } else { Swal.fire('오류', res.message, 'error'); }
        },
        error: function () { Swal.fire('오류', '재입사 처리 중 오류가 발생했습니다.', 'error'); }
    });
}

/** 퇴사 모달 오픈 */
function openResign() {
    $('#resignEmpName').text((currentName || currentUserId));
    $('#resignDate').val('');
    $('#resignReason').val('');
    resignModal.show();
}

function submitResign() {
    const payload = {
        resignDate: ($('#resignDate').val() || null),
        resignReason: $('#resignReason').val().trim()
    };
    $.ajax({
        url: '/hr/api/employments/' + encodeURIComponent(currentUserId) + '/resign',
        method: 'POST',
        data: JSON.stringify(payload),
        contentType: 'application/json',
        success: function (res) {
            if (res.success) {
                Swal.fire('완료', res.message, 'success');
                resignModal.hide();
                historyModal.hide();
                loadEmployees();
            } else { Swal.fire('오류', res.message, 'error'); }
        },
        error: function () { Swal.fire('오류', '퇴사 처리 중 오류가 발생했습니다.', 'error'); }
    });
}

/** cycle 편집 모달 오픈 */
function openCycleEdit(cyclePk, seq, weight, reflect) {
    editingCyclePk = cyclePk;
    $('#cycleEditSeq').text(seq);
    $('#editWeight').val(weight != null ? weight : '');
    $('#editReflect').val(reflect === true ? 'true' : 'false');
    cycleEditModal.show();
}

function submitCycleEdit() {
    const weightRaw = $('#editWeight').val();
    const payload = {
        userId: currentUserId,
        weightPercent: (weightRaw === '' ? null : parseInt(weightRaw, 10)),
        reflectPosition: $('#editReflect').val() === 'true'
    };
    $.ajax({
        url: '/hr/api/employments/cycle/' + editingCyclePk,
        method: 'PUT',
        data: JSON.stringify(payload),
        contentType: 'application/json',
        success: function (res) {
            if (res.success) {
                Swal.fire('성공', res.message, 'success');
                cycleEditModal.hide();
                loadCycles(currentUserId);
                loadEmployees();
            } else { Swal.fire('오류', res.message, 'error'); }
        },
        error: function () { Swal.fire('오류', 'cycle 수정 중 오류가 발생했습니다.', 'error'); }
    });
}
