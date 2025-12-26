package com.example.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class ApiGatewayApplication {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class);

    public static void main(String[] args) {
        logger.info("=================================================");
        logger.info("Starting API Gateway Application...");
        logger.info("=================================================");

        try {
            Environment env = SpringApplication.run(ApiGatewayApplication.class, args).getEnvironment();

            logger.info("=================================================");
            logger.info("API Gateway Application started successfully!");
            logger.info("Application Name: {}", env.getProperty("spring.application.name"));
            logger.info("Server Port: {}", env.getProperty("server.port"));
            logger.info("Active Profiles: {}", String.join(", ", env.getActiveProfiles()));
            logger.info("=================================================");

        } catch (Exception e) {
            logger.error("Failed to start API Gateway Application: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostConstruct
    public void init() {
        logger.info("API Gateway Application initialized successfully!");
    }
}
