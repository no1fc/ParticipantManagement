/**
 * schedule_0.0.3.js - 상담 일정 관리
 * FullCalendar 6.1.11 + jQuery + SweetAlert2
 * v0.0.3: 시간 콤보박스(input 직접입력 + 드롭다운 선택) + 자동 포커스 이동
 */
$(document).ready(function () {

    // =============================================
    // 1. 상수 및 색상/배지 맵
    // =============================================
    let COLOR_MAP = {
        '대면상담': '#007bff',
        '전화상담': '#28a745',
        '화상상담': '#ffc107',
        '기타': '#6c757d'
    };

    let BADGE_CLASS_MAP = {
        '대면상담': 'badge-face',
        '전화상담': 'badge-phone',
        '화상상담': 'badge-video',
        '기타': 'badge-etc'
    };

    let searchTimer = null;

    // 허용 값 목록
    let VALID_HOURS = ['08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20'];
    let VALID_MINUTES = ['00', '10', '20', '30', '40', '50'];

    // 자동 포커스 이동 순서
    let FOCUS_ORDER = ['startHourInput', 'startMinuteInput', 'endHourInput', 'endMinuteInput'];

    // =============================================
    // 2. 시간 콤보박스 (드롭다운 + 직접입력)
    // =============================================

    // input 포커스 → 드롭다운 열기
    $('.time-input').on('focus', function () {
        // 다른 열린 드롭다운 모두 닫기
        $('.time-dropdown').removeClass('show');
        let $combo = $(this).closest('.time-combo');
        let $dropdown = $combo.find('.time-dropdown');
        $dropdown.addClass('show');

        // 현재 값에 해당하는 옵션 활성화 표시
        let currentVal = $(this).val();
        $dropdown.find('.time-option').removeClass('active');
        if (currentVal) {
            $dropdown.find('.time-option[data-value="' + currentVal + '"]').addClass('active');
        }

        $(this).select();
    });

    // 드롭다운 영역 클릭 시 input blur 방지 (스크롤바 포함)
    $(document).on('mousedown', '.time-dropdown', function (e) {
        e.preventDefault();
    });

    // 드롭다운 옵션 클릭 → 값 선택
    $(document).on('mousedown', '.time-option', function (e) {
        e.preventDefault();
        let val = $(this).data('value');
        let $combo = $(this).closest('.time-combo');
        let $input = $combo.find('.time-input');
        let $hidden = $combo.find('input[type="hidden"]');

        $input.val(val).removeClass('is-invalid');
        $hidden.val(val);

        // 활성 표시 갱신
        $(this).closest('.time-dropdown').find('.time-option').removeClass('active');
        $(this).addClass('active');

        // 드롭다운 닫기
        $(this).closest('.time-dropdown').removeClass('show');

        // 다음 필드로 이동
        let inputId = $input.attr('id');
        moveToNextField(inputId);
    });

    // input 직접 입력 → 숫자만 허용 + 2자리 완성 시 검증
    $('.time-input').on('input', function () {
        let raw = $(this).val().replace(/[^0-9]/g, '');
        $(this).val(raw);

        let $combo = $(this).closest('.time-combo');
        let type = $combo.data('type');
        let $hidden = $combo.find('input[type="hidden"]');
        let $dropdown = $combo.find('.time-dropdown');
        let inputId = $(this).attr('id');

        if (raw.length === 2) {
            let padded = raw.padStart(2, '0');
            let validList = (type === 'hour') ? VALID_HOURS : VALID_MINUTES;

            if (validList.indexOf(padded) !== -1) {
                // 유효한 값: hidden 동기화 + 드롭다운 닫기 + 다음 필드로 이동
                $(this).val(padded).removeClass('is-invalid');
                $hidden.val(padded);
                $dropdown.removeClass('show');
                moveToNextField(inputId);
            } else {
                // 유효하지 않은 값: 초기화 + 흔들기 효과
                $(this).val('').addClass('is-invalid');
                $hidden.val('');
                $dropdown.find('.time-option').removeClass('active');
                setTimeout(function () {
                    $('#' + inputId).removeClass('is-invalid');
                }, 1000);
            }
        } else if (raw.length === 0) {
            $hidden.val('');
            $(this).removeClass('is-invalid');
            $dropdown.find('.time-option').removeClass('active');
        } else {
            // 1자리 입력 중: 매칭되는 옵션 하이라이트
            $dropdown.find('.time-option').removeClass('active');
            $dropdown.find('.time-option').each(function () {
                let optVal = String($(this).data('value'));
                if (optVal.indexOf(raw) === 0) {
                    $(this).addClass('active');
                }
            });
        }
    });

    // input에서 숫자 외 키 방지 (화살표, 백스페이스 등은 허용)
    $('.time-input').on('keydown', function (e) {
        let allowedKeys = [8, 9, 37, 39, 46]; // Backspace, Tab, Left, Right, Delete
        if (allowedKeys.indexOf(e.keyCode) !== -1) {
            return true;
        }
        if (e.keyCode >= 48 && e.keyCode <= 57) {
            return true; // 숫자 0-9
        }
        if (e.keyCode >= 96 && e.keyCode <= 105) {
            return true; // 넘패드 0-9
        }
        e.preventDefault();
    });

    // input blur → 드롭다운 닫기
    $('.time-input').on('blur', function () {
        let $combo = $(this).closest('.time-combo');
        $combo.find('.time-dropdown').removeClass('show');
    });

    // 외부 클릭 시 모든 시간 드롭다운 닫기
    $(document).on('click', function (e) {
        if (!$(e.target).closest('.time-combo').length) {
            $('.time-dropdown').removeClass('show');
        }
    });

    // 다음 필드로 포커스 이동
    function moveToNextField(currentInputId) {
        let currentIndex = FOCUS_ORDER.indexOf(currentInputId);
        if (currentIndex !== -1 && currentIndex < FOCUS_ORDER.length - 1) {
            let nextInputId = FOCUS_ORDER[currentIndex + 1];
            setTimeout(function () {
                $('#' + nextInputId).focus();
            }, 50);
        }
    }

    // hidden + input 동시 초기화
    function clearTimeFields() {
        $('#startHour, #startMinute, #endHour, #endMinute').val('');
        $('#startHourInput, #startMinuteInput, #endHourInput, #endMinuteInput')
            .val('').removeClass('is-invalid');
        $('.time-dropdown').removeClass('show');
    }

    // hidden + input 값 동시 설정
    function setTimeField(hiddenId, value) {
        $('#' + hiddenId).val(value);
        $('#' + hiddenId + 'Input').val(value).removeClass('is-invalid');
    }

    // =============================================
    // 3. FullCalendar 초기화
    // =============================================
    let calendarEl = document.getElementById('calendar');
    let calendar = new FullCalendar.Calendar(calendarEl, {
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
        slotMinTime: '08:00:00',
        slotMaxTime: '20:00:00',
        allDaySlot: false,

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
                        let events = res.data.map(function (item) {
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
    // 4. 등록 버튼 클릭
    // =============================================
    $('#btnRegister').on('click', function () {
        let today = new Date().toISOString().substring(0, 10);
        openRegisterModal(today);
    });

    // =============================================
    // 5. 등록 모달 열기
    // =============================================
    function openRegisterModal(dateStr) {
        $('#modalTitle').html('<i class="bi bi-calendar-plus me-2"></i>상담 일정 등록');
        $('#editScheduleId').val('');
        $('#scheduleDate').val(dateStr);
        clearTimeFields();
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
    // 6. 참여자 검색 (debounce 300ms)
    // =============================================
    $('#participantSearch').on('input', function () {
        let keyword = $(this).val().trim();
        let $dropdown = $('#participantDropdown');

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
                        let html = '';
                        for (let i = 0; i < res.data.length; i++) {
                            let p = res.data[i];
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
        let name = $(this).data('name');
        let jobNo = $(this).data('jobno');
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
    // 7. 알림 설정
    // =============================================
    $('#alertSelect').on('change', function () {
        let val = $(this).val();
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
    // 8. 메모 글자 수 카운터
    // =============================================
    $('#scheduleMemo').on('input', function () {
        $('#memoCount').text($(this).val().length);
    });

    // =============================================
    // 9. 저장 (등록/수정)
    // =============================================
    $('#btnSave').on('click', function () {
        let scheduleDate = $('#scheduleDate').val();
        let startHour = $('#startHour').val();
        let startMinute = $('#startMinute').val();
        let endHour = $('#endHour').val();
        let endMinute = $('#endMinute').val();
        let participantJobNo = $('#participantJobNo').val();
        let scheduleType = $('#scheduleType').val();
        let memo = $('#scheduleMemo').val();
        let editScheduleId = $('#editScheduleId').val();

        // 알림 값 계산
        let alertMinutes = null;
        let alertVal = $('#alertSelect').val();
        if (alertVal === 'custom') {
            let customVal = $('#customAlertMinutes').val();
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

        let startTime = startHour + ':' + startMinute;
        let endTime = (endHour && endMinute) ? endHour + ':' + endMinute : null;

        let requestData = {
            scheduleDate: scheduleDate,
            startTime: startTime,
            endTime: endTime,
            participantJobNo: participantJobNo ? parseInt(participantJobNo, 10) : null,
            scheduleType: scheduleType,
            memo: memo,
            alertMinutes: alertMinutes
        };

        let url, method;
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
            error: function (errorResult) {
                if(errorResult.status === 409) {
                    Swal.fire('오류', errorResult.message, 'error');
                }
                else {
                    if(errorResult.success === false) {
                        Swal.fire('오류', errorResult.message, 'error');
                    }
                    else {
                        Swal.fire('오류', '서버 통신 중 오류가 발생했습니다.', 'error');
                    }
                }
            }
        });
    });

    // =============================================
    // 10. 상세 모달 열기
    // =============================================
    function openDetailModal(props) {
        $('#detailScheduleId').val(props.scheduleId);
        $('#detailDate').text(props.scheduleDate || '-');
        $('#detailTime').text(formatTimeRange(props.startTime, props.endTime));
        $('#detailParticipant').text(props.participantName || '(미정)');

        let type = props.scheduleType || '기타';
        let badgeClass = BADGE_CLASS_MAP[type] || 'badge-etc';
        $('#detailType').html('<span class="badge-schedule ' + badgeClass + '">' + type + '</span>');

        $('#detailAlert').text(formatAlertText(props.alertMinutes));
        $('#detailMemo').text(props.memo || '-');

        $('#detailModal').modal('show');
    }

    function formatTimeRange(startTime, endTime) {
        if (!startTime) return '-';
        let result = startTime;
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
    // 11. 수정 버튼 (상세 → 등록 모달 전환)
    // =============================================
    $('#btnEdit').on('click', function () {
        let scheduleId = $('#detailScheduleId').val();
        if (!scheduleId) return;

        $('#detailModal').modal('hide');

        $.ajax({
            url: '/api/schedule/detail/' + scheduleId,
            type: 'GET',
            success: function (res) {
                if (res.success && res.data) {
                    let data = res.data;

                    $('#modalTitle').html('<i class="bi bi-pencil me-2"></i>상담 일정 수정');
                    $('#editScheduleId').val(data.scheduleId);
                    $('#scheduleDate').val(data.scheduleDate);

                    // 시간 파싱 (hidden + input 동시 설정)
                    clearTimeFields();
                    if (data.startTime) {
                        let startParts = data.startTime.split(':');
                        setTimeField('startHour', startParts[0]);
                        setTimeField('startMinute', startParts[1]);
                    }
                    if (data.endTime) {
                        let endParts = data.endTime.split(':');
                        setTimeField('endHour', endParts[0]);
                        setTimeField('endMinute', endParts[1]);
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

        let presetValues = ['10', '30', '60'];
        let alertStr = String(alertMinutes);

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
    // 12. 삭제 버튼
    // =============================================
    $('#btnDelete').on('click', function () {
        let scheduleId = $('#detailScheduleId').val();
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
    // 13. 상담일지 작성 버튼
    // =============================================
    $('#btnCounselNote').on('click', function () {
        let scheduleId = $('#detailScheduleId').val();
        if (!scheduleId) return;
        Swal.fire('안내', '상담일지 연동 기능은 추후 업데이트 예정입니다.', 'info');
    });

    // =============================================
    // 14. 드래그 핸들러 (eventDrop / eventResize)
    // =============================================
    function handleDrag(info) {
        let event = info.event;
        let props = event.extendedProps;
        let newStart = event.start;
        let newEnd = event.end;

        let newDate = newStart.toISOString().substring(0, 10);
        let newStartTime = padTime(newStart.getHours()) + ':' + padTime(newStart.getMinutes());
        let newEndTime = newEnd ? padTime(newEnd.getHours()) + ':' + padTime(newEnd.getMinutes()) : null;

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