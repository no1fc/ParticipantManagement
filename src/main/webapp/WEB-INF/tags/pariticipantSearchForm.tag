<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="actionURL"%>


<!-- Search Section Card -->
<div class="col-12 card-modern p-4 mb-4">
    <div class="d-flex align-items-center justify-content-between mb-3">
        <h3 class="fw-bold text-dark m-0">참여자 조회</h3>
        <button id="excelDownload" class="btn btn-outline-success btn-sm rounded-pill px-3">
            <i class="bi bi-file-earmark-excel-fill me-1"></i>
            엑셀 다운로드
        </button>
    </div>
    <%-- 참여자 검색 시작 --%>
    <form class="row col-md-12 pt-3 pb-0 ms-auto me-auto" id="searchForm" name="searchForm" method="GET" action="${actionURL}">
        <input type="hidden" name="page" value="1">
        <!-- 검색 조건 선택 -->

        <div class="col-md-4 ms-auto d-flex justify-content-center pe-1">
            <select
                    class="form-select shadow-sm  w-75 me-2"
                    name="endDateOption"
                    id="endDate-Option"
            >
                <option ${param.endDateOption.equals("false") ? 'selected' : ''} value="false">진행중</option>
                <option ${param.endDateOption.equals("true") ? 'selected' : ''} value="true">마감</option>
                <option ${param.endDateOption.equals("allType") ? 'selected' : ''} value="allType">전체</option>
            </select>
            <select
                    class="form-select shadow-sm w-75 me-2"
                    name="participantInItCons"
                    id="year">
                <option ${param.participantInItCons.equals("All") ? 'selected' : ''} value="All">년도</option>
                <option ${param.participantInItCons.equals("2026") ? 'selected' : ''} value="2026">2026</option>
                <option ${param.participantInItCons.equals("2025") ? 'selected' : ''} value="2025">2025</option>
                <option ${param.participantInItCons.equals("2024") ? 'selected' : ''} value="2024">2024</option>
                <option ${param.participantInItCons.equals("2023") ? 'selected' : ''} value="2023">2023</option>
                <option ${param.participantInItCons.equals("2022") ? 'selected' : ''} value="2022">2022</option>
            </select>
            <select
                    class="form-select shadow-sm w-75 me-2"
                    name="participantPartType"
                    id="search-PartType"
            >
                <option ${param.participantPartType.equals("") ? 'selected' : ''} value="">유형</option>
                <option ${param.participantPartType.equals("1") ? 'selected' : ''} value="1">1유형</option>
                <option ${param.participantPartType.equals("2") ? 'selected' : ''} value="2">2유형</option>
            </select>
            <select
                    class="form-select shadow-sm"
                    name="searchOption"
                    id="search-Option"
            >
                <option ${param.searchOption.equals("참여자") ? 'selected' : ''} value="참여자">참여자</option>
                <c:if test="${branchManagementPageFlag}">
                    <option ${param.searchOption.equals("전담자") ? 'selected' : ''} value="전담자">전담자</option>
                </c:if>
                <option ${param.searchOption.equals("구직번호") ? 'selected' : ''} value="구직번호">구직번호</option>
                <option ${param.searchOption.equals("진행단계") ? 'selected' : ''} value="진행단계">진행단계</option>
                <option ${param.searchOption.equals("알선") ? 'selected' : ''} value="알선">알선요청</option>
            </select>
        </div>
        <!-- 검색 입력 -->
        <div id="searchTextDiv" class="col-md-6 ps-1">
            <input
                    type="text"
                    class="form-control shadow-sm"
                    id="search"
                    name="search"
                    placeholder="참여자를 입력해주세요."
                    value="${param.search}"
            />
        </div>
        <%-- 검색버튼 --%>
        <div class="col-md-1 text-center btn btn-secondary" id="searchBtn">
            검색<i class="bi bi-search"></i>
        </div>
        <div class="col-md-1 ps-1 pe-1 text-center me-auto">
            <select class="form-select shadow-sm" name="pageRows" id="pageRows">
                <option ${param.pageRows.equals("100") ? 'selected' : ''} value="100">100</option>
                <option ${param.pageRows.equals("90") ? 'selected' : ''} value="90">90</option>
                <option ${param.pageRows.equals("80") ? 'selected' : ''} value="80">80</option>
                <option ${param.pageRows.equals("70") ? 'selected' : ''} value="70">70</option>
                <option ${param.pageRows.equals("60") ? 'selected' : ''} value="60">60</option>
            </select>
        </div>
        <div class="navbar-expand mt-3">
            <ul class="navbar-nav w-75 ms-auto me-auto">
                <li class="nav-item d-none d-md-block w-auto btn-link ms-auto me-auto">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="searchType" id="allType" value="" ${(param.searchType.equals("") or param.searchType eq null) ? 'checked' : ''}>
                        <label class="form-check-label" for="allType">옵션 선택</label>
                    </div>
                </li>
                <li class="nav-item d-none d-md-block w-auto btn-link ms-auto me-auto">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="searchType" id="noInitialConsult" value="noInitial" ${param.searchType.equals("noInitial") ? 'checked' : ''}>
                        <label class="form-check-label" for="noInitialConsult">초기상담 미실시자</label>
                    </div>
                </li>
                <li class="nav-item d-none d-md-block w-auto btn-link ms-auto me-auto">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="searchType" id="recentConsult21" value="recent21" ${param.searchType.equals("recent21") ? 'checked' : ''}>
                        <label class="form-check-label" for="recentConsult21">최근상담일 21일</label>
                    </div>
                </li>
                <li class="nav-item d-none d-md-block w-auto btn-link ms-auto me-auto">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="searchType" id="jobExpire15" value="jobExpire" ${param.searchType.equals("jobExpire") ? 'checked' : ''}>
                        <label class="form-check-label" for="jobExpire15">구직 만료 15일 도래자</label>
                    </div>
                </li>
                <li class="nav-item d-none d-md-block w-auto btn-link ms-auto me-auto">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="searchType" id="periodExpire15" value="periodExpire" ${param.searchType.equals("periodExpire") ? 'checked' : ''}>
                        <label class="form-check-label" for="periodExpire15">기간 만료 15일 예정자</label>
                    </div>
                </li>
                <li class="nav-item d-none d-md-block w-auto btn-link ms-auto me-auto">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="searchType" id="employment" value="employment" ${param.searchType.equals("employment") ? 'checked' : ''}>
                        <label class="form-check-label" for="employment">취업자</label>
                    </div>
                </li>
                <li class="nav-item d-none d-md-block w-auto btn-link ms-auto me-auto">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="searchType" id="isIntesiveMediation" value="isIntesiveMediation" ${param.searchType.equals("isIntesiveMediation") ? 'checked' : ''}>
                        <label class="form-check-label" for="isIntesiveMediation">집중알선인원</label>
                    </div>
                </li>
            </ul>
        </div>
    </form>
    <div id="filter-tags"></div>
</div>
<!-- End Search Section Card -->
<%-- 참여자 검색 끝 --%>