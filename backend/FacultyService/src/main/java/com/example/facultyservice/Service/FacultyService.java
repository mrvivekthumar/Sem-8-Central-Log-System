package com.example.facultyservice.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.example.facultyservice.client.AuthInterface;
import com.example.facultyservice.client.StudentInterface;
import com.example.facultyservice.controller.SheetHandler;
import com.example.facultyservice.domain.Faculty;
import com.example.facultyservice.domain.Project;
import com.example.facultyservice.domain.Status;
import com.example.facultyservice.domain.Student;
import com.example.facultyservice.domain.StudentAvaibility;
import com.example.facultyservice.repository.FacultyDao;
import com.example.facultyservice.repository.ProjectDao;

import jakarta.transaction.Transactional;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    @Autowired
    private AuthInterface authInterface;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private FacultyDao facultyDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private ProjectService projectService;

    // private final String STUDENT_SERVICE =
    // "http://localhost:8765/STUDENT-SERVICE/api/studentProject/";
    // private final String STUDENT =
    // "http://localhost:8765/STUDENT-SERVICE/students";
    @Autowired
    private StudentInterface studentInterface;

    public ResponseEntity<Faculty> registerFaculty(Faculty faculty) {
        try {
            return new ResponseEntity<>(facultyDao.save(faculty), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<List<Project>> getProjectsById(int facultyId) {
        try {
            // Fix: Cast int to Integer for findById
            Optional<Faculty> facultyOpt = facultyDao.findById(facultyId);
            if (!facultyOpt.isPresent()) {
                logger.error("Faculty not found with id: {}", facultyId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Faculty faculty = facultyOpt.get();
            List<Project> projects = projectDao.findByFacultyId(faculty.getFId());
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to get projects for faculty {}: {}", facultyId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> registerFacultyWithAuth(Faculty faculty) {
        try {
            // Step 1: Create user credentials
            Map<String, Object> user = new HashMap<>();
            user.put("email", faculty.getEmail()); // Changed from username
            user.put("password", faculty.getPassword());
            user.put("role", "FACULTY"); // Changed from userRole

            logger.info("Registering faculty user: {}", user.get("email"));

            // Call auth service with single user
            ResponseEntity<String> authResponse = authInterface.addNewUser(user);

            if (!authResponse.getStatusCode().is2xxSuccessful()) {
                return new ResponseEntity<>("Auth registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Step 2: Create faculty profile
            faculty.setPassword(null); // Don't store password
            facultyDao.save(faculty);

            logger.info("Faculty registration successful: {}", faculty.getEmail());
            return new ResponseEntity<>("Faculty registration successful", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Faculty registration failed: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<String>> getProjectsByName(int fId) {
        try {
            Optional<Faculty> facultyOpt = facultyDao.findById(fId);
            if (!facultyOpt.isPresent()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
            }

            Faculty faculty = facultyOpt.get();
            List<Project> projects = projectDao.findByFacultyId(faculty.getFId());
            List<String> projectNames = new ArrayList<>();
            for (Project project : projects) {
                projectNames.add(project.getTitle());
            }
            return new ResponseEntity<>(projectNames, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to get project names for faculty {}: {}", fId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteProject(int p_id) {
        try {
            projectDao.deleteById(p_id);
            return new ResponseEntity<>("Project deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Student>> getStudentsByProject(int projectId) {
        try {
            ResponseEntity<List<Student>> response = studentInterface.getStudents(projectId);
            List<Student> students = response.getBody();
            List<Student> availableStudents = students.stream()
                    .filter(s -> s.getStudentAvaibility() == StudentAvaibility.AVAILABLE)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(availableStudents, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    public ResponseEntity<Integer> getStudentCountByProjectId(int projectId) {
        try {
            Integer count = studentInterface.getStudentCountByProject(projectId).getBody();
            // ResponseEntity<Integer> responseEntity =
            // restTemplate.exchange(STUDENT_SERVICE + "/" + projectId + "/student-count",
            // HttpMethod.GET, null, Integer.class);
            // Integer count = responseEntity.getBody();
            System.out.println(count.intValue());
            if (count != null) {
                return new ResponseEntity<>(count.intValue(), HttpStatus.OK);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    public ResponseEntity<String> getApprovedStudent(int facultyId, int projectId, List<Integer> studentIds) {
        try {
            ResponseEntity<List<Student>> responseEntityStudent = studentInterface.getStudentsById(studentIds);

            List<Student> students = null;
            if (responseEntityStudent.getStatusCode() == HttpStatus.OK) {
                students = responseEntityStudent.getBody();
                logger.info("Retrieved {} students", students != null ? students.size() : 0);
            }

            if (students == null || students.isEmpty()) {
                return new ResponseEntity<>("No students found", HttpStatus.NOT_FOUND);
            }

            for (Student s : students) {
                s.setStudentAvaibility(StudentAvaibility.NOT_AVAILABLE);
                studentInterface.makeUnavailable(s.getStudentId());
            }

            for (int studentId : studentIds) {
                studentInterface.updateStatus(studentId, projectId);
            }

            Optional<Project> projectOpt = projectDao.findById(projectId);
            if (!projectOpt.isPresent()) {
                return new ResponseEntity<>("Project not found", HttpStatus.NOT_FOUND);
            }

            Project project = projectOpt.get();

            // Fix: Compare int values directly
            if (project.getFaculty().getFId() != facultyId) {
                return new ResponseEntity<>("Unauthorized faculty for this project", HttpStatus.UNAUTHORIZED);
            }

            project.setStatus(Status.APPROVED);
            projectDao.save(project);
            logger.info("Project {} approved and assigned to students", projectId);

            return new ResponseEntity<>(
                    "Project is given to students by " + project.getFaculty().getName(),
                    HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Failed to approve students for project {}: {}", projectId, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Project>> createProjects(List<Project> projects, int facultyId) {
        try {
            Optional<Faculty> facultyOpt = facultyDao.findById(facultyId);
            if (!facultyOpt.isPresent()) {
                logger.error("Faculty not found with id: {}", facultyId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Faculty faculty = facultyOpt.get();

            for (Project p : projects) {
                p.setFaculty(faculty);
            }

            List<Project> savedProjects = projectDao.saveAll(projects);
            logger.info("Created {} projects for faculty {}", savedProjects.size(), facultyId);

            return new ResponseEntity<>(savedProjects, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to create projects for faculty {}: {}", facultyId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<String> registerFileForFaculty(@RequestPart("file") MultipartFile file) {
        try {
            List<Faculty> faculties = new ArrayList<>();
            List<Map<String, Object>> users = new ArrayList<>();
            int recordCount = 0;

            // Use streaming implementation
            try (InputStream inputStream = file.getInputStream()) {
                OPCPackage pkg = OPCPackage.open(inputStream);
                XSSFReader reader = new XSSFReader(pkg);
                SharedStrings sst = reader.getSharedStringsTable();

                XMLReader parser = XMLReaderFactory.createXMLReader();
                SheetHandler handler = new SheetHandler(sst, faculties, users, facultyDao);
                parser.setContentHandler(handler);

                InputStream sheetStream = reader.getSheetsData().next();
                InputSource sheetSource = new InputSource(sheetStream);
                parser.parse(sheetSource);
                sheetStream.close();

                recordCount = handler.getRowCount();

                if (handler.getDuplicateEmail() != null) {
                    return new ResponseEntity<>("Email " + handler.getDuplicateEmail() + " is already registered",
                            HttpStatus.CONFLICT);
                }
            }

            // Save faculties to database
            if (!faculties.isEmpty()) {
                int batchSize = 500;
                for (int i = 0; i < faculties.size(); i += batchSize) {
                    int endIndex = Math.min(i + batchSize, faculties.size());
                    facultyDao.saveAll(faculties.subList(i, endIndex));
                }
                logger.info("Saved {} faculties to database", faculties.size());
            }

            // Register users in Auth Service one by one
            int successCount = 0;
            int failCount = 0;
            List<String> failedEmails = new ArrayList<>();

            for (Map<String, Object> user : users) {
                try {
                    ResponseEntity<String> response = authInterface.addNewUser(user);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        successCount++;
                        logger.info("Registered faculty in Auth Service: {}", user.get("email"));
                    } else {
                        failCount++;
                        failedEmails.add((String) user.get("email"));
                        logger.warn("Failed to register faculty in Auth Service: {}", user.get("email"));
                    }
                } catch (Exception e) {
                    failCount++;
                    failedEmails.add((String) user.get("email"));
                    logger.error("Error registering faculty {}: {}", user.get("email"), e.getMessage());
                }
            }

            String resultMessage = String.format(
                    "Bulk registration completed. Faculties added: %d, Auth registrations - Success: %d, Failed: %d",
                    faculties.size(), successCount, failCount);

            if (!failedEmails.isEmpty()) {
                resultMessage += ". Failed emails: " + String.join(", ", failedEmails);
            }

            logger.info(resultMessage);
            return new ResponseEntity<>(resultMessage, HttpStatus.OK);

        } catch (IOException e) {
            logger.error("File processing failed due to IO error", e);
            return new ResponseEntity<>("File processing failed due to IO error", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("File upload failed: {}", e.getMessage());
            return new ResponseEntity<>("File upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Integer> getCount() {
        try {
            Integer count = facultyDao.findTotalUsers();
            return new ResponseEntity<>(count, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Faculty> findByEmail(String email) {
        try {
            System.out.println(email);
            Faculty f = facultyDao.findByEmail(email);
            if (f == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(f, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Faculty>> findAll() {
        try {
            List<Faculty> faculties = facultyDao.findAll();
            return new ResponseEntity<>(faculties, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Integer> getTotalApprovedProjects() {
        try {
            Optional<Integer> existCount = projectDao.findTotalApprovedProjects(Status.APPROVED);
            int count = 0;
            if (existCount.isPresent()) {
                count = existCount.get();
            }
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> updateRatings(int projectId, float ratings) {
        try {
            ResponseEntity<String> success = studentInterface.updateRatings(projectId, ratings);
            return new ResponseEntity<>(success.getBody(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Boolean> getIsComplete(int projectId) {
        try {
            Optional<Project> project = projectDao.findById(projectId);
            if (project.isPresent()) {
                return new ResponseEntity<>(project.get().getStatus() == Status.COMPLETED, HttpStatus.OK);
            }
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
