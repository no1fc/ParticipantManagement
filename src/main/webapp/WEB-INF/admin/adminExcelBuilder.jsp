<%--
  엑셀 다운로드 설정 페이지 (Excel Builder)
  Created by: JobMoa Admin System
  Description: 참여자 정보, 희망직무, 자격증, 직업훈련 시트를 선택하여 커스텀 엑셀 다운로드
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>잡모아 - 엑셀 다운로드 설정</title>
    <mytag:Logo/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@fontsource/source-sans-3@5.0.12/index.css" integrity="sha256-tXJfXfp6Ewt1ilPzLDtQnJV4hclT9XuaZUKyUvmyr+Q=" crossorigin="anonymous" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/styles/overlayscrollbars.min.css" integrity="sha256-tZHrRjVqNSRyWg2wbppGnT833E/Ys0DHWGwT04GiqQg=" crossorigin="anonymous" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" integrity="sha256-9kPW/n5nn53j4WMRYAxe9c1rCY96Oogo/MKSVdKzPmI=" crossorigin="anonymous" />
    <link rel="stylesheet" href="/css/adminlte.css" />
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.4.10/dist/sweetalert2.min.js"></script>
    <script src="/js/sweetAlert_0.0.1.js"></script>
    <link rel="stylesheet" href="/css/adminCss/adminCommon_0.0.1.css">
    <link rel="stylesheet" href="/css/adminCss/adminExcelBuilder_0.0.1.css">
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">

<div class="app-wrapper">
    <mytag:gnb gnb_main_header="관리자" gnb_sub_header="엑셀 다운로드 설정"/>

    <main class="app-main">
        <div class="app-content">
            <div class="container-fluid">

                <!-- 페이지 헤더 -->
                <div class="admin-page-header">
                    <div class="admin-page-title">
                        <h4><i class="bi bi-file-earmark-excel"></i> 엑셀 다운로드 설정</h4>
                        <p>다운로드할 데이터 시트와 필터를 설정합니다.</p>
                    </div>
                </div>

                <!-- 다운로드 시트 선택 -->
                <div class="excel-section-title"><i class="bi bi-check2-square"></i> 다운로드 시트 선택</div>
                <div class="admin-sheet-selector">
                    <div class="admin-sheet-option checked">
                        <i class="bi bi-people sheet-icon"></i>
                        <span class="sheet-name">참여자 기본정보</span>
                        <span class="sheet-desc">컬럼 선택 가능</span>
                        <input type="checkbox" name="excelSheet" value="main" checked>
                    </div>
                    <div class="admin-sheet-option checked">
                        <i class="bi bi-star sheet-icon"></i>
                        <span class="sheet-name">희망직무</span>
                        <span class="sheet-desc">직무/카테고리 정보</span>
                        <input type="checkbox" name="excelSheet" value="wishJob" checked>
                    </div>
                    <div class="admin-sheet-option checked">
                        <i class="bi bi-award sheet-icon"></i>
                        <span class="sheet-name">자격증</span>
                        <span class="sheet-desc">자격증 목록</span>
                        <input type="checkbox" name="excelSheet" value="certificate" checked>
                    </div>
                    <div class="admin-sheet-option checked">
                        <i class="bi bi-book sheet-icon"></i>
                        <span class="sheet-name">직업훈련</span>
                        <span class="sheet-desc">직업훈련 목록</span>
                        <input type="checkbox" name="excelSheet" value="training" checked>
                    </div>
                </div>

                <!-- 컬럼 선택 (참여자 기본정보) -->
                <div id="columnSelectionPanel" class="admin-filter-panel" style="margin-bottom: 20px;">
                    <div class="admin-filter-title">
                        <i class="bi bi-columns-gap"></i> 참여자 기본정보 컬럼 선택
                        <button class="btn btn-sm btn-outline-primary ms-auto" onclick="toggleAllColumns(true)">전체 선택</button>
                        <button class="btn btn-sm btn-outline-secondary ms-2" onclick="toggleAllColumns(false)">전체 해제</button>
                    </div>

                    <div class="column-group mb-3">
                        <div class="d-flex align-items-center mb-2">
                            <strong style="font-size:0.85rem; color:var(--admin-text);" class="me-2">기본 정보</strong>
                            <input type="checkbox" class="form-check-input group-toggle" checked onchange="toggleGroup('basic', this.checked)">
                        </div>
                        <div class="row g-2">
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-basic" name="excelColumn" value="jobNo" checked> <span class="form-check-label" style="font-size:0.8rem;">구직번호</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-basic" name="excelColumn" value="participantRegDate" checked> <span class="form-check-label" style="font-size:0.8rem;">등록일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-basic" name="excelColumn" value="counselorAccount" checked> <span class="form-check-label" style="font-size:0.8rem;">전담자 계정</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-basic" name="excelColumn" value="counselorName" checked> <span class="form-check-label" style="font-size:0.8rem;">상담사명</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-basic" name="excelColumn" value="participantName" checked> <span class="form-check-label" style="font-size:0.8rem;">참여자명</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-basic" name="excelColumn" value="birthDate" checked> <span class="form-check-label" style="font-size:0.8rem;">생년월일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-basic" name="excelColumn" value="gender" checked> <span class="form-check-label" style="font-size:0.8rem;">성별</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-basic" name="excelColumn" value="branch" checked> <span class="form-check-label" style="font-size:0.8rem;">지점</span></label></div>
                        </div>
                    </div>

                    <div class="column-group mb-3">
                        <div class="d-flex align-items-center mb-2">
                            <strong style="font-size:0.85rem; color:var(--admin-text);" class="me-2">교육/경력</strong>
                            <input type="checkbox" class="form-check-input group-toggle" checked onchange="toggleGroup('edu', this.checked)">
                        </div>
                        <div class="row g-2">
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-edu" name="excelColumn" value="recruitPath" checked> <span class="form-check-label" style="font-size:0.8rem;">모집경로</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-edu" name="excelColumn" value="participationType" checked> <span class="form-check-label" style="font-size:0.8rem;">참여유형</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-edu" name="excelColumn" value="schoolName" checked> <span class="form-check-label" style="font-size:0.8rem;">학교명</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-edu" name="excelColumn" value="major" checked> <span class="form-check-label" style="font-size:0.8rem;">전공</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-edu" name="excelColumn" value="address" checked> <span class="form-check-label" style="font-size:0.8rem;">주소</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-edu" name="excelColumn" value="career" checked> <span class="form-check-label" style="font-size:0.8rem;">경력</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-edu" name="excelColumn" value="education" checked> <span class="form-check-label" style="font-size:0.8rem;">학력</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-edu" name="excelColumn" value="specialClass" checked> <span class="form-check-label" style="font-size:0.8rem;">특정계층</span></label></div>
                        </div>
                    </div>

                    <div class="column-group mb-3">
                        <div class="d-flex align-items-center mb-2">
                            <strong style="font-size:0.85rem; color:var(--admin-text);" class="me-2">상담 정보</strong>
                            <input type="checkbox" class="form-check-input group-toggle" checked onchange="toggleGroup('counsel', this.checked)">
                        </div>
                        <div class="row g-2">
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-counsel" name="excelColumn" value="placementRequest" checked> <span class="form-check-label" style="font-size:0.8rem;">알선요청</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-counsel" name="excelColumn" value="employmentCapacity" checked> <span class="form-check-label" style="font-size:0.8rem;">취업역량</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-counsel" name="excelColumn" value="lastCounselDate" checked> <span class="form-check-label" style="font-size:0.8rem;">최근상담일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-counsel" name="excelColumn" value="progressStage" checked> <span class="form-check-label" style="font-size:0.8rem;">진행단계</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-counsel" name="excelColumn" value="initialCounselDate" checked> <span class="form-check-label" style="font-size:0.8rem;">초기상담일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-counsel" name="excelColumn" value="jobExpiryDate" checked> <span class="form-check-label" style="font-size:0.8rem;">구직만료일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-counsel" name="excelColumn" value="iapCompletionDate" checked> <span class="form-check-label" style="font-size:0.8rem;">IAP수료일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-counsel" name="excelColumn" value="stage3EntryDate" checked> <span class="form-check-label" style="font-size:0.8rem;">3단계진입일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-counsel" name="excelColumn" value="periodExpiryDate" checked> <span class="form-check-label" style="font-size:0.8rem;">기간만료일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-counsel" name="excelColumn" value="clinicDate" checked> <span class="form-check-label" style="font-size:0.8rem;">클리닉실시일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-counsel" name="excelColumn" value="intensivePlacement" checked> <span class="form-check-label" style="font-size:0.8rem;">집중알선여부</span></label></div>
                        </div>
                    </div>

                    <div class="column-group mb-3">
                        <div class="d-flex align-items-center mb-2">
                            <strong style="font-size:0.85rem; color:var(--admin-text);" class="me-2">희망/급여</strong>
                            <input type="checkbox" class="form-check-input group-toggle" checked onchange="toggleGroup('wish', this.checked)">
                        </div>
                        <div class="row g-2">
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-wish" name="excelColumn" value="desiredJob" checked> <span class="form-check-label" style="font-size:0.8rem;">희망직무</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-wish" name="excelColumn" value="desiredSalary" checked> <span class="form-check-label" style="font-size:0.8rem;">희망급여</span></label></div>
                        </div>
                    </div>

                    <div class="column-group mb-3">
                        <div class="d-flex align-items-center mb-2">
                            <strong style="font-size:0.85rem; color:var(--admin-text);" class="me-2">취업 정보</strong>
                            <input type="checkbox" class="form-check-input group-toggle" checked onchange="toggleGroup('emp', this.checked)">
                        </div>
                        <div class="row g-2">
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-emp" name="excelColumn" value="employmentDate" checked> <span class="form-check-label" style="font-size:0.8rem;">취창업일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-emp" name="excelColumn" value="employmentProcessDate" checked> <span class="form-check-label" style="font-size:0.8rem;">취창업처리일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-emp" name="excelColumn" value="employmentType" checked> <span class="form-check-label" style="font-size:0.8rem;">취업유형</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-emp" name="excelColumn" value="employer" checked> <span class="form-check-label" style="font-size:0.8rem;">취업처</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-emp" name="excelColumn" value="salary" checked> <span class="form-check-label" style="font-size:0.8rem;">임금</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-emp" name="excelColumn" value="jobRole" checked> <span class="form-check-label" style="font-size:0.8rem;">직무</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-emp" name="excelColumn" value="incentiveType" checked> <span class="form-check-label" style="font-size:0.8rem;">취업인센티브</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-emp" name="excelColumn" value="workExperienceType" checked> <span class="form-check-label" style="font-size:0.8rem;">일경험분류</span></label></div>
                        </div>
                    </div>

                    <div class="column-group">
                        <div class="d-flex align-items-center mb-2">
                            <strong style="font-size:0.85rem; color:var(--admin-text);" class="me-2">기타/관리</strong>
                            <input type="checkbox" class="form-check-input group-toggle" checked onchange="toggleGroup('etc', this.checked)">
                        </div>
                        <div class="row g-2">
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-etc" name="excelColumn" value="memo" checked> <span class="form-check-label" style="font-size:0.8rem;">메모</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-etc" name="excelColumn" value="others" checked> <span class="form-check-label" style="font-size:0.8rem;">기타</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-etc" name="excelColumn" value="closed" checked> <span class="form-check-label" style="font-size:0.8rem;">마감</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-etc" name="excelColumn" value="resignationDate" checked> <span class="form-check-label" style="font-size:0.8rem;">퇴사일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-etc" name="excelColumn" value="indirectEmploymentService" checked> <span class="form-check-label" style="font-size:0.8rem;">간접고용서비스</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-etc" name="excelColumn" value="managerChangeDate" checked> <span class="form-check-label" style="font-size:0.8rem;">전담자 변경일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-etc" name="excelColumn" value="initialManagerAccount" checked> <span class="form-check-label" style="font-size:0.8rem;">초기전담자 계정</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-etc" name="excelColumn" value="participantModifyDate" checked> <span class="form-check-label" style="font-size:0.8rem;">참여자 수정일</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-etc" name="excelColumn" value="iap3Month" checked> <span class="form-check-label" style="font-size:0.8rem;">IAP3개월여부</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-etc" name="excelColumn" value="iap5Month" checked> <span class="form-check-label" style="font-size:0.8rem;">IAP5개월여부</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-etc" name="excelColumn" value="iap3MonthDate" checked> <span class="form-check-label" style="font-size:0.8rem;">IAP3개월일자</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-etc" name="excelColumn" value="iap5MonthDate" checked> <span class="form-check-label" style="font-size:0.8rem;">IAP5개월일자</span></label></div>
                            <div class="col-md-3"><label class="form-check"><input type="checkbox" class="form-check-input col-check group-etc" name="excelColumn" value="allowanceDate" checked> <span class="form-check-label" style="font-size:0.8rem;">수당지급일</span></label></div>
                        </div>
                    </div>
                </div>

                <!-- 검색 필터 -->
                <div class="excel-section-title"><i class="bi bi-funnel"></i> 검색 필터</div>
                <div class="admin-filter-panel">
                    <div class="admin-filter-row">
                        <div class="admin-filter-group">
                            <label>지점</label>
                            <select class="form-select" id="excelSearchBranch" onchange="updateSummary()"><option value="">전체</option></select>
                        </div>
                        <div class="admin-filter-group">
                            <label>상담사</label>
                            <select class="form-select" id="excelSearchCounselor" onchange="updateSummary()"><option value="">전체</option></select>
                        </div>
                        <div class="admin-filter-group">
                            <label>진행단계</label>
                            <select class="form-select" id="excelSearchStatus" onchange="updateSummary()">
                                <option value="">전체</option><option value="IAP전">IAP전</option><option value="IAP후">IAP후</option><option value="취업">취업</option>
                            </select>
                        </div>
                        <div class="admin-filter-group">
                            <label>마감여부</label>
                            <select class="form-select" id="excelSearchClosed" onchange="updateSummary()">
                                <option value="">전체</option><option value="0">진행중</option><option value="1">마감</option>
                            </select>
                        </div>
                    </div>
                    <div class="admin-filter-row" style="margin-top:12px;">
                        <div class="admin-filter-group">
                            <label>등록일 시작</label>
                            <input type="date" class="form-control" id="excelSearchStartDate" onchange="updateSummary()">
                        </div>
                        <div class="admin-filter-group">
                            <label>등록일 종료</label>
                            <input type="date" class="form-control" id="excelSearchEndDate" onchange="updateSummary()">
                        </div>
                        <div class="admin-filter-actions">
                            <button class="btn btn-outline-secondary" onclick="resetFilters()"><i class="bi bi-arrow-counterclockwise"></i> 초기화</button>
                        </div>
                    </div>
                </div>

                <!-- 다운로드 미리보기 -->
                <div class="excel-section-title"><i class="bi bi-eye"></i> 다운로드 미리보기</div>
                <div class="filter-summary">
                    <h6>선택된 시트</h6>
                    <div id="summarySheets"></div>
                    <h6 class="mt-3">적용 필터</h6>
                    <div id="summaryFilters"></div>
                </div>

                <!-- 다운로드 버튼 -->
                <div class="admin-download-action">
                    <button class="btn btn-primary btn-lg" onclick="downloadExcel()"><i class="bi bi-download"></i> 엑셀 다운로드</button>
                </div>

            </div>
        </div>
    </main>

    <footer class="app-footer">
        <div class="float-end d-none d-sm-inline">JobMoa Admin System v1.0</div>
        <strong>Copyright &copy; 2024-2026 JobMoa.</strong> All rights reserved.
    </footer>
</div>

<script src="https://cdn.jsdelivr.net/npm/overlayscrollbars@2.10.1/browser/overlayscrollbars.browser.es5.min.js" integrity="sha256-dghWARbRe2eLlIJ56wNB+b760ywulqK3DzZYEpsg2fQ=" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js" integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js" integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+" crossorigin="anonymous"></script>
<script src="/js/adminlte.js"></script>
<script src="/js/adminJs/adminExcelBuilder_0.0.1.js"></script>

<script>
    const { OverlayScrollbars: OS2 } = OverlayScrollbarsGlobal;
    if (document.querySelector('.app-sidebar-wrapper')) {
        OS2(document.querySelector('.app-sidebar-wrapper'), {});
    }
</script>
</body>
</html>