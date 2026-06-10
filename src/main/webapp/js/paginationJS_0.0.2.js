/**
 * @file 페이지네이션 UI 생성 유틸리티
 * @version 0.0.2
 * @requires jQuery
 */
function paginationAddItems(page, startButton, endButton, totalButton) {
    //페이지네이션 변수
    const pagination = $('.pagination');

    //totalButton이 비어 있는 경우 함수 실행 종료
    if (totalButton < 0) {
        return;
    }

    //페이지네이션 삭제
    pagination.empty();
    //시작 li 코드 생성
    let addLi = pageStartLi(page);
    //숫자 li 코드 생성
    addLi += pageMiddleLi(page, startButton, endButton);
    //마지막 li 코드 생성
    addLi += pageEndLi(page, endButton, totalButton);
    //최종 코드 pagination div에 추가
    pagination.append(addLi);
}

function pageStartLi(page) {
    let startLi = '<li class="page-item ' + (page <= 10 ? 'disabled' : '') + '">';
    startLi += '<a class="page-link" href="' + searchHref((page - 10)) + '"><i class="bi bi-chevron-double-left" style="font-size: 12px;"></i></a> <li>';
    startLi += '<li class="page-item ' + (page == 1 ? 'disabled' : '') + '">';
    startLi += '<a class="page-link" href="' + searchHref((page - 1)) + '"><i class="bi bi-chevron-compact-left" style="font-size: 12px;"></i></a> <li>';
    return startLi;
}

function pageMiddleLi(page, startButton, endButton) {
    let middleLi = "";
    for(let i = startButton; i <= endButton; i++) {
        middleLi += '<li class="page-item '+(i == page ? 'active' : '')+'">';
        middleLi += '<a class="page-link" href="'+searchHref(i)+'">'+i+'</a> <li>';
    }
    return middleLi;
}

function pageEndLi(page, endButton, totalButton) {
    let endLi = '<li class="page-item ' + (page >= totalButton ? 'disabled' : '') + '">';
    endLi += '<a class="page-link" href="' + searchHref((page + 1)) + '"><i class="bi bi-chevron-compact-right" style="font-size: 12px;"></i></a> <li>';
    endLi += '<li class="page-item ' + (endButton >= totalButton ? 'disabled' : '') + '">';
    endLi += '<a class="page-link" href="' + searchHref(((endButton - (endButton % 1))+1)) + '"><i class="bi bi-chevron-double-right" style="font-size: 12px;"></i></a> <li>';
    return endLi;
}

function searchHref(page) {
    // 현재 쿼리스트링의 모든 검색 파라미터(다중값 포함)를 보존하고 page만 교체한다.
    // URLSearchParams를 사용해, page가 첫 파라미터가 아닌 경우(예: 대시보드에서
    // ?searchTypeList=noInitial&endDateOptionList=false 로 딥링크 진입)에도
    // searchTypeList/endDateOptionList 등 필터가 누락되지 않도록 한다.
    const params = new URLSearchParams(window.location.search);
    params.set('page', page);
    return '?' + params.toString();
}
