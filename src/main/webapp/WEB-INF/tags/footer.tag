<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>

<script src="/js/footerJS_0.0.1.js"></script>
<link href="/css/participantCss/footerCss_0.0.1.css" rel="stylesheet">

<!--begin::Footer-->
<footer class="app-footer">
<%--    <!-- iframe 모달 -->
    <div class="iframe-modal" id="iframeModal">
        <div class="iframe-container">
            <!-- 닫기 버튼 -->
            <button class="close-button" id="closeButton">X</button>

            <!-- iframe -->
            <iframe src="/chatBot/chatgpt" title="Chatbot"></iframe>
        </div>
    </div>

    <!-- 동그란 버튼 -->
    <div class="circle-button" id="circleButton">챗봇</div>--%>
    <!--begin::To the end-->
    <div class="float-end d-none d-sm-inline">기업 전화번호 : <a href="tel:02-2607-9119">02-2607-9119 (609)</a></div>
    <!--end::To the end-->
    <!--begin::Copyright-->
    <strong>
        사이트 관리자
<%--        <a href="mailto:namsd@jobmoa.com?subject=문의사항&body=문의사항 내용 작성" class="text-decoration-none email-a-tag">남상도</a>.--%>
        <a class="text-decoration-none" id="email-a-tag">남상도</a>.
    </strong>

    <!--end::Copyright-->
</footer>
<!--end::Footer-->
