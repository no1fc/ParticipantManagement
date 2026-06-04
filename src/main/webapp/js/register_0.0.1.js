/**
 * @file 회원가입 페이지 (아이디 중복 체크, 비밀번호 검증, 폼 제출)
 * @version 0.0.1
 * @requires jQuery, SweetAlert2
 */
$(document).ready(function () {

    /** 아이디 중복 체크 완료 여부 */
    let idChecked = false;

    /** 마지막으로 중복 체크한 아이디 */
    let lastCheckedId = '';

    const registerBtn = $('#registerBtn');
    const checkDuplicateBtn = $('#checkDuplicateBtn');
    const memberUserID = $('#memberUserID');
    const memberUserPW = $('#memberUserPW');
    const memberUserPWConfirm = $('#memberUserPWConfirm');
    const idCheckResult = $('#idCheckResult');
    const pwMatchResult = $('#pwMatchResult');

    // 비밀번호 정규식: 소문자, 숫자, 특수문자 포함 5자 이상
    const passwordRegex = /^(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{5,}$/;

    // ========================================
    // 아이디 중복 체크
    // ========================================

    checkDuplicateBtn.on('click', function () {
        const userId = memberUserID.val().trim();
        if (!userId) {
            idCheckResult.html('<span class="is-error">아이디를 입력해주세요.</span>');
            return;
        }

        $.ajax({
            url: '/register.api',
            type: 'GET',
            data: {memberUserID: userId},
            dataType: 'json',
            success: function (data) {
                if (data.available) {
                    idCheckResult.html('<span class="is-success">' + data.message + '</span>');
                    idChecked = true;
                    lastCheckedId = userId;
                } else {
                    idCheckResult.html('<span class="is-error">' + data.message + '</span>');
                    idChecked = false;
                }
            },
            error: function () {
                idCheckResult.html('<span class="is-error">중복 확인 중 오류가 발생했습니다.</span>');
                idChecked = false;
            }
        });
    });

    // 아이디 변경 시 중복 체크 상태 초기화
    memberUserID.on('input', function () {
        if ($(this).val().trim() !== lastCheckedId) {
            idChecked = false;
            idCheckResult.html('');
        }
    });

    // ========================================
    // 비밀번호 실시간 검증
    // ========================================

    memberUserPWConfirm.on('input', function () {
        validatePassword();
    });

    memberUserPW.on('input', function () {
        if (memberUserPWConfirm.val()) {
            validatePassword();
        }
    });

    function validatePassword() {
        const pw = memberUserPW.val();
        const pwConfirm = memberUserPWConfirm.val();

        pwMatchResult.html('');

        if (pw.search(/\s/) !== -1) {
            pwMatchResult.html('<span class="is-error">비밀번호는 공백 없이 입력해주세요.</span>');
            return false;
        }
        if (!passwordRegex.test(pw)) {
            pwMatchResult.html('<span class="is-error">비밀번호는 5자 이상, 소문자/숫자/특수문자(#?!@$%^&*-)를 모두 포함해야 합니다.</span>');
            return false;
        }
        if (pw !== pwConfirm) {
            pwMatchResult.html('<span class="is-error">비밀번호가 일치하지 않습니다.</span>');
            return false;
        }

        pwMatchResult.html('<span class="is-success">비밀번호가 일치합니다.</span>');
        return true;
    }

    // ========================================
    // 비밀번호 표시/숨김 토글
    // ========================================

    $('#togglePW').on('click', function () {
        togglePasswordVisibility($(this), memberUserPW);
    });

    $('#togglePWConfirm').on('click', function () {
        togglePasswordVisibility($(this), memberUserPWConfirm);
    });

    function togglePasswordVisibility($icon, $input) {
        if ($input.attr('type') === 'password') {
            $input.attr('type', 'text');
            $icon.removeClass('fa-eye-slash').addClass('fa-eye');
        } else {
            $input.attr('type', 'password');
            $icon.removeClass('fa-eye').addClass('fa-eye-slash');
        }
    }

    // ========================================
    // 회원가입 제출
    // ========================================

    registerBtn.on('click', function () {
        // 필수값 검증
        const name = $('#memberUserName').val().trim();
        const userId = memberUserID.val().trim();
        const pw = memberUserPW.val();
        const branch = $('#memberBranch').val();

        if (!name) {
            Swal.fire({icon: 'warning', title: '알림', text: '이름을 입력해주세요.'});
            return;
        }
        if (!userId) {
            Swal.fire({icon: 'warning', title: '알림', text: '아이디를 입력해주세요.'});
            return;
        }
        if (!idChecked) {
            Swal.fire({icon: 'warning', title: '알림', text: '아이디 중복 확인을 해주세요.'});
            return;
        }
        if (!pw) {
            Swal.fire({icon: 'warning', title: '알림', text: '비밀번호를 입력해주세요.'});
            return;
        }
        if (!validatePassword()) {
            return;
        }
        if (!branch) {
            Swal.fire({icon: 'warning', title: '알림', text: '지점을 선택해주세요.'});
            return;
        }

        $.ajax({
            url: '/register.api',
            type: 'POST',
            data: $('#registerForm').serialize(),
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    Swal.fire({
                        icon: 'success',
                        title: '회원가입 완료',
                        text: data.message,
                        confirmButtonText: '로그인으로 이동'
                    }).then(function () {
                        location.href = '/login.do';
                    });
                } else {
                    Swal.fire({icon: 'error', title: '회원가입 실패', text: data.message});
                }
            },
            error: function () {
                Swal.fire({icon: 'error', title: '오류', text: '회원가입 처리 중 오류가 발생했습니다.'});
            }
        });
    });
});
