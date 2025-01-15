package com.example.studentservice.Model;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Faculty {
    @Id

    private int f_id;
    private String f_password;
    private String name;
    private String email;
    private String department;


}
