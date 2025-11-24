package com.javaproject.java_project.model;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private int ID;
    private String userName;
    private String passwordHash;
    private int level;
    private int xp;
    private int streak;
    List<Integer> userCourses;
}
