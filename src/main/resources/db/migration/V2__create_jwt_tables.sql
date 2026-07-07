CREATE TABLE IF NOT EXISTS cur_ex.users (
    id bigserial NOT NULL,
    username text NOT NULL,
    password_hash text NOT NULL,
    enabled bool NOT NULL DEFAULT true,
    CONSTRAINT users_pk PRIMARY KEY (id),
    CONSTRAINT users_username_unique UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS cur_ex.user_roles (
    user_id int8 NOT NULL,
    role text NOT NULL,
    CONSTRAINT user_roles_pk PRIMARY KEY (user_id, role),
    CONSTRAINT user_roles_fk FOREIGN KEY (user_id) REFERENCES cur_ex.users(id)
);

CREATE TABLE IF NOT EXISTS cur_ex.refresh_tokens (
    id bigserial NOT NULL,
    user_id int8 NOT NULL,
    token text NOT NULL,
    expiry_date timestamp NOT NULL,
    revoked bool NOT NULL DEFAULT false,
    CONSTRAINT refresh_tokens_pk PRIMARY KEY (id),
    CONSTRAINT refresh_tokens_token_unique UNIQUE (token),
    CONSTRAINT refresh_tokens_fk FOREIGN KEY (user_id) REFERENCES cur_ex.users(id)
);

-- пароль: admin123 (BCrypt)
INSERT INTO cur_ex.users (username, password_hash, enabled) VALUES
('admin', '$2a$10$OMbC8uQBnpEc9TRODBBopu2ZweNAVFAdUNjT27BnIppl5cWRJboZe', true),
('user', '$2a$10$i55MDaBINY7Jn9D42K/zy.a.JODuDTsNhOsbIGGZ0A4SV858pFShS', true)
ON CONFLICT (username) DO NOTHING;

INSERT INTO cur_ex.user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN' FROM cur_ex.users WHERE username = 'admin'
UNION ALL
SELECT id, 'ROLE_USER' FROM cur_ex.users WHERE username = 'user';