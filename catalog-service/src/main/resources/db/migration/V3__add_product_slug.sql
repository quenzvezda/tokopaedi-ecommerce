-- Add slug column to products
ALTER TABLE products ADD COLUMN slug VARCHAR(220);

-- normalize name then slugify
UPDATE products
SET slug = trim(both '-' FROM regexp_replace(lower(name), '[^a-z0-9]+', '-', 'g'));

ALTER TABLE products ALTER COLUMN slug SET NOT NULL;
CREATE UNIQUE INDEX uq_products_slug ON products(slug);
