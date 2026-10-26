# Airline Management System — Viva Preparation Guide
**DBMS Lab (Experiment No. 1) & Java Mini-Project**
**Student: Udit Yadav | Roll No: 55 | SE Comp A**

This document covers all potential viva and theoretical questions that your examiners, professors, or lab evaluators may ask regarding your ER/EER model, MySQL database, Java Spring Boot backend, JDBC connectivity, and React frontend.

---

## Part 1: DBMS & ER / EER Model Concepts (Experiment 1)

### Q1: What is an Entity-Relationship (ER) model?
**Answer:**
An ER model is a high-level conceptual data model that defines the data elements and relationships for a specific system. It visually represents entities (real-world objects or concepts), their attributes (properties), and relationships (associations among entities).

### Q2: What is the difference between standard ER and Extended ER (EER)?
**Answer:**
Standard ER includes basic entities, attributes, and relationships. Extended ER (EER) introduces advanced semantic concepts such as:
1. **Generalization & Specialization**: Representing superclass/subclass hierarchies.
2. **Categorization (Union Types)**.
3. **Aggregation**.

In our airline project, **Employee** is a superclass specialized into subclasses: **Pilot**, **Cabin Crew**, and **Engineer**.

### Q3: What is Disjoint Specialization vs Overlapping Specialization?
**Answer:**
- **Disjoint (denoted by 'd' in a circle)**: An entity instance of the superclass can belong to **at most one** subclass. In our system, an employee cannot simultaneously be both an active Pilot and a Cabin Crew member.
- **Overlapping (denoted by 'o')**: An entity instance can belong to multiple subclasses at the same time (e.g., a Person who is both an Employee and a Passenger).

### Q4: What is a Weak Entity? Where is it in the ER diagram?
**Answer:**
A weak entity is an entity that does not possess a primary key of its own and depends on the existence of a strong (identifying) owner entity. Its instances are identified by a **partial key (discriminator)** combined with the primary key of the owner entity.
- In our ER diagram, **Baggage** is a weak entity dependent on **Passenger** (partial key: `Baggage Number`), and **Maintenance Record** is a weak entity dependent on **Maintenance** (partial key: `Record Number`).

### Q5: What are the disadvantages of a traditional file-based system compared to a DBMS?
**Answer:**
1. **Data Redundancy and Inconsistency**: Data is duplicated across multiple files, leading to conflicting versions.
2. **Difficulty in Accessing Data**: Ad-hoc queries require writing new application programs.
3. **Data Isolation**: Data scattered in various formats makes integration difficult.
4. **Integrity Problems**: Constraints must be coded into every program rather than enforced by the database engine.
5. **Atomicity & Concurrency Issues**: File systems lack automatic ACID transaction guarantees and concurrent update locks.

### Q6: What is Data Independence? Explain the Two Levels.
**Answer:**
Data independence is the capacity to modify the schema at one level of the database architecture without altering the schema at the next higher level:
1. **Logical Data Independence**: The ability to modify the conceptual schema without having to change external schemas or application programs.
2. **Physical Data Independence**: The ability to change the internal storage structure (e.g., indexes, disk layout) without having to alter the conceptual or logical schema.

---

## Part 2: Database Design, Constraints & Normalization

### Q7: Describe the tables and relationships in `airline_db`.
**Answer:**
Our database consists of four normalized tables:
1. `employees`: Stores staff records with `specialty` (`Pilot`, `Cabin Crew`, `Engineer`) and `employment_status`.
2. `passengers`: Stores traveler records with a `UNIQUE` constraint on `passport_number`.
3. `flights`: Stores route schedules, departure/arrival timestamps, origin airport, and destination airport.
4. `reservations`: Relational table with foreign keys referencing `passengers` (`passenger_id`) and `flights` (`flight_id`), seat number, booking date, and confirmation status.

### Q8: How is double-booking prevented in the database?
**Answer:**
We enforced a composite unique constraint on the `reservations` table:
```sql
CONSTRAINT uq_flight_seat UNIQUE (flight_id, seat_number);
```
If an attempt is made to insert a second reservation with the same `flight_id` and `seat_number`, MySQL immediately rejects the transaction with MySQL Error code `1062 (Duplicate entry)`. The backend translates this into an HTTP `409 Conflict` response with a friendly user message.

### Q9: Why did you use `ON DELETE RESTRICT` for foreign keys?
**Answer:**
`ON DELETE RESTRICT` protects relational integrity. If an administrator tries to delete a passenger or flight that already has active reservations, MySQL blocks the deletion. This prevents orphan records and accidental data loss.

### Q10: What Normal Form (NF) is the database in?
**Answer:**
The schema is in **Third Normal Form (3NF)** / **BCNF**:
- **1NF**: Every column contains atomic (indivisible) values, and each record is uniquely identified by a primary key.
- **2NF**: It is in 1NF, and there are no partial dependencies (all non-key attributes are fully functionally dependent on the entire primary key).
- **3NF**: It is in 2NF, and there are no transitive dependencies (no non-key attribute depends on another non-key attribute; e.g., employee details depend strictly on `employee_id`).

---

## Part 3: Java, JDBC & Object-Oriented Programming (OOP)

### Q11: What is JDBC and what are its key interfaces?
**Answer:**
JDBC (Java Database Connectivity) is a standard Java API for connecting to relational databases and executing SQL statements. Key interfaces in `java.sql` used in our project:
1. `DriverManager` / `DataSource`: Establishes the database connection.
2. `Connection`: Represents the active session with the MySQL server.
3. `PreparedStatement`: Precompiled SQL statement used to execute parameterized queries safely.
4. `ResultSet`: A tabular cursor containing the rows returned by an SQL `SELECT` query.

### Q12: Why did you use `PreparedStatement` instead of `Statement`?
**Answer:**
1. **Security (SQL Injection Prevention)**: `PreparedStatement` uses parameter placeholders (`?`). Values passed through `pstmt.setString(...)` or `pstmt.setInt(...)` are treated strictly as data literals, never as executable SQL code.
2. **Performance**: The database server compiles and caches the query plan once, allowing efficient re-execution with different parameters.
3. **Automatic Type Casting & Escaping**: It handles quotes, dates, and special characters automatically.

### Q13: What is the difference between `executeQuery()` and `executeUpdate()`?
**Answer:**
- `executeQuery()`: Used exclusively for SQL `SELECT` statements. It returns a `ResultSet` object containing the retrieved rows.
- `executeUpdate()`: Used for data manipulation statements (`INSERT`, `UPDATE`, `DELETE`) or DDL statements. It returns an `int` representing the number of rows affected.

### Q14: How are generated keys retrieved after an INSERT?
**Answer:**
When creating the `PreparedStatement`, we pass `Statement.RETURN_GENERATED_KEYS`:
```java
PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
pstmt.executeUpdate();
try (ResultSet rs = pstmt.getGeneratedKeys()) {
    if (rs.next()) {
        int id = rs.getInt(1);
        employee.setEmployeeId(id);
    }
}
```

### Q15: How are OOP principles demonstrated in your backend?
**Answer:**
1. **Encapsulation**: Model classes (`Employee`, `Passenger`, `Flight`, `Reservation`) keep all attributes `private` and provide public getters and setters.
2. **Abstraction**: The Service layer abstracts business rules away from the DAO layer which abstracts raw SQL operations away from the REST Controller.
3. **Polymorphism & Interfaces**: Spring's dependency injection relies on interface contracts (e.g., `WebMvcConfigurer`).
4. **Exception Handling**: Customized `ApiException` extending `RuntimeException` and try-with-resources blocks that ensure `Connection`, `PreparedStatement`, and `ResultSet` are closed deterministically to prevent connection leaks.

---

## Part 4: Spring Boot & REST Architecture

### Q16: What is Dependency Injection (DI) and Inversion of Control (IoC)?
**Answer:**
- **Inversion of Control (IoC)** is a design principle where the control of object creation and lifecycle management is transferred from the application code to a framework container (Spring IoC Container).
- **Dependency Injection (DI)** is the mechanism by which Spring injects dependent objects (e.g., injecting `EmployeeDao` into `EmployeeService`) rather than having the service instantiate the DAO with `new EmployeeDao()`. We used **constructor-based dependency injection**, which is recommended for immutability and testability.

### Q17: What are the main Spring annotations used in this project?
**Answer:**
- `@SpringBootApplication`: Marks the main bootstrap class and enables auto-configuration, component scanning, and extra configuration.
- `@Configuration`: Marks classes (`DatabaseConfig`, `WebConfig`) that declare Spring bean definitions.
- `@RestController`: Combines `@Controller` and `@ResponseBody`, converting Java object return values directly into JSON responses.
- `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@DeleteMapping`: Map HTTP methods and URL paths to controller handler methods.
- `@RequestBody`: Binds the incoming JSON request payload to a Java object.
- `@PathVariable`: Extracts dynamic values from URI path templates (e.g., `/api/employees/{id}`).
- `@RestControllerAdvice`: Provides centralized global exception handling across all controllers.

### Q18: What is CORS and why did you configure it?
**Answer:**
CORS (Cross-Origin Resource Sharing) is a browser security mechanism that restricts web applications running at one origin (e.g., React on `http://localhost:5173`) from making requests to a different origin (e.g., Spring Boot on `http://localhost:8080`).
We implemented `WebConfig` using `addCorsMappings(...)` to explicitly permit requests from `http://localhost:5173`.

---

## Part 5: React & Frontend Architecture

### Q19: Explain the React Hooks used: `useState` and `useEffect`.
**Answer:**
- `useState`: Declares reactive state variables inside functional components. When the state setter is called, React triggers a re-render with the updated value. Used for form inputs, table lists, active tabs, and modals.
- `useEffect`: Handles side effects in functional components. In our project, it runs on component mount (`[]` dependency array) to asynchronously fetch initial data from the Spring Boot REST API.

### Q20: How does data flow in your React frontend?
**Answer:**
React follows a **unidirectional (top-down) data flow**:
- State resides in container components (e.g., `App.jsx` holding `activePage` and `notification`, or `FlightsPage.jsx` holding the flight list).
- Data and callback functions are passed down to child components via `props` (e.g., `StatCard` receiving `title` and `value`, or `ConfirmModal` receiving `onConfirm` and `onCancel`).
