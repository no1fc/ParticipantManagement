<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 상담 일정</title>
    <mytag:Logo/>

    <!--begin::Primary Meta Tags-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <!--end::Primary Meta Tags-->

    <!--begin::Fonts-->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.0.12/index.css"
          integrity="sha256-tXJfXfp6Ewt1ilPzLDtQnJV4hclT9XuaZUKyUvmyr+Q="
          crossorigin="anonymous" />
    <!--end::Fonts-->

    <!--begin::Third Party Plugin(OverlayScrollbars)-->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/styles/overlayscrollbars.min.css"
          integrity="sha256-tZHrRjVqNSRyWg2wbppGnT833E/Ys0DHWGwT04GiqQg="
          crossorigin="anonymous" />
    <!--end::Third Party Plugin(OverlayScrollbars)-->

    <!--begin::Third Party Plugin(Bootstrap Icons)-->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
          integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI="
          crossorigin="anonymous" />
    <!--end::Third Party Plugin(Bootstrap Icons)-->

    <!--begin::Required Plugin(AdminLTE)-->
    <link rel="stylesheet" href="/css/adminlte.css" />
    <!--end::Required Plugin(AdminLTE)-->

    <!--begin::FullCalendar CSS-->
    <link href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.css" rel="stylesheet" />
    <!--end::FullCalendar CSS-->

    <!--begin::SweetAlert2 CSS-->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css" />
    <!--end::SweetAlert2 CSS-->

    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.1.js"
            integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4="
            crossorigin="anonymous"></script>

    <!--begin::Schedule CSS-->
    <link rel="stylesheet" href="/css/scheduleCss/schedule_0.0.1.css" />
    <!--end::Schedule CSS-->
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <mytag:gnb gnb_main_header="상담관리" gnb_sub_header="상담 일정"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid py-4 pt-3 pb-0">

                <!-- Hidden fields for JS -->
                <input type="hidden" id="counselorId" value="${counselorId}">
                <input type="hidden" id="counselorName" value="${counselorName}">

                <!-- Page Actions -->
                <div class="page-actions">
                    <h4><i class="bi bi-calendar-event me-2"></i>상담 일정 조회 및 등록</h4>
                    <button class="btn btn-primary" id="btnRegister">
                        <i class="bi bi-plus-lg me-1"></i>상담 일정 등록
                    </button>
                </div>

                <!-- Calendar -->
                <div id="calendar"></div>

            </div>
        </div>
    </main>

    <mytag:footer/>

</div>

<!-- =============================== -->
<!-- Register/Edit Modal (Bootstrap 4) -->
<!-- =============================== -->
<div class="modal fade" id="scheduleModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">
                    <i class="bi bi-calendar-plus me-2"></i>상담 일정 등록
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <input type="hidden" id="editScheduleId">

                <!-- 일정 날짜 -->
                <div class="form-group mb-3">
                    <label class="form-label">일정 날짜 <span class="text-danger">*</span></label>
                    <input type="date" class="form-control" id="scheduleDate">
                </div>

                <!-- 시간 -->
                <div class="form-group mb-3">
                    <label class="form-label">시간 <span class="text-danger">*</span></label>
                    <div class="time-select">
                        <select class="form-control" id="startHour">
                            <option value="">시</option>
                            <option value="09">09</option>
                            <option value="10">10</option>
                            <option value="11">11</option>
                            <option value="12">12</option>
                            <option value="13">13</option>
                            <option value="14">14</option>
                            <option value="15">15</option>
                            <option value="16">16</option>
                            <option value="17">17</option>
                            <option value="18">18</option>
                        </select>
                        <span>:</span>
                        <select class="form-control" id="startMinute">
                            <option value="">분</option>
                            <option value="00">00</option>
                            <option value="10">10</option>
                            <option value="20">20</option>
                            <option value="30">30</option>
                            <option value="40">40</option>
                            <option value="50">50</option>
                        </select>
                        <span>~</span>
                        <select class="form-control" id="endHour">
                            <option value="">시</option>
                            <option value="09">09</option>
                            <option value="10">10</option>
                            <option value="11">11</option>
                            <option value="12">12</option>
                            <option value="13">13</option>
                            <option value="14">14</option>
                            <option value="15">15</option>
                            <option value="16">16</option>
                            <option value="17">17</option>
                            <option value="18">18</option>
                        </select>
                        <span>:</span>
                        <select class="form-control" id="endMinute">
                            <option value="">분</option>
                            <option value="00">00</option>
                            <option value="10">10</option>
                            <option value="20">20</option>
                            <option value="30">30</option>
                            <option value="40">40</option>
                            <option value="50">50</option>
                        </select>
                    </div>
                </div>

                <!-- 참여자 선택 -->
                <div class="form-group mb-3">
                    <label class="form-label">참여자 선택</label>
                    <div class="participant-search-wrapper">
                        <input type="text" class="form-control" id="participantSearch"
                               placeholder="참여자 이름을 입력하세요" autocomplete="off">
                        <input type="hidden" id="participantJobNo">
                        <div class="participant-dropdown" id="participantDropdown"></div>
                    </div>
                </div>

                <!-- 일정 유형 -->
                <div class="form-group mb-3">
                    <label class="form-label">일정 유형</label>
                    <select class="form-control" id="scheduleType">
                        <option value="대면상담">대면상담</option>
                        <option value="전화상담">전화상담</option>
                        <option value="화상상담">화상상담</option>
                        <option value="기타">기타</option>
                    </select>
                </div>

                <!-- 알림 설정 -->
                <div class="form-group mb-3">
                    <label class="form-label">알림 설정</label>
                    <div class="alert-setting-row">
                        <select class="form-control" id="alertSelect">
                            <option value="">알림없음</option>
                            <option value="10">10분 전</option>
                            <option value="30">30분 전</option>
                            <option value="60">60분 전</option>
                            <option value="custom">직접입력</option>
                        </select>
                        <input type="number" class="form-control custom-alert-input" id="customAlertMinutes"
                               min="1" max="1440" placeholder="분">
                        <span class="custom-alert-unit">분 전</span>
                    </div>
                </div>

                <!-- 메모 -->
                <div class="form-group mb-3">
                    <label class="form-label">메모</label>
                    <textarea class="form-control" id="scheduleMemo" rows="3"
                              maxlength="500" placeholder="메모를 입력하세요 (최대 500자)"></textarea>
                    <div class="memo-counter"><span id="memoCount">0</span>/500</div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary" id="btnSave">
                    <i class="bi bi-check-lg me-1"></i>저장
                </button>
            </div>
        </div>
    </div>
</div>

<!-- =============================== -->
<!-- Detail Modal (Bootstrap 4) -->
<!-- =============================== -->
<div class="modal fade" id="detailModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="bi bi-calendar-check me-2"></i>일정 상세</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body schedule-detail">
                <input type="hidden" id="detailScheduleId">
                <div class="detail-body">
                    <div class="detail-row">
                        <div class="detail-label">날짜</div>
                        <div class="detail-value" id="detailDate"></div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">시간</div>
                        <div class="detail-value" id="detailTime"></div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">참여자</div>
                        <div class="detail-value" id="detailParticipant"></div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">유형</div>
                        <div class="detail-value" id="detailType"></div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">알림</div>
                        <div class="detail-value" id="detailAlert"></div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">메모</div>
                        <div class="detail-value" id="detailMemo"></div>
                    </div>
                    <div class="detail-actions">
                        <button class="btn btn-success btn-sm" id="btnCounselNote">
                            <i class="bi bi-journal-text me-1"></i>상담일지
                        </button>
                        <button class="btn btn-warning btn-sm" id="btnEdit">
                            <i class="bi bi-pencil me-1"></i>수정
                        </button>
                        <button class="btn btn-danger btn-sm" id="btnDelete">
                            <i class="bi bi-trash me-1"></i>삭제
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>

<!--begin::Third Party Plugin(OverlayScrollbars)-->
<script src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es6.min.js"
        integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ="
        crossorigin="anonymous"></script>
<!--end::Third Party Plugin(OverlayScrollbars)-->

<!--begin::Required Plugin(popperjs for Bootstrap 5)-->
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r"
        crossorigin="anonymous"></script>
<!--end::Required Plugin(popperjs for Bootstrap 5)-->

<!--begin::Required Plugin(Bootstrap 5)-->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.min.js"
        integrity="sha384-0pUGZvbkm6XF6gxjEnlmuGrJXVbNuzT9qBBavbLwCsOGabYfZo0T0to5eqruptLy"
        crossorigin="anonymous"></script>
<!--end::Required Plugin(Bootstrap 5)-->

<!--begin::Required Plugin(AdminLTE)-->
<script src="/js/adminlte.js"></script>
<!--end::Required Plugin(AdminLTE)-->

<!--begin::OverlayScrollbars Configure-->
<script>
    const SELECTOR_SIDEBAR_WRAPPER = '.sidebar-wrapper';
    const Default = {
        scrollbarTheme: 'os-theme-light',
        scrollbarAutoHide: 'leave',
        scrollbarClickScroll: true,
    };
    document.addEventListener('DOMContentLoaded', function () {
        const sidebarWrapper = document.querySelector(SELECTOR_SIDEBAR_WRAPPER);
        if (sidebarWrapper && typeof OverlayScrollbarsGlobal?.OverlayScrollbars !== 'undefined') {
            OverlayScrollbarsGlobal.OverlayScrollbars(sidebarWrapper, {
                scrollbars: {
                    theme: Default.scrollbarTheme,
                    autoHide: Default.scrollbarAutoHide,
                    clickScroll: Default.scrollbarClickScroll,
                },
            });
        }
    });
</script>
<!--end::OverlayScrollbars Configure-->

<!--begin::SweetAlert2-->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
<!--end::SweetAlert2-->

<!--begin::FullCalendar JS-->
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.11/index.global.min.js"></script>
<!--end::FullCalendar JS-->

<!--begin::Schedule JS-->
<script src="/js/schedule_0.0.1.js"></script>
<!--end::Schedule JS-->

</html>
