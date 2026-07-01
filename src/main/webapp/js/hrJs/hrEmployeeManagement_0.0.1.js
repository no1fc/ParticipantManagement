/**
 * @file HR 직원 관리 (데모) — J_직원 외 6테이블 CRUD(신규등록/편집/퇴사 soft-delete)
 * @version 0.0.1
 * @requires jQuery, SweetAlert2, DataTables, Bootstrap
 */

let employeeTable, employeeModal;
let departmentOptions = [];

$(document).ready(function () {
    employeeTable = $('#employeeTable').DataTable({
        language: { url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json' },
        order: [[1, 'asc']],
        pageLength: 25
    });
    employeeModal = new bootstrap.Modal(document.getElementById('employeeModal'));
    loadDepartments(function () { loadEmployees(); });
});

function loadDepartments(done) {
    $.ajax({
        url: '/hr/api/departments',
        method: 'GET',
        data: { searchUseStatus: '사용' },
        success: function (data) {
            departmentOptions = data || [];
            const $search = $('#searchDeptCode');
            const $modal = $('#deptCode');
            $search.find('option:not(:first)').remove();
            $modal.find('option:not(:first)').remove();
            departmentOptions.forEach(function (d) {
                const label = d.deptName + ' (' + d.deptCode + ')';
                $search.append('<option value="' + d.deptCode + '">' + label + '</option>');
                $modal.append('<option value="' + d.deptCode + '">' + label + '</option>');
            });
            if (typeof done === 'function') done();
        },
        error: function () {
            if (typeof done === 'function') done();
        }
    });
}

function currentSearch() {
    return {
        searchName: $('#searchName').val(),
        searchUserId: $('#searchUserId').val(),
        searchDeptCode: $('#searchDeptCode').val(),
        searchEmpStatus: $('#searchEmpStatus').val()
    };
}

function loadEmployees() {
    $.ajax({
        url: '/hr/api/employees',
        method: 'GET',
        data: currentSearch(),
        success: function (data) {
            employeeTable.clear();
            (data || []).forEach(function (item) {
                employeeTable.row.add([
                    item.userId,
                    item.name || '',
                    item.deptName || '-',
                    item.position || '-',
                    empStatusBadge(item.empStatus),
                    item.hireDate || '-',
                    accountStatusBadge(item.accountStatus),
                    actionButtons(item)
                ]);
            });
            employeeTable.draw(false);
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

function accountStatusBadge(status) {
    if (status === '사용') return '<span class="hr-badge hr-badge-on">사용</span>';
    if (status == null || status === '') return '<span class="hr-badge">-</span>';
    return '<span class="hr-badge hr-badge-off">' + status + '</span>';
}

function actionButtons(item) {
    const edit = '<button class="btn btn-sm btn-warning" onclick="editEmployee(\'' + item.userId + '\')"><i class="bi bi-pencil"></i> 수정</button>';
    if (item.empStatus === '퇴사') return edit;
    const resign = ' <button class="btn btn-sm btn-danger" onclick="resignEmployee(\'' + item.userId + '\', \'' + (item.name || '') + '\')"><i class="bi bi-box-arrow-right"></i> 퇴사</button>';
    return edit + resign;
}

function searchEmployees() { loadEmployees(); }

function resetEmployeeSearch() {
    $('#searchName').val('');
    $('#searchUserId').val('');
    $('#searchDeptCode').val('');
    $('#searchEmpStatus').val('');
    loadEmployees();
}

function openAddEmployeeModal() {
    $('#employeeForm')[0].reset();
    $('#employeeModalLabel').text('직원 등록');
    $('#userId').prop('readonly', false);
    $('#role').val('상담');
    $('#passwordGroup').show();
    $('#accountStatusGroup').hide();
    employeeModal.show();
}

function editEmployee(userId) {
    $.ajax({
        url: '/hr/api/employees/' + encodeURIComponent(userId),
        method: 'GET',
        success: function (d) {
            $('#employeeForm')[0].reset();
            $('#employeeModalLabel').text('직원 수정');
            $('#userId').val(d.userId).prop('readonly', true);
            $('#name').val(d.name || '');
            $('#email').val(d.email || '');
            $('#phone').val(d.phone || '');
            $('#hireDate').val(d.hireDate || '');
            $('#daouNo').val(d.daouNo || '');
            $('#note').val(d.note || '');
            $('#deptCode').val(d.deptCode || '');
            $('#position').val(d.position || '');
            $('#role').val(d.role || '상담');
            $('#isAdmin').val(d.isAdmin === true ? 'true' : 'false');
            $('#accountStatus').val(d.accountStatus || '사용');
            $('#passwordGroup').hide();
            $('#accountStatusGroup').show();
            employeeModal.show();
        },
        error: function () { Swal.fire('오류', '직원 정보 조회 중 오류가 발생했습니다.', 'error'); }
    });
}

function saveEmployee() {
    const userId = $('#userId').val().trim();
    const name = $('#name').val().trim();
    const hireDate = $('#hireDate').val();
    const deptCode = $('#deptCode').val();
    const isEdit = $('#userId').prop('readonly');

    if (!userId || !name) { Swal.fire('확인', '아이디와 이름은 필수입니다.', 'warning'); return; }
    if (!hireDate) { Swal.fire('확인', '최초입사일은 필수입니다.', 'warning'); return; }
    if (!deptCode) { Swal.fire('확인', '주부서는 필수입니다.', 'warning'); return; }
    if (!isEdit && !$('#password').val()) { Swal.fire('확인', '비밀번호는 필수입니다.', 'warning'); return; }

    const formData = {
        userId: userId,
        name: name,
        email: $('#email').val().trim(),
        phone: $('#phone').val().trim(),
        hireDate: hireDate,
        daouNo: $('#daouNo').val().trim(),
        note: $('#note').val().trim(),
        deptCode: deptCode,
        position: $('#position').val().trim(),
        role: $('#role').val().trim() || '상담',
        isAdmin: $('#isAdmin').val() === 'true'
    };
    if (isEdit) {
        formData.accountStatus = $('#accountStatus').val();
    } else {
        formData.password = $('#password').val();
    }

    const method = isEdit ? 'PUT' : 'POST';
    const url = isEdit ? '/hr/api/employees/' + encodeURIComponent(userId) : '/hr/api/employees';

    $.ajax({
        url: url,
        method: method,
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function (res) {
            if (res.success) { Swal.fire('성공', res.message, 'success'); employeeModal.hide(); loadEmployees(); }
            else { Swal.fire('오류', res.message, 'error'); }
        },
        error: function () { Swal.fire('오류', '저장 중 오류가 발생했습니다.', 'error'); }
    });
}

function resignEmployee(userId, name) {
    Swal.fire({
        title: '퇴사 처리 확인',
        html: (name || userId) + ' 직원을 <b>퇴사</b> 처리하시겠습니까?<br><small class="text-muted">현재재직상태가 \'퇴사\'로 바뀌고, 계정이 자동으로 정지됩니다.</small>',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '퇴사 처리',
        cancelButtonText: '취소',
        confirmButtonColor: '#c0392b'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/hr/api/employees/' + encodeURIComponent(userId),
                method: 'DELETE',
                success: function (res) {
                    Swal.fire(res.success ? '완료' : '오류', res.message, res.success ? 'success' : 'error');
                    loadEmployees();
                },
                error: function () { Swal.fire('오류', '퇴사 처리 중 오류가 발생했습니다.', 'error'); }
            });
        }
    });
}
