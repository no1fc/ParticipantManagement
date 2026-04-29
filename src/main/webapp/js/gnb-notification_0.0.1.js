// ===========================
// GNB 알림 관리 (WebSocket + 드롭다운 + 토스트)
// ===========================
(function() {
    'use strict';

    var MAX_NOTIFICATIONS = 10;
    var RECONNECT_DELAY = 5000;
    var TOAST_DURATION = 5000;

    function escapeHtml(str) {
        var div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    var _stompClient = null;
    var _wsConnected = false;
    var _notifications = [];

    function getStorageKey() {
        return 'gnbNotifications_' + (typeof JOBMOA_USER_ID !== 'undefined' ? JOBMOA_USER_ID : '');
    }

    // ===========================
    // WebSocket 연결
    // ===========================
    function connectGnbWebSocket() {
        if (_wsConnected) return;
        if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') return;
        if (typeof JOBMOA_USER_ID === 'undefined' || !JOBMOA_USER_ID) return;

        var socket = new SockJS('/ws-notification');
        _stompClient = Stomp.over(socket);
        _stompClient.debug = null;

        _stompClient.connect({}, function() {
            _wsConnected = true;
            // AI 추천 완료 알림
            _stompClient.subscribe(
                '/topic/recommend-complete/' + JOBMOA_USER_ID,
                function(msg) {
                    var notification = JSON.parse(msg.body);
                    handleGnbNotification(notification);
                }
            );
            // 이력서 요청 알림
            _stompClient.subscribe(
                '/topic/resume-request/' + JOBMOA_USER_ID,
                function(msg) {
                    var notification = JSON.parse(msg.body);
                    notification._notifType = 'resume-request';
                    handleGnbNotification(notification);
                }
            );
        }, function() {
            _wsConnected = false;
            _stompClient = null;
            setTimeout(connectGnbWebSocket, RECONNECT_DELAY);
        });
    }

    // ===========================
    // 알림 수신 처리
    // ===========================
    function handleGnbNotification(notification) {
        // 알림 데이터 구성
        var notifType;
        if (notification._notifType === 'resume-request') {
            notifType = 'info';
        } else {
            notifType = notification.reused ? 'warning' : (notification.success ? 'success' : 'error');
        }

        var item = {
            id: Date.now().toString(),
            type: notifType,
            jobSeekerNo: notification.jobSeekerNo,
            participantName: notification.participantName || '',
            message: buildNotificationMessage(notification),
            timestamp: new Date().toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit', second: '2-digit' }),
            read: false,
            activeRecommendCount: notification.activeRecommendCount || 0
        };

        // 목록 앞에 추가 (최대 10건)
        _notifications.unshift(item);
        if (_notifications.length > MAX_NOTIFICATIONS) {
            _notifications = _notifications.slice(0, MAX_NOTIFICATIONS);
        }

        saveNotificationsToStorage();
        renderNotificationItem(item, true);
        updateNotificationBadge();
        triggerBellAnimation();
        showGnbToast(item.message, item.type);

        // 커스텀 이벤트 발행 (recommend-modal에서 수신)
        document.dispatchEvent(new CustomEvent('recommendComplete', { detail: notification }));
    }

    function buildNotificationMessage(notification) {
        // 이력서 요청 알림
        if (notification._notifType === 'resume-request') {
            return notification.message || '이력서 요청이 접수되었습니다.';
        }

        // AI 추천 완료 알림
        var name = notification.participantName || '';
        var no = notification.jobSeekerNo || '';
        var prefix = name ? (name + '(' + no + ')') : ('구직번호 ' + no);

        if (notification.reused) {
            return prefix + ' - 24시간 이내에 이미 추천이 완료되었습니다.';
        }
        if (notification.success) {
            var count = notification.savedCount || 0;
            return count > 0
                ? prefix + ' - ' + count + '개의 채용정보가 추천 저장되었습니다.'
                : prefix + ' - 조건에 맞는 채용정보를 찾지 못했습니다.';
        }
        return prefix + ' - 추천 처리 중 오류가 발생했습니다.';
    }

    // ===========================
    // sessionStorage 관리
    // ===========================
    function saveNotificationsToStorage() {
        try {
            sessionStorage.setItem(getStorageKey(), JSON.stringify(_notifications));
        } catch (e) { /* storage full - ignore */ }
    }

    function loadNotificationsFromStorage() {
        try {
            var data = sessionStorage.getItem(getStorageKey());
            if (data) {
                _notifications = JSON.parse(data);
            }
        } catch (e) {
            _notifications = [];
        }
    }

    // ===========================
    // UI 렌더링
    // ===========================
    function renderAll() {
        var list = document.getElementById('gnbNotificationList');
        if (!list) return;

        list.innerHTML = '';

        if (_notifications.length === 0) {
            list.innerHTML = '<div class="gnb-notification-empty">알림이 없습니다</div>';
            return;
        }

        for (var i = 0; i < _notifications.length; i++) {
            renderNotificationItem(_notifications[i], false);
        }
    }

    function renderNotificationItem(item, prepend) {
        var list = document.getElementById('gnbNotificationList');
        if (!list) return;

        // 빈 메시지 제거
        var empty = list.querySelector('.gnb-notification-empty');
        if (empty) empty.remove();

        var iconMap = {
            success: '<i class="bi bi-check-circle-fill text-success"></i>',
            error: '<i class="bi bi-x-circle-fill text-danger"></i>',
            warning: '<i class="bi bi-exclamation-triangle-fill text-warning"></i>',
            info: '<i class="bi bi-envelope-fill text-info"></i>'
        };

        var el = document.createElement('a');
        el.href = '#';
        el.className = 'gnb-notification-item' + (item.read ? '' : ' unread');
        el.setAttribute('data-notification-id', item.id);
        el.setAttribute('data-jobseeker-no', item.jobSeekerNo || '');
        el.onclick = function(e) {
            e.preventDefault();
            handleNotificationClick(this);
        };

        var nameLabel = item.participantName
            ? escapeHtml(item.participantName) + '(' + escapeHtml(String(item.jobSeekerNo)) + ')'
            : '구직번호 ' + escapeHtml(String(item.jobSeekerNo));

        el.innerHTML =
            '<div class="d-flex align-items-start">' +
                '<span class="gnb-notification-icon me-2">' + (iconMap[item.type] || '') + '</span>' +
                '<div class="gnb-notification-content">' +
                    '<p class="gnb-notification-name mb-0">' + nameLabel + '</p>' +
                    '<p class="gnb-notification-text mb-0">' + escapeHtml(item.message) + '</p>' +
                    '<span class="gnb-notification-time">' + item.timestamp + '</span>' +
                '</div>' +
            '</div>';

        if (prepend) {
            list.insertBefore(el, list.firstChild);
            // 최대 건수 유지
            while (list.children.length > MAX_NOTIFICATIONS) {
                list.removeChild(list.lastChild);
            }
        } else {
            list.appendChild(el);
        }
    }

    function updateNotificationBadge() {
        var badge = document.getElementById('gnbNotifBadge');
        if (!badge) return;

        var unreadCount = 0;
        for (var i = 0; i < _notifications.length; i++) {
            if (!_notifications[i].read) unreadCount++;
        }

        if (unreadCount > 0) {
            badge.textContent = unreadCount > 99 ? '99+' : unreadCount;
            badge.style.display = '';
        } else {
            badge.style.display = 'none';
        }

        // 벨 아이콘 변경 (알림 있으면 채워진 벨)
        var bellIcon = document.getElementById('gnbBellIcon');
        if (bellIcon) {
            bellIcon.className = unreadCount > 0 ? 'bi bi-bell-fill' : 'bi bi-bell';
        }
    }

    function triggerBellAnimation() {
        var bellIcon = document.getElementById('gnbBellIcon');
        if (!bellIcon) return;

        bellIcon.classList.add('gnb-bell-ringing');
        setTimeout(function() {
            bellIcon.classList.remove('gnb-bell-ringing');
        }, 600);
    }

    // ===========================
    // 알림 클릭 처리
    // ===========================
    function handleNotificationClick(element) {
        var notifId = element.getAttribute('data-notification-id');

        // 읽음 처리
        for (var i = 0; i < _notifications.length; i++) {
            if (_notifications[i].id === notifId) {
                _notifications[i].read = true;
                break;
            }
        }
        element.classList.remove('unread');
        saveNotificationsToStorage();
        updateNotificationBadge();
    }

    // ===========================
    // 모든 알림 지우기
    // ===========================
    function clearAllNotifications() {
        _notifications = [];
        saveNotificationsToStorage();
        renderAll();
        updateNotificationBadge();
    }

    // ===========================
    // 토스트 알림
    // ===========================
    function showGnbToast(message, type) {
        var container = document.getElementById('gnbToastContainer');
        if (!container) {
            container = document.createElement('div');
            container.id = 'gnbToastContainer';
            container.className = 'gnb-toast-container';
            document.body.appendChild(container);
        }

        var iconMap = {
            success: '<i class="bi bi-check-circle-fill text-success"></i>',
            error: '<i class="bi bi-x-circle-fill text-danger"></i>',
            warning: '<i class="bi bi-exclamation-triangle-fill text-warning"></i>',
            info: '<i class="bi bi-envelope-fill text-info"></i>'
        };

        var toast = document.createElement('div');
        toast.className = 'gnb-toast gnb-toast-' + type;
        toast.innerHTML =
            '<div class="d-flex align-items-center">' +
                '<span class="me-2">' + (iconMap[type] || '') + '</span>' +
                '<span class="gnb-toast-message">' + escapeHtml(message) + '</span>' +
                '<button class="gnb-toast-close ms-2" onclick="this.closest(\'.gnb-toast\').remove()">&times;</button>' +
            '</div>';

        container.appendChild(toast);

        setTimeout(function() {
            if (toast.parentElement) {
                toast.classList.add('gnb-toast-fade-out');
                setTimeout(function() { toast.remove(); }, 300);
            }
        }, TOAST_DURATION);
    }

    // ===========================
    // 드롭다운 열릴 때 읽음 처리
    // ===========================
    function setupDropdownEvents() {
        var dropdownEl = document.getElementById('gnbNotificationArea');
        if (!dropdownEl) return;

        dropdownEl.addEventListener('shown.bs.dropdown', function() {
            // 드롭다운 열면 모든 알림 읽음 처리
            var changed = false;
            for (var i = 0; i < _notifications.length; i++) {
                if (!_notifications[i].read) {
                    _notifications[i].read = true;
                    changed = true;
                }
            }
            if (changed) {
                saveNotificationsToStorage();
                updateNotificationBadge();
                // unread 클래스 제거
                var items = document.querySelectorAll('.gnb-notification-item.unread');
                for (var j = 0; j < items.length; j++) {
                    items[j].classList.remove('unread');
                }
            }
        });
    }

    // ===========================
    // 초기화
    // ===========================
    function init() {
        loadNotificationsFromStorage();
        renderAll();
        updateNotificationBadge();
        setupDropdownEvents();
        connectGnbWebSocket();

        // "모든 알림 지우기" 버튼 이벤트
        var clearBtn = document.getElementById('gnbNotifClearAll');
        if (clearBtn) {
            clearBtn.addEventListener('click', function(e) {
                e.preventDefault();
                clearAllNotifications();
            });
        }
    }

    // DOM 로드 완료 후 초기화
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    // 전역 함수 노출 (gnb.tag HTML에서 직접 호출 필요한 경우)
    window.clearAllGnbNotifications = clearAllNotifications;

})();
