package com.jobmoa.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/**
 * WebSocket 설정 (Spring Boot 방식)
 * STOMP 프로토콜을 사용한 메시지 브로커 설정
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String WS_ENDPOINT = "/ws";
    private static final String WS_NOTIFICATION_ENDPOINT = "/ws-notification";
    private static final String ALLOWED_ORIGINS_WILDCARD = "*";

    // configurMessageBroker Prefix And Destination
    private static final String SIMPLE_BROKER_PREFIX = "/topic";
    private static final String APP_CLIENT_DESTINATION_PREFIX = "/app";

    /**
     * 메시지 브로커 설정
     * - /topic: 구독(subscribe) 엔드포인트
     * - /app: 애플리케이션 목적지 prefix
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Simple broker를 활성화하고 destination prefix 설정
        config.enableSimpleBroker(SIMPLE_BROKER_PREFIX);

        // 클라이언트에서 보내는 메시지의 prefix 설정
        config.setApplicationDestinationPrefixes(APP_CLIENT_DESTINATION_PREFIX);
    }

    /**
     * STOMP 엔드포인트 등록
     * WebSocket 연결을 위한 엔드포인트 설정
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 엔드포인트 등록 (Spring Boot 자동 설정 사용)
        registry.addEndpoint(WS_ENDPOINT)
                .setAllowedOriginPatterns(ALLOWED_ORIGINS_WILDCARD)  // 개발 환경용, 운영에서는 특정 도메인으로 변경
                .withSockJS();  // SockJS fallback 옵션 활성화

        // 추가 엔드포인트
        registry.addEndpoint(WS_NOTIFICATION_ENDPOINT)
                .setAllowedOriginPatterns(ALLOWED_ORIGINS_WILDCARD)
                .withSockJS();
    }
}
