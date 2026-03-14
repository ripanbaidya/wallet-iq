CREATE TABLE recurring_transactions
(
    id                  UUID PRIMARY KEY,

    created_at          TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP      NOT NULL DEFAULT now(),

    title               VARCHAR(100)   NOT NULL,
    amount              DECIMAL(12, 2) NOT NULL,

    type                VARCHAR(10)    NOT NULL,
    frequency           VARCHAR(10)    NOT NULL,

    start_date          DATE           NOT NULL,
    end_date            DATE,

    next_execution_date DATE           NOT NULL,

    is_active           BOOLEAN        NOT NULL DEFAULT TRUE,

    note                TEXT,

    user_id             UUID           NOT NULL,
    category_id         UUID,
    payment_mode_id     UUID,

    CONSTRAINT fk_recurring_user
        FOREIGN KEY (user_id) REFERENCES users (id),

    CONSTRAINT fk_recurring_category
        FOREIGN KEY (category_id) REFERENCES categories (id),

    CONSTRAINT fk_recurring_payment_mode
        FOREIGN KEY (payment_mode_id) REFERENCES payment_modes (id),

    CONSTRAINT chk_recurring_amount_positive
        CHECK (amount > 0),

    CONSTRAINT chk_recurring_type
        CHECK (type IN ('INCOME', 'EXPENSE')),

    CONSTRAINT chk_recurring_frequency
        CHECK (frequency IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY')),

    CONSTRAINT chk_recurring_date_range
        CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE INDEX idx_recurring_scheduler
    ON recurring_transactions (is_active, next_execution_date);

CREATE INDEX idx_recurring_user_active
    ON recurring_transactions (user_id, is_active);