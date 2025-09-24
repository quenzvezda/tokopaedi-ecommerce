CREATE TABLE IF NOT EXISTS user_profiles (
    user_id UUID PRIMARY KEY,
    full_name VARCHAR(200) NOT NULL,
    bio TEXT,
    phone VARCHAR(32),
    avatar_object_key VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS store_profiles (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(200) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS store_profiles_owner_slug_idx
    ON store_profiles(owner_id, LOWER(slug));

CREATE INDEX IF NOT EXISTS store_profiles_owner_idx
    ON store_profiles(owner_id);
