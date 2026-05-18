<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 회원가입</title>
    <mytag:Logo/>
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <!-- SweetAlert2 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <link rel="stylesheet" href="/css/participantCss/loginPage_0.0.1.css">
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css">
    <style>
        body {
            background: linear-gradient(135deg, #f3f5f9 0%, #c3cfe2 100%);
        }
        .register-container {
            max-width: 500px;
            width: 100%;
        }
        .validation-message {
            font-size: 0.85rem;
            margin-top: 0.25rem;
        }
        .is-success { color: green; }
        .is-error { color: red; }
    </style>
</head>
<body>
<div class="container d-flex w-100 h-100 justify-content-center align-items-center min-vh-100">
    <div class="register-container">
        <div class="login-box p-5">
            <div class="login-brand">
                <img src="/img/JobmoaLogo.svg" alt="JobMoa Logo" class="login-logo">
            </div>
            <h2 class="text-center mb-4 fw-bold login-title">회원가입</h2>
            <form id="registerForm">
                <!-- 이름 -->
                <div class="mb-3">
                    <label for="memberUserName" class="form-label fw-bold">이름 <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fa fa-user"></i></span>
                        <input type="text" class="form-control" id="memberUserName" name="memberUserName"
                               placeholder="이름을 입력해주세요" required>
                    </div>
                </div>

                <!-- 아이디 -->
                <div class="mb-3">
                    <label for="memberUserID" class="form-label fw-bold">아이디 <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fa fa-id-card"></i></span>
                        <input type="text" class="form-control" id="memberUserID" name="memberUserID"
                               placeholder="다우오피스 아이디를 입력해주세요" required>
                        <button type="button" class="btn btn-outline-primary" id="checkDuplicateBtn">중복확인</button>
                    </div>
                    <div id="idCheckResult" class="validation-message"></div>
                </div>

                <!-- 비밀번호 -->
                <div class="mb-3">
                    <label for="memberUserPW" class="form-label fw-bold">비밀번호 <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fa fa-lock"></i></span>
                        <input type="password" class="form-control" id="memberUserPW" name="memberUserPW"
                               placeholder="비밀번호를 입력해주세요" required>
                        <span class="input-group-text" style="cursor:pointer;">
                            <i class="fas fa-eye-slash" id="togglePW"></i>
                        </span>
                    </div>
                    <small class="text-muted">5자 이상, 소문자/숫자/특수문자(#?!@$%^&*-) 포함</small>
                </div>

                <!-- 비밀번호 확인 -->
                <div class="mb-3">
                    <label for="memberUserPWConfirm" class="form-label fw-bold">비밀번호 확인 <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fa fa-lock"></i></span>
                        <input type="password" class="form-control" id="memberUserPWConfirm"
                               placeholder="비밀번호를 다시 입력해주세요" required>
                        <span class="input-group-text" style="cursor:pointer;">
                            <i class="fas fa-eye-slash" id="togglePWConfirm"></i>
                        </span>
                    </div>
                    <div id="pwMatchResult" class="validation-message"></div>
                </div>

                <!-- 지점 -->
                <div class="mb-3">
                    <label for="memberBranch" class="form-label fw-bold">지점 <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fa fa-building"></i></span>
                        <select class="form-select" id="memberBranch" name="memberBranch" required>
                            <option value="">지점을 선택해주세요</option>
                            <c:forEach var="branch" items="${branchList}">
                                <option value="${branch.branchName}">${branch.branchName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <!-- 이메일 -->
                <div class="mb-3">
                    <label for="memberEmail" class="form-label fw-bold">이메일</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fa fa-envelope"></i></span>
                        <input type="email" class="form-control" id="memberEmail" name="memberEmail"
                               placeholder="다우오피스 이메일을 입력해주세요">
                    </div>
                </div>

                <!-- 전화번호 -->
                <div class="mb-3">
                    <label for="memberPhoneNumber" class="form-label fw-bold">전화번호</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fa fa-phone"></i></span>
                        <input type="text" class="form-control" id="memberPhoneNumber" name="memberPhoneNumber"
                               placeholder="010-1234-5678">
                    </div>
                </div>

                <div class="d-grid gap-2 mb-3">
                    <button type="button" class="btn btn-primary btn-lg" id="registerBtn">회원가입</button>
                </div>
                <div class="text-center">
                    <a href="/login.do" class="text-decoration-none text-primary fw-bold">
                        로그인으로 돌아가기
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
<script src="/js/register_0.0.1.js"></script>
</html>
