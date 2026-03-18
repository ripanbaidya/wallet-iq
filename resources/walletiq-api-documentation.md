# WalletIq - API's

## Authentication - done

| API | Description |
|---|---|
| `POST /auth/signup` | Registers a new user with full name, email, and password. Triggers OTP email for verification. |
| `POST /auth/login` | Authenticates user credentials and returns a JWT access token + refresh token pair. |
| `POST /auth/logout` | Revokes the provided refresh token, invalidating the session. |
| `POST /auth/refresh-token` | Issues a new access token using a valid refresh token. Implements rotation — old token is revoked on use. |
| `POST /auth/email/send-otp` | Generates a 6-digit OTP and dispatches it to the user's registered email address. |
| `POST /auth/email/verify-otp` | Validates the submitted OTP against the stored hash. Marks the account as email-verified on success. |
| `POST /auth/password-hash` | Utility endpoint — hashes a plain-text password via BCrypt. Development use only, should be hidden in production. |

---

## Users - done

| API | Description |
|---|---|
| `GET /users/me` | Returns the authenticated user's profile — name, email, active status, email verification state. |
| `PATCH /users/me` | Updates the authenticated user's full name. Partial update — only provided fields are modified. |

---

## Transactions - done

| API | Description |
|---|---|
| `GET /transactions` | Returns a paginated list of the user's transactions. Supports optional filtering by type (INCOME/EXPENSE), category, and date range. |
| `POST /transactions` | Creates a new transaction (income or expense). Triggers embedding generation for RAG and budget alert check as side effects. |
| `GET /transactions/{id}` | Fetches a single transaction by ID. Enforces ownership — returns 404 if not owned by the authenticated user. |
| `PUT /transactions/{id}` | Fully updates an existing transaction. Re-triggers embedding update and budget alert re-evaluation. |
| `DELETE /transactions/{id}` | Deletes a transaction and removes its corresponding vector embedding. Evicts the dashboard cache for the affected month. |
| `GET /transactions/export/csv` | Exports all transactions for the authenticated user as a downloadable CSV file. |

---

## Categories - done

| API | Description |
|---|---|
| `GET /categories?type=` | Returns all categories for the authenticated user filtered by type (INCOME or EXPENSE). Includes system default categories. Response is Redis-cached per user, invalidated on any mutation. |
| `POST /categories` | Creates a new user-scoped category with a name and type. Evicts the category cache. |
| `PUT /categories/{id}` | Updates the name or type of an existing category. Evicts the category cache. |
| `DELETE /categories/{id}` | Deletes a category by ID. Evicts the category cache. |

---

## Payment Modes - done

| API | Description |
|---|---|
| `GET /payment-modes` | Returns all payment modes available to the authenticated user. Includes system defaults. Response is Redis-cached per user, invalidated on any mutation. |
| `POST /payment-modes` | Creates a new user-scoped payment mode (e.g. UPI, Cash, Credit Card). Evicts the payment mode cache. |
| `PUT /payment-modes/{id}` | Updates the name of an existing payment mode. Evicts the payment mode cache. |
| `DELETE /payment-modes/{id}` | Deletes a payment mode by ID. Evicts the payment mode cache. |

---

## Budgets - done

| API | Description |
|---|---|
| `POST /budgets` | Creates a monthly spending budget for a specific category. Accepts a limit amount and an alert threshold percentage (default 80%). Unique per user + category + month. |
| `GET /budgets?month=` | Returns all budgets set by the authenticated user for the specified month (format: `YYYY-MM`). |
| `GET /budgets/{id}/status` | Returns real-time spending status for a budget — spent amount, remaining amount, usage percentage, and flags indicating whether the alert threshold or hard limit has been breached. |

---

## Savings Goals - done

| API | Description |
|---|---|
| `POST /goals` | Creates a new savings goal with a title, target amount, deadline, and optional note. Initial status is `IN_PROGRESS`. |
| `GET /goals` | Returns all savings goals for the authenticated user with full progress details. |
| `GET /goals/{id}/progress` | Returns detailed progress for a single goal — saved amount, remaining amount, progress percentage, days remaining, and current status. |
| `PATCH /goals/{id}/contribute` | Adds a monetary contribution toward the goal. Recalculates progress and updates status to `ACHIEVED` if target is met, or `FAILED` if deadline has passed. |

---

## Recurring Transactions - done

| API | Description |
|---|---|
| `POST /recurring` | Creates a recurring transaction rule with a title, amount, type, frequency (DAILY/WEEKLY/MONTHLY/YEARLY), start date, optional end date, category, and payment mode. Sets the initial `next_execution_date`. |
| `GET /recurring` | Returns all active recurring transaction rules for the authenticated user. |
| `GET /recurring/{id}` | Fetches a single recurring transaction rule by ID. |
| `PATCH /recurring/{id}` | Updates an existing recurring rule — amount, frequency, end date, category, payment mode, or note. |
| `DELETE /recurring/{id}` | Soft-deletes a recurring rule by setting `is_active = false`. The rule is retained for historical reference. |
| `GET /recurring/forecast?days=` | Projects the cash flow for the next N days (1–365, default 30) based on all active recurring rules. Returns projected income, expense, net balance, and a day-by-day breakdown. |

---

## Dashboard - done

| API | Description |
|---|---|
| `GET /dashboard?month=` | Returns a full financial snapshot for the specified month (defaults to current month). Includes: total income/expense/net balance summary, expense and income breakdown by category with percentages (chart-ready), daily income vs expense trend (chart-ready), and top 5 expenses. Response is Redis-cached per user per month with a 10-minute TTL. Cache is evicted when a transaction is created, updated, or deleted for that month. |

---

## Notifications - done

| API | Description |
|---|---|
| `GET /notifications` | Returns all notifications for the authenticated user ordered by creation date descending. Notifications are pushed in real-time via WebSocket (`/topic/notifications/{userId}`) and persisted to the database. |
| `DELETE /notifications/{id}` | Deletes a specific notification by ID. Ownership is enforced — wrong owner receives 404 to prevent existence leakage. |
| `DELETE /notifications` | Deletes all notifications for the authenticated user. |

---

## Chat (RAG) - done

| API | Description |
|---|---|
| `POST /chat/sessions` | Creates a new chat session with an optional title. Each session maintains isolated conversation history. |
| `GET /chat/sessions` | Returns all chat sessions for the authenticated user. |
| `DELETE /chat/sessions/{id}` | Deletes a chat session and all its associated messages. |
| `GET /chat/sessions/{id}/messages` | Returns the full message history for a specific session, ordered chronologically. |
| `POST /chat/sessions/{id}/query` | Submits a question to the RAG-powered finance assistant. The assistant retrieves semantically relevant transactions from PgVector, combines them with conversation history, and returns a contextual financial insight. |

---

## Admin - done

| API | Description |
|---|---|
| `GET /admin/users` | Returns a paginated list of all registered users. Requires ADMIN role. |
| `GET /admin/users/{id}` | Returns profile details for a specific user by ID. Requires ADMIN role. |
| `GET /admin/users/count?role=&active=` | Returns the total count of users filtered by role (ADMIN/USER) and active status. Useful for admin dashboard metrics. |

---

## WebSocket

| Channel | Description |
|---|---|
| `STOMP /ws` (SockJS endpoint) | Handshake endpoint for establishing a WebSocket connection. Requires authentication. |
| `/topic/notifications/{userId}` | User-scoped subscription topic. The server pushes `NotificationResponse` payloads here whenever a notification is triggered — budget alerts, recurring transaction executions, savings goal status changes, or system events. |

---

**Total: 39 REST endpoints + 1 WebSocket topic across 11 functional areas.**