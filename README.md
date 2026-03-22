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
