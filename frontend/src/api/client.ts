const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api").replace(/\/$/, "");

let token: string | null = null;
let onUnauthorized: (() => void) | undefined;

export class ApiError extends Error {
  constructor(message: string, public readonly status: number) {
    super(message);
    this.name = "ApiError";
  }
}

export function setAccessToken(value: string | null) { token = value; }
export function setUnauthorizedHandler(handler: (() => void) | undefined) { onUnauthorized = handler; }

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers);
  if (options.body && !headers.has("Content-Type")) headers.set("Content-Type", "application/json");
  if (token) headers.set("Authorization", `Bearer ${token}`);

  let response: Response;
  try {
    response = await fetch(`${API_BASE_URL}${path}`, { ...options, headers });
  } catch {
    throw new ApiError("Could not reach TaskTrek. Check that the Spring Boot API is running.", 0);
  }
  if (response.status === 401 || response.status === 403) onUnauthorized?.();
  if (response.status === 204) return undefined as T;

  const raw = await response.text();
  let payload: unknown;
  try { payload = raw ? JSON.parse(raw) : undefined; } catch { payload = raw; }
  if (!response.ok) {
    const message = typeof payload === "object" && payload !== null && "message" in payload
      ? String(payload.message) : "Something went wrong. Please try again.";
    throw new ApiError(message, response.status);
  }
  return payload as T;
}
