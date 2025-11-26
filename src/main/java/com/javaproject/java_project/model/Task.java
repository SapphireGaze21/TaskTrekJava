package com.javaproject.java_project.model;

import lombok.Data;

@Data

public class Task {
    private int taskID;
    private String title;
    private String description;
    private int baseXP;
    private double multiplier;
    private int courseID;
    private int ownerUserID;
    private boolean completed;
}
