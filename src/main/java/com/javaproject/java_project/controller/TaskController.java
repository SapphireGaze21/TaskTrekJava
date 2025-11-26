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

    @PutMapping("/{taskid}")
    public String editTask(@PathVariable int taskid)
    {
        return "Task Edited";
    }

    @DeleteMapping("/{taskid}")
    public String deleteTask(@PathVariable int taskid)
    {
        return "Task Deleted";
    }

    @PostMapping("/{taskid}/complete")
    public String completeTask(@PathVariable int taskid)
    {
        return "Task Complete";
    }
}
