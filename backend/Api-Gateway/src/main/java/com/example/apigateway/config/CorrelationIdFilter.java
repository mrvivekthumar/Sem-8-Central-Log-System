package com.example.apigateway.config;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @PostConstruct
    public void init() {
        logger.info("CorrelationIdFilter initialized with HIGHEST_PRECEDENCE order");
        logger.info("Correlation ID header name: {}", CORRELATION_ID_HEADER);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        HttpHeaders headers = exchange.getRequest().getHeaders();
        String correlationId = headers.getFirst(CORRELATION_ID_HEADER);

        boolean generated = false;
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
            generated = true;
            logger.debug("Generated new correlation ID: {} for request: {} {}", correlationId, method, path);
        } else {
            logger.debug("Using existing correlation ID: {} for request: {} {}", correlationId, method, path);
        }

        String finalCorrelationId = correlationId;

        exchange = exchange.mutate()
                .request(builder -> builder.header(CORRELATION_ID_HEADER, finalCorrelationId))
                .build();

        MDC.put(CORRELATION_ID_HEADER, finalCorrelationId);

        if (generated) {
            logger.info("Request: {} {} | Correlation ID: {} (Generated)", method, path, finalCorrelationId);
        } else {
            logger.info("Request: {} {} | Correlation ID: {} (Received)", method, path, finalCorrelationId);
        }

        return chain.filter(exchange)
                .doFinally(signal -> {
                    logger.debug("Removing correlation ID from MDC: {}", finalCorrelationId);
                    MDC.remove(CORRELATION_ID_HEADER);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
