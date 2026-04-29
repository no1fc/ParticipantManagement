<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 25. 4. 22.
  Time: 오전 10:27
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
  <link rel="stylesheet" href="/css/adminlte.css" />
  <!--end::Required Plugin(AdminLTE)-->
  <link rel="stylesheet" href="/css/participantCss/custom-modern_0.0.1.css" />
  <link rel="stylesheet" href="/css/participantCss/branchUserManagement_0.0.2.css" />

  <!-- Custom sweetAlert_0.0.1.js -->
  <script src="/js/sweetAlert_0.0.1.js"></script>

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
  <!-- SweetAlert2 라이브러리 추가 -->
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
  <!--begin:::App Gnb-->
  <mytag:gnb gnb_main_header="지점관리" gnb_sub_header="아이디 관리"/>
  <!--end:::App Gnb-->
  <!--begin:::App main content-->
  <!--begin::App Main-->
  <main class="app-main">
    <!--begin::App Content-->
    <div class="app-content">
      <!--begin::Main content-->
      <div class="container-fluid">
        <!-- 필요 본문 내용은 이쪽에 만들어 주시면 됩니다. -->
        <div class="row">
          <div class="col-12">
            <div class="card-modern">
              <div class="card-header d-flex justify-content-between align-items-center">
                <div>
                  <h3 class="card-title">상담사 관리</h3>
                  <p class="card-subtitle">드래그로 순서를 변경할 수 있습니다.</p>
                </div>
                <button type="button" class="btn btn-primary ms-auto" data-bs-toggle="modal" data-bs-target="#addParticipantModal">
                  <i class="bi bi-person-plus"></i> 신규 상담사 추가
                </button>
              </div>
              <!-- /.card-header -->
              <div class="card-body">
                <table id="participantTable" class="table table-bordered table-striped text-center">
                  <thead>
                  <tr>
                    <th width="5%">순서</th>
                    <th width="15%">아이디</th>
                    <th width="15%">이름</th>
                    <th width="20%">이메일</th>
                    <th width="15%">입사일</th>
                    <th width="10%">상태</th>
                    <th width="20%">관리</th>
                  </tr>
                  </thead>
                  <tbody id="sortableList" class="sortable">
                  <!-- 상담사 목록이 여기에 동적으로 추가됩니다 -->
                  <c:forEach begin="1" end="10" var="i" varStatus="status">
                    <tr class="user-item drag-handle">
                      <td class="position-input"><i class="bi bi-grip-vertical drag-icon"></i>${status.count}</td>
                      <td>${i}</td>
                      <td></td>
                      <td></td>
                      <td></td>
                      <td></td>
                      <td>
                        <button type="button" class="btn btn-warning btn-sm statusChange">상태변경</button>
                        <button type="button" class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#passwordModal">비밀번호 변경</button>
                      </td>
                    </tr>
                  </c:forEach>
                  </tbody>
                </table>
              </div>
              <!-- /.card-body -->
            </div>
            <!-- /.card -->
          </div>
        </div>
      </div>

      <!-- 비밀번호 변경 모달 -->
      <div class="modal fade" id="passwordModal" tabindex="-1" aria-labelledby="passwordModalLabel" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="passwordModalLabel">비밀번호 변경</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <input type="hidden" id="passwordUserId">
              <div class="mb-3">
                <label for="newPassword" class="form-label">새 비밀번호</label>
                <input type="password" class="form-control" id="newPassword" placeholder="새 비밀번호 입력">
              </div>
              <div class="mb-3">
                <label for="confirmPassword" class="form-label">비밀번호 확인</label>
                <input type="password" class="form-control" id="confirmPassword" placeholder="비밀번호 재입력">
                <div class="invalid-feedback" id="passwordMismatch">
                  비밀번호가 일치하지 않습니다.
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
              <button type="button" class="btn btn-primary" id="savePasswordBtn">저장</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 신규 상담사 추가 모달 -->
      <div class="modal fade" id="addParticipantModal" tabindex="-1" aria-labelledby="addParticipantModalLabel" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="addParticipantModalLabel">신규 상담사 추가</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label for="newUserId" class="form-label">아이디 <small>(필수)</small></label>
                <input type="text" class="form-control" id="newUserId" placeholder="아이디 입력 (예: namsd)">
                <small class="form-text text-muted">성(이름약어) 형식으로 입력하세요. 예: 남상도 → namsd</small>
              </div>
              <div class="mb-3">
                <label for="newUserName" class="form-label">이름 <small>(필수)</small></label>
                <input type="text" class="form-control" id="newUserName" placeholder="이름 입력">
              </div>
              <div class="mb-3 d-none">
                <label for="newUserBranchId" class="form-label">지점</label>
                <input type="text" class="form-control" id="newUserBranchId" readonly>
              </div>
              <div class="mb-3">
                <label for="newUserPassword" class="form-label">초기 비밀번호</label>
                <input type="password" class="form-control" id="newUserPassword" placeholder="초기 비밀번호 입력">
                <small class="form-text text-muted">입력하지 않으면 시스템 기본값으로 설정됩니다.(기본 : jobmoa100!)</small>
              </div>
              <div class="mb-3" id="emailFormGroup">
                <label for="newUserEmail" class="form-label">이메일</label>
                <input type="email" class="form-control" id="newUserEmail" placeholder="이메일 입력" readonly>
              </div>
              <div class="mb-3" id="manualEmailGroup">
                <label for="manualEmail" class="form-label">이메일 직접 입력</label>
                <input type="email" class="form-control" id="manualEmail" placeholder="이메일 직접 입력">
                <small class="form-text text-muted">입력하지 않으면 시스템 기본값으로 설정됩니다.(기본:아이디@jobmoa.com)</small>
              </div>
              <div class="mb-3">
                <label for="newUserHireDate" class="form-label">입사일 <small>(필수)</small></label>
                <input type="date" class="form-control" id="newUserHireDate" required>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
              <button type="button" class="btn btn-primary" id="saveNewUserBtn">저장</button>
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
<script src="https://cdn.jsdelivr.net/npm/sortablejs@1.15.0/Sortable.min.js"></script>
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

<%--table 순서 변경 관련해서 수정하고 유저 비밀번호, 신규참여자 등록, 사용정지 여부 등록하는 jQuery를 추가한다.--%>
<script>
  $(document).ready(function () {
    // sortable 초기화
    $("#sortableList").sortable({
      items: ".user-item",             // 정렬 대상
      handle: ".drag-handle",          // 드래그 핸들
      axis: "y",                       // 세로 방향으로만 이동 가능
      cursor: "move",                  // 드래그 시 커서 모양
      opacity: 0.6,                    // 드래그 중인 항목의 투명도
      update: function(event, ui) {    // 정렬이 완료된 후 실행
        updateOrder();
        // updateTbodyOrder();
      }
    });

    // 순서 업데이트 함수
    function updateOrder() {
      $('.user-item').each(function(index) {
        // 순서 번호 업데이트
        $(this).find('.position-input').text(index + 1);

        // 선택적: 서버에 순서 정보 전송
        // let userId = $(this).data('id');
        let newPosition = index + 1;

        // 콘솔에 순서 변경 로그 출력
        console.log(`User ID: ${userId}, New Position: ${newPosition}`);
      });
    }

    // // tbody 순서 업데이트 함수
    // function updateTbodyOrder() {
    //   const $tbody = $('#status-tbody');
    //   const $rows = $tbody.find('tr').toArray();
    //
    //   // user-item의 순서대로 tbody의 행 재정렬
    //   $('.user-item').each(function(index) {
    //     const userId = $(this).data('id');
    //     const $matchingRow = $rows.find(row => $(row).data('id') === userId);
    //
    //     if ($matchingRow) {
    //       $tbody.append($matchingRow);
    //     }
    //   });
    // }

  });
</script>
