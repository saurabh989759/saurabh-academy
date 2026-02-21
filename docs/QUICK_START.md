# Quick Start

## Launch Everything

Run the entire stack — backend, frontend, MySQL, Redis, Kafka, and Zookeeper — with a single command:

```bash
docker-compose up --build
```

Once all containers are healthy, the system is available at:

| Service | Address |
|---------|---------|
| Frontend UI | http://localhost |
| Backend API | http://localhost:8080 |
| API via proxy | http://localhost/api |
| WebSocket | ws://localhost/ws |

---

## Login Credentials

| Field | Value |
|-------|-------|
| Email | admin@academy.com |
| Password | password123 |

---

## Check Container Status

```bash
# List all running containers
docker-compose ps

# Stream all logs
docker-compose logs -f

# Logs for a specific service
docker-compose logs frontend
docker-compose logs backend
```

---

## Smoke Tests

```bash
# Backend health
curl http://localhost:8080/actuator/health

# Frontend
curl http://localhost/health

# API through Nginx proxy
curl http://localhost/api/actuator/health
```

---

## Stop and Restart

```bash
# Bring everything down
docker-compose down

# Restart
docker-compose up
```

---

## Common Issues

**Port 80 is already in use**

```bash
# Find what's holding port 80
sudo lsof -i :80

# Or change the host port in docker-compose.yml:
# "80:80"  →  "8081:80"
```

**Frontend container fails to build**

```bash
docker-compose logs frontend
docker-compose build --no-cache frontend
```

**Backend not starting up**

```bash
docker-compose logs backend
docker-compose ps
```

---

## What's Next

1. Open http://localhost in your browser
2. Sign in with the credentials above
3. Explore Students, Batches, Classes, Mentors, and Sessions
4. All data mutations trigger real-time UI updates via WebSocket
