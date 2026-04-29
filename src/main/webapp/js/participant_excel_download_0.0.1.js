$(document).ready(function () {

    let excelDownloadButton = $('#excelDownload');
    excelDownloadButton.on('click', function () {
        downloadExcel();
    });

    /**
     * 엑셀 다운로드 - 간단한 방식
     */
    function downloadExcel() {
        // 로딩 메시지 표시
        showLoading('엑셀 파일을 준비하는 중입니다...');

        // 안정적인 방식으로 다운로드 시작
        try {
            const branchManagementPageFlag = $('#branchManagementPageFlag').val();
            // participantExcel.login은 기존에 잘 작동하던 엔드포인트
            window.location.href = '/participantExcel.login' + searchHref(1) + '&branchManagementPageFlag=' + branchManagementPageFlag;
            console.log('/participantExcel.login' + searchHref(1) + '&branchManagementPageFlag=' + branchManagementPageFlag);

            // 3초 후 로딩 숨기기
            setTimeout(hideLoading, 3000);
        } catch (e) {
            hideLoading();
            showError('엑셀 다운로드 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
            console.error('다운로드 오류:', e);
        }
    }

    // 로딩 표시 함수
    function showLoading(message) {
        // 기존 로딩 제거
        hideLoading();

        // 로딩 요소 생성
        const overlay = document.createElement('div');
        overlay.id = 'loading-overlay';
        overlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 9999;
        `;

        const loadingBox = document.createElement('div');
        loadingBox.style.cssText = `
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            text-align: center;
            max-width: 80%;
        `;

        const spinner = document.createElement('div');
        spinner.style.cssText = `
            border: 4px solid #f3f3f3;
            border-top: 4px solid #3498db;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            margin: 0 auto 15px;
            animation: spin 1s linear infinite;
        `;

        // 애니메이션 정의
        const style = document.createElement('style');
        style.textContent = `
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
        `;
        document.head.appendChild(style);

        const messageElem = document.createElement('p');
        messageElem.style.margin = '0';
        messageElem.textContent = message || '처리 중...';

        loadingBox.appendChild(spinner);
        loadingBox.appendChild(messageElem);
        overlay.appendChild(loadingBox);
        document.body.appendChild(overlay);
    }

    // 로딩 숨기기 함수
    function hideLoading() {
        const overlay = document.getElementById('loading-overlay');
        if (overlay) {
            document.body.removeChild(overlay);
        }
    }

    // 오류 메시지 표시 함수
    function showError(message) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({
                icon: 'error',
                title: '오류',
                text: message
            });
        } else {
            alert(message);
        }
    }

    // searchHref 함수가 없을 경우를 대비한 fallback
    if (typeof window.searchHref !== 'function') {
        window.searchHref = function(param) {
            return window.location.search || '';
        };
    }
});