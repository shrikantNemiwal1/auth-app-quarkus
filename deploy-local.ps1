Write-Host "🚀 Starting local deployment with Docker Desktop..." -ForegroundColor Green

# Check if Docker Desktop is running
try {
    docker info | Out-Null
}
catch {
    Write-Host "❌ Docker Desktop is not running. Please start Docker Desktop first." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Build Quarkus application
Write-Host "🏗️ Building Quarkus application..." -ForegroundColor Yellow
Set-Location quarkus-service
& .\mvnw.cmd clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Quarkus build failed" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "✅ Quarkus build completed" -ForegroundColor Green
Set-Location ..

# Stop existing containers
Write-Host "🛑 Stopping existing containers..." -ForegroundColor Yellow
docker-compose -f docker-compose.local.yml down

# Build and start all services
Write-Host "🐳 Starting Docker containers..." -ForegroundColor Yellow
docker-compose -f docker-compose.local.yml --env-file .env.local up --build -d

# Wait for services to be ready
Write-Host "⏳ Waiting for services to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Check service health
Write-Host "🔍 Checking service health..." -ForegroundColor Yellow
docker-compose -f docker-compose.local.yml ps

Write-Host "✅ Local deployment completed!" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 Your application is available at:" -ForegroundColor Cyan
Write-Host "   Frontend:  http://localhost:3000" -ForegroundColor White
Write-Host "   Node.js:   http://localhost:3001" -ForegroundColor White
Write-Host "   Quarkus:   http://localhost:8081" -ForegroundColor White
Write-Host "   Postgres:  localhost:5433" -ForegroundColor White
Write-Host ""
Write-Host "📋 To view logs: docker-compose -f docker-compose.local.yml logs -f [service-name]" -ForegroundColor Yellow
Write-Host "🛑 To stop: docker-compose -f docker-compose.local.yml down" -ForegroundColor Yellow
Read-Host "Press Enter to continue"
