package com.example.studentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.studentservice.domain.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentDao extends JpaRepository<Student,Integer> {
    Student findStudentByStudentId(int studentId);

    boolean existsByEmail(String email);
    @Query("SELECT s FROM Student s WHERE s.studentId IN :ids")
    List<Student> findAllByStudentId(List<Integer> ids);

    @Query("SELECT  COUNT(s) FROM Student s")
    Integer findTotalUsers();

    Student findByEmail(String email);

    @Query("SELECT s.email FROM Student s")
    List<String> findAllEmails();
}
