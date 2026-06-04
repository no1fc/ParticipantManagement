package com.jobmoa.app.jobPlacement.view.webSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * WebSocket STOMP 메시지 처리 컨트롤러.
 * STOMP 프로토콜을 통한 실시간 메시지 송수신 및 채팅방 기반 메시지 브로드캐스트를 처리한다.
 */
@Slf4j
@Controller
public class WebSocketController {

    /**
     * 클라이언트에서 전송한 메시지를 수신하여 전체 구독자에게 브로드캐스트한다.
     *
     * @param message 클라이언트가 전송한 메시지 문자열
     * @return 가공된 응답 메시지
     * @throws Exception 메시지 처리 중 발생할 수 있는 예외
     */
    @MessageMapping("/sendMessage") // JSP와 일치
    @SendTo("/topic/getResponse")
    public String sendMessage(String message) throws Exception {
        log.info("Received message: {}", message);
        return "테스트 메시지: " + message;
    }

    /**
     * 특정 채팅방에 메시지를 전송한다.
     * 채팅방 ID를 기반으로 해당 토픽을 구독 중인 사용자에게 메시지를 브로드캐스트한다.
     *
     * @param roomId      채팅방 ID (URL 경로 변수)
     * @param chatMessage 전송할 채팅 메시지 객체
     * @return 채팅방 ID가 설정된 채팅 메시지 객체
     */
    @MessageMapping("/testMessage/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatMessage test(@DestinationVariable String roomId, ChatMessage chatMessage){
        log.info("Received test message in room {}: {}", roomId, chatMessage.getMessage());
        chatMessage.setRoomId(roomId);
        return chatMessage;
    }
}
