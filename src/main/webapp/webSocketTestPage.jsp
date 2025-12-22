<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 25. 12. 17.
  Time: 오후 2:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>WebSocketTestPage</title>
    <!-- jQuery JS -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>
    <!-- SockJS 라이브러리 로드 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.6.1/sockjs.min.js" integrity="sha512-1QvjE7BtotQjkq8PxLeF6P46gEpBRXuskzIVgjFpekzFVF4yjRgrQvTG1MTOJ3yQgvTteKAcO7DSZI92+u/yZw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    <!-- Stomp.js 라이브러리 로드 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js" integrity="sha512-iKDtgDyTHjAitUDdLljGhenhPwrbBfqTKWO1mkhSFH3A7blITC9MhYon6SjnMhp4o0rADGw9yAC6EW4t5a4K3g==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
</head>
<body>
<div>
    <input type="text" id="inputmessage">
</div>
<button type="button" onclick="sendMessage()">메세지 전송</button>
<div id="response">

</div>
</body>

<script type="text/javascript">
    var socket = new SockJS('/ws');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // 클라이언트가 서버로부터 메시지를 받을 경로를 구독
        stompClient.subscribe('/topic/getResponse', function (message) {
            console.log("Received message: " + message.body);
            // 받은 메시지를 화면에 표시
            $("#response").append("<p>" + message.body + "</p>");
        });
    }, function (error) {
        console.log('Connect Error: ' + error);
    });

    function sendMessage() {
        let inputmessage = $("#inputmessage").val();
        stompClient.send("/app/sendMessage", {}, JSON.stringify(inputmessage));
    }
</script>
</html>
