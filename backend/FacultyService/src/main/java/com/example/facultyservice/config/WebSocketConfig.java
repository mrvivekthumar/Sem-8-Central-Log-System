package com.example.facultyservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @PostConstruct
    public void init() {
        logger.info("=================================================");
        logger.info("WebSocketConfig initialized!");
        logger.info("WebSocket Message Broker ENABLED for notifications");
        logger.info("=================================================");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        logger.info("Configuring WebSocket Message Broker...");

        // Enable a simple in-memory message broker
        config.enableSimpleBroker("/topic", "/queue");
        logger.info("Simple broker enabled with destinations: /topic, /queue");

        // Prefix for messages bound for methods annotated with @MessageMapping
        config.setApplicationDestinationPrefixes("/app");
        logger.info("Application destination prefix set to: /app");

        logger.info("WebSocket Message Broker configuration completed successfully");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        logger.info("Registering STOMP WebSocket endpoints...");

        // Register STOMP endpoint for WebSocket connections with SockJS
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        logger.info("WebSocket endpoint registered: /ws (with SockJS support)");
        logger.info("Allowed origin patterns: * (all origins)");

        // Also register without SockJS for native WebSocket support
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
        logger.info("WebSocket endpoint registered: /ws (native WebSocket)");

        logger.info("STOMP endpoints registration completed successfully");
        logger.info("WebSocket is ready to accept connections on /ws");
    }
}
