<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 25. 2. 27.
  Time: 오후 1:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Chat Bot</title>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>

    <script>
        $(document).ready(function () {
            $("#sendMassage").on("click", sendMessage);

            const userInput = $("#userInput");
            userInput.keypress(function (e) {
                if (e.which == 13) {
                    sendMessage();
                }
            });

            async function sendMessage() {
                const chatLog = $("#chatLog");
                const response = await fetch("/chatBot/ask", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(userInput.val())
                });
                console.log(response);
                const botResponse = await response.text();
                console.log(botResponse);
                chatLog.append("<p><strong>사용자: </strong>"+userInput.val()+"</p>");
                chatLog.append("<p><strong>챗봇: </strong>"+botResponse+"</p>");
                userInput.val("");

                //스크롤 하단 이동
                chatLog.scrollTop(chatLog[0].scrollHeight);
            }
        });
    </script>

    <link rel="stylesheet" href="/css/chatBot_0.0.1.css">
</head>
<body>
<div id="chatLog"></div>
<div id="sendBox">
    <input type="text" id="userInput" placeholder="입력하세요" />
    <button id="sendMassage">보내기</button>
</div>
</body>
</html>