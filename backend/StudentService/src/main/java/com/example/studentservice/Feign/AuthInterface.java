package com.example.studentservice.Feign;

import com.example.studentservice.Vo.UserCredential;

import org.checkerframework.checker.units.qual.g;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "auth-service", url = "${auth-service.url:http://localhost:8070}")
public interface AuthInterface {
    @PostMapping("auth/register")
    public String addNewUser(List<UserCredential> users);

    public ResponseEntity<UserCredential> addSingleOne(UserCredential user);
}