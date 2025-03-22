package com.example.studentservice.Service;

import com.example.studentservice.Dao.StudentDao;
import com.example.studentservice.Dao.StudentProjectDao;
import com.example.studentservice.Feign.AuthInterface;
import com.example.studentservice.Model.PersonalProject;
import com.example.studentservice.Model.StudentAvaibility;
import com.example.studentservice.Model.StudentProject;
import com.example.studentservice.SheetHandler;
import com.example.studentservice.Vo.Project;
import com.example.studentservice.Model.Student;
import com.example.studentservice.Vo.Status;
import com.example.studentservice.Vo.UserCredential;
import com.example.studentservice.Dto.NotificationRequest;
import com.example.studentservice.Vo.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStrings;
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
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
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
    private CloudinaryService cloudinaryService;
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
        try {
            List<Student> students = new ArrayList<>();
            List<UserCredential> users = new ArrayList<>();
            int recordCount = 0;

            // Use streaming implementation
            try (InputStream inputStream = file.getInputStream()) {
                // Create streaming reader
                OPCPackage pkg = OPCPackage.open(inputStream);
                XSSFReader reader = new XSSFReader(pkg);
                SharedStrings sst = reader.getSharedStringsTable();

                // Get the first sheet
                // Note: XMLReaderFactory is deprecated in newer versions, use SAXParserFactory instead
                XMLReader parser = XMLReaderFactory.createXMLReader();

                // Set up custom handler
                SheetHandler handler = new SheetHandler(sst,students, users, studentDao);
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
            if (!students.isEmpty()) {
                // Process in batches of 500
                int batchSize = 500;
                for (int i = 0; i < students.size(); i += batchSize) {
                    int endIndex = Math.min(i + batchSize, students.size());
                    studentDao.saveAll(students.subList(i, endIndex));
                }

                for (int i = 0; i < users.size(); i += batchSize) {
                    int endIndex = Math.min(i + batchSize, users.size());
                    authInterface.addNewUser(users.subList(i, endIndex));
                }
            }

            return new ResponseEntity<>("File uploaded successfully: " + recordCount + " records added.", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("File processing failed due to IO error", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("File upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            if(student.getStudentAvaibility()==StudentAvaibility.NOT_AVAILABLE){
                return new ResponseEntity<>("You cant Apply Because You are working on project",HttpStatus.CONFLICT);
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
            //get Highest preference
            Integer maxPreferenced=studentProjectDao.findMaxPreferenceByStudentId(studentId);
            int newPrefernce=(maxPreferenced==null)?1:maxPreferenced+1;
            StudentProject studentProject=new StudentProject();
            studentProject.setStudent(student);
            studentProject.setStatus(Status.PENDING);
            studentProject.setProjectId(projectId);
            studentProject.setApplicationDate(LocalDate.now());
            studentProject.setPreference(newPrefernce);
            studentProjectDao.save(studentProject);

            System.out.println("Fetched Project: " + project);


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

    public ResponseEntity<List<Student>> getAllStudentsById(List<Integer> ids) {
        try{
            List<Student> students=studentDao.findAllByStudentId(ids);
            return new ResponseEntity<>(students,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<String> updateStudentsAvailable(int projectId) {
        try {
            System.out.println(projectId + " in student service");

            List<Integer> studentIds = studentProjectDao.findStudent_StudentIdByProjectId(projectId);
            System.out.println("Student IDs: " + studentIds);

            List<Student> students = new ArrayList<>(); // Mutable list
            for (int studentId : studentIds) {
                Optional<Student> optionalStudent = studentDao.findById(studentId);
                if (optionalStudent.isPresent()) {
                    Student s = optionalStudent.get();
                    System.out.println("Fetched Student: " + s.getName() + " (ID: " + s.getStudentId() + ")");
                    students.add(s);
                } else {
                    System.out.println("Student with ID " + studentId + " not found.");
                    return new ResponseEntity<>("Student with ID " + studentId + " not found.", HttpStatus.NOT_FOUND);
                }
            }

            // Update availability
            for (Student s : students) {
                if (s.getStudentAvaibility() == StudentAvaibility.NOT_AVAILABLE) {
                    s.setStudentAvaibility(StudentAvaibility.AVAILABLE);
                }
            }
            List<StudentProject> studentProjects=studentProjectDao.findByProjectId(projectId);
            for (StudentProject s:studentProjects){
                if(s.getStatus()==Status.APPROVED){
                    s.setStatus(Status.COMPLETED);
                }
            }
            studentProjectDao.saveAll(studentProjects);


            studentDao.saveAll(students);
            System.out.println("Students updated successfully.");
            return new ResponseEntity<>("Project is completed", HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception stack trace
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<Integer> getCount() {
        try{
            Integer count=studentDao.findTotalUsers();
            return new ResponseEntity<>(count,HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Student> findByEmail(String email) {
        try{
            System.out.println(email);
           Student s=studentDao.findByEmail(email);
           if(s==null){
               return new ResponseEntity<>(HttpStatus.NOT_FOUND);
           }
           return new ResponseEntity<>(s,HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> withdrawProject(int studentId, int projectId) {
        try {
            // Step 1: Check if the student exists
            Student student = studentDao.findStudentByStudentId(studentId);
            if (student == null) {
                return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
            }

            // Step 2: Check if the student has applied for the project
            StudentProject studentProject = studentProjectDao.findByStudent_StudentIdAndProjectId(studentId, projectId);
            if (studentProject == null) {
                return new ResponseEntity<>("You have not applied for this project", HttpStatus.CONFLICT);
            }

            // Step 3: Check if the project is already approved
            if (studentProject.getStatus() == Status.APPROVED) {
                return new ResponseEntity<>("You cannot withdraw from an approved project", HttpStatus.CONFLICT);
            }

            // Get the preference of the withdrawn project
            int withdrawnPreference = studentProject.getPreference();

            // Step 4: Remove the student from the project
            studentProjectDao.delete(studentProject);

            // Step 5: Update preferences for remaining projects
            List<StudentProject> remainingProjects = studentProjectDao.findByStudent_StudentId(studentId);

            for (StudentProject project : remainingProjects) {
                if (project.getPreference() > withdrawnPreference) {
                    project.setPreference(project.getPreference() - 1);
                    studentProjectDao.save(project);  // Save updated preference
                }
            }

            // Step 6: Send notification to faculty about the withdrawal


            return new ResponseEntity<>("Project application withdrawn successfully", HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<Student> findStudent(int studentId) {
        try{
            Optional<Student>student=studentDao.findById(studentId);
            if(student.isPresent()){
                Student s=student.get();
                return new ResponseEntity<>(s,HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    public ResponseEntity<Student> updateStudentDetails(int studentId, Student updateStudent) {
        try{
            Optional<Student>existStudent=studentDao.findById(studentId);
            if(existStudent.isPresent()){
                Student student=existStudent.get();
                student.setGithubProfileLink(updateStudent.getGithubProfileLink());
                student.setSkills(updateStudent.getSkills());
                student.setSemesterNo(updateStudent.getSemesterNo());
                student.setCgpa(updateStudent.getCgpa());
                student.setBio(updateStudent.getBio());
                student.setLinkedInUrl(updateStudent.getLinkedInUrl());
                student.setPhoneNo(updateStudent.getPhoneNo());
                studentDao.save(student);
                return new ResponseEntity<>(student,HttpStatus.OK);
            }
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    public ResponseEntity<List<Student>> findAll() {
        try{
            List<Student> students=studentDao.findAll();
            return new ResponseEntity<>(students,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Student> editAvtar(int studentId, MultipartFile avtar) {
        try {
            Optional<Student> existStudent = studentDao.findById(studentId);
            if (existStudent.isPresent()) {
                Student student = existStudent.get();
                student.setImageUrl(cloudinaryService.uploadFile(avtar));

                studentDao.save(student);
                return new ResponseEntity<>(student, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Student> uploadImageToCloudinary(MultipartFile image,int studentId) {
        try{
            String fileUrl=cloudinaryService.uploadFile(image);
            Optional<Student> existStudent=studentDao.findById(studentId);
            Student student=null;
            if(existStudent.isPresent()){
                student=existStudent.get();
                student.setImageUrl(fileUrl);
                studentDao.save(student);

            }
            return new ResponseEntity<>(student,HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
