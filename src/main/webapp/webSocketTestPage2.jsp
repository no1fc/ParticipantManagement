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
<button type="button" id="connect" onclick="connect()">Connect</button>
<button type="button" id="disconnect" onclick="disconnect()" disabled>Disconnect</button>
<button type="button" onclick="sendMessage()">메세지 전송</button>
<div id="chat">

</div>
</body>

<script type="text/javascript">
    let socket = new SockJS('/ws');
    let stompClient = Stomp.over(socket);
    let roomId = 1;

    // 참고 블로그
    //https://velog.io/@ygy0102/Spring-Boot-STOMP%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%B1%84%ED%8C%85-%EB%B0%8F-%EC%B1%84%ED%8C%85%EB%B0%A9-%EB%8F%99%EC%A0%81-%EC%83%9D%EC%84%B1
    function setConnected(connected) {
        $("#connect").prop("disabled", connected);
        $("#disconnect").prop("disabled", !connected);

        console.log("Connected: " + connected);

        $("#chat").append("<p>Connected: " + connected + "</p>");
    }

    function connect() {
        stompClient.connect({}, function (frame) {
            setConnected(true);
            console.log("Connected: " + frame);

            stompClient.subscribe('/topic/'+roomId , function (message) {
                showChat( JSON.parse( message.body ))
            })
        })
    }

    function disconnect() {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
    }

    function sendMessage() {
        stompClient.send("/app/testMessage/"+roomId, {}, JSON.stringify( {'message': $("#inputmessage").val()} ));
        $("#inputmessage").val("");
    }


    $(document).ready(function () {
        connect();
    })

    window.onbeforeunload = function () {
        disconnect();
    }

    //보낸 채팅 보기
    function showChat(chatMessage) {
        console.log("showChat:"+chatMessage.message)
        $("#chat").append(
            "<div class = 'chatting_own'><tr><td>" + chatMessage.message + "</td></tr></div>"
        );
    }

    function getChat(){

    }

</script>
</html>
