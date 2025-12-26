package com.example.authenticationservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class AuthenticationServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceApplication.class);

    public static void main(String[] args) {
        logger.info("=================================================");
        logger.info("Starting Authentication Service Application...");
        logger.info("=================================================");

        try {
            Environment env = SpringApplication.run(AuthenticationServiceApplication.class, args).getEnvironment();

            logger.info("=================================================");
            logger.info("Authentication Service started successfully!");
            logger.info("Application Name: {}", env.getProperty("spring.application.name"));
            logger.info("Server Port: {}", env.getProperty("server.port"));
            logger.info("Database URL: {}", env.getProperty("spring.datasource.url"));
            logger.info("Active Profiles: {}", String.join(", ", env.getActiveProfiles()));
            logger.info("=================================================");

        } catch (Exception e) {
            logger.error("Failed to start Authentication Service: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostConstruct
    public void init() {
        logger.info("Authentication Service Application initialized successfully!");
    }
}
