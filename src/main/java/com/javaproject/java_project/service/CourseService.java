package com.javaproject.java_project.service;

import com.javaproject.java_project.model.Course;
import com.javaproject.java_project.model.User;
import com.javaproject.java_project.repositories.UsersRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    UsersRepository usersRepository;

    private int nextID = 1; // autoincrement this for next courses

    // we use AuthService to know which user is currently logged in

    private final AuthService authService ;
    int userID;

    @Getter
    List<Course> courses ;
    public CourseService(AuthService authService) {
        this.authService = authService;
        User currentUser = authService.getCurrentUser();
        userID = currentUser.getId();
        courses = usersRepository.findCourses(userID);
    }

    public String
    public Course createCourse(int userID, String courseName)
    {
        // no logged-in user
        if (authService.getCurrentUser() == null)
            return null;

        int currentUserID = authService.getCurrentUser().getId();

        // check if name already exists in the courses, if so, return NULL (course name taken)
        for(Course course:courses) {
            if (courseName.equals(course.getCourseName())) {
                return null;
            }
        }
        Course newcourse = Course.builder()
                .courseId(nextID)
                .courseName(courseName)
                .userId(currentUserID)
                .build();

        courses.add(newcourse);
        usersRepository.addCourse(currentUserID);

        authService.getCurrentUser().getUserCourses().add(nextID);
        nextID += 1;
        return newcourse;

    }

    public Course getCourseByID(int userID, int courseID) {

        // no logged-in user
        if (authService.getCurrentUser() == null)
            return null;

        int currentUserID = authService.getCurrentUser().getId();

        boolean courseAlreadyThere = false;

        for(int course:authService.getCurrentUser().getUserCourses()) {
            if(course == courseID) {
                courseAlreadyThere = true;
            }
        }
        if(courseAlreadyThere) {
            for (Course course : courses) {
                if (course.getCourseId() == courseID) {
                    return course;
                }
            }
        }
        return null;
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

        for(int course:authService.getCurrentUser().getUserCourses()) {
            if (course == courseID) {
                getCourseByID(userID,courseID).setCourseName(newCourseName);
                usersRepository.renameCourse(currentUserID,courseID);
                return getCourseByID(userID,courseID);
            }
        }
        return null;
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
        for(int course:authService.getCurrentUser().getUserCourses()) {
            if (course == courseID) {
                courses.remove(getCourseByID(userID,courseID));
                authService.getCurrentUser().getUserCourses().remove(Integer.valueOf(courseID));
                usersRepository.deleteCourse(userID, courseID);
                return true;
            }
        }
        return false;
        // if not, return NULL (course not found)
        // Else, delete from the courses array
        // True if deleted
    }
}
