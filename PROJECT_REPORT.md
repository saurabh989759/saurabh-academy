# Academy Management System — Project Report

**Title:** Backend Implementation — Academy Management Platform
**Stack:** Java 21 · Spring Boot 3.2.0 · MySQL 8.0 · Redis · Apache Kafka
**Date:** November 2024

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [System Goals](#system-goals)
3. [Requirements](#requirements)
4. [Architecture](#architecture)
5. [Database Design](#database-design)
6. [Feature Implementation](#feature-implementation)
7. [Technology Decisions](#technology-decisions)
8. [API Reference](#api-reference)
9. [Testing Approach](#testing-approach)
10. [Infrastructure & DevOps](#infrastructure--devops)
11. [Performance & Caching](#performance--caching)
12. [Security](#security)
13. [Engineering Challenges](#engineering-challenges)
14. [Outcomes & Next Steps](#outcomes--next-steps)
15. [References](#references)

---

## Executive Summary

This report covers the design and implementation of a production-ready backend for an academy management platform. The system is built on **Spring Boot 3.2.0** and **Java 21** using a multi-module Gradle project. It uses **MySQL 8.0** for relational data, **Redis** for TTL-based caching, and **Apache Kafka** for decoupled event publishing.

Core engineering concerns addressed include:

- RESTful API contracts driven from an OpenAPI specification
- Flyway-versioned database schema management
- Distributed locking (via Redis) to prevent duplicate operations under concurrency
- Optimistic locking at the database level using `@Version`
- JWT-based stateless authentication
- Containerized deployment with Docker Compose
- Integration testing using Testcontainers with real MySQL, Redis, and Kafka instances

The platform supports horizontal scaling and is structured to be microservices-ready.

---

## System Goals

### Core Capabilities

1. **Student Management** — Full CRUD: register students with academic profile, assign to batches, track batch history, and pair students via buddy system
2. **Batch Management** — Create and manage batches with batch types, class associations, and instructor tracking
3. **Mentor Session Booking** — Schedule student-mentor sessions with duration, bidirectional ratings, and Kafka event emission
4. **Class Scheduling** — Define classes (date, time, instructor) and associate them with batches (many-to-many)
5. **Kafka Event Pipeline** — Emit domain events on critical writes; consume and persist to audit log
6. **Redis Caching** — Entity-level caching with data-volatility-aware TTLs for faster reads
7. **Concurrency Safety** — Distributed Redis locks prevent race conditions; `@Version` fields prevent lost updates

### Why This Matters

The project demonstrates production-level practices commonly required in real-world backend engineering:

| Practice | Implementation |
|----------|---------------|
| API-first design | OpenAPI YAML → generated interfaces → controllers |
| Database versioning | Flyway migrations |
| Event-driven architecture | Kafka producers and consumers |
| Caching strategy | Per-entity TTL tuned to data volatility |
| Containerized delivery | Docker + Docker Compose |
| Automated testing | JUnit 5, Mockito, Testcontainers |
| Security | JWT authentication and input validation |
| Observability | Spring Actuator health/metrics endpoints |

---

## Requirements

### Functional Requirements

#### Student Registration
- Register with name, email, graduation year, university, and phone
- Assign to a batch on creation or update
- Track batch history when students move between batches
- Prevent duplicate emails using distributed locking
- Publish `student.registered` Kafka event

#### Batch Management
- Create batches with a name, start month, instructor, and batch type
- Associate multiple classes (many-to-many relationship)
- Support paginated listing
- Publish `batch.created` Kafka event

#### Mentor Session Booking
- Book a session specifying student, mentor, time, and duration
- Validate both student and mentor exist before saving
- Record bidirectional ratings on update
- Publish `mentor.session.created` Kafka event

#### Class Management
- Create classes with date, time, instructor
- Attach classes to batches
- Full CRUD support

#### Event Audit
- Consume all three Kafka topics
- Persist event type, payload (JSON), and timestamp to `audit_events`

### Non-Functional Requirements

| Quality | Target |
|---------|--------|
| Read latency | < 200 ms for cached operations |
| Scalability | Stateless API; supports multiple instances |
| Reliability | ACID transactions; graceful cache-miss fallback |
| Security | Bean Validation; JWT; JPA parameterized queries |
| Maintainability | Multi-module; comprehensive tests; OpenAPI docs |

---

## Architecture

### System Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                    Client Tier                                  │
│           (React Frontend / Mobile Applications)                │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTPS / REST
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                  API Gateway Tier                               │
│  JWT Filter → REST Controllers (OpenAPI-Generated Interfaces)   │
│  Student · Batch · Class · Mentor · MentorSession · Auth        │
└────────────────────────────┬────────────────────────────────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
   Service Layer       Redis Cache         Kafka Layer
   (Business Logic)   (TTL-based)      Producers + Consumers
          │
   JPA Repository Layer
          │
      MySQL 8.0
```

### Multi-Module Structure

```
academy-backend/
├── modules/
│   ├── academy-api/
│   │   ├── Controllers           REST endpoints
│   │   ├── Security              JWT auth filter + config
│   │   └── Configuration         Redis, Kafka, OpenAPI, CORS
│   │
│   ├── academy-service/
│   │   ├── Services              Business logic + cache annotations
│   │   ├── Mappers               MapStruct DTO ↔ entity
│   │   ├── Aspects               @WithLock AOP interceptor
│   │   └── Annotations           Custom @WithLock definition
│   │
│   ├── academy-common/
│   │   ├── Entities              JPA entities with @Version
│   │   ├── DTOs                  Validated request/response objects
│   │   ├── Repositories          Spring Data JPA interfaces
│   │   └── Exceptions            Domain exceptions + global handler
│   │
│   ├── academy-kafka-producer/   KafkaTemplate-based event publishing
│   └── academy-kafka-consumer/   Audit event persistence
```

### Design Patterns Applied

| Pattern | Usage |
|---------|-------|
| Repository | Data access abstraction via Spring Data JPA |
| Service Layer | Business logic isolated from transport layer |
| DTO | API contracts decoupled from domain entities |
| Factory (MapStruct) | Compile-time, type-safe entity/DTO mapping |
| AOP | Cross-cutting: distributed locking and caching |
| Event-Driven | Kafka decouples producers from consumers |
| Strategy | Cache TTL chosen per entity data volatility |

### Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| OpenAPI-driven controllers | Single source of truth; eliminates hand-coded interface drift |
| Multi-module Gradle build | Independent deployment units; enforced layering |
| Redis caching | Sub-200ms read latency without hitting the database |
| Distributed locking | Prevents duplicate student registration under concurrency |
| Optimistic locking (`@Version`) | Prevents lost updates without pessimistic DB locks |
| JWT authentication | Stateless; scales horizontally |

---

## Database Design

### Relationships

- **BatchType → Batch**: One-to-Many
- **Batch → Student**: One-to-Many
- **Batch ↔ Class**: Many-to-Many via `batches_classes`
- **Student → MentorSession**: One-to-Many
- **Mentor → MentorSession**: One-to-Many
- **Student → Student**: Self-reference (buddy pairing)
- **Student → StudentBatchHistory**: One-to-Many

### Nine Tables

| Table | Description |
|-------|-------------|
| `batch_types` | Batch category definitions |
| `batches` | Batch records with instructor and type |
| `classes` | Scheduled class definitions |
| `batches_classes` | Many-to-many join |
| `students` | Student profiles |
| `mentors` | Mentor profiles |
| `mentor_sessions` | Booking records with ratings |
| `audit_events` | Consumed Kafka events |
| `student_batch_history` | Historical batch assignments |

All tables use `BIGINT` primary keys with `AUTO_INCREMENT`. Optimistic locking columns (`version INT`) exist on `batches`, `students`, and `mentor_sessions`.

**Unique constraints:** `students.email`, `batch_types.name`

**Flyway migrations:**
- `V1__init.sql` — schema creation + seed data
- `V2__add_version_columns.sql` — add `version` fields

---

## Feature Implementation

### Mentor Session Booking (End-to-End)

This walkthrough shows how a single POST request flows through every layer.

**Request:**
```
POST /api/mentor-sessions
{
  "time": "2024-01-15T15:00:00Z",
  "durationMinutes": 60,
  "studentId": 1,
  "mentorId": 1
}
```

**Controller** validates the payload (`@Valid`) and delegates to `MentorSessionService`.

**Service:**
```java
@Transactional
public MentorSessionDTO createSession(MentorSessionDTO request) {
    Student student = studentRepository.findById(request.getStudentId())
        .orElseThrow(() -> new StudentNotFoundException(request.getStudentId()));
    Mentor mentor = mentorRepository.findById(request.getMentorId())
        .orElseThrow(() -> new MentorNotFoundException(request.getMentorId()));

    MentorSession session = sessionMapper.toEntity(request);
    session.setStudent(student);
    session.setMentor(mentor);
    MentorSession persisted = sessionRepository.save(session);

    eventProducer.publishSessionCreatedEvent(persisted);
    return sessionMapper.toDTO(persisted);
}
```

**Kafka Producer** sends to topic `mentor.session.created` (3 partitions, RF 1).

**Kafka Consumer** receives and persists to `audit_events`:
```java
@KafkaListener(topics = "mentor.session.created", groupId = "academy-backend-group")
public void onMentorSessionCreated(EventDTO event, Acknowledgment ack) {
    persist(event);
    ack.acknowledge();
}
```

**Flow Summary:**
```
POST /api/mentor-sessions
  → Controller (validate)
  → Service (resolve student + mentor, save)
  → Repository → MySQL
  → Kafka Producer → mentor.session.created
  → Event Consumer → audit_events
```

### Distributed Locking for Student Registration

**Problem:** Concurrent requests can register the same email twice before either commits.

**Solution:**
```java
@WithLock(key = "student:onboarding:#{#request.email}", timeout = 30, maxRetries = 3)
@Transactional
public StudentDTO createStudent(StudentDTO request) {
    studentRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
        throw new IllegalStateException("Student with that email already exists");
    });
    Student student = studentMapper.toEntity(request);
    Student persisted = studentRepository.save(student);
    eventProducer.publishStudentRegisteredEvent(persisted);
    return studentMapper.toDTO(persisted);
}
```

Lock key format: `student:onboarding:{email}` — scoped per email address.

---

## Technology Decisions

### Core Framework

| Technology | Role |
|------------|------|
| Java 21 | Modern LTS with virtual threads, records, pattern matching |
| Spring Boot 3.2.0 | Auto-configuration, embedded server, actuator |
| Spring Data JPA | Repository abstraction over Hibernate |
| Spring Data Redis | Cache abstraction with Redis backend |
| Spring Kafka | High-level Kafka producer/consumer integration |
| Spring Security | Authentication filter chain |

### Data & Persistence

| Technology | Role |
|------------|------|
| MySQL 8.0 | ACID-compliant relational database |
| Flyway | Version-controlled schema migrations |
| Hibernate | JPA implementation, ORM |
| HikariCP | Connection pooling |

### Mapping & Utilities

| Technology | Role |
|------------|------|
| MapStruct | Compile-time DTO mapping (no reflection overhead) |
| Lombok | `@Getter`, `@Setter`, `@Builder` — eliminates boilerplate |

### API & Docs

| Technology | Role |
|------------|------|
| Springdoc OpenAPI | OpenAPI 3.0 spec generation |
| Swagger UI | Interactive API browser |
| OpenAPI Generator | TypeScript client for frontend |

### Testing

| Technology | Role |
|------------|------|
| JUnit 5 | Test runner |
| Mockito | Unit test mocking |
| Testcontainers | Real MySQL, Redis, Kafka in integration tests |
| AssertJ | Fluent assertion library |

### DevOps

| Technology | Role |
|------------|------|
| Docker | Containerization |
| Docker Compose | Multi-container orchestration |
| Gradle | Build and dependency management |

---

## API Reference

### Endpoint Tables

#### Students

| Method | Endpoint | Cache TTL | Kafka Event |
|--------|----------|-----------|-------------|
| GET | `/api/students` | 30 min | — |
| GET | `/api/students/paged` | — | — |
| GET | `/api/students/{id}` | 30 min | — |
| POST | `/api/students` | evicts | student.registered |
| PUT | `/api/students/{id}` | evicts | — |
| DELETE | `/api/students/{id}` | evicts | — |

#### Batches

| Method | Endpoint | Cache TTL | Kafka Event |
|--------|----------|-----------|-------------|
| GET | `/api/batches` | 30 min | — |
| GET | `/api/batches/{id}` | 30 min | — |
| POST | `/api/batches` | evicts | batch.created |
| PUT | `/api/batches/{id}` | evicts | — |
| DELETE | `/api/batches/{id}` | evicts | — |
| POST | `/api/batches/{id}/classes/{classId}` | evicts | — |

#### Classes

| Method | Endpoint | Cache TTL |
|--------|----------|-----------|
| GET | `/api/classes` | 30 min |
| GET | `/api/classes/{id}` | 30 min |
| POST | `/api/classes` | evicts |
| PUT | `/api/classes/{id}` | evicts |
| DELETE | `/api/classes/{id}` | evicts |

#### Mentors

| Method | Endpoint | Cache TTL |
|--------|----------|-----------|
| GET | `/api/mentors` | 1 hour |
| GET | `/api/mentors/{id}` | 1 hour |
| POST | `/api/mentors` | evicts |
| PUT | `/api/mentors/{id}` | evicts |
| DELETE | `/api/mentors/{id}` | evicts |

#### Mentor Sessions

| Method | Endpoint | Cache TTL | Kafka Event |
|--------|----------|-----------|-------------|
| GET | `/api/mentor-sessions` | 10 min | — |
| GET | `/api/mentor-sessions/{id}` | 10 min | — |
| POST | `/api/mentor-sessions` | evicts | mentor.session.created |
| PUT | `/api/mentor-sessions/{id}` | evicts | — |
| DELETE | `/api/mentor-sessions/{id}` | evicts | — |

API docs: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/api-docs

---

## Testing Approach

### Unit Tests

Service layer tested in isolation using Mockito:

```java
@Test
void createStudent_shouldPublishEvent() {
    when(studentRepository.findByEmail(any())).thenReturn(Optional.empty());
    when(studentRepository.save(any())).thenReturn(student);

    StudentDTO result = studentService.createStudent(dto);

    assertThat(result).isNotNull();
    verify(eventProducer).publishStudentRegisteredEvent(any());
}
```

### Integration Tests

Full application context with Testcontainers:

```java
@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerIntegrationTest {

    @Test
    void createStudent_returns201() throws Exception {
        mockMvc.perform(post("/api/students")
                .contentType(APPLICATION_JSON)
                .content(studentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
```

### Coverage Targets

- Service layer: > 80%
- All API endpoints: integration-covered
- Kafka: produce-consume round-trip verified

---

## Infrastructure & DevOps

### Docker Compose Stack

```yaml
services:
  zookeeper:    confluentinc/cp-zookeeper:7.5.0  → port 2181
  kafka:        confluentinc/cp-kafka:7.5.0       → port 9092
  kafka-init:   creates topics when broker is healthy
  mysql:        mysql:8.0                         → port 3306
  redis:        redis:7-alpine                    → port 6379
  backend:      multi-stage build from Dockerfile → port 8080
  frontend:     React build via Nginx             → port 80
```

Dependencies are health-check controlled — the backend waits for MySQL, Redis, and Kafka to be healthy before starting.

### Deployment Options

| Environment | Approach |
|-------------|----------|
| Local Dev | Docker Compose dev config |
| Staging | Full `docker-compose.infrastructure.yml` |
| Production (small) | Docker Compose + Nginx reverse proxy |
| Production (large) | Kubernetes with StatefulSets for databases |
| Cloud | AWS RDS + MSK + ElastiCache + ECS/EKS |

### CI/CD (Recommended)

GitHub Actions pipeline:
1. Compile and run tests (including integration with Testcontainers)
2. Build Docker images
3. Push to registry
4. Deploy to staging / production

---

## Performance & Caching

### Redis Strategy

| Cache | TTL | Why |
|-------|-----|-----|
| `mentorSessions`, `mentorSession` | 10 min | Frequent writes |
| `mentors`, `mentor`, `batchTypes`, `batchType` | 1 hour | Stable reference data |
| `students`, `student`, `batches`, `batch`, `classes`, `class` | 30 min | Moderate write rate |

### Performance Targets

| Metric | Goal |
|--------|------|
| Cached read latency | < 200 ms |
| DB read latency | < 500 ms |
| Cache hit ratio | > 80% |
| DB pool utilization | < 70% |

### Database Optimization

- All foreign key and query columns indexed
- HikariCP connection pool with tuned settings
- JPA method queries with appropriate `FetchType`
- Pagination on all list endpoints

---

## Security

### Authentication Flow

1. Client sends `POST /api/auth/login` with credentials
2. `AuthController` validates against `UserDetailsService`
3. On success, `JwtService.buildToken()` generates a signed HS256 token
4. Client includes token as `Authorization: Bearer <token>` on subsequent requests
5. `JwtAuthenticationFilter` extracts, validates, and sets `SecurityContext`

### Input Validation

JSR-380 annotations (`@NotBlank`, `@Email`, `@Size`, `@NotNull`) enforce constraints at the controller layer. Violations return structured 400 responses.

### SQL Injection Prevention

JPA/Hibernate parameterized queries eliminate raw SQL concatenation throughout the codebase.

### Log Sanitization

Logbook automatically redacts `password`, `token`, `secret` parameters and `Authorization`, `Cookie`, `X-Api-Key` headers before writing to logs.

---

## Engineering Challenges

### Challenge 1 — Concurrent Student Registration

**Problem:** Two requests with the same email could both pass the uniqueness check before either commits.

**Solution:** Redis distributed lock scoped to `student:onboarding:{email}`. Only one thread can hold the lock per email.

**Result:** Data integrity guaranteed; race condition eliminated.

### Challenge 2 — Lock Timeout Edge Case

**Problem:** `IllegalStateException` thrown when a Redis lock expired before the operation completed.

**Solution:** Catch and log as a warning rather than propagating as an error — the operation itself succeeded; only the lock cleanup failed.

**Result:** Improved system stability without hiding genuine errors.

### Challenge 3 — Kafka Topics on First Boot

**Problem:** Application failed to publish events when topics didn't exist yet.

**Solution:** A `kafka-init` init container creates topics once the broker is healthy, before the backend starts.

**Result:** Fully automated topic provisioning in Docker Compose.

### Challenge 4 — Cache Consistency Across Instances

**Problem:** Multiple app instances could serve stale cached data after writes.

**Solution:** `@CacheEvict(allEntries = true)` on every write operation clears all entries in the affected cache region, affecting all instances sharing the same Redis.

**Result:** Cache remains consistent regardless of horizontal scale.

---

## Outcomes & Next Steps

### What Was Delivered

| Feature | Status |
|---------|--------|
| Multi-module Spring Boot architecture | Complete |
| Full CRUD REST APIs (5 domains) | Complete |
| OpenAPI-driven controller contracts | Complete |
| JWT authentication | Complete |
| Redis caching with per-entity TTL | Complete |
| Kafka event publishing + audit consumption | Complete |
| Distributed locking | Complete |
| Optimistic locking (`@Version`) | Complete |
| Flyway migrations | Complete |
| Docker Compose full stack | Complete |
| React frontend with WebSocket updates | Complete |
| Swagger UI / OpenAPI docs | Complete |
| Spring Actuator health/metrics | Complete |

### Limitations

- No OAuth2, RBAC, or refresh tokens
- Single Redis node (no clustering)
- No full-text search
- No file upload
- No email/SMS notifications
- JWT secret stored in config (should use secrets manager)
- No API rate limiting

### Roadmap

**Short-term (1–3 months)**
- Database-backed users with roles
- OAuth2 / refresh tokens
- Prometheus + Grafana monitoring
- Distributed tracing (Jaeger or Zipkin)

**Medium-term (3–6 months)**
- Redis Cluster for high availability
- Multi-broker Kafka configuration
- Vault for secrets management
- Rate limiting

**Long-term (6+ months)**
- Kubernetes with Helm charts
- Elasticsearch for search
- WebSocket real-time notifications
- CI/CD with blue-green deploys

---

## References

1. Spring Boot — https://spring.io/projects/spring-boot
2. Apache Kafka — https://kafka.apache.org/documentation/
3. MySQL — https://dev.mysql.com/doc/
4. Redis — https://redis.io/documentation
5. Flyway — https://flywaydb.org/documentation/
6. MapStruct — https://mapstruct.org/documentation/
7. Testcontainers — https://www.testcontainers.org/
8. Docker — https://docs.docker.com/
9. OpenAPI Specification — https://swagger.io/specification/
10. Spring Data JPA — https://spring.io/projects/spring-data-jpa
11. Spring Security — https://spring.io/projects/spring-security
12. JWT.io — https://jwt.io/

---

*End of Report*
