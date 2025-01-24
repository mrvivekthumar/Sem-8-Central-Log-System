package com.example.studentservice.Controller;

import com.example.studentservice.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @PostMapping("submit")
    public ResponseEntity<String> submitReport(
            @RequestParam("studentId") int studentId,
            @RequestParam("projectId") int projectId,
            @RequestParam("file") MultipartFile file
    ){
        System.out.println("Student service");
        return reportService.submitReport(studentId,projectId,file);

    }

}
