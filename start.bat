@echo off
setlocal enabledelayedexpansion

REM ========================================================
REM   Central Log System - One-Click Startup Script (Windows)
REM   Author: Vivek Thumar
REM   This script sets up and runs the entire project.
REM ========================================================

title Central Log System - Startup
color 0A

echo.
echo  ============================================
echo       Central Log System - Project Launcher
echo  ============================================
echo.

REM -------------------------------------------------------
REM  1. Pre-flight checks: Docker, Node.js, npm
REM -------------------------------------------------------
echo  [1/6] Checking prerequisites...
echo.

REM -- Docker --
docker info >nul 2>&1
if errorlevel 1 (
    color 0C
    echo  [X] Docker is NOT running.
    echo      Please install and start Docker Desktop first.
    echo      Download: https://www.docker.com/products/docker-desktop
    echo.
    pause
    exit /b 1
)
echo  [OK] Docker is running

REM -- docker compose --
docker compose version >nul 2>&1
if errorlevel 1 (
    color 0C
    echo  [X] 'docker compose' command not found.
    echo      Please update Docker Desktop to the latest version.
    echo.
    pause
    exit /b 1
)
echo  [OK] Docker Compose found

REM -- Node.js --
where node >nul 2>&1
if errorlevel 1 (
    color 0C
    echo  [X] Node.js is NOT installed.
    echo      Please install Node.js v18+ from https://nodejs.org
    echo.
    pause
    exit /b 1
)
for /f "tokens=*" %%v in ('node -v') do set NODE_VER=%%v
echo  [OK] Node.js %NODE_VER% found

REM -- npm --
where npm >nul 2>&1
if errorlevel 1 (
    color 0C
    echo  [X] npm is NOT installed. It should come with Node.js.
    echo.
    pause
    exit /b 1
)
echo  [OK] npm found
echo.

REM -------------------------------------------------------
REM  2. Ensure backend .env file exists
REM -------------------------------------------------------
echo  [2/6] Checking environment files...
echo.

if not exist "backend\.env" (
    color 0C
    echo  [X] backend\.env file is missing!
    echo      Copy backend\.env.example to backend\.env and fill in your secrets.
    echo.
    pause
    exit /b 1
)
echo  [OK] backend\.env found

REM -- Create frontend .env if missing --
if not exist "frontend\.env" (
    echo  [!] frontend\.env not found - creating default...
    (
        echo VITE_API_BASE_URL=http://localhost:8080
        echo VITE_API_TIMEOUT=30000
        echo VITE_ENV=development
    ) > "frontend\.env"
)
echo  [OK] frontend\.env found
echo.

REM -------------------------------------------------------
REM  3. Create local data directory
REM -------------------------------------------------------
echo  [3/6] Setting up local data storage...
echo.

set "DATA_DIR=%USERPROFILE%\Documents\Central Log System"

REM Export for Docker Compose volume mounts (prevents '~' path issues on Windows)
set "CENTRAL_LOG_SYSTEM_DATA_DIR=%DATA_DIR%"
set "CENTRAL_LOG_SYSTEM_DATA_DIR=%CENTRAL_LOG_SYSTEM_DATA_DIR:\=/%"

if not exist "%DATA_DIR%\databases\auth-db" mkdir "%DATA_DIR%\databases\auth-db"
if not exist "%DATA_DIR%\databases\faculty-db" mkdir "%DATA_DIR%\databases\faculty-db"
if not exist "%DATA_DIR%\databases\student-db" mkdir "%DATA_DIR%\databases\student-db"
if not exist "%DATA_DIR%\logs" mkdir "%DATA_DIR%\logs"
if not exist "%DATA_DIR%\rabbitmq" mkdir "%DATA_DIR%\rabbitmq"

echo  [OK] Data directory: %DATA_DIR%
echo       databases\auth-db\       - Auth PostgreSQL data
echo       databases\faculty-db\    - Faculty PostgreSQL data
echo       databases\student-db\    - Student PostgreSQL data
echo       logs\combined.log        - Central log file
echo       rabbitmq\                - RabbitMQ data
echo.

REM -------------------------------------------------------
REM  4. Start backend services (Docker Compose)
REM -------------------------------------------------------
echo  [4/6] Starting backend services (this may take a few minutes on first run)...
echo.

pushd backend
docker compose up -d --build 2>&1

if errorlevel 1 (
    color 0C
    echo.
    echo  [X] Failed to start backend. Check Docker logs:
    echo      cd backend ^&^& docker compose logs
    echo.
    popd
    pause
    exit /b 1
)
popd

color 0A
echo.
echo  [OK] Backend containers started. Waiting for services to become healthy...
echo.

REM -- Wait for databases to be healthy (up to 90 seconds) --
set TRIES=0
set MAX_TRIES=18

:wait_db
set /a TRIES+=1
if %TRIES% gtr %MAX_TRIES% (
    color 0E
    echo  [!] Timed out waiting for databases. They may still be initializing.
    echo      Run: cd backend ^&^& docker compose ps
    goto :after_wait
)

REM Check if all 3 DBs are healthy
docker inspect --format="{{.State.Health.Status}}" auth-db 2>nul | findstr /i "healthy" >nul 2>&1
if errorlevel 1 (
    echo      Waiting for databases... (%TRIES%/%MAX_TRIES%)
    timeout /t 5 /nobreak >nul
    goto :wait_db
)
docker inspect --format="{{.State.Health.Status}}" faculty-db 2>nul | findstr /i "healthy" >nul 2>&1
if errorlevel 1 (
    echo      Waiting for databases... (%TRIES%/%MAX_TRIES%)
    timeout /t 5 /nobreak >nul
    goto :wait_db
)
docker inspect --format="{{.State.Health.Status}}" student-db 2>nul | findstr /i "healthy" >nul 2>&1
if errorlevel 1 (
    echo      Waiting for databases... (%TRIES%/%MAX_TRIES%)
    timeout /t 5 /nobreak >nul
    goto :wait_db
)

echo  [OK] All databases are healthy
:after_wait
echo.

REM -- Wait a bit more for application services to start --
echo  Waiting for application services to initialize...
timeout /t 15 /nobreak >nul

REM -- Show container status --
echo.
echo  Container Status:
echo  -----------------
pushd backend
docker compose ps
popd
echo.

REM -------------------------------------------------------
REM  5. Install frontend dependencies
REM -------------------------------------------------------
echo  [5/6] Setting up frontend...
echo.

pushd frontend
if not exist "node_modules" (
    echo  Installing frontend dependencies (npm install)...
    call npm install
    if errorlevel 1 (
        color 0C
        echo  [X] npm install failed.
        popd
        pause
        exit /b 1
    )
    echo.
)
echo  [OK] Frontend dependencies ready
popd
echo.

REM -------------------------------------------------------
REM  6. Start frontend dev server
REM -------------------------------------------------------
echo  [6/6] Starting frontend...
echo.
echo  ============================================
echo       Central Log System is running!
echo  ============================================
echo.
echo   Frontend:       http://localhost:5173
echo   API Gateway:    http://localhost:8080
echo   Auth Service:   http://localhost:8081
echo   Faculty Svc:    http://localhost:8082
echo   Student Svc:    http://localhost:8083
echo   RabbitMQ UI:    http://localhost:15672  (guest/guest)
echo.
echo   Central Logs:   %DATA_DIR%\logs\combined.log
echo   DB Data:        %DATA_DIR%\databases\
echo.
echo   Stop frontend:  Press Ctrl+C in this window
echo   Stop backend:   cd backend ^&^& docker compose down
echo   View logs:      type "%DATA_DIR%\logs\combined.log"
echo  ============================================
echo.

pushd frontend
call npm run dev
popd

echo.
echo  Frontend stopped. Backend services are still running.
echo  To stop everything:  cd backend ^&^& docker compose down
echo.
pause
