package com.example.studentservice.Model;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


public class Faculty {

    private int f_id;
    private String f_password;
    private String name;
    private String email;
    private String department;

    public int getF_id() {
        return f_id;
    }

    public void setF_id(int f_id) {
        this.f_id = f_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getF_password() {
        return f_password;
    }

    public void setF_password(String f_password) {
        this.f_password = f_password;
    }

    public Faculty(int f_id, String f_password, String name, String email, String department) {
        this.f_id = f_id;
        this.f_password = f_password;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "f_id=" + f_id +
                ", f_password='" + f_password + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
