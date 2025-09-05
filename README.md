# Auth App Quarkus Solution

## Project Overview

This project implements a secure user signup and login portal as per the following assignment:

> **Assignment:**
>
> - Users can sign up with email and password (passwords are salted & hashed in DB).
> - Email verification is required after signup.
> - Login is only fully enabled after email verification.
> - 15-minute inactivity session timeout; session persists across tabs.
> - Users can log out; accessing the portal after logout redirects to login.
> - Mocking is used for backend and frontend tests.
> - **Tech stack:** Quarkus (Java) backend, PostgreSQL, Node.js (session proxy), React frontend, Docker Compose for orchestration.

---

## Architecture

- **Frontend**: React app (TypeScript, Vite, Tailwind), served via Nginx. Handles all user UI.
- **Node.js Proxy**: Express server. Manages user sessions (15-min inactivity timeout, cross-tab), proxies API calls to backend, isolates frontend from backend.
- **Quarkus Backend**: Java Quarkus REST API. Handles user registration, login, email verification, password hashing, and business logic.
- **PostgreSQL**: Stores user and verification data.

---

## Component Details

### 1. Frontend (`/frontend`)

- React + TypeScript, Vite, Tailwind CSS.
- Pages: Signup, Login, Dashboard, Email Verification.
- Calls `/api` endpoints (proxied by Node.js).
- Served via Nginx (see Dockerfile/nginx.conf).

### 2. Node.js Proxy (`/node-proxy`)

- Express server, session management with `express-session` (HTTP-only cookies).
- 15-min inactivity timeout, rolling sessions, cross-tab support.
- Proxies API requests to Quarkus backend.
- Centralizes authentication and session logic.

### 3. Quarkus Backend (`/quarkus-service`)

- Java (Quarkus), REST API for user management.
- Passwords are salted & hashed.
- Email verification via token.
- Persists users in PostgreSQL.
- Uses Flyway for DB migrations.

### 4. Database

- PostgreSQL (Dockerized, managed by Compose).
- Stores users and email verification tokens.

---

## Prerequisites

- [Docker](https://www.docker.com/get-started) & Docker Compose
- (Optional for local dev) Node.js >= 18, Java 21+, Maven (if running services outside Docker)

---

## Running Locally

1. **Clone the repository:**

   ```sh
   git clone https://github.com/shrikantNemiwal1/auth-app-quarkus.git
   cd auth-app-quarkus
   ```

2. **Set environment variables:**

   - Create a `.env` file or set `SESSION_SECRET` for Node.js proxy (see `docker-compose.yml`).

3. **Start all services:**

   ```sh
   docker-compose up --build
   ```

   - Frontend: http://localhost (Nginx serves React app)
   - API: http://localhost/api (proxied to Node.js, then Quarkus)
   - DB: Internal only

4. **Access the app:**
   - Open [http://localhost](http://localhost) in your browser.

---

## Testing

- **Backend:**
  - Quarkus tests: `./mvnw test` (from `/quarkus-service`)
- **Frontend:**
  - Add tests/mocks as needed (see `/frontend`)
- **Node Proxy:**
  - Jest/Supertest (see `/node-proxy`)

---

## Deployment

- All services are containerized; can be deployed to AWS ECS, EC2, or similar.
- Use HTTPS in production (see Nginx config and Docker Compose for certs).

---

## Notes

- **Session Management:** 15-min inactivity timeout, session persists across tabs (handled by Node.js proxy).
- **Security:** Passwords are salted & hashed; sessions use HTTP-only cookies.
- **Email Verification:** Users must verify email before accessing the portal.
- **Environment Variables:** See `docker-compose.yml` for required variables.

---

## Directory Structure

- `/frontend` - React app (UI)
- `/node-proxy` - Node.js session/auth proxy
- `/quarkus-service` - Java Quarkus backend
- `/docker-compose.yml` - Orchestration for all services

---
