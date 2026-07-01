/**
 * @file HR 근속정책 관리 (데모) — J_근속산정정책 CRUD(소프트삭제)
 * @version 0.0.1
 * @requires jQuery, SweetAlert2, DataTables, Bootstrap
 */

let tenurePolicyTable, tenurePolicyModal;

$(document).ready(function () {
    tenurePolicyTable = $('#tenurePolicyTable').DataTable({
        language: { url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json' },
        order: [[0, 'asc']],
        pageLength: 25
    });
    tenurePolicyModal = new bootstrap.Modal(document.getElementById('tenurePolicyModal'));
    loadTenurePolicies();
});

function currentSearch() {
    return {
        searchScope: $('#searchScope').val(),
        searchUseStatus: $('#searchUseStatus').val()
    };
}

function loadTenurePolicies() {
    $.ajax({
        url: '/hr/api/tenure-policies',
        method: 'GET',
        data: currentSearch(),
        success: function (data) {
            tenurePolicyTable.clear();
            data.forEach(function (item) {
                const statusBadge = item.useStatus === '사용'
                    ? '<span class="hr-badge hr-badge-on">사용</span>'
                    : '<span class="hr-badge hr-badge-off">미사용</span>';
                const weight = item.weightPercent != null ? item.weightPercent + '%' : '-';
                tenurePolicyTable.row.add([
                    item.policyKey,
                    weight,
                    item.scope || '-',
                    item.remark || '-',
                    statusBadge,
                    `<button class="btn btn-sm btn-warning" onclick="editTenurePolicy('${item.policyKey}')"><i class="bi bi-pencil"></i> 수정</button>
                     <button class="btn btn-sm btn-danger" onclick="deleteTenurePolicy('${item.policyKey}')"><i class="bi bi-trash"></i> 비활성화</button>`
                ]);
            });
            tenurePolicyTable.draw(false);
        },
        error: function () { Swal.fire('오류', '근속정책 목록 로드 중 오류가 발생했습니다.', 'error'); }
    });
}

function searchTenurePolicies() { loadTenurePolicies(); }

function resetTenurePolicySearch() {
    $('#searchScope').val('');
    $('#searchUseStatus').val('사용');
    loadTenurePolicies();
}

function openAddTenurePolicyModal() {
    $('#tenurePolicyForm')[0].reset();
    $('#tenurePolicyModalLabel').text('근속정책 추가');
    $('#policyKey').prop('readonly', false);
    tenurePolicyModal.show();
}

function editTenurePolicy(policyKey) {
    $.ajax({
        url: '/hr/api/tenure-policies/' + encodeURIComponent(policyKey),
        method: 'GET',
        success: function (d) {
            $('#tenurePolicyModalLabel').text('근속정책 수정');
            $('#policyKey').val(d.policyKey).prop('readonly', true);
            $('#weightPercent').val(d.weightPercent);
            $('#scope').val(d.scope || '');
            $('#remark').val(d.remark || '');
            tenurePolicyModal.show();
        },
        error: function () { Swal.fire('오류', '정책 정보 조회 중 오류가 발생했습니다.', 'error'); }
    });
}

function saveTenurePolicy() {
    const policyKey = $('#policyKey').val().trim();
    const weightRaw = $('#weightPercent').val();
    if (!policyKey || weightRaw === '') { Swal.fire('확인', '정책키와 가중퍼센트는 필수입니다.', 'warning'); return; }

    const weightPercent = parseInt(weightRaw, 10);
    if (isNaN(weightPercent) || weightPercent < 0 || weightPercent > 100) {
        Swal.fire('확인', '가중퍼센트는 0~100 사이여야 합니다.', 'warning');
        return;
    }

    const formData = {
        policyKey: policyKey,
        weightPercent: weightPercent,
        scope: $('#scope').val().trim(),
        remark: $('#remark').val().trim()
    };

    const isEdit = $('#policyKey').prop('readonly');
    const method = isEdit ? 'PUT' : 'POST';
    const url = isEdit ? '/hr/api/tenure-policies/' + encodeURIComponent(policyKey) : '/hr/api/tenure-policies';

    $.ajax({
        url: url,
        method: method,
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function (res) {
            if (res.success) { Swal.fire('성공', res.message, 'success'); tenurePolicyModal.hide(); loadTenurePolicies(); }
            else { Swal.fire('오류', res.message, 'error'); }
        },
        error: function () { Swal.fire('오류', '저장 중 오류가 발생했습니다.', 'error'); }
    });
}

function deleteTenurePolicy(policyKey) {
    Swal.fire({
        title: '비활성화 확인',
        text: policyKey + ' 정책을 비활성화(사용여부 미사용)하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '비활성화',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/hr/api/tenure-policies/' + encodeURIComponent(policyKey),
                method: 'DELETE',
                success: function (res) {
                    Swal.fire(res.success ? '완료' : '오류', res.message, res.success ? 'success' : 'error');
                    loadTenurePolicies();
                },
                error: function () { Swal.fire('오류', '비활성화 중 오류가 발생했습니다.', 'error'); }
            });
        }
    });
}
