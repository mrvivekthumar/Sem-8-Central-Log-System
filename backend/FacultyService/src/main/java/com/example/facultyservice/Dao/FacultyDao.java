package com.example.facultyservice.Dao;

import com.example.facultyservice.Model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultyDao extends JpaRepository<Faculty, Integer> {

    boolean existsByEmail(String email);
}
