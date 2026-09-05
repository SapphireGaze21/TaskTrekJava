package com.javaproject.java_project.dto;

import java.util.List;
import java.util.Map;

/** Complete course view, including its tasks and XP progress. */
public record CourseDetailResponse(
        Long id,
        String name,
        List<TaskResponse> tasks,
        Map<String, Object> progress
) { }
