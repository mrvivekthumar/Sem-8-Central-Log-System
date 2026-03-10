package com.example.studentservice.controller;

import com.example.studentservice.domain.*;
import com.example.studentservice.repository.ReportRepository;
import com.example.studentservice.repository.StudentProjectRepository;
import com.example.studentservice.repository.StudentRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/reports")
@Slf4j
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * GET /student/reports/project/{projectId}
     * Get report by project ID
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getReportByProject(@PathVariable int projectId) {
        log.info("GET /reports/project/{}", projectId);
        Optional<Report> report = reportRepository.findReportByProjectId(projectId);
        return report.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /student/reports/project/{projectId}/report
     * Get report by project ID (alternative endpoint)
     */
    @GetMapping("/project/{projectId}/report")
    public ResponseEntity<?> getReportForProject(@PathVariable int projectId) {
        log.info("GET /reports/project/{}/report", projectId);
        Optional<Report> report = reportRepository.findReportByProjectId(projectId);
        return report.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /student/reports/student/{studentId}/project/{projectId}/submit
     * Submit a report (with file upload)
     */
    @PostMapping("/student/{studentId}/project/{projectId}/submit")
    public ResponseEntity<?> submitReport(
            @PathVariable int studentId,
            @PathVariable int projectId,
            @RequestParam("file") MultipartFile file) {
        log.info("POST /reports/student/{}/project/{}/submit", studentId, projectId);

        StudentProject sp = studentProjectRepository.findByStudent_StudentIdAndProjectId(studentId, projectId);
        if (sp == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Student-project assignment not found"));
        }

        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Student not found"));
        }

        // Store file name as document URL (in production, upload to Cloudinary/S3)
        String documentUrl = "uploads/reports/" + studentId + "_" + projectId + "_" + file.getOriginalFilename();

        // Check if report already exists for this project
        Optional<Report> existingReport = reportRepository.findReportByProjectId(projectId);
        Report report;
        if (existingReport.isPresent()) {
            report = existingReport.get();
            report.setDocumentUrl(documentUrl);
            report.setSubmissionDate(LocalDate.now());
            report.setStatus(ReportStatus.PENDING);
            report.setFinalSubmission(false);
        } else {
            report = new Report();
            report.setStudentProject(sp);
            report.setSubmittedBy(student);
            report.setDocumentUrl(documentUrl);
            report.setSubmissionDate(LocalDate.now());
            report.setStatus(ReportStatus.PENDING);
            report.setFinalSubmission(false);
        }

        Report saved = reportRepository.save(report);
        log.info("Report saved with ID: {}", saved.getReportId());
        return ResponseEntity.ok(saved);
    }

    /**
     * PUT /student/reports/report/{reportId}/final-submit
     * Mark report as final submission
     */
    @PutMapping("/report/{reportId}/final-submit")
    public ResponseEntity<?> finalSubmit(@PathVariable int reportId) {
        log.info("PUT /reports/report/{}/final-submit", reportId);
        Optional<Report> optReport = reportRepository.findById(reportId);
        if (optReport.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Report report = optReport.get();
        report.setFinalSubmission(true);
        report.setStatus(ReportStatus.SUBMITTED);
        Report saved = reportRepository.save(report);
        return ResponseEntity.ok(saved);
    }

    /**
     * DELETE /student/reports/report/{reportId}
     * Delete a report
     */
    @DeleteMapping("/report/{reportId}")
    public ResponseEntity<?> deleteReport(@PathVariable int reportId) {
        log.info("DELETE /reports/report/{}", reportId);
        if (!reportRepository.existsById(reportId)) {
            return ResponseEntity.notFound().build();
        }
        reportRepository.deleteById(reportId);
        return ResponseEntity.ok(Map.of("message", "Report deleted successfully"));
    }
}
