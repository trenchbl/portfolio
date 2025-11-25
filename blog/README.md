# Blog Application

A Spring Boot-based blogging website with MySQL database integration and Liquibase for database versioning. This version features a modern, responsive multi-page blog with dynamic content from a database, navigation bar, contact form, and beautiful UI design.

## Project Description

This is a blogging website application built using Spring Boot, Java, Spring Data JPA, and Thymeleaf. The application features a complete multi-page blog with database-driven content, a fixed navigation bar, multiple pages (Home, About, Contact, Categories, Archive), and a modern, responsive design. Blog posts are stored in a MySQL database and managed using Liquibase for version-controlled schema migrations. The site includes a homepage displaying all blog posts from the database, an about page with author information, a fully functional contact form with social media links, and placeholder pages for future features. The design features a beautiful purple gradient background, smooth animations, and a mobile-responsive hamburger menu.

## Technologies Used

### Backend
- **Java 17+** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Boot Starter Web** - Web application support
- **Spring Boot Starter Thymeleaf** - Template engine for server-side HTML rendering
- **Spring Data JPA** - Database access and ORM
- **Hibernate** - JPA implementation for object-relational mapping
- **MySQL 8.0** - Relational database
- **Liquibase** - Database schema version control and migrations
- **Maven** - Build tool and dependency management

### Frontend
- **Thymeleaf** - Server-side template engine for dynamic HTML
- **HTML5** - Semantic markup
- **CSS3** - Modern styling with gradients, shadows, and responsive design
- **JavaScript** - Client-side interactivity

### Infrastructure
- **Docker** - Container platform for running MySQL and Blog application
- **Docker Compose** - Multi-container orchestration for complete stack deployment
- **Multi-stage Docker Build** - Optimized container images for production

## Prerequisites

Before running this project, ensure you have the following installed:

- **Java Development Kit (JDK) 17 or newer**
  - Check if installed: `java -version`
  - If not installed, you can install it using:
    - Windows: `winget install Microsoft.OpenJDK.21`
    - Or download from [Microsoft OpenJDK](https://learn.microsoft.com/java/openjdk/download) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)

- **Apache Maven 3.6+**
  - Check if installed: `mvn -version`
  - If not installed, download from [Apache Maven](https://maven.apache.org/download.cgi)
  - Or install using a package manager:
    - Windows: Download and install manually, or use Chocolatey: `choco install maven`
      - Install Scoop (for Windows)
        ```
         \blog> Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
         \blog> Invoke-RestMethod -Uri https://get.scoop.sh | Invoke-Expression
        ```

      - Install Maven using scoop
        ```         
        scoop install main/maven
        ```

- **Docker Desktop**
  - Required for running MySQL database container
  - Download from: [Docker Desktop](https://www.docker.com/products/docker-desktop)
  - Verify installation: `docker --version`
         

## Project Structure

```
blog/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── blog/
│   │   │           ├── BlogApplication.java          # Main Spring Boot application class
│   │   │           ├── controller/
│   │   │           │   └── HomeController.java       # Controller with all page routes
│   │   │           ├── model/
│   │   │           │   └── BlogPost.java             # JPA entity for blog posts
│   │   │           └── repository/
│   │   │               └── BlogPostRepository.java   # Spring Data JPA repository
│   │   └── resources/
│   │       ├── application.properties                # Application configuration & DB settings
│   │       ├── db/
│   │       │   └── changelog/
│   │       │       ├── db.changelog-master.xml       # Liquibase master changelog
│   │       │       └── changes/
│   │       │           └── v1.0/
│   │       │               ├── 001-create-blog-posts-table.xml
│   │       │               └── 002-insert-sample-blog-posts.xml
│   │       └── templates/
│   │           ├── index.html                        # Home page with blog posts from DB
│   │           ├── about.html                        # About page
│   │           ├── contact.html                      # Contact page with form
│   │           ├── categories.html                   # Categories page (Under Construction)
│   │           └── archive.html                      # Archive page (Under Construction)
├── Dockerfile                                        # Multi-stage Docker build configuration
├── .dockerignore                                     # Docker build exclusions
├── docker-compose.yml                                # Docker Compose for complete stack (MySQL + App)
├── pom.xml                                           # Maven configuration with dependencies
└── README.md                                         # This file
```

## How to Run the Project

### Method 1: Using Docker Compose (Recommended for Production & Easy Setup)

This is the easiest and most reliable method. Docker Compose will automatically build the application, start MySQL, and connect everything together.

1. **Ensure Docker Desktop is running**
   - Verify with: `docker --version`

2. **Build and start all services** (MySQL + Blog Application):
   ```bash
   docker-compose up --build
   ```
   
   Or run in detached mode (background):
   ```bash
   docker-compose up --build -d
   ```

3. **Wait for services to start.** You should see:
   - MySQL container starting and becoming healthy
   - Blog application building from source
   - Liquibase migrations executing
   - "Started BlogApplication" message

4. **Open your web browser** and navigate to:
   ```
   http://localhost:8080
   ```

5. **You should see** the blog home page with:
   - Fixed navigation bar at the top with links to: Home, About, Categories, Archive, Contact
   - Responsive hamburger menu on mobile devices
   - Purple gradient background (#667eea to #764ba2)
   - Three blog posts from the database
   - Author (John Doe) and creation date for each post
   - Smooth animations and hover effects

6. **View application logs** (if running in detached mode):
   ```bash
   docker-compose logs -f blog-app
   ```

7. **Stop all services:**
   ```bash
   docker-compose down
   ```
   
   **Stop and remove volumes** (⚠️ WARNING: This deletes all data):
   ```bash
   docker-compose down -v
   ```

### Method 2: Using Maven (For Development)

1. **Start the MySQL database** (required before running the application):
   ```bash
   docker-compose up -d
   ```
   
   Wait until MySQL is ready (check with `docker-compose logs -f mysql` and look for "ready for connections")

2. **Open a terminal/command prompt** in the project root directory (`blog/`)

3. **Build and run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Wait for the application to start.** You should see:
   - Liquibase migration logs (creating tables, inserting sample data)
   - "Liquibase Update Successful"
   - "Started BlogApplication in X.XXX seconds"

5. **Open your web browser** and navigate to:
   ```
   http://localhost:8080
   ```

6. **You should see** the blog home page with:
   - Fixed navigation bar at the top with links to: Home, About, Categories, Archive, Contact
   - Responsive hamburger menu on mobile devices
   - Purple gradient background (#667eea to #764ba2)
   - Three blog posts from the database:
     - "Welcome to My Blog"
     - "Getting Started with Spring Boot"
     - "Database Versioning with Liquibase"
   - Author (John Doe) and creation date for each post
   - Smooth animations and hover effects
   
7. **Explore the site:**
   - Click on **About** to see author information and background
   - Click on **Contact** to access the contact form with social media links
   - **Categories** and **Archive** are to be developed. For the time being they show an "Under Construction" message.
   - Try resizing your browser to see the responsive navigation menu

### Method 3: Using an IDE (For Development)

1. **Start the MySQL database first:**
   ```bash
   docker-compose up -d
   ```

2. **Import the project** into your IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

3. **Ensure Maven dependencies are downloaded:**
   - Most IDEs will do this automatically
   - Or run: `mvn clean install`

4. **Run the `BlogApplication.java` file:**
   - Right-click on `BlogApplication.java`
   - Select "Run" or "Run 'BlogApplication'"

5. **Watch the console for:**
   - Liquibase migration messages
   - "Liquibase Update Successful"
   - "Started BlogApplication in X.XXX seconds"

6. **Open your web browser** and navigate to:
   ```
   http://localhost:8080
   ```

7. **You should see** the blog home page with:
   - Fixed navigation bar at the top with links to: Home, About, Categories, Archive, Contact
   - Responsive hamburger menu on mobile devices
   - Purple gradient background (#667eea to #764ba2)
   - Three blog posts from the database (newest first)
   - Author and date metadata for each post
   - Smooth animations and hover effects
   
8. **Explore the site:**
   - Click on **About** to see author information and background
   - Click on **Contact** to access the contact form with social media links
   - Click on **Categories** or **Archive** to see "Under Construction" pages
   - Try resizing your browser to see the responsive navigation menu

### Stopping the Application

- **In terminal:** Press `Ctrl + C` to stop the application
- **In IDE:** Click the stop button in the console/run window

## Configuration

The application is configured via `src/main/resources/application.properties`:

### Server Configuration
- **Port:** 8080 (default)
- **Application Name:** blog

### Database Configuration
- **URL:** `jdbc:mysql://localhost:3306/blogdb`
- **Username:** `bloguser`
- **Password:** `blogpass`
- **Driver:** MySQL Connector/J

### JPA/Hibernate Configuration
- **DDL Auto:** `none` (schema managed by Liquibase)
- **Show SQL:** `true` (logs SQL queries for debugging)
- **Dialect:** MySQL8Dialect
- **Format SQL:** `true` (pretty-prints SQL in logs)

### Liquibase Configuration
- **Enabled:** `true`
- **Change Log:** `classpath:db/changelog/db.changelog-master.xml`
- **Drop First:** `false` (preserves data on restart)

You can modify these settings in the `application.properties` file if needed.

## Docker Deployment

This project is fully containerized using Docker and Docker Compose for easy deployment and scalability.

### Docker Compose Stack

The `docker-compose.yml` file defines a complete application stack with:

1. **MySQL Database Service** (`mysql`)
   - Image: `mysql:8.0`
   - Container name: `blog-mysql`
   - Port: `3306` (mapped to host)
   - Data persistence via Docker volume
   - Health checks to ensure database is ready
   - Auto-restart policy

2. **Blog Application Service** (`blog-app`)
   - Built from source using multi-stage Dockerfile
   - Container name: `blog-app`
   - Port: `8080` (mapped to host)
   - Depends on MySQL (waits for health check)
   - Environment variables for database connection
   - Auto-restart policy

3. **Docker Network** (`blog-network`)
   - Private bridge network for service communication
   - Allows containers to communicate by service name
   - Isolated from host network for security

### Prerequisites
- Docker Desktop installed and running
- Download from: [Docker Desktop](https://www.docker.com/products/docker-desktop)

### Quick Start with Docker Compose

**Start the entire stack:**
```bash
docker-compose up --build
```

**Run in background (detached mode):**
```bash
docker-compose up --build -d
```

**View logs:**
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f blog-app
docker-compose logs -f mysql
```

**Check service status:**
```bash
docker-compose ps
```

**Stop all services:**
```bash
docker-compose down
```

**Stop and remove all data:**
```bash
docker-compose down -v
```

**Restart services:**
```bash
docker-compose restart
```

**Rebuild after code changes:**
```bash
docker-compose up --build
```

### Database Configuration

The MySQL container is configured with:
- **Database Name:** `blogdb`
- **Username:** `bloguser`
- **Password:** `blogpass`
- **Root Password:** `rootpassword`
- **Port:** `3306` (mapped to host)
- **Data Persistence:** Stored in `blog-mysql-data` volume

### Connecting to MySQL

**From the host machine:**
```bash
docker exec -it blog-mysql mysql -u bloguser -p
```
Enter password: `blogpass`

**Using MySQL Workbench or other GUI tools:**
- **Hostname:** `127.0.0.1` or `localhost`
- **Port:** `3306`
- **Username:** `bloguser`
- **Password:** `blogpass`
- **Database:** `blogdb`

**From within the blog-app container:**
- **Hostname:** `mysql` (service name in Docker network)
- **Port:** `3306`
- **Username:** `bloguser`
- **Password:** `blogpass`
- **Database:** `blogdb`

### Docker Environment Variables

The blog application container accepts the following environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | JDBC connection URL | `jdbc:mysql://mysql:3306/blogdb...` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `bloguser` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `blogpass` |
| `SERVER_PORT` | Application port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profiles | (none) |

These can be modified in `docker-compose.yml` or overridden at runtime.

### Production Deployment

For production deployment, consider:

1. **Use environment-specific configurations:**
   ```bash
   docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
   ```

2. **Set secure passwords:**
   - Never use default passwords in production
   - Use Docker secrets or environment variables from secure sources

3. **Enable SSL/TLS:**
   - Configure MySQL with SSL certificates
   - Use HTTPS for the application (reverse proxy like Nginx)

4. **Resource limits:**
   - Set memory and CPU limits in docker-compose.yml
   - Monitor container resource usage

5. **Logging and monitoring:**
   - Configure centralized logging (ELK stack, Splunk, etc.)
   - Use monitoring tools (Prometheus, Grafana, etc.)

6. **Backup strategy:**
   - Regular database backups
   - Backup Docker volumes
   - Test restore procedures


## Database Schema & Liquibase

### Database Tables

The application uses the following database tables:

**blog_posts** - Stores all blog post content
- `id` - BIGINT, Primary Key, Auto-increment
- `title` - VARCHAR(255), NOT NULL
- `author` - VARCHAR(100), NOT NULL
- `content` - TEXT, NOT NULL
- `created_at` - TIMESTAMP, NOT NULL (auto-set on insert)
- `updated_at` - TIMESTAMP, NOT NULL (auto-updated on modification)

**databasechangelog** - Liquibase tracking table (auto-created)
- Tracks all executed database migrations

**databasechangeloglock** - Liquibase locking table (auto-created)
- Prevents concurrent migration execution

### Liquibase Database Versioning

This project uses **Liquibase** for database schema version control. All database changes are tracked in XML changelog files:

**Master Changelog:** `src/main/resources/db/changelog/db.changelog-master.xml`

**Version 1.0 Changes:**
- `001-create-blog-posts-table.xml` - Creates the blog_posts table
- `002-insert-sample-blog-posts.xml` - Inserts 3 sample blog posts

### How Liquibase Works

1. On application startup, Liquibase checks the `databasechangelog` table
2. Compares executed changesets with changelog files
3. Runs any new changesets that haven't been executed yet
4. Records execution in the tracking tables
5. Ensures changesets are **never** re-executed

### Viewing Database Content

**Show all tables:**
```bash
docker exec blog-mysql mysql -u bloguser -pblogpass blogdb -e "SHOW TABLES;"
```

**View blog posts:**
```bash
docker exec blog-mysql mysql -u bloguser -pblogpass blogdb -e "SELECT id, title, author, created_at FROM blog_posts;"
```

**View Liquibase history:**
```bash
docker exec blog-mysql mysql -u bloguser -pblogpass blogdb -e "SELECT id, author, filename, dateexecuted FROM databasechangelog;"
```

**Connect interactively:**
```bash
docker exec -it blog-mysql mysql -u bloguser -pblogpass blogdb
```

### Adding New Database Changes

When you need to modify the database schema:

1. Create a new changelog file in `src/main/resources/db/changelog/changes/v1.0/`
2. Use sequential numbering (e.g., `003-add-category-column.xml`)
3. Add the changelog to `db.changelog-master.xml`
4. Restart the application - Liquibase will apply changes automatically

**Example:** Adding a category column
```xml
<changeSet id="003-add-category-column" author="developer">
    <addColumn tableName="blog_posts">
        <column name="category" type="VARCHAR(50)" defaultValue="General">
            <constraints nullable="false"/>
        </column>
    </addColumn>
    <rollback>
        <dropColumn tableName="blog_posts" columnName="category"/>
    </rollback>
</changeSet>
```

**Important Rules:**
- Never modify existing changesets after deployment
- Always create new changesets for changes
- Include rollback sections when possible
- Test on development database first

For more detailed information, see `005-step-by-step-blog-post-db-table.txt`.

## Building the Project

### Building with Maven

To build the project without running it:

```bash
mvn clean package
```

This will create a JAR file in the `target/` directory that can be run independently:

```bash
java -jar target/blog-1.0.0.jar
```

### Building Docker Image

The application includes a multi-stage Dockerfile that builds the application from source and creates an optimized production-ready container.

**Build the Docker image:**
```bash
docker build -t blog-app:latest .
```

**Run the container** (requires MySQL running separately):
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/blogdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true \
  -e SPRING_DATASOURCE_USERNAME=bloguser \
  -e SPRING_DATASOURCE_PASSWORD=blogpass \
  blog-app:latest
```

**View running containers:**
```bash
docker ps
```

**View container logs:**
```bash
docker logs -f blog-app
```

**Stop the container:**
```bash
docker stop blog-app
```

### Docker Image Details

The Dockerfile uses a multi-stage build approach for optimal performance:

**Stage 1: Build Stage**
- Uses `maven:3.9-eclipse-temurin-17` image
- Downloads dependencies (cached for faster rebuilds)
- Compiles the application and creates JAR file

**Stage 2: Runtime Stage**
- Uses lightweight `eclipse-temurin:17-jre-alpine` image
- Only includes the JRE (not full JDK) for smaller image size
- Runs as non-root user for security
- Includes health checks for container orchestration
- Final image size: ~200-250MB (vs ~700MB+ with full JDK)

**Security Features:**
- ✅ Non-root user execution
- ✅ Minimal base image (Alpine Linux)
- ✅ No unnecessary build tools in production image
- ✅ Multi-stage build separates build and runtime environments

## Troubleshooting

### Java Version Issues
- Ensure you have JDK 17 or newer installed
- Verify with: `java -version`
- Set `JAVA_HOME` environment variable if needed

### Maven Issues
- Ensure Maven is installed and in your PATH
- Verify with: `mvn -version`
- If dependencies fail to download, check your internet connection
- Run `mvn clean install` to refresh dependencies

### Database Connection Issues
**Problem:** Application fails to start with database connection error

**Solutions:**
- Verify MySQL container is running: `docker-compose ps`
- Check MySQL logs: `docker-compose logs mysql`
- Ensure port 3306 is not blocked by firewall
- Verify credentials in `application.properties` match `docker-compose.yml`

**Problem:** "Communications link failure" error

**Solutions:**
- Wait longer for MySQL to fully start (check logs for "ready for connections")
- Restart MySQL container: `docker-compose restart`
- Check if another service is using port 3306: `netstat -an | findstr 3306`

### Liquibase Issues
**Problem:** Liquibase checksum validation failed

**Solution:**
- This happens if you modify an already-executed changeset
- Create a new changeset instead of modifying existing ones
- Or clear checksums (development only): `mvn liquibase:clearCheckSums`

**Problem:** Table already exists error

**Solution:**
- Drop and recreate database: `docker-compose down -v && docker-compose up -d`
- Or manually drop tables via MySQL client

### Port Already in Use
- If port 8080 is already in use, change it in `application.properties`:
  ```
  server.port=8081
  ```
- If port 3306 is in use, modify `docker-compose.yml`:
  ```yaml
  ports:
    - "3307:3306"
  ```
  Then update `application.properties` database URL to use port 3307

### Docker Issues
**Problem:** Docker container won't start

**Solutions:**
- Ensure Docker Desktop is running
- Check Docker logs: `docker logs blog-mysql` or `docker logs blog-app`
- Remove and recreate container: `docker-compose down && docker-compose up --build`
- Check for port conflicts: `netstat -an | findstr "8080 3306"`

**Problem:** Permission denied accessing Docker

**Solution:**
- Ensure Docker Desktop is running with proper permissions
- On Windows, run command prompt/PowerShell as Administrator if needed

**Problem:** Blog application can't connect to MySQL in Docker

**Solutions:**
- Verify MySQL is healthy: `docker-compose ps`
- Check MySQL logs: `docker-compose logs mysql`
- Ensure services are on the same network: `docker network ls`
- Verify environment variables in docker-compose.yml
- Wait longer for MySQL initialization (especially first run)

**Problem:** Docker build fails

**Solutions:**
- Ensure you have enough disk space
- Clear Docker build cache: `docker builder prune`
- Check for network issues during dependency download
- Verify JDK 17 is specified correctly in Dockerfile

**Problem:** Changes not reflected after rebuild

**Solution:**
- Force rebuild without cache: `docker-compose build --no-cache blog-app`
- Remove old containers and volumes: `docker-compose down -v && docker-compose up --build`

## Features

### Current Features (v4.0.0)
- **Docker Containerization:** 
  - Multi-stage Dockerfile for optimized builds
  - Docker Compose for complete stack deployment
  - Automated container orchestration
  - Health checks and dependency management
  - Production-ready container images
  - Non-root user execution for security
  - Isolated Docker network for services
- **Database Integration:** 
  - MySQL database for persistent storage
  - Spring Data JPA for data access
  - Dynamic blog post loading from database
  - Three sample blog posts pre-loaded
  - Persistent data storage with Docker volumes
- **Database Versioning:**
  - Liquibase for schema version control
  - Automated database migrations
  - Rollback support for all changes
  - Change tracking and audit trail
- **Multi-page Navigation:** Complete navigation bar with 5 pages
- **Fixed Navigation Bar:** Stays at top while scrolling with active page indicator
- **Responsive Mobile Menu:** Hamburger menu with smooth slide-in animation
- **Home Page:** 
  - Dynamic blog post display from database
  - Posts ordered by creation date (newest first)
  - Modern card design with hover effects
  - Automatic date formatting
- **About Page:** Author information with background, skills, and interests
- **Contact Page:** 
  - Fully functional contact form with validation
  - Social media links (GitHub, LinkedIn, Twitter, Email)
  - Success message animation
  - Form fields: Name, Email, Subject, Message
- **Categories Page:** "Under Construction" placeholder with animated icon
- **Archive Page:** "Under Construction" placeholder with animated icon
- **Data Model:**
  - BlogPost JPA entity
  - Repository pattern with Spring Data JPA
  - Automatic timestamp management
- Modern, responsive UI with Thymeleaf server-side rendering
- Beautiful gradient background design
- Smooth animations and hover effects throughout
- Mobile-responsive design (320px+)
- Semantic HTML5 structure
- Clean, readable typography
- Consistent design across all pages

### UI Design Details
- **Navigation Bar:**
  - Fixed position with semi-transparent white background
  - Backdrop blur effect for modern look
  - Active page highlighting with gradient underline
  - Smooth hover animations
  - Mobile hamburger menu (≤768px)
- **Layout:** Centered with max-width of 800px (700px for contact form)
- **Spacing:** Consistent padding across all pages
- **Background:** Purple gradient (from #667eea to #764ba2)
- **Card Design:** White background with rounded corners and shadow effects
- **Typography:** System fonts with excellent readability (line-height: 1.8)
- **Interactive Elements:**
  - Form inputs with focus states
  - Buttons with hover and active states
  - Social links with transform effects
  - Bouncing animations on construction icons
- **Responsive Breakpoints:** 
  - Desktop: Full navigation bar (>768px)
  - Tablet: Hamburger menu, adjusted layouts (481px - 768px)
  - Mobile: Optimized for small screens (≤480px)

## Available Pages

The blog application includes the following pages:

| Route | Page | Description | Status |
|-------|------|-------------|--------|
| `/` | Home | Main blog page with blog posts | ✅ Complete |
| `/about` | About | Information about the blog author, background, and skills | ✅ Complete |
| `/contact` | Contact | Contact form with validation and social media links | ✅ Complete |
| `/categories` | Categories | Browse posts by category | 🚧 Under Construction |
| `/archive` | Archive | Chronological archive of all posts | 🚧 Under Construction |

All pages feature:
- Consistent navigation bar
- Responsive design
- Mobile hamburger menu
- Active page highlighting
- Beautiful gradient background
- Smooth animations

## Future Enhancements

Planned features for future versions:

### Backend Development
- ✅ Database integration (MySQL) 
- ✅ Blog post model and repository 
- ✅ Liquibase database versioning 
- REST API endpoints for blog posts
- CRUD operations for blog posts (Create, Update, Delete)
- Admin panel for managing blog posts
- User authentication and authorization
- Contact form backend integration (email sending)
- File upload for blog images
- Image storage and optimization

<!-- 
### Frontend Features
- Complete Categories page with category filtering
- Complete Archive page with chronological post listing
- Pagination for blog posts
- Individual blog post detail pages (/post/{id})
- Markdown support for blog content
- Rich text editor for blog writing
- Search functionality across all posts
- Comments system with moderation
- Tags and categories implementation
- Dark mode toggle
- RSS feed generation
- Social media sharing buttons
- Related posts suggestions

### Database Enhancements
- Add categories table and many-to-many relationship
- Add tags table for post tagging
- Add comments table for user comments
- Add users table for authentication
- Full-text search indexing
- Database backup and restore scripts

### Advanced Features
- Admin dashboard for content management
- Analytics and visitor tracking
- SEO optimization (meta tags, sitemap)
- Newsletter subscription system
- Email notifications
- Post scheduling (publish at future date)
- Draft posts functionality
- Post versioning and revision history
-->

<!-- ## License

This project is part of a portfolio/blog project.

## Author

Created as part of a portfolio project. -->

