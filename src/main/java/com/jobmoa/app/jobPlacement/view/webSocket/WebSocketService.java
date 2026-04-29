package com.jobmoa.app.jobPlacement.view.webSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WebSocketService {


    private final SimpMessagingTemplate messagingTemplate;

    private final List<WebSocketSession> sessionList = new ArrayList<WebSocketSession>();

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public boolean connect(WebSocketSession session) {
        boolean flag;
        try {
            flag = addSession(session);
            log.info("WebSocket session connected: {}", session.getId());
        }
        catch (Exception e) {
            log.error("WebSocket session connected error: {}", e.getMessage());
            flag = false;
        }
        return flag;
    }
    public boolean disconnect(WebSocketSession session) {
        boolean flag;
        try {
            flag = removeSession(session);
            log.info("WebSocket session disconnected: {}", session.getId());
        }
        catch (Exception e) {
            log.error("WebSocket session disconnected error: {}", e.getMessage());
            flag = false;
        }
        return flag;
    }

    public void sendMessage(String destination, String message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    public void sendObject(String destination, Object message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    public void sendObjectToUser(String destination, Object message) {
        messagingTemplate.convertAndSendToUser("user", destination, message);
    }

    public void sendMessageToUser(String destination, String message, String userId) {
        messagingTemplate.convertAndSendToUser(userId, destination, message);
    }

    public void sendObjectToUser(String destination, Object message, String userId) {
        messagingTemplate.convertAndSendToUser(userId, destination, message);
    }

    public boolean addSession(WebSocketSession session) {
        return sessionList.add(session);
    }

    public boolean removeSession(WebSocketSession session) {
        return sessionList.remove(session);
    }
}
