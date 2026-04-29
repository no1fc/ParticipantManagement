<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 25. 9. 30.
  Time: 오후 2:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="이력서 요청 목록 관리 페이지">
  <title>이력서 요청 목록 - 국민취업지원제도</title>
  <mytag:Logo/>

  <!-- Preconnect for performance -->
  <link rel="preconnect" href="https://cdn.jsdelivr.net">

  <!-- Bootstrap 5 CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">

  <!-- Google Fonts -->
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;600;700&display=swap" rel="stylesheet">

  <!-- Custom Style CSS -->
  <link href="/css/jobPlacementCss/jobPlacementDefault_0.0.1.css" rel="stylesheet">
  <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css" />
  <link href="/css/participantCss/resumeRequestList_0.0.2.css" rel="stylesheet">

  <!-- jQuery JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

  <!-- Custom JS -->
  <script src="/js/resumeRequestListJS_0.0.1.js"></script>

  <!-- Tailwind CSS -->
  <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
</head>
<body>
<mytag:jobPlacementView-header/>

<!-- Main Content -->
<main id="main-content" role="main" class="container">
  <!-- Breadcrumb -->
  <nav aria-label="breadcrumb" class="breadcrumb-nav">
    <ol class="breadcrumb">
      <li class="breadcrumb-item">
        <a href="/jobPlacement/placementList">
          <i class="bi bi-house"></i> 청년잡스케어
        </a>
      </li>
        <li class="breadcrumb-item">
            <a href="http://localhost:8088/">
                <i class="bi bi-house"></i> 참여자관리사이트
            </a>
        </li>
    </ol>
  </nav>

  <div class="main-content">
    <!-- 페이지 헤더 -->
    <div class="pageHeader" id="pageHeader">
      <div>
        <h2 class="h4 mb-1">이력서 요청 목록</h2>
        <p class="text-muted mb-0">기업에서 요청한 이력서 목록을 관리하세요.</p>
      </div>
      <div class="sub-title">
        <button type="button" class="btn btn-modern btn-primary" id="refreshBtn">
          <i class="bi bi-arrow-clockwise"></i> 새로고침
        </button>
      </div>
    </div>

    <c:set var="statusRequest" value="0" />
    <c:set var="statusScheduled" value="0" />
    <c:set var="statusRejected" value="0" />
    <c:set var="statusDone" value="0" />
    <c:forEach items="${resumeRequestList}" var="statusItem">
      <c:if test="${statusItem.status eq '요청'}">
        <c:set var="statusRequest" value="${statusRequest + 1}" />
      </c:if>
      <c:if test="${statusItem.status eq '예정'}">
        <c:set var="statusScheduled" value="${statusScheduled + 1}" />
      </c:if>
      <c:if test="${statusItem.status eq '거부'}">
        <c:set var="statusRejected" value="${statusRejected + 1}" />
      </c:if>
      <c:if test="${statusItem.status eq '완료'}">
        <c:set var="statusDone" value="${statusDone + 1}" />
      </c:if>
    </c:forEach>

    <div class="status-summary">
      <span class="status-chip status-total">전체 ${fn:length(resumeRequestList)}</span>
      <span class="status-chip status-request">요청 ${statusRequest}</span>
      <span class="status-chip status-scheduled">예정 ${statusScheduled}</span>
      <span class="status-chip status-rejected">거부 ${statusRejected}</span>
      <span class="status-chip status-done">완료 ${statusDone}</span>
    </div>

    <div class="filter-tags">
      <c:if test="${not empty param.status}">
        <span class="filter-tag">상태: ${param.status}</span>
      </c:if>
      <c:if test="${not empty param.companyName}">
        <span class="filter-tag">기업명: ${param.companyName}</span>
      </c:if>
      <c:if test="${not empty param.participant}">
        <span class="filter-tag">참여자명: ${param.participant}</span>
      </c:if>
      <c:if test="${not empty param.status or not empty param.companyName or not empty param.participant}">
        <a class="filter-reset" href="/jobPlacement/placementList">전체 해제</a>
      </c:if>
    </div>

    <!-- 필터 섹션 -->
    <div class="card-modern mb-4">
      <div class="card-header">
        <h3 class="card-title">
          <i class="bi bi-funnel"></i>
          검색 필터
        </h3>
      </div>
      <div class="card-body">
        <form id="filterForm" class="row g-3">
          <div class="col-md-3">
            <label for="statusFilter" class="form-label">상태</label>
            <select class="form-select" id="statusFilter" name="status">
              <option value="">전체</option>
              <option value="요청">요청</option>
              <option value="예정">예정</option>
              <option value="거부">거부</option>
              <option value="완료">완료</option>
            </select>
          </div>
          <div class="col-md-3">
            <label for="companyNameFilter" class="form-label">기업명</label>
            <input type="text" class="form-control" id="companyNameFilter" name="companyName" placeholder="기업명 검색">
          </div>
          <div class="col-md-3">
            <label for="participantFilter" class="form-label">참여자명</label>
            <input type="text" class="form-control" id="participantFilter" name="participant" placeholder="참여자명 검색">
          </div>
          <div class="col-md-3">
            <label class="form-label">&nbsp;</label>
            <div class="d-grid">
              <button type="submit" class="btn btn-primary">
                <i class="bi bi-search"></i> 검색
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>

    <!-- 이력서 요청 목록 테이블 -->
    <div class="card-modern">
      <div class="card-header">
        <h3 class="card-title">
          <i class="bi bi-list-ul"></i>
          이력서 요청 목록 (총 ${fn:length(resumeRequestList)}건)
        </h3>
      </div>
      <div class="card-body">
        <c:choose>
          <c:when test="${empty resumeRequestList}">
            <div class="text-center py-5">
              <i class="bi bi-inbox display-1 text-muted"></i>
              <h5 class="mt-3 text-muted">등록된 이력서 요청이 없습니다.</h5>
              <p class="text-muted">새로운 이력서 요청이 들어오면 여기에 표시됩니다.</p>
            </div>
          </c:when>
          <c:otherwise>
            <div class="table-responsive">
              <table class="table table-hover">
                <thead class="table-light">
                  <tr>
                    <th scope="col">번호</th>
                    <th scope="col">참여자명</th>
                    <th scope="col">구직번호</th>
                    <th scope="col">기업명</th>
                    <th scope="col">담당자명</th>
                    <th scope="col">이메일</th>
                    <th scope="col">연락처</th>
                    <th scope="col">요청일</th>
                    <th scope="col">상태</th>
                    <th scope="col">관리</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach items="${resumeRequestList}" var="request" varStatus="status">
                    <tr>
                      <td>${status.count}</td>
                      <td>
                        <strong>${request.participantPartic}</strong>
                        <c:if test="${not empty request.participantJobWant}">
                          <br><small class="text-muted">${request.participantJobWant}</small>
                        </c:if>
                      </td>
                      <td><code>${request.participantJobNo}</code></td>
                      <td>
                        <strong>${request.companyName}</strong>
                        <c:if test="${not empty request.contactOther}">
                          <br><small class="text-muted">기타: ${fn:substring(request.contactOther, 0, 20)}<c:if test="${fn:length(request.contactOther) > 20}">...</c:if></small>
                        </c:if>
                      </td>
                      <td>${request.contactName}</td>
                      <td>${request.contactEmail}</td>
                      <td>${request.contactPhone}</td>
                      <td>${request.registrationDate}</td>
                      <td>
                        <c:choose>
                          <c:when test="${request.status eq '요청'}">
                            <span class="badge bg-warning text-dark">${request.status}</span>
                          </c:when>
                          <c:when test="${request.status eq '예정'}">
                            <span class="badge bg-success">${request.status}</span>
                          </c:when>
                          <c:when test="${request.status eq '거부'}">
                            <span class="badge bg-danger">${request.status}</span>
                          </c:when>
                          <c:when test="${request.status eq '완료'}">
                            <span class="badge bg-primary">${request.status}</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge bg-secondary">${request.status}</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>
                        <div class="btn-group-vertical btn-group-sm" role="group">
                            <c:choose>
                                <c:when test="${request.status eq '요청'}">
                                    <button type="button" class="btn btn-outline-success btn-sm status-update-btn"
                                            data-job-number="${request.participantJobNo}"
                                            data-company-name="${request.companyName}"
                                            data-registration-date="${request.registrationDate}"
                                            data-status="예정">
                                        <i class="bi bi-check-lg"></i> 예정
                                    </button>
                                    <button type="button" class="btn btn-outline-danger btn-sm status-update-btn"
                                            data-job-number="${request.participantJobNo}"
                                            data-company-name="${request.companyName}"
                                            data-registration-date="${request.registrationDate}"
                                            data-status="거부">
                                        <i class="bi bi-x-lg"></i> 거부
                                    </button>
                                </c:when>
                                <c:when test="${request.status eq '예정'}">
                                    <button type="button" class="btn btn-outline-primary btn-sm status-update-btn"
                                            data-job-number="${request.participantJobNo}"
                                            data-company-name="${request.companyName}"
                                            data-registration-date="${request.registrationDate}"
                                            data-status="완료">
                                        <i class="bi bi-check2-all"></i> 완료
                                    </button>
                                </c:when>
                            </c:choose>
                          <a href="/jobPlacement/placementDetail?jobNumber=${request.participantJobNo}"
                             class="btn btn-outline-info btn-sm" target="_blank">
                            <i class="bi bi-eye"></i> 상세
                          </a>
                        </div>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>

            <!-- 페이지네이션 -->
            <c:if test="${totalButton > 1}">
              <nav aria-label="페이지 네비게이션">
                <ul class="pagination justify-content-center">
                  <c:if test="${startButton > 1}">
                    <li class="page-item">
                      <a class="page-link" href="?page=1" aria-label="첫 페이지">
                        <span aria-hidden="true">&laquo;&laquo;</span>
                      </a>
                    </li>
                    <li class="page-item">
                      <a class="page-link" href="?page=${startButton - 1}" aria-label="이전">
                        <span aria-hidden="true">&laquo;</span>
                      </a>
                    </li>
                  </c:if>

                  <c:forEach begin="${startButton}" end="${endButton}" var="i">
                    <li class="page-item ${i == page ? 'active' : ''}">
                      <a class="page-link" href="?page=${i}">${i}</a>
                    </li>
                  </c:forEach>

                  <c:if test="${endButton < totalButton}">
                    <li class="page-item">
                      <a class="page-link" href="?page=${endButton + 1}" aria-label="다음">
                        <span aria-hidden="true">&raquo;</span>
                      </a>
                    </li>
                    <li class="page-item">
                      <a class="page-link" href="?page=${totalButton}" aria-label="마지막 페이지">
                        <span aria-hidden="true">&raquo;&raquo;</span>
                      </a>
                    </li>
                  </c:if>
                </ul>
              </nav>
            </c:if>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>
</main>

<!-- Toast Container -->
<div class="toast-container position-fixed top-0 end-0 p-3" id="toastContainer"></div>

<!-- 상태 변경 확인 모달 -->
<div class="modal fade" id="statusUpdateModal" tabindex="-1" aria-labelledby="statusUpdateModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="statusUpdateModalLabel">상태 변경 확인</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <p id="statusUpdateMessage">정말로 상태를 변경하시겠습니까?</p>
        <div class="alert alert-info">
          <strong>참여자:</strong> <span id="modalParticipant"></span><br>
          <strong>기업명:</strong> <span id="modalCompany"></span><br>
          <strong>변경할 상태:</strong> <span id="modalNewStatus"></span>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
        <button type="button" class="btn btn-primary" id="confirmStatusUpdate">확인</button>
      </div>
    </div>
  </div>
</div>

</body>
</html>
