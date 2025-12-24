package com.example.studentservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentservice.security.CurrentUser;
import com.example.studentservice.service.ReportReviewService;

@RestController
@RequestMapping("/students/reviews")
public class ReportReviewController {

    @Autowired
    private ReportReviewService reportReviewService;

    @Autowired
    private CurrentUser currentUser;

    /*
     * =========================
     * STUDENT APPROVALS
     * =========================
     */

    @PostMapping("/{reportId}/approve")
    public ResponseEntity<String> approveReport(@PathVariable int reportId) {
        int studentId = currentUser.getUserId().intValue();
        return reportReviewService.approveReport(studentId, reportId);
    }

    @PutMapping("/{reportId}/reject")
    public ResponseEntity<String> rejectReport(@PathVariable int reportId) {
        int studentId = currentUser.getUserId().intValue();
        return reportReviewService.rejectReport(studentId, reportId);
    }

    /*
     * =========================
     * STATUS CHECKS
     * =========================
     */

    @GetMapping("/{reportId}/is-approved")
    public ResponseEntity<Boolean> isReportFullyApproved(@PathVariable int reportId) {
        return reportReviewService.isFullyApproved(reportId);
    }

    @GetMapping("/{reportId}/me/is-approved")
    public ResponseEntity<Boolean> isMyApprovalDone(@PathVariable int reportId) {
        int studentId = currentUser.getUserId().intValue();
        return reportReviewService.isStudentApproved(reportId, studentId);
    }
}
