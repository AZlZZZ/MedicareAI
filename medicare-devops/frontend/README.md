# MediCareAI 🏥

> Smart healthcare management platform — PIDEV 2025/2026 | Esprit School of Engineering

## 📋 Description

MediCareAI is a full-stack web application that streamlines healthcare management across three roles:
- **Agent** — manages patients, doctors, and medicament inventory
- **Doctor** — assigns medication schedules to patients with automated alerts
- **Patient** — views their medication schedules and upcoming dose reminders

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3, Spring Security, JWT, JPA/Hibernate, MySQL |
| Frontend | Angular 17, NgBootstrap, NgRx Signals, ngx-translate |
| Auth | JWT — Role-based (AGENT, DOCTOR, PATIENT) |
| Scheduler | Spring `@Scheduled` — auto-generates medication alerts |

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8+

### Backend
```bash
cd mediCareAiTigerBack
# Configure DB in src/main/resources/application.properties
./mvnw spring-boot:run
# Runs on http://localhost:8081
```

### Frontend
```bash
cd mediCareAiTigerFront
npm install
ng serve
# Runs on http://localhost:4200
```

## ✅ Features

### Agent
- Patient & doctor management (CRUD)
- Medicament inventory with stock control
- User state management (enable / block / delete)
- CSV export

### Doctor
- Browse patient list
- Create, edit, delete medication schedules per patient
- Configure dose, days of week, times of day, and alert timing

### Patient
- View active medication schedules
- Upcoming dose alerts with read/unread status
- Email reminders sent automatically before each dose

## 📁 Project Structure

```
mediCareAiTigerBack/     → Spring Boot REST API
mediCareAiTigerFront/    → Angular SPA
```

## 🔐 Security

- Stateless JWT authentication
- Role-based endpoint protection (`ROLE_AGENT`, `ROLE_DOCTOR`, `ROLE_PATIENT`)
- Token expiry handling with automatic logout

## 👥 Team — 4SE3

| Name | Role |
|------|------|

| Aziz | Full-stack |
| Ilef | Full-stack |
