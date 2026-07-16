DELETE FROM cur_ex.exchange_rate
WHERE from_currency IS NULL
   OR to_currency IS NULL
   OR rate IS NULL
   OR "scale" IS NULL;

DELETE FROM cur_ex.exchange_rate first_rate
USING cur_ex.exchange_rate duplicate_rate
WHERE first_rate.id < duplicate_rate.id
  AND first_rate.from_currency = duplicate_rate.from_currency
  AND first_rate.to_currency = duplicate_rate.to_currency;

ALTER TABLE cur_ex.exchange_rate
    ALTER COLUMN from_currency TYPE VARCHAR(3),
    ALTER COLUMN from_currency SET NOT NULL,
    ALTER COLUMN to_currency TYPE VARCHAR(3),
    ALTER COLUMN to_currency SET NOT NULL,
    ALTER COLUMN rate TYPE NUMERIC(24, 12),
    ALTER COLUMN rate SET NOT NULL,
    ALTER COLUMN "scale" TYPE NUMERIC(24, 12),
    ALTER COLUMN "scale" SET NOT NULL;

ALTER TABLE cur_ex.exchange_rate
    ADD CONSTRAINT exchange_rate_currency_pair_unique
        UNIQUE (from_currency, to_currency);

ALTER TABLE cur_ex.refresh_tokens
    ALTER COLUMN token TYPE TEXT;

SELECT setval(
    pg_get_serial_sequence('cur_ex.exchange_rate', 'id'),
    COALESCE((SELECT MAX(id) FROM cur_ex.exchange_rate), 1),
    true
);
