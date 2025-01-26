package com.example.adminservice.Feign;

import com.example.adminservice.Model.Faculty;
import com.example.adminservice.Model.Student;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "STUDENT-SERVICE")
public interface StudentClient {
    @PostMapping("students/register")
    public ResponseEntity<Student> registerStudent(@RequestBody Student student);
    @PostMapping(value = "students/registerFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerFile(@RequestPart("file") MultipartFile file);
}