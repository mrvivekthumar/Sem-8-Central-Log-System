package com.example.studentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studentservice.domain.PersonalProject;

import java.util.List;
@Repository
public interface PersonalProjectDao extends JpaRepository<PersonalProject,Integer> {
    List<PersonalProject> findByStudent_StudentId(int studentId);
}
