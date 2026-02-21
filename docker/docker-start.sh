#!/bin/bash

# Academy Backend Docker Startup Script

set -e

echo "🚀 Starting Academy Backend with Docker Compose..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker Desktop."
    exit 1
fi

# Build and start services
echo "📦 Building and starting services..."
docker-compose up --build -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check service status
echo ""
echo "📊 Service Status:"
docker-compose ps

# Check if backend is healthy
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
    echo "⚠️  Backend health check failed. Check logs with: docker-compose logs academy-backend"
else
    echo ""
    echo "🎉 Academy Backend is ready!"
    echo ""
    echo "📍 Access points:"
    echo "   - API: http://localhost:8080"
    echo "   - Health: http://localhost:8080/actuator/health"
    echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "   - API Docs: http://localhost:8080/api-docs"
    echo ""
    echo "📝 Useful commands:"
    echo "   - View logs: docker-compose logs -f"
    echo "   - Stop services: docker-compose down"
    echo "   - View status: docker-compose ps"
fi

