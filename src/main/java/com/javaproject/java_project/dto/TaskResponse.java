package com.javaproject.java_project.dto;

import com.javaproject.java_project.model.Task;

import java.time.LocalDateTime;

/** Task data returned to clients. It deliberately excludes the JPA course relation. */
public record TaskResponse(
        Long id,
        String type,
        String title,
        String description,
        boolean completed,
        LocalDateTime deadline,
        int baseXp
) {
    public static TaskResponse from(Task task) {
        String type = task.getClass().getSimpleName().replace("Task", "").toUpperCase();
        if (type.isEmpty()) {
            type = "GENERAL";
        }
        return new TaskResponse(
                task.getTaskID(), type, task.getTitle(), task.getDescription(),
                task.isCompleted(), task.getDeadline(), task.getBaseXP()
        );
    }
}
