<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404Error 발생</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <link rel="icon" href="/img/JobmoaLogo.svg"/>
    <link rel="apple-touch-icon" href="/img/JobmoaLogo.svg"/>


    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <link rel="stylesheet" href="/css/participantCss/error_0.0.1.css">
</head>
<body>
<div class="page-error">
    <div class="outer">
        <div class="middle">
            <div class="inner">
                <!--BEGIN CONTENT-->
                <div class="inner-circle"><i class="fa fa-cogs"></i><span>404</span></div>
                <span class="inner-status">페이지를 찾지 못했습니다!</span>
                <span class="inner-detail pointer" onclick="history.back()">이전 페이지로 돌아가기.</span>
                <span class="inner-detail"><a href="/index.jsp">메인 페이지로 이동</a></span>
                <span class="inner-detail"><a href="/jobPlacement/">알선 페이지로 이동</a></span>
                <!--END CONTENT-->
            </div>
        </div>
    </div>
</div>
</body>
</html>
