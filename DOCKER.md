# Docker Setup Guide

This guide explains how to build and run the Academy Backend application using Docker.

## Prerequisites

- Docker Desktop (or Docker Engine + Docker Compose)
- At least 4GB of available RAM
- Ports 8080, 3306, 9092, 2181 available

## Quick Start

### 1. Build and Start All Services

```bash
docker-compose up --build
```

This will:
- Build the Academy Backend application
- Start MySQL database
- Start Kafka and Zookeeper
- Start the backend application

### 2. Check Service Status

```bash
docker-compose ps
```

### 3. View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f academy-backend
docker-compose logs -f mysql
docker-compose logs -f kafka
```

### 4. Stop All Services

```bash
docker-compose down
```

### 5. Stop and Remove Volumes (Clean Slate)

```bash
docker-compose down -v
```

## Services

### Academy Backend
- **Port**: 8080
- **Health Check**: http://localhost:8080/actuator/health
- **API Docs**: http://localhost:8080/swagger-ui.html
- **API Docs JSON**: http://localhost:8080/api-docs

### MySQL Database
- **Port**: 3306
- **Database**: academy_db
- **Username**: root
- **Password**: rootpassword
- **Data Persistence**: Stored in Docker volume `mysql-data`

### Kafka
- **Port**: 9092
- **Topics**: Auto-created on first use
  - `student.registered`
  - `batch.created`
  - `mentor.session.created`

### Zookeeper
- **Port**: 2181
- **Purpose**: Required by Kafka for coordination

## Building the Application

### Build Only (No Run)

```bash
docker-compose build
```

### Build Without Cache

```bash
docker-compose build --no-cache
```

## Running in Detached Mode

```bash
docker-compose up -d --build
```

## Accessing Services

### Application API
```bash
curl http://localhost:8080/actuator/health
```

### MySQL Database
```bash
docker exec -it academy-mysql mysql -uroot -prootpassword academy_db
```

### Kafka Topics
```bash
# List topics
docker exec -it academy-kafka kafka-topics --bootstrap-server localhost:9092 --list

# Describe topic
docker exec -it academy-kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic student.registered
```

## Troubleshooting

### Application Won't Start

1. **Check if MySQL is ready:**
   ```bash
   docker-compose logs mysql
   ```

2. **Check if Kafka is ready:**
   ```bash
   docker-compose logs kafka
   ```

3. **Check application logs:**
   ```bash
   docker-compose logs academy-backend
   ```

### Database Connection Issues

1. **Verify MySQL is healthy:**
   ```bash
   docker-compose ps mysql
   ```

2. **Check MySQL logs:**
   ```bash
   docker-compose logs mysql
   ```

3. **Restart MySQL:**
   ```bash
   docker-compose restart mysql
   ```

### Kafka Connection Issues

1. **Verify Kafka is healthy:**
   ```bash
   docker-compose ps kafka
   ```

2. **Check Kafka logs:**
   ```bash
   docker-compose logs kafka
   ```

3. **Restart Kafka:**
   ```bash
   docker-compose restart kafka
   ```

### Port Already in Use

If ports are already in use, you can:

1. **Stop conflicting services** using those ports
2. **Modify ports** in `docker-compose.yml`:
   ```yaml
   ports:
     - "8081:8080"  # Change host port
   ```

### Clean Rebuild

If you encounter issues, try a clean rebuild:

```bash
# Stop and remove everything
docker-compose down -v

# Remove old images
docker rmi academy-backend_academy-backend

# Rebuild from scratch
docker-compose up --build
```

## Development Workflow

### Rebuild After Code Changes

```bash
# Rebuild and restart
docker-compose up --build academy-backend

# Or restart only
docker-compose restart academy-backend
```

### View Real-time Logs

```bash
docker-compose logs -f academy-backend
```

### Execute Commands in Container

```bash
# Access shell
docker exec -it academy-backend sh

# Run specific command
docker exec -it academy-backend java -version
```

## Environment Variables

You can override environment variables by creating a `.env` file:

```env
SPRING_DATASOURCE_PASSWORD=yourpassword
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

Or modify `docker-compose.yml` directly.

## Health Checks

All services have health checks configured:

- **MySQL**: Checks if database is accepting connections
- **Kafka**: Checks if broker is responding
- **Backend**: Checks actuator health endpoint

## Data Persistence

- **MySQL data**: Persisted in Docker volume `mysql-data`
- **Kafka data**: Stored in container (ephemeral by default)

To persist Kafka data, add volumes to the kafka service in `docker-compose.yml`.

## Performance Tuning

For better performance, you can:

1. **Increase memory limits** in `docker-compose.yml`:
   ```yaml
   deploy:
     resources:
       limits:
         memory: 2G
   ```

2. **Adjust JVM options** in Dockerfile:
   ```dockerfile
   ENTRYPOINT ["java", "-Xmx1g", "-Xms512m", "-jar", "app.jar"]
   ```

## Production Considerations

For production deployment:

1. Use environment-specific configuration files
2. Set up proper secrets management
3. Configure SSL/TLS for database connections
4. Use external Kafka cluster
5. Set up proper monitoring and logging
6. Configure resource limits
7. Use Docker secrets for passwords

