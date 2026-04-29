let year = $('#year');
let today = new Date();
year.val(today.getFullYear());

// 순서 업데이트 함수
function updateOrder() {
    $('.user-item').each(function(index) {
        // 순서 번호 업데이트
        $(this).find('.position-input').text(index + 1);
        // 콘솔에 순서 변경 로그 출력
        // console.log(`User ID: ${userId}, New Position: ${newPosition}`);
    });
}

// tbody 순서 업데이트 함수
function updateTbodyOrder() {
    const $tbody = $('#status-tbody');
    const $rows = $tbody.find('tr').toArray();

    // user-item의 순서대로 tbody의 행 재정렬
    $('.user-item').each(function(index) {
        const userId = $(this).data('id');
        const $matchingRow = $rows.find(row => $(row).data('id') === userId);

        if ($matchingRow) {
            $tbody.append($matchingRow);
        }
    });
}

// 선택자 상수
const SELECTORS = {
    ROW: 'tr',
    TODAY_EMPLOYMENT: '.todayEmployment',
    WEEK_EMPLOYMENT: '.toWeekEmployment',
    MONTH_EMPLOYMENT: '.toMonthEmployment',
    YEAR_EMPLOYMENT: '.toYearEmployment',
    TODAY_PLACEMENT: '.todayPlacement',
    WEEK_PLACEMENT: '.toWeekPlacement',
    MONTH_PLACEMENT: '.toMonthPlacement',
    YEAR_PLACEMENT: '.toYearPlacement',
    TODAY_PERSONNEL_ONE_TYPE_CLASS: '.todayPersonnelOne',
    TODAY_PERSONNEL_TWO_TYPE_CLASS: '.todayPersonnelTwo',
    TODAY_PERSONNEL_ONE_TYPE: '#branchType1',
    TODAY_PERSONNEL_TWO_TYPE: '#branchType2'
};

const previousValues = {
    employment: new Map(),
    placement: new Map(),
    typeOne: new Map(),
    typeTwo: new Map(),
};

const parseNumericValue = (value) => parseFloat(value) || 0;

const FIELD_DEPENDENCIES = {
    today: {
        employment: [SELECTORS.WEEK_EMPLOYMENT, SELECTORS.MONTH_EMPLOYMENT, SELECTORS.YEAR_EMPLOYMENT],
        placement: [SELECTORS.WEEK_PLACEMENT, SELECTORS.MONTH_PLACEMENT, SELECTORS.YEAR_PLACEMENT],
        typeOne: [SELECTORS.TODAY_PERSONNEL_ONE_TYPE],
        typeTwo: [SELECTORS.TODAY_PERSONNEL_TWO_TYPE]
    },
    week: {
        employment: [SELECTORS.MONTH_EMPLOYMENT, SELECTORS.YEAR_EMPLOYMENT],
        placement: [SELECTORS.MONTH_PLACEMENT, SELECTORS.YEAR_PLACEMENT]
    },
    month: {
        employment: [SELECTORS.YEAR_EMPLOYMENT],
        placement: [SELECTORS.YEAR_PLACEMENT]
    }
};

const updateRowValues = (row, difference, type, period) => {
    const fields = FIELD_DEPENDENCIES[period]?.[type] || [];
    fields.forEach(selector => {
        // ID 선택자(#)인 경우 전역에서 찾고, 클래스 선택자(.)인 경우 행 내에서 찾음
        const input = selector.startsWith('#') ? $(selector) : $(row).find(selector);
        const currentValue = parseNumericValue(input.val());
        input.val(currentValue + difference);
    });
};

const handleValueChange = (element, type, period) => {
    const currentRow = $(element).closest(SELECTORS.ROW);
    const rowId = currentRow.data('id') || currentRow.index();
    const previousValue = previousValues[type].get(rowId) || 0;
    const newValue = parseNumericValue($(element).val());
    const difference = newValue - previousValue;

    updateRowValues(currentRow, difference, type, period);
    previousValues[type].set(rowId, newValue);
};

const EVENT_MAPPINGS = [
    { selector: SELECTORS.TODAY_EMPLOYMENT, type: 'employment', period: 'today' },
    { selector: SELECTORS.TODAY_PLACEMENT,  type: 'placement',  period: 'today' },
    { selector: SELECTORS.WEEK_EMPLOYMENT,  type: 'employment', period: 'week' },
    { selector: SELECTORS.WEEK_PLACEMENT,   type: 'placement',  period: 'week' },
    { selector: SELECTORS.MONTH_EMPLOYMENT, type: 'employment', period: 'month' },
    { selector: SELECTORS.MONTH_PLACEMENT,  type: 'placement',  period: 'month' },
    { selector: SELECTORS.TODAY_PERSONNEL_ONE_TYPE_CLASS, type: 'typeOne', period: 'today' },
    { selector: SELECTORS.TODAY_PERSONNEL_TWO_TYPE_CLASS, type: 'typeTwo', period: 'today' }
];

EVENT_MAPPINGS.forEach(({ selector, type, period }) => {
    $(document).on('change', selector, function() {
        handleValueChange(this, type, period);
    });
});

const getWeek = (date) => {
    const currentDate = new Date(date);
    const currentMonday = new Date(currentDate);
    const day = currentDate.getDay();
    currentMonday.setDate(currentDate.getDate() + (day === 0 ? -6 : 1 - day));

    const firstMonday = new Date(currentDate.getFullYear(), 0, 1);
    const firstDay = firstMonday.getDay();
    firstMonday.setDate(firstMonday.getDate() + (firstDay === 0 ? 1 : 9 - firstDay) - 7);

    const diffDays = Math.floor((currentMonday - firstMonday) / (1000 * 60 * 60 * 24));
    return Math.floor(diffDays / 7) + 1;
};

const isDateMatch = (lastDate, today, condition) => {
    const strategies = {
        year:  () => today.getFullYear() === lastDate.getFullYear(),
        month: () => today.getMonth() === lastDate.getMonth(),
        week:  () => getWeek(today) === getWeek(lastDate),
        day:   () => today.getDate() === lastDate.getDate()
    };
    return strategies[condition] ? strategies[condition]() : false;
};

const initializeValues = () => {
    const lastSavedDate = new Date($('#lastSavedDate').val());
    const today = new Date();
    const resetConfig = {
        year:  [SELECTORS.YEAR_EMPLOYMENT, SELECTORS.YEAR_PLACEMENT],
        month: [SELECTORS.MONTH_EMPLOYMENT, SELECTORS.MONTH_PLACEMENT],
        week:  [SELECTORS.WEEK_EMPLOYMENT, SELECTORS.WEEK_PLACEMENT],
        day:   [SELECTORS.TODAY_EMPLOYMENT, SELECTORS.TODAY_PLACEMENT, SELECTORS.TODAY_PERSONNEL_ONE_TYPE_CLASS,
            SELECTORS.TODAY_PERSONNEL_TWO_TYPE_CLASS, SELECTORS.TODAY_PERSONNEL_ONE_TYPE, SELECTORS.TODAY_PERSONNEL_TWO_TYPE]
    };

    Object.keys(resetConfig).forEach(period => {
        if (!isDateMatch(lastSavedDate, today, period)) {
            resetConfig[period].forEach(selector => $(selector).val(0));
        }
    });
    function leftPad(value) {
        if (value >= 10) {
            return value;
        }

        return `0${value}`;
    }

    // 일일보고 날짜 초기화(현재일)
    function toStringByFormatting(source, delimiter = '-') {
        const year = source.getFullYear();
        const month = leftPad(source.getMonth() + 1);
        const day = leftPad(source.getDate());

        return [year, month, day].join(delimiter);
    }

    $('#dailyReportDate').val(toStringByFormatting(today));
};

/* datepicker JS Start */
function default_datepicker(datepicker_on) {
    // 한국어 기본 설정 적용
    // $.datepicker.setDefaults($.datepicker.regional['ko']);
    // datepicker_on.attr('readonly', true);// 날짜만 선택하도록 읽기 전용으로 만들어줍니다.
    datepicker_on.attr('maxLength', 10);// 입력 가능 개수 10개 고정
    datepicker_on.datepicker({
        format: 'yyyy-mm-dd', //데이터 포맷 형식(yyyy : 년 mm : 월 dd : 일 )
        startDate: '-2y', //달력에서 선택 할 수 있는 가장 빠른 날짜. 이전으로는 선택 불가능 ( d : 일 m : 달 y : 년 w : 주)
        endDate: '+0d', //달력에서 선택 할 수 있는 가장 느린 날짜. 이후로 선택 불가 ( d : 일 m : 달 y : 년 w : 주)
        autoclose: true, //사용자가 날짜를 클릭하면 자동 캘린더가 닫히는 옵션
        calendarWeeks: false, //캘린더 옆에 몇 주차인지 보여주는 옵션 기본값 false 보여주려면 true
        clearBtn: false, //날짜 선택한 값 초기화 해주는 버튼 보여주는 옵션 기본값 false 보여주려면 true
        //datesDisabled: ['2019-06-24', '2019-06-26'], //선택 불가능한 일 설정 하는 배열 위에 있는 format 과 형식이 같아야함.
        //daysOfWeekDisabled: [0, 6], //선택 불가능한 요일 설정 0 : 일요일 ~ 6 : 토요일
        //daysOfWeekHighlighted: [3], //강조 되어야 하는 요일 설정
        disableTouchKeyboard: false, //모바일에서 플러그인 작동 여부 기본값 false 가 작동 true가 작동 안함.
        immediateUpdates: false, //사용자가 보는 화면으로 바로바로 날짜를 변경할지 여부 기본값 :false
        multidate: false, //여러 날짜 선택할 수 있게 하는 옵션 기본값 :false
        multidateSeparator: ',', //여러 날짜를 선택했을 때 사이에 나타나는 글짜 2019-05-01,2019-06-01
        templates: {
            leftArrow: '&laquo;',
            rightArrow: '&raquo;',
        }, //다음달 이전달로 넘어가는 화살표 모양 커스텀 마이징
        showWeekDays: true, // 위에 요일 보여주는 옵션 기본값 : true
        // title: "", //캘린더 상단에 보여주는 타이틀
        todayHighlight: true, //오늘 날짜에 하이라이팅 기능 기본값 :false
        toggleActive: true, //이미 선택된 날짜 선택하면 기본값 : false인경우 그대로 유지 true인 경우 날짜 삭제
        weekStart: 0, //달력 시작 요일 선택하는 것 기본값은 0인 일요일
        language: 'ko', //달력의 언어 선택, 그에 맞는 js로 교체해줘야한다.
    })
        .on('changeDate', function (e) {
            /* 이벤트의 종류 */
            //show : datePicker가 보이는 순간 호출
            //hide : datePicker가 숨겨지는 순간 호출
            //clearDate: clear 버튼 누르면 호출
            //changeDate : 사용자가 클릭해서 날짜가 변경되면 호출 (개인적으로 가장 많이 사용함)
            //changeMonth : 월이 변경되면 호출
            //changeYear : 년이 변경되는 호출
            //changeCentury : 한 세기가 변경되면 호출 ex) 20세기에서 21세기가 되는 순간

            console.log(e);
            // e.date를 찍어보면 Thu Jun 27 2019 00:00:00 GMT+0900 (한국 표준시) 위와 같은 형태로 보인다.
        });
}

/* datepicker JS End */


