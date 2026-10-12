# Blog Application

A server-rendered blogging platform built with Spring Boot and MySQL. Authors write posts in Markdown, organise them with categories and tags, and manage everything through an admin UI protected by Spring Security.

## Features

- Public home page with pagination, post detail pages, categories, tags, and search
- Markdown posts rendered to safe HTML
- Admin CRUD for posts and categories (drafts, publish/unpublish, preview)
- Login with Spring Security (ADMIN role, CSRF, bcrypt password hashes)
- First admin account created from local configuration when the users table is empty
- Schema managed by Liquibase; sample data for local development
- Tests with JUnit 5, Spring Boot test slices, and Testcontainers (MySQL)

## Tech stack

- Java 21, Spring Boot 4.1.1, Maven Wrapper
- Spring MVC, Thymeleaf, Spring Security
- Spring Data JPA (Hibernate), MySQL 8.4, Liquibase
- CommonMark
- Docker Compose for MySQL

## Prerequisites

- Java 21
- Docker Desktop (for MySQL and for running tests)
- Git (optional)

## Start the application

All commands below are run from the `blog` directory.

### 1. Start MySQL

```bash
docker compose up -d
```

Wait until the container is healthy (`docker compose ps`). The database credentials match `src/main/resources/application.properties` (`bloguser` / `blogpass`, database `blogdb`, port `3306`).

If another MySQL is already using port 3306, stop it first or change the published port in `compose.yaml`.

### 2. Configure the first admin (once)

Create `application-local.properties` next to `pom.xml` (this file is gitignored):

```properties
blog.admin.username=admin
blog.admin.password=choose-a-password-at-least-12-characters
```

On the first start with an empty `users` table, the app creates that admin and stores a bcrypt hash. Afterwards you can remove the password from the file; changing the file alone does not update an existing account.

### 3. Run the app

Windows:

```bash
.\mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
./mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). Admin pages are at [http://localhost:8080/admin/posts](http://localhost:8080/admin/posts) (login required).

Stop the app with `Ctrl+C`.

### 4. Stop MySQL (optional)

```bash
docker compose down
```

Data is kept in the `blog-mysql-data` volume. To remove the data as well: `docker compose down -v`.

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
├── compose.yaml                 # MySQL 8.4 for local development
├── application-local.properties # Local secrets (gitignored; you create this)
├── src/main/java/com/blog/      # Application code
├── src/main/resources/
│   ├── application.properties
│   ├── db/changelog/            # Liquibase migrations
│   ├── templates/               # Thymeleaf pages
│   └── static/css/
├── src/test/                    # Unit and integration tests
└── implementation-notes/        # Step-by-step build notes for this project
```

## Notes

- Liquibase uses the `dev` context in the default profile, so sample posts, categories, and tags are loaded for local development.
- Implementation notes that walk through how the app was built live in [`implementation-notes/`](./implementation-notes/).
