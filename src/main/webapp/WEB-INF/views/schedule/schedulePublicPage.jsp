<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>상담 일정 조회 - Jobmoa</title>

    <!--begin::Bootstrap CSS-->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!--end::Bootstrap CSS-->

    <!--begin::Bootstrap Icons-->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
          integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI="
          crossorigin="anonymous" />
    <!--end::Bootstrap Icons-->

    <!--begin::FullCalendar CSS-->
    <link href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.css" rel="stylesheet" />
    <!--end::FullCalendar CSS-->

    <!--begin::Schedule Public CSS-->
    <link rel="stylesheet" href="/css/scheduleCss/schedulePublic_0.0.3.css" />
    <!--end::Schedule Public CSS-->

    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.1.js"
            integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4="
            crossorigin="anonymous"></script>
</head>
<body style="margin:0; font-family:'Segoe UI',sans-serif; background:#f4f6f9;">

<!-- Header -->
<header class="public-header">
    <div class="logo">
        <i class="bi bi-briefcase-fill"></i>
        <h4>Jobmoa 상담 일정 조회</h4>
    </div>
    <div class="branch-info" id="headerBranchInfo"></div>
</header>

<!-- Auth Section -->
<div id="authSection">
    <div class="auth-card">
        <div class="auth-header">
            <i class="bi bi-calendar-check"></i>
            <h3>상담 일정 조회</h3>
            <p>지점을 선택하고 접근코드를 입력하세요</p>
        </div>
        <div class="auth-body">
            <div class="mb-3">
                <label class="form-label">지점 선택 <span class="text-danger">*</span></label>
                <select class="form-select" id="branchSelect">
                    <option value="">지점을 선택하세요</option>
                    <option value="남부">남부지점</option>
                    <option value="서부">서부지점</option>
                    <option value="부천">부천지점</option>
                    <option value="수원">수원지점</option>
                    <option value="천호">천호지점</option>
                    <option value="의정부">의정부지점</option>
                    <option value="인천">인천서부지점</option>
                    <option value="인천남부">인천남부지점</option>
                    <option value="동대문">동대문지점</option>
                    <option value="광명">광명지점</option>
                    <option value="안양">안양지점</option>
                    <option value="북부">북부지점</option>
                    <option value="성남">성남지점</option>
                    <option value="관악">관악지점</option>
                    <option value="테스트">테스트지점</option>
                </select>
            </div>
            <div class="mb-4">
                <label class="form-label">접근코드 <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="accessCode" placeholder="지점 접근코드를 입력하세요">
                <div class="form-text">접근코드는 지점 관리자에게 문의하세요.</div>
            </div>
            <button class="btn-verify" id="btnVerify">
                <i class="bi bi-search me-2"></i>조회하기
            </button>
            <div class="alert alert-danger auth-error" id="authError">
                <i class="bi bi-exclamation-circle me-1"></i>접근코드가 올바르지 않습니다.
            </div>
        </div>
    </div>
</div>

<!-- Schedule View -->
<div class="schedule-view" id="scheduleView">
    <div class="container-main">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
            <div class="branch-badge" id="branchBadge">
                <i class="bi bi-geo-alt-fill"></i><span id="branchName"></span>
            </div>
            <button class="btn btn-outline-secondary btn-sm" id="btnBack">
                <i class="bi bi-arrow-left me-1"></i>다른 지점 조회
            </button>
        </div>

        <!-- Legend -->
        <div class="legend-section" id="legendSection"></div>

        <!-- 일정유형 필터 -->
        <div class="type-filter-bar" id="typeFilterBar">
            <span class="filter-label"><i class="bi bi-funnel me-1"></i>일정유형</span>
            <label class="type-chip active" data-type="대면상담">
                <span class="badge-schedule badge-face">대면상담</span>
            </label>
            <label class="type-chip inactive" data-type="전화상담">
                <span class="badge-schedule badge-phone">전화상담</span>
            </label>
            <label class="type-chip inactive" data-type="화상상담">
                <span class="badge-schedule badge-video">화상상담</span>
            </label>
            <label class="type-chip inactive" data-type="기타">
                <span class="badge-schedule badge-etc">기타</span>
            </label>
        </div>

        <!-- Calendar -->
        <div id="calendar"></div>

        <!-- Day Detail -->
        <div class="day-detail-panel" id="dayDetailPanel">
            <div class="day-detail-header">
                <h5><i class="bi bi-clock me-2"></i><span id="dayDetailTitle"></span></h5>
                <div class="day-detail-actions">
                    <select class="form-select form-select-sm" id="displayIntervalSelect" style="width:auto;" title="시간표 간격">
                        <option value="10">10분 간격</option>
                        <option value="30">30분 간격</option>
                        <option value="60" selected>1시간 간격</option>
                    </select>
                    <button class="btn btn-sm btn-outline-primary" id="refreshDayDetail" title="새로고침">
                        <i class="bi bi-arrow-clockwise"></i>
                    </button>
                    <button class="btn btn-sm" id="closeDayDetail"><i class="bi bi-x-lg"></i></button>
                </div>
            </div>
            <div class="table-scroll">
                <table class="time-table" id="timeTable">
                    <thead><tr id="timeTableHead"></tr></thead>
                    <tbody id="timeTableBody"></tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Footer -->
<div class="public-footer">
    &copy; 2026 Jobmoa. All rights reserved.
</div>

<!--begin::Bootstrap JS-->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<!--end::Bootstrap JS-->

<!--begin::FullCalendar JS-->
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.js"></script>
<!--end::FullCalendar JS-->

<!--begin::Schedule Public JS-->
<script src="/js/schedulePublic_0.0.3.js"></script>
<!--end::Schedule Public JS-->

</body>
</html>
