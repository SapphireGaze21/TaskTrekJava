package com.javaproject.java_project.controller;

import com.javaproject.java_project.request.LoginRequest;
import com.javaproject.java_project.model.User;
import com.javaproject.java_project.request.SignupRequest;
import com.javaproject.java_project.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController 
{
    private final AuthService authService;

    public AuthController(AuthService authService)
    {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@org.jetbrains.annotations.NotNull @RequestBody SignupRequest signupDetails)
    {
        // trim will take care of strings with just spaces (they are empty)
        if (signupDetails.getUsername() == null || signupDetails.getUsername().trim().isEmpty())
            return new ResponseEntity<>("Username cannot be empty", HttpStatus.BAD_REQUEST);

        if (signupDetails.getPassword() == null || signupDetails.getPassword().trim().isEmpty())
            return new ResponseEntity<>("Password cannot be empty", HttpStatus.BAD_REQUEST);
        
        User created = authService.registerUser(signupDetails.getUsername(), signupDetails.getPassword());

        if (created == null)
            return new ResponseEntity<>( "Username already exists.", HttpStatus.CONFLICT);
        else
            return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@org.jetbrains.annotations.NotNull @RequestBody LoginRequest loginCreds)
    {
        User loggedIn = authService.loginUser(loginCreds.getUsername(), loginCreds.getPassword());

        if (loggedIn == null)
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        else
            return new ResponseEntity<>(loggedIn, HttpStatus.OK);
    }
}
