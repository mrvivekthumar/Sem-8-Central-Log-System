package com.example.adminservice.Feign;

import com.example.adminservice.Vo.UserCredential;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AUTHENTICATION-SERVICE")
public interface AuthInterface {
    @PostMapping("auth/updatePassword")
    public ResponseEntity<String> updatePassword(@RequestBody UserCredential user);
}
