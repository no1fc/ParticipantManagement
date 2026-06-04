package com.jobmoa.app.jobPlacement.view.webSocket;

import lombok.Data;

/**
 * WebSocket 채팅 메시지 DTO.
 * 채팅방 ID와 메시지 내용을 담아 STOMP 메시지 송수신에 사용된다.
 */
@Data
public class ChatMessage {
    private String roomId;
    private String message;
}
