package com.example.apigateway.config;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

/**
 * CorrelationIdFilter - Global filter for request tracking
 * 
 * Purpose:
 * - Generates or extracts correlation ID for each request
 * - Propagates correlation ID to downstream services
 * - Enables end-to-end request tracing across microservices
 * - Helps in debugging and log correlation
 * 
 * Flow:
 * 1. Check if request has X-Correlation-Id header
 * 2. If not present, generate new UUID
 * 3. Add correlation ID to request header (forward to services)
 * 4. Add correlation ID to response header (return to client)
 * 5. Store in MDC for logging context
 * 6. Clean up MDC after request completes
 */
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);

    // Header name for correlation ID
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    // Alternative header name (some systems use different names)
    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    @PostConstruct
    public void init() {
        logger.info("=================================================");
        logger.info("CorrelationIdFilter initialized successfully");
        logger.info("Filter Order: HIGHEST_PRECEDENCE (runs first)");
        logger.info("Correlation ID Header: {}", CORRELATION_ID_HEADER);
        logger.info("Request ID Header: {}", REQUEST_ID_HEADER);
        logger.info("=================================================");
    }

    /**
     * Main filter logic
     * Executes for every request passing through the gateway
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        HttpHeaders headers = request.getHeaders();

        // =========================================
        // STEP 1: Extract or Generate Correlation ID
        // =========================================
        String correlationId = extractCorrelationId(headers);
        boolean wasGenerated = (correlationId == null);

        if (wasGenerated) {
            correlationId = generateCorrelationId();
            logger.debug("🆕 Generated new correlation ID: {} for {} {}",
                    correlationId, method, path);
        } else {
            logger.debug("♻️ Using existing correlation ID: {} for {} {}",
                    correlationId, method, path);
        }

        final String finalCorrelationId = correlationId;

        // =========================================
        // STEP 2: Add Correlation ID to Request
        // =========================================
        // Mutate request to add correlation ID header
        // This ensures downstream services receive the correlation ID
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(builder -> {
                    builder.header(CORRELATION_ID_HEADER, finalCorrelationId);
                    builder.header(REQUEST_ID_HEADER, finalCorrelationId);
                })
                .build();

        // =========================================
        // STEP 3: Add Correlation ID to Response
        // =========================================
        // Add to response headers so client can track their request
        response.getHeaders().add(CORRELATION_ID_HEADER, finalCorrelationId);
        response.getHeaders().add(REQUEST_ID_HEADER, finalCorrelationId);

        // =========================================
        // STEP 4: Add to MDC for Logging Context
        // =========================================
        // MDC allows correlation ID to appear in all log statements
        MDC.put(CORRELATION_ID_HEADER, finalCorrelationId);
        MDC.put("method", method);
        MDC.put("path", path);

        // Log the request with correlation ID
        if (wasGenerated) {
            logger.info("📨 Incoming Request: {} {} | Correlation-ID: {} [GENERATED]",
                    method, path, finalCorrelationId);
        } else {
            logger.info("📨 Incoming Request: {} {} | Correlation-ID: {} [RECEIVED]",
                    method, path, finalCorrelationId);
        }

        // =========================================
        // STEP 5: Process Request & Clean Up
        // =========================================
        return chain.filter(modifiedExchange)
                .doOnSuccess(aVoid -> {
                    // Log successful completion
                    int statusCode = response.getStatusCode() != null ? response.getStatusCode().value() : 0;
                    logger.info("✅ Request Completed: {} {} | Status: {} | Correlation-ID: {}",
                            method, path, statusCode, finalCorrelationId);
                })
                .doOnError(error -> {
                    // Log error completion
                    logger.error("❌ Request Failed: {} {} | Error: {} | Correlation-ID: {}",
                            method, path, error.getMessage(), finalCorrelationId);
                })
                .doFinally(signalType -> {
                    // Always clean up MDC to prevent memory leaks
                    logger.trace("🧹 Cleaning up MDC for Correlation-ID: {}", finalCorrelationId);
                    MDC.remove(CORRELATION_ID_HEADER);
                    MDC.remove(REQUEST_ID_HEADER);
                    MDC.remove("method");
                    MDC.remove("path");
                });
    }

    /**
     * Extract correlation ID from request headers
     * Checks multiple possible header names
     * 
     * @param headers Request headers
     * @return Correlation ID if found, null otherwise
     */
    private String extractCorrelationId(HttpHeaders headers) {
        // Try primary header
        String correlationId = headers.getFirst(CORRELATION_ID_HEADER);

        // If not found, try alternative header
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = headers.getFirst(REQUEST_ID_HEADER);
        }

        // Validate and clean the ID
        if (correlationId != null) {
            correlationId = correlationId.trim();
            if (correlationId.isEmpty()) {
                correlationId = null;
            }
        }

        return correlationId;
    }

    /**
     * Generate a new unique correlation ID
     * Uses UUID for guaranteed uniqueness
     * 
     * @return New correlation ID
     */
    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Filter execution order
     * HIGHEST_PRECEDENCE ensures this runs before all other filters
     * This is critical - correlation ID must be set before any other processing
     * 
     * @return Order value
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
