package com.javaproject.java_project.controller;

import com.javaproject.java_project.model.Course;
import com.javaproject.java_project.model.User;
import com.javaproject.java_project.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    User currentUser;

    @GetMapping
    public String dashBoard()
    {
        String courses = "";
        for (Course course : usersRepository.findCourses(currentUser.getId())) {
            courses+= course.getCourseName();
            courses+="\n";
        }

        
        return courses;
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
