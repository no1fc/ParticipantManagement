<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 25. 6. 27.
  Time: 오후 2:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="국민취업지원제도 참여자 정보 작성 페이지">
    <title>참여자 정보 작성 - 국민취업지원제도</title>
    <mytag:Logo/>

    <!-- Favicon -->
<%--    <link rel="icon" type="image/x-icon" href="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzIiIGhlaWdodD0iMzIiIHZpZXdCb3g9IjAgMCAzMiAzMiIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjMyIiBoZWlnaHQ9IjMyIiByeD0iNCIgZmlsbD0iIzI1NjNlYiIvPgo8cGF0aCBkPSJNOCAxMmg0djhoLTR2LTh6bTYgMGg0djhoLTR2LTh6bTYgMGg0djhoLTR2LTh6IiBmaWxsPSJ3aGl0ZSIvPgo8L3N2Zz4K">--%>

    <!-- Preconnect for performance -->
    <link rel="preconnect" href="https://cdn.jsdelivr.net">

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">

    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <!-- Custom Style CSS  -->
    <link href="/css/jobPlacementCss/jobPlacementDefault_0.0.1.css" rel="stylesheet">

    <script src="/js/jobPlacementJs/locationSelect_0.0.1.js"></script>
</head>
<body>
<!-- Skip Navigation for Accessibility -->
<a href="#main-content" class="skip-link">메인 콘텐츠로 바로가기</a>

<mytag:jobPlacementView-header/>

<mytag:jobPlacementView-nav pageController="main"/>

<!-- Main Content -->
<main id="main-content" role="main" class="container">
    <div class="main-content">
        <!-- 저장 상태 표시 -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="h4 mb-1">참여자 정보 작성</h2>
                <p class="text-muted mb-0">기본 정보와 자기소개서를 작성해주세요.</p>
            </div>
            <div class="d-flex align-items-center gap-3">
                <span class="status-badge draft" id="statusBadge">작성 중</span>
                <small class="text-muted" id="lastSaved">마지막 저장: 저장된 내용 없음</small>
            </div>
        </div>

        <form id="participantForm" novalidate>
            <!-- 기본 정보 섹션 -->
            <div class="card-modern mb-4">
                <div class="card-header">
                    <h3 class="card-title">
                        <i class="bi bi-person-badge"></i>
                        기본 정보
                    </h3>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="participantName" class="form-label">
                                    이름 <span class="required">*</span>
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="participantName"
                                       name="participantName"
                                       placeholder="홍길동"
                                       required
                                       aria-describedby="nameHelp">
                                <div id="nameHelp" class="form-text">실명을 입력해주세요.</div>
                                <div class="invalid-feedback">이름을 입력해주세요.</div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="participantAge" class="form-label">
                                    나이 <span class="required">*</span>
                                </label>
                                <select class="form-select"
                                        id="participantAge"
                                        name="participantAge"
                                        required>
                                    <option value="" selected>나이를 선택하세요</option>
                                    <option value="20대">20대</option>
                                    <option value="30대">30대</option>
                                    <option value="40대">40대</option>
                                    <option value="50대">50대</option>
                                    <option value="60대 이상">60대 이상</option>
                                </select>
                                <div class="invalid-feedback">나이를 선택해주세요.</div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="row">
                                <div class="form-group col-md-6">
                                    <label for="participantLocationCity" class="form-label">
                                        거주지(시) <span class="required">*</span>
                                    </label>
                                    <select class="form-select"
                                            id="participantLocationCity"
                                            name="participantLocationCity"
                                            required>
                                        <option value="" selected>거주지(시)를 선택하세요</option>
                                        <option value="서울특별시">서울특별시</option>
                                        <option value="부산광역시">부산광역시</option>
                                        <option value="대구광역시">대구광역시</option>
                                        <option value="인천광역시">인천광역시</option>
                                        <option value="광주광역시">광주광역시</option>
                                        <option value="대전광역시">대전광역시</option>
                                        <option value="울산광역시">울산광역시</option>
                                        <option value="세종특별자치시">세종특별자치시</option>
                                        <option value="경기도">경기도</option>
                                        <option value="강원도">강원도</option>
                                        <option value="충청북도">충청북도</option>
                                        <option value="충청남도">충청남도</option>
                                        <option value="전라북도">전라북도</option>
                                        <option value="전라남도">전라남도</option>
                                        <option value="경상북도">경상북도</option>
                                        <option value="경상남도">경상남도</option>
                                        <option value="제주특별자치도">제주특별자치도</option>
                                    </select>
                                    <div class="invalid-feedback">거주지(시)를 선택해주세요.</div>
                                </div>

                                <div class="form-group col-md-6">
                                    <label for="participantLocationDistrict" class="form-label">
                                        거주지(구) <span class="required">*</span>
                                    </label>
                                    <select class="form-select"
                                            id="participantLocationDistrict"
                                            name="participantLocationDistrict"
                                            required>
                                        <option value="">거주지(구)를 선택하세요</option>

                                    </select>
                                    <div class="invalid-feedback">거주지(구)를 선택해주세요.</div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="desiredJob" class="form-label">
                                    희망직종 <span class="required">*</span>
                                </label>
                                <select class="form-select"
                                        id="desiredJob"
                                        name="desiredJob"
                                        required>
                                    <option value="" selected>희망직종을 선택하세요</option>
                                    <option value="사무직">사무직</option>
                                    <option value="영업직">영업직</option>
                                    <option value="생산직">생산직</option>
                                    <option value="기술직">기술직</option>
                                    <option value="전문직">전문직</option>
                                    <option value="관리직">관리직</option>
                                    <option value="기타">기타</option>
                                </select>
                                <div class="invalid-feedback">희망직종을 선택해주세요.</div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="desiredSalary" class="form-label">
                                    희망연봉 <span class="required">*</span>
                                </label>
                                <select class="form-select"
                                        id="desiredSalary"
                                        name="desiredSalary"
                                        required>
                                    <option value="" selected>희망연봉을 선택하세요</option>
                                    <option value="2000만원 이하">2000만원 이하</option>
                                    <option value="2000-3000만원">2000-3000만원</option>
                                    <option value="3000-4000만원">3000-4000만원</option>
                                    <option value="4000-5000만원">4000-5000만원</option>
                                    <option value="5000만원 이상">5000만원 이상</option>
                                </select>
                                <div class="invalid-feedback">희망연봉을 선택해주세요.</div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label for="certificates" class="form-label">자격증</label>
                                <input type="text"
                                       class="form-control"
                                       id="certificates"
                                       name="certificates"
                                       placeholder="컴퓨터활용능력 1급, 토익 800점"
                                       aria-describedby="certificatesHelp">
                                <div id="certificatesHelp" class="form-text">
                                    보유한 자격증을 입력하세요. (쉼표로 구분)
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="experience" class="form-label">경력사항</label>
                        <textarea class="form-control"
                                  id="experience"
                                  name="experience"
                                  rows="3"
                                  placeholder="주요 경력사항을 입력하세요. Ex)ABC회사 사무직 2년 (2020-2022)"
                                  aria-describedby="experienceHelp"></textarea>
                        <div id="experienceHelp" class="form-text">
                            회사명, 직무, 근무기간 등을 입력하세요.
                        </div>
                    </div>
                </div>
            </div>

            <!-- 자기소개서 섹션 -->
            <div class="card-modern mb-4">
                <div class="card-header">
                    <div class="d-flex justify-content-between align-items-center">
                        <h3 class="card-title">
                            <i class="bi bi-file-text"></i>
                            자기소개서
                        </h3>
                        <button type="button"
                                class="btn btn-modern btn-outline"
                                id="addEssayBtn"
                                aria-label="자기소개서 항목 추가">
                            <i class="bi bi-plus-lg"></i> 항목 추가
                        </button>
                    </div>
                </div>
                <div class="card-body">
                    <div id="essayContainer">
                        <!-- 기본 자기소개서 항목 -->
                        <div class="essay-section" data-index="0">
                            <div class="essay-header">
                                <div class="flex-grow-1">
                                    <input type="text"
                                           class="form-control essay-title"
                                           placeholder="제목을 입력하세요 (예: 지원동기)"
                                           value="지원동기 및 포부"
                                           name="essayTitle[]"
                                           required>
                                </div>
                                <div class="essay-controls">
                                    <button type="button"
                                            class="btn-icon btn-move"
                                            aria-label="항목 이동"
                                            title="드래그하여 순서 변경">
                                        <i class="bi bi-grip-vertical"></i>
                                    </button>
                                    <button type="button"
                                            class="btn-icon btn-delete"
                                            aria-label="항목 삭제"
                                            title="항목 삭제">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                            </div>
                            <textarea class="form-control essay-content"
                                      rows="6"
                                      placeholder="내용을 입력하세요..."
                                      name="essayContent[]"
                                      required>저는 이 직무에 지원하게 된 동기는 제가 가진 IT 관련 지식과 경험을 활용하여 회사의 발전에 기여하고 싶기 때문입니다. 특히 사무직으로서의 업무 경험을 바탕으로 체계적이고 효율적인 업무 처리 능력을 발휘할 수 있을 것이라고 생각합니다.</textarea>
                        </div>
                    </div>
                    <div class="form-text mt-3">
                        <i class="bi bi-info-circle"></i>
                        드래그하여 순서를 변경할 수 있습니다. 최소 1개 이상의 항목이 필요합니다.
                    </div>
                </div>
            </div>

            <!-- 버튼 영역 -->
            <div class="d-flex gap-3 justify-content-end">
                <a href="placementDetail"
                   class="btn btn-modern btn-secondary">
                    <i class="bi bi-eye"></i> 미리보기
                </a>
                <button type="submit"
                        class="btn btn-modern btn-primary"
                        id="saveBtn">
                    <i class="bi bi-floppy"></i> 저장하기
                </button>
                <button type="button"
                        class="btn btn-modern btn-success"
                        id="requestJobBtn"
                        disabled
                        title="저장 후 이용 가능합니다">
                    <i class="bi bi-send"></i> 알선요청
                </button>
            </div>
        </form>
    </div>
</main>

<!-- Toast Container -->
<div class="toast-container" id="toastContainer"></div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sortablejs@1.15.0/Sortable.min.js"></script>

<script>
    // 참여자 작성 페이지 관리
    class ParticipantManager {
        constructor() {
            this.isFormSaved = false;
            this.formData = {};
            this.essayCount = 1;
            this.init();
        }

        init() {
            this.setupEventListeners();
            this.setupFormValidation();
            this.setupSortableEssays();
            this.loadSavedData();
            this.updateLastSaved();
        }

        // 이벤트 리스너 설정
        setupEventListeners() {
            // 폼 저장
            document.getElementById('participantForm').addEventListener('submit', (e) => this.handleFormSubmit(e));

            // 자기소개서 항목 추가
            document.getElementById('addEssayBtn').addEventListener('click', () => this.addEssaySection());

            // 자기소개서 삭제 (이벤트 위임)
            document.getElementById('essayContainer').addEventListener('click', (e) => {
                if (e.target.closest('.btn-delete')) {
                    this.removeEssaySection(e);
                }
            });

            // 알선요청
            document.getElementById('requestJobBtn').addEventListener('click', () => this.handleJobRequest());

            // 폼 변경 감지
            const formElements = document.querySelectorAll('#participantForm input, #participantForm select, #participantForm textarea');
            formElements.forEach(element => {
                element.addEventListener('input', () => this.updateSaveStatus(false));
                element.addEventListener('change', () => this.updateSaveStatus(false));
            });
        }

        // 폼 검증 설정
        setupFormValidation() {
            const form = document.getElementById('participantForm');
            const inputs = form.querySelectorAll('input, select, textarea');

            inputs.forEach(input => {
                input.addEventListener('blur', () => this.validateField(input));
                input.addEventListener('input', () => this.clearFieldErrors(input));
            });
        }

        // 필드 검증
        validateField(field) {
            const isValid = field.checkValidity();

            if (!isValid) {
                field.classList.add('is-invalid');
                field.classList.remove('is-valid');
            } else {
                field.classList.remove('is-invalid');
                field.classList.add('is-valid');
            }

            return isValid;
        }

        // 필드 오류 제거
        clearFieldErrors(field) {
            field.classList.remove('is-invalid');
        }

        // 폼 제출 처리
        handleFormSubmit(e) {
            e.preventDefault();

            if (this.validateForm()) {
                this.saveForm();
            }
        }

        // 전체 폼 검증
        validateForm() {
            const form = document.getElementById('participantForm');
            const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
            let isValid = true;

            inputs.forEach(input => {
                if (!this.validateField(input)) {
                    isValid = false;
                }
            });

            // 자기소개서 최소 1개 항목 검증
            const essaySections = document.querySelectorAll('.essay-section');
            if (essaySections.length === 0) {
                this.showToast('error', '최소 1개 이상의 자기소개서 항목이 필요합니다.');
                isValid = false;
            }

            return isValid;
        }

        // 폼 저장
        saveForm() {
            const saveBtn = document.getElementById('saveBtn');
            const originalText = saveBtn.innerHTML;

            // 로딩 상태
            saveBtn.innerHTML = '<span class="loading-spinner"></span> 저장 중...';
            saveBtn.disabled = true;

            // 폼 데이터 수집
            this.formData = this.collectFormData();

            // localStorage에 저장
            this.saveToLocalStorage();

            // 실제 서버 저장 시뮬레이션
            setTimeout(() => {
                this.updateSaveStatus(true);
                this.updateLastSaved();

                // 저장 완료 후 버튼 상태 변경
                saveBtn.innerHTML = '<i class="bi bi-pencil"></i> 수정하기';
                saveBtn.disabled = false;

                // 알선요청 버튼 활성화
                const requestBtn = document.getElementById('requestJobBtn');
                requestBtn.disabled = false;
                requestBtn.title = '알선요청이 가능합니다';

                this.showToast('success', '정보가 성공적으로 저장되었습니다.');
            }, 1500);
        }

        // 폼 데이터 수집
        collectFormData() {
            const form = document.getElementById('participantForm');
            const formData = new FormData(form);
            const data = {};

            // 기본 필드
            for (let [key, value] of formData.entries()) {
                if (!key.includes('[]')) {
                    data[key] = value;
                }
            }

            // 자기소개서 배열 처리
            const essayTitles = Array.from(document.querySelectorAll('.essay-title')).map(el => el.value);
            const essayContents = Array.from(document.querySelectorAll('.essay-content')).map(el => el.value);

            data.essays = essayTitles.map((title, index) => ({
                title: title,
                content: essayContents[index]
            }));

            return data;
        }

        // 로컬 스토리지에 저장
        saveToLocalStorage() {
            try {
                localStorage.setItem('participantData', JSON.stringify(this.formData));
                localStorage.setItem('participantSaveTime', new Date().toISOString());
            } catch (e) {
                console.warn('로컬 저장 실패:', e);
            }
        }

        // 저장된 데이터 로드
        loadSavedData() {
            try {
                const savedData = localStorage.getItem('participantData');
                if (savedData) {
                    const data = JSON.parse(savedData);
                    this.populateForm(data);
                    this.updateSaveStatus(true);
                }
            } catch (e) {
                console.warn('저장된 데이터 로드 실패:', e);
            }
        }

        // 폼에 데이터 채우기
        populateForm(data) {
            // 기본 필드 채우기
            Object.keys(data).forEach(key => {
                if (key !== 'essays') {
                    const element = document.querySelector('[name="' + key + '"]');
                    if (element) {
                        element.value = data[key];
                    }
                }
            });

            // 자기소개서 재구성
            if (data.essays && data.essays.length > 0) {
                // 기존 항목 제거
                document.getElementById('essayContainer').innerHTML = '';

                // 새 항목 추가
                data.essays.forEach((essay, index) => {
                    this.addEssaySection(essay.title, essay.content);
                });
            }
        }

        // 저장 상태 업데이트
        updateSaveStatus(saved) {
            this.isFormSaved = saved;
            const statusBadge = document.getElementById('statusBadge');

            if (saved) {
                statusBadge.textContent = '저장완료';
                statusBadge.className = 'status-badge saved';
            } else {
                statusBadge.textContent = '작성중';
                statusBadge.className = 'status-badge draft';
            }
        }

        // 마지막 저장 시간 업데이트
        updateLastSaved() {
            const lastSavedEl = document.getElementById('lastSaved');

            if (this.isFormSaved) {
                try {
                    const saveTime = localStorage.getItem('participantSaveTime');
                    if (saveTime) {
                        const date = new Date(saveTime);
                        const timeString = date.toLocaleString('ko-KR');
                        lastSavedEl.textContent = '마지막 저장: ' + timeString;
                    }
                } catch (e) {
                    lastSavedEl.textContent = '마지막 저장: 방금 전';
                }
            } else {
                lastSavedEl.textContent = '마지막 저장: 저장된 내용 없음';
            }
        }

        // 자기소개서 항목 추가,삭제,인덱스 변경 function 시작
        // 자기소개서 드래그 앤 드롭 설정
        setupSortableEssays() {
            const container = document.getElementById('essayContainer');
            new Sortable(container, {
                animation: 150,
                handle: '.btn-move',
                ghostClass: 'dragging',
                onEnd: () => {
                    this.updateEssayIndices();
                    this.showToast('info', '항목 순서가 변경되었습니다.');
                }
            });
        }

        // 자기소개서 항목 추가
        addEssaySection(title = '', content = '') {
            const container = document.getElementById('essayContainer');
            const newIndex = this.essayCount++;

            const essayHTML = '<div class="essay-section" data-index="' + newIndex + '">' +
                '<div class="essay-header">' +
                '<div class="flex-grow-1">' +
                '<input type="text" ' +
                'class="form-control essay-title" ' +
                'placeholder="제목을 입력하세요"' +
                'name="essayTitle[]"' +
                'value="' + title + '"' +
                'required>' +
                '</div>' +
                '<div class="essay-controls">' +
                '<button type="button" ' +
                'class="btn-icon btn-move" ' +
                'aria-label="항목 이동"' +
                'title="드래그하여 순서 변경">' +
                '<i class="bi bi-grip-vertical"></i>' +
                '</button>' +
                '<button type="button" ' +
                'class="btn-icon btn-delete" ' +
                'aria-label="항목 삭제"' +
                'title="항목 삭제">' +
                '<i class="bi bi-trash"></i>' +
                '</button>' +
                '</div>' +
                '</div>' +
                '<textarea class="form-control essay-content" ' +
                'rows="6"' +
                'placeholder="내용을 입력하세요..."' +
                'name="essayContent[]"' +
                'required>' + content + '</textarea>' +
                '</div>';

            container.insertAdjacentHTML('beforeend', essayHTML);
            this.updateSaveStatus(false);

            if (!title && !content) {
                this.showToast('info', '새 자기소개서 항목이 추가되었습니다.');
            }
        }

        // 자기소개서 항목 삭제
        removeEssaySection(e) {
            const section = e.target.closest('.essay-section');
            const essaySections = document.querySelectorAll('.essay-section');

            if (essaySections.length <= 1) {
                this.showToast('warning', '최소 1개 이상의 자기소개서 항목이 필요합니다.');
                return;
            }

            if (confirm('이 항목을 삭제하시겠습니까?')) {
                section.remove();
                this.updateEssayIndices();
                this.updateSaveStatus(false);
                this.showToast('info', '자기소개서 항목이 삭제되었습니다.');
            }
        }

        // 자기소개서 인덱스 업데이트
        updateEssayIndices() {
            const sections = document.querySelectorAll('.essay-section');
            sections.forEach((section, index) => {
                section.setAttribute('data-index', index);
            });
        }
        // 자기소개서 항목 추가,삭제,인덱스 변경 function 끝

        // 알선요청 처리
        handleJobRequest() {
            if (!this.isFormSaved) {
                this.showToast('warning', '먼저 정보를 저장해주세요.');
                return;
            }

            if (confirm('알선요청을 하시겠습니까? 요청 후에는 수정이 제한됩니다.')) {
                const requestBtn = document.getElementById('requestJobBtn');
                requestBtn.innerHTML = '<span class="loading-spinner"></span> 요청 중...';
                requestBtn.disabled = true;

                setTimeout(() => {
                    this.updateJobRequestStatus();
                    this.showToast('success', '알선요청이 완료되었습니다.');
                }, 2000);
            }
        }

        // 알선요청 상태 업데이트
        updateJobRequestStatus() {
            const statusBadge = document.getElementById('statusBadge');
            statusBadge.textContent = '알선요청';
            statusBadge.className = 'status-badge submitted';

            const requestBtn = document.getElementById('requestJobBtn');
            requestBtn.innerHTML = '<i class="bi bi-check-lg"></i> 요청완료';
            requestBtn.classList.remove('btn-success');
            requestBtn.classList.add('btn-secondary');

            // 폼 필드 읽기 전용으로 변경
            const formElements = document.querySelectorAll('#participantForm input, #participantForm select, #participantForm textarea');
            formElements.forEach(el => el.readOnly = true);

            const saveBtn = document.getElementById('saveBtn');
            saveBtn.style.display = 'none';

            // 로컬 스토리지에 상태 저장
            try {
                localStorage.setItem('participantStatus', 'submitted');
            } catch (e) {
                console.warn('상태 저장 실패:', e);
            }
        }

        // 토스트 알림 표시 function 시작
        showToast(type, message) {
            const toastContainer = document.getElementById('toastContainer');
            const toastId = 'toast-' + Date.now();

            const toastHTML = '<div class="toast toast-custom toast-' + type + '" id="' + toastId + '" role="alert" aria-live="assertive" aria-atomic="true">' +
                '<div class="toast-header">' +
                '<i class="bi bi-' + this.getToastIcon(type) + ' me-2"></i>' +
                '<strong class="me-auto">' + this.getToastTitle(type) + '</strong>' +
                '<button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="닫기"></button>' +
                '</div>' +
                '<div class="toast-body">' +
                message +
                '</div>' +
                '</div>';

            toastContainer.insertAdjacentHTML('beforeend', toastHTML);

            const toastElement = document.getElementById(toastId);
            const toast = new bootstrap.Toast(toastElement, {
                autohide: true,
                delay: 4000
            });

            toast.show();

            // 토스트가 숨겨진 후 DOM에서 제거
            toastElement.addEventListener('hidden.bs.toast', () => {
                toastElement.remove();
            });
        }

        // 토스트 아이콘 반환
        getToastIcon(type) {
            const icons = {
                success: 'check-circle-fill',
                error: 'exclamation-triangle-fill',
                warning: 'exclamation-triangle-fill',
                info: 'info-circle-fill'
            };
            return icons[type] || 'info-circle-fill';
        }

        // 토스트 제목 반환
        getToastTitle(type) {
            const titles = {
                success: '성공',
                error: '오류',
                warning: '주의',
                info: '정보'
            };
            return titles[type] || '알림';
        }
    }
    // 토스트 알림 표시 function 끝

    // 페이지 초기화
    document.addEventListener('DOMContentLoaded', function() {
        window.participantManager = new ParticipantManager();
        console.log('참여자 작성 페이지가 초기화되었습니다.');
    });

    // $(document).ready(function(){
    //     var xhr = new XMLHttpRequest();
    //     var url = 'http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdService/retrieveNewAdressAreaCdService/getNewAddressListAreaCd'; /*URL*/
    //     var queryParams = '?' + encodeURIComponent('serviceKey') + '='+'C3TvPDrtr2HmxYyU4k9/GjfmdgfqPz3U24qcsjTxvGYVkkf5KuLJMHDFCYOSr46yilNNj8vOsQ5JZMetzfjhZw=='; /*Service Key*/
    //     queryParams += '&' + encodeURIComponent('searchSe') + '=' + encodeURIComponent('dong'); /**/
    //     queryParams += '&' + encodeURIComponent('srchwrd') + '=' + encodeURIComponent('주월동 408-1'); /**/
    //     queryParams += '&' + encodeURIComponent('countPerPage') + '=' + encodeURIComponent('10'); /**/
    //     queryParams += '&' + encodeURIComponent('currentPage') + '=' + encodeURIComponent('1'); /**/
    //     xhr.open('GET', url + queryParams);
    //     xhr.onreadystatechange = function () {
    //         if (this.readyState == 4) {
    //             alert('Status: '+this.status+'nHeaders: '+JSON.stringify(this.getAllResponseHeaders())+'nBody: '+this.responseText);
    //         }
    //     };
    //
    //     xhr.send('');
    // })
</script>
</body>
</html>
