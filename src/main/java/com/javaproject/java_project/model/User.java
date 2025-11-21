package com.javaproject.java_project.model;

import java.util.List;
import java.util.ArrayList;

public class User 
{
    private int id;
    private String username;
    private String passwordHash;
    private int level;
    private int xp;
    private List<Integer> userTasks; // taskID-based storage

    public User(int id, String username, String passwordHash)
    {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.level = 0;
        this.xp = 0;
        this.userTasks = new ArrayList<>();
    }

    public int getLevel()
    {
        return level;
    }

    public int getXP()
    {
        return xp;
    }

    public List<Integer> getUserTasks()
    {
        return userTasks;
    }


    public double getLvlUpXP()
    {
        return 100 + (level - 1) * 50;
    }

    // when a task is complete
    public void addXP(double xp)
    {
        this.xp += xp;

        while (xp >= getLvlUpXP()) 
        { 
            xp -= getLvlUpXP(); 
            level++; 
        }

    }

    // login method
}

    
