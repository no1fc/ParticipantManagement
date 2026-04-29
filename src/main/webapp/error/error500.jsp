<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500Error 발생</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <link rel="icon" href="/img/JobmoaLogo.svg"/>
    <link rel="apple-touch-icon" href="/img/JobmoaLogo.svg"/>

    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

    <link rel="stylesheet" href="/css/participantCss/error_0.0.1.css">

    <script>
        $(document).ready(function () {
            let APP_URL = window.location.href; // 현재 페이지 URL
            let today = new Date();
            let date = today.getDate();
            let month = today.getMonth() + 1;
            let year = today.getFullYear();
            if (month < 10) month = "0" + month;
            if (date < 10) date = "0" + date;
            date = year + "-" + month + "-" + date;

            let email = "namsd@jobmoa.com"; // 수신 이메일

            let subject = APP_URL + " 오류가 발생해 문의드립니다."; // 이메일 제목
            let body = "이름 : ";
            // 줄바꿈 추가
            body += "\n\n"; // 줄바꿈
            body += "지점 : "; // 지점
            body += "\n\n"; // 줄바꿈
            body += "발생일 : " + date; // 발생일
            body += "\n\n"; // 줄바꿈
            body += "문의 사항"; // 추가 설명
            body += "\n"; // 줄바꿈

            // 이메일 태그에 동적으로 href 속성 설정
            let emailTag = $("#email-a-tag");

            // mailto 링크 생성
            emailTag.on("click", function () {
                location.href = "mailto:" + email + "?subject=" + encodeURIComponent(subject) + "&body=" + encodeURIComponent(body);
            })
        });
    </script>
</head>
<body>
<div class="page-error">
    <div class="outer">
        <div class="middle">
            <div class="inner">
                <!--BEGIN CONTENT-->
                <div class="inner-circle"><i class="fa fa-cogs"></i><span>500</span></div>
                <span class="inner-status">Server Error!</span>
                <span class="inner-detail" id="email-a-tag">사이트 담당자에게 메일 전달</span>
                <span class="inner-detail"><a href="/index.jsp">메인 페이지로 이동</a></span>
                <span class="inner-detail"><a href="/jobPlacement/">알선 페이지로 이동</a></span>
                <!--END CONTENT-->
            </div>
        </div>
    </div>
</div>
</body>
</html>
