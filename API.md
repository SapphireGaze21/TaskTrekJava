# TaskTrek frontend API

Run the application with `./mvnw spring-boot:run`. The default API base URL is `http://localhost:8080/api`.

All protected requests must send `Authorization: Bearer <token>`. CORS is enabled for local frontends at ports `3000` and `5173`; configure `app.cors.allowed-origins` for a deployed frontend.

| Method | Route | Body / result |
| --- | --- | --- |
| POST | `/auth/register` | `{ "username", "password" }` -> `{ token, user }` (201) |
| POST | `/auth/login` | `{ "username", "password" }` -> `{ token, user }` |
| GET | `/auth/me` | current user |
| GET | `/courses` | course summaries |
| POST | `/courses` | `{ "courseName" }` -> course (201) |
| GET | `/courses/{courseId}` | course, tasks, and course progress |
| PUT | `/courses/{courseId}` | `{ "courseName" }` -> course |
| DELETE | `/courses/{courseId}` | 204 |
| GET | `/courses/{courseId}/progress` | XP / level progress |
| POST | `/courses/{courseId}/tasks` | task body below -> task (201) |
| PATCH | `/courses/{courseId}/tasks/{taskId}` | any of `title`, `description`, `deadline` -> task |
| DELETE | `/courses/{courseId}/tasks/{taskId}` | 204 |
| POST | `/courses/{courseId}/tasks/{taskId}/complete` | completed task and refreshed progress |

Task-create body:

```json
{
  "title": "Finish chapter 3",
  "description": "Review the exercises",
  "deadline": "2026-09-08T18:00:00",
  "taskType": "ASSIGNMENT"
}
```

`deadline` and `description` are optional. Valid `taskType` values are `ASSIGNMENT`, `PROJECT`, `QUIZPREP`, and `EXAMPREP`; any other or missing value creates a general task.

Successful task responses have `{ id, type, title, description, completed, deadline, baseXp }`. Course responses have `{ id, name, taskCount, completedTaskCount }`. Error responses include a JSON `message`; malformed JSON has a detailed `{ timestamp, status, error, message, path }` response.

Example frontend request:

```js
const response = await fetch("http://localhost:8080/api/courses", {
  headers: { Authorization: `Bearer ${token}` }
});
const courses = await response.json();
```
