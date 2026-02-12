# Running Backend Manually

## Prerequisites
- Java 21 or higher
- Gradle (or use ./gradlew)

## Step 1: Ensure Docker Services are Running
```bash
# Check services
docker-compose ps

# Start only infrastructure services (if not running)
docker-compose up -d mysql redis kafka zookeeper
```

## Step 2: Set Environment Variables (Optional)
```bash
export SPRING_PROFILES_ACTIVE=dev
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/academy_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=rootpassword
export SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export SPRING_REDIS_HOST=localhost
export SPRING_REDIS_PORT=6379
```

## Step 3: Run the Application

### Option A: Using Gradle Wrapper
```bash
./gradlew :modules:academy-api:bootRun
```

### Option B: Build and Run JAR
```bash
# Build the project
./gradlew clean build -x test

# Run the JAR
java -jar modules/academy-api/build/libs/academy-api-*.jar
```

### Option C: Run from IDE
1. Open the project in your IDE
2. Set active profile to `dev`
3. Run `AcademyBackendApplication` from `modules/academy-api/src/main/java/com/academy/AcademyBackendApplication.java`

## Step 4: Verify
- Application will start on: http://localhost:8080
- Health check: http://localhost:8080/actuator/health
- Swagger UI: http://localhost:8080/swagger-ui.html

## Connection Details
- **MySQL**: localhost:3306
  - Database: academy_db
  - Username: root
  - Password: rootpassword

- **Redis**: localhost:6379
  - No password

- **Kafka**: localhost:9092

## Troubleshooting
- If port 8080 is in use, change it in `application.yml`:
  ```yaml
  server:
    port: 8081
  ```

- To view Docker logs:
  ```bash
  docker-compose logs -f mysql
  docker-compose logs -f redis
  docker-compose logs -f kafka
  ```
