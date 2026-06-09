/**
 * @file 알선 목록 공통 유틸리티 (특수문자 제거 등)
 * @version 0.0.1
 * @requires jQuery
 */

function regexSpecialSymbols(val, input) {
    let changeData = val.replace(/[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9 ]/gm, "");
    // console.log(changeData);
    input.val(changeData);
}

// function regexNumber(val, input){
//     let changeData = val.replace(/[^0-9]/gm, "");
//     // console.log(changeData);
//     input.val(changeData);
// }