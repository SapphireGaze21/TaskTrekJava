### TaskTrek - a Gamified Task Tracker (Java Spring Boot)

## Overview

A gamified task manager inspired by Google Tasks & Duolingo, where users manage courses and tasks — earning XP and levels as they complete them.
Built with Spring Boot in Java.

Users can:-

- Register / Log in (via /auth)

- Create and manage courses (via /courses)

- Add and complete tasks (via /tasks)

- Track XP and levels (future addition: /skills)

## Tech Stack

- Language: Java (JDK 21)

- Framework: Spring Boot

- Database: MongoDB (to be integrated)

- Tools: Maven, Postman

## Project Structure (as of date)

src/

├── controller/

│   ├── AuthController.java

│   ├── CourseController.java

│   └── TaskController.java

│

├── service/

│   ├── AuthService.java

│   ├── CourseService.java

│   ├── TaskService.java

│   └── SkillProgressService.java

│

├── model/

│   ├── User.java

│   ├── Course.java

│   └── Task.java

│   ├── Achievement.java

│   ├── SkillProgress.java

│

└── request/

│   ├── LoginRequest.java

│   ├── SignupRequest.java

│   └── NewCourseRequest.java

## Current Features (as of 28/11/2025)

- Auth system (register + login) tested on Postman
 
- Course CRUD (create, rename, delete, view)

- Controller–Service separation with in-memory storage

- Auth state maintained through AuthService


## API Endpoints Summary (currently implemented)

| Method | Endpoint	| Description |
| :---: | :---: | :---: |
| POST | /auth/register	| Register a new user |
| POST | /auth/login | Login existing user |
| GET | /courses | List all user courses |
| POST | /courses | Create a new course |
| PUT | /courses/{courseId}	| Rename a course |
| DELETE | /courses/{courseId} | Delete a course |
| GET | /courses/{id} | View tasks under a course |

## Remaining Features

- Task Service partially implemented (logic pending), along with SkillProgress and Achievements

- Task Type implementations via inheritance pending

- MongoDB integration pending

- JWT token authentication (substituted with an injected AuthService object, for now)

- Final end-to-end Postman testing
