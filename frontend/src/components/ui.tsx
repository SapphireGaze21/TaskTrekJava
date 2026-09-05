import { AlertCircle, CheckCircle2, X } from "lucide-react";
import { useEffect, type ButtonHTMLAttributes, type ReactNode } from "react";

export function Button({ className = "", children, ...props }: ButtonHTMLAttributes<HTMLButtonElement>) {
  return <button className={`button ${className}`.trim()} {...props}>{children}</button>;
}

export function ProgressBar({ value, label }: { value: number; label?: string }) {
  const safeValue = Math.max(0, Math.min(100, value));
  return <div className="progress-wrap">{label && <div className="progress-label">{label}</div>}<div className="progress-track" aria-label={label} aria-valuemin={0} aria-valuemax={100} aria-valuenow={Math.round(safeValue)} role="progressbar"><div className="progress-fill" style={{ width: `${safeValue}%` }} /></div></div>;
}

export function Modal({ title, children, onClose }: { title: string; children: ReactNode; onClose: () => void }) {
  useEffect(() => {
    const closeOnEscape = (event: KeyboardEvent) => event.key === "Escape" && onClose();
    window.addEventListener("keydown", closeOnEscape);
    return () => window.removeEventListener("keydown", closeOnEscape);
  }, [onClose]);
  return <div className="modal-backdrop" role="presentation" onMouseDown={onClose}><section className="modal" role="dialog" aria-modal="true" aria-labelledby="modal-title" onMouseDown={(event) => event.stopPropagation()}><div className="modal-header"><h2 id="modal-title">{title}</h2><button className="icon-button" onClick={onClose} aria-label="Close dialog"><X size={20} /></button></div>{children}</section></div>;
}

export function EmptyState({ title, description, action }: { title: string; description: string; action?: ReactNode }) {
  return <div className="empty-state"><div className="empty-icon"><CheckCircle2 size={24} /></div><h3>{title}</h3><p>{description}</p>{action}</div>;
}

export function Notice({ tone = "error", children, onClose }: { tone?: "error" | "success"; children: ReactNode; onClose?: () => void }) {
  const Icon = tone === "success" ? CheckCircle2 : AlertCircle;
  return <div className={`notice ${tone}`} role="status"><Icon size={18} /><span>{children}</span>{onClose && <button onClick={onClose} aria-label="Dismiss message"><X size={16} /></button>}</div>;
}

export function PageLoader() { return <div className="page-loader"><div className="loader-orbit" /><span>Loading your trek...</span></div>; }
