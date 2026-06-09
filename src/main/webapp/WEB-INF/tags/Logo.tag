<%--
  Logo.tag - 파비콘 및 CDN 프리커넥트 설정 태그
  브라우저 탭에 표시되는 Jobmoa 로고 파비콘과 외부 CDN(jsdelivr, cloudflare, jquery)
  DNS prefetch/preconnect 링크를 출력한다. 모든 페이지의 <head> 영역에서 공통으로 사용된다.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>
<!-- Jobmoa 로고 탭 이미지 -->
<link rel="icon" href="<c:url value="/img/JobmoaLogo.svg"/>"/>
<link rel="apple-touch-icon" href="<c:url value="/img/JobmoaLogo.svg"/>"/>
<!-- CDN preconnect / dns-prefetch (LCP 최적화) -->
<link rel="dns-prefetch" href="//cdn.jsdelivr.net">
<link rel="dns-prefetch" href="//cdnjs.cloudflare.com">
<link rel="dns-prefetch" href="//code.jquery.com">
<link rel="preconnect" href="https://cdn.jsdelivr.net" crossorigin>
<link rel="preconnect" href="https://cdnjs.cloudflare.com" crossorigin>
<link rel="preconnect" href="https://code.jquery.com" crossorigin>