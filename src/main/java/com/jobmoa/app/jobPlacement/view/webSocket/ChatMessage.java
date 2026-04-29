package com.jobmoa.app.jobPlacement.view.webSocket;

import lombok.Data;

@Data
public class ChatMessage {
    private String roomId;
    private String message;
}
