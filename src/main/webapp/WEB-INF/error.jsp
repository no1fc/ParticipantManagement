<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--
  공통 에러 페이지 (업무형) - Spring Boot BasicErrorController의 "error" 뷰
  - /WEB-INF/error.jsp 로 해석됨(spring.mvc.view.prefix=/WEB-INF/, suffix=.jsp)
  - Spring Boot 에러 모델(status/path/timestamp) 우선, 없으면 서블릿 에러 속성 폴백
  - 안전 정보만 표시(스택/예외메시지 미노출). 요청 유래 값은 c:out으로 이스케이프(반사형 XSS 방지)
--%>
<jsp:useBean id="now" class="java.util.Date" />
<c:set var="sc" value="${not empty status ? status : requestScope['jakarta.servlet.error.status_code']}" />
<c:set var="uri" value="${not empty path ? path : requestScope['jakarta.servlet.error.request_uri']}" />
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>오류 발생 - 잡모아</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <link rel="icon" href="/img/JobmoaLogo.svg"/>
    <link rel="apple-touch-icon" href="/img/JobmoaLogo.svg"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <link rel="stylesheet" href="/css/participantCss/error_0.0.2.css">
</head>
<body>
<div class="error-wrap">
    <div class="error-card">
        <div class="error-accent"></div>
        <div class="error-body">
            <div class="error-code">
                <c:choose>
                    <c:when test="${not empty sc}"><c:out value="${sc}"/></c:when>
                    <c:otherwise>오류</c:otherwise>
                </c:choose>
            </div>
            <h1 class="error-title">
                <c:choose>
                    <c:when test="${sc == 403}">접근 권한이 없습니다</c:when>
                    <c:when test="${sc == 404}">페이지를 찾을 수 없습니다</c:when>
                    <c:otherwise>일시적인 오류가 발생했습니다</c:otherwise>
                </c:choose>
            </h1>
            <p class="error-desc">
                <c:choose>
                    <c:when test="${sc == 403}">이 페이지에 접근할 권한이 없습니다. 권한이 필요하시면 담당자에게 문의해 주세요.</c:when>
                    <c:when test="${sc == 404}">요청하신 페이지가 존재하지 않거나 이동되었습니다. 주소를 다시 확인해 주세요.</c:when>
                    <c:otherwise>잠시 후 다시 시도해 주세요. 문제가 계속되면 아래 정보로 담당자에게 신고해 주시면 빠르게 확인하겠습니다.</c:otherwise>
                </c:choose>
            </p>

            <%-- 오류 신고용 안전 정보 (상태코드/요청경로/발생시각/참조번호) --%>
            <div class="error-info">
                <div class="error-info-row">
                    <span class="error-info-key">상태코드</span>
                    <span class="error-info-val" id="info-code"><c:out value="${not empty sc ? sc : '-'}"/></span>
                </div>
                <div class="error-info-row">
                    <span class="error-info-key">요청경로</span>
                    <span class="error-info-val" id="info-uri"><c:out value="${not empty uri ? uri : '-'}"/></span>
                </div>
                <div class="error-info-row">
                    <span class="error-info-key">발생시각</span>
                    <span class="error-info-val" id="info-time"><fmt:formatDate value="${now}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
                </div>
                <div class="error-info-row">
                    <span class="error-info-key">참조번호</span>
                    <span class="error-info-val" id="info-ref">ERR-<fmt:formatDate value="${now}" pattern="yyyyMMddHHmmss"/></span>
                </div>
            </div>

            <div class="error-actions">
                <button type="button" class="error-btn error-btn-outline" onclick="history.back()"><i class="fa fa-arrow-left"></i> 이전 페이지</button>
                <a class="error-btn error-btn-outline" href="/index.jsp"><i class="fa fa-house"></i> 메인 페이지</a>
                <a class="error-btn error-btn-outline" href="/jobPlacement/"><i class="fa fa-briefcase"></i> 알선 페이지</a>
                <button type="button" class="error-btn error-btn-primary" id="btn-report"><i class="fa fa-paper-plane"></i> 오류 신고</button>
            </div>
        </div>
    </div>
</div>

<script>
    $(function () {
        // 담당자 메일로 오류 정보를 자동 작성하여 신고
        const email = "namsd@jobmoa.com";
        const code = $("#info-code").text();
        const uri = $("#info-uri").text();
        const time = $("#info-time").text();
        const ref = $("#info-ref").text();

        $("#btn-report").on("click", function () {
            const subject = "[잡모아 오류신고] " + ref;
            let body = "";
            body += "아래 오류가 발생하여 신고드립니다. (이름/지점/문의사항을 작성해 주시면 빠른 처리가 가능합니다)\n\n";
            body += "이름 : \n";
            body += "지점 : \n\n";
            body += "----- 오류 정보 -----\n";
            body += "참조번호 : " + ref + "\n";
            body += "상태코드 : " + code + "\n";
            body += "요청경로 : " + uri + "\n";
            body += "발생시각 : " + time + "\n";
            body += "----------------------\n\n";
            body += "문의사항 : \n";
            location.href = "mailto:" + email + "?subject=" + encodeURIComponent(subject) + "&body=" + encodeURIComponent(body);
        });
    });
</script>
</body>
</html>
