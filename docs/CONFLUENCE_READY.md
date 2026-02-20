# Academy Management System — Backend Documentation

## Overview

A production-ready backend platform for managing academy operations — students, batches, mentor sessions, classes, and instructors. Built on Spring Boot 3.2.0 with Java 21, the system follows a layered multi-module architecture with MySQL 8.0 for persistence, Apache Kafka for event streaming, and Redis for caching. Schema versioning is handled via Flyway, and the test suite relies on Testcontainers for realistic integration coverage.

---

## Technology Stack

| Category | Technology |
|---|---|
| Core Framework | Java 21, Spring Boot 3.2.0 |
| Persistence | Spring Data JPA, MySQL 8.0, Flyway, Hibernate |
| Messaging | Apache Kafka, Zookeeper |
| Caching | Redis (Spring Cache abstraction) |
| Mapping | MapStruct, Lombok |
| API Docs | Springdoc OpenAPI, Swagger UI |
| Testing | JUnit 5, Mockito, Testcontainers |
| DevOps | Docker, Docker Compose, GitHub Actions |
| Monitoring | Spring Boot Actuator |

---

## System Design

### Layered Architecture

The system is organized into four primary layers:

**Entity Layer** — `BatchType`, `Batch`, `Student`, `ClassEntity`, `Mentor`, `MentorSession`, `StudentBatchHistory`, `AuditEvent`

**Service Layer** — `StudentService`, `BatchService`, `MentorService`, `MentorSessionService`, `ClassService`

**Controller Layer** — One REST controller per domain entity (OpenAPI-generated interfaces)

**Kafka Layer** — Domain event producers and audit event consumers

![Class Diagram](class-diagram.png)

---

## Requirements

### What the System Does

**Student Management**
- Register students with personal and academic details
- Enroll students into batches
- Maintain historical batch assignment records
- Buddy pairing between students

**Batch Management**
- Create batches tied to batch types (e.g., Full Stack, Data Science)
- Link multiple classes to a batch
- Track start dates and assigned instructors

**Mentor Session Booking**
- Schedule sessions between a student and a mentor
- Record session duration and timing
- Collect bidirectional ratings (student → mentor and mentor → student)

**Class Management**
- Create classes with date, time, and instructor
- Attach classes to one or more batches

**Kafka Event Publishing**
- `student.registered` — on student creation
- `mentor.session.created` — on session booking
- `batch.created` — on batch creation

### Quality Attributes

| Attribute | Target |
|---|---|
| Performance | Read API response < 200 ms |
| Scalability | Stateless design; horizontal scaling supported |
| Reliability | Full transaction management and error handling |
| Security | Input validation; JWT-based authentication |
| Maintainability | Layered architecture; comprehensive test coverage |

---

## Database Schema

Nine tables cover all domain concepts and audit history:

| Table | Purpose |
|---|---|
| `batch_type` | Batch category definitions |
| `batches` | Batch records |
| `classes` | Scheduled class records |
| `batches_classes` | Many-to-many join between batches and classes |
| `students` | Student records |
| `mentors` | Mentor records |
| `mentor_sessions` | Session bookings |
| `student_batch_history` | Tracks batch reassignments over time |
| `audit_events` | Persisted Kafka event log |

![Entity Relationship Diagram](erd.png)

---

## Feature Walkthrough: Booking a Mentor Session

The flow below illustrates how a single API request propagates through every layer of the system.

```
POST /api/mentor-sessions
        │
        ▼
   Controller  ──► validates request, delegates to service
        │
        ▼
    Service    ──► validates student & mentor, creates session
        │
        ├──► Repository ──► Database  (persists session)
        │
        └──► Kafka Producer ──► mentor.session.created topic
                                         │
                                         ▼
                               Event Consumer ──► audit_events table
```

Steps in sequence:
1. **POST** `/api/mentor-sessions` — client submits booking payload
2. **Controller** validates request body and delegates to `MentorSessionService`
3. **Service** confirms both student and mentor exist, saves session transactionally
4. **Repository** persists the entity to MySQL
5. **Kafka Producer** fires `mentor.session.created` event
6. **Event Consumer** receives the event and writes it to `audit_events`

---

## Deployment

### Local Development

```bash
# 1. Start infrastructure
docker-compose -f docker-compose.infrastructure.yml up -d

# 2. Run the application
./gradlew bootRun

# 3. Access API
curl http://localhost:8080/actuator/health
```

### Docker (Full Stack)

The Docker Compose file starts MySQL, Zookeeper, Kafka, Redis, the backend, and the React frontend together using a multi-stage Dockerfile build.

```bash
docker-compose -f docker-compose.infrastructure.yml up --build
```

### Production Options

- **Docker Compose** — suitable for smaller deployments
- **Kubernetes** — recommended for scale; use StatefulSets for MySQL/Kafka, Deployments for the app
- **Cloud Managed Services** — AWS RDS + MSK + ElastiCache + ECS/EKS

---

## Key Takeaways

- **Layered architecture** keeps concerns cleanly separated and improves testability
- **Event-driven design** with Kafka allows async processing and audit trail maintenance
- **Flyway migrations** version the schema alongside the application code
- **Containerization** ensures consistent environments from development to production
- **Testcontainers** enables integration tests that run against real databases and brokers

---

## Known Constraints

- JWT authentication is implemented but OAuth2/RBAC are not yet in place
- No Redis cluster — single node only
- No full-text search capability
- No file upload support

---

## References

- Spring Boot — https://spring.io/projects/spring-boot
- Apache Kafka — https://kafka.apache.org/documentation/
- MySQL — https://dev.mysql.com/doc/
- Flyway — https://flywaydb.org/documentation/
- MapStruct — https://mapstruct.org/documentation/
- Testcontainers — https://www.testcontainers.org/
- Docker — https://docs.docker.com/
- OpenAPI Specification — https://swagger.io/specification/
