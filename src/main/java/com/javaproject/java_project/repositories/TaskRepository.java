package com.javaproject.java_project.repositories;
import  java.lang.Integer;

import com.javaproject.java_project.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task,Integer> {


}
