import { ArrowLeft, CalendarClock, Check, ChevronRight, ClipboardList, Edit3, Flame, FolderPen, Pencil, Plus, Sparkles, Trash2, Trophy } from "lucide-react";
import { type FormEvent, useCallback, useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { coursesApi } from "../../api/courses";
import { ApiError } from "../../api/client";
import type { CourseDetail, CreateTaskInput, EditTaskInput, Task, TaskType } from "../../api/types";
import { useSession } from "../../auth/SessionProvider";
import { Button, EmptyState, Modal, Notice, PageLoader, ProgressBar } from "../../components/ui";
import { formatDeadline, TASK_TYPE_LABELS, toDateTimeLocal } from "../../utils/format";

export function CourseDetailPage() {
  const { courseId } = useParams();
  const id = Number(courseId);
  const navigate = useNavigate();
  const { refreshUser } = useSession();
  const [course, setCourse] = useState<CourseDetail | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [showTaskForm, setShowTaskForm] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [isRenaming, setIsRenaming] = useState(false);
  const [name, setName] = useState("");

  const load = useCallback(async () => {
    if (!Number.isInteger(id) || id < 1) { setError("This course link is not valid."); setIsLoading(false); return; }
    try { setError(""); const detail = await coursesApi.get(id); setCourse(detail); setName(detail.name); }
    catch (reason) { setError(reason instanceof ApiError ? reason.message : "Could not find this course."); }
    finally { setIsLoading(false); }
  }, [id]);

  useEffect(() => { void load(); }, [load]);

  const complete = async (task: Task) => {
    try {
      const result = await coursesApi.completeTask(id, task.id);
      const earnedXp = Math.max(result.progress.totalXp - (course?.progress.totalXp ?? 0), result.task.baseXp);
      setNotice(`Task complete! You earned ${earnedXp} XP.`);
      await Promise.all([load(), refreshUser()]);
    } catch (reason) { setError(reason instanceof ApiError ? reason.message : "Could not complete that task."); }
  };
  const removeTask = async (task: Task) => {
    if (!window.confirm(`Delete "${task.title}"? This cannot be undone.`)) return;
    try { await coursesApi.removeTask(id, task.id); setNotice("Task deleted."); await load(); }
    catch (reason) { setError(reason instanceof ApiError ? reason.message : "Could not delete that task."); }
  };
  const deleteCourse = async () => {
    if (!window.confirm(`Delete the ${course?.name} course and all its tasks? This cannot be undone.`)) return;
    try { await coursesApi.remove(id); navigate("/dashboard", { replace: true }); }
    catch (reason) { setError(reason instanceof ApiError ? reason.message : "Could not delete this course."); }
  };
  const rename = async (event: FormEvent) => {
    event.preventDefault(); if (!name.trim()) { setError("Course name cannot be empty."); return; }
    try { await coursesApi.rename(id, name.trim()); setIsRenaming(false); setNotice("Course renamed."); await load(); }
    catch (reason) { setError(reason instanceof ApiError ? reason.message : "Could not rename this course."); }
  };

  if (isLoading) return <PageLoader />;
  if (!course) return <div className="page page-narrow"><Notice>{error || "Course not found."}</Notice><Link className="text-link" to="/dashboard"><ArrowLeft size={16} />Back to dashboard</Link></div>;
  const todo = course.tasks.filter((task) => !task.completed);
  const completed = course.tasks.filter((task) => task.completed);
  const progress = course.progress;

  return <div className="page course-page">
    {error && <Notice onClose={() => setError("")}>{error}</Notice>}{notice && <Notice tone="success" onClose={() => setNotice("")}>{notice}</Notice>}
    <nav className="breadcrumbs"><Link to="/dashboard"><ArrowLeft size={16} />Dashboard</Link><ChevronRight size={15} /><span>{course.name}</span></nav>
    <section className="course-header"><div>{isRenaming ? <form className="rename-form" onSubmit={rename}><input autoFocus value={name} onChange={(event) => setName(event.target.value)} /><Button className="primary-button" type="submit">Save</Button><Button className="secondary-button" type="button" onClick={() => { setName(course.name); setIsRenaming(false); }}>Cancel</Button></form> : <><p className="eyebrow">Course overview</p><h1>{course.name}</h1><button className="inline-action" onClick={() => setIsRenaming(true)}><Pencil size={15} />Rename course</button></>}</div><div className="course-header-actions"><Button className="primary-button" onClick={() => { setEditingTask(null); setShowTaskForm(true); }}><Plus size={18} />Add task</Button><button className="danger-button" onClick={() => void deleteCourse()}><Trash2 size={17} /><span>Delete</span></button></div></section>
    <section className="course-stats"><article className="course-stat-main"><div><p className="eyebrow">Course progress</p><h2>Level {progress.level} <span>{progress.levelName}</span></h2></div><div className="course-level-icon"><Trophy size={27} /></div><ProgressBar value={progress.progressPercentage} label={progress.isMaxLevel ? "Maximum level reached" : `${progress.xpToNextLevel} XP until next level`} /><div className="course-stat-foot"><span>{progress.totalXp} XP earned</span><span>{progress.isMaxLevel ? "Max level" : `${progress.xpInCurrentLevel} / ${progress.totalXpForCurrentLevel} XP`}</span></div></article><article className="course-small-stat"><ClipboardList size={20} /><strong>{todo.length}</strong><span>Tasks to do</span></article><article className="course-small-stat complete"><Check size={20} /><strong>{completed.length}</strong><span>Tasks completed</span></article></section>
    <TaskSection title="To do" subtitle={`${todo.length} task${todo.length === 1 ? "" : "s"} ready when you are`} tasks={todo} courseId={id} onComplete={complete} onEdit={(task) => { setEditingTask(task); setShowTaskForm(true); }} onDelete={removeTask} emptyAction={() => { setEditingTask(null); setShowTaskForm(true); }} />
    <TaskSection title="Completed" subtitle={`${completed.length} win${completed.length === 1 ? "" : "s"} collected`} tasks={completed} courseId={id} onComplete={complete} onEdit={(task) => { setEditingTask(task); setShowTaskForm(true); }} onDelete={removeTask} />
    {showTaskForm && <TaskForm courseId={id} task={editingTask} onClose={() => { setShowTaskForm(false); setEditingTask(null); }} onSaved={async (message) => { setShowTaskForm(false); setEditingTask(null); setNotice(message); await load(); }} />}
  </div>;
}

function TaskSection({ title, subtitle, tasks, onComplete, onEdit, onDelete, emptyAction }: { title: string; subtitle: string; tasks: Task[]; courseId: number; onComplete: (task: Task) => void; onEdit: (task: Task) => void; onDelete: (task: Task) => void; emptyAction?: () => void; }) {
  return <section className="task-section"><div className="section-header"><div><p className="eyebrow">{subtitle}</p><h2>{title}</h2></div>{title === "To do" && <Button className="secondary-button" onClick={emptyAction}><Plus size={17} />Add task</Button>}</div>{tasks.length ? <div className="detail-task-list">{tasks.map((task) => <TaskCard key={task.id} task={task} onComplete={onComplete} onEdit={onEdit} onDelete={onDelete} />)}</div> : <EmptyState title={title === "To do" ? "Your slate is clear" : "No completed tasks yet"} description={title === "To do" ? "Add a concrete next step to keep the momentum going." : "Finished tasks will become part of your win collection."} action={title === "To do" && emptyAction ? <Button className="primary-button" onClick={emptyAction}><Plus size={18} />Add your first task</Button> : undefined} />}</section>;
}

function TaskCard({ task, onComplete, onEdit, onDelete }: { task: Task; onComplete: (task: Task) => void; onEdit: (task: Task) => void; onDelete: (task: Task) => void }) {
  return <article className={`detail-task ${task.completed ? "completed" : ""}`}><button className="task-check" disabled={task.completed} onClick={() => onComplete(task)} aria-label={task.completed ? `${task.title} completed` : `Complete ${task.title}`}>{task.completed && <Check size={16} />}</button><div className="detail-task-content"><div className="task-title-row"><h3>{task.title}</h3><span className="type-label">{TASK_TYPE_LABELS[task.type] ?? task.type}</span></div>{task.description && <p>{task.description}</p>}<div className="task-meta"><span><CalendarClock size={15} />{formatDeadline(task.deadline)}</span><span><Sparkles size={15} />{task.baseXp} XP</span></div></div><div className="task-actions"><button onClick={() => onEdit(task)} aria-label={`Edit ${task.title}`}><Edit3 size={17} /></button><button onClick={() => onDelete(task)} aria-label={`Delete ${task.title}`}><Trash2 size={17} /></button></div></article>;
}

function TaskForm({ courseId, task, onClose, onSaved }: { courseId: number; task: Task | null; onClose: () => void; onSaved: (message: string) => Promise<void>; }) {
  const [title, setTitle] = useState(task?.title ?? "");
  const [description, setDescription] = useState(task?.description ?? "");
  const [deadline, setDeadline] = useState(toDateTimeLocal(task?.deadline ?? null));
  const [type, setType] = useState<TaskType>(task?.type ?? "GENERAL");
  const [error, setError] = useState(""); const [submitting, setSubmitting] = useState(false);
  const editing = Boolean(task);
  const submit = async (event: FormEvent) => {
    event.preventDefault(); if (!title.trim()) { setError("A task title is required."); return; }
    setError(""); setSubmitting(true);
    try {
      if (task) {
        const input: EditTaskInput = { title: title.trim(), description, deadline: deadline || null };
        await coursesApi.updateTask(courseId, task.id, input);
        await onSaved("Task updated.");
      } else {
        const input: CreateTaskInput = { title: title.trim(), ...(description ? { description } : {}), ...(deadline ? { deadline } : {}), ...(type !== "GENERAL" ? { taskType: type } : {}) };
        await coursesApi.createTask(courseId, input);
        await onSaved("Task added to your course.");
      }
    } catch (reason) { setError(reason instanceof ApiError ? reason.message : "Could not save this task."); setSubmitting(false); }
  };
  return <Modal title={editing ? "Edit task" : "Add a task"} onClose={onClose}><form className="modal-form task-form" onSubmit={submit}><label><span>Task title</span><input autoFocus value={title} onChange={(event) => setTitle(event.target.value)} placeholder="e.g. Finish chapter 3 exercises" disabled={submitting} /></label><label><span>Description <small>Optional</small></span><textarea value={description} onChange={(event) => setDescription(event.target.value)} placeholder="Add useful details or a study note" rows={3} disabled={submitting} /></label><div className="form-two-column"><label><span>Deadline <small>Optional</small></span><input type="datetime-local" value={deadline} onChange={(event) => setDeadline(event.target.value)} disabled={submitting} /></label>{!editing && <label><span>Task type</span><select value={type} onChange={(event) => setType(event.target.value as TaskType)} disabled={submitting}>{(Object.keys(TASK_TYPE_LABELS) as TaskType[]).map((value) => <option key={value} value={value}>{TASK_TYPE_LABELS[value]}</option>)}</select></label>}</div>{error && <Notice>{error}</Notice>}<div className="form-actions"><Button type="button" className="secondary-button" onClick={onClose}>Cancel</Button><Button type="submit" className="primary-button" disabled={submitting}>{submitting ? "Saving..." : editing ? "Save changes" : "Add task"}</Button></div></form></Modal>;
}
