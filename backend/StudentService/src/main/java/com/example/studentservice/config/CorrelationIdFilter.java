package com.example.studentservice.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class CorrelationIdFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_KEY = "correlationId";

    @PostConstruct
    public void init() {
        logger.info("CorrelationIdFilter initialized with Order 1 (HIGHEST)");
        logger.info("Correlation ID header: {}, MDC key: {}", CORRELATION_ID_HEADER, CORRELATION_ID_KEY);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String correlationId = null;
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();

        try {
            // Get correlation ID from header or generate new one
            correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
            boolean generated = false;

            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = UUID.randomUUID().toString();
                generated = true;
                logger.debug("Generated new correlation ID: {} for {} {}", correlationId, method, uri);
            } else {
                logger.debug("Using existing correlation ID: {} for {} {}", correlationId, method, uri);
            }

            // Put correlation ID in MDC for logging
            MDC.put(CORRELATION_ID_KEY, correlationId);

            // Add correlation ID to response header
            httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);

            if (generated) {
                logger.info("Request: {} {} | Correlation ID: {} (Generated)", method, uri, correlationId);
            } else {
                logger.info("Request: {} {} | Correlation ID: {} (Received)", method, uri, correlationId);
            }

            chain.doFilter(request, response);
        } finally {
            // Clean up MDC
            logger.debug("Cleaning up MDC for correlation ID: {}", correlationId);
            MDC.clear();
            logger.debug("Completed request {} {} with correlation ID: {}", method, uri, correlationId);
        }
    }
}
