**# Rotido

Rotido is a full-stack web application designed for managing **roti-making machines** manufactured by **Food Technics**, developed in collaboration with **Thinkfinity Labs**. Built using **Spring Boot**, **React**, and **MongoDB**, the application follows a **three-tier architecture** and leverages **MQTT** for real-time updates using a subscription model.

---

## 🔧 Tech Stack

- **Frontend:** React.js
- **Backend:** Spring Boot (Java)
- **Database:** MongoDB (NoSQL)
- **Real-Time Communication:** MQTT
- **Architecture:** Three-tier (Presentation, Business Logic, Data Access)

---

## 👥 User Roles and Views

The platform provides role-based access with encrypted authorization for the following user types:

### 1. **Customer**
- Register/login and manage profile
- View assigned machine status
- File complaints regarding machine issues
- Track status of complaints

### 2. **Service Provider**
- Access assigned complaints
- Update complaint status (IN_PROGRESS, RESOLVED, etc.)
- Manage service timeline

### 3. **Admin**
- Dashboard overview of all customers, machines, and service providers
- View and manage complaints
- Allot service providers to complaints
- Full CRUD access to:
  - Customer records
  - Machine inventory
  - Service provider accounts

---

## 🔐 Security and Authorization

- Implements **role-based access control (RBAC)**
- Token-based **authorization sent in encrypted format**
- Admins have elevated privileges for full system control

---

## 📡 Real-Time Updates with MQTT

- MQTT is used for lightweight, real-time communication between the machine and backend.
- Customers receive live updates on machine state and complaint progress via **subscription topics**.

---

## 🗂 Features

- ✅ Role-based dashboards (Customer, Admin, Service Provider)
- ✅ Complaint tracking and management
- ✅ Real-time machine updates via MQTT
- ✅ Secure login and authorization
- ✅ Admin CRUD operations
- ✅ Service provider allotment workflow
- ✅ Scalable three-tier architecture

---

## 🏁 Getting Started

### Backend (Spring Boot)
cd rotido-backend
./mvnw spring-boot:run

### Frontend (React)
cd rotido-frontend
npm install
npm start

**
![image](https://github.com/user-attachments/assets/e0ab52c0-a7c4-4845-9fc9-f1be2f2757f5)

![image](https://github.com/user-attachments/assets/68a5e43c-9bd3-4f72-9e38-fbac1ac593ca)

![image](https://github.com/user-attachments/assets/b1cf6c39-8c8d-437f-8f94-0cdb2d5467a2)

![image](https://github.com/user-attachments/assets/a9e1709a-b45b-4e61-8f4c-cc1fb252a588)

![image](https://github.com/user-attachments/assets/1415a2bf-5d97-4a78-b22a-b467c9337a11)

