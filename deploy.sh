#!/bin/bash

set -e

echo "🚀 Starting deployment..."

# Update system packages
sudo yum update -y

# Install Docker
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Clone or update your repository
if [ ! -d "auth-app" ]; then
    git clone YOUR_GITHUB_REPO_URL auth-app
fi

cd auth-app
git pull origin main

# Update environment variables
echo "📝 Updating environment variables..."
EC2_PUBLIC_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)
sed -i "s/YOUR_EC2_PUBLIC_IP/$EC2_PUBLIC_IP/g" .env.production

# Build Quarkus application (generates Docker files)
echo "🏗️ Building Quarkus application..."
cd quarkus-backend
./mvnw clean package -DskipTests
echo "✅ Quarkus Docker files generated in src/main/docker/"
cd ..

# Build and start all services
echo "🐳 Starting Docker containers..."
sudo docker-compose --env-file .env.production up --build -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 60

# Check service health
echo "🔍 Checking service health..."
sudo docker-compose ps

echo "✅ Deployment completed!"
echo "🌐 Your application is available at: http://$EC2_PUBLIC_IP"
