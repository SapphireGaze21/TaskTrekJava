package com.javaproject.java_project.model;

import lombok.Data;
import java.util.List;

@Data
public class Course{
    private int CourseID;
    private String title;
    private String description;
    private int numberOfTasks;
    private List<Integer> TaskIDs;
}
