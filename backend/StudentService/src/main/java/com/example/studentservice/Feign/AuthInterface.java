package com.example.studentservice.Feign;

import com.example.studentservice.Vo.UserCredential;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "AUTHENTICATION-SERVICE")
public interface AuthInterface {
    @PostMapping("auth/register")
    public String addNewUser(List<UserCredential> users);
}