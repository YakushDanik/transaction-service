CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    account_from VARCHAR(10) NOT NULL,
    account_to VARCHAR(10) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    expense_category VARCHAR(20) NOT NULL,
    datetime TIMESTAMP NOT NULL,
    limit_exceeded BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE limits (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(20) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    set_date TIMESTAMP NOT NULL
);

CREATE TABLE exchange_rates (
    id BIGSERIAL PRIMARY KEY,
    currency_from VARCHAR(3) NOT NULL,
    currency_to VARCHAR(3) NOT NULL,
    rate NUMERIC(15, 6) NOT NULL,
    date DATE NOT NULL
);
