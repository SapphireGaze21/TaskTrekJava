package com.javaproject.java_project.dto;

import com.javaproject.java_project.model.Course;

/** Course summary used in dashboard lists. */
public record CourseResponse(Long id, String name, int taskCount, int completedTaskCount) {
    public static CourseResponse from(Course course) {
        int taskCount = course.getTasks().size();
        int completedTaskCount = (int) course.getTasks().stream().filter(task -> task.isCompleted()).count();
        return new CourseResponse(course.getCourseId(), course.getCourseName(), taskCount, completedTaskCount);
    }
}
