# Blog Application

A Spring Boot-based blogging website built with Java. This is the initial version featuring a simple "Hello World" webpage.

## Project Description

This is a blogging website application built using Spring Boot and Java. The project is currently in its initial development phase and displays a "Hello World" message on the homepage. It's designed to be the foundation for a full-featured blog platform with plans for future enhancements including database integration, blog post management, user authentication, and more.

## Technologies Used

- **Java 17+** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Boot Starter Web** - Web application support
- **Spring Boot Starter Thymeleaf** - Template engine (for future HTML templates)
- **Maven** - Build tool and dependency management

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
│   │   │               └── HomeController.java   # REST controller for homepage
│   │   └── resources/
│   │       ├── application.properties            # Application configuration
│   │       └── templates/                        # Template directory (for future use)
├── pom.xml                                       # Maven configuration file
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

5. **You should see** "Hello World" displayed on the page.

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

## Future Enhancements

Planned features for future versions:
- Database integration (PostgreSQL/MySQL)
- Blog post model and repository
- CRUD operations for blog posts
- User authentication and authorization
- UI with Vue3.js
- Blog post listing and detail pages
- Markdown support for blog content
- Search functionality
- Comments system

<!-- ## License

This project is part of a portfolio/blog project.

## Author

Created as part of a portfolio project. -->

