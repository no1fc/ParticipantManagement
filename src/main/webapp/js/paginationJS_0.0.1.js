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
    let href = '?page=' + (page);
    let search = window.location.search.split('&');
    if (search[1] != null || search[1] != undefined) {
        /*console.log('search :['+search+']');*/
        search.forEach(function (item) {
            /*console.log('item['+item+']');
            console.log('search['+search.indexOf(item)+']');*/
            if (search.indexOf(item) > 0) {
                href += '&' + item
            }
        });
        /*console.log('href :['+href+']');*/
    }
    return href;
}
