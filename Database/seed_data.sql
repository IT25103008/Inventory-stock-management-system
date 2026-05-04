
USE papol_lite;

-- ────────────────────────────────────────────────────────────
-- 1. Users (Admin & Staff accounts)
-- ────────────────────────────────────────────────────────────
INSERT INTO users (user_id, name, email, password, role, phone, status) VALUES
    ('USR-001', 'Nimesha Admin',  'admin@inventory.lk', '1234', 'Admin', '+94711111111', 'Active'),
    ('USR-002', 'Kamal Staff',    'kamal@inventory.lk', '1234', 'Staff', '+94722222222', 'Active'),
    ('USR-003', 'Saman Staff',    'saman@inventory.lk', '1234', 'Staff', '+94733333333', 'Active');

-- ────────────────────────────────────────────────────────────
-- 2. Categories
-- ────────────────────────────────────────────────────────────
INSERT INTO categories (category_id, name, description) VALUES
    ('CAT-001', 'Electronics',      'Electronic devices and accessories'),
    ('CAT-002', 'Office Supplies',  'Stationery and office items'),
    ('CAT-003', 'Furniture',        'Tables, chairs, and storage');

-- ────────────────────────────────────────────────────────────
-- 3. Suppliers
-- ────────────────────────────────────────────────────────────
INSERT INTO suppliers (supplier_id, name, contact, email, address) VALUES
    ('SUP-001', 'TechMart Ltd.',  '+94112345678', 'tech@mart.lk',         'No.10, Galle Rd, Colombo'),
    ('SUP-002', 'Office World',   '+94119876543', 'info@officeworld.lk',  'No.25, Kandy Rd, Colombo'),
    ('SUP-003', 'FurniCo',        '+94113456789', 'sales@furnico.lk',     'No.5, Nugegoda');

-- ────────────────────────────────────────────────────────────
-- 4. Products
-- ────────────────────────────────────────────────────────────
INSERT INTO products (product_id, name, category_id, supplier_id, quantity, price) VALUES
    ('PRD-001', 'Samsung Monitor', 'CAT-001', 'SUP-001', 15,  45000.00),
    ('PRD-002', 'USB Keyboard',    'CAT-001', 'SUP-001', 30,   3500.00),
    ('PRD-003', 'A4 Paper Ream',   'CAT-002', 'SUP-002', 100,   850.00),
    ('PRD-004', 'Office Chair',    'CAT-003', 'SUP-003', 8,   18000.00),
    ('PRD-005', 'Printer Ink',     'CAT-002', 'SUP-002', 3,    1200.00);

-- ────────────────────────────────────────────────────────────
-- 5. Stock Transactions
-- ────────────────────────────────────────────────────────────
INSERT INTO stock_transactions (transaction_id, product_id, type, quantity, date, notes) VALUES
    ('TXN-001', 'PRD-001', 'IN',  10, '2026-04-01', 'Initial stock'),
    ('TXN-002', 'PRD-002', 'IN',  30, '2026-04-01', 'Initial stock'),
    ('TXN-003', 'PRD-001', 'OUT',  2, '2026-04-10', 'Sold to IT dept'),
    ('TXN-004', 'PRD-005', 'IN',   3, '2026-04-12', 'Restock');

-- ============================================================
-- END OF SEED DATA
-- ============================================================
