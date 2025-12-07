package com.javaproject.java_project.service;

import com.javaproject.java_project.model.Task;
import com.javaproject.java_project.model.User;
import com.javaproject.java_project.repositories.CoursesRepository;
import com.javaproject.java_project.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.javaproject.java_project.model.Course;


import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class TaskService
{
    @Autowired
    UsersRepository usersRepository;
    CoursesRepository coursesRepository;

    // In memory list of tasks
    // Going to have courseID in the task
    List<Task> tasks = new ArrayList<Task>();

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

    public List<Task> getTasksForCourse(int courseId)
    {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null)
            return new ArrayList<>();

        List<Task> result = new ArrayList<>();
        for (Task task : tasks)
        {
            if (task.getCourseID() == courseId)
                result.add(task);
        }
        return result;
    }

    public Task createTask(int courseId, String title, String description, LocalDateTime deadline, String difficulty)
    {
        // create the task object using the model, PASS THE COURSE ID TOO!
        // push to array and return the task

        User currentUser = authService.getCurrentUser();

        if(currentUser == null)
            return null;

        Task task = Task.builder()
                .taskID(nextID++)
                .courseID(courseId)
                .title(title)
                .description(description)
                .completed(false)
                .deadline(deadline)
                .build();
                //.difficulty(difficulty); to change

        // to be inherited
        /*
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
        */

        tasks.add(task);
        return task;
    }

    public Task getTaskByID(int taskID)
    {
        User currentUser = authService.getCurrentUser();

        // no logged-in user
        if (currentUser == null)
            return null;

        // check if taskID matches any of the tasks list IDs
        // if not, return null (task not found)
        // else, return the task
        for (Task task : tasks)
        {
            if(task.getTaskID() == taskID)
                return task;
        }
        return null;
    }

    public Task editTask(int courseID, int taskID, String newTitle, String newDesc, LocalDateTime newDeadline, String newDifficulty)
    {
        // check if taskID matches any of the taskToEdit list IDs
        // if not, return NULL (taskToEdit not found)
        // Else, edit the taskToEdit
        // return the taskToEdit
        User currentUser = authService.getCurrentUser();

        if (currentUser == null)
            return null;

        Task taskToEdit = getTaskByID(taskID);

        // checking if the same courseID is used
        if (taskToEdit == null || courseID != taskToEdit.getCourseID())
            return null;

        if (newTitle != null)
            taskToEdit.setTitle(newTitle);
        if (newDesc != null)
            taskToEdit.setDescription(newDesc);
        if (newDeadline != null)
            taskToEdit.setDeadline(newDeadline);

        if (newDifficulty != null)
        {
            taskToEdit.setDifficulty(newDifficulty);

            if (newDifficulty.equalsIgnoreCase("HARD"))
            {
                taskToEdit.setBaseXP(100);
                taskToEdit.setMultiplier(2.0);
            }
            else if (newDifficulty.equalsIgnoreCase("MEDIUM"))
            {
                taskToEdit.setBaseXP(80);
                taskToEdit.setMultiplier(1.5);
            }
            else
            {
                taskToEdit.setBaseXP(50);
                taskToEdit.setMultiplier(1.0);
            }
        }

        return taskToEdit;
    }

    public boolean deleteTask(int courseID, int taskID)
    {
        // check if taskID matches any of the taskToDelete list IDs
        // if not, return NULL (taskToDelete not found)
        // Else, delete from the taskToDelete array
        // True if deleted
        User currentUser = authService.getCurrentUser();
        if(currentUser == null)
            return false;

        Task taskToDelete = getTaskByID(taskID);

        // checking if the same courseID is used
        if (taskToDelete == null || courseID != taskToDelete.getCourseID())
            return false;

        tasks.remove(taskToDelete);
        return true;
    }

    public boolean completeTask(int courseID, int taskID)
    {
        // check if taskID matches any of the task list IDs
        // if not, return NULL (task not found)
        // add xp, level up etc etc etc
        User currentUser = authService.getCurrentUser();
        if(currentUser == null)
            return false;

        if (courseService.getCourseByID(courseID) == null)
            return false;

        Task task = getTaskByID(taskID);
        if (task == null)
            return false;

        if (task.isCompleted())
            return false; // already finished, prevents double XP

        task.setCompleted(true);

        // get course name for XP mapping
        Course course = courseService.getCourseByID(courseID);
        String courseName = course.getCourseName();

        // award XP based on task difficulty computed earlier
        skillProgressService.awardTaskCompletionXp(courseName, task.getBaseXP(), task.getMultiplier());


        return true;

    }
}
