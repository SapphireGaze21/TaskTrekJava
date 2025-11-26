package com.javaproject.java_project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaProjectApplication {
    @Autowired
    TaskRepository taskrepo1;

	public static void main(String[] args) {

        SpringApplication.run(JavaProjectApplication.class, args);
        taskrepo1;


	}

}
