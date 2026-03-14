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
    CONSTRAINT pk_budgets PRIMARY KEY (id)
);

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
    CONSTRAINT pk_savings_goals PRIMARY KEY (id)
);

ALTER TABLE budgets
    ADD CONSTRAINT uq_budget_user_category_month UNIQUE (user_id, category_id, month);

CREATE INDEX idx_budget_user_month ON budgets (user_id, month);

CREATE INDEX idx_transactions_user_id ON transactions (user_id);

ALTER TABLE budgets
    ADD CONSTRAINT FK_BUDGETS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE budgets
    ADD CONSTRAINT FK_BUDGETS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE savings_goals
    ADD CONSTRAINT FK_SAVINGS_GOALS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_goal_user_id ON savings_goals (user_id);

DROP TABLE vector_store CASCADE;

ALTER TABLE users
    ALTER COLUMN email DROP NOT NULL;

ALTER TABLE recurring_transactions
    ALTER COLUMN note TYPE VARCHAR(255) USING (note::VARCHAR(255));

ALTER TABLE users
    ALTER COLUMN password_hash DROP NOT NULL;

ALTER TABLE refresh_token
    ALTER COLUMN token DROP NOT NULL;