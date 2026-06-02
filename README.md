# Invtr

> Repository for team **The Night King** — AIBEST Tech Academy. Ask Yavor to setup .env for Gemini API and mailing. Current API keys are outdated

---

## Quick Start

**You do not need to install Java, Node.js, or PostgreSQL manually.**

1. Install **[Docker Desktop](https://www.docker.com/products/docker-desktop/)**.
2. Open a terminal in the root `Invtr` folder.
3. Build and start all containers:

```bash
docker-compose up --build
```

4. Wait for all services to report healthy, then open **http://localhost:5173**.

To stop everything:

```bash
docker-compose down
```

To stop and wipe the database volume:

```bash
docker-compose down -v
```

### Exposed Ports

| Port   | Service            |
|--------|--------------------|
| `5173` | Vue.js Frontend    |
| `8080` | API Gateway        |
| `5432` | PostgreSQL         |

Internal microservices (`8081`–`8084`) are reachable only inside the Docker network and are not exposed to the host by default.

---

## Project Structure

```
Invtr/
├── docker-compose.yml          # Orchestrates all services
├── postgres-init/
│   └── init-dbs.sql            # Creates auth_db, equipment_db, request_db on first boot
├── frontend/                   # Vue 3 single-page application
│   ├── Dockerfile
│   ├── vue.config.js           # Dev-server proxy (routes /auth, /equipment, etc. to gateway)
│   ├── public/
│   │   └── index.html
│   └── src/
│       ├── assets/             # CSS and images
│       ├── components/
│       │   └── admin/          # Admin dashboard sub-views (Inventory, Users, Requests, Reports…)
│       ├── locales/            # i18n strings (bg.json, en.json)
│       ├── router/
│       │   └── index.js        # Route definitions + JWT auth guard
│       ├── services/
│       │   └── toast.js        # Global toast notification helper
│       ├── views/
│       │   ├── Login.vue
│       │   ├── UserDashboard.vue
│       │   └── AdminDashboard.vue
│       ├── App.vue
│       ├── i18n.js
│       └── main.js
└── backend/
    ├── api-gateway/            # Spring Cloud Gateway — single entry point (port 8080)
    ├── auth-service/           # User registration, login, JWT issuance (port 8081)
    ├── equipment-service/      # Inventory CRUD, condition logs, AI assessment (port 8082)
    ├── request-service/        # Borrow requests, approvals, returns (port 8083)
    └── reports-service/        # CSV/Excel report export (port 8084)
```

Each backend service follows the same internal layout:

```
<service>/
├── Dockerfile
├── pom.xml
└── src/main/java/com/invtr/<service>/
    ├── controller/     # REST endpoints
    ├── service/        # Business logic
    ├── repository/     # Spring Data JPA interfaces
    ├── entity/         # JPA entities (database tables)
    ├── dto/            # Request/response payload classes
    ├── security/       # JwtFilter — validates the Bearer token on every request
    ├── config/         # SecurityConfig, bean wiring
    └── exception/      # GlobalExceptionHandler + custom exception classes
```

---

## Components

### API Gateway (`port 8080`)

The single entry point for all client traffic. Built with **Spring Cloud Gateway**, it declaratively routes requests to downstream services based on path prefix:

| Path prefix    | Forwarded to        |
|----------------|---------------------|
| `/auth/**`     | auth-service:8081   |
| `/equipment/**`| equipment-service:8082 |
| `/requests/**` | request-service:8083 |
| `/reports/**`  | reports-service:8084 |

The gateway also blocks external access to internal endpoints (e.g. `/auth/internal/**` returns `403` before the request reaches the service) and handles CORS for the frontend origin.

---

### Auth Service (`port 8081`)

Handles user identity. Stores users and roles in **`auth_db`** (PostgreSQL). On successful login it issues a signed **JWT** containing the user's `id`, `email`, and `role` (`USER` or `ADMIN`). That token is used by every other service to authorize requests.

Key endpoints:

| Method   | Path                          | Access  | Description                       |
|----------|-------------------------------|---------|-----------------------------------|
| `POST`   | `/auth/register`              | Public  | Create a new user account         |
| `POST`   | `/auth/login`                 | Public  | Authenticate and receive a JWT    |
| `GET`    | `/auth/users`                 | ADMIN   | List / filter all users           |
| `PATCH`  | `/auth/users/{id}`            | ADMIN   | Update user details               |
| `DELETE` | `/auth/users/{id}`            | ADMIN   | Delete a user                     |
| `GET`    | `/auth/internal/admins/emails`| Internal| Returns admin email list (used by equipment-service for notifications) |

---

### Equipment Service (`port 8082`)

Manages the equipment inventory stored in **`equipment_db`**. Supports CRUD operations, equipment status transitions (`AVAILABLE`, `IN_USE`, etc.), and condition logs.

Additional features:
- **Email notifications** — when stock runs low, it fetches admin emails from auth-service and sends alerts via SMTP.
- **AI condition assessment** — accepts a photo upload and calls the **Gemini API** to assess equipment condition and return a structured recommendation.

Key endpoints:

| Method   | Path                              | Access       | Description                          |
|----------|-----------------------------------|--------------|--------------------------------------|
| `GET`    | `/equipment`                      | USER, ADMIN  | List equipment (filterable by status/type/condition) |
| `GET`    | `/equipment/{id}`                 | USER, ADMIN  | Get a single item                    |
| `POST`   | `/equipment`                      | ADMIN        | Add new equipment                    |
| `PATCH`  | `/equipment/{id}`                 | ADMIN        | Update equipment details             |
| `PATCH`  | `/equipment/{id}/status`          | ADMIN        | Change equipment status              |
| `DELETE` | `/equipment/{id}`                 | ADMIN        | Remove equipment                     |
| `GET`    | `/equipment/condition-logs`       | USER, ADMIN  | List condition log entries           |
| `POST`   | `/equipment/{id}/assess-condition`| ADMIN        | AI-powered photo condition assessment|

---

### Request Service (`port 8083`)

Manages equipment borrow requests stored in **`request_db`**. Users submit requests; admins approve or reject them. When a request is approved, the service calls equipment-service to mark the items as `IN_USE`. When a return is processed, it calls equipment-service again to restore availability.

Key endpoints:

| Method | Path                   | Access      | Description                              |
|--------|------------------------|-------------|------------------------------------------|
| `GET`  | `/requests`            | USER, ADMIN | List requests for the current user       |
| `GET`  | `/requests/manager`    | ADMIN       | List all requests (manager view)         |
| `POST` | `/requests`            | USER        | Submit a new borrow request              |
| `PUT`  | `/requests/{id}/approve`| ADMIN      | Approve a request (marks equipment IN_USE)|
| `PUT`  | `/requests/{id}/reject` | ADMIN      | Reject a request                         |
| `PUT`  | `/requests/{id}/return` | ADMIN      | Process a return (restores equipment)    |

---

### Reports Service (`port 8084`)

A stateless service (no own database) that aggregates data from equipment-service and request-service and exports it as **CSV** or **Excel (XLSX)** files. All endpoints are admin-only.

| Method | Path                       | Description                                      |
|--------|----------------------------|--------------------------------------------------|
| `POST` | `/reports/usage`           | Export usage report for selected equipment IDs   |
| `GET`  | `/reports/history/{userId}`| Export full borrow history for a user            |
| `GET`  | `/reports/export-equipment`| Export full equipment inventory                  |
| `GET`  | `/reports/export-requests` | Export all borrow requests                       |

---

### Frontend (`port 5173`)

A **Vue 3** single-page application (Composition API, `<script setup>`). It communicates exclusively with the API Gateway.

After login, the JWT is stored in `localStorage`/`sessionStorage`. The router guard decodes the `role` claim client-side and redirects the user to the correct dashboard. Unauthenticated requests to protected routes are redirected to `/login`.

| Path               | View                  | Access      |
|--------------------|-----------------------|-------------|
| `/login`           | `Login.vue`           | Public      |
| `/dashboard`       | `UserDashboard.vue`   | USER only   |
| `/admin-dashboard` | `AdminDashboard.vue`  | ADMIN only  |

The admin dashboard is a shell (`AdminDashboard.vue`) that renders one of several sub-views (Inventory, Requests, Users, Reports, Settings) via the sidebar.

---

### PostgreSQL (`port 5432`)

A single Postgres container (`invtr-db`) shared by three services, each with its own isolated database:

| Database       | Owner service      |
|----------------|--------------------|
| `auth_db`      | auth-service       |
| `equipment_db` | equipment-service  |
| `request_db`   | request-service    |

Databases are created automatically by `postgres-init/init-dbs.sql` on the first boot. Schema migrations inside each database are applied by **Flyway** when the owning service starts.

---

## Service Communication

All inter-service calls go over the internal Docker bridge network (`invtr-network`) using container hostnames. No service is reachable from outside Docker except through the API Gateway.

```
Browser
  │
  │  HTTP (port 5173)
  ▼
Frontend (Vue)
  │
  │  HTTP /auth/**, /equipment/**, /requests/**, /reports/**
  │  (via vue.config.js proxy in dev, direct in Docker)
  ▼
API Gateway (port 8080)
  │
  ├──► auth-service:8081       (path: /auth/**)
  ├──► equipment-service:8082  (path: /equipment/**)
  ├──► request-service:8083    (path: /requests/**)
  └──► reports-service:8084    (path: /reports/**)
```

Backend services also call each other directly (bypassing the gateway) using Spring's `RestClient`:

| Caller              | Calls                    | Why                                              |
|---------------------|--------------------------|--------------------------------------------------|
| equipment-service   | auth-service             | Fetch admin emails for low-stock notifications   |
| request-service     | equipment-service        | Update equipment status on approve / return      |
| reports-service     | equipment-service        | Read inventory data for reports                  |
| reports-service     | request-service          | Read borrow history for reports                  |

### Authentication flow

1. The client sends `POST /auth/login` → API Gateway → auth-service.
2. auth-service validates credentials and returns a signed **JWT** (`HS256`, shared secret).
3. The client includes the JWT as `Authorization: Bearer <token>` on every subsequent request.
4. Each microservice has its own `JwtFilter` that validates the token using the shared secret and populates Spring Security's `SecurityContext` with the user's role.
5. Endpoint-level access control (`@PreAuthorize`) then enforces `USER` vs `ADMIN` permissions.

Internal endpoints (e.g. `/auth/internal/**`) are blocked at the gateway level and are only reachable from inside the Docker network.

---

## Backend — Local Development (IntelliJ)

> Only needed if you are editing backend code. For just running the app, use Docker.

**Requirements:** JDK 21. Spring Boot 3.x does not support Java 8.

```bash
java -version   # must show 21.x.x
```

**IntelliJ setup:**

1. Open the `Invtr` folder.
2. Right-click the `pom.xml` inside each service folder (`auth-service`, `equipment-service`, etc.).
3. Select **"Add as Maven Project"** — this triggers dependency download.

Database credentials for a direct connection (while Docker is running):

- Host: `localhost:5432`
- User: `user`
- Password: `password`

---

## Frontend — Local Development

> Not required if running via Docker.

**Requirements:** Node.js 16+.

```bash
cd frontend
npm install
npm run serve   # starts dev server on port 5173
```

The `vue.config.js` proxy routes all API calls to the gateway at `http://localhost:8080`, so the backend can run in Docker while the frontend runs locally.

```bash
npm run build   # production build → dist/
npm run lint    # ESLint across all .vue files
```

---

## Adding a New Microservice

1. In IntelliJ: right-click `backend` → **New → Module → Spring Boot**.
2. Group: `com.invtr`, Java 21, Maven, Jar packaging.
3. Add a `Dockerfile` (copy from an existing service).
4. Add the service to `docker-compose.yml` with the appropriate `depends_on` and network.
5. Add a route entry to `backend/api-gateway/src/main/resources/application.yml`.
6. If it needs a database, add `CREATE DATABASE <name>;` to `postgres-init/init-dbs.sql`.