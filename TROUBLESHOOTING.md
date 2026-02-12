# Troubleshooting Docker Compose Issues

## Common Issues and Solutions

### 1. Frontend Build Fails

**Error**: `npm ci` fails or `npm run generate` fails

**Solution**:
- The Dockerfile now uses `npm install` instead of `npm ci`
- API generation is optional - placeholder types are used if generation fails
- Check frontend logs: `docker-compose -f docker-compose.infrastructure.yml logs frontend`

### 2. Port 80 Already in Use

**Error**: `Bind for 0.0.0.0:80 failed: port is already allocated`

**Solution**:
```bash
# Check what's using port 80
sudo lsof -i :80

# Stop the service or change frontend port in docker-compose:
# Change "80:80" to "8081:80" in docker-compose.infrastructure.yml
```

### 3. Backend Not Starting

**Error**: Backend health check fails

**Solution**:
- Check backend logs: `docker-compose -f docker-compose.infrastructure.yml logs backend`
- Ensure MySQL, Redis, Kafka are healthy
- Check database connection: `docker-compose -f docker-compose.infrastructure.yml exec mysql mysql -uroot -prootpassword -e "SHOW DATABASES;"`

### 4. Frontend Can't Connect to Backend

**Error**: API calls fail with CORS or connection errors

**Solution**:
- Ensure backend is running: `docker-compose -f docker-compose.infrastructure.yml ps`
- Check nginx.conf proxy settings
- Verify VITE_API_URL in frontend environment

### 5. API Generation Fails

**Error**: `swagger-typescript-api` fails during build

**Solution**:
- Placeholder types are in `src/generated/index.ts`
- Build will continue even if generation fails
- To fix: Ensure OpenAPI file exists at `../modules/academy-api/src/main/resources/openapi.yaml`
- Or run manually: `cd frontend && npm run generate`

## Step-by-Step Debugging

### 1. Check All Services Status
```bash
docker-compose -f docker-compose.infrastructure.yml ps
```

### 2. View Logs
```bash
# All services
docker-compose -f docker-compose.infrastructure.yml logs

# Specific service
docker-compose -f docker-compose.infrastructure.yml logs frontend
docker-compose -f docker-compose.infrastructure.yml logs backend
```

### 3. Rebuild Specific Service
```bash
# Rebuild frontend only
docker-compose -f docker-compose.infrastructure.yml build frontend

# Rebuild backend only
docker-compose -f docker-compose.infrastructure.yml build backend

# Rebuild all
docker-compose -f docker-compose.infrastructure.yml build --no-cache
```

### 4. Start Services One by One
```bash
# Start infrastructure first
docker-compose -f docker-compose.infrastructure.yml up -d mysql redis kafka zookeeper

# Wait for health checks
docker-compose -f docker-compose.infrastructure.yml ps

# Start backend
docker-compose -f docker-compose.infrastructure.yml up -d backend

# Start frontend
docker-compose -f docker-compose.infrastructure.yml up -d frontend
```

### 5. Test Connections
```bash
# Test backend
curl http://localhost:8080/actuator/health

# Test frontend
curl http://localhost/health

# Test API through frontend
curl http://localhost/api/actuator/health
```

## Quick Fixes

### Reset Everything
```bash
# Stop all
docker-compose -f docker-compose.infrastructure.yml down

# Remove volumes (⚠️ deletes data)
docker-compose -f docker-compose.infrastructure.yml down -v

# Rebuild and start
docker-compose -f docker-compose.infrastructure.yml up --build
```

### Fix Frontend Build Issues
```bash
# Remove node_modules and rebuild
cd frontend
rm -rf node_modules package-lock.json
npm install
npm run generate
npm run build
```

### Fix Backend Build Issues
```bash
# Clean Gradle build
./gradlew clean

# Rebuild
./gradlew build -x test
```

