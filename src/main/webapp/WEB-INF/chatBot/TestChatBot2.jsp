<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 25. 2. 27.
  Time: 오후 4:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.1.js" integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4=" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/jquery/dist/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
    <script src="/js/chatBotAsync_0.0.1.js"></script>

    <link rel="stylesheet" href="/css/chatBot_0.0.1.css"/>
</head>
<body>
<div id="chatLog">
</div>
<div id="chatLoading">ChatGPT가 내용을 출력하고 있습니다<div class="loader"></div></div>
<div id="sendBox">
    <textarea id="userInput" placeholder="질문을 입력해주세요." minlength="0" maxlength="1000"></textarea>
    <button id="sendMassage">보내기</button>
    <p id="charCount">0/1000 글자</p>
</div>

<div id="userInputLimits">1000자 이하로 입력 부탁드립니다.</div>
</body>
</html>
