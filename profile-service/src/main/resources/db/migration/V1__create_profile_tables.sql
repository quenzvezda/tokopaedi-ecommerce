-- Base schema and seed data for profile-service
CREATE TABLE user_profiles (
    user_id UUID PRIMARY KEY,
    full_name VARCHAR(200) NOT NULL,
    bio TEXT,
    phone VARCHAR(32),
    avatar_object_key VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE store_profiles (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES user_profiles(user_id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(200) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX store_profiles_owner_slug_idx ON store_profiles(owner_id, LOWER(slug));
CREATE INDEX store_profiles_owner_idx ON store_profiles(owner_id);

INSERT INTO user_profiles (user_id, full_name, bio, phone, avatar_object_key, created_at, updated_at) VALUES
    ('0f9db8e3-5a4d-4b2f-9c1d-1234567890ab', 'Admin Tokopaedi', 'System administrator', '+62111111111', NULL, NOW(), NOW()),
    ('1a2b3c4d-5e6f-4a70-8b9c-d0e1f2a3b4c5', 'Seller Tokopaedi', 'Trusted marketplace seller', '+62222222222', NULL, NOW(), NOW()),
    ('2b3c4d5e-6f70-4b81-92a3-b4c5d6e7f809', 'Customer Tokopaedi', 'Loyal customer', '+62333333333', NULL, NOW(), NOW())
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO store_profiles (id, owner_id, name, slug, description, active, created_at, updated_at) VALUES
    ('6e8f3c1d-2b4a-4c8d-9e7f-1234abcd5678', '1a2b3c4d-5e6f-4a70-8b9c-d0e1f2a3b4c5', 'Seller Central Store', 'seller-central-store', 'Primary storefront for the default seller account.', TRUE, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
