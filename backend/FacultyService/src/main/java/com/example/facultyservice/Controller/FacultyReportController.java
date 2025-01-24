package com.example.facultyservice.Controller;

import com.example.facultyservice.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/reports")
public class FacultyReportController {
    @Autowired
    private ReportService reportService;

    @PostMapping(path = "{projectId}/report",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> submitReport(@PathVariable int projectId, @RequestPart("file") MultipartFile file){
        return reportService.submitReport(projectId,file);
    }


}
