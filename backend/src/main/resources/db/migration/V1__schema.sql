CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE categories
(
    id            UUID                        NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name          VARCHAR(100)                NOT NULL,
    category_type VARCHAR(10),
    user_id       UUID,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE chat_messages
(
    id         UUID                        NOT NULL,
    role       VARCHAR(20)                 NOT NULL,
    content    TEXT                        NOT NULL,
    session_id UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_chat_messages PRIMARY KEY (id)
);

CREATE TABLE chat_sessions
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title      VARCHAR(255)                NOT NULL,
    user_id    UUID                        NOT NULL,
    CONSTRAINT pk_chat_sessions PRIMARY KEY (id)
);

CREATE TABLE email_verification_otp
(
    id         UUID                        NOT NULL,
    otp        VARCHAR(6)                  NOT NULL,
    used       BOOLEAN                     NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id    UUID                        NOT NULL,
    CONSTRAINT pk_email_verification_otp PRIMARY KEY (id)
);

CREATE TABLE payment_modes
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name       VARCHAR(100)                NOT NULL,
    user_id    UUID,
    CONSTRAINT pk_payment_modes PRIMARY KEY (id)
);

CREATE TABLE refresh_token
(
    id         UUID                        NOT NULL,
    token      TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    revoked    BOOLEAN                     NOT NULL,
    revoked_at TIMESTAMP WITHOUT TIME ZONE,
    user_id    UUID                        NOT NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id)
);

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
    CONSTRAINT pk_transactions PRIMARY KEY (id)
);

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
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refresh_token_token UNIQUE (token);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE categories
    ADD CONSTRAINT uq_category_name_user UNIQUE (name, user_id);

ALTER TABLE payment_modes
    ADD CONSTRAINT uq_payment_mode_name_user UNIQUE (name, user_id);

CREATE INDEX idx_chat_messages_session_created ON chat_messages (session_id, created_at);

CREATE INDEX idx_email_otp_expires_at ON email_verification_otp (expires_at);

CREATE INDEX idx_email_otp_user_created_at ON email_verification_otp (user_id, created_at);

CREATE INDEX idx_email_otp_user_otp_used ON email_verification_otp (user_id, otp, used);

CREATE INDEX idx_transactions_user_date ON transactions (user_id, date);

CREATE INDEX idx_users_email ON users (email);

ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_categories_user_id ON categories (user_id);

ALTER TABLE chat_messages
    ADD CONSTRAINT FK_CHAT_MESSAGES_ON_SESSION FOREIGN KEY (session_id) REFERENCES chat_sessions (id);

ALTER TABLE chat_sessions
    ADD CONSTRAINT FK_CHAT_SESSIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_chat_sessions_user_id ON chat_sessions (user_id);

ALTER TABLE email_verification_otp
    ADD CONSTRAINT FK_EMAIL_VERIFICATION_OTP_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE payment_modes
    ADD CONSTRAINT FK_PAYMENT_MODES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_payment_modes_user_id ON payment_modes (user_id);

ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESH_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_refresh_token_user ON refresh_token (user_id);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

CREATE INDEX idx_transactions_category_id ON transactions (category_id);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_PAYMENT_MODE FOREIGN KEY (payment_mode_id) REFERENCES payment_modes (id);

CREATE INDEX idx_transactions_payment_mode_id ON transactions (payment_mode_id);

ALTER TABLE transactions
    ADD CONSTRAINT FK_TRANSACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_transactions_user_id ON transactions (user_id);