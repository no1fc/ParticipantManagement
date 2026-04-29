$(document).ready(function() {
    let selectedParticipants = new Set();
    let transferredParticipants = new Set();
    const $sourceCounselor = $('#sourceCounselor');
    const $targetCounselor = $('#targetCounselor');

    // 상담사 선택 시 참여자 목록 로드
    $sourceCounselor.change(function() {
        const counselorId = $(this).val();
        if (counselorId) {
            loadParticipants(counselorId);
            updateButtonStates();
        }
        else {
            $('#participantList').empty();
            $('#transferredList').empty();
        }
    });

    // 상담사 선택 시 참여자 목록 로드
    $targetCounselor.change(function() {
        //const counselorId = $(this).val();
        $('#transferredList').empty();
    });

    function loadParticipants(counselorId, pkArray) {

        $.ajax({
            url: 'transferGetAjax.login',
            method: 'GET',
            dataType: 'json', // 중요: 응답을 JSON으로 파싱
            data: {
                participantUserid: counselorId,
                participantBranch: memberBranch,
                participantIDs: pkArray ? pkArray : ''
            }
        }).done(function(data) {
            const list = $('#participantList');
            list.empty();

            // 여기서 data는 이미 배열(객체)이므로 JSON.parse 제거
            data.forEach(function(participant) {
                const appendHTML =
                    '<div class="list-group-item participant-item">' +
                    '<input type="checkbox" class="form-check-input participant-check" id="'+participant.jobno+'" value="'+participant.jobno+'">' +
                    '<label class="form-check-label ms-2 participant-label" for="'+participant.jobno+'">' +
                    '<div class="d-flex justify-content-between align-items-center w-100">' +
                    '<span class="participant-jobno">'+ participant.jobno +'</span>' +
                    '<span class="participant-name">'+ participant.particName +'</span>' +
                    '<div class="participant-info">' +
                    '<span class="info-item">'+ participant.dob +'</span>' +
                    '<span class="info-item">'+ participant.gender +'</span>' +
                    '</div>' +
                    '</div>' +
                    '</label>' +
                    '</div>';
                list.append(appendHTML);
            });
        }).fail(function(jqXHR, textStatus, errorThrown) {
            console.error('AJAX 실패 상태:', textStatus);
            console.error('HTTP 상태:', jqXHR.status);
            console.error('오류 메시지:', errorThrown);
            console.error('응답 텍스트:', jqXHR.responseText);
        });
    }

    function loadTransferredParticipants(counselorId, pkArray) {
        $.get('transferGetAjax.login', {
            participantUserid: counselorId,
            participantBranch: memberBranch,
            participantIDs: pkArray ? pkArray : ''
        })
            .done(function(data) {
                const list = $('#transferredList');
                list.empty();
                // JSON.parse(data).forEach(participant => {
                data.forEach(participant => {
                    let appendHTML = '<div class="list-group-item participant-item">' +
                        '<div class="d-flex justify-content-between align-items-center w-100">'+
                        '<span class="participant-jobno">'+ participant.jobno +'</span>'+
                        '<span class="participant-name">'+ participant.particName +'</span>'+
                        '<div class="participant-info">'+
                        '<span class="info-item">'+ participant.dob +'</span>'+
                        '<span class="info-item">'+ participant.gender +'</span>'+
                        '</div></div></label></div>';

                    list.append(appendHTML);
                });
            })
            .fail(function(jqXHR, textStatus, errorThrown) {
                // 상세한 오류 정보 로깅
                console.error('AJAX 실패 상태:', textStatus);
                console.error('HTTP 상태:', jqXHR.status);
                console.error('오류 메시지:', errorThrown);
                console.error('응답 텍스트:', jqXHR.responseText);
            });
    }

    // 전체 선택 체크박스 이벤트
    const $selectAllSource = $('#selectAllSource');
    $selectAllSource.change(function() {
        changeAllParticipants();
    });

    //전체 선택 체크박스 이벤트
    function changeAllParticipants() {
        const isChecked = $selectAllSource.prop('checked');
        $('.participant-check').prop('checked', isChecked);
        updateSelectedParticipants();
        updateButtonStates();
    }

    // 참여자 선택 시 이벤트
    $(document).on('change', '.participant-check, #targetCounselor', function() {
        updateSelectedParticipants();
        updateButtonStates();
    });

    // 선택 이전 버튼 클릭
    $('#transferSelected').click(function() {
        alertConfirmWarning('선택한 참여자를 이전합니다.', '이전 후 복구는 불가능합니다.', '이전', '취소').then(function(result) {
            if (result) {
                transferParticipants(Array.from(selectedParticipants));
            }
            else {
                $selectAllSource.prop('checked', false);
            }
        });
    });

    // 전체 이전 버튼 클릭
    $('#transferAll').click(function() {

        alertConfirmWarning('참여자 전체를 이전합니다.', '이전 후 복구는 불가능합니다.', '이전', '취소').then(function(result) {
            if (result) {
                $selectAllSource.prop('checked', true);
                changeAllParticipants();
                console.log(Array.from(selectedParticipants));
                transferParticipants(Array.from(transferredParticipants));
            }
            else {
                $selectAllSource.prop('checked', false);
            }
        });
    });

    function updateSelectedParticipants() {
        selectedParticipants.clear();
        transferredParticipants.clear();
        $('.participant-check:checked').each(function() {
            selectedParticipants.add($(this).val());
            transferredParticipants.add($(this).val());
        });
    }

    function updateButtonStates() {
        const sourceSelected = $sourceCounselor.val();
        const targetSelected = $targetCounselor.val();
        const hasSelectedParticipants = selectedParticipants.size > 0;
        const hasSourceSelectedAndTargetSelected = sourceSelected !== '' && sourceSelected !== null && targetSelected !== '' && targetSelected !== null;

        $('#transferSelected').prop('disabled',!hasSourceSelectedAndTargetSelected || !hasSelectedParticipants);
        $('#transferAll').prop('disabled',!hasSourceSelectedAndTargetSelected);
    }

    function transferParticipants(participantIds) {
        // 이전 API 호출 및 처리
        $.ajax({
            url: 'transferPostAjax.login',
            method: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
                sourceCounselorID: $sourceCounselor.val(),
                targetCounselorID: $targetCounselor.val(),
                participantBranch: memberBranch,
                participantIDs: participantIds
            }),
            success: function(response) {
                console.log("이관 성공 여부 : ["+response+"]");
                if (response) {
                    alert('데이터 이전이 완료되었습니다.');
                    loadParticipants($sourceCounselor.val());
                    console.log(participantIds);
                    loadTransferredParticipants($targetCounselor.val(),participantIds);

                    //검색 초기화
                    clearSearch();
                }
                else{
                    alert('데이터 이전에 실패했습니다.')
                }
            },
            error: function(xhr) {
                alert('데이터 이전 중 오류가 발생했습니다.');
            }

        });
    }

    let searchTimeout;
    $('#participantSearch').on('keyup', function() {
        clearTimeout(searchTimeout);
        const searchTerm = $(this).val().toLowerCase().trim();

        searchTimeout = setTimeout(function() {
            let visibleCount = 0;

            $('.participant-item').each(function() {
                const $item = $(this);

                const isChecked = $item.find('.participant-check').is(':checked');

                // 체크된 항목은 항상 표시
                if (isChecked) {
                    $item.show();
                    visibleCount++;
                    return;
                }

                // 구직번호, 이름, 생년월일, 성별 텍스트 추출
                const jobNo = $item.find('.participant-jobno').text();
                const name = $item.find('.participant-name').text();
                const info = $item.find('.participant-info').text();
                const searchText = (jobNo + ' ' + name + ' ' + info).toLowerCase();

                // 검색어 매칭
                const isMatch = searchText.includes(searchTerm);
                $item.toggle(isMatch || !searchTerm);

                if (isMatch || !searchTerm) visibleCount++;
            });

            // 결과 표시
            if (searchTerm) {
                $('#searchResult').text(`${visibleCount}명 검색됨`);
            } else {
                $('#searchResult').text('');
            }
        }, 150); // 250ms 디바운싱
    });

    // 초기화 버튼 (선택 사항)
    $('#clearSearch').on('click', clearSearch);

    function clearSearch(){
        $('#participantSearch').val('');
        $('.participant-item').show();
        $('#searchResult').text('');
    }

});