/**
 * 공개 일정 조회 (메인 데스크용) JS
 * @version 0.0.1
 */
$(function () {
    'use strict';

    // ===========================
    // 상수 & 변수
    // ===========================
    var COLOR_PALETTE = [
        '#0d6efd', '#198754', '#fd7e14', '#6f42c1',
        '#20c997', '#17a2b8', '#6610f2', '#ffc107',
        '#795548', '#607d8b', '#0b5394', '#2e7d32'
    ];
    var BADGE_MAP = {
        '대면상담': 'badge-face',
        '전화상담': 'badge-phone',
        '화상상담': 'badge-video',
        '기타': 'badge-etc'
    };

    var currentBranch = null;
    var counselorList = [];
    var counselorColorMap = {};
    var calendar = null;

    // 일정유형 필터 (기본: 대면상담만 활성)
    var typeFilter = {
        '대면상담': true,
        '전화상담': false,
        '화상상담': false,
        '기타': false
    };

    // ===========================
    // 일정유형 필터 칩 클릭
    // ===========================
    $('#typeFilterBar').on('click', '.type-chip', function () {
        var type = $(this).data('type');
        typeFilter[type] = !typeFilter[type];
        $(this).toggleClass('active', typeFilter[type]).toggleClass('inactive', !typeFilter[type]);
        if (calendar) {
            calendar.refetchEvents();
        }
    });

    // ===========================
    // 인증
    // ===========================
    $('#btnVerify').on('click', function () {
        var branch = $('#branchSelect').val();
        var code = $('#accessCode').val().trim();

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
                var msg = '접근코드가 올바르지 않습니다.';
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
    });

    // ===========================
    // 상담사 로드 + 일정 표시
    // ===========================
    function loadCounselorsAndShow() {
        $.ajax({
            url: '/schedulePublic/api/counselors',
            type: 'GET',
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
        var legendHtml = '<strong style="font-size:0.85rem;">상담사:</strong>';
        counselorList.forEach(function (c) {
            var color = counselorColorMap[c.counselorId];
            legendHtml += '<div class="legend-item"><div class="legend-dot" style="background:' + color + '"></div>' + c.counselorName + '</div>';
        });
        $('#legendSection').html(legendHtml);

        // 캘린더 초기화
        initCalendar();
    }

    // ===========================
    // FullCalendar 초기화
    // ===========================
    function initCalendar() {
        var calendarEl = document.getElementById('calendar');
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
                    data: {
                        searchStartDate: info.startStr.substring(0, 10),
                        searchEndDate: info.endStr.substring(0, 10)
                    },
                    success: function (res) {
                        if (!res.success) {
                            failureCallback();
                            return;
                        }
                        var events = [];
                        res.data.forEach(function (item) {
                            // 일정유형 필터 적용
                            if (!typeFilter[item.scheduleType]) return;

                            var color = counselorColorMap[item.counselorId] || '#6c757d';
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
    function loadDayDetail(dateStr) {
        $.ajax({
            url: '/schedulePublic/api/day-detail',
            type: 'GET',
            data: { date: dateStr },
            success: function (res) {
                if (res.success) {
                    renderDayDetail(dateStr, res.data);
                }
            }
        });
    }

    function renderDayDetail(dateStr, schedules) {
        var $panel = $('#dayDetailPanel');
        var dateObj = new Date(dateStr + 'T00:00:00');
        var dateText = dateObj.toLocaleDateString('ko-KR', {
            year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
        });
        $('#dayDetailTitle').text(dateText + ' 시간별 일정');

        // 시간 범위 (08:00 ~ 20:00)
        var hours = [];
        for (var h = 8; h <= 20; h++) hours.push(h);

        // 테이블 헤더
        var headHtml = '<th>시간</th>';
        counselorList.forEach(function (c) {
            var color = counselorColorMap[c.counselorId];
            headHtml += '<th style="color:' + color + '; border-bottom: 3px solid ' + color + ';">' + c.counselorName + '</th>';
        });
        $('#timeTableHead').html(headHtml);

        // 테이블 본문
        var now = new Date();
        var todayStr = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0') + '-' + String(now.getDate()).padStart(2, '0');
        var currentHour = now.getHours();
        var isToday = (dateStr === todayStr);

        var bodyHtml = '';
        hours.forEach(function (h) {
            var hStr = String(h).padStart(2, '0');
            var rowClass = (isToday && h === currentHour) ? ' class="current-hour-row"' : '';
            bodyHtml += '<tr' + rowClass + '><td class="time-col">' + hStr + ':00</td>';

            counselorList.forEach(function (c) {
                var match = null;
                for (var i = 0; i < schedules.length; i++) {
                    var s = schedules[i];
                    if (s.counselorId === c.counselorId && s.startTime && s.startTime.substring(0, 2) === hStr
                        && typeFilter[s.scheduleType]) {
                        match = s;
                        break;
                    }
                }

                if (match) {
                    var badgeClass = BADGE_MAP[match.scheduleType] || 'badge-etc';
                    bodyHtml += '<td class="has-schedule">' +
                        '<div class="schedule-cell">' +
                        '<span class="p-name">' + (match.participantName || '(미정)') + '</span>' +
                        '<span class="badge-schedule ' + badgeClass + '">' + (match.scheduleType || '') + '</span>' +
                        '</div></td>';
                } else {
                    bodyHtml += '<td>-</td>';
                }
            });

            bodyHtml += '</tr>';
        });
        $('#timeTableBody').html(bodyHtml);

        $panel.addClass('show');
        $('body').css('overflow', 'hidden');

        // 09:00 행이 보이도록 스크롤 (08:00은 위로 스크롤해서 확인)
        setTimeout(function () {
            var $scrollArea = $panel.find('.table-scroll');
            var $row09 = $panel.find('td.time-col').filter(function () {
                return $(this).text() === '09:00';
            }).closest('tr');
            if ($row09.length && $scrollArea.length) {
                $scrollArea.scrollTop($row09[0].offsetTop - $scrollArea.find('thead').outerHeight());
            }
        }, 50);
    }

    // 시간표 닫기
    $('#closeDayDetail').on('click', function () {
        $('#dayDetailPanel').removeClass('show');
        $('body').css('overflow', '');
    });
});
