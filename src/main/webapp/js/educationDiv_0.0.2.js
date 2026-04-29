/**
 * 교육내역 동적 추가/삭제 모듈 (희망직무와 동일 패턴)
 * - 항목 단위 [핸들][입력][삭제] 구조
 * - 추가 버튼: #addEduBtn
 * - 컨테이너: #education (기존 ID 유지)
 * - 입력 클래스: .education
 * - 폼 name: educations[], educationNos[]
 * - SortableJS 드래그 정렬
 */

// HTML 이스케이프
function _eduEscapeHtml(str) {
    if (!str) return '';
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;').replace(/'/g, '&#039;');
}

// 교육내역 항목 카운트 갱신
function _updateEduCount() {
    let $count = $("#eduCount");
    if ($count.length) {
        $count.text($("#education").children('.edu-item').length);
    }
}

// 교육내역 항목 추가 (data: { education, educationNo })
function addEduItem(data) {
    let $container = $("#education");
    if (!$container.length) return;
    let value = (data && data.education) || '';
    let pk = (data && data.educationNo) || 0;

    let $item = $(
        '<div class="edu-item">' +
        '  <span class="edu-handle" title="드래그로 순서 변경"><i class="bi bi-grip-vertical"></i></span>' +
        '  <input type="text" class="form-control education" name="educations" value="' + _eduEscapeHtml(value) + '" placeholder="교육 내역 입력">' +
        '  <input type="hidden" name="educationNos" value="' + pk + '">' +
        '  <button type="button" class="edu-remove" title="삭제"><i class="bi bi-x-lg"></i></button>' +
        '</div>'
    );
    $container.append($item);
    _updateEduCount();
}

// 교육내역 항목 삭제
function removeEduItem($item) {
    $item.remove();
    _updateEduCount();
}

$(document).ready(function () {
    let $container = $("#education");
    // 진단 로그: 캐시/타이밍 이슈 추적용 (문제 해결 후 제거 가능)
    console.log('[educationDiv] ready - container:', $container.length, 'btn:', $("#addEduBtn").length);
    if (!$container.length) return;

    // 추가 버튼
    $("#addEduBtn").on("click", function () {
        console.log('[educationDiv] addEduBtn click!');
        addEduItem();
    });

    // 삭제 버튼 (이벤트 위임)
    $container.on("click", ".edu-remove", function () {
        removeEduItem($(this).closest('.edu-item'));
    });

    // SortableJS 드래그 정렬
    // ※ forceFallback 제거(데스크톱 native 우선), filter로 input/button 클릭 보호
    if (typeof Sortable !== 'undefined') {
        Sortable.create($container[0], {
            handle: '.edu-handle',
            animation: 150,
            ghostClass: 'sortable-ghost',
            filter: 'input, button',
            preventOnFilter: false
        });
    }

    // 신규 페이지: 빈 항목 1개 자동 추가 (education이 호출되지 않는 경우)
    if ($container.children('.edu-item').length === 0) {
        addEduItem();
    }
});

// 기존 호환: 수정 페이지에서 호출되는 초기화 함수 (배열 → 항목 렌더링)
function education(educationArr) {
    let $container = $("#education");
    if (!$container.length) return;
    $container.empty();
    if (educationArr && educationArr.length > 0) {
        educationArr.forEach(function (item) {
            addEduItem(item);
        });
    } else {
        // 데이터 없을 시 빈 항목 1개 추가
        addEduItem();
    }
}