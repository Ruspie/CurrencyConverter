ALTER TABLE cur_ex.users
    ADD enabled BOOLEAN DEFAULT true;

ALTER TABLE cur_ex.users
    ALTER COLUMN enabled SET NOT NULL;

ALTER TABLE cur_ex.user_roles
    DROP CONSTRAINT user_roles_pk;

ALTER TABLE cur_ex.users
    DROP COLUMN enable;

ALTER TABLE cur_ex.exchange_rate
    ALTER COLUMN from_currency TYPE VARCHAR(255) USING (from_currency::VARCHAR(255));

ALTER TABLE cur_ex.users
    ALTER COLUMN password_hash TYPE VARCHAR(255) USING (password_hash::VARCHAR(255));

ALTER TABLE cur_ex.user_roles
    ALTER COLUMN role TYPE VARCHAR(255) USING (role::VARCHAR(255));

ALTER TABLE cur_ex.user_roles
    ALTER COLUMN role DROP NOT NULL;

ALTER TABLE cur_ex.exchange_rate
    ALTER COLUMN to_currency TYPE VARCHAR(255) USING (to_currency::VARCHAR(255));

ALTER TABLE cur_ex.refresh_tokens
    ALTER COLUMN token TYPE VARCHAR(255) USING (token::VARCHAR(255));

ALTER TABLE cur_ex.users
    ALTER COLUMN username TYPE VARCHAR(255) USING (username::VARCHAR(255));