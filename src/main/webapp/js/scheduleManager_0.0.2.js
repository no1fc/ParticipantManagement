/**
 * 관리자 지점 일정 통합 조회 JS
 * @version 0.0.2
 */
$(function () {
    'use strict';

    // ===========================
    // 상수 & 변수
    // ===========================
    let COLOR_PALETTE = [
        '#0d6efd', '#198754', '#fd7e14', '#6f42c1',
        '#20c997', '#17a2b8', '#6610f2', '#ffc107',
        '#795548', '#607d8b', '#0b5394', '#2e7d32'
    ];
    let BADGE_MAP = {
        '대면상담': 'badge-face',
        '전화상담': 'badge-phone',
        '화상상담': 'badge-video',
        '기타': 'badge-etc'
    };

    let counselorList = [];
    let counselorFilter = {};
    let counselorColorMap = {};
    let calendar;

    // ===========================
    // 초기화
    // ===========================
    loadCounselors();
    loadStats();

    // ===========================
    // 상담사 목록 로드 & 칩 렌더링
    // ===========================
    function loadCounselors() {
        $.ajax({
            url: '/api/schedule/counselors',
            type: 'GET',
            success: function (res) {
                if (res.success && res.data) {
                    counselorList = res.data;
                    counselorList.forEach(function (c, idx) {
                        counselorFilter[c.counselorId] = true;
                        counselorColorMap[c.counselorId] = COLOR_PALETTE[idx % COLOR_PALETTE.length];
                    });
                    renderChips();
                    initCalendar();
                }
            }
        });
    }

    function renderChips() {
        let $container = $('#counselorChips');
        $container.empty();

        counselorList.forEach(function (c) {
            let color = counselorColorMap[c.counselorId];
            let $chip = $('<label class="counselor-chip active"></label>');
            $chip.css({ background: color + '20', borderColor: color });
            $chip.html(
                '<input type="checkbox" checked data-id="' + c.counselorId + '">' +
                '<span class="dot" style="background:' + color + '"></span>' +
                c.counselorName
            );

            $chip.find('input').on('change', function () {
                let checked = $(this).prop('checked');
                counselorFilter[c.counselorId] = checked;
                $chip.removeClass('active inactive').addClass(checked ? 'active' : 'inactive');
                calendar.refetchEvents();
            });

            $container.append($chip);
        });
    }

    // 전체 선택/해제
    $('#btnSelectAll').on('click', function () {
        let allChecked = true;
        $.each(counselorFilter, function (key, val) {
            if (!val) { allChecked = false; return false; }
        });

        let newState = !allChecked;
        counselorList.forEach(function (c) {
            counselorFilter[c.counselorId] = newState;
        });

        $('#counselorChips input[type="checkbox"]').each(function () {
            $(this).prop('checked', newState);
            $(this).parent().removeClass('active inactive').addClass(newState ? 'active' : 'inactive');
        });

        calendar.refetchEvents();
    });

    // ===========================
    // 통계 로드
    // ===========================
    function loadStats() {
        $.ajax({
            url: '/api/schedule/stats',
            type: 'GET',
            success: function (res) {
                if (res.success && res.data) {
                    let d = res.data;
                    $('#statCounselorCount').text(d.counselorCount || 0);
                    $('#statWeeklyCount').text(d.weeklyCount || 0);
                    $('#statTodayCount').text(d.todayCount || 0);
                    $('#statMonthlyCount').text(d.monthlyCount || 0);
                }
            }
        });
    }

    // ===========================
    // FullCalendar 초기화
    // ===========================
    function initCalendar() {
        let calendarEl = document.getElementById('calendar');
        calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            locale: 'ko',
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek,timeGridDay'
            },
            navLinks: true,
            dayMaxEvents: 4,
            height: 'auto',
            slotMinTime: '08:00:00',
            slotMaxTime: '20:00:00',
            allDaySlot: false,

            events: function (info, successCallback, failureCallback) {
                $.ajax({
                    url: '/api/schedule/branch-list',
                    type: 'GET',
                    data: {
                        searchStartDate: info.startStr.substring(0, 10),
                        searchEndDate: info.endStr.substring(0, 10)
                    },
                    success: function (res) {
                        if (!res.success) {
                            failureCallback();
                            return;
                        }
                        let events = [];
                        res.data.forEach(function (item) {
                            // 필터 적용
                            if (!counselorFilter[item.counselorId]) return;

                            let color = counselorColorMap[item.counselorId] || '#6c757d';
                            events.push({
                                id: item.scheduleId,
                                title: (item.participantName || '(미정)') + ' - ' + item.scheduleType,
                                start: item.scheduleDate + 'T' + item.startTime,
                                end: item.endTime ? item.scheduleDate + 'T' + item.endTime : null,
                                color: color,
                                extendedProps: {
                                    scheduleId: item.scheduleId,
                                    counselorId: item.counselorId,
                                    counselorName: item.counselorName,
                                    participantName: item.participantName,
                                    scheduleType: item.scheduleType,
                                    memo: item.memo,
                                    scheduleDate: item.scheduleDate,
                                    startTime: item.startTime,
                                    endTime: item.endTime
                                }
                            });
                        });
                        successCallback(events);
                    },
                    error: failureCallback
                });
            },

            eventClick: function (info) {
                showDetailPopup(info.event, info.jsEvent);
            }
        });

        calendar.render();
        window._calendar = calendar;
    }

    // 뷰 전환
    $('#viewSelect').on('change', function () {
        if (calendar) {
            calendar.changeView($(this).val());
        }
    });

    // ===========================
    // 상세 팝업
    // ===========================
    let $popup = $('#detailPopup');

    function showDetailPopup(event, jsEvent) {
        let props = event.extendedProps;
        $('#popupTitle').text(props.participantName || '(미정)');
        $('#popupCounselor').text(props.counselorName || '-');
        $('#popupDate').text(props.scheduleDate || '-');

        let timeText = props.startTime || '';
        if (props.endTime) {
            timeText += ' ~ ' + props.endTime;
        }
        $('#popupTime').text(timeText);
        $('#popupParticipant').text(props.participantName || '(미정)');

        let badgeClass = BADGE_MAP[props.scheduleType] || 'badge-etc';
        $('#popupType').html('<span class="badge-schedule ' + badgeClass + '">' + (props.scheduleType || '-') + '</span>');
        $('#popupMemo').text(props.memo || '-');

        // 팝업 위치
        let x = jsEvent.clientX + 10;
        let y = jsEvent.clientY + 10;
        if (x + 330 > window.innerWidth) x = window.innerWidth - 340;
        if (y + 250 > window.innerHeight) y = window.innerHeight - 260;
        $popup.css({ left: x + 'px', top: y + 'px' });
        $popup.addClass('show');
    }

    $('#closePopup').on('click', function () {
        $popup.removeClass('show');
    });

    $(document).on('click', function (e) {
        if (!$(e.target).closest('.detail-popup').length && !$(e.target).closest('.fc-event').length) {
            $popup.removeClass('show');
        }
    });
});
