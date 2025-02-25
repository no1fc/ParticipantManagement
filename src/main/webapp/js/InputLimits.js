
function inputLimits(id,min,max) {
    // 숫자 입력 필드에 이벤트 바인딩
    id.on("input", function() {

        // 현재 입력된 값 가져오기
        let value = parseInt($(this).val(), 10);

        // 값이 숫자가 아닌 경우 초기화
        if (isNaN(value)) {
            $(this).val('');
            return;
        }

        // 값이 최소값보다 작으면 최소값으로 설정
        if (value < min) {
            $(this).val(min);
            return;
        }

        // 값이 최대값보다 크면 최대값으로 설정
        if (value > max) {
            $(this).val(max);
            return;
        }
    });
}
