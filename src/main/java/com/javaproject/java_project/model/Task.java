package com.javaproject.java_project.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Task {
    @Id
    private int taskID;
    private String title;
    private String Description;
    private int baseXP;
    private double multiplier;
}
