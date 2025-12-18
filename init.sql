-- Удаляем таблицы, если они есть (для чистого перезапуска)
DROP TABLE IF EXISTS txn;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS app_user;

-- 1. Таблица пользователей
CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(50) UNIQUE NOT NULL,
                          password_hash VARCHAR(255) NOT NULL,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 2. Счета (Кошелек, Карта и т.д.)
CREATE TABLE account (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
                         name VARCHAR(80) NOT NULL,
                         currency VARCHAR(8) NOT NULL DEFAULT 'EUR',
                         created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                         UNIQUE(user_id, name)
);

-- 3. Категории (Еда, Зарплата...)
CREATE TABLE category (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
                          name VARCHAR(80) NOT NULL,
                          type VARCHAR(10) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
                          UNIQUE(user_id, name, type)
);

-- 4. Транзакции (Сами записи о тратах/доходах)
-- Называем таблицу txn, так как transaction - зарезервированное слово
CREATE TABLE txn (
                     id BIGSERIAL PRIMARY KEY,
                     user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
                     account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE RESTRICT,
                     category_id BIGINT NOT NULL REFERENCES category(id) ON DELETE RESTRICT,
                     amount NUMERIC(12,2) NOT NULL CHECK (amount > 0),
                     occurred_at TIMESTAMPTZ NOT NULL,
                     note TEXT,
                     created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Индексы для ускорения поиска (плюсик к карме на защите)
CREATE INDEX idx_txn_user_date ON txn(user_id, occurred_at);
CREATE INDEX idx_txn_user_category ON txn(user_id, category_id);