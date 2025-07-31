# 🤝 CollabBridge

**CollabBridge** is a microservices-based collaborative platform developed using **Spring Boot**, designed to connect faculty and students for academic project collaborations. It includes secure authentication, user management, project idea sharing, and a modern **React + Vite** frontend interface.

---

## 🧩 Architecture Overview

CollabBridge is built with a **microservices architecture**, where each service handles a specific domain:


---

## 🚀 Features

- 🔐 **Auth Service**  
  Handles user authentication and role-based authorization using **Spring Security** and **JWT**.

- 👨‍🏫 **Faculty Service**  
  Faculty can post project ideas, view their own projects, and manage them.

- 👨‍🎓 **Student Service**  
  Students can browse and join projects shared by faculty.

- 🧑‍💼 **Admin Service**  
  Admin can register both students and faculty and manage user roles.

- 🔀 **API Gateway**  
  Routes incoming requests to the appropriate microservices.

- 📡 **Service Registry**  
  All services are registered with **Eureka Server** for dynamic discovery.

- 🌐 **Frontend (Vite + React)**  
  Modern and fast UI built with React and styled for user-friendly navigation.

---

## 🛠️ Tech Stack

### Backend:
- **Java 17**
- **Spring Boot**
- **Spring Security + JWT**
- **Spring Cloud Gateway**
- **Eureka Service Registry**
- **Spring Data JPA**
- **PostgreSQL / NeonDb**
- **Lombok**

### Frontend:
- **React.js**
- **Vite**
- **Axios**
- **TailwindCSS / Bootstrap**

---
## 📎 Deployed Link

You can access the live project here:  
👉 [**CollabBridge Live**](https://colab-bridge-git-main-hetbhagatji09-gmailcoms-projects.vercel.app/)



