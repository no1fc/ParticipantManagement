
/**
 * CSV 파일 업로드 및 테이블 생성 스크립트
 *
 * 주요 기능:
 * - CSV 파일 유효성 검증
 * - 파일 읽기 및 파싱
 * - HTML 테이블 생성 및 표시
 * - 에러 처리 및 사용자 알림
 * - 상담사 랜덤 배정 알고리즘
 *
 * 사용 라이브러리:
 * - jQuery 3.7.1
 * - jQuery CSV 1.0.9
 *
 * 필요한 HTML 요소:
 * - .file-input: 파일 입력 요소
 * - .response-text-div: 상태 메시지 표시 영역
 * - #csvData: 테이블이 표시될 영역
 *
 * @author [남상도]
 * @version 1.0.0
 * @since 2025-07-18
 */

/**
 * CSV 파일을 읽어오는 메인 함수
 *
 * 동작 순서:
 * 1. 파일 유효성 검증 (타입, 확장자)
 * 2. FileReader API를 사용한 파일 읽기
 * 3. 읽기 완료 시 processCSV 함수 호출
 *
 * @param {Event} file - 파일 선택 이벤트 객체
 * @throws {Error} CSV 파일이 아닌 경우 에러 발생
 */

function readCsvFile(file) {
    //  이 함수는 "사용자가 파일 선택 버튼으로 올린 파일"을 읽는 시작점이다.
    // - FileReader: 브라우저가 로컬 파일 내용을 읽게 해주는 표준 도구
    // - file: 사용자가 파일을 선택할 때 브라우저가 전달해주는 이벤트 객체
    // - .response-text-div: 화면에 성공/실패 메시지를 보여주는 영역
    const fileReader = new FileReader(); // 실제 파일 내용을 읽는 역할
    const fileData = file.target.files[0]; // 사용자가 선택한 첫 번째 파일
    const fileType = fileData.type; // 파일의 MIME 타입 (예: text/csv)
    const fileName = fileData.name; // 파일명 문자열
    const responseTextDiv = $('.response-text-div'); // 메시지를 표시할 DOM 요소

    // 이전 메시지 초기화
    responseTextDiv.empty();

    // 파일 타입 검증: MIME 타입이 text/csv인지 확인
    //  CSV가 아닌 파일이면 여기서 즉시 중단한다.
    // MIME 타입이 text/csv가 아니면 오류 메시지를 보여주고 예외를 던진다.
    if (fileType !== 'text/csv') {
        responseTextDiv.show();
        responseTextDiv.append('<strong class="csv-danger">csv 파일만 업로드 가능합니다.</strong>');
        throw new Error('csv 파일만 업로드 가능합니다.');
    }

    // 파일명 로깅 (디버깅용)
    console.log('업로드된 파일명:', fileName);

    /**
     * TextDecoder 기반 디코딩:
     * 1) UTF-8로 디코딩 후 깨짐(U+FFFD) 여부 검사
     * 2) 깨짐이면 EUC-KR/CP949 계열로 재시도
     */
    const decodeCsvFromBuffer = function(buffer) {
        //  buffer는 "바이너리 형태의 파일 내용"이다.
        // 여기서는 텍스트(문자열)로 변환하는 과정을 수행한다.
        if (typeof TextDecoder !== 'undefined') {
            const utf8Decoder = new TextDecoder('utf-8');
            let csvData = utf8Decoder.decode(buffer);
            if (csvData && csvData.charCodeAt(0) === 0xFEFF) {
                csvData = csvData.slice(1);
            }

            //  U+FFFD(�)는 글자가 깨졌을 때 나타나는 특수 문자다.
            // 이 문자가 포함되면 인코딩이 맞지 않을 가능성이 높다.
            const isBroken = csvData.indexOf('\uFFFD') > -1;
            if (!isBroken) {
                return csvData;
            }

            // UTF-8이 깨졌다면 EUC-KR/CP949 계열로 재시도
            //  한국어 Windows 환경에서 흔히 쓰는 인코딩들을 차례대로 시도한다.
            const fallbackEncodings = ['euc-kr', 'ms949', 'cp949'];
            for (let i = 0; i < fallbackEncodings.length; i++) {
                try {
                    const decoder = new TextDecoder(fallbackEncodings[i]);
                    let fallbackData = decoder.decode(buffer);
                    if (fallbackData && fallbackData.charCodeAt(0) === 0xFEFF) {
                        fallbackData = fallbackData.slice(1);
                    }
                    return fallbackData;
                } catch (e) {
                    // 지원하지 않는 인코딩이면 다음 인코딩으로 넘어간다.
                }
            }
        }

        // TextDecoder가 없으면 기존 방식으로 UTF-8 시도 후 CP949 재시도
        return null;
    };

    fileReader.onload = function (e) {
        //  파일 읽기가 끝나면 이 콜백 함수가 실행된다.
        const buffer = e.target.result;
        const decoded = decodeCsvFromBuffer(buffer);
        if (decoded !== null) {
            const success = processCSV(decoded);
            if (!success) {
                responseTextDiv.show();
                responseTextDiv.append('<strong class="csv-danger">CSV 파싱에 실패했습니다.</strong>');
            }
            return;
        }

        //  TextDecoder가 없거나 실패한 경우,
        // FileReader.readAsText로 다시 읽어보는 "우회 방법"을 사용한다.
        const fallbackReader = new FileReader();
        fallbackReader.onload = function(ev) {
            let csvData = ev.target.result;
            if (csvData && csvData.charCodeAt(0) === 0xFEFF) {
                csvData = csvData.slice(1);
            }
            //  글자 깨짐 여부 확인
            const isBroken = csvData.indexOf('\uFFFD') > -1;
            if (isBroken) {
                const retryReader = new FileReader();
                retryReader.onload = function(retryEv) {
                    let retryData = retryEv.target.result;
                    if (retryData && retryData.charCodeAt(0) === 0xFEFF) {
                        retryData = retryData.slice(1);
                    }
                    processCSV(retryData);
                };
                //  UTF-8로 깨졌다면 CP949로 다시 시도한다.
                retryReader.readAsText(fileData, 'CP949');
                return;
            }
            processCSV(csvData);
        };
        fallbackReader.readAsText(fileData, 'UTF-8');
    };

    fileReader.onerror = function() {
        //  파일을 읽는 도중 브라우저에서 오류가 발생한 경우
        responseTextDiv.show();
        responseTextDiv.append('<strong class="csv-danger">파일 읽기 중 오류가 발생했습니다.</strong>');
        return false;
    };

    //  파일을 "바이너리(ArrayBuffer)"로 읽고,
    // 이후 TextDecoder로 사람 읽을 수 있는 문자열로 바꾼다.
    fileReader.readAsArrayBuffer(fileData);
    return true;
}

/**
 * CSV 텍스트 데이터를 파싱하여 HTML 테이블로 변환하는 함수
 *
 * 처리 과정:
 * 1. 데이터 정제 (줄바꿈 통일, 빈 줄 제거)
 * 2. jQuery CSV 라이브러리를 사용한 파싱
 * 3. HTML 테이블 생성
 * 4. DOM에 삽입
 *
 * @param {string} csvText - 파싱할 CSV 텍스트 데이터
 */
function processCSV(csvText) {
    //  이 함수는 CSV 문자열을 받아서 화면에 표(테이블)로 그려준다.
    // - csvText: 파일에서 읽어온 전체 텍스트
    // - 첫 줄은 "헤더(컬럼 이름)"라고 가정하고 검증한다.
    // - 나머지 줄은 실제 데이터로 처리한다.
    const responseTextDiv = $('.response-text-div');
    responseTextDiv.empty();
    const csv_column = $('.csv-column');

    try {
        // 1단계: CSV 파싱 전 기본적인 데이터 정제
        //  줄바꿈과 빈 줄을 정리해 파싱이 안정적으로 되게 만든다.
        const cleanedCsvText = cleanCsvData(csvText);

        // 2단계: jQuery CSV 라이브러리 옵션 설정 및 파싱
        const data = $.csv.toArrays(cleanedCsvText, {
            separator: ',',      // 구분자: 쉼표
            delimiter: '"',      // 문자열 구분자: 큰따옴표
            headers: false,      // 헤더 자동 처리 비활성화
            onParseValue: function(value, index) {
                //  각 칸의 앞뒤 공백을 제거해서 깔끔하게 만든다.
                return value.trim();
            }
        });

        // 3단계: 데이터 유효성 검증
        if (data.length === 0) {
            responseTextDiv.show();
            responseTextDiv.append('<strong class="csv-danger">CSV 파일이 비어있습니다.</strong>');
            return false;
        }

        // 4단계: HTML 테이블 생성 시작
        // 예상 헤더를 배열로 미리 수집 (한 번만 실행)
        //  화면에 정의된 "기대하는 헤더 목록"을 미리 읽어온다.
        // CSV 첫 줄과 정확히 같아야 정상으로 처리한다.
        const expectedHeaders = [];
        csv_column.each(function() {
            expectedHeaders.push($(this).text().trim());
        });

        // 헤더 검증 (O(n+m) → O(n))
        if (data.length > 0 && !validateCSVHeaders(data[0], expectedHeaders)) {
            responseTextDiv.show();
            responseTextDiv.append('<strong class="csv-danger">CSV 파일의 헤더가 다릅니다.</strong>');
            return false; // 헤더가 맞지 않으면 처리 중단
        }

        let table = '';
        //  여기서부터는 "실제 데이터 행"을 HTML 문자열로 만든다.
        for (let i = 1; i < data.length; i++) {
            const travelCounselorValue = (data[i][data[i].length - 1] || '').trim();
            const fixedAttr = travelCounselorValue ? ' data-fixed="true"' : '';
            table += '<tr class="csv-data-tr">';
            for (let j = 0; j < data[i].length; j++) {
                if (j === 0) {
                    //  첫 번째 칸은 "행 번호"를 넣는다.
                    table += '<td>'+i+'</td>'
                    // table += '<td class="random-td-input" ><input type="text" class="random-input" id="random-'+(i+1)+' readonly"></td>';
                    //  두 번째 칸은 상담사 ID를 넣는 칸이다.
                    // 출장상담사 값이 있으면 고정 배정(data-fixed="true")으로 표시한다.
                    table += '<td class="random-td-input"' + fixedAttr + '>' + escapeHtml(travelCounselorValue) + '</td>';
                }
                //  나머지 칸은 CSV 데이터 그대로 넣는다.
                table += '<td class="random-td">' + escapeHtml(data[i][j]) + '</td>';
            }
            table += '</tr>';
        }
        // table += '</tbody></table>';

        // 5단계: 생성된 테이블을 DOM에 삽입
        //  완성된 HTML 문자열을 실제 화면의 테이블 영역에 넣는다.
        $('#csv-data').html(table);

        // 6단계: 성공 메시지 표시
        responseTextDiv.show();
        responseTextDiv.append('<strong style="color: green;">CSV 파일이 성공적으로 로드되었습니다.</strong>');
        //  고정 배정을 반영한 상담사 통계 테이블도 같이 갱신한다.
        refreshCounselorTableWithFixed();
        return true;

    } catch (error) {
        // 에러 처리: 파싱 실패, 형식 오류 등
        console.error('CSV 파싱 에러:', error);
        responseTextDiv.show();
        responseTextDiv.append('<strong class="csv-danger">CSV 파일 형식이 올바르지 않습니다. 파일을 확인해주세요.</strong>');

        // 특정 에러 타입에 대한 상세 정보 표시
        if (error.message.includes('Illegal Quote')) {
            responseTextDiv.append('<br><small>오류 위치: ' + error.message + '</small>');
        }
        return false;
    }
}

// CSV 헤더 검증 함수 (효율적인 버전)
function validateCSVHeaders(csvHeaders, expectedHeaders) {
    //  CSV 첫 줄이 "정해진 컬럼 순서"와 완전히 같은지 확인한다.
    // 길이(컬럼 개수)와 순서가 하나라도 다르면 잘못된 파일로 본다.
    if (csvHeaders.length !== expectedHeaders.length) {
        console.log(`컬럼 개수가 맞지 않습니다. CSV: ${csvHeaders.length}, 예상: ${expectedHeaders.length}`);
        return false;

    }

    // 순서대로 비교 (O(n))
    for (let i = 0; i < csvHeaders.length; i++) {
        //  HTML에 넣기 전에 안전하게 이스케이프한 뒤 비교한다.
        const csvHeader = escapeHtml(csvHeaders[i]).trim();
        const expectedHeader = expectedHeaders[i].trim();

        if (csvHeader !== expectedHeader) {
            console.log(`컬럼이 맞지 않습니다. ${csvHeader} != ${expectedHeader} (위치: ${i})`);
            return false;
        }
    }

    return true;
}

/**
 * CSV 데이터를 정제하는 유틸리티 함수
 *
 * 수행 작업:
 * - 운영체제별 줄바꿈 문자 통일 (Windows: \r\n, Mac: \r, Unix: \n)
 * - 빈 줄 제거
 * - 데이터 무결성 보장
 *
 * @param {string} csvText - 정제할 CSV 텍스트
 * @returns {string} 정제된 CSV 텍스트
 */
function cleanCsvData(csvText) {
    //  운영체제마다 줄바꿈 문자가 다르기 때문에
    // 파싱 오류를 줄이기 위해 줄바꿈을 통일하고 빈 줄을 제거한다.
    let cleaned = csvText.replace(/\r\n/g, '\n'); // Windows 줄바꿈을 Unix 형태로 통일
    cleaned = cleaned.replace(/\r/g, '\n');       // Mac 줄바꿈을 Unix 형태로 통일

    // 비어있는 줄 제거 (공백만 있는 줄도 제거)
    cleaned = cleaned.split('\n').filter(line => line.trim() !== '').join('\n');

    return cleaned;
}

/**
 * HTML 특수 문자를 이스케이프하는 보안 함수
 *
 * XSS(Cross-Site Scripting) 공격 방지를 위해
 * HTML에서 특별한 의미를 가지는 문자들을 안전한 형태로 변환
 *
 * 변환 대상:
 * - & → &amp;   (앰퍼샌드)
 * - < → &lt;    (왼쪽 꺾쇠)
 * - > → &gt;    (오른쪽 꺾쇠)
 * - " → &quot;  (큰따옴표)
 * - ' → &#039;  (작은따옴표)
 *
 * @param {string} text - 이스케이프할 텍스트
 * @returns {string} 이스케이프된 안전한 텍스트
 */
function escapeHtml(text) {
    //  사용자가 입력한 텍스트가 HTML로 해석되지 않도록 안전하게 변환한다.
    // 예: "<"는 HTML 태그 시작으로 해석되므로 "&lt;"로 바꿔준다.
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}

// ================== 랜덤 배정 알고리즘 시작 ==================

/**
 * 참여자 데이터를 파싱하여 필요한 정보 추출
 *
 * @param {Array} participantRow - 참여자 한 줄 데이터
 * @returns {Object} 파싱된 참여자 정보
 */
function parseParticipantData(participantRow) {
    //  CSV 한 줄(배열)을 프로그램이 쓰기 편한 "객체"로 바꾼다.
    // 배열은 위치(인덱스)로 값을 찾지만, 객체는 이름으로 값을 찾는다.
    // 여기서는 CSV 열 순서를 아래처럼 고정으로 가정한다.
    // [번호, 상담사ID, 참여자 성명, 참여유형, 성별, 생년월일, 모집경로, 경력유무, 학력, 특정계층여부, 진행단계, 출장상담사]
    const [, , participantName, participationType, gender, birthDate, recruitmentPath, hasCareerRaw, education, specificClass, progressStage, travelCounselorRaw] = participantRow;

    //  경력유무가 숫자로 들어오는 경우가 있으므로 숫자로 정리한다.
    const careerYears = normalizeCareerValue(hasCareerRaw);

    return {
        name: participantName || '참여자',
        participationType: normalizeParticipationType(participationType),
        gender: normalizeGender(gender),
        ageGroup: calculateAgeGroup(birthDate),
        birthDate: birthDate,
        recruitmentPath: recruitmentPath || '센터배정',
        hasCareer: careerYears > 0,
        careerYears: careerYears,
        education: normalizeEducation(education),
        specificClass: normalizeSpecificClass(specificClass || 'X'),
        progressStage: progressStage || 'IAP 전',
        travelCounselor: normalizeTravelCounselor(travelCounselorRaw)
    };
}

/**
 * 연령대 계산 (청년/중장년 입력 또는 생년월일 입력 대응)
 *
 * @param {string} value - 연령대 또는 생년월일
 * @returns {string} 'youth' 또는 'middleAged'
 */
function calculateAgeGroup(value) {
    //  입력값이 "청년/중장년" 글자이든, 생년월일이든
    // 그에 맞춰 연령대를 계산해서 내부 코드로 돌려준다.
    if (!value) return 'youth'; // 기본값

    const trimmedValue = value.trim();
    if (trimmedValue.includes('청년') || trimmedValue.toLowerCase() === 'youth') {
        return 'youth';
    }
    if (trimmedValue.includes('중장년') || trimmedValue.toLowerCase() === 'middleaged') {
        return 'middleAged';
    }

    //  YYYY-MM-DD 형식일 때만 실제 나이를 계산한다.
    if (/^\d{4}-\d{2}-\d{2}$/.test(trimmedValue)) {
        const birth = new Date(trimmedValue);
        const today = new Date();
        let age = today.getFullYear() - birth.getFullYear();

        const monthDiff = today.getMonth() - birth.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
            age--;
        }

        return age <= 34 ? 'youth' : 'middleAged';
    }

    return 'youth';
}

function normalizeParticipationType(value) {
    //  참여유형을 "type1/type2" 같은 내부 코드로 통일한다.
    const trimmedValue = (value || '').trim();
    if (trimmedValue.includes('1')) return 'type1';
    if (trimmedValue.includes('2')) return 'type2';
    return 'type1';
}

function normalizeGender(value) {
    //  성별 표기를 내부 코드(man/woman)로 통일한다.
    const trimmedValue = (value || '').trim();
    if (trimmedValue === '남' || trimmedValue.toLowerCase() === 'male') return 'man';
    if (trimmedValue === '여' || trimmedValue.toLowerCase() === 'female') return 'woman';
    return 'man';
}

function normalizeSpecificClass(value) {
    //  특정계층 여부를 내부 코드(normal/specialGroup)로 통일한다.
    const trimmedValue = (value || '').trim().toLowerCase();
    if (trimmedValue === 'o' || trimmedValue === 'y' || trimmedValue === 'yes' || trimmedValue === '해당') {
        return 'specialGroup';
    }
    return 'normal';
}

function normalizeEducation(value) {
    // [초보 설명] 학력 표기를 내부 코드(college/high)로 통일한다.
    // - high: 고졸, 검정고시
    // - college: 초대졸, 대졸, 대학원
    const trimmedValue = (value || '').trim();
    if (trimmedValue === '고졸' || trimmedValue === '검정고시') return 'high';
    if (trimmedValue === '초대졸' || trimmedValue === '대졸' || trimmedValue === '대학원') return 'college';
    return '';
}

function normalizeBooleanFlag(value) {
    //  사람 친화적인 표기(O/X, Y/N, 예/아니오)를
    // 자바스크립트의 true/false로 변환한다.
    const trimmedValue = (value || '').trim().toLowerCase();
    if (trimmedValue === 'o' || trimmedValue === 'y' || trimmedValue === 'yes' || trimmedValue === '해당' || trimmedValue === '있음') {
        return true;
    }
    if (trimmedValue === 'x' || trimmedValue === 'n' || trimmedValue === 'no' || trimmedValue === '해당 없음' || trimmedValue === '없음') {
        return false;
    }
    return false;
}

function normalizeCareerValue(value) {
    //  경력 값이 숫자로 변환 가능한지 확인하고, 아니면 0으로 처리한다.
    const trimmedValue = (value || '').trim();
    const parsed = parseInt(trimmedValue, 10);
    if (Number.isFinite(parsed) && parsed >= 0) {
        return parsed;
    }
    return 0;
}

function normalizeTravelCounselor(value) {
    //  출장상담사 입력이 비어있으면 null로 바꿔서
    // "값 없음"을 명확히 구분한다.
    const trimmedValue = (value || '').trim();
    if (!trimmedValue) {
        return null;
    }

    return trimmedValue;
}

/**
 * 참여자에게 최적의 상담사를 찾는 함수
 *
 * @param {Object} counselors - 상담사 데이터 객체
 * @param {Object} client - 참여자 정보
 * @returns {string|null} 선택된 상담사 ID 또는 null
 */
function findOptimalCounselorForParticipant(counselors, client, debugCollector, options) {
    // 한 참여자를 누구에게 배정할지 결정하는 "중앙 로직"이다.
    // 흐름: 1) 고정 상담사가 있으면 먼저 배정 시도
    //       2) 조건을 만족하는 후보들을 모음
    //       3) 강제로 배정해야 하는 그룹이 있으면 그쪽 우선
    //       4) 마지막으로 점수가 가장 높은 상담사를 선택
    const assignmentOptions = options || {};
    if (client.travelCounselor) {
        const preferred = counselors[client.travelCounselor];
        if (preferred && (preferred.total || 0) < (preferred.max || Number.MAX_SAFE_INTEGER)) {
            if (debugCollector) {
                debugCollector.selectionType = 'fixed';
                debugCollector.selectedId = client.travelCounselor;
                debugCollector.candidateIds = [];
                debugCollector.scores = [];
                debugCollector.selectionReason = '고정 배정';
            }
            return client.travelCounselor;
        }
    }

    let candidateAll = buildCandidatePool(counselors);
    if (candidateAll.length === 0 && assignmentOptions.ignoreLimits === true) {
        // [초보 설명] "점수만 반영" 모드에서는 한도 조건을 무시하고 후보를 만든다.
        candidateAll = buildCandidatePoolIgnoringLimits(counselors);
    }
    if (candidateAll.length === 0) {
        if (debugCollector) {
            debugCollector.selectionType = 'unassigned';
            debugCollector.selectedId = '';
            debugCollector.candidateIds = [];
            debugCollector.scores = [];
            debugCollector.selectionReason = '후보 없음';
        }
        return null;
    }

    if (debugCollector) {
        debugCollector.candidateIds = candidateAll.slice();
        debugCollector.scores = buildScoreEntries(counselors, candidateAll, client);
    }

    const forcedPool = buildForcedPool(counselors, candidateAll);
    if (forcedPool.length > 0) {
        //  강제 배정 풀은 "특정 그룹이 너무 불균형할 때" 사용된다.
        if (debugCollector) {
            debugCollector.selectionType = 'forced';
            debugCollector.forcedPool = forcedPool.slice();
        }
        // [초보 설명] 강제 풀 안에서도 점수가 높은 상담사를 우선 선택한다.
        // (점수가 같으면 랜덤)
        const result = pickHighestScoreCounselorDetailed(counselors, forcedPool, client, assignmentOptions);
        if (debugCollector) {
            debugCollector.selectedId = result.selectedId;
            debugCollector.selectionReason = '강제 풀 내 최고점수';
            debugCollector.topCandidates = result.topCandidates;
            debugCollector.finalCandidates = result.finalCandidates;
        }
        return result.selectedId;
    }

    const result = pickHighestScoreCounselorDetailed(counselors, candidateAll, client, assignmentOptions);
    if (debugCollector) {
        debugCollector.selectionType = 'random';
        debugCollector.selectedId = result.selectedId;
        debugCollector.selectionReason = assignmentOptions.ignoreLimits ? '한도무시-최고점수' : '최고점수';
        debugCollector.topCandidates = result.topCandidates;
        debugCollector.finalCandidates = result.finalCandidates;
    }
    return result.selectedId;
}

function buildCandidatePool(counselors) {
    //  배정 후보 목록을 만든다.
    // 조건:
    // - 최대 배정 가능 인원을 넘지 않았는가?
    // - 상담사가 현재 배정 가능 상태인가?
    // - 일/주/2주/월 제한을 넘지 않았는가?
    const config = getScoringConfig();
    const candidates = [];
    Object.keys(counselors).forEach(counselorId => {
        const counselor = counselors[counselorId];
        const maxCapacity = counselor.max || Number.MAX_SAFE_INTEGER;
        if ((counselor.total || 0) >= maxCapacity) {
            return;
        }
        if (!isCounselorAvailable(counselor)) {
            return;
        }

        if (!withinDailyLimit(counselor, config)) {
            return;
        }

        if (!withinRollingLimits(counselor, config)) {
            return;
        }

        candidates.push(counselorId);
    });

    return candidates;
}

function buildCandidatePoolIgnoringLimits(counselors) {
    //  한도 제한을 무시하고 "정원/가용성"만 보고 후보를 만든다.
    const candidates = [];
    Object.keys(counselors).forEach(counselorId => {
        const counselor = counselors[counselorId];
        const maxCapacity = counselor.max || Number.MAX_SAFE_INTEGER;
        if ((counselor.total || 0) >= maxCapacity) {
            return;
        }
        if (!isCounselorAvailable(counselor)) {
            return;
        }
        candidates.push(counselorId);
    });
    return candidates;
}

function isCounselorAvailable(counselor) {
    //  상담사 상태가 하나라도 비활성(false)이면 배정하지 않는다.
    if (counselor.isAvailable === false) {
        return false;
    }
    if (counselor.available === false) {
        return false;
    }
    if (counselor.onDuty === false) {
        return false;
    }
    return true;
}

function withinDailyLimit(counselor, config) {
    //  하루 배정 제한을 넘지 않았는지 확인한다.
    const assigned1d = getAssignedDay(counselor);
    return assigned1d < config.dailyLimit;
}

function withinRollingLimits(counselor, config) {
    //  근속 그룹별로 주/2주/월 배정 제한을 확인한다.
    const tenureGroup = getTenureGroup(counselor);
    const limits = getTenureLimits(tenureGroup, config);

    const assigned7d = getAssignedWeek(counselor);
    const assigned14d = getAssignedTwoWeek(counselor);
    const assigned30d = getAssignedMonth(counselor);

    return assigned7d < limits.week && assigned14d < limits.biweek && assigned30d < limits.month;
}

function getTenureGroup(counselor) {
    //  입사일을 기준으로 근속 기간을 계산해 G1/G2/G3 그룹을 만든다.
    // - 입사일이 없으면 기존 데이터나 기본값(G2)을 사용한다.
    const hireDateRaw = counselor.employmentDate || counselor.hireDate || counselor.hire_date || counselor.joinDate || counselor.joinedAt;
    const hireDate = parseHireDate(hireDateRaw);
    if (hireDate) {
        const months = calculateTenureMonths(hireDate, new Date());
        if (months <= 3) {
            return 'G1';
        }
        if (months <= 11) {
            return 'G2';
        }
        return 'G3';
    }

    return counselor.tenureGroup || counselor.tenure_group || counselor.tenure || 'G2';
}

function getTenureLimits(group, config) {
    //  그룹별 제한값이 없으면 기본값(G2)을 사용한다.
    return config.tenureLimits[group] || config.tenureLimits.G2;
}

function shouldResetAllocationLimits(counselors) {
    //  모든 상담사가 동시에 한도에 걸렸을 때만 "카운트 초기화"를 허용한다.
    const config = getScoringConfig();
    const ids = Object.keys(counselors);
    let hasEligible = false;

    for (let i = 0; i < ids.length; i++) {
        const counselor = counselors[ids[i]];
        const maxCapacity = counselor.max || Number.MAX_SAFE_INTEGER;
        if ((counselor.total || 0) >= maxCapacity) {
            continue;
        }
        if (!isCounselorAvailable(counselor)) {
            continue;
        }
        hasEligible = true;
        if (!hasReachedAllLimits(counselor, config)) {
            return false;
        }
    }

    return hasEligible;
}

function hasReachedAllLimits(counselor, config) {
    //  해당 상담사가 하루/주/2주/월 모든 한도에 도달했는지 확인한다.
    const group = getTenureGroup(counselor);
    const limits = getTenureLimits(group, config);
    return getAssignedDay(counselor) >= config.dailyLimit
        && getAssignedWeek(counselor) >= limits.week
        && getAssignedTwoWeek(counselor) >= limits.biweek
        && getAssignedMonth(counselor) >= limits.month;
}

function resetAllocationLimits(counselors) {
    //  배정 카운트를 0으로 리셋해서 다시 배정이 가능하게 한다.
    Object.keys(counselors).forEach(id => {
        const counselor = counselors[id];
        counselor.assignmentAllocationDay = 0;
        counselor.assignmentAllocationWeek = 0;
        counselor.assignmentAllocationTwoWeek = 0;
        counselor.assignmentMonth = 0;
        counselor.assigned_1d = 0;
        counselor.assigned_7d = 0;
        counselor.assigned_14d = 0;
        counselor.assigned_30d = 0;
    });
}

function parseHireDate(raw) {
    //  "YYYY-MM-DD" 문자열을 Date 객체로 변환한다.
    // 형식이 맞지 않으면 null을 돌려준다.
    if (!raw || typeof raw !== 'string') {
        return null;
    }
    const trimmed = raw.trim();
    if (!/^\d{4}-\d{2}-\d{2}$/.test(trimmed)) {
        return null;
    }
    const [year, month, day] = trimmed.split('-').map(Number);
    const date = new Date(year, month - 1, day);
    if (Number.isNaN(date.getTime())) {
        return null;
    }
    return date;
}

function calculateTenureMonths(startDate, endDate) {
    //  입사일부터 기준일까지의 "완전한 개월 수"를 계산한다.
    let months = (endDate.getFullYear() - startDate.getFullYear()) * 12;
    months += endDate.getMonth() - startDate.getMonth();
    if (endDate.getDate() < startDate.getDate()) {
        months -= 1;
    }
    return Math.max(0, months);
}

function normalizeAllocationValue() {
    //  여러 후보 필드 중 "유효한 숫자"를 찾아 반환한다.
    // 값이 없거나 숫자가 아니면 0으로 처리한다.
    for (let i = 0; i < arguments.length; i++) {
        const value = arguments[i];
        if (value === 0) {
            return 0;
        }
        if (value !== null && value !== undefined && value !== '') {
            const parsed = Number(value);
            if (Number.isFinite(parsed)) {
                return parsed;
            }
        }
    }
    return 0;
}

function getAssignedDay(counselor) {
    //  상담사의 "하루 배정 수"를 여러 필드에서 찾아온다.
    return normalizeAllocationValue(
        counselor.assignmentAllocationDay,
        counselor.assigned_1d,
        counselor.assigned1d
    );
}

function getAssignedWeek(counselor) {
    //  상담사의 "7일 배정 수"를 여러 필드에서 찾아온다.
    return normalizeAllocationValue(
        counselor.assignmentAllocationWeek,
        counselor.assigned_7d,
        counselor.assigned7d
    );
}

function getAssignedTwoWeek(counselor) {
    //  상담사의 "14일 배정 수"를 여러 필드에서 찾아온다.
    return normalizeAllocationValue(
        counselor.assignmentAllocationTwoWeek,
        counselor.assigned_14d,
        counselor.assigned14d
    );
}

function getAssignedMonth(counselor) {
    //  상담사의 "30일 배정 수"를 여러 필드에서 찾아온다.
    return normalizeAllocationValue(
        counselor.assignmentAllocationMonth,
        counselor.assigned_30d,
        counselor.assigned30d
    );
}

function buildForcedPool(counselors, candidateIds) {
    // [초보 설명] 그룹 구분 없이 전체 후보를 대상으로 격차를 계산한다.
    // 배정 격차가 기준 이상이면, 가장 적게 배정된 사람들만 강제 후보로 만든다.
    const config = getScoringConfig();
    if (!candidateIds || candidateIds.length === 0) {
        return [];
    }

    const counts = candidateIds.map(id => getActiveCaseCount(counselors[id]));
    const gap = Math.max(...counts) - Math.min(...counts);
    if (gap < config.gapThreshold) {
        return [];
    }

    const minActive = Math.min(...counts);
    return candidateIds.filter(id => getActiveCaseCount(counselors[id]) === minActive);
}

function pickForcedCounselor(counselors, forcedPool) {
    //  강제 후보 풀 안에서 우선순위를 정해 1명을 선택한다.
    // 기준: 7일 배정 수 → 연속 배정 수 → 무작위
    const sorted = [...forcedPool].sort((a, b) => {
        const a7d = getAssignedWeek(counselors[a]);
        const b7d = getAssignedWeek(counselors[b]);
        if (a7d !== b7d) {
            return a7d - b7d;
        }

        const aStreak = counselors[a].streak_count || counselors[a].streakCount || 0;
        const bStreak = counselors[b].streak_count || counselors[b].streakCount || 0;
        if (aStreak !== bStreak) {
            return aStreak - bStreak;
        }

        return Math.random() - 0.5;
    });

    return sorted[0];
}

function pickHighestScoreCounselor(counselors, candidateIds, client, options) {
    // [초보 설명] 후보 전체를 "점수"로 정렬한 뒤 가장 높은 사람을 고른다.
    // - 점수가 높을수록 배정 우선
    // - 점수가 같으면 기간별 한도를 만족하는 후보 중 랜덤으로 선택
    const result = pickHighestScoreCounselorDetailed(counselors, candidateIds, client, options);
    return result.selectedId;
}

function pickHighestScoreCounselorDetailed(counselors, candidateIds, client, options) {
    // [초보 설명] 후보 전체를 "점수"로 정렬한 뒤 가장 높은 사람을 고른다.
    // 상세 정보를 포함하여 반환한다.
    const config = getScoringConfig();
    const overallAverages = buildOverallAverages(counselors, candidateIds);

    const scored = candidateIds.map(id => {
        const counselor = counselors[id];
        const costs = calculateCosts(counselor, client, overallAverages, config);
        const score = convertCostToScore(costs.total);
        const totalAssigned = counselor.total || 0;
        const withinLimit = withinDailyLimit(counselor, config) && withinRollingLimits(counselor, config);
        return { id, counselor, costs, score, totalAssigned, withinLimit };
    });

    // [초보 설명] 점수 상위 4명을 우선 후보로 제한한다.
    const sortedByScore = [...scored].sort((a, b) => b.score - a.score);
    const topScorers = sortedByScore.slice(0, Math.min(4, sortedByScore.length));

    // [초보 설명] 상위 후보 중 기간별 한도를 만족하는 사람만 우선 고려한다.
    // 단, "점수만 반영 배정" 모드에서는 한도를 무시한다.
    let pool = topScorers;
    if (!options.ignoreLimits) {
        const withinLimits = topScorers.filter(item => item.withinLimit);
        pool = withinLimits.length > 0 ? withinLimits : topScorers;
    }

    // [초보 설명] 전체 배정 인원이 가장 적은 상담사를 우선으로 좁힌 뒤 랜덤 선택한다.
    const minTotalAssigned = Math.min(...pool.map(item => item.totalAssigned));
    const leastAssignedPool = pool.filter(item => item.totalAssigned === minTotalAssigned);
    const pickIndex = Math.floor(Math.random() * leastAssignedPool.length);

    return {
        selectedId: leastAssignedPool[pickIndex].id,
        topCandidates: topScorers.map(item => ({
            id: item.id,
            score: item.score,
            totalAssigned: item.totalAssigned,
            withinLimit: item.withinLimit
        })),
        finalCandidates: leastAssignedPool.map(item => ({
            id: item.id,
            score: item.score,
            totalAssigned: item.totalAssigned
        }))
    };
}

function buildGroupAverages(counselors, candidateIds) {
    //  그룹별 평균(활성 케이스 수, 연간 배정 수)을 계산한다.
    const groups = {};
    candidateIds.forEach(id => {
        const group = getTenureGroup(counselors[id]);
        if (!groups[group]) {
            groups[group] = { activeSum: 0, activeCount: 0, ytdSum: 0, ytdCount: 0 };
        }

        groups[group].activeSum += getActiveCaseCount(counselors[id]);
        groups[group].activeCount += 1;
        groups[group].ytdSum += getAssignedYtd(counselors[id]);
        groups[group].ytdCount += 1;
    });

    const averages = {};
    Object.keys(groups).forEach(group => {
        const data = groups[group];
        averages[group] = {
            activeAvg: data.activeCount ? data.activeSum / data.activeCount : 0,
            ytdAvg: data.ytdCount ? data.ytdSum / data.ytdCount : 0
        };
    });

    return averages;
}

function buildOverallAverages(counselors, candidateIds) {
    // [초보 설명] 전체 후보 기준 평균(활성 케이스 수, 연간 배정 수)을 계산한다.
    let activeSum = 0;
    let ytdSum = 0;
    let count = 0;

    candidateIds.forEach(id => {
        activeSum += getActiveCaseCount(counselors[id]);
        ytdSum += getAssignedYtd(counselors[id]);
        count += 1;
    });

    return {
        activeAvg: count ? activeSum / count : 0,
        ytdAvg: count ? ytdSum / count : 0
    };
}

function buildScoreEntries(counselors, candidateIds, client) {
    // 후보 상담사별 점수(비용)를 계산해 표로 보여줄 수 있는 형태로 만든다.
    const config = getScoringConfig();
    const overallAverages = buildOverallAverages(counselors, candidateIds);

    return candidateIds.map(id => {
        const counselor = counselors[id];
        const group = getTenureGroup(counselor);
        const costs = calculateCosts(counselor, client, overallAverages, config);
        const fairDetails = calculateFairCostDetails(counselor, client);
        const totalAssigned = counselor.total || 0;
        const withinLimit = withinDailyLimit(counselor, config) && withinRollingLimits(counselor, config);
        return {
            counselorId: id,
            tenureGroup: group,
            total: costs.total,
            score: convertCostToScore(costs.total),
            cLoad: costs.cLoad,
            cFair: costs.cFair,
            cFairDetail: fairDetails,
            cStreak: costs.cStreak,
            cPace: costs.cPace,
            headcountWeight: costs.headcountWeight,
            totalAssigned: totalAssigned,
            withinLimit: withinLimit,
            ytd: getAssignedYtd(counselor)
        };
    });
}

function calculateCosts(counselor, client, overallAvg, config) {
    //  여러 기준(업무량, 공정성, 연속 배정, 진행 속도)을
    // 가중치로 합산해 "비용"을 만든다. 값이 낮을수록 유리하며, 이후 점수로 변환된다.
    // total = w_load*cLoad + w_fair*cFair + w_streak*cStreak + w_pace*cPace
    const active = getActiveCaseCount(counselor);
    const ytd = getAssignedYtd(counselor);

    const cLoad = Math.pow(active - (overallAvg?.activeAvg || 0), 2);
    const cStreak = Math.pow((counselor.streak_count || counselor.streakCount || 0), 2);
    const cPace = Math.pow(ytd - (overallAvg?.ytdAvg || 0), 2);

    const cFair = calculateFairCost(counselor, client);

    const total = (config.weights.load * cLoad)
        + (config.weights.fair * cFair)
        + (config.weights.streak * cStreak)
        + (config.weights.pace * cPace);

    // [초보 설명] 상담사별 인원 가중치를 적용해 최종 점수를 보정한다.
    // - 인원 가중치가 높을수록 더 많은 인원을 배정해야 하므로 점수를 "낮게" 만든다.
    // - 인원 가중치가 낮을수록 덜 배정해야 하므로 점수를 "높게" 만든다.
    // - 백엔드에서 0.5 ~ 2.0 범위로 전달된다는 전제다.
    const headcountWeightRaw = getHeadcountWeight(counselor);
    const headcountWeight = Math.min(2, Math.max(0.5, headcountWeightRaw));
    const adjustedTotal = total / headcountWeight;
    return { total: adjustedTotal, cLoad, cFair, cStreak, cPace, headcountWeight };

}

function convertCostToScore(costValue) {
    // [초보 설명] 기존 산정식(비용)을 "점수"로 변환한다.
    // 비용이 낮을수록 점수가 높아지도록 1/(1+cost) 형태를 사용한다.
    if (!Number.isFinite(costValue)) {
        return 0;
    }
    return 1 / (1 + costValue);
}

function calculateFairCost(counselor, client) {
    // [초보 설명] 공정성(편향 최소화) 점수의 총점을 반환한다.
    return calculateFairCostDetails(counselor, client).total;
}

function calculateFairCostDetails(counselor, client) {
    // [초보 설명] 공정성(편향 최소화)을 위한 비용을 상세 항목으로 계산한다.
    // 특정 유형/성별/연령대가 과도하게 몰리지 않도록 비율을 계산한다.
    const total = counselor.total || 0;
    if (!total) {
        return {
            total: 0,
            type: null,
            gender: null,
            age: null,
            specificClass: null,
            education: null,
            career: null
        };
    }

    const ratios = [];
    let typeRatio = null;
    let genderRatio = null;
    let ageRatio = null;
    let specificRatio = null;
    let educationRatio = null;
    let careerRatio = null;

    //  참가자의 특성에 맞는 "현재 비율"을 수집한다.
    if (client.participationType === 'type1') {
        typeRatio = (counselor.type1 || 0) / total;
    } else {
        typeRatio = (counselor.type2 || 0) / total;
    }
    ratios.push(typeRatio);

    if (client.gender === 'man') {
        genderRatio = (counselor.man || 0) / total;
    } else {
        genderRatio = (counselor.woman || 0) / total;
    }
    ratios.push(genderRatio);

    if (client.ageGroup === 'youth') {
        ageRatio = (counselor.youth || 0) / total;
    } else {
        ageRatio = (counselor.middleAged || 0) / total;
    }
    ratios.push(ageRatio);

    if (client.specificClass === 'specialGroup') {
        specificRatio = (counselor.specialGroup || 0) / total;
        ratios.push(specificRatio);
    }
    if (client.education === 'college') {
        educationRatio = (counselor.assignmentEducationCollege || 0) / total;
        ratios.push(educationRatio);
    } else if (client.education === 'high') {
        educationRatio = (counselor.assignmentEducationHigh || 0) / total;
        ratios.push(educationRatio);
    }
    if (client.hasCareer === true) {
        careerRatio = (counselor.assignmentCareerYes || 0) / total;
        ratios.push(careerRatio);
    } else if (client.hasCareer === false) {
        careerRatio = (counselor.assignmentCareerNo || 0) / total;
        ratios.push(careerRatio);
    }

    if (ratios.length === 0) {
        return {
            total: 0,
            type: typeRatio,
            gender: genderRatio,
            age: ageRatio,
            specificClass: specificRatio,
            education: educationRatio,
            career: careerRatio
        };
    }

    //  비율의 평균을 구해 공정성 비용으로 사용한다.
    const sum = ratios.reduce((acc, val) => acc + val, 0);
    return {
        total: sum / ratios.length,
        type: typeRatio,
        gender: genderRatio,
        age: ageRatio,
        specificClass: specificRatio,
        education: educationRatio,
        career: careerRatio
    };
}

function getHeadcountWeight(counselor) {
    //  상담사별 가중치(상담사 1명이 감당 가능한 규모)를 읽는다.
    const raw = counselor.assignmentHeadcountWeight;
    const parsed = Number(raw);
    if (Number.isFinite(parsed) && parsed > 0) {
        console.log(`Headcount weight for ${counselor.name} is ${parsed}`);
        return parsed;
    }
    return 1;
}

function getActiveCaseCount(counselor) {
    //  현재 담당 중인 케이스 수를 다양한 필드명 중에서 찾아 반환한다.
    return counselor.active_case_count || counselor.activeCaseCount || counselor.current || 0;
}

function getAssignedYtd(counselor) {
    //  올해 누적 배정 수를 다양한 필드명 중에서 찾아 반환한다.
    return counselor.assigned_ytd || counselor.assignedYtd || counselor.year2025 || 0;
}

function getScoringConfig() {
    //  화면 입력값을 읽어서 점수 계산에 사용할 설정값을 만든다.
    return {
        weights: {
            load: readNumberValue('#weight-load', 0.45),
            fair: readNumberValue('#weight-fair', 0.35),
            streak: readNumberValue('#weight-streak', 0.10),
            pace: readNumberValue('#weight-pace', 0.10)
        },
        gapThreshold: readIntValue('#gap-threshold', 5),
        dailyLimit: readIntValue('#daily-limit', 3),
        tenureLimits: {
            G1: parseTenureLimit('#limit-g1', { week: 3, biweek: 6, month: 12 }),
            G2: parseTenureLimit('#limit-g2', { week: 5, biweek: 10, month: 14 }),
            G3: parseTenureLimit('#limit-g3', { week: 8, biweek: 15, month: 15 })
        }
    };
}

function readNumberValue(selector, defaultValue) {
    //  숫자 입력값을 읽고, 유효하지 않으면 기본값을 사용한다.
    const raw = $(selector).val();
    const parsed = parseFloat(raw);
    if (Number.isFinite(parsed)) {
        return parsed;
    }
    return defaultValue;
}

function readIntValue(selector, defaultValue) {
    //  정수 입력값을 읽고, 유효하지 않으면 기본값을 사용한다.
    const raw = $(selector).val();
    const parsed = parseInt(raw, 10);
    if (Number.isFinite(parsed)) {
        return parsed;
    }
    return defaultValue;
}

function parseTenureLimit(selector, fallback) {
    //  "주/2주/월" 형태의 입력값(예: 5/10/14)을 파싱한다.
    // 형식이 맞지 않으면 기본값(fallback)을 사용한다.
    const raw = ($(selector).val() || '').trim();
    const parts = raw.split('/');
    if (parts.length !== 3) {
        return fallback;
    }
    const week = parseInt(parts[0], 10);
    const biweek = parseInt(parts[1], 10);
    const month = parseInt(parts[2], 10);

    if (!Number.isFinite(week) || !Number.isFinite(biweek) || !Number.isFinite(month)) {
        return fallback;
    }

    return { week, biweek, month };
}

/**
 * Update counselor data based on client assignment
 */

function updateCounselorData(counselor, client) {
    //  한 명을 배정할 때 상담사 통계를 모두 갱신한다.
    // (총 배정 수, 유형/성별/연령대/학력/특정계층 카운트 등)
    counselor.total = (counselor.total || 0) + 1;
    counselor.year2025 = (counselor.year2025 || 0) + 1;
    counselor.assigned_ytd = (counselor.assigned_ytd || counselor.assignedYtd || 0) + 1;
    // 참여유형 카운트
    if (client.participationType === 'type1') {
        counselor.type1 = (counselor.type1 || 0) + 1;
    } else {
        counselor.type2 = (counselor.type2 || 0) + 1;
    }

    // 성별 카운트
    if (client.gender === 'man') {
        counselor.man = (counselor.man || 0) + 1;
    } else {
        counselor.woman = (counselor.woman || 0) + 1;
    }

    // 연령대 카운트
    if (client.ageGroup === 'youth') {
        counselor.youth = (counselor.youth || 0) + 1;
    } else {
        counselor.middleAged = (counselor.middleAged || 0) + 1;
    }

    // 특정계층 카운트
    if (client.specificClass === 'specialGroup') {
        counselor.specialGroup = (counselor.specialGroup || 0) + 1;
    }




    // 학력 카운트
    if (client.education === 'college') {
        counselor.assignmentEducationCollege = (counselor.assignmentEducationCollege || 0) + 1;
    } else if (client.education === 'high') {
        counselor.assignmentEducationHigh = (counselor.assignmentEducationHigh || 0) + 1;
    }

    // 경력 유무 카운트
    if (client.hasCareer === true) {
        counselor.assignmentCareerYes = (counselor.assignmentCareerYes || 0) + 1;
    } else {
        counselor.assignmentCareerNo = (counselor.assignmentCareerNo || 0) + 1;
    }

    // 현재 담당 건수와 활성 케이스 카운트
    counselor.current = (counselor.current || 0) + 1;
    counselor.active_case_count = (counselor.active_case_count || counselor.activeCaseCount || 0) + 1;

    // 기간별 배정 카운트(1일/7일/14일/30일)를 1씩 증가
    const dayCount = getAssignedDay(counselor) + 1;
    const weekCount = getAssignedWeek(counselor) + 1;
    const twoWeekCount = getAssignedTwoWeek(counselor) + 1;
    const monthCount = getAssignedMonth(counselor) + 1;



    counselor.assignmentAllocationDay = dayCount;
    counselor.assignmentAllocationWeek = weekCount;
    counselor.assignmentAllocationTwoWeek = twoWeekCount;
    counselor.assignmentAllocationMonth = monthCount;


    counselor.assigned_1d = dayCount;
    counselor.assigned_7d = weekCount;
    counselor.assigned_14d = twoWeekCount;
    counselor.assigned_30d = monthCount;
}

function updateStreakCount(counselor, counselorId, lastAssignedCounselorId) {
    //  같은 상담사에게 연속으로 배정됐는지 확인해 연속 배정 수를 계산한다.
    if (counselorId === lastAssignedCounselorId) {
        counselor.streak_count = (counselor.streak_count || counselor.streakCount || 0) + 1;
    } else {
        counselor.streak_count = 1;
    }
    counselor.streakCount = counselor.streak_count;
    return counselorId;
}


/**
 * Update counselor data based on client assignment
 */

function filterExcludedParticipants(counselorsData, excludedPersonnel) {
    //  "배정 제외"로 체크된 상담사를 목록에서 제외한다.
    console.log("excludedPersonnel:"+excludedPersonnel);
    const excludedIds = extractExcludedIds(excludedPersonnel);
    return filterCounselorsByExcludedIds(counselorsData, excludedIds);
}

/**

 * Extract counselor IDs from excluded personnel data
 */

function extractExcludedIds(excludedPersonnel) {
    //  제외 표시(true)된 상담사 ID만 추린다.
    return Object.keys(excludedPersonnel).filter(counselorId => excludedPersonnel[counselorId] === true);
}

/**

 * Filter counselors by excluded IDs
 */

function filterCounselorsByExcludedIds(counselorsData, excludedIds) {
    //  상담사 전체 목록에서 제외 대상 ID를 제거한 새 객체를 만든다.
    const filteredCounselors = {};

    Object.keys(counselorsData).forEach(counselorId => {
        if (!excludedIds.includes(counselorId)) {
            filteredCounselors[counselorId] = counselorsData[counselorId];
        }
    });

    return filteredCounselors;

}

/**

 * Assign participants to counselors based on allocation criteria
 *
 * @param {Object} counselorsData - Counselors data object
 * @param {Array} participantData - Participant data array
 * @returns {Array} Assignment results array
 */

function assignParticipantsToCounselors(counselorsData, participantData, options) {
    //  참여자 목록을 순회하면서 상담사를 배정한다.
    // - 고정 배정이 있으면 그 상담사로 배정
    // - 아니면 랜덤(점수 기반) 배정
    // - 아무도 배정되지 못하면 "미배정"으로 기록
    const assignmentResults = [];
    const filteredParticipantData = filterExcludedParticipants(counselorsData, excludedPersonnel);
    let lastAssignedCounselorId = null;

    participantData.forEach((participant, index) => {
        const participantIndex = typeof participant._rowIndex === 'number' ? participant._rowIndex : index;
        const clientInfo = parseParticipantData(participant);
        const fixedCounselorId = (participant[1] || '').trim(); // CSV의 상담사ID 칸

        if (fixedCounselorId) {
            const fixedCounselor = filteredParticipantData[fixedCounselorId];
            if (!fixedCounselor) {
                return;
            }

            //  고정 배정은 상담사 통계에 바로 반영한다.
            lastAssignedCounselorId = updateStreakCount(fixedCounselor, fixedCounselorId, lastAssignedCounselorId);
            updateCounselorData(fixedCounselor, clientInfo);
            assignmentResults.push({
                participantIndex: participantIndex,
                participantName: clientInfo.name,
                assignedCounselorId: fixedCounselorId,
                assignedCounselorName: fixedCounselor.name,
                assignmentType: 'fixed'
            });
            // 고정 배정은 점수 비교 대상이 아니므로 점수 목록은 비운다.
            upsertAssignmentScoreLog({
                participantIndex: participantIndex,
                participantName: clientInfo.name,
                selectionType: 'fixed',
                selectedId: fixedCounselorId,
                candidateIds: [],
                scores: [],
                selectionReason: '고정 배정'
            });
            return;
        }

        //  고정 배정이 없으면 최적 상담사를 찾는다.
        const scoreDebug = {
            participantIndex: participantIndex,
            participantName: clientInfo.name,
            selectionType: '',
            selectedId: '',
            candidateIds: [],
            scores: [],
            selectionReason: ''
        };
        const assignedCounselorId = findOptimalCounselorForParticipant(
            filteredParticipantData,
            clientInfo,
            scoreDebug,
            options
        );

        if (assignedCounselorId) {
            lastAssignedCounselorId = updateStreakCount(filteredParticipantData[assignedCounselorId], assignedCounselorId, lastAssignedCounselorId);
            updateCounselorData(filteredParticipantData[assignedCounselorId], clientInfo);
            assignmentResults.push({
                participantIndex: participantIndex,
                participantName: clientInfo.name,
                assignedCounselorId: assignedCounselorId,
                assignedCounselorName: filteredParticipantData[assignedCounselorId].name,
                assignmentType: 'random'
            });
            upsertAssignmentScoreLog(scoreDebug);
        } else {
            assignmentResults.push({
                participantIndex: participantIndex,
                participantName: clientInfo.name,
                assignedCounselorId: '',
                assignedCounselorName: '',
                assignmentType: 'unassigned'
            });
            upsertAssignmentScoreLog(scoreDebug);
        }
    });

    return assignmentResults;
}

/**
 * Update assignment UI with results
 * @param {Array} results - Assignment results array
 */
function updateAssignmentUI(results) {
    //  배정 결과를 화면 테이블에 표시한다.
    $('#csv-data .csv-data-tr').each(function(index) {
        const result = results.find(r => r.participantIndex === index);
        if (result) {
            const $cell = $(this).find('.random-td-input');
            $cell.text(result.assignedCounselorId);
            if (result.assignmentType === 'fixed') {
                $cell.attr('data-fixed', 'true');
            } else {
                $cell.removeAttr('data-fixed');
            }
        }
    });

}



function appendManualEntryRow(data) {
    //  사용자가 직접 입력한 참여자 정보를
    // 테이블에 "한 줄"로 추가한다.
    const assignedCounselorId = data.travelCounselor ? data.travelCounselor : '';
    const fixedAttr = data.travelCounselor ? ' data-fixed="true"' : '';
    const rowHtml = `
        <tr class="csv-data-tr">
            <td></td>
            <td class="random-td-input"${fixedAttr}>${escapeHtml(assignedCounselorId)}</td>
            <td class="random-td">${escapeHtml(data.participantName)}</td>
            <td class="random-td">${escapeHtml(data.participationType)}</td>
            <td class="random-td">${escapeHtml(data.gender)}</td>
            <td class="random-td">${escapeHtml(data.birthDate)}</td>
            <td class="random-td">${escapeHtml(data.recruitmentPath)}</td>
            <td class="random-td">${escapeHtml(data.hasCareer)}</td>
            <td class="random-td">${escapeHtml(data.education)}</td>
            <td class="random-td">${escapeHtml(data.specificClass)}</td>
            <td class="random-td">${escapeHtml(data.progressStage)}</td>
            <td class="random-td">${escapeHtml(data.travelCounselor)}</td>
        </tr>
    `;

    $('#csv-data').append(rowHtml);
    renumberCsvRows();
    refreshCounselorTableWithFixed();
}

function renumberCsvRows() {
    //  테이블의 첫 번째 열(번호)을 1부터 다시 매긴다.
    $('#csv-data .csv-data-tr').each(function(index) {
        $(this).find('td').first().text(index + 1);
    });
}

/**
 * Update counselor table with fixed assignments
 */
function updateCounselorTable(updatedCounselors) {
    //  상담사 통계 테이블을 최신 데이터로 다시 그린다.
    $('#assign-count-table-body').empty();
    Object.keys(updatedCounselors).forEach(counselorId => {
        const counselor = updatedCounselors[counselorId];
        const dayCount = getAssignedDay(counselor);
        const weekCount = getAssignedWeek(counselor);
        const twoWeekCount = getAssignedTwoWeek(counselor);
        const monthCount = getAssignedMonth(counselor);
        const headcountWeight = Number.isFinite(Number(counselor.assignmentHeadcountWeight)) ? Number(counselor.assignmentHeadcountWeight) : 1;
        const row = `
            <tr>  
                <td>
                <input type="checkbox" value="${counselorId}" id="${counselorId}" class="assign-counselor-input">
                <label for="${counselorId}">${counselorId}</label>
                </td>
                <td>${counselor.name}</td>
                <td>${counselor.employmentDate}</td>
                <td>${headcountWeight}</td>
                <td>${counselor.total}</td>
                <td>${dayCount}</td>
                <td>${weekCount}</td>
                <td>${twoWeekCount}</td>
                <td>${monthCount}</td>
                <td>${counselor.type1 || 0}</td>
                <td>${counselor.type2 || 0}</td>
                <td>${counselor.assignmentEducationCollege || 0}</td>
                <td>${counselor.assignmentEducationHigh || 0}</td>
                <td>${counselor.man || 0}</td>
                <td>${counselor.woman || 0}</td>
                <td>${counselor.youth || 0}</td>
                <td>${counselor.middleAged || 0}</td>
                <td>${counselor.specialGroup || 0}</td>
                <td>${counselor.current || 0}</td>
                <td>${counselor.max}</td>
            </tr>
        `;
        $('#assign-count-table-body').append(row);
        if(excludedPersonnel[counselorId] != null && excludedPersonnel[counselorId] !== undefined)
            $('#'+counselorId+'').prop('checked', excludedPersonnel[counselorId]);
    });

    updateManualTravelOptions();
}

function updateManualTravelOptions() {
    //  직접 입력 폼의 "출장상담사 선택 목록"을
    // 현재 상담사 목록으로 갱신한다.
    const $select = $('#manual-travel');
    if ($select.length === 0) {
        return;
    }

    const currentValue = $select.val() || '';
    const ids = [];
    $('#assign-count-table .assign-counselor-input').each(function() {
        const value = $(this).val();
        if (value) {
            ids.push(value);
        }
    });

    const uniqueIds = Array.from(new Set(ids)).sort();
    let optionsHtml = '<option value="">선택 안함</option>';
    uniqueIds.forEach(id => {
        optionsHtml += `<option value="${id}">${id}</option>`;
    });
    $select.html(optionsHtml);

    if (currentValue && uniqueIds.includes(currentValue)) {
        $select.val(currentValue);
    }
}
/**
 * Execute random assignment process
 */
function executeRandomAssignment(options) {
    // UI → 배정 → UI 반영 전체 흐름:
    // 1) 테이블에서 참가자 행 데이터를 수집
    // 2) 출장상담사 입력 여부로 고정 배정 행을 표시
    // 3) 상담사 데이터 유효성 확인
    // 4) 고정 배정 반영 후 랜덤 배정 실행
    // 5) 결과를 테이블/상담사 통계에 반영
    const assignmentOptions = options || {};

    try {
        //  먼저 데이터 검증을 통과해야 배정을 시작한다.
        if (!executeDataValidation()) {
            resetRandomAssignments();
            return false;
        }

        // 이전 점수 로그를 초기화한다.
        assignmentScoreLog = [];
        clearAssignmentScoreTable();

        //  화면에 표시된 CSV 테이블을 한 줄씩 읽어 배열로 만든다.
        const participantRows = [];
        $('#csv-data .csv-data-tr').each(function(index) {
            const rowData = [];
            $(this).find('td').each(function() {
                rowData.push($(this).text().trim());
            });
            rowData._rowIndex = index;
            applyDefaultValuesToRow(rowData, $(this), index + 1);
            const counselorCell = $(this).find('.random-td-input');
            const travelCounselorId = rowData[11] || '';

            //  출장상담사 컬럼이 채워져 있으면
            // 해당 상담사로 "고정 배정" 처리한다.
            if (travelCounselorId.trim() !== '') {
                counselorCell.attr('data-fixed', 'true');
                rowData[1] = travelCounselorId.trim();
            } else if (counselorCell.attr('data-fixed') !== 'true') {
                rowData[1] = '';
            }

            participantRows.push(rowData);
        });

        if (participantRows.length === 0) {
            alert('먼저 CSV 파일을 업로드해주세요.');
            return false;
        }

        //  상담사 목록이 없으면 배정할 수 없으므로 중단한다.
        if (typeof currentCounselor === 'undefined' || Object.keys(currentCounselor).length === 0) {
            alert('상담사 데이터가 없습니다. 페이지를 새로고침해주세요.');
            return false;
        }

        //  원본 상담사 데이터는 보호하고,
        // 복사본을 만들어 배정 결과를 반영한다.
        const counselorsCopy = getBaseCounselorState();

        //  랜덤 배정 실행.
        // 미배정이 남아 있으면 다시 배정 시도한다.
        let pendingRows = participantRows.slice();
        let assignmentResults = [];
        let lastPendingCount = pendingRows.length + 1;

        while (pendingRows.length > 0 && pendingRows.length < lastPendingCount) {
            lastPendingCount = pendingRows.length;
            const batchResults = assignParticipantsToCounselors(counselorsCopy, pendingRows, assignmentOptions);
            assignmentResults = assignmentResults.concat(batchResults);
            pendingRows = batchResults
                .filter(item => item.assignmentType === 'unassigned')
                .map(item => participantRows[item.participantIndex]);
        }

        //  화면 테이블에 배정 결과 반영
        updateAssignmentUI(assignmentResults);

        //  상담사 통계 테이블 반영
        updateCounselorTable(counselorsCopy);

        // [초보 설명] 배정 점수 표 출력은 테스트 종료 전까지 주석 처리한다.
        // renderAssignmentScoreTable(assignmentScoreLog);

        //  성공 메시지와 통계 요약 표시
        const responseTextDiv = $('.response-text-div');
        const fixedCount = assignmentResults.filter(item => item.assignmentType === 'fixed').length;
        const randomCount = assignmentResults.filter(item => item.assignmentType === 'random').length;
        const unassignedCount = assignmentResults.filter(item => item.assignmentType === 'unassigned').length;

        responseTextDiv.empty();
        responseTextDiv.show();
        responseTextDiv.append('<br><strong style="color: blue;">랜덤 배정이 완료되었습니다.</strong>');
        responseTextDiv.append('<br><span>총 ' + (fixedCount + randomCount) + '명 배정 (고정 배정: ' + fixedCount + '명, 랜덤 배정: ' + randomCount + '명)</span>');
        if (unassignedCount > 0) {
            responseTextDiv.append('<br><span style="color: #d9534f;">미배정: ' + unassignedCount/2 + '명</span>');
        }

        return true;

    } catch (error) {
        console.error('랜덤 배정 중 오류 발생:', error);
        alert('랜덤 배정 중 오류가 발생했습니다: ' + error.message);
        return false;
    }
}
// 초기 상담사 상태를 저장해 두는 스냅샷(원본 복원용)
let baseCounselorSnapshot = null;
// 배정 테스트용 점수 로그(참여자별 상담사 점수) 저장소
let assignmentScoreLog = [];

function formatScoreNumber(value, digits) {
    // 숫자를 보기 좋게 소수점 자리수로 표시한다.
    if (!Number.isFinite(value)) {
        return '-';
    }
    return value.toFixed(digits);
}

function ensureAssignmentScoreArea() {
    // 점수 표를 표시할 영역이 없으면 생성한다.
    let $area = $('#assignment-score-area');
    if ($area.length === 0) {
        $area = $('<div id="assignment-score-area"></div>');
        const $anchor = $('#assign-count-table');
        if ($anchor.length > 0) {
            $area.insertAfter($anchor);
        } else if ($('#csv-data').length > 0) {
            $area.insertAfter($('#csv-data'));
        } else {
            $('body').append($area);
        }
    }
    return $area;
}

function clearAssignmentScoreTable() {
    // 이전 점수 표를 비운다.
    const $area = ensureAssignmentScoreArea();
    $area.empty();
}

function upsertAssignmentScoreLog(entry) {
    // 동일 참여자에 대한 점수 로그가 있으면 마지막 결과로 덮어쓴다.
    const index = assignmentScoreLog.findIndex(item => item.participantIndex === entry.participantIndex);
    if (index >= 0) {
        assignmentScoreLog[index] = entry;
        return;
    }
    assignmentScoreLog.push(entry);
}

function renderAssignmentScoreTable(scoreLogs) {
    // [초보 설명] 참여자별 점수를 1행씩 확인할 수 있도록 표로 표시한다.
    const $area = ensureAssignmentScoreArea();
    if (!scoreLogs || scoreLogs.length === 0) {
        $area.html('<div class="csv-danger">점수 표를 표시할 데이터가 없습니다.</div>');
        return;
    }

    const formatFairDetail = function(detail) {
        if (!detail) {
            return '-';
        }
        const type = Number.isFinite(detail.type) ? formatScoreNumber(detail.type, 4) : '-';
        const gender = Number.isFinite(detail.gender) ? formatScoreNumber(detail.gender, 4) : '-';
        const age = Number.isFinite(detail.age) ? formatScoreNumber(detail.age, 4) : '-';
        const specificClass = Number.isFinite(detail.specificClass) ? formatScoreNumber(detail.specificClass, 4) : '-';
        const education = Number.isFinite(detail.education) ? formatScoreNumber(detail.education, 4) : '-';
        const career = Number.isFinite(detail.career) ? formatScoreNumber(detail.career, 4) : '-';
        const total = formatScoreNumber(detail.total, 4);
        return `총점:${total} | 유형:${type}, 성별:${gender}, 연령:${age}, 특정계층:${specificClass}, 학력(대졸/고졸):${education}, 경력:${career}`;
    };

    let html = '';
    html += '<h3 style="margin-top: 20px;">배정 점수 확인 표</h3>';
    html += '<p style="font-size: 0.85em; color: #666; margin-bottom: 10px;">';
    html += '※ 배정 로직: 전체 후보 → 상위 4명 선정 → 한도 만족 필터(랜덤 배정 시) → 배정인원 최소인 상담사 우선 → 랜덤 선택';
    html += '</p>';
    html += '<table class="table table-bordered" style="width: 100%; font-size: 0.9em;">';
    html += '<thead><tr>';
    html += '<th rowspan="2">참여자번호</th>';
    html += '<th rowspan="2">선택유형</th>';
    html += '<th rowspan="2">선택상담사</th>';
    html += '<th rowspan="2">전체 후보(점수)</th>';
    html += '<th rowspan="2">상위 4명</th>';
    html += '<th rowspan="2">최종 후보</th>';
    html += '<th rowspan="2">상담사</th>';
    html += '<th rowspan="2">총점</th>';
    html += '<th rowspan="2">배정인원</th>';
    html += '<th rowspan="2">한도만족</th>';
    html += '<th colspan="4">비용 상세</th>';
    html += '<th rowspan="2">인원가중치</th>';
    html += '<th rowspan="2">근속그룹</th>';
    html += '<th rowspan="2">선택이유</th>';
    html += '</tr>';
    html += '<tr>';
    html += '<th>C_load</th>';
    html += '<th>C_fair</th>';
    html += '<th>C_streak</th>';
    html += '<th>C_pace</th>';
    html += '</tr></thead><tbody>';

    scoreLogs.forEach(log => {
        const candidateText = Array.isArray(log.candidateIds) && log.candidateIds.length > 0
            ? escapeHtml(
                log.candidateIds.map(id => {
                    const scoreEntry = Array.isArray(log.scores)
                        ? log.scores.find(item => item.counselorId === id)
                        : null;
                    const scoreText = scoreEntry ? formatScoreNumber(scoreEntry.score, 3) : '-';
                    return `${id}(${scoreText})`;
                }).join(', ')
            )
            : '-';

        // 상위 4명 후보 텍스트 생성
        const topCandidatesText = Array.isArray(log.topCandidates) && log.topCandidates.length > 0
            ? escapeHtml(
                log.topCandidates.map(item =>
                    `${item.id}(${formatScoreNumber(item.score, 3)}, T:${item.totalAssigned || 0}${item.withinLimit !== undefined ? (item.withinLimit ? ', ✓' : ', ✗') : ''})`
                ).join(', ')
            )
            : '-';

        // 최종 후보 텍스트 생성
        const finalCandidatesText = Array.isArray(log.finalCandidates) && log.finalCandidates.length > 0
            ? escapeHtml(
                log.finalCandidates.map(item =>
                    `${item.id}(T:${item.totalAssigned || 0})`
                ).join(', ')
            )
            : '-';

        if (!Array.isArray(log.scores) || log.scores.length === 0) {
            html += '<tr>';
            html += `<td>${escapeHtml(String(log.participantIndex + 1))}</td>`;
            html += `<td>${escapeHtml(log.selectionType || '')}</td>`;
            html += `<td>${escapeHtml(log.selectedId || '')}</td>`;
            html += `<td>${candidateText}</td>`;
            html += `<td>${topCandidatesText}</td>`;
            html += `<td>${finalCandidatesText}</td>`;
            html += '<td>-</td><td>-</td><td>-</td><td>-</td><td>-</td><td>-</td><td>-</td><td>-</td><td>-</td><td>-</td><td>-</td>';
            html += '</tr>';
            return;
        }

        log.scores.forEach(score => {
            const total = formatScoreNumber(score.score, 6);
            const totalAssigned = score.totalAssigned !== undefined ? score.totalAssigned : 0;
            const withinLimit = score.withinLimit !== undefined ? (score.withinLimit ? '✓' : '✗') : '-';
            const cLoad = formatScoreNumber(score.cLoad, 4);
            const cFair = formatFairDetail(score.cFairDetail);
            const cStreak = formatScoreNumber(score.cStreak, 4);
            const cPace = formatScoreNumber(score.cPace, 4);
            const weight = formatScoreNumber(score.headcountWeight, 2);
            const tenureGroup = escapeHtml(score.tenureGroup || '');
            const reason = escapeHtml(log.selectionReason || '');

            html += '<tr>';
            html += `<td>${escapeHtml(String(log.participantIndex + 1))}</td>`;
            html += `<td>${escapeHtml(log.selectionType || '')}</td>`;
            html += `<td>${escapeHtml(log.selectedId || '')}</td>`;
            html += `<td>${candidateText}</td>`;
            html += `<td>${topCandidatesText}</td>`;
            html += `<td>${finalCandidatesText}</td>`;
            html += `<td>${escapeHtml(score.counselorId)}</td>`;
            html += `<td>${total}</td>`;
            html += `<td>${totalAssigned}</td>`;
            html += `<td>${withinLimit}</td>`;
            html += `<td>${cLoad}</td>`;
            html += `<td>${cFair}</td>`;
            html += `<td>${cStreak}</td>`;
            html += `<td>${cPace}</td>`;
            html += `<td>${weight}</td>`;
            html += `<td>${tenureGroup}</td>`;
            html += `<td>${reason}</td>`;
            html += '</tr>';
        });
    });

    html += '</tbody></table>';
    $area.html(html);
}

function cloneCounselors(data) {
    //  상담사 객체를 깊은 복사하여 원본을 보호한다.
    return JSON.parse(JSON.stringify(data));
}

function getBaseCounselorState() {
    //  초기 상담사 상태(스냅샷)가 있으면 그것을 사용하고,
    // 없으면 현재 상담사 데이터를 복사한다.
    return baseCounselorSnapshot ? cloneCounselors(baseCounselorSnapshot) : cloneCounselors(currentCounselor);
}

function getCounselorStateWithFixed() {
    //  기본 상담사 상태에 "고정 배정"을 반영한 버전을 만든다.
    const base = baseCounselorSnapshot ? cloneCounselors(baseCounselorSnapshot) : cloneCounselors(currentCounselor);
    applyFixedAssignments(base);
    return base;

}

function refreshCounselorTableWithFixed() {
    //  고정 배정 정보를 반영한 상담사 테이블을 갱신한다.
    if (typeof currentCounselor === 'undefined' || !currentCounselor) {
        return;
    }
    if (!baseCounselorSnapshot) {
        baseCounselorSnapshot = cloneCounselors(currentCounselor);
    }
    const updated = getCounselorStateWithFixed();
    updateCounselorTable(updated);
}

function applyFixedAssignments(counselorData) {
    //  CSV 테이블에서 data-fixed 표시된 행을 찾아
    // 해당 상담사에게 "이미 배정된 것처럼" 카운트를 반영한다.
    let lastAssignedCounselorId = null;
    $('#csv-data .csv-data-tr').each(function() {
        const counselorCell = $(this).find('.random-td-input');
        if (counselorCell.attr('data-fixed') !== 'true') {
            return;
        }
        const counselorId = counselorCell.text().trim();
        if (!counselorId || !counselorData[counselorId]) {
            return;
        }
        const rowData = [];
        $(this).find('td').each(function() {
            rowData.push($(this).text().trim());
        });
        //  참여자 정보를 파싱하고 상담사 통계를 업데이트한다.
        const clientInfo = parseParticipantData(rowData);
        lastAssignedCounselorId = updateStreakCount(counselorData[counselorId], counselorId, lastAssignedCounselorId);
        updateCounselorData(counselorData[counselorId], clientInfo);
    });
}

/**
 * Execute data validation process
 */
function executeDataValidation() {
    //  CSV 테이블의 모든 행을 검사해서
    // 입력 형식이 맞는지 확인하고 오류를 모아 보여준다.
    const responseTextDiv = $('.response-text-div');
    try {
        responseTextDiv.empty();
        const csvRows = $('#csv-data .csv-data-tr');
        if (csvRows.length === 0) {
            responseTextDiv.show().html('<strong class="csv-danger">검증할 데이터가 없습니다. CSV 파일을 먼저 업로드해주세요.</strong>');
            return false;
        }

        let validationErrors = [];
        let autoFixMessages = [];
        let validRowCount = 0;

        csvRows.each(function(index) {
            const rowData = [];
            $(this).find('td').each(function() {
                rowData.push($(this).text().trim());
            });

            if (rowData.length >= 12) {
                // 모집경로/특정계층/진행단계/경력유무가 비어 있으면 기본값으로 자동 보정
                applyDefaultValuesToRow(rowData, $(this), index + 1, autoFixMessages); // 최소 12개 컬럼 확인
                const rowErrors = validateRowData(rowData, index + 1);
                if (rowErrors.length > 0) {
                    validationErrors = validationErrors.concat(rowErrors);
                } else {
                    const travelCounselor = rowData[11];
                    if (travelCounselor && travelCounselor.trim() !== '' && (!currentCounselor || !currentCounselor[travelCounselor.trim()])) {
                        validationErrors.push(`${index + 1}행: 출장상담사 아이디가 상담사 목록에 없습니다. (입력값: ${travelCounselor})`);
                    } else {
                        validRowCount++;
                    }
                }
            }
        });

        responseTextDiv.show();
        if (validationErrors.length === 0) {
            //  오류가 없으면 성공 메시지와 자동 보정 내역을 표시한다.
            let html = `<strong style="color: green;">데이터 검증 완료</strong><br>총 ${validRowCount}건의 데이터가 모두 유효합니다.`;
            if (autoFixMessages.length > 0) {
                html += `<br><br><strong style="color: #0d6efd;">자동 보정 내역</strong><br>`;
                html += autoFixMessages.map(message => `<span>${message}</span>`).join('<br>');
            }
            responseTextDiv.html(html);
        } else {
            //  오류가 있으면 목록 형태로 자세히 보여준다.
            let errorHtml = `<strong class="csv-danger">데이터 검증 실패</strong><br>${validationErrors.length}개의 오류가 발생했습니다.<br>`;
            errorHtml += '<div style="max-height: 200px; overflow-y: auto; margin-top: 10px; padding: 10px; border: 1px solid #ddd; background-color: #f9f9f9;">';
            errorHtml += '<ul style="margin: 0; padding-left: 20px; font-size: 0.9em;">';
            validationErrors.forEach(error => {
                errorHtml += `<li style="margin-bottom: 5px;">${error}</li>`;
            });
            errorHtml += '</ul></div>';
            if (autoFixMessages.length > 0) {
                errorHtml += `<div style="margin-top: 10px;"><strong style="color: #0d6efd;">자동 보정 내역</strong><br>`;
                errorHtml += autoFixMessages.map(message => `<span>${message}</span>`).join('<br>');
                errorHtml += '</div>';
            }
            responseTextDiv.html(errorHtml);
        }

        return validationErrors.length === 0;

    } catch (error) {
        console.error('데이터 검증 중 오류 발생:', error);
        responseTextDiv.show().html('<strong class="csv-danger">데이터 검증 중 시스템 오류가 발생했습니다.</strong>');
        return false;
    }
}
function validateRowData(rowData, rowNumber) {
    //  한 줄(한 사람)의 데이터를 규칙에 맞게 검사한다.
    const errors = [];

    // 데이터 구조: [번호, 상담사ID, 참여자 성명, 참여유형, 성별, 생년월일, 모집경로, 경력유무, 학력, 특정계층여부, 진행단계, 출장상담사]
    const [number, counselorAccount, participantName, participationType, gender, birthDate, recruitmentPath, hasCareer, education, specificClass, progressStage, travelCounselor] = rowData;

    //  아래 조건들은 "빈 값이거나 형식이 잘못된 경우"를 체크한다.
    if (!participantName || participantName.trim() === '') {
        errors.push(`${rowNumber}행: 참여자 성명이 비어있습니다.`);
    }

    if (!participationType || participationType.trim() === '') {
        errors.push(`${rowNumber}행: 참여유형이 비어있습니다.`);
    } else if (!['1', '2', '1유형', '2유형'].includes(participationType.trim())) {
        errors.push(`${rowNumber}행: 참여유형은 '1', '2', '1유형', '2유형'만 입력 가능합니다. (입력값: ${participationType})`);
    }

    if (!gender || gender.trim() === '') {
        errors.push(`${rowNumber}행: 성별이 비어있습니다.`);
    } else if (!['남', '여'].includes(gender.trim())) {
        errors.push(`${rowNumber}행: 성별은 '남' 또는 '여'만 입력 가능합니다. (입력값: ${gender})`);
    }

    if (!birthDate || birthDate.trim() === '') {
        errors.push(`${rowNumber}행: 생년월일이 비어있습니다.`);
    } else if (!/^\d{4}-\d{2}-\d{2}$/.test(birthDate.trim())) {
        errors.push(`${rowNumber}행: 생년월일은 YYYY-MM-DD 형식만 입력 가능합니다. (입력값: ${birthDate})`);
    }

    if (!education || education.trim() === '') {
        errors.push(`${rowNumber}행: 학력이 비어있습니다.`);
    }

    if (specificClass && specificClass.trim() !== '' &&
        !['O', 'X', 'Y', 'N', '해당', '해당 없음'].includes(specificClass.trim())) {
        errors.push(`${rowNumber}행: 특정계층 여부는 '해당', '해당 없음', 'O', 'X'만 입력 가능합니다. (입력값: ${specificClass})`);
    }

    if (travelCounselor && travelCounselor.trim() !== '' && !/^[A-Za-z0-9_-]+$/.test(travelCounselor.trim())) {
        errors.push(`${rowNumber}행: 출장상담사 아이디 형식이 올바르지 않습니다. (입력값: ${travelCounselor})`);
    }

    return errors;
}
function executeDataSave() {
    //  화면의 배정 결과를 서버로 저장하는 작업을 수행한다.
    const responseTextDiv = $('.response-text-div');

    try {
        const csvRows = $('#csv-data .csv-data-tr');
        if (csvRows.length === 0) {
            responseTextDiv.show().html('<strong class="csv-danger">저장할 데이터가 없습니다. CSV 파일을 먼저 업로드해주세요.</strong>');
            return false;
        }

        const assignmentDataList = [];
        const csvHistoryList = [];
        const dailyReportList = [];
        let unassignedCount = 0;

        csvRows.each(function(index) {
            const row = $(this);
            const cells = row.find('td');

            // CSV 데이터 구조: [번호, 상담사ID, 참여자 성명, 참여유형, 성별, 생년월일, 모집경로, 경력유무, 학력, 특정계층여부, 진행단계, 출장상담사]
            const counselorId = cells.eq(1).text().trim();
            const participantName = cells.eq(2).text().trim();
            const participationType = cells.eq(3).text().trim();
            const gender = cells.eq(4).text().trim();
            const birthDate = cells.eq(5).text().trim();
            const recruitmentPath = cells.eq(6).text().trim();
            const hasCareer = cells.eq(7).text().trim();
            const education = cells.eq(8).text().trim();
            const specificClass = cells.eq(9).text().trim();
            const progressStage = cells.eq(10).text().trim();
            const travelCounselor = cells.eq(11).text().trim();

            csvHistoryList.push({
                rowNumber: index + 1,
                counselorID: counselorId,
                participant: participantName,
                participationType: participationType,
                gender: gender,
                birthDate: birthDate,
                recruitmentPath: recruitmentPath,
                hasCareer: hasCareer,
                education: education,
                specificClass: specificClass,
                progressStage: progressStage,
                travelCounselor: travelCounselor
            });

            if (!counselorId || counselorId === '') {
                unassignedCount++;
            } else {
                //  서버에 보낼 배정 데이터(Assignment DTO)를 만든다.
                const assignmentDTO = {
                    counselorID: counselorId,
                    participant: participantName,
                    birthDate: birthDate,
                    gender: gender,
                    recruitmentPath: recruitmentPath,
                    participationType: participationType,
                    education: education,
                    progressStage: progressStage,
                    specificClass: specificClass
                    // condition and branch are set on the server.
                };

                assignmentDataList.push(assignmentDTO);
            }
        });

        if (unassignedCount > 0) {
            //  미배정 인원이 있으면 사용자에게 확인을 받는다.
            if (!confirm(`${unassignedCount}명의 참여자가 아직 배정되지 않았습니다. 그래도 저장하시겠습니까?`)) {
                return false;
            }
        }

        $('#assign-count-table-body tr').each(function() {
            const $row = $(this);
            const counselorId = $row.find('.assign-counselor-input').val() || '';
            if (!counselorId) {
                return;
            }
            //  일일 리포트용 통계를 만든다.
            const type1 = parseInt($row.find('td').eq(8).text().trim(), 10) || 0;
            const type2 = parseInt($row.find('td').eq(9).text().trim(), 10) || 0;
            dailyReportList.push({
                counselorID: counselorId,
                assignmentType1: type1,
                assignmentType2: type2
            });
        });

        const scoringConfig = {
            weightLoad: readNumberValue('#weight-load', 0.45),
            weightFair: readNumberValue('#weight-fair', 0.35),
            weightStreak: readNumberValue('#weight-streak', 0.10),
            weightPace: readNumberValue('#weight-pace', 0.10),
            gapThreshold: readIntValue('#gap-threshold', 5),
            dailyLimit: readIntValue('#daily-limit', 3),
            limitG1: ($('#limit-g1').val() || '').trim(),
            limitG2: ($('#limit-g2').val() || '').trim(),
            limitG3: ($('#limit-g3').val() || '').trim()
        };

        if (!confirm(`배정 ${assignmentDataList.length}건, CSV ${csvHistoryList.length}건을 저장하시겠습니까?`)) {
            return false;
        }

        responseTextDiv.show().html('<strong style="color: blue;">데이터 저장 중...</strong>');

        const historyPayload = {
            assignments: assignmentDataList,
            dailyReports: dailyReportList,
            scoringConfig: scoringConfig,
            csvHistories: csvHistoryList
        };

        $.ajax({
            url: '/api/pra.history',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(historyPayload),
            success: function(response) {
                if (response === true) {
                    //  서버 저장 성공
                    responseTextDiv.html('<strong style="color: green;">✓ 데이터가 성공적으로 저장되었습니다.</strong>');
                    // console.log('저장 성공:', response);
                    alert('데이터가 성공적으로 저장되었습니다.');
                } else {
                    //  서버가 실패 응답을 준 경우
                    responseTextDiv.html('<strong class="csv-danger">✗ 데이터 저장에 실패했습니다.</strong>');
                    // console.error('저장 실패:', response);
                    alert('데이터 저장에 실패했습니다.');
                }
            },
            error: function(xhr, status, error) {
                //  네트워크나 서버 오류 처리
                console.error('저장 중 오류 발생:', error);
                console.error('응답 상태:', xhr.status);
                console.error('응답 내용:', xhr.responseText);

                let errorMessage = '데이터 저장 중 오류가 발생했습니다.';
                if (xhr.status === 400) {
                    errorMessage += ' (잘못된 요청)';
                } else if (xhr.status === 500) {
                    errorMessage += ' (서버 오류)';
                }

                responseTextDiv.html(`<strong class="csv-danger">✗ ${errorMessage}</strong>`);
                alert('데이터 저장 중 오류가 발생했습니다. 관리자에게 문의하세요.');
            }
        });

        return true;

    } catch (error) {
        console.error('데이터 저장 중 클라이언트 오류 발생:', error);
        responseTextDiv.show().html('<strong class="csv-danger">데이터 저장 중 클라이언트 오류가 발생했습니다.</strong>');
        return false;
    }
}
$(document).ready(function() {
    //  화면이 준비되면 버튼/입력 이벤트를 연결한다.
    $('.file-input').on('change', readCsvFile);

    // // [초보 설명] "점수만 반영 배정" 버튼이 없으면 동적으로 추가한다.
    // if ($('#score-only-random-button').length === 0) {
    //     const $scoreOnlyButton = $('<button type="button" id="score-only-random-button">점수만 반영 배정</button>');
    //     $scoreOnlyButton.css({ 'margin-left': '8px' });
    //     const $randomButton = $('.random-button-label').first();
    //     if ($randomButton.length > 0) {
    //         $scoreOnlyButton.insertAfter($randomButton);
    //     } else {
    //         $('body').append($scoreOnlyButton);
    //     }
    // }

    $('#random-button-label').on('click', function() {
        if ($(this).hasClass('is-disabled')) {
            return;
        }
        if (confirm('참여자 랜덤 배정을 시작하시겠습니까?')) {
            const success = executeRandomAssignment({ ignoreLimits: false });
            if (success) {
                $(this).addClass('is-disabled').css({ 'opacity': '0.5', 'pointer-events': 'none' });
                $('#score-only-random-button').addClass('is-disabled').css({ 'opacity': '0.5', 'pointer-events': 'none' });
            }
        }
    });

    $('#score-only-random-button').on('click', function() {
        if ($(this).hasClass('is-disabled')) {
            return;
        }
        if (confirm('점수만 반영하여 배정을 시작하시겠습니까? (한도 무시)')) {
            const success = executeRandomAssignment({ ignoreLimits: true });
            if (success) {
                $(this).addClass('is-disabled').css({ 'opacity': '0.5', 'pointer-events': 'none' });
                $('#random-button-label').addClass('is-disabled').css({ 'opacity': '0.5', 'pointer-events': 'none' });
            }
        }
    });

    $('.reset-button-label').on('click', function() {
        if (confirm('배정 결과를 초기화하시겠습니까?')) {
            $('#csv-data .random-td-input').each(function() {
                if ($(this).attr('data-fixed') === 'true') {
                    return;
                }
                $(this).text('');
            });
            $('.response-text-div').hide().empty();
            refreshCounselorTableWithFixed();
            // 점수 표도 함께 초기화한다.
            assignmentScoreLog = [];
            clearAssignmentScoreTable();
            excludedPersonnel = {};
            $('#random-button-label').removeClass('is-disabled').css({ 'opacity': '', 'pointer-events': '' });
            $('#score-only-random-button').removeClass('is-disabled').css({ 'opacity': '', 'pointer-events': '' });
            console.log('배정 결과가 초기화되었습니다.');
        }
    });

    $('#helpButton').on('click', function() {
        $('.title-sub-header').toggle();
    });

    $('#save-button').on('click', function() {
        const save_button = $('#save-button');
        save_button.prop('disabled', true);
        executeDataSave();
        save_button.prop('disabled', false);
    });

    $('#varification-button').on('click', function() {
        executeDataValidation();
    });

    $('#excludeBtn').on('click', function() {
        excludeCounselor(excludedPersonnel);
    });

    $('#manual-add').on('click', function() {
        //  사용자가 직접 입력한 데이터를 테이블에 추가한다.
        const responseTextDiv = $('.response-text-div');
        responseTextDiv.empty();

        const manualData = {
            participantName: $('#manual-name').val().trim(),
            participationType: $('#manual-type').val(),
            gender: $('#manual-gender').val(),
            birthDate: $('#manual-birthdate').val(),
            recruitmentPath: $('#manual-recruitment').val(),
            hasCareer: $('#manual-career').val(),
            education: $('#manual-education-type').val().trim(),
            specificClass: $('#manual-special').val(),
            progressStage: $('#manual-stage').val().trim(),
            travelCounselor: $('#manual-travel').val().trim()
        };

        const validationRow = [
            '',
            '',
            manualData.participantName,
            manualData.participationType,
            manualData.gender,
            manualData.birthDate,
            manualData.recruitmentPath,
            manualData.hasCareer,
            manualData.education,
            manualData.specificClass,
            manualData.progressStage,
            manualData.travelCounselor
        ];

        if (manualData.travelCounselor && (!currentCounselor || !currentCounselor[manualData.travelCounselor])) {
            responseTextDiv.show();
            responseTextDiv.html(`<strong class="csv-danger">직접 입력 오류</strong><br>출장상담사 아이디가 상담사 목록에 없습니다. (입력값: ${manualData.travelCounselor})`);
            return;
        }

        const errors = validateRowData(validationRow, '직접입력');
        if (errors.length > 0) {
            responseTextDiv.show();
            responseTextDiv.html(`<strong class="csv-danger">직접 입력 오류</strong><br>${errors.join('<br>')}`);
            return;
        }

        appendManualEntryRow(manualData);
        responseTextDiv.show();
        responseTextDiv.html('<strong style="color: green;">직접 입력 데이터가 추가되었습니다.</strong>');

        $('#manual-name').val('');
        $('#manual-birthdate').val('');
        $('#manual-recruitment').prop('selectedIndex', 0);
        $('#manual-education-type').val('');
        $('#manual-stage').prop('selectedIndex', 0);
        $('#manual-travel').val('');

        refreshCounselorTableWithFixed();
    });
});
function excludeCounselor(excludedPersonnel) {
    //  "배정 제외" 체크박스를 읽어서 제외 목록을 저장한다.
    let $assignCounselorInput = $(".assign-counselor-input");

    $assignCounselorInput.each(function (){
        let $this = $(this);
        let excludedCounselor = $this.val();
        excludedPersonnel[excludedCounselor] = $this.is(":checked");
    })

    //  현재 제외 상태를 사용자에게 알림창으로 보여준다.
    let alertMassage = "다음과 같이 배정 제외 설정을 완료했습니다. \n"+Object.entries(excludedPersonnel).map(([key, value]) => key + ' : ' + value).join("\n");
    alert(alertMassage);
}
function applyDefaultValuesToRow(rowData, rowElement, rowNumber, messages) {
    //  모집경로/특정계층/진행단계가 비어있으면 기본값으로 채운다.
    const defaults = {
        recruitmentPath: "센터배정",
        specificClass: "X",
        progressStage: "IAP 전"
    };

    const hasMessages = Array.isArray(messages);

    if (!rowData[6] || rowData[6].trim() === "") {
        //  모집경로가 비어있으면 기본값으로 채운다.
        rowData[6] = defaults.recruitmentPath;
        if (rowElement) rowElement.find("td").eq(6).text(defaults.recruitmentPath);
        if (hasMessages) messages.push(`${rowNumber}행: 모집경로 미입력 → '${defaults.recruitmentPath}'으로 자동 설정`);
    }

    if (!rowData[9] || rowData[9].trim() === "") {
        //  특정계층여부가 비어있으면 기본값으로 채운다.
        rowData[9] = defaults.specificClass;
        if (rowElement) rowElement.find("td").eq(9).text(defaults.specificClass);
        if (hasMessages) messages.push(`${rowNumber}행: 특정계층여부 미입력 → '${defaults.specificClass}'로 자동 설정`);
    }

    if (!rowData[10] || rowData[10].trim() === "") {
        //  진행단계가 비어있으면 기본값으로 채운다.
        rowData[10] = defaults.progressStage;
        if (rowElement) rowElement.find("td").eq(10).text(defaults.progressStage);
        if (hasMessages) messages.push(`${rowNumber}행: 진행단계 미입력 → '${defaults.progressStage}'으로 자동 설정`);
    }

    if (!rowData[7] || rowData[7].trim() === "") {
        // [초보 설명] 경력유무가 비어있으면 기본값(0)으로 채운다.
        rowData[7] = "0";
        if (rowElement) rowElement.find("td").eq(7).text("0");
        if (hasMessages) messages.push(`${rowNumber}행: 경력유무 미입력 → '0'으로 자동 설정`);
    }
}

function resetRandomAssignments() {
    // 랜덤 배정 결과만 지우고, 고정 배정은 그대로 둔다.
    $('#csv-data .random-td-input').each(function() {
        if ($(this).attr('data-fixed') === 'true') {
            return;
        }
        $(this).text('');
    });
    // 점수 표도 함께 초기화한다.
    assignmentScoreLog = [];
    clearAssignmentScoreTable();
    refreshCounselorTableWithFixed();
}

