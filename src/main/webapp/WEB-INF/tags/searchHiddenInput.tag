<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>

<%-- 업데이트 종료 후 이동될 페이지 --%>
<input type="hidden" name="page" value="${empty param.page ? '1' : param.page}">
<input type="hidden" name="endDateOption" value="${empty param.endDateOption ? '' : param.endDateOption}">
<c:forEach items="${paramValues.endDateOptionList}" var="ed">
    <input type="hidden" name="endDateOptionList" value="${ed}">
</c:forEach>
<input type="hidden" name="participantRegDate" value="${empty param.participantRegDate ? '' : param.participantRegDate}">
<input type="hidden" name="searchOption" value="${empty param.searchOption ? '' : param.searchOption}">
<input type="hidden" name="search" value="${empty param.search ? '' : param.search}">
<input type="hidden" name="searchType" value="${empty param.searchType ? '' : param.searchType}">
<c:forEach items="${paramValues.searchTypeList}" var="st">
    <input type="hidden" name="searchTypeList" value="${st}">
</c:forEach>
<input type="hidden" name="column" value="${empty param.column ? '' : param.column}">
<input type="hidden" name="order" value="${empty param.order ? '' : param.order}">
<input type="hidden" name="pageRows" value="${empty param.pageRows ? '100' : param.pageRows}">
<input type="hidden" name="participantRegDate" value="${empty param.participantRegDate ? 'All' : param.participantRegDate}">
<input type="hidden" name="participantInItCons" value="${empty param.participantInItCons ? 'All' : param.participantInItCons}">
<input type="hidden" name="participantPartType" value="${empty param.participantPartType ? '' : param.participantPartType}">
<c:forEach items="${paramValues.participantPartTypeList}" var="pt">
    <input type="hidden" name="participantPartTypeList" value="${pt}">
</c:forEach>
<input type="hidden" name="wishJobSearch" value="${empty param.wishJobSearch ? '' : param.wishJobSearch}">
<c:forEach items="${paramValues.wishJobSearchList}" var="wj">
    <input type="hidden" name="wishJobSearchList" value="${wj}">
</c:forEach>