package com.example.authenticationservice.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to add correlation ID to all requests for distributed tracing
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String correlationId = null;
        
        try {
            // Get correlation ID from request header or generate new one
            correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.trim().isEmpty()) {
                correlationId = UUID.randomUUID().toString();
                logger.debug("Generated new correlation ID: {}", correlationId);
            } else {
                logger.debug("Using existing correlation ID: {}", correlationId);
            }

            // Store in MDC for logging
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            
            // Add to response header for client tracking
            httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);

            logger.debug("Processing request: {} {} with correlationId: {}", 
                    httpRequest.getMethod(), 
                    httpRequest.getRequestURI(), 
                    correlationId);

            // Continue with the request
            chain.doFilter(request, response);

        } finally {
            // Always clean up MDC to prevent memory leaks
            MDC.remove(CORRELATION_ID_MDC_KEY);
            logger.debug("Completed request with correlationId: {}", correlationId);
        }
    }
}
