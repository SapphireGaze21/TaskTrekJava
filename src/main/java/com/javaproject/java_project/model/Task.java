package com.javaproject.java_project.model;

import java.time.LocalDateTime;

public class Task {
    private int taskID;
    private String title;
    private String description;
    // type
    private LocalDateTime createdAt;
    private LocalDateTime deadline;

    // status
    private int baseXP;
    private double multiplier;

    public Task(int taskID, String title, String description, LocalDateTime deadline)
    {
        this.taskID = taskID;
        this.title = title;
        this.description = description;
        this.baseXP = 10;
        this.multiplier = 1.0;

        this.createdAt = LocalDateTime.now();
        this.deadline = deadline; 
    }

    public int getTaskID()
    {
        return taskID;
    }

    public LocalDateTime getDeadline()
    {
        return deadline;
    }

    public void setTaskTitle(String title)
    {
        this.title = title;
    }

    public void setTaskDescription(String description)
    {
        this.description = description;
    }

    public void setTaskDeadline(LocalDateTime deadline)
    {
        this.deadline = deadline;
    }

    public boolean isOverdue()
    {
        return LocalDateTime.now().isAfter(deadline);
    }
}
