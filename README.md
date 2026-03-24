# Invtr
Repository for team The night king - AIBEST Tech Academy 

# Invtr - Backend Services

### Tech Stack
* **Framework:** Spring Boot 3.x
* **Build Tool:** Maven
* **Database:** PostgreSQL
* **Architecture:** Multi-module microservices (Parent POM with child services)

### Prerequisites (Read Before Anything Else)

**Java 21 is required.** Spring Boot 3.x does not support Java 8 or below. If you have an older Java version installed, Maven will fail with cryptic `dependencies.dependency.version is missing` errors even though the pom files are correct.

**Check your Java version:**
```
java -version
```
It must show `21.x.x`. If it shows `1.8.x` or anything below 17, stop and fix this first.

**Install JDK 21:**
* Download from: https://www.oracle.com/java/technologies/downloads/#java21-windows
* Or use Amazon Corretto 21: https://aws.amazon.com/corretto/
* IntelliJ can also download it for you — go to **File → Project Structure → SDK → Add SDK → Download JDK → select version 21**. IntelliJ stores it under `C:\Users\<you>\.jdks\`.

**Set JAVA_HOME after installing** (open PowerShell and run):
```powershell
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Users\<you>\.jdks\corretto-21.0.10", "User")
[System.Environment]::SetEnvironmentVariable("PATH", "C:\Users\<you>\.jdks\corretto-21.0.10\bin;" + [System.Environment]::GetEnvironmentVariable("PATH", "User"), "User")
```
Replace the path with wherever your JDK 21 is installed. Close and reopen your terminal after running this.

**Verify Maven is using Java 21:**
```
mvn -version
```
The output must show `Java version: 21`. If it still shows Java 8, your JAVA_HOME is not set correctly — do not proceed until this is fixed.

---

### Getting Started

**1. Database Setup**
* Ensure PostgreSQL is installed and running on your machine.
* Make sure to also change the password in the application.properties (it is in (microservice_name)/src/main/resources/application.properties
* Microservices Databases Setup: Because we use a database-per-service architecture, you must also create the separate databases in your PostgreSQL server - find in Discord:
* After creation, make the connections to the each of them
* Then run the rest of the code - table creation and more in the respective databases

**2. IntelliJ Setup (Crucial Step)**
Because this project uses a multi-module Maven structure, IntelliJ might not immediately recognize it as a Maven project when you first clone the repository.
* Open the `Invtr` folder in IntelliJ.
* Open the `backend` folder in your project tree.
* **Right-click the main `pom.xml` file inside the `backend` folder.**
* Select **"Add as Maven Project"** (or click the floating Maven 'Load Changes' icon).
* This will trigger IntelliJ to download all dependencies and link the microservices (like `auth-service`) properly.

**3. Building the Project**

Before running any service, build the entire project from the `backend` root folder. This installs the parent POM into your local Maven repository so the child services can inherit dependencies correctly.

Open a terminal in the `backend` folder and run:
```
mvn install -N -DskipTests
mvn clean install -DskipTests
```
The first command installs the parent POM. The second builds all services. Both must succeed before you try to run anything.

**4. Running All Services**

From the `backend` folder, run the Python script:
```
python run_services.py
```
This opens a separate terminal window for each service. Ports:
* `8080` — API Gateway
* `8081` — Auth Service
* `8082` — Equipment Service

Make sure you run this from the `backend` root folder, not from inside any individual service folder.

**5. How to add a microservices**
To correctly configure a new microservice please follow these steps
* While being in the backend folder right click on the folder -> New -> Module -> Spring Boot.
* Name it however you think is suitable for the service. Ex: auth-service.
* Language - java
* type - Maven
* Group: com.invtr
* Artifact: Match the name (e.g., auth-service)
* Package name: com.invtr.authservice
* JDK - 21
* Java - 21
* Packaging: Jar
* Dependencies: Select the required Spring dependencies (e.g., Spring Web, Spring Data JPA, PostgreSQL Driver).
* Crucial Linking Step: Once created, open the new service's pom.xml and change its <parent> tag to point to our custom backend parent POM instead of the default Spring Boot parent. Then, add the new module name to the <modules> list in the main backend/pom.xml.
* Also add the new service to the `SERVICES` list in `run_services.py`.

---

# Invtr - Frontend

### Tech Stack
* **Framework:** Vue 3 (Composition API with `<script setup>`)
* **Build Tool:** Vue CLI 5 (Webpack)
* **Routing:** Vue Router 4
* **HTTP:** Native `fetch` API
* **Port:** 5173

### Prerequisites (Read Before Anything Else)

**Node.js 16 or higher is required.** Vue CLI 5 will fail with older Node versions.

**Check your Node version:**
```
node -v
```
It must show `v16.x.x` or higher. If it shows something older, stop and fix this first.

**Install Node.js:**
* Download from: https://nodejs.org/en/download (choose LTS)
* Or use **nvm** (Node Version Manager) to manage multiple versions

**Verify npm is available:**
```
npm -v
```

---

### Getting Started

**1. Install Dependencies**

Open a terminal in the `frontend` folder and run:
```
npm install
```
This installs all packages listed in `package.json` into a local `node_modules` folder. Run this once after cloning, and again whenever `package.json` changes.

**2. Make Sure the Backend is Running**

The frontend proxies all API calls through the dev server to `http://localhost:8080` (the API Gateway). If the backend is not running, login will fail and all data will be empty.

Start the backend first — see the backend README for instructions. Ports expected:
* `8080` — API Gateway
* `8081` — Auth Service
* `8082` — Equipment Service
* `8083` — Request Service *(not yet implemented)*

**3. Run the Dev Server**
```
npm run serve
```
The app will be available at: **http://localhost:5173**

---

**4. How to Add a New View**

To correctly add a new page or section to the frontend, follow these steps:
* Create your component in `src/views/` (full pages) or `src/components/` (reusable pieces).
* Open `src/router/index.js` and import your new component.
* Add a new route object to the `routes` array. Example:
```js
{ path: '/my-page', name: 'MyPage', component: MyPage, meta: { requiresAuth: true, role: 'USER' } }
```
* Use `role: 'USER'` or `role: 'ADMIN'` in `meta` to protect the route — the auth guard handles redirection automatically.

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
├── vue.config.js               # Dev server proxy config
└── package.json
```

---

### Routing & Auth Guard

Routes are defined in `src/router/index.js`. The router guard checks for a JWT token in `localStorage` or `sessionStorage` before allowing access to protected routes. After login, the JWT payload is decoded client-side to read the `role` claim and the user is redirected to their respective dashboard automatically.

| Path | Component | Access |
|------|-----------|--------|
| `/` | → redirects to `/login` | Public |
| `/login` | `Login.vue` | Public |
| `/dashboard` | `UserDashboard.vue` | USER only |
| `/admin-dashboard` | `AdminDashboard.vue` | ADMIN only |

---

### API Integration

All `fetch` calls target `http://localhost:8080` (the API Gateway). The dev server proxy in `vue.config.js` forwards requests automatically so you do not need to worry about CORS during development.

| Endpoint | Service | Used in |
|----------|---------|---------|
| `POST /auth/login` | Auth Service | `Login.vue` |
| `POST /auth/register` | Auth Service | `Login.vue` |
| `GET /auth/me` | Auth Service | `UserDashboard.vue` |
| `GET /equipment` | Equipment Service | `UserDashboard.vue` |
| `POST /request` | Request Service | `UserDashboard.vue` |
| `GET /requests/my` | Request Service | `UserDashboard.vue` |
| `GET /requests/history` | Request Service | `UserDashboard.vue` |

**Note:** The Request Service (port 8083) is not yet implemented. The frontend handles this gracefully — the Requests and History sections show an empty state with a small offline warning until the service is ready.

---

### Authentication Flow

1. User submits email + password on `/login`
2. Frontend calls `POST /auth/login` → receives `{ token: "..." }`
3. Token is stored in `localStorage` (if Remember Me is checked) or `sessionStorage`
4. All subsequent API calls include `Authorization: Bearer <token>` in the request header
5. On logout, the token is removed and the user is redirected to `/login`

---

### Available Scripts

From the `frontend` folder:
* `npm run serve` — Start the dev server on port 5173
* `npm run build` — Build for production (outputs to `dist/`)
* `npm run lint` — Run ESLint across all `.vue` files
