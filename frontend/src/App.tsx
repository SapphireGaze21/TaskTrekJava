import { BrowserRouter, Navigate, Outlet, Route, Routes, useLocation } from "react-router-dom";
import { useSession, SessionProvider } from "./auth/SessionProvider";
import { PageLoader } from "./components/ui";
import { AuthPage } from "./features/auth/AuthPage";
import { CourseDetailPage } from "./features/courses/CourseDetailPage";
import { DashboardPage } from "./features/dashboard/DashboardPage";
import { ProfilePage } from "./features/profile/ProfilePage";
import { AppShell } from "./layouts/AppShell";
import { ThemeProvider } from "./theme/ThemeProvider";

function ProtectedLayout() {
  const { user, isBootstrapping } = useSession();
  const location = useLocation();
  if (isBootstrapping) return <PageLoader />;
  if (!user) return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  return <AppShell><Outlet /></AppShell>;
}

function PublicRoute({ mode }: { mode: "login" | "register" }) {
  const { isBootstrapping } = useSession();
  if (isBootstrapping) return <PageLoader />;
  return <AuthPage mode={mode} />;
}

export function App() {
  return <BrowserRouter><ThemeProvider><SessionProvider><Routes><Route path="/login" element={<PublicRoute mode="login" />} /><Route path="/register" element={<PublicRoute mode="register" />} /><Route element={<ProtectedLayout />}><Route path="/dashboard" element={<DashboardPage />} /><Route path="/courses/:courseId" element={<CourseDetailPage />} /><Route path="/profile" element={<ProfilePage />} /></Route><Route path="*" element={<Navigate to="/dashboard" replace />} /></Routes></SessionProvider></ThemeProvider></BrowserRouter>;
}
