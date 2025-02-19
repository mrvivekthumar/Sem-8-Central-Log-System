package com.example.adminservice.Feign;

import com.example.adminservice.Model.Faculty;
import com.example.adminservice.Model.Student;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "FACULTY-SERVICE")
public interface FacultyClient {
    @PostMapping("faculty/register")
    public ResponseEntity<Faculty> registerFaculty(@RequestBody Faculty faculty);
    @PostMapping(value = "api/faculty/registerFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerFile(@RequestPart("file") MultipartFile file);
    @GetMapping("api/faculty/count")
    Integer getCount();
}
