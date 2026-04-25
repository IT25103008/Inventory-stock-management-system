# 🏷️ SE1020 — Inventory & Stock Management System
## Ultimate Team Guide — Solid Above-Average Level
### Mapped to Your 6-Member Workload Distribution

> **How to use this guide:** Each team member gets one section. Build exactly what is described in your section — no more, no less. Together, the 6 sections form a complete, above-average system.

---

# 📋 OVERVIEW — What the Full System Looks Like

## Pages (7 total — more than the minimum 3)
| Page File | Owner | Purpose |
|-----------|-------|---------|
| `login.html` | Member 5 | Login form |
| `dashboard.html` | Member 5 | Welcome + navigation links |
| `users.html` | Member 5 | User & Admin CRUD |
| `products.html` | Member 1 | Product CRUD |
| `categories.html` | Member 4 | Category CRUD |
| `suppliers.html` | Member 3 | Supplier CRUD |
| `stock.html` | Member 2 | Stock In/Out CRUD |
| `reports.html` | Member 6 | Low stock + summary |

## Database Tables (6 total)
| Table | Owner |
|-------|-------|
| `users` | Member 5 |
| `products` | Member 1 |
| `categories` | Member 4 |
| `suppliers` | Member 3 |
| `stock_transactions` | Member 2 |
| `reports` (metadata) | Member 6 |

## OOP Hierarchy (Everyone uses this — build it once, shared by all)
```
Employee  ←  Abstract base class (Member 5 builds this)
   |
  ISA
 /   \
Admin   StaffMember     ← Subclasses (Member 5 builds these)
```

---

# 🗄️ SHARED — Database Schema

Everyone runs this SQL. One person sets it up, everyone else uses it.

```sql
CREATE DATABASE IF NOT EXISTS inventory_db;
USE inventory_db;

CREATE TABLE users (
    user_id   VARCHAR(10) PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    email     VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    role      VARCHAR(20) NOT NULL,    -- 'Admin' or 'Staff'
    phone     VARCHAR(20),
    status    VARCHAR(20) DEFAULT 'Active'
);

CREATE TABLE categories (
    category_id   VARCHAR(10) PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    description   VARCHAR(200)
);

CREATE TABLE suppliers (
    supplier_id   VARCHAR(10) PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    contact       VARCHAR(50),
    email         VARCHAR(100),
    address       VARCHAR(200)
);

CREATE TABLE products (
    product_id    VARCHAR(10) PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    category_id   VARCHAR(10),
    supplier_id   VARCHAR(10),
    quantity      INT NOT NULL DEFAULT 0,
    price         DOUBLE NOT NULL
);

CREATE TABLE stock_transactions (
    transaction_id  VARCHAR(10) PRIMARY KEY,
    product_id      VARCHAR(10),
    type            VARCHAR(5) NOT NULL,   -- 'IN' or 'OUT'
    quantity        INT NOT NULL,
    date            DATE NOT NULL,
    notes           VARCHAR(200)
);

CREATE TABLE reports (
    report_id       VARCHAR(10) PRIMARY KEY,
    report_type     VARCHAR(100),
    generated_by    VARCHAR(10),
    generated_date  DATE
);
```

## Sample Data

```sql
-- Users
INSERT INTO users VALUES
  ('USR-001','Nimesha Admin','admin@inventory.lk','1234','Admin','+94711111111','Active'),
  ('USR-002','Kamal Staff','kamal@inventory.lk','1234','Staff','+94722222222','Active'),
  ('USR-003','Saman Staff','saman@inventory.lk','1234','Staff','+94733333333','Active');

-- Categories
INSERT INTO categories VALUES
  ('CAT-001','Electronics','Electronic devices and accessories'),
  ('CAT-002','Office Supplies','Stationery and office items'),
  ('CAT-003','Furniture','Tables, chairs, and storage');

-- Suppliers
INSERT INTO suppliers VALUES
  ('SUP-001','TechMart Ltd.','+94112345678','tech@mart.lk','No.10, Galle Rd, Colombo'),
  ('SUP-002','Office World','+94119876543','info@officeworld.lk','No.25, Kandy Rd, Colombo'),
  ('SUP-003','FurniCo','+94113456789','sales@furnico.lk','No.5, Nugegoda');

-- Products
INSERT INTO products VALUES
  ('PRD-001','Samsung Monitor','CAT-001','SUP-001',15,45000.00),
  ('PRD-002','USB Keyboard','CAT-001','SUP-001',30,3500.00),
  ('PRD-003','A4 Paper Ream','CAT-002','SUP-002',100,850.00),
  ('PRD-004','Office Chair','CAT-003','SUP-003',8,18000.00),
  ('PRD-005','Printer Ink','CAT-002','SUP-002',3,1200.00);

-- Stock Transactions
INSERT INTO stock_transactions VALUES
  ('TXN-001','PRD-001','IN',10,'2026-04-01','Initial stock'),
  ('TXN-002','PRD-002','IN',30,'2026-04-01','Initial stock'),
  ('TXN-003','PRD-001','OUT',2,'2026-04-10','Sold to IT dept'),
  ('TXN-004','PRD-005','IN',3,'2026-04-12','Restock');
```

---

# 🏗️ SHARED — Spring Boot Project Setup

**One person creates the IntelliJ project. Everyone else adds their own files.**

## application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=1234
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
server.port=8080
spring.jackson.default-property-inclusion=non_null
```

## pom.xml dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

## InventoryApplication.java
```java
package com.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InventoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }
}
```

## CorsConfig.java
```java
package com.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS");
    }
}
```

---

# 👤 MEMBER 5 — User & Admin Management
**You own the OOP base class. This is the most important module for the viva.**

## Description
Manages all user accounts in the system. Handles registration, search, update, and deletion of both Admin and Staff accounts. Also handles login and session management.

## CRUD Operations
| Operation | What it does | Endpoint |
|-----------|-------------|----------|
| **Create** | Register a new user (Admin or Staff) and store in `users` table | `POST /users` |
| **Read** | View all users list + search by ID | `GET /users` / `GET /users/{id}` |
| **Update** | Modify user details (name, email, phone, status) | `PUT /users/{id}` |
| **Delete** | Remove a user account from the system | `DELETE /users/{id}` |
| **Login** | Authenticate by email + password | `POST /login` |

## UI Pages You Build
- `login.html` — Login form (email + password)
- `dashboard.html` — Welcome page with navigation cards
- `users.html` — Full CRUD: list, add, edit, delete users

## OOP Concepts Applied
- **Encapsulation:** `Employee` class has all private fields; access only through getters/setters
- **Inheritance:** `Admin` and `StaffMember` both extend `Employee`, inheriting all identity fields
- **Polymorphism:** `getAccessLevel()` is abstract in `Employee`; each subclass returns different access string
- **Abstraction:** `Employee` is abstract — you cannot create a plain `Employee` object

---

## Your Java Files

### Employee.java (Abstract Base — the OOP showcase)
```java
package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class Employee {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", insertable = false, updatable = false)
    private String role;

    @Column(name = "phone")
    private String phone;

    @Column(name = "status")
    private String status;

    // Default constructor — required by JPA
    public Employee() {}

    // Parameterized constructor
    public Employee(String userId, String name, String email, String password, String phone) {
        this.userId   = userId;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.phone    = phone;
        this.status   = "Active";
    }

    // Abstract method — Polymorphism — each subclass implements differently
    public abstract String getAccessLevel();

    // Getters and Setters — Encapsulation
    public String getUserId()              { return userId; }
    public void   setUserId(String id)     { this.userId = id; }

    public String getName()                { return name; }
    public void   setName(String n)        { this.name = n; }

    public String getEmail()               { return email; }
    public void   setEmail(String e)       { this.email = e; }

    public String getPassword()            { return password; }
    public void   setPassword(String p)    { this.password = p; }

    public String getRole()                { return role; }

    public String getPhone()               { return phone; }
    public void   setPhone(String ph)      { this.phone = ph; }

    public String getStatus()              { return status; }
    public void   setStatus(String s)      { this.status = s; }
}
```

### Admin.java
```java
package com.inventory.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Admin")
public class Admin extends Employee {

    public Admin() { super(); }

    public Admin(String userId, String name, String email, String password, String phone) {
        super(userId, name, email, password, phone);
    }

    @Override
    public String getAccessLevel() {
        return "FULL_ACCESS";  // Admin can manage all modules
    }
}
```

### StaffMember.java
```java
package com.inventory.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Staff")
public class StaffMember extends Employee {

    public StaffMember() { super(); }

    public StaffMember(String userId, String name, String email, String password, String phone) {
        super(userId, name, email, password, phone);
    }

    @Override
    public String getAccessLevel() {
        return "LIMITED_ACCESS";  // Staff can view and do stock transactions
    }
}
```

### UserRepository.java
```java
package com.inventory.repository;

import com.inventory.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findByEmailAndPassword(String email, String password);
    boolean existsByEmail(String email);
}
```

### UserService.java
```java
package com.inventory.service;

import com.inventory.model.*;
import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<Employee> getAllUsers()                  { return userRepository.findAll(); }
    public Optional<Employee> getUserById(String id)    { return userRepository.findById(id); }

    public Employee addUser(Employee user) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new RuntimeException("Email already exists");
        return userRepository.save(user);
    }

    public Employee updateUser(String id, Employee updated) {
        Employee existing = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setStatus(updated.getStatus());
        return userRepository.save(existing);
    }

    public void deleteUser(String id)                   { userRepository.deleteById(id); }

    public Optional<Employee> login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
}
```

### UserController.java
```java
package com.inventory.controller;

import com.inventory.model.*;
import com.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        Optional<Employee> emp = userService.login(body.get("email"), body.get("password"));
        return emp.isPresent()
            ? ResponseEntity.ok(emp.get())
            : ResponseEntity.status(401).body(Map.of("message","Invalid credentials"));
    }

    @GetMapping("/users")
    public List<Employee> getAllUsers() { return userService.getAllUsers(); }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody Admin user) {
        try {
            return ResponseEntity.status(201).body(userService.addUser(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody Admin user) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}
```

---

# 📦 MEMBER 1 — Product Management

## Description
Manages the inventory product catalog. Products link to a category and a supplier. Handles adding, searching, updating, and removing products from the system.

## CRUD Operations
| Operation | What it does | Endpoint |
|-----------|-------------|----------|
| **Create** | Add a new product with name, category, supplier, qty, price | `POST /products` |
| **Read** | List all products; view single product by ID | `GET /products` / `GET /products/{id}` |
| **Update** | Edit product details (name, category, price, quantity) | `PUT /products/{id}` |
| **Delete** | Remove a product from the catalog | `DELETE /products/{id}` |

## UI Page You Build
- `products.html` — Product list + Add/Edit modal + Delete button

## OOP Concepts Applied
- **Encapsulation:** `Product` class has private fields; values only changed through setters
- **Inheritance:** `Product` is a standalone entity; demonstrate that it's modelled as a separate domain object from `Employee` hierarchy
- **Abstraction:** `ProductService` hides all database logic; controller only calls service methods

---

## Your Java Files

### Product.java
```java
package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private double price;

    public Product() {}

    public Product(String productId, String name, String categoryId, String supplierId, int quantity, double price) {
        this.productId  = productId;
        this.name       = name;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.quantity   = quantity;
        this.price      = price;
    }

    // Getters and Setters — Encapsulation
    public String getProductId()               { return productId; }
    public void   setProductId(String id)      { this.productId = id; }

    public String getName()                    { return name; }
    public void   setName(String n)            { this.name = n; }

    public String getCategoryId()              { return categoryId; }
    public void   setCategoryId(String c)      { this.categoryId = c; }

    public String getSupplierId()              { return supplierId; }
    public void   setSupplierId(String s)      { this.supplierId = s; }

    public int    getQuantity()                { return quantity; }
    public void   setQuantity(int q)           { this.quantity = q; }

    public double getPrice()                   { return price; }
    public void   setPrice(double p)           { this.price = p; }
}
```

### ProductRepository.java
```java
package com.inventory.repository;

import com.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByQuantityLessThan(int threshold);   // for low-stock report
}
```

### ProductService.java
```java
package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts()               { return productRepository.findAll(); }
    public Optional<Product> getProductById(String id) { return productRepository.findById(id); }
    public Product addProduct(Product p)               { return productRepository.save(p); }

    public Product updateProduct(String id, Product updated) {
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setName(updated.getName());
        existing.setCategoryId(updated.getCategoryId());
        existing.setSupplierId(updated.getSupplierId());
        existing.setQuantity(updated.getQuantity());
        existing.setPrice(updated.getPrice());
        return productRepository.save(existing);
    }

    public void deleteProduct(String id)               { productRepository.deleteById(id); }

    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findByQuantityLessThan(threshold);
    }
}
```

### ProductController.java
```java
package com.inventory.controller;

import com.inventory.model.Product;
import com.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public List<Product> getAll()                        { return productService.getAllProducts(); }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        return productService.getProductById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/products")
    public ResponseEntity<Product> add(@RequestBody Product p) {
        return ResponseEntity.status(201).body(productService.addProduct(p));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Product p) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, p));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}
```

---

# 🏷️ MEMBER 4 — Category Management

## Description
Manages product categories used to classify inventory items. Each category has a name and description. Products are linked to categories by `category_id`.

## CRUD Operations
| Operation | What it does | Endpoint |
|-----------|-------------|----------|
| **Create** | Add a new category with name and description | `POST /categories` |
| **Read** | List all categories; view single category by ID | `GET /categories` / `GET /categories/{id}` |
| **Update** | Edit category name or description | `PUT /categories/{id}` |
| **Delete** | Remove a category from the system | `DELETE /categories/{id}` |

## UI Page You Build
- `categories.html` — Category list + Add/Edit modal + Delete button

## OOP Concepts Applied
- **Encapsulation:** `Category` class stores all fields privately with getters/setters
- **Abstraction:** `CategoryService` hides DB logic from the controller

---

## Your Java Files

### Category.java
```java
package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    public Category() {}

    public Category(String categoryId, String name, String description) {
        this.categoryId  = categoryId;
        this.name        = name;
        this.description = description;
    }

    public String getCategoryId()              { return categoryId; }
    public void   setCategoryId(String id)     { this.categoryId = id; }

    public String getName()                    { return name; }
    public void   setName(String n)            { this.name = n; }

    public String getDescription()             { return description; }
    public void   setDescription(String d)     { this.description = d; }
}
```

### CategoryRepository.java
```java
package com.inventory.repository;

import com.inventory.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {}
```

### CategoryService.java
```java
package com.inventory.service;

import com.inventory.model.Category;
import com.inventory.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories()               { return categoryRepository.findAll(); }
    public Optional<Category> getCategoryById(String id)  { return categoryRepository.findById(id); }
    public Category addCategory(Category c)               { return categoryRepository.save(c); }

    public Category updateCategory(String id, Category updated) {
        Category existing = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        return categoryRepository.save(existing);
    }

    public void deleteCategory(String id) { categoryRepository.deleteById(id); }
}
```

### CategoryController.java
```java
package com.inventory.controller;

import com.inventory.model.Category;
import com.inventory.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public List<Category> getAll()                         { return categoryService.getAllCategories(); }

    @GetMapping("/categories/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        return categoryService.getCategoryById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> add(@RequestBody Category c) {
        return ResponseEntity.status(201).body(categoryService.addCategory(c));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Category c) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, c));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}
```

---

# 🏭 MEMBER 3 — Supplier Management

## Description
Manages vendor/supplier records. Suppliers are linked to products. Handles adding, searching, modifying, and removing supplier accounts.

## CRUD Operations
| Operation | What it does | Endpoint |
|-----------|-------------|----------|
| **Create** | Add a new supplier with name, contact, email, address | `POST /suppliers` |
| **Read** | List all suppliers; view single supplier by ID | `GET /suppliers` / `GET /suppliers/{id}` |
| **Update** | Edit supplier contact or address details | `PUT /suppliers/{id}` |
| **Delete** | Remove a supplier from the system | `DELETE /suppliers/{id}` |

## UI Page You Build
- `suppliers.html` — Supplier list + Add/Edit modal + Delete button

## OOP Concepts Applied
- **Encapsulation:** `Supplier` class has private contact fields; access only through getters/setters
- **Abstraction:** `SupplierService` layer hides all JPA/SQL logic

---

## Your Java Files

### Supplier.java
```java
package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact")
    private String contact;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    public Supplier() {}

    public Supplier(String supplierId, String name, String contact, String email, String address) {
        this.supplierId = supplierId;
        this.name       = name;
        this.contact    = contact;
        this.email      = email;
        this.address    = address;
    }

    public String getSupplierId()              { return supplierId; }
    public void   setSupplierId(String id)     { this.supplierId = id; }

    public String getName()                    { return name; }
    public void   setName(String n)            { this.name = n; }

    public String getContact()                 { return contact; }
    public void   setContact(String c)         { this.contact = c; }

    public String getEmail()                   { return email; }
    public void   setEmail(String e)           { this.email = e; }

    public String getAddress()                 { return address; }
    public void   setAddress(String a)         { this.address = a; }
}
```

### SupplierRepository.java
```java
package com.inventory.repository;

import com.inventory.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, String> {}
```

### SupplierService.java
```java
package com.inventory.service;

import com.inventory.model.Supplier;
import com.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers()               { return supplierRepository.findAll(); }
    public Optional<Supplier> getSupplierById(String id) { return supplierRepository.findById(id); }
    public Supplier addSupplier(Supplier s)              { return supplierRepository.save(s); }

    public Supplier updateSupplier(String id, Supplier updated) {
        Supplier existing = supplierRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Supplier not found"));
        existing.setName(updated.getName());
        existing.setContact(updated.getContact());
        existing.setEmail(updated.getEmail());
        existing.setAddress(updated.getAddress());
        return supplierRepository.save(existing);
    }

    public void deleteSupplier(String id)                { supplierRepository.deleteById(id); }
}
```

### SupplierController.java
```java
package com.inventory.controller;

import com.inventory.model.Supplier;
import com.inventory.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/suppliers")
    public List<Supplier> getAll()                        { return supplierService.getAllSuppliers(); }

    @GetMapping("/suppliers/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        return supplierService.getSupplierById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/suppliers")
    public ResponseEntity<Supplier> add(@RequestBody Supplier s) {
        return ResponseEntity.status(201).body(supplierService.addSupplier(s));
    }

    @PutMapping("/suppliers/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Supplier s) {
        try {
            return ResponseEntity.ok(supplierService.updateSupplier(id, s));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}
```

---

# 📊 MEMBER 2 — Stock In / Stock Out Management

## Description
Records stock movement transactions. When stock arrives (IN) or is consumed/sold (OUT), a transaction record is created and the product quantity is automatically updated.

## CRUD Operations
| Operation | What it does | Endpoint |
|-----------|-------------|----------|
| **Create** | Record a new IN or OUT transaction; auto-updates product qty | `POST /transactions` |
| **Read** | List all transactions; filter by product or type | `GET /transactions` |
| **Update** | Edit transaction notes or date | `PUT /transactions/{id}` |
| **Delete** | Remove a transaction record | `DELETE /transactions/{id}` |

## UI Page You Build
- `stock.html` — Transaction list + Add transaction form (product, type IN/OUT, quantity, date, notes)

## OOP Concepts Applied
- **Encapsulation:** `StockTransaction` stores all movement data privately
- **Abstraction:** `StockService` hides the double-write logic (transaction insert + product quantity update) from the controller

---

## Your Java Files

### StockTransaction.java
```java
package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stock_transactions")
public class StockTransaction {

    @Id
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "type", nullable = false)
    private String type;    // 'IN' or 'OUT'

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "notes")
    private String notes;

    public StockTransaction() {}

    public StockTransaction(String transactionId, String productId, String type, int quantity, LocalDate date, String notes) {
        this.transactionId = transactionId;
        this.productId     = productId;
        this.type          = type;
        this.quantity      = quantity;
        this.date          = date;
        this.notes         = notes;
    }

    public String       getTransactionId()               { return transactionId; }
    public void         setTransactionId(String id)      { this.transactionId = id; }

    public String       getProductId()                   { return productId; }
    public void         setProductId(String p)           { this.productId = p; }

    public String       getType()                        { return type; }
    public void         setType(String t)                { this.type = t; }

    public int          getQuantity()                    { return quantity; }
    public void         setQuantity(int q)               { this.quantity = q; }

    public LocalDate    getDate()                        { return date; }
    public void         setDate(LocalDate d)             { this.date = d; }

    public String       getNotes()                       { return notes; }
    public void         setNotes(String n)               { this.notes = n; }
}
```

### StockTransactionRepository.java
```java
package com.inventory.repository;

import com.inventory.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, String> {
    List<StockTransaction> findByProductId(String productId);
    List<StockTransaction> findByType(String type);
}
```

### StockService.java
```java
package com.inventory.service;

import com.inventory.model.StockTransaction;
import com.inventory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StockService {

    @Autowired
    private StockTransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<StockTransaction> getAllTransactions() { return transactionRepository.findAll(); }

    public StockTransaction recordTransaction(StockTransaction txn) {
        // Update product quantity when transaction is recorded
        productRepository.findById(txn.getProductId()).ifPresent(product -> {
            if ("IN".equalsIgnoreCase(txn.getType())) {
                product.setQuantity(product.getQuantity() + txn.getQuantity());
            } else if ("OUT".equalsIgnoreCase(txn.getType())) {
                product.setQuantity(product.getQuantity() - txn.getQuantity());
            }
            productRepository.save(product);
        });
        return transactionRepository.save(txn);
    }

    public StockTransaction updateTransaction(String id, StockTransaction updated) {
        StockTransaction existing = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
        existing.setNotes(updated.getNotes());
        existing.setDate(updated.getDate());
        return transactionRepository.save(existing);
    }

    public void deleteTransaction(String id) { transactionRepository.deleteById(id); }
}
```

### StockTransactionController.java
```java
package com.inventory.controller;

import com.inventory.model.StockTransaction;
import com.inventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class StockTransactionController {

    @Autowired
    private StockService stockService;

    @GetMapping("/transactions")
    public List<StockTransaction> getAll() { return stockService.getAllTransactions(); }

    @PostMapping("/transactions")
    public ResponseEntity<StockTransaction> record(@RequestBody StockTransaction txn) {
        return ResponseEntity.status(201).body(stockService.recordTransaction(txn));
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody StockTransaction txn) {
        try {
            return ResponseEntity.ok(stockService.updateTransaction(id, txn));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        stockService.deleteTransaction(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}
```

---

# 📈 MEMBER 6 — Reports & Low Stock Alert

## Description
Generates summary reports for the inventory system. Shows low-stock alerts, total inventory value, and stock movement history. Does not manage its own data — reads from products and transactions tables.

## CRUD Operations
| Operation | What it does | Endpoint |
|-----------|-------------|----------|
| **Create** | Save a generated report's metadata to `reports` table | `POST /reports` |
| **Read (main)** | Get low-stock products (qty < 5) | `GET /reports/low-stock` |
| **Read** | Get total inventory value (sum of price × qty) | `GET /reports/inventory-value` |
| **Read** | Get all stock movement history | `GET /reports/stock-movement` |
| **Delete** | Remove a saved report record | `DELETE /reports/{id}` |

## UI Page You Build
- `reports.html` — Three sections: Low Stock alert list, Inventory Value summary, Stock Movement table

## OOP Concepts Applied
- **Abstraction:** `ReportService` hides the computation logic (low-stock filter, value calculation) from the controller
- **Encapsulation:** `Report` entity stores report metadata with private fields

---

## Your Java Files

### Report.java
```java
package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @Column(name = "report_id")
    private String reportId;

    @Column(name = "report_type")
    private String reportType;

    @Column(name = "generated_by")
    private String generatedBy;

    @Column(name = "generated_date")
    private LocalDate generatedDate;

    public Report() {}

    public String    getReportId()                  { return reportId; }
    public void      setReportId(String id)         { this.reportId = id; }

    public String    getReportType()                { return reportType; }
    public void      setReportType(String t)        { this.reportType = t; }

    public String    getGeneratedBy()               { return generatedBy; }
    public void      setGeneratedBy(String by)      { this.generatedBy = by; }

    public LocalDate getGeneratedDate()             { return generatedDate; }
    public void      setGeneratedDate(LocalDate d)  { this.generatedDate = d; }
}
```

### ReportRepository.java
```java
package com.inventory.repository;

import com.inventory.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, String> {}
```

### ReportService.java
```java
package com.inventory.service;

import com.inventory.model.*;
import com.inventory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ReportService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockTransactionRepository transactionRepository;

    @Autowired
    private ReportRepository reportRepository;

    // Low stock: products with quantity below threshold
    public List<Product> getLowStockProducts() {
        return productRepository.findByQuantityLessThan(5);
    }

    // Total inventory value: sum of (price × quantity) for all products
    public Map<String, Object> getInventoryValue() {
        List<Product> products = productRepository.findAll();
        double totalValue = products.stream()
            .mapToDouble(p -> p.getPrice() * p.getQuantity())
            .sum();
        Map<String, Object> result = new HashMap<>();
        result.put("products", products);
        result.put("totalValue", totalValue);
        return result;
    }

    // Stock movement history
    public List<StockTransaction> getStockMovement() {
        return transactionRepository.findAll();
    }

    public Report saveReport(Report r)   { return reportRepository.save(r); }
    public void deleteReport(String id)  { reportRepository.deleteById(id); }
}
```

### ReportController.java
```java
package com.inventory.controller;

import com.inventory.model.*;
import com.inventory.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/reports/low-stock")
    public List<Product> getLowStock() { return reportService.getLowStockProducts(); }

    @GetMapping("/reports/inventory-value")
    public Map<String, Object> getInventoryValue() { return reportService.getInventoryValue(); }

    @GetMapping("/reports/stock-movement")
    public List<StockTransaction> getMovement() { return reportService.getStockMovement(); }

    @PostMapping("/reports")
    public ResponseEntity<Report> save(@RequestBody Report r) {
        return ResponseEntity.status(201).body(reportService.saveReport(r));
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}
```

---

# 🌐 FRONTEND — Shared JS Pattern

Every page uses the same pattern. Copy this for your page.

```javascript
const API = 'http://localhost:8080';

// 1. Load data when page opens
window.addEventListener('DOMContentLoaded', loadData);

async function loadData() {
    const res  = await fetch(`${API}/YOUR-ENDPOINT`);
    const data = await res.json();
    renderTable(data);
}

// 2. Render into table
function renderTable(items) {
    const tbody = document.getElementById('tableBody');
    tbody.innerHTML = '';
    items.forEach(item => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${item.someField}</td>
                        <td><button onclick="edit('${item.id}')">Edit</button>
                            <button onclick="remove('${item.id}')">Delete</button></td>`;
        tbody.appendChild(tr);
    });
}

// 3. Save (add or edit)
async function save() {
    const body = { /* collect form values */ };
    const url    = editingId ? `${API}/endpoint/${editingId}` : `${API}/endpoint`;
    const method = editingId ? 'PUT' : 'POST';

    const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });

    if (res.ok) { closeModal(); loadData(); }
    else        { alert('Error saving'); }
}

// 4. Delete
async function remove(id) {
    if (!confirm('Delete this record?')) return;
    await fetch(`${API}/endpoint/${id}`, { method: 'DELETE' });
    loadData();
}
```

## auth.js (shared — Member 5 creates this)
```javascript
const API = 'http://localhost:8080';

async function doLogin() {
    const email    = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;

    try {
        const res  = await fetch(`${API}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();

        if (res.ok) {
            localStorage.setItem('currentUser', JSON.stringify(data));
            window.location.href = 'dashboard.html';
        } else {
            document.getElementById('errorMsg').textContent = data.message || 'Login failed';
        }
    } catch (err) {
        document.getElementById('errorMsg').textContent = 'Cannot connect to server';
    }
}
```

---

# 🖼️ Class Diagram (Draw in draw.io)

Draw these 6 boxes. Use proper UML notation. The inheritance arrows are the most important part.

```
                    ┌─────────────────────────────┐
                    │       <<abstract>>           │
                    │          Employee            │
                    ├─────────────────────────────┤
                    │ - userId : String            │
                    │ - name : String              │
                    │ - email : String             │
                    │ - password : String          │
                    │ - phone : String             │
                    │ - status : String            │
                    ├─────────────────────────────┤
                    │ + getAccessLevel() : String  │ ← abstract
                    │ + getName() : String         │
                    │ + setName(n) : void          │
                    │   ... (all getters/setters)  │
                    └──────────┬──────────────────┘
                               │  <<inheritance>>
                    ┌──────────┴──────────┐
                    │                     │
          ┌─────────▼──────┐    ┌─────────▼──────────┐
          │    Admin        │    │    StaffMember       │
          ├─────────────────┤    ├────────────────────-┤
          │ (no new fields) │    │ (no new fields)      │
          ├─────────────────┤    ├──────────────────────┤
          │ +getAccessLevel │    │ +getAccessLevel()    │
          │ ():"FULL_ACCESS"│    │ :"LIMITED_ACCESS"    │
          └─────────────────┘    └──────────────────────┘

┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
│    Product        │   │    Category       │   │    Supplier      │
├──────────────────┤   ├──────────────────┤   ├──────────────────┤
│ - productId      │   │ - categoryId     │   │ - supplierId     │
│ - name           │   │ - name           │   │ - name           │
│ - categoryId     │   │ - description    │   │ - contact        │
│ - supplierId     │   ├──────────────────┤   │ - email          │
│ - quantity       │   │ + getters/setters│   │ - address        │
│ - price          │   └──────────────────┘   ├──────────────────┤
├──────────────────┤                           │ + getters/setters│
│ + getters/setters│                           └──────────────────┘
└──────────────────┘

┌─────────────────────┐   ┌──────────────────────┐
│  StockTransaction   │   │       Report          │
├─────────────────────┤   ├──────────────────────┤
│ - transactionId     │   │ - reportId            │
│ - productId         │   │ - reportType          │
│ - type (IN/OUT)     │   │ - generatedBy         │
│ - quantity          │   │ - generatedDate       │
│ - date              │   ├──────────────────────┤
│ - notes             │   │ + getters/setters    │
├─────────────────────┤   └──────────────────────┘
│ + getters/setters   │
└─────────────────────┘
```

---

# 📄 Final Report Outline

**8–12 pages. Keep it clean.**

```
1. Cover Page
   Module: SE1020 — Object Oriented Programming
   Project: Inventory & Stock Management System
   Team members + which module each built

2. Introduction (1 page)
   What the system does, why inventory management matters

3. Team Workload Distribution (1 page)
   Table: Member → Module → CRUD ops → Pages built

4. Technology Stack (half page)
   Java, Spring Boot, MySQL, HTML/CSS/JS, GitHub

5. System Architecture (1 page)
   Browser → Frontend HTML/JS → fetch() → Spring Boot → MySQL
   Include the ASCII architecture diagram

6. Database Design (1 page)
   Show all 6 CREATE TABLE statements + key field descriptions

7. OOP Design & Class Diagram (2 pages)
   Insert class diagram image
   Explain each concept with 3–4 lines + code example:
   → Encapsulation: private fields + getters/setters in Employee
   → Inheritance: Admin and StaffMember extend Employee
   → Polymorphism: getAccessLevel() returns different values
   → Abstraction: abstract Employee class + Service layer

8. API Endpoints Table (half page)
   List all endpoints across all 6 modules

9. UI Screenshots (1 page)
   One screenshot per page, with a one-line description

10. GitHub Commit History (1 page)
    Screenshot of GitHub showing commits from all members

11. Conclusion (half page)
    What OOP concepts you applied and what you learned
```

---

# 🐙 GitHub Commit Guide

## Each Member's Commits

**Member 5 (User Module)**
```
git commit -m "Add Employee abstract base class with inheritance hierarchy"
git commit -m "Add Admin and StaffMember subclasses"
git commit -m "Add UserRepository and UserService"
git commit -m "Add UserController with login and CRUD endpoints"
git commit -m "Add login.html and auth.js"
git commit -m "Add dashboard.html"
git commit -m "Add users.html and users.js"
```

**Member 1 (Product Module)**
```
git commit -m "Add Product entity class"
git commit -m "Add ProductRepository, ProductService, ProductController"
git commit -m "Add products.html and products.js"
```

**Member 4 (Category Module)**
```
git commit -m "Add Category entity class"
git commit -m "Add CategoryRepository, CategoryService, CategoryController"
git commit -m "Add categories.html and categories.js"
```

**Member 3 (Supplier Module)**
```
git commit -m "Add Supplier entity class"
git commit -m "Add SupplierRepository, SupplierService, SupplierController"
git commit -m "Add suppliers.html and suppliers.js"
```

**Member 2 (Stock Module)**
```
git commit -m "Add StockTransaction entity class"
git commit -m "Add StockTransactionRepository and StockService"
git commit -m "Add StockTransactionController"
git commit -m "Add stock.html and stock.js"
```

**Member 6 (Reports Module)**
```
git commit -m "Add Report entity and ReportRepository"
git commit -m "Add ReportService with low-stock and inventory value logic"
git commit -m "Add ReportController with analytics endpoints"
git commit -m "Add reports.html and reports.js"
```

**Shared commits (one person does these)**
```
git commit -m "Initial Spring Boot project structure"
git commit -m "Add database schema and sample data SQL"
git commit -m "Add CorsConfig for frontend-backend communication"
git commit -m "Add class diagram"
git commit -m "Add final report"
```

---

# 🎓 Viva Q&A — All Members Must Know These

**Q: Explain the OOP inheritance in your project.**
A: We have an abstract class called `Employee` that stores common fields like `userId`, `name`, `email`, and `password`. Two subclasses — `Admin` and `StaffMember` — extend `Employee` and inherit all those fields. This means we don't duplicate code. We use `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)` so both subclasses share one `users` table in the database, with a `role` column as the discriminator.

**Q: What is polymorphism and where did you use it?**
A: Polymorphism means the same method behaves differently depending on which object is calling it. We have an abstract method `getAccessLevel()` in `Employee`. When `Admin` calls it, it returns `"FULL_ACCESS"`. When `StaffMember` calls it, it returns `"LIMITED_ACCESS"`. Java resolves the correct version at runtime — that's runtime polymorphism.

**Q: What is encapsulation?**
A: Encapsulation means hiding internal data and only allowing access through defined methods. In all our model classes (`Employee`, `Product`, `Supplier`, etc.), every field is `private`. Outside classes cannot directly read or change them — they must use `getters` and `setters`. This protects the data from accidental modification.

**Q: What is abstraction?**
A: Abstraction hides implementation details and shows only what's necessary. We use it in two ways: (1) `Employee` is an `abstract` class — you cannot create a plain `Employee` object, only `Admin` or `StaffMember`. (2) Our `Service` classes (`ProductService`, `StockService`, etc.) hide all the database/SQL logic from the controllers. The controller just calls `productService.addProduct(p)` without knowing how it's saved.

**Q: Why MySQL instead of file handling?**
A: The project specification allows either. We chose MySQL because it handles multiple users accessing data simultaneously, supports queries to search and filter records efficiently, and prevents data corruption. File handling would require manual parsing and would be error-prone with concurrent writes.

**Q: How does `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)` work?**
A: It tells JPA to store all subclasses in one table. Every `Admin` and `StaffMember` row goes into the `users` table. The `role` column tells Hibernate which Java class to create when reading a row. If `role = 'Admin'`, it creates an `Admin` object. If `role = 'Staff'`, it creates a `StaffMember` object.

**Q: What happens when a stock transaction is recorded?**
A: The `StockService.recordTransaction()` method does two things atomically: it first finds the product by `productId`, then adds or subtracts the quantity depending on whether the type is `IN` or `OUT`, saves the updated product, and then saves the transaction record. So one API call updates both tables.

**Q: How does the frontend communicate with the backend?**
A: JavaScript uses the `fetch()` API to send HTTP requests to Spring Boot running on `localhost:8080`. For example, when adding a product, JavaScript sends `POST /products` with the product data as a JSON body. Spring Boot receives it, saves to MySQL, and returns the saved product as JSON. JavaScript then refreshes the table.

**Q: What is CORS and why is it needed?**
A: CORS (Cross-Origin Resource Sharing) is a browser security rule. The frontend runs on port 5500 (Live Server) and the backend on port 8080. Different ports = different origins. Without CORS config, the browser blocks all requests. Our `CorsConfig.java` tells the browser: "this server allows requests from any origin."

**Q: What is the role of the Repository layer?**
A: Repository interfaces extend `JpaRepository` and give us standard database operations for free — `findAll()`, `findById()`, `save()`, `deleteById()` — without writing any SQL. We can also add custom query methods like `findByQuantityLessThan(5)` and Spring Data generates the SQL automatically from the method name.

---

# ✅ Final Checklist

**Before viva — each member verifies:**
- [ ] Your module's table exists in MySQL with sample data
- [ ] Your Spring Boot files compile with no errors
- [ ] All 4 CRUD operations work (test in Postman first)
- [ ] Your HTML page loads and connects to backend
- [ ] You can explain every class and method you wrote
- [ ] Your commits are on GitHub under your own account

**Team verifies together:**
- [ ] `GET /products`, `GET /users`, `GET /categories`, `GET /suppliers`, `GET /transactions`, `GET /reports/low-stock` all return data
- [ ] Class diagram is drawn and exported as PNG
- [ ] Final report has Git commit history screenshot
- [ ] Sample data SQL file is ready for demo during viva

---

*This guide describes exactly the "solid above-average" SE1020 submission. Every member has equal, clearly-defined work. The OOP concepts are unmistakably visible in the code. The system does exactly what was asked — nothing more, nothing less.*
