/**
 * @file 다중 타사연계 관리 모듈
 * @version 0.0.1
 * @requires jQuery, Bootstrap Datepicker
 * - 한 참여자당 여러 연계(연계일 / 연계유형 / 연계비고)를 반복 행으로 등록
 * - 연계유형이 "기타 일경험"/"기타"일 때만 연계비고 입력란 노출
 * - 폼 제출 시 linkageList[i].linkDate/linkType/linkNote 로 바인딩
 */
$(function () {
    const MAX_LINK = 20;   // 연계 최대 등록 수
    const MIN_LINK = 0;    // 연계는 0건 허용 (없을 수 있음)
    const NOTE_TYPES = ["기타 일경험", "기타"]; // 연계비고 노출 대상 유형

    // 연계유형 옵션 (ParticipantCounsel.tag 의 기존 select 와 동일하게 유지)
    const LINK_TYPES = [
        "미래내일일경험",
        "지자체일경험",
        "심리안정(정신건강복지센터)",
        "복지ㆍ금용ㆍ연계",
        "기타 일경험",
        "기타"
    ];

    let linkCount = 0;
    const $container = $('#linkageListContainer');
    const $addBtn = $('#addLinkageBtn');

    if (!$container.length) return;

    // ========== 유틸 ==========
    function escapeHtml(str) {
        if (str === null || str === undefined) return '';
        return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&#039;');
    }

    function buildTypeOptions(selected) {
        let html = '<option value="">선택</option>';
        LINK_TYPES.forEach(function (t) {
            const sel = (t === selected) ? ' selected' : '';
            html += '<option value="' + escapeHtml(t) + '"' + sel + '>' + escapeHtml(t) + '</option>';
        });
        return html;
    }

    // ========== 행 관리 ==========
    function addLinkageItem(data) {
        if (linkCount >= MAX_LINK) return;
        const idx = linkCount;
        const linkDate = (data && data.linkDate) || '';
        const linkType = (data && data.linkType) || '';
        const linkNote = (data && data.linkNote) || '';
        const showNote = NOTE_TYPES.indexOf(linkType) >= 0;

        const $item = $(
            '<div class="linkage-item d-flex align-items-center gap-2 mb-2" data-idx="' + idx + '">'
            + '  <span class="linkage-rank badge bg-secondary">' + (idx + 1) + '</span>'
            + '  <div class="input-group linkage-date-group" style="max-width:180px;">'
            + '    <i class="bi bi-calendar-date input-group-text"></i>'
            + '    <input type="text" class="form-control datepicker_on linkage-date"'
            + '           name="linkageList[' + idx + '].linkDate" placeholder="yyyy-mm-dd"'
            + '           value="' + escapeHtml(linkDate) + '" autocomplete="off">'
            + '  </div>'
            + '  <select class="form-select linkage-type" style="max-width:220px;"'
            + '          name="linkageList[' + idx + '].linkType">' + buildTypeOptions(linkType) + '</select>'
            + '  <input type="text" class="form-control linkage-note"'
            + '         name="linkageList[' + idx + '].linkNote" placeholder="기타 상세 사유 입력"'
            + '         value="' + escapeHtml(linkNote) + '" style="max-width:240px;' + (showNote ? '' : 'display:none;') + '">'
            + '  <button type="button" class="btn btn-outline-danger btn-sm linkage-remove" title="삭제">'
            + '    <i class="bi bi-x-lg"></i>'
            + '  </button>'
            + '</div>'
        );

        $container.append($item);
        bindDatepicker($item.find('.datepicker_on'));
        linkCount++;
        updateState();
    }

    function removeLinkageItem($item) {
        if (linkCount <= MIN_LINK) return;
        $item.remove();
        linkCount--;
        reindex();
        updateState();
    }

    function reindex() {
        $container.children('.linkage-item').each(function (i) {
            const $el = $(this);
            $el.attr('data-idx', i);
            $el.find('.linkage-rank').text(i + 1);
            $el.find('.linkage-date').attr('name', 'linkageList[' + i + '].linkDate');
            $el.find('.linkage-type').attr('name', 'linkageList[' + i + '].linkType');
            $el.find('.linkage-note').attr('name', 'linkageList[' + i + '].linkNote');
        });
    }

    function updateState() {
        if ($addBtn.length) {
            $addBtn.prop('disabled', linkCount >= MAX_LINK);
        }
        const $countBadge = $('#linkageCount');
        if ($countBadge.length) {
            $countBadge.text(linkCount);
        }
    }

    // bootstrap-datepicker 를 동적 추가 입력란에 바인딩
    function bindDatepicker($input) {
        if (!$input || !$input.length || typeof $input.datepicker !== 'function') return;
        $input.attr('maxLength', 10);
        $input.datepicker({
            format: 'yyyy-mm-dd',
            endDate: '+10y',
            autoclose: true,
            todayHighlight: true,
            language: 'ko'
        });
    }

    // ========== 이벤트 위임 ==========
    // 연계유형 변경 시 연계비고 노출 토글
    $container.on('change', '.linkage-type', function () {
        const $row = $(this).closest('.linkage-item');
        const $note = $row.find('.linkage-note');
        const show = NOTE_TYPES.indexOf($(this).val()) >= 0;
        $note.toggle(show);
        if (!show) { $note.val(''); }
    });

    // 삭제
    $container.on('click', '.linkage-remove', function () {
        removeLinkageItem($(this).closest('.linkage-item'));
    });

    // 추가
    if ($addBtn.length) {
        $addBtn.on('click', function () {
            if (linkCount >= MAX_LINK) return;
            addLinkageItem({});
        });
    }

    // ========== 외부 초기화 API ==========
    // 수정 페이지 데이터 복원 (레거시 단일 연계 호환 포함)
    window.initLinkageList = function (arr) {
        $container.empty();
        linkCount = 0;
        if (arr && arr.length > 0) {
            arr.forEach(function (item) {
                addLinkageItem(item);
            });
        }
        updateState();
    };

    // 초기 상태
    updateState();
});
