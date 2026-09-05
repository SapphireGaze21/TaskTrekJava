import { ArrowRight, BookOpen, LockKeyhole, UserRound } from "lucide-react";
import { type FormEvent, useState } from "react";
import { Navigate, useLocation, useNavigate } from "react-router-dom";
import { authApi } from "../../api/auth";
import { ApiError } from "../../api/client";
import { useSession } from "../../auth/SessionProvider";
import { Button, Notice } from "../../components/ui";

type AuthMode = "login" | "register";

export function AuthPage({ mode: initialMode }: { mode: AuthMode }) {
  const [mode, setMode] = useState<AuthMode>(initialMode);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { user, saveSession } = useSession();
  const navigate = useNavigate();
  const location = useLocation();
  if (user) return <Navigate to="/dashboard" replace />;

  const switchMode = (nextMode: AuthMode) => {
    setMode(nextMode); setError("");
    navigate(nextMode === "login" ? "/login" : "/register", { replace: true });
  };

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    const cleanUsername = username.trim();
    if (!cleanUsername || !password) { setError("Enter both a username and password."); return; }
    setError(""); setIsSubmitting(true);
    try {
      const response = mode === "login" ? await authApi.login(cleanUsername, password) : await authApi.register(cleanUsername, password);
      saveSession(response);
      navigate((location.state as { from?: string } | null)?.from ?? "/dashboard", { replace: true });
    } catch (reason) {
      setError(reason instanceof ApiError ? reason.message : "Unable to start your session.");
    } finally { setIsSubmitting(false); }
  };

  const isLogin = mode === "login";
  return <div className="auth-page"><div className="auth-glow auth-glow-one" /><div className="auth-glow auth-glow-two" /><div className="auth-content"><section className="auth-intro"><div className="brand brand-large"><span className="brand-mark"><BookOpen size={24} /></span><span>Task<span>Trek</span></span></div><p className="eyebrow">A better way to study</p><h1>Make every study session count.</h1><p className="auth-copy">Organize your courses, finish meaningful tasks, and watch your progress turn into momentum.</p><div className="auth-points"><span><i />Plan with clarity</span><span><i />Earn XP as you finish</span><span><i />Build your streak</span></div></section><section className="auth-card" aria-labelledby="auth-heading"><p className="eyebrow">{isLogin ? "Welcome back" : "Start your trek"}</p><h2 id="auth-heading">{isLogin ? "Sign in to TaskTrek" : "Create your account"}</h2><p>{isLogin ? "Pick up where you left off." : "Your next focused study session starts here."}</p><div className="auth-toggle" role="tablist" aria-label="Authentication option"><button className={isLogin ? "active" : ""} onClick={() => switchMode("login")} role="tab" aria-selected={isLogin}>Sign in</button><button className={!isLogin ? "active" : ""} onClick={() => switchMode("register")} role="tab" aria-selected={!isLogin}>Create account</button></div><form onSubmit={submit} noValidate><label><span>Username</span><div className="field-wrap"><UserRound size={18} /><input autoComplete="username" value={username} onChange={(event) => setUsername(event.target.value)} placeholder="Your study name" disabled={isSubmitting} /></div></label><label><span>Password</span><div className="field-wrap"><LockKeyhole size={18} /><input autoComplete={isLogin ? "current-password" : "new-password"} type="password" value={password} onChange={(event) => setPassword(event.target.value)} placeholder="Your password" disabled={isSubmitting} /></div></label>{error && <Notice>{error}</Notice>}<Button type="submit" className="primary-button auth-submit" disabled={isSubmitting}>{isSubmitting ? "Please wait..." : isLogin ? "Enter TaskTrek" : "Create account"}<ArrowRight size={18} /></Button></form></section></div></div>;
}
