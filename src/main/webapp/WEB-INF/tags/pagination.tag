<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="page"%>
<%@ attribute name="startButton"%>
<%@ attribute name="endButton"%>
<%@ attribute name="totalButton"%>


<%--  페이지네이션 시작 --%>
<div class="col-md-11 text-center ms-auto me-auto d-flex justify-content-center">
    <ul class="pagination">
        <li class="page-item ${page <= 10 ? 'disabled' : ''}">
            <a class="page-link" href="?page=${page - 10}" ><i class="bi bi-chevron-double-left" style="font-size: 12px;"></i></a>
        </li>
        <li class="page-item ${page == 1 ? 'disabled' : ''}">
            <a class="page-link" href="?page=${page - 1}"><i class="bi bi-chevron-compact-left" style="font-size: 12px;"></i></a>
        </li>
        <c:forEach var="i" begin="${startButton}" end="${endButton}">
            <li class="page-item ${i == page ? 'active' : ''}">
                <a class="page-link" href="?page=${i}">${i}</a>
            </li>
        </c:forEach>
        <li class="page-item ${page == totalButton ? 'disabled' : ''}">
            <a class="page-link" href="?page=${page + 1}"><i class="bi bi-chevron-compact-right" style="font-size: 12px;"></i></a>
        </li>
        <li class="page-item ${totalButton == endButton ? 'disabled' : ''}">
            <a class="page-link" href="?page=${(endButton - (endButton % 1))+1}"><i class="bi bi-chevron-double-right" style="font-size: 12px;"></i></a>
        </li>
    </ul>
</div>
<%--페이지네이션 끝--%>