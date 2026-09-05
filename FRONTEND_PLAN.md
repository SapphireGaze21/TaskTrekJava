# TaskTrek Frontend Plan

## Goal

Build a responsive single-page web app for TaskTrek that makes study planning feel rewarding. The frontend will consume the existing Spring Boot API at `/api`, let a student manage courses and tasks, and make XP, levels, streaks, and task completion visible throughout the experience.

The visual direction is based on `frontend_1_1.ts`, `frontend_1_2.css`, and `frontend_1_3.html`: a dark, immersive background; frosted translucent surfaces; bold type; colour-coded cards; subtle motion; horizontal discovery rows; and layouts that collapse cleanly on smaller screens. The reference's placeholder weather, restaurant, tool, and movie sections will be replaced by TaskTrek data and actions.

## Product Structure

```text
Unauthenticated
  Landing / sign in / register
      |
Authenticated
  Dashboard ---- Course detail ---- Task create/edit
      |                |
      +---- Profile ---+---- Complete task -> XP/streak feedback
```

### 1. Landing and authentication

- Full-viewport dark study-themed background with a readable gradient overlay.
- Centre-aligned glass authentication panel instead of the reference PIN lock screen.
- Toggle between **Sign in** and **Create account** using `username` and `password` fields.
- Loading, validation, and API-error states; buttons are disabled while requests are in flight.
- On success, persist the JWT, store the returned user, and navigate to the dashboard.
- If a saved token exists, call `GET /auth/me` before rendering authenticated content; clear the session and return to sign-in on a 401/403 response.

### 2. Dashboard

The dashboard is the main replacement for the reference menu.

| Area | Content and interaction | API/data source |
| --- | --- | --- |
| Top bar | TaskTrek logo, greeting, level badge, XP total, streak, logout | authenticated user from `/auth/me` |
| Hero progress card | Level, XP, progress-to-next-level indicator, current streak, motivational copy | user `level`, `xp`, `streak` |
| Quick navigation | Chips for All, In progress, Completed, Due soon; scrollable on mobile | client-side course/task state |
| Course gallery | One accent-colour card per course; course name, completion fraction, progress bar, open action | `GET /courses` |
| Upcoming tasks | Tasks from selected/all loaded courses, ordered by deadline; task type, course, deadline, XP, completion control | course detail `tasks` |
| Empty states | Helpful CTA when there are no courses or tasks | client state |

The gallery borrows the reference's image-card hierarchy, but uses accessible solid/gradient accent backgrounds rather than relying on remote decorative images. Course accent colour can be deterministically derived from the course ID so it remains stable.

### 3. Course detail

- Breadcrumb/back action, editable course name, course progress card, and a destructive delete action with confirmation.
- Segmented task list: **To do** and **Completed**. Each task card shows type, title, optional description, deadline, and base XP.
- Add-task button opens a modal or side panel; edit opens the same component prefilled.
- Completing a task immediately shows a small success animation/toast and refreshes both course progress and the authenticated user so newly earned XP and streak are accurate.
- Completion should be irreversible in the initial UI because the API exposes completion but no uncomplete route.

### 4. Task form

- Required: `title`.
- Optional: `description`, local date/time `deadline`.
- Type selector: General, Assignment, Project, Quiz prep, Exam prep. Send `ASSIGNMENT`, `PROJECT`, `QUIZPREP`, or `EXAMPREP`; omit/generalise the type for General.
- Inline client validation and API-error display. Use a browser-local date/time value and serialize it as the API's ISO local date-time format.

### 5. Profile and settings

- Compact profile view showing username, total XP, level, streak, and last completion date when available.
- Logout clears locally stored authentication state and routes to sign-in.
- This view does not require a separate backend endpoint beyond `/auth/me`.

## Technical Approach

### Recommended stack

- **React + TypeScript + Vite** for a fast, component-based SPA. The reference is already React/TypeScript in spirit, so its interaction patterns translate naturally.
- **React Router** for `/login`, `/register`, `/dashboard`, `/courses/:courseId`, and `/profile`.
- **CSS Modules or a small global token stylesheet** using plain CSS; do not copy the reference's Sass-style nesting into a `.css` file. Use CSS custom properties for theme tokens.
- **Lucide React** (or another one consistent icon set) for navigation, task-type, status, and action icons.
- Native `fetch` in a typed API client. A data-fetching library is optional; begin with a small hook/context layer and add TanStack Query only if cache invalidation becomes repetitive.

### Proposed project layout

```text
frontend/
  src/
    api/          # apiClient, authApi, coursesApi and response types
    components/   # reusable UI: Button, Card, Modal, ProgressBar, EmptyState
    features/
      auth/       # AuthPage, auth form and session provider
      dashboard/  # Dashboard, course gallery, upcoming tasks
      courses/    # CourseDetail, CourseForm, TaskForm, TaskList
      profile/    # ProfilePage
    layouts/      # App shell and protected-route wrapper
    styles/       # tokens, reset, global responsive rules
    utils/        # deadline and progress formatting
    App.tsx
```

### Frontend state and API rules

- Keep `{ token, user }` in a session provider and persist the token in `localStorage` for the initial version. Attach `Authorization: Bearer <token>` to every non-auth request.
- Put `VITE_API_BASE_URL=http://localhost:8080/api` in `.env.example`; use the environment value rather than hard-coding a host.
- The API client parses `{ message }` and detailed error bodies into a user-safe error message, and treats 401/403 globally by expiring the session.
- After a mutation, refresh or update the affected course summary/detail and user profile. This is especially important after task completion, which changes progress and gamification data.
- Render server fields exactly as documented: course summaries use `name`, while course details use `name`, `tasks`, and `progress`; tasks use `type`, `completed`, `deadline`, and `baseXp`.

## API-to-UI Map

| User action | Request | UI result |
| --- | --- | --- |
| Register / sign in | `POST /auth/register`, `POST /auth/login` | establish authenticated session |
| Restore session | `GET /auth/me` | populate header, hero, and profile |
| View courses | `GET /courses` | course gallery and dashboard totals |
| Add / rename / delete course | `POST`, `PUT`, `DELETE /courses/{courseId}` | refresh course gallery; return to dashboard after deletion |
| Open course | `GET /courses/{courseId}` and `GET /courses/{courseId}/progress` as needed | render task sections and course XP card |
| Add task | `POST /courses/{courseId}/tasks` | insert task into To do list |
| Edit / delete task | `PATCH`, `DELETE /courses/{courseId}/tasks/{taskId}` | update/remove task card and course counts |
| Complete task | `POST /courses/{courseId}/tasks/{taskId}/complete` | mark complete, celebrate, update progress and user stats |

## Design System

- **Base:** near-black/navy page background (`#15171d` family), large soft background shapes or a locally licensed study image, and a dark overlay to preserve contrast.
- **Surfaces:** semi-transparent charcoal panels with a 1px low-opacity border, restrained blur, 12–16px radius, and light elevation.
- **Accents:** blue as the primary interaction colour; violet, green, yellow, orange, and red as stable course/task-type accents. Do not make colour the sole carrier of status.
- **Typography:** an expressive display face for page/hero headings and a clean sans-serif body face. Keep a system-font fallback and use at least 16px body text.
- **Motion:** 150–250ms opacity/transform transitions, card lift on hover, a single XP burst on task completion, and `prefers-reduced-motion` fallbacks. Avoid permanent decorative animation.
- **Responsive behaviour:** desktop uses a max-width content rail with 3–4 course cards; tablet uses two columns; phones use one column, a sticky compact header, and horizontally scrollable filters. All actions need keyboard focus states and touch targets of at least 44px.

## Delivery Phases

1. **Foundation:** scaffold the Vite React app, install routing/icons, define tokens, API client, typed DTOs, environment configuration, and session restoration.
2. **Authentication:** implement register, login, logout, protected routes, validation, and expired-token handling.
3. **Courses and tasks:** build dashboard, course detail, CRUD forms, deletion confirmations, loading skeletons, and empty/error states.
4. **Gamification polish:** add XP/level/streak hero cards, course progress, completion feedback, filters, responsive refinements, accessibility review, and smoke tests against the local API.

## Acceptance Criteria

- A new user can register, sign in, reload the page, and remain signed in while the JWT is valid.
- The dashboard shows real course summaries and user gamification data, with useful empty and failure states.
- A user can create, rename, and delete their own courses; create, edit, delete, and complete tasks inside a course.
- Completing a task visibly updates its status, course progress, XP/level/streak information, and handles API failures without corrupting the view.
- The UI works from a 320px-wide phone viewport through desktop, supports keyboard navigation, respects reduced motion, and maintains readable contrast.
- No reference-demo content (PIN `1234`, weather, restaurants, movies, or third-party image dependencies) remains in the production TaskTrek experience.
