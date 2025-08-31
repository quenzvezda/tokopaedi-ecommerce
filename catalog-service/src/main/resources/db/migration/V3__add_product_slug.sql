-- Add slug column to products
ALTER TABLE products ADD COLUMN slug VARCHAR(220);

-- generate slug for existing products and ensure uniqueness
WITH normalized AS (
    SELECT id,
           trim(both '-' FROM regexp_replace(lower(name), '[^a-z0-9]+', '-', 'g')) AS base_slug
    FROM products
), deduped AS (
    SELECT id,
           base_slug,
           row_number() OVER (PARTITION BY base_slug ORDER BY id) AS seq
    FROM normalized
)
UPDATE products p
SET slug = CASE WHEN d.seq = 1 THEN d.base_slug ELSE concat(d.base_slug, '-', d.seq) END
FROM deduped d
WHERE p.id = d.id;

ALTER TABLE products ALTER COLUMN slug SET NOT NULL;
CREATE UNIQUE INDEX uq_products_slug ON products(slug);
