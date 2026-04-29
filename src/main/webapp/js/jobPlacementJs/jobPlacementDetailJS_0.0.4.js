/**
 * TextArea로 저장된 값에 대한 내용중 Enter 키를 HTML 코드 <br> Code로 전환
 */
function lineFeedChange(textData){
    return textData.replace(/\n/g, "<br>");
}

function locationBack(page){
    let href = 'placementList'+searchHref(page);

    // Starbucks 전용 url로 접속했다면 다시 해당 리스트로 이동
    const url = new URL(window.location.href);
    const urlParams = url.searchParams;
    console.log(urlParams.get('Starbucks'));
    if(urlParams.get('Starbucks') == 'true'){
        urlParams.delete('jobNumber');
        urlParams.delete('Starbucks');
        window.location.href = window.location.origin + '/Starbucks?' + urlParams.toString();
        return;
    }

    window.location.href = href.lastIndexOf('jobNumber') > 0 ? href.substring(0,href.lastIndexOf('jobNumber')-1) : href;
}

/**
 * 희망직무 다중 행 관리 함수
 */
function initDesiredJobSelects() {
    $('#desiredJobEditList .desired-job-row').each(function() {
        const $row = $(this);
        const $large = $row.find('.jobCategoryLargeSelect');
        const $mid = $row.find('.jobCategoryMidSelect');
        const selectedLarge = $large.data('selected') || '';
        const selectedMid = $mid.data('selected') || '';

        populateLargeSelect($large, selectedLarge);
        populateMidSelect($mid, selectedLarge, selectedMid);

        // 대분류 변경 시 중분류 갱신
        $large.off('change').on('change', function() {
            populateMidSelect($mid, $(this).val(), '');
        });
    });
}

function populateLargeSelect($select, selected) {
    $select.empty();
    $select.append('<option value="">카테고리 선택</option>');
    window.JOB_CATEGORY_LARGE.forEach(function(item) {
        const isSelected = (selected === item.value) ? ' selected' : '';
        $select.append('<option value="' + item.value + '"' + isSelected + '>' + item.text + '</option>');
    });
    $select.append('<option value="기타">기타</option>');
}

function populateMidSelect($select, largeValue, selected) {
    $select.empty();
    $select.append('<option value="">카테고리 선택</option>');
    const midItems = window.JOB_CATEGORY_MID[largeValue] || [];
    midItems.forEach(function(item) {
        const isSelected = (selected === item.value) ? ' selected' : '';
        $select.append('<option value="' + item.value + '"' + isSelected + '>' + item.text + '</option>');
    });
    if (midItems.length > 0) {
        $select.append('<option value="기타">기타</option>');
    }
    $select.prop('disabled', midItems.length === 0);
}

function addDesiredJobRow() {
    const $list = $('#desiredJobEditList');
    const nextRank = $list.find('.desired-job-row').length + 1;
    const rowHtml =
        '<div class="desired-job-row d-flex align-items-center mb-2" data-rank="' + nextRank + '">' +
            '<span class="badge bg-secondary me-2">' + nextRank + '순위</span>' +
            '<select class="form-control form-control-sm jobCategoryLargeSelect me-1"><option value="">카테고리 선택</option></select>' +
            '<i class="bi bi-caret-right-fill mx-1"></i>' +
            '<select class="form-control form-control-sm jobCategoryMidSelect me-1"><option value="">카테고리 선택</option></select>' +
            '<i class="bi bi-caret-right-fill mx-1"></i>' +
            '<input type="text" class="form-control form-control-sm desiredJobInput me-1" placeholder="희망직무"/>' +
            '<button type="button" class="btn btn-outline-danger btn-sm btn-remove-job"><i class="bi bi-x-lg"></i></button>' +
        '</div>';
    const $row = $(rowHtml);
    $list.append($row);

    const $large = $row.find('.jobCategoryLargeSelect');
    const $mid = $row.find('.jobCategoryMidSelect');
    populateLargeSelect($large, '');
    populateMidSelect($mid, '', '');
    $large.on('change', function() {
        populateMidSelect($mid, $(this).val(), '');
    });
}

function removeDesiredJobRow($btn) {
    $btn.closest('.desired-job-row').remove();
    updateDesiredJobRanks();
}

function updateDesiredJobRanks() {
    $('#desiredJobEditList .desired-job-row').each(function(index) {
        $(this).attr('data-rank', index + 1);
        $(this).find('.badge').text((index + 1) + '순위');
    });
}

function collectDesiredJobList() {
    const list = [];
    $('#desiredJobEditList .desired-job-row').each(function(index) {
        const $row = $(this);
        const large = $row.find('.jobCategoryLargeSelect').val() || '';
        const mid = $row.find('.jobCategoryMidSelect').val() || '';
        const job = $row.find('.desiredJobInput').val() || '';
        list.push({
            desiredJobRank: index + 1,
            jobCategoryLarge: large,
            jobCategoryMid: mid,
            desiredJob: job
        });
    });
    return list;
}

function updateMergedDesiredJobDisplay() {
    const list = collectDesiredJobList();
    let html = '';
    list.forEach(function(job, index) {
        let line = '';
        if (job.jobCategoryLarge) line += job.jobCategoryLarge + ' <i class="bi bi-caret-right-fill"></i> ';
        if (job.jobCategoryMid) line += job.jobCategoryMid + ' <i class="bi bi-caret-right-fill"></i> ';
        line += job.desiredJob;
        html += line;
        if (index < list.length - 1) html += '\n';
    });
    $('#mergedDesiredJob').html(html);
}

$(document).ready(function() {
    let updateFlag = false;
    const updateBtn = $('#updateBtn');
    let originalData = {};

    // 희망직무 추가 버튼
    $(document).on('click', '#addDesiredJobBtn', function() {
        addDesiredJobRow();
    });

    // 희망직무 삭제 버튼
    $(document).on('click', '.btn-remove-job', function() {
        removeDesiredJobRow($(this));
    });

    let keywordDiv = $('#keyword-div');

    updateBtn.on('click', function() {
        if (!updateFlag) {
            updateFlag = true;
            convertToEditMode();
            default_datepicker();
            updateBtn.html('<i class="bi bi-check-square"></i> 저장');
            updateBtn.removeClass('btn-info').addClass('btn-success');
        } else {
            updateFlag = false;
            convertToReadMode();
            updateBtn.html('<i class="bi bi-pencil-square"></i> 수정');
            updateBtn.removeClass('btn-success').addClass('btn-info');
            saveUpdatedData().then(function(result) {
                console.log("Ascync Success [" +result+"]");
            })
            .catch(function(error) {
                alert("Async Error [" +error+"]");
            })
        }
    });

    function convertToEditMode() {
        // 읽기 모드 병합 텍스트 숨김, 수정 모드 카테고리 선택 표시
        $('#mergedDesiredJobSection').hide();
        $('#jobCategoryEditSection').show();

        // 희망직무 행들의 select 옵션 초기화
        initDesiredJobSelects();

        $('.readonly-section .readonly-item').each(function() {
            const $item = $(this);
            const $value = $item.find('.readonly-value');
            const $pre = $item.find('.readonly-pre');

            const fieldId = $value.attr('id') || $pre.attr('id');

            if ($value.length > 0) {
                originalData[fieldId] = $value.text();
            } else if ($pre.length > 0) {
                originalData[fieldId] = $pre.text();
            }

            if ($value.length > 0) {
                const currentValue = $value.text();
                let inputHtml = '';
                let inputName = '';

                // 각 필드별로 name 속성 설정
                switch(fieldId) {
                    case 'detailName':
                        inputName = 'participant';
                        break;
                    case 'detailAge':
                        inputName = 'age';
                        break;
                    case 'detailLocation':
                        inputName = 'address';
                        break;
                    case 'detailSalary':
                        inputName = 'desiredSalary';
                        break;
                    case 'schoolName':
                        inputName = 'schoolName';
                        break;
                    case 'major':
                        inputName = 'major';
                        break;
                    case 'detailCertificates':
                        inputName = 'certificate';
                        break;
                    case 'detailExperience':
                        inputName = 'career';
                        break;
                    case 'detailGender':
                        inputName = 'gender';
                        break;
                    default:
                        inputName = fieldId;
                }

                // 입력 타입 결정
                if (fieldId === 'detailAge') {
                    let detailBirthDate = $('#detailBirthDate').val();
                    inputHtml = '<input type="text" class="form-control editable-input datepicker_on" id="' + fieldId + '" name="' + inputName + '" value="' + detailBirthDate + '">';
                } else if (fieldId === 'detailSalary') {
                    const salaryValue = currentValue.replace(/[^0-9]/g, '');
                    inputHtml = '<input type="number" class="form-control editable-input" id="' + fieldId + '" name="' + inputName + '" value="' + salaryValue + '" min="0">';
                } else if(fieldId === 'detailGender'){
                    let flag = (currentValue === '남');
                    inputHtml = '<select class="form-control editable-input editable-select" id="'+fieldId+'" name="'+inputName+'">' +
                    '<option value="남" '+(flag?'selected':'')+' >남</option>' +
                    '<option value="여" '+(!flag?'selected':'')+' >여</option>' +
                    '</select>'
                }
                else if (fieldId === 'detailLocation') {
                    inputHtml = '<input type="text" class="form-control editable-input" id="' + fieldId + '" name="' + inputName + '" value="' + currentValue + '" readonly>';
                }
                else {
                    inputHtml = '<input type="text" class="form-control editable-input" id="' + fieldId + '" name="' + inputName + '" value="' + currentValue + '">';
                }

                $value.replaceWith(inputHtml);
            }

            if ($pre.length > 0) {
                const currentValue = $pre.text();
                const inputName = fieldId === 'placementDetail' ? 'placementDetail' : fieldId;
                const textareaHtml = '<textarea class="form-control editable-textarea" id="' + fieldId + '" name="' + inputName + '" rows="5">' + currentValue + '</textarea>';
                $pre.replaceWith(textareaHtml);
            }
        });
    }

    function convertToReadMode() {
        // 수정 모드 카테고리 선택 숨김, 읽기 모드 병합 텍스트 표시
        $('#jobCategoryEditSection').hide();
        $('#mergedDesiredJobSection').show();

        // 희망직무 읽기 모드 텍스트 갱신
        updateMergedDesiredJobDisplay();

        $('.readonly-section .readonly-item').each(function() {
            const $item = $(this);
            const $input = $item.find('.editable-input');
            const $textarea = $item.find('.editable-textarea');

            if ($input.length > 0) {
                let fieldId = $input.attr('id');
                let newValue = $input.val();

                // 특정 필드의 값 처리
                if (fieldId === 'detailAge') {
                } else if (fieldId === 'detailCertificates' && newValue === '') {
                    newValue = '없음';
                } else if (fieldId === 'detailExperience' && newValue === '') {
                    newValue = '신입';
                }

                const spanHtml = '<span class="readonly-value" id="' + fieldId + '">' + newValue + '</span>';
                $input.replaceWith(spanHtml);
            }

            if ($textarea.length > 0) {
                const fieldId = $textarea.attr('id');
                const newValue = $textarea.val();
                const preHtml = '<pre class="readonly-pre" id="' + fieldId + '">' + newValue + '</pre>';
                $textarea.replaceWith(preHtml);
            }
        });
    }

    function cancelEdit() {
        if (updateFlag) {
            updateFlag = false;

            // 수정 모드 카테고리 선택 숨김, 읽기 모드 병합 텍스트 표시
            $('#jobCategoryEditSection').hide();
            $('#mergedDesiredJobSection').show();

            $('.readonly-section .readonly-item').each(function() {
                const $item = $(this);
                const $input = $item.find('.editable-input');
                const $textarea = $item.find('.editable-textarea');

                if ($input.length > 0) {
                    let fieldId = $input.attr('id');
                    let originalValue = originalData[fieldId] || '';
                    const spanHtml = '<span class="readonly-value" id="' + fieldId + '">' + originalValue + '</span>';
                    $input.replaceWith(spanHtml);
                }

                if ($textarea.length > 0) {
                    const fieldId = $textarea.attr('id');
                    const originalValue = originalData[fieldId] || '';
                    const preHtml = '<pre class="readonly-pre" id="' + fieldId + '">' + originalValue + '</pre>';
                    $textarea.replaceWith(preHtml);
                }
            });

            updateBtn.html('<i class="bi bi-pencil-square"></i> 수정');
            updateBtn.removeClass('btn-success').addClass('btn-info');
        }
    }

    $(document).keydown(function(e) {
        if (e.keyCode === 27 && updateFlag) {
            cancelEdit();
        }
    });

    async function saveUpdatedData() {
        const updatedData = {};
        const jobNumber = $('#selectedParticipantId').text();
        const counselorId = $('#detailCounselorId').val();
        const detailBirthDate = $('#detailBirthDate').val();

        $('.readonly-section .readonly-value, .readonly-section .readonly-pre').each(function () {
            const $element = $(this);
            const fieldId = $element.attr('id');
            const value = $element.text();

            // 필드 ID를 서버에서 예상하는 필드명으로 매핑
            switch (fieldId) {
                case 'detailName':
                    updatedData.participant = value === '' ? originalData.birthDate : value;
                    break;
                case 'detailAge':
                    updatedData.birthDate = value === '' ? detailBirthDate : value;
                    break;
                case 'detailGender':
                    updatedData.gender = value;
                    break;
                case 'detailLocation':
                    updatedData.address = value === '' ? originalData.address : value;
                    break;
                case 'detailSalary':
                    updatedData.desiredSalary = Math.floor((value/12));
                    break;
                case 'schoolName':
                    updatedData.schoolName = value;
                    break;
                case 'major':
                    updatedData.major = value;
                    break;
                case 'detailCertificates':
                    updatedData.certificate = value === '없음' ? '' : value;
                    break;
                case 'detailExperience':
                    updatedData.career = value === '신입' ? '' : value;
                    break;
                case 'placementDetail':
                    updatedData.placementDetail = value;
                    break;
            }
        });

        // 희망직무 배열 수집
        updatedData.desiredJobList = collectDesiredJobList();

        updatedData.jobNumber = jobNumber;
        updatedData.counselorId = counselorId;

        console.log('Updated data:', updatedData);

        return await fetch('jobPlacementAsync', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedData)
        })
        .then(response => {return response.json();})
        .catch(error => {return error;})
    }

    //만나이 생성 함수
    function calculateAge(birthdate) {
        const today = new Date();
        const birthDate = new Date(birthdate);
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDifference = today.getMonth() - birthDate.getMonth();

        if (monthDifference < 0 || (monthDifference === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    }

    function keywordDivChange(){
        const keyword = keywordDiv.text();
        const keywords = keyword.split(',');
        // console.log(keywords);
        keywordDiv.empty();
        keywords.forEach(element => {
            keywordDiv.append('<div>'+element+'</div>');
        })
    }

    //시작시 적용
    keywordDivChange();

    /* 이력서 요청 모달창 함수 시작 */
    const $resumeEmailRequestButton = $('#resumeEmailRequestButton'); // 이메일 요청 버튼
    const $resumeRequestForm = $('#resumeRequestForm'); // 이메일 요청 form
    const $companyName = $('#companyName'); // 기업명
    const $managerName = $('#managerName'); // 담당자명
    const $email = $('#email'); // 이메일
    const $emergencyContact = $('#emergencyContact'); // 비상연락처
    const $otherRequests = $('#otherRequests'); // 기타사항
    const checkboxNames = [
        'personalInformationAgreeMarketing',
        'personalInformationAgreeCompany',
        'personalInformationAgreeManager',
    ];

    // 백단으로 저장 요청 전달
    $resumeEmailRequestButton.click(function() {
        const resumeDataArray = {};

        //otherRequests 기타사항 XLS 공격방어를 위해 작성
        const XLSRegex = /(<script\b[^>]*>([\s\S]*?)<\/script>)/gm

        // 폼 요소(input, textarea) 값을 배열에 추가
        $resumeRequestForm.find('input, textarea').each(function () {
            const $element = $(this);
            const name = changeInputName($element.attr('name'));

            if (!name) {
                return; // 이름이 없는 요소는 건너뜁니다.
            }

            let value = checkboxNames.includes(name) ? $element.is(':checked') : $element.val();

            if (typeof value === 'string') {
                value = value.replace(XLSRegex, '');
            }

            console.log(`inputVal: ${value}`);
            resumeDataArray[name] = value;
        });

        const companyName = $companyName.val().trim();
        const managerName = $managerName.val().trim();
        const email = $email.val().trim();
        const emergencyContact = $emergencyContact.val().trim();

        const isCheckValue = companyName === '' || managerName === '' || email === '' || emergencyContact === '';
        if(isCheckValue){
            if (companyName === ''){
                $companyName.focus();
                $companyName.css('border-color', 'red');
                $companyName.css('border-width', '2px');
                $companyName.css('border-style', 'solid');
                $companyName.css('border-radius', '5px');
                $companyName.css('background-color', '#f8d7da');
                $companyName.css('color', '#721c24');
                $companyName.css('font-weight', 'bold');
            }
            else if (managerName === ''){
                $managerName.focus();
                $managerName.css('border-color', 'red');
                $managerName.css('border-width', '2px');
                $managerName.css('border-style', 'solid');
                $managerName.css('border-radius', '5px');
                $managerName.css('background-color', '#f8d7da');
                $managerName.css('color', '#721c24');
                $managerName.css('font-weight', 'bold');
            }
            else if (email === ''){
                $email.focus();
                $email.css('border-color', 'red');
                $email.css('border-width', '2px');
                $email.css('border-style', 'solid');
                $email.css('border-radius', '5px');
                $email.css('background-color', '#f8d7da');
                $email.css('color', '#721c24');
                $email.css('font-weight', 'bold');
            }
            else if (emergencyContact === ''){
                $emergencyContact.focus();
                $emergencyContact.css('border-color', 'red');
                $emergencyContact.css('border-width', '2px');
                $emergencyContact.css('border-style', 'solid');
                $emergencyContact.css('border-radius', '5px');
                $emergencyContact.css('background-color', '#f8d7da');
                $emergencyContact.css('color', '#721c24');
                $emergencyContact.css('font-weight', 'bold');
            }
            return;
        }
        else{
            $companyName.removeAttr('style');
            $managerName.removeAttr('style');
            $email.removeAttr('style');
            $emergencyContact.removeAttr('style');
        }

        //emergencyContact(비상연락처) 전화번호 정규식
        const emergencyContactRegex = /^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$/;
        //이메일 정규식
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

        //emergencyContact(비상연락처) 전화번호 형식 확인
        if(!emergencyContactRegex.test(emergencyContact)){
            alert("전화번호 형식으로 작성해주세요.")
            return;
        }
        //email 이메일 형식으로 확인
        else if(!emailRegex.test(email)){
            alert("이메일 형식으로 작성해주세요.")
            return;
        }


        sendResumeRequest(resumeDataArray)
            .then(r => {
                const response = r.json();
                console.log("sendResumeRequest(resumeDataArray): "+r)
                response.then(data => {
                    if(data.statusData === 'success'){
                        console.log(data.message)
                        alertDefaultSuccess(data.message);
                        $('#resumeRequestModal').modal('hide');
                        $resumeRequestForm.trigger('reset');
                        $resumeEmailRequestButton.attr('disabled', true);
                    }
                    else if(data.statusData === 'error'){
                        console.log(data.message)
                        alertDefaultError("이력서 발송에 실패했습니다.");
                    }
                    else{
                        console.log(data.message)
                        alertDefaultError("서버 오류로 이력서 발송에 실패했습니다.");
                    }
                })
            })
            .catch(e => {
                console.log(e)
                alertDefaultError("서버 오류로 이력서 발송에 실패했습니다. \n하단 상담사 이메일로 요청 부탁드립니다.");
            });
    })

    function changeInputName(inputName){
        if(inputName === 'companyName'){
            return "companyName"
        }
        else if(inputName === 'managerName'){
            return "contactName"
        }
        else if(inputName === 'email'){
            return "contactEmail"
        }
        else if(inputName === 'emergencyContact'){
            return "contactPhone"
        }
        else if(inputName === 'otherRequests'){
            return "contactOther"
        }
        //마케팅 개인정보 사용 동의
        else if(inputName === 'personalInformationAgreeMarketing'){
            return "marketingConsent"
        }
        //기업 담당자 개인정보 동의
        else if(inputName === 'personalInformationAgreeManager'){
            return "contactPrivacy"
        }
        //기업 담당자가 확인해야할 개인정보 처리 동의
        else if(inputName === 'personalInformationAgreeCompany'){
            return "companyPrivacy"
        }
        else{
            return inputName
        }
    }

    async function sendResumeRequest(resumeDataArray) {
        console.log(resumeDataArray);
        // return resumeDataArray;
        return await fetch('/jobPlacement/resumeRequest', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json; charset=UTF-8'
            },
            body: JSON.stringify(resumeDataArray)
        })
    }

    // 이력서 요청 개인정보 모달창 표시
    $('#resume-request-button').click(function() {
        const personalInformation = new bootstrap.Modal($('#personalInformationModal'));
        personalInformation.show();
    })



    // 개인정보 전체 동의
    $('#personal-information-agree').click(function() {
        console.log("personal-information-agree Start");

        // 공통 클래스를 사용하여 모든 동의 체크박스를 한번에 선택합니다.
        const $agreeCheckboxes = $('.personalInformationAgree');

        // 모든 체크박스가 이미 선택되어 있는지 확인합니다.
        const allChecked = $agreeCheckboxes.length === $agreeCheckboxes.filter(':checked').length;

        // 새로운 체크 상태를 결정합니다. (모두 선택되었다면 -> 해제, 그렇지 않다면 -> 모두 선택)
        const newCheckedState = !allChecked;

        // 모든 체크박스에 새로운 상태를 일괄 적용합니다.
        $agreeCheckboxes.prop('checked', newCheckedState);

        console.log("personal-information-agree End");
    });

    // 이력서 요청 모달창 표시
    $('#btn-next-modal').click(function() {
        const resumeRequestModal = new bootstrap.Modal($('#resumeRequestModal'));
        const $personalInformation = $('#personalInformationModal');
        const $personalInformationAgreeCompany = $('#personalInformationAgreeCompany')
        const $personalInformationAgreeManager = $('#personalInformationAgreeManager')

        if($personalInformationAgreeCompany.is(':checked') === false || $personalInformationAgreeManager.is(':checked') === false){
            alert("필수 동의를 확인해주세요.")
            return;
        }

        $personalInformation.modal('hide');
        resumeRequestModal.show();
    });

    //modal 해제 후 input 값 제거
    /*$('#personalInformationModal').on('hidden.bs.modal', function (e) {
        modalHidden()
    })*/
    $('#resumeRequestModal').on('hidden.bs.modal', function (e) {
        modalHidden()
    })

    function modalHidden(){
        // const $resumeRequestModalLabel = $('#resumeRequestModalLabel');
        const $agreeCheckboxes = $('.personalInformationAgree');

        // $resumeRequestModalLabel.val('');
        $companyName.val('');
        $managerName.val('');
        $email.val('');
        $emergencyContact.val('');
        $otherRequests.val('');
        $agreeCheckboxes.prop('checked', false);

    }

    /* 이력서 요청 모달창 함수 끝 */

});