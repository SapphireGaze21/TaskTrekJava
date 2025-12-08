package com.javaproject.java_project.controller;

import com.javaproject.java_project.model.Course;
import com.javaproject.java_project.model.User;
import com.javaproject.java_project.request.NewCourseRequest;
import com.javaproject.java_project.service.AuthService;
import com.javaproject.java_project.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final AuthService authService;
    private final CourseService courseService;

    public CourseController(AuthService authService, CourseService courseService) {
        this.authService = authService;
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<?> dashBoard() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null)
            return new ResponseEntity<>("Log-In First", HttpStatus.UNAUTHORIZED);

        List<Course> courses = courseService.getCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> taskList(@PathVariable int courseId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null)
            return new ResponseEntity<>("Log-In First", HttpStatus.UNAUTHORIZED);

        Course toShow = courseService.getCourseByID(courseId);
        if (toShow == null)
            return new ResponseEntity<>("Course Not Found", HttpStatus.NOT_FOUND);

        // returns integers for taskIDs for now
        return new ResponseEntity<>(toShow.getTasks(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@org.jetbrains.annotations.NotNull @RequestBody NewCourseRequest newCourseDetails) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null)
            return new ResponseEntity<>("Log-In First", HttpStatus.UNAUTHORIZED);

        // trim will take care of strings with just spaces (they are empty)
        if (newCourseDetails.getCourseName() == null || newCourseDetails.getCourseName().trim().isEmpty())
            return new ResponseEntity<>("Course Name cannot be empty", HttpStatus.BAD_REQUEST);

        Course created = courseService.createCourse(newCourseDetails.getCourseName());

        if (created == null)
            return new ResponseEntity<>("Course Name already exists.", HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> removeCourse(@PathVariable int courseId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null)
            return new ResponseEntity<>("Log-In First", HttpStatus.UNAUTHORIZED);

        boolean deleted = courseService.deleteCourse(courseId);

        if (!deleted)
            return new ResponseEntity<>("Course does not Exist.", HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>("Course deleted", HttpStatus.NO_CONTENT); // fetch the course name
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<?> renameCourse(@PathVariable int courseId, @org.jetbrains.annotations.NotNull @RequestBody NewCourseRequest newCourseDetails) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null)
            return new ResponseEntity<>("Log-In First", HttpStatus.UNAUTHORIZED);

        Course toRename = courseService.renameCourse(courseId, newCourseDetails.getCourseName());

        if (toRename == null)
            return new ResponseEntity<>("Course does not Exist.", HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>("Course renamed to '" + toRename.getCourseName() + "'", HttpStatus.OK);
    }
}
