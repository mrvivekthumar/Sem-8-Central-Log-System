@echo off
REM Central Log System - Development Startup Script for Windows
REM Author: Vivek Thumar
REM Date: December 27, 2025

color 0A
echo ========================================
echo   Central Log System - Dev Startup
echo ========================================
echo.

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    color 0C
    echo [ERROR] Docker is not running!
    echo Please start Docker Desktop and try again.
    pause
    exit /b 1
)

color 0A
echo [OK] Docker is running

REM Check if docker-compose exists
docker-compose --version >nul 2>&1
if errorlevel 1 (
    color 0C
    echo [ERROR] docker-compose is not installed!
    pause
    exit /b 1
)

echo [OK] docker-compose found
echo.

REM Start Backend Services
echo Starting Backend Services...
cd backend

REM Check if .env exists
if not exist ".env" (
    color 0C
    echo [ERROR] backend\.env file not found!
    echo Please create .env file in backend directory
    pause
    exit /b 1
)

echo [OK] .env file found
echo.

REM Start docker-compose
echo Building and starting backend services...
docker-compose up -d --build

if errorlevel 1 (
    color 0C
    echo [ERROR] Failed to start backend services
    pause
    exit /b 1
)

color 0A
echo [OK] Backend services started successfully
echo.
echo Waiting for services to be healthy (30 seconds)...
timeout /t 30 /nobreak >nul

echo.
echo Service Status:
docker-compose ps

echo.
echo ========================================
echo   Backend Services Information
echo ========================================
echo API Gateway:     http://localhost:8080
echo Auth Service:    http://localhost:8081
echo Faculty Service: http://localhost:8082
echo Student Service: http://localhost:8083
echo RabbitMQ UI:     http://localhost:15672 (guest/guest)
echo Auth DB:         localhost:55320
echo Faculty DB:      localhost:55321
echo Student DB:      localhost:55322
echo.
echo ========================================
echo   To view logs: docker-compose logs -f
echo   To stop: docker-compose down
echo ========================================
echo.

REM Go back to root
cd ..

REM Start Frontend in a new window
echo Starting Frontend...
cd frontend

REM Check if .env exists
if not exist ".env" (
    echo [WARNING] frontend\.env not found. Creating default...
    (
        echo VITE_API_BASE_URL=http://localhost:8080
        echo VITE_API_TIMEOUT=30000
    ) > .env
)

REM Check if node_modules exists
if not exist "node_modules" (
    echo Installing frontend dependencies...
    call npm install
)

echo.
echo [OK] Starting frontend development server...
echo Frontend will be available at: http://localhost:5173
echo.
echo ========================================
echo   Press Ctrl+C to stop the frontend
echo ========================================
echo.

REM Start frontend dev server
call npm run dev

REM If we get here, user stopped the frontend
echo.
echo Frontend stopped.
echo Backend services are still running.
echo To stop backend: cd backend && docker-compose down
pause
