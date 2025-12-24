package com.example.studentservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.studentservice.domain.Report;
import com.example.studentservice.security.CurrentUser;
import com.example.studentservice.service.ReportService;

@RestController
@RequestMapping("/students/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private CurrentUser currentUser;

    /*
     * =========================
     * STUDENT ACTIONS
     * =========================
     */

    @PostMapping("/projects/{projectId}/submit")
    public ResponseEntity<Report> submitReport(
            @PathVariable int projectId,
            @RequestParam MultipartFile file) {

        int studentId = currentUser.getUserId().intValue();
        return reportService.submitReport(studentId, projectId, file);
    }

    @PutMapping("/{reportId}/final-submit")
    public ResponseEntity<String> finalSubmit(@PathVariable int reportId) {
        return reportService.finalSubmit(reportId);
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<String> deleteReport(@PathVariable int reportId) {
        return reportService.deleteReport(reportId);
    }

    /*
     * =========================
     * READ-ONLY
     * =========================
     */

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<Report> getTeamReport(@PathVariable int projectId) {
        return reportService.findReportForTeamByProjectId(projectId);
    }
}
