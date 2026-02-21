# Academy Management System — Backend

A production-grade Spring Boot backend for running an academy platform. Manages students, batches, classes, mentors, and mentor sessions with full CRUD APIs, Redis caching, Kafka event streaming, JWT authentication, and a React frontend.

**Stack:** Java 22 · Spring Boot 3.2.0 · MySQL 8.0 · Redis 7 · Apache Kafka · Docker

---

## Contents

- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [API Reference](#api-reference)
- [Caching Behavior](#caching-behavior)
- [Kafka Topics](#kafka-topics)
- [Logging](#logging)
- [Project Layout](#project-layout)
- [Health & Metrics](#health--metrics)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

---

## Getting Started

### Requirements

| Tool | Version |
|------|---------|
| Java | 22+ |
| Gradle | 8.10+ (or use `./gradlew`) |
| Docker + Docker Compose | Any recent version |
| MySQL | 8.0+ (only if running without Docker) |
| Redis | 7+ (only if running without Docker) |

---

## Running the Application

### Option A — Docker Compose (Recommended)

Starts all services — MySQL, Redis, Kafka, Zookeeper, the backend, and the frontend — in one command:

```bash
git clone <repository-url>
cd academy-backend
docker-compose up --build
```

Services will be available at:

| Service | URL |
|---------|-----|
| Frontend | http://localhost |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Health Check | http://localhost:8080/actuator/health |

### Option B — Run Locally

```bash
# Start infrastructure only
docker-compose -f docker/docker-compose.dev.yml up -d mysql redis kafka zookeeper

# Run the Spring Boot application
./gradlew bootRun

# Or build a JAR and run it
./gradlew clean build
java -jar modules/academy-api/build/libs/academy-api-1.0.0.jar
```

### Verify Everything Is Up

```bash
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP","components":{"db":{"status":"UP"},"redis":{"status":"UP"},"kafka":{"status":"UP"}}}
```

---

## API Reference

### Authentication

Every endpoint (except `/api/auth/**`) requires a JWT bearer token.

**Step 1 — Login:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin@academy.com", "password": "password123"}'
```

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

**Step 2 — Use the token:**

```bash
export JWT_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
curl http://localhost:8080/api/students -H "Authorization: Bearer $JWT_TOKEN"
```

Default credentials: `admin@academy.com` / `password123`

---

### Students

#### Create a Student

```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "name": "Akhilesh Gupta",
    "email": "akhilesh@example.com",
    "graduationYear": 2024,
    "universityName": "State University",
    "phoneNumber": "+1-555-123-4567",
    "batchId": 1
  }'
```

Response `201 Created`:
```json
{
  "id": 1,
  "name": "Akhilesh Gupta",
  "email": "akhilesh@example.com",
  "graduationYear": 2024,
  "universityName": "State University",
  "phoneNumber": "+1-555-123-4567",
  "batchId": 1,
  "buddyId": null
}
```

Publishes: `student.registered` Kafka event · Invalidates: `student`, `students` caches

#### Get Student by ID

```bash
curl http://localhost:8080/api/students/1 -H "Authorization: Bearer $JWT_TOKEN"
```

Cached with key `student::student:1` (TTL 30 min). First request hits the database; subsequent requests return from Redis.

```bash
# Inspect the cache
docker exec -it academy-redis redis-cli KEYS "*student*"
docker exec -it academy-redis redis-cli TTL "student::student:1"
```

#### List All Students

```bash
# All students
curl http://localhost:8080/api/students -H "Authorization: Bearer $JWT_TOKEN"

# Filter by batch
curl "http://localhost:8080/api/students?batchId=1" -H "Authorization: Bearer $JWT_TOKEN"

# Paginated
curl "http://localhost:8080/api/students/paged?page=0&size=20&sort=name,asc" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

Paginated response:
```json
{
  "content": [{ "id": 1, "name": "Akhilesh Gupta", "..." : "..." }],
  "totalElements": 100,
  "totalPages": 5,
  "size": 20,
  "number": 0,
  "first": true,
  "last": false
}
```

#### Update a Student

```bash
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "name": "Akhilesh Gupta Updated",
    "email": "akhilesh@example.com",
    "graduationYear": 2024,
    "universityName": "Updated University",
    "phoneNumber": "+1-555-123-4567",
    "batchId": 1
  }'
```

Invalidates `student::student:1` and all `students::*` list caches.

#### Delete a Student

```bash
curl -X DELETE http://localhost:8080/api/students/1 -H "Authorization: Bearer $JWT_TOKEN"
```

Response `204 No Content` · Invalidates caches.

---

### Batches

```bash
# Create
curl -X POST http://localhost:8080/api/batches \
  -H "Content-Type: application/json" -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{"name":"FSD-2024-03","startMonth":"2024-03-01","currentInstructor":"John Instructor","batchTypeId":1,"classIds":[1,2]}'

# Get by ID (cached, TTL 30 min)
curl http://localhost:8080/api/batches/1 -H "Authorization: Bearer $JWT_TOKEN"

# Paginated list
curl "http://localhost:8080/api/batches?page=0&size=10" -H "Authorization: Bearer $JWT_TOKEN"

# Update
curl -X PUT http://localhost:8080/api/batches/1 \
  -H "Content-Type: application/json" -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{"name":"FSD-2024-03-Updated","startMonth":"2024-03-01","currentInstructor":"Jane Instructor","batchTypeId":1}'

# Delete
curl -X DELETE http://localhost:8080/api/batches/1 -H "Authorization: Bearer $JWT_TOKEN"

# Assign a class to a batch
curl -X POST http://localhost:8080/api/batches/1/classes/2 -H "Authorization: Bearer $JWT_TOKEN"
```

Publishes `batch.created` on creation.

---

### Batch Types

```bash
# Create
curl -X POST http://localhost:8080/api/batch-types \
  -H "Content-Type: application/json" -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{"name":"Full Stack Development"}'

# List all (cached, TTL 1 hour)
curl http://localhost:8080/api/batch-types -H "Authorization: Bearer $JWT_TOKEN"

# Get by ID
curl http://localhost:8080/api/batch-types/1 -H "Authorization: Bearer $JWT_TOKEN"

# Update
curl -X PUT http://localhost:8080/api/batch-types/1 \
  -H "Content-Type: application/json" -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{"name":"Full Stack Development — Advanced"}'

# Delete
curl -X DELETE http://localhost:8080/api/batch-types/1 -H "Authorization: Bearer $JWT_TOKEN"
```

---

### Classes

```bash
# Create
curl -X POST http://localhost:8080/api/classes \
  -H "Content-Type: application/json" -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{"name":"Introduction to Java","date":"2024-03-15","time":"10:00:00","instructor":"Jane Smith"}'

# List all (cached)
curl http://localhost:8080/api/classes -H "Authorization: Bearer $JWT_TOKEN"

# Get by ID
curl http://localhost:8080/api/classes/1 -H "Authorization: Bearer $JWT_TOKEN"

# Update
curl -X PUT http://localhost:8080/api/classes/1 \
  -H "Content-Type: application/json" -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{"name":"Advanced Java","date":"2024-03-20","time":"14:00:00","instructor":"John Doe"}'

# Delete
curl -X DELETE http://localhost:8080/api/classes/1 -H "Authorization: Bearer $JWT_TOKEN"
```

---

### Mentors

```bash
# Create
curl -X POST http://localhost:8080/api/mentors \
  -H "Content-Type: application/json" -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{"name":"Alice Johnson","currentCompany":"Tech Corp"}'

# List all (cached, TTL 1 hour — stable reference data)
curl http://localhost:8080/api/mentors -H "Authorization: Bearer $JWT_TOKEN"

# Get by ID
curl http://localhost:8080/api/mentors/1 -H "Authorization: Bearer $JWT_TOKEN"

# Update
curl -X PUT http://localhost:8080/api/mentors/1 \
  -H "Content-Type: application/json" -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{"name":"Alice Johnson","currentCompany":"New Tech Corp"}'

# Delete
curl -X DELETE http://localhost:8080/api/mentors/1 -H "Authorization: Bearer $JWT_TOKEN"
```

---

### Mentor Sessions

```bash
# Create (publishes mentor.session.created event)
curl -X POST http://localhost:8080/api/mentor-sessions \
  -H "Content-Type: application/json" -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{"time":"2024-03-20T15:00:00Z","durationMinutes":60,"studentId":1,"mentorId":1}'

# List all (cached, TTL 10 min — frequently changing)
curl http://localhost:8080/api/mentor-sessions -H "Authorization: Bearer $JWT_TOKEN"

# Get by ID
curl http://localhost:8080/api/mentor-sessions/1 -H "Authorization: Bearer $JWT_TOKEN"

# Update (with ratings)
curl -X PUT http://localhost:8080/api/mentor-sessions/1 \
  -H "Content-Type: application/json" -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{"time":"2024-03-21T16:00:00Z","durationMinutes":90,"studentId":1,"mentorId":1,"studentRating":5,"mentorRating":4}'

# Delete
curl -X DELETE http://localhost:8080/api/mentor-sessions/1 -H "Authorization: Bearer $JWT_TOKEN"
```

---

## Caching Behavior

The application uses Spring Cache with Redis as the backing store.

### TTL Configuration

| Cache | TTL | Rationale |
|-------|-----|-----------|
| `mentorSession`, `mentorSessions` | 10 min | Session data changes frequently |
| `mentor`, `mentors`, `batchType`, `batchTypes` | 1 hour | Reference data, rarely modified |
| `student`, `students`, `batch`, `batches`, `class`, `classes` | 30 min | General transactional data |

### What Gets Cached vs. What Doesn't

| Cached | Not Cached |
|--------|------------|
| GET responses | POST / PUT / DELETE responses |
| Non-null values | Null values |
| Non-empty collections | Empty collections |
| Successful responses | Error responses |

### Redis Cache Key Patterns

```
student::student:1        → single student
students::batch:1         → students filtered by batch
students::all             → full student list
batch::batch:1            → single batch
mentor::mentor:1          → single mentor
mentorSession::mentorSession:1  → single session
```

### Monitor the Cache

```bash
# List all cache keys
docker exec -it academy-redis redis-cli KEYS "*"

# Check TTL of a key
docker exec -it academy-redis redis-cli TTL "student::student:1"

# Read a cached value
docker exec -it academy-redis redis-cli GET "student::student:1"

# Watch Redis commands live
docker exec -it academy-redis redis-cli MONITOR

# Clear all cache
docker exec -it academy-redis redis-cli FLUSHALL
```

### Testing Cache Hit / Miss

```bash
# First call hits the database
time curl http://localhost:8080/api/students/1 -H "Authorization: Bearer $JWT_TOKEN"

# Second call returns from Redis
time curl http://localhost:8080/api/students/1 -H "Authorization: Bearer $JWT_TOKEN"
```

---

## Kafka Topics

| Topic | Published When |
|-------|----------------|
| `student.registered` | A student is created |
| `mentor.session.created` | A mentor session is booked |
| `batch.created` | A batch is created |

All topics are auto-created with 3 partitions and replication factor 1.

### Inspect Topics

```bash
# List topics
docker exec -it academy-kafka kafka-topics --list --bootstrap-server localhost:9092

# Consume messages
docker exec -it academy-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic student.registered \
  --from-beginning
```

---

## Logging

The application uses **Logback** with JSON output and **Logbook** for HTTP request/response tracing.

### Log Levels

| Package | Level |
|---------|-------|
| `com.academy` | INFO |
| `org.zalando.logbook.Logbook` | TRACE |
| `org.springframework` | WARN |
| `org.hibernate` | WARN |

### Sample Log Entry

```json
{
  "timestamp": "2024-11-29T12:00:00.000+05:30",
  "level": "INFO",
  "logger": "com.academy.service.StudentService",
  "message": "Creating student: akhilesh@example.com",
  "thread": "http-nio-8080-exec-1"
}
```

### Obfuscated Fields (via Logbook)

Parameters: `password`, `token`, `secret`
Headers: `Authorization`, `Cookie`, `X-Api-Key`

### Excluded from HTTP Logging

`/actuator/**`, `/swagger-ui/**`, `/api-docs/**`, `/v3/api-docs/**`, `/favicon.ico`

### View Logs

```bash
# Docker
docker logs academy-backend -f

# Pretty-print JSON logs
docker logs academy-backend -f | jq

# Local
./gradlew bootRun | jq
```

Compatible with ELK Stack, Splunk, CloudWatch, Datadog, and Loki.

---

## Project Layout

```
academy-backend/
├── README.md
├── build.gradle / settings.gradle / gradle.properties
├── gradlew / gradlew.bat
├── docker-compose.yml            # Full stack — run with: docker-compose up --build
├── modules/
│   ├── academy-api/              # REST controllers, JWT security, configuration
│   │   └── src/main/
│   │       ├── java/com/academy/
│   │       │   ├── controller/   # OpenAPI-generated interface implementations
│   │       │   ├── security/     # JWT filter, UserDetailsService
│   │       │   └── config/       # Redis, Kafka, Security, OpenAPI configs
│   │       └── resources/
│   │           ├── application.yml
│   │           └── openapi.yaml  # Source of truth for API contracts
│   ├── academy-service/          # Business logic
│   │   └── java/com/academy/
│   │       ├── service/          # Service layer with caching annotations
│   │       ├── mapper/           # MapStruct DTO ↔ entity mappers
│   │       ├── aspect/           # AOP: distributed lock interceptor
│   │       └── annotation/       # @WithLock custom annotation
│   ├── academy-common/           # Shared across modules
│   │   └── java/com/academy/
│   │       ├── entity/           # JPA entities
│   │       ├── dto/              # Data transfer objects
│   │       ├── repository/       # Spring Data JPA repositories
│   │       └── exception/        # Exception hierarchy + global handler
│   ├── academy-kafka-producer/   # Domain event publishers
│   └── academy-kafka-consumer/   # Audit event consumers
├── frontend/                     # React 18 + TypeScript UI
├── docker/                       # Dockerfiles, compose override for local dev, init scripts
│   ├── Dockerfile                # Backend multi-stage build
│   ├── Dockerfile.kafka-consumer
│   ├── docker-compose.dev.yml    # Infra only — for local ./gradlew bootRun
│   ├── kafka/                    # Kafka topic creation script
│   └── mysql/                    # MySQL init SQL (schema + seed data)
├── docs/                         # All project documentation
├── scripts/                      # Utility shell scripts
└── postman/                      # Postman collection for API testing
```

---

## Health & Metrics

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application, DB, Redis, Kafka health |
| `/actuator/metrics` | JVM, HTTP, cache metrics |
| `/actuator/prometheus` | Prometheus-format metrics |

---

## Testing

```bash
# All tests
./gradlew test

# Integration tests only
./gradlew test --tests "*Integration*"

# Unit tests only (exclude integration)
./gradlew test --tests "*Test" --exclude-tests "*Integration*"

# Clean run
./gradlew clean test
```

### Testing Tools

- **Postman**: Import `postman/Academy.postman_collection.json`; set `base_url` and `jwt_token` environment variables
- **Swagger UI**: http://localhost:8080/swagger-ui.html — click Authorize, enter `Bearer <token>`

---

## Troubleshooting

### Application Won't Start

```bash
# MySQL not ready?
docker ps | grep mysql
docker exec -it academy-mysql mysql -uroot -prootpassword -e "SELECT 1"

# Redis not reachable?
docker exec -it academy-redis redis-cli ping

# Kafka not up?
docker exec -it academy-kafka kafka-topics --list --bootstrap-server localhost:9092

# Port conflicts?
lsof -i :8080   # application
lsof -i :3306   # MySQL
lsof -i :6379   # Redis
lsof -i :9092   # Kafka
```

### View Container Logs

```bash
docker logs academy-backend -f
docker logs academy-mysql -f
docker logs academy-redis -f
docker logs academy-kafka -f
```

### Database Access

```bash
docker exec -it academy-mysql mysql -uroot -prootpassword academy_db
docker exec -it academy-mysql mysql -uroot -prootpassword academy_db -e "SELECT * FROM students;"
```

### JWT Token Issues

```bash
# Get a fresh token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin@academy.com", "password": "password123"}'
```

### Full Reset

```bash
docker-compose down -v
docker-compose up --build
```

---

## Documentation

- **Technical Architecture:** [PROJECT_DOCUMENTATION.md](./docs/PROJECT_DOCUMENTATION.md)
- **Project Report:** [PROJECT_REPORT.md](./docs/PROJECT_REPORT.md)
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

---

*This project is for educational purposes.*
