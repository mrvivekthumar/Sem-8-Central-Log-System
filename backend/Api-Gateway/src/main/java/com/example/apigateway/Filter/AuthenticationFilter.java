package com.example.apigateway.Filter;

import com.example.apigateway.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpResponse response = exchange.getResponse();

            // Handle OPTIONS requests immediately (CORS preflight)
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                // response.setStatusCode(HttpStatus.OK);
                // // Add CORS headers for preflight
                // response.getHeaders().add("Access-Control-Allow-Origin",
                // exchange.getRequest().getHeaders().getFirst("Origin"));
                // response.getHeaders().add("Access-Control-Allow-Methods",
                // "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
                // response.getHeaders().add("Access-Control-Allow-Headers", "*");
                // response.getHeaders().add("Access-Control-Allow-Credentials", "true");
                // response.getHeaders().add("Access-Control-Max-Age", "3600");
                // return response.setComplete();

                return chain.filter(exchange);
            }

            // Check if the route needs authentication
            if (validator.isSecured.test(exchange.getRequest())) {
                // Check for authorization header
                ServerHttpResponse response = exchange.getResponse();
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                } else {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }

                try {
                    jwtUtil.validateToken(authHeader);
                } catch (Exception e) {
                    logger.error("Invalid token attempt: {}", e.getMessage());
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }
            }

            return chain.filter(exchange);
        });
    }

    public static class Config {
        // Configuration properties if needed
    }
}