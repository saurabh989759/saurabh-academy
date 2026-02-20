# Docker Reference Guide

Everything you need to build, run, and manage the Academy backend with Docker.

---

## System Requirements

- Docker Desktop or Docker Engine + Docker Compose
- At least 4 GB of free RAM
- Free ports: 8080, 3306, 9092, 2181

---

## Starting the Stack

### Build and Launch All Services

```bash
docker-compose up --build
```

Brings up:
- Academy Backend API
- MySQL database
- Apache Kafka + Zookeeper

### Start in the Background

```bash
docker-compose up -d --build
```

### Check What's Running

```bash
docker-compose ps
```

### Shut Down

```bash
# Stop containers (keep volumes)
docker-compose down

# Stop and remove all data volumes
docker-compose down -v
```

---

## Service Inventory

### Academy Backend
| Property | Value |
|----------|-------|
| Port | 8080 |
| Health | http://localhost:8080/actuator/health |
| Swagger | http://localhost:8080/swagger-ui.html |
| OpenAPI | http://localhost:8080/api-docs |

### MySQL Database
| Property | Value |
|----------|-------|
| Port | 3306 |
| Database | academy_db |
| Username | root |
| Password | rootpassword |
| Data | Stored in `mysql-data` Docker volume |

### Apache Kafka
| Property | Value |
|----------|-------|
| Port | 9092 |
| Topics | `student.registered`, `batch.created`, `mentor.session.created` |

### Zookeeper
| Property | Value |
|----------|-------|
| Port | 2181 |
| Purpose | Kafka coordination |

---

## Logs

```bash
# All services at once
docker-compose logs -f

# One service at a time
docker-compose logs -f academy-backend
docker-compose logs -f mysql
docker-compose logs -f kafka
```

---

## Build Operations

```bash
# Build images without starting
docker-compose build

# Force a clean build (no layer cache)
docker-compose build --no-cache

# Rebuild and restart a single service
docker-compose up --build academy-backend
```

---

## Connecting to Services

### Backend API

```bash
curl http://localhost:8080/actuator/health
```

### MySQL

```bash
docker exec -it academy-mysql mysql -uroot -prootpassword academy_db
```

### Kafka Topics

```bash
# List all topics
docker exec -it academy-kafka kafka-topics \
  --bootstrap-server localhost:9092 --list

# Inspect a specific topic
docker exec -it academy-kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --describe --topic student.registered
```

---

## Container Shell Access

```bash
# Open a shell inside the backend container
docker exec -it academy-backend sh

# Run a quick command
docker exec -it academy-backend java -version
```

---

## Environment Variables

Override defaults by creating a `.env` file in the project root:

```env
SPRING_DATASOURCE_PASSWORD=yourpassword
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

Alternatively, edit the `environment:` section of `docker-compose.yml` directly.

---

## Health Checks

All services declare health checks in Docker Compose:

| Service | Check |
|---------|-------|
| MySQL | Accepts connections |
| Kafka | Broker responds |
| Backend | `/actuator/health` returns `UP` |

---

## Data Persistence

- **MySQL data** → persisted in Docker volume `mysql-data`
- **Kafka data** → stored in the container (ephemeral by default)

To persist Kafka data across restarts, add a named volume to the kafka service definition in `docker-compose.yml`.

---

## Troubleshooting

### Application Fails to Start

**Step 1 — Check MySQL readiness:**
```bash
docker-compose logs mysql
docker-compose ps mysql
```

**Step 2 — Check Kafka readiness:**
```bash
docker-compose logs kafka
docker-compose ps kafka
```

**Step 3 — Check application logs:**
```bash
docker-compose logs academy-backend
```

### Database Won't Connect

```bash
# Verify MySQL is healthy
docker-compose ps mysql

# Restart MySQL
docker-compose restart mysql
```

### Kafka Issues

```bash
# Verify Kafka is healthy
docker-compose ps kafka

# Restart Kafka
docker-compose restart kafka
```

### Port Already in Use

Edit `docker-compose.yml` to remap the host port:

```yaml
ports:
  - "8081:8080"   # maps host port 8081 → container port 8080
```

### Full Clean Rebuild

When in doubt, wipe everything and start fresh:

```bash
docker-compose down -v
docker rmi academy-backend_academy-backend
docker-compose up --build
```

---

## Performance Tuning

**Increase container memory:**

```yaml
deploy:
  resources:
    limits:
      memory: 2G
```

**Tune JVM heap in Dockerfile:**

```dockerfile
ENTRYPOINT ["java", "-Xmx1g", "-Xms512m", "-jar", "app.jar"]
```

---

## Development Workflow

After changing backend code:

```bash
# Rebuild and restart only the backend
docker-compose up --build academy-backend

# Or just restart (if no code changes, image already built)
docker-compose restart academy-backend

# Watch live output
docker-compose logs -f academy-backend
```

---

## Production Checklist

Before going to production:

1. Use environment-specific config files or secrets management (e.g., Vault)
2. Enable SSL/TLS for all service connections
3. Switch to an external Kafka cluster
4. Configure resource limits on all containers
5. Replace plaintext passwords with Docker secrets
6. Set up proper log aggregation (ELK, Datadog, etc.)
7. Add monitoring and alerting
