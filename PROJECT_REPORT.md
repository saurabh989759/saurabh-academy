# Academy Backend System - Project Report

**Project Title:** Academy Management System - Backend Implementation  
**Technology Stack:** Spring Boot 3.2.0, Java 21, MySQL 8.0, Redis, Apache Kafka  
**Date:** November 2024

---

## Table of Contents

1. [Abstract](#abstract)
2. [Project Description](#project-description)
3. [Requirement Gathering](#requirement-gathering)
4. [System Architecture](#system-architecture)
5. [Database Design](#database-design)
6. [Feature Development Process](#feature-development-process)
7. [Technology Stack & Implementation](#technology-stack--implementation)
8. [API Documentation](#api-documentation)
9. [Testing Strategy](#testing-strategy)
10. [Deployment & DevOps](#deployment--devops)
11. [Performance Optimization](#performance-optimization)
12. [Security Implementation](#security-implementation)
13. [Challenges & Solutions](#challenges--solutions)
14. [Conclusion & Future Work](#conclusion--future-work)
15. [References](#references)

---

## Abstract

This project implements a comprehensive, production-ready backend system for managing an academy with students, batches, mentors, classes, and mentor sessions. The system is built using **Spring Boot 3.2.0** with **Java 21**, following a multi-module architecture pattern. It incorporates **MySQL 8.0** for data persistence, **Redis** for caching, and **Apache Kafka** for event-driven messaging. The application demonstrates enterprise-level practices including RESTful API design, database migrations using Flyway, distributed locking, optimistic concurrency control, JWT authentication, Docker containerization, and comprehensive testing strategies.

The system provides a scalable, maintainable, and high-performance solution for academy management with features such as student registration, batch management, mentor session booking, class scheduling, and real-time event processing. The architecture supports horizontal scaling and follows microservices-ready design principles.

---

## Project Description

### Objectives

The Academy Backend system aims to provide:

1. **Student Management**: Complete CRUD operations for student registration, enrollment in batches, and tracking of academic progress with buddy system support
2. **Batch Management**: Creation and management of student batches with associated classes and batch types
3. **Mentor Management**: Tracking mentors and their sessions with students, including bidirectional ratings
4. **Class Scheduling**: Management of classes and their association with multiple batches
5. **Event-Driven Architecture**: Asynchronous event processing using Kafka for domain events (student registration, batch creation, mentor session booking)
6. **Scalable Architecture**: Microservices-ready design with clear separation of concerns, supporting horizontal scaling
7. **Performance Optimization**: Redis caching layer with intelligent TTL strategies for improved response times
8. **Concurrency Control**: Distributed locking and optimistic locking to prevent race conditions

### Relevance

This project demonstrates real-world backend development practices including:

- **RESTful API Design**: OpenAPI-driven API development with comprehensive documentation
- **Database Schema Design**: Proper relationships, constraints, indexes, and migration management
- **Event-Driven Architecture**: Kafka-based messaging for decoupled, scalable systems
- **Caching Strategy**: Redis-based caching with TTL-based expiration for performance
- **Containerization**: Docker and Docker Compose for simplified deployment
- **Automated Testing**: Unit tests, integration tests using Testcontainers
- **Security**: JWT-based authentication and authorization
- **Monitoring**: Spring Boot Actuator for health checks and metrics
- **API Documentation**: Swagger/OpenAPI for interactive API exploration

---

## Requirement Gathering

### Functional Requirements

#### 1. Student Registration
- Register new students with personal and academic information (name, email, graduation year, university, phone)
- Assign students to batches
- Track student batch history (when students move between batches)
- Support buddy system (pairing students for peer support)
- Prevent duplicate email registrations using distributed locking
- Cache student data for improved read performance

#### 2. Batch Management
- Create and manage batches with name, start date, and instructor
- Associate batches with batch types (e.g., Full Stack Development, Data Science)
- Link multiple classes to batches (many-to-many relationship)
- Track batch start dates and current instructors
- Support pagination for batch listings
- Publish `batch.created` Kafka event on batch creation

#### 3. Mentor Session Booking
- Book mentor sessions between students and mentors
- Track session duration and timings
- Support bidirectional ratings (student rates mentor, mentor rates student)
- Validate student and mentor existence before booking
- Publish `mentor.session.created` Kafka event on session creation
- Cache session data with shorter TTL (10 minutes) due to frequent changes

#### 4. Class Management
- Create classes with date, time, and instructor information
- Associate classes with multiple batches (many-to-many)
- Support CRUD operations for classes
- Cache class data for improved performance

#### 5. Event Publishing & Consumption
- Publish `student.registered` event when a student is registered
- Publish `mentor.session.created` event when a session is booked
- Publish `batch.created` event when a batch is created
- Consume events and store in `audit_events` table for audit trail
- Support event replay and historical tracking

### Non-Functional Requirements

#### 1. Performance
- API response time < 200ms for cached read operations
- Support for pagination on list endpoints
- Database query optimization with proper indexes
- Redis caching for frequently accessed data
- Connection pooling for database connections

#### 2. Scalability
- Horizontal scaling capability (stateless API design)
- Kafka-based event processing for async operations
- Distributed locking to prevent race conditions in concurrent scenarios
- Support for multiple application instances

#### 3. Reliability
- Database transaction management (ACID compliance)
- Comprehensive error handling and logging
- Health check endpoints for monitoring
- Graceful degradation when cache is unavailable
- Optimistic locking to prevent lost updates

#### 4. Security
- Input validation using JSR-380 (Bean Validation)
- JWT-based authentication and authorization
- SQL injection prevention via JPA/Hibernate
- Sensitive data obfuscation in logs
- CORS configuration for frontend integration

#### 5. Maintainability
- Layered architecture with clear separation of concerns
- Multi-module structure for independent deployment
- DTOs for API contracts
- Comprehensive test coverage
- API documentation via OpenAPI/Swagger
- Structured JSON logging for log aggregation

---

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client Layer                            │
│              (React Frontend / Mobile Applications)             │
└────────────────────────────┬────────────────────────────────────┘
                              │
                              │ HTTPS/REST
                              │
┌─────────────────────────────▼────────────────────────────────────┐
│                      API Gateway Layer                           │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Spring Security + JWT Authentication Filter              │   │
│  └──────────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  REST Controllers (OpenAPI Generated)                     │   │
│  │  - StudentController                                      │   │
│  │  - BatchController                                        │   │
│  │  - ClassController                                        │   │
│  │  - MentorController                                       │   │
│  │  - MentorSessionController                                │   │
│  │  - AuthController                                         │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────┬────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
┌───────▼────────┐   ┌────────▼────────┐   ┌───────▼────────┐
│  Service Layer │   │  Cache Layer    │   │  Kafka Layer   │
│                │   │                 │   │                │
│  - Student     │   │  Redis Cache    │   │  - Producers   │
│  - Batch       │◄──┤  Manager         │   │  - Consumers   │
│  - Class       │   │  (TTL-based)    │   │                │
│  - Mentor      │   │                 │   │                │
│  - MentorSession│  └─────────────────┘   └────────────────┘
└───────┬────────┘
        │
┌───────▼────────┐
│  Data Layer    │
│                │
│  - JPA Repos   │
│  - Entities    │
│  - Flyway      │
└───────┬────────┘
        │
┌───────▼────────┐
│  Database      │
│  (MySQL 8.0)   │
└────────────────┘
```

### Module Structure

The project follows a **multi-module architecture** for better separation of concerns:

```
academy-backend/
├── modules/
│   ├── academy-api/              # API layer (controllers, security, config)
│   │   ├── Controllers (REST endpoints)
│   │   ├── Security (JWT authentication)
│   │   └── Configuration (Redis, Kafka, OpenAPI)
│   │
│   ├── academy-service/          # Business logic layer
│   │   ├── Services (business logic)
│   │   ├── Mappers (MapStruct DTO mapping)
│   │   ├── Aspects (AOP for locking)
│   │   └── Annotations (custom annotations)
│   │
│   ├── academy-common/           # Shared components
│   │   ├── Entities (JPA entities)
│   │   ├── DTOs (data transfer objects)
│   │   ├── Repositories (JPA repositories)
│   │   └── Exceptions (custom exception hierarchy)
│   │
│   ├── academy-kafka-producer/   # Kafka event producers
│   │   └── Event producers for domain events
│   │
│   └── academy-kafka-consumer/   # Kafka event consumers
│       └── Event consumers for audit trail
```

### Design Patterns

1. **Repository Pattern**: Data access abstraction through Spring Data JPA
2. **Service Layer Pattern**: Business logic separation from controllers
3. **DTO Pattern**: Data transfer objects for API contracts
4. **Factory Pattern**: Object creation through MapStruct mappers
5. **Aspect-Oriented Programming**: Cross-cutting concerns (locking, caching)
6. **Event-Driven Architecture**: Kafka-based messaging for loose coupling
7. **Strategy Pattern**: Cache TTL strategies based on data volatility
8. **Template Method**: Base service methods for common operations

### Key Design Decisions

1. **Multi-Module Architecture**: Enables independent deployment and clear separation of concerns
2. **OpenAPI-Driven APIs**: Single source of truth for API contracts, type safety
3. **Redis Caching**: Performance optimization with TTL-based expiration
4. **Distributed Locking**: Prevents race conditions in concurrent scenarios (e.g., duplicate student registration)
5. **Optimistic Locking**: Database-level concurrency control using `@Version` annotation
6. **Event-Driven**: Loose coupling via Kafka events for scalable architecture
7. **JWT Authentication**: Stateless, scalable authentication mechanism

---

## Database Design

### Entity Relationship Diagram

The database consists of **9 main tables** with the following relationships:

```
┌──────────────┐
│  BatchType   │
│  (1)         │
└──────┬───────┘
       │ 1
       │
       │ Many
       ▼
┌──────────────┐         ┌──────────────┐
│    Batch     │◄────────┤   Student    │
│  (Many)      │    Many │  (Many)      │
└──────┬───────┘         └──────┬───────┘
       │                         │
       │ Many                   │ Many
       │                        │
       ▼                        ▼
┌──────────────┐         ┌──────────────────┐
│    Class     │         │ MentorSession    │
│  (Many)      │         │  (Many)          │
└──────────────┘         └────────┬─────────┘
                                   │
                                   │ Many
                                   │
                                   │ 1
                                   ▼
                            ┌──────────────┐
                            │   Mentor     │
                            │  (Many)      │
                            └──────────────┘
```

### Database Schema

#### Core Tables

1. **batch_type**
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `name` (VARCHAR(255), NOT NULL, UNIQUE)
   - `description` (TEXT)

2. **batches**
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `name` (VARCHAR(255), NOT NULL)
   - `start_month` (DATE)
   - `current_instructor` (VARCHAR(255))
   - `batch_type_id` (BIGINT, FK → batch_type.id)
   - `version` (INT, for optimistic locking)

3. **students**
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `name` (VARCHAR(255), NOT NULL)
   - `email` (VARCHAR(255), UNIQUE, NOT NULL)
   - `graduation_year` (INT)
   - `university_name` (VARCHAR(255))
   - `phone_number` (VARCHAR(50))
   - `batch_id` (BIGINT, FK → batches.id, NULLABLE)
   - `buddy_id` (BIGINT, FK → students.id, NULLABLE, self-reference)
   - `version` (INT, for optimistic locking)

4. **classes**
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `name` (VARCHAR(255), NOT NULL)
   - `date` (DATE, NOT NULL)
   - `time` (TIME, NOT NULL)
   - `instructor` (VARCHAR(255), NOT NULL)

5. **mentors**
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `name` (VARCHAR(255), NOT NULL)
   - `current_company` (VARCHAR(255))

6. **mentor_sessions**
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `time` (DATETIME, NOT NULL)
   - `duration_minutes` (INT, NOT NULL)
   - `student_id` (BIGINT, FK → students.id, NOT NULL)
   - `mentor_id` (BIGINT, FK → mentors.id, NOT NULL)
   - `student_rating` (INT, 1-5, NULLABLE)
   - `mentor_rating` (INT, 1-5, NULLABLE)
   - `version` (INT, for optimistic locking)

7. **batches_classes** (Join Table - Many-to-Many)
   - `batch_id` (BIGINT, FK → batches.id)
   - `class_id` (BIGINT, FK → classes.id)
   - Primary Key: (batch_id, class_id)

8. **audit_events** (Kafka Consumer - Event Storage)
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `event_type` (VARCHAR(255), NOT NULL)
   - `payload` (TEXT, JSON format)
   - `created_at` (TIMESTAMP, NOT NULL)

9. **student_batch_history** (Historical Tracking)
   - `id` (BIGINT, PK, AUTO_INCREMENT)
   - `student_id` (BIGINT, FK → students.id, NOT NULL)
   - `batch_id` (BIGINT, FK → batches.id, NOT NULL)
   - `shift_date` (DATE, NOT NULL)

### Relationships

- **BatchType → Batch**: One-to-Many (one batch type can have many batches)
- **Batch → Student**: One-to-Many (one batch can have many students)
- **Batch ↔ Class**: Many-to-Many (via `batches_classes` join table)
- **Student → MentorSession**: One-to-Many (one student can have many sessions)
- **Mentor → MentorSession**: One-to-Many (one mentor can have many sessions)
- **Student → Student**: Self-reference (buddy system, one-to-one)
- **Student → StudentBatchHistory**: One-to-Many (historical tracking)

### Indexes

**Primary Keys**: All tables have `id` as PRIMARY KEY with AUTO_INCREMENT

**Unique Indexes**:
- `students.email` - UNIQUE constraint (prevents duplicate emails)
- `batch_type.name` - UNIQUE constraint

**Foreign Key Indexes**: All foreign key columns are indexed for join performance

**Composite Indexes**:
- `batches_classes(batch_id, class_id)` - Composite primary key

### Database Migrations

Flyway manages database schema versioning:
- `V1__init.sql` - Initial schema creation with sample data
- `V2__add_version_columns.sql` - Optimistic locking support

---

## Feature Development Process

### Key Feature: "Book a Mentor Session"

This section details the complete implementation flow of the mentor session booking feature, demonstrating the end-to-end process from API request to event publication.

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

**Responsibilities**:
- Validates request using JSR-380 annotations (`@Valid`)
- Delegates to service layer
- Returns HTTP 201 Created with the created session

#### 3. Service Layer

The `MentorSessionService.createSession()` method:

```java
@Transactional
public MentorSessionDTO createSession(MentorSessionDTO dto) {
    // 1. Validate entities
    Student student = studentRepository.findById(dto.getStudentId())
        .orElseThrow(() -> new StudentNotFoundException(dto.getStudentId()));
    Mentor mentor = mentorRepository.findById(dto.getMentorId())
        .orElseThrow(() -> new MentorNotFoundException(dto.getMentorId()));
    
    // 2. Create and save session
    MentorSession session = sessionMapper.toEntity(dto);
    session.setStudent(student);
    session.setMentor(mentor);
    MentorSession saved = sessionRepository.save(session);
    
    // 3. Publish Kafka event
    eventProducer.publishSessionCreatedEvent(saved);
    
    // 4. Return DTO
    return sessionMapper.toDTO(saved);
}
```

**Flow**:
1. **Validates entities**: Fetches and validates student and mentor exist
2. **Creates session**: Maps DTO to entity and sets relationships
3. **Saves to database**: Persists the session via repository (within transaction)
4. **Publishes event**: Triggers Kafka event producer asynchronously
5. **Returns DTO**: Maps entity back to DTO for response

#### 4. Repository Layer

Spring Data JPA repository handles database operations:
- `MentorSessionRepository.save()` persists the entity
- Transaction management ensures atomicity
- Optimistic locking prevents concurrent modifications

#### 5. Kafka Event Publication

The `MentorSessionEventProducer` publishes the event:

```java
public void publishSessionCreatedEvent(MentorSession session) {
    EventDTO event = new EventDTO();
    event.setEventType("mentor.session.created");
    event.setTimestamp(Instant.now());
    event.setPayload(/* session data as JSON */);
    
    kafkaTemplate.send(KafkaConfig.MENTOR_SESSION_CREATED_TOPIC, event);
}
```

**Kafka Topic**: `mentor.session.created`
- **Partitions**: 3
- **Replication Factor**: 1 (single broker setup)

#### 6. Event Consumption

The `EventConsumer` listens to the topic and stores the event:

```java
@KafkaListener(topics = KafkaConfig.MENTOR_SESSION_CREATED_TOPIC, 
               groupId = "academy-backend-group")
public void consumeMentorSessionCreated(EventDTO event) {
    log.info("Consumed mentor.session.created event: {}", event);
    saveAuditEvent(event);
}
```

**Audit Trail**: Events are stored in `audit_events` table for:
- Compliance and auditing
- Event replay capability
- Historical tracking

#### Complete Flow Diagram

```
API Request (POST /api/mentor-sessions)
    │
    ▼
Controller (Validates request)
    │
    ▼
Service Layer
    │
    ├──► Validate Student & Mentor
    │
    ├──► Create Session Entity
    │
    ├──► Save to Database (Transaction)
    │
    └──► Publish Kafka Event
            │
            ▼
        Kafka Topic (mentor.session.created)
            │
            ▼
        Event Consumer
            │
            ▼
        Audit Events Table
```

### Additional Features

#### Student Registration with Distributed Locking

**Challenge**: Prevent duplicate student registrations with the same email in concurrent scenarios.

**Solution**: Implemented distributed locking using Redis:

```java
@WithLock(key = "student:onboarding:#{#dto.email}", timeout = 30, maxRetries = 3)
@Transactional
public StudentDTO createStudent(StudentDTO dto) {
    // Check if student with email already exists
    studentRepository.findByEmail(dto.getEmail())
        .ifPresent(existing -> {
            throw new IllegalStateException("Student with email already exists");
        });
    
    // Create and save student
    Student student = studentMapper.toEntity(dto);
    Student saved = studentRepository.save(student);
    
    // Publish Kafka event
    eventProducer.publishStudentRegisteredEvent(saved);
    
    return studentMapper.toDTO(saved);
}
```

**Lock Key**: `student:onboarding:{email}` ensures only one registration per email at a time.

---

## Technology Stack & Implementation

### Core Framework

- **Java 21**: Latest LTS version with modern language features (records, pattern matching, virtual threads)
- **Spring Boot 3.2.0**: Application framework providing auto-configuration and production-ready features
- **Spring Data JPA**: Simplifies database access with repository pattern
- **Spring Data Redis**: Caching abstraction with Redis implementation
- **Spring Kafka**: Integration with Apache Kafka for event-driven architecture
- **Spring Security**: Authentication and authorization framework

### Database & Persistence

- **MySQL 8.0**: Relational database with ACID compliance
- **Flyway**: Database migration tool for version-controlled schema changes
- **Hibernate**: JPA implementation for ORM (Object-Relational Mapping)
- **Connection Pooling**: HikariCP for efficient database connections

### Messaging & Events

- **Apache Kafka 7.5.0**: Distributed event streaming platform
- **Zookeeper**: Coordination service for Kafka cluster management
- **Spring Kafka**: High-level abstraction for Kafka integration

### Caching

- **Redis 7**: In-memory data structure store
- **Spring Cache**: Abstraction layer for caching
- **JSON Serialization**: Jackson for cache value serialization

### Mapping & Utilities

- **MapStruct**: Compile-time code generation for DTO mapping (type-safe, no reflection)
- **Lombok**: Reduces boilerplate code (getters, setters, constructors)

### API Documentation

- **Springdoc OpenAPI 3**: Generates OpenAPI 3.0 specification
- **Swagger UI**: Interactive API documentation interface
- **OpenAPI Generator**: Generates TypeScript client for frontend

### Testing

- **JUnit 5**: Unit and integration testing framework
- **Mockito**: Mocking framework for unit tests
- **Testcontainers**: Integration testing with real databases and Kafka
- **Spring Boot Test**: Testing utilities and annotations

### DevOps & Deployment

- **Docker**: Containerization for consistent environments
- **Docker Compose**: Multi-container orchestration
- **Gradle**: Build automation and dependency management
- **Git**: Version control

### Monitoring & Observability

- **Spring Boot Actuator**: Production-ready monitoring and metrics
- **Logback**: Logging framework with JSON formatting
- **Logbook**: HTTP request/response logging with sensitive data obfuscation

### Frontend Integration

- **React 18**: Frontend framework
- **TypeScript**: Type-safe frontend development
- **Vite**: Fast build tool
- **Axios**: HTTP client for API calls
- **OpenAPI TypeScript Client**: Generated from OpenAPI spec

---

## API Documentation

### Base URL

- **Local Development**: `http://localhost:8080`
- **Docker**: `http://localhost:8080`
- **Production**: Configured via environment variables

### Authentication

All APIs (except `/api/auth/**`) require JWT authentication.

**Header Format**:
```
Authorization: Bearer <jwt-token>
```

**Login Endpoint**:
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin@academy.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

### API Endpoints

#### Students API

| Method | Endpoint | Description | Caching |
|--------|----------|-------------|---------|
| GET | `/api/students` | Get all students (optional batchId filter) | ✅ 30 min |
| GET | `/api/students/paged` | Paginated list of students | ✅ 30 min |
| GET | `/api/students/{id}` | Get student by ID | ✅ 30 min |
| POST | `/api/students` | Create new student | ❌ (invalidates cache) |
| PUT | `/api/students/{id}` | Update student | ❌ (invalidates cache) |
| DELETE | `/api/students/{id}` | Delete student | ❌ (invalidates cache) |

**Kafka Event**: `student.registered` published on creation

#### Batches API

| Method | Endpoint | Description | Caching |
|--------|----------|-------------|---------|
| GET | `/api/batches` | Paginated list of batches | ✅ 30 min |
| GET | `/api/batches/{id}` | Get batch by ID | ✅ 30 min |
| POST | `/api/batches` | Create new batch | ❌ (invalidates cache) |
| PUT | `/api/batches/{id}` | Update batch | ❌ (invalidates cache) |
| DELETE | `/api/batches/{id}` | Delete batch | ❌ (invalidates cache) |
| POST | `/api/batches/{id}/classes/{classId}` | Assign class to batch | ❌ (invalidates cache) |

**Kafka Event**: `batch.created` published on creation

#### Classes API

| Method | Endpoint | Description | Caching |
|--------|----------|-------------|---------|
| GET | `/api/classes` | Get all classes | ✅ 30 min |
| GET | `/api/classes/{id}` | Get class by ID | ✅ 30 min |
| POST | `/api/classes` | Create new class | ❌ (invalidates cache) |
| PUT | `/api/classes/{id}` | Update class | ❌ (invalidates cache) |
| DELETE | `/api/classes/{id}` | Delete class | ❌ (invalidates cache) |

#### Mentors API

| Method | Endpoint | Description | Caching |
|--------|----------|-------------|---------|
| GET | `/api/mentors` | Get all mentors | ✅ 1 hour |
| GET | `/api/mentors/{id}` | Get mentor by ID | ✅ 1 hour |
| POST | `/api/mentors` | Create new mentor | ❌ (invalidates cache) |
| PUT | `/api/mentors/{id}` | Update mentor | ❌ (invalidates cache) |
| DELETE | `/api/mentors/{id}` | Delete mentor | ❌ (invalidates cache) |

#### Mentor Sessions API

| Method | Endpoint | Description | Caching |
|--------|----------|-------------|---------|
| GET | `/api/mentor-sessions` | Get all sessions | ✅ 10 min |
| GET | `/api/mentor-sessions/{id}` | Get session by ID | ✅ 10 min |
| POST | `/api/mentor-sessions` | Create new session | ❌ (invalidates cache) |
| PUT | `/api/mentor-sessions/{id}` | Update session | ❌ (invalidates cache) |
| DELETE | `/api/mentor-sessions/{id}` | Delete session | ❌ (invalidates cache) |

**Kafka Event**: `mentor.session.created` published on creation

### API Documentation Access

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **OpenAPI YAML**: http://localhost:8080/api-docs.yaml

---

## Testing Strategy

### Unit Tests

**Coverage**: Service layer, mappers, utilities

**Technologies**:
- JUnit 5
- Mockito for mocking dependencies
- AssertJ for fluent assertions

**Example**:
```java
@Test
void testCreateStudent() {
    // Given
    StudentDTO dto = new StudentDTO();
    dto.setEmail("test@example.com");
    
    when(studentRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(studentRepository.save(any(Student.class))).thenReturn(student);
    
    // When
    StudentDTO result = studentService.createStudent(dto);
    
    // Then
    assertThat(result).isNotNull();
    verify(eventProducer).publishStudentRegisteredEvent(any());
}
```

### Integration Tests

**Coverage**: API endpoints, database operations, Kafka integration

**Technologies**:
- Testcontainers for MySQL and Kafka
- Spring Boot Test for application context
- @SpringBootTest for full integration testing

**Example**:
```java
@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testCreateStudent() throws Exception {
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(studentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
```

### Test Coverage Goals

- **Unit Tests**: > 80% coverage for service layer
- **Integration Tests**: All API endpoints covered
- **Kafka Tests**: Event publishing and consumption verified

---

## Deployment & DevOps

### Docker Compose Setup

The project includes comprehensive Docker Compose configurations:

#### Infrastructure Services (`docker-compose.infrastructure.yml`)

```yaml
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    ports: ["2181:2181"]
  
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on: [zookeeper]
    ports: ["9092:9092"]
    environment:
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://kafka:29092
  
  kafka-init:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      kafka:
        condition: service_healthy
    volumes:
      - ./docker/kafka/create-topics.sh:/create-topics.sh:ro
    entrypoint: ["/bin/bash", "/create-topics.sh"]
    # Automatically creates Kafka topics on startup
  
  mysql:
    image: mysql:8.0
    ports: ["3306:3306"]
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: academy_db
  
  redis:
    image: redis:7-alpine
    ports: ["6379:6379"]
    command: redis-server --appendonly yes
  
  backend:
    build:
      context: .
      dockerfile: Dockerfile
    depends_on:
      mysql:
        condition: service_healthy
      kafka:
        condition: service_healthy
      kafka-init:
        condition: service_completed_successfully
      redis:
        condition: service_healthy
    ports: ["8080:8080"]
  
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    depends_on:
      backend:
        condition: service_healthy
    ports: ["80:80"]
```

### Deployment Steps

#### Local Development

```bash
# Start all services
docker-compose -f docker-compose.infrastructure.yml up --build

# Access application
# Backend: http://localhost:8080
# Frontend: http://localhost
# Swagger: http://localhost:8080/swagger-ui.html
```

#### Production Deployment

**Option 1: Docker Compose (Small Scale)**
- Use production-ready `docker-compose.yml`
- Configure environment variables
- Set up reverse proxy (Nginx) for load balancing

**Option 2: Kubernetes (Large Scale)**
- Deploy MySQL as StatefulSet or use managed RDS
- Deploy Kafka cluster using Strimzi operator
- Deploy application as Deployment with ConfigMaps and Secrets
- Use Ingress for external access

**Option 3: Cloud Services**
- **Database**: AWS RDS MySQL or Azure Database for MySQL
- **Kafka**: AWS MSK or Confluent Cloud
- **Cache**: AWS ElastiCache (Redis) or Azure Cache
- **Application**: AWS ECS/EKS, Azure Container Instances, or GCP Cloud Run

### CI/CD Pipeline

**GitHub Actions** (recommended):
- Build and test on every push
- Run integration tests with Testcontainers
- Build Docker images
- Deploy to staging/production

---

## Performance Optimization

### Redis Caching Strategy

#### Cache Configuration

**TTL Strategy** (Time-To-Live based on data volatility):

| Cache Name | TTL | Reason |
|------------|-----|--------|
| `mentorSessions`, `mentorSession` | 10 minutes | Frequently changing data |
| `mentors`, `mentor`, `batchTypes`, `batchType` | 1 hour | Stable reference data |
| `students`, `student`, `batches`, `batch`, `classes`, `class` | 30 minutes | Default for transactional data |

#### Cache Key Patterns

```
{cacheName}::{keyExpression}

Examples:
- student::student:1
- students::batch:1
- students::all
- batch::batch:1
```

#### Cache Operations

**What Gets Cached**:
- ✅ GET operations (read-only)
- ✅ Non-null results
- ✅ Non-empty collections

**What Doesn't Get Cached**:
- ❌ POST/PUT/DELETE responses
- ❌ Null values
- ❌ Empty collections

**Cache Invalidation**:
- Automatic invalidation on create/update/delete
- Pattern-based eviction (e.g., `allEntries = true`)

### Database Optimization

1. **Indexes**: All foreign keys and frequently queried columns are indexed
2. **Connection Pooling**: HikariCP with optimized pool size
3. **Query Optimization**: JPA query methods with proper joins
4. **Pagination**: All list endpoints support pagination

### Performance Metrics

**Target Metrics**:
- API response time < 200ms for cached reads
- API response time < 500ms for database reads
- Cache hit ratio > 80%
- Database connection pool utilization < 70%

---

## Security Implementation

### Authentication

**JWT-Based Authentication**:
- Stateless authentication mechanism
- Token-based access control
- Configurable header name and prefix

**Implementation**:
```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

### Input Validation

**JSR-380 Bean Validation**:
- `@NotNull`, `@NotBlank`, `@Email`, `@Size` annotations
- Automatic validation at controller level
- Custom validation messages

### SQL Injection Prevention

- JPA/Hibernate parameterized queries
- No raw SQL queries
- Input sanitization

### Logging Security

**Sensitive Data Obfuscation**:
- Passwords, tokens, and secrets are automatically obfuscated in logs
- Logbook integration for HTTP request/response logging
- JSON structured logging for log aggregation

---

## Challenges & Solutions

### Challenge 1: Distributed Locking for Concurrent Student Registration

**Problem**: Multiple concurrent requests could create duplicate students with the same email.

**Solution**: Implemented distributed locking using Redis and Spring Integration's RedisLockRegistry:

```java
@WithLock(key = "student:onboarding:#{#dto.email}", timeout = 30)
public StudentDTO createStudent(StudentDTO dto) {
    // Lock ensures only one registration per email at a time
}
```

**Result**: Prevents race conditions and ensures data integrity.

### Challenge 2: Lock Expiration Handling

**Problem**: Locks expiring before operation completion caused `IllegalStateException`.

**Solution**: Graceful handling of expired locks:

```java
catch (IllegalStateException e) {
    if (e.getMessage().contains("expiration")) {
        log.warn("Lock expired before release (expected when operations take longer than lock timeout)");
    }
}
```

**Result**: Errors are logged as warnings instead of exceptions, improving system stability.

### Challenge 3: Kafka Topic Creation on Startup

**Problem**: Kafka topics needed to be created manually before application startup.

**Solution**: Automated topic creation using Docker Compose init container:

```yaml
kafka-init:
  image: confluentinc/cp-kafka:7.5.0
  depends_on:
    kafka:
      condition: service_healthy
  volumes:
    - ./docker/kafka/create-topics.sh:/create-topics.sh:ro
  entrypoint: ["/bin/bash", "/create-topics.sh"]
```

**Result**: Topics are automatically created when Kafka broker is ready.

### Challenge 4: Cache Invalidation Strategy

**Problem**: Ensuring cache consistency across multiple application instances.

**Solution**: Pattern-based cache eviction:

```java
@CacheEvict(value = {"student", "students"}, allEntries = true)
public StudentDTO createStudent(StudentDTO dto) { ... }
```

**Result**: Cache remains consistent across all instances.

---

## Conclusion & Future Work

### Key Achievements

1. ✅ **Production-Ready Architecture**: Multi-module design with clear separation of concerns
2. ✅ **Event-Driven System**: Kafka-based messaging for scalable, decoupled architecture
3. ✅ **Performance Optimization**: Redis caching with intelligent TTL strategies
4. ✅ **Concurrency Control**: Distributed locking and optimistic locking
5. ✅ **Comprehensive Testing**: Unit and integration tests with Testcontainers
6. ✅ **Containerization**: Docker and Docker Compose for easy deployment
7. ✅ **API Documentation**: OpenAPI/Swagger for developer-friendly API exploration
8. ✅ **Security**: JWT authentication with input validation
9. ✅ **Monitoring**: Spring Boot Actuator for health checks and metrics
10. ✅ **Frontend Integration**: React frontend with TypeScript and OpenAPI client

### Limitations

1. **Authentication**: Basic JWT implementation (can be enhanced with OAuth2)
2. **Rate Limiting**: No API rate limiting implemented
3. **Search**: No full-text search capability (consider Elasticsearch)
4. **File Storage**: No file upload functionality
5. **Notifications**: No email/SMS notification system
6. **Real-time Updates**: No WebSocket support for real-time notifications

### Future Enhancements

#### 1. Authentication & Authorization
- Implement OAuth2/JWT with refresh tokens
- Role-based access control (RBAC)
- API key management
- Multi-factor authentication (MFA)

#### 2. Performance Optimization
- Database read replicas for scaling reads
- Advanced caching strategies (cache warming, cache aside)
- Connection pooling tuning
- Query result pagination optimization

#### 3. Advanced Features
- Full-text search for students/mentors (Elasticsearch)
- File upload for student documents (S3/Azure Blob)
- Email notifications for session bookings
- Real-time notifications using WebSockets
- Batch operations (bulk student import)

#### 4. Monitoring & Observability
- Distributed tracing (Jaeger/Zipkin)
- Log aggregation (ELK stack)
- Performance monitoring (New Relic, Datadog)
- Custom metrics and dashboards (Grafana)

#### 5. Scalability
- Horizontal scaling with load balancer
- Kafka cluster configuration for high throughput
- Database sharding strategies
- CDN for static assets

#### 6. Testing
- Increase test coverage to >90%
- End-to-end testing with Cypress/Playwright
- Performance testing with JMeter
- Chaos engineering for resilience testing

#### 7. DevOps
- Kubernetes deployment manifests
- CI/CD pipeline with GitHub Actions
- Infrastructure as Code (Terraform)
- Automated backup and recovery

---

## References

1. **Spring Boot Documentation**: https://spring.io/projects/spring-boot
2. **Apache Kafka Documentation**: https://kafka.apache.org/documentation/
3. **MySQL Documentation**: https://dev.mysql.com/doc/
4. **Redis Documentation**: https://redis.io/documentation
5. **Flyway Documentation**: https://flywaydb.org/documentation/
6. **MapStruct Documentation**: https://mapstruct.org/documentation/
7. **Testcontainers Documentation**: https://www.testcontainers.org/
8. **Docker Documentation**: https://docs.docker.com/
9. **OpenAPI Specification**: https://swagger.io/specification/
10. **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
11. **Spring Security**: https://spring.io/projects/spring-security
12. **JWT.io**: https://jwt.io/

---

## Appendix

### A. Project Structure

```
academy-backend/
├── modules/
│   ├── academy-api/              # API layer
│   ├── academy-service/          # Business logic
│   ├── academy-common/           # Shared components
│   ├── academy-kafka-producer/   # Event producers
│   └── academy-kafka-consumer/  # Event consumers
├── frontend/                      # React frontend
├── docker/                        # Docker scripts
├── docker-compose.yml             # Docker Compose config
├── Dockerfile                     # Application Dockerfile
└── README.md                      # Project documentation
```

### B. Environment Variables

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/academy_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=rootpassword

# Redis
SPRING_REDIS_HOST=redis
SPRING_REDIS_PORT=6379

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Application
SPRING_PROFILES_ACTIVE=docker
FRONTEND_URL=http://localhost:5173
```

### C. Sample API Requests

See `postman/Academy.postman_collection.json` for complete Postman collection.

### D. Metrics Dashboard

**Health Check**: http://localhost:8080/actuator/health

**Metrics**: http://localhost:8080/actuator/metrics

**Prometheus**: http://localhost:8080/actuator/prometheus

---

**End of Report**

