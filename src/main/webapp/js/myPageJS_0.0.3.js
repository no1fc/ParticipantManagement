/**
 * @file 마이페이지 JavaScript (계정 정보, 일일보고, 비밀번호 변경)
 * @version 0.0.3
 * @requires jQuery, SweetAlert2
 */
$(document).ready(function () {

    const $passwordVerifySection = $('#password-verify-section');
    const $mypageTabsSection = $('#mypage-tabs-section');
    const $checkPasswordBtn = $('#checkPasswordBtn');
    const $checkPassword = $('#checkPassword');

    // 비밀번호 정규식: 영문 대소문자, 특수문자, 숫자 포함 6자 이상
    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*[!@#$%^&*])(?=.*[0-9])(?=.{6,})/;

    // 일일보고 이전 값 저장용
    const prevValues = { employment: 0, placement: 0 };

    // ========================================
    // 1. 비밀번호 확인 → 탭 표시
    // ========================================

    $checkPasswordBtn.on('click', function () {
        fetch('checkPassword.api', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ memberUserPW: $checkPassword.val() })
        })
        .then(function (r) {
            if (r.status === 200) return r.json();
            if (r.status === 401) return r.json().then(function (d) { throw new Error(d.message); });
            if (r.status === 400) throw new Error('비밀번호를 입력해주세요.');
            throw new Error('서버 오류가 발생했습니다.');
        })
        .then(function (result) {
            // 비밀번호 미설정 사용자 → 설정 화면 표시
            if (result.status === 'PASSWORD_REQUIRED') {
                $passwordVerifySection.hide();
                $('#password-setup-section').show();
                return;
            }
            // 비밀번호 확인 섹션 숨기고 탭 표시
            $passwordVerifySection.hide();
            $mypageTabsSection.show();
            populateData(result.data);
        })
        .catch(function (e) {
            Swal.fire({ icon: 'error', title: '확인 실패', text: e.message });
        });
    });

    // Enter 키로 비밀번호 확인
    $checkPassword.on('keypress', function (e) {
        if (e.which === 13) $checkPasswordBtn.click();
    });

    // ========================================
    // 2. 데이터 채우기
    // ========================================

    function populateData(data) {
        // 계정 정보 (읽기 전용)
        $('#profileName').text(getVal(data, 'memberUserName'));
        $('#profileId').text(getVal(data, 'memberUserID'));
        $('#profileBranch').text(getVal(data, 'memberBranch'));
        $('#profileUniqueNo').text(getVal(data, 'memberUniqueNumber'));
        $('#profileRegDate').text(getVal(data, 'memberRegDate'));
        $('#profileJoinDate').text(getVal(data, 'memberJoinedDate'));
        $('#profileContinuous').text(getVal(data, 'memberContinuous'));

        // 연락처 (수정 가능)
        $('#contactPhone').val(getVal(data, 'memberPhoneNumber'));
        $('#contactEmail').val(getVal(data, 'memberEmail'));

        // 일일보고
        const todayEmp = getNumVal(data, 'memberTodayEmployment');
        const todayPlc = getNumVal(data, 'memberTodayPlacement');
        $('#todayEmployment').val(todayEmp);
        $('#todayPlacement').val(todayPlc);
        $('#weekEmployment').val(getNumVal(data, 'memberToWeekEmployment'));
        $('#weekPlacement').val(getNumVal(data, 'memberToWeekPlacement'));
        $('#monthEmployment').val(getNumVal(data, 'memberToMonthEmployment'));
        $('#monthPlacement').val(getNumVal(data, 'memberToMonthPlacement'));
        $('#yearEmployment').val(getNumVal(data, 'memberToYearEmployment'));
        $('#yearPlacement').val(getNumVal(data, 'memberToYearPlacement'));

        // 이전 값 저장
        prevValues.employment = todayEmp;
        prevValues.placement = todayPlc;

        // 업데이트 일자
        const updateDate = getVal(data, 'endUpdateStatus');
        $('#reportUpdateDate').text(updateDate ? '마지막 저장: ' + updateDate : '저장 기록 없음');

        // 날짜 기반 초기화
        initializeDateValues(updateDate);
    }

    function getVal(data, key) {
        if (!data || !data[key]) return '-';
        return data[key].val || '-';
    }

    function getNumVal(data, key) {
        if (!data || !data[key]) return 0;
        return parseInt(data[key].val) || 0;
    }

    // ========================================
    // 3. 날짜 기반 실적 초기화
    // ========================================

    function initializeDateValues(lastDate) {
        if (!lastDate || lastDate === '-') return;

        const saved = new Date(lastDate);
        const today = new Date();

        if (saved.getFullYear() !== today.getFullYear()) {
            $('#yearEmployment, #yearPlacement').val(0);
        }
        if (saved.getMonth() !== today.getMonth()) {
            $('#monthEmployment, #monthPlacement').val(0);
        }
        if (getWeek(saved) !== getWeek(today)) {
            $('#weekEmployment, #weekPlacement').val(0);
        }
        if (saved.getDate() !== today.getDate() || saved.getMonth() !== today.getMonth()) {
            $('#todayEmployment, #todayPlacement').val(0);
            prevValues.employment = 0;
            prevValues.placement = 0;
        }
    }

    function getWeek(date) {
        const d = new Date(date);
        const day = d.getDay();
        const diff = day === 0 ? -6 : 1 - day;
        const monday = new Date(d);
        monday.setDate(d.getDate() + diff);

        const firstDay = new Date(d.getFullYear(), 0, 1);
        const firstDayOfWeek = firstDay.getDay();
        const firstDiff = firstDayOfWeek === 0 ? 1 : 9 - firstDayOfWeek;
        const firstMonday = new Date(firstDay);
        firstMonday.setDate(firstDay.getDate() + firstDiff - 7);

        return Math.floor((monday - firstMonday) / (1000 * 60 * 60 * 24 * 7)) + 1;
    }

    // ========================================
    // 4. 일일보고 — 금일 변경 시 누적 자동 계산
    // ========================================

    $('#todayEmployment').on('change', function () {
        const newVal = parseInt($(this).val()) || 0;
        const diff = newVal - prevValues.employment;
        addToField('#weekEmployment', diff);
        addToField('#monthEmployment', diff);
        addToField('#yearEmployment', diff);
        prevValues.employment = newVal;
    });

    $('#todayPlacement').on('change', function () {
        const newVal = parseInt($(this).val()) || 0;
        const diff = newVal - prevValues.placement;
        addToField('#weekPlacement', diff);
        addToField('#monthPlacement', diff);
        addToField('#yearPlacement', diff);
        prevValues.placement = newVal;
    });

    function addToField(selector, diff) {
        const $el = $(selector);
        $el.val((parseInt($el.val()) || 0) + diff);
    }

    // ========================================
    // 5. 연락처 저장
    // ========================================

    $('#saveContactBtn').on('click', function () {
        const phone = $('#contactPhone').val();
        const email = $('#contactEmail').val();

        $.ajax({
            url: '/updateContact.api',
            type: 'POST',
            contentType: 'application/json;charset=UTF-8',
            data: JSON.stringify({ memberPhoneNumber: phone, memberEmail: email }),
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    Swal.fire({ icon: 'success', title: '저장 완료', text: data.message });
                } else {
                    Swal.fire({ icon: 'error', title: '실패', text: data.message });
                }
            },
            error: function () {
                Swal.fire({ icon: 'error', title: '오류', text: '서버 연결 오류가 발생했습니다.' });
            }
        });
    });

    // ========================================
    // 6. 일일보고 저장
    // ========================================

    $('#saveDailyReportBtn').on('click', function () {
        const reportData = {
            memberTodayEmployment: parseInt($('#todayEmployment').val()) || 0,
            memberTodayPlacement: parseInt($('#todayPlacement').val()) || 0,
            memberToWeekEmployment: parseInt($('#weekEmployment').val()) || 0,
            memberToWeekPlacement: parseInt($('#weekPlacement').val()) || 0,
            memberToMonthEmployment: parseInt($('#monthEmployment').val()) || 0,
            memberToMonthPlacement: parseInt($('#monthPlacement').val()) || 0,
            memberToYearEmployment: parseInt($('#yearEmployment').val()) || 0,
            memberToYearPlacement: parseInt($('#yearPlacement').val()) || 0
        };

        $.ajax({
            url: '/updateDailyReport.api',
            type: 'POST',
            contentType: 'application/json;charset=UTF-8',
            data: JSON.stringify(reportData),
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    Swal.fire({ icon: 'success', title: '저장 완료', text: data.message });
                    const today = new Date();
                    const dateStr = today.getFullYear() + '-' +
                        String(today.getMonth() + 1).padStart(2, '0') + '-' +
                        String(today.getDate()).padStart(2, '0');
                    $('#reportUpdateDate').text('마지막 저장: ' + dateStr);
                } else {
                    Swal.fire({ icon: 'error', title: '실패', text: data.message });
                }
            },
            error: function () {
                Swal.fire({ icon: 'error', title: '오류', text: '서버 연결 오류가 발생했습니다.' });
            }
        });
    });

    // ========================================
    // 7. 비밀번호 변경
    // ========================================

    const $newPW = $('#newPW');
    const $newPWConfirm = $('#newPWConfirm');
    const $pwResult = $('#pwValidationResult');

    $newPWConfirm.on('input', validateNewPW);
    $newPW.on('input', function () {
        if ($newPWConfirm.val()) validateNewPW();
    });

    function validateNewPW() {
        const pw = $newPW.val();
        const confirm = $newPWConfirm.val();
        $pwResult.empty();

        if (pw.search(/\s/) !== -1) {
            $pwResult.html('<span class="text-danger small">공백 없이 입력해주세요.</span>');
            return false;
        }
        if (!passwordRegex.test(pw)) {
            $pwResult.html('<span class="text-danger small">영문, 특수문자(!@#$%^&*), 숫자 포함 6자 이상</span>');
            return false;
        }
        if (pw !== confirm) {
            $pwResult.html('<span class="text-danger small">비밀번호가 일치하지 않습니다.</span>');
            return false;
        }
        $pwResult.html('<span class="text-success small">비밀번호가 일치합니다.</span>');
        return true;
    }

    $('#changePasswordBtn').on('click', function () {
        if (!validateNewPW()) return;

        Swal.fire({
            icon: 'warning',
            title: '비밀번호 변경',
            text: '비밀번호를 변경하시겠습니까?',
            showCancelButton: true,
            confirmButtonText: '변경',
            cancelButtonText: '취소'
        }).then(function (result) {
            if (!result.isConfirmed) return;

            $.ajax({
                url: '/changeMyPassword.api',
                type: 'POST',
                contentType: 'application/json;charset=UTF-8',
                data: JSON.stringify({ memberUserChangePW: $newPW.val() }),
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        Swal.fire({ icon: 'success', title: '변경 완료', text: data.message });
                        $newPW.val('');
                        $newPWConfirm.val('');
                        $pwResult.empty();
                    } else {
                        Swal.fire({ icon: 'error', title: '실패', text: data.message });
                    }
                },
                error: function () {
                    Swal.fire({ icon: 'error', title: '오류', text: '서버 연결 오류가 발생했습니다.' });
                }
            });
        });
    });

    // ========================================
    // 비밀번호 초기 설정 (미설정 사용자)
    // ========================================

    $('#setupPasswordBtn').on('click', function () {
        const newPW = $('#setupNewPassword').val();
        const confirmPW = $('#setupConfirmPassword').val();

        if (!newPW || newPW.trim() === '') {
            Swal.fire({ icon: 'warning', title: '입력 오류', text: '새 비밀번호를 입력해주세요.' });
            return;
        }
        if (!passwordRegex.test(newPW)) {
            Swal.fire({ icon: 'warning', title: '비밀번호 조건 불충족', text: '영문, 특수문자, 숫자를 포함하여 6자 이상 입력해주세요.' });
            return;
        }
        if (newPW !== confirmPW) {
            Swal.fire({ icon: 'warning', title: '불일치', text: '비밀번호가 일치하지 않습니다.' });
            return;
        }

        fetch('/changeMyPassword.api', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ memberUserChangePW: newPW })
        })
        .then(function (r) { return r.json(); })
        .then(function (result) {
            if (result.success) {
                Swal.fire({ icon: 'success', title: '설정 완료', text: '비밀번호가 설정되었습니다. 설정한 비밀번호로 확인해주세요.' })
                    .then(function () {
                        $('#password-setup-section').hide();
                        $passwordVerifySection.show();
                        $checkPassword.val('').focus();
                    });
            } else {
                Swal.fire({ icon: 'error', title: '설정 실패', text: result.message || '비밀번호 설정에 실패했습니다.' });
            }
        })
        .catch(function () {
            Swal.fire({ icon: 'error', title: '오류', text: '서버 오류가 발생했습니다.' });
        });
    });
});
