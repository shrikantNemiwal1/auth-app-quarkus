#!/bin/bash

set -e

echo "🚀 Starting local deployment with Docker Desktop..."

# Check if Docker Desktop is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker Desktop is not running. Please start Docker Desktop first."
    exit 1
fi

# Build Quarkus application
echo "🏗️ Building Quarkus application..."
cd quarkus-backend
./mvnw clean package -DskipTests
echo "✅ Quarkus build completed"
cd ..

# Stop existing containers
echo "🛑 Stopping existing containers..."
docker-compose -f docker-compose.local.yml down

# Build and start all services
echo "🐳 Starting Docker containers..."
docker-compose -f docker-compose.local.yml --env-file .env.local up --build -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 30

# Check service health
echo "🔍 Checking service health..."
docker-compose -f docker-compose.local.yml ps

echo "✅ Local deployment completed!"
echo ""
echo "🌐 Your application is available at:"
echo "   Frontend:  http://localhost:3000"
echo "   Node.js:   http://localhost:3001"
echo "   Quarkus:   http://localhost:8081"
echo "   Postgres:  localhost:5433"
echo ""
echo "📋 To view logs: docker-compose -f docker-compose.local.yml logs -f [service-name]"
echo "🛑 To stop: docker-compose -f docker-compose.local.yml down"
