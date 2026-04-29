<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 25. 6. 27.
  Time: 오후 2:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="국민취업지원제도 참여자 상세 정보 확인 페이지">
  <title>참여자 상세 정보 - 국민취업지원제도</title>
  <mytag:Logo/>

  <!-- Favicon -->
<%--  <link rel="icon" type="image/x-icon" href="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzIiIGhlaWdodD0iMzIiIHZpZXdCb3g9IjAgMCAzMiAzMiIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjMyIiBoZWlnaHQ9IjMyIiByeD0iNCIgZmlsbD0iIzI1NjNlYiIvPgo8cGF0aCBkPSJNOCAxMmg0djhoLTR2LTh6bTYgMGg0djhoLTR2LTh6bTYgMGg0djhoLTR2LTh6IiBmaWxsPSJ3aGl0ZSIvPgo8L3N2Zz4K">--%>

  <!-- Preconnect for performance -->
  <link rel="preconnect" href="https://cdn.jsdelivr.net">

  <!-- Bootstrap 5 CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">

  <!-- Google Fonts -->
  <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;600;700&display=swap" rel="stylesheet">

  <!-- Custom Style CSS  -->
  <link href="/css/jobPlacementCss/jobPlacementDefault_0.0.1.css" rel="stylesheet">

  <!-- Custom print Style CSS  -->
  <link href="/css/jobPlacementCss/companyDetailPrintCss_0.0.1.css" rel="stylesheet">


  <!-- jQuery JS  -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

  <!-- jobPlacementDetailJS -->
  <script src="/js/jobPlacementJs/jobPlacementDetailJS_0.0.4.js"></script>

  <!-- datepicker CSS JS -->
  <!-- mouse pointer 모양 bootstrap 5 -->
  <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

  <!-- Bootstrap Datepicker 로드 -->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/js/bootstrap-datepicker.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/locales/bootstrap-datepicker.ko.min.js" integrity="sha512-L4qpL1ZotXZLLe8Oo0ZyHrj/SweV7CieswUODAAPN/tnqN3PA1P+4qPu5vIryNor6HQ5o22NujIcAZIfyVXwbQ==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
  <script src="/js/jobPlacementJs/datepickerJS_0.0.1.js"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/css/bootstrap-datepicker.min.css">
  <link rel="stylesheet" href="/css/participantCss/datepicker_0.0.1.css">

  <!-- kakao 주소 API 호출 JS API 문서 주소 https://postcode.map.daum.net/guide#usage -->
  <script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>

  <!-- kakao 주소 API 호출 JS API 문서 주소 https://postcode.map.daum.net/guide#usage -->
  <script src="/js/jobPlacementJs/kakaoAddressAPIJS_0.0.1.js"></script>

  <!-- 카테고리 설정 jobPlacementDefault JS -->
  <script src="/js/jobCategorySelectRenderText_0.0.2.js"></script>

  <!-- selectOption JS -->
  <script src="/js/selectOptionJS_0.0.1.js"></script>

  <!-- Tailwind CSS -->
  <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>

  <!-- sweetalert2 -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
  <script src="/js/sweetAlert_0.0.1.js"></script>


</head>
<body>
<!-- Skip Navigation for Accessibility -->
<%--<a href="#main-content" class="skip-link">메인 콘텐츠로 바로가기</a>--%>

<mytag:jobPlacementView-header/>

<%--<mytag:jobPlacementView-nav pageController="detail"/>--%>

<!-- Main Content -->
<main id="main-content" role="main" class="container">
  <!-- Breadcrumb -->
  <nav aria-label="breadcrumb" class="breadcrumb-nav">
    <ol class="breadcrumb">
      <li class="breadcrumb-item">
        <a href="#" id="backToListATag">
          <i class="bi bi-list-ul"></i> 참여자 목록
        </a>
      </li>
      <li class="breadcrumb-item active" aria-current="page">
        <span id="currentParticipantName">${data.participant}</span> 상세정보
      </li>
    </ol>
  </nav>

  <div class="main-content detail-content" data-participant-info="참여자: ${data.participant} | 구직번호: ${data.jobNumber}">
    <!-- 페이지 헤더 -->
    <div class="pageHeader" id="pageHeader">
      <div>
        <h2 class="h4 mb-1">${data.participant} 상세 정보</h2>
        <p class="text-muted mb-0">선택된 참여자의 상담사정보를 확인하세요.<%--이력서와 자기소개서를 확인하세요.--%></p>
      </div>
      <div class="sub-title">
        <div class="text-muted">참여자 구직번호 : <strong id="selectedParticipantId">${data.jobNumber}</strong></div>
        <c:if test="${JOBMOA_LOGIN_DATA.memberUserID eq data.counselorId}">
          <button type="button"
                  class="btn btn-modern btn-info"
                  id="updateBtn">
            <i class="bi bi-pencil-square"></i> 수정
          </button>
        </c:if>
        <button type="button"
                class="btn btn-modern btn-secondary"
                id="printBtn">
          <i class="bi bi-printer"></i> 인쇄
        </button>
        <a href="#"
           id="backToListBtn"
           class="btn btn-modern btn-outline">
          <i class="bi bi-arrow-left"></i> 목록으로
        </a>
      </div>
    </div>

    <!-- 기본 정보 (읽기 전용) -->
    <div class="card-modern">
      <div class="card-header">
        <h3 class="card-title">
          <i class="bi bi-person-badge-fill"></i>
          기본 정보
        </h3>
      </div>
      <div class="card-body">
        <div class="readonly-section row">
          <div class="col-md-6 p-0 pe-1">
            <div class="detail-section">
              <div class="readonly-item">
                <input type="hidden" id="detailJobNumber" name="jobNumber" value="${data.jobNumber}">
                <input type="hidden" id="detailCounselorId" name="counselorId" value="${JOBMOA_LOGIN_DATA.memberUserID}">
                <span class="readonly-label">이름</span>
                <span class="readonly-value" id="detailName" name="participant" >${data.participant}</span>
              </div>
              <div class="readonly-item">
                <span class="readonly-label">(만)나이</span>
                  <span class="readonly-value" id="detailAge" name="age" >
                      <c:choose>
                          <c:when test="${data.ageRangeContent eq null || data.ageRangeContent eq ''}">
                              <td>${data.age}</td>
                          </c:when>
                          <c:otherwise>
                              <td>${data.ageRangeContent}</td>
                          </c:otherwise>
                      </c:choose>
                  </span>
                <input type="hidden" id="detailBirthDate" name="birthDate" value="${data.birthDate}">
              </div>
              <div class="readonly-item">
                <span class="readonly-label">성별</span>
                <span class="readonly-value" id="detailGender" name="gender" >${data.gender == null ? '비공개':data.gender}</span>
              </div>
              <div class="readonly-item">
                <span class="readonly-label">거주지</span>
                <span class="readonly-value" id="detailLocation" name="address" >${data.address}</span>
                <div id="layer" style="display:none;position:fixed;overflow:hidden;z-index:1;-webkit-overflow-scrolling:touch;">
                  <img src="//t1.daumcdn.net/postcode/resource/images/close.png" id="btnCloseLayer" style="cursor:pointer;position:absolute;right:-3px;top:-3px;z-index:1" alt="닫기 버튼">
                </div>
              </div>
              <!-- 읽기 모드: 전체 희망직무 표시 -->
              <div class="readonly-item" id="mergedDesiredJobSection" style="justify-content: start;">
                <span class="readonly-label">희망직종</span>
                <div id="desiredJobReadList">
                  <c:forEach var="job" items="${data.desiredJobList}" varStatus="status">
                    <div class="d-flex align-items-center mb-1">
                      <c:if test="${not empty job.jobCategoryLarge}"><span class="me-1">${job.jobCategoryLarge}</span><i class="bi bi-caret-right-fill mx-1"></i></c:if>
                      <span>${job.desiredJob}</span>
                    </div>
                  </c:forEach>
                </div>
              </div>
              <!-- 수정 모드: 카테고리 선택 (초기 숨김) -->
              <div id="jobCategoryEditSection" style="display:none;">
                <span class="readonly-label">희망직종</span>
                <div id="desiredJobEditList">
                  <c:forEach var="job" items="${data.desiredJobList}" varStatus="status">
                    <div class="desired-job-row d-flex align-items-center mb-2" data-rank="${status.index + 1}">
                      <span class="badge bg-secondary me-2">${status.index + 1}순위</span>
                      <select class="form-control form-control-sm jobCategoryLargeSelect me-1" data-selected="${job.jobCategoryLarge}">
                        <option value="">카테고리 선택</option>
                      </select>
                      <i class="bi bi-caret-right-fill mx-1"></i>
                      <select class="form-control form-control-sm jobCategoryMidSelect me-1" data-selected="${job.jobCategoryMid}">
                        <option value="">카테고리 선택</option>
                      </select>
                      <i class="bi bi-caret-right-fill mx-1"></i>
                      <input type="text" class="form-control form-control-sm desiredJobInput me-1" value="${job.desiredJob}" placeholder="희망직무"/>
                      <button type="button" class="btn btn-outline-danger btn-sm btn-remove-job"><i class="bi bi-x-lg"></i></button>
                    </div>
                  </c:forEach>
                </div>
                <button type="button" class="btn btn-outline-primary btn-sm mt-2" id="addDesiredJobBtn"><i class="bi bi-plus-lg"></i> 희망직무 추가</button>
              </div>

              <div class="readonly-item">
                <span class="readonly-label">희망연봉</span>
                <span class="readonly-value" id="detailSalary" name="desiredSalary" >${data.desiredSalary}</span>
              </div>
              <div class="readonly-item">
                <span class="readonly-label">학교명</span>
                <span class="readonly-value" id="schoolName" name="schoolName" >${data.schoolName}</span>
              </div>
              <div class="readonly-item">
                <span class="readonly-label">전공</span>
                <span class="readonly-value" id="major" name="major" >${data.major}</span>
              </div>
              <div class="readonly-item">
                <span class="readonly-label">자격증</span>
                <span class="readonly-value" id="detailCertificates" name="certificate" >${data.certificate eq '' ? '없음':data.certificate}</span>
              </div>
              <div class="readonly-item">
                <span class="readonly-label">경력사항</span>
                <span class="readonly-value" id="detailExperience" name="career" >${data.career eq '' ? '신입':data.career}</span>
              </div>
              <%--          <div class="readonly-item">--%>
              <%--            <span class="readonly-label">등록일</span>--%>
              <%--            <span class="readonly-value" id="detailRegistrationDate">${data.career}</span>--%>
              <%--          </div>--%>
              <%--            <div class="readonly-item">
                            <span class="readonly-label">상세정보</span>
                            <pre class="readonly-pre" id="placementDetail" name="placementDetail" >${data.placementDetail eq '' ? '' : data.placementDetail}</pre>
                          </div>--%>
            </div>
          </div>
          <div class="col-md-6 p-0 ps-1">
            <div class="participant-profile-section">
              <!-- 키워드 섹션 -->
              <div>
                <div class="keyword-title">
                  ${data.participant}님의 키워드
                </div>
                <div class="keyword-container">
                  <div class="keyword-div" id="keyword-div">
                    ${data.recommendedKeyword}
                  </div>
                  <div class="resume-request-button-div" id="resume-request-button-div">
                    <input type="button"
                           class="resume-request-button"
                           id="resume-request-button"
                           value="이력서 요청하기"
                           aria-label="참여자 이력서 요청">
                  </div>
                </div>
              </div>
              <!-- 상세정보 섹션 -->
              <div class="recommendation-section">
                <span class="recommendation-title">상세정보</span>
                  <div class="readonly-item">
                    <pre class="readonly-pre" id="placementDetail" name="placementDetail" >${data.placementDetail eq '' ? '' : data.placementDetail}</pre>
                  </div>
              </div>
            </div>

          </div>

          <!-- 추천사 섹션 -->
          <div class="recommendation-section mt-2">
            <div class="recommendation-title">추천사</div>
            <div class="recommendation-content">
              ${data.suggestionDetail}
            </div>
          </div>
        </div>
      </div>

    </div>

    <!-- 자기소개서 (읽기 전용) -->
    <div class="card-modern">
      <div class="card-header">
        <h3 class="card-title">
          <i class="bi bi-file-text-fill"></i>
          담당 상담사
        </h3>
      </div>
      <div class="card-body">
        <div id="detailEssayContainer">
<%--          <div class="essay-section">--%>
<%--            <h4 class="essay-title">1. 지원동기 및 포부</h4>--%>
<%--            <p class="essay-content">저는 이 직무에 지원하게 된 동기는 제가 가진 IT 관련 지식과 경험을 활용하여 회사의 발전에 기여하고 싶기 때문입니다. 특히 사무직으로서의 업무 경험을 바탕으로 체계적이고 효율적인 업무 처리 능력을 발휘할 수 있을 것이라고 생각합니다.--%>

<%--              앞으로의 포부는 단순히 주어진 업무를 수행하는 것을 넘어서, 업무 프로세스 개선과 효율성 향상에 기여하고 싶습니다. 또한 지속적인 학습을 통해 전문성을 키워나가며, 팀원들과의 협업을 통해 더 나은 결과를 만들어내고 싶습니다.</p>--%>
<%--          </div>--%>
          <div class="essay-section">
            <h4 class="essay-title">이력서와 자기소개서는 아래 상담사에게 문의 부탁드립니다.</h4>
            <p class="essay-content">
              <strong>지점 : </strong> ${data.counselorBranch}
              <strong>상담사 : </strong> ${data.counselor}
              <strong>이메일 : </strong> ${data.email}
              <strong>전화 번호 : </strong> ${data.counselorPhone}
            </p>
          </div>
        </div>
      </div>
    </div>

  </div>
</main>


<form id="resumeRequestForm" class="space-y-4">
<div class="modal fade" id="resumeRequestModal" tabindex="-1" aria-labelledby="resumeRequestModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content bg-gray-50 text-gray-800 rounded-lg">
      <div class="modal-header p-4 border-b border-gray-200">
        <h5 class="modal-title text-xl font-semibold" id="resumeRequestModalLabel">이력서 요청</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body p-4">
        <%-- 참여자 구직번호 전달용 input --%>
        <input type="hidden" id="jobNumber" name="jobNumber" value="${data.jobNumber}">
          <div>
            <label for="companyName" class="block text-sm font-medium text-gray-700">기업명 <span class="text-red-600 text-lg">*</span></label>
            <input type="text" id="companyName" name="companyName" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm sm:text-sm" required>
          </div>
          <div>
            <label for="managerName" class="block text-sm font-medium text-gray-700 mt-2">담당자명 <span class="text-red-600 text-lg">*</span></label>
            <input type="text" id="managerName" name="managerName" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm  sm:text-sm" required>
          </div>
          <div>
            <label for="email" class="block text-sm font-medium text-gray-700 mt-2">이메일 <span class="text-red-600 text-lg">*</span></label>
            <input type="email" id="email" name="email" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm  sm:text-sm" required>
          </div>
          <div>
            <label for="emergencyContact" class="block text-sm font-medium text-gray-700 mt-2">비상연락처 <span class="text-red-600 text-lg">*</span></label>
            <input type="tel" id="emergencyContact" name="emergencyContact" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm sm:text-sm" required>
          </div>
          <div>
            <label for="otherRequests" class="block text-sm font-medium text-gray-700 mt-2">기타요청사항</label>
            <textarea id="otherRequests" name="otherRequests" rows="3" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm sm:text-sm"></textarea>
          </div>
      </div>
      <div class="modal-footer p-3 border-t border-gray-200">
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">취소</button>
        <button type="button" form="resumeRequestForm" id="resumeEmailRequestButton" class="btn btn-primary">요청하기</button>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="personalInformationModal" tabindex="-1" aria-labelledby="personalInformationModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content bg-gray-50 text-gray-800 rounded-lg">
      <!--modal header -->
      <div class="modal-header p-4 border-b border-gray-200">
        <h5 class="modal-title text-xl font-semibold" id="personalInformationModalLabel">이력서 요청</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>

      <!--modal body -->
      <div class="modal-body p-4 space-y-4">

        <div class="personalInformationTextAreaDiv">
          <label for="" class="block text-sm font-medium text-gray-700 mb-1">채용 기업(개인정보 수신자)을 위한 개인정보 처리 안내(필수)</label>
          <div class="w-full p-3 border border-gray-300 bg-gray-100 rounded-md overflow-auto text-sm">
            <p>귀사(채용 기업)께서 (주)잡모아를 통해 제공받는 구직자의 개인정보는 개인정보보호법에 따라 엄격하게 처리되어야 합니다.</p>
            <h3 class="font-semibold mt-2 mb-1"><strong>개인정보 처리 목적의 한정 및 이용 금지</strong></h3>
            <ul class="list-disc list-inside space-y-1">
              <li>제공받은 개인정보는 <strong>오직 구직자의 채용 심사 및 면접 기회 제공 목적으로만 이용</strong>되어야 합니다.</li>
              <li>해당 정보를 당초의 채용 목적 <strong>외로 이용하거나 제3자에게 제공할 수 없습니다</strong>.</li>
            </ul>
            <h3 class="font-semibold mt-2 mb-1"><strong>안전성 확보 의무 및 파기</strong></h3>
            <ul class="list-disc list-inside space-y-1">
              <li><strong>안전성 확보</strong>: 귀사는 제공받은 구직자의 개인정보가 유출되거나 훼손되지 않도록 기술적·관리적 안전조치를 취해야 합니다. 데이터베이스(DB)에 저장된 개인정보는 <strong>AES256 이상 알고리즘으로 암호화</strong>되어야 하며, 관리자 계정이라도 <strong>접근 권한 제한(RBAC)을 설정</strong>해야 합니다.</li>
              <li><strong>파기 의무</strong>: 채용 심사 목적이 달성되어 개인정보가 <strong>불필요하게 된 시점</strong>에 지체 없이 파기해야 합니다. 표준 개인정보 보호지침에 따르면, 불필요해진 후 <strong>5일 이내에 파기</strong>하는 것이 권고됩니다.</li>
            </ul>
            <h3 class="font-semibold mt-2 mb-1"><strong>주민등록번호 및 민감 정보 수집 제한</strong></h3>
            <ul class="list-disc list-inside space-y-1">
              <li><strong>주민등록번호 수집 제한</strong>: 채용 심사를 위한 이력서 제출 단계에서는 법률에 명확한 근거가 없으므로 구직자의 <strong>주민등록번호를 수집할 수 없습니다</strong>. 이는 <strong>최종합격이 확정된 이후에만</strong> 법률 근거에 따라 처리할 수 있습니다.</li>
            </ul>
          </div>
          <div class="mt-2 text-end">
            <label for="personalInformationAgreeCompany">[필수] 채용 기업(개인정보 수신자)을 위한 개인정보 처리 안내</label>
            <input type="checkbox" id="personalInformationAgreeCompany" class="personalInformationAgree" name="personalInformationAgreeCompany" value="true" required>
          </div>
        </div>

        <div class="personalInformationTextAreaDiv">
          <label for="" class="block text-sm font-medium text-gray-700 mb-1">채용 담당자 개인정보 수집·이용 동의(필수)</label>
          <div class="w-full p-3 border border-gray-300 bg-gray-100 rounded-md overflow-auto text-sm">
            <p>(주)잡모아는 귀사(채용 기업)의 담당자님과 원활한 서비스 이행을 위해 아래와 같이 개인정보를 수집 및 이용하고자 합니다.</p>
            <h3 class="font-semibold mt-2 mb-1"><strong>개인정보 수집·이용</strong></h3>
            <ul class="list-disc list-inside space-y-1">
              <li><strong>수집 항목</strong>: <strong>기업명, 담당자명, 이메일, 비상연락처</strong></li>
              <li><strong>수집 및 이용 목적</strong>: 취업 알선 서비스 이행, 이력서 발부 및 전송, 채용 프로세스 진행 상황 안내, 담당자 커뮤니케이션 및 상담 지원</li>
              <li><strong>이용 및 보유 기간</strong>: 서비스 계약 종료 시 또는 정보주체의 동의 철회 시까지</li>
              <li><strong>동의 거부 권리</strong>: 담당자님은 위 개인정보 수집 및 이용에 대한 동의를 거부할 권리가 있으나, 거부 시 <strong>취업 알선 서비스 이용이 불가능</strong>할 수 있습니다.</li>
            </ul>
          </div>
          <div class="mt-2 text-end">
            <label for="personalInformationAgreeManager">[필수] 채용 담당자 개인정보 수집·이용 동의</label>
            <input type="checkbox" id="personalInformationAgreeManager" class="personalInformationAgree" name="personalInformationAgreeManager" value="true" required>
          </div>
        </div>

        <div class="personalInformationTextAreaDiv">
          <label for="" class="block text-sm font-medium text-gray-700 mb-1">홍보 및 마케팅 정보 수신 동의(선택)</label>
          <div class="w-full p-3 border border-gray-300 bg-gray-100 rounded-md overflow-auto text-sm">
            <p>(주)잡모아는 귀사(채용 기업)의 담당자님과 원활한 서비스 이행을 위해 아래와 같이 개인정보를 수집 및 이용하고자 합니다.</p>
            <h3 class="font-semibold mt-2 mb-1"><strong>개인정보 수집·이용</strong></h3>
            <ul class="list-disc list-inside space-y-1">
              <li><strong>수집 항목</strong>: 담당자명, 연락처</li>
              <li><strong>수집 및 이용 목적</strong>: (주)잡모아의 신규 상품 및 서비스 관련 홍보∙마케팅 정보 전달</li>
              <li><strong>이용 및 보유 기간</strong>: 정보주체의 동의 철회 시 또는 홍보/마케팅 목적 달성 시까지 (예시: 수집일로부터 6개월까지 보관/이용 후 파기)</li>
              <li><strong>동의 거부 권리</strong>: 귀하는 홍보‧마케팅 활용을 위한 개인정보 수집·이용 동의를 거부할 권리가 있으며, <strong>동의 거부 시에도 핵심 서비스(채용 알선) 신청 및 이용은 가능</strong>합니다.</li>
            </ul>
          </div>
          <div class="mt-2 text-end">
            <label for="personalInformationAgreeMarketing">[선택] 홍보 및 마케팅 정보 수신 동의</label>
            <input type="checkbox" id="personalInformationAgreeMarketing" class="personalInformationAgree" name="personalInformationAgreeMarketing" value="true" required>
          </div>
        </div>

      </div>

      <!--modal footer -->
      <div class="modal-footer p-3 flex justify-end items-center space-x-2 border-t border-gray-200">
        <button type-="button" id="personal-information-agree" class="py-2 px-4 bg-gray-200 text-gray-800 font-semibold rounded-lg shadow-md hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400 focus:ring-opacity-75 transition duration-150 ease-in-out" aria-label="전체동의">
          전체동의
        </button>
        <button type="button" id="btn-next-modal" class="py-2 px-4 bg-indigo-600 text-white font-semibold rounded-lg shadow-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-opacity-75 transition duration-150 ease-in-out" data-bs-dismiss="resumeRequestModal" aria-label="다음">
          다음
        </button>
      </div>
    </div>
  </div>
</div>
</form>

<!-- Toast Container -->
<div class="toast-container" id="toastContainer"></div>

<!-- Scripts -->
<script src="/js/paginationJS_0.0.1.js"></script>
<script>
  $(document).ready(function(){
    let page = parseInt("${param.page}", 10) || 1; // page가 비어있거나 아닌 경우 숫자로 변환 후 기본값 1 적용
    page = (page == 0) ? 1 : page; //page가 0이라면 1로 변경
//updateBtn

    /* 리스트 돌아가기 버튼 주소 추가 */
    $('#backToListATag').on('click',function(){
      locationBack(page);
    })
    $('#backToListBtn').on('click',function(){
      locationBack(page);
    })
    /* 리스트 돌아가기 버튼 주소 추가 */

    /* 프린트 버튼 실행 시작 */
    $('#printBtn').click(function(){
      window.print();
    })
    /* 프린트 버튼 실행 끝 */

    /* datePicker 날짜 형식 변환 시작 */
    const datepicker_on = $('.datepicker_on');
    let dateValue = "";

    default_datepicker();

    datepicker_on.on('keyup', function () {
      dateValue = $(this).val();
      if(dateValue.length == 7) {
        dateValue+='-'
      }
      else if(dateValue.length == 4) {
        dateValue+='-'
      }
      $(this).val(dateValue);
    });

    datepicker_on.on('change', function () {
      dateValue = $(this).val();
      const datePattern = /^\d{4}-\d{2}-\d{2}$/;
      // console.log(dateValue);
      if (!datePattern.test(dateValue)) {
        $(this).val(''); // 날짜형식이 아니면 삭제
        // console.log('Invalid date format, value cleared.');
      } else {
        // console.log('datepicker_on');
        //console.log(dateValue);
      }
    });
    /* datePicker 날짜 형식 변환 끝 */
  })
</script>
</body>
</html>