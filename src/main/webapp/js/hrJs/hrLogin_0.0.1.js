/*
 * HR 로그인 (데모)
 * AJAX POST /hr/login → 성공 시 리다이렉트, 실패 시 SweetAlert2.
 */
$(function () {
    const $form = $('#hrLoginForm');
    const $submit = $form.find('.hr-login-submit');

    // 비밀번호 표시/숨김 토글
    $('#togglePassword').on('click', function () {
        const $pw = $('#password');
        const $icon = $('#eyeIcon');
        const isHidden = $pw.attr('type') === 'password';
        $pw.attr('type', isHidden ? 'text' : 'password');
        $icon.toggleClass('bi-eye-slash', !isHidden);
        $icon.toggleClass('bi-eye', isHidden);
    });

    $form.on('submit', function (e) {
        e.preventDefault();

        const userId = $('#userId').val().trim();
        const password = $('#password').val();

        if (!userId || !password) {
            Swal.fire({ icon: 'warning', title: '입력 확인', text: '아이디와 비밀번호를 입력하세요.' });
            return;
        }

        $submit.prop('disabled', true);

        $.ajax({
            url: '/hr/login',
            type: 'POST',
            data: { userId: userId, password: password }
        }).done(function (res) {
            if (res && res.success) {
                window.location.href = res.redirect || '/hr/departments';
                return;
            }
            Swal.fire({
                icon: 'error',
                title: '로그인 실패',
                text: (res && res.message) || '아이디 또는 비밀번호를 확인해주세요.'
            });
            $submit.prop('disabled', false);
        }).fail(function () {
            Swal.fire({ icon: 'error', title: '오류', text: '로그인 처리 중 오류가 발생했습니다. 잠시 후 다시 시도하세요.' });
            $submit.prop('disabled', false);
        });
    });
});
