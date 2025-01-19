package com.example.studentservice.Dao;

import com.example.studentservice.Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentDao extends JpaRepository<Student,Integer> {
    Student findStudentByStudentId(int studentId);

    boolean existsByEmail(String email);
}
