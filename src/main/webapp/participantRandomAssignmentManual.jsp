<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 26. 2. 6.
  Time: 오전 9:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>참여자 자동 배정 시스템 매뉴얼</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Noto Sans KR', sans-serif; }
        .scroll-mt-24 { scroll-margin-top: 6rem; }
        .img-placeholder {
            background-color: #f3f4f6;
            border: 2px dashed #d1d5db;
            color: #6b7280;
            display: flex;
            align-items: center;
            justify-content: center;
            height: 200px;
            margin-bottom: 1.5rem;
            border-radius: 0.5rem;
            font-weight: 500;
        }
    </style>
</head>
<body class="bg-gray-50 text-gray-800">

<!-- Header / Navigation -->
<nav class="bg-white border-b border-gray-200 fixed w-full z-50 top-0 shadow-sm">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
            <div class="flex items-center">
                <span class="text-blue-700 text-xl font-bold"><i class="fa-solid fa-users-gear mr-2"></i>랜덤 배정 시스템</span>
            </div>
            <div class="hidden md:flex items-center space-x-6">
                <a href="#section-access" class="text-gray-600 hover:text-blue-600 font-medium text-sm">1. 접속</a>
                <a href="#section-direct" class="text-gray-600 hover:text-blue-600 font-medium text-sm">2. 직접입력</a>
                <a href="#section-csv" class="text-gray-600 hover:text-blue-600 font-medium text-sm">3. CSV등록</a>
                <a href="#section-buttons" class="text-gray-600 hover:text-blue-600 font-medium text-sm">4. 기능버튼</a>
                <a href="#section-algorithm" class="text-gray-600 hover:text-blue-600 font-medium text-sm">부록: 알고리즘</a>
            </div>
        </div>
    </div>
</nav>

<!-- Main Content -->
<main class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-16">

    <!-- Title -->
    <div class="text-center mb-12">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">국민취업지원제도 참여자 자동배정 매뉴얼</h1>
        <p class="text-gray-600">본 매뉴얼은 참여자 배정 시스템의 상세 사용법을 안내합니다.</p>
    </div>

    <!-- Section 1: 접속 방법 -->
    <section id="section-access" class="bg-white p-8 rounded-xl shadow-sm border border-gray-200 mb-10 scroll-mt-24">
        <h2 class="text-xl font-bold text-gray-800 mb-4 border-l-4 border-blue-600 pl-3">1. 랜덤 배정 페이지 접속 방법</h2>

        <!-- 이미지 영역 1 -->
        <div class="img-placeholder h-full">
            <img src="img/randomAssignmentManual/participant-random-assignment-manual-01.png" class="w-full">
        </div>

        <div class="bg-blue-50 p-4 rounded-lg text-blue-900 font-medium flex flex-wrap items-center gap-2">
            <span>참여자관리사이트 로그인</span>
            <i class="fa-solid fa-chevron-right text-gray-400 text-sm"></i>
            <span>지점관리</span>
            <i class="fa-solid fa-chevron-right text-gray-400 text-sm"></i>
            <span>참여자 랜덤 배정 탭 클릭</span>
        </div>
    </section>

    <!-- Section 2: 직접 입력 방식 -->
    <section id="section-direct" class="bg-white p-8 rounded-xl shadow-sm border border-gray-200 mb-10 scroll-mt-24">
        <h2 class="text-xl font-bold text-gray-800 mb-4 border-l-4 border-blue-600 pl-3">2. 참여자 직접 입력 방식</h2>

        <!-- 이미지 영역 2 -->
        <div class="img-placeholder h-full">
            <img src="img/randomAssignmentManual/participant-random-assignment-manual-02.png" class="w-full">
        </div>

        <p class="mb-4 text-gray-600">직접 입력 또는 CSV 파일 등록 중 하나를 선택하여 진행합니다. 아래는 직접 입력 항목에 대한 설명입니다.</p>

        <ul class="space-y-3 text-sm text-gray-700">
            <li class="flex items-start"><span class="font-bold w-32 shrink-0">• 참여자 성명:</span> <span>참여자 이름 (띄어쓰기로 구분)</span></li>
            <li class="flex items-start"><span class="font-bold w-32 shrink-0">• 참여유형:</span> <span>1유형 / 2유형 중 선택</span></li>
            <li class="flex items-start"><span class="font-bold w-32 shrink-0">• 성별:</span> <span>남 / 여 중 선택</span></li>
            <li class="flex items-start"><span class="font-bold w-32 shrink-0">• 생년월일:</span> <span>생년월일 직접 입력 또는 달력 선택</span></li>
            <li class="flex items-start"><span class="font-bold w-32 shrink-0">• 모집경로:</span> <span>센터배정, 자체모집(대학) 등 목록에서 선택</span></li>
            <li class="flex items-start"><span class="font-bold w-32 shrink-0">• 경력유무:</span> <span>참여자 경력을 <strong>0~99 숫자</strong>로 기입 (값이 없다면 0으로 자동 추가)</span></li>
            <li class="flex items-start"><span class="font-bold w-32 shrink-0">• 학력:</span> <span>고졸(고졸, 검정고시) / 대졸(초대졸, 대졸, 대학원) 목록 선택</span></li>
            <li class="flex items-start"><span class="font-bold w-32 shrink-0">• 특정계층 여부:</span> <span>해당 없음 / 해당 선택</span></li>
            <li class="flex items-start"><span class="font-bold w-32 shrink-0">• 진행단계:</span> <span>IAP 전, IAP 후 등 목록에서 선택</span></li>
            <li class="flex items-start bg-yellow-50 p-2 rounded w-full">
                <span class="font-bold w-32 shrink-0 text-yellow-800">• 출장상담사(ID):</span>
                <div class="text-yellow-800">
                    지점 내 상담사 목록에서 선택<br>
                    <span class="text-xs">* 학원, 학교 등 자체모집을 진행한 상담사를 위한 목록입니다. <strong>(선택 시 해당 상담사에게 강제 배정)</strong></span>
                </div>
            </li>
        </ul>
        <div class="mt-4 text-center">
            <span class="inline-block bg-gray-100 px-3 py-1 rounded text-sm text-gray-600">위 입력사항을 모두 입력 후 <strong>「목록에 추가」</strong> 버튼을 클릭하여 등록합니다.</span>
        </div>
    </section>

    <!-- Section 3: CSV 파일 등록 방식 -->
    <section id="section-csv" class="bg-white p-8 rounded-xl shadow-sm border border-gray-200 mb-10 scroll-mt-24">
        <h2 class="text-xl font-bold text-gray-800 mb-4 border-l-4 border-green-600 pl-3">3. CSV 파일 등록 방식</h2>

        <div class="mb-6">
            <h3 class="font-bold text-lg mb-2">3-1. CSV 입력 규칙</h3>
            <div class="bg-gray-50 p-4 rounded text-sm space-y-2">
                <p><strong>• 기본 정보 (성명, 유형, 성별, 생년월일):</strong> 직접 입력 방식과 동일</p>
                <p><strong>• 모집경로:</strong> 목록 확인 후 작성 (미기입 시 '센터배정' 자동 입력)</p>
                <p><strong>• 경력유무:</strong> 숫자 입력 (미기입 시 '0' 자동 입력)</p>
                <p><strong>• 학력:</strong> 목록 확인 후 작성 (고졸/대졸)</p>
                <p><strong>• 특정계층 여부:</strong> O/X 또는 해당/해당없음 중 선택 (미기입 시 'X' 자동 입력)</p>
                <p><strong>• 출장상담사(ID):</strong> 자체모집, 고정배정이 있는 경우 상담사 ID 입력</p>
            </div>
        </div>

        <!-- 이미지 영역 3 -->
        <div class="img-placeholder h-full">
            <img src="img/randomAssignmentManual/participant-random-assignment-manual-03.png" class="w-full">
        </div>

        <div class="mb-8">
            <h3 class="font-bold text-lg mb-2">3-2. 템플릿 작성</h3>
            <p class="mb-2 text-sm">「템플릿 다운로드」 버튼을 클릭하여 csv 형식의 템플릿 파일을 다운받습니다.</p>
            <div class="bg-red-50 text-red-600 px-3 py-2 rounded text-sm font-bold mb-3">
                <i class="fa-solid fa-triangle-exclamation mr-1"></i> 참고: 한셀(Hancom Office)로 열어야 저장할 때 한글 깨짐이 발생하지 않습니다.
            </div>

            <!-- 이미지 영역 4 -->
            <div class="img-placeholder h-full">
                <img src="img/randomAssignmentManual/participant-random-assignment-manual-04.png" class="w-full" alt="">
            </div>
        </div>

        <div class="mb-8">
            <h3 class="font-bold text-lg mb-2">3-3. 파일 선택 및 업로드</h3>
            <p class="mb-3 text-sm">작성을 완료한 후 「파일 찾기」 버튼을 클릭하여 저장한 csv 파일을 선택합니다.</p>

            <!-- 이미지 영역 5 -->
            <div class="img-placeholder h-full">
                <img src="img/randomAssignmentManual/participant-random-assignment-manual-05.png" class="w-full" alt="">
            </div>
        </div>

        <div>
            <h3 class="font-bold text-lg mb-2">3-4. 등록 완료 확인</h3>
            <p class="mb-3 text-sm">정상적으로 파일을 불러왔다면 성공 메시지를 확인할 수 있습니다.</p>

            <!-- 이미지 영역 6 -->
            <div class="img-placeholder h-full">
                <img src="img/randomAssignmentManual/participant-random-assignment-manual-06.png" class="w-full" alt="">
            </div>
        </div>
    </section>

    <!-- Section 4: 기능 버튼 설명 -->
    <section id="section-buttons" class="bg-white p-8 rounded-xl shadow-sm border border-gray-200 mb-10 scroll-mt-24">
        <h2 class="text-xl font-bold text-gray-800 mb-4 border-l-4 border-purple-600 pl-3">4. 기능 버튼 설명</h2>

        <!-- 이미지 영역 7 -->
        <div class="img-placeholder h-32">
            <img src="img/randomAssignmentManual/participant-random-assignment-manual-07.png" class="w-full" alt="">
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-6">
            <!-- 저장 -->
            <div class="border rounded-lg p-4 bg-green-50 border-green-200">
                <h3 class="font-bold text-green-800 mb-2"><i class="fa-solid fa-floppy-disk mr-2"></i>저장</h3>
                <p class="text-sm text-gray-700">배정이 완료되고 저장 버튼을 클릭하게 되면 배정된 상담사에게 데이터가 최종 등록됩니다.</p>
            </div>

            <!-- 랜덤 배정 -->
            <div class="border rounded-lg p-4 bg-blue-50 border-blue-200">
                <h3 class="font-bold text-blue-800 mb-2"><i class="fa-solid fa-shuffle mr-2"></i>랜덤 배정</h3>
                <p class="text-sm text-gray-700">
                    입사일 기준 그룹별 <strong>배정 한도를 지정</strong>하고, 한도가 남은 <strong>최적 상담사(4명)</strong>를 찾아 랜덤하게 배정합니다.
                </p>
            </div>

            <!-- 점수만 반영 배정 -->
            <div class="border rounded-lg p-4 bg-indigo-50 border-indigo-200">
                <h3 class="font-bold text-indigo-800 mb-2"><i class="fa-solid fa-star mr-2"></i>점수만 반영 배정</h3>
                <p class="text-sm text-gray-700">
                    <strong>한도와 상관없이</strong> 점수 계산 방식에 따라 가장 적합한 상담사에게 배정합니다. (긴급 배정 등 특수 상황)
                </p>
            </div>

            <!-- 초기화 -->
            <div class="border rounded-lg p-4 bg-yellow-50 border-yellow-200">
                <h3 class="font-bold text-yellow-800 mb-2"><i class="fa-solid fa-rotate-right mr-2"></i>초기화</h3>
                <p class="text-sm text-gray-700">랜덤배정이 완료된 결과를 초기화하여 다시 배정을 시도할 수 있도록 합니다.</p>
            </div>

            <!-- 데이터 검증 (새로 추가됨) -->
            <div class="border rounded-lg p-4 bg-gray-50 border-gray-300 md:col-span-2">
                <h3 class="font-bold text-gray-800 mb-2"><i class="fa-solid fa-check-double mr-2"></i>데이터 검증</h3>
                <p class="text-sm text-gray-700">
                    등록된 내용에 오류가 있는지 확인하고, 등록되어 있지 않은 필수값들을 <strong>기본값으로 자동 보정 및 추가</strong>하는 기능입니다.
                </p>
            </div>
        </div>
    </section>

    <!-- Section 5: 알고리즘 (부록) -->
    <section id="section-algorithm" class="bg-slate-800 text-white p-8 rounded-xl shadow-lg mb-16 scroll-mt-24">
        <h2 class="text-xl font-bold mb-4 border-l-4 border-blue-400 pl-3"><i class="fa-solid fa-microchip mr-2"></i>부록: 참여자 자동 배정 알고리즘</h2>
        <p class="text-sm text-slate-300 mb-6">
            * 본 내용은 시스템이 자동으로 상담사를 선정하는 내부 기준입니다. 참고용으로 확인해 주세요.
        </p>

        <div class="grid grid-cols-1 md:grid-cols-4 gap-4 text-center">
            <div class="bg-slate-700 p-4 rounded-lg">
                <div class="text-blue-400 text-2xl mb-2"><i class="fa-solid fa-briefcase"></i></div>
                <h4 class="font-bold text-sm">C_load (업무량)</h4>
                <p class="text-xs text-slate-400 mt-1">현재 진행 건수와 전체 평균의 차이</p>
            </div>
            <div class="bg-slate-700 p-4 rounded-lg">
                <div class="text-purple-400 text-2xl mb-2"><i class="fa-solid fa-scale-balanced"></i></div>
                <h4 class="font-bold text-sm">C_fair (공정성)</h4>
                <p class="text-xs text-slate-400 mt-1">유형/성별/학력 등의 분포 균형</p>
            </div>
            <div class="bg-slate-700 p-4 rounded-lg">
                <div class="text-red-400 text-2xl mb-2"><i class="fa-solid fa-arrow-down-short-wide"></i></div>
                <h4 class="font-bold text-sm">C_streak (쏠림방지)</h4>
                <p class="text-xs text-slate-400 mt-1">연속 배정 횟수 제어</p>
            </div>
            <div class="bg-slate-700 p-4 rounded-lg">
                <div class="text-orange-400 text-2xl mb-2"><i class="fa-solid fa-gauge-high"></i></div>
                <h4 class="font-bold text-sm">C_pace (속도)</h4>
                <p class="text-xs text-slate-400 mt-1">연간 배정 수와 평균의 차이</p>
            </div>
        </div>

        <div class="mt-6 bg-slate-900 p-4 rounded text-xs text-slate-400">
            <p class="mb-2 font-bold">[배정 프로세스]</p>
            <p>1. 모든 상담사의 비용(Cost) 계산 및 점수화</p>
            <p>2. 점수 상위 4명 선정</p>
            <p>3. 입사일 기준 그룹별 한도(일/주/월) 체크 (랜덤배정 시)</p>
            <p>4. 배정인원이 가장 적은 상담사 우선 필터링</p>
            <p>5. 최종 후보 중 랜덤 1명 배정</p>
        </div>
    </section>

</main>

<!-- Footer -->
<footer class="bg-white border-t border-gray-200 py-8">
    <div class="max-w-7xl mx-auto px-4 text-center">
        <p class="text-gray-800 font-bold mb-2">JobMoa 배정 시스템</p>
        <p class="text-sm text-gray-500">본 매뉴얼은 2026-02-06 기준으로 작성되었습니다.</p>
    </div>
</footer>

<script>
    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });
</script>
</body>
</html>