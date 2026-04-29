/**
 * 이력서 요청 목록 페이지 JavaScript
 * CounselMain 패키지용 버전
 */

$(document).ready(function() {
    let currentRequest = {}; // 현재 처리중인 요청 정보

    // 새로고침 버튼 이벤트
    $('#refreshBtn').on('click', function() {
        location.reload();
    });

    // 필터 폼 제출 이벤트
    $('#filterForm').on('submit', function(e) {
        e.preventDefault();

        const status = $('#statusFilter').val();
        const companyName = $('#companyNameFilter').val().trim();
        const participantName = $('#participantFilter').val().trim();

        // URL 파라미터 구성
        const params = new URLSearchParams(window.location.search);
        params.delete('page'); // 검색 시 페이지 리셋

        if (status) {
            params.set('status', status);
        } else {
            params.delete('status');
        }

        if (companyName) {
            params.set('companyName', companyName);
        } else {
            params.delete('companyName');
        }

        if (participantName) {
            params.set('participantPartic', participantName);
        } else {
            params.delete('participantPartic');
        }

        // 페이지 리로드
        window.location.search = params.toString();
    });

    // 페이지 로드 시 필터 값 복원
    const urlParams = new URLSearchParams(window.location.search);
    $('#statusFilter').val(urlParams.get('status') || '');
    $('#companyNameFilter').val(urlParams.get('companyName') || '');
    $('#participantFilter').val(urlParams.get('participantPartic') || '');

    // 상태 변경 버튼 이벤트
    $('.status-update-btn').on('click', function() {
        const $btn = $(this);
        const $row = $btn.closest('tr');

        currentRequest = {
            participantJobNo: $btn.data('job-number'),
            companyName: $btn.data('company-name'),
            registrationDate: $btn.data('registration-date'),
            newStatus: $btn.data('status'),
            participantName: $row.find('td:nth-child(2) strong').text(),
            $row: $row
        };

        // 모달 정보 업데이트
        $('#modalParticipant').text(currentRequest.participantName);
        $('#modalCompany').text(currentRequest.companyName);
        $('#modalNewStatus').text(currentRequest.newStatus);

        // 모달 표시
        const modal = new bootstrap.Modal($('#statusUpdateModal'));
        modal.show();
    });

    // 상태 변경 확인 버튼 이벤트
    $('#confirmStatusUpdate').on('click', function() {
        updateRequestStatus(currentRequest);
    });

    /**
     * 이력서 요청 상태 업데이트
     */
    function updateRequestStatus(request) {
        const updateData = {
            participantJobNo: request.participantJobNo,
            companyName: request.companyName,
            registrationDate: request.registrationDate,
            status: request.newStatus
        };

        // 버튼 로딩 상태
        const $confirmBtn = $('#confirmStatusUpdate');
        const originalText = $confirmBtn.text();
        $confirmBtn.prop('disabled', true).html('<i class="spinner-border spinner-border-sm" role="status"></i> 처리중...');

        $.ajax({
            url: '/resumeRequest/statusUpdate.login',
            type: 'POST',
            contentType: 'application/json; charset=UTF-8',
            data: JSON.stringify(updateData),
            success: function(response) {
                try {
                    const result = typeof response === 'string' ? JSON.parse(response) : response;

                    if (result.statusData === 'success') {
                        showToast('성공', result.message, 'success');

                        // 페이지 새로고침 대신 해당 행만 업데이트
                        updateRowStatus(request.$row, request.newStatus);

                        // 모달 닫기
                        bootstrap.Modal.getInstance($('#statusUpdateModal')).hide();
                    } else {
                        showToast('오류', result.message, 'error');
                    }
                } catch (e) {
                    console.error('Response parsing error:', e);
                    showToast('오류', '서버 응답 처리 중 오류가 발생했습니다.', 'error');
                }
            },
            error: function(xhr, status, error) {
                console.error('AJAX Error:', error);
                let errorMessage = '상태 변경 중 오류가 발생했습니다.';

                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMessage = xhr.responseJSON.message;
                } else if (xhr.responseText) {
                    try {
                        const errorResponse = JSON.parse(xhr.responseText);
                        errorMessage = errorResponse.message || errorMessage;
                    } catch (e) {
                        // JSON 파싱 실패 시 기본 메시지 사용
                    }
                }

                showToast('오류', errorMessage, 'error');
            },
            complete: function() {
                // 버튼 상태 복원
                $confirmBtn.prop('disabled', false).text(originalText);
            }
        });
    }

    /**
     * 테이블 행의 상태 업데이트
     */
    function updateRowStatus($row, newStatus) {
        const $statusCell = $row.find('td:nth-child(9)');
        const $actionCell = $row.find('td:nth-child(10)');

        // 상태 배지 업데이트
        let badgeClass = 'bg-secondary';
        switch (newStatus) {
            case '요청':
                badgeClass = 'bg-warning text-dark';
                break;
            case '예정':
                badgeClass = 'bg-success';
                break;
            case '거부':
                badgeClass = 'bg-danger';
                break;
            case '완료':
                badgeClass = 'bg-primary';
                break;
        }

        $statusCell.html(`<span class="badge ${badgeClass}">${newStatus}</span>`);

        // 액션 버튼 업데이트
        const jobNumber = $row.find('code').text();
        const companyName = $row.find('td:nth-child(4) strong').text();
        const registrationDate = $row.find('.status-update-btn').first().data('registration-date');

        let actionButtons = '';

        if (newStatus === '요청') {
            actionButtons = `
                <button type="button" class="btn btn-outline-success btn-sm status-update-btn"
                        data-job-number="${jobNumber}"
                        data-company-name="${companyName}"
                        data-registration-date="${registrationDate}"
                        data-status="예정">
                    <i class="bi bi-check-lg"></i> 예정
                </button>
                <button type="button" class="btn btn-outline-danger btn-sm status-update-btn"
                        data-job-number="${jobNumber}"
                        data-company-name="${companyName}"
                        data-registration-date="${registrationDate}"
                        data-status="거부">
                    <i class="bi bi-x-lg"></i> 거부
                </button>
            `;
        } else if (newStatus === '예정') {
            actionButtons = `
                <button type="button" class="btn btn-outline-primary btn-sm status-update-btn"
                        data-job-number="${jobNumber}"
                        data-company-name="${companyName}"
                        data-registration-date="${registrationDate}"
                        data-status="완료">
                    <i class="bi bi-check2-all"></i> 완료
                </button>
            `;
        }

        // 상세보기 버튼은 항상 포함
        actionButtons += `
            <a href="/jobPlacement/placementDetail?jobNumber=${jobNumber}"
               class="btn btn-outline-info btn-sm">
                <i class="bi bi-eye"></i> 상세
            </a>
        `;

        $actionCell.find('.btn-group-vertical').html(actionButtons);

        // 새로운 버튼에 이벤트 리스너 다시 등록
        $actionCell.find('.status-update-btn').on('click', function() {
            const $btn = $(this);
            const $row = $btn.closest('tr');

            currentRequest = {
                participantJobNo: $btn.data('job-number'),
                companyName: $btn.data('company-name'),
                registrationDate: $btn.data('registration-date'),
                newStatus: $btn.data('status'),
                participantName: $row.find('td:nth-child(2) strong').text(),
                $row: $row
            };

            $('#modalParticipant').text(currentRequest.participantName);
            $('#modalCompany').text(currentRequest.companyName);
            $('#modalNewStatus').text(currentRequest.newStatus);

            const modal = new bootstrap.Modal($('#statusUpdateModal'));
            modal.show();
        });
    }

    /**
     * 토스트 메시지 표시
     */
    function showToast(title, message, type = 'info') {
        const toastId = 'toast-' + Date.now();
        const iconClass = type === 'success' ? 'bi-check-circle-fill text-success' :
                         type === 'error' ? 'bi-exclamation-triangle-fill text-danger' :
                         'bi-info-circle-fill text-primary';

        const toastHtml = `
            <div id="${toastId}" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header">
                    <i class="bi ${iconClass} me-2"></i>
                    <strong class="me-auto">${title}</strong>
                    <small class="text-muted">방금 전</small>
                    <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
                <div class="toast-body">
                    ${message}
                </div>
            </div>
        `;

        $('#toastContainer').append(toastHtml);

        const toast = new bootstrap.Toast($('#' + toastId), {
            autohide: true,
            delay: 5000
        });

        toast.show();

        // 토스트가 숨겨진 후 DOM에서 제거
        $('#' + toastId).on('hidden.bs.toast', function() {
            $(this).remove();
        });
    }

    /**
     * 초기화 함수
     */
    function initializePage() {
        console.log('이력서 요청 목록 페이지 초기화 완료');

        // 현재 시간 표시 (디버깅용)
        const now = new Date();
        console.log('페이지 로드 시간:', now.toLocaleString());

        // 테이블 행 수 표시
        const rowCount = $('tbody tr').length;
        console.log('현재 표시된 요청 수:', rowCount);
    }

    // 페이지 초기화 실행
    initializePage();
});