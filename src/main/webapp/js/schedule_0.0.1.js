/**
 * schedule_0.0.1.js - 상담 일정 관리
 * FullCalendar 6.1.11 + jQuery + SweetAlert2
 */
$(document).ready(function () {

    // =============================================
    // 1. 상수 및 색상/배지 맵
    // =============================================
    var COLOR_MAP = {
        '대면상담': '#007bff',
        '전화상담': '#28a745',
        '화상상담': '#ffc107',
        '기타': '#6c757d'
    };

    var BADGE_CLASS_MAP = {
        '대면상담': 'badge-face',
        '전화상담': 'badge-phone',
        '화상상담': 'badge-video',
        '기타': 'badge-etc'
    };

    var searchTimer = null;

    // =============================================
    // 2. FullCalendar 초기화
    // =============================================
    var calendarEl = document.getElementById('calendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        navLinks: true,
        editable: true,
        selectable: true,
        dayMaxEvents: 3,
        height: 'auto',

        events: function (info, successCallback, failureCallback) {
            $.ajax({
                url: '/api/schedule/list',
                type: 'GET',
                data: {
                    searchStartDate: info.startStr.substring(0, 10),
                    searchEndDate: info.endStr.substring(0, 10)
                },
                success: function (res) {
                    if (res.success && res.data) {
                        var events = res.data.map(function (item) {
                            return {
                                id: item.scheduleId,
                                title: (item.participantName || '(미정)') + ' - ' + item.scheduleType,
                                start: item.scheduleDate + 'T' + item.startTime,
                                end: item.endTime ? item.scheduleDate + 'T' + item.endTime : null,
                                color: COLOR_MAP[item.scheduleType] || COLOR_MAP['기타'],
                                extendedProps: {
                                    scheduleId: item.scheduleId,
                                    participantJobNo: item.participantJobNo,
                                    counselorId: item.counselorId,
                                    scheduleDate: item.scheduleDate,
                                    startTime: item.startTime,
                                    endTime: item.endTime,
                                    scheduleType: item.scheduleType,
                                    memo: item.memo,
                                    alertMinutes: item.alertMinutes,
                                    participantName: item.participantName,
                                    participantStage: item.participantStage
                                }
                            };
                        });
                        successCallback(events);
                    } else {
                        successCallback([]);
                    }
                },
                error: function () {
                    failureCallback();
                }
            });
        },

        dateClick: function (info) {
            openRegisterModal(info.dateStr);
        },

        eventClick: function (info) {
            openDetailModal(info.event.extendedProps);
        },

        eventDrop: function (info) {
            handleDrag(info);
        },

        eventResize: function (info) {
            handleDrag(info);
        }
    });

    calendar.render();

    // =============================================
    // 3. 등록 버튼 클릭
    // =============================================
    $('#btnRegister').on('click', function () {
        var today = new Date().toISOString().substring(0, 10);
        openRegisterModal(today);
    });

    // =============================================
    // 4. 등록 모달 열기
    // =============================================
    function openRegisterModal(dateStr) {
        $('#modalTitle').html('<i class="bi bi-calendar-plus me-2"></i>상담 일정 등록');
        $('#editScheduleId').val('');
        $('#scheduleDate').val(dateStr);
        $('#startHour').val('');
        $('#startMinute').val('');
        $('#endHour').val('');
        $('#endMinute').val('');
        $('#participantSearch').val('');
        $('#participantJobNo').val('');
        $('#scheduleType').val('대면상담');
        $('#alertSelect').val('');
        $('#customAlertMinutes').val('');
        $('.custom-alert-input').removeClass('show');
        $('.custom-alert-unit').removeClass('show');
        $('#scheduleMemo').val('');
        $('#memoCount').text('0');
        $('#scheduleModal').modal('show');
    }

    // =============================================
    // 5. 참여자 검색 (debounce 300ms)
    // =============================================
    $('#participantSearch').on('input', function () {
        var keyword = $(this).val().trim();
        var $dropdown = $('#participantDropdown');

        if (searchTimer) {
            clearTimeout(searchTimer);
        }

        if (keyword.length === 0) {
            $dropdown.removeClass('show');
            return;
        }

        searchTimer = setTimeout(function () {
            $.ajax({
                url: '/api/schedule/participants',
                type: 'GET',
                data: { keyword: keyword },
                success: function (res) {
                    if (res.success && res.data && res.data.length > 0) {
                        var html = '';
                        for (var i = 0; i < res.data.length; i++) {
                            var p = res.data[i];
                            html += '<div class="item" data-jobno="' + p.participantJobNo + '" data-name="' + p.participantName + '">';
                            html += '<span class="name">' + p.participantName + '</span>';
                            html += '<span class="num">' + p.participantJobNo + ' | ' + (p.participantStage || '') + '</span>';
                            html += '</div>';
                        }
                        $dropdown.html(html).addClass('show');
                    } else {
                        $dropdown.removeClass('show');
                    }
                },
                error: function () {
                    $dropdown.removeClass('show');
                }
            });
        }, 300);
    });

    // 참여자 드롭다운 항목 클릭
    $(document).on('click', '#participantDropdown .item', function () {
        var name = $(this).data('name');
        var jobNo = $(this).data('jobno');
        $('#participantSearch').val(name);
        $('#participantJobNo').val(jobNo);
        $('#participantDropdown').removeClass('show');
    });

    // 드롭다운 외부 클릭 시 닫기
    $(document).on('click', function (e) {
        if (!$(e.target).closest('.participant-search-wrapper').length) {
            $('#participantDropdown').removeClass('show');
        }
    });

    // =============================================
    // 6. 알림 설정
    // =============================================
    $('#alertSelect').on('change', function () {
        var val = $(this).val();
        if (val === 'custom') {
            $('.custom-alert-input').addClass('show');
            $('.custom-alert-unit').addClass('show');
        } else {
            $('.custom-alert-input').removeClass('show');
            $('.custom-alert-unit').removeClass('show');
            $('#customAlertMinutes').val('');
        }
    });

    // =============================================
    // 7. 메모 글자 수 카운터
    // =============================================
    $('#scheduleMemo').on('input', function () {
        $('#memoCount').text($(this).val().length);
    });

    // =============================================
    // 8. 저장 (등록/수정)
    // =============================================
    $('#btnSave').on('click', function () {
        var scheduleDate = $('#scheduleDate').val();
        var startHour = $('#startHour').val();
        var startMinute = $('#startMinute').val();
        var endHour = $('#endHour').val();
        var endMinute = $('#endMinute').val();
        var participantJobNo = $('#participantJobNo').val();
        var scheduleType = $('#scheduleType').val();
        var memo = $('#scheduleMemo').val();
        var editScheduleId = $('#editScheduleId').val();

        // 알림 값 계산
        var alertMinutes = null;
        var alertVal = $('#alertSelect').val();
        if (alertVal === 'custom') {
            var customVal = $('#customAlertMinutes').val();
            if (customVal) {
                alertMinutes = parseInt(customVal, 10);
            }
        } else if (alertVal) {
            alertMinutes = parseInt(alertVal, 10);
        }

        // 필수 입력 검증
        if (!scheduleDate) {
            Swal.fire('알림', '일정 날짜를 선택해주세요.', 'warning');
            return;
        }
        if (!startHour || !startMinute) {
            Swal.fire('알림', '시작 시간을 선택해주세요.', 'warning');
            return;
        }

        var startTime = startHour + ':' + startMinute;
        var endTime = (endHour && endMinute) ? endHour + ':' + endMinute : null;

        var requestData = {
            scheduleDate: scheduleDate,
            startTime: startTime,
            endTime: endTime,
            participantJobNo: participantJobNo ? parseInt(participantJobNo, 10) : null,
            scheduleType: scheduleType,
            memo: memo,
            alertMinutes: alertMinutes
        };

        var url, method;
        if (editScheduleId) {
            requestData.scheduleId = parseInt(editScheduleId, 10);
            url = '/api/schedule/update';
            method = 'PUT';
        } else {
            url = '/api/schedule/save';
            method = 'POST';
        }

        $.ajax({
            url: url,
            type: method,
            contentType: 'application/json',
            data: JSON.stringify(requestData),
            success: function (res) {
                if (res.success) {
                    $('#scheduleModal').modal('hide');
                    calendar.refetchEvents();
                    Swal.fire('완료', editScheduleId ? '일정이 수정되었습니다.' : '일정이 등록되었습니다.', 'success');
                } else {
                    Swal.fire('오류', res.message || '저장에 실패했습니다.', 'error');
                }
            },
            error: function () {
                Swal.fire('오류', '서버 통신 중 오류가 발생했습니다.', 'error');
            }
        });
    });

    // =============================================
    // 9. 상세 모달 열기
    // =============================================
    function openDetailModal(props) {
        $('#detailScheduleId').val(props.scheduleId);
        $('#detailDate').text(props.scheduleDate || '-');
        $('#detailTime').text(formatTimeRange(props.startTime, props.endTime));
        $('#detailParticipant').text(props.participantName || '(미정)');

        var type = props.scheduleType || '기타';
        var badgeClass = BADGE_CLASS_MAP[type] || 'badge-etc';
        $('#detailType').html('<span class="badge-schedule ' + badgeClass + '">' + type + '</span>');

        $('#detailAlert').text(formatAlertText(props.alertMinutes));
        $('#detailMemo').text(props.memo || '-');

        $('#detailModal').modal('show');
    }

    function formatTimeRange(startTime, endTime) {
        if (!startTime) return '-';
        var result = startTime;
        if (endTime) {
            result += ' ~ ' + endTime;
        }
        return result;
    }

    function formatAlertText(alertMinutes) {
        if (!alertMinutes && alertMinutes !== 0) return '알림없음';
        if (alertMinutes === 0) return '알림없음';
        if (alertMinutes === 10) return '10분 전';
        if (alertMinutes === 30) return '30분 전';
        if (alertMinutes === 60) return '60분 전';
        return alertMinutes + '분 전';
    }

    // =============================================
    // 10. 수정 버튼 (상세 → 등록 모달 전환)
    // =============================================
    $('#btnEdit').on('click', function () {
        var scheduleId = $('#detailScheduleId').val();
        if (!scheduleId) return;

        $('#detailModal').modal('hide');

        $.ajax({
            url: '/api/schedule/detail/' + scheduleId,
            type: 'GET',
            success: function (res) {
                if (res.success && res.data) {
                    var data = res.data;

                    $('#modalTitle').html('<i class="bi bi-pencil me-2"></i>상담 일정 수정');
                    $('#editScheduleId').val(data.scheduleId);
                    $('#scheduleDate').val(data.scheduleDate);

                    // 시간 파싱
                    if (data.startTime) {
                        var startParts = data.startTime.split(':');
                        $('#startHour').val(startParts[0]);
                        $('#startMinute').val(startParts[1]);
                    }
                    if (data.endTime) {
                        var endParts = data.endTime.split(':');
                        $('#endHour').val(endParts[0]);
                        $('#endMinute').val(endParts[1]);
                    } else {
                        $('#endHour').val('');
                        $('#endMinute').val('');
                    }

                    $('#participantSearch').val(data.participantName || '');
                    $('#participantJobNo').val(data.participantJobNo || '');
                    $('#scheduleType').val(data.scheduleType || '대면상담');

                    // 알림 복원
                    restoreAlertSetting(data.alertMinutes);

                    $('#scheduleMemo').val(data.memo || '');
                    $('#memoCount').text((data.memo || '').length);

                    $('#scheduleModal').modal('show');
                } else {
                    Swal.fire('오류', '일정 정보를 불러올 수 없습니다.', 'error');
                }
            },
            error: function () {
                Swal.fire('오류', '서버 통신 중 오류가 발생했습니다.', 'error');
            }
        });
    });

    function restoreAlertSetting(alertMinutes) {
        if (!alertMinutes && alertMinutes !== 0) {
            $('#alertSelect').val('');
            $('.custom-alert-input').removeClass('show');
            $('.custom-alert-unit').removeClass('show');
            $('#customAlertMinutes').val('');
            return;
        }

        var presetValues = ['10', '30', '60'];
        var alertStr = String(alertMinutes);

        if (alertMinutes === 0 || alertMinutes === null) {
            $('#alertSelect').val('');
            $('.custom-alert-input').removeClass('show');
            $('.custom-alert-unit').removeClass('show');
            $('#customAlertMinutes').val('');
        } else if (presetValues.indexOf(alertStr) !== -1) {
            $('#alertSelect').val(alertStr);
            $('.custom-alert-input').removeClass('show');
            $('.custom-alert-unit').removeClass('show');
            $('#customAlertMinutes').val('');
        } else {
            $('#alertSelect').val('custom');
            $('.custom-alert-input').addClass('show');
            $('.custom-alert-unit').addClass('show');
            $('#customAlertMinutes').val(alertMinutes);
        }
    }

    // =============================================
    // 11. 삭제 버튼
    // =============================================
    $('#btnDelete').on('click', function () {
        var scheduleId = $('#detailScheduleId').val();
        if (!scheduleId) return;

        Swal.fire({
            title: '일정을 삭제하시겠습니까?',
            text: '삭제된 일정은 복구할 수 없습니다.',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#6c757d',
            confirmButtonText: '삭제',
            cancelButtonText: '취소'
        }).then(function (result) {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/api/schedule/delete/' + scheduleId,
                    type: 'DELETE',
                    success: function (res) {
                        if (res.success) {
                            $('#detailModal').modal('hide');
                            calendar.refetchEvents();
                            Swal.fire('완료', '일정이 삭제되었습니다.', 'success');
                        } else {
                            Swal.fire('오류', res.message || '삭제에 실패했습니다.', 'error');
                        }
                    },
                    error: function () {
                        Swal.fire('오류', '서버 통신 중 오류가 발생했습니다.', 'error');
                    }
                });
            }
        });
    });

    // =============================================
    // 12. 상담일지 작성 버튼
    // =============================================
    $('#btnCounselNote').on('click', function () {
        var scheduleId = $('#detailScheduleId').val();
        if (!scheduleId) return;
        Swal.fire('안내', '상담일지 연동 기능은 추후 업데이트 예정입니다.', 'info');
    });

    // =============================================
    // 13. 드래그 핸들러 (eventDrop / eventResize)
    // =============================================
    function handleDrag(info) {
        var event = info.event;
        var props = event.extendedProps;
        var newStart = event.start;
        var newEnd = event.end;

        var newDate = newStart.toISOString().substring(0, 10);
        var newStartTime = padTime(newStart.getHours()) + ':' + padTime(newStart.getMinutes());
        var newEndTime = newEnd ? padTime(newEnd.getHours()) + ':' + padTime(newEnd.getMinutes()) : null;

        Swal.fire({
            title: '일정을 변경하시겠습니까?',
            html: '<strong>' + newDate + '</strong><br>' + newStartTime + (newEndTime ? ' ~ ' + newEndTime : ''),
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#007bff',
            cancelButtonColor: '#6c757d',
            confirmButtonText: '변경',
            cancelButtonText: '취소'
        }).then(function (result) {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/api/schedule/drag-update',
                    type: 'PUT',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        scheduleId: props.scheduleId,
                        newDate: newDate,
                        newStartTime: newStartTime,
                        newEndTime: newEndTime
                    }),
                    success: function (res) {
                        if (res.success) {
                            calendar.refetchEvents();
                            Swal.fire('완료', '일정이 변경되었습니다.', 'success');
                        } else {
                            info.revert();
                            Swal.fire('오류', res.message || '변경에 실패했습니다.', 'error');
                        }
                    },
                    error: function () {
                        info.revert();
                        Swal.fire('오류', '서버 통신 중 오류가 발생했습니다.', 'error');
                    }
                });
            } else {
                info.revert();
            }
        });
    }

    function padTime(num) {
        return String(num).padStart(2, '0');
    }

});
