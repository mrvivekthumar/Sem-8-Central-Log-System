package com.example.studentservice.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    private final HttpServletRequest request;

    public CurrentUser(HttpServletRequest request) {
        this.request = request;
    }

    public Long getUserId() {
        return Long.valueOf(request.getHeader("X-User-Id"));
    }

    public String getRole() {
        return request.getHeader("X-User-Role");
    }

    public String getEmail() {
        return request.getHeader("X-User-Email");
    }
}
