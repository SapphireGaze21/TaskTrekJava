export interface User {
  id: number;
  username: string;
  level: number;
  xp: number;
  streak: number;
  lastTaskCompletedDate: string | null;
}

export interface AuthResponse { token: string; user: User; }

export interface CourseSummary {
  id: number;
  name: string;
  taskCount: number;
  completedTaskCount: number;
}

export type TaskType = "GENERAL" | "ASSIGNMENT" | "PROJECT" | "QUIZPREP" | "EXAMPREP";

export interface Task {
  id: number;
  type: TaskType;
  title: string;
  description: string | null;
  completed: boolean;
  deadline: string | null;
  baseXp: number;
}

export interface CourseProgress {
  courseName: string;
  totalXp: number;
  level: number;
  levelName: string;
  xpInCurrentLevel: number;
  xpToNextLevel: number;
  totalXpForCurrentLevel: number;
  progressPercentage: number;
  isMaxLevel: boolean;
}

export interface CourseDetail { id: number; name: string; tasks: Task[]; progress: CourseProgress; }
export interface CompletedTaskResult { task: Task; progress: CourseProgress; }
export interface CreateTaskInput { title: string; description?: string; deadline?: string; taskType?: Exclude<TaskType, "GENERAL">; }
export interface EditTaskInput { title?: string; description?: string; deadline?: string | null; }
