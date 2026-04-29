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