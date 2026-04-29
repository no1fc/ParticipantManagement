$(document).ready(function () {

    /* 기본 정보 */
    //참여자 성명
    const basicPartic = $("#basicPartic");
    //생년월일
    const basicDob = $("#basicDob");
    //주소
    const basicAddress = $("#basicAddress");
    //학교명
    const basicSchool = $("#basicSchool");
    //전공
    const basicSpecialty = $("#basicSpecialty");
    //학력
    const basicEducation = $("#basicEducation");

    /* 상담 정보 */
    //취업역량
    const counselJobSkill = $("#counselJobSkill");
    //진행단계
    const counselProgress = $("#counselProgress");
    //초기상담일
    const counselInItCons = $("#counselInItCons");
    //최근상담일
    const counselLastCons = $("#counselLastCons");
    //IAP 수립일
    const counselIAPDateVal = $("#counselIAPDate");
    //IAP 수립일 3개월차
    const counselIAP3Month = $("#counselIAP3Month");

    //IAP 수립일 5개월차
    const counselIAP5Month = $("#counselIAP5Month");
    //기간만료일
    let counselEXPDate = $("#counselEXPDate");
    //알선요청
    const counselPlacement = $("#counselPlacement");
    //알선 상세정보
    const jobPlacementTextArea = $("#jobPlacementTextArea");
    //알선 추천사
    const suggestionTextArea = $("#suggestionTextArea");
    //키워드 배열
    const keywordArray = [];
    // //직무 카테고리 소분류
    // const jobCategorySub = $("#jobCategorySub");

    /* 취업정보 */
    //취창업일
    const employmentStartDate = $("#employmentStartDate");
    //취창업처리일
    const employmentProcDate = $("#employmentProcDate");
    //퇴사일
    const employmentQuit = $("#employmentQuit");
    //취업유형
    const employmentEmpType = $("#employmentEmpType");
    //취업처
    const employmentLoyer = $("#employmentLoyer");
    //희망급여
    const counselSalWant = $("#counselSalWant");
    //임금
    const employmentSalary = $("#employmentSalary");
    //취업인센티브_구분
    const employmentIncentive = $("#employmentIncentive");

    //키워드 등록 개수 확인용 함수
    function keywordCountFunction(){
        //hiddenKeywordInput 이 0이라면 배열 제거
        keywordArray.splice(0,keywordArray.length);
        
        //알선 키워드 클릭시 개수 확인을 위해 추가.
        const hiddenKeywordInput = $(".hidden-keyword-input");

        hiddenKeywordInput.each(function () {
            keywordArray.push($(this).val());
        })
        //키워드 배열 개수 확인
        console.log("keywordArray.length : "+keywordArray.length);
        return keywordArray.length;
    }

    // form 전달 시작
    const btn_check = $("#btn_check") // 전송 버튼을 추가
    btn_check.on("click", function () {


        /* 기본 정보 */
        //참여자 성명
        const basicParticVal = basicPartic.val();
        //생년월일
        const basicDobVal = basicDob.val();
        //주소
        let basicAddressVal = basicAddress.val();
        //학교명
        const basicSchoolVal = basicSchool.val();
        //전공
        const basicSpecialtyVal = basicSpecialty.val();
        //학력
        const basicEducationVal = basicEducation.val();

        /* 상담 정보 */
        //취업역량
        let counselJobSkillVal = counselJobSkill.val();
        //진행단계
        let counselProgressVal = counselProgress.val();
        //초기상담일
        let counselInItConsVal = counselInItCons.val();
        //최근상담일
        let counselLastConsVal = counselLastCons.val();
        //희망급여
        const counselSalWantVal = counselSalWant.val();
        //IAP 수립일
        const counselIAPDateVal = counselIAPDate.val();
        //IAP 수립일 3개월차
        const counselIAP3MonthVal = counselIAP3Month.val();
        //IAP 수립일 5개월차
        const counselIAP5MonthVal = counselIAP5Month.val();
        //기간만료일
        let counselEXPDateVal = counselEXPDate.val();
        //알선요청
        let counselPlacementVal = counselPlacement.val();
        //알선 상세정보
        let jobPlacementTextAreaVal = jobPlacementTextArea.val();
        //키워드 개수
        const keywordCount = keywordCountFunction();
        //알선 추천사
        const suggestionTextAreaVal = suggestionTextArea.val();

        /* 취업정보 */
        //취창업일
        const employmentStartDateVal = employmentStartDate.val();
        //취창업처리일
        const employmentProcDateVal = employmentProcDate.val();
        //퇴사일
        const employmentQuitVal = employmentQuit.val();
        //취업유형
        const employmentEmpTypeVal = employmentEmpType.val();
        //취업처
        const employmentLoyerVal = employmentLoyer.val();
        //임금
        const employmentSalaryVal = employmentSalary.val();
        //취업인센티브_구분
        const employmentIncentiveVal = employmentIncentive.val();

        //flag 변수 생성
        //각 변수들이 비어 있다면 값이 없는 것으로 간주하여 form 태그 실행 함수에서 내보낸다.
        let flag = basicParticVal.length > 0;
        if(!flag){
            alertDefaultInfo("참여자 성명은 필수 입력 입니다.","참여자를 입력해주세요.");
            return;
        }
        else if(counselIAPDateVal.length > 0){
            //iap 전 상태에서 iap 수립일을 작성하고 등록 OR 저장하면 경고 출력
            //iap 수립일이 비어있지 않은 상태로
            // iap 3개월차, 5개월차 칸이 비어있다면 입력 요청 후 함수에서 내보낸다.
            if(!counselIAP3MonthVal.length > 0){
                alertDefaultInfo("IAP 3개월차가 비어있습니다.","IAP 3개월 이후 일자를 입력해주세요.");
                counselIAP3Month.focus();
                return;
            }
            else if(!counselIAP5MonthVal.length > 0){
                alertDefaultInfo("IAP 5개월차가 비어있습니다.","IAP 5개월 이후 일자를 입력해주세요.");
                counselIAP5Month.focus();
                return;
            }
            // iap 관련 일자가 모두 입력된 상태로 진행단계가 IAP 전으로 작성되어 있다면 IAP 후로 변경
            if(counselProgressVal == "IAP 전"){
                counselProgress.val("IAP 후")
            }

        }

        // 유효성 검사 상수 및 유틸 함수 도입
        const PROGRESS_REQUIRING_EXP_DATE = new Set([
            '미고보',
            '고보일반',
            '등록창업',
            '미등록창업',
            '미취업사후종료',
            '이관',
            '중단',
        ]);

        const KEYWORD_MIN = 1;
        const KEYWORD_MAX = 5;

        const isBlank = (v) => v == null || String(v).trim() === '';
        const progressRequiresExpDate = (progress) => PROGRESS_REQUIRING_EXP_DATE.has(progress);
        const isKeywordCountInvalid = (count) => count < KEYWORD_MIN || count > KEYWORD_MAX;

        // 기존 조건문 리팩터링
        if (progressRequiresExpDate(counselProgressVal) && isBlank(counselEXPDateVal)) {
            alertDefaultInfo(
                `현재 선택한 진행단계의 ${counselProgressVal}은/는 기간만료(예정)일은 필수로 입력되어야 합니다.`
            );
            return;
        } else if (counselPlacementVal === '희망') {
            // 다중 희망직무 목록에서 최소 1건 이상 등록 여부 확인
            const $wishItems = $("#wishJobListContainer .wish-job-item");
            const hasWishJob = $wishItems.length > 0 && $wishItems.toArray().some(function (el) {
                const $el = $(el);
                return !isBlank($el.find('.wish-large-input').val())
                    && !isBlank($el.find('.wish-mid-input').val())
                    && !isBlank($el.find('.wish-want').val());
            });

            // 필수 항목 묶음 체크 (알선요청 '희망'일 때)
            const requiredFields = [
                basicDobVal,              // 생년월일
                basicAddressVal,          // 주소
                basicSchoolVal,           // 학교명
                basicSpecialtyVal,        // 전공
                basicEducationVal,        // 학력
                counselSalWantVal,        // 희망급여
                jobPlacementTextAreaVal,  // 상세정보
                suggestionTextAreaVal,    // 추천사
            ];

            const hasBlankRequired = requiredFields.some(isBlank);
            const invalidKeywordRange = isKeywordCountInvalid(Number(keywordCount));

            if (hasBlankRequired || !hasWishJob || invalidKeywordRange) {
                alertDefaultInfo(
                    '알선요청 희망시 필수 항목을 모두 입력해주세요.',
                    '생년월일, 주소, 학교명, 전공, 희망직무(1개이상), 희망급여, 키워드(1개이상~5개이하), 상세정보, 추천사를 반드시 입력해주세요.'
                );
                return;
            }
        }


        // 초기상담일이 비어있는 상태라면 최근상담일이 초기상담일이 입력된다는 안내를 출력한다.
        if(!counselInItConsVal.length > 0){
            // let flag = false;
            // alertConfirmQuestion('초기상담일이 작성되지 않았습니다.','최근상담일을 초기상담일로 작성합니다.','수정','취소')
            //     .then(result => {
            //         if(result){
            //             counselInItCons.val(counselLastConsVal);
            //             flag = true;
            //         }
            //     })
            
            // 초기상담일 변경
            counselInItCons.val(counselLastConsVal);

            // if(!flag){
            //     return;
            // }
            // 기간만료(예정)일 수정
            counselEXPDateChangeFunction();
        }

        // 기간만료(예정)일 값이 없다면 초기상담일 기준으로 1년 이후로 변경
        if(!counselEXPDateVal.length > 0){
            // 기간만료(예정)일 수정
            counselEXPDateChangeFunction();
        }

        //취창업일이 비어있고 임금 OR 취업인센티브_구분이 비어있다면 함수에서 내보낸다.
        if(!employmentStartDateVal.length > 0){
            //임금이 작성되어 있거나
            flag = employmentSalaryVal.length > 0;
            //취업인센티브_구분이 선택되어 있다면
            flag = flag || employmentIncentiveVal.length > 0;

            flag = flag || employmentProcDateVal.length > 0;
            flag = flag || employmentQuitVal.length > 0;
            flag = flag || (employmentEmpTypeVal != null?employmentEmpTypeVal.length > 0:false);
            flag = flag || (employmentLoyerVal != null?employmentLoyerVal.length > 0:false);
            if(flag){
                alertDefaultInfo("취창업일을 입력해주세요.","");
                return;
            }
        }
        else {
            if(employmentEmpTypeVal === "창업" && (employmentSalaryVal < 0 || employmentSalaryVal > 1000)){
                alertDefaultInfo("임금을 정확히 입력해주세요","0~1000까지 입력부탁드립니다.");
                return;
            }
            else if(employmentEmpTypeVal !== "창업" && (employmentSalaryVal <= 0 || employmentSalaryVal > 1000)){
                alertDefaultInfo("임금을 정확히 입력해주세요","1~1000까지 입력부탁드립니다.");
                return;
            }
            //임금이 작성되어 있거나
            if (!employmentSalaryVal.length > 0){
                alertDefaultInfo("임금은 필수 입력입니다.","");
                return;
            }
            //취업인센티브_구분이 선택되어 있다면
            if (!employmentIncentiveVal.length > 0){
                alertDefaultInfo("취업인센티브_구분은 필수 입력입니다.","");
                return;
            }
        }

        // 1순위 희망직무를 hidden input에 동기화
        if (typeof syncPrimaryJobWish === 'function') {
            syncPrimaryJobWish();
        }

        const form = $("#participantsForm");
        form.submit();
    });
//  form 전달 끝

    // 기간만료(예정)일 수정 시작
    function counselEXPDateChangeFunction() {
        const counselInItConsVal = counselInItCons.val();

        if(counselInItConsVal.length === 0){
            counselEXPDate.val("");
            return;
        }

        const defaultDate = new Date(counselInItConsVal);
        let changeDate = new Date(defaultDate.setDate(defaultDate.getDate()-1));

        const year = changeDate.getFullYear()+1;
        const month = String(changeDate.getMonth() + 1).padStart(2, '0');
        const day = String(changeDate.getDate()).padStart(2, '0');
        counselEXPDate.val(`${year}-${month}-${day}`);
    }

    counselInItCons.on("change", function () {
        counselEXPDateChangeFunction();
    })
    // 기간만료(예정)일 수정 끝

// 사용자 편의성을 위해 목록 리스트 출력 시작
    //자격증 목록 리스트 출력
    $(document).on("focus", ".particcertifCertif", function () {
        recommendFunction($(this), "#basicParticcertiflist",xmlData("./XMLData/particcertifXMLData.xml", "particcertif name"));
    });

    //학교명 목록 리스트 출력
    recommendFunction("#basicSchool", "#basicSchoollist",xmlData("./XMLData/SchoolXMLData.xml", "school name"));
//  사용자 편의성을 위해 목록 리스트 출력 끝

//  IAP 수립일 기준 3,5개월 일자 지정 시작
    const counselIAPDate = $('#counselIAPDate');

    if(counselIAPDate.val().length > 0){

        //IAP 수립일 3개월 INPUT TAG 변수
        const counselIAP3Month = $('#counselIAP3Month')
        const counselIAP5Month = $('#counselIAP5Month')

        //활성화
        counselIAP3Month.attr("disabled", false);
        counselIAP5Month.attr("disabled", false);
    }

    counselIAPDate.on("change", function () {
        //IAP 수립일 값 가져온 변수
        const counselIAPDate = $('#counselIAPDate');
        const counselIAPDateVal = counselIAPDate.val();

        //IAP 수립일 3개월 INPUT TAG 변수
        const counselIAP3Month = $('#counselIAP3Month')
        const counselIAP5Month = $('#counselIAP5Month')

        console.log('변경 진행')
        if(counselIAPDateVal.length > 0){
            //사용자가 IAP 수립일을 입력하면
            //IAP3개월일자, IAP5개월일자 DISABLE를 제거
            counselIAP3Month.attr("disabled", false);
            counselIAP5Month.attr("disabled", false);

            //3개월, 5개월 자동 계산 함수 실행
            iapDateChangeFunction(counselIAPDateVal,counselIAP3Month,counselIAP5Month)
        }
        else{
            //IAP 수립일을 지우면 DISABLE를 추가
            counselIAP3Month.attr("disabled", true);
            counselIAP5Month.attr("disabled", true);
            //IAP3개월일자, IAP5개월일자를 제거
            counselIAP3Month.val("");
            counselIAP5Month.val("");
        }


    })

    //IAP 수립일을 받아오고
    //받아온 IAP 날짜에 m +3 / m +5 를 진행
    //3개월 후 일자와 5개월 후 일자를 구한 후 input tag에 입력
    function iapDateChangeFunction(counselIAPDateVal,counselIAP3Month,counselIAP5Month) {
        console.log('counselIAPDateVal : ['+counselIAPDateVal+']')

        // 날짜로 변환 시작
        const baseDate = new Date(counselIAPDateVal);
        //3개월
        const iap3MonthUpdate = new Date(baseDate);
        iap3MonthUpdate.setMonth(baseDate.getMonth() + 3);
        //5개월
        const iap5MonthUpdate = new Date(baseDate);
        iap5MonthUpdate.setMonth(baseDate.getMonth() + 5);
        // 날짜로 변환 끝

        //Date Format 변환 및 Div Data 추가 시작
        //3개월
        const iap3MonthUpdateVal = formatDate(iap3MonthUpdate);
        counselIAP3Month.val(iap3MonthUpdateVal);
        //5개월
        const iap5MonthUpdateVal = formatDate(iap5MonthUpdate);
        counselIAP5Month.val(iap5MonthUpdateVal);
        //Date Format 변환 및 Div Data 추가 끝
    }

    // YYYY-MM-DD 형식으로 포맷팅
    const formatDate = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return year+'-'+month+'-'+day;
    };
//  IAP 수립일 기준 3,5개월 일자 지정 끝

    // kakao 주소 api function 시작
    const btnCloseLayer = $("#btnCloseLayer");

    // 주소 입력 필드 클릭 시 주소 검색 창 열기
    basicAddress.on("click", function () {
        getAddress();
    });

    // 닫기 버튼 클릭 시 주소 검색 창 닫기
    btnCloseLayer.on("click", function () {
        closeDaumPostcode();
    });

    // 우편번호 찾기 화면을 넣을 element
    const element_layer = $("#layer");

    function closeDaumPostcode() {
        // iframe을 넣은 element를 안보이게 한다.
        element_layer.css('display', 'none');
    }

    function getAddress(){
        new kakao.Postcode({
            oncomplete: function(data) {
                // 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.
                var addr = ''; // 주소 변수
                var extraAddr = ''; // 참고항목 변수

                //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
                if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                    addr = data.roadAddress;
                } else { // 사용자가 지번 주소를 선택했을 경우(J)
                    addr = data.jibunAddress;
                }

                // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
                if(data.userSelectedType === 'R'){
                    // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                    // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                    if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                        extraAddr += data.bname;
                    }
                    // 건물명이 있고, 공동주택일 경우 추가한다.
                    if(data.buildingName !== '' && data.apartment === 'Y'){
                        extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                    }
                    // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                    if(extraAddr !== ''){
                        extraAddr = ' (' + extraAddr + ')';
                    }
                }

                // 우편번호와 주소 정보를 해당 필드에 넣는다.
                basicAddress.val(addr + extraAddr);

                // 커서를 상세주소 필드로 이동한다.
                basicAddress.focus();

                // iframe을 넣은 element를 안보이게 한다.
                element_layer.css('display', 'none');
            },
            width : '100%',
            height : '100%',
            maxSuggestItems : 5
        }).embed(element_layer[0]); // jQuery 객체를 DOM 객체로 변환

        // iframe을 넣은 element를 보이게 한다.
        element_layer.css('display', 'block');

        // iframe을 넣은 element의 위치를 화면의 가운데로 이동시킨다.
        // initLayerPosition();
    }

    // 브라우저의 크기 변경에 따라 레이어를 가운데로 이동시키고자 하실때에는
    // resize이벤트나, orientationchange이벤트를 이용하여 값이 변경될때마다 아래 함수를 실행 시켜 주시거나,
    // 직접 element_layer의 top,left값을 수정해 주시면 됩니다.
    // function initLayerPosition(){
    //     var width = 400; //우편번호서비스가 들어갈 element의 width
    //     var height = 500; //우편번호서비스가 들어갈 element의 height
    //     var borderWidth = 1; //샘플에서 사용하는 border의 두께
    //
    //     // 위에서 선언한 값들을 실제 element에 넣는다.
    //     element_layer.css({
    //         'width': width + 'px',
    //         'height': height + 'px',
    //         'border': borderWidth + 'px solid',
    //         // 'left': (((window.innerWidth || document.documentElement.clientWidth) - width)/2 - borderWidth) + 'px',
    //         // 'top': (((window.innerHeight || document.documentElement.clientHeight) - height)/2 - borderWidth) + 'px'
    //         'left': '0px',
    //         'top': '0px'
    //     });
    // }
    // kakao 주소 api function 끝

    //알선 상세 정보 입력 function 시작
    /*
    상단에 이미 선언해둔 변수
    //알선요청
    const counselPlacement = $("#counselPlacement");
    //알선 상세정보
    const jobPlacementTextArea = $("#jobPlacementTextArea");
    */

    //page 로딩시 알선 상세정보 입력란을 숨김
    const hiddenDiv = $("#hiddenDiv");

    //알선요청을 변경할 때마다 함수를 실행
    counselPlacement.on("change", function () {
        JobPlacementDetail(hiddenDiv);
    })
    //알선 상세 정보 입력 function 끝

})

//알선요청 함수 시작
function JobPlacementDetail($div){
    //알선요청
    const counselPlacement = $("#counselPlacement");

    let counselPlacementVal = counselPlacement.val();
    console.log('counselPlacementVal : ['+counselPlacementVal+']')
    counselPlacementVal = (counselPlacementVal !== '' && counselPlacementVal != null)? counselPlacementVal.trim():''

    const $inputs = $div.find("textarea, input:not([type='hidden'])");

    if (counselPlacementVal === '희망'){
        $inputs.attr("readonly", false);
        $div.show();
    }
    else{
        $inputs.attr("readonly", true);
        $div.hide();
    }
}
//알선요청 함수 끝
