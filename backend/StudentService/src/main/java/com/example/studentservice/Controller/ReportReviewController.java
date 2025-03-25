package com.example.studentservice.Controller;

import com.example.studentservice.Service.ReportReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class ReportReviewController {
    @Autowired
    private ReportReviewService reportReviewService;

    // Approve report API
    @PostMapping("/{reportId}/approve/student/{studentId}")
    public ResponseEntity<String> approveReport(@PathVariable int studentId, @PathVariable int reportId) {
        return reportReviewService.approveReport(studentId, reportId);
    }
    // Reject report API
    @PutMapping("/{reportId}/reject/student/{studentId}")
    public ResponseEntity<String> rejectReport(
            @PathVariable int studentId,
            @PathVariable int reportId) {
        return reportReviewService.rejectReport(studentId, reportId);
    }
    @GetMapping("/report/{reportId}/is-approved")
    public ResponseEntity<Boolean> isReportApprovedByStudents(@PathVariable int reportId) {
       return reportReviewService.isFullyApproved(reportId);
    }
    @GetMapping("/report/{reportId}/student/{studentId}/is-approved")
    public ResponseEntity<Boolean> isStudentApprovedReport(
            @PathVariable int reportId,
            @PathVariable int studentId) {
        return reportReviewService.isStudentApproved(reportId, studentId);
    }

}
