# OpenAPI-Driven Controller Architecture

## Design Overview

Controllers in this project implement auto-generated OpenAPI interfaces rather than hand-written method signatures. This means the OpenAPI YAML specification is the single source of truth for all API contracts — changing the spec automatically updates the interface that controllers must implement.

```
HTTP Request
    │
    ▼
Generated API Interface (e.g., StudentsApi, BatchesApi)
    │
    ▼
Controller Implementation (implements generated interface)
    │
    ▼
Service Layer (existing business logic)
    │
    ▼
Repository → Database
```

---

## What OpenAPI Generator Produces

### API Interfaces

Each domain has a generated interface that controllers implement:

| Interface | Controller |
|-----------|------------|
| `StudentsApi` | `StudentController` |
| `BatchesApi` | `BatchController` |
| `ClassesApi` | `ClassController` |
| `MentorsApi` | `MentorController` |
| `MentorSessionsApi` | `MentorSessionController` |

### Request Models (`*Input`)

| Generated Class | Used For |
|----------------|----------|
| `StudentInput` | POST/PUT `/api/students` |
| `BatchInput` | POST/PUT `/api/batches` |
| `ClassInput` | POST/PUT `/api/classes` |
| `MentorInput` | POST/PUT `/api/mentors` |
| `MentorSessionInput` | POST/PUT `/api/mentor-sessions` |

### Response Models

`Student`, `Batch`, `ModelClass`, `Mentor`, `MentorSession`, `PageStudent`, `PageBatch`

---

## What Stays Manual

The following are NOT generated — they remain hand-written and unchanged by spec updates:

| Component | Location |
|-----------|----------|
| Services | Business logic + caching + locking |
| Repositories | Spring Data JPA interfaces |
| Entities | JPA entity classes |
| Internal DTOs | Service ↔ controller boundary objects |
| MapStruct Mappers | DTO ↔ entity conversion |

---

## Controller Responsibility

Each controller follows this pattern:

1. Implements the generated API interface
2. Receives a generated `*Input` model from the HTTP layer
3. Maps it to an internal DTO for the service layer
4. Calls the service method
5. Maps the returned DTO to a generated response model
6. Returns the response model to the HTTP layer

### Example — `StudentController`

```java
@RestController
@RequiredArgsConstructor
@Slf4j
public class StudentController implements StudentsApi {

    private final StudentService studentService;
    private final ApiModelMapper mapper;

    @Override
    public ResponseEntity<Student> createStudent(StudentInput input) {
        log.info("POST /students — email={}", input.getEmail());
        StudentDTO dto = mapper.toStudentDTO(input);
        StudentDTO created = studentService.createStudent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toStudent(created));
    }
}
```

---

## Type Conversions at the Controller Boundary

Controllers translate between generated model types and internal DTO types:

| Conversion | Direction |
|------------|-----------|
| `StudentInput` ↔ `StudentDTO` | request → internal |
| `StudentDTO` ↔ `Student` | internal → response |
| `OffsetDateTime` ↔ `Instant` | time type bridging |
| `List<Long>` ↔ `Set<Long>` | collection type bridging |
| `String` ↔ `LocalTime` | time string parsing |

---

## Package Structure

```
com.academy/
├── generated/                    # Produced by OpenAPI Generator — do not edit
│   ├── api/                      # StudentsApi, BatchesApi, etc.
│   └── model/                    # StudentInput, Student, Batch, etc.
│
├── controller/                   # Hand-written implementations
│   ├── StudentController.java
│   ├── BatchController.java
│   ├── ClassController.java
│   ├── MentorController.java
│   └── MentorSessionController.java
│
├── service/                      # Business logic (unchanged by API updates)
├── repository/                   # Data access (unchanged by API updates)
├── entity/                       # JPA entities (unchanged by API updates)
├── dto/                          # Internal DTOs (unchanged by API updates)
└── mapper/                       # MapStruct mappers (unchanged by API updates)
```

---

## Advantages

| Benefit | Description |
|---------|-------------|
| API-First | Spec drives implementation, not the other way around |
| Type Safety | Generated interfaces enforce contract compliance at compile time |
| Simplicity | Controllers call services directly — no ports/adapters indirection |
| Maintainability | Update the spec, run the generator, compile — breakages surface immediately |
| Separation | Generated API models never leak into the service or persistence layer |

---

## Updating the API

```bash
# 1. Edit the OpenAPI specification
vim modules/academy-api/src/main/resources/openapi.yaml

# 2. Regenerate the interfaces and models
./gradlew generateOpenApi

# 3. Compile — the compiler will flag any controller that no longer satisfies the interface
./gradlew compileJava
```

Services remain untouched by this process.

---

## Verification Checklist

- [x] All controllers implement generated API interfaces
- [x] Request/response models come exclusively from generated classes
- [x] Controllers call service layer directly (no additional abstraction)
- [x] Build compiles without errors
- [x] All API endpoints verified working
