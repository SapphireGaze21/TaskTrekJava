package com.javaproject.java_project.service;

import com.javaproject.java_project.model.Course;
import com.javaproject.java_project.model.User;
import com.javaproject.java_project.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class CourseService {

    private final AuthService authService;
    private final CourseRepository courseRepository;

    public CourseService(AuthService authService, CourseRepository courseRepository)
    {
        this.authService = authService;
        this.courseRepository = courseRepository;
    }

    public List<Course> getCourses()
    {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null)
            return new ArrayList<>();

        return courseRepository.findByUser(currentUser);
    }

    public Course createCourse(String courseName)
    {
        User currentUser = authService.getCurrentUser();

        // no logged-in user
        if (currentUser == null)
            return null;

        List<Course> courses = courseRepository.findByUser(currentUser);

        // check if name already exists in the courses for the user, if so, return NULL (course name taken)
        for(Course course : courses)
        {
            if (courseName.equals(course.getCourseName()))
                return null;
        }

        Course newCourse = Course.builder()
                .user(currentUser)
                .courseName(courseName)
                .build();

        return courseRepository.save(newCourse);
    }

    // Tasks of a specific course
    public Course getCourseByID(Long courseID)
    {
        User currentUser = authService.getCurrentUser();

        // no logged-in user
        if (currentUser == null)
            return null;

        return courseRepository.findByCourseIdAndUser(courseID, currentUser).orElse(null);
    }

    public Course renameCourse(Long courseID, String newCourseName)
    {
        User currentUser = authService.getCurrentUser();

        // no logged-in user
        if (currentUser == null)
            return null;

        List<Course> courses = courseRepository.findByUser(currentUser);

        for (Course course : courses)
        {
            // name clash
            if (!course.getCourseId().equals(courseID) && course.getCourseName().equals(newCourseName))
                return null;
        }

        Course course = getCourseByID(courseID);
        if (course == null)
            return null;

        course.setCourseName(newCourseName);
        return courseRepository.save(course);
    }

    public boolean deleteCourse(Long courseID)
    {
        User currentUser = authService.getCurrentUser();

        // no logged-in user
        if (currentUser == null)
            return false;

        Course course = getCourseByID(courseID);
        if (course == null)
            return false;

        courseRepository.delete(course);
        return true;
    }
}
