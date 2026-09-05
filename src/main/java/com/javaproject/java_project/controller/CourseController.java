package com.javaproject.java_project.controller;

import com.javaproject.java_project.dto.CourseDetailResponse;
import com.javaproject.java_project.dto.CourseResponse;
import com.javaproject.java_project.dto.TaskResponse;
import com.javaproject.java_project.model.Course;
import com.javaproject.java_project.request.NewCourseRequest;
import com.javaproject.java_project.service.CourseService;
import com.javaproject.java_project.service.SkillProgressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;
    private final SkillProgressService skillProgressService;

    public CourseController(CourseService courseService, SkillProgressService skillProgressService) {
        this.courseService = courseService;
        this.skillProgressService = skillProgressService;
    }

    @GetMapping
    public List<CourseResponse> listCourses() {
        return courseService.getCourses().stream().map(CourseResponse::from).toList();
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourse(@PathVariable Long courseId) {
        Course course = courseService.getCourseByID(courseId);
        if (course == null) return notFound("Course not found.");
        List<TaskResponse> tasks = course.getTasks().stream().map(TaskResponse::from).toList();
        return ResponseEntity.ok(new CourseDetailResponse(course.getCourseId(), course.getCourseName(), tasks,
                skillProgressService.getCourseProgress(course.getCourseName())));
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody NewCourseRequest request) {
        if (isBlank(request.getCourseName())) return ResponseEntity.badRequest().body(Map.of("message", "Course name is required."));
        Course course = courseService.createCourse(request.getCourseName().trim());
        if (course == null) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "A course with this name already exists."));
        return ResponseEntity.status(HttpStatus.CREATED).body(CourseResponse.from(course));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<?> renameCourse(@PathVariable Long courseId, @RequestBody NewCourseRequest request) {
        if (isBlank(request.getCourseName())) return ResponseEntity.badRequest().body(Map.of("message", "Course name is required."));
        Course course = courseService.renameCourse(courseId, request.getCourseName().trim());
        if (course == null) return notFound("Course not found or the name is already in use.");
        return ResponseEntity.ok(CourseResponse.from(course));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        return courseService.deleteCourse(courseId) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{courseId}/progress")
    public ResponseEntity<?> getProgress(@PathVariable Long courseId) {
        Course course = courseService.getCourseByID(courseId);
        if (course == null) return notFound("Course not found.");
        return ResponseEntity.ok(skillProgressService.getCourseProgress(course.getCourseName()));
    }

    private ResponseEntity<Map<String, String>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", message));
    }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
}
