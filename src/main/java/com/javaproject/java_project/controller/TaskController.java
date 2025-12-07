package com.javaproject.java_project.controller;

import com.javaproject.java_project.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/tasks")
public class TaskController 
{
    @Autowired
    UsersRepository userRepo;

    @GetMapping
    public String dashBoard()
    {
        return "Dashboard shown";
    }

    @PostMapping
    public String createTask()
    {
        return "Task Created";
    }

    @PutMapping("/{taskId}")
    public String editTask(@PathVariable int taskId)
    {
        return "Task Edited";
    }

    @DeleteMapping("/{taskId}")
    public String deleteTask(@PathVariable int taskId)
    {
        return "Task Deleted";
    }

    @PostMapping("/{taskId}/complete")
    public String completeTask(@PathVariable int taskId)
    {
        return "Task Complete";
    }
}
