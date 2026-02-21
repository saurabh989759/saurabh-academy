# Multi-stage build for multi-module project

# Build stage
FROM gradle:8.10-jdk22 AS build
WORKDIR /app

# Copy Gradle files
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradlew.bat ./

# Copy all module source files
COPY modules ./modules
COPY src/main/resources/openapi.yaml ./src/main/resources/openapi.yaml

# Build all modules
RUN gradle clean build -x test --no-daemon

# Runtime stage for API module
FROM eclipse-temurin:22-jre-alpine
WORKDIR /app

RUN apk add --no-cache wget

# Copy API module JAR
COPY --from=build /app/modules/academy-api/build/libs/academy-api-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
