package com.javaproject.java_project.service;

import com.javaproject.java_project.model.Course;
import com.javaproject.java_project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class CourseService {

    // In-memory database of courses for now (mongo later)
    List<Course> courses = new ArrayList<>();
    private int nextID = 1; // autoincrement this for next courses

    // we use AuthService to know which user is currently logged in
    @Autowired
    private final AuthService authService;

    public CourseService(AuthService authService) {
        this.authService = authService;
    }

    public Course createCourse(int userID, String courseName)
    {
        // no logged-in user
        if (authService.getCurrentUser() == null)
            return null;

        int currentUserID = authService.getCurrentUser().getId();

        // check if name already exists in the courses, if so, return NULL (course name taken)
        // if fine, create the course object using the model, PASS THE USER ID TOO!
        // push to array and return the course
    }

    public Course getCourseByID(int userID, int courseID)
    {
        // no logged-in user
        if (authService.getCurrentUser() == null)
            return null;

        int currentUserID = authService.getCurrentUser().getId();

        // check if courseID matches any of the course list IDs (userIDs should match too)
        // if not, return null (course not found)
        // else, return the course
    }

    public Course renameCourse(int userID, int courseID, String newCourseName)
    {
        // no logged-in user
        if (authService.getCurrentUser() == null)
            return null;

        int currentUserID = authService.getCurrentUser().getId();

        // check if courseID matches any of the course list IDs (userIDs should match too)
        // if not, return NULL (course not found)
        // Else, rename the course
        // return the course
    }

    public boolean deleteCourse(int userID, int courseID)
    {
        // no logged-in user
        if (authService.getCurrentUser() == null)
            return false;

        int currentUserID = authService.getCurrentUser().getId();

        // check if courseID matches any of the course list IDs (userIDs should match too)
        // if not, return NULL (course not found)
        // Else, delete from the courses array
        // True if deleted
    }
}
