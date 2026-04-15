package com.example.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;

/**
 * API Gateway Application - Entry point for Central Log System Gateway
 * 
 * Responsibilities:
 * - Routes requests to appropriate microservices
 * - Handles authentication and authorization via JWT
 * - Manages CORS for frontend applications
 * - Tracks requests with correlation IDs   
 * - Provides centralized access point for all services
 * 
 * Architecture:
 * Frontend → API Gateway → [Auth/Faculty/Student Services]
 */
@SpringBootApplication
public class ApiGatewayApplication {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class);

    public static void main(String[] args) {
        logger.info("╔════════════════════════════════════════════════════════╗");
        logger.info("║   Central Log System - API Gateway Starting...        ║");
        logger.info("╚════════════════════════════════════════════════════════╝");

        try {
            Environment env = SpringApplication.run(ApiGatewayApplication.class, args).getEnvironment();

            String appName = env.getProperty("spring.application.name");
            String port = env.getProperty("server.port");
            String profile = String.join(", ", env.getActiveProfiles());
            if (profile.isEmpty())
                profile = "default";

            logger.info("╔════════════════════════════════════════════════════════╗");
            logger.info("║   API Gateway Started Successfully! ✅                 ║");
            logger.info("╠════════════════════════════════════════════════════════╣");
            logger.info("║   Application Name: {}", String.format("%-32s", appName) + "║");
            logger.info("║   Server Port: {}", String.format("%-37s", port) + "║");
            logger.info("║   Active Profile: {}", String.format("%-36s", profile) + "║");
            logger.info("║   Gateway URL: {}", String.format("%-37s", "http://localhost:" + port) + "║");
            logger.info("║   Health Check: {}",
                    String.format("%-34s", "http://localhost:" + port + "/actuator/health") + "║");
            logger.info("╠════════════════════════════════════════════════════════╣");
            logger.info("║   Configured Routes:                                   ║");
            logger.info("║   • /api/auth/**    → Authentication Service          ║");
            logger.info("║   • /api/faculty/** → Faculty Service                 ║");
            logger.info("║   • /api/student/** → Student Service                 ║");
            logger.info("╚════════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            logger.error("╔════════════════════════════════════════════════════════╗");
            logger.error("║   ❌ Failed to start API Gateway Application           ║");
            logger.error("╚════════════════════════════════════════════════════════╝");
            logger.error("Error Details: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    @PostConstruct
    public void init() {
        logger.info("🔧 API Gateway Application initialized - Post-construction complete");
    }
}
