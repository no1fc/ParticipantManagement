/**
 * @file HR 부서/조직 관리 (데모) — J_부서 CRUD(소프트삭제)
 * @version 0.0.1
 * @requires jQuery, SweetAlert2, DataTables, Bootstrap, OverlayScrollbars
 */

let departmentTable, departmentModal;
let departmentCache = [];

$(document).ready(function () {
    departmentTable = $('#departmentTable').DataTable({
        language: { url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json' },
        order: [[5, 'asc']],
        pageLength: 25
    });
    departmentModal = new bootstrap.Modal(document.getElementById('departmentModal'));
    loadDepartments();
});

function currentSearch() {
    return {
        searchDeptName: $('#searchDeptName').val(),
        searchDeptType: $('#searchDeptType').val(),
        searchUseStatus: $('#searchUseStatus').val()
    };
}

function loadDepartments() {
    $.ajax({
        url: '/hr/api/departments',
        method: 'GET',
        data: currentSearch(),
        success: function (data) {
            departmentCache = data;
            departmentTable.clear();
            data.forEach(function (item) {
                const statusBadge = item.useStatus === '사용'
                    ? '<span class="hr-badge hr-badge-on">사용</span>'
                    : '<span class="hr-badge hr-badge-off">미사용</span>';
                departmentTable.row.add([
                    item.deptCode,
                    item.deptName || '',
                    item.deptType || '',
                    item.parentDeptName || '-',
                    item.branchName || '-',
                    item.deptOrder != null ? item.deptOrder : '',
                    statusBadge,
                    `<button class="btn btn-sm btn-warning" onclick="editDepartment('${item.deptCode}')"><i class="bi bi-pencil"></i> 수정</button>
                     <button class="btn btn-sm btn-danger" onclick="deleteDepartment('${item.deptCode}')"><i class="bi bi-trash"></i> 비활성화</button>`
                ]);
            });
            departmentTable.draw(false);
        },
        error: function () { Swal.fire('오류', '부서 목록 로드 중 오류가 발생했습니다.', 'error'); }
    });
}

function searchDepartments() { loadDepartments(); }

function resetDepartmentSearch() {
    $('#searchDeptName').val('');
    $('#searchDeptType').val('');
    $('#searchUseStatus').val('사용');
    loadDepartments();
}

function fillParentOptions(excludeCode) {
    const $sel = $('#parentDeptCode');
    $sel.empty().append('<option value="">(최상위)</option>');
    departmentCache.forEach(function (item) {
        if (item.deptCode === excludeCode) return;
        $sel.append(`<option value="${item.deptCode}">${item.deptName} (${item.deptCode})</option>`);
    });
}

function openAddDepartmentModal() {
    $('#departmentForm')[0].reset();
    $('#departmentModalLabel').text('부서 추가');
    $('#deptCode').prop('readonly', false);
    fillParentOptions(null);
    departmentModal.show();
}

function editDepartment(deptCode) {
    $.ajax({
        url: '/hr/api/departments/' + encodeURIComponent(deptCode),
        method: 'GET',
        success: function (d) {
            $('#departmentModalLabel').text('부서 수정');
            fillParentOptions(d.deptCode);
            $('#deptCode').val(d.deptCode).prop('readonly', true);
            $('#deptName').val(d.deptName);
            $('#deptType').val(d.deptType);
            $('#parentDeptCode').val(d.parentDeptCode || '');
            $('#branchName').val(d.branchName || '');
            $('#deptOrder').val(d.deptOrder);
            departmentModal.show();
        },
        error: function () { Swal.fire('오류', '부서 정보 조회 중 오류가 발생했습니다.', 'error'); }
    });
}

function saveDepartment() {
    const deptCode = $('#deptCode').val().trim();
    const deptName = $('#deptName').val().trim();
    if (!deptCode || !deptName) { Swal.fire('확인', '부서코드와 부서명은 필수입니다.', 'warning'); return; }

    const formData = {
        deptCode: deptCode,
        deptName: deptName,
        deptType: $('#deptType').val(),
        parentDeptCode: $('#parentDeptCode').val(),
        branchName: $('#branchName').val().trim(),
        deptOrder: $('#deptOrder').val()
    };

    const isEdit = $('#deptCode').prop('readonly');
    const method = isEdit ? 'PUT' : 'POST';
    const url = isEdit ? '/hr/api/departments/' + encodeURIComponent(deptCode) : '/hr/api/departments';

    $.ajax({
        url: url,
        method: method,
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function (res) {
            if (res.success) { Swal.fire('성공', res.message, 'success'); departmentModal.hide(); loadDepartments(); }
            else { Swal.fire('오류', res.message, 'error'); }
        },
        error: function () { Swal.fire('오류', '저장 중 오류가 발생했습니다.', 'error'); }
    });
}

function deleteDepartment(deptCode) {
    Swal.fire({
        title: '비활성화 확인',
        text: deptCode + ' 부서를 비활성화(사용여부 미사용)하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '비활성화',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/hr/api/departments/' + encodeURIComponent(deptCode),
                method: 'DELETE',
                success: function (res) {
                    Swal.fire(res.success ? '완료' : '오류', res.message, res.success ? 'success' : 'error');
                    loadDepartments();
                },
                error: function () { Swal.fire('오류', '비활성화 중 오류가 발생했습니다.', 'error'); }
            });
        }
    });
}

const { OverlayScrollbars } = OverlayScrollbarsGlobal;
if (document.querySelector('.app-sidebar-wrapper')) {
    OverlayScrollbars(document.querySelector('.app-sidebar-wrapper'), {});
}
