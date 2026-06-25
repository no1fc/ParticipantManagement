<%--
  HR 로그인 (데모)
  Description: 입퇴사자관리(HR) 독립 로그인 페이지 (크롬리스 — hrGnb 미포함)
  인증: J_직원_계정(비밀번호) → J_직원_사이트접속(사이트코드='HR') 게이트 → 사이트내권한 역할
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>잡모아 HR - 로그인</title>
    <mytag:Logo/>

    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="author" content="JobMoa" />

    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.0.12/index.css"
          integrity="sha256-tXJfXfp6Ewt1ilPzLDtQnJV4hclT9XuaZUKyUvmyr+Q=" crossorigin="anonymous" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
          integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI=" crossorigin="anonymous" />
    <link rel="stylesheet" href="/css/adminlte.min.css" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script defer src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>

    <link rel="stylesheet" href="/css/hrCss/hrLogin_0.0.1.css">
</head>
<body class="hr-login-body">

<div class="hr-login-wrapper">
    <div class="hr-login-card">
        <div class="hr-login-header">
            <i class="bi bi-person-vcard"></i>
            <h1>입퇴사자관리<span>(HR)</span></h1>
            <p>인사 담당자 전용 시스템 <span class="hr-login-badge">데모</span></p>
        </div>

        <form id="hrLoginForm" class="hr-login-form" autocomplete="off">
            <div class="hr-login-field">
                <label for="userId">아이디</label>
                <div class="hr-login-input">
                    <i class="bi bi-person"></i>
                    <input type="text" id="userId" name="userId" placeholder="아이디를 입력하세요" required autofocus>
                </div>
            </div>

            <div class="hr-login-field">
                <label for="password">비밀번호</label>
                <div class="hr-login-input">
                    <i class="bi bi-lock"></i>
                    <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" required>
                    <button type="button" id="togglePassword" class="hr-login-eye" aria-label="비밀번호 표시">
                        <i class="bi bi-eye-slash" id="eyeIcon"></i>
                    </button>
                </div>
            </div>

            <button type="submit" class="hr-login-submit">
                <i class="bi bi-box-arrow-in-right"></i> 로그인
            </button>
        </form>

        <div class="hr-login-footer">
            <span>입퇴사자관리(HR) 접근 권한이 있는 직원만 로그인할 수 있습니다.</span>
        </div>
    </div>
    <p class="hr-login-copyright">Copyright &copy; 2024-2026 JobMoa. All rights reserved.</p>
</div>

<script defer src="/js/hrJs/hrLogin_0.0.1.js"></script>
</body>
</html>
