package com.jobmoa.app.jobPlacement.view.webSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class WebSocketController {

    @MessageMapping("/sendMessage") // JSP와 일치
    @SendTo("/topic/getResponse")
    public String sendMessage(String message) throws Exception {
        log.info("Received message: {}", message);
        return "테스트 메시지: " + message;
    }

    @MessageMapping("/testMessage/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatMessage test(@DestinationVariable String roomId, ChatMessage chatMessage){
        log.info("Received test message in room {}: {}", roomId, chatMessage.getMessage());
        chatMessage.setRoomId(roomId);
        return chatMessage;
    }
}
