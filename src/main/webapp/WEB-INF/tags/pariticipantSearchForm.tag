<%--
  pariticipantSearchForm.tag - 참여자 검색 폼 태그
  참여자 목록 페이지 상단에 표시되는 통합 검색 폼을 렌더링한다. 참여자명/전담자/구직번호 검색,
  종료일/참여유형/검색유형 필터, 엑셀 다운로드 버튼 등을 포함하며, actionURL 속성으로 폼 제출 경로를 지정한다.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="actionURL"%>

<c:set var="joinedEndDateList" value="${fn:join(paramValues.endDateOptionList, ',')}" />
<c:set var="joinedPartTypeList" value="${fn:join(paramValues.participantPartTypeList, ',')}" />
<c:set var="joinedSearchTypeList" value="${fn:join(paramValues.searchTypeList, ',')}" />

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
    <form class="col-md-12 pt-3 pb-0 ms-auto me-auto" id="searchForm" name="searchForm" method="GET" action="${actionURL}">
        <input type="hidden" name="page" value="1">

        <%-- 1행: 검색 바 --%>
        <div class="d-flex align-items-center gap-2 mb-3">
            <select class="form-select shadow-sm" name="searchOption" id="search-Option" style="max-width: 130px;">
                <option ${param.searchOption.equals("참여자") ? 'selected' : ''} value="참여자">참여자</option>
                <c:if test="${branchManagementPageFlag}">
                    <option ${param.searchOption.equals("전담자") ? 'selected' : ''} value="전담자">전담자</option>
                </c:if>
                <option ${param.searchOption.equals("구직번호") ? 'selected' : ''} value="구직번호">구직번호</option>
                <option ${param.searchOption.equals("진행단계") ? 'selected' : ''} value="진행단계">진행단계</option>
                <option ${param.searchOption.equals("알선") ? 'selected' : ''} value="알선">알선요청</option>
            </select>
            <div id="searchTextDiv" class="flex-grow-1">
                <input type="text" class="form-control shadow-sm" id="search" name="search"
                       placeholder="참여자를 입력해주세요." value="${param.search}" />
            </div>
            <div class="btn btn-secondary text-nowrap" id="searchBtn">
                검색<i class="bi bi-search ms-1"></i>
            </div>
            <select class="form-select shadow-sm" name="pageRows" id="pageRows" style="max-width: 80px;">
                <option ${param.pageRows.equals("100") ? 'selected' : ''} value="100">100</option>
                <option ${param.pageRows.equals("90") ? 'selected' : ''} value="90">90</option>
                <option ${param.pageRows.equals("80") ? 'selected' : ''} value="80">80</option>
                <option ${param.pageRows.equals("70") ? 'selected' : ''} value="70">70</option>
                <option ${param.pageRows.equals("60") ? 'selected' : ''} value="60">60</option>
            </select>
        </div>

        <%-- 2행: 필터 --%>
        <div class="d-flex flex-wrap align-items-center gap-4 mb-3 py-2 px-3 bg-light rounded">
            <%-- 년도 (맨 앞) --%>
            <div class="d-flex align-items-center gap-2">
                <span class="text-nowrap small fw-bold text-muted">년도:</span>
                <select class="form-select form-select-sm shadow-sm" name="participantInItCons" id="year" style="width: auto;">
                    <option ${param.participantInItCons.equals("All") ? 'selected' : ''} value="All">전체</option>
                    <option ${param.participantInItCons.equals("2026") ? 'selected' : ''} value="2026">2026</option>
                    <option ${param.participantInItCons.equals("2025") ? 'selected' : ''} value="2025">2025</option>
                    <option ${param.participantInItCons.equals("2024") ? 'selected' : ''} value="2024">2024</option>
                    <option ${param.participantInItCons.equals("2023") ? 'selected' : ''} value="2023">2023</option>
                    <option ${param.participantInItCons.equals("2022") ? 'selected' : ''} value="2022">2022</option>
                </select>
            </div>

            <div class="vr"></div>

            <%-- 진행여부 --%>
            <div class="d-flex align-items-center gap-2">
                <span class="text-nowrap small fw-bold text-muted">진행여부:</span>
                <div class="form-check form-check-inline m-0">
                    <input class="form-check-input" type="checkbox" name="endDateOptionList"
                           id="endDateFalse" value="false"
                           ${fn:contains(joinedEndDateList, 'false') ? 'checked' : ''}>
                    <label class="form-check-label" for="endDateFalse">진행중</label>
                </div>
                <div class="form-check form-check-inline m-0">
                    <input class="form-check-input" type="checkbox" name="endDateOptionList"
                           id="endDateTrue" value="true"
                           ${fn:contains(joinedEndDateList, 'true') ? 'checked' : ''}>
                    <label class="form-check-label" for="endDateTrue">마감</label>
                </div>
            </div>

            <div class="vr"></div>

            <%-- 유형 --%>
            <div class="d-flex align-items-center gap-2">
                <span class="text-nowrap small fw-bold text-muted">유형:</span>
                <div class="form-check form-check-inline m-0">
                    <input class="form-check-input" type="checkbox" name="participantPartTypeList"
                           id="partType1" value="1"
                           ${fn:contains(joinedPartTypeList, '1') ? 'checked' : ''}>
                    <label class="form-check-label" for="partType1">1유형</label>
                </div>
                <div class="form-check form-check-inline m-0">
                    <input class="form-check-input" type="checkbox" name="participantPartTypeList"
                           id="partType2" value="2"
                           ${fn:contains(joinedPartTypeList, '2') ? 'checked' : ''}>
                    <label class="form-check-label" for="partType2">2유형</label>
                </div>
            </div>

            <div class="vr"></div>

            <%-- 희망직무 (모달 다중 선택) --%>
            <div class="d-flex align-items-center gap-2 flex-wrap">
                <span class="text-nowrap small fw-bold text-muted">희망직무:</span>
                <div id="wishJobSearchTags" class="d-flex flex-wrap gap-1">
                    <c:forEach items="${paramValues.wishJobSearchList}" var="wj">
                        <span class="badge bg-primary d-flex align-items-center gap-1 wish-job-tag" data-value="${wj}">
                            ${wj}
                            <i class="bi bi-x-lg" style="cursor:pointer; font-size:0.65rem;" onclick="removeWishJobTag(this)"></i>
                            <input type="hidden" name="wishJobSearchList" value="${wj}">
                        </span>
                    </c:forEach>
                </div>
                <button type="button" class="btn btn-outline-primary btn-sm" id="openWishJobSearchModal">
                    <i class="bi bi-plus-circle"></i> 추가
                </button>
            </div>
        </div>

        <%-- 3행: 옵션 체크박스 --%>
        <div class="d-flex flex-wrap align-items-center gap-4 mb-1">
            <span class="text-nowrap small fw-bold text-muted">옵션:</span>
            <div class="form-check form-check-inline m-0">
                <input class="form-check-input" type="checkbox" name="searchTypeList" id="noInitialConsult" value="noInitial" ${fn:contains(joinedSearchTypeList, 'noInitial') ? 'checked' : ''}>
                <label class="form-check-label" for="noInitialConsult">초기상담 미실시자</label>
            </div>
            <div class="form-check form-check-inline m-0">
                <input class="form-check-input" type="checkbox" name="searchTypeList" id="recentConsult21" value="recent21" ${fn:contains(joinedSearchTypeList, 'recent21') ? 'checked' : ''}>
                <label class="form-check-label" for="recentConsult21">최근상담일 21일</label>
            </div>
            <div class="form-check form-check-inline m-0">
                <input class="form-check-input" type="checkbox" name="searchTypeList" id="jobExpire15" value="jobExpire" ${fn:contains(joinedSearchTypeList, 'jobExpire') ? 'checked' : ''}>
                <label class="form-check-label" for="jobExpire15">구직 만료 15일 도래자</label>
            </div>
            <div class="form-check form-check-inline m-0">
                <input class="form-check-input" type="checkbox" name="searchTypeList" id="periodExpire15" value="periodExpire" ${fn:contains(joinedSearchTypeList, 'periodExpire') ? 'checked' : ''}>
                <label class="form-check-label" for="periodExpire15">기간 만료 15일 예정자</label>
            </div>
            <div class="form-check form-check-inline m-0">
                <input class="form-check-input" type="checkbox" name="searchTypeList" id="employment" value="employment" ${fn:contains(joinedSearchTypeList, 'employment') ? 'checked' : ''}>
                <label class="form-check-label" for="employment">취업자</label>
            </div>
            <div class="form-check form-check-inline m-0">
                <input class="form-check-input" type="checkbox" name="searchTypeList" id="isIntesiveMediation" value="isIntesiveMediation" ${fn:contains(joinedSearchTypeList, 'isIntesiveMediation') ? 'checked' : ''}>
                <label class="form-check-label" for="isIntesiveMediation">집중알선인원</label>
            </div>
        </div>
    </form>
    <div id="filter-tags" class="mt-2"></div>
</div>
<!-- End Search Section Card -->

<%-- 희망직무 검색 모달 --%>
<div class="modal fade" id="wishJobSearchModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-briefcase-fill text-primary"></i> 희망 직무 검색
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
            </div>
            <div class="modal-body p-0">
                <div class="wj-modal-search">
                    <i class="bi bi-search"></i>
                    <input type="text" id="wjSearchModalInput" class="form-control" placeholder="직종명 검색...">
                </div>
                <div class="wj-modal-columns" id="wjSearchModalColumns">
                    <div class="wj-col wj-col-large" id="wjSearchColLarge"></div>
                    <div class="wj-col wj-col-mid" id="wjSearchColMid">
                        <div class="wj-col-placeholder">대분류를 선택하세요</div>
                    </div>
                    <div class="wj-col wj-col-sub" id="wjSearchColSub">
                        <div class="wj-col-placeholder">중분류를 선택하세요</div>
                    </div>
                </div>
                <div class="wj-modal-search-results" id="wjSearchModalResults" style="display:none;"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary btn-sm" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary btn-sm" id="wjSearchModalConfirm" disabled>선택 확인</button>
            </div>
        </div>
    </div>
</div>
<%-- 참여자 검색 끝 --%>