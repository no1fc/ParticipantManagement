/**
 * 날짜 형식을 확인하는 함수
 *
 * @param {string} dateString - 확인할 날짜 문자열
 * @param {string} format - 예상 형식 ('YYYY-MM-DD', 'YYYYMMDD', 'YYYY.MM.DD' 등)
 * @returns {boolean} 유효한 날짜 형식이면 true, 아니면 false
 */
function isValidDateFormat(dateString, format = 'YYYY-MM-DD') {
    if (!dateString || typeof dateString !== 'string') {
        return false;
    }

    let regex;
    let year, month, day;

    switch (format) {
        case 'YYYY-MM-DD':
            regex = /^(\d{4})-(\d{2})-(\d{2})$/;
            break;
        // case 'YYYYMMDD':
        //     regex = /^(\d{4})(\d{2})(\d{2})$/;
        //     break;
        // case 'YYYY.MM.DD':
        //     regex = /^(\d{4})\.(\d{2})\.(\d{2})$/;
        //     break;
        // case 'MM/DD/YYYY':
        //     regex = /^(\d{2})\/(\d{2})\/(\d{4})$/;
        //     break;
        default:
            return false;
    }

    const match = dateString.match(regex);
    if (!match) {
        return false;
    }

    // 형식에 따라 년, 월, 일 추출
    if (format === 'MM/DD/YYYY') {
        [, month, day, year] = match;
    } else {
        [, year, month, day] = match;
    }

    // 숫자로 변환
    year = parseInt(year, 10);
    month = parseInt(month, 10);
    day = parseInt(day, 10);

    // 기본 범위 확인
    if (year < 1900 || year > 2100) return false;
    if (month < 1 || month > 12) return false;
    if (day < 1 || day > 31) return false;

    // Date 객체로 실제 유효성 확인
    const date = new Date(year, month - 1, day);
    return date.getFullYear() === year &&
        date.getMonth() === month - 1 &&
        date.getDate() === day;
}

/**
 * 여러 형식을 자동으로 감지하여 날짜 유효성을 확인하는 함수
 *
 * @param {string} dateString - 확인할 날짜 문자열
 * @returns {boolean} 유효한 날짜이면 true, 아니면 false
 */
function isValidDate(dateString) {
    const formats = ['YYYY-MM-DD', 'YYYYMMDD', 'YYYY.MM.DD', 'MM/DD/YYYY'];

    for (const format of formats) {
        if (isValidDateFormat(dateString, format)) {
            return true;
        }
    }
    return false;
}

/**
 * 생년월일 형식 확인 (추가 검증 포함)
 *
 * @param {string} birthDate - 생년월일 문자열
 * @returns {object} {isValid: boolean, message: string}
 */
function validateBirthDate(birthDate) {
    if (!birthDate || typeof birthDate !== 'string') {
        console.log('birthDate', birthDate);
        return { isValid: false, message: '생년월일이 입력되지 않았습니다.' };
    }

    // 기본 형식 확인
    if (!isValidDate(birthDate)) {
        return { isValid: false, message: '올바른 날짜 형식이 아닙니다.' };
    }

    // 현재 날짜와 비교하여 미래 날짜 확인
    const today = new Date();
    const inputDate = new Date(birthDate.replace(/\./g, '-'));

    if (inputDate > today) {
        return { isValid: false, message: '미래 날짜는 입력할 수 없습니다.' };
    }

    // 너무 오래된 날짜 확인 (예: 120년 전)
    const minDate = new Date();
    minDate.setFullYear(today.getFullYear() - 120);

    if (inputDate < minDate) {
        return { isValid: false, message: '너무 오래된 날짜입니다.' };
    }

    return { isValid: true, message: '유효한 생년월일입니다.' };
}