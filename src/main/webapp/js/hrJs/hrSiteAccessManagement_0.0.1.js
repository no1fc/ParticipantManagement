/**
 * @file HR 사이트 접속·권한 관리 (데모) — J_직원_사이트접속 CRUD
 * @version 0.0.1
 * @requires jQuery, SweetAlert2, DataTables, Bootstrap, OverlayScrollbars
 */

let siteAccessTable, siteAccessModal;
let siteList = [];

$(document).ready(function () {
    siteAccessTable = $('#siteAccessTable').DataTable({
        language: { url: '//cdn.datatables.net/plug-ins/1.13.4/i18n/ko.json' },
        order: [[0, 'asc']],
        pageLength: 25
    });
    siteAccessModal = new bootstrap.Modal(document.getElementById('siteAccessModal'));
    loadSites(loadSiteAccess);
});

function loadSites(callback) {
    $.ajax({
        url: '/hr/api/sites',
        method: 'GET',
        success: function (data) {
            siteList = data;
            const filterOptions = ['<option value="">전체</option>'];
            const modalOptions = [];
            data.forEach(function (s) {
                filterOptions.push(`<option value="${s.siteCode}">${s.siteName} (${s.siteCode})</option>`);
                modalOptions.push(`<option value="${s.siteCode}">${s.siteName} (${s.siteCode})</option>`);
            });
            $('#searchSiteCode').html(filterOptions.join(''));
            $('#siteCode').html(modalOptions.join(''));
            if (typeof callback === 'function') callback();
        },
        error: function () { Swal.fire('오류', '사이트 목록 로드 중 오류가 발생했습니다.', 'error'); }
    });
}

function currentSearch() {
    return {
        searchUserId: $('#searchUserId').val(),
        searchSiteCode: $('#searchSiteCode').val()
    };
}

function loadSiteAccess() {
    $.ajax({
        url: '/hr/api/site-access',
        method: 'GET',
        data: currentSearch(),
        success: function (data) {
            siteAccessTable.clear();
            data.forEach(function (item) {
                const allowBadge = item.accessAllowed
                    ? '<span class="hr-badge hr-allow-on">허용</span>'
                    : '<span class="hr-badge hr-allow-off">차단</span>';
                const roleText = item.siteRole
                    ? item.siteRole
                    : '<span class="text-muted">(상속)</span>';
                accessRow(item, allowBadge, roleText);
            });
            siteAccessTable.draw(false);
        },
        error: function () { Swal.fire('오류', '접속 목록 로드 중 오류가 발생했습니다.', 'error'); }
    });
}

function accessRow(item, allowBadge, roleText) {
    siteAccessTable.row.add([
        item.userId,
        item.userName || '',
        (item.siteName || '') + ' (' + item.siteCode + ')',
        item.deptCode || '<span class="text-muted">(공통)</span>',
        allowBadge,
        roleText,
        item.grantDate || '-',
        `<button class="btn btn-sm btn-warning" onclick="editSiteAccess(${item.accessPk})"><i class="bi bi-pencil"></i> 수정</button>
         <button class="btn btn-sm btn-danger" onclick="deleteSiteAccess(${item.accessPk})"><i class="bi bi-trash"></i> 회수</button>`
    ]);
}

function searchSiteAccess() { loadSiteAccess(); }

function resetSiteAccessSearch() {
    $('#searchUserId').val('');
    $('#searchSiteCode').val('');
    loadSiteAccess();
}

function setKeyFieldsEditable(editable) {
    $('#userId').prop('readonly', !editable);
    $('#siteCode').prop('disabled', !editable);
    $('#deptCode').prop('readonly', !editable);
}

function openAddSiteAccessModal() {
    $('#siteAccessForm')[0].reset();
    $('#accessPk').val('');
    $('#siteAccessModalLabel').text('사이트 접속 부여');
    setKeyFieldsEditable(true);
    $('#accessAllowed').val('true');
    siteAccessModal.show();
}

function editSiteAccess(accessPk) {
    $.ajax({
        url: '/hr/api/site-access/' + accessPk,
        method: 'GET',
        success: function (d) {
            $('#siteAccessModalLabel').text('접속 정보 수정');
            $('#accessPk').val(d.accessPk);
            $('#userId').val(d.userId);
            $('#siteCode').val(d.siteCode);
            $('#deptCode').val(d.deptCode || '');
            $('#accessAllowed').val(d.accessAllowed ? 'true' : 'false');
            $('#siteRole').val(d.siteRole || '');
            setKeyFieldsEditable(false);
            siteAccessModal.show();
        },
        error: function () { Swal.fire('오류', '접속 정보 조회 중 오류가 발생했습니다.', 'error'); }
    });
}

function saveSiteAccess() {
    const accessPk = $('#accessPk').val();
    const userId = $('#userId').val().trim();
    const siteCode = $('#siteCode').val();
    if (!userId || !siteCode) { Swal.fire('확인', '직원아이디와 사이트는 필수입니다.', 'warning'); return; }

    const formData = {
        userId: userId,
        siteCode: siteCode,
        deptCode: $('#deptCode').val().trim(),
        accessAllowed: $('#accessAllowed').val() === 'true',
        siteRole: $('#siteRole').val().trim()
    };

    const isEdit = accessPk !== '';
    const method = isEdit ? 'PUT' : 'POST';
    const url = isEdit ? '/hr/api/site-access/' + accessPk : '/hr/api/site-access';

    $.ajax({
        url: url,
        method: method,
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function (res) {
            if (res.success) { Swal.fire('성공', res.message, 'success'); siteAccessModal.hide(); loadSiteAccess(); }
            else { Swal.fire('오류', res.message, 'error'); }
        },
        error: function () { Swal.fire('오류', '저장 중 오류가 발생했습니다.', 'error'); }
    });
}

function deleteSiteAccess(accessPk) {
    Swal.fire({
        title: '접속 회수',
        text: '해당 사이트 접속 행을 삭제(회수)하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '회수',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/hr/api/site-access/' + accessPk,
                method: 'DELETE',
                success: function (res) {
                    Swal.fire(res.success ? '완료' : '오류', res.message, res.success ? 'success' : 'error');
                    loadSiteAccess();
                },
                error: function () { Swal.fire('오류', '회수 중 오류가 발생했습니다.', 'error'); }
            });
        }
    });
}

const { OverlayScrollbars } = OverlayScrollbarsGlobal;
if (document.querySelector('.app-sidebar-wrapper')) {
    OverlayScrollbars(document.querySelector('.app-sidebar-wrapper'), {});
}
