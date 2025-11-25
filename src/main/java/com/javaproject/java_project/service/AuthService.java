package com.javaproject.java_project.service;

import com.javaproject.java_project.model.User;
import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    // In memory Database for now, Mongo later
    private List<User> users = new ArrayList<>();
    private int nextID = 1; // autoincrement this for registering users

    public User registerUser(String username, String password)
    {
        // check if username already exists, if so, return null (username taken)
        // if fine, create new user with the ID, username, passwordHash (just store the normal pwd for now)
        // lvl = 1, xp = 0 (see constructors with lombok...)
        // User object should match the model, add to arraylist
        // return the user object
    }

    public User loginUser(String username, String password)
    {
        // check if user exists in the list, if not just null
        // If exists check if pwd matches what's there in the users list (simple string comp for now)
        // Match => return user
        // Else => null
    }

}
