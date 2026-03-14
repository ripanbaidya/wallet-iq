CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- USERS

CREATE TABLE users
(
    id                UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    created_at        TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at        TIMESTAMP    NOT NULL DEFAULT now(),
    full_name         VARCHAR(100),
    email             VARCHAR(100) NOT NULL,
    password_hash     VARCHAR(100) NOT NULL,
    role              VARCHAR(20)  NOT NULL,
    active            BOOLEAN      NOT NULL,
    is_email_verified BOOLEAN      NOT NULL
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

CREATE INDEX idx_users_email ON users (email);

-- CATEGORIES

CREATE TABLE categories
(
    id            UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    created_at    TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT now(),
    name          VARCHAR(100) NOT NULL,
    category_type VARCHAR(10),
    user_id       UUID
);

ALTER TABLE categories
    ADD CONSTRAINT uq_category_name_user UNIQUE (name, user_id);

ALTER TABLE categories
    ADD CONSTRAINT fk_categories_user
        FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_categories_user_id ON categories (user_id);

-- PAYMENT MODES

CREATE TABLE payment_modes
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at TIMESTAMP    NOT NULL DEFAULT now(),
    name       VARCHAR(100) NOT NULL,
    user_id    UUID
);

ALTER TABLE payment_modes
    ADD CONSTRAINT uq_payment_mode_name_user UNIQUE (name, user_id);

ALTER TABLE payment_modes
    ADD CONSTRAINT fk_payment_modes_user
        FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_payment_modes_user_id ON payment_modes (user_id);

-- TRANSACTIONS

CREATE TABLE transactions
(
    id              UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    created_at      TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP      NOT NULL DEFAULT now(),
    amount          DECIMAL(12, 2) NOT NULL,
    type            VARCHAR(10)    NOT NULL,
    date            DATE           NOT NULL,
    note            TEXT,
    embedding_id    VARCHAR(255),
    user_id         UUID           NOT NULL,
    category_id     UUID,
    payment_mode_id UUID
);

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_user
        FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_category
        FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_payment_mode
        FOREIGN KEY (payment_mode_id) REFERENCES payment_modes (id);

CREATE INDEX idx_transactions_user_date
    ON transactions (user_id, date);

CREATE INDEX idx_transactions_category_id
    ON transactions (category_id);

CREATE INDEX idx_transactions_payment_mode_id
    ON transactions (payment_mode_id);

-- REFRESH TOKENS

CREATE TABLE refresh_token
(
    id         UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    token      TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    expires_at TIMESTAMP NOT NULL,
    revoked    BOOLEAN   NOT NULL,
    revoked_at TIMESTAMP,
    user_id    UUID      NOT NULL
);

ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refresh_token_token UNIQUE (token);

ALTER TABLE refresh_token
    ADD CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_refresh_token_user
    ON refresh_token (user_id);

CREATE INDEX idx_refresh_token_expires
    ON refresh_token (expires_at);

-- EMAIL OTP

CREATE TABLE email_verification_otp
(
    id         UUID PRIMARY KEY    DEFAULT gen_random_uuid(),
    otp        VARCHAR(6) NOT NULL,
    used       BOOLEAN    NOT NULL,
    created_at TIMESTAMP  NOT NULL DEFAULT now(),
    expires_at TIMESTAMP  NOT NULL,
    user_id    UUID       NOT NULL
);

ALTER TABLE email_verification_otp
    ADD CONSTRAINT fk_email_otp_user
        FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_email_otp_expires_at
    ON email_verification_otp (expires_at);

CREATE INDEX idx_email_otp_user_created_at
    ON email_verification_otp (user_id, created_at);

CREATE INDEX idx_email_otp_user_otp_used
    ON email_verification_otp (user_id, otp, used);

-- CHAT SESSIONS

CREATE TABLE chat_sessions
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at TIMESTAMP    NOT NULL DEFAULT now(),
    title      VARCHAR(255) NOT NULL,
    user_id    UUID         NOT NULL
);

ALTER TABLE chat_sessions
    ADD CONSTRAINT fk_chat_sessions_user
        FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_chat_sessions_user_id
    ON chat_sessions (user_id);

-- CHAT MESSAGES

CREATE TABLE chat_messages
(
    id         UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    role       VARCHAR(20) NOT NULL,
    content    TEXT        NOT NULL,
    session_id UUID        NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT now()
);

ALTER TABLE chat_messages
    ADD CONSTRAINT fk_chat_messages_session
        FOREIGN KEY (session_id) REFERENCES chat_sessions (id);

CREATE INDEX idx_chat_messages_session_created
    ON chat_messages (session_id, created_at);