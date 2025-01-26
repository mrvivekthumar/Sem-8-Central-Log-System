package com.example.facultyservice.Feign;

import com.example.facultyservice.Vo.UserCredential;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "AUTHENTICATION-SERVICE")
public interface AuthInterface {
    @PostMapping("auth/register")
    public String addNewUser(List<UserCredential> users);
}