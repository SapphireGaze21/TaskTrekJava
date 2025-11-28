package com.javaproject.java_project.service;

import com.javaproject.java_project.model.Task;
import com.javaproject.java_project.model.User;
import org.springframework.stereotype.Service;
import com.javaproject.java_project.model.Course;


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
    private final SkillProgressService skillProgressService;


    public TaskService(AuthService authService, CourseService courseService, SkillProgressService skillProgressService)
    {
        this.authService = authService;
        this.courseService = courseService;
        this.skillProgressService = skillProgressService;
    }

    public Task createTask(int courseID, String title, String description, LocalDateTime deadline, String difficulty)
    {
        // create the task object using the model, PASS THE COURSE ID TOO!
        // push to array and return the task

        User currentUser = authService.getCurrentUser();

        if(currentUser == null){
            return null;
        }

        if(courseService.getCourseByID(currentUser.getId(), courseID) == null){
            return null;
        }

        Task task = Task.builder()
                .TaskID(nextID++)
                .OwnerUserID(currentUser.getId())
                .CourseID(courseID)
                .Title(title)
                .Description(description)
                .Completed(false)
                .Deadline(deadline)
                .Difficulty(difficulty);

        if(difficulty.equalsIgnoreCase("HARD")){
            task.setBaseXP(100);
            task.setMultiplier(2.0);
        }
        else if(difficulty.equalsIgnoreCase("MEDIUM")){
            task.setBaseXP(80);
            task.setMultiplier(1.5);
        }
        else{
            task.setBaseXP(50);
            task.setMultiplier(1.0);
        }


        tasks.add(task);
        return task;
    }

    public Task getTaskByID(int courseID, int taskID)
    {
        // check if taskID matches any of the tasks list IDs
        // if not, return null (task not found)
        // else, return the task
        for (Task task : tasks){
            if(task.getCourseID() == courseID && task.getTaskID() == taskID) {
                return task;
            }
        }
        return null;
    }

    public Task editTask(int courseID, int taskID, String newTitle, String newDesc, LocalDateTime newDeadline, String newDifficulty)
    {
        // check if taskID matches any of the task list IDs
        // if not, return NULL (task not found)
        // Else, edit the task
        // return the task
        User currentUser = authService.getCurrentUser();
        if(currentUser == null){
            return null;
        }

        Task task = getTaskByID(courseID, taskID);
        if(task == null){
            return null;
        }

        if(currentUser.getId() != task.getOwnerUserID()){
            return null;
        }

        if(newTitle != null) task.setTitle(newTitle);
        if(newDesc != null) task.setDescription(newDesc);
        if(newDeadline != null) task.setDeadline(newDeadline);

        if(newDifficulty != null){
            task.setDifficulty(newDifficulty);

            if(newDifficulty.equalsIgnoreCase("HARD")){
                task.setBaseXP(100);
                task.setMultiplier(2.0);
            }
            else if(newDifficulty.equalsIgnoreCase("MEDIUM")){
                task.setBaseXP(80);
                task.setMultiplier(1.5);
            }
            else{
                task.setBaseXP(50);
                task.setMultiplier(1.0);
            }
        }

        return task;
    }

    public boolean deleteTask(int courseID, int taskID)
    {
        // check if taskID matches any of the task list IDs
        // if not, return NULL (task not found)
        // Else, delete from the task array
        // True if deleted
        User currentUser = authService.getCurrentUser();
        if(currentUser == null){
            return false;
        }

        if(courseService.getCourseByID(currentUser.getId(), courseID) == null){
            return false;
        }


        Task task = getTaskByID(courseID, taskID);
        if(task == null){
            return false;
        }

        if(task.getOwnerUserID() != currentUser.getId()){
            return false;
        }

        tasks.remove(task);
        return true;

    }

    public boolean completeTask(int courseID, int taskID)
    {
        // check if taskID matches any of the task list IDs
        // if not, return NULL (task not found)
        // add xp, level up etc etc etc
        User currentUser = authService.getCurrentUser();
        if(currentUser == null){
            return false;
        }

        if(courseService.getCourseByID(currentUser.getId(), courseID) == null){
            return false;
        }


        Task task = getTaskByID(courseID, taskID);
        if(task == null){
            return false;
        }

        if(task.isCompleted()){
            return false; // already finished, prevents double XP
        }
        task.setCompleted(true);

        // get course name for XP mapping
        Course course = courseService.getCourseByID(currentUser.getId(), courseID);
        String courseName = course.getCourseName();

        // award XP based on task difficulty computed earlier
        skillProgressService.awardTaskCompletionXp(courseName, task.getBaseXP(), task.getMultiplier());


        return true;

    }
}
