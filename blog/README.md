# Blog Application

A Spring Boot-based blogging website built with Java and Vue3.js. This version features a modern, responsive multi-page blog with navigation bar, contact form, and beautiful UI design.

## Project Description

This is a blogging website application built using Spring Boot, Java, and Vue3.js. The application features a complete multi-page blog with a fixed navigation bar, multiple pages (Home, About, Contact, Categories, Archive), and a modern, responsive design. The site includes a homepage with blog posts, an about page with author information, a fully functional contact form with social media links, and placeholder pages for future features. The design features a beautiful purple gradient background, smooth animations, and a mobile-responsive hamburger menu. It's designed to be the foundation for a full-featured blog platform with plans for future enhancements including database integration, multiple blog posts, user authentication, and more.

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
│   │   │               └── HomeController.java   # Controller with all page routes
│   │   └── resources/
│   │       ├── application.properties            # Application configuration
│   │       └── templates/
│   │           ├── index.html                    # Home page with blog posts
│   │           ├── about.html                    # About page
│   │           ├── contact.html                  # Contact page with form
│   │           ├── categories.html               # Categories page (Under Construction)
│   │           └── archive.html                  # Archive page (Under Construction)
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

5. **You should see** the blog home page with:
   - Fixed navigation bar at the top with links to: Home, About, Categories, Archive, Contact
   - Responsive hamburger menu on mobile devices
   - Purple gradient background (#667eea to #764ba2)
   - Centered blog post card
   - Blog post title: "Welcome to My Blog"
   - Author and date metadata
   - Multiple paragraphs of content
   - Smooth animations and hover effects
   
6. **Explore the site:**
   - Click on **About** to see author information and background
   - Click on **Contact** to access the contact form with social media links
   - Click on **Categories** or **Archive** to see "Under Construction" pages
   - Try resizing your browser to see the responsive navigation menu

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

5. **You should see** the blog home page with:
   - Fixed navigation bar at the top with links to: Home, About, Categories, Archive, Contact
   - Responsive hamburger menu on mobile devices
   - Purple gradient background (#667eea to #764ba2)
   - Centered blog post card
   - Blog post title: "Welcome to My Blog"
   - Author and date metadata
   - Multiple paragraphs of content
   - Smooth animations and hover effects
   
6. **Explore the site:**
   - Click on **About** to see author information and background
   - Click on **Contact** to access the contact form with social media links
   - Click on **Categories** or **Archive** to see "Under Construction" pages
   - Try resizing your browser to see the responsive navigation menu

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

### Current Features (v2.0.0)
- **Multi-page Navigation:** Complete navigation bar with 5 pages
- **Fixed Navigation Bar:** Stays at top while scrolling with active page indicator
- **Responsive Mobile Menu:** Hamburger menu with smooth slide-in animation
- **Home Page:** Blog post display with modern card design
- **About Page:** Author information with background, skills, and interests
- **Contact Page:** 
  - Fully functional contact form with validation
  - Social media links (GitHub, LinkedIn, Twitter, Email)
  - Success message animation
  - Form fields: Name, Email, Subject, Message
- **Categories Page:** "Under Construction" placeholder with animated icon
- **Archive Page:** "Under Construction" placeholder with animated icon
- Modern, responsive UI with Vue3.js
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
- Database integration (PostgreSQL/MySQL)
- Blog post model and repository
- REST API endpoints for blog posts
- CRUD operations for blog posts
- User authentication and authorization
- Contact form backend integration (email sending)
- File upload for blog images

### Frontend Features
- Complete Categories page with category filtering
- Complete Archive page with chronological post listing
- Multiple blog posts with pagination
- Individual blog post detail pages
- Markdown support for blog content
- Rich text editor for blog writing
- Search functionality across all posts
- Comments system with moderation
- Dark mode toggle
- RSS feed

### Advanced Features
- Vuex/Pinia state management
- Vue Router for client-side navigation
- Admin dashboard for content management
- Analytics and visitor tracking
- SEO optimization
- Social media sharing buttons
- Newsletter subscription
- Tags and categories system
- Related posts suggestions

<!-- ## License

This project is part of a portfolio/blog project.

## Author

Created as part of a portfolio project. -->

