let certificateTable, trainingTable;
let certificateModal, trainingModal;

$(document).ready(function() {
    certificateTable = $('#certificateTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[0, 'desc']],
        pageLength: 15
    });

    trainingTable = $('#trainingTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[0, 'desc']],
        pageLength: 15
    });

    certificateModal = new bootstrap.Modal(document.getElementById('certificateModal'));
    trainingModal = new bootstrap.Modal(document.getElementById('trainingModal'));

    loadCertificates();
    loadTrainings();
});

function loadCertificates() {
    $.ajax({
        url: '/admin/api/certificates',
        method: 'GET',
        success: function(data) {
            certificateTable.clear();
            data.forEach(function(item) {
                certificateTable.row.add([
                    item.certificateNo,
                    item.certJobNo || '',
                    item.certificateName || '',
                    `<button class="btn btn-sm btn-warning" onclick="editCertificate(${item.certificateNo})"><i class="bi bi-pencil"></i> 수정</button>
                     <button class="btn btn-sm btn-danger" onclick="deleteCertificate(${item.certificateNo})"><i class="bi bi-trash"></i> 삭제</button>`
                ]);
            });
            certificateTable.draw(false);
        },
        error: function() {
            Swal.fire('오류', '자격증 목록 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

function loadTrainings() {
    $.ajax({
        url: '/admin/api/trainings',
        method: 'GET',
        success: function(data) {
            trainingTable.clear();
            data.forEach(function(item) {
                trainingTable.row.add([
                    item.trainingNo,
                    item.trainJobNo || '',
                    item.trainingName || '',
                    `<button class="btn btn-sm btn-warning" onclick="editTraining(${item.trainingNo})"><i class="bi bi-pencil"></i> 수정</button>
                     <button class="btn btn-sm btn-danger" onclick="deleteTraining(${item.trainingNo})"><i class="bi bi-trash"></i> 삭제</button>`
                ]);
            });
            trainingTable.draw(false);
        },
        error: function() {
            Swal.fire('오류', '직업훈련 목록 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

function openCertificateModal() {
    $('#certificateForm')[0].reset();
    $('#certificateModalLabel').text('자격증 추가');
    $('#cert-id').val('');
    certificateModal.show();
}

function editCertificate(id) {
    const rowData = certificateTable.rows().data().toArray().find(r => r[0] == id);
    if (rowData) {
        $('#certificateModalLabel').text('자격증 수정');
        $('#cert-id').val(rowData[0]);
        $('#cert-jobNo').val(rowData[1]);
        $('#cert-name').val(rowData[2]);
        certificateModal.show();
    }
}

function saveCertificate() {
    const formData = {
        certificateNo: $('#cert-id').val(),
        certJobNo: $('#cert-jobNo').val(),
        certificateName: $('#cert-name').val()
    };

    const isEdit = formData.certificateNo !== '';
    const method = isEdit ? 'PUT' : 'POST';
    const url = isEdit ? '/admin/api/certificates/' + formData.certificateNo : '/admin/api/certificates';

    $.ajax({
        url: url,
        method: method,
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function(res) {
            if (res.success) {
                Swal.fire('성공', res.message || '저장되었습니다.', 'success');
                certificateModal.hide();
                loadCertificates();
            } else {
                Swal.fire('오류', res.message || '저장 중 오류가 발생했습니다.', 'error');
            }
        },
        error: function() {
            Swal.fire('오류', '저장 중 오류가 발생했습니다.', 'error');
        }
    });
}

function deleteCertificate(id) {
    Swal.fire({
        title: '삭제 확인',
        text: '정말 이 자격증을 삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/admin/api/certificates/' + id,
                method: 'DELETE',
                success: function(res) {
                    Swal.fire('삭제됨', res.message || '삭제되었습니다.', 'success');
                    loadCertificates();
                },
                error: function() {
                    Swal.fire('오류', '삭제 중 오류가 발생했습니다.', 'error');
                }
            });
        }
    });
}

function openTrainingModal() {
    $('#trainingForm')[0].reset();
    $('#trainingModalLabel').text('직업훈련 추가');
    $('#train-id').val('');
    trainingModal.show();
}

function editTraining(id) {
    const rowData = trainingTable.rows().data().toArray().find(r => r[0] == id);
    if (rowData) {
        $('#trainingModalLabel').text('직업훈련 수정');
        $('#train-id').val(rowData[0]);
        $('#train-jobNo').val(rowData[1]);
        $('#train-name').val(rowData[2]);
        trainingModal.show();
    }
}

function saveTraining() {
    const formData = {
        trainingNo: $('#train-id').val(),
        trainJobNo: $('#train-jobNo').val(),
        trainingName: $('#train-name').val()
    };

    const isEdit = formData.trainingNo !== '';
    const method = isEdit ? 'PUT' : 'POST';
    const url = isEdit ? '/admin/api/trainings/' + formData.trainingNo : '/admin/api/trainings';

    $.ajax({
        url: url,
        method: method,
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function(res) {
            if (res.success) {
                Swal.fire('성공', res.message || '저장되었습니다.', 'success');
                trainingModal.hide();
                loadTrainings();
            } else {
                Swal.fire('오류', res.message || '저장 중 오류가 발생했습니다.', 'error');
            }
        },
        error: function() {
            Swal.fire('오류', '저장 중 오류가 발생했습니다.', 'error');
        }
    });
}

function deleteTraining(id) {
    Swal.fire({
        title: '삭제 확인',
        text: '정말 이 직업훈련을 삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/admin/api/trainings/' + id,
                method: 'DELETE',
                success: function(res) {
                    Swal.fire('삭제됨', res.message || '삭제되었습니다.', 'success');
                    loadTrainings();
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