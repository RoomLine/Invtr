# Invtr

> Repository for team **The Night King** — AIBEST Tech Academy. Ask Yavor to setup .env for Gemini API and mailing. Current API keys are outdated

---

## 🚀 Quick Start (The Docker Way)

**You no longer need to manually install databases, Java, or Node.js to run this project!**

1. Install **[Docker Desktop](https://www.docker.com/products/docker-desktop/)**.
2. Open a terminal in the root `Invtr` folder.
3. Run the following command:

```bash
docker-compose up --build
```

Wait for the green logs to settle, then open your browser to **http://localhost:5173**.

> **Note:** The database, backend APIs, and frontend are all automatically built, linked, and started by Docker.

---

## Backend Services

### Tech Stack

| Tool | Details |
|------|---------|
| Framework | Spring Boot 3.x |
| Build Tool | Maven |
| Database | PostgreSQL (Containerized) |
| Architecture | Multi-module microservices (Dockerized) |
| Migrations | Flyway |

### Local IDE Prerequisites (For Editing Code)

> If you are just running the app, use Docker. If you are writing code in IntelliJ, **Java 21 is required**. Spring Boot 3.x does not support Java 8 or below.

Check your Java version:

```bash
java -version
```

It must show `21.x.x`.

**Install JDK 21:**

- Download from: https://www.oracle.com/java/technologies/downloads/#java21-windows
- Or use Amazon Corretto 21: https://aws.amazon.com/corretto/
- IntelliJ can also download it — go to **File → Project Structure → SDK → Add SDK → Download JDK → select version 21**. IntelliJ stores it under `C:\Users\<you>\.jdks\`.

**Set `JAVA_HOME` after installing** (open PowerShell and run):

```powershell
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Users\<you>\.jdks\corretto-21.0.10", "User")
[System.Environment]::SetEnvironmentVariable("PATH", "C:\Users\<you>\.jdks\corretto-21.0.10\bin;" + [System.Environment]::GetEnvironmentVariable("PATH", "User"), "User")
```

> Replace the path with wherever your JDK 21 is installed. Close and reopen your terminal after running this.

---

### Getting Started

#### 1. Database Setup

**Handled Automatically:** You do not need to install PostgreSQL or create databases manually. Docker boots up a Postgres container (`invtr-db`) and Flyway automatically creates the tables on startup.

To access the database manually, use IntelliJ's Database tool to connect to:

- **Host:** `localhost:5432`
- **User:** `user`
- **Password:** `password`

#### 2. IntelliJ Setup (Crucial Step)

Because this project uses a multi-module Maven structure, IntelliJ might not immediately recognize it as a Maven project when you first clone the repository.

1. Open the `Invtr` folder in IntelliJ.
2. Open the `backend` folder in your project tree.
3. Right-click the `pom.xml` files inside the individual service folders (`auth-service`, `equipment-service`, etc.).
4. Select **"Add as Maven Project"** (or click the floating Maven "Load Changes" icon).

This will trigger IntelliJ to download all dependencies.

#### 3. Running All Services

> **Note:** The old `python run_services.py` script is deprecated.

From the root `Invtr` folder, run:

```bash
docker-compose up
```

**Ports exposed to your local machine:**

| Port | Service |
|------|---------|
| `8080` | API Gateway |
| `5432` | PostgreSQL Database |
| `5173` | Vue Frontend |

> Internal microservices communicate dynamically inside the Docker network on ports `8081`, `8082`, etc.

#### 4. How to Add a Microservice

To correctly configure a new microservice, follow these steps:

1. **Right-click** on the `backend` folder → **New → Module → Spring Boot**.
2. Name it appropriately (e.g., `new-service`).
3. Set the following options:
   - **Language:** Java
   - **Type:** Maven
   - **Group:** `com.invtr`
   - **JDK / Java:** 21
   - **Packaging:** Jar
   - **Dependencies:** Spring Web, Spring Data JPA, PostgreSQL Driver, Flyway
4. **Crucial Linking Step:** Ensure your new service's `pom.xml` uses the standard Spring Boot parent. Remove the `flyway-database-postgresql` dependency if it generates one.
5. Add the new service to `docker-compose.yml` so it boots up with the rest of the application.

---

## Frontend

### Tech Stack

| Tool | Details |
|------|---------|
| Framework | Vue 3 (Composition API with `<script setup>`) |
| Build Tool | Vue CLI 5 (Webpack) |
| Routing | Vue Router 4 |
| HTTP | Native `fetch` API |
| Port | `5173` |

### Local IDE Prerequisites (For Editing Code)

> Not required if just running via Docker.

**Node.js 16 or higher is required.** Vue CLI 5 will fail with older Node versions.

Check your Node version:

```bash
node -v
```

It must show `v16.x.x` or higher.

---

### Getting Started

#### 1. Run via Docker (Recommended)

Simply run `docker-compose up` from the root folder. The frontend will compile and hot-reload automatically at **http://localhost:5173**.

#### 2. Run Locally (Without Docker)

If you want to run the frontend outside of Docker while the backend runs inside Docker:

1. Open a terminal in the `frontend` folder.
2. Run `npm install`
3. Run `npm run serve`

The `vue.config.js` proxy will automatically route `/auth` and `/equipment` requests to the Dockerized API Gateway at `http://localhost:8080`.

#### 3. How to Add a New View

1. Create your component in `src/views/` (full pages) or `src/components/` (reusable pieces).
2. Open `src/router/index.js` and import your new component.
3. Add a new route object to the `routes` array:

```javascript
{
  path: '/my-page',
  name: 'MyPage',
  component: MyPage,
  meta: { requiresAuth: true, role: 'USER' }
}
```

> Use `role: 'USER'` or `role: 'ADMIN'` in `meta` to protect the route — the auth guard handles redirection automatically.

---

### Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── assets/
│   │   ├── dashboard.css       # Styles for user + admin dashboards
│   │   ├── login.css           # Styles for the login/register page
│   │   └── logo-png.jpg        # App logo
│   ├── components/
│   │   └── admin/
│   │       ├── AdminSidebar.vue
│   │       ├── AdminTopbar.vue
│   │       ├── DashboardView.vue
│   │       ├── InventoryView.vue
│   │       ├── RequestsView.vue
│   │       ├── UsersView.vue
│   │       ├── SettingsView.vue
│   │       ├── AddItemModal.vue
│   │       └── AddUserModal.vue
│   ├── router/
│   │   └── index.js            # Route definitions + auth guard
│   ├── views/
│   │   ├── Login.vue           # Login + Register page
│   │   ├── UserDashboard.vue   # User-facing dashboard
│   │   └── AdminDashboard.vue  # Admin dashboard (shell + view switcher)
│   ├── App.vue
│   └── main.js
├── vue.config.js               # Dev server proxy config (Docker + Local)
└── package.json
```

---

### Routing & Auth Guard

Routes are defined in `src/router/index.js`. The router guard checks for a JWT token in `localStorage` or `sessionStorage` before allowing access to protected routes. After login, the JWT payload is decoded client-side to read the `role` claim, and the user is redirected to their respective dashboard automatically.

| Path | Component | Access |
|------|-----------|--------|
| `/` | → redirects to `/login` | Public |
| `/login` | `Login.vue` | Public |
| `/dashboard` | `UserDashboard.vue` | USER only |
| `/admin-dashboard` | `AdminDashboard.vue` | ADMIN only |

---

### API Integration

All `fetch` calls target the API Gateway (`http://localhost:8080`). The dev server proxy in `vue.config.js` forwards requests automatically (using the `VUE_APP_API_TARGET` environment variable inside Docker).

| Endpoint | Service | Used In |
|----------|---------|---------|
| `POST /auth/login` | Auth Service | `Login.vue` |
| `POST /auth/register` | Auth Service | `Login.vue` |
| `GET /auth/me` | Auth Service | `UserDashboard.vue` |
| `GET /equipment` | Equipment Service | `UserDashboard.vue` |
| `POST /request` | Request Service | `UserDashboard.vue` |
| `GET /requests/my` | Request Service | `UserDashboard.vue` |
| `GET /requests/history` | Request Service | `UserDashboard.vue` |

> **Note:** The Request Service is a work in progress. The frontend handles this gracefully — the Requests and History sections show an empty state until the endpoints are fully implemented.

---

### Available Scripts

From the `frontend` folder (if running locally):

```bash
npm run serve   # Start the dev server on port 5173
npm run build   # Build for production (outputs to dist/)
npm run lint    # Run ESLint across all .vue files
```
