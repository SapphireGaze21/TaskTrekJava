import { api } from "./client";
import type { CompletedTaskResult, CourseDetail, CourseProgress, CourseSummary, CreateTaskInput, EditTaskInput, Task } from "./types";

export const coursesApi = {
  list: () => api<CourseSummary[]>("/courses"),
  get: (courseId: number) => api<CourseDetail>(`/courses/${courseId}`),
  getProgress: (courseId: number) => api<CourseProgress>(`/courses/${courseId}/progress`),
  create: (courseName: string) => api<CourseSummary>("/courses", { method: "POST", body: JSON.stringify({ courseName }) }),
  rename: (courseId: number, courseName: string) => api<CourseSummary>(`/courses/${courseId}`, { method: "PUT", body: JSON.stringify({ courseName }) }),
  remove: (courseId: number) => api<void>(`/courses/${courseId}`, { method: "DELETE" }),
  createTask: (courseId: number, task: CreateTaskInput) => api<Task>(`/courses/${courseId}/tasks`, { method: "POST", body: JSON.stringify(task) }),
  updateTask: (courseId: number, taskId: number, task: EditTaskInput) => api<Task>(`/courses/${courseId}/tasks/${taskId}`, { method: "PATCH", body: JSON.stringify(task) }),
  removeTask: (courseId: number, taskId: number) => api<void>(`/courses/${courseId}/tasks/${taskId}`, { method: "DELETE" }),
  completeTask: (courseId: number, taskId: number) => api<CompletedTaskResult>(`/courses/${courseId}/tasks/${taskId}/complete`, { method: "POST" })
};
