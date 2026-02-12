#!/bin/bash

# Script to build and run Academy Backend in Docker
# This script performs actual executable commands, not just echo statements

set -e

echo "=========================================="
echo "Academy Backend - Docker Setup"
echo "=========================================="

# Check if Docker is running (EXECUTABLE COMMAND)
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

echo "✅ Docker is running"

# Check if docker-compose is available (EXECUTABLE COMMAND)
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE_CMD="docker-compose"
elif docker compose version &> /dev/null; then
    DOCKER_COMPOSE_CMD="docker compose"
else
    echo "❌ docker-compose not found. Please install docker-compose."
    exit 1
fi

echo "✅ Using: $DOCKER_COMPOSE_CMD"

# Stop any existing containers (EXECUTABLE COMMAND)
echo ""
echo "🛑 Stopping any existing containers..."
$DOCKER_COMPOSE_CMD down 2>/dev/null || true

# Build and start all services (EXECUTABLE COMMAND)
echo ""
echo "📦 Building and starting services..."
$DOCKER_COMPOSE_CMD up --build -d

# Wait for services to be healthy (EXECUTABLE COMMAND)
echo ""
echo "⏳ Waiting for services to be healthy..."
sleep 15

# Check service status (EXECUTABLE COMMAND)
echo ""
echo "📊 Service Status:"
$DOCKER_COMPOSE_CMD ps

# Wait for backend to be ready (EXECUTABLE COMMAND)
echo ""
echo "🏥 Checking backend health..."
max_attempts=30
attempt=0

while [ $attempt -lt $max_attempts ]; do
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅ Backend is healthy!"
        break
    fi
    attempt=$((attempt + 1))
    echo "   Attempt $attempt/$max_attempts - Waiting for backend..."
    sleep 2
done

if [ $attempt -eq $max_attempts ]; then
    echo "⚠️  Backend health check failed. Check logs with: $DOCKER_COMPOSE_CMD logs academy-backend"
else
    echo ""
    echo "=========================================="
    echo "✅ Services are running!"
    echo "=========================================="
    echo ""
    echo "📍 Access points:"
    echo "   - Application: http://localhost:8080"
    echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "   - Health Check: http://localhost:8080/actuator/health"
    echo "   - API Docs: http://localhost:8080/api-docs"
    echo ""
    echo "📝 API Logs Location:"
    echo "   - In Docker: /app/logs/api-requests_*.log"
    echo ""
    echo "🔍 Useful commands:"
    echo "   - View logs: $DOCKER_COMPOSE_CMD logs -f academy-backend"
    echo "   - View API logs: docker exec academy-backend tail -f /app/logs/api-requests_*.log"
    echo "   - Stop services: $DOCKER_COMPOSE_CMD down"
    echo "   - View status: $DOCKER_COMPOSE_CMD ps"
    echo ""
fi

