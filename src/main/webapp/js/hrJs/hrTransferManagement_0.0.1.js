/**
 * @file HR 발령 관리 (데모) — 발령이력(J_직원_발령이력) 타임라인 조회 + 직급변경/휴직/복직 등록
 * @version 0.0.1
 * @requires jQuery, SweetAlert2, DataTables, Bootstrap
 */

let transferEmployeeTable;
let timelineModal, positionModal, statusModal;

/** 현재 선택된 직원 컨텍스트 */
let currentUserId = null;
let currentName = null;
let currentStatus = null;
let currentPosition = null;
/** 휴직/복직 공용 모달 모드 ('leave' | 'return') */
let statusMode = null;
/** 직급 드롭다운 캐시 (최초 1회) */
let positionOptionsCache = null;

$(document).ready(function () {
    transferEmployeeTable = $('#transferEmployeeTable').DataTable({
        language: { url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json' },
        order: [[1, 'asc']],
        pageLength: 25
    });
    timelineModal = new bootstrap.Modal(document.getElementById('timelineModal'));
    positionModal = new bootstrap.Modal(document.getElementById('positionModal'));
    statusModal = new bootstrap.Modal(document.getElementById('statusModal'));
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
        url: '/hr/api/transfers/employees',
        method: 'GET',
        data: currentSearch(),
        success: function (data) {
            transferEmployeeTable.clear();
            (data || []).forEach(function (item) {
                transferEmployeeTable.row.add([
                    item.userId,
                    item.name || '',
                    item.deptName || '-',
                    item.position || '-',
                    empStatusBadge(item.empStatus),
                    (item.transferCount != null ? item.transferCount + '건' : '-'),
                    timelineButton(item)
                ]);
            });
            transferEmployeeTable.draw(false);
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

/** 발령유형 배지 */
function transferTypeBadge(type) {
    const cls = {
        '입사': 'hr-tt-join', '재입사': 'hr-tt-join',
        '퇴사': 'hr-tt-leave', '휴직': 'hr-tt-pause', '복직': 'hr-tt-join',
        '부서이동': 'hr-tt-move', '겸직추가': 'hr-tt-move', '겸직해제': 'hr-tt-move',
        '직급변경': 'hr-tt-rank'
    }[type] || 'hr-tt-etc';
    return '<span class="hr-tt-badge ' + cls + '">' + (type || '-') + '</span>';
}

function timelineButton(item) {
    const nameEsc = (item.name || '').replace(/'/g, "\\'");
    const posEsc = (item.position || '').replace(/'/g, "\\'");
    return '<button class="btn btn-sm btn-primary" onclick="openTimeline(\'' + item.userId + '\', \'' + nameEsc + '\', \'' +
        (item.empStatus || '') + '\', \'' + posEsc + '\')"><i class="bi bi-clock-history"></i> 발령 이력</button>';
}

function searchEmployees() { loadEmployees(); }

function resetTransferSearch() {
    $('#searchName').val('');
    $('#searchUserId').val('');
    $('#searchEmpStatus').val('재직');
    loadEmployees();
}

/** 발령 이력 모달: 타임라인 로드 + 상태별 액션(직급변경/휴직/복직) */
function openTimeline(userId, name, empStatus, position) {
    currentUserId = userId;
    currentName = name;
    currentStatus = empStatus;
    currentPosition = position;
    $('#timelineEmpName').text((name || userId) + ' (' + userId + ')');

    // 상태별 액션 버튼: 재직 → 직급변경/휴직, 휴직 → 복직, 퇴사 → 없음
    let actionBar = '';
    if (empStatus === '재직') {
        actionBar = '<button class="btn btn-sm btn-primary me-1" onclick="openPosition()"><i class="bi bi-arrow-up-right-circle"></i> 직급변경</button>' +
            '<button class="btn btn-sm btn-warning" onclick="openStatus(\'leave\')"><i class="bi bi-pause-circle"></i> 휴직 처리</button>';
    } else if (empStatus === '휴직') {
        actionBar = '<button class="btn btn-sm btn-success" onclick="openStatus(\'return\')"><i class="bi bi-play-circle"></i> 복직 처리</button>';
    } else {
        actionBar = '<span class="text-muted small">퇴사자는 발령 등록 대상이 아닙니다.</span>';
    }
    $('#timelineActionBar').html(actionBar);

    ensurePositionOptions();
    loadTimeline(userId);
    timelineModal.show();
}

/** 직급 드롭다운(datalist) 최초 1회 로드·캐시 */
function ensurePositionOptions() {
    if (positionOptionsCache !== null) {
        $('#positionOptions').html(positionOptionsCache);
        return;
    }
    $.ajax({
        url: '/hr/api/transfers/positions',
        method: 'GET',
        success: function (data) {
            let html = '';
            (data || []).forEach(function (p) {
                if (p.position) html += '<option value="' + p.position + '"></option>';
            });
            positionOptionsCache = html;
            $('#positionOptions').html(html);
        }
    });
}

function loadTimeline(userId) {
    $.ajax({
        url: '/hr/api/transfers/' + encodeURIComponent(userId),
        method: 'GET',
        success: function (data) {
            const $body = $('#timelineTableBody');
            $body.empty();
            (data || []).forEach(function (t) {
                const dept = arrow(t.prevDeptName || t.prevDept, t.newDeptName || t.newDept);
                const pos = arrow(t.prevPosition, t.newPosition);
                $body.append(
                    '<tr>' +
                    '<td>' + (t.transferDate || '-') + '</td>' +
                    '<td>' + transferTypeBadge(t.transferType) + '</td>' +
                    '<td>' + dept + '</td>' +
                    '<td>' + pos + '</td>' +
                    '<td>' + (t.reason || '-') + '</td>' +
                    '</tr>'
                );
            });
            if (($('#timelineTableBody tr').length) === 0) {
                $body.append('<tr><td colspan="5" class="text-center text-muted">발령 이력이 없습니다.</td></tr>');
            }
        },
        error: function () { Swal.fire('오류', '발령 이력 로드 중 오류가 발생했습니다.', 'error'); }
    });
}

/** 이전→신규 표기. 둘 다 없으면 '-', 한쪽만 있으면 그 값. */
function arrow(prev, next) {
    const p = (prev == null || prev === '') ? null : prev;
    const n = (next == null || next === '') ? null : next;
    if (p == null && n == null) return '-';
    if (p == null) return n;
    if (n == null) return p;
    return p + ' → ' + n;
}

/** 직급변경 모달 오픈 */
function openPosition() {
    $('#positionEmpName').text((currentName || currentUserId));
    $('#positionCurrent').text(currentPosition || '-');
    $('#positionNew').val('');
    $('#positionDate').val('');
    $('#positionReason').val('');
    positionModal.show();
}

function submitPosition() {
    const newPos = $('#positionNew').val().trim();
    if (!newPos) { Swal.fire('확인', '변경할 직급을 입력하세요.', 'warning'); return; }
    if (newPos === (currentPosition || '')) { Swal.fire('확인', '현재 직급과 동일합니다.', 'warning'); return; }
    const payload = {
        newPosition: newPos,
        transferDate: ($('#positionDate').val() || null),
        reason: $('#positionReason').val().trim()
    };
    $.ajax({
        url: '/hr/api/transfers/' + encodeURIComponent(currentUserId) + '/position',
        method: 'PUT',
        data: JSON.stringify(payload),
        contentType: 'application/json',
        success: function (res) {
            if (res.success) {
                Swal.fire('성공', res.message, 'success');
                positionModal.hide();
                timelineModal.hide();
                loadEmployees();
            } else { Swal.fire('오류', res.message, 'error'); }
        },
        error: function () { Swal.fire('오류', '직급변경 중 오류가 발생했습니다.', 'error'); }
    });
}

/** 휴직/복직 공용 모달 오픈 */
function openStatus(mode) {
    statusMode = mode;
    const isLeave = (mode === 'leave');
    $('#statusModalHeader').removeClass('bg-primary bg-warning bg-success').addClass(isLeave ? 'bg-warning' : 'bg-success');
    $('#statusModalIcon').attr('class', isLeave ? 'bi bi-pause-circle' : 'bi bi-play-circle');
    $('#statusModalTitle').text(isLeave ? '휴직 처리' : '복직 처리');
    $('#statusModalDesc').html(isLeave
        ? '<b>' + (currentName || currentUserId) + '</b> 직원을 휴직 처리합니다. 계정이 자동으로 정지되어 로그인이 차단됩니다.'
        : '<b>' + (currentName || currentUserId) + '</b> 직원을 복직 처리합니다. 계정이 재활성화됩니다.');
    $('#statusSubmitBtn').removeClass('btn-primary btn-warning btn-success').addClass(isLeave ? 'btn-warning' : 'btn-success')
        .text(isLeave ? '휴직 처리' : '복직 처리');
    $('#statusDate').val('');
    $('#statusReason').val('');
    statusModal.show();
}

function submitStatus() {
    const url = '/hr/api/transfers/' + encodeURIComponent(currentUserId) + (statusMode === 'leave' ? '/leave' : '/return');
    const payload = {
        transferDate: ($('#statusDate').val() || null),
        reason: $('#statusReason').val().trim()
    };
    $.ajax({
        url: url,
        method: 'PUT',
        data: JSON.stringify(payload),
        contentType: 'application/json',
        success: function (res) {
            if (res.success) {
                Swal.fire('완료', res.message, 'success');
                statusModal.hide();
                timelineModal.hide();
                loadEmployees();
            } else { Swal.fire('오류', res.message, 'error'); }
        },
        error: function () { Swal.fire('오류', '처리 중 오류가 발생했습니다.', 'error'); }
    });
}
