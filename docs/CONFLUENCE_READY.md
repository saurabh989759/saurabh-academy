# Academy Backend - Project Documentation

h1. Abstract

This project implements a comprehensive backend system for managing an academy with students, batches, mentors, classes, and mentor sessions. The system is built using Spring Boot 3.2.0 with Java 21, MySQL 8.0 for data persistence, and Apache Kafka for event-driven messaging. The application follows a layered architecture pattern with RESTful APIs, implements database migrations using Flyway, and includes comprehensive testing using Testcontainers.

h1. Project Description

h2. Objectives

The Academy Backend system aims to provide:

* Student Management: Registration, enrollment in batches, and tracking of academic progress
* Batch Management: Creation and management of student batches with associated classes
* Mentor Management: Tracking mentors and their sessions with students
* Class Scheduling: Management of classes and their association with batches
* Event-Driven Architecture: Asynchronous event processing using Kafka for domain events
* Scalable Architecture: Microservices-ready design with clear separation of concerns

h2. Relevance

This project demonstrates real-world backend development practices including RESTful API design, database schema design, event-driven architecture, containerization, automated testing, CI/CD pipelines, and API documentation.

h1. Requirement Gathering

h2. Functional Requirements

* Student Registration
** Register new students with personal and academic information
** Assign students to batches
** Track student batch history
** Support buddy system (pairing students)

* Batch Management
** Create and manage batches
** Associate batches with batch types (e.g., Full Stack, Data Science)
** Link classes to batches
** Track batch start dates and instructors

* Mentor Session Booking
** Book mentor sessions between students and mentors
** Track session duration and timings
** Support bidirectional ratings (student rates mentor, mentor rates student)

* Class Management
** Create classes with date, time, and instructor information
** Associate classes with multiple batches

* Event Publishing
** Publish student.registered event when a student is registered
** Publish mentor.session.created event when a session is booked
** Publish batch.created event when a batch is created

h2. Non-Functional Requirements

* Performance: API response time < 200ms for read operations
* Scalability: Horizontal scaling capability with stateless API design
* Reliability: Database transaction management and error handling
* Security: Input validation and placeholder for OAuth2/JWT authentication
* Maintainability: Layered architecture with comprehensive test coverage

h1. Class Diagrams

The system follows a layered architecture with Entity, Service, Controller, and Kafka layers.

*Entity Layer* includes: BatchType, Batch, Student, ClassEntity, Mentor, MentorSession, StudentBatchHistory, AuditEvent

*Service Layer* includes: StudentService, BatchService, MentorService, MentorSessionService, ClassService

*Controller Layer* includes REST controllers for each entity

*Kafka Layer* includes event producers and consumers

{image:class-diagram.png|width=800}

h1. Database Schema Design

The database consists of 9 main tables with proper relationships and indexes:

* batch_type: Stores batch type definitions
* batches: Stores batch information
* classes: Stores class/session information
* batches_classes: Join table for many-to-many relationship
* students: Stores student information
* mentors: Stores mentor information
* mentor_sessions: Stores mentor session bookings
* student_batch_history: Tracks historical batch assignments
* audit_events: Stores consumed Kafka events

{image:erd.png|width=800}

h1. Feature Development Process

h2. Key Feature: "Book a Mentor Session"

This section details the implementation flow:

# API Request: POST /api/mentor-sessions
# Controller Layer: Validates request and delegates to service
# Service Layer: Validates entities, creates session, publishes event
# Repository Layer: Persists to database
# Kafka Event Publication: Publishes mentor.session.created event
# Event Consumption: Stores event in audit_events table

{code}
API Request → Controller → Service → Repository → Database
                                    ↓
                              Kafka Producer → Kafka Topic
                                    ↓
                              Event Consumer → Audit Table
{code}

h1. Deployment Flow

h2. Local Development

# Prerequisites: Java 21, Maven, Docker
# Start Infrastructure: docker-compose -f docker-compose.dev.yml up -d
# Run Application: mvn spring-boot:run
# Access: http://localhost:8080

h2. Docker Deployment

The application is containerized using multi-stage Docker build. Docker Compose orchestrates MySQL, Zookeeper, Kafka, and the application.

h2. Production Deployment

Options include:
* Docker Compose for small scale
* Kubernetes for large scale
* Cloud Services (AWS RDS, MSK, ECS/EKS)

h1. Technologies Used

* Core Framework: Java 21, Spring Boot 3.2.0, Spring Data JPA, Spring Kafka
* Database: MySQL 8.0, Flyway, Hibernate
* Messaging: Apache Kafka, Zookeeper
* Mapping & Utilities: MapStruct, Lombok
* API Documentation: Springdoc OpenAPI, Swagger UI
* Testing: JUnit 5, Mockito, Testcontainers
* DevOps: Docker, Docker Compose, GitHub Actions
* Monitoring: Spring Boot Actuator

h1. Conclusion

h2. Key Takeaways

* Layered Architecture improves maintainability
* Event-Driven Design enables scalable systems
* Database Migrations ensure consistency
* Containerization simplifies deployment
* Testing Strategy ensures code quality

h2. Limitations

* Security: Authentication/authorization is placeholder
* Caching: No caching layer implemented
* Search: No full-text search capability
* File Storage: No file upload functionality

h2. Further Work

* Implement OAuth2/JWT authentication
* Add Redis caching
* Implement full-text search
* Add file upload functionality
* Set up monitoring and observability

h1. References

* Spring Boot Documentation
* Apache Kafka Documentation
* MySQL Documentation
* Flyway Documentation
* MapStruct Documentation
* Testcontainers Documentation
* Docker Documentation
* OpenAPI Specification

*Note: Replace with actual URLs if used in production documentation.*

