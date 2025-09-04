@echo off
echo 🚀 Starting local deployment with Docker Desktop...

:: Check if Docker Desktop is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker Desktop is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

:: Build Quarkus application
echo 🏗️ Building Quarkus application...
cd quarkus-service
call mvnw.cmd clean package -DskipTests
if %errorlevel% neq 0 (
    echo ❌ Quarkus build failed
    pause
    exit /b 1
)
echo ✅ Quarkus build completed
cd ..

:: Stop existing containers
echo 🛑 Stopping existing containers...
docker-compose -f docker-compose.local.yml down

:: Build and start all services
echo 🐳 Starting Docker containers...
docker-compose -f docker-compose.local.yml --env-file .env.local up --build -d

:: Wait for services to be ready
echo ⏳ Waiting for services to start...
timeout /t 30 /nobreak >nul

:: Check service health
echo 🔍 Checking service health...
docker-compose -f docker-compose.local.yml ps

echo ✅ Local deployment completed!
echo.
echo 🌐 Your application is available at:
echo    Frontend:  http://localhost:3000
echo    Node.js:   http://localhost:3001
echo    Quarkus:   http://localhost:8081
echo    Postgres:  localhost:5433
echo.
echo 📋 To view logs: docker-compose -f docker-compose.local.yml logs -f [service-name]
echo 🛑 To stop: docker-compose -f docker-compose.local.yml down
pause
