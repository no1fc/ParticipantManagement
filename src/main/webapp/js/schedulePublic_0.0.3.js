/**
 * 공개 일정 조회 (메인 데스크용) JS
 * @version 0.0.3
 */
$(function () {
    'use strict';

    // ===========================
    // 상수 & 변수
    // ===========================
    const COLOR_PALETTE = [
        '#0d6efd', '#198754', '#fd7e14', '#6f42c1',
        '#20c997', '#17a2b8', '#6610f2', '#ffc107',
        '#795548', '#607d8b', '#0b5394', '#2e7d32'
    ];
    const BADGE_MAP = {
        '대면상담': 'badge-face',
        '전화상담': 'badge-phone',
        '화상상담': 'badge-video',
        '기타': 'badge-etc'
    };

    let currentBranch = null;
    let counselorList = [];
    let counselorColorMap = {};
    let calendar = null;
    let hourlyRefreshTimeoutId = null;
    let intervalRefreshId = null;
    let displayIntervalMinutes = 60; // 기본: 1시간 간격

    // 일정유형 필터 (기본: 대면상담만 활성)
    let typeFilter = {
        '대면상담': true,
        '전화상담': false,
        '화상상담': false,
        '기타': false
    };

    // ===========================
    // 일정유형 필터 칩 클릭
    // ===========================
    $('#typeFilterBar').on('click', '.type-chip', function () {
        let type = $(this).data('type');
        typeFilter[type] = !typeFilter[type];
        $(this).toggleClass('active', typeFilter[type]).toggleClass('inactive', !typeFilter[type]);
        if (calendar) {
            calendar.refetchEvents();
        }
        // 시간표가 열려있으면 재렌더링
        reRenderDayDetailIfOpen();
    });

    // ===========================
    // 인증
    // ===========================
    $('#btnVerify').on('click', function () {
        let branch = $('#branchSelect').val();
        let code = $('#accessCode').val().trim();

        if (!branch || !code) {
            showAuthError('지점과 접근코드를 모두 입력해주세요.');
            return;
        }

        $.ajax({
            url: '/schedulePublic/api/verify',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ uniqueNumber: code, branch: branch }),
            success: function (res) {
                if (res.success) {
                    $('#authError').hide();
                    currentBranch = branch;
                    loadCounselorsAndShow();
                }
            },
            error: function (xhr) {
                let msg = '접근코드가 올바르지 않습니다.';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    msg = xhr.responseJSON.message;
                }
                showAuthError(msg);
            }
        });
    });

    // Enter 키로 인증
    $('#accessCode').on('keypress', function (e) {
        if (e.key === 'Enter') {
            $('#btnVerify').click();
        }
    });

    function showAuthError(message) {
        $('#authError').html('<i class="bi bi-exclamation-circle me-1"></i>' + message).show();
    }

    // 다른 지점 조회
    $('#btnBack').on('click', function () {
        $('#authSection').show();
        $('#scheduleView').removeClass('show');
        $('#headerBranchInfo').text('');
        if (calendar) {
            calendar.destroy();
            calendar = null;
        }
        stopHourlyRefresh();
        stopIntervalRefresh();
    });

    // ===========================
    // 상담사 로드 + 일정 표시
    // ===========================
    function loadCounselorsAndShow() {
        $.ajax({
            url: '/schedulePublic/api/counselors',
            type: 'GET',
            cache: false,
            success: function (res) {
                if (res.success && res.data) {
                    counselorList = res.data;
                    counselorColorMap = {};
                    counselorList.forEach(function (c, idx) {
                        counselorColorMap[c.counselorId] = COLOR_PALETTE[idx % COLOR_PALETTE.length];
                    });
                    showScheduleView();
                }
            }
        });
    }

    function showScheduleView() {
        $('#authSection').hide();
        $('#scheduleView').addClass('show');
        $('#branchName').text(currentBranch + '지점');
        $('#headerBranchInfo').text(currentBranch + '지점 일정 조회 중');

        // 상담사 범례
        let legendHtml = '<strong style="font-size:0.85rem;">상담사:</strong>';
        counselorList.forEach(function (c) {
            let color = counselorColorMap[c.counselorId];
            legendHtml += '<div class="legend-item"><div class="legend-dot" style="background:' + color + '"></div>' + c.counselorName + '</div>';
        });
        $('#legendSection').html(legendHtml);

        // 캘린더 초기화
        initCalendar();

        // 간격별 자동 새로고침 시작
        startIntervalRefresh();

        // 정각 자동 새로고침 시작
        scheduleHourlyRefresh();
    }

    // ===========================
    // FullCalendar 초기화
    // ===========================
    function initCalendar() {
        let calendarEl = document.getElementById('calendar');
        calendarEl.innerHTML = '';

        calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            locale: 'ko',
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth'
            },
            navLinks: false,
            editable: false,
            selectable: false,
            dayMaxEvents: 3,
            height: 'auto',

            events: function (info, successCallback, failureCallback) {
                $.ajax({
                    url: '/schedulePublic/api/schedule-list',
                    type: 'GET',
                    cache: false,
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
                            // 일정유형 필터 적용
                            if (!typeFilter[item.scheduleType]) return;

                            let color = counselorColorMap[item.counselorId] || '#6c757d';
                            events.push({
                                id: item.scheduleId,
                                title: (item.participantName || '(미정)') + ' - ' + item.scheduleType,
                                start: item.scheduleDate + 'T' + item.startTime,
                                end: item.endTime ? item.scheduleDate + 'T' + item.endTime : null,
                                color: color,
                                extendedProps: {
                                    counselorId: item.counselorId,
                                    counselorName: item.counselorName,
                                    participantName: item.participantName,
                                    scheduleType: item.scheduleType,
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

            dateClick: function (info) {
                loadDayDetail(info.dateStr);
            }
        });

        calendar.render();
    }

    // ===========================
    // 날짜별 시간표
    // ===========================
    let cachedSchedules = []; // 현재 시간표 데이터 캐시 (간격 변경 시 재활용)

    function loadDayDetail(dateStr) {
        $.ajax({
            url: '/schedulePublic/api/day-detail',
            type: 'GET',
            cache: false,
            data: { date: dateStr },
            success: function (res) {
                if (res.success) {
                    cachedSchedules = res.data;
                    renderDayDetail(dateStr, cachedSchedules);
                }
            }
        });
    }

    /**
     * 시간 슬롯 배열 생성
     * @param {number} intervalMin - 간격(분): 10, 30, 60
     * @returns {Array<{hour: number, minute: number, label: string}>}
     */
    function buildTimeSlots(intervalMin) {
        let slots = [];
        for (let totalMin = 8 * 60; totalMin <= 20 * 60; totalMin += intervalMin) {
            let h = Math.floor(totalMin / 60);
            let m = totalMin % 60;
            let label = String(h).padStart(2, '0') + ':' + String(m).padStart(2, '0');
            slots.push({ hour: h, minute: m, label: label });
        }
        return slots;
    }

    /**
     * 일정이 해당 시간 슬롯에 매칭되는지 확인
     * - startTime(HH:MM)이 슬롯 시작 ~ 슬롯 끝 범위 내인지 체크
     */
    function matchScheduleToSlot(schedule, slot, intervalMin) {
        if (!schedule.startTime) return false;
        let parts = schedule.startTime.split(':');
        let schedMin = parseInt(parts[0], 10) * 60 + parseInt(parts[1], 10);
        let slotStart = slot.hour * 60 + slot.minute;
        let slotEnd = slotStart + intervalMin;
        return schedMin >= slotStart && schedMin < slotEnd;
    }

    function renderDayDetail(dateStr, schedules) {
        let $panel = $('#dayDetailPanel');
        $panel.data('currentDate', dateStr);

        let dateObj = new Date(dateStr + 'T00:00:00');
        let dateText = dateObj.toLocaleDateString('ko-KR', {
            year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
        });
        $('#dayDetailTitle').text(dateText + ' 시간별 일정');

        // 시간 슬롯 생성 (선택된 간격 기준)
        let slots = buildTimeSlots(displayIntervalMinutes);

        // 테이블 헤더
        let headHtml = '<th>시간</th>';
        counselorList.forEach(function (c) {
            let color = counselorColorMap[c.counselorId];
            headHtml += '<th style="color:' + color + '; border-bottom: 3px solid ' + color + ';">' + c.counselorName + '</th>';
        });
        $('#timeTableHead').html(headHtml);

        // 현재 시간 정보
        let now = new Date();
        let todayStr = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0') + '-' + String(now.getDate()).padStart(2, '0');
        let currentTotalMin = now.getHours() * 60 + now.getMinutes();
        let isToday = (dateStr === todayStr);

        // 테이블 본문
        let bodyHtml = '';
        slots.forEach(function (slot) {
            let slotStart = slot.hour * 60 + slot.minute;
            let slotEnd = slotStart + displayIntervalMinutes;
            // 현재 시간이 이 슬롯 범위 안에 있으면 강조
            let isCurrentSlot = isToday && currentTotalMin >= slotStart && currentTotalMin < slotEnd;
            let rowClass = isCurrentSlot ? ' class="current-hour-row"' : '';

            bodyHtml += '<tr' + rowClass + '><td class="time-col">' + slot.label + '</td>';

            counselorList.forEach(function (c) {
                // 해당 슬롯에 매칭되는 일정들을 모두 찾기
                let matches = [];
                for (let i = 0; i < schedules.length; i++) {
                    let s = schedules[i];
                    if (s.counselorId === c.counselorId
                        && matchScheduleToSlot(s, slot, displayIntervalMinutes)
                        && typeFilter[s.scheduleType]) {
                        matches.push(s);
                    }
                }

                if (matches.length > 0) {
                    let cellHtml = '<td class="has-schedule"><div class="schedule-cell">';
                    matches.forEach(function (match) {
                        let badgeClass = BADGE_MAP[match.scheduleType] || 'badge-etc';
                        cellHtml += '<span class="p-name">' + (match.participantName || '(미정)') + '</span>';
                        cellHtml += '<span class="badge-schedule ' + badgeClass + '">' + (match.scheduleType || '') + '</span>';
                    });
                    cellHtml += '</div></td>';
                    bodyHtml += cellHtml;
                } else {
                    bodyHtml += '<td>-</td>';
                }
            });

            bodyHtml += '</tr>';
        });
        $('#timeTableBody').html(bodyHtml);

        $panel.addClass('show');
        $('body').css('overflow', 'hidden');

        // 09:00 행이 보이도록 스크롤
        setTimeout(function () {
            let $scrollArea = $panel.find('.table-scroll');
            let $row09 = $panel.find('td.time-col').filter(function () {
                return $(this).text() === '09:00';
            }).closest('tr');
            if ($row09.length && $scrollArea.length) {
                $scrollArea.scrollTop($row09[0].offsetTop - $scrollArea.find('thead').outerHeight());
            }
        }, 50);
    }

    // 시간표가 열려있으면 캐시된 데이터로 재렌더링
    function reRenderDayDetailIfOpen() {
        let $panel = $('#dayDetailPanel');
        if ($panel.hasClass('show')) {
            let dateStr = $panel.data('currentDate');
            if (dateStr && cachedSchedules.length >= 0) {
                renderDayDetail(dateStr, cachedSchedules);
            }
        }
    }

    // ===========================
    // 새로고침 기능
    // ===========================
    function refreshData() {
        if (calendar) {
            calendar.refetchEvents();
        }
        // 시간표가 열려있으면 서버에서 재조회
        let $panel = $('#dayDetailPanel');
        if ($panel.hasClass('show')) {
            let dateStr = $panel.data('currentDate');
            if (dateStr) {
                loadDayDetail(dateStr);
            }
        }
    }

    // ===========================
    // 정각 자동 새로고침 (매시 정각: 12:00, 13:00, ...)
    // ===========================
    function scheduleHourlyRefresh() {
        stopHourlyRefresh();

        let now = new Date();
        // 다음 정각까지 남은 밀리초 계산
        let nextHour = new Date(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours() + 1, 0, 0, 0);
        let msUntilNextHour = nextHour.getTime() - now.getTime();

        hourlyRefreshTimeoutId = setTimeout(function () {
            refreshData();
            scheduleHourlyRefresh();
        }, msUntilNextHour);
    }

    function stopHourlyRefresh() {
        if (hourlyRefreshTimeoutId) {
            clearTimeout(hourlyRefreshTimeoutId);
            hourlyRefreshTimeoutId = null;
        }
    }

    // ===========================
    // 간격별 자동 새로고침 (10분/30분/1시간)
    // ===========================
    function startIntervalRefresh() {
        stopIntervalRefresh();

        let now = new Date();
        let intervalMs = displayIntervalMinutes * 60 * 1000;
        // 자정 기준 경과 밀리초로 다음 간격 경계까지 남은 시간 계산
        let msSinceMidnight = now.getTime() - new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime();
        let msUntilNext = intervalMs - (msSinceMidnight % intervalMs);

        intervalRefreshId = setTimeout(function () {
            refreshData();
            startIntervalRefresh();
        }, msUntilNext);
    }

    function stopIntervalRefresh() {
        if (intervalRefreshId) {
            clearTimeout(intervalRefreshId);
            intervalRefreshId = null;
        }
    }

    // 수동 새로고침 버튼
    $('#refreshDayDetail').on('click', function () {
        refreshData();
    });

    // 시간표 간격 변경 (10분 / 30분 / 1시간) → 표시 + 새로고침 간격 모두 변경
    $('#displayIntervalSelect').on('change', function () {
        displayIntervalMinutes = parseInt($(this).val(), 10);
        reRenderDayDetailIfOpen();
        startIntervalRefresh();
    });

    // 시간표 닫기
    $('#closeDayDetail').on('click', function () {
        $('#dayDetailPanel').removeClass('show');
        $('body').css('overflow', '');
    });

});