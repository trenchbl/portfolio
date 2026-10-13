# Software Developer Portfolio

A collection of projects demonstrating backend development expertise and software engineering skills.

## About

This repository serves as a portfolio showcasing practical software development projects. The focus is on backend development with emphasis on clean architecture, database design, and modern development practices.

## Projects

### [Blog Application](./blog)

A server-rendered blogging platform with Markdown posts, categories and tags, search, and an admin UI protected by Spring Security.

**Key features:**
- Public post list, detail pages, categories, tags, and search
- Admin CRUD for posts and categories (drafts and publishing)
- Spring Security login (ADMIN role, CSRF, bcrypt)
- Liquibase migrations, Actuator health, Docker image and Compose full stack

**Technologies:**
- Java 21, Spring Boot 4.1.1, Maven
- MySQL 8.4, Liquibase, Spring Data JPA
- Thymeleaf, Spring Security, Actuator, CommonMark
- Docker Compose, Testcontainers

[Setup and run →](./blog/README.md)

## Getting Started

Each project contains its own README with detailed setup instructions, prerequisites, and documentation.

To explore a project:
1. Navigate to the project directory
2. Follow the setup instructions in the project's README
3. Ensure all prerequisites are installed

## Repository Structure

```
portfolio/
├── blog/           # Spring Boot blogging application
├── LICENSE         # MIT License
└── README.md       # This file
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

<!-- ## Contact

GitHub: [@trenchbl](https://github.com/trenchbl) -->

