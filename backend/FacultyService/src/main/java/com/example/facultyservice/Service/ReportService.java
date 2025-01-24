package com.example.facultyservice.Service;

import com.example.facultyservice.Dao.ProjectDao;
import com.example.facultyservice.Dao.ReportDao;
import com.example.facultyservice.Model.Project;
import com.example.facultyservice.Model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.facultyservice.Model.Report;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class ReportService {
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private ReportDao reportDao;
    public ResponseEntity<String> submitReport(int projectId, MultipartFile file) {
        try{
            Project project = projectDao.findById(projectId)
                    .orElseThrow(() -> new Exception("Project not found"));

            if(project.getStatus()== Status.APPROVED){
                Report report = new Report();
                report.setFileName(file.getOriginalFilename());
                report.setFileData(file.getBytes());
                report.setContentType(file.getContentType());
                report.setSubmissionDate(LocalDateTime.now());
                report.setProject(project);
                reportDao.save(report);
                return new ResponseEntity<>("Submitted", HttpStatus.OK);
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;


    }
}
