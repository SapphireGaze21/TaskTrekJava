package com.javaproject.java_project.model;

import lombok.Data;

@Data

public class Task {
    private int taskID;
    private String title;
    private String Description;
    private int baseXP;
    private double multiplier;
}
