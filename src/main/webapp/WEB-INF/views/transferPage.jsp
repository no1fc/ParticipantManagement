<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 25. 4. 3.
  Time: 오전 9:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <mytag:Logo/>
    <!--begin::Primary Meta Tags-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="title" content="AdminLTE v4 | Dashboard" />
    <meta name="author" content="ColorlibHQ" />
    <meta
            name="description"
            content="AdminLTE is a Free Bootstrap 5 Admin Dashboard, 30 example pages using Vanilla JS."
    />
    <meta
            name="keywords"
            content="bootstrap 5, bootstrap, bootstrap 5 admin dashboard, bootstrap 5 dashboard, bootstrap 5 charts, bootstrap 5 calendar, bootstrap 5 datepicker, bootstrap 5 tables, bootstrap 5 datatable, vanilla js datatable, colorlibhq, colorlibhq dashboard, colorlibhq admin dashboard"
    />
    <!--end::Primary Meta Tags-->
    <!-- jQuery JS -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>

    <!--begin::Fonts-->
    <link
            rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.0.12/index.css"
            integrity="sha256-tXJfXfp6Ewt1ilPzLDtQnJV4hclT9XuaZUKyUvmyr+Q="
            crossorigin="anonymous"
    />
    <!--end::Fonts-->
    <!--begin::Third Party Plugin(OverlayScrollbars)-->
    <link
            rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/styles/overlayscrollbars.min.css"
            integrity="sha256-tZHrRjVqNSRyWg2wbppGnT833E/Ys0DHWGwT04GiqQg="
            crossorigin="anonymous"
    />
    <!--end::Third Party Plugin(OverlayScrollbars)-->
    <!--begin::Third Party Plugin(Bootstrap Icons)-->
    <link
            rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
            integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI="
            crossorigin="anonymous"
    />
    <!--end::Third Party Plugin(Bootstrap Icons)-->
    <!--begin::Required Plugin(AdminLTE)-->
    <link rel="stylesheet" href="css/adminlte.css" />
    <!--end::Required Plugin(AdminLTE)-->
    <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css" />
    <!-- apexcharts -->
    <link
            rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/apexcharts@3.37.1/dist/apexcharts.css"
            integrity="sha256-4MX+61mt9NVvvuPjUWdUdyfZfxSB1/Rf9WtqRHgG5S0="
            crossorigin="anonymous"
    />
    <!-- jsvectormap -->
    <link
            rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/jsvectormap@1.5.3/dist/css/jsvectormap.min.css"
            integrity="sha256-+uGLJmmTKOqBr+2E6KDYs/NRsHxSkONXFHUL0fy2O/4="
            crossorigin="anonymous"
    />
    <!-- mouse pointer 모양 bootstrap 5 -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <!-- sweetalert2 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
    <script src="/js/sweetAlert_0.0.1.js"></script>

    <!-- transferJS_0.0.1.js -->
    <script src="/js/transferJS_0.0.1.js"></script>

    <style>
        .transfer-buttons {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .transfer-buttons button {
            width: 120px;
        }

        .participant-container {
            border: 1px solid #dee2e6;
            border-radius: 4px;
            padding: 15px;
        }

        .list-group-item {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .participant-list {
            max-height: 400px;
            overflow-y: auto;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        }

        .participant-item {
            padding: 0.25rem 0.1rem;
            border-left: 3px solid transparent;
            transition: all 0.2s ease;
        }

        .participant-item:hover {
            background-color: #f8f9fa;
            border-left-color: #0d6efd;
        }

        .participant-item .form-check {
            margin: 0;
            width: 100%;
        }

        .participant-label {
            cursor: pointer;
            margin: 0;
            padding: 0;
            width: 100%;
        }

        .participant-name {
            font-weight: 500;
            color: #212529;
            min-width: 100px;
        }

        .participant-info {
            display: flex;
            gap: 1.5rem;
            color: #6c757d;
        }

        .info-item {
            min-width: 80px;
            text-align: center;
        }

        /* 체크박스 스타일 개선 */
        .form-check-input {
            width: 1.2rem;
            height: 1.2rem;
            margin-top: 0;
            cursor: pointer;
        }

        .form-check-input:checked {
            background-color: #0d6efd;
            border-color: #0d6efd;
        }

        /* 체크된 항목 스타일 */
        .participant-item:has(.form-check-input:checked) {
            background-color: #e8f0fe;
            border-left-color: #0d6efd;
        }

        /* 스크롤바 스타일링 */
        .participant-list::-webkit-scrollbar {
            width: 12px;
        }

        .participant-list::-webkit-scrollbar-track {
            background: #f1f1f1;
            border-radius: 4px;
        }

        .participant-list::-webkit-scrollbar-thumb {
            background: #c1c1c1;
            border-radius: 4px;
        }

        .participant-list::-webkit-scrollbar-thumb:hover {
            background: #a8a8a8;
        }

        /* 반응형 스타일 */
        @media (max-width: 768px) {
            .participant-info {
                flex-direction: column;
                gap: 0.5rem;
            }

            .info-item {
                min-width: auto;
                text-align: left;
            }
        }
    </style>
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin:::App Gnb-->
    <mytag:gnb gnb_main_header="지점관리" gnb_sub_header="데이터 이전"/>
    <!--end:::App Gnb-->
    <!--begin:::App main content-->
    <!--begin::App Main-->
    <main class="app-main">
        <!--begin::App Content-->
        <div class="app-content">
            <!--begin::Main content-->
            <div class="container-fluid">
                <div class="row mt-4">
                    <!-- 원본 상담사 카드 -->
                    <div class="col-md-5">
                        <div class="card-modern">
                            <div class="card-header bg-primary text-white">
                                <h5 class="card-title mb-0">원본 상담사</h5>
                            </div>
                            <div class="card-body">
                                <select id="sourceCounselor" class="form-select mb-3">
                                    <option value="">상담사를 선택하세요</option>
                                    <c:if test="${not empty counselors}">
                                        <c:forEach items="${counselors}" var="counsel">
                                            <option value="${counsel.memberUserID}">
                                                    ${counsel.memberUserName}
                                            </option>
                                        </c:forEach>
                                    </c:if>
                                </select>

                                <div class="participant-container">
                                    <div class="d-flex justify-content-between mb-2">
                                        <h6>참여자 목록</h6>
                                        <div>
                                            <input type="checkbox" id="selectAllSource" class="form-check-input">
                                            <label for="selectAllSource" class="form-check-label">전체 선택</label>
                                        </div>
                                    </div>

                                    <div class="input-group mb-3">
                                        <input type="text"
                                               id="participantSearch"
                                               class="form-control"
                                               placeholder="구직번호, 이름, 생년월일, 성별 검색">
                                        <button class="btn btn-outline-secondary"
                                                type="button"
                                                id="clearSearch">
                                            <i class="bi bi-x-circle"></i> 초기화
                                        </button>
                                    </div>
                                    <small id="searchResult" class="text-muted d-block mb-2"></small>

                                    <div id="participantList" class="list-group participant-list" style="max-height: 600px; overflow-y: auto;">

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 이전 버튼 영역 -->
                    <div class="col-md-2 d-flex align-items-center justify-content-center">
                        <div class="transfer-buttons">
                            <button id="transferSelected" class="btn btn-primary mb-2" disabled>
                                <i class="bi bi-arrow-right"></i> 선택 이전
                            </button>
                            <button id="transferAll" class="btn btn-secondary" disabled>
                                <i class="bi bi-arrow-right-square"></i> 전체 이전
                            </button>
                        </div>
                    </div>

                    <!-- 대상 상담사 카드 -->
                    <div class="col-md-5">
                        <div class="card-modern">
                            <div class="card-header bg-success text-white">
                                <h5 class="card-title mb-0">대상 상담사</h5>
                            </div>
                            <div class="card-body">
                                <select id="targetCounselor" class="form-select mb-3">
                                    <option value="">상담사를 선택하세요</option>
                                    <c:if test="${not empty counselors}">
                                        <c:forEach items="${counselors}" var="counsel">
                                            <option value="${counsel.memberUserID}">
                                                    ${counsel.memberUserName}
                                            </option>
                                        </c:forEach>
                                    </c:if>
                                </select>

                                <div class="participant-container">
                                    <div class="d-flex justify-content-between mb-2">
                                        <h6>이전된 참여자 목록</h6>
                                    </div>
                                    <div id="transferredList" class="list-group" style="max-height: 700px; overflow-y: auto;">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--end::Main content-->
        </div>
        <!--end::App Content-->
    </main>
    <!--end::App Main-->
    <!--end:::App main content-->

    <!--begin::Footer-->
    <mytag:footer/>
    <!--end::Footer-->

</div>

</body>
<!--begin::Script-->
<!--begin::Third Party Plugin(OverlayScrollbars)-->
<script
        src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es6.min.js"
        integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ="
        crossorigin="anonymous"
></script>
<!--end::Third Party Plugin(OverlayScrollbars)--><!--begin::Required Plugin(popperjs for Bootstrap 5)-->
<script
        src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r"
        crossorigin="anonymous"
></script>
<!--end::Required Plugin(popperjs for Bootstrap 5)--><!--begin::Required Plugin(Bootstrap 5)-->
<script
        src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.min.js"
        integrity="sha384-0pUGZvbkm6XF6gxjEnlmuGrJXVbNuzT9qBBavbLwCsOGabYfZo0T0to5eqruptLy"
        crossorigin="anonymous"
></script>
<!--end::Required Plugin(Bootstrap 5)--><!--begin::Required Plugin(AdminLTE)-->
<script src="js/adminlte.js"></script>
<!--begin::Script-->
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

<!-- OPTIONAL SCRIPTS -->
<!-- sortablejs -->
<script>
    const connectedSortables = document.querySelectorAll('.connectedSortable');
    connectedSortables.forEach((connectedSortable) => {
        let sortable = new Sortable(connectedSortable, {
            group: 'shared',
            handle: '.card-header',
        });
    });

    const cardHeaders = document.querySelectorAll('.connectedSortable .card-header');
    cardHeaders.forEach((cardHeader) => {
        cardHeader.style.cursor = 'move';
    });
</script>
<!-- apexcharts -->
<script
        src="https://cdn.jsdelivr.net/npm/apexcharts@3.37.1/dist/apexcharts.min.js"
        integrity="sha256-+vh8GkaU7C9/wbSLIcwq82tQ2wTf44aOHA8HlBMwRI8="
        crossorigin="anonymous"
></script>


<script>

    const memberBranch = '${JOBMOA_LOGIN_DATA.memberBranch}'

    /*$(document).ready(function() {
        let selectedParticipants = new Set();
        let transferredParticipants = new Set();
        const $sourceCounselor = $('#sourceCounselor');
        const $targetCounselor = $('#targetCounselor');

        // 상담사 선택 시 참여자 목록 로드
        $sourceCounselor.change(function() {
            const counselorId = $(this).val();
            if (counselorId) {
                loadParticipants(counselorId);
                updateButtonStates();
            }
            else {
                $('#participantList').empty();
                $('#transferredList').empty();
            }
        });

        // 상담사 선택 시 참여자 목록 로드
        $targetCounselor.change(function() {
            //const counselorId = $(this).val();
            $('#transferredList').empty();
        });

        function loadParticipants(counselorId, pkArray) {

            $.ajax({
                url: 'transferGetAjax.login',
                method: 'GET',
                dataType: 'json', // 중요: 응답을 JSON으로 파싱
                data: {
                    participantUserid: counselorId,
                    participantBranch: '${JOBMOA_LOGIN_DATA.memberBranch}',
                    participantIDs: pkArray ? pkArray : ''
                }
            }).done(function(data) {
                const list = $('#participantList');
                list.empty();

                // 여기서 data는 이미 배열(객체)이므로 JSON.parse 제거
                data.forEach(function(participant) {
                    const appendHTML =
                        '<div class="list-group-item participant-item">' +
                        '<input type="checkbox" class="form-check-input participant-check" id="'+participant.jobno+'" value="'+participant.jobno+'">' +
                        '<label class="form-check-label ms-2 participant-label" for="'+participant.jobno+'">' +
                        '<div class="d-flex justify-content-between align-items-center w-100">' +
                        '<span class="participant-jobno">'+ participant.jobno +'</span>' +
                        '<span class="participant-name">'+ participant.particName +'</span>' +
                        '<div class="participant-info">' +
                        '<span class="info-item">'+ participant.dob +'</span>' +
                        '<span class="info-item">'+ participant.gender +'</span>' +
                        '</div>' +
                        '</div>' +
                        '</label>' +
                        '</div>';
                    list.append(appendHTML);
                });
            }).fail(function(jqXHR, textStatus, errorThrown) {
                console.error('AJAX 실패 상태:', textStatus);
                console.error('HTTP 상태:', jqXHR.status);
                console.error('오류 메시지:', errorThrown);
                console.error('응답 텍스트:', jqXHR.responseText);
            });
        }

        function loadTransferredParticipants(counselorId, pkArray) {
            $.get('transferGetAjax.login', {
                participantUserid: counselorId,
                participantBranch: '${JOBMOA_LOGIN_DATA.memberBranch}',
                participantIDs: pkArray ? pkArray : ''
            })
                .done(function(data) {
                    const list = $('#transferredList');
                    list.empty();
                    JSON.parse(data).forEach(participant => {
                        let appendHTML = '<div class="list-group-item participant-item">' +
                            '<div class="d-flex justify-content-between align-items-center w-100">'+
                            '<span class="participant-jobno">'+ participant.jobno +'</span>'+
                            '<span class="participant-name">'+ participant.particName +'</span>'+
                            '<div class="participant-info">'+
                            '<span class="info-item">'+ participant.dob +'</span>'+
                            '<span class="info-item">'+ participant.gender +'</span>'+
                            '</div></div></label></div>';

                        list.append(appendHTML);
                    });
                })
                .fail(function(jqXHR, textStatus, errorThrown) {
                    // 상세한 오류 정보 로깅
                    console.error('AJAX 실패 상태:', textStatus);
                    console.error('HTTP 상태:', jqXHR.status);
                    console.error('오류 메시지:', errorThrown);
                    console.error('응답 텍스트:', jqXHR.responseText);
                });
        }

        // 전체 선택 체크박스 이벤트
        const $selectAllSource = $('#selectAllSource');
        $selectAllSource.change(function() {
            changeAllParticipants();
        });

        //전체 선택 체크박스 이벤트
        function changeAllParticipants() {
            const isChecked = $selectAllSource.prop('checked');
            $('.participant-check').prop('checked', isChecked);
            updateSelectedParticipants();
            updateButtonStates();
        }

        // 참여자 선택 시 이벤트
        $(document).on('change', '.participant-check, #targetCounselor', function() {
            updateSelectedParticipants();
            updateButtonStates();
        });

        // 선택 이전 버튼 클릭
        $('#transferSelected').click(function() {
            alertConfirmWarning('선택한 참여자를 이전합니다.', '이전 후 복구는 불가능합니다.', '이전', '취소').then(function(result) {
                if (result) {
                    transferParticipants(Array.from(selectedParticipants));
                }
                else {
                    $selectAllSource.prop('checked', false);
                }
            });
        });

        // 전체 이전 버튼 클릭
        $('#transferAll').click(function() {

            alertConfirmWarning('참여자 전체를 이전합니다.', '이전 후 복구는 불가능합니다.', '이전', '취소').then(function(result) {
               if (result) {
                   $selectAllSource.prop('checked', true);
                   changeAllParticipants();
                   console.log(Array.from(selectedParticipants));
                   transferParticipants(Array.from(transferredParticipants));
               }
               else {
                   $selectAllSource.prop('checked', false);
               }
            });
        });

        function updateSelectedParticipants() {
            selectedParticipants.clear();
            transferredParticipants.clear();
            $('.participant-check:checked').each(function() {
                selectedParticipants.add($(this).val());
                transferredParticipants.add($(this).val());
            });
        }

        function updateButtonStates() {
            const sourceSelected = $sourceCounselor.val();
            const targetSelected = $targetCounselor.val();
            const hasSelectedParticipants = selectedParticipants.size > 0;
            const hasSourceSelectedAndTargetSelected = sourceSelected !== '' && sourceSelected !== null && targetSelected !== '' && targetSelected !== null;

            $('#transferSelected').prop('disabled',!hasSourceSelectedAndTargetSelected || !hasSelectedParticipants);
            $('#transferAll').prop('disabled',!hasSourceSelectedAndTargetSelected);
        }

        function transferParticipants(participantIds) {
            // 이전 API 호출 및 처리
            $.ajax({
                url: 'transferPostAjax.login',
                method: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify({
                    sourceCounselorID: $sourceCounselor.val(),
                    targetCounselorID: $targetCounselor.val(),
                    participantBranch: '${JOBMOA_LOGIN_DATA.memberBranch}',
                    participantIDs: participantIds
                }),
                success: function(response) {
                    console.log("이관 성공 여부 : ["+response+"]");
                    if (response) {
                        alert('데이터 이전이 완료되었습니다.');
                        loadParticipants($sourceCounselor.val());
                        console.log(participantIds);
                        loadTransferredParticipants($targetCounselor.val(),participantIds);
                    }
                    else{
                        alert('데이터 이전에 실패했습니다.')
                    }
                },
                error: function(xhr) {
                    alert('데이터 이전 중 오류가 발생했습니다.');
                }
            });
        }
    });*/
</script>
</html>