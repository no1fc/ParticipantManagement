<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JobMoa | Portal</title>
    <mytag:Logo/>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans+KR:wght@300;400;500;600;700&family=Space+Grotesk:wght@500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/css/participantCss/index.css">
</head>
<body>
    <main class="shell">
        <section class="hero">
            <h1>JobMoa 통합 포털</h1>
        </section>

        <section class="cards">
            <article class="card">
                <span class="tag">상담사용</span>
                <h2 class="card-title">참여자 관리 페이지</h2>
                <ul>
                    <li>참여자 현황 조회 및 관리</li>
                    <li>상담/취업 정보 업데이트</li>
                    <li>보고서 및 엑셀 다운로드</li>
                </ul>
                <a class="cta" href="/login.do">참여자 관리 페이지 이동</a>
            </article>

            <article class="card enterprise">
                <span class="tag">기업용</span>
                <h2 class="card-title">기업용 참여자 확인</h2>
                <ul>
                    <li>알선 요청 참여자 확인</li>
                    <li>참여자 상세정보 확인</li>
                </ul>
                <a class="cta" href="/jobPlacement/placementList">기업용 페이지로 이동</a>
            </article>

            <article class="card recruitmentInformation">
                <span class="tag">참여자용</span>
                <h2 class="card-title">채용공고 확인(고용24)</h2>
                <ul>
                    <li>고용 24에 올라온 채용공고를 검색</li>
                    <li>필요한 채용공고를 한번에 확인</li>
                </ul>
                <a class="cta" href="/jobinfo">참여자용 페이지로 이동</a>
            </article>
        </section>
    </main>
</body>
</html>
