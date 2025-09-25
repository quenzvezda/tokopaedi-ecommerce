-- Base schema and seed data for iam-service
CREATE TABLE role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE permission (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE role_permission (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permission(id) ON DELETE CASCADE,
    UNIQUE (role_id, permission_id)
);
CREATE INDEX idx_role_permission_role ON role_permission(role_id);
CREATE INDEX idx_role_permission_permission ON role_permission(permission_id);

CREATE TABLE user_role (
    id BIGSERIAL PRIMARY KEY,
    account_id UUID NOT NULL,
    role_id BIGINT NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    UNIQUE (account_id, role_id)
);
CREATE INDEX idx_user_role_account ON user_role(account_id);
CREATE INDEX idx_user_role_role ON user_role(role_id);

CREATE TABLE user_entitlement_version (
    id BIGSERIAL PRIMARY KEY,
    account_id UUID NOT NULL UNIQUE,
    version INTEGER NOT NULL
);
CREATE INDEX idx_user_entitlement_account ON user_entitlement_version(account_id);

INSERT INTO role (id, name) VALUES
    (1, 'ADMIN'),
    (2, 'SELLER'),
    (3, 'CUSTOMER')
ON CONFLICT (id) DO NOTHING;

INSERT INTO permission (id, name, description) VALUES
    (1, 'catalog:product:write', 'Manage catalog products'),
    (2, 'catalog:product:read', 'Read catalog products'),
    (3, 'profile:profile:read', 'Read customer profiles')
ON CONFLICT (id) DO NOTHING;

INSERT INTO role_permission (role_id, permission_id) VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (2, 2),
    (2, 3)
ON CONFLICT DO NOTHING;

INSERT INTO user_role (account_id, role_id) VALUES
    ('0f9db8e3-5a4d-4b2f-9c1d-1234567890ab', 1),
    ('1a2b3c4d-5e6f-4a70-8b9c-d0e1f2a3b4c5', 2),
    ('2b3c4d5e-6f70-4b81-92a3-b4c5d6e7f809', 3)
ON CONFLICT DO NOTHING;

INSERT INTO user_entitlement_version (account_id, version) VALUES
    ('0f9db8e3-5a4d-4b2f-9c1d-1234567890ab', 1),
    ('1a2b3c4d-5e6f-4a70-8b9c-d0e1f2a3b4c5', 1),
    ('2b3c4d5e-6f70-4b81-92a3-b4c5d6e7f809', 1)
ON CONFLICT (account_id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('role','id'), COALESCE((SELECT MAX(id) FROM role), 0), true);
SELECT setval(pg_get_serial_sequence('permission','id'), COALESCE((SELECT MAX(id) FROM permission), 0), true);
SELECT setval(pg_get_serial_sequence('role_permission','id'), COALESCE((SELECT MAX(id) FROM role_permission), 0), true);
SELECT setval(pg_get_serial_sequence('user_role','id'), COALESCE((SELECT MAX(id) FROM user_role), 0), true);
SELECT setval(pg_get_serial_sequence('user_entitlement_version','id'), COALESCE((SELECT MAX(id) FROM user_entitlement_version), 0), true);
