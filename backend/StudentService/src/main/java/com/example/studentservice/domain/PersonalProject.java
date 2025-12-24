package com.example.studentservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int personalProjectId;
    private String name;
    private String descreption;
    private String projectLink;
    @ManyToOne
    private Student student;


}
