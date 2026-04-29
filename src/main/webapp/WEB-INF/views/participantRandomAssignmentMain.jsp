<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 25. 7. 17.
  Time: 오후 3:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 참여자 랜덤 배정</title>
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
    <link rel="stylesheet" href="/css/participantCss/participantRandomAssignment_0.0.2.css" />

    <!-- csv file array change CDN -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-csv/1.0.9/jquery.csv.min.js" integrity="sha512-Y8iWYJDo6HiTo5xtml1g4QqHtl/PO1w+dmUpQfQSOTqKNsMhExfyPN2ncNAe9JuJUSKzwK/b6oaNPop4MXzkwg==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-csv/1.0.9/jquery.csv.js" integrity="sha512-eDkr7sqAJqr3s63mdge3uTyuKVpEbzw3eji7CbGYr8VeM+NtqNajwuAiU31S0buRspDF1mZ8qTSeEZ4v/8b3Gw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>

    <!-- praCSVtoTable JS -->
    <script src="/js/praCSVtoTableJS_0.1.3.js"></script>

    <!-- praDataVerification JS -->
    <script src="/js/praDataVerification_0.0.1.js"></script>

    <!-- table2excel Table to Excel -->
    <script src="https://cdn.rawgit.com/rainabba/jquery-table2excel/1.1.0/dist/jquery.table2excel.min.js"></script>
    <!-- praTableExport JS -->
    <script src="/js/praTableToExcel_0.0.1.js"></script>

    <!-- Bootstrap Datepicker 로드 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/js/bootstrap-datepicker.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/locales/bootstrap-datepicker.ko.min.js" integrity="sha512-L4qpL1ZotXZLLe8Oo0ZyHrj/SweV7CieswUODAAPN/tnqN3PA1P+4qPu5vIryNor6HQ5o22NujIcAZIfyVXwbQ==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <script src="/js/datepickerJS_0.0.1.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.10.0/css/bootstrap-datepicker.min.css">
    <link rel="stylesheet" href="/css/participantCss/datepicker_0.0.1.css">

</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<!--begin::App Wrapper-->
<div class="app-wrapper">
    <!--begin:::App Gnb-->
    <mytag:gnb gnb_main_header="지점관리" gnb_sub_header="참여자 랜덤 배정"/>
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
                    <div class="col-md-12">
                        <div class="title-header col-md-12 pt-2">
                            참여자 랜덤 배정 <span id="helpButton" data-toggle="tooltip" data-placement="top" title="사용 방법 및 파일 설명"><a href="/participantRandomAssignmentManual.jsp" target='_blank'><i class="bi bi-lightbulb-fill"></i>도움말</a></span>
                        </div>
                        <%--<div class="title-sub-header col-md-12" id="helpText">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="alert alert-info">
                                        <h6>랜덤 배정 사용 방법:</h6>
                                        <ul>
                                            <li>csv 파일 찾기 클릭</li>
                                            <li>상담사 랜덤 배정 클릭</li>
                                            <li>배정 상담사 확인 후 저장</li>
                                        </ul>
                                        <h6>CSV 파일 작성 가이드:</h6>
                                        <ul>
                                            <li>첫 번째 줄은 헤더(컬럼명)로 작성해주세요.</li>
                                            <li>필수 컬럼: 참여자 성명, 참여유형, 성별, 생년월일, 모집경로, 경력유무, 학력, 특정계층여부, 진행단계, 출장상담사</li>
                                            <li>데이터에 쉼표(,)가 포함된 경우 따옴표(")로 감싸주세요.</li>
                                            <li>따옴표가 포함된 데이터는 두 개의 따옴표("")로 표시해주세요.</li>
                                            <li>CSV파일 저장시 CSV UTF-8(쉼표로 분리)로 저장해주세요.</li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>--%>
                        <div class="body-content col-md-12">
                            <div class="counselor-container col-md-12 mt-1">
                                <button class="btn btn-secondary float-end" id="excludeBtn">선택 상담사 배정 제외</button>
                            </div>
                            <div class="counselor-container col-md-12 overflow-y-scroll">
                                <table class="table table-striped text-center" id="assign-count-table">
                                    <thead id="assign-count-table-header">
                                    <tr>
                                        <th>상담사 ID</th>
                                        <th>상담사명</th>
                                        <th>입사일</th>
                                        <th>인원 가중치</th>
                                        <th>배정 인원</th>
                                        <th>당일 배정 인원</th>
                                        <th>주간 배정 인원</th>
                                        <th>2주간 배정 인원</th>
                                        <th>당월 배정 인원</th>
                                        <th>1유형</th>
                                        <th>2유형</th>
                                        <th>대졸</th>
                                        <th>고졸</th>
                                        <th>남</th>
                                        <th>여</th>
                                        <th>청년</th>
                                        <th>중장년</th>
                                        <th>특정계층</th>
                                        <th>현재 배정 인원</th>
                                        <th>최대 배정 인원</th>
                                    </tr>
                                    </thead>
                                    <tbody id="assign-count-table-body">
                                    </tbody>
                                </table>
                            </div>
                        </div>

                    </div>
                </div>

                <div class="row">

                    <div class="col-md-12">
                        <div class="section-card col-md-12">
                            <div class="section-title">입력 방식</div>
                            <div class="muted-text">직접 입력 또는 CSV 파일 등록 중 하나를 선택합니다.</div>
                        </div>
                    </div>
                    <div class="col-md-12 d-flex justify-content-center align-items-center">
                        <div class="section-card col-md-4 h-100">
                            <div class="section-title">직접 입력</div>
                            <div class="row g-2">
                                <div class="col-6">
                                    <label class="form-label">참여자 성명</label>
                                    <input type="text" class="form-control form-control-sm" id="manual-name" placeholder="예: 홍길동">
                                </div>
                                <div class="col-6">
                                    <label class="form-label">참여유형</label>
                                    <select class="form-select form-select-sm" id="manual-type">
                                        <option value="1">1유형</option>
                                        <option value="2">2유형</option>
                                    </select>
                                </div>
                                <div class="col-6">
                                    <label class="form-label">성별</label>
                                    <select class="form-select form-select-sm" id="manual-gender">
                                        <option value="남">남</option>
                                        <option value="여">여</option>
                                    </select>
                                </div>
                                <div class="col-6">
                                    <label class="form-label">생년월일</label>
                                    <input type="text" class="datepicker_on form-control form-control-sm" id="manual-birthdate">
                                </div>
                                <div class="col-6">
                                    <label class="form-label">모집경로</label>
                                    <select class="form-select form-select-sm" id="manual-recruitment">
                                        <option selected value="센터배정">센터배정</option>
                                        <option value="자체모집(대학)">자체모집(대학)</option>
                                        <option value="자체모집(고교)">자체모집(고교)</option>
                                        <option value="자체모집(훈련기관)">자체모집(훈련기관)</option>
                                        <option value="자체모집(기타)">자체모집(기타)</option>
                                        <option value="이관">이관</option>
                                    </select>
                                </div>
                                <div class="col-6">
                                    <label class="form-label">경력유무</label>
                                    <input type="number" min="0" class="form-control form-control-sm" id="manual-career" placeholder="예: 0">
                                </div>
                                <div class="col-6">
                                    <label class="form-label">학력</label>
                                    <select class="form-select form-select-sm" id="manual-education-type">
                                        <option value="고졸">고졸</option>
                                        <option value="검정고시">검정고시</option>
                                        <option value="초대졸">초대졸</option>
                                        <option value="대졸">대졸</option>
                                        <option value="대학원">대학원</option>
                                    </select>
                                </div>
                                <div class="col-6">
                                    <label class="form-label">특정계층 여부</label>
                                    <select class="form-select form-select-sm" id="manual-special">
                                        <option value="해당 없음">해당 없음</option>
                                        <option value="해당">해당</option>
                                    </select>
                                </div>
                                <div class="col-6">
                                    <label class="form-label">진행단계</label>
                                    <select class="form-select form-select-sm" id="manual-stage">
                                        <option selected value="IAP 전">IAP 전</option>
                                        <option value="IAP 후">IAP 후</option>
                                        <option value="미고보">미고보</option>
                                        <option value="고보일반">고보일반</option>
                                        <option value="등록창업">등록창업</option>
                                        <option value="미등록창업">미등록창업</option>
                                        <option value="미취업사후관리">미취업사후관리</option>
                                        <option value="미취업사후종료">미취업사후종료</option>
                                        <option value="유예">유예</option>
                                        <option value="취소">취소</option>
                                        <option value="이관">이관</option>
                                        <option value="중단">중단</option>
                                    </select>
                                </div>
                                <div class="col-6">
                                    <label class="form-label">출장상담사(아이디)</label>
                                    <select class="form-control form-control-sm" id="manual-travel">
                                    </select>
                                </div>
                            </div>
                            <div class="mt-2">
                                <button type="button" class="btn btn-sm btn-outline-secondary" id="manual-add">목록에 추가</button>
                                <span class="muted-text ms-2">입력값은 하단 배정 목록에 반영됩니다.</span>
                            </div>
                        </div>

                        <div class="section-card col-md-4 h-100">
                            <div class="section-title">산정식 설정</div>
                            <div class="muted-text mb-2">가중치 합계는 1.0 권장</div>
                            <div class="muted-text mb-2">인원 가중치는 상담사별 값이며, 낮을수록 배정이 줄어듭니다.</div>
                            <div class="row g-2">
                                <div class="col-6">
                                    <label class="form-label">C_load</label>
                                    <br>
                                    <small class="text-muted">현재 진행 건수와</small>
                                    <br>
                                    <small class="text-muted">전체 평균의 차이(제곱)</small>
                                    <small class="text-muted"></small>
                                    <input type="number" step="0.01" min="0" max="1" class="form-control form-control-sm" id="weight-load" value="0.45">
                                </div>
                                <div class="col-6">
                                    <label class="form-label">C_fair</label>
                                    <br>
                                    <small class="text-muted">유형/성별/연령/특정계층 분포 균형 비용</small>
                                    <br>
                                    <small class="text-muted">학력(대졸/고졸) 균형 포함</small>
                                    <input type="number" step="0.01" min="0" max="1" class="form-control form-control-sm" id="weight-fair" value="0.35">
                                </div>
                                <div class="col-6">
                                    <label class="form-label">C_streak</label>
                                    <br>
                                    <small class="text-muted">연속 배정 횟수 제곱(쏠림 방지)</small>
                                    <input type="number" step="0.01" min="0" max="1" class="form-control form-control-sm" id="weight-streak" value="0.10">
                                </div>
                                <div class="col-6">
                                    <label class="form-label">C_pace</label>
                                    <br>
                                    <small class="text-muted">연간 배정 수와 전체 평균의 차이(제곱)</small>
                                    <input type="number" step="0.01" min="0" max="1" class="form-control form-control-sm" id="weight-pace" value="0.10">
                                </div>
                                <div class="col-6">
                                    <label class="form-label">불균형 임계값</label>
                                    <input type="number" min="1" class="form-control form-control-sm" id="gap-threshold" value="5">
                                </div>
                                <div class="col-6">
                                    <label class="form-label">일일 한도</label>
                                    <input type="number" min="1" class="form-control form-control-sm" id="daily-limit" value="3">
                                </div>
                            </div>
                            <div class="mt-2">
                                <div class="muted-text">경력 그룹 기간 한도(7/14/30일)</div>
                                <div class="row g-2 mt-1">
                                    <div class="col-4">
                                        <label class="form-label">G1</label>
                                        <br>
                                        <small class="text-muted">입사일로 3개월 이하 그룹</small>
                                        <input type="text" class="form-control form-control-sm" id="limit-g1" value="3/6/12">
                                    </div>
                                    <div class="col-4">
                                        <label class="form-label">G2</label>
                                        <br>
                                        <small class="text-muted">입사일로 4~11개월 그룹</small>
                                        <input type="text" class="form-control form-control-sm" id="limit-g2" value="5/10/14">
                                    </div>
                                    <div class="col-4">
                                        <label class="form-label">G3</label>
                                        <br>
                                        <small class="text-muted">입사일로 12개월 이상 그룹</small>
                                        <input type="text" class="form-control form-control-sm" id="limit-g3" value="8/15/15">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="section-card col-md-4 h-100">
                            <div class="section-title">파일 등록</div>
                            <div class="muted-text mb-2">
                                CSV 컬럼 순서: 참여자 성명, 참여유형, 성별, 생년월일, 모집경로, 경력유무, 학력, 특정계층여부, 진행단계, 출장상담사
                            </div>
                            <div class="file-input-container col-md-12 align-content-end">
                                <label for="file-input" class="file-button-label"><i class="bi bi-filetype-csv"></i> 파일 찾기</label>
                                <input type="file" id="file-input" class="file-input" accept=".csv">
                                <a href="/templateFolder/Template.csv"><button id="template-button"><i class="bi bi-download"></i> 템플릿 다운로드</button></a>

                                <div class="response-text-div pb-2">경고 내용 출력 부분</div>
                            </div>
                        </div>
                    </div>

                </div>

                <div class="row">
                    <div class="content-body col-md-12">
                        <!-- csvData ID를 JavaScript와 일치시킴 -->
                        <div class="csvData" id="csvData">
                            <div class="counselor-container col-md-12 mt-1">
                                <button id="save-button" class="save-button mt-1"><i class="bi bi-save-fill"></i> 저장</button>
                                <label id="random-button-label" class="random-button-label"><i class="bi bi-shuffle"></i> 랜덤 배정</label>
                                <button id="score-only-random-button" class="score-only-random-button"><i class="bi bi-shuffle"></i> 점수만 반영 배정</button>
                                <label class="reset-button-label"><i class="bi bi-arrow-counterclockwise"></i> 초기화</label>
                                <button id="varification-button" class="varification-button"><i class="bi bi-check-circle-fill"></i> 데이터 검증</button>
                                <button class="btn btn-success float-end" id="excel-download-btn" onclick="exportToExcel()">Excel 다운로드</button>
                            </div>
                            <table class="table table-striped" id="pra-csv-table">
                                <thead class="csv-data-header">
                                <tr>
                                    <th class="">번호</th>
                                    <th>상담사 ID</th>
                                    <th class="csv-column">참여자 성명</th>
                                    <th class="csv-column">참여유형</th>
                                    <th class="csv-column">성별</th>
                                    <th class="csv-column">생년월일</th>
                                    <th class="csv-column">모집경로</th>
                                    <th class="csv-column">경력유무</th>
                                    <th class="csv-column">학력</th>
                                    <th class="csv-column">특정계층여부</th>
                                    <th class="csv-column">진행단계</th>
                                    <th class="csv-column">출장상담사</th>
                                </tr>
                                </thead>
                                <tbody class="csv-data" id="csv-data">
                                </tbody>
                            </table>
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

<!-- 상담사 데이터 초기화 -->
<script>
    // 상담사 데이터를 전역 변수로 설정 (실제 데이터로 교체 필요)
    let currentCounselor = ${assignData};
    console.log(currentCounselor);

    // 배정 제외 인원 배열
    let excludedPersonnel = {};

    // 페이지 로드 시 상담사 테이블 초기화
    $(document).ready(function() {
        refreshCounselorTableWithFixed();

        // 초기화 버튼 표시
        $('.reset-button-label').show();
    });
</script>

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

</html>
