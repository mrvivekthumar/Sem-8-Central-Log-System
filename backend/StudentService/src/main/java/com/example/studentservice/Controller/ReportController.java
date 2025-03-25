package com.example.studentservice.Controller;

import com.example.studentservice.Model.Report;
import com.example.studentservice.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @PostMapping("student/{studentId}/project/{projectId}/submit")
    public ResponseEntity<Report> submitReport(@PathVariable int studentId, @PathVariable int projectId, @RequestParam MultipartFile file){
        return reportService.submitReport(studentId,projectId,file);
    }
    @GetMapping("project/{projectId}/report")
    public ResponseEntity<Report>getTeamReport(@PathVariable int projectId){
       return reportService.findReportForTeamByProjectId(projectId);
    }
    @DeleteMapping("report/{reportId}")
    public ResponseEntity<String> deleteReport(@PathVariable int reportId){
        return reportService.deleteReport(reportId);
    }



}
