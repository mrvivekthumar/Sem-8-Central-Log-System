package com.example.facultyservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.facultyservice.interceptor.CorrelationIdInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Web MVC Configuration
 * 
 * Purpose:
 * - Register custom interceptors
 * - Configure request/response handling
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    private final CorrelationIdInterceptor correlationIdInterceptor;

    public WebConfig(CorrelationIdInterceptor correlationIdInterceptor) {
        this.correlationIdInterceptor = correlationIdInterceptor;
    }

    @PostConstruct
    public void init() {
        logger.info("Web MVC Configuration initialized");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("Registering CorrelationIdInterceptor");
        registry.addInterceptor(correlationIdInterceptor);
    }
}
