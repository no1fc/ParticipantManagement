$(document).ready(function () {
    // kakao 주소 api function 시작
    const btnCloseLayer = $("#btnCloseLayer");

// 주소 입력 필드 클릭 시 주소 검색 창 열기
    $(document).on("click",".editable-input#detailLocation", function () {
        getAddress($(this),$("#layer"));
    });

// 닫기 버튼 클릭 시 주소 검색 창 닫기
    btnCloseLayer.on("click", function () {
        closeDaumPostcode();
    });

    // 우편번호 찾기 화면을 넣을 element
    // const element_layer = ;

    function closeDaumPostcode() {
        // iframe을 넣은 element를 안보이게 한다.
        $("#layer").css('display', 'none');
    }

    function getAddress(addressBtn,element_layer){
        new daum.Postcode({
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
                addressBtn.val(addr + extraAddr);

                // 커서를 상세주소 필드로 이동한다.
                addressBtn.focus();

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
        initLayerPosition(element_layer);
    }

// 브라우저의 크기 변경에 따라 레이어를 가운데로 이동시키고자 하실때에는
// resize이벤트나, orientationchange이벤트를 이용하여 값이 변경될때마다 아래 함수를 실행 시켜 주시거나,
// 직접 element_layer의 top,left값을 수정해 주시면 됩니다.
    function initLayerPosition(element_layer){
        let width = 500; //우편번호서비스가 들어갈 element의 width
        let height = 600; //우편번호서비스가 들어갈 element의 height
        let borderWidth = 1; //샘플에서 사용하는 border의 두께

        // 위에서 선언한 값들을 실제 element에 넣는다.
        element_layer.css({
            'width': width + 'px',
            'height': height + 'px',
            'border': borderWidth + 'px solid',
            'left': (((window.innerWidth || document.documentElement.clientWidth) - width)/2 - borderWidth) + 'px',
            'top': (((window.innerHeight || document.documentElement.clientHeight) - height)/2 - borderWidth) + 'px'
        });
    }
// kakao 주소 api function 끝
})

