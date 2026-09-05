package com.javaproject.java_project.service;

import com.javaproject.java_project.model.*;
import com.javaproject.java_project.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TaskService
{
    private final AuthService authService;
    private final CourseService courseService;
    private final SkillProgressService skillProgressService;
    private final TaskRepository taskRepository;

    public TaskService(AuthService authService, CourseService courseService, 
                       SkillProgressService skillProgressService, TaskRepository taskRepository)
    {
        this.authService = authService;
        this.courseService = courseService;
        this.skillProgressService = skillProgressService;
        this.taskRepository = taskRepository;
    }

    public Task createTask(Long courseId, String taskType, String title, String description, LocalDateTime deadline)
    {
        User currentUser = authService.getCurrentUser();
        if(currentUser == null)
            return null;

        Course currentCourse = courseService.getCourseByID(courseId);
        if (currentCourse == null)
            return null;

        String normalizedTaskType = taskType == null ? "GENERAL" : taskType.trim().toUpperCase();
        Task task = switch (normalizedTaskType) {
            case "ASSIGNMENT" -> AssignmentTask.builder()
                    .course(currentCourse)
                    .title(title)
                    .description(description)
                    .deadline(deadline)
                    .completed(false)
                    .build();
            case "PROJECT" -> ProjectTask.builder()
                    .course(currentCourse)
                    .title(title)
                    .description(description)
                    .deadline(deadline)
                    .completed(false)
                    .build();
            case "QUIZPREP" -> QuizPrepTask.builder()
                    .course(currentCourse)
                    .title(title)
                    .description(description)
                    .deadline(deadline)
                    .completed(false)
                    .build();
            case "EXAMPREP" -> ExamPrepTask.builder()
                    .course(currentCourse)
                    .title(title)
                    .description(description)
                    .deadline(deadline)
                    .completed(false)
                    .build();
            default -> Task.builder()
                    .course(currentCourse)
                    .title(title)
                    .description(description)
                    .deadline(deadline)
                    .completed(false)
                    .build();
        };

        task.configureXp();
        return taskRepository.save(task);
    }

    public Task getTaskByID(Long courseID, Long taskID)
    {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null)
            return null;

        Course currentCourse = courseService.getCourseByID(courseID);
        if (currentCourse == null)
            return null;

        return taskRepository.findByTaskIDAndCourse(taskID, currentCourse).orElse(null);
    }

    public Task editTask(Long courseID, Long taskID, String newTitle, String newDesc, LocalDateTime newDeadline)
    {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null)
            return null;

        Task taskToEdit = getTaskByID(courseID, taskID);
        if (taskToEdit == null)
            return null;

        if (newTitle != null)
            taskToEdit.setTitle(newTitle);
        if (newDesc != null)
            taskToEdit.setDescription(newDesc);
        if (newDeadline != null)
            taskToEdit.setDeadline(newDeadline);

        return taskRepository.save(taskToEdit);
    }

    public boolean deleteTask(Long courseID, Long taskID)
    {
        User currentUser = authService.getCurrentUser();
        if(currentUser == null)
            return false;

        Task taskToDelete = getTaskByID(courseID, taskID);
        if (taskToDelete == null)
            return false;

        taskRepository.delete(taskToDelete);
        return true;
    }

    public boolean completeTask(Long courseID, Long taskID)
    {
        User currentUser = authService.getCurrentUser();
        if(currentUser == null)
            return false;

        Course course = courseService.getCourseByID(courseID);
        if (course == null)
            return false;

        Task task = getTaskByID(courseID, taskID);
        if (task == null || task.isCompleted())
            return false;

        task.setCompleted(true);

        String courseName = course.getCourseName();

        // Penalty of 0.5x if the task is done late
        if (task.getDeadline() != null && LocalDateTime.now().isAfter(task.getDeadline()))
            task.setMultiplier(task.getMultiplier() / 2);

        // new streak
        if (currentUser.getLastTaskCompletedDate() == null)
            currentUser.setStreak(1);
        // the streak continues
        else if (currentUser.getLastTaskCompletedDate().equals(LocalDate.now().minusDays(1)))
            currentUser.setStreak(currentUser.getStreak() + 1);
        else if (!currentUser.getLastTaskCompletedDate().equals(LocalDate.now()))
            currentUser.setStreak(1);

        currentUser.setLastTaskCompletedDate(LocalDate.now());

        taskRepository.save(task);

        // award XP based on task difficulty computed earlier
        skillProgressService.awardTaskCompletionXp(courseName, task.getBaseXP(), task.getMultiplier(), currentUser.getStreak());
        authService.saveUsers();

        return true;
    }
}
