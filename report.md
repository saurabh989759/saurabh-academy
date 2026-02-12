# Academy Backend - Project Report

## Abstract

This project implements a comprehensive backend system for managing an academy with students, batches, mentors, classes, and mentor sessions. The system is built using Spring Boot 3.2.0 with Java 21, MySQL 8.0 for data persistence, and Apache Kafka for event-driven messaging. The application follows a layered architecture pattern with RESTful APIs, implements database migrations using Flyway, and includes comprehensive testing using Testcontainers. The system demonstrates production-ready practices including Docker containerization, CI/CD pipelines, API documentation, and monitoring capabilities.

## Project Description

### Objectives

The Academy Backend system aims to provide:

1. **Student Management**: Registration, enrollment in batches, and tracking of academic progress
2. **Batch Management**: Creation and management of student batches with associated classes
3. **Mentor Management**: Tracking mentors and their sessions with students
4. **Class Scheduling**: Management of classes and their association with batches
5. **Event-Driven Architecture**: Asynchronous event processing using Kafka for domain events
6. **Scalable Architecture**: Microservices-ready design with clear separation of concerns

### Relevance

This project demonstrates real-world backend development practices including:
- RESTful API design and implementation
- Database schema design with proper relationships and constraints
- Event-driven architecture for decoupled systems
- Containerization and orchestration with Docker
- Automated testing strategies
- CI/CD pipeline implementation
- API documentation and monitoring

## Requirement Gathering

### Functional Requirements

1. **Student Registration**
   - Register new students with personal and academic information
   - Assign students to batches
   - Track student batch history
   - Support buddy system (pairing students)

2. **Batch Management**
   - Create and manage batches
   - Associate batches with batch types (e.g., Full Stack, Data Science)
   - Link classes to batches
   - Track batch start dates and instructors

3. **Mentor Session Booking**
   - Book mentor sessions between students and mentors
   - Track session duration and timings
   - Support bidirectional ratings (student rates mentor, mentor rates student)

4. **Class Management**
   - Create classes with date, time, and instructor information
   - Associate classes with multiple batches

5. **Event Publishing**
   - Publish `student.registered` event when a student is registered
   - Publish `mentor.session.created` event when a session is booked
   - Publish `batch.created` event when a batch is created

### Non-Functional Requirements

1. **Performance**
   - API response time < 200ms for read operations
   - Support for pagination on list endpoints
   - Database query optimization with proper indexes

2. **Scalability**
   - Horizontal scaling capability
   - Stateless API design
   - Kafka-based event processing for async operations

3. **Reliability**
   - Database transaction management
   - Error handling and logging
   - Health check endpoints

4. **Security**
   - Input validation using JSR-380
   - Placeholder for OAuth2/JWT authentication
   - SQL injection prevention via JPA

5. **Maintainability**
   - Layered architecture
   - DTOs for API contracts
   - Comprehensive test coverage
   - API documentation via OpenAPI/Swagger

## Class Diagrams (UML)

The system follows a layered architecture with clear separation of concerns across multiple layers. The complete class diagram illustrates all components and their relationships.

### Architecture Layers

#### 1. Entity Layer (Domain Models)
The core domain entities represent the business domain:
- **`BatchType`**: Represents types of batches (Full Stack, Data Science, etc.)
- **`Batch`**: Represents a batch of students with start date and instructor
- **`Student`**: Represents a student with personal and academic information
- **`ClassEntity`**: Represents a class/session with date, time, and instructor
- **`Mentor`**: Represents a mentor with company information
- **`MentorSession`**: Represents a booking between a student and mentor (key feature)
- **`StudentBatchHistory`**: Tracks student batch assignments over time
- **`AuditEvent`**: Stores consumed Kafka events for audit purposes
- **`User`**: Represents system users for authentication (username, password, role, enabled status)

#### 2. Repository Layer (Data Access)
Spring Data JPA repositories provide data access abstraction:
- **`StudentRepository`**: CRUD operations for students with custom query methods
- **`BatchRepository`**: Batch management with pessimistic locking support
- **`ClassRepository`**: Class entity operations
- **`MentorRepository`**: Mentor data access
- **`MentorSessionRepository`**: Session management with student/mentor queries
- **`BatchTypeRepository`**: Batch type reference data
- **`AuditEventRepository`**: Event storage for audit trail
- **`UserRepository`**: User data access with `findByUsername()` and `existsByUsername()` methods

#### 3. Service Layer (Business Logic)
Services implement business logic with transaction management:
- **`StudentService`**: Handles student registration, updates, and management with distributed locking
- **`BatchService`**: Manages batches, class assignments, and associations
- **`MentorService`**: Manages mentor information
- **`MentorSessionService`**: Handles mentor session booking (key feature) with event publishing
- **`ClassService`**: Manages class information
- **`DistributedLockService`**: Provides distributed locking using Redis for concurrency control

#### 4. Controller Layer (REST API)
REST controllers expose HTTP endpoints implementing OpenAPI interfaces:
- **`StudentController`**: `/api/students` - Student CRUD operations
- **`BatchController`**: `/api/batches` - Batch management and class assignment
- **`MentorController`**: `/api/mentors` - Mentor operations
- **`MentorSessionController`**: `/api/mentor-sessions` - Session booking
- **`ClassController`**: `/api/classes` - Class management
- **`AuthController`**: `/api/auth` - Authentication endpoints

#### 5. Kafka Layer (Event-Driven)
Event producers and consumers for asynchronous messaging:
- **`StudentEventProducer`**: Publishes `student.registered` events
- **`MentorSessionEventProducer`**: Publishes `mentor.session.created` events
- **`BatchEventProducer`**: Publishes `batch.created` events
- **`EventConsumer`**: Consumes events and stores in `audit_events` table

#### 6. Security Layer (Authentication & Authorization)
JWT-based authentication and authorization:
- **`User`**: Entity storing user credentials (username, password, role, enabled)
- **`UserRepository`**: Repository for user data access
- **`UserDetailsServiceImpl`**: Implements Spring Security's `UserDetailsService` to load users from database
- **`JwtService`**: Handles JWT token generation, validation, and claims extraction
- **`JwtAuthenticationFilter`**: Spring Security filter that intercepts requests and validates JWT tokens
- **`AuthController`**: REST controller for login and token validation endpoints

#### 7. Aspect Layer (AOP)
Cross-cutting concerns:
- **`LockAspect`**: AOP aspect for distributed locking using `@WithLock` annotation

### UML Class Diagram

**Complete Class Diagram**: `puml/complete-class-diagram.puml`

This comprehensive UML diagram shows:
- All entity classes with attributes and methods
- Repository interfaces with query methods
- Service classes with business logic methods
- Controller classes with REST endpoints
- Kafka producers and consumers
- Relationships and dependencies between layers
- AOP aspects and their integration

**How to View:**
1. **PlantUML**: Use PlantUML viewer (VS Code extension, online viewer, or IntelliJ plugin)
2. **Draw.io**: Import the `.puml` file into draw.io (File → Import → PlantUML)
3. **Online**: Use http://www.plantuml.com/plantuml/uml/ to render

**Key Relationships:**
- Controllers depend on Services
- Services depend on Repositories and Kafka Producers
- Repositories manage Entities
- Services use DistributedLockService for concurrency control
- Event Consumers persist to AuditEventRepository
- LockAspect intercepts service methods with `@WithLock` annotation
- AuthController uses JwtService and AuthenticationManager for login
- JwtAuthenticationFilter uses JwtService to validate tokens
- UserDetailsServiceImpl uses UserRepository to load user details
- JwtAuthenticationFilter sets authentication in SecurityContext for authorized requests

## Database Schema Design (ERD)

### Schema Overview

The database consists of **10 main tables** organized into core entities, join tables, and audit/history tables:

#### Core Tables (7)
1. **batch_type**: Stores batch type definitions (reference data)
2. **batches**: Stores batch information with foreign key to batch_type
3. **classes**: Stores class/session information
4. **students**: Stores student information with foreign key to batches and self-reference for buddy system
5. **mentors**: Stores mentor information
6. **mentor_sessions**: Stores mentor session bookings with foreign keys to students and mentors
7. **users**: Stores user credentials for JWT authentication (username, password, role, enabled)

#### Join Tables (1)
8. **batches_classes**: Many-to-many relationship between batches and classes

#### Audit & History Tables (2)
9. **student_batch_history**: Tracks historical batch assignments for students
10. **audit_events**: Stores consumed Kafka events for auditing and event replay

### Detailed Table Structure

#### batch_type
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Unique Constraint**: `name` (VARCHAR(255))
- **Additional Fields**: `description` (TEXT)
- **Indexes**: PRIMARY KEY (id), UNIQUE INDEX (name)

#### batches
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Foreign Keys**: `batch_type_id` → batch_type(id)
- **Fields**: `name`, `start_month`, `current_instructor`, `version` (optimistic locking)
- **Indexes**: PRIMARY KEY (id), FOREIGN KEY (batch_type_id), INDEX (batch_type_id), INDEX (start_month)

#### classes
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Fields**: `name`, `date`, `time`, `instructor` (all NOT NULL)
- **Indexes**: PRIMARY KEY (id), INDEX (date)

#### students
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Unique Constraint**: `email` (VARCHAR(255))
- **Foreign Keys**: 
  - `batch_id` → batches(id) [NULLABLE]
  - `buddy_id` → students(id) [NULLABLE, self-reference]
- **Fields**: `name`, `email`, `graduation_year`, `university_name`, `phone_number`, `version` (optimistic locking)
- **Indexes**: PRIMARY KEY (id), UNIQUE INDEX (email), FOREIGN KEY (batch_id), FOREIGN KEY (buddy_id), INDEX (batch_id), INDEX (email)

#### mentors
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Fields**: `name`, `current_company`
- **Indexes**: PRIMARY KEY (id)

#### mentor_sessions
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Foreign Keys**: 
  - `student_id` → students(id) [NOT NULL]
  - `mentor_id` → mentors(id) [NOT NULL]
- **Fields**: `time`, `duration_minutes`, `student_rating` (1-5), `mentor_rating` (1-5), `version` (optimistic locking)
- **Indexes**: PRIMARY KEY (id), FOREIGN KEY (student_id), FOREIGN KEY (mentor_id), INDEX (student_id), INDEX (mentor_id), INDEX (time)

#### users
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Unique Constraint**: `username` (VARCHAR(255))
- **Fields**: 
  - `username` (VARCHAR(255), NOT NULL, UNIQUE) - Email address used for login
  - `password` (VARCHAR(255), NOT NULL) - BCrypt hashed password
  - `role` (VARCHAR(50), NOT NULL, DEFAULT 'ROLE_USER') - User role for authorization
  - `enabled` (BOOLEAN, NOT NULL, DEFAULT TRUE) - Account activation status
- **Indexes**: PRIMARY KEY (id), UNIQUE INDEX (username), INDEX (username)
- **Default Users**: 
  - `admin@academy.com` / `password123` (BCrypt encoded)
  - `user@academy.com` / `password123` (BCrypt encoded)

#### batches_classes (Join Table)
- **Composite Primary Key**: (`batch_id`, `class_id`)
- **Foreign Keys**: 
  - `batch_id` → batches(id) ON DELETE CASCADE
  - `class_id` → classes(id) ON DELETE CASCADE
- **Indexes**: PRIMARY KEY (batch_id, class_id)

#### student_batch_history
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Foreign Keys**: 
  - `student_id` → students(id) [NOT NULL]
  - `batch_id` → batches(id) [NOT NULL]
- **Fields**: `shift_date` (DATE, NOT NULL)
- **Indexes**: PRIMARY KEY (id), FOREIGN KEY (student_id), FOREIGN KEY (batch_id), INDEX (student_id), INDEX (batch_id), INDEX (shift_date)

#### audit_events
- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Fields**: `event_type` (VARCHAR(255)), `payload` (TEXT, JSON format), `created_at` (TIMESTAMP, DEFAULT NOW())
- **Indexes**: PRIMARY KEY (id), INDEX (event_type), INDEX (created_at)

### Key Relationships

| Relationship | Type | Description |
|-------------|------|-------------|
| `BatchType` → `Batch` | One-to-Many | One batch type can have many batches |
| `Batch` → `Student` | One-to-Many | One batch can contain many students |
| `Batch` ↔ `ClassEntity` | Many-to-Many | Batches can have multiple classes, classes can belong to multiple batches (via `batches_classes`) |
| `Student` → `MentorSession` | One-to-Many | One student can book many mentor sessions |
| `Mentor` → `MentorSession` | One-to-Many | One mentor can conduct many sessions |
| `Student` → `StudentBatchHistory` | One-to-Many | One student can have multiple batch assignment records |
| `Student` → `Student` | Self-Reference | Buddy system (one-to-one via `buddy_id`) |

### Constraints & Features

#### Primary Keys
- All tables have `id` as PRIMARY KEY with AUTO_INCREMENT

#### Unique Constraints
- `students.email` - Prevents duplicate email registrations
- `batch_type.name` - Ensures unique batch type names
- `users.username` - Prevents duplicate usernames (email addresses)

#### Foreign Key Constraints
- All foreign keys are indexed for join performance
- Cascade delete enabled on `batches_classes` join table
- Self-referential foreign key on `students.buddy_id`

#### Optimistic Locking
- `version` column in `batches`, `students`, and `mentor_sessions` tables
- Prevents lost updates in concurrent scenarios
- Automatically incremented by Hibernate on updates

#### Indexes Strategy
- **Foreign Key Indexes**: All foreign key columns are indexed
- **Frequently Queried Columns**: `email`, `date`, `start_month`, `shift_date`, `time`
- **Composite Indexes**: `batches_classes(batch_id, class_id)` as composite primary key

### Sample Data

The migration script (`V1__init.sql`) includes sample data:
- **3 batch types**: Full Stack Development, Data Science, DevOps
- **2 batches**: FSD-2024-01, DS-2024-01
- **3 classes**: Introduction to Java, Advanced Java, Database Design
- **4 students**: Sample student records with batch assignments
- **2 mentors**: Sample mentor records
- **2 mentor sessions**: Sample booking records

### ERD Diagram

**Enhanced ERD Diagram**: `puml/enhanced-erd.puml`

This comprehensive ERD diagram shows:
- All tables with complete field definitions
- Data types and constraints (NOT NULL, UNIQUE, etc.)
- Primary keys and foreign keys
- Indexes and their purposes
- Relationship cardinalities
- Optimistic locking columns
- Notes explaining key features

**How to View/Edit in Draw.io:**
1. **Open Draw.io**: Go to https://app.diagrams.net/ or use desktop app
2. **Import PlantUML**: 
   - File → Import → PlantUML
   - Select `puml/enhanced-erd.puml`
   - Draw.io will automatically convert PlantUML to visual diagram
3. **Alternative Method**:
   - Use PlantUML online viewer: http://www.plantuml.com/plantuml/uml/
   - Export as PNG/SVG
   - Import image into Draw.io
4. **Edit in Draw.io**: 
   - Modify colors, shapes, and layout
   - Add annotations or additional details
   - Export as PNG, PDF, or SVG for documentation

**Original ERD**: `puml/erd.puml` (simpler version)

## Feature Development Process

### Key Feature: "Book a Mentor Session"

This section details the implementation of the mentor session booking feature, which demonstrates the complete flow from API request to event publication.

#### 1. API Request

**Endpoint**: `POST /api/mentor-sessions`

**Request Body**:
```json
{
  "time": "2024-01-15T15:00:00Z",
  "durationMinutes": 60,
  "studentId": 1,
  "mentorId": 1
}
```

#### 2. Controller Layer

The `MentorSessionController` receives the request:

```java
@PostMapping
public ResponseEntity<MentorSessionDTO> createSession(
    @Valid @RequestBody MentorSessionDTO dto) {
    MentorSessionDTO created = sessionService.createSession(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

- Validates the request using JSR-380 annotations
- Delegates to service layer
- Returns HTTP 201 Created with the created session

#### 3. Service Layer

The `MentorSessionService.createSession()` method:

1. **Validates entities**: Fetches and validates student and mentor exist
2. **Creates session**: Maps DTO to entity and sets relationships
3. **Saves to database**: Persists the session via repository
4. **Publishes event**: Triggers Kafka event producer

```java
@Transactional
public MentorSessionDTO createSession(MentorSessionDTO dto) {
    // Validate student and mentor
    Student student = studentRepository.findById(dto.getStudentId())
        .orElseThrow(() -> new RuntimeException("Student not found"));
    Mentor mentor = mentorRepository.findById(dto.getMentorId())
        .orElseThrow(() -> new RuntimeException("Mentor not found"));
    
    // Create and save session
    MentorSession session = sessionMapper.toEntity(dto);
    session.setStudent(student);
    session.setMentor(mentor);
    MentorSession saved = sessionRepository.save(session);
    
    // Publish Kafka event
    eventProducer.publishSessionCreatedEvent(saved);
    
    return sessionMapper.toDTO(saved);
}
```

#### 4. Repository Layer

Spring Data JPA repository handles database operations:
- `MentorSessionRepository.save()` persists the entity
- Transaction management ensures atomicity

#### 5. Kafka Event Publication

The `MentorSessionEventProducer` publishes the event:

```java
public void publishSessionCreatedEvent(MentorSession session) {
    EventDTO event = new EventDTO();
    event.setEventType("mentor.session.created");
    event.setTimestamp(Instant.now());
    event.setPayload(/* session data */);
    
    kafkaTemplate.send(KafkaConfig.MENTOR_SESSION_CREATED_TOPIC, event);
}
```

#### 6. Event Consumption

The `EventConsumer` listens to the topic and stores the event in the `audit_events` table for audit purposes.

#### Flow Diagram

```
API Request → Controller → Service → Repository → Database
                                    ↓
                              Kafka Producer → Kafka Topic
                                    ↓
                              Event Consumer → Audit Table
```

## Authentication & User Management

### User Entity & Database

The system implements JWT-based authentication using a `users` table to store user credentials.

#### User Entity Structure

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;  // Email address
    
    @Column(nullable = false)
    private String password;   // BCrypt hashed
    
    @Column(nullable = false)
    private String role = "ROLE_USER";  // Default role
    
    @Column(nullable = false)
    private Boolean enabled = true;    // Account status
}
```

#### Database Table: users

- **Primary Key**: `id` (BIGINT, AUTO_INCREMENT)
- **Unique Constraint**: `username` (email address)
- **Fields**:
  - `username`: VARCHAR(255), NOT NULL, UNIQUE - Email used for login
  - `password`: VARCHAR(255), NOT NULL - BCrypt hashed password
  - `role`: VARCHAR(50), NOT NULL, DEFAULT 'ROLE_USER' - User role for authorization
  - `enabled`: BOOLEAN, NOT NULL, DEFAULT TRUE - Account activation status
- **Indexes**: PRIMARY KEY (id), UNIQUE INDEX (username), INDEX (username)

#### Default Users

The system includes two default users (created via Flyway migration `V3__create_users_table.sql`):

1. **admin@academy.com** / password: `password123`
2. **user@academy.com** / password: `password123`

Passwords are stored as BCrypt hashed values for security.

### Authentication Flow

#### 1. Login Process

**Endpoint**: `POST /api/auth/login`

**Request**:
```json
{
  "username": "admin@academy.com",
  "password": "password123"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

#### 2. Authentication Components

**AuthController** (`com.academy.controller.AuthController`):
- Implements OpenAPI-generated `AuthenticationApi` interface
- Handles login and token validation requests
- Uses `AuthenticationManager` for credential validation
- Uses `JwtService` for token generation

**UserDetailsServiceImpl** (`com.academy.security.UserDetailsServiceImpl`):
- Implements Spring Security's `UserDetailsService`
- Loads user from database via `UserRepository`
- Converts `User` entity to Spring Security `UserDetails`
- Handles disabled user accounts

**JwtService** (`com.academy.security.JwtService`):
- Generates JWT tokens with username and roles
- Validates token expiration and signature
- Extracts claims (username, roles) from tokens
- Configurable secret key and expiration time

**JwtAuthenticationFilter** (`com.academy.security.JwtAuthenticationFilter`):
- Extends `OncePerRequestFilter` (Spring Security)
- Intercepts HTTP requests to extract JWT tokens
- Validates tokens using `JwtService`
- Sets authentication in `SecurityContext` for authorized requests
- Skips authentication for `/api/auth/**` endpoints

#### 3. Authentication Flow Diagram

```
Client Request (with JWT token)
    │
    ▼
JwtAuthenticationFilter
    │
    ├──► Extract token from Authorization header
    │
    ├──► Validate token (JwtService)
    │
    ├──► Extract username and roles
    │
    └──► Set Authentication in SecurityContext
            │
            ▼
        Controller (authenticated request)
```

#### 4. Login Flow Diagram

```
Client Login Request
    │
    ▼
AuthController.login()
    │
    ├──► Authenticate credentials (AuthenticationManager)
    │       │
    │       ▼
    │   UserDetailsServiceImpl
    │       │
    │       ├──► Load user from database (UserRepository)
    │       │
    │       └──► Return UserDetails
    │
    ├──► Generate JWT token (JwtService)
    │       │
    │       ├──► Create claims (username, roles)
    │       │
    │       └──► Sign with secret key
    │
    └──► Return token to client
```

### Security Configuration

**Spring Security Filter Chain**:
- `/api/auth/**` - Public endpoints (no authentication required)
- `/swagger-ui/**`, `/api-docs/**` - Public (API documentation)
- All other endpoints - Require JWT authentication

**JWT Configuration**:
- **Secret Key**: Configurable via `jwt.secret` property (default: 256-bit key)
- **Expiration**: Configurable via `jwt.expiration` property (default: 24 hours)
- **Header Name**: Configurable via `jwt.header.name` (default: `Authorization`)
- **Header Prefix**: Configurable via `jwt.header.prefix` (default: `Bearer `)

### User Repository

**UserRepository** (`com.academy.repository.UserRepository`):
- Extends `JpaRepository<User, Long>`
- Custom methods:
  - `findByUsername(String username)`: Find user by email/username
  - `existsByUsername(String username)`: Check if user exists

### Token Validation

**Endpoint**: `POST /api/auth/validate`

**Request**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response**:
```json
{
  "valid": true,
  "username": "admin@academy.com"
}
```

### Security Features

1. **Password Hashing**: BCrypt with salt (one-way hashing)
2. **JWT Tokens**: Stateless authentication (no server-side session storage)
3. **Token Expiration**: Automatic expiration after configured time
4. **Role-Based Access**: Token includes user roles for future authorization
5. **Account Status**: `enabled` field allows account deactivation
6. **Input Validation**: JSR-380 validation on login requests

### Future Enhancements

- Refresh token mechanism for extended sessions
- Role-based access control (RBAC) implementation
- Password reset functionality
- Account lockout after failed login attempts
- Multi-factor authentication (MFA)
- OAuth2 integration for social login

## Deployment Flow

### Local Development

1. **Prerequisites**: Java 21, Maven, Docker
2. **Start Infrastructure**: `docker-compose -f docker-compose.dev.yml up -d`
3. **Run Application**: `mvn spring-boot:run`
4. **Access**: http://localhost:8080

### Docker Deployment

The application is containerized using a multi-stage Docker build:

1. **Build Stage**: Compiles the application using Maven
2. **Runtime Stage**: Creates lightweight JRE image with the JAR

**Docker Compose** orchestrates:
- MySQL 8.0 database
- Zookeeper and Kafka
- Academy Backend application

**Deployment Steps**:
```bash
docker-compose up --build
```

### Production Deployment

#### Option 1: Docker Compose (Small Scale)

- Use `docker-compose.yml` with production configurations
- Configure environment variables for database and Kafka
- Set up reverse proxy (Nginx) for load balancing

#### Option 2: Kubernetes (Large Scale)

- Deploy MySQL as StatefulSet or use managed RDS
- Deploy Kafka cluster using Strimzi operator
- Deploy application as Deployment with ConfigMaps and Secrets
- Use Ingress for external access

#### Option 3: Cloud Services

- **Database**: AWS RDS MySQL or Azure Database for MySQL
- **Kafka**: AWS MSK or Confluent Cloud
- **Application**: AWS ECS/EKS, Azure Container Instances, or GCP Cloud Run

### Security Considerations

1. **Database**: Use managed database services with encryption at rest
2. **Network**: Configure security groups to restrict access
3. **Secrets**: Use secret management (AWS Secrets Manager, Azure Key Vault)
4. **Authentication**: Implement OAuth2/JWT as per `SecurityConfig` comments
5. **HTTPS**: Terminate SSL at load balancer or ingress

### Monitoring

- **Health Checks**: `/actuator/health`
- **Metrics**: `/actuator/metrics` and `/actuator/prometheus`
- **Logging**: Centralized logging (ELK stack, CloudWatch, etc.)

## Technologies Used

### Core Framework
- **Java 21**: Latest LTS version with modern language features
- **Spring Boot 3.2.0**: Application framework providing auto-configuration and production-ready features
- **Spring Data JPA**: Simplifies database access with repository pattern
- **Spring Kafka**: Integration with Apache Kafka for event-driven architecture

### Database
- **MySQL 8.0**: Relational database with ACID compliance
- **Flyway**: Database migration tool for version-controlled schema changes
- **Hibernate**: JPA implementation for ORM

### Messaging
- **Apache Kafka**: Distributed event streaming platform
- **Zookeeper**: Coordination service for Kafka

### Mapping & Utilities
- **MapStruct**: Compile-time code generation for DTO mapping
- **Lombok**: Reduces boilerplate code

### API Documentation
- **Springdoc OpenAPI**: Generates OpenAPI 3.0 documentation
- **Swagger UI**: Interactive API documentation interface

### Testing
- **JUnit 5**: Unit and integration testing framework
- **Mockito**: Mocking framework for unit tests
- **Testcontainers**: Integration testing with real databases and Kafka

### DevOps
- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration
- **GitHub Actions**: CI/CD pipeline

### Monitoring
- **Spring Boot Actuator**: Production-ready monitoring and metrics

## Conclusion

### Key Takeaways

This project provided comprehensive hands-on experience with modern backend development practices and enterprise-level technologies. The following key concepts and technologies were learned and implemented:

#### 1. **Multi-Module Architecture & Design Patterns**
- **Learned**: Separation of concerns through modular architecture (API, Service, Common, Kafka modules)
- **Concepts**: Repository Pattern, Service Layer Pattern, DTO Pattern, Factory Pattern, Aspect-Oriented Programming
- **Benefits**: Independent deployment, clear boundaries, improved testability, easier maintenance
- **Application**: Enables microservices migration, team collaboration, and code reusability

#### 2. **Spring Boot Framework & Dependency Injection**
- **Learned**: Spring Boot auto-configuration, dependency injection, and IoC container
- **Concepts**: Bean lifecycle, configuration properties, profile-based configuration, actuator endpoints
- **Benefits**: Rapid development, production-ready features, minimal boilerplate code
- **Application**: Industry standard for Java enterprise applications, used by Netflix, Amazon, and many Fortune 500 companies

#### 3. **RESTful API Design & OpenAPI Specification**
- **Learned**: REST principles, HTTP methods, status codes, API versioning, OpenAPI-driven development
- **Concepts**: Contract-first API design, code generation, API documentation, client generation
- **Benefits**: Type-safe APIs, automatic documentation, frontend-backend decoupling
- **Application**: Standard approach for microservices communication, mobile app backends, and public APIs

#### 4. **Database Design & ORM (JPA/Hibernate)**
- **Learned**: Entity relationships, JPA annotations, optimistic/pessimistic locking, query optimization
- **Concepts**: ACID properties, transactions, lazy/eager loading, cascade operations, database migrations
- **Benefits**: Database abstraction, type safety, automatic SQL generation, schema versioning
- **Application**: Used in enterprise applications for data persistence, reducing SQL boilerplate

#### 5. **Event-Driven Architecture with Apache Kafka**
- **Learned**: Message brokers, topics, partitions, producers, consumers, event sourcing patterns
- **Concepts**: Asynchronous messaging, decoupled systems, eventual consistency, event replay
- **Benefits**: Scalability, fault tolerance, loose coupling, audit trail
- **Application**: Real-time data processing, microservices communication, event streaming (Netflix, LinkedIn, Uber)

#### 6. **Caching Strategy with Redis**
- **Learned**: In-memory caching, TTL strategies, cache invalidation, cache-aside pattern
- **Concepts**: Cache hit/miss ratios, cache warming, distributed caching, cache coherence
- **Benefits**: Reduced database load, improved response times, better scalability
- **Application**: High-traffic websites, session management, real-time leaderboards, rate limiting

#### 7. **Distributed Systems & Concurrency Control**
- **Learned**: Distributed locking, race conditions, optimistic locking, version control
- **Concepts**: CAP theorem, consistency models, deadlock prevention, concurrent access patterns
- **Benefits**: Data integrity, preventing duplicate operations, handling concurrent requests
- **Application**: E-commerce systems, booking systems, financial transactions, inventory management

#### 8. **Security & Authentication (JWT)**
- **Learned**: JWT token generation/validation, Spring Security, password hashing (BCrypt), stateless authentication
- **Concepts**: Token-based auth, claims, expiration, signature verification, role-based access
- **Benefits**: Stateless scalability, cross-domain support, mobile-friendly, no session storage
- **Application**: Single Sign-On (SSO), API security, mobile app authentication, microservices security

#### 9. **Containerization & DevOps**
- **Learned**: Docker, Docker Compose, multi-stage builds, container orchestration
- **Concepts**: Immutable infrastructure, environment parity, service discovery, health checks
- **Benefits**: Consistent deployments, easy scaling, simplified dependency management
- **Application**: Cloud-native applications, CI/CD pipelines, Kubernetes deployments, serverless functions

#### 10. **Testing Strategies**
- **Learned**: Unit testing, integration testing, Testcontainers, mocking, test coverage
- **Concepts**: Test-driven development (TDD), test isolation, test doubles, contract testing
- **Benefits**: Code quality assurance, regression prevention, documentation, refactoring confidence
- **Application**: Continuous integration, quality gates, automated testing pipelines

#### 11. **Database Migrations with Flyway**
- **Learned**: Version-controlled schema changes, migration scripts, rollback strategies
- **Concepts**: Database versioning, forward-only migrations, baseline migrations
- **Benefits**: Consistent schemas across environments, automated deployments, audit trail
- **Application**: Production database management, team collaboration, deployment automation

#### 12. **API Documentation & Code Generation**
- **Learned**: OpenAPI specification, Swagger UI, code generation from specs
- **Concepts**: Contract-first development, API versioning, client SDK generation
- **Benefits**: Automatic documentation, type safety, frontend-backend synchronization
- **Application**: API-first development, developer portals, SDK distribution

### Practical Applications

The technologies and patterns implemented in this project have significant real-world applications across various industries:

#### 1. **E-Commerce Platforms**
- **Event-Driven Architecture**: Order processing, inventory updates, payment notifications
- **Caching**: Product catalogs, user sessions, shopping carts
- **Distributed Locking**: Inventory management, preventing overselling
- **Real-World Example**: Amazon, eBay, Shopify

#### 2. **Financial Services**
- **Transaction Management**: ACID properties for money transfers, account updates
- **Event Sourcing**: Audit trails for regulatory compliance, transaction history
- **Security**: JWT for secure API access, role-based permissions
- **Real-World Example**: Banking APIs, payment gateways, trading platforms

#### 3. **Healthcare Systems**
- **Patient Management**: Similar to student management in this project
- **Appointment Booking**: Similar to mentor session booking
- **Event-Driven**: Patient registration events, appointment reminders
- **Real-World Example**: Hospital management systems, telemedicine platforms

#### 4. **Education Technology (EdTech)**
- **Student Management**: Enrollment, course assignment, progress tracking
- **Batch/Class Management**: Course scheduling, instructor assignment
- **Mentor Sessions**: Tutoring bookings, virtual classrooms
- **Real-World Example**: Coursera, Udemy, Khan Academy backends

#### 5. **SaaS Platforms**
- **Multi-Tenancy**: Similar modular architecture supports multiple customers
- **API-First Design**: Third-party integrations, webhooks
- **Scalability**: Horizontal scaling for growing user base
- **Real-World Example**: Salesforce, Slack, Zoom

#### 6. **Social Media Platforms**
- **Event Streaming**: Real-time feeds, notifications, activity streams
- **Caching**: User profiles, timelines, trending content
- **Microservices**: Independent services for posts, comments, messaging
- **Real-World Example**: Twitter, LinkedIn, Facebook

#### 7. **IoT & Real-Time Systems**
- **Kafka Streaming**: Processing sensor data, device events
- **Event-Driven**: Real-time alerts, automated responses
- **Scalability**: Handling millions of device connections
- **Real-World Example**: Smart cities, industrial automation, connected vehicles

#### 8. **Content Management Systems**
- **CRUD Operations**: Content creation, editing, publishing
- **Search**: Full-text search (future enhancement)
- **Caching**: Frequently accessed content, CDN integration
- **Real-World Example**: WordPress, Drupal, Contentful

### Limitations

While the implemented technologies are powerful, they come with limitations, cost implications, and areas for improvement:

#### 1. **Technology Limitations**

##### **Spring Boot & Java**
- **Limitations**: 
  - Memory consumption (JVM overhead)
  - Cold start times (slower than native languages)
  - Verbose code compared to modern languages (Kotlin, Go)
- **Cost Implications**: 
  - Higher infrastructure costs due to memory requirements
  - Longer startup times in serverless environments
- **Suggestions for Improvement**:
  - Use GraalVM for native compilation (faster startup, lower memory)
  - Consider Kotlin for more concise code
  - Implement connection pooling optimization
  - Use reactive programming (Spring WebFlux) for better resource utilization

##### **MySQL Database**
- **Limitations**:
  - Vertical scaling limitations (single server)
  - Complex queries can be slow on large datasets
  - Limited support for JSON/document queries
  - Replication lag in read replicas
- **Cost Implications**:
  - Managed database services (AWS RDS, Azure SQL) add 30-50% cost
  - Scaling requires larger instance sizes (expensive)
  - Backup storage costs increase with data volume
- **Suggestions for Improvement**:
  - Implement read replicas for scaling reads
  - Use connection pooling (HikariCP) efficiently
  - Consider PostgreSQL for better JSON support
  - Implement database sharding for horizontal scaling
  - Use database indexing strategy optimization

##### **Apache Kafka**
- **Limitations**:
  - Complex setup and configuration
  - Requires Zookeeper (additional dependency)
  - Message ordering only within partitions
  - No built-in message transformation
  - High memory and disk requirements
- **Cost Implications**:
  - Managed Kafka (AWS MSK, Confluent Cloud) is expensive ($200-1000+/month)
  - Self-hosted requires dedicated infrastructure
  - Storage costs for message retention
- **Suggestions for Improvement**:
  - Use Kafka Streams for message transformation
  - Implement proper partition strategy
  - Consider alternatives (RabbitMQ, AWS SQS) for simpler use cases
  - Use schema registry (Confluent Schema Registry) for message validation
  - Implement proper retention policies to control storage costs

##### **Redis Caching**
- **Limitations**:
  - Memory-only storage (data loss on restart without persistence)
  - Single-threaded (CPU-bound operations can block)
  - Limited query capabilities (no complex queries like SQL)
  - Memory costs increase with cache size
- **Cost Implications**:
  - Managed Redis (AWS ElastiCache, Azure Cache) costs $50-500+/month
  - Memory is expensive compared to disk storage
  - Need to size appropriately to avoid evictions
- **Suggestions for Improvement**:
  - Implement Redis Cluster for high availability
  - Use Redis persistence (AOF/RDB) for data durability
  - Implement cache warming strategies
  - Monitor cache hit ratios and adjust TTLs
  - Consider multi-tier caching (L1: local, L2: Redis)

##### **JWT Authentication**
- **Limitations**:
  - Token size (can be large with many claims)
  - No built-in revocation (tokens valid until expiration)
  - Stateless nature makes logout challenging
  - Secret key management complexity
- **Cost Implications**:
  - Minimal (stateless, no session storage)
  - Key management services (AWS KMS, Azure Key Vault) add cost
- **Suggestions for Improvement**:
  - Implement refresh tokens for better security
  - Use token blacklisting (Redis) for logout
  - Rotate secret keys regularly
  - Use asymmetric keys (RS256) for better security
  - Implement token compression for large payloads

##### **Docker & Containerization**
- **Limitations**:
  - Container overhead (slight performance penalty)
  - Image sizes can be large
  - Security concerns (container escape, image vulnerabilities)
  - Orchestration complexity (Kubernetes learning curve)
- **Cost Implications**:
  - Container registry costs (Docker Hub, AWS ECR)
  - Kubernetes cluster management overhead
  - Monitoring and logging tools add costs
- **Suggestions for Improvement**:
  - Use multi-stage builds to reduce image size
  - Implement image scanning for vulnerabilities
  - Use container orchestration (Kubernetes) for production
  - Implement proper resource limits
  - Use container registries with vulnerability scanning

#### 2. **Architecture Limitations**

##### **Monolithic Structure (Current)**
- **Limitations**:
  - Single deployment unit (all-or-nothing deployments)
  - Technology lock-in (all modules use Java/Spring)
  - Scaling requires scaling entire application
  - Single point of failure
- **Cost Implications**:
  - Over-provisioning resources for peak loads
  - Cannot optimize individual services
- **Suggestions for Improvement**:
  - Migrate to microservices architecture
  - Implement service mesh (Istio, Linkerd) for communication
  - Use API Gateway for routing and load balancing
  - Implement circuit breakers for fault tolerance

##### **Database as Single Source of Truth**
- **Limitations**:
  - Database becomes bottleneck
  - Difficult to scale reads
  - Complex transactions across services
- **Cost Implications**:
  - Database instance costs increase with load
  - Backup and replication costs
- **Suggestions for Improvement**:
  - Implement CQRS (Command Query Responsibility Segregation)
  - Use read replicas for scaling reads
  - Consider event sourcing for audit and replay
  - Implement database sharding

#### 3. **Feature Limitations**

##### **No Full-Text Search**
- **Limitation**: Cannot search across student names, emails, or other text fields efficiently
- **Cost Implication**: Database full-text search is limited; Elasticsearch adds $100-500+/month
- **Suggestion**: Implement Elasticsearch or use PostgreSQL full-text search

##### **No File Storage**
- **Limitation**: Cannot upload student documents, profile pictures, or attachments
- **Cost Implication**: Object storage (S3, Azure Blob) costs $0.023/GB/month
- **Suggestion**: Integrate AWS S3 or Azure Blob Storage for file uploads

##### **No Real-Time Notifications**
- **Limitation**: Users must poll for updates; no push notifications
- **Cost Implication**: WebSocket infrastructure, push notification services (Firebase, AWS SNS)
- **Suggestion**: Implement WebSockets or Server-Sent Events (SSE) for real-time updates

##### **Limited Monitoring**
- **Limitation**: Basic health checks only; no distributed tracing or advanced metrics
- **Cost Implication**: APM tools (New Relic, Datadog) cost $100-1000+/month
- **Suggestion**: Implement Prometheus + Grafana (open-source) or use cloud-native monitoring

#### 4. **Cost Optimization Strategies**

1. **Infrastructure Costs**:
   - Use reserved instances (30-50% savings)
   - Implement auto-scaling to reduce idle resources
   - Use spot instances for non-critical workloads
   - Optimize container resource requests/limits

2. **Database Costs**:
   - Implement connection pooling to reduce connection count
   - Use read replicas instead of scaling primary database
   - Archive old data to cheaper storage (S3 Glacier)
   - Optimize queries to reduce database load

3. **Caching Costs**:
   - Implement cache warming to reduce cache misses
   - Use appropriate TTLs to balance freshness and cost
   - Monitor cache hit ratios and adjust strategies
   - Consider CDN for static content

4. **Development Costs**:
   - Use open-source alternatives where possible
   - Implement CI/CD to reduce manual deployment costs
   - Use infrastructure as code (Terraform) for cost visibility
   - Regular cost audits and optimization reviews

### Future Work & Recommendations

Based on the limitations identified, the following improvements are recommended:

1. **Performance Optimization**
   - Implement database read replicas
   - Add CDN for static assets
   - Optimize JPA queries (N+1 problem prevention)
   - Implement connection pooling tuning

2. **Scalability Enhancements**
   - Migrate to microservices architecture
   - Implement API Gateway (Kong, AWS API Gateway)
   - Add horizontal auto-scaling
   - Implement database sharding

3. **Feature Additions**
   - Full-text search with Elasticsearch
   - File storage with S3/Azure Blob
   - Real-time notifications with WebSockets
   - Email/SMS notifications

4. **Monitoring & Observability**
   - Distributed tracing (Jaeger/Zipkin)
   - Log aggregation (ELK stack)
   - Performance monitoring (Prometheus + Grafana)
   - Cost monitoring and alerts

5. **Security Enhancements**
   - Refresh token mechanism
   - OAuth2 integration
   - API rate limiting
   - Security scanning in CI/CD pipeline

6. **Testing Improvements**
   - Increase test coverage to >90%
   - End-to-end testing
   - Performance testing
   - Chaos engineering for resilience

## References

1. Spring Boot Documentation: https://spring.io/projects/spring-boot
2. Apache Kafka Documentation: https://kafka.apache.org/documentation/
3. MySQL Documentation: https://dev.mysql.com/doc/
4. Flyway Documentation: https://flywaydb.org/documentation/
5. MapStruct Documentation: https://mapstruct.org/documentation/stable/reference/html/
6. Testcontainers Documentation: https://www.testcontainers.org/
7. Docker Documentation: https://docs.docker.com/
8. OpenAPI Specification: https://swagger.io/specification/

*Note: Replace with actual URLs if used in production documentation.*

---

## Appendix: Metrics Dashboard

### Sample API Metrics (Placeholder)

- **Average Response Time**: 150ms
- **P95 Response Time**: 250ms
- **P99 Response Time**: 400ms
- **Request Rate**: 1000 requests/minute
- **Error Rate**: 0.1%

### Prometheus + Grafana Setup

To set up a metrics dashboard:

1. Add Prometheus to `docker-compose.yml`:
```yaml
prometheus:
  image: prom/prometheus
  volumes:
    - ./prometheus.yml:/etc/prometheus/prometheus.yml
  ports:
    - "9090:9090"
```

2. Add Grafana:
```yaml
grafana:
  image: grafana/grafana
  ports:
    - "3000:3000"
```

3. Configure Prometheus to scrape `/actuator/prometheus` endpoint
4. Create dashboards in Grafana for API metrics, database metrics, and Kafka metrics

