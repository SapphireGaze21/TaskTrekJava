import { api } from "./client";
import type { AuthResponse, User } from "./types";

export const authApi = {
  login: (username: string, password: string) => api<AuthResponse>("/auth/login", { method: "POST", body: JSON.stringify({ username, password }) }),
  register: (username: string, password: string) => api<AuthResponse>("/auth/register", { method: "POST", body: JSON.stringify({ username, password }) }),
  me: () => api<User>("/auth/me")
};
