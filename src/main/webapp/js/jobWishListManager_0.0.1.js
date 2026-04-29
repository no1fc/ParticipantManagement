/**
 * 다중 희망직무 관리 모듈 (모달 선택 방식)
 * - 3컬럼 모달: 대분류 > 중분류 > 소분류(직무)
 * - 소분류 선택 시 대/중/직무명 자동 채움, 직무명 수정 가능
 * - SortableJS 드래그 순위 변경
 * - hidden input 동기화 (1순위 → 기존 jobCategoryLarge/Mid/counselJobWant)
 */
$(function () {
    let MAX_WISH = 5;
    let MIN_WISH = 1;
    let wishCount = 0;
    let $container = $('#wishJobListContainer');
    let $addBtn = $('#addWishJobBtn');
    let OCC = window.OCC_DATA || [];

    if (!$container.length) return;

    // --- 모달 상태 ---
    let editingIdx = -1;          // -1 = 신규, 0+ = 수정 대상 인덱스
    let activeLargeIdx = -1;      // 좌측 패널 선택 인덱스
    let activeMidIdx = -1;        // 중앙 패널 선택 인덱스
    let selectedSub = null;       // {code, name, midName, largeName}

    let $modal = null;            // Bootstrap Modal 인스턴스 (lazy)
    let $colLarge = $('#wjColLarge');
    let $colMid = $('#wjColMid');
    let $colSub = $('#wjColSub');
    let $searchInput = $('#wjModalSearchInput');
    let $searchResults = $('#wjSearchResults');
    let $columns = $('#wjModalColumns');
    let $confirmBtn = $('#wjModalConfirm');

    // ========== 유틸 ==========
    function escapeHtml(str) {
        if (!str) return '';
        return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&#039;');
    }

    // ========== 모달 렌더링 ==========
    function renderLargePanel() {
        let html = '';
        OCC.forEach(function (cat, i) {
            let cls = (i === activeLargeIdx) ? ' active' : '';
            html += '<div class="wj-col-item' + cls + '" data-idx="' + i + '">'
                + '<i class="bi bi-chevron-right"></i> ' + escapeHtml(cat.name) + '</div>';
        });
        $colLarge.html(html);
    }

    function renderMidPanel() {
        if (activeLargeIdx < 0) {
            $colMid.html('<div class="wj-col-placeholder">대분류를 선택하세요</div>');
            $colSub.html('<div class="wj-col-placeholder">중분류를 선택하세요</div>');
            return;
        }
        let subs = OCC[activeLargeIdx].subs || [];
        let html = '';
        subs.forEach(function (mid, i) {
            let cls = (i === activeMidIdx) ? ' active' : '';
            html += '<div class="wj-col-item' + cls + '" data-idx="' + i + '">'
                + '<i class="bi bi-chevron-right"></i> ' + escapeHtml(mid.name) + '</div>';
        });
        $colMid.html(html);
    }

    function renderSubPanel() {
        if (activeLargeIdx < 0 || activeMidIdx < 0) {
            $colSub.html('<div class="wj-col-placeholder">중분류를 선택하세요</div>');
            return;
        }
        let midSubs = (OCC[activeLargeIdx].subs[activeMidIdx] || {}).subs || [];
        let largeName = OCC[activeLargeIdx].name;
        let midName = OCC[activeLargeIdx].subs[activeMidIdx].name;
        let html = '';
        midSubs.forEach(function (sub) {
            let isSel = selectedSub && String(selectedSub.code) === String(sub.code);
            let cls = isSel ? ' selected' : '';
            html += '<div class="wj-col-item' + cls + '" data-code="' + sub.code + '"'
                + ' data-name="' + escapeHtml(sub.name) + '"'
                + ' data-mid="' + escapeHtml(midName) + '"'
                + ' data-large="' + escapeHtml(largeName) + '">'
                + '<i class="bi ' + (isSel ? 'bi-check-circle-fill' : 'bi-circle') + '"></i> '
                + escapeHtml(sub.name) + '</div>';
        });
        if (!midSubs.length) {
            html = '<div class="wj-col-placeholder">소분류가 없습니다</div>';
        }
        $colSub.html(html);
    }

    function updateConfirmBtn() {
        $confirmBtn.prop('disabled', !selectedSub);
    }

    // ========== 모달 이벤트 ==========
    // 대분류 클릭
    $colLarge.on('click', '.wj-col-item', function () {
        activeLargeIdx = parseInt($(this).data('idx'));
        activeMidIdx = -1;
        renderLargePanel();
        renderMidPanel();
        renderSubPanel();
    });

    // 중분류 클릭
    $colMid.on('click', '.wj-col-item', function () {
        activeMidIdx = parseInt($(this).data('idx'));
        renderMidPanel();
        renderSubPanel();
    });

    // 소분류 클릭 (라디오 방식: 1건만)
    $colSub.on('click', '.wj-col-item', function () {
        let $el = $(this);
        selectedSub = {
            code: String($el.data('code')),
            name: $el.data('name'),
            midName: $el.data('mid'),
            largeName: $el.data('large')
        };
        renderSubPanel();
        updateConfirmBtn();
    });

    // 검색
    $searchInput.on('input', function () {
        let kw = ($(this).val() || '').trim().toLowerCase();
        if (!kw) {
            $searchResults.hide();
            $columns.show();
            return;
        }
        $columns.hide();
        $searchResults.show();

        let results = [];
        OCC.forEach(function (cat) {
            (cat.subs || []).forEach(function (mid) {
                (mid.subs || []).forEach(function (sub) {
                    if (sub.name.toLowerCase().indexOf(kw) >= 0
                        || mid.name.toLowerCase().indexOf(kw) >= 0) {
                        results.push({
                            code: sub.code,
                            name: sub.name,
                            midName: mid.name,
                            largeName: cat.name
                        });
                    }
                });
            });
        });

        if (!results.length) {
            $searchResults.html('<div class="wj-search-empty">검색 결과가 없습니다</div>');
            return;
        }

        let html = '';
        results.forEach(function (r) {
            let isSel = selectedSub && selectedSub.code === r.code;
            let cls = isSel ? ' selected' : '';
            html += '<div class="wj-search-item' + cls + '" data-code="' + r.code + '"'
                + ' data-name="' + escapeHtml(r.name) + '"'
                + ' data-mid="' + escapeHtml(r.midName) + '"'
                + ' data-large="' + escapeHtml(r.largeName) + '">'
                + '<span class="wj-search-prefix">[' + escapeHtml(r.largeName.split('·')[0]) + '] '
                + escapeHtml(r.midName) + ' &gt; </span>'
                + '<span class="wj-search-name">' + escapeHtml(r.name) + '</span>'
                + '</div>';
        });
        $searchResults.html(html);
    });

    // 검색 결과 클릭
    $searchResults.on('click', '.wj-search-item', function () {
        let $el = $(this);
        selectedSub = {
            code: String($el.data('code')),
            name: $el.data('name'),
            midName: $el.data('mid'),
            largeName: $el.data('large')
        };
        $searchResults.find('.wj-search-item').removeClass('selected');
        $el.addClass('selected');
        updateConfirmBtn();
    });

    // 모달 열기
    function openModal(idx) {
        editingIdx = (typeof idx === 'number') ? idx : -1;
        activeLargeIdx = -1;
        activeMidIdx = -1;
        selectedSub = null;
        $searchInput.val('');
        $searchResults.hide();
        $columns.show();

        // 수정 모드: 기존 값으로 패널 위치 복원
        if (editingIdx >= 0) {
            let $item = $container.children('.wish-job-item').eq(editingIdx);
            let prevLarge = $item.attr('data-large') || '';
            let prevMid = $item.attr('data-mid') || '';
            let prevCode = $item.attr('data-code') || '';

            for (let li = 0; li < OCC.length; li++) {
                if (OCC[li].name === prevLarge) {
                    activeLargeIdx = li;
                    let mids = OCC[li].subs || [];
                    for (let mi = 0; mi < mids.length; mi++) {
                        if (mids[mi].name === prevMid) {
                            activeMidIdx = mi;
                            let subs = mids[mi].subs || [];
                            for (let si = 0; si < subs.length; si++) {
                                if (String(subs[si].code) === String(prevCode)) {
                                    selectedSub = {
                                        code: String(subs[si].code),
                                        name: subs[si].name,
                                        midName: mids[mi].name,
                                        largeName: OCC[li].name
                                    };
                                }
                            }
                        }
                    }
                }
            }
        }

        renderLargePanel();
        renderMidPanel();
        renderSubPanel();
        updateConfirmBtn();

        if (!$modal) {
            $modal = new bootstrap.Modal(document.getElementById('wishJobModal'));
        }
        $modal.show();
    }

    // 확인 버튼
    $confirmBtn.on('click', function () {
        if (!selectedSub) return;
        if (editingIdx >= 0) {
            updateWishJobItem(editingIdx, selectedSub);
        } else {
            addWishJobItem({
                categoryLarge: selectedSub.largeName,
                categoryMid: selectedSub.midName,
                jobWant: selectedSub.name,
                code: String(selectedSub.code)
            });
        }
        $modal.hide();
    });

    // ========== 위시리스트 관리 ==========
    function addWishJobItem(data) {
        if (wishCount >= MAX_WISH) return;
        let idx = wishCount;
        let large = (data && data.categoryLarge) || '';
        let mid = (data && data.categoryMid) || '';
        let want = (data && data.jobWant) || '';
        let code = (data && data.code) || '';

        let $item = $(
            '<div class="wish-job-item" data-idx="' + idx + '"'
            + ' data-large="' + escapeHtml(large) + '"'
            + ' data-mid="' + escapeHtml(mid) + '"'
            + ' data-code="' + escapeHtml(code) + '">'
            + '  <span class="wish-job-handle"><i class="bi bi-grip-vertical"></i></span>'
            + '  <span class="wish-job-rank">' + (idx + 1) + '</span>'
            + '  <span class="wish-job-category" title="' + escapeHtml(large + ' > ' + mid) + '">'
            +       escapeHtml(large) + ' &gt; ' + escapeHtml(mid)
            + '  </span>'
            + '  <span class="wish-job-want">'
            + '    <input type="text" class="form-control form-control-sm wish-want"'
            + '           name="wishJobList[' + idx + '].jobWant" value="' + escapeHtml(want) + '"'
            + '           placeholder="희망직무명">'
            + '  </span>'
            + '  <input type="hidden" class="wish-large-input" name="wishJobList[' + idx + '].categoryLarge" value="' + escapeHtml(large) + '">'
            + '  <input type="hidden" class="wish-mid-input" name="wishJobList[' + idx + '].categoryMid" value="' + escapeHtml(mid) + '">'
            + '  <input type="hidden" class="wish-rank-input" name="wishJobList[' + idx + '].wishRank" value="' + (idx + 1) + '">'
            + '  <span class="wish-job-actions">'
            + '    <button type="button" class="btn btn-outline-secondary btn-sm wish-job-edit" title="변경">'
            + '      <i class="bi bi-pencil"></i>'
            + '    </button>'
            + '    <button type="button" class="btn btn-outline-danger btn-sm wish-job-remove" title="삭제">'
            + '      <i class="bi bi-x-lg"></i>'
            + '    </button>'
            + '  </span>'
            + '</div>'
        );

        $container.append($item);
        wishCount++;
        updateState();
    }

    function updateWishJobItem(idx, sub) {
        let $item = $container.children('.wish-job-item').eq(idx);
        if (!$item.length) return;
        $item.attr('data-large', sub.largeName);
        $item.attr('data-mid', sub.midName);
        $item.attr('data-code', String(sub.code));
        $item.find('.wish-job-category')
            .attr('title', sub.largeName + ' > ' + sub.midName)
            .html(escapeHtml(sub.largeName) + ' &gt; ' + escapeHtml(sub.midName));
        $item.find('.wish-want').val(sub.name);
        $item.find('.wish-large-input').val(sub.largeName);
        $item.find('.wish-mid-input').val(sub.midName);
    }

    function removeWishJobItem($item) {
        if (wishCount <= MIN_WISH) return;
        $item.remove();
        wishCount--;
        reindex();
        updateState();
    }

    function reindex() {
        $container.children('.wish-job-item').each(function (i) {
            let $el = $(this);
            $el.attr('data-idx', i);
            $el.find('.wish-job-rank').text(i + 1);
            $el.find('.wish-want').attr('name', 'wishJobList[' + i + '].jobWant');
            $el.find('.wish-large-input').attr('name', 'wishJobList[' + i + '].categoryLarge');
            $el.find('.wish-mid-input').attr('name', 'wishJobList[' + i + '].categoryMid');
            $el.find('.wish-rank-input').attr('name', 'wishJobList[' + i + '].wishRank').val(i + 1);
        });
    }

    function updateState() {
        if ($addBtn.length) {
            $addBtn.prop('disabled', wishCount >= MAX_WISH);
        }
        $container.find('.wish-job-remove').each(function () {
            $(this).prop('disabled', wishCount <= MIN_WISH);
        });
        let $countBadge = $('#wishJobCount');
        if ($countBadge.length) {
            $countBadge.text(wishCount);
        }
        // 희망직무 변경 시 1순위 동기화 + 추천 키워드 갱신
        if (typeof window.syncPrimaryJobWish === 'function') {
            window.syncPrimaryJobWish();
        }
    }

    // ========== 이벤트 위임 ==========
    // 삭제
    $container.on('click', '.wish-job-remove', function () {
        removeWishJobItem($(this).closest('.wish-job-item'));
    });

    // 변경 (모달 재오픈)
    $container.on('click', '.wish-job-edit', function () {
        let idx = $(this).closest('.wish-job-item').index();
        openModal(idx);
    });

    // 추가 버튼 → 모달
    if ($addBtn.length) {
        $addBtn.on('click', function () {
            if (wishCount >= MAX_WISH) return;
            openModal(-1);
        });
    }

    // SortableJS
    if (typeof Sortable !== 'undefined') {
        Sortable.create($container[0], {
            handle: '.wish-job-handle',
            animation: 150,
            ghostClass: 'sortable-ghost',
            filter: 'input, button',
            preventOnFilter: false,
            onEnd: function () {
                reindex();
                updateState();
            }
        });
    }

    // 1순위 동기화
    window.syncPrimaryJobWish = function () {
        let $first = $container.children('.wish-job-item').first();
        if ($first.length) {
            $('#jobCategoryLarge').val($first.find('.wish-large-input').val() || '').trigger('change');
            $('#jobCategoryMid').val($first.find('.wish-mid-input').val() || '');
            $('#counselJobWant').val($first.find('.wish-want').val() || '');
        }
    };

    // 수정 페이지 데이터 복원
    window.initWishJobList = function (arr) {
        $container.empty();
        wishCount = 0;
        if (arr && arr.length > 0) {
            arr.forEach(function (item) {
                addWishJobItem(item);
            });
        } else {
            // 신규: 빈 항목 없이 시작 (모달로 추가하므로)
            updateState();
        }
    };

    // 초기 상태
    updateState();
});