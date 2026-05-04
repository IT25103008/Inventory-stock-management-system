-- ============================================================
-- 📦 PAPOL LITE — Inventory & Stock Management System
-- Database Schema (MySQL 8.x)
-- ============================================================
-- Project  : SE1020 — OOP Inventory System
-- Database : papol_lite
-- Engine   : InnoDB (default)
-- Charset  : utf8mb4
-- ============================================================

-- 1. Create the database
CREATE DATABASE IF NOT EXISTS papol_lite
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE papol_lite;

-- ============================================================
-- 2. TABLE: users
-- ============================================================
-- Stores all system users (Admin & Staff).
-- The 'role' column acts as JPA's @DiscriminatorColumn for
-- the Employee → Admin / StaffMember inheritance hierarchy.
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id   VARCHAR(10)  PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    email     VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    role      VARCHAR(20)  NOT NULL          COMMENT 'Discriminator: Admin | Staff',
    phone     VARCHAR(20),
    status    VARCHAR(20)  DEFAULT 'Active'  COMMENT 'Active | Inactive'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 3. TABLE: categories
-- ============================================================
-- Product categories (e.g. Electronics, Office Supplies).
-- Referenced by products.category_id.
-- ============================================================
CREATE TABLE IF NOT EXISTS categories (
    category_id   VARCHAR(10)  PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    description   VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 4. TABLE: suppliers
-- ============================================================
-- Vendor / supplier records.
-- Referenced by products.supplier_id.
-- ============================================================
CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id   VARCHAR(10)  PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    contact       VARCHAR(50),
    email         VARCHAR(100),
    address       VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 5. TABLE: products
-- ============================================================
-- Core inventory catalog.
-- Links to categories and suppliers via foreign keys.
-- ============================================================
CREATE TABLE IF NOT EXISTS products (
    product_id    VARCHAR(10)  PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    category_id   VARCHAR(10),
    supplier_id   VARCHAR(10),
    quantity      INT          NOT NULL DEFAULT 0,
    price         DOUBLE       NOT NULL,

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES categories(category_id)
        ON UPDATE CASCADE ON DELETE SET NULL,

    CONSTRAINT fk_product_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 6. TABLE: stock_transactions
-- ============================================================
-- Records every stock-in and stock-out event.
-- Links to products via foreign key.
-- ============================================================
CREATE TABLE IF NOT EXISTS stock_transactions (
    transaction_id  VARCHAR(10)  PRIMARY KEY,
    product_id      VARCHAR(10),
    type            VARCHAR(5)   NOT NULL       COMMENT 'IN or OUT',
    quantity        INT          NOT NULL,
    date            DATE         NOT NULL,
    notes           VARCHAR(200),

    CONSTRAINT fk_transaction_product
        FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 7. TABLE: reports
-- ============================================================
-- Metadata for generated reports (low-stock alerts, summaries).
-- Links to the user who generated it.
-- ============================================================
CREATE TABLE IF NOT EXISTS reports (
    report_id       VARCHAR(10)  PRIMARY KEY,
    report_type     VARCHAR(100),
    generated_by    VARCHAR(10),
    generated_date  DATE,

    CONSTRAINT fk_report_user
        FOREIGN KEY (generated_by) REFERENCES users(user_id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- END OF SCHEMA
-- ============================================================
