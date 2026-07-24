# Second-Hand Marketplace

A complete second-hand goods trading platform with a Spring Boot backend and JavaFX desktop frontend.  
Users can register, post listings, chat with sellers, save favorites, rate sellers, and manage content through an admin panel.

---

## Team Members

- **Atbin Jafarzadeh Afshari** – Backend Developer (Spring Boot, REST API, SQLite, Security, JavaDoc)
- **Aein Akbarzadeh** – Frontend Developer (JavaFX, UI/UX, API Integration)

---

## Features

### Core Features
- User registration & login with JWT authentication
- Create, edit, delete, and view listings (with admin approval workflow)
- Advanced search with filters (keyword, category, city, price range)
- Chat system (conversations, messages with timestamps)
- Favorites management
- Seller rating system (average score & review count)
- Admin panel: approve/reject listings, manage users (block/unblock)

### Technical Highlights
- RESTful API with Spring Boot
- JWT-based stateless authentication
- BCrypt password hashing
- JPA Specifications for dynamic search queries
- File upload support for listing images
- Global exception handling with consistent error responses
- CORS support for frontend communication

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot 3.4, Spring Data JPA, Spring Security, JWT |
| Frontend | JavaFX 21, Maven, HttpClient (Java 11+), Jackson |
| Database | SQLite (embedded) |
| Build Tool | Maven |

---

## Prerequisites

- **JDK 17** or higher
- **Maven 3.8+**
- **IntelliJ IDEA** (recommended) or any Java IDE
- **Git** (for cloning)

---

## How to Run the Project

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd secondhand-marketplace
```

---

### Step 2: Run the Backend

#### Option 1: Using IntelliJ IDEA (Recommended)

1. Open IntelliJ IDEA and select **Open**.
2. Navigate to the project root folder and select the `pom.xml` file.
3. Wait for Maven to load all dependencies.
4. Locate the main class:  
   `backend/src/main/java/com/marketplace/MarketplaceApplication.java`
5. Right-click on the file and select **Run 'MarketplaceApplication.main()'**.
   - Or click the green **Run** button in the top-right corner of the IDE.

#### Option 2: Using Maven Command Line

```bash
cd backend
mvn spring-boot:run
```

#### Option 3: Using Maven Plugin in IntelliJ

1. Open the **Maven** tool window (View → Tool Windows → Maven).
2. Expand the `backend` module → **Plugins** → **spring-boot**.
3. Double-click on **spring-boot:run**.

**The backend will start on `http://localhost:8080`.**

> ⚠️ **Note:** If port 8080 is already in use, change it in `backend/src/main/resources/application.properties`:
> ```properties
> server.port=8081
> ```

---

### Step 3: Run the Frontend

#### Option 1: Using Maven Plugin in IntelliJ (Recommended)

1. Open the **Maven** tool window (View → Tool Windows → Maven).
2. Expand the `frontend` module → **Plugins** → **javafx**.
3. Double-click on **javafx:run**.

#### Option 2: Using Maven Command Line

```bash
cd frontend
mvn javafx:run
```

#### Option 3: Direct Run in IntelliJ

1. Locate the main class:  
   `frontend/src/main/java/com/marketplace/Main.java`
2. Right-click and select **Run 'Main.main()'**.
   - If you see `JavaFX runtime components are missing`, add the following VM options:
     ```
     --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics
     ```
   - For simplicity, use Option 1 (Maven plugin) which handles this automatically.

---

### Step 4: Access the Application

- The JavaFX window will open automatically.
- Use the test accounts below to log in.
- **Make sure the backend is running** before using the frontend.

---

## Test Accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Regular User | `alice` | `user123` |
| Regular User | `bob` | `user123` |

These accounts are automatically created by the `DataInitializer` on the first run.

---

## Database

- **SQLite** is used as the embedded database.
- The database file `secondhand.db` is automatically created in the `backend/` directory on first run.
- Default data (categories, cities, test users) is seeded by `DataInitializer`.
- No manual database setup is required.


---

## Key Implementation Notes

### Backend

- **Security:** JWT authentication with stateless sessions.  
  Public endpoints are defined in `SecurityConfig`. Protected endpoints require a valid JWT token.
- **Validation:** Bean validation (`@NotBlank`, `@Size`, etc.) is used for all request DTOs.
- **Error Handling:** `GlobalExceptionHandler` intercepts exceptions and returns consistent `ApiResponse` objects.
- **Search:** `ListingSpecifications` uses JPA Criteria API for dynamic, type-safe search queries.
- **Images:** Uploaded images are stored in the configured directory and served via `/api/uploads/**`.

### Frontend

- **API Client:** `ApiClient` centralizes all HTTP calls to the backend using Java `HttpClient`.
- **Session Management:** `UserSession` stores JWT token and user info for authenticated requests.
- **Async Operations:** `UiTasks` handles background tasks with `Task` and `Platform.runLater`.
- **Navigation:** `SceneNavigator` manages all screen transitions and view instantiation.

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| **Backend won't start** | Check if port 8080 is free. Change `server.port` in `application.properties`. |
| **Frontend shows "Backend unavailable"** | Ensure the backend is running on `http://localhost:8080`. |
| **JavaFX runtime components are missing** | Use `mvn javafx:run` instead of direct run, or add VM options. |
| **Database error** | Delete `secondhand.db` and restart the backend to recreate it. |
| **Images not loading** | Verify the upload directory exists and the backend is running. |
| **Maven build fails** | Run `mvn clean install` and reload Maven dependencies. |

---

## Acknowledgments

This project was developed as part of the **Advanced Programming** course at **Amirkabir University of Technology** (Spring 2026).  
Special thanks to the course instructors and teaching assistants for their guidance.

---

## License

This project is for educational purposes only and is not intended for commercial use.
