-- =============================================================================
-- WalletIQ — Full Database Migration Script For Initialize Schema
-- =============================================================================

-- Extensions

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS vector;

-- Core user table
-- role → Role enum (ADMIN | USER)

CREATE TABLE users
(
    id                UUID                        NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    full_name         VARCHAR(100),
    email             VARCHAR(100),
    password_hash     VARCHAR(100),
    role              VARCHAR(20)                 NOT NULL,
    active            BOOLEAN                     NOT NULL,
    is_email_verified BOOLEAN                     NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uc_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'USER'))
);

CREATE INDEX idx_users_email ON users (email);

-- Categories

CREATE TABLE categories
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name          VARCHAR(100)                NOT NULL,
    category_type VARCHAR(10),
    user_id       UUID,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uq_category_name_user UNIQUE (name, user_id),
    CONSTRAINT chk_categories_type CHECK (category_type IN ('INCOME', 'EXPENSE')),
    CONSTRAINT FK_CATEGORIES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_categories_user_id ON categories (user_id);

-- Payment modes
-- user_id is nullable — allows system-wide default payment modes

CREATE TABLE payment_modes
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name       VARCHAR(100)                NOT NULL,
    user_id    UUID,
    CONSTRAINT pk_payment_modes PRIMARY KEY (id),
    CONSTRAINT uq_payment_mode_name_user UNIQUE (name, user_id),
    CONSTRAINT FK_PAYMENT_MODES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_payment_modes_user_id ON payment_modes (user_id);

-- Transactions
-- type → TxnType enum (INCOME | EXPENSE)

CREATE TABLE transactions
(
    id              UUID                        NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    amount          DECIMAL(12, 2)              NOT NULL,
    type            VARCHAR(10)                 NOT NULL,
    date            date                        NOT NULL,
    note            TEXT,
    embedding_id    VARCHAR(255),
    user_id         UUID                        NOT NULL,
    category_id     UUID,
    payment_mode_id UUID,
    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT chk_transactions_type CHECK (type IN ('INCOME', 'EXPENSE')),
    CONSTRAINT FK_TRANSACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_TRANSACTIONS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT FK_TRANSACTIONS_ON_PAYMENT_MODE FOREIGN KEY (payment_mode_id) REFERENCES payment_modes (id)
);

CREATE INDEX idx_transactions_user_date ON transactions (user_id, date);
CREATE INDEX idx_transactions_user_id ON transactions (user_id);
CREATE INDEX idx_transactions_category_id ON transactions (category_id);
CREATE INDEX idx_transactions_payment_mode_id ON transactions (payment_mode_id);

-- Recurring transactions
-- type      → TxnType enum (INCOME | EXPENSE)

CREATE TABLE recurring_transactions
(
    id                  UUID                        NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title               VARCHAR(100)                NOT NULL,
    amount              DECIMAL(12, 2)              NOT NULL,
    type                VARCHAR(10)                 NOT NULL,
    frequency           VARCHAR(10)                 NOT NULL,
    start_date          date                        NOT NULL,
    end_date            date,
    next_execution_date date                        NOT NULL,
    is_active           BOOLEAN                     NOT NULL,
    note                VARCHAR(255),
    user_id             UUID                        NOT NULL,
    category_id         UUID,
    payment_mode_id     UUID,
    CONSTRAINT pk_recurring_transactions PRIMARY KEY (id),
    CONSTRAINT chk_recurring_type CHECK (type IN ('INCOME', 'EXPENSE')),
    CONSTRAINT chk_recurring_frequency CHECK (frequency IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY')),
    CONSTRAINT FK_RECURRING_TRANSACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_RECURRING_TRANSACTIONS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT FK_RECURRING_TRANSACTIONS_ON_PAYMENT_MODE FOREIGN KEY (payment_mode_id) REFERENCES payment_modes (id)
);

CREATE INDEX idx_recurring_scheduler ON recurring_transactions (is_active, next_execution_date);
CREATE INDEX idx_recurring_user_active ON recurring_transactions (user_id, is_active);

-- Budgets
-- month format: 'YYYY-MM' (e.g. '2025-03') — enforced via CHECK

CREATE TABLE budgets
(
    id              UUID                        NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id         UUID                        NOT NULL,
    category_id     UUID                        NOT NULL,
    month           VARCHAR(7)                  NOT NULL,
    limit_amount    DECIMAL(12, 2)              NOT NULL,
    alert_threshold INTEGER                     NOT NULL,
    CONSTRAINT pk_budgets PRIMARY KEY (id),
    CONSTRAINT uq_budget_user_category_month UNIQUE (user_id, category_id, month),
    CONSTRAINT chk_budgets_month_format CHECK (month ~ '^\d{4}-(0[1-9]|1[0-2])$'),
    CONSTRAINT FK_BUDGETS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_BUDGETS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE INDEX idx_budget_user_month ON budgets (user_id, month);

-- Savings goals
-- status → GoalStatus enum (IN_PROGRESS | ACHIEVED | FAILED)

CREATE TABLE savings_goals
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title         VARCHAR(100)                NOT NULL,
    target_amount DECIMAL(12, 2)              NOT NULL,
    saved_amount  DECIMAL(12, 2)              NOT NULL,
    deadline      date                        NOT NULL,
    status        VARCHAR(20)                 NOT NULL,
    note          TEXT,
    user_id       UUID                        NOT NULL,
    CONSTRAINT pk_savings_goals PRIMARY KEY (id),
    CONSTRAINT chk_savings_goal_status CHECK (status IN ('IN_PROGRESS', 'ACHIEVED', 'FAILED')),
    CONSTRAINT FK_SAVINGS_GOALS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_goal_user_id ON savings_goals (user_id);

-- Auth — refresh tokens

CREATE TABLE refresh_token
(
    id         UUID                        NOT NULL,
    token      TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    revoked    BOOLEAN                     NOT NULL,
    revoked_at TIMESTAMP WITHOUT TIME ZONE,
    user_id    UUID                        NOT NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id),
    CONSTRAINT uc_refresh_token_token UNIQUE (token),
    CONSTRAINT FK_REFRESH_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_refresh_token_user ON refresh_token (user_id);

-- Auth — email verification OTP

CREATE TABLE email_verification_otp
(
    id         UUID                        NOT NULL,
    otp        VARCHAR(6)                  NOT NULL,
    used       BOOLEAN                     NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id    UUID                        NOT NULL,
    CONSTRAINT pk_email_verification_otp PRIMARY KEY (id),
    CONSTRAINT FK_EMAIL_VERIFICATION_OTP_ON_USER FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_email_otp_expires_at ON email_verification_otp (expires_at);
CREATE INDEX idx_email_otp_user_created_at ON email_verification_otp (user_id, created_at);
CREATE INDEX idx_email_otp_user_otp_used ON email_verification_otp (user_id, otp, used);

-- Chat sessions & messages
-- role → MessageRole enum (USER | ASSISTANT)

CREATE TABLE chat_sessions
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title      VARCHAR(255)                NOT NULL,
    user_id    UUID                        NOT NULL,
    CONSTRAINT pk_chat_sessions PRIMARY KEY (id),
    CONSTRAINT FK_CHAT_SESSIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_chat_sessions_user_id ON chat_sessions (user_id);

CREATE TABLE chat_messages
(
    id         UUID                        NOT NULL,
    role       VARCHAR(20)                 NOT NULL,
    content    TEXT                        NOT NULL,
    session_id UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_chat_messages PRIMARY KEY (id),
    CONSTRAINT chk_chat_message_role CHECK (role IN ('USER', 'ASSISTANT')),
    CONSTRAINT FK_CHAT_MESSAGES_ON_SESSION FOREIGN KEY (session_id) REFERENCES chat_sessions (id)
);

CREATE INDEX idx_chat_messages_session_created ON chat_messages (session_id, created_at);

-- Vector store (RAG / PgVector)

CREATE TABLE vector_store
(
    id        UUID NOT NULL,
    content   TEXT NOT NULL,
    metadata  JSONB,
    embedding VECTOR(768),
    CONSTRAINT pk_vector_store PRIMARY KEY (id)
);

CREATE INDEX vector_store_embedding_idx
    ON vector_store
        USING hnsw (embedding vector_cosine_ops);

CREATE INDEX vector_store_metadata_idx
    ON vector_store USING GIN (metadata);

-- Notification

CREATE TABLE notifications
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id    UUID                        NOT NULL,
    type       VARCHAR(50)                 NOT NULL,
    message    TEXT                        NOT NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT chk_notifications_type CHECK (type IN (
                                                      'LOGIN_ALERT',
                                                      'BUDGET_ALERT',
                                                      'RECURRING_TRANSACTION',
                                                      'SAVINGS_GOAL',
                                                      'SYSTEM'
        )),
    CONSTRAINT FK_NOTIFICATIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_notifications_user_id ON notifications (user_id);

-- =============================================================================
-- END OF MIGRATION
-- =============================================================================