DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'auth_user') THEN CREATE USER auth_user WITH PASSWORD 'auth_pass'; END IF;
   IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'iam_user') THEN CREATE USER iam_user WITH PASSWORD 'iam_pass'; END IF;
   IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'profile_user') THEN CREATE USER profile_user WITH PASSWORD 'profile_pass'; END IF;
   IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'legacy_user') THEN CREATE USER legacy_user WITH PASSWORD 'legacy_pass'; END IF;
END$$;

ALTER DATABASE auth_db OWNER TO auth_user;
ALTER DATABASE iam_db OWNER TO iam_user;
ALTER DATABASE profile_db OWNER TO profile_user;
ALTER DATABASE legacy_db OWNER TO legacy_user;

/* optional: pastikan user bisa create di schema public pada masing-masing DB */
GRANT ALL ON DATABASE auth_db TO auth_user;
GRANT ALL ON DATABASE iam_db TO iam_user;
GRANT ALL ON DATABASE profile_db TO profile_user;
GRANT ALL ON DATABASE legacy_db TO legacy_user;
