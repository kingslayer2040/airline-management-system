# Airline Management System — Admin Panel
**SE Computer Engineering — DBMS / Java Mini-Project (Experiment No. 1)**

A simple, robust, professional, and fully functional **Airline Management System (Admin Panel)** built with **Java, Spring Boot, MySQL (plain JDBC with PreparedStatement), and React JS**.

---

## 1. Project Overview

This project implements an operational admin dashboard for managing commercial airline operations:
1. **Employees**: Tracking pilots, cabin crew, and engineers with active/inactive statuses (reflecting the disjoint specialization from Experiment 1 ER Diagram).
2. **Passengers**: Managing passenger directory with unique passport verification.
3. **Flights & Routes**: Scheduling flights, origin/destination airport validation, and departure/arrival constraints.
4. **Reservations**: Relational seat reservations joining passengers and flights with strict duplicate seat prevention.
5. **Dashboard**: Real-time operational metrics (counts and recent flights) directly queried from MySQL via JDBC aggregate functions.

---

## 2. Technology Stack

Strictly adheres to college syllabus constraints (no JPA/Hibernate, no TypeScript, no external CSS libraries, no microservices):

- **Backend**:
  - Java 21 LTS
  - Spring Boot 3.3.4 (REST Controllers, Dependency Injection, IoC)
  - Pure JDBC (`DriverManager`, `Connection`, `PreparedStatement`, `ResultSet`, no ORM/Hibernate)
- **Database**:
  - MySQL 8.0 (`airline_db`)
  - Relational Foreign Keys with `ON DELETE RESTRICT`
  - Composite Unique Constraints (`(flight_id, seat_number)`)
- **Frontend**:
  - React 18 (Functional components, `useState`, `useEffect`)
  - HTML5 & Pure CSS3 (Navy Blue `#0F2A4A` and Light Blue `#0284C7` theme)
  - JavaScript (Fetch API, ES6 modules)
  - Vite dev server & build tool

---

## 3. Project Folder Structure

```
airline-management-system/
├── database/
│   └── airline_db_setup.sql        # Database schema, constraints, and seed data
├── backend/
│   ├── pom.xml                     # Maven project configuration
│   ├── mvnw.cmd                    # Maven wrapper script
│   └── src/main/
│       ├── java/com/airline/
│       │   ├── AirlineApplication.java     # Spring Boot entrypoint
│       │   ├── config/
│       │   │   ├── DatabaseConfig.java     # Pure JDBC connection provider
│       │   │   └── WebConfig.java          # CORS configuration
│       │   ├── controller/
│       │   │   ├── DashboardController.java    # /api/dashboard
│       │   │   ├── EmployeeController.java     # /api/employees
│       │   │   ├── PassengerController.java    # /api/passengers
│       │   │   ├── FlightController.java       # /api/flights
│       │   │   └── ReservationController.java  # /api/reservations
│       │   ├── dao/
│       │   │   ├── DashboardDao.java       # COUNT() queries
│       │   │   ├── EmployeeDao.java        # JDBC PreparedStatement CRUD
│       │   │   ├── PassengerDao.java       # JDBC PreparedStatement CRUD
│       │   │   ├── FlightDao.java          # JDBC PreparedStatement CRUD
│       │   │   └── ReservationDao.java     # Relational JOIN & seat checks
│       │   ├── exception/
│       │   │   ├── ApiException.java           # Custom runtime exception
│       │   │   └── GlobalExceptionHandler.java # Uniform JSON error handler
│       │   ├── model/
│       │   │   ├── DashboardStats.java     # Aggregated stats DTO
│       │   │   ├── Employee.java           # Employee domain model
│       │   │   ├── Flight.java             # Flight schedule model
│       │   │   ├── Passenger.java          # Passenger model
│       │   │   ├── Reservation.java        # Reservation model
│       │   │   └── ReservationView.java    # Joined view DTO for UI table
│       │   └── service/
│       │       ├── DashboardService.java
│       │       ├── EmployeeService.java
│       │       ├── FlightService.java
│       │       ├── PassengerService.java
│       │       └── ReservationService.java
│       └── resources/
│           └── application.properties      # MySQL connection credentials
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── App.jsx                 # Main layout & page switcher
│       ├── App.css                 # Plain CSS styling
│       ├── main.jsx                # React root mount
│       ├── components/
│       │   ├── ConfirmModal.jsx    # Deletion confirmation modal
│       │   ├── Header.jsx          # Top navbar & clock
│       │   ├── Notification.jsx    # Success & error alerts
│       │   ├── Sidebar.jsx         # Sidebar navigation
│       │   └── StatCard.jsx        # Metric counter card
│       ├── pages/
│       │   ├── DashboardPage.jsx   # Page 1: Overview & Recent Flights
│       │   ├── EmployeesPage.jsx   # Page 2: Add/View/Delete Employees
│       │   ├── PassengersPage.jsx  # Page 3: Register/View/Delete Passengers
│       │   ├── FlightsPage.jsx     # Page 4: Add/View/Delete Flights
│       │   └── ReservationsPage.jsx# Page 5: Book Seat/View/Cancel
│       └── services/
│           └── api.js              # REST API Fetch client
└── README.md
```

---

## 4. Database Setup & Configuration

1. **Start MySQL Server** (verify MySQL service is running on port `3306`).
2. **Execute Setup Script**:
   ```powershell
   Get-Content database/airline_db_setup.sql -Raw | mysql -u root -p
   ```
   *Default password configured:* `root` (change in `backend/src/main/resources/application.properties` if different).
3. **Tables Created**:
   - `employees` (employee_id, full_name, specialty, contact_number, email, employment_status)
   - `passengers` (passenger_id, full_name, email, contact_number, passport_number UNIQUE)
   - `flights` (flight_id, flight_number, source_airport, destination_airport, departure_time, arrival_time, status)
   - `reservations` (reservation_id, passenger_id FK, flight_id FK, seat_number, booking_date, status, UNIQUE(flight_id, seat_number))

---

## 5. How to Run the Application

### A. Run Spring Boot Backend (Port 8080)
Open a terminal in `backend/`:
```powershell
cd backend
mvn clean package -DskipTests
java -jar target/airline-management-backend-1.0.0.jar
```
*(Or use `mvn spring-boot:run`)*

The backend starts at `http://localhost:8080/api`.

### B. Run React Frontend (Port 5173)
Open a second terminal in `frontend/`:
```powershell
cd frontend
npm install
npm run dev
```

Open your browser and navigate to:
**`http://localhost:5173`**

---

## 6. REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/dashboard` | Returns employee, passenger, flight, reservation counts and recent 5 flights |
| `GET` | `/api/employees` | List all employees |
| `POST` | `/api/employees` | Add a new employee |
| `DELETE`| `/api/employees/{id}` | Delete an employee by ID |
| `GET` | `/api/passengers` | List all passengers |
| `POST` | `/api/passengers` | Register a new passenger (enforces unique passport) |
| `DELETE`| `/api/passengers/{id}`| Delete a passenger (blocked if reservations exist) |
| `GET` | `/api/flights` | List all flights |
| `POST` | `/api/flights` | Schedule a flight (validates origin != destination, arrival > departure) |
| `DELETE`| `/api/flights/{id}` | Delete a flight (blocked if reservations exist) |
| `GET` | `/api/reservations` | List all reservations joined with passenger and flight info |
| `POST` | `/api/reservations` | Create reservation (enforces unique seat per flight) |
| `DELETE`| `/api/reservations/{id}`| Cancel/Delete a reservation |

---

## 7. Syllabus & Viva Concepts Demonstrated

### Java & Object-Oriented Programming (OOP)
1. **Encapsulation**: Models (`Employee`, `Flight`, etc.) keep member variables `private` and expose public getters/setters.
2. **Constructors**: Default and overloaded constructors implemented for flexibility during DB mapping.
3. **Layered Architecture (IoC / DI)**: Decoupling Controller ➔ Service ➔ DAO using Spring's `@Autowired` constructor injection.
4. **Exception Handling**: Try-catch-finally and try-with-resources used to safeguard database connections and handle runtime constraint violations.
5. **Collections**: `List<T>`, `ArrayList<T>`, and `Map<String, Object>` used throughout data access and controller tiers.

### Relational Database & JDBC
1. **JDBC PreparedStatement**: Prevents SQL injection and binds parameters (`pstmt.setString(...)`, `pstmt.setInt(...)`).
2. **Relational Constraints**:
   - `PRIMARY KEY` with `AUTO_INCREMENT`.
   - `FOREIGN KEY` with `ON DELETE RESTRICT` preventing accidental orphan reservations.
   - `UNIQUE KEY (flight_id, seat_number)` preventing double-booking.
3. **Aggregate Queries**: `COUNT(*)` in `DashboardDao` for summary analytics.
4. **Relational Joins**: `JOIN passengers p JOIN flights f` in `ReservationDao` to produce unified reservation records.

### React & Modern Web
1. **Functional Components & Hooks**: `useState` for UI state; `useEffect` for asynchronous REST API data loading.
2. **Controlled Forms**: Full two-way data binding and client-side validation hints.
3. **Conditional Rendering**: Handling loading spinners, empty states, and error alerts.
4. **CSS Architecture**: Responsive flexbox and CSS grid with a clean corporate palette.
