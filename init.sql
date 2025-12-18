DROP TABLE IF EXISTS budget;
DROP TABLE IF EXISTS txn;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS app_user;

CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(50) UNIQUE NOT NULL,
                          password_hash VARCHAR(255) NOT NULL,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE account (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
                         name VARCHAR(80) NOT NULL,
                         currency VARCHAR(8) NOT NULL DEFAULT 'EUR',
                         created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                         UNIQUE(user_id, name)
);

CREATE TABLE category (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
                          name VARCHAR(80) NOT NULL,
                          type VARCHAR(10) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
                          UNIQUE(user_id, name, type)
);

CREATE TABLE txn (
                     id BIGSERIAL PRIMARY KEY,
                     user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
                     account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
                     category_id BIGINT NOT NULL REFERENCES category(id) ON DELETE CASCADE,
                     amount NUMERIC(12,2) NOT NULL CHECK (amount > 0),
                     occurred_at TIMESTAMPTZ NOT NULL,
                     note TEXT,
                     created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Индексы
CREATE INDEX idx_txn_user_date ON txn(user_id, occurred_at);

-- Бюджеты по счетам (период произвольный)
CREATE TABLE budget (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
                        account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
                        limit_amount NUMERIC(12,2) NOT NULL CHECK (limit_amount > 0),
                        start_date DATE NOT NULL,
                        end_date DATE NOT NULL,
                        created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                        CHECK (start_date <= end_date)
);

CREATE INDEX idx_budget_user_account ON budget(user_id, account_id);
