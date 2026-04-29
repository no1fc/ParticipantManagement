<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="스타벅스 희망직무 참여자 목록">
    <title>스타벅스 희망직무 참여자 - 잡모아</title>
    <mytag:Logo/>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"
            integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo="
            crossorigin="anonymous"></script>
    <link rel="preconnect" href="https://cdn.jsdelivr.net"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;600;700&display=swap" rel="stylesheet"/>
    <link href="/css/jobPlacementCss/jobPlacementDefault_0.0.1.css" rel="stylesheet"/>
</head>
<body>
<a href="#main-content" class="skip-link">메인 콘텐츠로 바로가기</a>

<mytag:jobPlacementView-header/>

<main id="main-content" role="main" class="container">
    <div class="main-content">

        <!-- 페이지 헤더 -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="h4 mb-1">슈퍼바이저 / 바리스타 희망 참여자</h2>
                <p class="text-muted mb-0">
                    희망직무가 <strong>슈퍼바이저 / 바리스타 / Supervisor / Barista</strong>인 참여자 목록입니다.
                </p>
            </div>
            <div class="d-flex align-items-center gap-3">
                <select class="form-select" id="countFilter" name="pageRows" style="width:auto;">
                    <option value="10">10개씩</option>
                    <option value="20">20개씩</option>
                    <option value="30">30개씩</option>
                    <option value="50">50개씩</option>
                </select>
                <button type="button" class="btn btn-modern btn-primary" id="refreshListBtn">
                    <i class="bi bi-arrow-clockwise"></i> 새로고침
                </button>
            </div>
        </div>

        <!-- 참여자 목록 테이블 -->
        <div class="card-modern">
            <div class="table-responsive">
                <table class="table table-modern mb-0">
                    <thead>
                    <tr>
                        <th scope="col" style="width: 130px;">구직번호</th>
                        <th scope="col">이름</th>
                        <th scope="col">성별</th>
                        <th scope="col">나이</th>
                        <th scope="col">거주지</th>
                        <th scope="col">희망직무</th>
                        <th scope="col">희망연봉</th>
                        <th scope="col">자격증</th>
                        <th scope="col" style="width: 110px;">상세정보</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty starbucksDatas}">
                            <c:forEach items="${starbucksDatas}" var="data">
                                <tr>
                                    <td>${data.jobNumber}</td>
                                    <td>${fn:substring(data.participant, 0, 4)}</td>
                                    <td>${data.gender}</td>
                                    <c:choose>
                                        <c:when test="${not empty data.ageRangeContent}">
                                            <td>${data.ageRangeContent}</td>
                                        </c:when>
                                        <c:otherwise>
                                            <td>${data.age}</td>
                                        </c:otherwise>
                                    </c:choose>
                                    <td>${data.address}</td>
                                    <td>
                                        <c:forEach var="job" items="${data.desiredJobList}" varStatus="status">
                                            <c:if test="${not empty job.jobCategoryLarge}">${job.jobCategoryLarge} &gt; </c:if><c:if test="${not empty job.jobCategoryMid}">${job.jobCategoryMid} &gt; </c:if>${job.desiredJob}<c:if test="${!status.last}"><br/></c:if>
                                        </c:forEach>
                                    </td>
                                    <td>${data.desiredSalary}</td>
                                    <td>${data.certificate}</td>
                                    <td>
                                        <a href="#"
                                           class="btn btn-outline-primary btn-sm detailPageATag"
                                           title="상세보기">
                                            <i class="bi bi-eye"></i>
                                        </a>
                                        <input type="hidden" value="${data.jobNumber}" name="jobNumber">
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="9" style="font-size: 1.2em; text-align: center; padding: 2rem;">
                                    해당 희망직무 참여자가 없습니다.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- 페이지네이션 -->
        <nav class="col-md-11 text-center ms-auto me-auto d-flex justify-content-center mt-3">
            <ul class="pagination"></ul>
        </nav>

    </div>
</main>

<div class="toast-container" id="toastContainer"></div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="/js/paginationJS_0.0.1.js"></script>
<script>
    $(document).ready(function () {
        let page = parseInt("${page}", 10) || 1;
        page = (page === 0) ? 1 : page;
        const startButton = parseInt("${startButton}", 10) || 1;
        const endButton   = parseInt("${endButton}",   10) || 1;
        const totalButton = parseInt("${totalButton}", 10) || 0;
        paginationAddItems(page, startButton, endButton, totalButton);

        // 조회 개수 변경 시 페이지 1로 이동
        $('#countFilter').val('${param.pageRows}' === '' ? '10' : '${param.pageRows}');
        $('#countFilter').on('change', function () {
            window.location.href = '/Starbucks?page=1&pageRows=' + $(this).val();
        });

        // 새로고침
        $('#refreshListBtn').on('click', function () {
            let countVal = $('#countFilter').val();
            window.location.href = '/Starbucks?page=1&pageRows=' + countVal;
        });

        // 상세보기 이동
        $('.detailPageATag').on('click', function () {
            let jobNumber = $(this).parent().find('input[name="jobNumber"]').val();
            window.location.href = '/jobPlacement/placementDetail' + searchHref(page) + '&jobNumber=' + jobNumber + '&Starbucks=true';
        });
    });
</script>
</body>
</html>