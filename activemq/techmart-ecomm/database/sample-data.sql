-- ============================================================
-- TechMart Online — Sample Data
-- Run this AFTER schema.sql
-- ============================================================

USE techmart_db;

-- ============================================================
-- SAMPLE PRODUCTS
-- ============================================================
INSERT INTO products
(name, description, price, stock_quantity, category)
VALUES
    ('Laptop Pro 15',
     'High-performance laptop with 16GB RAM and 512GB SSD',
     1299.99, 50, 'Electronics'),

    ('Wireless Mouse',
     'Ergonomic wireless mouse with 12-month battery life',
     29.99, 200, 'Electronics'),

    ('Mechanical Keyboard',
     'Tactile mechanical keyboard with RGB backlight',
     89.99, 150, 'Electronics'),

    ('USB-C Hub 7-in-1',
     '7-port USB-C hub with 4K HDMI and 100W PD',
     49.99, 100, 'Electronics'),

    ('Java EE 8 Guide',
     'Comprehensive guide to enterprise Java development',
     39.99, 75, 'Books'),

    ('Clean Code',
     'A handbook of agile software craftsmanship',
     34.99, 60, 'Books'),

    ('Desk Lamp LED',
     'Adjustable LED desk lamp with USB charging port',
     24.99, 120, 'Home'),

    ('Monitor Stand',
     'Adjustable aluminum monitor stand with storage drawer',
     59.99, 80, 'Home');

-- ============================================================
-- ADMIN USER
-- Password: admin123 (SHA-256 hashed)
-- ============================================================
INSERT INTO users
(name, email, password, role, created_at)
VALUES (
           'Admin',
           'admin@techmart.com',
           SHA2('admin123', 256),
           'ADMIN',
           NOW()
       );

-- ============================================================
-- VERIFY DATA
-- ============================================================
SELECT
    'products'  AS table_name,
    COUNT(*)    AS record_count
FROM products
UNION ALL
SELECT
    'users',
    COUNT(*)
FROM users;