package com.example.facultyservice.Dao;

import com.example.facultyservice.Model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProjectDao extends JpaRepository<Project,Integer> {

    @Query("SELECT p FROM Project p WHERE p.faculty.f_id = :facultyId")
    List<Project> findByFacultyId(@Param("facultyId") int facultyId);

}