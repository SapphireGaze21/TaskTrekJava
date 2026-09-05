import type { TaskType } from "../api/types";

export const TASK_TYPE_LABELS: Record<TaskType, string> = {
  GENERAL: "Study task", ASSIGNMENT: "Assignment", PROJECT: "Project", QUIZPREP: "Quiz prep", EXAMPREP: "Exam prep"
};

export function formatDeadline(value: string | null) {
  if (!value) return "No deadline";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "No deadline";
  return new Intl.DateTimeFormat(undefined, { month: "short", day: "numeric", hour: "numeric", minute: "2-digit" }).format(date);
}

export function isDueSoon(value: string | null) {
  if (!value) return false;
  const difference = new Date(value).getTime() - Date.now();
  return difference >= 0 && difference <= 7 * 24 * 60 * 60 * 1000;
}

export function toDateTimeLocal(value: string | null) { return value ? value.slice(0, 16) : ""; }
export function courseAccent(id: number) { return ["violet", "blue", "green", "gold", "rose"][id % 5]; }

export function userProgress(xp: number) {
  const thresholds = [0, 283, 844, 1789, 3162, 5097, 7588, 10734, 14627, 19149];
  const currentIndex = thresholds.reduce((level, threshold, index) => xp >= threshold ? index : level, 0);
  if (currentIndex === thresholds.length - 1) return 100;
  return ((xp - thresholds[currentIndex]) / (thresholds[currentIndex + 1] - thresholds[currentIndex])) * 100;
}
