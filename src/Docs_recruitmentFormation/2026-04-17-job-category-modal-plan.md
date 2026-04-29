# 희망직무 모달 선택기 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 희망직무 등록/수정 시 select 드롭다운을 3컬럼 모달(대분류>중분류>소분류) 검색/선택 UI로 교체

**Architecture:** OCC_DATA 3단계 직무 데이터를 `jobCategorySelectRenderText_0.0.2.js`에 전역 노출. `ParticipantCounsel.tag`에 Bootstrap 5 모달 마크업 추가. `jobWishListManager_0.0.1.js`를 전면 리팩토링하여 select 대신 모달 트리거 방식으로 전환. 폼 제출 구조(`wishJobList[].categoryLarge/categoryMid/jobWant`)와 legacy hidden field 동기화는 기존과 동일 유지.

**Tech Stack:** jQuery 3.7, Bootstrap 5.3, SortableJS 1.15, AdminLTE v4 CSS

---

## File Structure

| 파일 | 역할 | 변경 유형 |
|------|------|----------|
| `src/main/webapp/js/jobCategorySelectRenderText_0.0.2.js` | OCC_DATA 전역 노출 추가 | Modify (line 157 뒤에 추가) |
| `src/main/webapp/WEB-INF/tags/ParticipantCounsel.tag` | 모달 HTML 마크업 추가 | Modify (line 174 뒤에 추가) |
| `src/main/webapp/js/jobWishListManager_0.0.1.js` | 모달 기반 위시리스트 전면 리팩토링 | Rewrite (전체) |
| `src/main/webapp/css/participantCss/custom-modern_0.0.1.css` | 3컬럼 모달 스타일 | Modify (파일 끝에 추가) |

---

### Task 1: OCC_DATA 전역 노출

**Files:**
- Modify: `src/main/webapp/js/jobCategorySelectRenderText_0.0.2.js:157` (파일 끝)

- [ ] **Step 1: OCC_DATA를 index_0.0.1.js에서 추출하여 전역 변수로 추가**

`jobCategorySelectRenderText_0.0.2.js` 파일 끝(line 157 `};` 뒤)에 `window.OCC_DATA` 추가.
OCC_DATA는 `src/main/webapp/js/recruitmentInformationJS/index_0.0.1.js` line 128~2050의 데이터를 그대로 복사.

```javascript
// 3단계 직무 분류 데이터 (고용24 OCC_DATA 기준)
// 구조: [{code, name, subs: [{code, name, subs: [{code, name}]}]}]
window.OCC_DATA = [
    // ... index_0.0.1.js line 128~2050의 OCC_DATA 전체 복사
];
```

주의: `index_0.0.1.js`의 `const OCC_DATA = [...]`에서 `[...]` 배열 부분만 복사하여 `window.OCC_DATA = [...]`로 할당.

- [ ] **Step 2: 동작 확인**

브라우저 콘솔에서 `window.OCC_DATA.length` → 13 확인.
`window.OCC_DATA[0].subs[0].subs.length` → 11 이상 확인 (01 > 011의 소분류 수).

- [ ] **Step 3: Commit**

```bash
git add src/main/webapp/js/jobCategorySelectRenderText_0.0.2.js
git commit -m "feat: expose window.OCC_DATA for 3-level job category modal"
```

---

### Task 2: 모달 HTML 마크업 추가 (ParticipantCounsel.tag)

**Files:**
- Modify: `src/main/webapp/WEB-INF/tags/ParticipantCounsel.tag:174` (</tr> 뒤, </tbody> 전)

- [ ] **Step 1: 모달 HTML 추가**

`ParticipantCounsel.tag` line 174 (`</tr>`) 뒤에 모달 마크업을 삽입. 테이블 바깥, `</tbody>` (line 176) 전에 위치시킬 수 없으므로 **테이블 종료 후** 추가. 실제로는 line 178 (`</table>`) 뒤, line 179 (`<%-- hiddenDiv`) 전에 추가.

```html
<%-- 희망직무 선택 모달 --%>
<div class="modal fade" id="wishJobModal" tabindex="-1" aria-labelledby="wishJobModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="wishJobModalLabel">
                    <i class="bi bi-briefcase-fill text-primary"></i> 희망 직무 선택
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
            </div>
            <div class="modal-body p-0">
                <%-- 검색바 --%>
                <div class="wj-modal-search">
                    <i class="bi bi-search"></i>
                    <input type="text" id="wjModalSearchInput" class="form-control" placeholder="직종명 검색...">
                </div>
                <%-- 3컬럼 패널 --%>
                <div class="wj-modal-columns" id="wjModalColumns">
                    <div class="wj-col wj-col-large" id="wjColLarge"></div>
                    <div class="wj-col wj-col-mid" id="wjColMid">
                        <div class="wj-col-placeholder">대분류를 선택하세요</div>
                    </div>
                    <div class="wj-col wj-col-sub" id="wjColSub">
                        <div class="wj-col-placeholder">중분류를 선택하세요</div>
                    </div>
                </div>
                <%-- 검색 결과 (검색 시 3컬럼 대신 표시) --%>
                <div class="wj-modal-search-results" id="wjSearchResults" style="display:none;"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary btn-sm" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-primary btn-sm" id="wjModalConfirm" disabled>선택 확인</button>
            </div>
        </div>
    </div>
</div>
```

- [ ] **Step 2: 동작 확인**

페이지 로딩 후 Elements 패널에서 `#wishJobModal` 존재 확인.

- [ ] **Step 3: Commit**

```bash
git add src/main/webapp/WEB-INF/tags/ParticipantCounsel.tag
git commit -m "feat: add wish job modal HTML markup to ParticipantCounsel.tag"
```

---

### Task 3: 3컬럼 모달 CSS 추가

**Files:**
- Modify: `src/main/webapp/css/participantCss/custom-modern_0.0.1.css` (파일 끝에 추가)

- [ ] **Step 1: 모달 스타일 추가**

`custom-modern_0.0.1.css` 파일 끝에 추가:

```css
/* ===== 희망직무 모달 3컬럼 ===== */
.wj-modal-search {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1rem;
    border-bottom: 1px solid var(--border-color);
}
.wj-modal-search .bi-search {
    color: var(--text-muted);
    font-size: 1rem;
}
.wj-modal-search .form-control {
    border: none;
    box-shadow: none;
    padding: 0.4rem 0.5rem;
}
.wj-modal-search .form-control:focus {
    box-shadow: none;
}

.wj-modal-columns {
    display: flex;
    height: 380px;
    border-bottom: 1px solid var(--border-color);
}
.wj-col {
    overflow-y: auto;
    border-right: 1px solid var(--border-color);
    flex: 1;
    min-width: 0;
}
.wj-col:last-child {
    border-right: none;
}
.wj-col-large { flex: 0 0 30%; }
.wj-col-mid   { flex: 0 0 35%; }
.wj-col-sub   { flex: 0 0 35%; }

.wj-col-placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: var(--text-muted);
    font-size: 0.85rem;
}

.wj-col-item {
    padding: 0.55rem 0.75rem;
    cursor: pointer;
    font-size: 0.85rem;
    border-bottom: 1px solid #f0f0f0;
    transition: background-color 0.15s;
    display: flex;
    align-items: center;
    gap: 0.4rem;
}
.wj-col-item:hover {
    background-color: #f8f9fa;
}
.wj-col-item.active {
    background-color: rgba(67, 97, 238, 0.08);
    color: var(--brand-primary);
    font-weight: 600;
}
.wj-col-item.selected {
    background-color: rgba(67, 97, 238, 0.15);
    color: var(--brand-primary);
    font-weight: 600;
}
.wj-col-item .bi {
    font-size: 0.75rem;
    color: var(--text-muted);
}
.wj-col-item.active .bi,
.wj-col-item.selected .bi {
    color: var(--brand-primary);
}

/* 검색 결과 */
.wj-modal-search-results {
    height: 380px;
    overflow-y: auto;
    padding: 0.5rem 0;
}
.wj-search-item {
    padding: 0.6rem 1rem;
    cursor: pointer;
    font-size: 0.85rem;
    border-bottom: 1px solid #f0f0f0;
    transition: background-color 0.15s;
}
.wj-search-item:hover {
    background-color: #f8f9fa;
}
.wj-search-item.selected {
    background-color: rgba(67, 97, 238, 0.15);
    font-weight: 600;
}
.wj-search-prefix {
    color: var(--text-muted);
    font-size: 0.78rem;
}
.wj-search-name {
    color: var(--text-main);
}
.wj-search-empty {
    text-align: center;
    color: var(--text-muted);
    padding: 3rem 1rem;
    font-size: 0.9rem;
}

/* ===== 위시리스트 아이템 (모달 방식) ===== */
.wish-job-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.6rem 0.75rem;
    margin-bottom: 0.4rem;
    background: #fff;
    border: 1px solid var(--border-color);
    border-radius: var(--radius-sm);
    transition: box-shadow 0.15s;
}
.wish-job-item:hover {
    box-shadow: var(--shadow-sm);
}
.wish-job-handle {
    cursor: grab;
    color: var(--text-muted);
    padding: 0 0.25rem;
}
.wish-job-rank {
    font-weight: 700;
    color: var(--brand-primary);
    min-width: 1.5rem;
    text-align: center;
    font-size: 0.85rem;
}
.wish-job-category {
    color: var(--text-muted);
    font-size: 0.78rem;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 280px;
}
.wish-job-want {
    flex: 1;
    min-width: 120px;
}
.wish-job-want input {
    font-size: 0.85rem;
    padding: 0.3rem 0.5rem;
}
.wish-job-actions {
    display: flex;
    gap: 0.25rem;
    flex-shrink: 0;
}
.wish-job-actions .btn {
    padding: 0.2rem 0.4rem;
    font-size: 0.75rem;
}

/* SortableJS ghost */
.sortable-ghost {
    opacity: 0.4;
    background: #e8eaf6;
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/webapp/css/participantCss/custom-modern_0.0.1.css
git commit -m "feat: add 3-column modal and wish list item styles"
```

---

### Task 4: jobWishListManager 전면 리팩토링

**Files:**
- Rewrite: `src/main/webapp/js/jobWishListManager_0.0.1.js` (전체)

- [ ] **Step 1: 전체 파일 리팩토링**

기존 174줄 전체를 새로운 모달 기반 코드로 교체. 핵심 변경:
- `addWishJobItem(data)`: select 드롭다운 대신 카테고리 텍스트 + 편집 가능한 직무명 input + [변경]/[삭제] 버튼
- `$addBtn` 클릭: 모달 열기 (신규 모드)
- [변경] 클릭: 모달 열기 (수정 모드, 기존 선택값 하이라이트)
- 모달 내부: OCC_DATA 기반 3컬럼 렌더링, 검색, 라디오 선택
- 확인 버튼: 선택된 소분류로 위시 아이템 추가/수정

새 코드:

```javascript
/**
 * 다중 희망직무 관리 모듈 (모달 선택 방식)
 * - 3컬럼 모달: 대분류 > 중분류 > 소분류(직무)
 * - 소분류 선택 시 대/중/직무명 자동 채움, 직무명 수정 가능
 * - SortableJS 드래그 순위 변경
 * - hidden input 동기화 (1순위 → 기존 jobCategoryLarge/Mid/counselJobWant)
 */
$(function () {
    var MAX_WISH = 5;
    var MIN_WISH = 1;
    var wishCount = 0;
    var $container = $('#wishJobListContainer');
    var $addBtn = $('#addWishJobBtn');
    var OCC = window.OCC_DATA || [];

    if (!$container.length || !OCC.length) return;

    // --- 모달 상태 ---
    var editingIdx = -1;          // -1 = 신규, 0+ = 수정 대상 인덱스
    var activeLargeIdx = -1;      // 좌측 패널 선택 인덱스
    var activeMidIdx = -1;        // 중앙 패널 선택 인덱스
    var selectedSub = null;       // {code, name, midName, largeName}

    var $modal = null;            // Bootstrap Modal 인스턴스 (lazy)
    var $colLarge = $('#wjColLarge');
    var $colMid = $('#wjColMid');
    var $colSub = $('#wjColSub');
    var $searchInput = $('#wjModalSearchInput');
    var $searchResults = $('#wjSearchResults');
    var $columns = $('#wjModalColumns');
    var $confirmBtn = $('#wjModalConfirm');

    // ========== 유틸 ==========
    function escapeHtml(str) {
        if (!str) return '';
        return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&#039;');
    }

    // ========== 모달 렌더링 ==========
    function renderLargePanel() {
        var html = '';
        OCC.forEach(function (cat, i) {
            var cls = (i === activeLargeIdx) ? ' active' : '';
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
        var subs = OCC[activeLargeIdx].subs || [];
        var html = '';
        subs.forEach(function (mid, i) {
            var cls = (i === activeMidIdx) ? ' active' : '';
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
        var midSubs = (OCC[activeLargeIdx].subs[activeMidIdx] || {}).subs || [];
        var largeName = OCC[activeLargeIdx].name;
        var midName = OCC[activeLargeIdx].subs[activeMidIdx].name;
        var html = '';
        midSubs.forEach(function (sub) {
            var isSel = selectedSub && selectedSub.code === sub.code;
            var cls = isSel ? ' selected' : '';
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
        var $el = $(this);
        selectedSub = {
            code: $el.data('code'),
            name: $el.data('name'),
            midName: $el.data('mid'),
            largeName: $el.data('large')
        };
        renderSubPanel();
        updateConfirmBtn();
    });

    // 검색
    $searchInput.on('input', function () {
        var kw = ($(this).val() || '').trim().toLowerCase();
        if (!kw) {
            $searchResults.hide();
            $columns.show();
            return;
        }
        $columns.hide();
        $searchResults.show();

        var results = [];
        OCC.forEach(function (cat) {
            cat.subs.forEach(function (mid) {
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

        var html = '';
        results.forEach(function (r) {
            var isSel = selectedSub && selectedSub.code === r.code;
            var cls = isSel ? ' selected' : '';
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
        var $el = $(this);
        selectedSub = {
            code: $el.data('code'),
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
            var $item = $container.children('.wish-job-item').eq(editingIdx);
            var prevLarge = $item.data('large') || '';
            var prevMid = $item.data('mid') || '';
            var prevCode = $item.data('code') || '';

            for (var li = 0; li < OCC.length; li++) {
                if (OCC[li].name === prevLarge) {
                    activeLargeIdx = li;
                    var mids = OCC[li].subs || [];
                    for (var mi = 0; mi < mids.length; mi++) {
                        if (mids[mi].name === prevMid) {
                            activeMidIdx = mi;
                            var subs = mids[mi].subs || [];
                            for (var si = 0; si < subs.length; si++) {
                                if (subs[si].code === prevCode) {
                                    selectedSub = {
                                        code: subs[si].code,
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
            // 수정 모드
            updateWishJobItem(editingIdx, selectedSub);
        } else {
            // 신규 추가
            addWishJobItem({
                categoryLarge: selectedSub.largeName,
                categoryMid: selectedSub.midName,
                jobWant: selectedSub.name,
                code: selectedSub.code
            });
        }
        $modal.hide();
    });

    // ========== 위시리스트 관리 ==========
    function addWishJobItem(data) {
        if (wishCount >= MAX_WISH) return;
        var idx = wishCount;
        var large = (data && data.categoryLarge) || '';
        var mid = (data && data.categoryMid) || '';
        var want = (data && data.jobWant) || '';
        var code = (data && data.code) || '';

        var $item = $(
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
        var $item = $container.children('.wish-job-item').eq(idx);
        if (!$item.length) return;
        $item.data('large', sub.largeName);
        $item.data('mid', sub.midName);
        $item.data('code', sub.code);
        $item.attr('data-large', sub.largeName);
        $item.attr('data-mid', sub.midName);
        $item.attr('data-code', sub.code);
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
            var $el = $(this);
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
        var $countBadge = $('#wishJobCount');
        if ($countBadge.length) {
            $countBadge.text(wishCount);
        }
    }

    // ========== 이벤트 위임 ==========
    // 삭제
    $container.on('click', '.wish-job-remove', function () {
        removeWishJobItem($(this).closest('.wish-job-item'));
    });

    // 변경 (모달 재오픈)
    $container.on('click', '.wish-job-edit', function () {
        var idx = $(this).closest('.wish-job-item').index();
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
        var $first = $container.children('.wish-job-item').first();
        if ($first.length) {
            $('#jobCategoryLarge').val($first.find('.wish-large-input').val() || '');
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
            addWishJobItem();
        }
    };

    // 초기 상태: 최소 1개 (신규 페이지)
    if (wishCount === 0) {
        addWishJobItem();
    }
});
```

- [ ] **Step 2: 신규 참여자 페이지에서 동작 확인**

1. `/newparticipant.login` 접속
2. "희망직무 추가" 클릭 → 모달 열림 확인
3. 좌측 대분류 클릭 → 중앙 중분류 로딩 확인
4. 중앙 중분류 클릭 → 우측 소분류 로딩 확인
5. 소분류 클릭 → "선택 확인" 버튼 활성화 확인
6. "선택 확인" → 위시리스트에 1건 추가 확인 (대분류 > 중분류 텍스트 + 직무명 input)
7. 직무명 input 수정 가능 확인
8. [변경] 버튼 → 모달 재오픈, 기존 선택 하이라이트 확인
9. 검색 input에 키워드 입력 → 검색결과 표시 확인
10. 드래그앤드롭 순서 변경 확인

- [ ] **Step 3: 수정 참여자 페이지에서 동작 확인**

1. `/participantUpdate.login` 접속 (기존 참여자)
2. 기존 희망직무 데이터가 위시리스트에 복원되는지 확인
3. `initWishJobList` 호출 시 `categoryLarge`, `categoryMid`, `jobWant`가 올바르게 표시되는지 확인

- [ ] **Step 4: Commit**

```bash
git add src/main/webapp/js/jobWishListManager_0.0.1.js
git commit -m "feat: rewrite jobWishListManager with 3-column modal selector"
```

---

### Task 5: 문서 업데이트 및 최종 검증

**Files:**
- Modify: `src/Docs_recruitmentFormation/2026-04-17-job-category-modal-design.md`

- [ ] **Step 1: 전체 흐름 E2E 검증**

1. `./mvnw spring-boot:run` 서버 기동
2. 신규 참여자 등록 → 희망직무 3건 추가 → 폼 제출 → DB에 `wishJobList[0~2]` 저장 확인
3. 수정 페이지 → 기존 데이터 복원 확인 → 직무명 수정 → 폼 제출 → DB 업데이트 확인
4. 알선업체 목록 페이지 → 대분류/중분류 필터 정상 동작 확인

- [ ] **Step 2: 문서 업데이트**

설계 스펙 문서에 "구현 완료" 상태 추가.

- [ ] **Step 3: Commit**

```bash
git add src/Docs_recruitmentFormation/
git commit -m "docs: update job category modal spec with implementation status"
```
