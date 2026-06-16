<%--
  footer.tag - 페이지 하단 푸터 태그
  모든 페이지 하단에 표시되는 공통 푸터를 렌더링한다. 기업 전화번호,
  사이트 관리자 연락처 등의 정보를 포함한다.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>

<script defer src="/js/footerJS_0.0.2.js"></script>

<!--begin::Footer-->
<footer class="app-footer">
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
