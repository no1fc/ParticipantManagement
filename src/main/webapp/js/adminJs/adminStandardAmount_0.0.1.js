let standardAmountTable, betterWageTable;
let standardAmountModal, betterWageModal;

$(document).ready(function() {
    // DataTable 초기화
    standardAmountTable = $('#standardAmountTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[0, 'desc']],
        pageLength: 10
    });

    betterWageTable = $('#betterWageTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[0, 'desc']],
        pageLength: 10
    });

    // 모달 초기화
    standardAmountModal = new bootstrap.Modal(document.getElementById('standardAmountModal'));
    betterWageModal = new bootstrap.Modal(document.getElementById('betterWageModal'));

    // 초기 데이터 로드
    loadStandardAmounts();
    loadBetterWages();
});

/**
 * 기준금액 목록 로드
 * TODO: GET /admin/api/standard-amounts
 */
function loadStandardAmounts() {
    $.ajax({
        url: '/admin/api/standard-amounts',
        method: 'GET',
        success: function(data) {
            standardAmountTable.clear();
            data.forEach(function(item) {
                standardAmountTable.row.add([
                    item.pk,
                    item.division || '',
                    item.year || '',
                    item.minWage || 0,
                    item.maxWage || '',
                    item.successFee || 0,
                    `<button class="btn btn-sm btn-warning" onclick="editStandardAmount(${item.pk})"><i class="bi bi-pencil"></i> 수정</button>
                     <button class="btn btn-sm btn-danger" onclick="deleteStandardAmount(${item.pk})"><i class="bi bi-trash"></i> 삭제</button>`
                ]);
            });
            standardAmountTable.draw(false);
        },
        error: function() {
            Swal.fire('오류', '기준금액 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

function loadBetterWages() {
    $.ajax({
        url: '/admin/api/better-wages',
        method: 'GET',
        success: function(data) {
            betterWageTable.clear();
            data.forEach(function(item) {
                betterWageTable.row.add([
                    item.pk,
                    item.betterYear || '',
                    item.betterYearDate || '',
                    item.betterWage || 0,
                    item.betterRegDate || '',
                    `<button class="btn btn-sm btn-warning" onclick="editBetterWage(${item.pk})"><i class="bi bi-pencil"></i> 수정</button>
                     <button class="btn btn-sm btn-danger" onclick="deleteBetterWage(${item.pk})"><i class="bi bi-trash"></i> 삭제</button>`
                ]);
            });
            betterWageTable.draw(false);
        },
        error: function() {
            Swal.fire('오류', '나은기준임금 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

/**
 * 기준금액 추가 모달 열기
 */
function openStandardAmountModal() {
    $('#standardAmountForm')[0].reset();
    $('#standardAmountModalLabel').text('기준금액 추가');
    $('#standardAmountPK').val('');
    standardAmountModal.show();
}

/**
 * 기준금액 수정
 */
function editStandardAmount(pk) {
    $.ajax({
        url: '/admin/api/standard-amounts/' + pk,
        method: 'GET',
        success: function(data) {
            $('#standardAmountModalLabel').text('기준금액 수정');
            $('#standardAmountPK').val(data.pk).prop('readonly', true);
            $('#division').val(data.division);
            $('#year').val(data.year);
            $('#minWage').val(data.minWage);
            $('#maxWage').val(data.maxWage);
            $('#successFee').val(data.successFee);
            standardAmountModal.show();
        },
        error: function() {
            Swal.fire('오류', '기준금액 정보 조회 중 오류가 발생했습니다.', 'error');
        }
    });
}

function saveStandardAmount() {
    const formData = {
        pk: $('#standardAmountPK').val(),
        division: $('#division').val(),
        year: $('#year').val(),
        minWage: $('#minWage').val(),
        maxWage: $('#maxWage').val(),
        successFee: $('#successFee').val()
    };

    const isEdit = $('#standardAmountPK').prop('readonly');
    const method = isEdit ? 'PUT' : 'POST';
    const url = isEdit ? '/admin/api/standard-amounts/' + formData.pk : '/admin/api/standard-amounts';

    $.ajax({
        url: url,
        method: method,
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function(res) {
            if (res.success) {
                Swal.fire('성공', res.message || '저장되었습니다.', 'success');
                standardAmountModal.hide();
                loadStandardAmounts();
            } else {
                Swal.fire('오류', res.message || '저장 중 오류가 발생했습니다.', 'error');
            }
        },
        error: function() {
            Swal.fire('오류', '저장 중 오류가 발생했습니다.', 'error');
        }
    });
}

function deleteStandardAmount(pk) {
    Swal.fire({
        title: '삭제 확인',
        text: '정말 이 기준금액을 삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/admin/api/standard-amounts/' + pk,
                method: 'DELETE',
                success: function(res) {
                    Swal.fire('삭제됨', res.message || '삭제되었습니다.', 'success');
                    loadStandardAmounts();
                },
                error: function() {
                    Swal.fire('오류', '삭제 중 오류가 발생했습니다.', 'error');
                }
            });
        }
    });
}

function openBetterWageModal() {
    $('#betterWageForm')[0].reset();
    $('#betterWageModalLabel').text('나은기준임금 추가');
    $('#betterWagePK').val('').prop('readonly', false);
    betterWageModal.show();
}

function editBetterWage(pk) {
    $.ajax({
        url: '/admin/api/better-wages/' + pk,
        method: 'GET',
        success: function(data) {
            $('#betterWageModalLabel').text('나은기준임금 수정');
            $('#betterWagePK').val(data.pk).prop('readonly', true);
            $('#betterYear').val(data.betterYear);
            $('#betterYearDate').val(data.betterYearDate);
            $('#betterWage').val(data.betterWage);
            betterWageModal.show();
        },
        error: function() {
            Swal.fire('오류', '나은기준임금 정보 조회 중 오류가 발생했습니다.', 'error');
        }
    });
}

function saveBetterWage() {
    const formData = {
        pk: $('#betterWagePK').val(),
        betterYear: $('#betterYear').val(),
        betterYearDate: $('#betterYearDate').val(),
        betterWage: $('#betterWage').val()
    };

    const isEdit = $('#betterWagePK').prop('readonly');
    const method = isEdit ? 'PUT' : 'POST';
    const url = isEdit ? '/admin/api/better-wages/' + formData.pk : '/admin/api/better-wages';

    $.ajax({
        url: url,
        method: method,
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function(res) {
            if (res.success) {
                Swal.fire('성공', res.message || '저장되었습니다.', 'success');
                betterWageModal.hide();
                loadBetterWages();
            } else {
                Swal.fire('오류', res.message || '저장 중 오류가 발생했습니다.', 'error');
            }
        },
        error: function() {
            Swal.fire('오류', '저장 중 오류가 발생했습니다.', 'error');
        }
    });
}

function deleteBetterWage(pk) {
    Swal.fire({
        title: '삭제 확인',
        text: '정말 이 나은기준임금을 삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/admin/api/better-wages/' + pk,
                method: 'DELETE',
                success: function(res) {
                    Swal.fire('삭제됨', res.message || '삭제되었습니다.', 'success');
                    loadBetterWages();
                },
                error: function() {
                    Swal.fire('오류', '삭제 중 오류가 발생했습니다.', 'error');
                }
            });
        }
    });
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