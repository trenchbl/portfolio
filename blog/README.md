# Blog Application

A Spring Boot-based blogging website built with Java and Vue3.js. This version features a modern, responsive blog post display with centered layout and beautiful UI design.

## Project Description

This is a blogging website application built using Spring Boot, Java, and Vue3.js. The application displays a styled blog post with a modern, responsive design. The homepage features a centered blog post card with proper spacing, gradient background, and smooth hover effects. It's designed to be the foundation for a full-featured blog platform with plans for future enhancements including database integration, multiple blog posts, user authentication, and more.

## Technologies Used

### Backend
- **Java 17+** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Boot Starter Web** - Web application support
- **Spring Boot Starter Thymeleaf** - Template engine for HTML rendering
- **Maven** - Build tool and dependency management

### Frontend
- **Vue3.js** - Progressive JavaScript framework for building user interfaces
- **HTML5** - Semantic markup
- **CSS3** - Modern styling with gradients, shadows, and responsive design

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
         

## Project Structure

```
blog/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── blog/
│   │   │           ├── BlogApplication.java      # Main Spring Boot application class
│   │   │           └── controller/
│   │   │               └── HomeController.java   # Controller serving the HTML template
│   │   └── resources/
│   │       ├── application.properties            # Application configuration
│   │       └── templates/
│   │           └── index.html                    # Main page with Vue3.js integration
├── pom.xml                                       # Maven configuration file
├── 001-step-by-step-init                         # Initial setup documentation
├── 002-step-by-step-ui-init.txt                 # UI implementation plan
└── README.md                                     # This file
```

## How to Run the Project

### Method 1: Using Maven (Recommended)

1. **Open a terminal/command prompt** in the project root directory (`blog/`)

2. **Build and run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Wait for the application to start.** You should see output indicating that the application is running on port 8080:
   ```
   Started BlogApplication in X.XXX seconds
   ```

4. **Open your web browser** and navigate to:
   ```
   http://localhost:8080
   ```

5. **You should see** a blog post page with:
   - Centered blog post card with purple gradient background
   - Blog post title: "Welcome to My Blog"
   - Author and date metadata
   - Multiple paragraphs of content
   - Responsive design that adapts to different screen sizes
   - Smooth hover effects

### Method 2: Using an IDE

1. **Import the project** into your IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

2. **Ensure Maven dependencies are downloaded:**
   - Most IDEs will do this automatically
   - Or run: `mvn clean install`

3. **Run the `BlogApplication.java` file:**
   - Right-click on `BlogApplication.java`
   - Select "Run" or "Run 'BlogApplication'"

4. **Open your web browser** and navigate to:
   ```
   http://localhost:8080
   ```

5. **You should see** a blog post page with:
   - Centered blog post card with purple gradient background
   - Blog post title: "Welcome to My Blog"
   - Author and date metadata
   - Multiple paragraphs of content
   - Responsive design that adapts to different screen sizes
   - Smooth hover effects

### Stopping the Application

- **In terminal:** Press `Ctrl + C` to stop the application
- **In IDE:** Click the stop button in the console/run window

## Configuration

The application is configured via `src/main/resources/application.properties`:

- **Server Port:** 8080 (default)
- **Application Name:** blog

You can modify these settings in the `application.properties` file if needed.

## Building the Project

To build the project without running it:

```bash
mvn clean package
```

This will create a JAR file in the `target/` directory that can be run independently:

```bash
java -jar target/blog-1.0.0.jar
```

## Troubleshooting

### Java Version Issues
- Ensure you have JDK 17 or newer installed
- Verify with: `java -version`
- Set `JAVA_HOME` environment variable if needed

### Maven Issues
- Ensure Maven is installed and in your PATH
- Verify with: `mvn -version`
- If dependencies fail to download, check your internet connection
- 
### Port Already in Use
- If port 8080 is already in use, change it in `application.properties`:
  ```
  server.port=8081
  ```

## Features

### Current Features (v1.0.0)
- Modern, responsive UI with Vue3.js
- Centered blog post layout with proper spacing
- Beautiful gradient background design
- Smooth animations and hover effects
- Mobile-responsive design (320px+)
- Semantic HTML5 structure
- Clean, readable typography

### UI Design Details
- **Layout:** Centered with max-width of 800px
- **Spacing:** 20px minimum padding on left and right
- **Background:** Purple gradient (from #667eea to #764ba2)
- **Card Design:** White background with rounded corners and shadow effects
- **Typography:** System fonts with excellent readability (line-height: 1.8)
- **Responsive Breakpoints:** 
  - Desktop: Full featured (>768px)
  - Tablet: Adjusted padding and font sizes (≤768px)
  - Mobile: Optimized for small screens (≤480px)

## Future Enhancements

Planned features for future versions:
- Database integration (PostgreSQL/MySQL)
- Blog post model and repository
- REST API endpoints for blog posts
- CRUD operations for blog posts
- User authentication and authorization
- Multiple blog posts with Vue Router for navigation
- Blog post listing and detail pages
- Markdown support for blog content
- Search functionality
- Comments system
- Vuex/Pinia state management
- Admin dashboard for content management

<!-- ## License

This project is part of a portfolio/blog project.

## Author

Created as part of a portfolio project. -->

