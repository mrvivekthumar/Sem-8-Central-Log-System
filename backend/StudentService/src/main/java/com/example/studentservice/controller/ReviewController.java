package com.example.studentservice.controller;

import com.example.studentservice.domain.Report;
import com.example.studentservice.domain.ReportReview;
import com.example.studentservice.domain.ReportStatus;
import com.example.studentservice.domain.Student;
import com.example.studentservice.repository.ReportRepository;
import com.example.studentservice.repository.ReportReviewRepository;
import com.example.studentservice.repository.StudentRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/review")
@Slf4j
public class ReviewController {

    @Autowired
    private ReportReviewRepository reportReviewRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * GET /student/review/report/{reportId}/is-approved
     * Check if all team members have approved a report
     */
    @GetMapping("/report/{reportId}/is-approved")
    public ResponseEntity<Boolean> isApproved(@PathVariable int reportId) {
        log.info("GET /review/report/{}/is-approved", reportId);
        Optional<List<ReportReview>> reviews = reportReviewRepository.findByReport_ReportId(reportId);
        if (reviews.isEmpty() || reviews.get().isEmpty()) {
            return ResponseEntity.ok(false);
        }
        boolean allApproved = reviews.get().stream().allMatch(ReportReview::isApproved);
        return ResponseEntity.ok(allApproved);
    }

    /**
     * POST /student/review/{reportId}/approve/student/{studentId}
     * Approve a report by a student
     */
    @PostMapping("/{reportId}/approve/student/{studentId}")
    public ResponseEntity<?> approveReport(
            @PathVariable int reportId,
            @PathVariable int studentId) {
        log.info("POST /review/{}/approve/student/{}", reportId, studentId);

        Optional<Report> optReport = reportRepository.findById(reportId);
        if (optReport.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Student> optStudent = studentRepository.findById(studentId);
        if (optStudent.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Student not found"));
        }

        // Check if review already exists
        Optional<ReportReview> existing = reportReviewRepository
                .findByReport_ReportIdAndReviewedBy_StudentId(reportId, studentId);

        ReportReview review;
        if (existing.isPresent()) {
            review = existing.get();
        } else {
            review = new ReportReview();
            review.setReport(optReport.get());
            review.setReviewedBy(optStudent.get());
        }
        review.setApproved(true);
        ReportReview saved = reportReviewRepository.save(review);

        // Check if all reviews are now approved
        Optional<List<ReportReview>> allReviews = reportReviewRepository.findByReport_ReportId(reportId);
        boolean allApproved = allReviews.isPresent() && !allReviews.get().isEmpty()
                && allReviews.get().stream().allMatch(ReportReview::isApproved);

        if (allApproved) {
            Report report = optReport.get();
            report.setStatus(ReportStatus.APPROVED);
            reportRepository.save(report);
        }

        return ResponseEntity.ok(Map.of("approved", true, "reviewId", saved.getReviewId()));
    }

    /**
     * PUT /student/review/{reportId}/reject/student/{studentId}
     * Reject a report by a student
     */
    @PutMapping("/{reportId}/reject/student/{studentId}")
    public ResponseEntity<?> rejectReport(
            @PathVariable int reportId,
            @PathVariable int studentId) {
        log.info("PUT /review/{}/reject/student/{}", reportId, studentId);

        Optional<Report> optReport = reportRepository.findById(reportId);
        if (optReport.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Student> optStudent = studentRepository.findById(studentId);
        if (optStudent.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Student not found"));
        }

        Optional<ReportReview> existing = reportReviewRepository
                .findByReport_ReportIdAndReviewedBy_StudentId(reportId, studentId);

        ReportReview review;
        if (existing.isPresent()) {
            review = existing.get();
        } else {
            review = new ReportReview();
            review.setReport(optReport.get());
            review.setReviewedBy(optStudent.get());
        }
        review.setApproved(false);
        reportReviewRepository.save(review);

        // Mark report as needing resubmission
        Report report = optReport.get();
        report.setStatus(ReportStatus.NEEDS_RESUBMISSION);
        reportRepository.save(report);

        return ResponseEntity.ok(Map.of("rejected", true));
    }
}
