package com.example.studentservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
