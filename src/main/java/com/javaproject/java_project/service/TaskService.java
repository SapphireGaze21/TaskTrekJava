package com.javaproject.java_project.service;

import com.javaproject.java_project.model.Task;
import com.javaproject.java_project.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class TaskService {
    // In-memory database of tasks for now (mongo later)
    List<Task> tasks = new ArrayList<>();
    private int nextID = 1; // autoincrement this for next tasks

    // we use AuthService to know which user is currently logged in
    private final AuthService authService;
    private final CourseService courseService;

    public TaskService(AuthService authService, CourseService courseService)
    {
        this.authService = authService;
        this.courseService = courseService;
    }

    public Task createTask(int courseID, String title, String description, LocalDateTime deadline)
    {
        // create the task object using the model, PASS THE COURSE ID TOO!
        // push to array and return the task
    }

    public Task getTaskByID(int courseID, int taskID)
    {
        // check if taskID matches any of the tasks list IDs
        // if not, return null (task not found)
        // else, return the task
    }

    public Task editTask(int courseID, int taskID, String newTitle, String newDesc, LocalDateTime newDeadline)
    {
        // check if taskID matches any of the task list IDs
        // if not, return NULL (task not found)
        // Else, edit the task
        // return the task
    }

    public boolean deleteTask(int courseID, int taskID)
    {
        // check if taskID matches any of the task list IDs
        // if not, return NULL (task not found)
        // Else, delete from the task array
        // True if deleted
    }

    public boolean completeTask(int courseID, int taskID)
    {
        // check if taskID matches any of the task list IDs
        // if not, return NULL (task not found)
        // add xp, level up etc etc etc
    }
}
