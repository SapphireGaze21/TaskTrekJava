package com.javaproject.java_project.repositories;

import com.javaproject.java_project.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoursesRepository extends MongoRepository {

}
