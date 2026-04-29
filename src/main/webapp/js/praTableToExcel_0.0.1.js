function exportToExcel(e) {
    if(e) e.preventDefault();

    const today = new Date();
    const date = today.getFullYear() + '-' + (today.getMonth() + 1) + '-' + today.getDate();

    const $table = $('#pra-csv-table');
    let fileName = "참여자 랜덤 배정";
    $table.table2excel({
        exclude: ".noExl", // excel로 내보내지 않을 class
        name: fileName, // excel 시트 이름
        filename: `${fileName}_${date}`, // excel 파일 이름
        fileext: ".xls" // excel 파일 형식
    });
}
