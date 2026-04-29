let participantTable;
let participantModal;

$(document).ready(function() {
    // DataTable 초기화
    participantTable = $('#participantTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json'
        },
        order: [[0, 'desc']],
        pageLength: 25,
        responsive: true
    });

    // 모달 초기화
    participantModal = new bootstrap.Modal(document.getElementById('participantModal'));

    // 지점 목록 로드
    loadBranchOptions();

    // 초기 데이터 로드
    loadParticipants();
});

/**
 * 지점 목록을 드롭다운에 로드
 */
function loadBranchOptions() {
    $.ajax({
        url: '/admin/api/branches',
        method: 'GET',
        success: function(data) {
            const searchOption = '<option value="">전체</option>';
            const modalOption = '<option value="">선택</option>';
            let searchHtml = searchOption;
            let modalHtml = modalOption;
            data.forEach(function(item) {
                const opt = `<option value="${item.branchName}">${item.branchName}</option>`;
                searchHtml += opt;
                modalHtml += opt;
            });
            $('#searchBranch').html(searchHtml);
            $('#branch').html(modalHtml);
        }
    });
}

/**
 * 참여자 목록 로드
 * GET /admin/api/participants
 */
function loadParticipants(params) {
    $.ajax({
        url: '/admin/api/participants',
        method: 'GET',
        data: params || {},
        success: function(data) {
            participantTable.clear();
            data.forEach(function(item) {
                const closedBadge = item.closed
                    ? '<span class="status-badge status-closed">마감</span>'
                    : '<span class="status-badge status-active">진행중</span>';
                participantTable.row.add([
                    item.jobNo,
                    item.participantName || '',
                    item.birthDate || '',
                    item.gender || '',
                    item.branch || '',
                    item.counselorAccount || '',
                    `<span class="status-badge status-active">${item.progressStage || ''}</span>`,
                    item.participantRegDate || '',
                    closedBadge,
                    `<button class="btn btn-sm btn-info action-btn" onclick="viewDetail(${item.jobNo})"><i class="bi bi-eye"></i> 상세</button>
                         <button class="btn btn-sm btn-warning action-btn" onclick="editParticipant(${item.jobNo})"><i class="bi bi-pencil"></i> 수정</button>
                         <button class="btn btn-sm btn-danger action-btn" onclick="deleteParticipant(${item.jobNo})"><i class="bi bi-trash"></i> 삭제</button>`
                ]);
            });
            participantTable.draw(false);
        },
        error: function() {
            Swal.fire('오류', '참여자 목록 로드 중 오류가 발생했습니다.', 'error');
        }
    });
}

/**
 * 참여자 검색
 */
function searchParticipants() {
    const params = {
        searchJobNo: $('#searchJobNo').val(),
        searchName: $('#searchName').val(),
        searchBranch: $('#searchBranch').val(),
        searchStatus: $('#searchStatus').val(),
        searchStartDate: $('#searchStartDate').val(),
        searchEndDate: $('#searchEndDate').val(),
        searchClosed: $('#searchClosed').val()
    };
    loadParticipants(params);
}

/**
 * 참여자 추가 모달 열기
 */
function openAddModal() {
    Swal.fire('안내', '참여자 정보 추가는 메인 상담 화면에서 진행해 주세요. 관리자 패널에서는 참여자 조회 및 삭제만 가능합니다.', 'info');
}

/**
 * 참여자 상세 조회 (read-only 모달)
 */
function viewDetail(jobNo) {
    $.ajax({
        url: '/admin/api/participants/' + jobNo,
        method: 'GET',
        success: function(data) {
            $('#participantModalLabel').text('참여자 상세 조회 (읽기 전용)');
            $('#jobNo').val(data.jobNo).prop('readonly', true);
            $('#participantName').val(data.participantName).prop('readonly', true);
            $('#birthDate').val(data.birthDate).prop('readonly', true);
            $('#gender').val(data.gender).prop('disabled', true);
            $('#branch').val(data.branch).prop('disabled', true);
            $('#counselorAccount').val(data.counselorAccount).prop('readonly', true);
            $('#progressStage').val(data.progressStage).prop('disabled', true);
            $('#recruitPath').val(data.recruitPath).prop('readonly', true);
            $('#participationType').val(data.participationType).prop('readonly', true);
            $('#education').val(data.education).prop('readonly', true);
            $('#career').val(data.career).prop('readonly', true);
            $('#specialClass').val(data.specialClass).prop('readonly', true);
            $('#employmentCapacity').val(data.employmentCapacity).prop('readonly', true);
            $('#desiredJob').val(data.desiredJob).prop('readonly', true);
            $('#desiredSalary').val(data.desiredSalary).prop('readonly', true);
            $('#memo').val(data.memo).prop('readonly', true);
            participantModal.show();
        },
        error: function() {
            Swal.fire('오류', '참여자 정보 조회 중 오류가 발생했습니다.', 'error');
        }
    });
}

/**
 * 참여자 수정 모달 열기
 */
function editParticipant(jobNo) {
    $.ajax({
        url: '/admin/api/participants/' + jobNo,
        method: 'GET',
        success: function(data) {
            $('#participantModalLabel').text('참여자 상세 조회');
            // 필드 readonly 해제
            $('#jobNo, #participantName, #birthDate, #counselorAccount, #recruitPath, #participationType, #education, #career, #specialClass, #employmentCapacity, #desiredJob, #desiredSalary, #memo').prop('readonly', false);
            $('#gender, #branch, #progressStage').prop('disabled', false);

            $('#jobNo').val(data.jobNo).prop('readonly', true);
            $('#participantName').val(data.participantName);
            $('#birthDate').val(data.birthDate);
            $('#gender').val(data.gender);
            $('#branch').val(data.branch);
            $('#counselorAccount').val(data.counselorAccount);
            $('#progressStage').val(data.progressStage);
            $('#recruitPath').val(data.recruitPath);
            $('#participationType').val(data.participationType);
            $('#education').val(data.education);
            $('#career').val(data.career);
            $('#specialClass').val(data.specialClass);
            $('#employmentCapacity').val(data.employmentCapacity);
            $('#desiredJob').val(data.desiredJob);
            $('#desiredSalary').val(data.desiredSalary);
            $('#memo').val(data.memo);
            participantModal.show();
        },
        error: function() {
            Swal.fire('오류', '참여자 정보 조회 중 오류가 발생했습니다.', 'error');
        }
    });
}

function saveParticipant() {
    Swal.fire('안내', '참여자 정보 수정은 메인 상담 화면에서 진행해 주세요. 관리자 패널에서는 참여자 조회 및 삭제만 가능합니다.', 'info');
}

function deleteParticipant(jobNo) {
    Swal.fire({
        title: '삭제 확인',
        text: '정말 이 참여자를 삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/admin/api/participants/' + jobNo,
                method: 'DELETE',
                success: function(res) {
                    Swal.fire('삭제됨', res.message || '참여자가 삭제되었습니다.', 'success');
                    loadParticipants();
                },
                error: function() {
                    Swal.fire('오류', '삭제 중 오류가 발생했습니다.', 'error');
                }
            });
        }
    });
}

/**
 * 엑셀 다운로드
 */
function exportToExcel() {
    window.location.href = '/admin/api/participants/export';
}