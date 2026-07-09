CREATE TABLE IF NOT EXISTS cur_ex.users (
    id bigserial NOT NULL,
    username text NOT NULL,
    password_hash text NOT NULL,
    enable bool NOT NULL DEFAULT true,
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

INSERT INTO cur_ex.users (username, password_hash, enable) VALUES
('admin', '$2a$10$aqtGC4Ul9BCcen5nm5dtDOUGtcfUA6TY68RfxfkNegphVJlHsp/ma', true),
('user', '$2a$10$XRsqxghutJ6Ito23c0Jgf.Dl2.Q7LGTB78eC52Uu9lEO4d8PeJBZ.', true)
ON CONFLICT (username) DO NOTHING;

INSERT INTO cur_ex.user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN' FROM cur_ex.users WHERE username = 'admin'
UNION ALL
SELECT id, 'ROLE_USER' FROM cur_ex.users WHERE username = 'user'