# Blog Application

A server-rendered blogging platform built with Spring Boot and MySQL. Authors write posts in Markdown, organise them with categories and tags, and manage everything through an admin UI protected by Spring Security.

## Features

- Public home page with pagination, post detail pages, categories, tags, and search
- Markdown posts rendered to safe HTML
- Admin CRUD for posts and categories (drafts, publish/unpublish, preview)
- Login with Spring Security (ADMIN role, CSRF, bcrypt password hashes)
- First admin account created from configuration when the users table is empty
- Schema managed by Liquibase; sample data for local development
- Production profile, multi-stage Docker image, and `/actuator/health` for probes
- Tests with JUnit 5, Spring Boot test slices, and Testcontainers (MySQL)

## Tech stack

- Java 21, Spring Boot 4.1.1, Maven Wrapper
- Spring MVC, Thymeleaf, Spring Security, Spring Boot Actuator
- Spring Data JPA (Hibernate), MySQL 8.4, Liquibase
- CommonMark
- Docker / Docker Compose

## Prerequisites

- Java 21 (for local Maven runs)
- Docker Desktop (for MySQL, full-stack Compose, and tests)
- Git (optional)

## Start the application (local Maven)

All commands below are run from the `blog` directory.

### 1. Start MySQL

```bash
docker compose up -d
```

This starts only the database (`mysql` service). Wait until it is healthy (`docker compose ps`). Default credentials match `src/main/resources/application.properties` (`bloguser` / `blogpass`, database `blogdb`, port `3306`).

If another MySQL already uses port 3306, stop it first or change the published port in `compose.yaml`.

### 2. Configure the first admin (once)

Create `application-local.properties` next to `pom.xml` (gitignored):

```properties
blog.admin.username=admin
blog.admin.password=choose-a-password-at-least-12-characters
```

On the first start with an empty `users` table, the app creates that admin and stores a bcrypt hash. Changing the file later does not update an existing account.

### 3. Run the app

Windows:

```bash
.\mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
./mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). Health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health). Admin: [http://localhost:8080/admin/posts](http://localhost:8080/admin/posts).

Stop with `Ctrl+C`.

### 4. Stop MySQL (optional)

```bash
docker compose down
```

Data stays in the `blog-mysql-data` volume. Remove data with `docker compose down -v`.

## Full stack with Docker

Build and run the app container together with MySQL (production-like: `prod` profile, no sample Liquibase context on a fresh database):

1. Copy `.env.example` to `.env` and set passwords (`BLOG_ADMIN_PASSWORD` must be at least 12 characters).
2. Start:

```bash
docker compose --profile app up --build
```

3. Open [http://localhost:8080](http://localhost:8080). When `blog-app` is healthy, `/actuator/health` returns `{"status":"UP"}`.

Stop the app container (keep MySQL):

```bash
docker compose --profile app stop app
```

Stop everything:

```bash
docker compose --profile app down
```

Do not commit `.env`. Secrets come from environment variables (`SPRING_DATASOURCE_*`, `BLOG_ADMIN_*`).

## Tests

Docker must be running (Testcontainers starts a throwaway MySQL):

```bash
.\mvnw.cmd verify
```

or

```bash
./mvnw verify
```

## Project layout

```
blog/
├── Dockerfile                   # Multi-stage build (JDK → JRE)
├── compose.yaml                 # MySQL always; app behind Compose profile "app"
├── .env.example                 # Template for Compose secrets
├── application-local.properties # Local secrets (gitignored; you create this)
├── src/main/java/com/blog/      # Application code
├── src/main/resources/
│   ├── application.properties
│   ├── application-prod.properties
│   ├── db/changelog/            # Liquibase migrations
│   ├── templates/               # Thymeleaf pages
│   └── static/css/
├── src/test/                    # Unit and integration tests
└── implementation-notes/        # Step-by-step build notes for this project
```

## Notes

- Default profile uses Liquibase context `dev` (sample posts). Profile `prod` sets an empty context so sample inserts do not run on a new database.
- Implementation notes: [`implementation-notes/`](./implementation-notes/).
