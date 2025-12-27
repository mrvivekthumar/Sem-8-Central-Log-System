# Central Log System - Deployment & Debugging Guide

**Last Updated:** December 27, 2025  
**Author:** Vivek Thumar

---

## 🎯 Quick Start

### Prerequisites

- **Docker & Docker Compose** (v20.10+)
- **Node.js** (v18+ recommended)
- **npm** or **yarn**
- **Java 17+** (for local development)
- **PostgreSQL 15** (handled by Docker)
- **Git**

---

## 🚀 Running the Project

### Step 1: Clone the Repository

```bash
git clone https://github.com/mrvivekthumar/Sem-8-Central-Log-System.git
cd Sem-8-Central-Log-System
```

### Step 2: Start Backend Services (Docker Compose)

```bash
cd backend

# Build and start all backend services
docker-compose up --build

# Or run in detached mode
docker-compose up -d --build
```

**What this starts:**
- 🗄️ PostgreSQL Databases (3 instances)
  - Auth DB: `localhost:55320`
  - Faculty DB: `localhost:55321`
  - Student DB: `localhost:55322`
- 🐰 RabbitMQ: `localhost:5672` (Management UI: `localhost:15672`)
- 🔐 Auth Service: `localhost:8081`
- 👨‍🏫 Faculty Service: `localhost:8082`
- 👨‍🎓 Student Service: `localhost:8083`
- 🌐 API Gateway: `localhost:8080`

### Step 3: Start Frontend

```bash
# Open a new terminal
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

**Frontend will be available at:** `http://localhost:5173`

---

## 🔍 Service Health Checks

### Check if all services are running:

```bash
# View running containers
docker ps

# View logs for all services
cd backend
docker-compose logs -f

# View logs for specific service
docker-compose logs -f auth-service
docker-compose logs -f faculty-service
docker-compose logs -f student-service
docker-compose logs -f api-gateway
```

### Health Check Endpoints:

```bash
# API Gateway Health
curl http://localhost:8080/actuator/health

# Auth Service Health
curl http://localhost:8081/actuator/health

# Faculty Service Health
curl http://localhost:8082/actuator/health

# Student Service Health
curl http://localhost:8083/actuator/health

# RabbitMQ Management UI
open http://localhost:15672
# Username: guest
# Password: guest
```

---

## 🐛 Common Issues & Solutions

### Issue 1: Docker containers won't start

**Symptoms:**
- Containers exit immediately
- Database connection errors

**Solutions:**
```bash
# Stop all containers
docker-compose down

# Remove volumes (⚠️ This deletes all data)
docker-compose down -v

# Rebuild and restart
docker-compose up --build
```

### Issue 2: Port already in use

**Error:** `Bind for 0.0.0.0:8080 failed: port is already allocated`

**Solutions:**
```bash
# Find process using the port (Windows)
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Find process using the port (Linux/Mac)
lsof -i :8080
kill -9 <PID>

# Or change port in docker-compose.yml
```

### Issue 3: Frontend can't connect to backend

**Symptoms:**
- 404 errors on API calls
- CORS errors
- Network errors

**Solutions:**

1. **Verify .env file exists in frontend folder:**
```bash
cd frontend
cat .env  # Should show VITE_API_BASE_URL=http://localhost:8080
```

2. **Check API Gateway is running:**
```bash
curl http://localhost:8080/actuator/health
```

3. **Check browser console for specific errors**

4. **Restart frontend dev server:**
```bash
# Stop with Ctrl+C
npm run dev
```

### Issue 4: Database connection errors

**Error:** `Connection refused` or `Could not connect to database`

**Solutions:**
```bash
# Check if PostgreSQL containers are healthy
docker ps

# Check database logs
docker-compose logs auth-db
docker-compose logs faculty-db
docker-compose logs student-db

# Restart database containers
docker-compose restart auth-db faculty-db student-db
```

### Issue 5: JWT Token errors

**Symptoms:**
- 401 Unauthorized
- Token validation failed

**Solutions:**

1. **Ensure JWT_SECRET is the same across all services** (it is, from .env)
2. **Clear browser local storage:**
   - Open DevTools (F12)
   - Application → Local Storage → Clear All
3. **Re-login to get fresh token**

### Issue 6: RabbitMQ connection errors

**Solutions:**
```bash
# Check RabbitMQ is running
docker-compose logs rabbitmq

# Restart RabbitMQ
docker-compose restart rabbitmq

# Wait for RabbitMQ to be fully started (30 seconds)
```

---

## 📊 API Endpoint Mappings

### Authentication Endpoints

```
POST   /api/auth/login          → Auth Service
POST   /api/auth/register       → Auth Service
POST   /api/auth/refresh        → Auth Service
GET    /api/auth/verify         → Auth Service
GET    /api/auth/profile        → Auth Service
```

### Faculty Endpoints

```
GET    /api/faculty             → Faculty Service
GET    /api/faculty/{id}        → Faculty Service
POST   /api/faculty             → Faculty Service
PUT    /api/faculty/{id}        → Faculty Service
DELETE /api/faculty/{id}        → Faculty Service
```

### Project Endpoints (via Faculty Service)

```
GET    /api/projects            → Faculty Service
GET    /api/projects/{id}       → Faculty Service
POST   /api/projects            → Faculty Service
PUT    /api/projects/{id}       → Faculty Service
```

### Student Endpoints

```
GET    /api/students            → Student Service
GET    /api/students/{id}       → Student Service
POST   /api/students            → Student Service
PUT    /api/students/{id}       → Student Service
```

---

## 🧪 Testing the API

### Using cURL

```bash
# Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "name": "Test User",
    "role": "STUDENT"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# Get students (with token)
curl -X GET http://localhost:8080/api/students \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Using Postman

1. **Import Collection:**
   - Create a new collection
   - Add base URL: `http://localhost:8080`

2. **Set up authentication:**
   - Login to get token
   - Add token to Authorization header: `Bearer {{token}}`

---

## 🛠️ Development Workflow

### Making Changes to Backend

```bash
# Stop the specific service
docker-compose stop auth-service

# Make your changes in code

# Rebuild and restart
docker-compose up -d --build auth-service

# View logs
docker-compose logs -f auth-service
```

### Making Changes to Frontend

```bash
# Changes are hot-reloaded automatically
# Just save your files and the browser will refresh
```

### Database Migrations

```bash
# Connect to database
docker exec -it auth-db psql -U auth_user -d authdb

# List tables
\dt

# Describe table
\d table_name

# Run query
SELECT * FROM users;

# Exit
\q
```

---

## 📈 Monitoring & Logs

### View Real-time Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f api-gateway

# Last 100 lines
docker-compose logs --tail=100 auth-service
```

### Monitor Resource Usage

```bash
# Docker stats
docker stats

# Container resource usage
docker-compose ps
```

---

## 🧹 Cleanup Commands

### Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ Deletes all data)
docker-compose down -v

# Stop and remove images
docker-compose down --rmi all
```

### Clean Docker System

```bash
# Remove unused containers, networks, images
docker system prune

# Remove everything including volumes
docker system prune -a --volumes
```

---

## 🔐 Security Notes

1. **Never commit `.env` file with real credentials**
2. **Change JWT_SECRET in production**
3. **Use strong database passwords**
4. **Enable HTTPS in production**
5. **Rotate API keys regularly**

---

## 📝 Configuration Files

### Backend Configuration

- **API Gateway:** `backend/Api-Gateway/src/main/resources/application.yml`
- **Auth Service:** `backend/Authentication-Service/src/main/resources/application.yml`
- **Faculty Service:** `backend/FacultyService/src/main/resources/application.yml`
- **Student Service:** `backend/StudentService/src/main/resources/application.yml`
- **Docker Compose:** `backend/docker-compose.yml`
- **Environment:** `backend/.env`

### Frontend Configuration

- **Environment:** `frontend/.env`
- **API Endpoints:** `frontend/src/api/endpoints.js`
- **Axios Config:** `frontend/src/api/axiosInstance.js`
- **Vite Config:** `frontend/vite.config.js`

---

## 🎓 Architecture Overview

```
┌─────────────┐
│   Browser   │
│ (React App) │
└──────┬──────┘
       │ HTTP (Port 5173)
       ↓
┌─────────────────┐
│   API Gateway   │ ← Port 8080 (Entry Point)
│  (Spring Cloud) │
└────────┬────────┘
         │
    ┌────┼────┬─────────┬─────────┐
    ↓    ↓    ↓         ↓         ↓
┌────────┐ ┌──────────┐ ┌──────────┐
│  Auth  │ │ Faculty  │ │ Student  │
│Service │ │ Service  │ │ Service  │
│ :8081  │ │  :8082   │ │  :8083   │
└───┬────┘ └────┬─────┘ └────┬─────┘
    │           │             │
    ↓           ↓             ↓
┌────────┐ ┌──────────┐ ┌──────────┐
│Auth DB │ │Faculty DB│ │Student DB│
│:55320  │ │  :55321  │ │  :55322  │
└────────┘ └──────────┘ └──────────┘

         ┌────────────┐
         │  RabbitMQ  │
         │   :5672    │
         └────────────┘
```

---

## ✅ Verification Checklist

Before running the project, verify:

- [ ] Docker Desktop is running
- [ ] Ports 8080-8083, 5432, 5672, 15672 are available
- [ ] `.env` file exists in `backend/` directory
- [ ] `.env` file exists in `frontend/` directory
- [ ] Node.js v18+ is installed
- [ ] `npm install` completed successfully in frontend

---

## 🆘 Need Help?

1. Check this guide for common issues
2. Review service logs: `docker-compose logs -f`
3. Check browser console for frontend errors
4. Verify all services are healthy
5. Restart services if needed

---

## 📞 Contact

**Developer:** Vivek Thumar  
**Email:** mrvivekthumar@gmail.com  
**GitHub:** [@mrvivekthumar](https://github.com/mrvivekthumar)

---

**Happy Coding! 🚀**
