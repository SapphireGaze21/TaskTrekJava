package com.javaproject.java_project.model;

import lombok.Data;
import java.util.List;

@Data
public class Course{
    private int courseID;
    private String courseName;
    private int userID; // owner
    private List<Integer> tasks; // stored by IDs
}
