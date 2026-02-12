package com.example.studentservice.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for Auth Service
 * Auth Service has context-path: /auth
 */
@FeignClient(name = "auth-service", url = "${services.auth.url:http://auth-service:8081}")
public interface AuthInterface {

    @PostMapping("/auth/register")
    ResponseEntity<String> addNewUser(@RequestBody Map<String, Object> user);

    @GetMapping("/auth/validate")
    String validateToken(@RequestParam String token);

    @GetMapping("/auth/user/{id}")
    ResponseEntity<Map<String, Object>> getUserById(@PathVariable("id") Long id);
}
