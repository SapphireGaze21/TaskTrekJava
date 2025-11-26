package com.javaproject.java_project.controller;

import com.javaproject.java_project.model.LoginRequest;
import com.javaproject.java_project.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


@RestController
@RequestMapping("/auth")
public class AuthController 
{
    @Autowired
    UsersRepository usersRepository;

    @PostMapping("/register")
    public String register()
    {

        return "User registered";
    }

    @PostMapping("/login")
    public String login(@org.jetbrains.annotations.NotNull @RequestBody LoginRequest loginCredentials)
    {
        if (usersRepository.usernameExists(loginCredentials.getUsername()) && usersRepository.returnPasswordHash(loginCredentials.getUsername()).equals(loginCredentials.getPassword()) )
            return "User logged in";

        return "Invalid Credentials";
    }
}
