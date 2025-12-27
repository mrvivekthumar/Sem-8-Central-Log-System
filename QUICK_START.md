# 🚀 Quick Start Guide - Central Log System

**Just want to run it? Follow these steps:**

---

## 🟢 Super Quick Start (Windows)

```batch
# 1. Make sure Docker Desktop is running

# 2. Open Command Prompt in project root
cd path\to\Sem-8-Central-Log-System

# 3. Run the startup script
start-dev.bat

# 4. Wait for services to start (30-60 seconds)

# 5. Open browser
http://localhost:5173
```

---

## 🟢 Super Quick Start (Linux/Mac)

```bash
# 1. Make sure Docker is running

# 2. Open terminal in project root
cd path/to/Sem-8-Central-Log-System

# 3. Make script executable and run
chmod +x start-dev.sh
./start-dev.sh

# 4. Wait for services to start (30-60 seconds)

# 5. Open browser
http://localhost:5173
```

---

## 🔴 Manual Start (If Scripts Don't Work)

### Terminal 1: Backend

```bash
cd backend
docker-compose up --build
```

### Terminal 2: Frontend

```bash
cd frontend
npm install
npm run dev
```

---

## ✅ Check if Everything is Running

### Quick Health Check

```bash
curl http://localhost:8080/actuator/health
```

Should return: `{"status":"UP"}`

### Check All Containers

```bash
cd backend
docker ps
```

Should see 7 containers running:
- ✅ api-gateway
- ✅ auth-service  
- ✅ faculty-service
- ✅ student-service
- ✅ auth-db
- ✅ faculty-db
- ✅ student-db
- ✅ rabbitmq

---

## 🌐 Access URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| **Frontend** | http://localhost:5173 | - |
| **API Gateway** | http://localhost:8080 | - |
| **RabbitMQ UI** | http://localhost:15672 | guest / guest |
| **Auth DB** | localhost:55320 | auth_user / auth_password |
| **Faculty DB** | localhost:55321 | faculty_user / faculty_password |
| **Student DB** | localhost:55322 | student_user / student_password |

---

## 🐛 Common Problems

### Problem: "Port already in use"

**Windows:**
```batch
netstat -ano | findstr :8080
taskkill /PID <PID_NUMBER> /F
```

**Linux/Mac:**
```bash
lsof -i :8080
kill -9 <PID_NUMBER>
```

### Problem: "Docker is not running"

1. Start Docker Desktop
2. Wait until Docker icon shows "running"
3. Try again

### Problem: "Containers won't start"

```bash
cd backend
docker-compose down -v
docker-compose up --build
```

### Problem: "Frontend can't connect"

1. Check if `.env` file exists in `frontend/` folder
2. Content should be:
   ```
   VITE_API_BASE_URL=http://localhost:8080
   VITE_API_TIMEOUT=30000
   ```
3. Restart frontend:
   ```bash
   # Press Ctrl+C to stop
   npm run dev
   ```

---

## 🛑 Stop Everything

### Stop Frontend

```
Press Ctrl+C in the frontend terminal
```

### Stop Backend

```bash
cd backend
docker-compose down
```

### Stop and Delete Everything (Clean Slate)

```bash
cd backend
docker-compose down -v
```

---

## 📊 View Logs

### All Services

```bash
cd backend
docker-compose logs -f
```

### Specific Service

```bash
docker-compose logs -f auth-service
docker-compose logs -f faculty-service
docker-compose logs -f student-service
docker-compose logs -f api-gateway
```

### Last 100 Lines

```bash
docker-compose logs --tail=100 api-gateway
```

---

## 🧪 Test the API

### Register User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@test.com",
    "password": "Test@123",
    "name": "Test User",
    "role": "STUDENT"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@test.com",
    "password": "Test@123"
  }'
```

---

## 📚 Need More Help?

Read the detailed guides:

- **Full Deployment Guide:** [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)
- **All Fixes Applied:** [FIXES_APPLIED.md](./FIXES_APPLIED.md)

---

## ⏱️ Typical Startup Times

- **Backend (Docker):** 30-60 seconds
- **Frontend (npm):** 5-10 seconds
- **Total:** ~1 minute until fully ready

---

## ✅ Verification Checklist

Before starting:
- [ ] Docker Desktop is running
- [ ] Ports 8080-8083, 5432, 5672, 15672, 5173 are free
- [ ] Node.js v18+ installed
- [ ] At least 4GB RAM available

After starting:
- [ ] All 8 containers show "Up" status
- [ ] API Gateway health check returns `{"status":"UP"}`
- [ ] Frontend opens at http://localhost:5173
- [ ] No errors in browser console

---

**That's it! You're ready to develop! 🎉**

If you encounter any issues, check [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) for detailed troubleshooting.
