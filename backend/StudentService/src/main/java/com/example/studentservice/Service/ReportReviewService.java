package com.example.studentservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.studentservice.domain.Report;
import com.example.studentservice.domain.ReportReview;
import com.example.studentservice.domain.Student;
import com.example.studentservice.repository.ReportDao;
import com.example.studentservice.repository.ReportReviewDao;

@Service
public class ReportReviewService {
    @Autowired
    private ReportReviewDao reportReviewDao;
    @Autowired
    private ReportDao reportDao;
    @Autowired
    private StudentService studentService;

    public ResponseEntity<String> approveReport(int studentId, int reportId) {
        return updateReview(studentId, reportId, true);
    }

    public ResponseEntity<String> rejectReport(int studentId, int reportId) {
        return updateReview(studentId, reportId, false);
    }

    private ResponseEntity<String> updateReview(int studentId, int reportId, boolean isApproved) {
        try {
            // Fetch report details
            Optional<Report> reportOptional = reportDao.findById(reportId);
            if (reportOptional.isEmpty()) {
                return new ResponseEntity<>("Report not found", HttpStatus.NOT_FOUND);
            }
            Report report = reportOptional.get();

            // Fetch student details - FIXED: Now returns Student directly
            Student student = studentService.getStudentById(studentId);

            // Ensure the report submitter cannot review it
            if (report.getSubmittedBy().getStudentId() == studentId) {
                return new ResponseEntity<>("You cannot review your own report", HttpStatus.FORBIDDEN);
            }

            // Check if this student has already reviewed the report
            Optional<ReportReview> existingReview = reportReviewDao
                    .findByReport_ReportIdAndReviewedBy_StudentId(reportId, studentId);

            ReportReview review;
            if (existingReview.isPresent()) {
                review = existingReview.get();
            } else {
                review = new ReportReview();
                review.setReport(report);
                review.setReviewedBy(student);
            }

            // Set approval or rejection
            review.setApproved(isApproved);
            reportReviewDao.save(review);

            // Check if all students have approved
            reportDao.save(report);

            return ResponseEntity
                    .ok(isApproved ? "Your approval has been recorded" : "Your rejection has been recorded");

        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Boolean> isFullyApproved(int reportId) {
        try {
            Optional<Report> optionalReport = reportDao.findById(reportId);
            if (optionalReport.isEmpty()) {
                return new ResponseEntity<>(false, HttpStatus.NOT_ACCEPTABLE);
            }
            Report report = optionalReport.get();
            Optional<List<ReportReview>> reviews = reportReviewDao.findByReport_ReportId(reportId);
            if (reviews.isPresent()) {
                List<ReportReview> reviewList = reviews.get();
                long totalReviewers = reviewList.size();
                long approvedCount = reviewList.stream().filter(ReportReview::isApproved).count();
                return new ResponseEntity<>(totalReviewers > 0 && totalReviewers == approvedCount, HttpStatus.OK);
            } else
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Boolean> isStudentApproved(int reportId, int studentId) {
        try {
            Optional<ReportReview> review = reportReviewDao.findByReport_ReportIdAndReviewedBy_StudentId(reportId,
                    studentId);
            if (review.isPresent()) {
                return ResponseEntity.ok(review.get().isApproved());
            } else {
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteReviewsByReportId(int reportId) {
        try {
            Optional<List<ReportReview>> reviews = reportReviewDao.findByReport_ReportId(reportId);
            int reviewsCount = reviews.map(List::size).orElse(0);
            if (reviews.isPresent()) {
                reportReviewDao.deleteAll(reviews.get());
            }
            return new ResponseEntity<>(reviewsCount + " Reviews deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
