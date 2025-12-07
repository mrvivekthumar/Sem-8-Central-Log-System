package com.example.facultyservice.Feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "auth-service", url = "${auth-service.url:http://localhost:8070}")
public interface AuthInterface {

    @PostMapping("/auth/register")
    ResponseEntity<String> addNewUser(@RequestBody List<Map<String, Object>> users);
    
    @GetMapping("/auth/validate")
    String validateToken(@RequestParam String token);
}
