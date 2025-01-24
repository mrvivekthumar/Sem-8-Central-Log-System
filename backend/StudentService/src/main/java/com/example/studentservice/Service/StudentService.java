package com.example.studentservice.Service;

import com.example.studentservice.Dao.StudentDao;
import com.example.studentservice.Dao.StudentProjectDao;
import com.example.studentservice.Feign.AuthInterface;
import com.example.studentservice.Model.PersonalProject;
import com.example.studentservice.Model.StudentAvaibility;
import com.example.studentservice.Model.StudentProject;
import com.example.studentservice.Vo.Project;
import com.example.studentservice.Model.Student;
import com.example.studentservice.Vo.Status;
import com.example.studentservice.Vo.UserCredential;
import com.example.studentservice.Dto.NotificationRequest;
import com.example.studentservice.Vo.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AuthInterface authInterface;
    private static final String FACULTY_SERVICE_URL = "http://localhost:8765/FACULTY-SERVICE/api/project";
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private StudentProjectDao studentProjectDao;



    public ResponseEntity<Student> registerStudent(Student student) {
        try{
                return new ResponseEntity<>(studentDao.save(student), HttpStatus.OK);
            }
            catch(Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }

//    @RabbitListener(queues = "${rabbitmq.queue}")
//    public void receiveProject(Project project) {
//        System.out.println("Received project from" + project + "by " +project.getFaculty());
//        // Save the project to the database or update the student UI
//    }


    @Transactional
    public ResponseEntity<String> registerFile(@RequestPart("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Student> students = new ArrayList<>();
            List<UserCredential> users = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Student student = new Student();
                    UserCredential user = new UserCredential();

                    if (row.getCell(1) != null && row.getCell(1).getCellType() == CellType.NUMERIC) {
                        student.setRoll_no((int) Math.round(row.getCell(1).getNumericCellValue()));
                    }

                    if (row.getCell(2) != null && row.getCell(2).getCellType() == CellType.STRING) {
                        student.setName(row.getCell(2).getStringCellValue());
                    }

                    if (row.getCell(3) != null && row.getCell(3).getCellType() == CellType.STRING) {
                        student.setDepartment(row.getCell(3).getStringCellValue());
                    }

                    if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.STRING) {
                        String email=row.getCell(4).getStringCellValue();
                        user.setUsername(email);
                        if(studentDao.existsByEmail(email)) {
                            return new ResponseEntity<>("Email is aleready registered", HttpStatus.CONFLICT);
                        }

                        student.setEmail(row.getCell(4).getStringCellValue());
                    }

                    if (row.getCell(5) != null) {
                        if (row.getCell(5).getCellType() == CellType.STRING) {
                            user.setPassword(row.getCell(5).getStringCellValue());
                        } else if (row.getCell(5).getCellType() == CellType.NUMERIC) {
                            user.setPassword(String.valueOf((int) row.getCell(5).getNumericCellValue()));
                        }
                    }

                    user.setUserRole(UserRole.STUDENT);
                    students.add(student);
                    users.add(user);
                }
            }

            studentDao.saveAll(students); // Batch insert students
            authInterface.addNewUser(users); // Batch insert users

            return new ResponseEntity<>("File uploaded successfully: " + sheet.getLastRowNum() + " records added.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    public ResponseEntity<List<Project>> getAllProjets() {
        try{
            ResponseEntity<Project[]> response = restTemplate.getForEntity(FACULTY_SERVICE_URL+"/projects", Project[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Project> projects = Arrays.asList(response.getBody());
                return new ResponseEntity<>(projects, HttpStatus.OK);
            } else {
                // Handle empty or unexpected response
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    public ResponseEntity<String> applyProject(int studentId,int projectId) {
        try {
            Student student=studentDao.findStudentByStudentId(studentId);
            if(student==null){
                return new ResponseEntity<>("Student is not found",HttpStatus.NOT_FOUND);
            }
            if(studentProjectDao.existsByStudent_StudentIdAndProjectId(studentId,projectId)){
                return new ResponseEntity<>("Student has already applied for this project",HttpStatus.CONFLICT);
            }
            // Step 1: Fetch the project using RestTemplate from the Faculty microservice
            String project_url = FACULTY_SERVICE_URL + "/" + projectId;
            ResponseEntity<Project> response = restTemplate.getForEntity(project_url, Project.class);
            Project project = response.getBody();

            // Step 2: Handle case where the project was not found
            if (project == null) {
                System.out.println("Project not found for ID: " + projectId);
                return new ResponseEntity<>("Project not found", HttpStatus.NOT_FOUND);
            }
            if(project.getStatus()==Status.APPROVED){
                new ResponseEntity<>("Student is aleready registered",HttpStatus.CONFLICT);
            }
            StudentProject studentProject=new StudentProject();
            studentProject.setStudent(student);
            studentProject.setStatus(Status.PENDING);
            studentProject.setProjectId(projectId);
            studentProject.setApplicationDate(LocalDate.now());
            studentProjectDao.save(studentProject);

            System.out.println("Fetched Project: " + project);




            String url = FACULTY_SERVICE_URL + "/notify";
            NotificationRequest notificationRequest = new NotificationRequest(projectId, student);
            restTemplate.postForEntity(url, notificationRequest, String.class);


            return new ResponseEntity<>("Project applied successfully", HttpStatus.OK);
        } catch (RestClientException e) {
            // Handle RestTemplate communication errors
            System.err.println("Error with RestTemplate: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Failed to communicate with external service", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Handle any unexpected errors
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<Student> getStudentById(int studentId) {
        try{
            return new ResponseEntity<>(studentDao.findStudentByStudentId(studentId),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<String> makeUnavailibity(Student student) {
        try{
            student.setStudentAvaibility(StudentAvaibility.NOT_AVAILABLE);
            studentDao.save(student);
            return new ResponseEntity<>("Student is Unavailable Now",HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<Student> updateGithubLink(int studentId, String githubLink) {
        try{
            Optional<Student> studentOptional = studentDao.findById(studentId);
            if (studentOptional.isPresent()) {
                Student student = studentOptional.get();
                student.setGithubProfileLink(githubLink);
                studentDao.save(student);
                return new ResponseEntity<>(student,HttpStatus.OK);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


        return null;
    }

    public ResponseEntity<PersonalProject> addProject(int studentId, PersonalProject personalProject) {
        try{
            Student student=studentDao.findStudentByStudentId(studentId);
            student.getProjects().add(personalProject);
            studentDao.save(student);
            return new ResponseEntity<>(personalProject,HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Student> deleteProject(int studentId, int personalProjectId) {
        try{
            Optional<Student> studentOptional = studentDao.findById(studentId);
            Student s=null;
            if(studentOptional.isPresent()){
                s=studentOptional.get();
                s.getProjects().removeIf(p->p.getPersonalProjectId()==personalProjectId);
                studentDao.save(s);
                return new ResponseEntity<>(s,HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    public ResponseEntity<Student> findStudentById(int studentId) {
        try{
            Optional<Student> optionalStudent=studentDao.findById(studentId);
            if(optionalStudent.isPresent()){
                return new ResponseEntity<>(optionalStudent.get(),HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }
}
