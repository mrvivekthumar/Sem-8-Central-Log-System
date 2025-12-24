package com.example.facultyservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.example.facultyservice.domain.Faculty;

import java.util.List;

@Repository
public interface FacultyDao extends JpaRepository<Faculty, Integer> {

    boolean existsByEmail(String email);

    @Query("SELECT  COUNT(f) FROM Faculty f")
    Integer findTotalUsers();

    Faculty findByEmail(String email);

    @Query("SELECT f.email FROM Faculty f")
    List<String> findAllEmails();
}
