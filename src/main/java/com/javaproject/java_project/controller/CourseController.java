package com.javaproject.java_project.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")

public class CourseController {

    @GetMapping
    public String dashBoard()
    {
        return "Dashboard shown";
    }

    @GetMapping("/{courseId}")
    public String taskList(@PathVariable int courseId) { return "Task List shown";}

    @PostMapping
    public String addCourse()
    {
        return "Course added";
    }

    @DeleteMapping("/{courseId}")
    public String removeCourse(@PathVariable int courseId)
    {
        return "Course Removed";
    }
}
