ALTER TABLE cur_ex.refresh_tokens
    ALTER COLUMN token TYPE text USING (token::text);
