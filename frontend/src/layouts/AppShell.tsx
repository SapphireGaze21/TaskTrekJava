import { BookOpen, Flame, LayoutDashboard, LogOut, Trophy, UserRound } from "lucide-react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useSession } from "../auth/SessionProvider";

export function AppShell({ children }: { children: React.ReactNode }) {
  const { user, logout } = useSession();
  const navigate = useNavigate();
  const leave = () => { logout(); navigate("/login", { replace: true }); };
  return <div className="app-shell"><header className="topbar"><Link to="/dashboard" className="brand" aria-label="TaskTrek dashboard"><span className="brand-mark"><BookOpen size={19} /></span><span>Task<span>Trek</span></span></Link><nav className="main-nav" aria-label="Primary navigation"><NavLink to="/dashboard"><LayoutDashboard size={17} />Dashboard</NavLink><NavLink to="/profile"><UserRound size={17} />Profile</NavLink></nav><div className="topbar-user"><div className="stat-pill level"><Trophy size={15} /><span>Lv. {user?.level ?? 1}</span></div><div className="stat-pill streak"><Flame size={15} /><span>{user?.streak ?? 0}</span></div><button className="logout-button" onClick={leave}><LogOut size={17} /><span>Log out</span></button></div></header><main>{children}</main></div>;
}
