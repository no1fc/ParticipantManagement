package com.jobmoa.app.jobPlacement.view.webSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket 메시지 전송 서비스.
 * SimpMessagingTemplate을 활용하여 STOMP 토픽/사용자 대상 메시지 전송 기능을 제공하며,
 * WebSocket 세션의 연결/해제를 관리한다.
 */
@Slf4j
@Service
public class WebSocketService {


    private final SimpMessagingTemplate messagingTemplate;

    private final List<WebSocketSession> sessionList = new ArrayList<WebSocketSession>();

    /**
     * SimpMessagingTemplate을 주입받아 WebSocketService를 생성한다.
     *
     * @param messagingTemplate STOMP 메시지 전송 템플릿
     */
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * WebSocket 세션을 연결 처리한다.
     *
     * @param session 연결할 WebSocket 세션
     * @return 세션 추가 성공 여부
     */
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
    /**
     * WebSocket 세션을 연결 해제 처리한다.
     *
     * @param session 해제할 WebSocket 세션
     * @return 세션 제거 성공 여부
     */
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

    /**
     * 지정된 토픽에 문자열 메시지를 전송한다.
     *
     * @param destination 메시지 전송 대상 토픽 경로
     * @param message     전송할 메시지 문자열
     */
    public void sendMessage(String destination, String message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * 지정된 토픽에 객체 메시지를 전송한다.
     *
     * @param destination 메시지 전송 대상 토픽 경로
     * @param message     전송할 메시지 객체
     */
    public void sendObject(String destination, Object message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * 특정 사용자("user")에게 객체 메시지를 전송한다.
     *
     * @param destination 메시지 전송 대상 경로
     * @param message     전송할 메시지 객체
     */
    public void sendObjectToUser(String destination, Object message) {
        messagingTemplate.convertAndSendToUser("user", destination, message);
    }

    /**
     * 특정 사용자 ID에게 문자열 메시지를 전송한다.
     *
     * @param destination 메시지 전송 대상 경로
     * @param message     전송할 메시지 문자열
     * @param userId      대상 사용자 ID
     */
    public void sendMessageToUser(String destination, String message, String userId) {
        messagingTemplate.convertAndSendToUser(userId, destination, message);
    }

    /**
     * 특정 사용자 ID에게 객체 메시지를 전송한다.
     *
     * @param destination 메시지 전송 대상 경로
     * @param message     전송할 메시지 객체
     * @param userId      대상 사용자 ID
     */
    public void sendObjectToUser(String destination, Object message, String userId) {
        messagingTemplate.convertAndSendToUser(userId, destination, message);
    }

    /**
     * WebSocket 세션을 세션 목록에 추가한다.
     *
     * @param session 추가할 WebSocket 세션
     * @return 추가 성공 여부
     */
    public boolean addSession(WebSocketSession session) {
        return sessionList.add(session);
    }

    /**
     * WebSocket 세션을 세션 목록에서 제거한다.
     *
     * @param session 제거할 WebSocket 세션
     * @return 제거 성공 여부
     */
    public boolean removeSession(WebSocketSession session) {
        return sessionList.remove(session);
    }
}
