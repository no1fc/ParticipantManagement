$(document).ready(function () {

    //table tr 개수 지정
    let trCount = $('.align-middle tr').length;
    let trCountSpan = $('.countSpan');
    trCountSpan.text(trCount);

    // DOM 로드 후 툴팁 활성화
    $('[data-bs-toggle="tooltip"]').tooltip({
        delay: {show: 0, hide: 0} // 표시와 사라짐에 딜레이를 없애즉시 나타나도록 설정
    });

    //선택 버튼의 구직번호 불러올 함수
    function getJobNumber(currentRow) {
        // '구직번호' 열의 텍스트 추출 (구직번호가 2번째 열이라고 가정)
        return currentRow.find('td').eq(1).find('input').val();
    }

    //참여자 성명 불러올 함수
    function getParticipantName(currentRow) {
        // '참여자' 열의 텍스트 추출 (참여자가 3번째 열이라고 가정)
        return currentRow.find('td').eq(3).find('a').text();
    }

    //페이지 로드시 지점 관리자 페이지 접속인지 확인
    const branchManagementPageFlag = $('#branchManagementPageFlag').val();
    console.log('branchManagementPageFlag : ' + branchManagementPageFlag);
    
    // a태그 href search 값 변경
    aHrefChange();
    function aHrefChange() {
        let selectATag = $('.selectParticipant');

        selectATag.each(function () {
            let aTag = $(this);
            const jobNo = getJobNumber(aTag.closest('tr'));
            // console.log('jobNo : ' + jobNo);
            aTag.attr('href', aTag.attr('href') + '?' + searchMainHref('basicJobNo=' + jobNo + '&branchManagementPageFlag='+ branchManagementPageFlag));
        })
    }

    // URL 파라미터 추출 함수
    function getUrlParameter(name) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(name) || '';
    }

    function escapeHtml(value) {
        return $('<div />').text(value).html();
    }

    function renderFilterTags() {
        const $container = $('#filter-tags');
        const $form = $('#searchForm');
        console.log('renderFilterTags');
        console.log($container);
        console.log($form);
        if ($container.length === 0 || $form.length === 0) {
            return;
        }
        console.log('renderFilterTags end');

        const filters = [];
        const getParamText = (name) => {
            const value = getUrlParameter(name);
            if (value === '') {
                return { value: '', text: '' };
            }

            const $select = $form.find('select[name="' + name + '"]');
            if ($select.length > 0) {
                const $option = $select.find('option[value="' + value + '"]');
                const text = $option.length > 0 ? $option.text().trim() : value;
                return { value, text };
            }

            const $radio = $form.find('input[name="' + name + '"][value="' + value + '"]');
            if ($radio.length > 0) {
                const labelText = $radio.closest('.form-check').find('label').text().trim();
                return { value, text: labelText || value };
            }

            return { value, text: value };
        };

        const endDate = getParamText('endDateOption');
        if (endDate.value !== '') {
            filters.push({ label: '상태', value: endDate.text });
        }

        const initCons = getParamText('participantInItCons');
        if (initCons.value !== '' && initCons.value !== 'All') {
            filters.push({ label: '연도', value: initCons.text });
        }

        const partType = getParamText('participantPartType');
        if (partType.value !== '') {
            filters.push({ label: '유형', value: partType.text });
        }

        const searchParam = getParamText('search');
        if (searchParam.value !== '') {
            filters.push({ label: '검색', value: searchParam.text });
        }

        const searchTypeParam = getParamText('searchType');
        if (searchTypeParam.value !== '') {
            filters.push({ label: '옵션', value: searchTypeParam.text });
        }

        $container.empty();
        if (filters.length === 0) {
            return;
        }

        filters.forEach((filter) => {
            $container.append('<span class="filter-tag">' + filter.label + ': ' + escapeHtml(filter.value) + '</span>');
        });

        const actionUrl = $form.attr('action') || '/participant.login';
        $container.append('<a class="filter-reset" href="' + actionUrl + '">전체 해제</a>');
    }

    // 내림차순 오름차순 조회 함수
    let tableColumn = $('.table-Column');
    let columnParam = getUrlParameter('column');
    let orderParam = getUrlParameter('order');
    let columns = $('.column');

    if(columnParam != null && columnParam != ''){
        columns.each(function () {
            let columnValue = $(this).text();
            let form = $('#searchForm');

            if(columnValue == columnParam){
                if(orderParam == 'desc'){
                    $(this).append('<span class="order desc"><i class="bi bi-sort-down"></i></span>');
                    $(this).find('.order').val('desc');
                    form.append('<input type="hidden" name="column" value="'+columnValue+'">');
                    form.append('<input type="hidden" name="order" value="desc">');
                }
                else if(orderParam == 'asc'){
                    $(this).append('<span class="order asc"><i class="bi bi-sort-up-alt"></i></span>');
                    $(this).find('.order').val('asc');
                    form.append('<input type="hidden" name="column" value="'+columnValue+'">');
                    form.append('<input type="hidden" name="order" value="asc">');
                }
            }
        })
    }

    tableColumn.each(function () {
        let columnValue = $(this).find('.column').text();
        $(this).on('click', function () {
            console.log('columnValue : ' + columnValue);
            if(columnValue === '연번') {
                // 컬럼 제목을 '구직번호'로 변경
                $(this).find('.column').text('구직번호');

                // 해당 컬럼의 모든 데이터 셀을 구직번호로 변경
                let columnIndex = $(this).index();
                $('tbody tr').each(function() {
                    let $cell = $(this).find('td').eq(columnIndex);
                    let participantJobNo = $cell.find('.participantJobNo').val();
                    if (participantJobNo) {
                        $cell.contents().first().replaceWith(participantJobNo);
                    }
                });

                // columnValue 업데이트
                columnValue = '구직번호';
                return;
            }
            else if(columnValue === '구직번호') {
                // 컬럼 제목을 '연번'으로 변경
                $(this).find('.column').text('연번');

                // 해당 컬럼의 모든 데이터 셀을 연번으로 변경
                let columnIndex = $(this).index();
                $('tbody tr').each(function() {
                    let $cell = $(this).find('td').eq(columnIndex);
                    let rowNum = $cell.find('.rowNum').val();
                    if (rowNum) {
                        $cell.contents().first().replaceWith(rowNum);
                    }
                });

                // columnValue 업데이트
                columnValue = '연번';
                return;
            }
            sort($(this));
        });

        //tooltip 추가
        if (columnValue === '연번'){
            $(this).attr
            ({
                'data-bs-toggle': 'tooltip',
                'data-bs-placement': 'top',
                'data-bs-html': 'true',
                'title': '<strong>도움말</strong><br>연번 <-> 구직번호 전환 가능'
            }).tooltip();
        }
        else if(columnValue === 'IAP3개월차' || columnValue === 'IAP5개월차'){
            $(this).attr
            ({
                'data-bs-toggle': 'tooltip',
                'data-bs-placement': 'top',
                'data-bs-html': 'true',
                'title': '<strong>도움말</strong><br>'+columnValue+' 클릭시<br>오름차순,내림차순 정렬<br>초록:상담완료<br>빨강:상담미완료'
            }).tooltip();
        }
        else{
            $(this).attr
            ({
                'data-bs-toggle': 'tooltip',
                'data-bs-placement': 'top',
                'data-bs-html': 'true',
                'title': '<strong>도움말</strong><br>'+columnValue+' 클릭시<br>오름차순,내림차순 정렬'
            }).tooltip();
        }
    });

    function removeOrder(attribute){
        attribute.find('.order').remove();
        let returnValue;
        tableColumn.each(function () {
            let columnValue = $(this).find('.column').text();
            let findValue = attribute.find('.column').text();

            if(columnValue == findValue){
                returnValue = columnValue;
            }
            $(this).find('.order').remove();
        });
        return returnValue;
    }

    function sort(attribute){
        let orderValue = attribute.find('.order').val();
        let columnValue = removeOrder(attribute);
        let pageURL = '/participant.login?';
        // console.log('pageURL : ' + pageURL);

        if (branchManagementPageFlag == 'true') {
            pageURL = '/branchParitic.login?';
        }

        if (orderValue == 'desc') {
            location.replace(pageURL+sortHref('column=' + columnValue + '&order=asc'));
        }
        else{
            location.replace(pageURL+sortHref('column=' + columnValue + '&order=desc'));
        }
    }

    //검색 스크립트 시작
    //필터 변수
    const search_option = $('#search-Option');
    //검색어 변수
    const search = $('#search');
    //페이지 개수 변수
    const pageRows = $('#pageRows');
    //검색 버튼 변수
    const searchBtn = $('#searchBtn');
    //검색 입력 div
    const $searchTextDiv = $('#searchTextDiv');
    //select option 변수
    const $searchOptionParam = getUrlParameter('searchOption');
    //검색 param 값 변수
    const $searchParam = getUrlParameter('search');


    // 옵션 HTML 생성 함수
    function createOptionHtml(value, selectedValue) {
        const selected = selectedValue === value ? 'selected' : '';
        return '<option '+selected+' value="'+value+'">'+value+'</option>';
    }

    // 검색 셀렉트 박스 생성 함수
    function createProgressStageSelect(searchParam, options = PROGRESS_OPTIONS) {
        const optionsHtml = options
            .map(option => createOptionHtml(option, searchParam))
            .join('');

        return '<div class="w-auto">'+
            '<select id="search" name="search" class="form-control shadow-sm" aria-label="Default select">'+
            optionsHtml+
            '</select>'+
            '</div>';
    }

    //진행단계 옵션 생성 시작
    // 진행단계 옵션 목록
    const PROGRESS_OPTIONS = [
        'IAP 전', 'IAP 후', '미고보', '고보일반', '등록창업',
        '미등록창업', '미취업사후관리', '미취업사후종료',
        '유예', '취소', '이관', '중단'
    ];

    //진행단계 옵션
    let changeHtml = createProgressStageSelect($searchParam);

    //알선요청 옵션 생성 시작
    // 알선요청 옵션 목록
    const PROGRESS_OPTIONS_PLACEMENT = [
        '희망', '미희망'
    ];

    //알선요청 옵션
    let changeHtmlPlacement = createProgressStageSelect($searchParam, PROGRESS_OPTIONS_PLACEMENT);

    //검색 옵션 변경 함수
    function searchOptionHref(optionValue) {
        if(optionValue == '참여자'){
            console.log("search_option_value 실행중 [참여자]")
            $searchTextDiv.empty()
            $searchTextDiv.append('<input type="text" class="form-control shadow-sm" id="search" name="search" placeholder="참여자 성명을 입력해주세요." value="'+$searchParam+'" />')
        }
        else if(optionValue == '구직번호'){
            console.log("search_option_value 실행중 [구직번호]")
            $searchTextDiv.empty()
            $searchTextDiv.append('<input type="number" class="form-control shadow-sm" id="search" name="search" placeholder="구직번호를 입력해주세요." value="'+$searchParam+'" />')
        }
        else if(optionValue == '진행단계') {
            console.log("search_option_value 실행중 [진행단계]")
            $searchTextDiv.empty()
            $searchTextDiv.append(changeHtml)
        }
        else if(optionValue == '알선'){
            console.log("search_option_value 실행중 [알선요청]")
            $searchTextDiv.empty()
            $searchTextDiv.append(changeHtmlPlacement)
        }
        else if(optionValue == '전담자'){
            console.log("search_option_value 실행중 [전담자]")
            $searchTextDiv.empty()
            $searchTextDiv.append('<input type="text" class="form-control shadow-sm" id="search" name="search" placeholder="전담자 성명을 입력해주세요." value="'+$searchParam+'" />')
        }
    }

    //옵션 생성 끝
    searchOptionHref($searchOptionParam)

    //검색 옵션이 변경될 때 실행
    search_option.on('change', function () {
        let search_option_value = search_option.val()
        searchOptionHref(search_option_value)
    })

    function searchFunction() {
        $('#searchForm').submit();
    }

    searchBtn.on('click', function () {
        searchFunction();
    });

    search.on('keypress', function (e) {
        if (e.keyCode == 13) {
            searchFunction();
        }
    });
    //검색 스크립트 끝

    // 참여자 삭제 체크 시작
    let jobNo = [];
    let participantNames = [];
    const getCheckedValues = (items) => {
        jobNo = [];
        items.each(function () {
            const value = $(this).val();
            jobNo.push(value);
        });
    };

    $('#delete_btn').on('click', () => {
        const delete_items = $('.delete:checked');
        getCheckedValues(delete_items)

        let title = "선택된 참여자를 삭제합니다."
        let text = "삭제가 완료되면 복구가 불가능합니다."
        let confirmButtonText = "삭제"
        let cancelButtonText = "취소"

        alertConfirmQuestion(title, text, confirmButtonText, cancelButtonText).then((result) => {
            if (!result) {
                return;
            }
            $.ajax({
                url: 'participantDelete.login',
                type: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify({basicJobNos: jobNo}),
                success: function (data) {
                    let flag = false;
                    if (data.length > 0) {
                        alertDefaultInfo('삭제되지 않은 인원이 있습니다.' + '<br>' + data.length + ' 명 \n 구직번호 : ' + data + '<br>')
                            .then((result) => {
                                if (result) {
                                    flag = true
                                }
                            });
                    } else {
                        flag = true;
                    }
                    if (flag) location.reload();
                },
                error: function (data) {
                    alertDefaultError('오류발생', '삭제중 오류가 발생했습니다.');
                    console.log(data);
                }
            });
        });
    });
    // 참여자 삭제 체크 끝

    // IAP 수립 후 3,5개월 확인 시작
    let iapBefore = $('.iapBefore');
    iapBefore.on('click', function () {
        const number = getJobNumber($(this).closest('tr'));
        const $inputs = $(this).find('input');
        const participantIAP3MonthTD = $(this).closest('tr').find('.participantIAP3Month-td');
        const participantIAP5MonthTD = $(this).closest('tr').find('.participantIAP5Month-td');
        // const iapDate = $inputs.filter('.iapDate').val() || '';
        const iap3Month = $inputs.filter('.isIap3Month').val() || '';
        const iap5Month = $inputs.filter('.isIap5Month').val() || '';

        let is_iap3Month = (iap3Month==='true');
        let is_iap5Month = (iap5Month==='true');

        // // Date 객체로 변환
        // const baseDate = new Date(iapDate);

        //모달창 구직번호
        const jobNo = $('#iapBeforeJobNo');

        //모달창 IAP 3,5개월 이후 input
        const iap3MonthText = $('#iap3MonthText');
        const iap5MonthText = $('#iap5MonthText');

        //모달창 iap 3,5개월 여부 체크
        $('#iap3MonthModalCheckBox').prop('checked', is_iap3Month);
        $('#iap5MonthModalCheckBox').prop('checked', is_iap5Month);

        jobNo.val('');
        iap3MonthText.val('');
        iap5MonthText.val('');

        iap3MonthText.val(participantIAP3MonthTD.text());
        iap5MonthText.val(participantIAP5MonthTD.text());
        jobNo.val(number);

        /*//기본 날짜가 1900-01-01 일자 이후일때 실행
        if(iapDate > '1900-01-01'){
            // 3개월 이후 날짜 계산
            const after3Months = new Date(baseDate);
            after3Months.setMonth(baseDate.getMonth() + 3);

            // 5개월 이후 날짜 계산
            const after5Months = new Date(baseDate);
            after5Months.setMonth(baseDate.getMonth() + 5);

            // YYYY-MM-DD 형식으로 포맷팅
            const formatDate = (date) => {
                const year = date.getFullYear();
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const day = String(date.getDate()).padStart(2, '0');
                return year+'-'+month+'-'+day;
            };

            const iapDate3Months = formatDate(after3Months);
            const iapDate5Months = formatDate(after5Months);

            // console.log('기준일:', baseDate);
            // console.log('3개월 후:', iapDate3Months);
            // console.log('5개월 후:', iapDate5Months);

            iap3MonthText.val(iapDate3Months);
            iap5MonthText.val(iapDate5Months);
            jobNo.val(number);
        }*/
    })
    // IAP 수립 후 3,5개월 확인 끝

    // IAP 수립 후 3,5개월 td 색지정 시작
    /**
     * IAP 월차별 상담 여부 배경색 설정 및 클릭 이벤트 처리
     * @param {string} selector - 대상 요소 선택자
     * @param {string} monthType - 월차 타입 ('3' 또는 '5')
     */
    function setIapMonthBackground(selector, monthType) {
        const COLORS = {
            FALSE: 'rgba(255,58,58,0.55)',  // 빨간색 - 상담 미완료
            TRUE: 'rgba(0,255,0,0.51)'      // 초록색 - 상담 완료
        };

        const MESSAGES = {
            3: {
                title: "IAP 수립 후 3개월차 상담 여부를 변경하시겠습니까?",
                complete: "변경시 상담 완료 상태로 변환됩니다.",
                incomplete: "변경시 상담 미완료 상태로 변환됩니다.",
                priority: "5개월차 상담 여부를 먼저 변경해주세요."
            },
            5: {
                title: "IAP 수립 후 5개월차 상담 여부를 변경하시겠습니까?",
                complete: "변경시 상담 완료 상태로 변환됩니다.",
                incomplete: "변경시 상담 미완료 상태로 변환됩니다.",
                priority: "3개월차 상담 여부를 먼저 변경해주세요."
            }
        };

        // 초기 배경색 설정
        initializeBackgroundColors();

        // 클릭 이벤트 등록
        registerClickEvent();

        /**
         * 초기 배경색 설정
         */
        function initializeBackgroundColors() {
            $(selector).each(function () {
                const $this = $(this);

                if (shouldSkipElement($this)) return;

                const isIapMonth = getIapMonthValue($this, monthType);
                $this.css('background-color', isIapMonth ? COLORS.TRUE : COLORS.FALSE);
            });
        }

        /**
         * 클릭 이벤트 등록
         */
        function registerClickEvent() {
            $(selector).on('click', function () {
                const $this = $(this);

                if (shouldSkipElement($this)) return;

                handleIapMonthClick($this);
            });
        }

        /**
         * 요소를 건너뛸지 판단
         * @param {jQuery} $element - 대상 요소
         * @returns {boolean} 건너뛸지 여부
         */
        function shouldSkipElement($element) {
            const $participantProgressTD = $element.closest('tr').find('.participantProgress-td');
            const $isClose_span = $element.closest('tr').find('.isClose_span');
            // console.log('$participantProgressTD : [{}] ',$participantProgressTD.text().trim())
            // console.log('$isClose_span : [{}] ',$isClose_span.text().trim())
            return $participantProgressTD.text().trim() === 'IAP 전' || $isClose_span.text().trim() === '마감' ||
                $element.text().trim() === '';
        }

        /**
         * IAP 월차 값 조회
         * @param {jQuery} $element - 대상 요소
         * @param {string} type - 월차 타입
         * @returns {boolean} IAP 월차 값
         */
        function getIapMonthValue($element, type) {
            const $isIapMonth = $element.find('input').filter(`.isIap${type}Month`);
            return $isIapMonth.val() === 'true';
        }

        /**
         * IAP 월차 클릭 처리
         * @param {jQuery} $clickedElement - 클릭된 요소
         */
        function handleIapMonthClick($clickedElement) {
            const $row = $clickedElement.closest('tr');
            const $isIap3Month = $row.find('input').filter('.isIap3Month');
            const $isIap5Month = $row.find('input').filter('.isIap5Month');

            let isIap3MonthVal = $isIap3Month.val() === 'true';
            let isIap5MonthVal = $isIap5Month.val() === 'true';

            const currentValue = monthType === '3' ? isIap3MonthVal : isIap5MonthVal;
            const message = MESSAGES[monthType];
            const title = message.title;
            const subTitle = currentValue ? message.incomplete : message.complete;

            alertConfirmWarning(title, subTitle, "변경", "취소").then((result) => {
                if (!result) return;

                // 유효성 검사 및 값 변경
                const validationResult = validateAndUpdateValues();
                if (!validationResult.isValid) {
                    alertDefaultInfo(validationResult.message, "");
                    return;
                }

                // UI 업데이트
                $clickedElement.css('background-color',
                    validationResult.newValue ? COLORS.TRUE : COLORS.FALSE);

                // 서버 업데이트
                iapBeforeSaveAjax(
                    getJobNumber($row),
                    validationResult.iap3MonthVal,
                    validationResult.iap5MonthVal
                );

                // 폼 값 업데이트
                $isIap3Month.val(validationResult.iap3MonthVal);
                $isIap5Month.val(validationResult.iap5MonthVal);
            });

            /**
             * 유효성 검사 및 값 업데이트
             * @returns {Object} 검사 결과 및 새로운 값들
             */
            function validateAndUpdateValues() {
                if (monthType === '3') {
                    if (isIap5MonthVal) {
                        return { isValid: false, message: message.priority };
                    }

                    isIap3MonthVal = !isIap3MonthVal;
                    return {
                        isValid: true,
                        newValue: isIap3MonthVal,
                        iap3MonthVal: isIap3MonthVal,
                        iap5MonthVal: isIap5MonthVal
                    };
                } else if (monthType === '5') {
                    if (!isIap3MonthVal) {
                        return { isValid: false, message: message.priority };
                    }

                    isIap5MonthVal = !isIap5MonthVal;
                    return {
                        isValid: true,
                        newValue: isIap5MonthVal,
                        iap3MonthVal: isIap3MonthVal,
                        iap5MonthVal: isIap5MonthVal
                    };
                }
            }
        }
    }

// 함수 호출
    setIapMonthBackground('.participantIAP3Month-td', '3');
    setIapMonthBackground('.participantIAP5Month-td', '5');
    // IAP 수립 후 3,5개월 td 색지정 끝

    // IAP 수립 3,5 개월 여부 변경 함수 시작
    function iapBeforeSaveAjax(number, iap3Month, iap5Month) {
        $.ajax({
            url: 'iapBeforeSaveAjax.login',
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({
                participantJobNo: number,
                participantISIAP3Month: iap3Month,
                participantISIAP5Month: iap5Month,
            }),
            success: function (data) {
                console.log("Ajax Success : [" + data + "]");
                let title = '수정 완료'
                let message = '페이지를 새로고침합니다.'
                if (!data) {
                    title = '수정 실패'
                    message = 'iap 수립일이 없습니다.'
                    alertDefaultError(title, message).then((result) => {
                        console.log("result : [" + result + "]");
                    });
                    //return;
                }
                // alertDefaultSuccess(title, message).then((result) => {
                //     console.log("result : [" + result + "]");
                //     if (data) {
                //         location.reload();
                //     }
                // });
            },
            error: function (data) {
                console.log("Ajax Error : [" + data+ "]");
                alertDefaultError('수정 실패','오류 발생 : ' + data);
            }
        })
    }
    // IAP 수립 3,5 개월 여부 변경 끝

    // 마감 여부 시작
    const isCloses = $('.isClose_span');
    isCloses.on('click', function () {
        const $this = $(this);
        const number = getJobNumber($this.closest('tr'));
        console.log('number : [{}] ',number);
        const participantName = getParticipantName($this.closest('tr'));
        let title = participantName + " 참여자 선택"
        let text = "마감 처리 하시겠습니까?"
        let confirmButtonText = "마감"
        let $thisTextBoolean = $this.text() === "마감";
        if($thisTextBoolean){
            text = "진행중으로 변경 하시겠습니까?"
            confirmButtonText = "확인"
        }
        let cancelButtonText = "취소"
        let isClose = false;
        if ($this.hasClass('badge bg-success isClose_span')) {
            isClose = true;
        }

        if(branchManagementPageFlag == 'false'){
            // 진행단계 확인을 위한
            let participantProgressVal = $this.closest('tr').find('td').eq(6).text().trim();
            console.log('participantProgressVal : ['+participantProgressVal+']');

            console.log('isClose : ['+isClose+']');
            console.log('$thisText : ['+$thisTextBoolean+']');
            // 현재 텍스트가 마감이라면 변경이 가능하도록 설정
            if(!$thisTextBoolean){
                if(participantProgressVal === 'IAP 전' || participantProgressVal === 'IAP 후' || participantProgressVal === '유예'){
                    alertDefaultQuestion('진행단계를 확인해주세요.','IAP 전, IAP 후, 유예는 마감 처리 하실 수 없습니다.')
                    return;
                }
            }
        }

        alertConfirmQuestion(title, text, confirmButtonText, cancelButtonText).then((result) => {
            if (!result) {
                return;
            }
            $.ajax({
                url: 'ParticipantClose.login',
                type: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify({basicJobNo: number, basicClose: isClose}),
                success: function (data) {
                    if (data) {
                        $this.removeClass('badge bg-success isClose_span').addClass('badge bg-danger isClose_span')
                        $this.text("마감")
                    } else {
                        $this.removeClass('badge bg-danger isClose_span').addClass('badge bg-success isClose_span')
                        $this.text("진행중")
                    }
                },
                error: function (data) {
                    // 에러 처리
                }
            });
        })
    });
    // 마감 여부 끝

    // 최근상담일 기준 색 필터 시작
    let adventCons = $('.adventCons-td');
    adventCons.each(function () {
        let adventCons = $(this).text();
        if (adventCons.length == 0 || adventCons == null || adventCons == '') {
            // 값이 없는 경우
        } else if (adventCons > 21) {
            $(this).css('background-color', 'rgba(255,58,58,0.55)');
        } else if (adventCons > 15) {
            $(this).css('background-color', 'rgba(255,249,0,0.51)');
        } else {
            $(this).css('background-color', 'rgba(0,255,0,0.51)');
        }
    });
    // 최근상담일 기준 색 필터 끝

    // param 값 조회, &값으로 문자열 반환
    function searchMainHref(jobno) {
        let href=jobno;
        //url param data delete & order change
        let search = window.location.search.split('&');
        search[0] = search[0].replace('?', '');
        if (search[0] != null || search[0] != undefined) {
            search.forEach(function (item) {
                if (search.indexOf(item) >= 0) {
                    href += '&' + item
                }
            });
        }
        //마지막이 &라면 지우고 href 변수에 추가
        if (href.charAt(href.length - 1) == '&') {
            href = href + 'page=1';
        }
        return href;
    }

    function sortHref(jobno) {
        let href='';
        //url param data delete & order change
        let search = deleteParam(location.href,['column','order']).split('&');
        search[0] = search[0].replace('?', '');
        if (search[0] != null || search[0] != undefined) {
            search.forEach(function (item) {
                if (search.indexOf(item) >= 0) {
                    href += item+'&'
                }
            });
        }
        href = href + jobno;
        //마지막이 &라면 지우고 href 변수에 추가
        if (href.charAt(href.length - 1) == '&') {
            href = href.replace('&','');
        }
        return href;
    }

    function deleteParam(urlValue, deleteParams){
        //url에 있는 컬럼중 column, order param value 를 삭제하고 페이지 이동
        let url = new URL(urlValue);
        const searchParam = new URLSearchParams(url.search);

        //전달받은 삭제를 원하는 param data 값을 확인 후 삭제를 진행
        deleteParams.map((value) => {
            searchParam.delete(value);
        })
        return url.search = searchParam.toString();
    }

    // 전역 함수로 노출
    window.searchMainHref = searchMainHref;
    window.sortHref = sortHref;
    window.deleteParam = deleteParam;

    renderFilterTags();
});
