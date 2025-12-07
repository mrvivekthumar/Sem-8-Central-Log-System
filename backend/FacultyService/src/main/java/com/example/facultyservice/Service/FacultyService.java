package com.example.facultyservice.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.facultyservice.Controller.SheetHandler;
import com.example.facultyservice.Dao.FacultyDao;
import com.example.facultyservice.Dao.ProjectDao;
import com.example.facultyservice.Feign.AuthInterface;
import com.example.facultyservice.Feign.StudentInterface;
import com.example.facultyservice.Model.*;
import jakarta.transaction.Transactional;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import com.example.facultyservice.notification.model.NotificationType;
import com.example.facultyservice.notification.model.NotificationRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            Faculty faculty = facultyDao.findById(facultyId).get();
            List<Project> projects = projectDao.findByFacultyId(faculty.getFId());
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<String> registerFacultyWithAuth(Faculty faculty) {
        try {
            // Step 1: Create user credentials
            Map<String, Object> user = new HashMap<>();
            user.put("username", faculty.getEmail());
            user.put("password", faculty.getPassword());
            user.put("userRole", "FACULTY");

            logger.info("Registering faculty user: {}", user.get("username"));
            ResponseEntity<String> authResponse = authInterface.addNewUser(Arrays.asList(user));

            if (authResponse.getStatusCode() != HttpStatus.OK) {
                return new ResponseEntity<>("Auth registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Step 2: Create faculty profile
            faculty.setPassword(null); // Don't store password
            facultyDao.save(faculty);

            return new ResponseEntity<>("Faculty registration successful", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Faculty registration failed: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<String>> getProjectsByName(int fId) {
        try {
            Faculty faculty = facultyDao.findById(fId).get();
            List<Project> projects = projectDao.findByFacultyId(faculty.getFId());
            List<String> projectNames = new ArrayList<>();
            for (Project project : projects) {
                projectNames.add(project.getTitle());
            }
            return new ResponseEntity<>(projectNames, HttpStatus.OK);
        } catch (Exception e) {
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
                System.out.println(students);
            }
            for (Student s : students) {
                // if (s.getStudentAvaibility() != StudentAvaibility.AVAILABLE) {
                // return new ResponseEntity<>("Student is aleready asigned ",
                // HttpStatus.CONFLICT);
                // }
                s.setStudentAvaibility(StudentAvaibility.NOT_AVAILABLE);
                System.out.println(studentIds);
                studentInterface.makeUnavailable(s.getStudentId());
                // restTemplate.put(STUDENT + "/" + s.getStudentId(), s);
            }
            for (int studentId : studentIds) {
                // String updateUrl = STUDENT_SERVICE + "/updateStatus/" + studentId + "/" +
                // projectId;
                studentInterface.updateStatus(studentId, projectId);
                // restTemplate.put(updateUrl, null);

            }

            // if (student.getStudentAvaibility() != StudentAvaibility.AVAILABLE) {
            // return new ResponseEntity<>("Student is aleready asigned ",
            // HttpStatus.CONFLICT);
            // }
            Project project = projectDao.findById(projectId).get();
            project.setStatus(Status.APPROVED);
            System.out.println("Are you update bro");
            projectDao.save(project);
            System.out.println("Are you after update bro");
            //
            //
            if (project == null) {
                return new ResponseEntity<>("Project Not found", HttpStatus.NOT_FOUND);
            }
            if (project.getFaculty().getFId() != facultyId) {

                return new ResponseEntity<>("Unauthorized faculty for this project", HttpStatus.UNAUTHORIZED);
            }

            return new ResponseEntity<>("Project is given to student " + " by " + project.getFaculty().getName(),
                    HttpStatus.OK);
            //
            // return new ResponseEntity<>("Success",HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<List<Project>> createProjects(List<Project> projects, int facultyId) {
        try {
            for (Project p : projects) {
                p.setFaculty(facultyDao.findById(facultyId).get());
            }
            return new ResponseEntity<>(projectDao.saveAll(projects), HttpStatus.OK);
        } catch (Exception e) {
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
                // Create streaming reader
                OPCPackage pkg = OPCPackage.open(inputStream);
                XSSFReader reader = new XSSFReader(pkg);
                SharedStrings sst = reader.getSharedStringsTable();

                // Get the first sheet
                // Note: XMLReaderFactory is deprecated in newer versions, use SAXParserFactory
                // instead
                XMLReader parser = XMLReaderFactory.createXMLReader();

                // Set up custom handler
                SheetHandler handler = new SheetHandler(sst, faculties, users, facultyDao);
                parser.setContentHandler(handler);

                // Process first sheet only
                InputStream sheetStream = reader.getSheetsData().next();
                InputSource sheetSource = new InputSource(sheetStream);
                parser.parse(sheetSource);
                sheetStream.close();

                recordCount = handler.getRowCount();

                // Check if handler encountered any duplicate emails
                if (handler.getDuplicateEmail() != null) {
                    return new ResponseEntity<>("Email " + handler.getDuplicateEmail() + " is already registered",
                            HttpStatus.CONFLICT);
                }
            }

            // Batch save all records
            if (!faculties.isEmpty()) {
                // Process in batches of 500
                int batchSize = 500;
                for (int i = 0; i < faculties.size(); i += batchSize) {
                    int endIndex = Math.min(i + batchSize, faculties.size());
                    facultyDao.saveAll(faculties.subList(i, endIndex));
                }

                for (int i = 0; i < users.size(); i += batchSize) {
                    int endIndex = Math.min(i + batchSize, users.size());
                    authInterface.addNewUser(users.subList(i, endIndex));
                }
            }

            return new ResponseEntity<>("File uploaded successfully: " + recordCount + " records added.",
                    HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("File processing failed due to IO error", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
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



