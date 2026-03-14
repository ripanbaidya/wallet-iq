# WalletIq - RAG-Based Expense Tracker

### Features

**Already Implemented**

- Authentication and Authorization
- Manage Category + Payment Mode
- Transaction (Income + Expense)
- RAG based chat bot, uses users data to give answers and remember past conversions
- Chat session + Chat messages
- Email Verification via otp
- Send summary of transaction each day via email
- user can export there transactions details in the form of CSV

**Fix**

- In the income category, need to add <i>enum</i> for income and expense. for both income and expense user would have
  different category
- update the income save logic
-

**Implementation Pending**

1. Recurring Transactions - Users mark transactions as recurring. The system auto-generates future instances and uses
   them for forecasting.

```http request
POST   /api/recurring                  → create recurring transaction
GET    /api/recurring                  → list all recurring
PATCH  /api/recurring/{id}             → update (amount, frequency)
DELETE /api/recurring/{id}             → deactivate
GET    /api/recurring/forecast?days=30 → projected cash flow
```

2. Budget & Goals System - Users set spending limits per category per month, and saving targets with deadlines. The
   system tracks progress in
   real-time.
```http request
POST   /api/budgets                    → create budget
GET    /api/budgets?month=2025-07      → get budgets for a month
GET    /api/budgets/{id}/status        → current spend vs limit

POST   /api/goals                      → create savings goal
GET    /api/goals                      → list all goals
PATCH  /api/goals/{id}/contribute      → add money toward goal
GET    /api/goals/{id}/progress        → % achieved, days remaining
```

3. Notification using Websocket

### Core Entities

**User**

```
id (UUID), name, email, password, role (ENUM: USER/ADMIN), createdAt
```

**Category**

```
id (UUID), name, user (nullable → null = default/system), createdAt
```

**PaymentMode**

```
id (UUID), name, user (nullable → null = default/system), createdAt
```

**Transaction**

```
id (UUID), amount (BigDecimal), type (ENUM: INCOME/EXPENSE),
date (LocalDate), note (nullable), embeddingId (String, nullable),
user → ManyToOne(User),
category → ManyToOne(Category),
paymentMode → ManyToOne(PaymentMode),
createdAt
```

**ChatSession**

```
id (UUID), title (String), user → ManyToOne(User), createdAt
```

**ChatMessage**

```
id (UUID), role (ENUM: USER/ASSISTANT), content (Text),
chatSession → ManyToOne(ChatSession), createdAt
```

---

### Why `embeddingId` on Transaction?

When a transaction is saved, you generate a text representation (e.g., *"Spent ₹450 on Food via Card on 2025-03-01.
Note: lunch with team"*) and store its vector in the vector DB. `embeddingId` links the relational row to its vector
document so you can delete/update embeddings when transactions change.

---

## 2. Entity Relationships

```
User ──< Transaction         (OneToMany)
User ──< Category            (OneToMany, nullable user = system default)
User ──< PaymentMode         (OneToMany, nullable user = system default)
User ──< ChatSession         (OneToMany)
ChatSession ──< ChatMessage  (OneToMany)
Transaction >── Category     (ManyToOne)
Transaction >── PaymentMode  (ManyToOne)
```

**Category & PaymentMode ownership rule:**

- `user IS NULL` → system-wide default (visible to everyone)
- `user IS NOT NULL` → private to that user

## 3. API Design

### Authentication (already done)

| Method | Endpoint             | Description   |
|--------|----------------------|---------------|
| POST   | `/api/auth/register` | Register user |
| POST   | `/api/auth/login`    | Login         |
| POST   | `/api/auth/logout`   | Logout        |

---

### Categories

| Method | Endpoint               | Description              |
|--------|------------------------|--------------------------|
| GET    | `/api/categories`      | List system + user's own |
| POST   | `/api/categories`      | Create custom category   |
| PUT    | `/api/categories/{id}` | Update (own only)        |
| DELETE | `/api/categories/{id}` | Delete (own only)        |

---

### Payment Modes

| Method | Endpoint                  | Description              |
|--------|---------------------------|--------------------------|
| GET    | `/api/payment-modes`      | List system + user's own |
| POST   | `/api/payment-modes`      | Create custom mode       |
| PUT    | `/api/payment-modes/{id}` | Update (own only)        |
| DELETE | `/api/payment-modes/{id}` | Delete (own only)        |

---

### Transactions

| Method | Endpoint                 | Description                                    |
|--------|--------------------------|------------------------------------------------|
| GET    | `/api/transactions`      | List with filters (type, date range, category) |
| GET    | `/api/transactions/{id}` | Get single transaction                         |
| POST   | `/api/transactions`      | Create + trigger embedding                     |
| PUT    | `/api/transactions/{id}` | Update + re-embed                              |
| DELETE | `/api/transactions/{id}` | Delete + remove embedding                      |

---

### Chat Sessions

| Method | Endpoint                  | Description               |
|--------|---------------------------|---------------------------|
| GET    | `/api/chat/sessions`      | List user's sessions      |
| POST   | `/api/chat/sessions`      | Create new session        |
| DELETE | `/api/chat/sessions/{id}` | Delete session + messages |

---

### Chat Messages & RAG Query

| Method | Endpoint                           | Description                    |
|--------|------------------------------------|--------------------------------|
| GET    | `/api/chat/sessions/{id}/messages` | Get full conversation history  |
| POST   | `/api/chat/sessions/{id}/query`    | Send question → get RAG answer |

The `/query` endpoint is the heart of the RAG feature. Request body:

```json
{
  "question": "How much did I spend on food last month?"
}
```

Response:

```json
{
  "answer": "You spent ₹3,200 on food last month across 8 transactions.",
  "sessionId": "uuid"
}
```

---

### Admin APIs

| Method | Endpoint                             | Description                  |
|--------|--------------------------------------|------------------------------|
| GET    | `/api/admin/users`                   | List all users               |
| GET    | `/api/admin/users/{id}/transactions` | View any user's transactions |

## 6. Database Schema

```sql
-- Users
CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(100)        NOT NULL,
    email      VARCHAR(150) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    role       VARCHAR(20)         NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP                    DEFAULT now()
);

-- Categories (user NULL = system default)
CREATE TABLE categories
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    user_id    UUID REFERENCES users (id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT now(),
    UNIQUE (name, user_id) -- prevent duplicate names per user
);

-- Payment Modes (user NULL = system default)
CREATE TABLE payment_modes
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    user_id    UUID REFERENCES users (id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT now(),
    UNIQUE (name, user_id)
);

-- Transactions
CREATE TABLE transactions
(
    id              UUID PRIMARY KEY,
    amount          NUMERIC(12, 2) NOT NULL,
    type            VARCHAR(10)    NOT NULL, -- INCOME | EXPENSE
    date            DATE           NOT NULL,
    note            TEXT,
    embedding_id    VARCHAR(255),            -- ID in vector store
    user_id         UUID           NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    category_id     UUID           NOT NULL REFERENCES categories (id),
    payment_mode_id UUID           NOT NULL REFERENCES payment_modes (id),
    created_at      TIMESTAMP DEFAULT now()
);

-- Chat Sessions
CREATE TABLE chat_sessions
(
    id         UUID PRIMARY KEY,
    title      VARCHAR(255),
    user_id    UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT now()
);

-- Chat Messages
CREATE TABLE chat_messages
(
    id         UUID PRIMARY KEY,
    role       VARCHAR(20) NOT NULL, -- USER | ASSISTANT
    content    TEXT        NOT NULL,
    session_id UUID        NOT NULL REFERENCES chat_sessions (id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT now()
);

-- Indexes for common queries
CREATE INDEX idx_transactions_user_date ON transactions (user_id, date);
CREATE INDEX idx_chat_messages_session ON chat_messages (session_id, created_at);
```

---
