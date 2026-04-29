$('document').ready(function () {
    const userInput = $("#userInput"); // 사용자 입력 요소
    const charCount = $("#charCount"); // 글자 수 출력 요소
    const chatLoading = $("#chatLoading"); // 답변 생성 내용 요소
    const userInputLimits = $("#userInputLimits"); // 사용자 입력 값 제한 요소

    chatLoading.hide();
    userInputLimits.hide();
    //사용자 글자 상한 제한
    inputLimits();

    $("#sendMassage").on("click", function (){
        sendMessage(chatLoading)
    });

    userInput.on("keypress", function (e) {
        if (e.which === 13) {
            if(e.shiftKey){
            }
            else{
                sendMessage(chatLoading);
            }
        }
    });

    //chatgpt 비동기 함수
    async function sendMessage() {
        //어시스턴스 아이디와 쓰레드 아이디를 가져옵니다.
        const chatbotThreadId = $("#chatbotThreadId").val();
        const chatbotAssistantsId = $("#chatbotAssistantsId").val();
        console.log(chatbotThreadId);
        console.log(chatbotAssistantsId);

        /* chatGPT가 대화내역을 산출할때 */
        /* 내용을 출력중이라는 내용을 보여준다. */
        chatLoading.show();
        userInputLimits.hide();

        //사용자 입력값에 줄바꿈이 있다면 \\n 값으로 전환한다.
        const userMessage = userInput.val().replace(/\n/g, "\\n");
        console.log(userMessage);
        const chatLog = $("#chatLog");
        let botResponse;

        // 사용자 입력 유효성 검사
        if (!userMessage.trim()) {
            alert("메시지를 입력해주세요!");
            chatLoading.hide();
            return;
        }
        else if(userMessage.length > 1000){
            userInputLimits.show();
            return;
        }
        // 코드가 멈추는 것을 방지하기 위해 try catch finally 를 사용
        try{
            const response = await fetch("/chatBot/api", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({userMessage: userMessage,threadId: chatbotThreadId,assistantsId: chatbotAssistantsId})
            });
            //전달받은 내용 로그
            // console.log(response);
            // console.log(response.status);

            //비동기로 전달받은 값을 html로 전환
            //botResponse = JSON.parse(await response);// marked.js로 마크다운을 HTML로 변환
            botResponse = await response.json();// marked.js로 마크다운을 HTML로 변환
            // botResponse = botResponse.content //기본 형식
            //전환된 텍스트를 로그로 확인
            // console.log(botResponse);
            // console.log(marked.parse(botResponse));
        }
        catch (e) {
            botResponse = "chat gpt가 답변을 생성하지 못했습니다.";
        }
        finally {
            //html 태그를 등록
            chatLog.append("<div class='user'><p>"+userMessage.replace(/\\n/g, "<br>")+"</p></div><br>");
            chatLog.append("<div class='chatBot'>"+marked.parse(botResponse)+"</div><br>");
            userInput.val("");

            //스크롤 하단 이동
            chatLog.scrollTop(chatLog[0].scrollHeight);
            //글자 수 초기화
            const maxLength = userInput.attr("maxlength");
            charCount.text(`0/${maxLength} 글자`);
            //최종 산출된 내용을 전달
            chatLoading.hide();
        }
    }
});

//글자 수 확인용
function inputLimits() {
    // 입력 필드와 글자 수 나타낼 요소 선택
    const userInput = $("#userInput"); // 입력 필드
    const charCount = $("#charCount"); // 글자 수 출력 요소

    // 입력 이벤트를 감지하여 글자 수 업데이트
    userInput.on("input", function() {
        const currentLength = $(this).val().length; // 입력된 글자 수
        const maxLength = $(this).attr("maxlength"); // 최대 입력 가능 글자 수
        charCount.text(`${currentLength}/${maxLength} 글자`);
    });
}