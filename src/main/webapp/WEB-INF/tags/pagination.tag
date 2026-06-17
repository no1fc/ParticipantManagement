<%--
  pagination.tag - 페이지네이션 UI 컴포넌트 태그
  목록 페이지 하단에 표시되는 페이지 번호 네비게이션을 렌더링한다. 현재 페이지, 시작/끝 버튼 번호,
  전체 페이지 수를 속성으로 받아 이전/다음/페이지 번호 링크를 생성한다.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="page"%>
<%@ attribute name="startButton"%>
<%@ attribute name="endButton"%>
<%@ attribute name="totalButton"%>

<%--
  page를 제외한 현재 요청 파라미터(검색·필터·정렬, 다중값 포함)를 전부 보존하는 base URL을 구성한다.
  페이지 이동 링크가 ?page=N만 붙도록 하여 검색필터 유실을 방지한다(<c:url>/<c:param>이 URL 인코딩 처리).
--%>
<c:url var="pageBase" value="">
    <c:forEach var="e" items="${paramValues}">
        <c:if test="${e.key ne 'page'}">
            <c:forEach var="v" items="${e.value}"><c:param name="${e.key}" value="${v}"/></c:forEach>
        </c:if>
    </c:forEach>
</c:url>
<c:set var="pageSep" value="${fn:contains(pageBase, '?') ? '&' : '?'}"/>

<%--  페이지네이션 시작 --%>
<div class="col-md-11 text-center ms-auto me-auto d-flex justify-content-center">
    <ul class="pagination">
        <li class="page-item ${page <= 10 ? 'disabled' : ''}">
            <a class="page-link" href="${pageBase}${pageSep}page=${page - 10}" ><i class="bi bi-chevron-double-left" style="font-size: 12px;"></i></a>
        </li>
        <li class="page-item ${page == 1 ? 'disabled' : ''}">
            <a class="page-link" href="${pageBase}${pageSep}page=${page - 1}"><i class="bi bi-chevron-compact-left" style="font-size: 12px;"></i></a>
        </li>
        <c:forEach var="i" begin="${startButton}" end="${endButton}">
            <li class="page-item ${i == page ? 'active' : ''}">
                <a class="page-link" href="${pageBase}${pageSep}page=${i}">${i}</a>
            </li>
        </c:forEach>
        <li class="page-item ${page == totalButton ? 'disabled' : ''}">
            <a class="page-link" href="${pageBase}${pageSep}page=${page + 1}"><i class="bi bi-chevron-compact-right" style="font-size: 12px;"></i></a>
        </li>
        <li class="page-item ${totalButton == endButton ? 'disabled' : ''}">
            <a class="page-link" href="${pageBase}${pageSep}page=${(endButton - (endButton % 1))+1}"><i class="bi bi-chevron-double-right" style="font-size: 12px;"></i></a>
        </li>
    </ul>
</div>
<%--페이지네이션 끝--%>