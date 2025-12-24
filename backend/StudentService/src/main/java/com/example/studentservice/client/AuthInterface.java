package com.example.studentservice.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", url = "${auth-service.url:http://localhost:8070}")
public interface AuthInterface {

    @PostMapping("/register")
    ResponseEntity<String> addNewUser(@RequestBody Map<String, Object> user);

    @GetMapping("/validate")
    String validateToken(@RequestParam String token);
}
