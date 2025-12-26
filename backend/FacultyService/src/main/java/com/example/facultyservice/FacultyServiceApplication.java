package com.example.facultyservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class FacultyServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(FacultyServiceApplication.class);

    public static void main(String[] args) {
        logger.info("=================================================");
        logger.info("Starting Faculty Service Application...");
        logger.info("=================================================");

        try {
            Environment env = SpringApplication.run(FacultyServiceApplication.class, args).getEnvironment();

            logger.info("=================================================");
            logger.info("Faculty Service started successfully!");
            logger.info("Application Name: {}", env.getProperty("spring.application.name"));
            logger.info("Server Port: {}", env.getProperty("server.port"));
            logger.info("Database URL: {}", env.getProperty("spring.datasource.url"));
            logger.info("Feign Clients: ENABLED");
            logger.info("Scheduling: ENABLED");
            logger.info("Active Profiles: {}", String.join(", ", env.getActiveProfiles()));
            logger.info("=================================================");

        } catch (Exception e) {
            logger.error("Failed to start Faculty Service: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostConstruct
    public void init() {
        logger.info("Faculty Service Application initialized successfully!");
        logger.info("Feign Clients and Scheduling features are enabled");
    }
}
