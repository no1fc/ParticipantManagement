<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>

<%-- 업데이트 종료 후 이동될 페이지 --%>
<input type="hidden" name="page" value="${empty param.page ? '1' : param.page}">
<input type="hidden" name="endDateOption" value="${empty param.endDateOption ? '' : param.endDateOption}">
<input type="hidden" name="participantRegDate" value="${empty param.participantRegDate ? '' : param.participantRegDate}">
<input type="hidden" name="searchOption" value="${empty param.searchOption ? '' : param.searchOption}">
<input type="hidden" name="search" value="${empty param.search ? '' : param.search}">
<input type="hidden" name="searchType" value="${empty param.searchType ? '' : param.searchType}">
<input type="hidden" name="column" value="${empty param.column ? '' : param.column}">
<input type="hidden" name="order" value="${empty param.order ? '' : param.order}">
<input type="hidden" name="pageRows" value="${empty param.pageRows ? '100' : param.pageRows}">
<input type="hidden" name="participantRegDate" value="${empty param.participantRegDate ? 'All' : param.participantRegDate}">
<input type="hidden" name="participantInItCons" value="${empty param.participantInItCons ? 'All' : param.participantInItCons}">
<input type="hidden" name="participantPartType" value="${empty param.participantPartType ? '' : param.participantPartType}">