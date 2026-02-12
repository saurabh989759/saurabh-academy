# Academy Backend - Production-Ready Spring Boot Application

A production-ready Spring Boot backend application for managing an academy system with students, batches, classes, mentors, and mentor sessions. Built with Java 21, Spring Boot 3.x, MySQL, Redis caching, Kafka, and JWT authentication.

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Step-by-Step Setup](#step-by-step-setup)
- [API Usage Guide](#api-usage-guide)
- [Redis Caching Behavior](#redis-caching-behavior)
- [Testing APIs](#testing-apis)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Documentation](#documentation)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

- **Java 21** or higher
- **Gradle 8.10+** (or use the included Gradle Wrapper)
- **Docker and Docker Compose** (recommended)
- **MySQL 8.0+** (if running database locally)
- **Redis 7+** (if running Redis locally)
- **Kafka** (if running Kafka locally)

---

## Quick Start

### Using Docker Compose (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd academy-backend

# Start all services (MySQL, Redis, Kafka, and the application)
docker-compose up --build

# The application will be available at http://localhost:8080
```

### Running Locally

```bash
# Start MySQL, Redis, and Kafka using Docker Compose
docker-compose -f docker-compose.dev.yml up -d mysql redis kafka zookeeper

# Run the Spring Boot application
./gradlew bootRun
```

---

## Step-by-Step Setup

### 1. Clone Project

```bash
git clone <repository-url>
cd academy-backend
```

### 2. Start Infrastructure Services

**Option A: Using Docker Compose (Recommended)**

```bash
# Start all services
docker-compose up -d mysql redis kafka zookeeper

# Verify services are running
docker ps
```

**Option B: Manual Setup**

- Start MySQL on port 3306
- Start Redis on port 6379
- Start Kafka on port 9092

### 3. Verify Redis Connection

```bash
# Test Redis connection
docker exec -it academy-redis redis-cli ping
# Should return: PONG

# Check Redis keys (after running some APIs)
docker exec -it academy-redis redis-cli KEYS "*"
```

### 4. Run Spring Boot Application

```bash
# Using Gradle
./gradlew bootRun

# Or build and run JAR
./gradlew clean build
java -jar modules/academy-api/build/libs/academy-api-1.0.0.jar
```

### 5. Verify Application Health

```bash
# Health check
curl http://localhost:8080/actuator/health

# Expected response:
# {"status":"UP","components":{"db":{"status":"UP"},"redis":{"status":"UP"},"kafka":{"status":"UP"}}}
```

### 6. Access API Documentation

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

---

## API Usage Guide

### Authentication

**All APIs (except `/api/auth/**`) require JWT authentication.**

#### Step 1: Login to Get JWT Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@academy.com",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

**Save the token for subsequent requests:**
```bash
export JWT_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### Step 2: Use Token in API Requests

```bash
curl -X GET http://localhost:8080/api/students \
  -H "Authorization: Bearer $JWT_TOKEN"
```

---

### Student APIs

#### 1. Create Student (Cached on subsequent GET)

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

**Response (201):**
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

**Caching Behavior:**
- ✅ Response is NOT cached (POST operation)
- ✅ Cache is invalidated for `student` and `students` caches
- ✅ Subsequent GET requests will fetch from database and cache

#### 2. Get Student by ID (Cached)

```bash
curl -X GET http://localhost:8080/api/students/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**First Request:**
- ❌ Cache miss → Fetches from database
- ✅ Stores in Redis with key `student::student:1`
- ✅ TTL: 30 minutes

**Subsequent Requests (within 30 minutes):**
- ✅ Cache hit → Returns from Redis (faster)
- ⏱️ No database query

**Verify Cache:**
```bash
# Check Redis for cached student
docker exec -it academy-redis redis-cli KEYS "*student*"

# Check TTL
docker exec -it academy-redis redis-cli TTL "student::student:1"
```

**Response (200):**
```json
{
  "id": 1,
  "name": "Akhilesh Gupta",
  "email": "akhilesh@example.com",
  ...
}
```

#### 3. Get All Students (Cached)

```bash
# Get all students
curl -X GET "http://localhost:8080/api/students" \
  -H "Authorization: Bearer $JWT_TOKEN"

# Get students by batch
curl -X GET "http://localhost:8080/api/students?batchId=1" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**Caching Behavior:**
- ✅ Cached with key `students::all` or `students::batch:1`
- ✅ TTL: 30 minutes
- ✅ Empty results are NOT cached

#### 4. Get All Students (Paginated)

```bash
curl -X GET "http://localhost:8080/api/students/paged?page=0&size=20&sort=name,asc" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Akhilesh Gupta",
      ...
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "size": 20,
  "number": 0,
  "first": true,
  "last": false
}
```

#### 5. Update Student (Invalidates Cache)

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

**Caching Behavior:**
- ✅ Cache is invalidated for `student::student:1`
- ✅ All `students::*` list caches are invalidated
- ✅ Next GET request will fetch fresh data and cache it

#### 6. Delete Student (Invalidates Cache)

```bash
curl -X DELETE http://localhost:8080/api/students/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**Response (204):** No content

**Caching Behavior:**
- ✅ Cache is invalidated
- ✅ Student data removed from cache

---

### Batch APIs

#### 1. Create Batch

```bash
curl -X POST http://localhost:8080/api/batches \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "name": "FSD-2024-03",
    "startMonth": "2024-03-01",
    "currentInstructor": "John Instructor",
    "batchTypeId": 1,
    "classIds": [1, 2]
  }'
```

**Kafka Event:** Publishes `batch.created` event

#### 2. Get Batch by ID (Cached)

```bash
curl -X GET http://localhost:8080/api/batches/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**Caching:** ✅ TTL: 30 minutes

#### 3. Get All Batches (Paginated, Cached)

```bash
curl -X GET "http://localhost:8080/api/batches?page=0&size=10" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### 4. Update Batch

```bash
curl -X PUT http://localhost:8080/api/batches/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "name": "FSD-2024-03-Updated",
    "startMonth": "2024-03-01",
    "currentInstructor": "Jane Instructor",
    "batchTypeId": 1
  }'
```

#### 5. Delete Batch

```bash
curl -X DELETE http://localhost:8080/api/batches/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### 6. Assign Class to Batch

```bash
curl -X POST http://localhost:8080/api/batches/1/classes/2 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

---

### Class APIs

#### 1. Create Class

```bash
curl -X POST http://localhost:8080/api/classes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "name": "Introduction to Java",
    "date": "2024-03-15",
    "time": "10:00:00",
    "instructor": "Jane Smith"
  }'
```

#### 2. Get All Classes (Cached)

```bash
curl -X GET http://localhost:8080/api/classes \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### 3. Get Class by ID (Cached)

```bash
curl -X GET http://localhost:8080/api/classes/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### 4. Update Class

```bash
curl -X PUT http://localhost:8080/api/classes/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "name": "Advanced Java",
    "date": "2024-03-20",
    "time": "14:00:00",
    "instructor": "John Doe"
  }'
```

#### 5. Delete Class

```bash
curl -X DELETE http://localhost:8080/api/classes/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

---

### Mentor APIs

#### 1. Create Mentor

```bash
curl -X POST http://localhost:8080/api/mentors \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "name": "Alice Johnson",
    "currentCompany": "Tech Corp"
  }'
```

#### 2. Get All Mentors (Cached, TTL: 1 hour)

```bash
curl -X GET http://localhost:8080/api/mentors \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**Caching:** ✅ TTL: 1 hour (stable reference data)

#### 3. Get Mentor by ID (Cached, TTL: 1 hour)

```bash
curl -X GET http://localhost:8080/api/mentors/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### 4. Update Mentor

```bash
curl -X PUT http://localhost:8080/api/mentors/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "name": "Alice Johnson Updated",
    "currentCompany": "New Tech Corp"
  }'
```

#### 5. Delete Mentor

```bash
curl -X DELETE http://localhost:8080/api/mentors/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

---

### Mentor Session APIs

#### 1. Create Mentor Session

```bash
curl -X POST http://localhost:8080/api/mentor-sessions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "time": "2024-03-20T15:00:00Z",
    "durationMinutes": 60,
    "studentId": 1,
    "mentorId": 1
  }'
```

**Kafka Event:** Publishes `mentor.session.created` event

#### 2. Get All Mentor Sessions (Cached, TTL: 10 minutes)

```bash
curl -X GET http://localhost:8080/api/mentor-sessions \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**Caching:** ✅ TTL: 10 minutes (frequently changing data)

#### 3. Get Mentor Session by ID (Cached, TTL: 10 minutes)

```bash
curl -X GET http://localhost:8080/api/mentor-sessions/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

#### 4. Update Mentor Session

```bash
curl -X PUT http://localhost:8080/api/mentor-sessions/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "time": "2024-03-21T16:00:00Z",
    "durationMinutes": 90,
    "studentId": 1,
    "mentorId": 1,
    "studentRating": 5,
    "mentorRating": 4
  }'
```

#### 5. Delete Mentor Session

```bash
curl -X DELETE http://localhost:8080/api/mentor-sessions/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

---

## Redis Caching Behavior

### Cache Key Patterns

**Current Implementation (Spring Cache):**
- `student::student:1` - Individual student
- `students::batch:1` - Students by batch
- `students::all` - All students
- `batch::batch:1` - Individual batch
- `mentor::mentor:1` - Individual mentor (TTL: 1 hour)
- `mentorSession::mentorSession:1` - Individual session (TTL: 10 minutes)

### TTL Configuration

| Cache Name | TTL | Reason |
|------------|-----|--------|
| `mentorSessions`, `mentorSession` | 10 minutes | Frequently changing |
| `mentors`, `mentor`, `batchTypes`, `batchType` | 1 hour | Stable reference data |
| `students`, `student`, `batches`, `batch`, `classes`, `class` | 30 minutes | Default for transactional data |

### Cache Operations

**What Gets Cached:**
- ✅ GET operations (read-only)
- ✅ Non-null results
- ✅ Non-empty collections

**What Doesn't Get Cached:**
- ❌ POST/PUT/DELETE responses
- ❌ Null values
- ❌ Empty collections
- ❌ Error responses

### Monitoring Cache

```bash
# List all cache keys
docker exec -it academy-redis redis-cli KEYS "*"

# List student cache keys
docker exec -it academy-redis redis-cli KEYS "*student*"

# Check TTL of a key
docker exec -it academy-redis redis-cli TTL "student::student:1"

# Get cached value
docker exec -it academy-redis redis-cli GET "student::student:1"

# Monitor Redis commands in real-time
docker exec -it academy-redis redis-cli MONITOR

# Get Redis info
docker exec -it academy-redis redis-cli INFO
```

---

## Testing APIs

### Using cURL (Command Line)

All examples above use cURL. Save your JWT token:

```bash
export JWT_TOKEN="your-token-here"
```

### Using Postman

1. Import the Postman collection: `postman/Academy.postman_collection.json`
2. Set up environment variables:
   - `base_url`: `http://localhost:8080`
   - `jwt_token`: (from login response)
3. Run requests from the collection

### Using Swagger UI

1. Open http://localhost:8080/swagger-ui.html
2. Click "Authorize" button
3. Enter: `Bearer <your-jwt-token>`
4. Test APIs directly from the UI

### Verify Caching

**Test Cache Hit:**
```bash
# First request (cache miss)
time curl -X GET http://localhost:8080/api/students/1 \
  -H "Authorization: Bearer $JWT_TOKEN"

# Second request (cache hit - should be faster)
time curl -X GET http://localhost:8080/api/students/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**Test Cache Invalidation:**
```bash
# Get student (cached)
curl -X GET http://localhost:8080/api/students/1 \
  -H "Authorization: Bearer $JWT_TOKEN"

# Update student (invalidates cache)
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{"name": "Updated Name", ...}'

# Next GET will fetch from database and cache again
curl -X GET http://localhost:8080/api/students/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

---

## Project Structure

```
academy-backend/
├── modules/
│   ├── academy-api/              # API layer (controllers, security, config)
│   │   ├── src/main/java/com/academy/
│   │   │   ├── controller/       # REST controllers (OpenAPI-generated)
│   │   │   ├── security/         # JWT authentication
│   │   │   └── config/            # Redis, Kafka, Security config
│   │   └── src/main/resources/
│   │       ├── application.yml    # Configuration
│   │       └── openapi.yaml       # OpenAPI specification
│   ├── academy-service/          # Business logic
│   │   └── src/main/java/com/academy/
│   │       ├── service/           # Service layer
│   │       ├── mapper/            # MapStruct mappers
│   │       ├── aspect/            # AOP aspects (locking)
│   │       └── annotation/        # Custom annotations
│   ├── academy-common/           # Shared components
│   │   └── src/main/java/com/academy/
│   │       ├── entity/            # JPA entities
│   │       ├── dto/               # Data transfer objects
│   │       ├── repository/        # JPA repositories
│   │       └── exception/        # Exception handling
│   ├── academy-kafka-producer/   # Kafka event producers
│   └── academy-kafka-consumer/   # Kafka event consumers
├── docker-compose.yml             # Docker Compose configuration
├── Dockerfile                     # Application Dockerfile
├── README.md                      # This file
├── PROJECT_DOCUMENTATION.md       # Complete technical documentation
└── postman/                       # Postman collection
```

---

## Technologies Used

- **Java 21** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data persistence
- **Spring Data Redis** - Caching layer
- **Spring Kafka** - Event-driven messaging
- **Spring Security** - Authentication & authorization
- **MySQL 8.0** - Relational database
- **Redis 7** - In-memory cache
- **Apache Kafka** - Message broker
- **Flyway** - Database migrations
- **MapStruct** - DTO mapping
- **Lombok** - Boilerplate reduction
- **OpenAPI/Swagger** - API documentation
- **Docker & Docker Compose** - Containerization
- **JUnit & Mockito** - Testing
- **Testcontainers** - Integration testing

---

## Documentation

- **Complete Technical Documentation:** See [PROJECT_DOCUMENTATION.md](./PROJECT_DOCUMENTATION.md)
- **API Documentation:** http://localhost:8080/swagger-ui.html
- **OpenAPI Spec:** http://localhost:8080/api-docs

---

## Troubleshooting

### Application Won't Start

1. **Database Connection Issues:**
   ```bash
   # Check MySQL is running
   docker ps | grep mysql
   
   # Test MySQL connection
   docker exec -it academy-mysql mysql -uroot -prootpassword -e "SELECT 1"
   ```

2. **Redis Connection Issues:**
   ```bash
   # Check Redis is running
   docker ps | grep redis
   
   # Test Redis connection
   docker exec -it academy-redis redis-cli ping
   ```

3. **Kafka Connection Issues:**
   ```bash
   # Check Kafka is running
   docker ps | grep kafka
   
   # List Kafka topics
   docker exec -it academy-kafka kafka-topics --list --bootstrap-server localhost:9092
   ```

4. **Port Conflicts:**
   ```bash
   # Check if ports are in use
   lsof -i :8080  # Application
   lsof -i :3306  # MySQL
   lsof -i :6379  # Redis
   lsof -i :9092  # Kafka
   ```

### Viewing Logs

```bash
# Application logs
docker logs academy-backend -f

# MySQL logs
docker logs academy-mysql -f

# Redis logs
docker logs academy-redis -f

# Kafka logs
docker logs academy-kafka -f
```

### Database Access

```bash
# Connect to MySQL
docker exec -it academy-mysql mysql -uroot -prootpassword academy_db

# Run SQL queries
docker exec -it academy-mysql mysql -uroot -prootpassword academy_db -e "SELECT * FROM students;"
```

### Reset Everything

```bash
# Stop and remove all containers and volumes
docker-compose down -v

# Start fresh
docker-compose up --build
```

### Cache Issues

```bash
# Clear all Redis cache
docker exec -it academy-redis redis-cli FLUSHALL

# Clear specific cache pattern
docker exec -it academy-redis redis-cli --scan --pattern "*student*" | xargs docker exec -it academy-redis redis-cli DEL
```

### JWT Token Issues

```bash
# Get new token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin@academy.com", "password": "password123"}'

# Validate token
curl -X POST http://localhost:8080/api/auth/validate \
  -H "Content-Type: application/json" \
  -d '{"token": "your-token-here"}'
```

---

## Running Tests

### Unit Tests

```bash
./gradlew test
```

### Integration Tests

```bash
./gradlew test --tests "*Integration*"
```

### All Tests

```bash
./gradlew clean test
```

---

## Logging

### Logging Configuration

The application uses **Logback** with **JSON formatting** for structured logging and **Logbook** for HTTP request/response logging.

#### Logback Configuration

**Location:** `modules/academy-api/src/main/resources/logback-spring.xml`

**Features:**
- JSON formatted console output
- Structured logging for easy parsing
- Logbook integration for HTTP traces
- Configurable log levels per package

**Log Levels:**
- `com.academy` - INFO (application logs)
- `org.zalando.logbook.Logbook` - TRACE (HTTP request/response)
- `org.springframework` - WARN (framework logs)
- `org.hibernate` - WARN (database logs)

#### Logbook HTTP Logging

**Configuration:** `modules/academy-api/src/main/resources/application.yml`

**Features:**
- JSON format for HTTP logs
- Sensitive data obfuscation (passwords, tokens, Authorization headers)
- Excludes actuator and Swagger endpoints
- INFO level logging

**Obfuscated Fields:**
- Parameters: `password`, `token`, `secret`
- Headers: `Authorization`, `Cookie`, `X-Api-Key`

**Excluded Paths:**
- `/actuator/**`
- `/swagger-ui/**`
- `/api-docs/**`
- `/v3/api-docs/**`
- `/favicon.ico`

#### Log Format

**Application Logs (JSON):**
```json
{
  "timestamp": "2024-11-29T12:00:00.000+05:30",
  "level": "INFO",
  "logger": "com.academy.service.StudentService",
  "message": "Creating student: akhilesh@example.com",
  "thread": "http-nio-8080-exec-1"
}
```

**HTTP Request/Response Logs (Logbook):**
```json
{
  "origin": "remote",
  "type": "request",
  "correlation": "abc123",
  "protocol": "HTTP/1.1",
  "remote": "127.0.0.1",
  "method": "POST",
  "uri": "/api/students",
  "headers": {
    "Authorization": "***",
    "Content-Type": ["application/json"]
  },
  "body": "{\"name\":\"...\",\"password\":\"***\"}"
}
```

#### Viewing Logs

**Docker:**
```bash
# Application logs
docker logs academy-backend -f

# Follow logs with JSON formatting
docker logs academy-backend -f | jq
```

**Local:**
```bash
# Logs are output to console in JSON format
# Can be piped to log aggregation tools
./gradlew bootRun | jq
```

#### Log Aggregation

The JSON format enables easy integration with:
- **ELK Stack** (Elasticsearch, Logstash, Kibana)
- **Splunk**
- **CloudWatch Logs**
- **Datadog**
- **Loki** (Grafana)

#### Logging Best Practices

1. **Use `@Slf4j` annotation** - All classes use Lombok's `@Slf4j`
2. **Structured logging** - JSON format for machine parsing
3. **Sensitive data** - Automatically obfuscated by Logbook
4. **Log levels** - Appropriate levels (INFO for business, DEBUG for troubleshooting)
5. **Correlation IDs** - Logbook provides correlation IDs for request tracing

---

## Health & Metrics

- **Health Check:** http://localhost:8080/actuator/health
- **Metrics:** http://localhost:8080/actuator/metrics
- **Prometheus:** http://localhost:8080/actuator/prometheus

---

## Kafka Topics

The application uses the following Kafka topics:
- `student.registered` - Published when a student is registered
- `mentor.session.created` - Published when a mentor session is created
- `batch.created` - Published when a batch is created

### View Kafka Messages

```bash
# List topics
docker exec -it academy-kafka kafka-topics --list --bootstrap-server localhost:9092

# Consume messages from a topic
docker exec -it academy-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic student.registered \
  --from-beginning
```

---

## License

This project is for educational purposes.

---

**For complete technical documentation, see [PROJECT_DOCUMENTATION.md](./PROJECT_DOCUMENTATION.md)**
