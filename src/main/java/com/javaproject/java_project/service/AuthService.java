package com.javaproject.java_project.service;

import com.javaproject.java_project.model.User;
import java.util.List;
import java.util.ArrayList;

import com.javaproject.java_project.repositories.UsersRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Custom exception for duplicate usernames
class UsernameAlreadyTakenException extends RuntimeException {
    public UsernameAlreadyTakenException(String message) {
        super(message);
    }
}

// Custom exception for login failures
class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}

@Service
public class AuthService
{
    @Autowired
    UsersRepository usersRepository;

    // In memory Database for now, Mongo later
    //private List<User> users = usersRepository.find;
    private int nextID = 1; // autoincrement this for registering users


    // helper so other services can know who is logged in
    // currently logged-in user (set on successful login)
    @Getter
    private User currentUser;


    public User registerUser(String username, String password) {

        boolean usernameAlreadyExists = usersRepository.usernameExists(username);
        if(usernameAlreadyExists){
            throw new UsernameAlreadyTakenException("Username '" + username + "' is already taken.");            }

        User newuser = User.builder()
                .id(nextID)
                .username(username)
                .passwordHash(password)
                .build();
        nextID++;

        User insertedUser = usersRepository.insert(newuser);
        //users.add(newuser);
        return newuser;
    }
        // check if username already exists, if so, return null (username taken)
        // if fine, create new user with the ID, username, passwordHash (just store the normal pwd for now)
        // lvl = 1, xp = 0 (see constructors with lombok...)
        // User object should match the model, add to arraylist
        // return the user object

    public User loginUser(String username, String password) {
        try {
            User user = usersRepository.findByUsername(username);
            if (user.getUsername().equals(username)) {
                if (user.getPasswordHash().equals(password)) {
                    currentUser = user; // Set logged-in user
                    return user;
                } else {
                    throw new InvalidCredentialsException("Incorrect password for user '" + username + "'.");
                }
            }
        }
        catch(Exception E) {
            throw new InvalidCredentialsException("Username '" + username + "' not found.");
        }
        return null;
    }

        // check if user exists in the list, if not just null
        // If exists check if pwd matches what's there in the users list (simple string comp for now)
        // Match => return user
        // Else => Exception

}
