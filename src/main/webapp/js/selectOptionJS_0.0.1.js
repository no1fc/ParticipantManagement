/**
 * @file 셀렉트 옵션 동적 변경 유틸리티 (취업유형 연동)
 * @version 0.0.1
 * @requires jQuery
 */
function selectOption(selectID, data){
    selectID.val(data);

    // 대분류 선택 시 중분류 옵션을 갱신하도록 change 이벤트를 트리거
    if (selectID && selectID.length && selectID.is('#jobCategoryLarge')) {
        selectID.trigger('change');
    }
}

function changeSelect(selectID, changeSelectID, selectValue){

    if(selectValue != null){
        empTypeChange(changeSelectID);
    }

    selectID.on("change", function () {
        changeSelectID.empty();
        empTypeChange(changeSelectID);
    });

    function empTypeChange(changeSelectID){
        let option = [];
        if(selectID.val() == "미고보/인력공급업"){
            option = [{text:"고보미가입취업자",value:"고보미가입취업자"},
                {text: "인력공급업",value: "인력공급업"},
                {text: "프리랜서",value: "프리랜서"},
                {text: "특수고용",value: "특수고용"}
            ]
        }
        else if(selectID.val() == "고보일반"){
            option = [{text:"본인",value:"본인"},
                {text: "알선",value: "알선"},
                {text: "외부 알선",value: "외부 알선"}
            ]
        }
        else if(selectID.val() == "등록창업" || selectID.val() == "미등록창업"){
            option = [{text:"창업",value:"창업"}
            ]
        }

        option.forEach((item) => {
            changeSelectID.append(new Option(item.text, item.value));
        })
    }
}