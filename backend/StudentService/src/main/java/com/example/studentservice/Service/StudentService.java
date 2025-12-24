package com.example.studentservice.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.example.studentservice.SheetHandler;
import com.example.studentservice.client.AuthInterface;
import com.example.studentservice.client.FacultyInterface;
import com.example.studentservice.client.dto.Project;
import com.example.studentservice.client.dto.Status;
import com.example.studentservice.client.dto.UserCredential;
import com.example.studentservice.domain.Student;
import com.example.studentservice.domain.StudentAvaibility;
import com.example.studentservice.domain.StudentProject;
import com.example.studentservice.exception.InvalidOperationException;
import com.example.studentservice.exception.ResourceNotFoundException;
import com.example.studentservice.repository.StudentDao;
import com.example.studentservice.repository.StudentProjectDao;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final CloudinaryService cloudinaryService;
    private final StudentDao studentDao;
    private final StudentProjectDao studentProjectDao;
    private final StudentProjectService studentProjectService;
    private final FacultyInterface facultyInterface;
    private final AuthInterface authInterface;

    /*
     * =========================
     * REGISTRATION
     * =========================
     */

    public Student registerStudent(Student student) {
        logger.info("Registering student: {}", student.getEmail());
        return studentDao.save(student);
    }

    @Transactional
    public String registerFile(MultipartFile file) {
        try {
            List<Student> students = new ArrayList<>();
            List<UserCredential> users = new ArrayList<>();

            try (InputStream inputStream = file.getInputStream()) {
                OPCPackage pkg = OPCPackage.open(inputStream);
                XSSFReader reader = new XSSFReader(pkg);
                SharedStrings sst = reader.getSharedStringsTable();

                XMLReader parser = XMLReaderFactory.createXMLReader();
                SheetHandler handler = new SheetHandler(sst, students, users, studentDao);
                parser.setContentHandler(handler);

                InputStream sheetStream = reader.getSheetsData().next();
                parser.parse(new InputSource(sheetStream));
                sheetStream.close();

                if (handler.getDuplicateEmail() != null) {
                    throw new InvalidOperationException(
                            "Email already exists: " + handler.getDuplicateEmail());
                }
            }

            studentDao.saveAll(students);
            authInterface.addNewUser(users);

            return "Bulk registration completed. Records added: " + students.size();

        } catch (Exception e) {
            logger.error("Bulk registration failed", e);
            throw new InvalidOperationException("Bulk registration failed");
        }
    }

    /*
     * =========================
     * PROJECT APPLICATION
     * =========================
     */

    public void applyProject(int studentId, int projectId) {

        Student student = studentDao.findStudentByStudentId(studentId);
        if (student == null) {
            throw new ResourceNotFoundException("Student not found");
        }

        if (student.getStudentAvaibility() == StudentAvaibility.NOT_AVAILABLE) {
            throw new InvalidOperationException(
                    "Student already working on a project");
        }

        if (studentProjectDao.existsByStudent_StudentIdAndProjectId(studentId, projectId)) {
            throw new InvalidOperationException("Already applied for this project");
        }

        Project project = facultyInterface.getProjectById(projectId).getBody();
        if (project == null) {
            throw new ResourceNotFoundException("Project not found");
        }

        Integer maxPref = studentProjectDao.findMaxPreferenceByStudentId(studentId);
        int preference = (maxPref == null) ? 1 : maxPref + 1;

        StudentProject sp = new StudentProject();
        sp.setStudent(student);
        sp.setProjectId(projectId);
        sp.setPreference(preference);
        sp.setStatus(Status.PENDING);
        sp.setApplicationDate(LocalDate.now());

        studentProjectDao.save(sp);
    }

    public void withdrawProject(int studentId, int projectId) {

        StudentProject sp = studentProjectDao
                .findByStudent_StudentIdAndProjectId(studentId, projectId);

        if (sp == null) {
            throw new InvalidOperationException("Project not applied");
        }

        if (sp.getStatus() == Status.APPROVED) {
            throw new InvalidOperationException("Cannot withdraw approved project");
        }

        int removedPref = sp.getPreference();
        studentProjectDao.delete(sp);

        List<StudentProject> remaining = studentProjectDao.findByStudent_StudentId(studentId);

        for (StudentProject p : remaining) {
            if (p.getPreference() > removedPref) {
                p.setPreference(p.getPreference() - 1);
            }
        }

        studentProjectDao.saveAll(remaining);
    }

    /*
     * =========================
     * STUDENT PROFILE
     * =========================
     */

    public Student getStudentById(int studentId) {
        return studentDao.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with id: " + studentId));
    }

    public Student updateStudentDetails(int studentId, Student updated) {
        Student student = getStudentById(studentId);

        student.setGithubProfileLink(updated.getGithubProfileLink());
        student.setSkills(updated.getSkills());
        student.setSemesterNo(updated.getSemesterNo());
        student.setCgpa(updated.getCgpa());
        student.setBio(updated.getBio());
        student.setLinkedInUrl(updated.getLinkedInUrl());
        student.setPhoneNo(updated.getPhoneNo());

        return studentDao.save(student);
    }

    public Student updateGithubLink(int studentId, String githubLink) {
        Student student = getStudentById(studentId);
        student.setGithubProfileLink(githubLink);
        return studentDao.save(student);
    }

    /*
     * =========================
     * MEDIA
     * =========================
     */

    public Student uploadAvatar(int studentId, MultipartFile image) {
        Student student = getStudentById(studentId);
        student.setImageUrl(cloudinaryService.uploadFile(image));
        return studentDao.save(student);
    }

    /*
     * =========================
     * READ-ONLY / SYSTEM
     * =========================
     */

    public List<Project> getAllProjects() {
        return facultyInterface.getAllProjects().getBody();
    }

    public List<Student> getAllStudentsById(List<Integer> ids) {
        return studentDao.findAllByStudentId(ids);
    }

    public int getCount() {
        return studentDao.findTotalUsers();
    }

    public List<Integer> getCompletedProjects(int studentId) {
        return studentProjectService.findCompletedProjects(studentId);
    }
}
