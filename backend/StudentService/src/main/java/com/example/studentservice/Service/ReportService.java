package com.example.studentservice.Service;

import com.example.studentservice.Dao.ReportDao;
import com.example.studentservice.Dao.ReportReviewDao;
import com.example.studentservice.Dao.StudentProjectDao;
import com.example.studentservice.Feign.FacultyInterface;
import com.example.studentservice.Model.*;
import com.example.studentservice.Vo.Status;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentProjectDao studentProjectDao;
    @Autowired
    private FacultyInterface facultyInterface;
    @Autowired
    private CloudinaryService cloudnaryService;
    @Autowired
    private ReportDao reportDao;
    @Autowired
    private ReportReviewService reportReviewService;
    @Autowired
    private ReportReviewDao reportReviewDao;
    public ResponseEntity<Report> submitReport(int studentId, int projectId, MultipartFile file) {
        try {
            // Check if student exists
            ResponseEntity<Student> existStudent = studentService.getStudentById(studentId);
            if (!existStudent.getStatusCode().is2xxSuccessful() || existStudent.getBody() == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Check if student is part of the project
            StudentProject studentProject = studentProjectDao.findByStudent_StudentIdAndProjectId(studentId, projectId);
            if (studentProject == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // No such project-student link
            }

            // Upload file
            String fileUrl = cloudnaryService.uploadFile(file);
            System.out.println("File URL: " + fileUrl);

            // Create Report
            Report report = new Report();
            report.setStudentProject(studentProject);
            report.setDocumentUrl(fileUrl);
            report.setSubmittedBy(existStudent.getBody());
            report.setSubmissionDate(LocalDate.now());
            report.setStatus(ReportStatus.PENDING);

            // Save Report first
            reportDao.save(report);
            System.out.println("Report Saved");

            // Fetch all teammates of the project
            List<StudentProject> teammates = studentProjectDao.findByProjectIdAndStatus(projectId, Status.APPROVED);

            // Add reviews for all teammates
            for (StudentProject teammate : teammates) {
                ReportReview review = new ReportReview();
                review.setReport(report);
                review.setReviewedBy(teammate.getStudent());

                // Set approved = true only for the submitting student
                review.setApproved(teammate.getStudent().getStudentId() == studentId);

                reportReviewDao.save(review);
            }

            System.out.println("Reviews Saved");
            return ResponseEntity.ok(report);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    public ResponseEntity<Report> findReportForTeamByProjectId(int projectId) {
        try{
            Optional<Report>report=reportDao.findReportByProjectId(projectId);
            if(report.isPresent()){
                Report report1=report.get();
                return new ResponseEntity<>(report1,HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<String> deleteReport(int reportId) {
        try {
            Optional<Report> reportOptional = reportDao.findById(reportId);
            if (reportOptional.isPresent()) {
                Report report = reportOptional.get();

                // Get the associated StudentProject
                StudentProject studentProject = report.getStudentProject();

                // First, delete related reviews
                ResponseEntity<String> success = reportReviewService.deleteReviewsByReportId(reportId);

                // Break the bidirectional relationship
                if (studentProject != null) {
                    studentProject.setReport(null);
                }

                // Delete the report
                reportDao.delete(report);

                return new ResponseEntity<>("Report deleted successfully and " + success, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Report not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<String> finalSubmit(int reportId) {
        try{
            Optional<Report>report=reportDao.findById(reportId);
            if(report.isPresent()){
                Report report1=report.get();
                report1.setFinalSubmission(true);
//                report1.setStatus(ReportStatus.SUBMITTED);
                reportDao.save(report1);
                return new ResponseEntity<>("Report submitted successfully",HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("Report not found",HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
