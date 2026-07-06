CREATE SCHEMA IF NOT EXISTS cur_ex;

CREATE TABLE IF NOT EXISTS cur_ex.exchange_rate (
    from_currency text NULL,
    id bigserial NOT NULL,
    to_currency text NULL,
    rate numeric NULL,
    scale numeric NULL,
    CONSTRAINT exchange_rate_pk PRIMARY KEY (id)
);