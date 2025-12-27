#!/bin/bash

# Central Log System - Development Startup Script
# Author: Vivek Thumar
# Date: December 27, 2025

echo "========================================"
echo "  Central Log System - Dev Startup"
echo "========================================"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running!${NC}"
    echo "Please start Docker Desktop and try again."
    exit 1
fi

echo -e "${GREEN}✓ Docker is running${NC}"

# Check if docker-compose exists
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}Error: docker-compose is not installed!${NC}"
    exit 1
fi

echo -e "${GREEN}✓ docker-compose found${NC}"
echo ""

# Start Backend Services
echo -e "${YELLOW}Starting Backend Services...${NC}"
cd backend || exit

# Check if .env exists
if [ ! -f ".env" ]; then
    echo -e "${RED}Error: backend/.env file not found!${NC}"
    echo "Please create .env file in backend directory"
    exit 1
fi

echo -e "${GREEN}✓ .env file found${NC}"

# Start docker-compose
docker-compose up -d --build

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Backend services started successfully${NC}"
else
    echo -e "${RED}Error: Failed to start backend services${NC}"
    exit 1
fi

echo ""
echo "Waiting for services to be healthy (30 seconds)..."
sleep 30

echo ""
echo -e "${YELLOW}Service Status:${NC}"
docker-compose ps

echo ""
echo -e "${YELLOW}Health Checks:${NC}"
echo -n "API Gateway (8080): "
curl -s http://localhost:8080/actuator/health > /dev/null && echo -e "${GREEN}✓ Healthy${NC}" || echo -e "${RED}✗ Not responding${NC}"

echo -n "Auth Service (8081): "
curl -s http://localhost:8081/actuator/health > /dev/null && echo -e "${GREEN}✓ Healthy${NC}" || echo -e "${RED}✗ Not responding${NC}"

echo -n "Faculty Service (8082): "
curl -s http://localhost:8082/actuator/health > /dev/null && echo -e "${GREEN}✓ Healthy${NC}" || echo -e "${RED}✗ Not responding${NC}"

echo -n "Student Service (8083): "
curl -s http://localhost:8083/actuator/health > /dev/null && echo -e "${GREEN}✓ Healthy${NC}" || echo -e "${RED}✗ Not responding${NC}"

echo ""
echo "========================================"
echo "  Backend Services Information"
echo "========================================"
echo "API Gateway:     http://localhost:8080"
echo "Auth Service:    http://localhost:8081"
echo "Faculty Service: http://localhost:8082"
echo "Student Service: http://localhost:8083"
echo "RabbitMQ UI:     http://localhost:15672 (guest/guest)"
echo "Auth DB:         localhost:55320"
echo "Faculty DB:      localhost:55321"
echo "Student DB:      localhost:55322"
echo ""

# Go back to root
cd ..

# Start Frontend
echo -e "${YELLOW}Starting Frontend...${NC}"
cd frontend || exit

# Check if .env exists
if [ ! -f ".env" ]; then
    echo -e "${YELLOW}Warning: frontend/.env not found. Creating default...${NC}"
    echo "VITE_API_BASE_URL=http://localhost:8080" > .env
    echo "VITE_API_TIMEOUT=30000" >> .env
fi

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}Installing frontend dependencies...${NC}"
    npm install
fi

echo ""
echo -e "${GREEN}Starting frontend development server...${NC}"
echo -e "${YELLOW}Frontend will be available at: http://localhost:5173${NC}"
echo ""
echo "========================================"
echo "  Press Ctrl+C to stop the frontend"
echo "  To stop backend: cd backend && docker-compose down"
echo "========================================"
echo ""

npm run dev
