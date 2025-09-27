-- Base schema and seed data for auth-service
CREATE TABLE account (
    id UUID PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    email VARCHAR(128) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX uk_account_username ON account (username);
CREATE UNIQUE INDEX uk_account_email ON account (email);

CREATE TABLE refresh_token (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_refresh_account ON refresh_token (account_id);

INSERT INTO account (id, username, email, password_hash, status, created_at) VALUES
    ('0f9db8e3-5a4d-4b2f-9c1d-1234567890ab', 'admin', 'admin@tokopaedi.test', '$2b$10$Yi10GS2exa3rI9c6S8J59.nkO/tKrdREL38kS9b9lbw2L3hAEi7YS', 'ACTIVE', NOW()),
    ('1a2b3c4d-5e6f-4a70-8b9c-d0e1f2a3b4c5', 'seller', 'seller@tokopaedi.test', '$2b$10$BKkM2/b7FAy6v/XKhKOKH.ZD4gfrPAvG9t4sA7MbMtOxPZzzOSHKO', 'ACTIVE', NOW()),
    ('2b3c4d5e-6f70-4b81-92a3-b4c5d6e7f809', 'customer', 'customer@tokopaedi.test', '$2b$10$4mV.JuID1WM4Nevo8b9w1uDbtpjTLF/X8mGgYfR08yim2s/VAeX0W', 'ACTIVE', NOW())
ON CONFLICT (id) DO NOTHING;
