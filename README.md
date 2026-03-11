# Invtr
Repository for team The night king - AIBEST Tech Academy 

# Invtr - Backend Services

### Tech Stack
* **Framework:** Spring Boot 3.x
* **Build Tool:** Maven
* **Database:** PostgreSQL
* **Architecture:** Multi-module microservices (Parent POM with child services)

### Getting Started

**1. Database Setup**
* Ensure PostgreSQL is installed and running on your machine.
* Create a new local database named `invtr_db`.

**2. IntelliJ Setup (Crucial Step)**
Because this project uses a multi-module Maven structure, IntelliJ might not immediately recognize it as a Maven project when you first clone the repository.
* Open the `Invtr` folder in IntelliJ.
* Open the `backend` folder in your project tree.
* **Right-click the main `pom.xml` file inside the `backend` folder.**
* Select **"Add as Maven Project"** (or click the floating Maven 'Load Changes' icon).
* This will trigger IntelliJ to download all dependencies and link the microservices (like `auth-service`) properly.

**3. How to add a microservices**
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


# Invtr - Frontend

