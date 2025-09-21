-- Add created_by column to track creator user ID for products
ALTER TABLE products ADD COLUMN IF NOT EXISTS created_by UUID;

-- Backfill existing records with a system placeholder user
UPDATE products SET created_by = '00000000-0000-0000-0000-000000000000' WHERE created_by IS NULL;

ALTER TABLE products ALTER COLUMN created_by SET NOT NULL;
