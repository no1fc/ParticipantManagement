<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 24. 12. 26.
  Time: 오전 11:14
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <mytag:Logo/>
    <!-- mouse pointer 모양 bootstrap 5 -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <!-- bootstrap5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js" integrity="sha384-IQsoLXl5PILFhosVNubq5LC7Qb9DXgDA9i+tQ8Zj3iwWAwPtgFTxbJ8NT4GN1R8p" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js" integrity="sha384-cVKIPhGWiC2Al4u+LWgxfKTRIcfu0JTxR+EQDz/bgldoEyl4H0zUF0QKbrJ0EcQF" crossorigin="anonymous"></script>

    <meta name="viewport" content="width=device-width, initial-scale=1">

<%--    눈 아이콘(예: fa-eye및 fa-eye-slash)--%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">

    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    
    <link rel="stylesheet" href="/css/participantCss/loginPage_0.0.1.css">
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">
    <style>
        body {
            background: linear-gradient(135deg, #f3f5f9 0%, #c3cfe2 100%);
        }
    </style>
</head>
<body>
<div class="container d-flex w-100 h-100 justify-content-center align-items-center min-vh-100">
    <div class="login-container">
        <div class="login-box p-5">
            <div class="login-brand">
                <img src="/img/JobmoaLogo.svg" alt="JobMoa Logo" class="login-logo">
            </div>            <h2 class="text-center mb-4 fw-bold login-title">로그인</h2>
            <form action="/login.do" method="POST">
                <div class="mb-4">
                    <label for="memberUserID" class="form-label fw-bold">아이디</label>
                    <div class="input-group">
                        <span class="input-group-text">
                            <i class="fa fa-user"></i>
                        </span>
                        <input type="text" class="form-control" id="memberUserID" name="memberUserID"
                               placeholder="아이디를 입력해주세요" required>
                    </div>
                </div>
                <div class="mb-4">
                    <label for="memberUserPW" class="form-label fw-bold">비밀번호</label>
                    <div class="input-group">
                        <span class="input-group-text">
                            <i class="fa fa-lock"></i>
                        </span>
                        <input type="password" class="form-control" id="memberUserPW" name="memberUserPW"
                               placeholder="비밀번호를 입력해주세요" required>
                        <span class="input-group-text">
                            <i class="fas fa-eye-slash" id="icon-password"></i>
                        </span>
                    </div>
                </div>
                <div class="d-grid gap-2 mb-4">
                    <button type="submit" class="btn btn-primary btn-lg">로그인</button>
                </div>
                <div class="text-center">
                    <a href="#" class="text-decoration-none text-primary fw-bold"
                       data-bs-toggle="modal" data-bs-target="#changePasswordModal">
                        비밀번호 찾기
                    </a>
                    <br>
                    <br>
                    <a href="/jobPlacement/placementList" class="text-decoration-none text-primary fw-bold">
                        기업회원 알선 확인
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>


<!-- 비밀번호 찾기 Modal -->
<div class="modal fade login-modal" id="changePasswordModal" data-bs-backdrop="static" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">비밀번호 변경</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="changePasswordForm">
                    <div class="mb-3 row">
                        <label for="changeUserID" class="form-label">아이디</label>
                        <input type="text" class="form-control" id="changeUserID" required>
                        <button type="button" class="btn btn-primary" id="sendMail">인증번호 발송</button>
                    </div>
                    <div id="primaryKeyDiv" class="mb-3 row">
                        <label for="primaryKey" class="form-label">인증번호</label>
                        <input type="text" class="form-control" id="primaryKey">
                        <button type="button" class="btn btn-primary" id="checkAuth">인증번호 확인</button>
                    </div>
                    <div id="changePasswordDiv" class="mb-3 row">
                        <label for="changePassword" class="form-label">변경 비밀번호</label>
                        <input type="password" class="form-control mb-3" id="changePassword">
                        <i class="fas fa-eye-slash" id="toggle-password"></i>

                        <label for="changePasswordCheck" class="form-label">변경 비밀번호 확인</label>
                        <input type="password" class="form-control" id="changePasswordCheck">
                        <i class="fas fa-eye-slash" id="toggle-password-check"></i>

                        <div id="changePasswordResult">

                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                <button type="button" class="btn btn-primary" id="changePasswordBtn">비밀번호 변경</button>
            </div>
        </div>
    </div>
</div>
</body>

<script>
    /**
     * 로그인 페이지 JavaScript 모듈
     *
     * 기능:
     * - 로그인 폼 UI 인터랙션
     * - 비밀번호 찾기 모달 관리
     * - 이메일 인증 및 비밀번호 변경 처리
     * - 입력 유효성 검증
     * - 비밀번호 표시/숨김 토글
     *
     * 의존성:
     * - jQuery 3.7.1
     * - Bootstrap 5.0.2
     * - Font Awesome 6.1.1
     *
     * @author [개발자명]
     * @version 1.0.0
     * @since 2024-12-26
     */

    $(document).ready(function() {
        // ========================================
        // DOM 요소 선택자 캐싱
        // ========================================

        /** @type {jQuery} 모든 form-control 클래스 요소 */
        let formControl = $('.form-control');

        /** @type {jQuery} 비밀번호 변경 실행 버튼 */
        let changePasswordBtn = $('#changePasswordBtn');

        /** @type {jQuery} 이메일 인증번호 발송 버튼 */
        let sendMailBtn = $('#sendMail');

        /** @type {jQuery} 비밀번호 찾기 폼 */
        let changePasswordForm = $('#changePasswordForm');

        /** @type {jQuery} 비밀번호 찾기 모달 */
        let changePasswordModal = $('#changePasswordModal');

        /** @type {jQuery} 인증번호 입력 필드 */
        let primaryKeyInput = $('#primaryKey');

        /** @type {jQuery} 인증번호 입력 영역 컨테이너 */
        let primaryKeyDiv = $('#primaryKeyDiv');

        /** @type {jQuery} 인증번호 확인 버튼 */
        let checkAuthBtn = $('#checkAuth');

        /** @type {boolean} 이메일 인증 완료 상태 플래그 */
        let checkAuthFlag = false;

        // 비밀번호 변경 관련 요소들
        /** @type {jQuery} 비밀번호 변경 섹션 컨테이너 */
        const changePasswordDiv = $("#changePasswordDiv");

        /** @type {jQuery} 새 비밀번호 표시/숨김 토글 아이콘 */
        const toggleChangePassword = $("#toggle-password");

        /** @type {jQuery} 비밀번호 확인 표시/숨김 토글 아이콘 */
        const toggleChangePasswordCheck = $("#toggle-password-check");

        /** @type {jQuery} 새 비밀번호 입력 필드 */
        const changePassword = $("#changePassword");

        /** @type {jQuery} 비밀번호 확인 입력 필드 */
        const changePasswordCheck = $("#changePasswordCheck");

        /** @type {jQuery} 비밀번호 유효성 검증 결과 표시 영역 */
        const changePasswordResult = $("#changePasswordResult");

        // 로그인 폼 관련 요소들
        /** @type {jQuery} 로그인 비밀번호 입력 필드 */
        const memberUserPW = $("#memberUserPW");

        /** @type {jQuery} 로그인 비밀번호 표시/숨김 토글 아이콘 */
        const iconPassword = $("#icon-password");

        // ========================================
        // 초기화 및 이벤트 리스너 등록
        // ========================================

        /** 페이지 로드 시 폼 초기 상태로 리셋 */
        resetForm();

        /**
         * 입력 필드 포커스 효과
         * 포커스 시 부모 요소에 shadow-sm 클래스 추가
         * 블러 시 shadow-sm 클래스 제거
         */
        formControl.focus(function() {
            $(this).parent().addClass('shadow-sm');
        }).blur(function() {
            $(this).parent().removeClass('shadow-sm');
        });

        // ========================================
        // 이메일 인증 관련 이벤트 핸들러
        // ========================================

        /**
         * 인증번호 발송 버튼 클릭 이벤트
         *
         * 처리 과정:
         * 1. 폼 유효성 검증
         * 2. 서버로 이메일 발송 요청
         * 3. 성공 시 인증번호 입력 영역 표시
         * 4. 실패 시 오류 메시지 표시
         */
        sendMailBtn.click(function() {
            // HTML5 폼 유효성 검증
            if(changePasswordForm[0].checkValidity()) {
                $.ajax({
                    url: 'pwChangeSendEmail.api',        // 이메일 발송 API 엔드포인트
                    type: 'POST',                       // HTTP POST 메서드
                    data: JSON.stringify({              // 요청 데이터를 JSON 문자열로 변환
                        "userId": changePasswordForm.find('#changeUserID').val(),
                    }),
                    contentType: 'application/json; charset=utf-8',  // 요청 Content-Type 설정
                    dataType: 'json',                   // 응답 데이터 타입 지정

                    /**
                     * 성공 콜백 함수
                     * @param {Object} data - 서버 응답 데이터
                     * @param {string} status - 요청 상태
                     * @param {jqXHR} xhr - XMLHttpRequest 객체
                     */
                    success: function(data, status, xhr) {
                        console.log("이메일 발송 응답:", data);
                        console.log("요청 상태:", status);
                        console.log("성공 플래그:", data.flag);
                        console.log("응답 메시지:", data.responseText);

                        // 서버에서 실패 응답인 경우
                        if(!data.flag){
                            alert(data.responseText);
                            return;
                        }

                        // 성공 시 사용자에게 알림 및 인증번호 입력 영역 표시
                        alert(data.responseText);
                        primaryKeyDiv.show();
                    },

                    /**
                     * 오류 콜백 함수
                     * @param {jqXHR} request - XMLHttpRequest 객체
                     * @param {string} status - 오류 상태
                     * @param {string} error - 오류 메시지
                     */
                    error: function(request, status, error) {
                        // 서버 오류 메시지를 사용자에게 표시
                        alert(request.responseText);
                    }
                })
            }
        });

        /**
         * 인증번호 확인 버튼 클릭 이벤트
         *
         * 처리 과정:
         * 1. 폼 유효성 검증
         * 2. 서버로 인증번호 확인 요청
         * 3. 성공 시 비밀번호 변경 영역 활성화
         * 4. 실패 시 오류 메시지 표시
         */
        checkAuthBtn.on('click', function() {
            if(changePasswordForm[0].checkValidity()) {
                $.ajax({
                    url: 'checkAuthCode.api',            // 인증번호 확인 API 엔드포인트
                    type: 'POST',
                    data: JSON.stringify({
                        "authCode": changePasswordForm.find('#primaryKey').val(),    // 사용자 입력 인증번호
                        "userId": changePasswordForm.find('#changeUserID').val(),   // 사용자 아이디
                    }),
                    contentType: 'application/json; charset=utf-8',
                    dataType: 'json',

                    /**
                     * 인증 성공 콜백 함수
                     */
                    success: function(data, status, xhr) {
                        console.log("인증번호 확인 응답:", data);
                        console.log("요청 상태:", status);
                        console.log("성공 플래그:", data.flag);
                        console.log("응답 메시지:", data.responseText);

                        if(!data.flag){
                            alert(data.responseText);
                            return;
                        }

                        // 인증 성공 시 UI 상태 업데이트
                        alert(data.responseText);
                        changePasswordBtn.prop('disabled', false);    // 비밀번호 변경 버튼 활성화
                        checkAuthFlag = true;                         // 인증 완료 플래그 설정
                        changePasswordDiv.show();                     // 비밀번호 변경 영역 표시
                    },

                    /**
                     * 인증 실패 콜백 함수
                     */
                    error: function(request, status, error) {
                        console.log("오류 코드:", request.status);
                        console.log("오류 메시지:", request.responseText);
                        console.log("오류 타입:", error);
                        alert(request.responseText);
                    }
                })
            }
        })

        // ========================================
        // 모달 관련 이벤트 핸들러
        // ========================================

        /**
         * 모달 닫기 이벤트 리스너
         * 모달이 닫힐 때 폼을 초기 상태로 리셋
         */
        changePasswordModal.on('hide.bs.modal', function (event) {
            resetForm();
        })

        /**
         * 폼 초기화 함수
         *
         * 수행 작업:
         * - 모든 입력 필드 값 초기화
         * - UI 요소 숨김/비활성화
         * - 상태 플래그 리셋
         */
        function resetForm() {
            changePasswordForm.find('#changeUserID').val('');     // 아이디 입력 필드 초기화
            changePasswordForm.find('#primaryKey').val('');       // 인증번호 입력 필드 초기화
            primaryKeyDiv.hide();                                 // 인증번호 입력 영역 숨김
            changePasswordBtn.prop('disabled', true);             // 비밀번호 변경 버튼 비활성화
            checkAuthFlag = false;                                // 인증 상태 플래그 리셋

            changePasswordDiv.hide();                             // 비밀번호 변경 영역 숨김
            changePassword.val('');                               // 새 비밀번호 필드 초기화
            changePasswordCheck.val('');                          // 비밀번호 확인 필드 초기화
            changePasswordResult.empty();                         // 유효성 검증 결과 메시지 초기화
        }

        // ========================================
        // 비밀번호 유효성 검증 관련 이벤트
        // ========================================

        /**
         * 비밀번호 확인 필드 입력 이벤트
         * 사용자가 입력할 때마다 실시간 유효성 검증 수행
         */
        changePasswordCheck.on('input', function() {
            changePasswordREG();
        })

        /**
         * 비밀번호 변경 버튼 클릭 이벤트
         *
         * 처리 과정:
         * 1. 비밀번호 유효성 검증
         * 2. 검증 통과 시 서버로 변경 요청 전송
         */
        changePasswordBtn.on('click', function() {
            if(!changePasswordREG()){
                return;  // 유효성 검증 실패 시 함수 종료
            }
            fetchChangePassword();  // 서버로 비밀번호 변경 요청
        })

        /**
         * 비밀번호 유효성 검증 함수
         *
         * 검증 항목:
         * 1. 인증 완료 여부 확인
         * 2. 공백 문자 포함 여부
         * 3. 비밀번호 복잡성 규칙 (정규식)
         * 4. 비밀번호 일치 여부
         *
         * @returns {boolean} 유효성 검증 통과 여부
         */
        function changePasswordREG(){
            let changePasswordVal = changePassword.val();          // 새 비밀번호 값
            let changePasswordCheckVal = changePasswordCheck.val(); // 비밀번호 확인 값

            // 비밀번호 복잡성 정규식: 소문자, 숫자, 특수문자 포함 5자 이상
            let reg = /^(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{5,}$/;

            // 1단계: 인증 완료 여부 확인
            if(!checkAuthFlag){
                alert("인증 완료 후 비밀번호 초기화가 가능합니다.");
                return false;
            }

            // 이전 검증 결과 메시지 초기화
            changePasswordResult.empty();

            console.log("비밀번호 일치 여부:", changePasswordVal === changePasswordCheckVal);

            // 2단계: 공백 문자 검증
            if (changePasswordVal.search(/\s/) !== -1) {
                passwordCheckInnerSpan(changePasswordResult, "비밀번호는 공백없이 부탁드립니다.", "red");
                return false;
            }
            // 3단계: 복잡성 규칙 검증
            else if (false === reg.test(changePasswordVal)) {
                passwordCheckInnerSpan(changePasswordResult, "비밀번호는 5자 이상이어야 하며, 숫자/소문자/특수문자(#?!@$%^&*-)를 모두 포함해야 합니다.", "red");
                return false;
            }
            // 4단계: 비밀번호 일치 검증
            else if (changePasswordVal === changePasswordCheckVal) {
                passwordCheckInnerSpan(changePasswordResult, "비밀번호가 정상적으로 입력되었습니다.", "green");
                return true;
            }
            else {
                passwordCheckInnerSpan(changePasswordResult, "동일한 비밀번호를 입력해주세요.", "red");
                return false;
            }
        }

        /**
         * 유효성 검증 결과 메시지 표시 함수
         *
         * @param {jQuery} innerDiv - 메시지를 삽입할 DOM 요소
         * @param {string} str - 표시할 메시지 텍스트
         * @param {string} color - 메시지 색상 (기본값: "red")
         */
        function passwordCheckInnerSpan(innerDiv, str, color = "red") {
            let className = (color === "green") ? "password-check-message is-success" : "password-check-message is-error";
            let spanHTML = "<span class=\"" + className + "\">" + str + "</span>";
            innerDiv.append(spanHTML);
        }

        // ========================================
        // 비밀번호 표시/숨김 토글 기능
        // ========================================

        /**
         * 새 비밀번호 표시/숨김 토글 이벤트
         */
        toggleChangePassword.on('click', function() {
            togglePasswordShow($(this), changePassword);
        })

        /**
         * 비밀번호 확인 표시/숨김 토글 이벤트
         */
        toggleChangePasswordCheck.on('click', function() {
            togglePasswordShow($(this), changePasswordCheck);
        })

        /**
         * 로그인 비밀번호 표시/숨김 토글 이벤트
         */
        iconPassword.on('click', function() {
            togglePasswordShow($(this), memberUserPW);
        })

        /**
         * 비밀번호 필드 표시/숨김 토글 함수
         *
         * 동작:
         * - password 타입 ↔ text 타입 전환
         * - Font Awesome 아이콘 변경 (fa-eye ↔ fa-eye-slash)
         *
         * @param {jQuery} $togglePassword - 토글 아이콘 요소
         * @param {jQuery} $password - 비밀번호 입력 필드
         */
        function togglePasswordShow($togglePassword, $password) {
            if ($password.attr('type') === 'password') {
                // 비밀번호 표시
                $password.attr('type', 'text');
                $togglePassword.removeClass('fa-eye-slash').addClass('fa-eye');
            } else {
                // 비밀번호 숨김
                $password.attr('type', 'password');
                $togglePassword.removeClass('fa-eye').addClass('fa-eye-slash');
            }
        }

        // ========================================
        // 비밀번호 변경 API 요청 함수
        // ========================================

        /**
         * 서버로 비밀번호 변경 요청을 전송하는 함수
         *
         * Fetch API를 사용하여 비동기 HTTP 요청 수행
         *
         * 처리 과정:
         * 1. 폼 데이터를 JSON으로 직렬화
         * 2. POST 요청으로 서버에 전송
         * 3. 응답 처리 (성공/실패)
         * 4. UI 상태 업데이트
         */
        function fetchChangePassword() {
            fetch('changePW.api', {
                method: 'POST',                          // HTTP POST 메서드
                headers: {
                    'Content-Type': 'application/json'   // 요청 헤더 설정
                },
                body: JSON.stringify({                   // 요청 본문을 JSON 문자열로 변환
                    "memberUserID": changePasswordForm.find('#changeUserID').val(),         // 사용자 아이디
                    "memberUserPW": changePasswordForm.find('#changePassword').val(),       // 새 비밀번호
                    "memberUserChangePW": changePasswordForm.find('#changePasswordCheck').val(), // 비밀번호 확인
                })
            })
                .then(async function(response) {
                    // 응답 본문을 텍스트로 변환
                    const responseData = await response.text();
                    console.log("서버 응답 데이터:", responseData);

                    let message;
                    try {
                        // JSON 형태 응답 파싱 시도
                        const jsonData = JSON.parse(responseData);
                        message = jsonData.message;
                    } catch (e) {
                        // JSON이 아닌 경우 원본 텍스트 사용
                        message = responseData;
                    }

                    if (response.ok) {
                        // HTTP 200-299 범위: 성공 응답
                        alert(message);

                        // 성공 시 UI 상태 업데이트
                        changePasswordResult.empty();
                        passwordCheckInnerSpan(changePasswordResult, "비밀번호가 정상적으로 변경되었습니다.", "green");

                        // 버튼들 비활성화 (재요청 방지)
                        sendMailBtn.prop('disabled', true);
                        checkAuthBtn.prop('disabled', true);
                        changePasswordBtn.prop('disabled', true);
                    } else {
                        // HTTP 400+ 범위: 클라이언트/서버 오류
                        alert(message);
                    }
                })
                .catch(function(error) {
                    // 네트워크 오류, 요청 실패 등의 예외 처리
                    console.log("요청 중 오류 발생:", error);
                    // 사용자에게 오류 알림은 주석 처리 (선택적)
                    // alert("서버 오류가 발생했습니다. 다시 시도해주세요.");
                })
        }
    });
</script>
</html>
