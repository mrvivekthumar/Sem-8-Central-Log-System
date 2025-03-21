package com.example.studentservice.Service;

import com.example.studentservice.Dao.ReportDao;
import com.example.studentservice.Dao.StudentProjectDao;
import com.example.studentservice.Feign.FacultyInterface;
import com.example.studentservice.Model.Report;
import com.example.studentservice.Model.ReportStatus;
import com.example.studentservice.Model.Student;
import com.example.studentservice.Model.StudentProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

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
    private StudentProjectService studentProjectService;
    public ResponseEntity<Report> submitReport(int studentId, int projectId, MultipartFile file) {
        try{

            StudentProject studentProject=studentProjectDao.findByStudent_StudentIdAndProjectId(studentId,projectId);
            String fileUrl=cloudnaryService.uploadFile(file);
            Report report=new Report();
            report.setStudentProject(studentProject);
            report.setDocumentUrl(fileUrl);
            report.setSubmissionDate(LocalDate.now());
            report.setStatus(ReportStatus.PENDING);
            report.setFeedback(null);
            reportDao.save(report);
            return new ResponseEntity<>(report,HttpStatus.OK);


        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<List<Report>> findReportForTeamByProjectId(int projectId) {
        try{
            List<Report> reports=reportDao.findReportsByProjectId(projectId);
            return new ResponseEntity<>(reports,HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
