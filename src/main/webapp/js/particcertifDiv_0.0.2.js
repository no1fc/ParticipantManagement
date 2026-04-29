/**
 * 자격증 동적 추가/삭제 모듈 (희망직무와 동일 패턴)
 * - 항목 단위 [핸들][입력][삭제] 구조
 * - 추가 버튼: #addCertBtn
 * - 컨테이너: #particcertifCertif (기존 ID 유지)
 * - 입력 클래스: .particcertifCertif (자동완성 위임 호환)
 * - 폼 name: particcertifCertifs[], particcertifPartNos[]
 * - SortableJS 드래그 정렬
 */

// HTML 이스케이프
function _certEscapeHtml(str) {
    if (!str) return '';
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;').replace(/'/g, '&#039;');
}

// 자격증 항목 카운트 갱신
function _updateCertCount() {
    var $count = $("#certCount");
    if ($count.length) {
        $count.text($("#particcertifCertif").children('.cert-item').length);
    }
}

// 자격증 항목 추가 (data: { particcertif, particcertifPartNo })
function addCertItem(data) {
    var $container = $("#particcertifCertif");
    if (!$container.length) return;
    var value = (data && data.particcertif) || '';
    var pk = (data && data.particcertifPartNo) || 0;

    var $item = $(
        '<div class="cert-item">' +
        '  <span class="cert-handle" title="드래그로 순서 변경"><i class="bi bi-grip-vertical"></i></span>' +
        '  <input type="text" class="form-control particcertifCertif" name="particcertifCertifs" value="' + _certEscapeHtml(value) + '" placeholder="자격증 입력">' +
        '  <input type="hidden" name="particcertifPartNos" value="' + pk + '">' +
        '  <button type="button" class="cert-remove" title="삭제"><i class="bi bi-x-lg"></i></button>' +
        '</div>'
    );
    $container.append($item);
    _updateCertCount();
}

// 자격증 항목 삭제
function removeCertItem($item) {
    $item.remove();
    _updateCertCount();
}

$(document).ready(function () {
    var $container = $("#particcertifCertif");
    // 진단 로그: 캐시/타이밍 이슈 추적용 (문제 해결 후 제거 가능)
    console.log('[particcertifDiv] ready - container:', $container.length, 'btn:', $("#addCertBtn").length);
    if (!$container.length) return;

    // 추가 버튼
    $("#addCertBtn").on("click", function () {
        console.log('[particcertifDiv] addCertBtn click!');
        addCertItem();
    });

    // 삭제 버튼 (이벤트 위임)
    $container.on("click", ".cert-remove", function () {
        removeCertItem($(this).closest('.cert-item'));
    });

    // SortableJS 드래그 정렬
    // ※ forceFallback 제거(데스크톱 native 우선), filter로 input/button 클릭 보호
    if (typeof Sortable !== 'undefined') {
        Sortable.create($container[0], {
            handle: '.cert-handle',
            animation: 150,
            ghostClass: 'sortable-ghost',
            filter: 'input, button',
            preventOnFilter: false
        });
    }

    // 신규 페이지: 빈 항목 1개 자동 추가 (specialty가 호출되지 않는 경우)
    if ($container.children('.cert-item').length === 0) {
        addCertItem();
    }
});

// 기존 호환: 수정 페이지에서 호출되는 초기화 함수 (배열 → 항목 렌더링)
function specialty(specialtyArr) {
    var $container = $("#particcertifCertif");
    if (!$container.length) return;
    $container.empty();
    if (specialtyArr && specialtyArr.length > 0) {
        specialtyArr.forEach(function (item) {
            addCertItem(item);
        });
    } else {
        // 데이터 없을 시 빈 항목 1개 추가
        addCertItem();
    }
}