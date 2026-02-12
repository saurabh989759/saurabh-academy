# Quick Start Guide

## 🚀 Start Everything with Docker Compose

```bash
# From project root
docker-compose -f docker-compose.infrastructure.yml up --build
```

This will start:
- ✅ MySQL (port 3306)
- ✅ Redis (port 6379)
- ✅ Kafka + Zookeeper (port 9092)
- ✅ Backend API (port 8080)
- ✅ Frontend (port 80)

## 📍 Access Points

- **Frontend**: http://localhost
- **Backend API**: http://localhost/api
- **Backend Direct**: http://localhost:8080
- **WebSocket**: ws://localhost/ws

## 🔍 Check Status

```bash
# View all services
docker-compose -f docker-compose.infrastructure.yml ps

# View logs
docker-compose -f docker-compose.infrastructure.yml logs -f

# View specific service logs
docker-compose -f docker-compose.infrastructure.yml logs frontend
docker-compose -f docker-compose.infrastructure.yml logs backend
```

## 🛠️ Common Issues

### Port 80 Already in Use

```bash
# Find what's using port 80
sudo lsof -i :80

# Or change port in docker-compose.infrastructure.yml:
# Change "80:80" to "8081:80"
```

### Frontend Build Fails

```bash
# Check frontend logs
docker-compose -f docker-compose.infrastructure.yml logs frontend

# Rebuild frontend only
docker-compose -f docker-compose.infrastructure.yml build --no-cache frontend
```

### Backend Not Starting

```bash
# Check backend logs
docker-compose -f docker-compose.infrastructure.yml logs backend

# Check if infrastructure is ready
docker-compose -f docker-compose.infrastructure.yml ps
```

## 🧪 Test the Setup

```bash
# Test backend health
curl http://localhost:8080/actuator/health

# Test frontend
curl http://localhost/health

# Test API through frontend proxy
curl http://localhost/api/actuator/health
```

## 🔄 Restart Services

```bash
# Stop all
docker-compose -f docker-compose.infrastructure.yml down

# Start again
docker-compose -f docker-compose.infrastructure.yml up
```

## 📝 Login Credentials

- **Email**: admin@academy.com
- **Password**: password123

## 🎯 Next Steps

1. Open http://localhost in your browser
2. Login with credentials above
3. Navigate through Students, Batches, Classes, Mentors, Sessions
4. All APIs are consumed with realtime updates via WebSocket

