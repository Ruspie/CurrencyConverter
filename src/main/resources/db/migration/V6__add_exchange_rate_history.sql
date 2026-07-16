TRUNCATE TABLE cur_ex.exchange_rate;

ALTER TABLE cur_ex.exchange_rate
    ADD COLUMN rate_date DATE NOT NULL DEFAULT CURRENT_DATE;

ALTER TABLE cur_ex.exchange_rate
    ALTER COLUMN rate_date DROP DEFAULT;

ALTER TABLE cur_ex.exchange_rate
    DROP CONSTRAINT exchange_rate_currency_pair_unique;

ALTER TABLE cur_ex.exchange_rate
    ADD CONSTRAINT exchange_rate_currency_pair_date_unique
        UNIQUE (from_currency, to_currency, rate_date);

CREATE INDEX exchange_rate_latest_lookup_idx
    ON cur_ex.exchange_rate (from_currency, to_currency, rate_date DESC);
