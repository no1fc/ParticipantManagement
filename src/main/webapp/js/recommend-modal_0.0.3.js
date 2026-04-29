// ===========================
// WebSocket 알림 수신 (GNB에서 커스텀 이벤트로 전달받음)
// ===========================

/**
 * WebSocket 추천 완료 알림 수신 핸들러
 */
function handleRecommendNotification(notification) {
    var notifJobSeekerNo = notification.jobSeekerNo;

    // 해당 참여자의 진행 중 플래그 제거
    if (notifJobSeekerNo) {
        sessionStorage.removeItem(_recommendStorageKey(notifJobSeekerNo));
    }

    // 슬롯 표시 업데이트
    if (notification.activeRecommendCount !== undefined) {
        updateSlotDisplay(notification.activeRecommendCount);
    }

    // 모달이 열려 있고 같은 참여자를 보고 있는 경우 → 버튼 재활성화 + 결과 표시
    var modal = document.getElementById('recommendModal');
    var currentGujikNo = _getCurrentModalJobSeekerNo();
    var isModalOpenForThis = modal && modal.style.display === 'flex'
        && currentGujikNo == notifJobSeekerNo;

    if (isModalOpenForThis) {
        enableAiRecommendButton(notifJobSeekerNo);

        if (notification.reused) {
            var lastTime = notification.lastRecommendedAt || '';
            document.getElementById('recommendStatusMsg').innerText =
                '24시간 이내에 이미 추천이 완료되었습니다. (마지막 추천: ' + lastTime + ')';
        } else if (notification.success) {
            var count = notification.savedCount || 0;
            document.getElementById('recommendStatusMsg').innerText =
                count > 0
                    ? count + '개의 채용정보가 추천 저장되었습니다.'
                    : (notification.message || '조건에 맞는 채용정보를 찾지 못했습니다.');
            if (count > 0) loadRecommendData(notifJobSeekerNo);
        } else {
            document.getElementById('recommendStatusMsg').innerText =
                '추천 실패: ' + (notification.message || '알 수 없는 오류');
        }
    }
}

/**
 * 현재 진행 중인 AI 추천 건수 표시
 */
function updateSlotDisplay(activeCount) {
    var active = activeCount || 0;
    var el = document.getElementById('recommendSlotCount');
    var info = document.getElementById('recommendSlotInfo');
    if (el && info) {
        if (active > 0) {
            info.style.display = 'inline';
            el.textContent = active;
            if (active >= 4) {
                el.className = 'slot-warning';
            } else {
                el.className = '';
            }
        } else {
            info.style.display = 'none';
        }
    }
}

/**
 * 토스트 알림 표시
 */
function showRecommendToast(message, type) {
    var container = document.getElementById('recommendToastContainer');
    if (!container) return;

    var iconMap = { success: '&#10003;', error: '&#10007;', warning: '&#9888;' };
    var toast = document.createElement('div');
    toast.className = 'recommend-toast recommend-toast-' + type;
    toast.innerHTML = '<span class="toast-icon">' + (iconMap[type] || '') + '</span>'
        + '<span class="toast-message">' + message + '</span>'
        + '<button class="toast-close" onclick="this.parentElement.remove()">&#10005;</button>';
    container.appendChild(toast);

    setTimeout(function() {
        if (toast.parentElement) {
            toast.classList.add('recommend-toast-fade-out');
            setTimeout(function() { toast.remove(); }, 300);
        }
    }, 5000);
}

// ===========================
// AI 추천 버튼 상태 관리
// ===========================

/**
 * sessionStorage 키 생성 (사용자 ID 포함 — 다른 상담사 로그인 시 충돌 방지)
 */
function _recommendStorageKey(jobSeekerNo) {
    return 'recommendInProgress_' + (JOBMOA_USER_ID || '') + '_' + jobSeekerNo;
}

/**
 * AI 추천 버튼 비활성화 (sessionStorage 기반 상태 유지)
 */
function disableAiRecommendButton(jobSeekerNo) {
    var btn = document.getElementById('btnAiRecommend');
    btn.style.opacity = '0.5';
    btn.style.cursor = 'not-allowed';
    btn.disabled = true;
    document.getElementById('loadingSpinner').style.display = 'inline-block';
    if (jobSeekerNo) {
        sessionStorage.setItem(_recommendStorageKey(jobSeekerNo), 'true');
    }
}

/**
 * AI 추천 버튼 재활성화 (특정 참여자의 진행 플래그만 제거)
 */
function enableAiRecommendButton(jobSeekerNo) {
    var btn = document.getElementById('btnAiRecommend');
    btn.style.opacity = '';
    btn.style.cursor = '';
    btn.disabled = false;
    document.getElementById('loadingSpinner').style.display = 'none';

    if (jobSeekerNo) {
        // 해당 참여자의 진행 중 플래그만 제거
        sessionStorage.removeItem(_recommendStorageKey(jobSeekerNo));
    }
}

/**
 * 현재 모달에 열린 참여자의 구직번호 가져오기
 */
function _getCurrentModalJobSeekerNo() {
    var btn = document.getElementById('btnAiRecommend');
    return btn ? parseInt(btn.getAttribute('data-gujik')) || 0 : 0;
}

/**
 * AI 추천 버튼 쿨다운 비활성화 (24시간 이내 재사용 차단)
 */
function disableAiRecommendButtonCooldown(lastRecommendedAt) {
    var btn = document.getElementById('btnAiRecommend');
    btn.style.opacity = '0.5';
    btn.style.cursor = 'not-allowed';
    btn.disabled = true;
    document.getElementById('loadingSpinner').style.display = 'none';
    document.getElementById('recommendStatusMsg').innerText =
        '24시간 이내에 이미 추천이 완료되었습니다. (마지막 추천: ' + (lastRecommendedAt || '') + ')';
}

/**
 * AI 추천 진행 중 여부 확인
 */
function isRecommendInProgress(jobSeekerNo) {
    return sessionStorage.getItem(_recommendStorageKey(jobSeekerNo)) === 'true';
}

// ===========================
// 추천 모달 기능
// ===========================

/**
 * 추천 모달 열기 함수
 * @param {HTMLElement} btn - 열기 버튼 요소
 */
function openRecommendModal(btn) {
    let gujikNo = btn.getAttribute('data-gujik')? btn.getAttribute('data-gujik') : 0;
    let name    = btn.getAttribute('data-name')? btn.getAttribute('data-name') : '';
    let stage   = btn.getAttribute('data-stage')? btn.getAttribute('data-stage') : '';

    // 모달 헤더 표시
    document.getElementById('modalTitle').innerText = name + ' - 추천 채용정보';

    // AI 추천 저장 버튼: 진행단계 조건 체크
    const allowStages = ['IAP 후', '미취업사후관리'];
    const btnAi = document.getElementById('btnAiRecommend');
    if (allowStages.includes(stage)) {
        btnAi.style.display = 'inline-block';
        btnAi.setAttribute('data-gujik', gujikNo);
    } else {
        btnAi.style.display = 'none';
    }

    // 리스트 초기화
    resetModalContent();

    // 버튼 상태 복원: 이 참여자가 진행 중이면 비활성화, 아니면 활성화
    if (isRecommendInProgress(gujikNo)) {
        disableAiRecommendButton(gujikNo);
        document.getElementById('recommendStatusMsg').innerText = 'AI 추천 처리 중... 완료되면 알림으로 안내됩니다.';
    } else {
        // 다른 참여자의 진행 상태와 무관하게 이 참여자의 버튼은 활성화
        enableAiRecommendButton(gujikNo);
    }

    // 모달 오픈
    document.getElementById('recommendModal').style.display = 'flex';

    // 데이터 로드 (5주차 AJAX 연동 시 구현)
    loadRecommendData(gujikNo);
}

/**
 * 추천 모달 닫기 함수
 */
function closeRecommendModal() {
    document.getElementById('recommendModal').style.display = 'none';
    resetModalContent();
}


/**
 * 모달 내부 내용 초기화 함수
 */
function resetModalContent() {
    document.getElementById('infoName').innerText = '';
    document.getElementById('infoGujikNo').innerText = '';
    document.getElementById('infoStage').innerText = '';
    document.getElementById('infoJibjoong').innerText = '';
    document.getElementById('infoEducation').innerText = '';
    document.getElementById('infoMajor').innerText = '';
    document.getElementById('infoCategory').innerText = '';
    document.getElementById('infoWantedJob').innerText = '';
    document.getElementById('infoAlsonDetail').innerText = '';
    document.getElementById('recommendListBody').innerHTML = '';
    document.getElementById('recommendListTable').style.display = 'none';
    document.getElementById('recommendListEmpty').style.display = 'none';
    document.getElementById('bestRecommendArea').style.display = 'none';
    document.getElementById('bestRecommendContent').innerHTML = '';
    document.getElementById('recommendStatusMsg').innerText = '';
}

$(document).ready(function () {
    /**
     * 추천 모달 닫기 함수
     */
    document.getElementById('recommendModal').addEventListener('click', function(e) {
        if (e.target === this) closeRecommendModal();
    });

    // GNB WebSocket에서 커스텀 이벤트로 추천 완료 알림 수신
    document.addEventListener('recommendComplete', function(e) {
        handleRecommendNotification(e.detail);
    });
})

/**
 * ESC 키로 모달 닫기 함수
 */
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') closeRecommendModal();
});


function loadRecommendData(gujikNo) {
    // 로딩 표시
    document.getElementById('recommendLoading').style.display = 'block';

    $.ajax({
        url: '/api/recommend/detail',
        type: 'POST',
        data: JSON.stringify({ jobSeekerNo: gujikNo }),
        dataType: 'json',
        contentType: 'application/json; charset=UTF-8',
        success: function(response) {
            document.getElementById('recommendLoading').style.display = 'none';
            if (response.success) {
                // 리스트 초기화 (진행 중 상태 메시지 보존)
                var savedStatusMsg = document.getElementById('recommendStatusMsg').innerText;
                var savedInProgress = isRecommendInProgress(gujikNo);
                resetModalContent();

                bindParticipantInfo(response.participant);
                bindRecommendList(response.recommendList);
                bindBestRecommend(response.bestRecommend);

                // 24시간 쿨다운 체크: 이미 추천된 참여자는 버튼 비활성화
                if (response.cooldownActive) {
                    disableAiRecommendButtonCooldown(response.lastRecommendedAt);
                } else if (savedInProgress) {
                    // 진행 중이었으면 상태 복원
                    disableAiRecommendButton(gujikNo);
                    document.getElementById('recommendStatusMsg').innerText = savedStatusMsg || 'AI 추천 처리 중...';
                }
            } else {
                document.getElementById('recommendStatusMsg').innerText = response.message;
            }
        },
        error: function() {
            document.getElementById('recommendLoading').style.display = 'none';
            document.getElementById('recommendStatusMsg').innerText
                = '서버 오류가 발생했습니다.';
        }
    });
}

function saveAiRecommend() {
    let jobSeekerNo = parseInt(
        document.getElementById('btnAiRecommend').getAttribute('data-gujik'));
    if (!jobSeekerNo) return;

    // 이미 진행 중인 경우 차단
    if (isRecommendInProgress(jobSeekerNo)) {
        document.getElementById('recommendStatusMsg').innerText = 'AI 추천이 진행 중입니다. 잠시 기다려주세요.';
        return;
    }

    if (!confirm('AI 추천 채용정보를 저장하시겠습니까?\n기존 저장된 정보에서 재 추천 됩니다.\n\n요청 후 모달을 닫고 다른 참여자를 선택할 수 있습니다.\n결과는 GNB 알림으로 확인할 수 있습니다.')) return;

    // 버튼 비활성화 (sessionStorage 기반 — 해당 참여자만)
    disableAiRecommendButton(jobSeekerNo);
    document.getElementById('recommendStatusMsg').innerText = 'AI 추천 요청이 전송되었습니다. 모달을 닫고 다른 참여자를 조회할 수 있습니다.';

    $.ajax({
        url: '/api/recommend/saveRecommendAI',
        type: 'POST',
        data: JSON.stringify({ jobSeekerNo: jobSeekerNo, forceRefresh: false }),
        dataType: 'json',
        contentType: 'application/json; charset=UTF-8',
        timeout: 180000,
        success: function(response) {
            // 결과는 WebSocket 알림(handleRecommendNotification)에서도 처리됨
            // 모달이 열려 있고, 같은 참여자를 보고 있는 경우에만 UI 업데이트
            var currentGujik = _getCurrentModalJobSeekerNo();
            if (currentGujik !== jobSeekerNo) return; // 다른 참여자 모달이 열려있으면 무시

            if (response.reused) {
                enableAiRecommendButton(jobSeekerNo);
                var lastTime = response.lastRecommendedAt || '';
                document.getElementById('recommendStatusMsg').innerText =
                    '24시간 이내에 이미 추천이 완료되었습니다. (마지막 추천: ' + lastTime + ')';
                return;
            }

            enableAiRecommendButton(jobSeekerNo);

            if (response.activeRecommendCount !== undefined) {
                updateSlotDisplay(response.activeRecommendCount);
            }

            if (response.success) {
                let count = response.savedCount || 0;
                document.getElementById('recommendStatusMsg').innerText =
                    count > 0
                        ? count + '개의 채용정보가 추천 저장되었습니다.'
                        : (response.message || '조건에 맞는 채용정보를 찾지 못했습니다.');
                if (count > 0) loadRecommendData(jobSeekerNo);
            } else {
                document.getElementById('recommendStatusMsg').innerText =
                    '저장 실패: ' + (response.message || '알 수 없는 오류');
            }
        },
        error: function(xhr, status) {
            var currentGujik = _getCurrentModalJobSeekerNo();

            // sessionStorage 진행 플래그 제거 (에러 시)
            sessionStorage.removeItem(_recommendStorageKey(jobSeekerNo));

            // 현재 모달이 같은 참여자면 UI 업데이트
            if (currentGujik === jobSeekerNo) {
                enableAiRecommendButton(jobSeekerNo);

                if (xhr.status === 429) {
                    var errBody = null;
                    try { errBody = JSON.parse(xhr.responseText); } catch(e) {}
                    var errMsg = (errBody && errBody.message)
                        ? errBody.message
                        : '동시 AI 추천 요청이 최대 5건을 초과했습니다.';
                    document.getElementById('recommendStatusMsg').innerText = errMsg;
                    if (errBody && errBody.activeRecommendCount !== undefined) {
                        updateSlotDisplay(errBody.activeRecommendCount);
                    }
                } else {
                    document.getElementById('recommendStatusMsg').innerText =
                        status === 'timeout'
                            ? 'AI 처리 시간이 초과되었습니다. 다시 시도해주세요.'
                            : '서버 오류가 발생했습니다.';
                }
            }
        }
    });
}

function bindParticipantInfo(p) {
    if (!p) return;
    document.getElementById('infoName').innerText      = p.infoName || '-'; // 참여자명
    document.getElementById('infoGujikNo').innerText   = p.infoGujikNo || '-'; // 구직번호
    document.getElementById('infoStage').innerText     = p.infoStage || '-'; // 진행단계
    document.getElementById('infoJibjoong').innerText
        = p.infoFocus ? '집중알선 대상' : '일반'; // 집중알선 여부
    document.getElementById('infoEducation').innerText = p.infoEducation || '-'; // 학력
    document.getElementById('infoMajor').innerText     = p.infoMajor || '-'; // 전공

    const categoryList = p.categoryList;
    if (categoryList && categoryList.length > 0) {
        for (let i = 0; i < categoryList.length; i++) {
            const category = categoryList[i];
            document.getElementById('infoCategory').innerHTML
                += `<p class="category-rank">${i + 1}. ${(category.categoryMain || '') + ' > ' + (category.categoryMiddle || '')}</p>`; // 카테고리(대,중)분류
            document.getElementById('infoWantedJob').innerHTML
                += `<p class="wanted-job-rank">${i + 1}. ${category.infoJob || '-'}</p>`; // 희망직무
        }
    } else {
        document.getElementById('infoCategory').innerHTML = '-';
        document.getElementById('infoWantedJob').innerHTML = '-';
    }

    const referral = p.referral || {};
    const reasonInfoAlsonDetail = referral.infoAlsonDetail || '';
    document.getElementById('infoAlsonDetail').innerHTML =
        createToggleReasonHTML(reasonInfoAlsonDetail); // 알선상세정보

    const reasonRecommendationReason = referral.infoAdditionalInfo || '';
    document.getElementById('infoAdditionalInfo').innerHTML =
        createToggleReasonHTML(reasonRecommendationReason); // 추천사

    // AI 추천 저장 버튼 제어 (서버 응답 기준)
    const allowStages = ['IAP 후', '미취업사후관리'];
    const btnAi = document.getElementById('btnAiRecommend');
    if (p.infoStage && allowStages.indexOf(p.infoStage) !== -1) {
        btnAi.style.display = 'inline-block';
        btnAi.setAttribute('data-gujik', p.infoGujikNo);
    } else {
        btnAi.style.display = 'none';
    }
}

function bindRecommendList(list) {
    let tbody = document.getElementById('recommendListBody');
    tbody.innerHTML = '';

    if (!list || list.length === 0) {
        document.getElementById('recommendListEmpty').style.display = 'block';
        document.getElementById('recommendListTable').style.display = 'none';
        return;
    }

    document.getElementById('recommendListEmpty').style.display = 'none';
    document.getElementById('recommendListTable').style.display = 'table';

    list.forEach(function(item, idx) {
        // bindRecommendList 내 수정 — DTO 필드명 기준 (recommendationReason)
        const reason = item.recommendationReason || '';

        let row = '<tr' + (item.bestJobInfo? ' class="best-row"' : '') + '>' // 추천채용정보
            + '<td><input type="checkbox" class="recommend-check"'
            +   ' data-company="' + ((item.recommendedJobCompany || '-').replace(/"/g, '&quot;')) + '"'
            +   ' data-title="' + ((item.recommendedJobTitle || '-').replace(/"/g, '&quot;')) + '"'
            +   ' data-url="' + (item.recommendedJobUrl || '') + '"'
            + '></td>'
            + '<td>' + (idx + 1) + '</td>'
            + '<td>' + (item.recommendedJobCompany || '-') + '</td>' // 추천채용정보 기업명
            + '<td>'
            + (item.recommendedJobUrl // 추천채용정보 URL
                ? '<a href="' + item.recommendedJobUrl + '" target="_blank" rel="noopener noreferrer">'
                + (item.recommendedJobTitle || '-') + '</a>' // 추천채용정보 제목
                : (item.recommendedJobTitle || '-'))
            + '</td>'
            + '<td>' + (item.recommendedJobIndustry || '-') + '</td>' // 추천채용정보 업종
            + '<td>'
            +   '<div class="score-bar-wrap">'
            +       '<div class="score-bar" style="width:' + (item.recommendationScore || 0) + '%"></div>'
            +       '<span class="score-text">' + (item.recommendationScore != null ? item.recommendationScore + '점' : '-') + '</span>'  // 추천채용정보 점수
            +   '</div>'
            + '</td>'
            + '<td>'
            +   createToggleReasonHTML(reason) // 채용정보 추천 사유
            + '</td>'
            + '<td>' + '<a href="' + item.recommendedJobUrl + '" onclick="copyLink(this); return false;">채용공고 링크 복사하기</a></td>' // 추천채용정보 URL 복사
            + '<td><button type="button" class="btn-kakao-share-sm" '
            +   'onclick="shareJobViaKakao('
            +     "'" + (item.recommendedJobCompany || '-').replace(/'/g, "\\'") + "',"
            +     "'" + (item.recommendedJobTitle || '-').replace(/'/g, "\\'") + "',"
            +     "'" + (item.recommendedJobUrl || '') + "'"
            +   ')">'
            +   '<svg viewBox="0 0 24 24" width="14" height="14"><path d="M12 3C6.48 3 2 6.58 2 10.94c0 2.8 1.86 5.27 4.66 6.67-.15.56-.96 3.6-.99 3.83 0 0-.02.17.09.24.11.06.24.01.24.01.32-.04 3.7-2.44 4.28-2.86.55.08 1.13.12 1.72.12 5.52 0 10-3.58 10-7.99C22 6.58 17.52 3 12 3z" fill="#191919"/></svg>'
            +   ' 공유</button></td>'
            + '</tr>';
        tbody.innerHTML += row;
    });
}

function bindBestRecommend(best) {
    if (!best || !best.bestJobInfo) {
        document.getElementById('bestRecommendArea').style.display = 'none';
        document.getElementById('loadingSpinner').disabled = true;
        return;
    }
    document.getElementById('bestRecommendArea').style.display = 'block';
     // 베스트 추천채용정보 사유
    document.getElementById('bestRecommendContent').innerHTML = '<p><strong>기업명:</strong> ' + (best.recommendedJobCompany || '-') + '</p>' // 베스트 추천채용정보 기업명
        + '<p><strong>채용공고:</strong> '
        + (best.recommendedJobUrl // 베스트 추천채용정보 URL
            ? '<a href="' + best.recommendedJobUrl + '" target="_blank">'
            + (best.recommendedJobTitle || '-') + '</a>' // 베스트 추천채용정보 제목
            : (best.recommendedJobTitle || '-'))
        + '</p>'
        + '<p><strong>업종:</strong> ' + (best.recommendedJobIndustry || '-') + '</p>' // 베스트 추천채용정보 업종
        + '<p><strong>추천점수:</strong> ' + (best.recommendationScore != null ? best.recommendationScore + '점' : '-') + '</p>' // 베스트 추천채용정보 점수
        + '<p><strong>추천사유:</strong> ' + (best.recommendationReason || '-') + '</p>';
}

// 복사 기능 navigator.clipboard.writeText() API
function copyLink(element) {
    // href 값 가져오기
    const href = element.getAttribute('href');

    if(href) {
        navigator.clipboard.writeText(href)
            .then(() => alert('채용공고 URL이 복사되었습니다'))
            .catch(err => console.error('복사 실패:', err));
    }
}

// 추천 사유 접기/펼치기 기능
function toggleReason(btn) {
    let cell = btn.parentElement;
    let short = cell.querySelector('.reason-short');
    let full  = cell.querySelector('.reason-full');
    if (full.style.display === 'none') {
        short.style.display = 'none';
        full.style.display  = 'inline';
        btn.innerText = '접기';
    } else {
        short.style.display = 'inline';
        full.style.display  = 'none';
        btn.innerText = '더보기';
    }
}

// 접기/펼치기 HTML 코드 생성 기능
function createToggleReasonHTML(reasonInfo) {
    let reasonShort = reasonInfo.length > 50 ? reasonInfo.substring(0, 50) + '...' : reasonInfo;
    return '<span class="reason-short">' + reasonShort + '</span>'
        +   '<button class="reason-toggle" onclick="toggleReason(this); return false;">더보기</button>'
        +   '<span class="reason-full" style="display:none;">' + reasonInfo + '</span>';
}


// ===========================
// 카카오톡 공유 기능 (Kakao Share JS SDK)
// ===========================

/**
 * 전체선택 체크박스 토글
 */
function toggleAllRecommendCheck(masterCheckbox) {
    let checkboxes = document.querySelectorAll('#recommendListBody .recommend-check');
    checkboxes.forEach(function(cb) {
        cb.checked = masterCheckbox.checked;
    });
}

// 공유 큐 상태
var _kakaoShareQueue = [];
var _kakaoShareIndex = 0;

var KAKAO_SVG = '<svg class="kakao-icon" viewBox="0 0 24 24" width="20" height="20">'
    + '<path d="M12 3C6.48 3 2 6.58 2 10.94c0 2.8 1.86 5.27 4.66 6.67-.15.56-.96 3.6-.99 3.83 0 0-.02.17.09.24.11.06.24.01.24.01.32-.04 3.7-2.44 4.28-2.86.55.08 1.13.12 1.72.12 5.52 0 10-3.58 10-7.99C22 6.58 17.52 3 12 3z" fill="#191919"/>'
    + '</svg>';

/**
 * 상단 버튼 — 체크된 채용정보를 큐에 담고 한 건씩 사용자 클릭으로 순차 공유
 * 브라우저 정책상 사용자 클릭 1회당 팝업 1개만 허용되므로 큐 방식 사용
 */
function shareSelectedViaKakao() {
    // 큐가 남아있으면 다음 건 공유
    if (_kakaoShareQueue.length > 0 && _kakaoShareIndex < _kakaoShareQueue.length) {
        shareCurrentQueueItem();
        return;
    }

    // 첫 클릭: 체크된 항목을 큐에 담기
    let checked = document.querySelectorAll('#recommendListBody .recommend-check:checked');
    if (checked.length === 0) {
        updateKakaoStatus('공유할 채용정보를 선택해주세요.', false);
        return;
    }

    _kakaoShareQueue = [];
    _kakaoShareIndex = 0;
    checked.forEach(function(cb) {
        _kakaoShareQueue.push({
            company: cb.getAttribute('data-company') || '-',
            title: cb.getAttribute('data-title') || '-',
            url: cb.getAttribute('data-url') || 'https://www.work24.go.kr'
        });
    });

    // 첫 번째 건 공유
    shareCurrentQueueItem();
}

/**
 * 큐의 현재 항목을 공유하고 UI 상태 업데이트
 */
function shareCurrentQueueItem() {
    let item = _kakaoShareQueue[_kakaoShareIndex];
    let infoName = document.getElementById('infoName').innerText || '참여자';
    sendKakaoShare(infoName, item.company, item.title, item.url);
    _kakaoShareIndex++;

    let total = _kakaoShareQueue.length;
    let done = _kakaoShareIndex;

    updateShareProgress(done, total);

    if (done < total) {
        // 아직 남은 항목이 있음 — 버튼을 "다음 공유"로 변경
        let next = _kakaoShareQueue[_kakaoShareIndex];
        let btn = document.getElementById('btnKakaoShare');
        btn.innerHTML = KAKAO_SVG + ' 다음 공유 (' + (done + 1) + '/' + total + ')';
        btn.classList.add('btn-kakao-share-active');

        document.getElementById('shareNextInfo').innerText =
            '다음: ' + next.company + ' - ' + next.title;
        updateKakaoStatus(total + '건 중 ' + done + '건 완료', true);
    } else {
        // 모든 항목 공유 완료
        finishKakaoShareQueue();
        updateKakaoStatus(total + '건 공유가 모두 완료되었습니다.', true);
    }
}

/**
 * 공유 진행 상태 프로그레스 바 업데이트
 */
function updateShareProgress(done, total) {
    let progressArea = document.getElementById('kakaoShareProgress');
    let progressFill = document.getElementById('shareProgressFill');
    let progressText = document.getElementById('shareProgressText');
    let cancelBtn = document.getElementById('btnKakaoCancelShare');

    progressArea.style.display = 'block';
    cancelBtn.style.display = (done < total) ? 'inline-flex' : 'none';

    let percent = Math.round((done / total) * 100);
    progressFill.style.width = percent + '%';
    progressText.innerText = '공유 진행: ' + done + ' / ' + total + '건';
}

/**
 * 공유 큐 취소 — 남은 항목 공유를 중단하고 원래 상태로 복원
 */
function cancelKakaoShareQueue() {
    let done = _kakaoShareIndex;
    let total = _kakaoShareQueue.length;
    finishKakaoShareQueue();
    updateKakaoStatus(total + '건 중 ' + done + '건 공유 후 취소되었습니다.', false);
}

/**
 * 공유 큐 종료 — 버튼/프로그레스 원래 상태로 복원
 */
function finishKakaoShareQueue() {
    _kakaoShareQueue = [];
    _kakaoShareIndex = 0;

    let btn = document.getElementById('btnKakaoShare');
    btn.innerHTML = KAKAO_SVG + ' 선택 항목 카카오톡 공유';
    btn.classList.remove('btn-kakao-share-active');

    document.getElementById('btnKakaoCancelShare').style.display = 'none';
    document.getElementById('shareNextInfo').innerText = '';

    // 프로그레스 바 완료 후 잠시 보여주고 숨기기
    setTimeout(function() {
        if (_kakaoShareQueue.length === 0) {
            document.getElementById('kakaoShareProgress').style.display = 'none';
            document.getElementById('shareProgressFill').style.width = '0%';
        }
    }, 3000);
}

/**
 * 각 행 카카오 공유 버튼 — 개별 채용공고 카카오톡 공유
 * bindRecommendList에서 data 속성으로 바인딩한 값 사용
 */
function shareJobViaKakao(company, title, url) {
    let infoName = document.getElementById('infoName').innerText || '참여자';
    sendKakaoShare(infoName, company, title, url || 'https://www.work24.go.kr');
}

/**
 * Kakao.Share.sendCustom 호출 — 카카오톡 메시지 템플릿 공유
 * 템플릿 ID: 124264
 * 템플릿 변수: participantName, company, jobTitle, jobUrl
 * 템플릿 링크: https://www.work24.go.kr/${jobUrl}
 */
function sendKakaoShare(participantName, company, jobTitle, jobUrl) {
    if (typeof Kakao === 'undefined' || !Kakao.isInitialized()) {
        updateKakaoStatus('카카오 SDK가 초기화되지 않았습니다.', false);
        return;
    }

    // 템플릿 링크가 https://www.work24.go.kr/${jobUrl} 형태이므로
    // 전체 URL에서 도메인 부분을 제거하여 경로만 전달
    let jobPath = jobUrl;
    if (jobUrl && jobUrl.startsWith('https://www.work24.go.kr/')) {
        jobPath = jobUrl.replace('https://www.work24.go.kr/', '');
    } else if (jobUrl && jobUrl.startsWith('http://www.work24.go.kr/')) {
        jobPath = jobUrl.replace('http://www.work24.go.kr/', '');
    }

    Kakao.Share.sendCustom({
        templateId: 132481,
        templateArgs: {
            participantName: participantName,
            company: company,
            jobTitle: jobTitle,
            jobUrl: jobPath
        }
    });

    updateKakaoStatus('카카오톡 공유 창이 열렸습니다.', true);
}

/**
 * 카카오 공유 상태 메시지 업데이트
 */
function updateKakaoStatus(message, success) {
    let statusEl = document.getElementById('kakaoShareStatus');
    statusEl.innerText = message;
    statusEl.className = 'kakao-share-status';
    if (success === true) {
        statusEl.classList.add('status-success');
    } else if (success === false) {
        statusEl.classList.add('status-error');
    }
}


