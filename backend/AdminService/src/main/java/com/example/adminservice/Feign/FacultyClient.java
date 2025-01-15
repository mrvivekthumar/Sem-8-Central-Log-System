package com.example.adminservice.Feign;

import com.example.adminservice.Model.Faculty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "FACULTY-SERVICE")
public interface FacultyClient {
    @PostMapping("faculty/register")
    public ResponseEntity<Faculty> registerFaculty(@RequestBody Faculty faculty);

}
