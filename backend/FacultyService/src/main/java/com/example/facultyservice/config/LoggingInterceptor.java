package com.example.facultyservice.config;

import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @PostConstruct
    public void init() {
        logger.info("LoggingInterceptor initialized and ready to intercept requests");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        // Log incoming request
        logger.info("==> Incoming Request: {} {}", request.getMethod(), request.getRequestURI());
        logger.debug("Request Headers: {}", getHeaders(request));
        logger.debug("Request Parameters: {}", request.getQueryString());
        logger.debug("Remote Address: {}", request.getRemoteAddr());
        logger.debug("Content Type: {}", request.getContentType());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {

        Long startTimeAttr = (Long) request.getAttribute("startTime");
        if (startTimeAttr == null) {
            logger.warn("startTime attribute not found in request");
            return;
        }

        long startTime = startTimeAttr;
        long duration = System.currentTimeMillis() - startTime;

        // Log outgoing response
        logger.info("<== Outgoing Response: {} {} - Status: {} - Duration: {}ms",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration);

        if (ex != null) {
            logger.error("Request failed with exception for {} {}: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    ex.getMessage(), ex);
        }

        // Log slow requests (more than 1 second)
        if (duration > 1000) {
            logger.warn("⚠️ SLOW REQUEST DETECTED: {} {} took {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    duration);
        }

        // Log very slow requests (more than 3 seconds)
        if (duration > 3000) {
            logger.error("🔥 VERY SLOW REQUEST DETECTED: {} {} took {}ms - PERFORMANCE ISSUE!",
                    request.getMethod(),
                    request.getRequestURI(),
                    duration);
        }
    }

    private String getHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // Skip sensitive headers
            if (!headerName.equalsIgnoreCase("Authorization") &&
                    !headerName.equalsIgnoreCase("Cookie")) {
                headers.append(headerName)
                        .append(": ")
                        .append(request.getHeader(headerName))
                        .append(", ");
            } else {
                headers.append(headerName)
                        .append(": [REDACTED], ");
            }
        }

        return headers.toString();
    }
}
