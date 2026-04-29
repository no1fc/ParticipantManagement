$(document).ready(function () {
    // let APP_URL = window.location.href; // 현재 페이지 URL
    // let email = "namsd@jobmoa.com"; // 수신 이메일
    // let subject = APP_URL + " 해당 페이지에 대해 문의 드립니다."; // 이메일 제목
    // let body = "로그인 정보";
    //
    // // 줄바꿈 추가
    // body += "\n"; // 줄바꿈
    // body += "ID : ${JOBMOA_LOGIN_DATA.memberUserID}"; // 사용자 ID
    // body += "\n\n"; // 줄바꿈
    // body += "지점 : ${JOBMOA_LOGIN_DATA.memberBranch}"; // 사용자 지점
    // body += "\n\n"; // 줄바꿈
    // body += "문의 사항"; // 추가 설명
    // body += "\n"; // 줄바꿈

    // 이메일 태그에 동적으로 href 속성 설정
    let emailTag = $("#email-a-tag");

    // mailto 링크 생성
    // emailTag.attr("href", "mailto:" + email + "?subject=" + encodeURIComponent(subject) + "&body=" + encodeURIComponent(body));
    emailTag.attr("href", "https://jobmoa.daouoffice.com/app/mail?work=write&toAddr=%EB%82%A8%EC%83%81%EB%8F%84%3Cnamsd%40jobmoa.com%3E");


    // 요소 가져오기
    const circleButton = $("#circleButton"); // 동그란 버튼
    const iframeModal = $("#iframeModal"); // iframe 모달
    const closeButton = $("#closeButton"); // 닫기 버튼

    // 동그란 버튼 클릭 시 iframe 모달 표시
    // circleButton.addEventListener("click", function () {
    //     iframeModal.style.display = "flex"; // 모달 보이기
    // });
    circleButton.on("click",function(){
        iframeModal.fadeIn(); // 모달 보이기
    });

    // 닫기 버튼 클릭 시 iframe 모달 숨기기
    // closeButton.addEventListener("click", function () {
    //     iframeModal.style.display = "none"; // 모달 숨기기
    // });
    closeButton.on("click",function(){
        iframeModal.fadeOut(); // 모달 숨기기
    });

    // 모달 배경 클릭 시에도 모달 숨기기
    // iframeModal.addEventListener("click", function (event) {
    //     if (event.target === iframeModal) { // 배경을 클릭했을 때만 동작
    //         iframeModal.style.display = "none"; // 모달 숨기기
    //     }
    // });
    iframeModal.on("click", function(event) {
        // 클릭된 요소가 모달 전체 영역일 경우에만 닫기
        if ($(event.target).is("#iframeModal")) {
            $("#iframeModal").fadeOut();
        }
    });
});