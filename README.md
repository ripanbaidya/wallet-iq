# WalletIQ 💰

<div align="center">

**A production-grade, RAG-powered personal finance system built with Spring Boot and React.**

Track expenses, set budgets, manage savings goals, and get AI-driven financial insights through a natural language chat assistant — all grounded in your real transaction data.

[![Live Demo](https://img.shields.io/badge/Live%20Demo-walletiq.online-black?style=for-the-badge&logo=vercel)](https://walletiq-eight.vercel.app)
[![Backend API](https://img.shields.io/badge/API%20Docs-Swagger%20UI-green?style=for-the-badge&logo=swagger)](http://localhost:8080/api/v1/swagger-ui.html)
[![License](https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-blue?style=for-the-badge)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?style=for-the-badge&logo=spring)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react)](https://react.dev)

</div>

---

## Table of Contents

- [What is WalletIQ?](#what-is-walletiq)
- [Problem It Solves](#problem-it-solves)
- [Features](#features)
- [Tech Stack](#tech-stack)
  - [Backend](#backend)
  - [Frontend](#frontend)
  - [Infrastructure](#infrastructure)
- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Getting Started (Local Development)](#getting-started-local-development)
  - [Prerequisites](#prerequisites)
  - [1. Clone the Repository](#1-clone-the-repository)
  - [2. Start Infrastructure (Docker)](#2-start-infrastructure-docker)
  - [3. Configure Backend](#3-configure-backend)
  - [4. Run the Backend](#4-run-the-backend)
  - [5. Run the Frontend](#5-run-the-frontend)
- [Environment Variables Reference](#environment-variables-reference)
- [API Documentation (Swagger)](#api-documentation-swagger)
- [Key Design Decisions & Best Practices](#key-design-decisions--best-practices)
- [Database Schema](#database-schema)
  - [Class Diagram](#class-diagram)
- [Deployment](#deployment)
- [Screenshots](#screenshots)
- [License](#license)
- [Author](#author)

---

## What is WalletIQ?

WalletIQ is a **full-stack personal finance management application** that combines traditional expense tracking with **Retrieval-Augmented Generation (RAG)** to give users AI-powered financial insights. Instead of generic advice, the AI assistant reads and reasons over the user's _actual_ transaction data to answer questions like:

> _"How much did I spend on food this month?"_
> _"Am I on track with my savings goals?"_
> _"What's my biggest expense category this week?"_

Every answer is grounded in real data — not guesswork.

---

## Problem It Solves

Most personal finance apps show you charts and numbers, but they don't _talk_ to you. You still have to interpret the data yourself. WalletIQ bridges this gap:

| Traditional Finance Apps    | WalletIQ                                        |
| --------------------------- | ----------------------------------------------- |
| Shows raw charts and tables | Gives you conversational insights               |
| Generic advice              | Answers based on YOUR data                      |
| Passive dashboards          | Active AI assistant                             |
| Siloed features             | Integrated: transactions + budgets + goals + AI |
| No natural language queries | Ask anything in plain English                   |

---

## Features

### 💬 RAG-Powered AI Chat

- Ask natural language questions about your finances
- Answers are retrieved from your actual transaction embeddings (PgVector)
- Context includes budget status, savings goals, and conversation history
- Powered by Ollama (dev) or OpenAI GPT-4o-mini (prod)

### 📊 Financial Dashboard

- Monthly income, expenses, and net balance summary
- Category-wise spending breakdown with percentages
- Daily trend chart (income vs expenses)
- Top 5 expenses for the month
- Month navigator to view historical data
- Results cached in **Redis**

### 💳 Transaction Management

- Create, edit, delete **income** and **expense** transactions
- Filter by type, category, and date range
- Paginated table view (20 per page)
- Export all transactions as **UTF-8 CSV** (Excel-compatible with BOM)

### 🔁 Recurring Transactions

- Set up **daily**, **weekly**, **monthly**, or **yearly** recurring rules
- Automatic transaction generation via scheduled jobs (8 AM daily)
- Cash flow forecast for **7 to 365 days** ahead
- Estimated monthly income/expense summary

### 📦 Budget Management

- Monthly category budgets with configurable **alert thresholds**
- Real-time spending status (spent, remaining, usage %)
- Alerts sent as in-app **notifications + WebSocket push** when threshold or limit is breached
- Duplicate prevention (one budget per category per month)

### 🎯 Savings Goals

- Create goals with target amount and deadline
- Contribute incrementally; quick-fill at **25%**, **50%**, or full remaining
- Auto-mark as ACHIEVED when target is reached
- Scheduler marks overdue **IN_PROGRESS** goals as **FAILED at 12:30 AM** daily
- Real-time notifications on achievement

### 🔔 Real-Time Notifications

- WebSocket (**STOMP over SockJS**) push notifications
- Budget threshold and limit breach alerts
- Recurring transaction execution confirmations
- Savings goal achievement and failure alerts
- In-app notification bell with unread count badge

### 🔐 Authentication & Security

- JWT-based authentication with **RS512 signing (RSA key pair)**
- Access token + refresh token with rotation (token-per-session)
- Email verification via **6-digit OTP** (5-minute expiry, Thymeleaf email template)
- **BCrypt** password hashing
- Spring Security with role-based access control (**USER / ADMIN**)
- CORS configuration per environment(dev/prod)

### 💳 Subscription System (Razorpay)

- **₹199/month** subscription to unlock AI chat
- Razorpay **HMAC-SHA256** signature verification
- Subscription lifecycle: **PENDING → ACTIVE → EXPIRED**
- Scheduler auto-expires subscriptions past their expiry timestamp

### 📧 Daily Email Summary

- Automated daily financial summary email at **9 PM**
- Includes total income, expenses, net balance, transaction count
- CSV attachment of day's transactions
- Rendered with **Thymeleaf** HTML email template
- Sent only to **active, non-admin** users

### 👤 User Profile & Admin Panel

- Edit display name with inline form
- Email verification flow built into the profile page
- Admin panel: **paginated** user list, user stats by role and status

### 📂 Categories & Payment Modes

- System-wide defaults (**Food, Rent, UPI**, etc.) visible to all users
- User-defined custom categories and payment modes
- Redis-cached (**24-hour TTL**)

---

## Tech Stack

### Backend

| Layer        | Technology                                                                  |
| ------------ | --------------------------------------------------------------------------- |
| Language     | **Java 21**                                                                 |
| Framework    | **Spring Boot 3.5**                                                         |
| Security     | **Spring Security + JWT (JJWT 0.12.6, RS512)**                              |
| Database     | **PostgreSQL 16 (via pgvector/pgvector Docker image)**                      |
| Vector Store | **PgVector (for RAG embeddings)**                                           |
| ORM          | **Spring Data JPA / Hibernate**                                             |
| Migrations   | **Flyway**                                                                  |
| Cache        | **Redis (Spring Cache + Spring Data Redis)**                                |
| AI (Dev)     | **Spring AI + Ollama (llama3.1:8b chat, nomic-embed-text:v1.5 embeddings)** |
| AI (Prod)    | **Spring AI + OpenAI (gpt-4o-mini chat, text-embedding-3-small)**           |
| Messaging    | **WebSocket (STOMP + SockJS)**                                              |
| Email        | **Spring Mail + Thymeleaf HTML templates**                                  |
| CSV Export   | **Apache Commons CSV**                                                      |
| API Docs     | **SpringDoc OpenAPI 3 (Swagger UI)**                                        |
| Build        | **Maven 3.9**                                                               |
| Payments     | **Razorpay Java SDK**                                                       |

### Frontend

| Layer            | Technology                                          |
| ---------------- | --------------------------------------------------- |
| Language         | **TypeScript (strict)**                             |
| Framework        | **React 19**                                        |
| Routing          | **React Router DOM 7**                              |
| State Management | **Zustand (auth store, persisted)**                 |
| Server State     | **TanStack React Query v5**                         |
| HTTP Client      | **Axios (with interceptors, silent token refresh)** |
| Styling          | **Tailwind CSS 3**                                  |
| Charts           | **Recharts**                                        |
| Animations       | **Framer Motion**                                   |
| Notifications    | **Sonner (toast)**                                  |
| Build Tool       | **Vite 8**                                          |

### Infrastructure

| Component        | Technology                       |
| ---------------- | -------------------------------- |
| Containerization | **Docker + Docker Compose**      |
| Reverse Proxy    | **Nginx**                        |
| CI/CD            | **GitHub Actions**               |
| Frontend Hosting | **Vercel**                       |
| Backend Hosting  | **VPS (via Docker + Traefik)**   |
| Database Hosting | **Neon (managed PostgreSQL)**    |
| Redis Hosting    | **Upstash (managed Redis, TLS)** |

---

## Architecture Overview

![Architecture Overview](/public/diagrams/readme-img-architecture-overview.png)

### RAG Pipeline

```
User Question
     │
     ▼
[Semantic Search] ──► PgVector (filter by userId, top-K=8)
     │
     ▼
[Context Assembly]
  ├─ Budget status (current month spending vs limits)
  ├─ Savings goals (progress, deadlines)
  ├─ Retrieved transactions (semantic matches)
  └─ Conversation history (last 10 messages)
     │
     ▼
[LLM Prompt] ──► System prompt + full context + question
     │
     ▼
[Sanitized Response] ──► Strips preamble, normalizes markdown
     │
     ▼
User receives grounded, data-backed answer
```

---

## Project Structure

```
walletiq/
├── backend/                          # Spring Boot application
│   ├── src/main/java/com/walletiq/
│   │   ├── Application.java
│   │   ├── cache/                    # Custom Spring Cache key generators
│   │   ├── config/                   # Spring configs (Security, Redis, AI, CORS, Razorpay)
│   │   │   └── properties/           # @ConfigurationProperties records
│   │   ├── constant/                 # CacheNames, OpenAPIConstant
│   │   ├── controller/               # REST controllers
│   │   ├── converter/                # JPA attribute converters (YearMonth)
│   │   ├── dto/                      # Request/Response records grouped by domain
│   │   ├── entity/                   # JPA entities (BaseEntity → UUID, timestamps)
│   │   ├── enums/                    # Domain enums (TxnType, ErrorCode, ErrorType...)
│   │   ├── exception/                # Domain exceptions extending BaseException
│   │   │   └── handler/              # GlobalExceptionHandler (@RestControllerAdvice)
│   │   ├── mapper/                   # Pure static mapper classes (no MapStruct)
│   │   ├── repository/               # Spring Data JPA repositories
│   │   ├── schedular/                # @Scheduled jobs (recurring, goals, cleanup)
│   │   ├── security/                 # JWT filter, service, entry points
│   │   ├── service/                  # Service interfaces + impl/ package
│   │   ├── util/                     # SecurityUtils, ResponseUtil, KeyUtils...
│   │   └── websocket/                # WebSocket notification publisher
│   ├── src/main/resources/
│   │   ├── application.yaml          # Shared config
│   │   ├── application-dev.yaml      # Dev profile (Ollama, local DB)
│   │   ├── application-prod.yaml     # Prod profile (OpenAI, NeonDB, Upstash)
│   │   ├── db/migration/             # Flyway SQL migrations (V1, V2, V3)
│   │   ├── keys/                     # RSA key pair (PKCS8 private + X.509 public)
│   │   ├── prompts/                  # walletiq-system-prompt.txt (LLM system prompt)
│   │   └── templates/mail/           # Thymeleaf HTML email templates
│   └── pom.xml
│
├── frontend/                         # React + TypeScript + Vite
│   └── src/
│       ├── api/                      # errorParser.ts (AppError class)
│       ├── features/                 # Feature-first folder structure
│       │   ├── auth/                 # Login, Signup, hooks, service, types
│       │   ├── dashboard/            # Dashboard page + components + service
│       │   ├── transactions/         # Transactions page + table + form + service
│       │   ├── recurring/            # Recurring page + forecast panel + service
│       │   ├── budgets/              # Budgets page + form + card + service
│       │   ├── savings/              # Savings goals page + form + modal + service
│       │   ├── categories/           # Categories page + row + service
│       │   ├── payment-modes/        # Payment modes page + row + service
│       │   ├── chat/                 # Chat page + session list + message components
│       │   ├── notifications/        # Notification bell + service
│       │   ├── profile/              # Profile page + email verify panel + service
│       │   ├── admin/                # Admin page + stats grid + user table
│       │   ├── subscription/         # Subscription page + Razorpay hook + service
│       │   ├── about/                # About page + app info components
│       │   └── home/                 # Landing page (hero, features, testimonials)
│       ├── lib/axios.ts              # Axios instance + interceptors + silent refresh
│       ├── routes/                   # AppRoutes.tsx + routePaths.ts
│       ├── shared/
│       │   ├── components/           # Layout (Sidebar, MonthNavigator), UI (Spinner, QueryError)
│       │   ├── constants/            # homeData.ts (features, steps, stats, testimonials)
│       │   ├── hooks/                # useAppQuery, useAppMutation, useAuth
│       │   └── utils/                # errorUtils.ts, profileHelpers.ts, animationHooks.ts
│       ├── store/authStore.ts        # Zustand auth store (persisted)
│       └── types/api.types.ts        # ResponseWrapper, ErrorResponse, FieldError
│
├── docker/
│   ├── backend/Dockerfile            # Multi-stage build (Maven builder + JRE runtime)
│   ├── frontend/Dockerfile           # Multi-stage build (Node builder + static output)
│   ├── nginx/
│   │   ├── nginx.conf                # Reverse proxy (API + WebSocket + frontend)
│   │   └── spa.conf                  # SPA fallback config
│   ├── docker-compose.yml            # Production compose (backend + frontend + nginx)
│   └── docker-compose.dev.yml        # Local dev infrastructure (PostgreSQL + Redis only)
│
├── .github/workflows/
│   ├── backend-deploy.yml            # SSH deploy to VPS on backend changes
│   └── frontend-deploy.yml           # Vercel deploy on frontend changes
│
└── README.md
```

---

## Getting Started (Local Development)

### Prerequisites

Make sure the following are installed on your machine:

| Tool           | Version | Notes                                        |
| -------------- | ------- | -------------------------------------------- |
| Java           | 21+     | [Download Temurin](https://adoptium.net)     |
| Maven          | 3.9+    | Or use the included `./mvnw` wrapper         |
| Node.js        | 20+     | [Download](https://nodejs.org)               |
| Docker         | 24+     | For PostgreSQL + Redis                       |
| Docker Compose | 2.x     | Usually bundled with Docker Desktop          |
| Git            | Any     | For cloning                                  |

> **OpenAI API Key required** — AI-powered features (chat, embeddings) require an OpenAI API key.
> New accounts receive **$5 in free credits**, which is more than sufficient for development and testing.
> If you prefer not to use OpenAI, see the alternatives below.

<details>
<summary>Alternatives if you don't have an OpenAI key</summary>

- **Ollama (local AI)** — swap the embedding and chat models in `application-dev.yml` to use Ollama. You'll need to pull the models yourself (`ollama pull nomic-embed-text`, etc.).
- **Disable AI features** — comment out the AI-related endpoints in the controllers to run the rest of the app without AI functionality.

</details>

---

### 1. Clone the Repository

```bash
git clone https://github.com/ripanbaidya/wallet-iq.git
cd wallet-iq
```

---

### 2. Configure Environment

The backend reads configuration from a `.env.local` file inside the `/docker` directory.
A template is provided — copy it and fill in your values:

```bash
cp docker/.env.local.template docker/.env.local
```

Open `docker/.env.local` and set at minimum:

- `OPENAI_API_KEY` — your OpenAI API key
- `MAIL_USERNAME` / `MAIL_PASSWORD` — Gmail address and [App Password](https://myaccount.google.com/apppasswords)

Everything else (DB, Redis, ports) is pre-filled and works out of the box with the Docker setup.

---

### 3. Start the Full Stack

Start the backend, PostgreSQL, and Redis with a single command:

```bash
docker compose -f docker/docker-compose.local.yml up -d
```

This starts:

| Service    | Address          | Details                                          |
| ---------- | ---------------- | ------------------------------------------------ |
| Backend    | `localhost:8080` | Spring Boot API                                  |
| PostgreSQL | `localhost:5432` | Database: `walletiq` · User/Password: `postgres` |
| Redis      | `localhost:6379` | Password: `strongPassword`                       |

Verify all containers are running:

```bash
docker ps
```

Once up, the full API is browsable at:
`http://localhost:8080/api/v1/swagger-ui/index.html`

---

### 4. Set Up the Admin User

A default admin is seeded by `V3__create_system_admin.sql`. Before running the migration,
generate a bcrypt hash for your chosen password using the hash endpoint:

```bash
curl -X POST http://localhost:8080/api/v1/auth/password-hash \
  -H "Content-Type: application/json" \
  -d '{"password": "your_password"}'
```

Copy the returned hash into your migration file before the containers start for the first time.

| Field    | Value                        |
| -------- | ---------------------------- |
| Email    | the email you seeded         |
| Password | the plain-text password used |

---

### 5. Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend reads its API base URL from `.env.development`, which is already committed with the correct default:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

The dev server starts at `http://localhost:5173`.

### Verify Everything Works

| Service        | URL                                            | Expected               |
| -------------- | ---------------------------------------------- | ---------------------- |
| Backend health | `http://localhost:8080/api/v1/actuator/health` | `{"status":"UP"}`      |
| App info       | `http://localhost:8080/api/v1/app/info`        | JSON with app metadata |
| Swagger UI     | `http://localhost:8080/api/v1/swagger-ui.html` | Interactive API docs   |
| Frontend       | `http://localhost:5173`                        | Landing page           |
| Frontend app   | `http://localhost:5173/login`                  | Login page             |

---

## Environment Variables Reference

### Backend (`application-dev.yaml` / `application-prod.yaml`)

| Variable                   | Required  | Default                        | Description                                            |
| -------------------------- | --------- | ------------------------------ | ------------------------------------------------------ |
| `MAIL_USERNAME`            | ✅        | —                              | Gmail address for sending emails                       |
| `MAIL_PASSWORD`            | ✅        | —                              | Gmail App Password (not your login password)           |
| `REDIS_URL`                | ✅        | —                              | Redis connection URL (`redis://...` or `rediss://...`) |
| `DB_HOST`                  | Prod only | —                              | PostgreSQL host                                        |
| `DB_PORT`                  | Prod only | `5432`                         | PostgreSQL port                                        |
| `DB_NAME`                  | Prod only | —                              | Database name                                          |
| `DB_USERNAME`              | Prod only | —                              | Database user                                          |
| `DB_PASSWORD`              | Prod only | —                              | Database password                                      |
| `OPENAI_API_KEY`           | Prod only | —                              | OpenAI API key                                         |
| `OPENAI_CHAT_MODEL`        | Prod only | `gpt-4o-mini`                  | OpenAI chat model                                      |
| `OPENAI_EMBEDDING_MODEL`   | Prod only | `text-embedding-3-small`       | OpenAI embedding model                                 |
| `RAZORPAY_KEY_ID`          | ✅        | `rzp_test_*` (test key in dev) | Razorpay key ID                                        |
| `RAZORPAY_KEY_SECRET`      | ✅        | test secret (in dev)           | Razorpay key secret                                    |
| `JWT_ACCESS_TOKEN_EXPIRY`  | ❌        | `604800000` (7d)               | Access token TTL in ms                                 |
| `JWT_REFRESH_TOKEN_EXPIRY` | ❌        | `2592000000` (30d)             | Refresh token TTL in ms                                |
| `SERVER_PORT`              | ❌        | `8080`                         | HTTP server port                                       |
| `CORS_ORIGIN_VERCEL`       | Prod only | —                              | Vercel frontend URL                                    |
| `CORS_ORIGIN_PRIMARY`      | Prod only | —                              | Primary domain URL                                     |

### Frontend (`.env.devlopement`)

| Variable            | Required | Default                        | Description          |
| ------------------- | -------- | ------------------------------ | -------------------- |
| `VITE_API_BASE_URL` | ✅       | `http://localhost:8080/api/v1` | Backend API base URL |

---

## API Documentation (Swagger)

When running in `dev` profile, the full interactive Swagger UI is available at:

```
http://localhost:8080/api/v1/swagger-ui.html
```

And the raw OpenAPI JSON spec at:

```
http://localhost:8080/api/v1/api-docs
```

The Swagger UI lets you:

- Browse all endpoints grouped by controller tag
- Execute requests directly from the browser
- Authenticate with a JWT token via the **Authorize** button (top right)
- See request/response schemas and examples

> **Swagger is disabled in production** (`springdoc.swagger-ui.enabled: false` in `application-prod.yaml`) for security.

### Getting a JWT to Test APIs

1. Hit `POST /api/v1/auth/login` with admin credentials
2. Copy the `accessToken` from the response
3. Click **Authorize** in Swagger UI
4. Enter `Bearer <your-token>` and click Authorize
5. All protected endpoints will now work

**Or**, You can simply create a new account and then use that account to login and get the JWT token.

---

## Key Design Decisions & Best Practices

### Backend

**Layered Architecture with Clean Separation**

- Controllers only delegate to services — no business logic
- Services are interface-driven (`CategoryService` ← `CategoryServiceImpl`)
- Repositories contain only queries — no business logic
- Mappers are pure static methods — no Spring beans, no state

**Standardized Error Handling**

- All custom exceptions extend `BaseException` with an `ErrorCode` enum
- `GlobalExceptionHandler` catches all exceptions and formats them into a consistent `ErrorResponse`
- `ErrorCode` contains the `ErrorType` (which maps to HTTP status) and a default message
- Field validation errors from `@Valid` are surfaced in a structured `errors` array

**Security**

- RSA key pair (RS512) for JWT signing — asymmetric, harder to forge than HS256
- Refresh token rotation on every `/auth/refresh-token` call
- Refresh tokens are stored in DB and can be individually revoked
- Path validation on key loading prevents directory traversal attacks
- A `shouldNotFilter` override in the JWT filter skips public routes without touching the security context

**Caching Strategy**

- Dashboard results cached per `userId:month` key (10-minute TTL) — invalidated on any transaction create/update
- Categories and payment modes cached per `userId-type` (24-hour TTL) — invalidated on any CRUD
- Custom `KeyGenerator` beans generate compound cache keys from `SecurityContext` + method params

**Pagination Safety**

- `PageableValidator` whitelists allowed sort fields before passing to JPA — prevents SQL injection via sort parameters

**Budget Alerts Never Block Transactions**

- `BudgetAlertService.checkAndAlert()` is called after transaction save and never throws
- Designed intentionally so a budget check failure cannot roll back a transaction

**Scheduled Jobs**

- Recurring transaction processor: 8 AM daily
- Savings goal expiry checker: 12:30 AM daily
- Refresh token cleanup: 2 AM daily
- Daily email summary: 9 PM daily
- Subscription expiry processor: configurable

### Frontend

**Feature-First Folder Structure**

- Each feature contains its own page, components, service, types, and hooks
- No "global components" unless truly shared (Spinner, QueryError, Layout)
- Easy to add or remove features without touching unrelated code

**Axios with Silent Token Refresh**

- A single Axios instance with request/response interceptors
- On a 401 response, the interceptor automatically calls `/auth/refresh-token`
- All in-flight requests during refresh are queued and replayed after the new token is received
- No user sees a logout unless the refresh token itself has expired

**React Query for Server State**

- All API calls use `useAppQuery` / `useAppMutation` wrappers around React Query
- `staleTime` and `placeholderData` configured for smooth navigation (no flickering)
- Cache invalidation is explicit and surgical after mutations

**Type Safety**

- `ResponseWrapper<T>` and `ErrorResponse` typed from the backend contract
- `AppError` class wraps `ErrorDetail` with convenience predicates (`isValidation`, `isConflict`, etc.)
- No `any` in production code paths

**Zustand Auth Store**

- Persisted to `localStorage` via `zustand/middleware/persist`
- Only the minimum auth state is persisted (user, accessToken, refreshToken)
- An `auth:unauthorized` custom event bridges the Axios interceptor to the React component tree without coupling

---

## Database Schema

The database is managed entirely by Flyway migrations. The full schema is in `V1__init_schema.sql`. Key tables:

| Table                    | Purpose                                                      |
| ------------------------ | ------------------------------------------------------------ |
| `users`                  | User accounts, roles, email verification status              |
| `categories`             | Income/expense categories (system defaults + user-defined)   |
| `payment_modes`          | Payment methods (system defaults + user-defined)             |
| `transactions`           | All financial transactions with optional embedding ID        |
| `recurring_transactions` | Recurring rules with next execution date tracking            |
| `budgets`                | Monthly category budgets with alert thresholds               |
| `savings_goals`          | Long-term savings targets with progress tracking             |
| `refresh_token`          | Stored refresh tokens for revocation support                 |
| `email_verification_otp` | Time-limited OTPs for email verification                     |
| `chat_sessions`          | AI chat session metadata                                     |
| `chat_messages`          | Individual messages per session (USER / ASSISTANT)           |
| `vector_store`           | Transaction embeddings (1536-dim for OpenAI, 768 for Ollama) |
| `notifications`          | In-app notification log                                      |
| `subscriptions`          | Razorpay subscription records                                |

---

### **Class Diagram**

![class-diagram](/public/diagrams/postgres@production.png)

---

## Deployment

### Production Stack

| Component | Platform                                          |
| --------- | ------------------------------------------------- |
| Backend   | VPS (Docker + Traefik for HTTPS)                  |
| Frontend  | Vercel                                            |
| Database  | Neon (serverless PostgreSQL with pgvector)        |
| Cache     | Upstash (serverless Redis with TLS)               |
| Email     | Gmail SMTP via App Password                       |
| AI        | OpenAI API (gpt-4o-mini + text-embedding-3-small) |
| Payments  | Razorpay                                          |

### CI/CD

Two GitHub Actions workflows handle automated deployment:

**`frontend-deploy.yml`** — Triggers on any push to `main` that changes `frontend/**`:

1. Installs Node.js dependencies
2. Builds the React app with `VITE_API_BASE_URL` from GitHub Secrets
3. Deploys to Vercel via the Vercel CLI

**`backend-deploy.yml`** — Triggers on any push to `main` that changes `backend/**` or Docker files:

1. SSHs into the VPS using a stored private key
2. Pulls latest changes from GitHub
3. Rebuilds the backend Docker image (no cache)
4. Restarts the container with `docker compose up -d`

### Run Production Locally with Docker

```bash
# From the project root
docker compose -f docker/docker-compose.yml up --build
```

This runs the full production stack (backend + nginx) locally. You'll need a `.env` file at `/opt/walletiq/.env` or adjust the `env_file` path in `docker-compose.yml`.

---

## Screenshots

<!-- Home -->

![home](/public/images/web/home/img1.png)
![home-img2](/public/images/web/home/img2.png)
![home-img3](/public/images/web/home/img3.png)

<!-- Login + Signup -->

![signup](/public/images/web/features/auth/signup.png)

<!-- Dashboard -->

![dashboard](/public/images/web/features/dashboard/home.png)

<h4>👉 <a href="/public/screenshots.md">Click here</a> to view more screenshots.</h4>

## License

This project is licensed under the **Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (CC BY-NC-SA 4.0)**.

**What this means:**

✅ You **CAN**:

- View, study, and learn from this code
- Fork the repository for personal learning or educational purposes
- Share the code with proper attribution

❌ You **CANNOT**:

- Use this project (or any derivative) for commercial purposes
- Submit this project as your own academic or professional work
- Redistribute modified versions under a different license
- Remove or alter attribution notices

**In plain terms:** You're welcome to read, learn from, and reference this code. You cannot take this project and claim it as your own portfolio project, sell it, or use it commercially.

[Read the full license →](https://creativecommons.org/licenses/by-nc-sa/4.0/)

> **Note:** If you wish to use this project for commercial purposes or need a different licensing arrangement, please contact the author directly.

---

## Author

**Ripan Baidya**

- GitHub: [@ripanbaidya](https://github.com/ripanbaidya)
- LinkedIn: [linkedin.com/in/ripanbaidya](https://www.linkedin.com/in/ripanbaidya/)
- Instagram: [@futurenoogler](https://www.instagram.com/futurenoogler)
- Email: official.ripanbaidya@gmail.com

---

<div align="center">

Built with ❤️ by Ripan Baidya

_If you find this project useful for learning, please consider giving it a ⭐ on GitHub!_

</div>
