package com.example.facultyservice.Feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "auth-service", url = "${auth-service.url:http://localhost:8070}")
public interface AuthInterface {

    // Changed: Now accepts single user Map instead of List
    // Path changed from /auth/register to /register to match Auth Service
    @PostMapping("/register")
    ResponseEntity<String> addNewUser(@RequestBody Map<String, Object> user);

    @GetMapping("/validate")
    String validateToken(@RequestParam String token);
}
