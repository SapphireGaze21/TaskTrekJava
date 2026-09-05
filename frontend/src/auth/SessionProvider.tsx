import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { authApi } from "../api/auth";
import { setAccessToken, setUnauthorizedHandler } from "../api/client";
import type { AuthResponse, User } from "../api/types";

const STORAGE_KEY = "tasktrek.token";

interface SessionContextValue {
  user: User | null;
  isBootstrapping: boolean;
  saveSession: (response: AuthResponse) => void;
  logout: () => void;
  refreshUser: () => Promise<void>;
}

const SessionContext = createContext<SessionContextValue | null>(null);

export function SessionProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isBootstrapping, setIsBootstrapping] = useState(true);
  const logout = useCallback(() => { localStorage.removeItem(STORAGE_KEY); setAccessToken(null); setUser(null); }, []);
  const refreshUser = useCallback(async () => { setUser(await authApi.me()); }, []);
  const saveSession = useCallback((response: AuthResponse) => {
    localStorage.setItem(STORAGE_KEY, response.token);
    setAccessToken(response.token);
    setUser(response.user);
  }, []);

  useEffect(() => {
    setUnauthorizedHandler(logout);
    const savedToken = localStorage.getItem(STORAGE_KEY);
    if (!savedToken) { setIsBootstrapping(false); return () => setUnauthorizedHandler(undefined); }
    setAccessToken(savedToken);
    authApi.me().then(setUser).catch(logout).finally(() => setIsBootstrapping(false));
    return () => setUnauthorizedHandler(undefined);
  }, [logout]);

  const value = useMemo(() => ({ user, isBootstrapping, saveSession, logout, refreshUser }), [user, isBootstrapping, saveSession, logout, refreshUser]);
  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>;
}

export function useSession() {
  const value = useContext(SessionContext);
  if (!value) throw new Error("useSession must be used inside SessionProvider");
  return value;
}
