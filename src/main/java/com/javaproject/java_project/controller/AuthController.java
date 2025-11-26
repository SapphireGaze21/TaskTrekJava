package com.javaproject.java_project.controller;

import com.javaproject.java_project.model.LoginRequest;
import com.javaproject.java_project.model.SignupRequest;
import com.javaproject.java_project.repositories.UsersRepository;
import com.javaproject.java_project.service.AuthService;
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
    AuthService authService;

    @PostMapping("/register")
    public String register(@org.jetbrains.annotations.NotNull @RequestBody SignupRequest signupDetails)
    {
        authService.registerUser(signupDetails.getUsername(), signupDetails.getPassword());
        return "User registered";
    }

    @PostMapping("/login")
    public String login(@org.jetbrains.annotations.NotNull @RequestBody LoginRequest loginCredentials)
    {
        authService.loginUser(loginCredentials.getUsername(), loginCredentials.getPassword());
        return "logged in";
    }
}
