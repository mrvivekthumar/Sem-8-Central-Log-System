package com.example.studentservice.Service;

import com.example.studentservice.Dao.ReportDao;
import com.example.studentservice.Dao.ReportReviewDao;
import com.example.studentservice.Dao.StudentProjectDao;
import com.example.studentservice.Feign.FacultyInterface;
import com.example.studentservice.Model.*;
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
            ResponseEntity<Student> existStudent = studentService.getStudentById(studentId);

            if (!existStudent.getStatusCode().is2xxSuccessful() || existStudent.getBody() == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            StudentProject studentProject = studentProjectDao.findByStudent_StudentIdAndProjectId(studentId, projectId);
            if (studentProject == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // No such project-student link
            }

            String fileUrl = cloudnaryService.uploadFile(file);
            System.out.println("File URL: " + fileUrl);

            Report report = new Report();
            report.setStudentProject(studentProject);
            report.setDocumentUrl(fileUrl);
            report.setSubmittedBy(existStudent.getBody()); // Ensure `submittedBy` exists in the entity
            report.setSubmissionDate(LocalDate.now());
            report.setStatus(ReportStatus.PENDING);
            ReportReview review=new ReportReview();
            review.setReviewedBy(existStudent.getBody());
            review.setApproved(true);
            review.setReport(report);
            System.out.println("Before saved");
            reportDao.save(report);
            reportReviewDao.save(review);


            System.out.println("After saved");
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


    public ResponseEntity<String> deleteReport(int reportId) {
        try{
            Optional<Report>report=reportDao.findById(reportId);
            if(report.isPresent()){
                ResponseEntity<String> success=reportReviewService.deleteReviewsByReportId(reportId);
                System.out.println();
                reportDao.deleteById(reportId);
                return new ResponseEntity<>("Report deleted successfully and "+success,HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("Report not found",HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
