-- Postgres schema init for catalog-service (Fase 1)
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp"; -- tidak wajib, kita pakai UUID dari aplikasi

-- == BRANDS ==
CREATE TABLE IF NOT EXISTS brands (
                                      id          UUID PRIMARY KEY,
                                      name        VARCHAR(160) NOT NULL UNIQUE,
                                      active      BOOLEAN NOT NULL DEFAULT TRUE
);

-- == CATEGORIES ==
CREATE TABLE IF NOT EXISTS categories (
                                          id          UUID PRIMARY KEY,
                                          parent_id   UUID NULL,
                                          name        VARCHAR(160) NOT NULL,
                                          active      BOOLEAN NOT NULL DEFAULT TRUE,
                                          sort_order  INTEGER NULL,
                                          CONSTRAINT fk_categories_parent
                                              FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_categories_parent  ON categories(parent_id);
CREATE INDEX IF NOT EXISTS idx_categories_active  ON categories(active);

-- == PRODUCTS ==
CREATE TABLE IF NOT EXISTS products (
                                        id           UUID PRIMARY KEY,
                                        name         VARCHAR(200) NOT NULL,
                                        short_desc   VARCHAR(1000),
                                        brand_id     UUID NOT NULL,
                                        category_id  UUID NOT NULL,
                                        published    BOOLEAN NOT NULL DEFAULT FALSE,
                                        created_at   TIMESTAMPTZ NOT NULL,
                                        updated_at   TIMESTAMPTZ NOT NULL,
                                        CONSTRAINT fk_products_brand
                                            FOREIGN KEY (brand_id) REFERENCES brands(id),
                                        CONSTRAINT fk_products_category
                                            FOREIGN KEY (category_id) REFERENCES categories(id)
);
CREATE INDEX IF NOT EXISTS idx_products_brand       ON products(brand_id);
CREATE INDEX IF NOT EXISTS idx_products_category    ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_products_published   ON products(published);
CREATE INDEX IF NOT EXISTS idx_products_created_at  ON products(created_at);

-- == SKUS ==
CREATE TABLE IF NOT EXISTS skus (
                                    id         UUID PRIMARY KEY,
                                    product_id UUID NOT NULL,
                                    sku_code   VARCHAR(120) NOT NULL UNIQUE,
                                    active     BOOLEAN NOT NULL DEFAULT TRUE,
                                    barcode    VARCHAR(120),
                                    CONSTRAINT fk_skus_product
                                        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_skus_product ON skus(product_id);
CREATE INDEX IF NOT EXISTS idx_skus_active  ON skus(active);
