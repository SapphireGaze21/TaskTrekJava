package com.javaproject.java_project.controller;

import com.javaproject.java_project.dto.TaskResponse;
import com.javaproject.java_project.model.Course;
import com.javaproject.java_project.model.Task;
import com.javaproject.java_project.request.EditedTaskRequest;
import com.javaproject.java_project.request.NewTaskRequest;
import com.javaproject.java_project.service.CourseService;
import com.javaproject.java_project.service.SkillProgressService;
import com.javaproject.java_project.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/courses/{courseId}/tasks")
public class TaskController {
    private final TaskService taskService;
    private final CourseService courseService;
    private final SkillProgressService skillProgressService;

    public TaskController(TaskService taskService, CourseService courseService, SkillProgressService skillProgressService) {
        this.taskService = taskService;
        this.courseService = courseService;
        this.skillProgressService = skillProgressService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@PathVariable Long courseId, @RequestBody NewTaskRequest request) {
        if (isBlank(request.getTitle())) return ResponseEntity.badRequest().body(Map.of("message", "Task title is required."));
        Task task = taskService.createTask(courseId, request.getTaskType(), request.getTitle().trim(), request.getDescription(), request.getDeadline());
        if (task == null) return notFound("Course not found.");
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.from(task));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<?> editTask(@PathVariable Long courseId, @PathVariable Long taskId, @RequestBody EditedTaskRequest request) {
        if (request.getTitle() == null && request.getDescription() == null && request.getDeadline() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Provide title, description, or deadline."));
        }
        if (request.getTitle() != null && request.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Task title cannot be blank."));
        }
        Task task = taskService.editTask(courseId, taskId, request.getTitle(), request.getDescription(), request.getDeadline());
        if (task == null) return notFound("Task not found.");
        return ResponseEntity.ok(TaskResponse.from(task));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long courseId, @PathVariable Long taskId) {
        return taskService.deleteTask(courseId, taskId) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{taskId}/complete")
    public ResponseEntity<?> completeTask(@PathVariable Long courseId, @PathVariable Long taskId) {
        if (!taskService.completeTask(courseId, taskId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Task was not found or is already completed."));
        }
        Course course = courseService.getCourseByID(courseId);
        Task task = taskService.getTaskByID(courseId, taskId);
        return ResponseEntity.ok(Map.of("task", TaskResponse.from(task),
                "progress", skillProgressService.getCourseProgress(course.getCourseName())));
    }

    private ResponseEntity<Map<String, String>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", message));
    }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
}
