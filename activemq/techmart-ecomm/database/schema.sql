-- ============================================================
-- TechMart Online E-Commerce Platform
-- Database Schema with Optimization Configurations
-- Jakarta EE 10 | Payara 6 | MySQL 8
-- Assignment: JIAT/BCD I/EX/01
-- ============================================================

-- Create and select database
CREATE DATABASE IF NOT EXISTS techmart_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE techmart_db;

-- ============================================================
-- TABLE: users
-- Stores customer and admin account information
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
                                     id          BIGINT          AUTO_INCREMENT PRIMARY KEY,
                                     name        VARCHAR(255)    NOT NULL,
    email       VARCHAR(255)    NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL COMMENT 'SHA-256 hashed',
    role        VARCHAR(50)     NOT NULL DEFAULT 'CUSTOMER',
    created_at  DATETIME        NOT NULL,

    -- Performance index on email for login lookups
    INDEX idx_users_email (email),
    INDEX idx_users_role  (role)

    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci
    COMMENT='User authentication and role management table';

-- ============================================================
-- TABLE: products
-- Stores product catalogue with inventory information
-- ============================================================
CREATE TABLE IF NOT EXISTS products (
                                        id             BIGINT          AUTO_INCREMENT PRIMARY KEY,
                                        name           VARCHAR(255)    NOT NULL,
    description    TEXT            NOT NULL,
    price          DOUBLE          NOT NULL,
    stock_quantity INT             NOT NULL DEFAULT 0,
    category       VARCHAR(100)    NOT NULL,
    image_url      VARCHAR(500)    DEFAULT NULL,

    -- Performance indexes for common query patterns
    INDEX idx_products_category     (category),
    INDEX idx_products_name         (name),
    INDEX idx_products_price        (price),
    INDEX idx_products_stock        (stock_quantity),

    -- Constraint: price cannot be negative
    CONSTRAINT chk_price_positive
    CHECK (price >= 0),
    -- Constraint: stock cannot be negative
    CONSTRAINT chk_stock_non_negative
    CHECK (stock_quantity >= 0)

    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci
    COMMENT='Product catalogue with inventory tracking';

-- ============================================================
-- TABLE: orders
-- Stores customer order records
-- ============================================================
CREATE TABLE IF NOT EXISTS orders (
                                      id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
                                      customer_id         VARCHAR(255)    NOT NULL,
    customer_email      VARCHAR(255)    NOT NULL,
    status              VARCHAR(50)     NOT NULL DEFAULT 'PENDING',
    total_amount        DOUBLE          NOT NULL DEFAULT 0.0,
    created_at          DATETIME        NOT NULL,
    updated_at          DATETIME        DEFAULT NULL,
    processing_time_ms  BIGINT          DEFAULT 0
    COMMENT 'Order processing duration in ms',

    -- Performance indexes for common query patterns
    INDEX idx_orders_customer_id    (customer_id),
    INDEX idx_orders_status         (status),
    INDEX idx_orders_created_at     (created_at),
    INDEX idx_orders_customer_date  (customer_id, created_at),

    -- Constraint: valid order status values
    CONSTRAINT chk_order_status
    CHECK (status IN (
           'PENDING','CONFIRMED','PROCESSING',
           'SHIPPED','DELIVERED','CANCELLED'
                     ))

    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci
    COMMENT='Customer order records with processing metrics';

-- ============================================================
-- TABLE: order_items
-- Stores individual line items within each order
-- ============================================================
CREATE TABLE IF NOT EXISTS order_items (
                                           id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
                                           order_id    BIGINT      NOT NULL,
                                           product_id  BIGINT      NOT NULL,
                                           quantity    INT         NOT NULL,
                                           unit_price  DOUBLE      NOT NULL,

    -- Foreign key constraints with cascade rules
                                           CONSTRAINT fk_order_items_order
                                           FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

    CONSTRAINT fk_order_items_product
    FOREIGN KEY (product_id)
    REFERENCES products(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,

    -- Performance indexes
    INDEX idx_order_items_order_id   (order_id),
    INDEX idx_order_items_product_id (product_id),

    -- Constraint: quantity must be positive
    CONSTRAINT chk_quantity_positive
    CHECK (quantity > 0),
    -- Constraint: unit price cannot be negative
    CONSTRAINT chk_unit_price_positive
    CHECK (unit_price >= 0)

    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci
    COMMENT='Order line items with product references';

-- ============================================================
-- DATABASE USER SETUP
-- Create dedicated application user with minimal privileges
-- ============================================================
CREATE USER IF NOT EXISTS
    'techmart_user'@'localhost'
    IDENTIFIED BY 'techmart123';

GRANT SELECT, INSERT, UPDATE, DELETE
      ON techmart_db.*
          TO 'techmart_user'@'localhost';

FLUSH PRIVILEGES;

-- ============================================================
-- PERFORMANCE VERIFICATION
-- Run these after setup to verify indexes
-- ============================================================
-- SHOW INDEX FROM products;
-- SHOW INDEX FROM orders;
-- SHOW INDEX FROM order_items;
-- EXPLAIN SELECT * FROM products WHERE category = 'Electronics';
-- EXPLAIN SELECT * FROM orders WHERE customer_id = 'USER-1';