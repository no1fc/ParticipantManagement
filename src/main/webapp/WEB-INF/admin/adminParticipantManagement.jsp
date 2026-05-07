<%--
  참여자 관리 페이지 (Participant Management)
  Created by: JobMoa Admin System
  Date: 2026-03-17
  Description: 참여자 정보 조회, 검색, 상세 조회, 엑셀 다운로드
  DB: J_참여자관리, J_참여자관리_자격증, J_참여자관리_직업훈련
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 참여자 관리</title>
    <!-- Jobmoa 로고 탭 이미지 -->
    <mytag:Logo/>

    <!--begin::Primary Meta Tags-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="title" content="AdminLTE v4 | Participant Management" />
    <meta name="author" content="JobMoa" />
    <!--end::Primary Meta Tags-->

    <!-- jQuery JS -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>

    <!--begin::Fonts-->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.0.12/index.css"
          integrity="sha256-tXJfXfp6Ewt1ilPzLDtQnJV4hclT9XuaZUKyUvmyr+Q=" crossorigin="anonymous" />
    <!--end::Fonts-->

    <!--begin::Third Party Plugin(OverlayScrollbars)-->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/styles/overlayscrollbars.min.css"
          integrity="sha256-tZHrRjVqNSRyWg2wbppGnT833E/Ys0DHWGwT04GiqQg=" crossorigin="anonymous" />
    <!--end::Third Party Plugin(OverlayScrollbars)-->

    <!--begin::Third Party Plugin(Bootstrap Icons)-->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css"
          integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI=" crossorigin="anonymous" />
    <!--end::Third Party Plugin(Bootstrap Icons)-->

    <!--begin::Required Plugin(AdminLTE)-->
    <link rel="stylesheet" href="/css/adminlte.css" />
    <!--end::Required Plugin(AdminLTE)-->

    <!-- Font Awesome -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />

    <!-- DataTables -->
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap5.min.css">
    <script src="https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.4/js/dataTables.bootstrap5.min.js"></script>

    <!-- SweetAlert2 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
    <script src="/js/sweetAlert_0.0.1.js"></script>

    <!-- Admin Common Design System -->
    <link rel="stylesheet" href="/css/adminCss/adminCommon_0.0.1.css">

    <!-- Page-specific CSS -->
    <link rel="stylesheet" href="/css/adminCss/adminParticipantManagement_0.0.2.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin::App Gnb-->
    <mytag:gnb gnb_main_header="관리자" gnb_sub_header="참여자 관리"/>
    <!--end::App Gnb-->

    <!--begin::App Main-->
    <main class="app-main">
        <!--begin::App Content-->
        <div class="app-content">
            <!--begin::Container-->
            <div class="container-fluid">

                <!-- 페이지 헤더 -->
                <div class="admin-page-header">
                    <div class="admin-page-title">
                        <h4><i class="bi bi-people"></i> 참여자 관리</h4>
                        <p>참여자 정보를 조회하고 상세 내용을 확인할 수 있습니다.</p>
                    </div>
                    <div class="admin-page-actions">
                        <button class="btn btn-outline-secondary" onclick="loadParticipants()" title="새로고침">
                            <i class="bi bi-arrow-clockwise"></i>
                        </button>
                        <button class="btn btn-success" onclick="exportToExcel()">
                            <i class="bi bi-file-earmark-excel"></i> 현재 검색 결과 엑셀 다운로드
                        </button>
                    </div>
                </div>

                <!-- 메트릭 카드 -->
                <div class="admin-metric-grid">
                    <div class="admin-metric-card">
                        <div class="admin-metric-icon icon-total"><i class="bi bi-people"></i></div>
                        <div class="admin-metric-info">
                            <div class="admin-metric-value" id="metricTotal">0</div>
                            <div class="admin-metric-label">전체 참여자</div>
                        </div>
                    </div>
                    <div class="admin-metric-card">
                        <div class="admin-metric-icon icon-active"><i class="bi bi-play-circle"></i></div>
                        <div class="admin-metric-info">
                            <div class="admin-metric-value" id="metricActive">0</div>
                            <div class="admin-metric-label">진행중</div>
                        </div>
                    </div>
                    <div class="admin-metric-card">
                        <div class="admin-metric-icon icon-closed"><i class="bi bi-stop-circle"></i></div>
                        <div class="admin-metric-info">
                            <div class="admin-metric-value" id="metricClosed">0</div>
                            <div class="admin-metric-label">마감</div>
                        </div>
                    </div>
                    <div class="admin-metric-card">
                        <div class="admin-metric-icon icon-employed"><i class="bi bi-briefcase"></i></div>
                        <div class="admin-metric-info">
                            <div class="admin-metric-value" id="metricEmployed">0</div>
                            <div class="admin-metric-label">취업</div>
                        </div>
                    </div>
                    <div class="admin-metric-card">
                        <div class="admin-metric-icon icon-recent"><i class="bi bi-clock-history"></i></div>
                        <div class="admin-metric-info">
                            <div class="admin-metric-value" id="metricRecent">0</div>
                            <div class="admin-metric-label">최근 등록</div>
                        </div>
                    </div>
                </div>

                <!-- 검색 필터 패널 -->
                <div class="admin-filter-panel">
                    <div class="admin-filter-title"><i class="bi bi-search"></i> 참여자 검색</div>
                    <div class="admin-filter-row">
                        <div class="admin-filter-group">
                            <label>구직번호</label>
                            <input type="text" class="form-control" id="searchJobNo" placeholder="구직번호 입력">
                        </div>
                        <div class="admin-filter-group">
                            <label>참여자명</label>
                            <input type="text" class="form-control" id="searchName" placeholder="이름 입력">
                        </div>
                        <div class="admin-filter-group">
                            <label>지점</label>
                            <select class="form-select" id="searchBranch">
                                <option value="">전체</option>
                            </select>
                        </div>
                        <div class="admin-filter-group">
                            <label>상담사</label>
                            <select class="form-select" id="searchCounselor">
                                <option value="">전체</option>
                            </select>
                        </div>
                        <div class="admin-filter-actions">
                            <button class="btn btn-primary" onclick="searchParticipants()"><i class="bi bi-search"></i> 검색</button>
                            <button class="btn btn-outline-secondary" onclick="resetParticipantFilters()"><i class="bi bi-arrow-counterclockwise"></i> 초기화</button>
                            <button class="btn btn-outline-secondary" id="btnToggleAdvanced"><i class="bi bi-chevron-down"></i> 고급검색</button>
                        </div>
                    </div>
                    <!-- 고급 검색 필터 -->
                    <div class="admin-advanced-filters" id="advancedFilters">
                        <div class="admin-filter-row">
                            <div class="admin-filter-group">
                                <label>진행단계</label>
                                <select class="form-select" id="searchStatus">
                                    <option value="">전체</option>
                                    <option value="IAP전">IAP전</option>
                                    <option value="IAP후">IAP후</option>
                                    <option value="취업">취업</option>
                                </select>
                            </div>
                            <div class="admin-filter-group">
                                <label>등록일 시작</label>
                                <input type="date" class="form-control" id="searchStartDate">
                            </div>
                            <div class="admin-filter-group">
                                <label>등록일 종료</label>
                                <input type="date" class="form-control" id="searchEndDate">
                            </div>
                            <div class="admin-filter-group">
                                <label>마감여부</label>
                                <select class="form-select" id="searchClosed">
                                    <option value="">전체</option>
                                    <option value="0">진행중</option>
                                    <option value="1">마감</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 참여자 목록 테이블 -->
                <div class="admin-table-card">
                    <div class="table-responsive">
                        <table id="participantTable" class="table admin-data-table mb-0">
                            <thead>
                            <tr>
                                <th class="col-check"><input type="checkbox" class="form-check-input" id="selectAll"></th>
                                <th>구직번호</th>
                                <th>참여자명</th>
                                <th>생년월일</th>
                                <th>성별</th>
                                <th>지점</th>
                                <th>상담사</th>
                                <th>진행단계</th>
                                <th>등록일</th>
                                <th>마감여부</th>
                                <th>액션</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>

            </div>
            <!--end::Container-->
        </div>
        <!--end::App Content-->
    </main>
    <!--end::App Main-->

    <!--begin::Footer-->
    <footer class="app-footer">
        <div class="float-end d-none d-sm-inline">JobMoa Admin System v1.0</div>
        <strong>Copyright &copy; 2024-2026 JobMoa.</strong> All rights reserved.
    </footer>
    <!--end::Footer-->
</div>
<!--end::App Wrapper-->

<!-- 참여자 상세 오프캔버스 -->
<div class="offcanvas offcanvas-end admin-offcanvas" tabindex="-1" id="participantOffcanvas">
    <div class="offcanvas-header">
        <h5 class="offcanvas-title" id="offcanvasTitle">참여자 상세</h5>
        <button type="button" class="btn-close" data-bs-dismiss="offcanvas"></button>
    </div>
    <div class="offcanvas-body">
        <!-- 기본 정보 -->
        <div class="participant-detail-section">
            <h6><i class="bi bi-person"></i> 기본 정보</h6>
            <div class="detail-row"><span class="detail-label">구직번호</span><span class="detail-value" id="detailJobNo"></span></div>
            <div class="detail-row"><span class="detail-label">참여자명</span><span class="detail-value" id="detailName"></span></div>
            <div class="detail-row"><span class="detail-label">생년월일</span><span class="detail-value" id="detailBirthDate"></span></div>
            <div class="detail-row"><span class="detail-label">성별</span><span class="detail-value" id="detailGender"></span></div>
            <div class="detail-row"><span class="detail-label">지점</span><span class="detail-value" id="detailBranch"></span></div>
            <div class="detail-row"><span class="detail-label">모집경로</span><span class="detail-value" id="detailRecruitPath"></span></div>
            <div class="detail-row"><span class="detail-label">참여유형</span><span class="detail-value" id="detailParticipationType"></span></div>
        </div>
        <!-- 학력 및 경력 -->
        <div class="participant-detail-section">
            <h6><i class="bi bi-mortarboard"></i> 학력 및 경력</h6>
            <div class="detail-row"><span class="detail-label">학력</span><span class="detail-value" id="detailEducation"></span></div>
            <div class="detail-row"><span class="detail-label">경력</span><span class="detail-value" id="detailCareer"></span></div>
            <div class="detail-row"><span class="detail-label">특정계층</span><span class="detail-value" id="detailSpecialClass"></span></div>
            <div class="detail-row"><span class="detail-label">취업역량</span><span class="detail-value" id="detailEmploymentCapacity"></span></div>
        </div>
        <!-- 상담 정보 -->
        <div class="participant-detail-section">
            <h6><i class="bi bi-chat-dots"></i> 상담 정보</h6>
            <div class="detail-row"><span class="detail-label">상담사</span><span class="detail-value" id="detailCounselor"></span></div>
            <div class="detail-row"><span class="detail-label">진행단계</span><span class="detail-value" id="detailProgressStage"></span></div>
        </div>
        <!-- 희망 정보 -->
        <div class="participant-detail-section">
            <h6><i class="bi bi-star"></i> 희망 정보</h6>
            <div class="detail-row"><span class="detail-label">희망직무</span><span class="detail-value" id="detailDesiredJob"></span></div>
            <div class="detail-row"><span class="detail-label">희망급여</span><span class="detail-value" id="detailDesiredSalary"></span></div>
        </div>
        <!-- 메모 -->
        <div class="participant-detail-section" style="border-bottom: none;">
            <h6><i class="bi bi-journal-text"></i> 메모</h6>
            <p class="detail-value" id="detailMemo" style="white-space: pre-wrap;"></p>
        </div>
    </div>
</div>

<!--begin::Script-->
<script src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es5.min.js"
        integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ=" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
        integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+" crossorigin="anonymous"></script>
<script src="/js/adminlte.js"></script>

<script src="/js/adminJs/adminParticipantManagement_0.0.3.js"></script>
<!--end::Script-->

<script>
    // OverlayScrollbars 초기화
    const {
        OverlayScrollbars: OS2
    } = OverlayScrollbarsGlobal;
    if (document.querySelector('.app-sidebar-wrapper')) {
        OS2(document.querySelector('.app-sidebar-wrapper'), {});
    }
</script>

</body>
</html>
