# 🗄️ Database — PAPOL LITE (Inventory & Stock Management)

## Overview

| Property       | Value                 |
|----------------|-----------------------|
| **DBMS**       | MySQL 8.x             |
| **Database**   | `papol_lite`          |
| **Tables**     | 6                     |
| **Engine**     | InnoDB                |
| **Charset**    | utf8mb4               |

---

## Quick Setup

```bash
# 1. Create schema (tables + constraints)
mysql -u root -p < schema.sql

# 2. Insert sample data (optional — for development/testing)
mysql -u root -p < seed_data.sql
```

> **Note:** You can also open these `.sql` files directly in **MySQL Workbench** and execute them.

---

## Entity Relationship Diagram

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│    users     │       │  categories  │       │  suppliers   │
├──────────────┤       ├──────────────┤       ├──────────────┤
│ PK user_id   │       │ PK category_id│      │ PK supplier_id│
│    name      │       │    name       │      │    name       │
│    email  UQ │       │    description│      │    contact    │
│    password  │       └──────┬───────┘       │    email      │
│    role      │              │               │    address    │
│    phone     │              │               └──────┬───────┘
│    status    │              │                      │
└──────┬───────┘              │ FK                   │ FK
       │                ┌─────┴──────────────────────┴──────┐
       │                │           products                │
       │                ├───────────────────────────────────┤
       │                │ PK product_id                     │
       │                │    name                           │
       │                │ FK category_id → categories       │
       │                │ FK supplier_id → suppliers        │
       │                │    quantity                        │
       │                │    price                           │
       │                └──────────────┬────────────────────┘
       │                               │ FK
       │                ┌──────────────┴────────────────────┐
       │                │     stock_transactions            │
       │                ├───────────────────────────────────┤
       │                │ PK transaction_id                 │
       │                │ FK product_id → products          │
       │                │    type  (IN / OUT)               │
       │                │    quantity                        │
       │                │    date                            │
       │                │    notes                           │
       │                └───────────────────────────────────┘
       │ FK
┌──────┴───────────────────────────────────┐
│              reports                     │
├──────────────────────────────────────────┤
│ PK report_id                             │
│    report_type                           │
│ FK generated_by → users                  │
│    generated_date                        │
└──────────────────────────────────────────┘
```

---

## Table Details

### `users`
| Column    | Type         | Constraints        | Notes                                 |
|-----------|--------------|--------------------|---------------------------------------|
| user_id   | VARCHAR(10)  | PK                 | Format: `USR-001`                     |
| name      | VARCHAR(100) | NOT NULL           |                                       |
| email     | VARCHAR(100) | NOT NULL, UNIQUE   |                                       |
| password  | VARCHAR(100) | NOT NULL           |                                       |
| role      | VARCHAR(20)  | NOT NULL           | JPA discriminator: `Admin` or `Staff` |
| phone     | VARCHAR(20)  |                    |                                       |
| status    | VARCHAR(20)  | DEFAULT `'Active'` | `Active` or `Inactive`                |

### `categories`
| Column      | Type         | Constraints | Notes               |
|-------------|--------------|-------------|----------------------|
| category_id | VARCHAR(10)  | PK          | Format: `CAT-001`    |
| name        | VARCHAR(100) | NOT NULL    |                      |
| description | VARCHAR(200) |             |                      |

### `suppliers`
| Column      | Type         | Constraints | Notes               |
|-------------|--------------|-------------|----------------------|
| supplier_id | VARCHAR(10)  | PK          | Format: `SUP-001`    |
| name        | VARCHAR(100) | NOT NULL    |                      |
| contact     | VARCHAR(50)  |             |                      |
| email       | VARCHAR(100) |             |                      |
| address     | VARCHAR(200) |             |                      |

### `products`
| Column      | Type         | Constraints           | Notes               |
|-------------|--------------|-----------------------|----------------------|
| product_id  | VARCHAR(10)  | PK                    | Format: `PRD-001`    |
| name        | VARCHAR(100) | NOT NULL              |                      |
| category_id | VARCHAR(10)  | FK → categories       | ON DELETE SET NULL   |
| supplier_id | VARCHAR(10)  | FK → suppliers        | ON DELETE SET NULL   |
| quantity    | INT          | NOT NULL, DEFAULT `0` |                      |
| price       | DOUBLE       | NOT NULL              |                      |

### `stock_transactions`
| Column         | Type         | Constraints     | Notes               |
|----------------|--------------|-----------------|----------------------|
| transaction_id | VARCHAR(10)  | PK              | Format: `TXN-001`    |
| product_id     | VARCHAR(10)  | FK → products   | ON DELETE CASCADE    |
| type           | VARCHAR(5)   | NOT NULL        | `IN` or `OUT`        |
| quantity       | INT          | NOT NULL        |                      |
| date           | DATE         | NOT NULL        |                      |
| notes          | VARCHAR(200) |                 |                      |

### `reports`
| Column         | Type         | Constraints   | Notes               |
|----------------|--------------|---------------|----------------------|
| report_id      | VARCHAR(10)  | PK            | Format: `RPT-001`    |
| report_type    | VARCHAR(100) |               |                      |
| generated_by   | VARCHAR(10)  | FK → users    | ON DELETE SET NULL   |
| generated_date | DATE         |               |                      |

---

## Files in This Folder

| File             | Purpose                                           |
|------------------|---------------------------------------------------|
| `schema.sql`     | Full DDL — creates database + all 6 tables + FKs  |
| `seed_data.sql`  | Sample data for development/testing                |
| `README.md`      | This documentation file                            |
