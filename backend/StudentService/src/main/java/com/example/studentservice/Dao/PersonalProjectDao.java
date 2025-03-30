package com.example.studentservice.Dao;

import com.example.studentservice.Model.PersonalProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PersonalProjectDao extends JpaRepository<PersonalProject,Integer> {
    List<PersonalProject> findByStudent_StudentId(int studentId);
}
