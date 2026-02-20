# Redis Cache Serialization Fix

## Problem

List endpoints (`GET /api/classes`, `GET /api/mentors`, `GET /api/mentor-sessions`,
`GET /api/batch-types`, `GET /api/students`) returned `400 Bad Request` on every
**second** request. The first request always succeeded; the error only appeared after
the result was written to Redis and read back on the next call.

**Error message from the backend logs:**

```
Could not read JSON: Unexpected token (START_OBJECT), expected VALUE_STRING:
need String, Number of Boolean value that contains type id (for subtype of java.lang.Object)
at [Source: (byte[])"[{"@class":"com.academy.dto.ClassDTO","id":1,...
```

Individual by-ID endpoints (`GET /api/classes/1`, etc.) were unaffected.

---

## Root Cause

The `RedisConfig` configured Jackson with:

```java
objectMapper.activateDefaultTyping(
    LaissezFaireSubTypeValidator.instance,
    ObjectMapper.DefaultTyping.NON_FINAL,
    JsonTypeInfo.As.PROPERTY       // <-- the problem
);
```

`JsonTypeInfo.As.PROPERTY` embeds the Java type as a `@class` field inside each
JSON object — which works fine for plain objects:

```json
{"@class": "com.academy.dto.ClassDTO", "id": 1, "name": "Intro to Spring Boot"}
```

It does **not** work for JSON arrays. A `List<ClassDTO>` is a JSON array, and JSON
arrays cannot carry properties. Jackson stored the list as:

```json
[{"@class": "com.academy.dto.ClassDTO", ...}, {"@class": "com.academy.dto.ClassDTO", ...}]
```

The outer array had no type metadata. When `GenericJackson2JsonRedisSerializer`
tried to deserialize this back as `Object`, Jackson's polymorphic type resolver
expected a type-name string but found an object `{` — hence the error.

By-ID methods return a single DTO object (not a list), so the `@class` property
embedded correctly and deserialization worked without issues.

---

## Fix

### 1. Removed `@Cacheable` from all list-returning service methods

List endpoints now always read directly from the database. The datasets are small
(under 10 records each), so the query cost is negligible.

**Files changed:**

| File | Change |
|------|--------|
| `ClassService.java` | Removed `@Cacheable` from `getAllClasses()` |
| `MentorService.java` | Removed `@Cacheable` from `getAllMentors()` |
| `MentorSessionService.java` | Removed `@Cacheable` from `getAllSessions()` |
| `StudentService.java` | Removed `@Cacheable` from `getAllStudents()` |
| `BatchTypeService.java` | Removed `@Cacheable` from `getAllBatchTypes()` |

By-ID methods (`getClassById`, `getMentorById`, etc.) retain their `@Cacheable`
annotations and continue to cache correctly.

### 2. Changed type inclusion to `WRAPPER_ARRAY` in `RedisConfig`

Updated both the `RedisTemplate` and `CacheManager` ObjectMapper configurations:

```java
// Before
objectMapper.activateDefaultTyping(
    LaissezFaireSubTypeValidator.instance,
    ObjectMapper.DefaultTyping.NON_FINAL,
    JsonTypeInfo.As.PROPERTY
);

// After
objectMapper.activateDefaultTyping(
    LaissezFaireSubTypeValidator.instance,
    ObjectMapper.DefaultTyping.NON_FINAL,
    JsonTypeInfo.As.WRAPPER_ARRAY
);
```

`WRAPPER_ARRAY` wraps each typed value as `["fully.qualified.ClassName", {...}]`,
which is a valid two-element JSON array that Jackson can reliably read back.

---

## What Still Gets Cached

| Method | Cache | TTL |
|--------|-------|-----|
| `getClassById(id)` | `class::class:{id}` | 30 min |
| `getMentorById(id)` | `mentor::mentor:{id}` | 1 hour |
| `getMentorSessionById(id)` | `mentorSession::mentorSession:{id}` | 10 min |
| `getBatchTypeById(id)` | `batchType::batchType:{id}` | 1 hour |
| `getStudentById(id)` | `student::student:{id}` | 30 min |
| `getBatchById(id)` | `batch::batch:{id}` | 30 min |

List endpoints (`getAll*`) hit the database directly on every request.

---

## Important: After Redeployment

If Redis is kept running across a backend rebuild (e.g., during development), old
cache entries in a different serialization format will cause the same 400 errors.

**Always flush Redis after rebuilding the backend:**

```bash
docker exec academy-redis redis-cli FLUSHALL
```

Or wipe volumes entirely for a clean start:

```bash
docker-compose down -v && docker-compose up --build
```
