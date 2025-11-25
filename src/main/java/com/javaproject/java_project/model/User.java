package com.javaproject.java_project.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class User
{
    private int id;
    private String username;
    private String passwordHash;
    private int level;
    private int xp;
    private int streak;
    List<Integer> userCourses;
}
