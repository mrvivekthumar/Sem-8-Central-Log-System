# 🤝 CollabBridge - Central Log System

**CollabBridge** is a microservices-based collaborative platform developed using **Spring Boot**, designed to connect faculty and students for academic project collaborations. It includes secure authentication, user management, project idea sharing, and a modern **React + Vite** frontend interface.

---

## 🚀 Quick Start

**Want to run the project immediately?**

### Windows Users

```batch
start-dev.bat
```

### Linux/Mac Users

```bash
chmod +x start-dev.sh
./start-dev.sh
```

**Then open:** <http://localhost:5173>

📚 **Need help?** Check [QUICK_START.md](./QUICK_START.md)

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [**QUICK_START.md**](./QUICK_START.md) | 5-minute guide to get started |
| [**DEPLOYMENT_GUIDE.md**](./DEPLOYMENT_GUIDE.md) | Complete deployment & troubleshooting |
| [**FIXES_APPLIED.md**](./FIXES_APPLIED.md) | Recent fixes and improvements |

---

## 🎯 Features

- 🔐 **Auth Service**  
  Handles user authentication and role-based authorization using **Spring Security** and **JWT**.

- 👨‍🏫 **Faculty Service**  
  Faculty can post project ideas, view their own projects, and manage them.

- 👨‍🎓 **Student Service**  
  Students can browse and join projects shared by faculty.

- 🔀 **API Gateway**  
  Routes incoming requests to the appropriate microservices with authentication.

- 🐰 **RabbitMQ Integration**  
  Event-driven communication between microservices.

- 🌐 **Frontend (Vite + React)**  
  Modern and fast UI built with React and styled for user-friendly navigation.

---

## 🧩 Architecture Overview

```
┌─────────────┐
│   Browser   │
│ (React App) │
└──────┬──────┘
       │ :5173
       ↓
┌─────────────────┐
│   API Gateway   │ :8080
│  (Spring Cloud) │
└────────┬────────┘
         │
    ┌────┼────┬─────────┐
    ↓    ↓    ↓         ↓
┌────────┐ ┌──────────┐ ┌──────────┐
│  Auth  │ │ Faculty  │ │ Student  │
│:8081   │ │  :8082   │ │  :8083   │
└───┬────┘ └────┬─────┘ └────┬─────┘
    │           │             │
┌────────┐ ┌──────────┐ ┌──────────┐
│Auth DB │ │Faculty DB│ │Student DB│
│:55320  │ │  :55321  │ │  :55322  │
└────────┘ └──────────┘ └──────────┘

         ┌────────────┐
         │  RabbitMQ  │
         │   :5672    │
         └────────────┘
```

CollabBridge uses a **microservices architecture** where each service handles a specific domain:

- **API Gateway** - Single entry point for all client requests
- **Auth Service** - User authentication and JWT token management
- **Faculty Service** - Faculty profiles and project management
- **Student Service** - Student profiles and project enrollment
- **PostgreSQL Databases** - Separate database per service (database-per-service pattern)
- **RabbitMQ** - Asynchronous messaging between services

---

## 🛠️ Tech Stack

### Backend

- **Java 17**
- **Spring Boot 3.x**
- **Spring Cloud Gateway**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL**
- **RabbitMQ**
- **Docker & Docker Compose**
- **Lombok**

### Frontend

- **React.js 18**
- **Vite**
- **Axios**
- **React Router**
- **Tailwind CSS**
- **Shadcn/UI**

### DevOps

- **Docker**
- **Docker Compose**
- **Kubernetes (K8s)**
- **GitHub Actions** (planned)

---

## ☸️ Kubernetes Deployment

### Prerequisites

- Kubernetes cluster (Minikube, Docker Desktop, or cloud K8s)
- kubectl CLI configured
- Docker images built and pushed (or use local images)

### Quick Deploy (Windows)

```powershell
cd backend/k8s
.\deploy.ps1
```

### Quick Deploy (Linux/Mac)

```bash
cd backend/k8s
chmod +x deploy.sh
./deploy.sh
```

### Access the Application

```bash
# Option 1: Port forwarding
kubectl port-forward svc/api-gateway 8080:8080 -n microservices

# Option 2: Minikube service
minikube service api-gateway -n microservices
```

### View Logs

```bash
kubectl logs -f -l app=api-gateway -n microservices
kubectl logs -f -l app=auth-service -n microservices
```

### Cleanup

```powershell
# Windows
cd backend/k8s
.\cleanup.ps1
```

```bash
# Linux/Mac
cd backend/k8s
./cleanup.sh
```

---

## 💻 Development Setup

### Prerequisites

- Docker Desktop (v20.10+)
- Node.js (v18+)
- Java 17+ (optional, for local development)
- Git

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/mrvivekthumar/Sem-8-Central-Log-System.git
   cd Sem-8-Central-Log-System
   ```

2. **Run the project**

   **Windows:**

   ```batch
   start-dev.bat
   ```

   **Linux/Mac:**

   ```bash
   chmod +x start-dev.sh
   ./start-dev.sh
   ```

3. **Access the application**
   - Frontend: <http://localhost:5173>
   - API Gateway: <http://localhost:8080>
   - RabbitMQ UI: <http://localhost:15672> (guest/guest)

---

## 🌐 Service Endpoints

| Service | Port | Health Check |
|---------|------|-------------|
| Frontend | 5173 | <http://localhost:5173> |
| API Gateway | 8080 | <http://localhost:8080/actuator/health> |
| Auth Service | 8081 | <http://localhost:8081/actuator/health> |
| Faculty Service | 8082 | <http://localhost:8082/actuator/health> |
| Student Service | 8083 | <http://localhost:8083/actuator/health> |
| RabbitMQ | 5672 | <http://localhost:15672> |

---

## 📦 Database Ports

| Database | Port | Credentials |
|----------|------|-------------|
| Auth DB | 55320 | auth_user / auth_password |
| Faculty DB | 55321 | faculty_user / faculty_password |
| Student DB | 55322 | student_user / student_password |

---

## 🧪 API Examples

### Register User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@example.com",
    "password": "Password@123",
    "name": "John Doe",
    "role": "STUDENT"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@example.com",
    "password": "Password@123"
  }'
```

### Get Students (Authenticated)

```bash
curl -X GET http://localhost:8080/api/students \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🐛 Troubleshooting

### Port Conflicts

**Windows:**

```batch
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Linux/Mac:**

```bash
lsof -i :8080
kill -9 <PID>
```

### Docker Issues

```bash
cd backend
docker-compose down -v
docker-compose up --build
```

### Frontend Connection Issues

1. Verify `.env` file exists in `frontend/` directory
2. Check API Gateway is running: `curl http://localhost:8080/actuator/health`
3. Restart frontend: `npm run dev`

**For more troubleshooting, see [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)**

---

## 📊 Project Structure

```
Sem-8-Central-Log-System/
├── backend/
│   ├── Api-Gateway/           # Spring Cloud Gateway
│   ├── Authentication-Service/ # Auth & JWT
│   ├── FacultyService/        # Faculty management
│   ├── StudentService/        # Student management
│   ├── docker-compose.yml     # Docker orchestration
│   └── .env                   # Environment variables
├── frontend/
│   ├── src/
│   │   ├── api/               # API service layer
│   │   ├── components/        # React components
│   │   ├── pages/             # Page components
│   │   └── contexts/          # React contexts
│   ├── package.json
│   └── .env                   # Frontend config
├── QUICK_START.md         # Quick start guide
├── DEPLOYMENT_GUIDE.md    # Detailed deployment
├── FIXES_APPLIED.md       # Recent fixes
├── start-dev.sh           # Linux/Mac startup
├── start-dev.bat          # Windows startup
└── README.md              # This file
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is developed for educational purposes as part of Semester 8 project at DDU University.

---

## 📧 Contact

**Developer:** Vivek Thumar  
**Email:** <mrvivekthumar@gmail.com>  
**GitHub:** [@mrvivekthumar](https://github.com/mrvivekthumar)  
**University:** DDU University, Gujarat

---

## 🌟 Acknowledgments

- DDU University - Information Technology Department
- Spring Boot & Spring Cloud communities
- React & Vite communities

---

## 📌 Deployed Link

You can access the live project here:  
👉 [**CollabBridge Live**](https://colab-bridge-git-main-hetbhagatji09-gmailcoms-projects.vercel.app/)

---

**Happy Coding! 🚀**

*Last Updated: December 27, 2025*
