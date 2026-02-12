# Multi-stage Dockerfile for React frontend
# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files
COPY package.json ./

# Install dependencies (use npm install since package-lock.json may not exist)
RUN npm install

# Copy source code (excluding node_modules via .dockerignore)
COPY . .

# Build arguments for environment variables (Vite requires VITE_* prefix)
# Use empty string or relative path since nginx proxies /api to backend
ARG VITE_API_URL=
ARG VITE_WS_URL=ws://localhost/ws
ENV VITE_API_URL=$VITE_API_URL
ENV VITE_WS_URL=$VITE_WS_URL

# Generate API client (skip if OpenAPI file doesn't exist or generation fails)
# The placeholder types in src/generated/index.ts will be used if generation fails
RUN npm run generate || echo "⚠️ API generation failed - using placeholder types"

    # Build the application
    RUN npm run build

# Stage 2: Production with Nginx
FROM nginx:alpine

# Install wget for health checks
RUN apk add --no-cache wget

# Copy built assets from builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost/health || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
