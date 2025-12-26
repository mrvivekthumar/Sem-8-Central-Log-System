package com.example.studentservice.config;

import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        // Log incoming request
        logger.info("==> Incoming Request: {} {}", request.getMethod(), request.getRequestURI());
        logger.debug("Request Headers: {}", getHeaders(request));
        logger.debug("Request Parameters: {}", request.getQueryString());
        logger.debug("Remote Address: {}", request.getRemoteAddr());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {

        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        // Log outgoing response
        logger.info("<== Outgoing Response: {} {} - Status: {} - Duration: {}ms",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration);

        if (ex != null) {
            logger.error("Request failed with exception: ", ex);
        }

        // Log slow requests (more than 1 second)
        if (duration > 1000) {
            logger.warn("SLOW REQUEST DETECTED: {} {} took {}ms",
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
            }
        }

        return headers.toString();
    }
}
