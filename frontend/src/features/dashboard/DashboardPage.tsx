import { ArrowRight, BookOpenCheck, CalendarClock, Check, CirclePlus, Flame, FolderPlus, Sparkles, Trophy } from "lucide-react";
import { type FormEvent, useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { coursesApi } from "../../api/courses";
import type { CourseDetail, CourseSummary, Task } from "../../api/types";
import { ApiError } from "../../api/client";
import { useSession } from "../../auth/SessionProvider";
import { Button, EmptyState, Modal, Notice, PageLoader, ProgressBar } from "../../components/ui";
import { courseAccent, formatDeadline, isDueSoon, TASK_TYPE_LABELS, userProgress } from "../../utils/format";

type Filter = "all" | "open" | "completed" | "soon";
interface TaskWithCourse { task: Task; course: CourseSummary; }

export function DashboardPage() {
  const { user, refreshUser } = useSession();
  const [courses, setCourses] = useState<CourseSummary[]>([]);
  const [courseDetails, setCourseDetails] = useState<CourseDetail[]>([]);
  const [filter, setFilter] = useState<Filter>("all");
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const [showCreate, setShowCreate] = useState(false);
  const [toast, setToast] = useState("");

  const load = useCallback(async () => {
    setError("");
    try {
      const summaries = await coursesApi.list();
      setCourses(summaries);
      const details = await Promise.all(summaries.map(async (course) => {
        try { return await coursesApi.get(course.id); } catch { return null; }
      }));
      setCourseDetails(details.filter((item): item is CourseDetail => item !== null));
    } catch (reason) {
      setError(reason instanceof ApiError ? reason.message : "Could not load your courses.");
    } finally { setIsLoading(false); }
  }, []);

  useEffect(() => { void load(); }, [load]);

  const tasks = useMemo<TaskWithCourse[]>(() => courseDetails.flatMap((detail) => {
    const course = courses.find((item) => item.id === detail.id);
    return course ? detail.tasks.map((task) => ({ task, course })) : [];
  }).sort((a, b) => {
    if (!a.task.deadline) return 1;
    if (!b.task.deadline) return -1;
    return new Date(a.task.deadline).getTime() - new Date(b.task.deadline).getTime();
  }), [courseDetails, courses]);

  const filteredTasks = tasks.filter(({ task }) => filter === "all" || (filter === "open" && !task.completed) || (filter === "completed" && task.completed) || (filter === "soon" && !task.completed && isDueSoon(task.deadline)));
  const completedCount = courses.reduce((sum, course) => sum + course.completedTaskCount, 0);
  const totalCount = courses.reduce((sum, course) => sum + course.taskCount, 0);

  const complete = async (courseId: number, taskId: number) => {
    setError("");
    try {
      const previousXp = courseDetails.find((detail) => detail.id === courseId)?.progress.totalXp ?? 0;
      const result = await coursesApi.completeTask(courseId, taskId);
      const earnedXp = Math.max(result.progress.totalXp - previousXp, result.task.baseXp);
      setToast(`+${earnedXp} XP earned. Great work!`);
      await Promise.all([load(), refreshUser()]);
    } catch (reason) { setError(reason instanceof ApiError ? reason.message : "Could not complete that task."); }
  };

  if (isLoading) return <PageLoader />;
  return <div className="page dashboard-page">
    {toast && <Notice tone="success" onClose={() => setToast("")}>{toast}</Notice>}
    {error && <Notice onClose={() => setError("")}>{error}</Notice>}
    <section className="dashboard-hero">
      <div className="hero-copy"><p className="eyebrow">Your study command center</p><h1>Hey, {user?.username}.<br /><em>Make today count.</em></h1><p>Small, focused wins build remarkable progress. Choose your next task and keep the trek moving.</p><div className="hero-actions"><Button className="primary-button" onClick={() => setShowCreate(true)}><CirclePlus size={18} />New course</Button><a className="text-link" href="#upcoming">See upcoming <ArrowRight size={16} /></a></div></div>
      <article className="level-card"><div className="level-card-top"><div><span className="eyebrow">Current standing</span><strong>Level {user?.level ?? 1}</strong></div><div className="hero-trophy"><Trophy size={27} /></div></div><div className="xp-number">{(user?.xp ?? 0).toLocaleString()} <span>XP</span></div><ProgressBar value={userProgress(user?.xp ?? 0)} label="Progress to your next level" /><div className="level-card-bottom"><span><Flame size={16} />{user?.streak ?? 0}-day streak</span><span><BookOpenCheck size={16} />{completedCount} tasks done</span></div></article>
    </section>

    <section className="section-header"><div><p className="eyebrow">Your subjects</p><h2>Course collection</h2></div><Button className="secondary-button" onClick={() => setShowCreate(true)}><FolderPlus size={17} />Add course</Button></section>
    {courses.length ? <div className="course-grid">{courses.map((course) => <CourseCard key={course.id} course={course} />)}</div> : <EmptyState title="Your course collection is waiting" description="Create your first course to turn your study plan into a focused, rewarding trek." action={<Button className="primary-button" onClick={() => setShowCreate(true)}><CirclePlus size={18} />Create a course</Button>} />}

    <section className="upcoming-section" id="upcoming"><div className="section-header"><div><p className="eyebrow">What is next</p><h2>Upcoming tasks</h2></div><div className="filter-row" aria-label="Task filters">{(["all", "open", "completed", "soon"] as Filter[]).map((item) => <button key={item} className={filter === item ? "active" : ""} onClick={() => setFilter(item)}>{item === "soon" ? "Due soon" : item === "open" ? "In progress" : item[0].toUpperCase() + item.slice(1)}</button>)}</div></div>
      {filteredTasks.length ? <div className="task-list">{filteredTasks.slice(0, 8).map(({ task, course }) => <article className={`upcoming-task ${task.completed ? "completed" : ""}`} key={`${course.id}-${task.id}`}><div className={`task-type-dot ${courseAccent(course.id)}`} /><div className="task-main"><div className="task-title-row"><h3>{task.title}</h3><span className="type-label">{TASK_TYPE_LABELS[task.type] ?? task.type}</span></div><div className="task-meta"><span><BookOpenCheck size={15} />{course.name}</span><span className={isDueSoon(task.deadline) ? "due-soon" : ""}><CalendarClock size={15} />{formatDeadline(task.deadline)}</span><span><Sparkles size={15} />{task.baseXp} XP</span></div></div>{task.completed ? <span className="done-badge"><Check size={15} />Done</span> : <Button className="complete-button" onClick={() => void complete(course.id, task.id)} aria-label={`Complete ${task.title}`}><Check size={18} /></Button>}</article>)}</div> : <EmptyState title={courses.length ? "Nothing in this view" : "No tasks yet"} description={courses.length ? "Try another filter or add a task to one of your courses." : "Create a course, then add your first task."} />}
    </section>
    {showCreate && <CourseForm onClose={() => setShowCreate(false)} onCreated={async () => { setShowCreate(false); setIsLoading(true); await load(); }} />}
  </div>;
}

function CourseCard({ course }: { course: CourseSummary }) {
  const percentage = course.taskCount ? (course.completedTaskCount / course.taskCount) * 100 : 0;
  return <Link to={`/courses/${course.id}`} className={`course-card ${courseAccent(course.id)}`}><div className="course-card-glow" /><div className="course-card-top"><span>Course</span><ArrowRight size={19} /></div><h3>{course.name}</h3><div className="course-card-footer"><div><span>{course.completedTaskCount} of {course.taskCount} complete</span><ProgressBar value={percentage} /></div><div className="course-fraction">{Math.round(percentage)}%</div></div></Link>;
}

function CourseForm({ onClose, onCreated }: { onClose: () => void; onCreated: () => Promise<void> }) {
  const [name, setName] = useState(""); const [error, setError] = useState(""); const [submitting, setSubmitting] = useState(false);
  const submit = async (event: FormEvent) => { event.preventDefault(); if (!name.trim()) { setError("Give your course a name."); return; } setSubmitting(true); setError(""); try { await coursesApi.create(name.trim()); await onCreated(); } catch (reason) { setError(reason instanceof ApiError ? reason.message : "Could not create this course."); setSubmitting(false); } };
  return <Modal title="Create a new course" onClose={onClose}><form className="modal-form" onSubmit={submit}><p>Start with a subject, module, or personal learning goal.</p><label><span>Course name</span><input autoFocus value={name} onChange={(event) => setName(event.target.value)} placeholder="e.g. Data Structures" disabled={submitting} /></label>{error && <Notice>{error}</Notice>}<div className="form-actions"><Button type="button" className="secondary-button" onClick={onClose}>Cancel</Button><Button type="submit" className="primary-button" disabled={submitting}>Create course</Button></div></form></Modal>;
}
