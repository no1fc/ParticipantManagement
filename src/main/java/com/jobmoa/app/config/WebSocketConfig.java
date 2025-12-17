package com.jobmoa.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정 (Spring Boot 방식)
 * STOMP 프로토콜을 사용한 메시지 브로커 설정
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 메시지 브로커 설정
     * - /topic: 구독(subscribe) 엔드포인트
     * - /app: 애플리케이션 목적지 prefix
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Simple broker를 활성화하고 destination prefix 설정
        config.enableSimpleBroker("/topic");

        // 클라이언트에서 보내는 메시지의 prefix 설정
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * STOMP 엔드포인트 등록
     * WebSocket 연결을 위한 엔드포인트 설정
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 엔드포인트 등록 (Spring Boot 자동 설정 사용)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // 개발 환경용, 운영에서는 특정 도메인으로 변경
                .withSockJS();  // SockJS fallback 옵션 활성화

        // 추가 엔드포인트
        registry.addEndpoint("/ws-notification")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
